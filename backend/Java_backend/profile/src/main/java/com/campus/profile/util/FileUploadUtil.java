package com.campus.profile.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理头像文件的上传、存储和URL生成
 */
@Component
public class FileUploadUtil {

    // 允许的图片文件类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // 允许的文件扩展名
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    // 最大文件大小（5MB）
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // 头像上传目录（从配置文件读取，默认值）
    @Value("${file.upload.avatar-path:/uploads/avatars/}")
    private String avatarUploadPath;

    // 服务器基础URL（从配置文件读取）
    @Value("${server.base-url:http://localhost:8080}")
    private String serverBaseUrl;

    /**
     * 上传头像文件
     * 
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件的访问URL
     * @throws IOException 文件操作异常
     * @throws IllegalArgumentException 文件验证失败
     */
    public String uploadAvatar(MultipartFile file, String userId) throws IOException {
        // 验证文件
        validateFile(file);
        
        // 创建上传目录
        createUploadDirectory();
        
        // 生成文件名
        String fileName = generateFileName(file, userId);
        
        // 构建文件路径
        Path filePath = Paths.get(avatarUploadPath, fileName);
        
        // 保存文件
        file.transferTo(filePath.toFile());
        
        // 返回文件访问URL
        return generateFileUrl(fileName);
    }

    /**
     * 验证上传的文件
     * 
     * @param file 上传的文件
     * @throws IllegalArgumentException 验证失败时抛出异常
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("只支持JPG、PNG、GIF、WEBP格式的图片文件");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("不支持的文件扩展名: " + extension);
        }
    }

    /**
     * 创建上传目录
     * 
     * @throws IOException 目录创建失败时抛出异常
     */
    private void createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(avatarUploadPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    /**
     * 生成唯一的文件名
     * 
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 生成的文件名
     */
    private String generateFileName(MultipartFile file, String userId) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // 生成UUID的前8位作为随机字符串
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        
        // 组合文件名：用户ID_时间戳_随机字符串.扩展名
        return userId + "_" + timestamp + "_" + randomStr + extension;
    }

    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成文件访问URL
     * 
     * @param fileName 文件名
     * @return 完整的文件访问URL
     */
    private String generateFileUrl(String fileName) {
        // 确保路径以/开头和结尾
        String normalizedPath = avatarUploadPath;
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (!normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath + "/";
        }
        
        return serverBaseUrl + normalizedPath + fileName;
    }

    /**
     * 删除旧的头像文件
     * 
     * @param avatarUrl 旧头像的URL
     * @return 是否删除成功
     */
    public boolean deleteOldAvatar(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return true; // 没有旧文件，认为删除成功
        }

        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(avatarUrl);
            if (fileName == null) {
                return false;
            }

            // 构建文件路径
            Path filePath = Paths.get(avatarUploadPath, fileName);
            
            // 删除文件
            return Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // 删除失败，记录日志但不影响主流程
            System.err.println("删除旧头像文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从URL中提取文件名
     * 
     * @param url 文件URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // 获取URL的最后一部分作为文件名
        int lastSlashIndex = url.lastIndexOf("/");
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        
        return null;
    }
}