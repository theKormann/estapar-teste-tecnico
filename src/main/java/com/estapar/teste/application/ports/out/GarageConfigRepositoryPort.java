package com.estapar.teste.application.ports.out;

import com.estapar.teste.domain.model.Sector;

import java.math.BigDecimal;
import java.util.List;

public interface GarageConfigRepositoryPort {

    // adicionei este método para buscar todas as opções disponíveis
    List<Sector> findAllSectors();

    // capacidade máxima de um setor
    long getSectorCapacity(String sectorCode);

    // ocupação atual de um setor
    long getCurrentOccupancy(String sectorCode);

    // preço base padrão da garagem para o setor
    BigDecimal getBasePrice(String sectorCode);

}