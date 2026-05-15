# Changelog — Achados & Perdidos

Todas as mudanças notáveis neste projeto estão documentadas aqui.

---
## [0.2] — 2026-05-07

### Adicionado

#### Banco de Dados
- **`schema_v02.sql`** *(novo)* — tabela `usuarios` com campos de autenticação e controle de bloqueio
- **`schema_v02.sql`** *(novo)* — tabela `admins` para acesso administrativo

#### Autenticação
- **`PasswordUtil`** *(novo)* — hash de senha com SHA-256 + salt aleatório (sem dependência externa)
- **`CodigoUtil`** *(novo)* — gerador de código numérico de 6 dígitos criptograficamente seguro
- **`EmailService`** *(novo)* — envio de e-mail de verificação via JavaMail/SMTP com layout HTML

#### Back-end
- **`UsuarioDAO`** *(novo)* — `save`, `findByEmail`, `findByNomeUsuario`, `incrementarTentativas`, `bloquear`, `desbloquear`, `findBloqueados`
- **`AdminDAO`** *(novo)* — `findByEmail` para autenticação de administradores
- **`UsuarioService`** *(novo)* — `cadastrar` (validação de senha forte), `verificarEmail` (código + expiração), `login` (bloqueio na 3ª tentativa errada), `desbloquear`
- **`AdminService`** *(novo)* — autenticação de administrador com hash de senha
- **`AuthFilter`** *(novo)* — protege `/itens/*`, redireciona para `/login` sem sessão ativa
- **`AdminAuthFilter`** *(novo)* — protege `/admin/*`, redireciona para `/admin/login` sem sessão admin

#### Servlets
- **`CadastroServlet`** *(novo)* — `GET/POST /cadastro`
- **`LoginServlet`** *(novo)* — `GET/POST /login`; cria sessão com `usuarioId` e `usuarioNome`
- **`LogoutServlet`** *(novo)* — `GET /logout`; invalida sessão e redireciona para `/login`
- **`VerificacaoEmailServlet`** *(novo)* — `GET/POST /verificar-email`
- **`AdminLoginServlet`** *(novo)* — `GET/POST /admin/login`
- **`AdminDashboardServlet`** *(novo)* — `GET /admin` lista bloqueados; `POST /admin` desbloqueia

#### Front-end
- **`cadastro.jsp`** *(novo)* — formulário com campos nome de usuário, e-mail, senha e confirmação
- **`verificacao-email.jsp`** *(novo)* — campo para inserção do código de 6 dígitos
- **`login.jsp`** *(novo)* — formulário de login com aviso sobre bloqueio por tentativas
- **`admin/login.jsp`** *(novo)* — login exclusivo para administradores
- **`admin/dashboard.jsp`** *(novo)* — tabela de contas bloqueadas com botão "Desbloquear" por linha

### Alterado
- **`pom.xml`** — adicionada dependência `com.sun.mail:javax.mail:1.6.2`
- **`web.xml`** — adicionado `<session-config>` com timeout de 30 minutos
- **`config.properties.example`** — adicionadas chaves de configuração SMTP

### Segurança
- Senhas nunca armazenadas em texto puro — SHA-256 + salt único por usuário
- Conta bloqueada automaticamente após **3 tentativas de senha erradas**
- Código de verificação de e-mail expira em **15 minutos**
- Rotas protegidas por filtros de sessão (`AuthFilter`, `AdminAuthFilter`)


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
