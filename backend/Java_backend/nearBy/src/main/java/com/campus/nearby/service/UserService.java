package com.campus.nearby.service;

import com.campus.nearby.dao.UserMapper;
import com.campus.nearby.exception.ServerException;
import com.campus.nearby.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final double EARTH_RADIUS = 6371000; // 地球半径（米）

    @Autowired
    private UserMapper userMapper;

    public void updateUserLocation(String userId, BigDecimal lat, BigDecimal lon) {
        logger.info("[updateUserLocation] userId={}, lat={}, lon={}", userId, lat, lon);
        try {
            userMapper.updateUserLocation(userId, lat, lon);
        } catch (Exception e) {
            logger.error("[updateUserLocation] 数据库更新失败: {}", e.getMessage(), e);
            throw new ServerException(50001, "Failed to update user location");
        }
    }

    // 计算两点之间的 Haversine 距离（米）
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private double[] getBoundingBox(double lat, double lon, double radiusMeters) {
        double deltaLat = Math.toDegrees(radiusMeters / EARTH_RADIUS);
        double deltaLon = Math.toDegrees(radiusMeters / (EARTH_RADIUS * Math.cos(Math.toRadians(lat))));
        return new double[]{lat - deltaLat, lat + deltaLat, lon - deltaLon, lon + deltaLon};
    }

    public List<User> findNearbyUsers(String userId, double lat, double lon, double radiusMeters) {
        logger.info("[findNearbyUsers] userId={}, lat={}, lon={}, radiusMeters={}", userId, lat, lon, radiusMeters);

        double[] bbox = getBoundingBox(lat, lon, radiusMeters);
        logger.debug("[findNearbyUsers] bounding box: latMin={}, latMax={}, lonMin={}, lonMax={}",
                bbox[0], bbox[1], bbox[2], bbox[3]);

        List<User> candidates;
        try {
            candidates = userMapper.findUsersInBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], userId);
            logger.debug("[findNearbyUsers] candidate count from DB: {}", candidates.size());
        } catch (Exception e) {
            logger.error("[findNearbyUsers] 查询候选用户失败: {}", e.getMessage(), e);
            throw new ServerException(50002, "Failed to query nearby users");
        }

        List<User> nearbyUsers = candidates.stream()
                .map(u -> {
                    double dist = haversine(lat, lon,
                            u.getUserLocLatitude().doubleValue(),
                            u.getUserLocLongitude().doubleValue());
                    logger.debug("User {} distance: {}m", u.getUserId(), dist);
                    return new AbstractMap.SimpleEntry<>(u, dist);
                })
                .filter(entry -> entry.getValue() <= radiusMeters)
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        logger.info("[findNearbyUsers] nearby user count: {}", nearbyUsers.size());
        return nearbyUsers;
    }
}
