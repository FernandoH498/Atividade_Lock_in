# Definition of Done — Achados & Perdidos

> Baseado nos critérios do Manual do Hackathon.  
> Um item só é considerado concluído quando **todos** os critérios abaixo forem atendidos.

---

## Critérios Gerais (todas as features)

- [ ] Código compila sem erros nem warnings (`mvn clean package`)
- [ ] **100% das queries** utilizam `PreparedStatement` — zero concatenação de SQL
- [ ] Stack trace **não** é exibido para o usuário final (somente mensagens amigáveis)
- [ ] Nenhum dado pessoal real nos seeds, código ou commits
- [ ] Commits atômicos com mensagem descritiva em português
- [ ] Arquivo `config.properties` ausente do repositório Git (presente no `.gitignore`)
- [ ] Código segue a arquitetura em camadas: `model → dao → service → servlet`

---

## F02 — Listagem de Itens (`/itens`)

- [ ] `GET /itens` retorna HTTP 200
- [ ] Listagem exibe: id, descrição, categoria, local, data encontrado, status, data_cadastro
- [ ] Filtro `status=ACHADO` retorna **somente** itens com `status = 'ACHADO'`
- [ ] Filtro `status=DEVOLVIDO` retorna **somente** itens com `status = 'DEVOLVIDO'`
- [ ] Busca `busca=<texto>` filtra por descrição (LIKE, sem distinção de maiúsculas)
- [ ] Sem filtros: retorna **todos** os itens ordenados por `data_cadastro DESC`
- [ ] Filtros combinados (status + descrição) funcionam corretamente
- [ ] Estado vazio exibe mensagem amigável com link para cadastro

---

## F01 — Cadastro de Item (`/itens/novo`)

- [ ] `GET /itens/novo` exibe o formulário sem erros
- [ ] `POST /itens/novo` persiste o item no banco de dados
- [ ] Validação: `descricao` obrigatória e com máximo de 255 caracteres
- [ ] Validação: `categoria` obrigatória e com valor dentro do conjunto permitido
- [ ] Validação: `local_encontrado` obrigatório
- [ ] Validação: `data_encontrado` obrigatória e não pode ser data futura
- [ ] Erro de validação reexibe o formulário com os dados digitados e mensagem clara
- [ ] Sucesso redireciona para `/itens` com mensagem de confirmação (PRG pattern)
- [ ] Status `ACHADO` é aplicado por padrão quando não informado

---

## Banco de Dados

- [ ] `schema.sql` executa sem erros em MySQL 8+ / MariaDB 10+
- [ ] `seed.sql` insere 5 registros fictícios **sem dados pessoais reais**
- [ ] Tabela `itens` contém todos os campos conforme o manual (pág. 8)
- [ ] Colunas e tipos correspondem ao schema definido

---

## Front-end

- [ ] Interface funcional em resoluções ≥ 768px
- [ ] Mensagens de erro do servidor exibidas de forma amigável (sem stack trace)
- [ ] Formulário valida campos obrigatórios no client-side antes do envio
- [ ] Simulação de IA (sugestão de categoria) está presente e documentada em `PROMPTS_IA.md`
- [ ] Datas exibidas no formato brasileiro (dd/MM/yyyy)

---

## Documentação

- [ ] `README.md` com instruções completas de setup (banco + Tomcat)
- [ ] `PROMPTS_IA.md` registra todos os usos de IA no projeto
- [ ] `DoD.md` (este arquivo) atualizado com os critérios da semana

---

## Marco v0.1 — Checklist Final

| Entregável             | Status |
|------------------------|--------|
| pom.xml                | ✅ |
| schema.sql             | ✅ |
| seed.sql               | ✅ |
| config.properties      | ✅ |
| Item.java              | ✅ |
| ConnectionFactory.java | ✅ |
| ItemDAO.java           | ✅ |
| ItemService.java       | ✅ |
| ItemListServlet.java   | ✅ |
| ItemCreateServlet.java | ✅ |
| web.xml                | ✅ |
| list.jsp               | ✅ |
| form.jsp               | ✅ |
| style.css              | ✅ |
| main.js                | ✅ |
| DoD.md                 | ✅ |
| README.md              | ✅ |
| PROMPTS_IA.md          | ✅ |
