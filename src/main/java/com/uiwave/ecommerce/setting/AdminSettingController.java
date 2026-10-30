package com.uiwave.ecommerce.setting;

import com.uiwave.ecommerce.setting.dto.SiteSettingRequest;
import com.uiwave.ecommerce.setting.dto.SiteSettingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administración - Configuración", description = "Ajustes generales del sitio")
public class AdminSettingController {

    private final SiteSettingService siteSettingService;

    @Operation(summary = "Obtener la configuración del sitio")
    @GetMapping("/settings")
    public SiteSettingResponse settings() {
        return siteSettingService.get();
    }

    @Operation(summary = "Actualizar la configuración del sitio")
    @PutMapping("/settings")
    public SiteSettingResponse updateSettings(@Valid @RequestBody SiteSettingRequest request) {
        return siteSettingService.update(request);
    }
}
