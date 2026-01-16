package com.estapar.teste;

import com.estapar.teste.domain.model.PricingPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PricingPolicyTest {

    @Test
    @DisplayName("Deve aplicar desconto de 10% quando lotação for menor que 25%")
    void shouldApplyDiscountWhenLowOccupancy() {
        // Cenário: Capacidade 100, Ocupação 20 (20%)
        long capacity = 100;
        long currentOccupancy = 20;
        BigDecimal basePrice = new BigDecimal("10.00");

        BigDecimal factor = PricingPolicy.calculateDynamicFactor(currentOccupancy, capacity);
        BigDecimal finalEntryPrice = PricingPolicy.calculateEntryPrice(basePrice, factor);

        Assertions.assertEquals(new BigDecimal("0.90"), factor);
        Assertions.assertEquals(new BigDecimal("9.00"), finalEntryPrice);
    }
}
