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

# PR Checklist — v0.2 (Sistema de Login)

> Marque cada item após verificar. Todos devem estar ✅ antes do merge.

---

## Banco de Dados

- [x] `schema_v03.sql` executado sem erros
- [x] Tabela `usuarios` criada com colunas: `id`, `nome_usuario`, `email`, `senha_hash`, `verificado`, `codigo_verificacao`, `codigo_expiracao`, `tentativas_falha`, `bloqueado`, `data_cadastro`
- [x] Tabela `admins` criada e populada com pelo menos um admin
- [x] Campos `email` e `nome_usuario` possuem `UNIQUE KEY`

---

## Cadastro

- [x] `GET /cadastro` exibe o formulário sem erros
- [x] `POST /cadastro` salva usuário com `verificado = 0`
- [x] Nome de usuário: obrigatório, 3–50 chars, apenas letras/números/`.`/`_`/`-`
- [x] E-mail: obrigatório, formato válido, único no banco
- [x] Senha: mínimo 8 chars com maiúscula, minúscula, número e símbolo
- [x] Campo "Confirmar Senha" deve coincidir com "Senha"
- [x] E-mail duplicado exibe mensagem de erro amigável (sem stack trace)
- [x] Após cadastro bem-sucedido, e-mail com código de 6 dígitos é enviado
- [x] Usuário é redirecionado para `/verificar-email`
---

## Verificação de E-mail

- [x] `GET /verificar-email` sem sessão ativa redireciona para `/cadastro`
- [x] Código correto e dentro do prazo → conta marcada como `verificado = 1`
- [x] Código correto mas expirado (> 15 min) → mensagem de erro clara
- [x] Código incorreto → mensagem de erro clara
- [x] Após verificação bem-sucedida → redireciona para `/login` com mensagem de confirmação

---

## Login

- [x] `GET /login` com sessão ativa redireciona direto para `/itens`
- [x] `POST /login` com e-mail e senha corretos cria sessão e redireciona para `/itens`
- [x] Login com conta não verificada exibe mensagem pedindo verificação de e-mail
- [x] Senha errada incrementa `tentativas_falha` no banco
- [x] Mensagem mostra quantas tentativas restam (ex: "Tentativa(s) restante(s): 2")
- [x] Na 3ª senha errada: `bloqueado = 1` e mensagem orientando a contatar o admin
- [x] Login com conta bloqueada exibe mensagem de bloqueio (mesmo com senha correta)
- [x] E-mail inexistente exibe mensagem genérica (não revela se o e-mail existe)
- [x] Após login bem-sucedido, `tentativas_falha` é zerado no banco

---

## Logout

- [x] `GET /logout` invalida a sessão e redireciona para `/login`
- [x] Após logout, acessar `/itens` redireciona para `/login`

---

## Proteção de Rotas

- [x] Acessar `/itens` sem sessão redireciona para `/login`
- [x] Acessar `/admin` sem sessão admin redireciona para `/admin/login`
- [x] Sessão de usuário expira após 30 minutos de inatividade
- [x] Sessão de admin expira após 60 minutos de inatividade

---

## Painel Admin

- [x] `GET /admin/login` exibe formulário de login do admin
- [x] Credenciais inválidas do admin exibem mensagem de erro
- [x] `GET /admin` com sessão admin lista todas as contas com `bloqueado = 1`
- [x] Lista vazia exibe mensagem "Nenhuma conta bloqueada"
- [x] Botão "Desbloquear" pede confirmação antes de enviar o POST
- [x] `POST /admin` zera `tentativas_falha` e define `bloqueado = 0` no banco
- [x] Após desbloquear, usuário consegue fazer login normalmente

---

## Segurança

- [x] Senha nunca é armazenada em texto puro — sempre hash (SHA-256 + salt)
- [x] 100% dos queries usam `PreparedStatement` (zero concatenação de SQL)
- [x] Nenhum stack trace exibido ao usuário final
- [x] Código de verificação tem expiração de 15 minutos
- [x] `config.properties` com credenciais SMTP ausente do repositório

---

## Build & Deploy

- [x] Dependência `javax.mail 1.6.2` adicionada ao `pom.xml`
- [x] `mvn clean package` sem erros
- [x] Configurações SMTP preenchidas no `config.properties` do servidor
- [x] Deploy no Tomcat sem erros no `catalina.out`
