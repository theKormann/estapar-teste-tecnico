package com.estapar.teste;

import com.estapar.teste.application.ports.in.ParkedCommand;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.application.usecases.HandleParkedUseCase;
import com.estapar.teste.domain.model.Ticket;
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
import static org.mockito.Mockito.*;

/**
 * Este teste é importante para garantir que:
 *
 * O sistema encontra o ticket correto (que deve estar ATIVO).
 *
 * As coordenadas (latitude/longitude) são atualizadas corretamente no Ticket.
 *
 * O ticket atualizado é salvo no banco.
 *
 * O sistema lança erro se não houver ticket ativo para aquela placa (ex: alguém tentando estacionar sem ter dado entrada).
 * */

@ExtendWith(MockitoExtension.class)
class HandleParkedUseCaseTest {

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @InjectMocks
    private HandleParkedUseCase useCase;

    @Test
    @DisplayName("Deve atualizar coordenadas do ticket quando encontrado e ativo")
    void shouldUpdateCoordinatesWhenTicketIsActive() {
        // Arrange
        String plate = "ABC-1234";
        Double lat = -23.5505;
        Double lng = -46.6333;

        // Criamos um ticket "dummy" que já existe no banco
        Ticket existingTicket = new Ticket(plate, "A", LocalDateTime.now(), new BigDecimal("10.00"));
        // Simulamos que ele tem ID 1 (já foi salvo antes)
        existingTicket.setId(1L);

        when(ticketRepositoryPort.findActiveByLicensePlate(plate))
                .thenReturn(Optional.of(existingTicket));

        ParkedCommand command = new ParkedCommand(plate, lat, lng);

        // Act
        useCase.handleParked(command);

        // Assert
        // 1. Verifica se os valores foram atualizados no objeto
        assertEquals(lat, existingTicket.getLat());
        assertEquals(lng, existingTicket.getLng());

        // 2. Verifica se o método save foi chamado passando o ticket alterado
        verify(ticketRepositoryPort).save(existingTicket);
    }

    @Test
    @DisplayName("Deve lançar exceção se não houver ticket ativo para a placa")
    void shouldThrowExceptionWhenNoActiveTicketFound() {
        // Arrange
        String plate = "GHOST-000";
        when(ticketRepositoryPort.findActiveByLicensePlate(plate))
                .thenReturn(Optional.empty()); // Banco não achou nada

        ParkedCommand command = new ParkedCommand(plate, -23.0, -46.0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            useCase.handleParked(command);
        });

        assertEquals("Ticket ativo não encontrado para PARKED: " + plate, exception.getMessage());

        // Garante que NUNCA tenta salvar se deu erro antes
        verify(ticketRepositoryPort, never()).save(any());
    }
}
