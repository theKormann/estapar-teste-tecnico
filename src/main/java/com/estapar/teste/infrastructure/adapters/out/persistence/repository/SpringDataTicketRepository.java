package com.estapar.teste.infrastructure.adapters.out.persistence.repository;

import com.estapar.teste.domain.model.TicketStatus;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, Long> {

    // Método crucial para saber a lotação atual: Conta quantos tickets ATIVOS existem num setor
    long countBySectorAndStatus(String sector, TicketStatus status);

    Optional<TicketEntity> findByLicensePlateAndStatus(String licensePlate, TicketStatus status);
}