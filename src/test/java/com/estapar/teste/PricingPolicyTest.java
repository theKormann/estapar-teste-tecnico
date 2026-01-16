package com.estapar.teste;

import com.estapar.teste.domain.model.PricingPolicy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

class PricingPolicyTest {

    @DisplayName("Deve calcular o fator dinâmico corretamente baseado na ocupação")
    @ParameterizedTest(name = "Ocupação {0}/{1} -> Fator esperado {2}")
    @CsvSource({
            "20, 100, 0.90",  // < 25% -> Desconto 10%
            "40, 100, 1.00",  // < 50% -> Preço normal
            "60, 100, 1.10",  // < 75% -> Aumento 10%
            "90, 100, 1.25",  // < 100% -> Aumento 25%
            "100, 100, 1.25"  // == 100% -> Aumento 25%
    })
    void shouldCalculateDynamicFactor(long occupancy, long capacity, String expectedFactorStr) {
        BigDecimal factor = PricingPolicy.calculateDynamicFactor(occupancy, capacity);
        Assertions.assertEquals(new BigDecimal(expectedFactorStr), factor);
    }
}