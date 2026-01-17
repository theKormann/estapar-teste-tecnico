package com.estapar.teste.infrastructure.adapters.out.client.dto;

import java.util.List;

public record GarageConfigResponse(
        List<SectorConfigDTO> garage,
        List<SpotDTO> spots
) {}
