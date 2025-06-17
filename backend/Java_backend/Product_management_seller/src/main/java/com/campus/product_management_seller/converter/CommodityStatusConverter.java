package com.campus.product_management_seller.converter;

import com.campus.product_management_seller.entity.Commodity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 商品状态枚举转换器
 * 用于在数据库字符串值和枚举之间进行转换
 */
@Converter(autoApply = true)
public class CommodityStatusConverter implements AttributeConverter<Commodity.CommodityStatus, String> {

    @Override
    public String convertToDatabaseColumn(Commodity.CommodityStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public Commodity.CommodityStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        return Commodity.CommodityStatus.fromValue(dbData);
    }
}