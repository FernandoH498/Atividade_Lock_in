# DoD — Definition of Done | Achados & Perdidos

> Template baseado no Apêndice A.1 do Manual do Hackathon.  
> Um item só é "Done" quando **todos** os critérios abaixo forem atendidos.

---

## Critérios Gerais

- [x] Roda no laboratório seguindo o README
- [x] Validações e mensagens claras (sem stack trace para o usuário)
- [ ] Erros 400/404/500 com páginas adequadas — parcialmente (erro.jsp existe; 400/404 em andamento)
- [x] `PreparedStatement` em 100% das queries
- [x] Sem dados reais (ECA) — seed com dados fictícios
- [ ] Evidências no `TESTS.md` preenchidas (testes manuais executados)
- [x] Commits pequenos e claros em português
- [x] Documentação atualizada

---

## Funcionalidades (F01–F05)

| Feature | Implementada | Testada |
|---------|-------------|---------|
| F01 — Cadastrar item | ✅ | ⬜ |
| F02 — Listar itens com filtros | ✅ | ⬜ |
| F03 — Detalhar item por ID | ✅ | ⬜ |
| F04 — Marcar como devolvido | ✅ | ⬜ |
| F05 — Excluir item | ✅ | ⬜ |

---

## Arquivos Obrigatórios

| Arquivo | Status |
|---------|--------|
| `README.md` | ✅ |
| `schema.sql` | ✅ |
| `seed.sql` | ✅ |
| `DoD.md` | ✅ |
| `PR_CHECKLIST.md` | ✅ |
| `TESTS.md` | ✅ (execução manual pendente) |
| `RISCOS_ETICOS.md` | ✅ |
| `CHANGELOG.md` | ✅ |
| `PROMPTS_IA.md` | ✅ |
| `config.properties` fora do Git | ✅ |

---

## Segurança e Ética

- [x] SQL Injection prevenido com `PreparedStatement`
- [x] Credenciais fora do repositório (`config.properties` no `.gitignore`)
- [x] Dados de seed fictícios (conformidade ECA)
- [x] Uso de IA documentado em `PROMPTS_IA.md`

---

## Marco v0.1 — Checklist Final

| Entregável | Status |
|-----------|--------|
| Código (camadas model/dao/service/servlet) | ✅ |
| schema.sql + seed.sql | ✅ |
| README reprodutível | ✅ |
| CRUD completo (F01–F05) | ✅ |
| Documentação obrigatória | ✅ |
| Tag `v0.1` no repositório | ✅ |
