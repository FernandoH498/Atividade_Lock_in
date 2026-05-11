# Achados & Perdidos — Equipe Lock in

> Sistema simulado de gestão de itens achados e perdidos

**Equipe:** Fernando Harmel · Arthur Ayyub · Arthur Krebs · Larissa · Victor Prim

---

## Stack Tecnológica

| Camada    | Tecnologia                   |
|-----------|------------------------------|
| Backend   | Java 17, Servlets 4.0, JDBC  |
| Front-end | JSP 2.3, JSTL 1.2, CSS3, JS |
| Banco     | MySQL 8.x (via XAMPP)        |
| Build     | Apache Maven 3.8+            |
| Servidor  | Apache Tomcat 9.x (via XAMPP)|

---

## Pré-requisitos

| Ferramenta                        | Versão mínima | Para quê serve                      |
|-----------------------------------|---------------|-------------------------------------|
| Eclipse IDE for Enterprise Java   | 2022-09+      | Editar e rodar o projeto            |
| XAMPP                             | 8.x           | Fornece o Tomcat e o MySQL          |
| MySQL Workbench                   | 8.0           | Executar os scripts SQL             |
| JDK                               | 17            | Compilar o código Java              |
| Maven (embutido no Eclipse)       | 3.8+          | Gerenciar dependências              |

> **ℹ️ O XAMPP já inclui o Tomcat e o MySQL. Não é necessário instalar o Tomcat separadamente.**

---

## Passo 1 — Clonar ou Baixar o Projeto

### Opção A — Clonar via Eclipse

1. Abra o Eclipse → menu **File → Import**
2. Escolha **Git → Projects from Git → Clone URI**
3. Cole a URL do repositório GitHub e clique em **Next**
4. Selecione a branch `main` e conclua o assistente

### Opção B — Baixar o ZIP

1. Acesse o repositório no GitHub
2. Clique em **Code → Download ZIP**
3. Extraia em uma pasta de fácil acesso (ex: `C:\projetos\achados-perdidos`)
4. No Eclipse: **File → Import → Maven → Existing Maven Projects** → selecione a pasta extraída

---

## Passo 2 — Configurar o Banco de Dados no XAMPP

### 2.1 — Iniciar o MySQL

1. Abra o **XAMPP Control Panel** (como Administrador no Windows)
2. Clique em **Start** na linha do **MySQL**
3. O status deve ficar verde — porta padrão: `3306`

> **⚠️ Não inicie o Apache pelo XAMPP — o Tomcat será iniciado pelo Eclipse.**

### 2.2 — Executar o `schema.sql` no MySQL Workbench

1. Abra o **MySQL Workbench**
2. Clique em **Local instance 3306** (usuário: `root`, senha em branco por padrão)
3. No menu: **File → Open SQL Script**
4. Navegue até a pasta do projeto e selecione `schema.sql`
5. Clique no **raio (⚡)** para executar — isso cria o banco `achados_perdidos` e a tabela `itens`

### 2.3 — Executar o `seed.sql` (dados de exemplo)

1. No Workbench: **File → Open SQL Script** → selecione `seed.sql`
2. Execute com o **raio (⚡)** — insere 5 registros fictícios de teste

> **❌ Se aparecer erro `Unknown database`, execute o `schema.sql` primeiro e tente novamente.**

---

## Passo 3 — Configurar as Credenciais do Banco

Localize o arquivo `config.properties.example` em:

```
src/main/resources/config.properties.example
```

1. Clique com botão direito no arquivo → **Copy → Paste** na mesma pasta
2. Renomeie a cópia para `config.properties` (remova o `.example`)
3. Abra o `config.properties` e edite:

```properties
db.url=jdbc:mysql://localhost:3306/achados_perdidos?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
db.user=root
db.password=
```

> **ℹ️ Se você definiu uma senha no XAMPP, preencha `db.password=` com ela. Se não definiu, deixe em branco.**

> **🔒 O arquivo `config.properties` está no `.gitignore` e nunca será enviado ao GitHub. Isso protege suas credenciais.**

---

## Passo 4 — Configurar o Tomcat no Eclipse

### 4.1 — Adicionar o servidor Tomcat

