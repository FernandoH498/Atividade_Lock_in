# Achados & Perdidos — Equipe Lock in

> Sistema simulado de gestão de itens achados e perdidos

## 👨‍💻 Desenvolvedores

Substitua `SEU_USUARIO_GITHUB` no código pelo @ do GitHub de cada membro para carregar a foto e o link corretamente.

| [<img src="https://github.com/SEU_USUARIO_GITHUB.png" width=115><br><sub>Fernando Harmel</sub>](https://github.com/SEU_USUARIO_GITHUB) | [<img src="https://github.com/SEU_USUARIO_GITHUB.png" width=115><br><sub>Arthur Ayyub</sub>](https://github.com/SEU_USUARIO_GITHUB) | [<img src="https://github.com/SEU_USUARIO_GITHUB.png" width=115><br><sub>Arthur Krebs</sub>](https://github.com/SEU_USUARIO_GITHUB) | [<img src="https://github.com/SEU_USUARIO_GITHUB.png" width=115><br><sub>Larissa</sub>](https://github.com/SEU_USUARIO_GITHUB) | [<img src="https://github.com/SEU_USUARIO_GITHUB.png" width=115><br><sub>Victor Prim</sub>](https://github.com/SEU_USUARIO_GITHUB) |
| :---: | :---: | :---: | :---: | :---: |

---

## 🛠️ O que foi utilizado

| Camada    | Tecnologia                             |
|-----------|----------------------------------------|
| Backend   | Java 17, Servlets 4.0, JDBC            |
| Front-end | JSP 2.3, JSTL 1.2, CSS3, JS            |
| Banco     | MySQL 8.x (via XAMPP)                  |
| Build     | Apache Maven 3.8+                      |
| Servidor  | Apache Tomcat 9.x                      |

---

## 📋 Pré-requisitos

| Ferramenta                          | Versão mínima | Para quê serve                      |
|-------------------------------------|---------------|-------------------------------------|
| Eclipse IDE for Enterprise Java     | 2022-09+      | Editar e rodar o projeto            |
| Apache Tomcat                       | 9.x           | Servidor web para rodar a aplicação |
| XAMPP                               | 8.x           | Fornece o banco de dados MySQL      |
| MySQL Workbench                     | 8.0           | Executar os scripts SQL             |
| JDK                                 | 17            | Compilar o código Java              |
| Maven (embutido no Eclipse)         | 3.8+          | Gerenciar dependências              |

---

## Passo 1 — Clonar ou Baixar o Projeto
Baixar o ZIP

1. Acesse o repositório no GitHub
2. Clique em **Code → Download ZIP**
3. Extraia para uma pasta
4. No Eclipse: **File → Import → Maven → Existing Maven Projects** → selecione a pasta extraída

---

## Passo 2 — Configurar o Banco de Dados no XAMPP

### 2.1 — Iniciar o MySQL

1. Abra o **XAMPP Control Panel**
2. Clique em **Start** na linha do **MySQL**
3. O status deve ficar verde — porta padrão: `3306`

### 2.2 — Executar o `schema.sql` no MySQL Workbench

1. Abra o **MySQL Workbench**
2. Clique em **Local instance 3306** (usuário: `root`, senha em branco por padrão)
3. No menu: **File → Open SQL Script**
4. Navegue até a pasta do projeto e selecione `schema.sql`
5. Clique no **raio (⚡)** para executar — isso cria o banco `achados_perdidos` e a tabela `itens`

### 2.3 — Executar o `seed.sql` (dados de exemplo)

1. No Workbench: **File → Open SQL Script** → selecione `seed.sql`
2. Execute com o **raio (⚡)** — insere 5 registros fictícios de teste

---

## Passo 3 — Configurar as Credenciais do Banco

Localize o arquivo `config.properties.example` em:
src/main/resources/config.properties.example
1. Clique com botão direito no arquivo → **Copy → Paste** na mesma pasta
2. Renomeie a cópia para `config.properties` (remova o `.example`)
3. Abra o `config.properties` e edite:
properties
db.url=jdbc:mysql://localhost:3306/achados_perdidos?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
db.user=root
db.password=

ℹ️ Se você definiu uma senha no MySQL/XAMPP, preencha db.password= com ela. Se não definiu, deixe em branco.🔒
O arquivo config.properties está no .gitignore e nunca será enviado ao GitHub.
Isso protege suas credenciais.

Passo 4 — Configurar o Servidor Tomcat no Eclipse4.1 — Adicionar o servidor Tomcat standaloneBaixe o Apache Tomcat 9.x (formato .zip) no site oficial e extraia em uma pasta de sua preferência (Ex: C:\apache-tomcat-9.0).
No Eclipse: Window → Preferences → Server → Runtime Environments
Clique em Add → Apache → Tomcat v9.0Em Tomcat installation directory, clique em "Browse" e aponte para a pasta onde você extraiu o Tomcat independente.
Selecione o JDK 17 instalado e clique em Finish

4.2 — Criar o servidor na aba ServersAbra a aba Servers (Window → Show View → Servers)
Clique com botão direito na aba → New → Server
Selecione Apache → Tomcat v9.0 Server → NextAdicione o projeto achados-perdidos em Available → Add → Finish

Passo 5 — Build e DeployNo Eclipse, clique com botão direito no projeto → Run As → Maven installAguarde a mensagem BUILD SUCCESS no console
Clique com botão direito no projeto → Run As → Run on Server
Selecione o Tomcat v9.0 que você acabou de configurar e clique em FinishAcesse no navegador:http://localhost:8080/achados-perdidos/itens
⚠️ Se a porta 8080 estiver ocupada, mude nas configurações do Tomcat (dando um duplo clique no servidor na aba Servers do Eclipse).
