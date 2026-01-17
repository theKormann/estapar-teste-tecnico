package com.estapar.teste.infrastructure.adapters.in.web.dto;

import java.time.LocalDate;

public record RevenueRequest(
        LocalDate date,
        String sector
) {}