package com.estapar.teste.domain.exception;


//aplica uma exception caso o setor esteja lotado.
public class ParkingFullException extends RuntimeException {
    public ParkingFullException(String sector) {
        super("O setor " + sector + " está lotado. Novas entradas não permitidas.");
    }
}
