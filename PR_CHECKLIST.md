# PR_CHECKLIST — Achados & Perdidos

Checklist a ser preenchido antes de qualquer merge ou entrega.

---

## v0.1 — Marco Semana 1

- [x] Roda no Tomcat seguindo o README
- [x] `PreparedStatement` em 100% das queries (nenhuma concatenação de SQL)
- [x] `config.properties` ausente do repositório (está no `.gitignore`)
- [x] `schema.sql` executa sem erros (MySQL 8+ / MariaDB 10+)
- [x] `seed.sql` insere 5 registros fictícios sem dados pessoais reais
- [x] `TESTS.md` atualizado com casos de teste documentados
- [x] `README.md` com instruções completas de setup, build e deploy
- [x] `PROMPTS_IA.md` atualizado com todos os usos de IA
- [x] `DoD.md` atualizado com critérios da semana
- [x] `RISCOS_ETICOS.md` com 3 riscos e mitigações documentados
- [x] `CHANGELOG.md` com entrada v0.1
- [x] Stack trace não exposto ao usuário final (mensagens amigáveis)
- [x] Commits atômicos com mensagens descritivas em português
- [x] Arquitetura em camadas respeitada: `model → dao → service → servlet`
