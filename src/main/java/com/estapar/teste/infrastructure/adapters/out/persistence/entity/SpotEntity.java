package com.estapar.teste.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spots")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotEntity {

    @Id
    private Long id;

    private String sectorCode; // "A", "B"... (Relacionamento lógico)

    private Double lat;
    private Double lng;
}