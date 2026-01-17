package com.estapar.teste.application.usecases;
import com.estapar.teste.application.ports.in.ExitCommand;
import com.estapar.teste.application.ports.in.ExitUseCase;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.model.Ticket;
import com.estapar.teste.domain.model.TicketStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleExitUseCase implements ExitUseCase {

    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    @Transactional
    public Ticket handleExit(ExitCommand command) {
        // 1. Busca o ticket ativo (Erro se não achar ou já estiver fechado)
        Ticket ticket = ticketRepositoryPort.findActiveByLicensePlate(command.licensePlate())
                .orElseThrow(() -> new RuntimeException("Ticket ativo não encontrado para EXIT: " + command.licensePlate()));

        // 2. Aqui o Domínio aplica as regras de negócio (Cálculo de tempo, valor e mudança de status)
        ticket.registerExit(command.exitTime());

        // 3. Persiste o estado final no banco
        Ticket savedTicket = ticketRepositoryPort.save(ticket);

        log.info("💰 Saída registrada. Placa: {}, Tempo: {} min, Valor Final: R$ {}",
                savedTicket.getLicensePlate(),
                java.time.Duration.between(savedTicket.getEntryTime(), savedTicket.getExitTime()).toMinutes(),
                savedTicket.getFinalAmount());

        return savedTicket;
    }
}
