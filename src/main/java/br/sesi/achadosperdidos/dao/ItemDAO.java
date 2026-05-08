package br.sesi.achadosperdidos.dao;

import br.sesi.achadosperdidos.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private static final String SELECT_COLS =
        "id, descricao, categoria, local_encontrado, data_encontrado, " +
        "status, data_cadastro, observacoes, foto_path";

    public List<Item> findAll(String status, String busca, String categoria) throws SQLException {
        List<Item> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " + SELECT_COLS + " FROM itens WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }
        if (busca != null && !busca.isBlank()) {
            sql.append(" AND descricao LIKE ?");
            params.add("%" + busca.trim() + "%");
        }
        if (categoria != null && !categoria.isBlank()) {
            sql.append(" AND categoria = ?");
            params.add(categoria.trim());
        }
        sql.append(" ORDER BY data_cadastro DESC");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Item findById(int id) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM itens WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void save(Item item) throws SQLException {
        String sql =
            "INSERT INTO itens " +
            "(descricao, categoria, local_encontrado, data_encontrado, status, observacoes, foto_path) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getDescricao());
            ps.setString(2, item.getCategoria());
            ps.setString(3, item.getLocalEncontrado());
            ps.setDate(4, Date.valueOf(item.getDataEncontrado()));
            ps.setString(5, item.getStatus() != null ? item.getStatus() : "ACHADO");
            ps.setString(6, item.getObservacoes());
            ps.setString(7, item.getFotoPath());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) item.setId(keys.getInt(1));
            }
        }
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE itens SET status = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM itens WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setDescricao(rs.getString("descricao"));
        item.setCategoria(rs.getString("categoria"));
        item.setLocalEncontrado(rs.getString("local_encontrado"));

        Date d = rs.getDate("data_encontrado");
        if (d != null) item.setDataEncontrado(d.toLocalDate());

        item.setStatus(rs.getString("status"));

        Timestamp ts = rs.getTimestamp("data_cadastro");
        if (ts != null) item.setDataCadastro(ts.toLocalDateTime());

        item.setObservacoes(rs.getString("observacoes"));
        item.setFotoPath(rs.getString("foto_path"));
        return item;
    }
}
