package com.estapar.teste.application.usecases;

import com.estapar.teste.application.ports.in.EntryCommand;
import com.estapar.teste.application.ports.in.ParkingOperationsUseCase;
import com.estapar.teste.application.ports.out.GarageConfigRepositoryPort;
import com.estapar.teste.application.ports.out.TicketRepositoryPort;
import com.estapar.teste.domain.exception.ParkingFullException;
import com.estapar.teste.domain.model.PricingPolicy;
import com.estapar.teste.domain.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor // @ Cria construtor para dependências automáticas
public class HandleEntryUseCaseImpl implements ParkingOperationsUseCase {

    // Portas de Saída (BANCO)
    private final GarageConfigRepositoryPort garageConfigPort;
    private final TicketRepositoryPort ticketRepositoryPort;

    @Override
    @Transactional // Garante que tudo aconteça numa transação atômica
    public Ticket handleEntry(EntryCommand command) {
        String sector = command.sector();

        // Busca de Capacidade e ocupação do setor
        long capacity = garageConfigPort.getSectorCapacity(sector);
        long occupancy = garageConfigPort.getCurrentOccupancy(sector);

        // Condição que verifica se está ocupado o setor.
        if (occupancy >= capacity) {
            throw new ParkingFullException(sector);
        }

        // Busca do Preço Base da Garagem para aquele setor
        BigDecimal basePrice = garageConfigPort.getBasePrice(sector);

        // Preço Dinâmico, atendendo o estado atual da ocupação
        BigDecimal factor = PricingPolicy.calculateDynamicFactor(occupancy, capacity);

        // Calculo do preço final que vai valer para o cliente
        BigDecimal priceToCharge = PricingPolicy.calculateEntryPrice(basePrice, factor);

        // Aqui estou iniciando a Entry(Ticket) com base no HandleEntryUseCaseImpl
        Ticket newTicket = new Ticket(
                command.licensePlate(),
                sector,
                command.entryTime(),
                priceToCharge // Salvamos o preço fixado na entrada
        );

        // Ao salvar, o Adapter do banco deve se encarregar de incrementar a ocupação
        return ticketRepositoryPort.save(newTicket);
    }
}
