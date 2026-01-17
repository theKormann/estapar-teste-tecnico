package com.estapar.teste.infrastructure.config;

import com.estapar.teste.infrastructure.adapters.out.client.GarageSimulatorClient;
import com.estapar.teste.infrastructure.adapters.out.client.dto.GarageConfigResponse;
import com.estapar.teste.infrastructure.adapters.out.client.dto.SectorConfigDTO;
import com.estapar.teste.infrastructure.adapters.out.client.dto.SpotDTO;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SpotEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSectorRepository;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSpotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageDataInitializerTest {

    @Mock
    private GarageSimulatorClient simulatorClient;

    @Mock
    private SpringDataSectorRepository sectorRepository;

    @Mock
    private SpringDataSpotRepository spotRepository;

    @InjectMocks
    private GarageDataInitializer initializer;

    @Test
    @DisplayName("Deve salvar Spots (Vagas) quando o simulador retornar a lista")
    void shouldPersistSpotsWhenClientReturnsData() {
        // Arrange
        // Cria os dados simulados (DTOs que viriam da API)
        SpotDTO spotDto1 = new SpotDTO(1L, "A", -23.00, -46.00);
        SpotDTO spotDto2 = new SpotDTO(2L, "B", -23.01, -46.01);

        // Setup da resposta completa (incluindo garage/setores para não dar null pointer)
        GarageConfigResponse mockResponse = new GarageConfigResponse(
                List.of(new SectorConfigDTO("A", BigDecimal.TEN, 100L)),
                List.of(spotDto1, spotDto2) // <--- Lista de Spots
        );

        when(simulatorClient.fetchGarageConfiguration()).thenReturn(mockResponse);

        // Act
        initializer.run(mock(ApplicationArguments.class));

        // Assert
        // Capturamos o que foi passado para o método save() do repositório de vagas
        ArgumentCaptor<SpotEntity> spotCaptor = ArgumentCaptor.forClass(SpotEntity.class);

        // Verifica se o save foi chamado 2 vezes (uma para cada spot)
        verify(spotRepository, times(2)).save(spotCaptor.capture());

        List<SpotEntity> savedSpots = spotCaptor.getAllValues();

        // Valida o primeiro Spot
        SpotEntity spot1 = savedSpots.get(0);
        assertEquals(1L, spot1.getId());
        assertEquals("A", spot1.getSectorCode());
        assertEquals(-23.00, spot1.getLat());

        // Valida o segundo Spot
        SpotEntity spot2 = savedSpots.get(1);
        assertEquals(2L, spot2.getId());
        assertEquals("B", spot2.getSectorCode());
    }

    @Test
    @DisplayName("Não deve tentar salvar Spots se a lista vier nula")
    void shouldNotSaveSpotsIfListIsNull() {
        // Arrange
        // Resposta apenas com setores, mas spots null
        GarageConfigResponse mockResponse = new GarageConfigResponse(
                List.of(new SectorConfigDTO("A", BigDecimal.TEN, 100L)),
                null // Lista de Spots NULA
        );

        when(simulatorClient.fetchGarageConfiguration()).thenReturn(mockResponse);

        // Act
        initializer.run(mock(ApplicationArguments.class));

        // Assert
        verify(sectorRepository, atLeastOnce()).save(any()); // Setores ok
        verify(spotRepository, never()).save(any()); // Spots nunca devem ser chamados
    }

    @Test
    @DisplayName("Deve lidar com erro no cliente sem quebrar a aplicação")
    void shouldHandleClientErrorGracefully() {
        // Arrange
        when(simulatorClient.fetchGarageConfiguration()).thenThrow(new RuntimeException("Simulador offline"));

        // Act
        // Não deve lançar exceção (tem try-catch dentro)
        initializer.run(mock(ApplicationArguments.class));

        // Assert
        verify(spotRepository, never()).save(any());
    }
}