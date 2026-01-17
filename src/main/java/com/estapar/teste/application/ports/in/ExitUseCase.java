package com.estapar.teste.application.ports.in;

import com.estapar.teste.domain.model.Ticket;

public interface ExitUseCase {
    Ticket handleExit(ExitCommand command);
}
