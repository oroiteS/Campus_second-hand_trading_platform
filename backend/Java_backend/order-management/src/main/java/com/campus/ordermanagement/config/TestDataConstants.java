package com.campus.ordermanagement.config;

/**
 * 测试数据常量类
 * 提供Swagger测试时使用的示例数据
 */
public class TestDataConstants {

    // Swagger示例数据
    public static final String EXAMPLE_ORDER_REQUEST = """
        {
          "commodityId": "11111111-2222-7333-4444-555555555555",
          "buyerId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
          "sellerId": "ffffffff-0000-7111-2222-333333333333",
          "money": 299.99,
          "saleLocation": "上海理工大学南校区"
        }
        """;

    public static final String EXAMPLE_UPDATE_STATUS_REQUEST = """
        {
          "newStatus": "PENDING_TRANSACTION",
          "operatorUserId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee",
          "remark": "买家已完成付款"
        }
        """;

    public static final String EXAMPLE_CONFIRM_PAYMENT_REQUEST = """
        {
          "operatorUserId": "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee"
        }
        """;

    public static final String EXAMPLE_COMPLETE_ORDER_REQUEST = """
        {
          "operatorUserId": "ffffffff-0000-7111-2222-333333333333"
        }
        """;

    // 测试用UUID
    public static final String TEST_ORDER_ID = "01234567-89ab-7def-0123-456789abcdef";
    public static final String TEST_BUYER_ID = "aaaaaaaa-bbbb-7ccc-dddd-eeeeeeeeeeee";
    public static final String TEST_SELLER_ID = "ffffffff-0000-7111-2222-333333333333";
    public static final String TEST_COMMODITY_ID = "11111111-2222-7333-4444-555555555555";

    // 私有构造函数，防止实例化
    private TestDataConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
