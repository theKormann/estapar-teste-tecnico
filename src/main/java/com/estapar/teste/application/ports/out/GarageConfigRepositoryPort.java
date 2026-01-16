package com.estapar.teste.application.ports.out;

import java.math.BigDecimal;

public interface GarageConfigRepositoryPort {

    // capacidade máxima de um setor
    long getSectorCapacity(String sectorCode);

    // ocupação atual de um setor
    long getCurrentOccupancy(String sectorCode);

    // preço base padrão da garagem para o setor
    BigDecimal getBasePrice(String sectorCode);

}