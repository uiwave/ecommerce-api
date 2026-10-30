package com.uiwave.ecommerce.setting.repository;

import com.uiwave.ecommerce.setting.model.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {
}
