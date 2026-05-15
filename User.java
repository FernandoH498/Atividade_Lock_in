package br.sesi.achadosperdidos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String username;
    private String email;
    private String nome;
    private String senhaHash;
    private String papel;
    private String status;
    private String codigoVerificacao;
    private LocalDateTime codigoExpira;
    private int tentativasFalhas;
    private LocalDateTime criadoEm;
    private LocalDateTime ultimoLogin;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCodigoVerificacao() { return codigoVerificacao; }
    public void setCodigoVerificacao(String codigoVerificacao) { this.codigoVerificacao = codigoVerificacao; }

    public LocalDateTime getCodigoExpira() { return codigoExpira; }
    public void setCodigoExpira(LocalDateTime codigoExpira) { this.codigoExpira = codigoExpira; }

    public int getTentativasFalhas() { return tentativasFalhas; }
    public void setTentativasFalhas(int tentativasFalhas) { this.tentativasFalhas = tentativasFalhas; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }

    public String getLogin() {
        return username != null ? username : email;
    }

    public boolean isAdmin() { return "ADMIN".equals(papel); }
    public boolean isAtivo() { return "ATIVO".equals(status); }
    public boolean isBloqueado() { return "BLOQUEADO".equals(status); }
    public boolean isPendente() { return "PENDENTE".equals(status); }

    public String getCriadoEmFormatado() {
        return criadoEm != null ? criadoEm.format(FMT) : "-";
    }

    public String getUltimoLoginFormatado() {
        return ultimoLogin != null ? ultimoLogin.format(FMT) : "Nunca";
    }
}
