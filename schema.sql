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

CREATE TABLE IF NOT EXISTS usuarios (
    id                   INT           NOT NULL AUTO_INCREMENT,
    username             VARCHAR(50)   NULL UNIQUE,
    email                VARCHAR(255)  NULL UNIQUE,
    nome                 VARCHAR(150)  NOT NULL,
    senha_hash           VARCHAR(255)  NOT NULL,
    papel                ENUM('USUARIO','ADMIN') NOT NULL DEFAULT 'USUARIO',
    status               ENUM('PENDENTE','ATIVO','BLOQUEADO') NOT NULL DEFAULT 'PENDENTE',
    codigo_verificacao   VARCHAR(10)   NULL,
    codigo_expira        DATETIME      NULL,
    tentativas_falhas    TINYINT       NOT NULL DEFAULT 0,
    criado_em            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login         DATETIME      NULL,
    PRIMARY KEY (id),
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
