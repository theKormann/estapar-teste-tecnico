package com.estapar.teste.application.ports.in;


public interface ParkedUseCase {
    /**
     *Processa o veículo estacionado
     */
    void handleParked(ParkedCommand command);
}
