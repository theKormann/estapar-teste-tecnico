package com.estapar.teste.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {

    private Long id;
    private String licensePlate;
    private String sector;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    // Preço base calculado assim que assumida uma Entry
    private BigDecimal pricePerContext;

    private BigDecimal finalAmount;
    private TicketStatus status;

    // Constructor para criar um novo Ticket na Entry
    public Ticket(String licensePlate, String sector, LocalDateTime entryTime, BigDecimal pricePerContext) {
        this.licensePlate = licensePlate;
        this.sector = sector;
        this.entryTime = entryTime;
        this.pricePerContext = pricePerContext;
        this.status = TicketStatus.ACTIVE;
    }

    // Métodos de negócio
    public void registerExit(LocalDateTime exitTime) {
        if (this.status != TicketStatus.ACTIVE) {
            throw new IllegalStateException("Ticket já finalizado.");
        }
        this.exitTime = exitTime;
        this.finalAmount = calculateTotal();
        this.status = TicketStatus.CLOSED;
    }

    private BigDecimal calculateTotal() {
        if (exitTime == null || entryTime == null) return BigDecimal.ZERO;

        long minutes = Duration.between(entryTime, exitTime).toMinutes();

        // Primeiros 30min grátis
        if (minutes <= 30) {
            return BigDecimal.ZERO;
        }

        // Regra de arredondamento do horário 30min = 1 hora...
        long hoursCharged = (long) Math.ceil((double) minutes / 60);

        return pricePerContext.multiply(BigDecimal.valueOf(hoursCharged))
                .setScale(2, RoundingMode.HALF_UP);
    }
}