1. No Eclipse: **Window → Preferences → Server → Runtime Environments**
2. Clique em **Add → Apache → Tomcat v9.0**
3. Em **Tomcat installation directory**, aponte para a pasta do Tomcat dentro do XAMPP:

```
Windows:   C:\xampp\tomcat
Mac/Linux: /Applications/XAMPP/tomcat
```

4. Selecione o **JDK 17** instalado e clique em **Finish**

### 4.2 — Criar o servidor na aba Servers

1. Abra a aba **Servers** (Window → Show View → Servers)
2. Clique com botão direito na aba → **New → Server**
3. Selecione **Apache → Tomcat v9.0 Server** → **Next**
4. Adicione o projeto `achados-perdidos` em Available → **Add** → **Finish**

---

## Passo 5 — Build e Deploy

1. No Eclipse, clique com botão direito no projeto → **Run As → Maven install**
   - Aguarde a mensagem `BUILD SUCCESS` no console

2. Clique com botão direito no projeto → **Run As → Run on Server**
3. Selecione o **Tomcat v9.0** que você configurou e clique em **Finish**
4. Acesse no navegador:

```
http://localhost:8080/achados-perdidos/itens
```

> **⚠️ Se a porta 8080 estiver ocupada, mude nas configurações do Tomcat no Eclipse ou no XAMPP.**

---

## URLs Disponíveis (v0.1)

| URL                                     | Método | Descrição                 |
|-----------------------------------------|--------|---------------------------|
| `/achados-perdidos/`                    | GET    | Redireciona para `/itens` |
| `/achados-perdidos/itens`               | GET    | Lista itens com filtros   |
| `/achados-perdidos/itens?status=ACHADO` | GET    | Filtra por status         |
| `/achados-perdidos/itens?busca=texto`   | GET    | Busca por descrição       |
| `/achados-perdidos/itens/novo`          | GET    | Formulário de cadastro    |
| `/achados-perdidos/itens/novo`          | POST   | Salva novo item           |

---

## Problemas Comuns

| Erro | Causa provável | Solução |
|------|---------------|---------|
| `BUILD FAILURE` no Maven | Dependência não baixada | Botão direito → Maven → Update Project |
| `Communications link failure` | MySQL não está rodando | Abra o XAMPP e inicie o MySQL |
| `Unknown database achados_perdidos` | `schema.sql` não foi executado | Execute `schema.sql` no Workbench |
| `Access denied for user root` | Senha errada no `config.properties` | Corrija `db.password=` no arquivo |
| Porta 8080 em uso | Outro programa usando a porta | Feche o conflito ou mude a porta do Tomcat |
| `ClassNotFoundException: com.mysql.cj` | Driver MySQL ausente | Verifique a dependência `mysql-connector-j` no `pom.xml` |

---

## Estrutura do Projeto

```
.
├── schema.sql                          ← Cria o banco e a tabela itens
├── seed.sql                            ← Insere 5 registros fictícios
├── DoD.md                              ← Definition of Done
├── README.md
├── PROMPTS_IA.md                       ← Registro de uso de IA
├── pom.xml
└── src/main/
    ├── java/br/sesi/achadosperdidos/
    │   ├── model/      Item.java
    │   ├── dao/        ConnectionFactory.java  ItemDAO.java
    │   ├── service/    ItemService.java
    │   └── servlet/    ItemListServlet.java  ItemCreateServlet.java
    │                   ItemDetailServlet.java ItemUpdateServlet.java
    │                   ItemDeleteServlet.java
    ├── resources/
    │   ├── config.properties           ← NÃO commitar (está no .gitignore)
    │   └── config.properties.example  ← Modelo — este sim vai ao GitHub
    └── webapp/
        ├── index.jsp
        ├── WEB-INF/
        │   ├── web.xml
        │   └── views/  list.jsp  form.jsp  erro.jsp
        ├── css/  style.css
        └── js/   main.js
```

---

## Segurança e Conformidade

- **SQL Injection:** 100% das queries utilizam `PreparedStatement` com parâmetros vinculados
- **Credenciais:** `config.properties` está no `.gitignore` e nunca vai ao GitHub
- **Dados fictícios:** `seed.sql` usa apenas dados genéricos — nenhum dado pessoal real
- **ECA / LGPD:** em conformidade com a legislação de proteção de dados
