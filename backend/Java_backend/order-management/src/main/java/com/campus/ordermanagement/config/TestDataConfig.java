package com.campus.ordermanagement.config;

import com.campus.ordermanagement.dto.CreateOrderRequest;
import com.campus.ordermanagement.service.OrderService;
import com.campus.ordermanagement.util.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 测试数据配置类
 * 仅在开发环境下生效，用于自动生成测试数据
 */
@Slf4j
@Configuration
@Profile("dev")
@ConditionalOnProperty(name = "order.config.test-data.auto-create-test-data", havingValue = "true")
public class TestDataConfig implements CommandLineRunner {

    @Autowired
    private OrderService orderService;

    private final Random random = new Random();

    // 测试用户ID
    private static final List<String> TEST_BUYER_IDS = Arrays.asList(
            "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
            "bbbbbbbb-cccc-7ddd-eeee-ffffffffffff",
            "cccccccc-dddd-7eee-ffff-000000000000",
            "dddddddd-eeee-7fff-0000-111111111111"
    );

    private static final List<String> TEST_SELLER_IDS = Arrays.asList(
            "ffffffff-0000-7111-2222-333333333333",
            "00000000-1111-7222-3333-444444444444",
            "11111111-2222-7333-4444-555555555555",
            "22222222-3333-7444-5555-666666666666"
    );

    // 测试商品ID
    private static final List<String> TEST_COMMODITY_IDS = Arrays.asList(
            "11111111-2222-7333-4444-555555555555",
            "22222222-3333-7444-5555-666666666666",
            "33333333-4444-7555-6666-777777777777",
            "44444444-5555-7666-7777-888888888888",
            "55555555-6666-7777-8888-999999999999"
    );

    // 测试地点
    private static final List<String> TEST_LOCATIONS = Arrays.asList(
            "上海理工大学南校区",
            "上海理工大学北校区",
            "上海理工大学图书馆",
            "上海理工大学食堂",
            "上海理工大学宿舍楼下",
            "上海理工大学体育馆",
            "上海理工大学实验楼",
            "上海理工大学教学楼"
    );

    // 测试商品价格范围
    private static final double[] PRICE_RANGES = {
            9.99, 19.99, 29.99, 49.99, 99.99, 199.99, 299.99, 499.99, 999.99, 1999.99
    };

    @Override
    public void run(String... args) throws Exception {
        log.info("开始创建测试数据...");
        
        try {
            createTestOrders();
            log.info("测试数据创建完成");
        } catch (Exception e) {
            log.error("创建测试数据失败", e);
        }
    }

    /**
     * 创建测试订单
     */
    private void createTestOrders() {
        // 创建不同状态的订单
        createOrdersWithStatus("PENDING_PAYMENT", 5);
        createOrdersWithStatus("PENDING_TRANSACTION", 3);
        createOrdersWithStatus("COMPLETED", 7);
        
        log.info("已创建15个测试订单");
    }

    /**
     * 创建指定状态的订单
     */
    private void createOrdersWithStatus(String targetStatus, int count) {
        for (int i = 0; i < count; i++) {
            try {
                CreateOrderRequest request = generateRandomOrderRequest();
                
                // 创建订单
                var orderResponse = orderService.createOrder(request);
                String orderId = orderResponse.getOrderId();
                
                // 根据目标状态更新订单
                switch (targetStatus) {
                    case "PENDING_TRANSACTION":
                        orderService.confirmPayment(orderId);
                        break;
                    case "COMPLETED":
                        orderService.confirmPayment(orderId);
                        orderService.completeOrder(orderId);
                        break;
                    // PENDING_PAYMENT 状态不需要额外操作
                }
                
                log.debug("创建测试订单: {} - 状态: {}", orderId, targetStatus);
                
            } catch (Exception e) {
                log.warn("创建测试订单失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 生成随机订单请求
     */
    private CreateOrderRequest generateRandomOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        
        request.setCommodityId(getRandomCommodityId());
        request.setBuyerId(getRandomBuyerId());
        request.setSellerId(getRandomSellerId());
        request.setMoney(BigDecimal.valueOf(getRandomPrice()));
        request.setSaleLocation(getRandomLocation());
        
        return request;
    }

    /**
     * 获取随机买家ID
     */
    private String getRandomBuyerId() {
        return TEST_BUYER_IDS.get(random.nextInt(TEST_BUYER_IDS.size()));
    }

    /**
     * 获取随机卖家ID
     */
    private String getRandomSellerId() {
        return TEST_SELLER_IDS.get(random.nextInt(TEST_SELLER_IDS.size()));
    }

    /**
     * 获取随机商品ID
     */
    private String getRandomCommodityId() {
        return TEST_COMMODITY_IDS.get(random.nextInt(TEST_COMMODITY_IDS.size()));
    }

    /**
     * 获取随机价格
     */
    private double getRandomPrice() {
        return PRICE_RANGES[random.nextInt(PRICE_RANGES.length)];
    }

    /**
     * 获取随机地点
     */
    private String getRandomLocation() {
        return TEST_LOCATIONS.get(random.nextInt(TEST_LOCATIONS.size()));
    }
}

