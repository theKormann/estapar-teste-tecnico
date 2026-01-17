package com.estapar.teste;

import com.estapar.teste.application.ports.in.EntryCommand;
import com.estapar.teste.application.usecases.HandleEntryUseCaseImpl;
import com.estapar.teste.application.ports.out.GarageConfigRepositoryPort;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.exception.ParkingFullException;
import com.estapar.teste.domain.model.Sector;
import com.estapar.teste.domain.model.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandleEntryUseCaseTest {
    /*
    Cenário 1 (Alocação Normal): O primeiro setor tem vaga. O carro deve ir para ele.
    Cenário 2 (Failover Inteligente): O setor A está lotado. O sistema deve ignorar o A e alocar no setor B automaticamente.
    Cenário 3 (Garagem Lotada): Setor A e Setor B estão lotados. O sistema deve lançar ParkingFullException.
     */
    @Mock
    private GarageConfigRepositoryPort garageConfigPort;

    @Mock
    private TicketRepositoryPort ticketRepositoryPort;

    @InjectMocks
    private HandleEntryUseCaseImpl useCase;

    @Test
    @DisplayName("Deve alocar vaga no primeiro setor disponível (Setor A)")
    void shouldAllocateToFirstSectorWhenAvailable() {
        // Arrange
        Sector sectorA = new Sector("A", 100L, new BigDecimal("10.00"));
        Sector sectorB = new Sector("B", 100L, new BigDecimal("10.00"));

        // Simula que existem 2 setores
        when(garageConfigPort.findAllSectors()).thenReturn(List.of(sectorA, sectorB));

        // Simula que o setor A tem apenas 50 carros (está livre)
        when(garageConfigPort.getCurrentOccupancy("A")).thenReturn(50L);

        // Mock para retornar o ticket salvo
        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        EntryCommand command = new EntryCommand("ABC-1234", null, LocalDateTime.now());

        // Act
        Ticket ticket = useCase.handleEntry(command);

        // Assert
        assertEquals("A", ticket.getSector()); // Deve ter escolhido o A
        verify(ticketRepositoryPort).save(any(Ticket.class));
        verify(garageConfigPort, never()).getCurrentOccupancy("B"); // Nem precisou checar o B
    }

    @Test
    @DisplayName("Deve pular setor lotado e alocar no próximo (Setor B)")
    void shouldSkipFullSectorAndAllocateToNext() {
        // Arrange
        Sector sectorA = new Sector("A", 100L, new BigDecimal("10.00")); // Capacidade 100
        Sector sectorB = new Sector("B", 100L, new BigDecimal("10.00")); // Capacidade 100

        when(garageConfigPort.findAllSectors()).thenReturn(List.of(sectorA, sectorB));

        // Setor A está LOTADO (100 ocupantes)
        when(garageConfigPort.getCurrentOccupancy("A")).thenReturn(100L);
        // Setor B está VAZIO (0 ocupantes)
        when(garageConfigPort.getCurrentOccupancy("B")).thenReturn(0L);

        when(ticketRepositoryPort.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        EntryCommand command = new EntryCommand("FULL-1234", null, LocalDateTime.now());

        // Act
        Ticket ticket = useCase.handleEntry(command);

        // Assert
        assertEquals("B", ticket.getSector()); // O sistema foi inteligente e escolheu B!
        verify(garageConfigPort).getCurrentOccupancy("A"); // Checou A
        verify(garageConfigPort).getCurrentOccupancy("B"); // Checou B
    }

    @Test
    @DisplayName("Deve lançar exceção se TODOS os setores estiverem lotados")
    void shouldThrowExceptionWhenGarageIsFull() {
        // Arrange
        Sector sectorA = new Sector("A", 10L, new BigDecimal("10.00"));

        when(garageConfigPort.findAllSectors()).thenReturn(List.of(sectorA));
        // Setor A está LOTADO
        when(garageConfigPort.getCurrentOccupancy("A")).thenReturn(10L);

        EntryCommand command = new EntryCommand("SAD-1234", null, LocalDateTime.now());

        // Act & Assert
        ParkingFullException exception = assertThrows(ParkingFullException.class, () -> {
            useCase.handleEntry(command);
        });

        assertEquals("O setor GARAGEM_COMPLETA está lotado. Novas entradas não permitidas.", exception.getMessage());

        verify(ticketRepositoryPort, never()).save(any());
    }
}