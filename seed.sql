-- ============================================================
-- Achados & Perdidos - SESI Blumenau | Hackathon v0.1
-- Dados fictícios para desenvolvimento
-- Conformidade ECA/LGPD: zero dados pessoais reais
-- ============================================================

USE achados_perdidos;

INSERT INTO itens (descricao, categoria, local_encontrado, data_encontrado, status, observacoes) VALUES
('Mochila escolar preta sem identificação',      'ACESSÓRIOS',   'Ginásio Poliesportivo',      '2026-04-28', 'ACHADO',    'Mochila com estojo e caderno em branco'),
('Carteira marrom de couro',                     'DOCUMENTOS',   'Refeitório Central',          '2026-04-29', 'ACHADO',    'Contém cartões sem nome visível'),
('Óculos de grau com armação azul',              'ACESSÓRIOS',   'Biblioteca',                  '2026-04-25', 'DEVOLVIDO', 'Devolvido ao portador no dia seguinte'),
('Garrafa de água verde 500ml',                  'OUTROS',       'Quadra Esportiva',            '2026-04-30', 'ACHADO',    NULL),
('Fone de ouvido intra-auricular branco',        'ELETRÔNICOS',  'Laboratório de Informática',  '2026-05-01', 'ACHADO',    'Encontrado embaixo de uma mesa no fundo da sala');
