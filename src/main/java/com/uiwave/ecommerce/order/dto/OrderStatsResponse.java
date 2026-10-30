package com.uiwave.ecommerce.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderStatsResponse(
        long totalUsers,
        long totalProducts,
        long totalOrders,
        long pendingOrders,
        BigDecimal totalRevenue,
        List<DailySalesResponse> salesByDay,
        List<LowStockProductResponse> lowStock) {
}
