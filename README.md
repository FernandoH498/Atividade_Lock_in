# Achados & Perdidos — Equipe Lock in

> Sistema simulado de gestão de itens achados e perdidos

## 👨‍💻 Desenvolvedores

| [<img src="https://github.com/FernandoH498.png" width=115><br><sub>Fernando Harmel</sub>](https://github.com/FernandoH498) | [<img src="https://github.com/ThurzinDaMassa.png" width=115><br><sub>Arthur Ayyub</sub>](https://github.com/ThurzinDaMassa) | [<img src="https://github.com/OwL-allfic.png" width=115><br><sub>Arthur Krebs</sub>](https://github.com/OwL-allfic) | [<img src="https://github.com/larissaBeatriz1121.png" width=115><br><sub>Larissa</sub>](https://github.com/larissaBeatriz1121) | [<img src="https://github.com/FerdinandoIanHarmel.png" width=115><br><sub>Victor Prim</sub>](https://github.com/FerdinandoIanHarmel) |
| :---: | :---: | :---: | :---: | :---: |

---

## 🛠️ O que foi utilizado

| Camada    | Tecnologia                             |
|-----------|----------------------------------------|
| Backend   | Java 17, Servlets 4.0, JDBC            |
| Front-end | JSP 2.3, JSTL 1.2, CSS3, JS            |
| Banco     | MySQL 8.x (via XAMPP)                  |
| Build     | Apache Maven 3.8+                      |
| Servidor  | **Apache Tomcat 9.x (Instalação Standalone)** |

---

## 📋 Pré-requisitos

| Ferramenta                          | Versão mínima | Para quê serve                      |
|-------------------------------------|---------------|-------------------------------------|
| Eclipse IDE for Enterprise Java     | 2022-09+      | Editar e rodar o projeto            |
| Apache Tomcat                       | 9.x           | Servidor web obrigatório para rodar a aplicação |
| XAMPP                               | 8.x           | Fornece o banco de dados MySQL      |
| MySQL Workbench                     | 8.0           | Executar os scripts SQL             |
| JDK                                 | 17            | Compilar o código Java              |
| Maven (embutido no Eclipse)         | 3.8+          | Gerenciar dependências              |

> **⚠️ IMPORTANTE:** É **obrigatório** o uso do Apache Tomcat como servidor independente para este projeto. O módulo Apache do XAMPP não deve ser utilizado para rodar a aplicação Java.

---

## Passo 1 — Baixar o Projeto

### Baixar o ZIP

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

> **⚠️ Atenção:** Não inicie o "Apache" no painel do XAMPP. Nosso servidor web será o Tomcat configurado no Eclipse.

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

> ℹ️ Se você definiu uma senha no MySQL/XAMPP, preencha `db.password=` com ela. Se não definiu, deixe em branco.

> 🔒 O arquivo `config.properties` está no `.gitignore` e nunca será enviado ao GitHub. Isso protege suas credenciais.

---

## Passo 4 — Configurar o Servidor Tomcat no Eclipse

### 4.1 — Adicionar o servidor Tomcat standalone

1. Baixe o Apache Tomcat 9.x (formato `.zip`) no site oficial e extraia em uma pasta de sua preferência (Ex: `C:\apache-tomcat-9.0`)
2. No Eclipse: **Window → Preferences → Server → Runtime Environments**
3. Clique em **Add → Apache → Tomcat v9.0**
4. Em **Tomcat installation directory**, clique em "Browse" e aponte para a pasta onde você extraiu o Tomcat independente
5. Selecione o JDK 17 instalado e clique em **Finish**

### 4.2 — Criar o servidor na aba Servers

1. Abra a aba **Servers** (**Window → Show View → Servers**)
2. Clique com botão direito na aba → **New → Server**
3. Selecione **Apache → Tomcat v9.0 Server → Next**
4. Adicione o projeto `achados-perdidos` em **Available → Add → Finish**

---

## Passo 5 — Build e Deploy

1. No Eclipse, clique com botão direito no projeto → **Run As → Maven install**
2. Aguarde a mensagem `BUILD SUCCESS` no console
3. Clique com botão direito no projeto → **Run As → Run on Server**
4. Selecione o **Tomcat v9.0** que você acabou de configurar e clique em **Finish**
5. Acesse no navegador:

```
http://localhost:8080/achados-perdidos/itens
```

> **⚠️** Se a porta `8080` estiver ocupada, mude nas configurações do Tomcat (dando um duplo clique no servidor na aba **Servers** do Eclipse).

---

## 🔗 URLs Disponíveis (v0.1)

| URL | Método | Descrição |
|-----|--------|-----------|
| `/achados-perdidos/` | GET | Redireciona para `/itens` |
| `/achados-perdidos/itens` | GET | Lista itens com filtros |
| `/achados-perdidos/itens?status=ACHADO` | GET | Filtra por status |
| `/achados-perdidos/itens?busca=texto` | GET | Busca por descrição |
| `/achados-perdidos/itens/novo` | GET | Formulário de cadastro |
| `/achados-perdidos/itens/novo` | POST | Salva novo item |

---

## 🐛 Problemas Comuns

| Erro | Causa provável | Solução |
|------|----------------|---------|
| `BUILD FAILURE` no Maven | Dependência não baixada | Botão direito → **Maven → Update Project** |
| `Communications link failure` | MySQL não está rodando | Abra o XAMPP e inicie o MySQL |
| `Unknown database achados_perdidos` | `schema.sql` não foi executado | Execute `schema.sql` no Workbench |
| `Access denied for user root` | Senha errada no `config.properties` | Corrija `db.password=` no arquivo |
| Porta 8080 em uso | Outro programa usando a porta | Feche o conflito ou mude a porta do Tomcat no Eclipse |
| `ClassNotFoundException: com.mysql.cj` | Driver MySQL ausente | Verifique a dependência `mysql-connector-j` no `pom.xml` |
| O Tomcat inicia, mas a página não carrega | Falha ao fazer o Deploy | Remova o projeto do Tomcat (aba Servers), limpe o servidor (Clean) e adicione novamente |

---

## 📂 Estrutura do Projeto

```
.
├── schema.sql                         ← Cria o banco e a tabela itens
├── seed.sql                           ← Insere 5 registros fictícios
├── DoD.md                             ← Definition of Done
├── README.md
├── PROMPTS_IA.md                      ← Registro de uso de IA
├── pom.xml
└── src/main/
    ├── java/br/sesi/achadosperdidos/
    │   ├── model/    Item.java
    │   ├── dao/      ConnectionFactory.java  ItemDAO.java
    │   ├── service/  ItemService.java
    │   └── servlet/  ItemListServlet.java  ItemCreateServlet.java
    │                 ItemDetailServlet.java ItemUpdateServlet.java
    │                 ItemDeleteServlet.java
    ├── resources/
    │   ├── config.properties          ← NÃO commitar (está no .gitignore)
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

## 🔒 Segurança e Conformidade

- **SQL Injection:** 100% das queries utilizam `PreparedStatement` com parâmetros vinculados
- **Credenciais:** `config.properties` está no `.gitignore` e nunca vai ao GitHub
- **Dados fictícios:** `seed.sql` usa apenas dados genéricos — nenhum dado pessoal real
- **ECA / LGPD:** em conformidade com a legislação de proteção de dados
