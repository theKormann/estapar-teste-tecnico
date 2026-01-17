package com.estapar.teste.application.ports.in;


import com.estapar.teste.domain.model.Ticket;

public interface EntryUseCase {
    /**
     * Processa a entrada do veículo.
     * Retorna o Ticket criado.
     */
    Ticket handleEntry(EntryCommand command);
}
