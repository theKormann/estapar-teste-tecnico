

# 🚗 Estapar Parking Management System

Este projeto é uma solução de backend para gerenciamento de estacionamento, desenvolvida como parte do desafio técnico para a vaga de Desenvolvedor Backend da Estapar.

O sistema gerencia o ciclo de vida de veículos (Entrada, Estacionamento, Saída), aplica regras de negócio complexas como precificação dinâmica e alocação inteligente de vagas, além de fornecer relatórios financeiros.

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.5.9
* **Banco de Dados:** MySQL 8.0
* **Migração de Dados:** Flyway Migration
* **Testes:** JUnit 5, Mockito
* **Infraestrutura:** Docker & Docker Compose
* **Arquitetura:** Arquitetura Hexagonal (Ports & Adapters)

## 🏗️ Arquitetura do Projeto

O projeto segue estritamente os princípios da **Arquitetura Hexagonal**, garantindo desacoplamento entre as regras de negócio e detalhes de infraestrutura.

* **Domain:** Contém as entidades (`Ticket`, `Sector`, `PricingPolicy`) e regras de negócio puras. Não depende de frameworks.
* **Application (Ports & Use Cases):** Orquestra os fluxos de entrada (`Entry`, `Parked`, `Exit`, `Revenue`). Define as interfaces (Portas) que o mundo externo deve implementar.
* **Infrastructure (Adapters):** Implementações reais. Controladores REST (`Web`), Persistência com Spring Data JPA (`Persistence`) e Clientes HTTP (`Client`).

## ✨ Funcionalidades Principais

1. **Sincronização Automática:** Ao iniciar, o sistema consome a API do Simulador da Estapar e carrega a configuração de **Setores** e **Vagas** (Spots) no banco de dados local.
2. **Alocação Inteligente (ENTRY):** O sistema determina automaticamente o melhor setor para o veículo com base na disponibilidade, sem exigir intervenção manual.
3. **Precificação Dinâmica:** O preço base é ajustado no momento da entrada conforme a lotação do setor (<25% tem desconto, >75% tem acréscimo).
4. **Geolocalização (PARKED):** Atualização da posição física (lat/long) do veículo após a entrada.
5. **Cobrança e Saída (EXIT):** Cálculo automático do valor final, respeitando a regra de 30 minutos de tolerância e arredondamento por hora cheia.
6. **Relatório Financeiro:** Endpoint dedicado para consulta de faturamento por data e setor.

## 🚀 Como Rodar o Projeto

### Pré-requisitos

* Docker e Docker Compose instalados.
* Java 21 (Opcional, caso queira rodar fora do Docker).

### Passo 1: Subir a Infraestrutura

O projeto utiliza um `docker-compose.yml` para orquestrar o Banco de Dados MySQL e o Simulador da Estapar.

```bash
docker-compose up -d

```

* **MySQL:** Porta 3306 (Login: `root` / Senha: `root`)
* **Simulador:** Porta 8080 (Rede Host)

### Passo 2: Executar a Aplicação

A aplicação utiliza o Maven Wrapper, não é necessário ter o Maven instalado globalmente.

**Linux/Mac:**

```bash
./mvnw spring-boot:run

```

**Windows:**

```cmd
mvnw.cmd spring-boot:run

```

A aplicação iniciará na porta **3003**.

> **Nota:** O Flyway executará automaticamente os scripts de criação de tabelas (`V1__create_tables.sql`) na inicialização.

### Passo 3: Testes Automatizados

Para rodar a suíte de testes unitários:

```bash
./mvnw test

```

## 📡 Documentação da API

A aplicação expõe os seguintes endpoints:

### 1. Webhook de Eventos

Recebe eventos do simulador ou chamadas manuais.
**POST** `/webhook`

**Exemplos de Payloads:**

* **Entrada (ENTRY):**
```json
{
  "event_type": "ENTRY",
  "license_plate": "ABC-1234",
  "entry_time": "2025-01-20T10:00:00"
}

```


* **Estacionado (PARKED):**
```json
{
  "event_type": "PARKED",
  "license_plate": "ABC-1234",
  "lat": -23.561684,
  "lng": -46.655981
}

```


* **Saída (EXIT):**
```json
{
  "event_type": "EXIT",
  "license_plate": "ABC-1234",
  "exit_time": "2025-01-20T12:00:00"
}

```



### 2. Relatório de Receita

Consulta o faturamento total de um setor em uma data específica.
**GET** `/revenue` (Nota: Aceita JSON no Body conforme especificação do desafio).

**Payload:**

```json
{
  "date": "2025-01-20",
  "sector": "A"
}

```

**Response:**
json
{
  "amount": 20.00,
  "currency": "BRL",
  "timestamp": "2025-01-20T12:00:05"
}


Desenvolvido com ❤️ e Java 21.