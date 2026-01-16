package com.estapar.teste.application.ports.out;

import com.estapar.teste.domain.model.Ticket;
import java.util.Optional;

public interface TicketRepositoryPort {
    // salva o ticket no repository
    Ticket save(Ticket ticket);

    // procura por uma licença válida para o ticket
    Optional<Ticket> findActiveByLicensePlate(String licensePlate);
}
