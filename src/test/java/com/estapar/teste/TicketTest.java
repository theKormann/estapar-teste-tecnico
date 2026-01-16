package com.estapar.teste;

import com.estapar.teste.domain.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class TicketTest {

    @Test
    @DisplayName("Deve ser grátis se permanência for até 30 minutos") //
    void shouldBeFreeIfUnder30Minutes() {
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal priceAtEntry = new BigDecimal("10.00");

        Ticket ticket = new Ticket("ABC-1234", "A", entry, priceAtEntry);
        ticket.registerExit(entry.plusMinutes(29));

        // Acesso direto via getter, muito mais legível
        Assertions.assertEquals(BigDecimal.ZERO, ticket.getFinalAmount());
    }

    @Test
    @DisplayName("Deve cobrar 1 hora cheia após 30 minutos") //
    void shouldChargeOneHourAfter30Minutes() {
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal priceAtEntry = new BigDecimal("10.00");

        Ticket ticket = new Ticket("ABC-1234", "A", entry, priceAtEntry);
        // 35 minutos = >30min tolerância -> cobra 1 hora cheia
        ticket.registerExit(entry.plusMinutes(35));

        Assertions.assertEquals(new BigDecimal("10.00"), ticket.getFinalAmount());
    }
}