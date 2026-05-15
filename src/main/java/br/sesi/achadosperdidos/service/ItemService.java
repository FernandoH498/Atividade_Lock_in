package br.sesi.achadosperdidos.service;

import br.sesi.achadosperdidos.dao.ItemDAO;
import br.sesi.achadosperdidos.model.Item;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ItemService {

    private static final int DESC_MAX_LEN = 255;
    private static final List<String> CATEGORIAS_VALIDAS = List.of(
        "ELETRÔNICOS", "DOCUMENTOS", "ROUPAS", "ACESSÓRIOS", "OUTROS"
    );
    private static final Set<String> EXTENSOES_IMAGEM = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private final ItemDAO dao = new ItemDAO();

    public List<Item> listarItens(String status, String busca, String categoria) throws SQLException {
        return dao.findAll(status, busca, categoria);
    }

    public Item buscarItemPorId(int id) throws SQLException {
        Item item = dao.findById(id);
        if (item == null) throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        return item;
    }

    public void cadastrarItem(Item item) throws SQLException {
        validar(item);
        if (item.getStatus() == null || item.getStatus().isBlank()) item.setStatus("ACHADO");
        dao.save(item);
    }

    public void devolverItem(int id) throws SQLException {
        dao.updateStatus(id, "DEVOLVIDO");
    }

    public void excluirItem(int id) throws SQLException {
        dao.delete(id);
    }

    public boolean isExtensaoImagemValida(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) return false;
        String lower = nomeArquivo.toLowerCase();
        return EXTENSOES_IMAGEM.stream().anyMatch(lower::endsWith);
    }

    private void validar(Item item) {
        if (item.getDescricao() == null || item.getDescricao().isBlank())
            throw new IllegalArgumentException("A descrição do item é obrigatória.");
        if (item.getDescricao().length() > DESC_MAX_LEN)
            throw new IllegalArgumentException("A descrição deve ter no máximo " + DESC_MAX_LEN + " caracteres.");
        if (item.getCategoria() == null || item.getCategoria().isBlank())
            throw new IllegalArgumentException("A categoria é obrigatória.");
        if (!CATEGORIAS_VALIDAS.contains(item.getCategoria()))
            throw new IllegalArgumentException("Categoria inválida.");
        if (item.getLocalEncontrado() == null || item.getLocalEncontrado().isBlank())
            throw new IllegalArgumentException("O local onde o item foi encontrado é obrigatório.");
        if (item.getDataEncontrado() == null)
            throw new IllegalArgumentException("A data em que o item foi encontrado é obrigatória.");
        if (item.getDataEncontrado().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("A data não pode ser no futuro.");
    }
}
