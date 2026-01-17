package com.estapar.teste.infrastructure.config;

import com.estapar.teste.infrastructure.adapters.out.client.GarageSimulatorClient;
import com.estapar.teste.infrastructure.adapters.out.client.dto.GarageConfigResponse;
import com.estapar.teste.infrastructure.adapters.out.persistence.entity.SectorEntity;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataSectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j // Anotação para o log do lombok
public class GarageDataInitializer implements ApplicationRunner {

    private final GarageSimulatorClient simulatorClient;
    private final SpringDataSectorRepository sectorRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("🔄 Iniciando sincronização com o Simulador Estapar...");

            GarageConfigResponse response = simulatorClient.fetchGarageConfiguration();

            if (response != null && response.garage() != null) {
                response.garage().forEach(dto -> {
                    SectorEntity entity = new SectorEntity(
                            dto.sector(),
                            dto.max_capacity(), // Mapeia max_capacity (JSON) para capacity (Entity)
                            dto.basePrice()
                    );

                    sectorRepository.save(entity);
                    log.info("✅ Setor {} configurado: Capacidade={}, Preço Base={}",
                            entity.getCode(), entity.getCapacity(), entity.getBasePrice());
                });
            }

            log.info("🚀 Configuração da garagem concluída com sucesso.");

        } catch (Exception e) {
            log.error("❌ Falha ao buscar configuração do simulador. Verifique se o Docker do simulador está rodando.", e);
            // Não aplique throw aqui para não derrubar a aplicação, mas em produção avaliarei isso.
        }
    }
}