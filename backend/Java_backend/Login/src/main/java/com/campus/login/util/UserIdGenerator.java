package com.campus.login.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 用户ID生成器
 */
public class UserIdGenerator {
    
    private static final Random RANDOM = new Random();
    
    /**
     * 生成9位用户ID
     * 格式：年份后2位 + 月日(4位) + 随机3位数字
     * 例如：241201001 (2024年12月1日 + 001)
     * @return 9位用户ID
     */
    public static String generateUserId() {
        LocalDateTime now = LocalDateTime.now();
        
        // 年份后2位
        String year = String.format("%02d", now.getYear() % 100);
        
        // 月日4位
        String monthDay = String.format("%02d%02d", now.getMonthValue(), now.getDayOfMonth());
        
        // 随机3位数字
        String randomNum = String.format("%03d", RANDOM.nextInt(1000));
        
        return year + monthDay + randomNum;
    }
    
    /**
     * 生成指定长度的随机数字字符串
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}