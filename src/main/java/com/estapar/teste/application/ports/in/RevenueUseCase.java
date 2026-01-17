package com.estapar.teste.application.ports.in;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueUseCase {
    BigDecimal calculateTotalRevenue(String sector, LocalDate date);
}
