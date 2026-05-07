-- ============================================================
-- Achados & Perdidos — v0.2 | Migração do banco
-- Adiciona suporte a foto nos itens
-- ============================================================

USE achados_perdidos;

ALTER TABLE itens
    ADD COLUMN foto_path VARCHAR(255) NULL AFTER observacoes;
