package com.estapar.teste.application.usecases;

import com.estapar.teste.application.ports.in.EntryCommand;
import com.estapar.teste.application.ports.in.ParkingOperationsUseCase;
import com.estapar.teste.application.ports.out.GarageConfigRepositoryPort;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.exception.ParkingFullException;
import com.estapar.teste.domain.model.PricingPolicy;
import com.estapar.teste.domain.model.Sector;
import com.estapar.teste.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandleEntryUseCaseImpl implements ParkingOperationsUseCase {

    private final GarageConfigRepositoryPort garageConfigPort;
    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    @Transactional
    public Ticket handleEntry(EntryCommand command) {
        // 1. Buscar todos os setores disponíveis na garagem
        List<Sector> allSectors = garageConfigPort.findAllSectors();

        // 2. Lógica de Alocação: Encontrar o primeiro setor que NÃO esteja lotado
        Sector selectedSector = null;
        long currentOccupancyOfSelected = 0;

        for (Sector sector : allSectors) {
            long occupancy = garageConfigPort.getCurrentOccupancy(sector.code());

            // Se tem vaga, escolhe este setor e para a busca
            if (occupancy < sector.capacity()) {
                selectedSector = sector;
                currentOccupancyOfSelected = occupancy;
                break;
                // Feature: Poderíamos iterar todos e pegar o "menos ocupado" (Load Balancing)
            }
        }

        // 3. Se rodou tudo e não achou setor, a garagem inteira está cheia
        if (selectedSector == null) {
            log.warn("⛔ Garagem lotada! Entrada rejeitada para placa {}", command.licensePlate());
            throw new ParkingFullException("GARAGEM_COMPLETA");
        }

        log.info("✅ Veículo {} alocado para o Setor {} (Ocupação: {}/{})",
                command.licensePlate(), selectedSector.code(), currentOccupancyOfSelected, selectedSector.capacity());

        // 4. Calcular Preço Dinâmico
        BigDecimal dynamicFactor = PricingPolicy.calculateDynamicFactor(
                currentOccupancyOfSelected,
                selectedSector.capacity()
        );

        BigDecimal finalEntryPrice = PricingPolicy.calculateEntryPrice(
                selectedSector.basePrice(),
                dynamicFactor
        );

        // 5. Criar e Salvar o Ticket
        Ticket newTicket = new Ticket(
                command.licensePlate(),
                selectedSector.code(), // O sistema decide o sector
                command.entryTime(),
                finalEntryPrice
        );

        return ticketRepositoryPort.save(newTicket);
    }
}