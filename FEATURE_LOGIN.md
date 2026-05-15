# Feature: Sistema de Login — v0.3

> Implementação completa de autenticação com: cadastro, verificação de e-mail por código,
> bloqueio por tentativas erradas e painel administrativo de desbloqueio.

---

## Sumário

1. [Visão Geral](#visão-geral)
2. [Schema SQL — `schema_v03.sql`](#schema-sql)
3. [pom.xml — dependência JavaMail](#pomxml)
4. [config.properties — novas chaves](#configproperties)
5. [Utilitários](#utilitários)
6. [Models](#models)
7. [DAOs](#daos)
8. [Services](#services)
9. [Filtros (AuthFilter)](#filtros)
10. [Servlets](#servlets)
11. [Views JSP](#views-jsp)
12. [web.xml — mapeamentos](#webxml)

---

## Visão Geral

```
Fluxo de Cadastro:
  POST /cadastro → valida campos → hash senha → salva usuario (verificado=false)
                 → gera código 6 dígitos → envia e-mail → redireciona /verificar-email

Fluxo de Verificação:
  POST /verificar-email → confere código e expiração → marca verificado=true → redireciona /login

Fluxo de Login:
  POST /login → busca por e-mail → conta bloqueada? → código expirado?
              → verifica senha (hash) → tentativas_falha++ se errado
              → bloqueia conta se tentativas_falha >= 3
              → sessão criada se correto → redireciona /itens

Fluxo Admin:
  POST /admin/login → valida credenciais admin → sessão admin
  GET  /admin        → lista contas bloqueadas
  POST /admin/desbloquear → zera tentativas_falha, bloqueado=false
```

---

## Schema SQL

**Arquivo:** `schema_v03.sql`

```sql
-- ============================================================
-- Achados & Perdidos — v0.3 | Sistema de Login
-- ============================================================

USE achados_perdidos;

-- Tabela de usuários comuns
CREATE TABLE IF NOT EXISTS usuarios (
    id                  INT           NOT NULL AUTO_INCREMENT,
    nome_usuario        VARCHAR(50)   NOT NULL,
    email               VARCHAR(255)  NOT NULL,
    senha_hash          VARCHAR(300)  NOT NULL,
    verificado          TINYINT(1)    NOT NULL DEFAULT 0,
    codigo_verificacao  VARCHAR(6)    NULL,
    codigo_expiracao    DATETIME      NULL,
    tentativas_falha    TINYINT       NOT NULL DEFAULT 0,
    bloqueado           TINYINT(1)    NOT NULL DEFAULT 0,
    data_cadastro       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_email        (email),
    UNIQUE KEY uq_nome_usuario (nome_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela de administradores (seed manual abaixo)
CREATE TABLE IF NOT EXISTS admins (
    id            INT           NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL,
    senha_hash    VARCHAR(300)  NOT NULL,
    data_cadastro DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_admin_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ⚠ Gere a senha_hash com PasswordUtil.hash("suaSenhaAdmin")
-- e substitua o valor abaixo antes de executar.
INSERT INTO admins (nome, email, senha_hash)
VALUES ('Administrador', 'admin@sesi.org.br', 'SUBSTITUA_PELO_HASH_GERADO');
```

---

## pom.xml

Adicione dentro de `<dependencies>`:

```xml
<!-- JavaMail — envio de e-mail de verificação -->
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.6.2</version>
</dependency>
```

---

## config.properties

Adicione ao `src/main/resources/config.properties`:

```properties
# ── SMTP (Gmail com App Password) ─────────────────────────────
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.user=seuemail@gmail.com
mail.smtp.password=sua_senha_de_app
mail.from.name=Achados e Perdidos SESI
```

E ao `config.properties.example` (sem valores reais):

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.user=SEU_EMAIL
mail.smtp.password=SUA_SENHA_DE_APP
mail.from.name=Achados e Perdidos SESI
```

---

## Utilitários

### `PasswordUtil.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/util/PasswordUtil.java`

```java
package br.sesi.achadosperdidos.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hash de senha com SHA-256 + salt aleatório de 16 bytes.
 * Formato armazenado: base64(salt) + "$" + base64(hash)
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String hash(String senha) {
        try {
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = hashComSalt(senha, salt);
            return saltB64 + "$" + hashB64;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash de senha", e);
        }
    }

    public static boolean verificar(String senha, String armazenado) {
        try {
            String[] partes = armazenado.split("\\$", 2);
            if (partes.length != 2) return false;
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            String hashCalculado = hashComSalt(senha, salt);
            return partes[1].equals(hashCalculado);
        } catch (Exception e) {
            return false;
        }
    }

    private static String hashComSalt(String senha, byte[] salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashed = md.digest(senha.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hashed);
    }
}
```

---

### `CodigoUtil.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/util/CodigoUtil.java`

```java
package br.sesi.achadosperdidos.util;

import java.security.SecureRandom;

/** Gera e valida o código de 6 dígitos para verificação de e-mail. */
public class CodigoUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Retorna um código numérico de 6 dígitos como String (ex: "047382"). */
    public static String gerar() {
        int numero = RANDOM.nextInt(900_000) + 100_000; // 100000–999999
        return String.valueOf(numero);
    }
}
```

---

## Models

### `Usuario.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/model/Usuario.java`

```java
package br.sesi.achadosperdidos.model;

import java.time.LocalDateTime;

public class Usuario {

    private int id;
    private String nomeUsuario;
    private String email;
    private String senhaHash;
    private boolean verificado;
    private String codigoVerificacao;
    private LocalDateTime codigoExpiracao;
    private int tentativasFalha;
    private boolean bloqueado;
    private LocalDateTime dataCadastro;

    public Usuario() {}

    // ── Getters & Setters ──────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }

    public String getCodigoVerificacao() { return codigoVerificacao; }
    public void setCodigoVerificacao(String codigoVerificacao) { this.codigoVerificacao = codigoVerificacao; }

    public LocalDateTime getCodigoExpiracao() { return codigoExpiracao; }
    public void setCodigoExpiracao(LocalDateTime codigoExpiracao) { this.codigoExpiracao = codigoExpiracao; }

    public int getTentativasFalha() { return tentativasFalha; }
    public void setTentativasFalha(int tentativasFalha) { this.tentativasFalha = tentativasFalha; }

    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
```

---

### `Admin.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/model/Admin.java`

```java
package br.sesi.achadosperdidos.model;

public class Admin {

    private int id;
    private String nome;
    private String email;
    private String senhaHash;

    public Admin() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
}
```

---

## DAOs

### `UsuarioDAO.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/dao/UsuarioDAO.java`

```java
package br.sesi.achadosperdidos.dao;

import br.sesi.achadosperdidos.model.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String COLS =
        "id, nome_usuario, email, senha_hash, verificado, " +
        "codigo_verificacao, codigo_expiracao, tentativas_falha, bloqueado, data_cadastro";

    public void save(Usuario u) throws SQLException {
        String sql =
            "INSERT INTO usuarios (nome_usuario, email, senha_hash, " +
            "codigo_verificacao, codigo_expiracao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNomeUsuario());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenhaHash());
            ps.setString(4, u.getCodigoVerificacao());
            ps.setTimestamp(5, Timestamp.valueOf(u.getCodigoExpiracao()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLS + " FROM usuarios WHERE email = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Usuario findByNomeUsuario(String nomeUsuario) throws SQLException {
        String sql = "SELECT " + COLS + " FROM usuarios WHERE nome_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomeUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Usuario> findBloqueados() throws SQLException {
        String sql = "SELECT " + COLS + " FROM usuarios WHERE bloqueado = 1 ORDER BY data_cadastro DESC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public void marcarVerificado(int id) throws SQLException {
        String sql = "UPDATE usuarios SET verificado = 1, codigo_verificacao = NULL, " +
                     "codigo_expiracao = NULL WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void incrementarTentativas(int id) throws SQLException {
        String sql = "UPDATE usuarios SET tentativas_falha = tentativas_falha + 1 WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void bloquear(int id) throws SQLException {
        String sql = "UPDATE usuarios SET bloqueado = 1 WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void desbloquear(int id) throws SQLException {
        String sql = "UPDATE usuarios SET bloqueado = 0, tentativas_falha = 0 WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void zerarTentativas(int id) throws SQLException {
        String sql = "UPDATE usuarios SET tentativas_falha = 0 WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNomeUsuario(rs.getString("nome_usuario"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setVerificado(rs.getBoolean("verificado"));
        u.setCodigoVerificacao(rs.getString("codigo_verificacao"));
        Timestamp exp = rs.getTimestamp("codigo_expiracao");
        if (exp != null) u.setCodigoExpiracao(exp.toLocalDateTime());
        u.setTentativasFalha(rs.getInt("tentativas_falha"));
        u.setBloqueado(rs.getBoolean("bloqueado"));
        Timestamp dc = rs.getTimestamp("data_cadastro");
        if (dc != null) u.setDataCadastro(dc.toLocalDateTime());
        return u;
    }
}
```

---

### `AdminDAO.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/dao/AdminDAO.java`

```java
package br.sesi.achadosperdidos.dao;

import br.sesi.achadosperdidos.model.Admin;

import java.sql.*;

public class AdminDAO {

    public Admin findByEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash FROM admins WHERE email = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setId(rs.getInt("id"));
                    a.setNome(rs.getString("nome"));
                    a.setEmail(rs.getString("email"));
                    a.setSenhaHash(rs.getString("senha_hash"));
                    return a;
                }
            }
        }
        return null;
    }
}
```

---

## Services

### `EmailService.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/service/EmailService.java`

```java
package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.ConnectionFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private final Session mailSession;
    private final String fromAddress;
    private final String fromName;

    public EmailService() {
        try {
            Properties config = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
            config.load(is);

            String host     = config.getProperty("mail.smtp.host");
            String port     = config.getProperty("mail.smtp.port");
            String user     = config.getProperty("mail.smtp.user");
            String password = config.getProperty("mail.smtp.password");
            fromAddress     = user;
            fromName        = config.getProperty("mail.from.name", "Achados e Perdidos");

            Properties props = new Properties();
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host",            host);
            props.put("mail.smtp.port",            port);

            mailSession = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar EmailService", e);
        }
    }

    public void enviarCodigoVerificacao(String destinatario, String nomeUsuario, String codigo) {
        try {
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(fromAddress, fromName, "UTF-8"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            msg.setSubject("Seu código de verificação — Achados & Perdidos");

            String corpo = "<div style='font-family:sans-serif;max-width:480px;margin:auto'>"
                + "<h2 style='color:#1a3a5c'>Achados &amp; Perdidos</h2>"
                + "<p>Olá, <strong>" + nomeUsuario + "</strong>!</p>"
                + "<p>Use o código abaixo para verificar sua conta.<br>"
                + "Ele expira em <strong>15 minutos</strong>.</p>"
                + "<div style='font-size:2.5rem;font-weight:bold;letter-spacing:.4rem;"
                + "background:#DBEAFE;color:#1e3a8a;padding:20px 30px;"
                + "border-radius:8px;text-align:center;margin:20px 0'>"
                + codigo + "</div>"
                + "<p style='color:#6B7280;font-size:.85rem'>"
                + "Se você não criou esta conta, ignore este e-mail.</p>"
                + "</div>";

            msg.setContent(corpo, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail de verificação", e);
        }
    }
}
```

---

### `UsuarioService.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/service/UsuarioService.java`

```java
package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.UsuarioDAO;
import br.sesi.achadosperdidos.model.Usuario;
import br.sesi.achadosperdidos.util.CodigoUtil;
import br.sesi.achadosperdidos.util.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class UsuarioService {

    private static final int MAX_TENTATIVAS = 3;
    private static final int CODIGO_EXPIRA_MINUTOS = 15;

    // Senha forte: mín. 8 chars, ao menos 1 maiúscula, 1 minúscula, 1 dígito, 1 símbolo
    private static final Pattern SENHA_FORTE = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|:;',.?/]).{8,}$"
    );
    private static final Pattern EMAIL_VALIDO = Pattern.compile(
        "^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)*\\.[a-zA-Z]{2,}$"
    );

    private final UsuarioDAO dao = new UsuarioDAO();
    private final EmailService emailService = new EmailService();

    // ── Cadastro ───────────────────────────────────────────────

    public void cadastrar(String nomeUsuario, String email, String senha) throws SQLException {
        validarNomeUsuario(nomeUsuario);
        validarEmail(email);
        validarSenhaForte(senha);

        if (dao.findByEmail(email) != null)
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        if (dao.findByNomeUsuario(nomeUsuario) != null)
            throw new IllegalArgumentException("Este nome de usuário já está em uso.");

        String codigo    = CodigoUtil.gerar();
        LocalDateTime exp = LocalDateTime.now().plusMinutes(CODIGO_EXPIRA_MINUTOS);

        Usuario u = new Usuario();
        u.setNomeUsuario(nomeUsuario.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setSenhaHash(PasswordUtil.hash(senha));
        u.setCodigoVerificacao(codigo);
        u.setCodigoExpiracao(exp);
        dao.save(u);

        emailService.enviarCodigoVerificacao(u.getEmail(), u.getNomeUsuario(), codigo);
    }

    // ── Verificação de e-mail ──────────────────────────────────

    public void verificarEmail(String email, String codigoDigitado) throws SQLException {
        Usuario u = dao.findByEmail(email);
        if (u == null)
            throw new IllegalArgumentException("Conta não encontrada.");
        if (u.isVerificado())
            throw new IllegalArgumentException("Conta já verificada.");
        if (u.getCodigoVerificacao() == null || u.getCodigoExpiracao() == null)
            throw new IllegalArgumentException("Nenhum código pendente para este e-mail.");
        if (LocalDateTime.now().isAfter(u.getCodigoExpiracao()))
            throw new IllegalArgumentException("Código expirado. Solicite um novo cadastro.");
        if (!u.getCodigoVerificacao().equals(codigoDigitado.trim()))
            throw new IllegalArgumentException("Código incorreto. Verifique o e-mail.");

        dao.marcarVerificado(u.getId());
    }

    // ── Login ──────────────────────────────────────────────────

    /** Retorna o Usuario autenticado ou lança IllegalArgumentException. */
    public Usuario login(String email, String senha) throws SQLException {
        Usuario u = dao.findByEmail(email.trim().toLowerCase());

        if (u == null)
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        if (!u.isVerificado())
            throw new IllegalArgumentException("Conta não verificada. Confirme o código enviado por e-mail.");
        if (u.isBloqueado())
            throw new IllegalArgumentException("Conta bloqueada após 3 tentativas. Contate o administrador.");

        if (!PasswordUtil.verificar(senha, u.getSenhaHash())) {
            dao.incrementarTentativas(u.getId());
            int restantes = MAX_TENTATIVAS - (u.getTentativasFalha() + 1);
            if (restantes <= 0) {
                dao.bloquear(u.getId());
                throw new IllegalArgumentException(
                    "Senha incorreta. Conta bloqueada após 3 tentativas. Contate o administrador.");
            }
            throw new IllegalArgumentException(
                "Senha incorreta. Tentativa(s) restante(s): " + restantes);
        }

        dao.zerarTentativas(u.getId());
        return u;
    }

    // ── Admin: listar bloqueados e desbloquear ─────────────────

    public List<Usuario> listarBloqueados() throws SQLException {
        return dao.findBloqueados();
    }

    public void desbloquear(int id) throws SQLException {
        dao.desbloquear(id);
    }

    // ── Validações internas ────────────────────────────────────

    private void validarNomeUsuario(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome de usuário é obrigatório.");
        if (nome.trim().length() < 3 || nome.trim().length() > 50)
            throw new IllegalArgumentException("Nome de usuário deve ter entre 3 e 50 caracteres.");
        if (!nome.trim().matches("[a-zA-Z0-9_.\\-]+"))
            throw new IllegalArgumentException(
                "Nome de usuário só pode conter letras, números, '.', '_' ou '-'.");
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        if (!EMAIL_VALIDO.matcher(email.trim()).matches())
            throw new IllegalArgumentException("Formato de e-mail inválido.");
    }

    private void validarSenhaForte(String senha) {
        if (senha == null || senha.isBlank())
            throw new IllegalArgumentException("Senha é obrigatória.");
        if (!SENHA_FORTE.matcher(senha).matches())
            throw new IllegalArgumentException(
                "A senha deve ter no mínimo 8 caracteres, incluindo maiúscula, " +
                "minúscula, número e símbolo especial (ex: !@#$%).");
    }
}
```

---

### `AdminService.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/service/AdminService.java`

```java
package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.AdminDAO;
import br.sesi.achadosperdidos.model.Admin;
import br.sesi.achadosperdidos.util.PasswordUtil;

import java.sql.SQLException;

public class AdminService {

    private final AdminDAO dao = new AdminDAO();

    /** Retorna o Admin autenticado ou lança IllegalArgumentException. */
    public Admin login(String email, String senha) throws SQLException {
        if (email == null || email.isBlank() || senha == null || senha.isBlank())
            throw new IllegalArgumentException("E-mail e senha são obrigatórios.");

        Admin a = dao.findByEmail(email.trim().toLowerCase());
        if (a == null || !PasswordUtil.verificar(senha, a.getSenhaHash()))
            throw new IllegalArgumentException("Credenciais de administrador inválidas.");

        return a;
    }
}
```

---

## Filtros

### `AuthFilter.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/filter/AuthFilter.java`

```java
package br.sesi.achadosperdidos.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/** Protege /itens/** — redireciona para /login se não houver sessão de usuário. */
@WebFilter("/itens/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        boolean logado = (session != null && session.getAttribute("usuarioId") != null);

        if (!logado) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
```

---

### `AdminAuthFilter.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/filter/AdminAuthFilter.java`

```java
package br.sesi.achadosperdidos.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/** Protege /admin/** — redireciona para /admin/login se não houver sessão de admin. */
@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Permite o acesso à própria página de login do admin
        if (request.getServletPath().equals("/admin/login")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean adminLogado = (session != null && session.getAttribute("adminId") != null);

        if (!adminLogado) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
```

---

## Servlets

### `CadastroServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/CadastroServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nomeUsuario = req.getParameter("nomeUsuario");
        String email       = req.getParameter("email");
        String senha       = req.getParameter("senha");
        String confirmacao = req.getParameter("confirmacaoSenha");

        if (!senha.equals(confirmacao)) {
            req.setAttribute("erro", "As senhas não coincidem.");
            req.setAttribute("nomeUsuario", nomeUsuario);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(req, resp);
            return;
        }

        try {
            service.cadastrar(nomeUsuario, email, senha);
            // Guarda e-mail na sessão para a tela de verificação
            req.getSession().setAttribute("emailVerificacao", email.trim().toLowerCase());
            resp.sendRedirect(req.getContextPath() + "/verificar-email");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("nomeUsuario", nomeUsuario);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao cadastrar usuário", e);
            req.setAttribute("erro", "Erro interno. Tente novamente mais tarde.");
            req.getRequestDispatcher("/WEB-INF/views/cadastro.jsp").forward(req, resp);
        }
    }
}
```

---

### `VerificacaoEmailServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/VerificacaoEmailServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/verificar-email")
public class VerificacaoEmailServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Se não há e-mail pendente na sessão, vai para cadastro
        if (req.getSession(false) == null ||
                req.getSession(false).getAttribute("emailVerificacao") == null) {
            resp.sendRedirect(req.getContextPath() + "/cadastro");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/verificacao-email.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        String email  = (session != null) ? (String) session.getAttribute("emailVerificacao") : null;
        String codigo = req.getParameter("codigo");

        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/cadastro");
            return;
        }

        try {
            service.verificarEmail(email, codigo);
            session.removeAttribute("emailVerificacao");
            resp.sendRedirect(req.getContextPath() + "/login?sucesso=Conta+verificada!+Faça+login.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/verificacao-email.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao verificar e-mail", e);
            req.setAttribute("erro", "Erro interno. Tente novamente.");
            req.getRequestDispatcher("/WEB-INF/views/verificacao-email.jsp").forward(req, resp);
        }
    }
}
```

---

### `LoginServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/LoginServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.model.Usuario;
import br.sesi.achadosperdidos.service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Já logado? Redireciona direto
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioId") != null) {
            resp.sendRedirect(req.getContextPath() + "/itens");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        try {
            Usuario u = service.login(email, senha);

            // Cria sessão com dados mínimos do usuário
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioId",       u.getId());
            session.setAttribute("usuarioNome",     u.getNomeUsuario());
            session.setAttribute("usuarioEmail",    u.getEmail());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            resp.sendRedirect(req.getContextPath() + "/itens");

        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao fazer login", e);
            req.setAttribute("erro", "Erro interno. Tente novamente mais tarde.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
```

---

### `LogoutServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/LogoutServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
```

---

### `AdminLoginServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/AdminLoginServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.model.Admin;
import br.sesi.achadosperdidos.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("adminId") != null) {
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        try {
            Admin a = service.login(email, senha);
            HttpSession session = req.getSession(true);
            session.setAttribute("adminId",   a.getId());
            session.setAttribute("adminNome", a.getNome());
            session.setMaxInactiveInterval(60 * 60); // 1 hora
            resp.sendRedirect(req.getContextPath() + "/admin");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro no login do admin", e);
            req.setAttribute("erro", "Erro interno.");
            req.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(req, resp);
        }
    }
}
```

---

### `AdminDashboardServlet.java`

**Caminho:** `src/main/java/br/sesi/achadosperdidos/servlet/AdminDashboardServlet.java`

```java
package br.sesi.achadosperdidos.servlet;

import br.sesi.achadosperdidos.service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    private final UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("usuariosBloqueados", service.listarBloqueados());
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Erro ao listar bloqueados", e);
            req.setAttribute("erro", "Erro ao carregar dados.");
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");
        try {
            int id = Integer.parseInt(idStr);
            service.desbloquear(id);
            resp.sendRedirect(req.getContextPath() + "/admin?sucesso=Conta+desbloqueada!");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin?erro=ID+inválido");
        } catch (SQLException e) {
            getServletContext().log("Erro ao desbloquear usuário id=" + idStr, e);
            resp.sendRedirect(req.getContextPath() + "/admin?erro=Erro+ao+desbloquear");
        }
    }
}
```

---

## Views JSP

### `cadastro.jsp`

**Caminho:** `src/main/webapp/WEB-INF/views/cadastro.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Criar Conta — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/itens" class="header-logo">
            <span class="icon">🔍</span>
            <div><h1>Achados &amp; Perdidos</h1></div>
        </a>
        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-ghost btn-sm">Entrar</a>
        </nav>
    </div>
</header>

<main class="container form-container">
    <div class="card">
        <div class="card-header">
            <span style="font-size:1.4rem">👤</span>
            <h2>Criar Conta</h2>
        </div>
        <div class="card-body">

            <c:if test="${not empty erro}">
                <div class="alert alert-error">⚠ <c:out value="${erro}"/></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/cadastro"
                  id="formCadastro" novalidate>
                <div class="form-grid">

                    <div class="form-group full-width">
                        <label class="form-label" for="nomeUsuario">Nome de Usuário <span class="required">*</span></label>
                        <input type="text" id="nomeUsuario" name="nomeUsuario"
                               class="form-control" required minlength="3" maxlength="50"
                               placeholder="Ex: joao.silva"
                               value="<c:out value='${nomeUsuario}'/>">
                        <span class="form-hint">3–50 caracteres. Letras, números, '.', '_', '-'</span>
                    </div>

                    <div class="form-group full-width">
                        <label class="form-label" for="email">E-mail <span class="required">*</span></label>
                        <input type="email" id="email" name="email"
                               class="form-control" required maxlength="255"
                               placeholder="seuemail@exemplo.com"
                               value="<c:out value='${email}'/>">
                        <span class="form-hint">Um código de verificação será enviado para este e-mail</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="senha">Senha <span class="required">*</span></label>
                        <div class="input-group">
                            <input type="password" id="senha" name="senha"
                                   class="form-control" required minlength="8"
                                   placeholder="Mín. 8 caracteres">
                            <button type="button" class="toggle-senha" onclick="toggleSenha('senha')">👁</button>
                        </div>
                        <span class="form-hint">Mín. 8 chars com maiúscula, minúscula, número e símbolo</span>
                        <div class="senha-strength" id="strengthBar"></div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="confirmacaoSenha">Confirmar Senha <span class="required">*</span></label>
                        <div class="input-group">
                            <input type="password" id="confirmacaoSenha" name="confirmacaoSenha"
                                   class="form-control" required
                                   placeholder="Repita a senha">
                            <button type="button" class="toggle-senha" onclick="toggleSenha('confirmacaoSenha')">👁</button>
                        </div>
                    </div>

                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-ghost">Já tenho conta</a>
                    <button type="submit" class="btn btn-accent">Criar Conta</button>
                </div>
            </form>

        </div>
    </div>
    <p class="form-disclaimer">* Campos obrigatórios · Conformidade LGPD/ECA</p>
</main>

<footer class="footer">Achados &amp; Perdidos &middot; v0.3</footer>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script>
function toggleSenha(id) {
    var el = document.getElementById(id);
    el.type = (el.type === 'password') ? 'text' : 'password';
}
</script>
</body>
</html>
```

---

### `verificacao-email.jsp`

**Caminho:** `src/main/webapp/WEB-INF/views/verificacao-email.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verificar E-mail — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/itens" class="header-logo">
            <span class="icon">🔍</span>
            <div><h1>Achados &amp; Perdidos</h1></div>
        </a>
    </div>
</header>

<main class="container form-container">
    <div class="card">
        <div class="card-header">
            <span style="font-size:1.4rem">✉️</span>
            <h2>Verificar E-mail</h2>
        </div>
        <div class="card-body">

            <p style="color:#374151;margin-bottom:1rem">
                Enviamos um código de 6 dígitos para o seu e-mail.
                Insira-o abaixo para ativar sua conta.<br>
                <small style="color:#6B7280">O código expira em <strong>15 minutos</strong>.</small>
            </p>

            <c:if test="${not empty erro}">
                <div class="alert alert-error">⚠ <c:out value="${erro}"/></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/verificar-email">
                <div class="form-group full-width" style="text-align:center">
                    <label class="form-label" for="codigo">Código de Verificação</label>
                    <input type="text" id="codigo" name="codigo"
                           class="form-control"
                           style="font-size:2rem;letter-spacing:.5rem;text-align:center;max-width:200px;margin:auto"
                           maxlength="6" pattern="\d{6}" required
                           placeholder="000000" autocomplete="off">
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn btn-accent">Verificar</button>
                </div>
            </form>

        </div>
    </div>
</main>

<footer class="footer">Achados &amp; Perdidos &middot; v0.3</footer>
</body>
</html>
```

---

### `login.jsp`

**Caminho:** `src/main/webapp/WEB-INF/views/login.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entrar — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/itens" class="header-logo">
            <span class="icon">🔍</span>
            <div><h1>Achados &amp; Perdidos</h1></div>
        </a>
        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/cadastro" class="btn btn-accent btn-sm">Criar Conta</a>
        </nav>
    </div>
</header>

<main class="container form-container">
    <div class="card">
        <div class="card-header">
            <span style="font-size:1.4rem">🔐</span>
            <h2>Entrar</h2>
        </div>
        <div class="card-body">

            <c:if test="${not empty param.sucesso}">
                <div class="alert alert-success">✓ <c:out value="${param.sucesso}"/></div>
            </c:if>
            <c:if test="${not empty erro}">
                <div class="alert alert-error">⚠ <c:out value="${erro}"/></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-grid">

                    <div class="form-group full-width">
                        <label class="form-label" for="email">E-mail <span class="required">*</span></label>
                        <input type="email" id="email" name="email"
                               class="form-control" required
                               placeholder="seuemail@exemplo.com"
                               value="<c:out value='${email}'/>">
                    </div>

                    <div class="form-group full-width">
                        <label class="form-label" for="senha">Senha <span class="required">*</span></label>
                        <div class="input-group">
                            <input type="password" id="senha" name="senha"
                                   class="form-control" required placeholder="Sua senha">
                            <button type="button" class="toggle-senha" onclick="toggleSenha('senha')">👁</button>
                        </div>
                        <span class="form-hint">
                            Após 3 tentativas erradas, a conta será bloqueada.
                        </span>
                    </div>

                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/cadastro" class="btn btn-ghost">Criar conta</a>
                    <button type="submit" class="btn btn-accent">Entrar</button>
                </div>
            </form>

        </div>
    </div>
</main>

<footer class="footer">Achados &amp; Perdidos &middot; v0.3</footer>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script>
function toggleSenha(id) {
    var el = document.getElementById(id);
    el.type = (el.type === 'password') ? 'text' : 'password';
}
</script>
</body>
</html>
```

---

### `admin/login.jsp`

**Caminho:** `src/main/webapp/WEB-INF/views/admin/login.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="background:#1a3a5c">

<main class="container form-container">
    <div class="card" style="max-width:400px;margin:80px auto">
        <div class="card-header" style="background:#1a3a5c">
            <span style="font-size:1.4rem">🛡️</span>
            <h2 style="color:#fff">Painel Administrativo</h2>
        </div>
        <div class="card-body">

            <c:if test="${not empty erro}">
                <div class="alert alert-error">⚠ <c:out value="${erro}"/></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/login">
                <div class="form-group full-width">
                    <label class="form-label" for="email">E-mail do Admin</label>
                    <input type="email" id="email" name="email" class="form-control"
                           required placeholder="admin@sesi.org.br">
                </div>
                <div class="form-group full-width">
                    <label class="form-label" for="senha">Senha</label>
                    <input type="password" id="senha" name="senha" class="form-control"
                           required placeholder="Senha administrativa">
                </div>
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-ghost">Voltar</a>
                    <button type="submit" class="btn btn-accent">Entrar</button>
                </div>
            </form>

        </div>
    </div>
</main>
</body>
</html>
```

---

### `admin/dashboard.jsp`

**Caminho:** `src/main/webapp/WEB-INF/views/admin/dashboard.jsp`

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"     prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/admin" class="header-logo">
            <span class="icon">🛡️</span>
            <div><h1>Admin — Achados &amp; Perdidos</h1></div>
        </a>
        <nav class="header-nav">
            <span style="color:#93C5FD;margin-right:1rem;font-size:.9rem">
                Olá, <strong><c:out value="${sessionScope.adminNome}"/></strong>
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-ghost btn-sm">Sair</a>
        </nav>
    </div>
</header>

<main class="container" style="padding-top:2rem">

    <c:if test="${not empty param.sucesso}">
        <div class="alert alert-success">✓ <c:out value="${param.sucesso}"/></div>
    </c:if>
    <c:if test="${not empty param.erro}">
        <div class="alert alert-error">⚠ <c:out value="${param.erro}"/></div>
    </c:if>

    <div class="section-header">
        <h2>Contas Bloqueadas</h2>
        <span class="item-count">${fn:length(usuariosBloqueados)} conta(s)</span>
    </div>

    <c:choose>
        <c:when test="${empty usuariosBloqueados}">
            <div class="empty-state">
                <span class="empty-icon">✅</span>
                <p>Nenhuma conta bloqueada no momento.</p>
            </div>
        </c:when>
        <c:otherwise>
            <table style="width:100%;border-collapse:collapse;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 1px 4px rgba(0,0,0,.08)">
                <thead>
                    <tr style="background:#1a3a5c;color:#fff">
                        <th style="padding:12px 16px;text-align:left">ID</th>
                        <th style="padding:12px 16px;text-align:left">Usuário</th>
                        <th style="padding:12px 16px;text-align:left">E-mail</th>
                        <th style="padding:12px 16px;text-align:left">Tentativas</th>
                        <th style="padding:12px 16px;text-align:left">Cadastro</th>
                        <th style="padding:12px 16px;text-align:center">Ação</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${usuariosBloqueados}" varStatus="s">
                        <tr style="background:${s.index % 2 == 0 ? '#f9fafb' : '#fff'};border-bottom:1px solid #E5E7EB">
                            <td style="padding:12px 16px">${u.id}</td>
                            <td style="padding:12px 16px;font-weight:600"><c:out value="${u.nomeUsuario}"/></td>
                            <td style="padding:12px 16px"><c:out value="${u.email}"/></td>
                            <td style="padding:12px 16px">
                                <span style="background:#FEE2E2;color:#DC2626;padding:2px 10px;border-radius:99px;font-size:.8rem;font-weight:bold">
                                    ${u.tentativasFalha}x
                                </span>
                            </td>
                            <td style="padding:12px 16px;font-size:.85rem;color:#6B7280">${u.dataCadastro}</td>
                            <td style="padding:12px 16px;text-align:center">
                                <form method="post" action="${pageContext.request.contextPath}/admin"
                                      onsubmit="return confirm('Desbloquear ${u.nomeUsuario}?')">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-success btn-sm">
                                        ✓ Desbloquear
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

</main>

<footer class="footer">Achados &amp; Perdidos · Admin · v0.3</footer>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
```

---

## web.xml

Adicione dentro de `<web-app>` (o `<welcome-file-list>` já existe, inclua abaixo das tags de erro):

```xml
<!-- Sessão expira em 30 minutos de inatividade -->
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

> Os servlets (`/cadastro`, `/login`, `/logout`, `/verificar-email`, `/admin`, `/admin/login`)
> são registrados automaticamente via `@WebServlet`.  
> Os filtros (`AuthFilter`, `AdminAuthFilter`) são registrados via `@WebFilter`.  
> Nenhuma entrada adicional em `web.xml` é necessária para eles.

---

## Estrutura de Arquivos Adicionados

```
src/main/java/br/sesi/achadosperdidos/
│
├── util/
│   ├── PasswordUtil.java
│   └── CodigoUtil.java
│
├── model/
│   ├── Usuario.java              ← novo
│   └── Admin.java                ← novo
│
├── dao/
│   ├── UsuarioDAO.java           ← novo
│   └── AdminDAO.java             ← novo
│
├── service/
│   ├── EmailService.java         ← novo
│   ├── UsuarioService.java       ← novo
│   └── AdminService.java         ← novo
│
├── filter/
│   ├── AuthFilter.java           ← novo
│   └── AdminAuthFilter.java      ← novo
│
└── servlet/
    ├── CadastroServlet.java      ← novo
    ├── LoginServlet.java         ← novo
    ├── LogoutServlet.java        ← novo
    ├── VerificacaoEmailServlet.java ← novo
    ├── AdminLoginServlet.java    ← novo
    └── AdminDashboardServlet.java   ← novo

src/main/webapp/WEB-INF/views/
├── cadastro.jsp                  ← novo
├── verificacao-email.jsp         ← novo
├── login.jsp                     ← novo
└── admin/
    ├── login.jsp                 ← novo
    └── dashboard.jsp             ← novo

schema_v03.sql                    ← novo
```
