package com.uiwave.ecommerce.order.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Ventas agregadas de un día para el gráfico del panel. */
public record DailySalesResponse(LocalDate date, int orders, BigDecimal revenue) {
}
