package com.estapar.teste.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record WebhookEventRequest(
        @JsonProperty("event_type") String eventType,   // ENTRY, PARKED, EXIT
        @JsonProperty("license_plate") String licensePlate,

        // Campos específicos de ENTRY/EXIT
        @JsonProperty("entry_time") LocalDateTime entryTime,
        @JsonProperty("exit_time") LocalDateTime exitTime,

        // Campos específicos de PARKED
        Double lat,
        Double lng
) {}
