-- V1__create_tables.sql

-- 1. Tabela de Setores (Configuration)
CREATE TABLE sectors (
    code VARCHAR(10) NOT NULL,
    capacity BIGINT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (code)
);

-- 2. Tabela de Vagas (Spots vindos do Simulador)
CREATE TABLE spots (
    id BIGINT NOT NULL,
    sector_code VARCHAR(10),
    lat DOUBLE,
    lng DOUBLE,
    PRIMARY KEY (id)
);

-- 3. Tabela de Tickets (Operação)
CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT NOT NULL,
    license_plate VARCHAR(20) NOT NULL,
    sector VARCHAR(10) NOT NULL,
    entry_time DATETIME(6) NOT NULL,
    exit_time DATETIME(6),
    price_per_context DECIMAL(10,2), -- Preço dinâmico fixado na entrada
    final_amount DECIMAL(10,2),      -- Valor final calculado na saída
    status VARCHAR(20) NOT NULL,     -- ACTIVE, CLOSED
    lat DOUBLE,                      -- Atualizado no PARKED
    lng DOUBLE,                      -- Atualizado no PARKED
    PRIMARY KEY (id)
);

-- Índices para performance
CREATE INDEX idx_ticket_plate_status ON tickets (license_plate, status);
CREATE INDEX idx_ticket_sector_status ON tickets (sector, status);