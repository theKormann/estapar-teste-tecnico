package com.estapar.teste.infrastructure.config;

import com.estapar.teste.infrastructure.adapters.out.client.GarageSimulatorClient;
import com.estapar.teste.infrastructure.adapters.out.client.dto.GarageConfigResponse;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SectorEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SpotEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSectorRepository;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GarageDataInitializer implements ApplicationRunner {

    private final GarageSimulatorClient simulatorClient;
    private final SpringDataSectorRepository sectorRepository;
    private final SpringDataSpotRepository spotRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("🔄 Iniciando sincronização com o Simulador Estapar...");

            GarageConfigResponse response = simulatorClient.fetchGarageConfiguration();

            if (response != null) {

                // 1. Processar Setores (Garage)
                if (response.garage() != null) {
                    response.garage().forEach(dto -> {
                        SectorEntity entity = new SectorEntity(
                                dto.sector(),
                                dto.max_capacity(),
                                dto.basePrice()
                        );
                        sectorRepository.save(entity);
                        log.info("✅ Setor {} configurado: Capacidade={}, Preço Base={}",
                                entity.getCode(), entity.getCapacity(), entity.getBasePrice());
                    });
                }

                // 2. Processar Vagas Individuais (Spots) - NOVO BLOCO
                if (response.spots() != null) {
                    response.spots().forEach(spotDto -> {
                        SpotEntity spotEntity = new SpotEntity(
                                spotDto.id(),
                                spotDto.sector(),
                                spotDto.lat(),
                                spotDto.lng()
                        );
                        spotRepository.save(spotEntity);
                    });
                    log.info("✅ {} Vagas (Spots) carregadas e salvas no banco.", response.spots().size());
                }
            }

            log.info("🚀 Configuração da garagem concluída com sucesso.");

        } catch (Exception e) {
            log.error("❌ Falha ao buscar configuração do simulador. Verifique se o Docker do simulador está rodando.", e);
        }
    }
}