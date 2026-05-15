# Registro de Uso de Inteligência Artificial

> Conforme exigido pelo Manual do Hackathon, este arquivo documenta **todos** os usos de IA no projeto de forma transparente.

---

## Ferramenta Utilizada

**Claude (Anthropic)** — acessado via Claude Code CLI  
Modelo: Claude Sonnet 4.6

---

## Sprint 1 — Marco v0.1 (Semana 1)

### Uso 1 — Geração da Estrutura Completa do Projeto

**Data:** 07/05/2026  
**Prompt enviado:**

> Atue como um Desenvolvedor Java Sênior. Inicie o desenvolvimento da Semana 1 do projeto "Achados & Perdidos (Simulado)" para um Hackathon técnico de 3 semanas. O foco é criar um MVP robusto, ético e rastreável, seguindo rigorosamente o manual_hackathon_achados_perdidos_sem_capa.pdf e o criterios_avaliacao.xlsx. [...]

**Artefatos gerados pela IA:**

| Arquivo | Descrição |
|---------|-----------|
| `schema.sql` | Schema do banco de dados |
| `seed.sql` | 5 registros fictícios de teste |
| `Item.java` | Model da entidade |
| `ConnectionFactory.java` | Fábrica de conexão JDBC |
| `ItemDAO.java` | Acesso a dados com PreparedStatement |
| `ItemService.java` | Regras de negócio e validações |
| `ItemListServlet.java` | Servlet de listagem (F02) |
| `ItemCreateServlet.java` | Servlet de cadastro (F01) |
| `web.xml` | Configuração do servlet container |
| `list.jsp` | Interface de listagem |
| `form.jsp` | Formulário de cadastro |
| `style.css` | Estilização completa |
| `main.js` | Interatividade e validação client-side |
| `DoD.md` | Definition of Done |
| `README.md` | Instruções de setup |
| `PROMPTS_IA.md` | Este arquivo |

**Revisão humana aplicada:**
- Verificação dos nomes de tabela e colunas conforme o manual (pág. 8)
- Ajuste das credenciais no `config.properties` para o ambiente local
- Validação das regras de negócio no `ItemService.java`
- Teste dos filtros de listagem (F02)

---

### Uso 2 — Simulação de IA no Front-end (Sugestão de Categoria)

**Funcionalidade:** O formulário de cadastro exibe um painel "Assistente de Categorização" que sugere automaticamente a categoria do item conforme o usuário digita a descrição.

**Como funciona:** O algoritmo em `main.js` (função `suggestCategory`) compara as palavras da descrição com um dicionário de palavras-chave mapeadas por categoria. A sugestão aparece após 450ms de inatividade (debounce) e o usuário pode aceitar com um clique.

**Importante:** Esta funcionalidade é uma **simulação** — não utiliza uma API de IA real. O objetivo é demonstrar o conceito de assistência por IA dentro dos limites técnicos do hackathon.

**Mapeamento de palavras-chave:**

| Categoria | Palavras-chave exemplo |
|-----------|----------------------|
| ELETRÔNICOS | celular, fone, headphone, carregador, notebook, tablet, câmera |
| DOCUMENTOS | carteira, rg, cpf, documento, identidade, cartão, habilitação |
| ROUPAS | casaco, jaqueta, blusa, camiseta, tênis, sapato, uniforme |
| ACESSÓRIOS | mochila, bolsa, relógio, guarda-chuva, óculos, garrafa |
| OUTROS | (fallback para itens não classificados) |

---

## Considerações Éticas

- Nenhum dado pessoal foi fornecido à IA durante os prompts
- Os dados de seed gerados são completamente fictícios (conformidade ECA/LGPD)
- O código gerado foi revisado para garantir `PreparedStatement` em 100% das queries
- O uso de IA está documentado de forma transparente neste arquivo

### Uso 3 — Sistema de Login com Bloqueio e Painel Admin

**Data:** 16/05/2026  
**Prompt enviado:**

> Primeira coisa: Implementar um sistema de login com nome de usuario email e senha
> forte alem de uma verificação de email por código se voce com uma conta já criada
> errar 3 vezes a senha ela sera bloqueada alem disso foi criada um login de adm que
> pode desbloquear as contas.

**Artefatos gerados pela IA:**

| Arquivo | Descrição |
|---------|-----------|
| `schema_v02.sql` | Tabelas `usuarios` e `admins` |
| `PasswordUtil.java` | Hash SHA-256 com salt aleatório |
| `CodigoUtil.java` | Gerador de código numérico de 6 dígitos |
| `Usuario.java` | Model do usuário |
| `Admin.java` | Model do administrador |
| `UsuarioDAO.java` | Acesso ao banco: save, findByEmail, bloquear, desbloquear, incrementarTentativas |
| `AdminDAO.java` | Acesso ao banco: findByEmail (admin) |
| `UsuarioService.java` | Regras: validação de senha forte, bloqueio na 3ª tentativa, verificação de código |
| `AdminService.java` | Autenticação do administrador |
| `EmailService.java` | Envio de código de verificação via JavaMail/SMTP |
| `AuthFilter.java` | Protege `/itens/*` — redireciona para login se sem sessão |
| `AdminAuthFilter.java` | Protege `/admin/*` — redireciona para admin/login se sem sessão |
| `CadastroServlet.java` | `GET/POST /cadastro` |
| `LoginServlet.java` | `GET/POST /login` |
| `LogoutServlet.java` | `GET /logout` — invalida sessão |
| `VerificacaoEmailServlet.java` | `GET/POST /verificar-email` |
| `AdminLoginServlet.java` | `GET/POST /admin/login` |
| `AdminDashboardServlet.java` | `GET/POST /admin` — lista bloqueados e desbloqueia |
| `cadastro.jsp` | Tela de cadastro |
| `verificacao-email.jsp` | Tela de inserção do código |
| `login.jsp` | Tela de login |
| `admin/login.jsp` | Tela de login do administrador |
| `admin/dashboard.jsp` | Painel com lista de contas bloqueadas |

**Revisão humana aplicada:**
- Verificação do regex de senha forte (maiúscula, minúscula, número, símbolo, mín. 8 chars)
- Confirmação do limite de 3 tentativas antes do bloqueio
- Validação do fluxo completo: cadastro → verificação de e-mail → login → bloqueio → desbloqueio admin
- Teste das credenciais do admin no banco antes do deploy
