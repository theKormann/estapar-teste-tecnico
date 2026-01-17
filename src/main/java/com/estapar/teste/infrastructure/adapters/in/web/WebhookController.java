package com.estapar.teste.infrastructure.adapters.in.web;

import com.estapar.teste.application.ports.in.EntryCommand;
import com.estapar.teste.application.ports.in.ParkedCommand;
import com.estapar.teste.application.ports.in.ParkingOperationsUseCase;
import com.estapar.teste.domain.model.Ticket;
import com.estapar.teste.infrastructure.adapters.in.web.dto.WebhookEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook") // Define a rota base para a URL = http://localhost:3003/webhook
@RequiredArgsConstructor
@Slf4j // logs do lombok
public class WebhookController {

    private final ParkingOperationsUseCase parkingOperationsUseCase;

    @PostMapping
    public ResponseEntity<Void> handleEvent(@RequestBody WebhookEventRequest request) {
        log.info("🔔 Evento recebido: Tipo={}, Placa={}", request.eventType(), request.licensePlate());

        switch (request.eventType()) {
            case "ENTRY":
                handleEntry(request);
                break;
            case "PARKED":
                handleParked(request);
                break;
            case "EXIT":
                log.info("🚧 Evento EXIT recebido (Lógica em construção). Hora Saída: {}", request.exitTime());
                break;
            default:
                log.warn("⚠️ Tipo de evento desconhecido: {}", request.eventType());
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build(); // Retorna 200 OK conforme pedido
    }

    private void handleEntry(WebhookEventRequest request) {

        EntryCommand command = new EntryCommand(
                request.licensePlate(),
                null, // Sector é nulo na entrada, o sistema decide
                request.entryTime()
        );

        Ticket ticket = parkingOperationsUseCase.handleEntry(command);
        log.info("✅ Ticket criado com sucesso: ID={}, Valor Base={}", ticket.getId(), ticket.getPricePerContext());
    }

    private void handleParked(WebhookEventRequest request) {
        ParkedCommand command = new ParkedCommand(
                request.licensePlate(),
                request.lat(),
                request.lng()
        );
        parkingOperationsUseCase.handleParked(command);
    }
}
