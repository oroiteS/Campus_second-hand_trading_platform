package com.campus.login;

import com.campus.login.dto.LoginRequest;
import com.campus.login.dto.RegisterRequest;
import com.campus.login.service.UserService;
import com.campus.login.util.PasswordUtil;
import com.campus.login.util.UserIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginApplicationTests {
    
    @Autowired
    private UserService userService;
    
    @Test
    void contextLoads() {
        assertNotNull(userService);
    }
    
    /**
     * 测试密码加密工具
     */
    @Test
    void testPasswordUtil() {
        String password = "123456";
        String encrypted = PasswordUtil.encryptPassword(password);
        
        assertNotNull(encrypted);
        assertEquals(64, encrypted.length()); // SHA256结果长度为64位
        assertTrue(PasswordUtil.matches(password, encrypted));
        assertFalse(PasswordUtil.matches("wrong_password", encrypted));
    }
    
    /**
     * 测试用户ID生成器
     */
    @Test
    void testUserIdGenerator() {
        String userId1 = UserIdGenerator.generateUserId();
        String userId2 = UserIdGenerator.generateUserId();
        
        assertNotNull(userId1);
        assertNotNull(userId2);
        assertEquals(9, userId1.length());
        assertEquals(9, userId2.length());
        // 由于包含随机数，两次生成的ID应该不同（概率极小相同）
    }
    
    /**
     * 测试用户注册功能
     */
    @Test
    void testUserRegister() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testuser" + System.currentTimeMillis());
        registerRequest.setPassword("123456");
        registerRequest.setTelephone("1" + String.format("%010d", System.currentTimeMillis() % 10000000000L));
        registerRequest.setRealName("测试用户");
        registerRequest.setIdCard("110101199001011234");

        
        Map<String, Object> result = userService.register(registerRequest);
        
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("注册成功", result.get("message"));
        assertNotNull(result.get("userId"));
    }
    
    /**
     * 测试用户登录功能
     */
    @Test
    void testUserLogin() {
        // 先注册一个用户
        String timestamp = String.valueOf(System.currentTimeMillis());
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("logintest" + timestamp);
        registerRequest.setPassword("123456");
        registerRequest.setTelephone("1" + timestamp.substring(timestamp.length() - 10));
        registerRequest.setRealName("登录测试用户");
        registerRequest.setIdCard("110101199001011235");

        
        Map<String, Object> registerResult = userService.register(registerRequest);
        assertTrue((Boolean) registerResult.get("success"));
        
        // 测试登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUserName());
        loginRequest.setPassword("123456");
        
        Map<String, Object> loginResult = userService.login(loginRequest);
        
        assertNotNull(loginResult);
        assertTrue((Boolean) loginResult.get("success"));
        assertEquals("登录成功", loginResult.get("message"));
        assertNotNull(loginResult.get("token"));
        assertNotNull(loginResult.get("userId"));
        assertEquals(registerRequest.getUserName(), loginResult.get("userName"));
    }
    
    /**
     * 测试错误密码登录
     */
    @Test
    void testLoginWithWrongPassword() {
        // 先注册一个用户
        String timestamp = String.valueOf(System.currentTimeMillis());
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("wrongpwdtest" + timestamp);
        registerRequest.setPassword("123456");
        registerRequest.setTelephone("1" + timestamp.substring(timestamp.length() - 10));
        registerRequest.setRealName("错误密码测试用户");
        registerRequest.setIdCard("110101199001011236");

        
        Map<String, Object> registerResult = userService.register(registerRequest);
        assertTrue((Boolean) registerResult.get("success"));
        
        // 测试错误密码登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(registerRequest.getUserName());
        loginRequest.setPassword("wrongpassword");
        
        Map<String, Object> loginResult = userService.login(loginRequest);
        
        assertNotNull(loginResult);
        assertFalse((Boolean) loginResult.get("success"));
        assertEquals("密码错误", loginResult.get("message"));
    }
    
    /**
     * 测试重复用户名注册
     */
    @Test
    void testDuplicateUsernameRegister() {
        // 先注册一个用户
        String timestamp = String.valueOf(System.currentTimeMillis());
        RegisterRequest registerRequest1 = new RegisterRequest();
        registerRequest1.setUserName("duplicatetest" + timestamp);
        registerRequest1.setPassword("123456");
        registerRequest1.setTelephone("1" + timestamp.substring(timestamp.length() - 10));
        registerRequest1.setRealName("重复测试用户1");
        registerRequest1.setIdCard("110101199001011237");

        
        Map<String, Object> result1 = userService.register(registerRequest1);
        assertTrue((Boolean) result1.get("success"));
        
        // 尝试注册相同用户名的用户
        RegisterRequest registerRequest2 = new RegisterRequest();
        registerRequest2.setUserName(registerRequest1.getUserName()); // 相同用户名
        registerRequest2.setPassword("123456");
        registerRequest2.setTelephone("13900000002");
        registerRequest2.setRealName("重复测试用户2");
        registerRequest2.setIdCard("110101199001011238");

        
        Map<String, Object> result2 = userService.register(registerRequest2);
        
        assertNotNull(result2);
        assertFalse((Boolean) result2.get("success"));
        assertEquals("用户名已存在", result2.get("message"));
    }
}
