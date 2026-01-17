package com.estapar.teste.application.usecases;

import com.estapar.teste.application.ports.in.RevenueUseCase;
import com.estapar.teste.infrastructure.adapters.out.persistence.repository.SpringDataTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RevenueUseCaseImpl implements RevenueUseCase {

    private final SpringDataTicketRepository repository;

    @Override
    public BigDecimal calculateTotalRevenue(String sector, LocalDate date) {
        return repository.calculateRevenue(sector, date);
    }
}