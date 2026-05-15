package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.UserDAO;
import br.sesi.achadosperdidos.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class UserService {

    private static final int MAX_TENTATIVAS = 3;
    private static final int CODIGO_EXPIRA_MINUTOS = 15;
    private static final int BCRYPT_ROUNDS = 12;

    private final UserDAO dao = new UserDAO();
    private final EmailService emailService = new EmailService();
    private final SecureRandom rng = new SecureRandom();

    public enum LoginResultado { OK, CREDENCIAIS_INVALIDAS, CONTA_BLOQUEADA, EMAIL_NAO_VERIFICADO }

    public User registrar(String email, String nome, String senha) throws SQLException, RegistroException {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new RegistroException("E-mail inválido.");
        if (nome == null || nome.trim().length() < 2)
            throw new RegistroException("Nome deve ter ao menos 2 caracteres.");
        if (senha == null || senha.length() < 8)
            throw new RegistroException("Senha deve ter ao menos 8 caracteres.");

        email = email.trim().toLowerCase();
        nome = nome.trim();

        if (dao.existsByEmail(email))
            throw new RegistroException("Este e-mail já está cadastrado.");

        String hash = BCrypt.hashpw(senha, BCrypt.gensalt(BCRYPT_ROUNDS));
        String codigo = gerarCodigo6Digitos();
        LocalDateTime expira = LocalDateTime.now().plusMinutes(CODIGO_EXPIRA_MINUTOS);

        User u = new User();
        u.setEmail(email);
        u.setNome(nome);
        u.setSenhaHash(hash);
        u.setPapel("USUARIO");
        u.setStatus("PENDENTE");
        u.setCodigoVerificacao(codigo);
        u.setCodigoExpira(expira);
        u.setTentativasFalhas(0);

        int id = dao.save(u);
        u.setId(id);

        try {
            emailService.enviarCodigoVerificacao(email, nome, codigo);
        } catch (Exception e) {
            dao.delete(id);
            e.printStackTrace();
            throw new RegistroException("Falha ao enviar e-mail de verificação. Tente novamente em instantes.");
        }

        return u;
    }

    public boolean verificarEmail(String email, String codigo) throws SQLException {
        if (email == null || codigo == null) return false;
        email = email.trim().toLowerCase();
        User u = dao.findByEmail(email);
        if (u == null) return false;
        if (!"PENDENTE".equals(u.getStatus())) return false;
        if (u.getCodigoExpira() == null || LocalDateTime.now().isAfter(u.getCodigoExpira())) return false;
        if (!codigo.trim().equals(u.getCodigoVerificacao())) return false;

        dao.activateAccount(u.getId());
        return true;
    }

    public LoginResultado login(String loginInput, String senha, int[] idOut) throws SQLException {
        if (loginInput == null || senha == null) return LoginResultado.CREDENCIAIS_INVALIDAS;

        User u = dao.findByLogin(loginInput.trim());
        if (u == null) return LoginResultado.CREDENCIAIS_INVALIDAS;

        if ("BLOQUEADO".equals(u.getStatus())) return LoginResultado.CONTA_BLOQUEADA;
        if ("PENDENTE".equals(u.getStatus())) return LoginResultado.EMAIL_NAO_VERIFICADO;

        if (!BCrypt.checkpw(senha, u.getSenhaHash())) {
            int novasTentativas = u.getTentativasFalhas() + 1;
            String novoStatus = novasTentativas >= MAX_TENTATIVAS ? "BLOQUEADO" : "ATIVO";
            dao.updateFailedAttempts(u.getId(), novasTentativas, novoStatus);
            return "BLOQUEADO".equals(novoStatus)
                    ? LoginResultado.CONTA_BLOQUEADA
                    : LoginResultado.CREDENCIAIS_INVALIDAS;
        }

        dao.updateFailedAttempts(u.getId(), 0, "ATIVO");
        dao.updateUltimoLogin(u.getId());
        if (idOut != null && idOut.length > 0) idOut[0] = u.getId();
        return LoginResultado.OK;
    }

    public void desbloquearConta(int userId) throws SQLException {
        dao.updateStatus(userId, "ATIVO");
    }

    public void reenviarCodigo(String email) throws SQLException, RegistroException {
        email = email.trim().toLowerCase();
        User u = dao.findByEmail(email);
        if (u == null || !"PENDENTE".equals(u.getStatus())) return;

        String codigo = gerarCodigo6Digitos();
        LocalDateTime expira = LocalDateTime.now().plusMinutes(CODIGO_EXPIRA_MINUTOS);
        dao.updateVerificationCode(u.getId(), codigo, expira);

        try {
            emailService.enviarCodigoVerificacao(email, u.getNome(), codigo);
        } catch (Exception e) {
            throw new RegistroException("Falha ao enviar e-mail. Tente novamente em instantes.");
        }
    }

    public List<User> listarTodos() throws SQLException {
        return dao.findAll();
    }

    public User buscarPorId(int id) throws SQLException {
        return dao.findById(id);
    }

    public void atualizarNome(int id, String nome) throws SQLException {
        if (nome == null || nome.trim().length() < 2) return;
        dao.updateNome(id, nome.trim());
    }

    public void atualizarSenha(int id, String novaSenha) throws SQLException {
        if (novaSenha == null || novaSenha.length() < 8) return;
        dao.updateSenha(id, BCrypt.hashpw(novaSenha, BCrypt.gensalt(BCRYPT_ROUNDS)));
    }

    public void excluirUsuario(int id) throws SQLException {
        dao.delete(id);
    }

    public void criarAdminSeNaoExistir(String username, String senha) throws SQLException {
        if (dao.findByUsername(username) != null) return;

        String hash = BCrypt.hashpw(senha, BCrypt.gensalt(BCRYPT_ROUNDS));
        User admin = new User();
        admin.setUsername(username);
        admin.setNome("Administrador");
        admin.setSenhaHash(hash);
        admin.setPapel("ADMIN");
        admin.setStatus("ATIVO");
        admin.setTentativasFalhas(0);
        dao.save(admin);
    }

    private String gerarCodigo6Digitos() {
        return String.format("%06d", rng.nextInt(1_000_000));
    }

    public static class RegistroException extends Exception {
        public RegistroException(String msg) { super(msg); }
    }
}
