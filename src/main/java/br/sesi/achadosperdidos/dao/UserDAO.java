package br.sesi.achadosperdidos.dao;

import br.sesi.achadosperdidos.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setNome(rs.getString("nome"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setPapel(rs.getString("papel"));
        u.setStatus(rs.getString("status"));
        u.setCodigoVerificacao(rs.getString("codigo_verificacao"));
        Timestamp exp = rs.getTimestamp("codigo_expira");
        u.setCodigoExpira(exp != null ? exp.toLocalDateTime() : null);
        u.setTentativasFalhas(rs.getInt("tentativas_falhas"));
        Timestamp criado = rs.getTimestamp("criado_em");
        u.setCriadoEm(criado != null ? criado.toLocalDateTime() : null);
        Timestamp login = rs.getTimestamp("ultimo_login");
        u.setUltimoLogin(login != null ? login.toLocalDateTime() : null);
        return u;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? OR username = ? LIMIT 1";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM usuarios ORDER BY criado_em DESC";
        List<User> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int save(User u) throws SQLException {
        String sql = "INSERT INTO usuarios (username, email, nome, senha_hash, papel, status, " +
                     "codigo_verificacao, codigo_expira, tentativas_falhas) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getNome());
            ps.setString(4, u.getSenhaHash());
            ps.setString(5, u.getPapel());
            ps.setString(6, u.getStatus());
            ps.setString(7, u.getCodigoVerificacao());
            ps.setTimestamp(8, u.getCodigoExpira() != null ? Timestamp.valueOf(u.getCodigoExpira()) : null);
            ps.setInt(9, u.getTentativasFalhas());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE usuarios SET status = ?, tentativas_falhas = 0 WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updateFailedAttempts(int id, int attempts, String status) throws SQLException {
        String sql = "UPDATE usuarios SET tentativas_falhas = ?, status = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, attempts);
            ps.setString(2, status);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void updateVerificationCode(int id, String code, LocalDateTime expira) throws SQLException {
        String sql = "UPDATE usuarios SET codigo_verificacao = ?, codigo_expira = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setTimestamp(2, Timestamp.valueOf(expira));
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void activateAccount(int id) throws SQLException {
        String sql = "UPDATE usuarios SET status = 'ATIVO', codigo_verificacao = NULL, " +
                     "codigo_expira = NULL WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateUltimoLogin(int id) throws SQLException {
        String sql = "UPDATE usuarios SET ultimo_login = NOW() WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void updateNome(int id, String nome) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void updateSenha(int id, String senhaHash) throws SQLException {
        String sql = "UPDATE usuarios SET senha_hash = ? WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, senhaHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
