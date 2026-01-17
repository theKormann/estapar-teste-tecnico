package com.estapar.teste.infrastructure.adapters.out.client.dto;

public record SpotDTO(
        Long id,
        String sector,
        Double lat,
        Double lng
) {}
