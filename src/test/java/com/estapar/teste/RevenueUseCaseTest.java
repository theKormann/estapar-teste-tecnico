package com.estapar.teste;

import com.estapar.teste.application.usecases.RevenueUseCaseImpl;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataTicketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * este teste unitário foca em garantir
 que o Use Case está chamando o repositório
 corretamente e repassando o valor recebido.
 * */

@ExtendWith(MockitoExtension.class)
class RevenueUseCaseTest {

    @Mock
    private SpringDataTicketRepository repository;

    @InjectMocks
    private RevenueUseCaseImpl useCase;

    @Test
    @DisplayName("Deve retornar o valor total somado pelo repositório")
    void shouldReturnTotalRevenueFromRepository() {
        // Arrange
        String sector = "A";
        LocalDate date = LocalDate.of(2025, 1, 1);
        BigDecimal expectedTotal = new BigDecimal("150.00");

        // Simula que o banco fez a query e retornou 150.00
        when(repository.calculateRevenue(sector, date)).thenReturn(expectedTotal);

        // Act
        BigDecimal result = useCase.calculateTotalRevenue(sector, date);

        // Assert
        assertEquals(expectedTotal, result);
        verify(repository).calculateRevenue(sector, date); // Garante que repassou os parâmetros certos
    }

    @Test
    @DisplayName("Deve retornar ZERO quando o repositório não encontrar faturamento")
    void shouldReturnZeroWhenRepositoryReturnsZero() {
        // Arrange
        String sector = "B";
        LocalDate date = LocalDate.now();

        // Simula o retorno de Zero (graças ao COALESCE na query)
        when(repository.calculateRevenue(sector, date)).thenReturn(BigDecimal.ZERO);

        // Act
        BigDecimal result = useCase.calculateTotalRevenue(sector, date);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }
}