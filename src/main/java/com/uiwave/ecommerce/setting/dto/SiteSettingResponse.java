package com.uiwave.ecommerce.setting.dto;

import com.uiwave.ecommerce.setting.model.SiteSetting;

import java.math.BigDecimal;
import java.time.Instant;

public record SiteSettingResponse(
        Long id,
        String siteName,
        String logoUrl,
        String contactEmail,
        String contactPhone,
        String currency,
        int lowStockThreshold,
        BigDecimal freeShippingThreshold,
        BigDecimal taxRate,
        BigDecimal shippingFlatRate,
        Instant updatedAt) {

    public static SiteSettingResponse from(SiteSetting setting) {
        return new SiteSettingResponse(
                setting.getId(),
                setting.getSiteName(),
                setting.getLogoUrl(),
                setting.getContactEmail(),
                setting.getContactPhone(),
                setting.getCurrency(),
                setting.getLowStockThreshold(),
                setting.getFreeShippingThreshold(),
                setting.getTaxRate(),
                setting.getShippingFlatRate(),
                setting.getUpdatedAt());
    }
}
