-- ============================================================
-- Achados & Perdidos - SESI Blumenau | Hackathon v0.1
-- Script de criação do banco de dados
-- Referência: Manual do Hackathon, pág. 8
-- ============================================================

CREATE DATABASE IF NOT EXISTS achados_perdidos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE achados_perdidos;

CREATE TABLE IF NOT EXISTS itens (
    id               INT           NOT NULL AUTO_INCREMENT,
    descricao        VARCHAR(255)  NOT NULL,
    categoria        VARCHAR(100)  NOT NULL,
    local_encontrado VARCHAR(255)  NOT NULL,
    data_encontrado  DATE          NOT NULL,
    status           ENUM('ACHADO','DEVOLVIDO') NOT NULL DEFAULT 'ACHADO',
    data_cadastro    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacoes      TEXT,
    foto_path VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
