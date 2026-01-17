package com.estapar.teste.infrastructure.adapters.out.client.dto;

import java.math.BigDecimal;

// Mapeia o objeto dentro do array "garage"
public record SectorConfigDTO(
        String sector,         // Ex: "A"
        BigDecimal basePrice,  // Ex: 10.0
        Long max_capacity
) {}
