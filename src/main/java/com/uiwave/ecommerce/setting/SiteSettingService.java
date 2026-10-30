package com.uiwave.ecommerce.setting;

import com.uiwave.ecommerce.setting.dto.SiteSettingRequest;
import com.uiwave.ecommerce.setting.dto.SiteSettingResponse;
import com.uiwave.ecommerce.setting.model.SiteSetting;
import com.uiwave.ecommerce.setting.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private static final long SETTINGS_ID = 1L;

    private final SiteSettingRepository siteSettingRepository;

    @Transactional
    public SiteSettingResponse get() {
        return SiteSettingResponse.from(findOrCreate());
    }

    @Transactional
    public SiteSettingResponse update(SiteSettingRequest request) {
        SiteSetting setting = findOrCreate();

        setting.setSiteName(request.siteName().trim());
        setting.setLogoUrl(normalize(request.logoUrl()));
        setting.setContactEmail(normalize(request.contactEmail()));
        setting.setContactPhone(normalize(request.contactPhone()));
        setting.setCurrency(request.currency());
        setting.setLowStockThreshold(request.lowStockThreshold());
        setting.setFreeShippingThreshold(request.freeShippingThreshold());
        setting.setTaxRate(request.taxRate());
        setting.setShippingFlatRate(request.shippingFlatRate());
        setting.setUpdatedAt(Instant.now());

        return SiteSettingResponse.from(setting);
    }

    // ----------------------------------------------------------------- helpers

    private SiteSetting findOrCreate() {
        return siteSettingRepository.findById(SETTINGS_ID)
                .orElseGet(() -> siteSettingRepository.save(SiteSetting.builder()
                        .siteName("Uiwave")
                        .currency("PEN")
                        .lowStockThreshold(5)
                        .taxRate(BigDecimal.ZERO)
                        .shippingFlatRate(BigDecimal.ZERO)
                        .build()));
    }

    private static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
