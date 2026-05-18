# Achados & Perdidos — Equipe Lock in

> Sistema simulado de gestão de itens achados e perdidos, com autenticação, verificação de e-mail e painel administrativo.

## Desenvolvedores

| [<img src="https://github.com/FernandoH498.png" width=115><br><sub>Fernando Harmel</sub>](https://github.com/FernandoH498) | [<img src="https://github.com/ThurzinDaMassa.png" width=115><br><sub>Arthur Ayyub</sub>](https://github.com/ThurzinDaMassa) | [<img src="https://github.com/OwL-allfic.png" width=115><br><sub>Arthur Krebs</sub>](https://github.com/OwL-allfic) | [<img src="https://github.com/larissaBeatriz1121.png" width=115><br><sub>Larissa</sub>](https://github.com/larissaBeatriz1121) | [<img src="https://github.com/FerdinandoIanHarmel.png" width=115><br><sub>Victor Prim</sub>](https://github.com/FerdinandoIanHarmel) |
| :---: | :---: | :---: | :---: | :---: |

---

## 🛠️ O que foi utilizado

| Camada    | Tecnologia                                              |
|-----------|---------------------------------------------------------|
| Backend   | Java 17, Servlets 4.0, JDBC                             |
| Front-end | JSP 2.3, JSTL 1.2, CSS3, JS                            |
| Banco     | MySQL 8.x (via XAMPP)                                   |
| E-mail    | JavaMail (SMTP Gmail com Senha de App)                  |
| Segurança | BCrypt (jBCrypt), CSRF Token, Session Fixation Protection |
| Build     | Apache Maven 3.8+                                       |
| Servidor  | **Apache Tomcat 9.x (Instalação Standalone)**           |

---

## 📋 Pré-requisitos

| Ferramenta                          | Versão mínima | Para quê serve                          |
|-------------------------------------|---------------|-----------------------------------------|
| Eclipse IDE for Enterprise Java     | 2022-09+      | Editar e rodar o projeto                |
| Apache Tomcat                       | 9.x           | Servidor web obrigatório                |
| XAMPP                               | 8.x           | Fornece o banco de dados MySQL          |
| MySQL Workbench                     | 8.0           | Executar os scripts SQL                 |
| JDK                                 | 17            | Compilar o código Java                  |
| Maven (embutido no Eclipse)         | 3.8+          | Gerenciar dependências                  |
| Conta Gmail                         | —             | Enviar e-mails de verificação de código |

> **⚠️ IMPORTANTE:** Use o Apache Tomcat standalone como servidor. O módulo Apache do XAMPP **não deve** ser iniciado — ele não serve aplicações Java.

---

## Passo 1 — Baixar o Projeto

1. Acesse o repositório no GitHub
2. Clique em **Code → Download ZIP**
3. Extraia em uma pasta de fácil acesso (ex: `C:\projetos\achados-perdidos`)
4. No Eclipse: **File → Import → Maven → Existing Maven Projects** → selecione a pasta extraída

---

## Passo 2 — Configurar o Banco de Dados no XAMPP

### 2.1 — Iniciar o MySQL

1. Abra o **XAMPP Control Panel** (como Administrador no Windows)
2. Clique em **Start** **APENAS** na linha do **MySQL**
3. O status deve ficar verde — porta padrão: `3306`

> **⚠️ Atenção:** Não inicie o "Apache" do XAMPP. O servidor web será o Tomcat configurado no Eclipse.

### 2.2 — Executar o `schema.sql` no MySQL Workbench

1. Abra o **MySQL Workbench**
2. Clique em **Local instance 3306** (usuário: `root`, senha em branco por padrão)
3. **File → Open SQL Script** → selecione `schema.sql` na raiz do projeto
4. Clique no **raio (⚡)** para executar

Esse script cria o banco `achados_perdidos` e as tabelas `itens` e `usuarios`.

### 2.3 — Executar o `seed.sql` (dados de exemplo)

1. **File → Open SQL Script** → selecione `seed.sql`
2. Execute com o **raio (⚡)** — insere 5 registros fictícios de teste

> **❌ Se aparecer erro `Unknown database`:** execute o `schema.sql` primeiro e tente novamente.

---

## Passo 3 — Configurar Credenciais do Banco e E-mail

Localize o arquivo modelo em:

```
src/main/resources/config.properties.example
```

1. Clique com botão direito → **Copy → Paste** na **mesma pasta**
2. Renomeie a cópia para `config.properties` (remova o `.example`)
3. Abra o `config.properties` e preencha as duas seções:

```properties
# --- Banco de dados ---
db.url=jdbc:mysql://localhost:3306/achados_perdidos?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
db.user=root
db.password=

# --- Gmail SMTP (verificação de e-mail) ---
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.user=seuemail@gmail.com
mail.smtp.password=xxxx xxxx xxxx xxxx
mail.from.name=Achados e Perdidos Equipe Lock In
```

> ℹ️ Se você definiu uma senha no MySQL/XAMPP, preencha `db.password=`. Se não definiu, deixe em branco.

> 🔒 O arquivo `config.properties` está no `.gitignore` e **nunca** será enviado ao GitHub.

### Como gerar a Senha de App do Gmail

O campo `mail.smtp.password` **não** aceita sua senha normal do Google. Você precisa de uma **Senha de App**:

1. Acesse [myaccount.google.com](https://myaccount.google.com)
2. Vá em **Segurança → Verificação em duas etapas** e confirme que está ativada
3. Na mesma página, pesquise por **"Senhas de app"**
4. Em app, escolha **Outro (nome personalizado)** → digite `Achados e Perdidos` → clique em **Gerar**
5. O Google exibirá uma senha de 16 caracteres no formato `xxxx xxxx xxxx xxxx`
6. Copie essa senha e cole em `mail.smtp.password=` no seu `config.properties`

> Essa senha de app pode ser revogada a qualquer momento sem afetar a senha principal da conta Google.

---

## Passo 4 — Configurar o Servidor Tomcat no Eclipse

### 4.1 — Adicionar o Tomcat standalone

1. Baixe o Apache Tomcat 9.x (formato `.zip`) no site oficial e extraia (ex: `C:\apache-tomcat-9.0`)
2. No Eclipse: **Window → Preferences → Server → Runtime Environments**
3. Clique em **Add → Apache → Tomcat v9.0**
4. Em **Tomcat installation directory**, aponte para a pasta extraída
5. Selecione o **JDK 17** e clique em **Finish**

### 4.2 — Criar o servidor na aba Servers

1. Abra a aba **Servers** (**Window → Show View → Servers**)
2. Clique com botão direito → **New → Server**
3. Selecione **Apache → Tomcat v9.0 Server → Next**
4. Adicione o projeto `achados-perdidos` em **Available → Add → Finish**

---

## Passo 5 — Build e Deploy

1. Clique com botão direito no projeto → **Run As → Maven install**
2. Aguarde `BUILD SUCCESS` no console
3. Clique com botão direito no projeto → **Run As → Run on Server**
4. Selecione o **Tomcat v9.0** configurado e clique em **Finish**
5. Acesse no navegador:

```
http://localhost:8080/achados-perdidos/login
```

> **⚠️** Se a porta `8080` estiver em uso, mude nas configurações do Tomcat (duplo clique no servidor na aba **Servers**).

---

## Passo 6 — Criar Conta e Verificar o E-mail

O sistema exige verificação de e-mail para ativar qualquer conta nova. O fluxo completo é:

1. Acesse `http://localhost:8080/achados-perdidos/register`
2. Preencha **Nome**, **E-mail** e **Senha** (mínimo 8 caracteres)
3. Clique em **Cadastrar**
4. Verifique a caixa de entrada do e-mail informado — você receberá um **código de 6 dígitos**
5. Na tela de verificação (redirecionamento automático), informe o código e clique em **Verificar**
6. Após a confirmação, você será redirecionado para o login — agora pode entrar normalmente

> ⏱️ O código expira em **15 minutos**. Se não receber ou o código expirar, clique em **Reenviar código** na tela de verificação.

> 📧 Se o e-mail cair no **spam**, marque como "não é spam" e revise as configurações do Passo 3.

---

## Conta Administrador

A conta de administrador é criada **automaticamente** quando o Tomcat sobe pela primeira vez — não é necessário cadastrar manualmente nem verificar e-mail.

| Campo    | Valor      |
|----------|------------|
| Username | `AdminFIH` |
| Senha    | definida em `AppStartupListener.java` |

Para acessar o painel administrativo:

```
http://localhost:8080/achados-perdidos/admin
```

---

## 🔗 URLs Disponíveis

| URL                                      | Método      | Descrição                            |
|------------------------------------------|-------------|--------------------------------------|
| `/achados-perdidos/`                     | GET         | Redireciona para `/itens`            |
| `/achados-perdidos/login`                | GET / POST  | Tela de login                        |
| `/achados-perdidos/register`             | GET / POST  | Cadastro de novo usuário             |
| `/achados-perdidos/verify`               | GET / POST  | Verificação de e-mail (código)       |
| `/achados-perdidos/logout`               | GET         | Encerra a sessão                     |
| `/achados-perdidos/itens`                | GET         | Lista itens (requer login)           |
| `/achados-perdidos/itens?status=ACHADO`  | GET         | Filtra por status                    |
| `/achados-perdidos/itens?busca=texto`    | GET         | Busca por descrição                  |
| `/achados-perdidos/itens/novo`           | GET / POST  | Cadastrar novo item                  |
| `/achados-perdidos/admin`                | GET         | Painel admin (requer papel ADMIN)    |
| `/achados-perdidos/admin/usuarios`       | GET         | Gerenciar usuários                   |
| `/achados-perdidos/admin/itens`          | GET         | Gerenciar itens                      |

---

## 🐛 Problemas Comuns

| Erro                                        | Causa provável                            | Solução                                                              |
|---------------------------------------------|-------------------------------------------|----------------------------------------------------------------------|
| `BUILD FAILURE` no Maven                    | Dependência não baixada                   | Botão direito → **Maven → Update Project**                           |
| `Communications link failure`               | MySQL não está rodando                    | Abra o XAMPP e inicie o MySQL                                        |
| `Unknown database achados_perdidos`         | `schema.sql` não executado                | Execute `schema.sql` no Workbench                                    |
| `Access denied for user root`               | Senha errada no `config.properties`       | Corrija `db.password=`                                               |
| Porta 8080 em uso                           | Outro programa na porta                   | Feche o conflito ou mude a porta do Tomcat                           |
| `ClassNotFoundException: com.mysql.cj`      | Driver MySQL ausente                      | Verifique a dependência `mysql-connector-j` no `pom.xml`             |
| Tomcat sobe mas página não carrega          | Falha no deploy                           | Remova o projeto do Tomcat, limpe (Clean) e adicione novamente       |
| Não recebe e-mail de verificação            | SMTP mal configurado                      | Revise `mail.smtp.*` no `config.properties` e gere nova Senha de App |
| E-mail cai no spam                          | Filtro do provedor                        | Marque como "não é spam"                                             |
| `Falha ao enviar e-mail de verificação`     | Senha de App incorreta ou 2FA desativado  | Gere nova Senha de App com 2FA ativo na conta Google                 |
| Código inválido ou expirado                 | Código expirou (15 min)                   | Clique em **Reenviar código** na tela de verificação                 |
| Conta bloqueada                             | 3 tentativas de login falhas              | Contate o administrador para desbloquear via painel admin            |

---

## 📂 Estrutura do Projeto

```
.
├── schema.sql                          ← Cria banco e tabelas (itens + usuarios)
├── seed.sql                            ← 5 registros fictícios de teste
├── DoD.md                              ← Definition of Done
├── README.md
├── PROMPTS_IA.md                       ← Registro de uso de IA
├── pom.xml
└── src/main/
    ├── java/br/sesi/achadosperdidos/
    │   ├── model/      Item.java  User.java
    │   ├── dao/        ConnectionFactory.java  ItemDAO.java  UserDAO.java
    │   ├── service/    ItemService.java  UserService.java  EmailService.java
    │   ├── servlet/    LoginServlet.java  LogoutServlet.java
    │   │               RegisterServlet.java  VerifyEmailServlet.java
    │   │               ItemListServlet.java  ItemCreateServlet.java
    │   │               ItemDetailServlet.java  ItemUpdateServlet.java
    │   │               ItemDeleteServlet.java
    │   │   └── admin/  AdminDashboardServlet.java  AdminUsersServlet.java
    │   │               AdminItemsServlet.java
    │   ├── filter/     AuthFilter.java  AdminFilter.java
    │   ├── listener/   AppStartupListener.java
    │   └── util/       AppConfig.java  CsrfUtil.java
    ├── resources/
    │   ├── config.properties           ← NÃO commitar (gitignore)
    │   └── config.properties.example   ← Modelo — este vai ao GitHub
    └── webapp/
        ├── index.jsp
        ├── WEB-INF/
        │   ├── web.xml
        │   └── views/  login.jsp  register.jsp  verify.jsp
        │               list.jsp  form.jsp  detail.jsp  erro.jsp
        ├── css/  style.css  auth.css  admin.css
        └── js/   main.js
```

---

## 🔒 Segurança e Conformidade

- **SQL Injection:** 100% das queries usam `PreparedStatement` com parâmetros vinculados
- **Senhas:** armazenadas com BCrypt (12 rounds) — nunca em texto puro
- **CSRF:** todas as operações POST validam token de sessão
- **Session Fixation:** sessão é invalidada e recriada após o login com sucesso
- **Bloqueio de conta:** conta bloqueada automaticamente após 3 tentativas de login falhas
- **Credenciais:** `config.properties` está no `.gitignore` e nunca vai ao GitHub
- **Dados fictícios:** `seed.sql` usa apenas dados genéricos — nenhum dado pessoal real
- **ECA / LGPD:** em conformidade com a legislação de proteção de dados
