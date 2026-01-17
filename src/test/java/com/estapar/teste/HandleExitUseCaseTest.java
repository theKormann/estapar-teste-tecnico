package com.estapar.teste;

import com.estapar.teste.application.ports.in.ExitCommand;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.application.usecases.HandleExitUseCase;
import com.estapar.teste.domain.model.Ticket;
import com.estapar.teste.domain.model.TicketStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *Pagamento Normal: Ficou mais de 30min, deve cobrar.
 *
 * Isenção (Grátis): Ficou menos de 30min, valor deve ser Zero.
 *
 * Erro: Tentar sair sem ter entrado (Ticket não encontrado).
 */

@ExtendWith(MockitoExtension.class)
class HandleExitUseCaseTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @InjectMocks
    private HandleExitUseCase useCase;

    @Test
    @DisplayName("Deve cobrar valor corretamente para estadia longa (>30min)")
    void shouldCalculatePriceForLongStay() {
        // Arrange
        String plate = "PAY-1234";
        LocalDateTime entryTime = LocalDateTime.of(2025, 1, 1, 10, 0); // 10:00
        BigDecimal pricePerContext = new BigDecimal("10.00"); // 10 reais/hora

        Ticket ticket = new Ticket(plate, "A", entryTime, pricePerContext);
        ticket.setId(1L);

        when(ticketRepositoryPort.findActiveByLicensePlate(plate)).thenReturn(Optional.of(ticket));
        // Mock do save retorna o próprio objeto alterado
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        // Saída às 11:10 (70 minutos de permanência)
        // Regra: 70 min > 1h -> Cobra 2 horas (Math.ceil) -> 2 * 10.00 = 20.00
        ExitCommand command = new ExitCommand(plate, entryTime.plusMinutes(70));

        // Act
        Ticket result = useCase.handleExit(command);

        // Assert
        assertEquals(TicketStatus.CLOSED, result.getStatus());
        assertEquals(new BigDecimal("20.00"), result.getFinalAmount());
        assertNotNull(result.getExitTime());
        verify(ticketRepositoryPort).save(ticket);
    }

    @Test
    @DisplayName("Deve isentar pagamento para estadia curta (<=30min)")
    void shouldBeFreeForShortStay() {
        // Arrange
        String plate = "FREE-1234";
        LocalDateTime entryTime = LocalDateTime.of(2025, 1, 1, 10, 0);

        Ticket ticket = new Ticket(plate, "A", entryTime, new BigDecimal("10.00"));
        ticket.setId(2L);

        when(ticketRepositoryPort.findActiveByLicensePlate(plate)).thenReturn(Optional.of(ticket));
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        // Saída às 10:29 (29 minutos)
        ExitCommand command = new ExitCommand(plate, entryTime.plusMinutes(29));

        // Act
        Ticket result = useCase.handleExit(command);

        // Assert
        assertEquals(TicketStatus.CLOSED, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getFinalAmount()); // GRÁTIS
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar sair sem ticket ativo")
    void shouldThrowErrorWhenNoTicketFound() {
        // Arrange
        when(ticketRepositoryPort.findActiveByLicensePlate("GHOST")).thenReturn(Optional.empty());

        ExitCommand command = new ExitCommand("GHOST", LocalDateTime.now());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.handleExit(command));
        verify(ticketRepositoryPort, never()).save(any());
    }
}