# Achados & Perdidos — SESI Blumenau

Sistema simulado de gestão de itens achados e perdidos — Hackathon Técnico SESI, v0.1.

---

## Stack Tecnológica

| Camada    | Tecnologia                    |
|-----------|-------------------------------|
| Backend   | Java 17, Servlets 4.0, JDBC   |
| Front-end | JSP 2.3, JSTL 1.2, CSS3, JS  |
| Banco     | MySQL 8.x / MariaDB 10.x      |
| Build     | Apache Maven 3.8+             |
| Servidor  | Apache Tomcat 9.x             |

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|-----------|---------------|
| JDK       | 17            |
| Maven     | 3.8           |
| MySQL     | 8.0           |
| Tomcat    | 9.0           |

---

## 1 — Configurar o Banco de Dados

```bash
# Conecte ao MySQL
mysql -u root -p

# Execute o schema (cria banco e tabela)
source /caminho/completo/schema.sql

# Execute os dados de exemplo (5 registros fictícios)
source /caminho/completo/seed.sql
```

Ou em um único comando (ajuste o caminho):

```bash
mysql -u root -p < schema.sql
mysql -u root -p achados_perdidos < seed.sql
```

---

## 2 — Configurar a Aplicação

Copie o arquivo de exemplo e preencha suas credenciais:

```bash
cp src/main/resources/config.properties.example \
   src/main/resources/config.properties
```

Edite `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/achados_perdidos?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
db.user=SEU_USUARIO
db.password=SUA_SENHA
```

> **Atenção:** `config.properties` está no `.gitignore` e **não deve ser commitado**.

---

## 3 — Build

```bash
mvn clean package
```

O WAR será gerado em `target/achados-perdidos.war`.

---

## 4 — Deploy no Tomcat

1. Copie o WAR para a pasta `webapps` do Tomcat:

```bash
cp target/achados-perdidos.war $TOMCAT_HOME/webapps/
```

2. Inicie (ou reinicie) o Tomcat:

```bash
$TOMCAT_HOME/bin/startup.sh     # Linux/Mac
$TOMCAT_HOME\bin\startup.bat    # Windows
```

3. Acesse no navegador:

```
http://localhost:8080/achados-perdidos/
```

---

## Estrutura do Projeto

```
.
├── schema.sql
├── seed.sql
├── DoD.md
├── README.md
├── PROMPTS_IA.md
├── pom.xml
└── src/main/
    ├── java/br/sesi/achadosperdidos/
    │   ├── model/      Item.java
    │   ├── dao/        ConnectionFactory.java  ItemDAO.java
    │   ├── service/    ItemService.java
    │   └── servlet/    ItemListServlet.java  ItemCreateServlet.java
    ├── resources/
    │   ├── config.properties          ← NÃO commitar
    │   └── config.properties.example
    └── webapp/
        ├── index.jsp
        ├── WEB-INF/
        │   ├── web.xml
        │   └── views/  list.jsp  form.jsp  erro.jsp
        ├── css/  style.css
        └── js/   main.js
```

---

## URLs disponíveis (v0.1)

| URL                                    | Método | Descrição                  |
|----------------------------------------|--------|----------------------------|
| `/achados-perdidos/`                   | GET    | Redireciona para `/itens`  |
| `/achados-perdidos/itens`              | GET    | Lista itens com filtros    |
| `/achados-perdidos/itens?status=ACHADO`| GET    | Filtra por status          |
| `/achados-perdidos/itens?busca=texto`  | GET    | Busca por descrição        |
| `/achados-perdidos/itens/novo`         | GET    | Formulário de cadastro     |
| `/achados-perdidos/itens/novo`         | POST   | Salva novo item            |

---

## Conformidade

- **ECA / LGPD:** Os dados de seed são completamente fictícios. Nenhum dado pessoal real é utilizado.
- **SQL Injection:** 100% das queries utilizam `PreparedStatement` com parâmetros vinculados.
- **Arquivos sensíveis:** `config.properties` está no `.gitignore`.
