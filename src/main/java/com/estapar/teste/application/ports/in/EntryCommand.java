package com.estapar.teste.application.ports.in;

import java.time.LocalDateTime;

/**
 * Dados necessários para processar uma entrada.
 * Estou utilizando 'Record' do Java 21 para criar um DTO imutável.
 */
public record EntryCommand(
        String licensePlate,
        String sector,
        LocalDateTime entryTime
) {}