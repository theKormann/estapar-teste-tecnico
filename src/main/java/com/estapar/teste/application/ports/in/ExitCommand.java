package com.estapar.teste.application.ports.in;

import java.time.LocalDateTime;

/**
 * DTO que carrega os dados de entrada para o caso de uso de EXIT
 */
public record ExitCommand(
        String licensePlate,
        LocalDateTime exitTime
) {}
