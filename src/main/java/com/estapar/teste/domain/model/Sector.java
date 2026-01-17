package com.estapar.teste.domain.model;

import java.math.BigDecimal;

public record Sector(
        String code,
        Long capacity,
        BigDecimal basePrice
) {}
