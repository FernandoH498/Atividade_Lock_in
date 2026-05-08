# Changelog — Achados & Perdidos

Todas as mudanças notáveis neste projeto estão documentadas aqui.

---

## [v0.1] — 2026-05-08

### Adicionado
- Estrutura completa do projeto em camadas: `model → dao → service → servlet`
- `Item.java` — modelo de domínio com todos os campos exigidos pelo manual (pág. 8)
- `ConnectionFactory.java` — gerenciamento de conexão via `config.properties`
- `ItemDAO.java` — CRUD completo com `PreparedStatement` (save, findAll, findById, updateStatus, delete)
- `ItemService.java` — validações de negócio (descrição, categoria, local, data)
- `ItemCreateServlet.java` — F01: cadastro de item com validação e upload de foto opcional
- `ItemListServlet.java` — F02: listagem com filtros por status, busca e categoria
- `ItemDetailServlet.java` — F03: exibição de detalhe do item por ID
- `ItemUpdateServlet.java` — F04: marcar item como devolvido
- `ItemDeleteServlet.java` — F05: excluir item com confirmação
- `list.jsp` — tela de listagem com filtros e contadores
- `form.jsp` — formulário de cadastro com simulação de IA (sugestão de categoria)
- `detail.jsp` — tela de detalhe com ações de devolver e excluir
- `erro.jsp` — página de erro amigável
- `schema.sql` — script de criação do banco `achados_perdidos`
- `seed.sql` — 5 registros fictícios sem dados pessoais reais (ECA)
- `config.properties.example` — template de configuração (credenciais fora do Git)
- `README.md` — instruções completas de setup, build e deploy
- `DoD.md` — Definition of Done
- `PR_CHECKLIST.md` — checklist de pull request
- `RISCOS_ETICOS.md` — 3 riscos éticos e mitigações
- `TESTS.md` — 12 casos de teste documentados
- `PROMPTS_IA.md` — registro de uso de IA no projeto

### Segurança
- 100% das queries usando `PreparedStatement` — zero concatenação de SQL
- `config.properties` no `.gitignore` — credenciais fora do repositório
- Dados de seed completamente fictícios (conformidade ECA)

---

## Próximos marcos

- `v0.5` — Semana 2: detalhe, revisão cruzada, testes e riscos
- `v1.0.0` — Semana 3: refatoração, hardening e apresentação final
