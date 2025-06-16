package com.campus.ordermanagement.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * UUID生成工具类
 * 提供UUIDv7生成功能
 */
public class UUIDGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成UUIDv7
     * UUIDv7是基于时间戳的UUID，具有更好的排序性能
     * 
     * @return UUIDv7字符串
     */
    public static String generateUUIDv7() {
        // 获取当前时间戳（毫秒）
        long timestamp = Instant.now().toEpochMilli();
        
        // 生成随机数部分
        byte[] randomBytes = new byte[10];
        RANDOM.nextBytes(randomBytes);
        
        // 构造UUIDv7
        // 时间戳占48位（6字节）
        long mostSigBits = (timestamp << 16) | (randomBytes[0] & 0xFF) << 8 | (randomBytes[1] & 0xFF);
        
        // 设置版本号为7
        mostSigBits = (mostSigBits & 0xFFFFFFFFFFFF0FFFL) | 0x7000L;
        
        // 构造最低有效位
        long leastSigBits = 0;
        for (int i = 2; i < 10; i++) {
            leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xFF);
        }
        
        // 设置变体位
        leastSigBits = (leastSigBits & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        
        UUID uuid = new UUID(mostSigBits, leastSigBits);
        return uuid.toString();
    }

    /**
     * 生成标准UUID（UUIDv4）
     * 
     * @return UUIDv4字符串
     */
    public static String generateUUIDv4() {
        return UUID.randomUUID().toString();
    }

    /**
     * 验证UUID格式是否正确
     * 
     * @param uuid UUID字符串
     * @return 是否为有效的UUID格式
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        
        try {
            UUID.fromString(uuid.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取UUID的版本号
     * 
     * @param uuid UUID字符串
     * @return 版本号，如果UUID无效则返回-1
     */
    public static int getUUIDVersion(String uuid) {
        if (!isValidUUID(uuid)) {
            return -1;
        }
        
        try {
            UUID uuidObj = UUID.fromString(uuid.trim());
            return uuidObj.version();
        } catch (Exception e) {
            return -1;
        }
    }
}