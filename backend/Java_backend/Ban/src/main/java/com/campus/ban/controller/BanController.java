package com.campus.ban.controller;

import com.campus.ban.service.BanService;
import com.campus.ban.common.Result;
import com.campus.ban.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

/**
 * 封号管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api-8082/ban")
@RequiredArgsConstructor
@Validated
public class BanController {
    
    private final BanService banService;
    
    /**
     * 根据用户ID封号用户
     */
    @PostMapping("/user/{userId}")
    public Result<String> banUserById(@PathVariable @NotBlank(message = "用户ID不能为空") String userId) {
        log.info("封号请求，用户ID: {}", userId);
        
        // 检查用户是否存在
        if (!banService.userExists(userId)) {
            return Result.badRequest("用户不存在");
        }
        
        boolean success = banService.banUserById(userId);
        if (success) {
            return Result.success("用户封号成功");
        } else {
            return Result.error("用户封号失败，用户可能已被封号");
        }
    }
    
    /**
     * 根据用户ID解封用户
     */
    @DeleteMapping("/user/{userId}")
    public Result<String> unbanUserById(@PathVariable @NotBlank(message = "用户ID不能为空") String userId) {
        log.info("解封请求，用户ID: {}", userId);
        
        // 检查用户是否存在
        if (!banService.userExists(userId)) {
            return Result.badRequest("用户不存在");
        }
        
        boolean success = banService.unbanUserById(userId);
        if (success) {
            return Result.success("用户解封成功");
        } else {
            return Result.error("用户解封失败，用户可能未被封号");
        }
    }
    
    /**
     * 根据用户名封号用户
     */
    @PostMapping("/username/{userName}")
    public Result<String> banUserByName(@PathVariable @NotBlank(message = "用户名不能为空") String userName) {
        log.info("封号请求，用户名: {}", userName);
        
        // 检查用户是否存在
        if (!banService.userNameExists(userName)) {
            return Result.badRequest("用户不存在");
        }
        
        boolean success = banService.banUserByName(userName);
        if (success) {
            return Result.success("用户封号成功");
        } else {
            return Result.error("用户封号失败，用户可能已被封号");
        }
    }
    
    /**
     * 根据用户名解封用户
     */
    @DeleteMapping("/username/{userName}")
    public Result<String> unbanUserByName(@PathVariable @NotBlank(message = "用户名不能为空") String userName) {
        log.info("解封请求，用户名: {}", userName);
        
        // 检查用户是否存在
        if (!banService.userNameExists(userName)) {
            return Result.badRequest("用户不存在");
        }
        
        boolean success = banService.unbanUserByName(userName);
        if (success) {
            return Result.success("用户解封成功");
        } else {
            return Result.error("用户解封失败，用户可能未被封号");
        }
    }
    
    /**
     * 查询用户状态（根据用户ID）
     */
    @GetMapping("/status/user/{userId}")
    public Result<User> getUserStatusById(@PathVariable @NotBlank(message = "用户ID不能为空") String userId) {
        log.info("查询用户状态，用户ID: {}", userId);
        
        User user = banService.getUserById(userId);
        if (user == null) {
            return Result.badRequest("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        user.setIdCard(null);
        
        return Result.success(user);
    }
    
    /**
     * 查询用户状态（根据用户名）
     */
    @GetMapping("/status/username/{userName}")
    public Result<User> getUserStatusByName(@PathVariable @NotBlank(message = "用户名不能为空") String userName) {
        log.info("查询用户状态，用户名: {}", userName);
        
        User user = banService.getUserByName(userName);
        if (user == null) {
            return Result.badRequest("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        user.setIdCard(null);
        
        return Result.success(user);
    }
    
    /**
     * 批量封号用户（根据用户ID列表）
     */
    @PostMapping("/batch/users")
    public Result<String> batchBanUsers(@RequestBody @NotBlank String[] userIds) {
        log.info("批量封号请求，用户数量: {}", userIds.length);
        
        int successCount = 0;
        int failCount = 0;
        
        for (String userId : userIds) {
            if (banService.userExists(userId) && banService.banUserById(userId)) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        String message = String.format("批量封号完成，成功: %d, 失败: %d", successCount, failCount);
        return Result.success(message);
    }
    
    /**
     * 批量解封用户（根据用户ID列表）
     */
    @DeleteMapping("/batch/users")
    public Result<String> batchUnbanUsers(@RequestBody @NotBlank String[] userIds) {
        log.info("批量解封请求，用户数量: {}", userIds.length);
        
        int successCount = 0;
        int failCount = 0;
        
        for (String userId : userIds) {
            if (banService.userExists(userId) && banService.unbanUserById(userId)) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        String message = String.format("批量解封完成，成功: %d, 失败: %d", successCount, failCount);
        return Result.success(message);
    }
}