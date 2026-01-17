package com.estapar.teste.infrastructure.adapters.out.persistence;

import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.model.Ticket;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.TicketEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TicketConfigRepositoryAdapter implements TicketRepositoryPort {

    private final SpringDataTicketRepository repository;

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = toEntity(ticket);
        TicketEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findActiveByLicensePlate(String licensePlate) {
        return repository.findByLicensePlateAndStatus(licensePlate, com.estapar.teste.domain.model.TicketStatus.ACTIVE)
                .map(this::toDomain);
    }

    private TicketEntity toEntity(Ticket domain) {
        TicketEntity entity = new TicketEntity();
        entity.setId(domain.getId());
        entity.setLicensePlate(domain.getLicensePlate());
        entity.setSector(domain.getSector());
        entity.setEntryTime(domain.getEntryTime());
        entity.setExitTime(domain.getExitTime());
        entity.setPricePerContext(domain.getPricePerContext());
        entity.setFinalAmount(domain.getFinalAmount());
        entity.setStatus(domain.getStatus());
        entity.setLat(domain.getLat());
        entity.setLng(domain.getLng());
        return entity;
    }

    private Ticket toDomain(TicketEntity entity) {
        Ticket ticket = new Ticket(
                entity.getLicensePlate(),
                entity.getSector(),
                entity.getEntryTime(),
                entity.getPricePerContext()
        );

        ticket.setId(entity.getId());
        ticket.setLat(entity.getLat());
        ticket.setLng(entity.getLng());

        // Mapeamento de dados de saída se existirem
        if (entity.getExitTime() != null) {
            ticket.setExitTime(entity.getExitTime());
            ticket.setFinalAmount(entity.getFinalAmount());
            ticket.setStatus(entity.getStatus());
        }

        return ticket;
    }
}