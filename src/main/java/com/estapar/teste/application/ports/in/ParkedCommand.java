package com.estapar.teste.application.ports.in;

public record ParkedCommand(
        String licensePlate,
        Double lat,
        Double lng
) {}
