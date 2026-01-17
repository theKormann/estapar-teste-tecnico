package com.estapar.teste.application.usecases;

import com.estapar.teste.application.ports.in.ParkedCommand;
import com.estapar.teste.application.ports.in.ParkedUseCase;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleParkedUseCase implements ParkedUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    @Transactional
    public void handleParked(ParkedCommand command) {
        Ticket ticket = ticketRepositoryPort.findActiveByLicensePlate(command.licensePlate())
                .orElseThrow(() -> new RuntimeException("Ticket ativo não encontrado para PARKED: " + command.licensePlate()));

        ticket.confirmParking(command.lat(), command.lng());
        ticketRepositoryPort.save(ticket);

        log.info("📍 Veículo estacionado: {}", command.licensePlate());
    }
}
