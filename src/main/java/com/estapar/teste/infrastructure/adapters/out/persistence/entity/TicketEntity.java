package com.estapar.teste.infrastructure.adapters.out.persistence.entity;

import com.estapar.teste.domain.model.TicketStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private String sector;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    // Preço cobrado (baseado na regra dinâmica no momento da entrada)
    private BigDecimal pricePerContext;

    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;
}
