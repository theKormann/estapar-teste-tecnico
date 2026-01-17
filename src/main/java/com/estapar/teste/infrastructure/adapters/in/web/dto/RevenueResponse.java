package com.estapar.teste.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RevenueResponse(
        BigDecimal amount,
        String currency,
        LocalDateTime timestamp
) {
    public static RevenueResponse of(BigDecimal amount) {
        return new RevenueResponse(amount, "BRL", LocalDateTime.now());
    }
}