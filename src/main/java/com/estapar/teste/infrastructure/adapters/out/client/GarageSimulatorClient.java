package com.estapar.teste.infrastructure.adapters.out.client;

import com.estapar.teste.infrastructure.adapters.out.client.dto.GarageConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GarageSimulatorClient {

    private final RestClient restClient;

    public GarageSimulatorClient(@Value("${simulator.url}") String simulatorUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(simulatorUrl)
                .build();
    }

    public GarageConfigResponse fetchGarageConfiguration() {
        return restClient.get()
                .uri("/garage") // Endpoint indicado no documento
                .retrieve()
                .body(GarageConfigResponse.class);
    }
}
