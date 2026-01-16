package com.estapar.teste.infrastructure.adapters.out.persistence;

import com.estapar.teste.application.ports.out.GarageConfigRepositoryPort;
import com.estapar.teste.domain.model.TicketStatus;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SectorEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSectorRepository;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GarageConfigRepositoryAdapter implements GarageConfigRepositoryPort {

    private final SpringDataSectorRepository sectorRepository;
    private final SpringDataTicketRepository ticketRepository;

    @Override
    public long getSectorCapacity(String sectorCode) {
        return sectorRepository.findById(sectorCode)
                .map(SectorEntity::getCapacity)
                .orElseThrow(() -> new RuntimeException("Setor não encontrado: " + sectorCode));
    }

    @Override
    public long getCurrentOccupancy(String sectorCode) {
        // A ocupação é calculada contando tickets ativos no banco
        return ticketRepository.countBySectorAndStatus(sectorCode, TicketStatus.ACTIVE);
    }

    @Override
    public BigDecimal getBasePrice(String sectorCode) {
        return sectorRepository.findById(sectorCode)
                .map(SectorEntity::getBasePrice)
                .orElseThrow(() -> new RuntimeException("Preço não configurado para o setor: " + sectorCode));
    }
}