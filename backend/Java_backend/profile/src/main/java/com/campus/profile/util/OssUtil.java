package com.campus.profile.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class OssUtil {
    
    @Autowired
    private OSS ossClient;
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    
    @Value("${aliyun.oss.domain}")
    private String domain;
    
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
    
    /**
     * 上传头像到OSS
     * 
     * @param file 头像文件
     * @param userId 用户ID
     * @return OSS中的文件key
     * @throws IOException 上传异常
     */
    public String uploadAvatar(MultipartFile file, String userId) throws IOException {
        // 验证文件
        validateFile(file);
        
        // 生成文件key
        String fileKey = generateFileKey(file, userId);
        
        try {
            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, fileKey, file.getInputStream());
            
            // 上传文件
            ossClient.putObject(putObjectRequest);
            
            return fileKey;
        } catch (Exception e) {
            throw new IOException("上传文件到OSS失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除OSS中的文件
     * 
     * @param fileKey 文件key
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return true;
        }
        
        try {
            ossClient.deleteObject(bucketName, fileKey);
            return true;
        } catch (Exception e) {
            System.err.println("删除OSS文件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 生成文件的访问URL（带签名，有效期1小时）
     * 
     * @param fileKey 文件key
     * @return 访问URL
     */
    public String generatePresignedUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }
        
        try {
            // 设置URL过期时间为1小时
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            
            // 生成预签名URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    bucketName, fileKey);
            request.setExpiration(expiration);
            
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            System.err.println("生成预签名URL失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 生成公共访问URL（如果bucket是公共读）
     * 
     * @param fileKey 文件key
     * @return 公共访问URL
     */
    public String generatePublicUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }
        
        return domain + "/" + fileKey;
    }
    
    /**
     * 验证上传的文件
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
     * 生成文件key
     */
    private String generateFileKey(MultipartFile file, String userId) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // 生成时间戳
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // 生成UUID的前8位作为随机字符串
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        
        // 组合文件key：avatars/用户ID_时间戳_随机字符串.扩展名
        return "avatars/" + userId + "_" + timestamp + "_" + randomStr + extension;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    /**
     * 从URL中提取文件key
     */
    public String extractFileKeyFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        // 如果是完整URL，提取文件key部分
        if (url.startsWith("http")) {
            int domainIndex = url.indexOf(domain);
            if (domainIndex >= 0) {
                return url.substring(domainIndex + domain.length() + 1);
            }
        }
        
        // 如果已经是文件key，直接返回
        return url;
    }
}