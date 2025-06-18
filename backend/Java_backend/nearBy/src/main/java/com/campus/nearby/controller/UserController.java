package com.campus.nearby.controller;

import com.campus.nearby.exception.ServerException;
import com.campus.nearby.pojo.User;
import com.campus.nearby.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8079", allowCredentials = "true")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/nearby")
    public List<User> updateLocationAndFindNearby(
            @RequestParam("userId") String userId,
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lon") BigDecimal lon
    ) {
        logger.info("[/nearby] Request received: userId={}, lat={}, lon={}", userId, lat, lon);
        try {
            userService.updateUserLocation(userId, lat, lon);
            int radiusMeters = 1000;
            List<User> result = userService.findNearbyUsers(userId, lat.doubleValue(), lon.doubleValue(), radiusMeters);
            logger.info("[/nearby] Returning {} nearby users", result.size());
            return result;
        } catch (ServerException e) {
            logger.error("[/nearby] Internal error occurred: {}", e.getMessage(), e);
            throw e; // 可以替换为自定义异常返回统一格式
        }
    }
}
