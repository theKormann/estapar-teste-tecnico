package com.estapar.teste.infrastructure.adapters.out.persistence.repository;

import com.estapar.teste.domain.model.TicketStatus;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, Long> {

    // Método crucial para saber a lotação atual: Conta quantos tickets ATIVOS existem num setor
    long countBySectorAndStatus(String sector, TicketStatus status);
    Optional<TicketEntity> findByLicensePlateAndStatus(String licensePlate, TicketStatus status);

    /**
     * Calcula o faturamento total.
     * Retorna 0 se não houver tickets (evita null).
     * Extrai apenas a data (ignora hora) para comparar.
     */

    @Query("SELECT COALESCE(SUM(t.finalAmount), 0) FROM TicketEntity t " +
            "WHERE t.sector = :sector " +
            "AND t.status = 'CLOSED' " +
            "AND CAST(t.exitTime AS LocalDate) = :date")
    BigDecimal calculateRevenue(@Param("sector") String sector, @Param("date") LocalDate date);
    
}