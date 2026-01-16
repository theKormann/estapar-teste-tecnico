package com.estapar.teste.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PricingPolicy {
    /**
     * (calculateDynamicFactor) Calcula o ajuste de preço baseaando na ocupação do setor.

     * < 25% ocupação -> -10% (Fator 0.9)
     * < 50% ocupação -> 0% (Fator 1.0)
     * < 75% ocupação -> +10% (Fator 1.1)
     * <= 100% ocupação -> +25% (Fator 1.25)

     */
    public static BigDecimal calculateDynamicFactor(long currentOccupancy, long maxCapacity) {
        if (maxCapacity == 0) return BigDecimal.ONE;

        double percentage = (double) currentOccupancy / maxCapacity;

        if (percentage < 0.25) {
            return new BigDecimal("0.90"); // Desconto de 10%
        } else if (percentage < 0.50) {
            return new BigDecimal("1.00");
        } else if (percentage < 0.75) {
            return new BigDecimal("1.10"); // Aumento de 10%
        } else {
            return new BigDecimal("1.25"); // Aumento de 25%
        }
    }

    /**
     * (calculateEntryPrice) Calcula o preço base ajustado para ser salvo no momento da **ENTRY**.
     * O enunciado diz para usar o `basePrice` da garagem e aplicar a regra dinâmica.
     */
    public static BigDecimal calculateEntryPrice(BigDecimal basePrice, BigDecimal dynamicFactor) {
        return basePrice.multiply(dynamicFactor).setScale(2, RoundingMode.HALF_UP);
    }

}
