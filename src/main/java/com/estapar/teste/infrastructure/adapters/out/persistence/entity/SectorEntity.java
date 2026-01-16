package com.estapar.teste.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "sectors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectorEntity {

    @Id
    private String code; // A, B...
    private Long capacity;
    private BigDecimal basePrice;
}
