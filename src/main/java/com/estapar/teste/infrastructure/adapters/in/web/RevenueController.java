package com.estapar.teste.infrastructure.adapters.in.web;

import com.estapar.teste.application.ports.in.RevenueUseCase;
import com.estapar.teste.infrastructure.adapters.in.web.dto.RevenueRequest;
import com.estapar.teste.infrastructure.adapters.in.web.dto.RevenueResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
@Slf4j
public class RevenueController {

    private final RevenueUseCase revenueUseCase;

    // O teste pede um GET com Body.
    @GetMapping
    public ResponseEntity<RevenueResponse> getRevenue(@RequestBody RevenueRequest request) {
        log.info("📊 Calculando receita. Setor: {}, Data: {}", request.sector(), request.date());

        BigDecimal total = revenueUseCase.calculateTotalRevenue(request.sector(), request.date());

        return ResponseEntity.ok(RevenueResponse.of(total));
    }
}