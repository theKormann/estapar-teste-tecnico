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
        // Reconhecendo como entidade
        TicketEntity entity = toEntity(ticket);

        // Salvando no MySQL
        TicketEntity savedEntity = repository.save(entity);

        // Convertendo Entity -> Domínio (com o ID gerado)
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findActiveByLicensePlate(String licensePlate) {
        return repository.findByLicensePlateAndStatus(licensePlate, com.estapar.teste.domain.model.TicketStatus.ACTIVE)
                .map(this::toDomain);
    }

    // Mappers (para o teste)
    private TicketEntity toEntity(Ticket domain) {
        TicketEntity entity = new TicketEntity();
        entity.setId(domain.getId()); // Se for null (novo), o Hibernate gera
        entity.setLicensePlate(domain.getLicensePlate());
        entity.setSector(domain.getSector());
        entity.setEntryTime(domain.getEntryTime());
        entity.setExitTime(domain.getExitTime());
        entity.setPricePerContext(domain.getPricePerContext());
        entity.setFinalAmount(domain.getFinalAmount());
        entity.setStatus(domain.getStatus());
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
        if(entity.getExitTime() != null) {
            // força o status fechado se tiver data de saída
            try {
                ticket.setStatus(entity.getStatus());
            } catch (Exception e) {}
        }
        return ticket;
    }
}
