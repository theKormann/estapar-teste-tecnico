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
    private BigDecimal pricePerContext;
    private BigDecimal finalAmount;
    private TicketStatus status;

    // Campos para o evento PARKED
    private Double lat;
    private Double lng;

    // Constructor para criar um novo Ticket na Entry (Regra de Negócio)
    public Ticket(String licensePlate, String sector, LocalDateTime entryTime, BigDecimal pricePerContext) {
        this.licensePlate = licensePlate;
        this.sector = sector;
        this.entryTime = entryTime;
        this.pricePerContext = pricePerContext;
        this.status = TicketStatus.ACTIVE;
    }

    // Métodos de Negócio

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
        if (minutes <= 30) return BigDecimal.ZERO;
        long hoursCharged = (long) Math.ceil((double) minutes / 60);
        return pricePerContext.multiply(BigDecimal.valueOf(hoursCharged))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Método de Negócio para processar o PARKED
    public void confirmParking(Double lat, Double lng) {
        if (this.status != TicketStatus.ACTIVE) {
            throw new IllegalStateException("Não é possível estacionar um ticket inativo.");
        }
        this.lat = lat;
        this.lng = lng;
    }

    // Getters

    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public Long getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getSector() { return sector; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public BigDecimal getPricePerContext() { return pricePerContext; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public TicketStatus getStatus() { return status; }

    // Setters

    public void setId(Long id) { this.id = id; }
    public void setLat(Double lat) { this.lat = lat; }
    public void setLng(Double lng) { this.lng = lng; }

    // Setters abaixo são usados apenas pela Infra para reconstituir o estado
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public void setStatus(TicketStatus status) { this.status = status; }
}