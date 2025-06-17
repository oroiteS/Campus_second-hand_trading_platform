// ... existing code ...
import com.campus.profile.dto.ChangePasswordRequest; // 新增导入
import java.nio.charset.StandardCharsets; // 新增导入
import java.security.MessageDigest; // 新增导入
import java.security.NoSuchAlgorithmException; // 新增导入

// ... existing code ...
public class UserController {

    // ... existing code ...

    /**
     * SHA-256加密密码
     * @param password 原始密码
     * @return 加密后的密码 (十六进制字符串)
     */
    private static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 在实际应用中，这里应该记录日志，并可能抛出自定义的业务异常
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    @Operation(summary = "修改用户密码", description = "根据用户ID、原密码和新密码修改用户密码")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "密码修改成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效或原密码错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/password/change")
    public ResponseEntity<Map<String, Object>> changePassword(
            @Parameter(description = "修改密码请求体", required = true)
            @Valid @RequestBody ChangePasswordRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = request.getUserId();
            String oldPassword = request.getOldPassword();
            String newPassword = request.getNewPassword();

            User user = userService.getUserById(userId);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            // 加密传入的原密码和新密码
            String encryptedOldPassword = encryptPassword(oldPassword);
            String encryptedNewPassword = encryptPassword(newPassword);

            // 验证原密码
            if (!user.getPassword().equals(encryptedOldPassword)) {
                response.put("success", false);
                response.put("message", "原密码错误");
                response.put("code", 400); // 或者使用更具体的错误码，例如401表示未授权（密码错误）
                return ResponseEntity.status(400).body(response);
            }

            // 更新密码
            user.setPassword(encryptedNewPassword);
            User updatedUser = userService.updateUser(user); // 假设userService有updateUser方法来更新整个用户信息
                                                        // 或者可以创建一个专门的updatePassword方法

            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "密码修改成功");
                response.put("code", 200);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "密码修改失败，请稍后重试");
                response.put("code", 500);
                return ResponseEntity.status(500).body(response);
            }

        } catch (RuntimeException e) {
            // 处理 encryptPassword 可能抛出的 RuntimeException
            response.put("success", false);
            response.put("message", "密码加密失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    // ... existing code ...
}