package com.estapar.teste;

import com.estapar.teste.domain.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class TicketTest {

    @Test
    void shouldBeFreeIfUnder30Minutes() throws Exception {
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal priceAtEntry = new BigDecimal("10.00");

        Ticket ticket = new Ticket("ABC-1234", "A", entry, priceAtEntry);
        ticket.registerExit(entry.plusMinutes(29));

        Assertions.assertEquals(BigDecimal.ZERO, getFinalAmount(ticket));
    }

    @Test
    void shouldChargeOneHourAfter30Minutes() throws Exception {
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 12, 0);
        BigDecimal priceAtEntry = new BigDecimal("10.00");

        Ticket ticket = new Ticket("ABC-1234", "A", entry, priceAtEntry);
        ticket.registerExit(entry.plusMinutes(35));

        Assertions.assertEquals(new BigDecimal("10.00"), getFinalAmount(ticket));
    }

    private BigDecimal getFinalAmount(Ticket ticket) throws Exception {
        Field field = Ticket.class.getDeclaredField("finalAmount");
        field.setAccessible(true);
        return (BigDecimal) field.get(ticket);
    }
}
