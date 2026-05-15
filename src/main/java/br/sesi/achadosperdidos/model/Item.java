package br.sesi.achadosperdidos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Item {

    private int id;
    private String descricao;
    private String categoria;
    private String localEncontrado;
    private LocalDate dataEncontrado;
    private String status;
    private LocalDateTime dataCadastro;
    private String observacoes;
    private String fotoPath;

    public Item() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getLocalEncontrado() { return localEncontrado; }
    public void setLocalEncontrado(String localEncontrado) { this.localEncontrado = localEncontrado; }

    public LocalDate getDataEncontrado() { return dataEncontrado; }
    public void setDataEncontrado(LocalDate dataEncontrado) { this.dataEncontrado = dataEncontrado; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    /** Formato dd/MM/yyyy — usado nas views de listagem. */
    public String getDataEncontradoFormatada() {
        return dataEncontrado != null
                ? dataEncontrado.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
    }

    /** Formato yyyy-MM-dd — compatível com input[type=date] HTML. */
    public String getDataEncontradoIso() {
        return dataEncontrado != null ? dataEncontrado.toString() : "";
    }

    /** Formato dd/MM/yyyy HH:mm — usado na coluna de cadastro. */
    public String getDataCadastroFormatada() {
        return dataCadastro != null
                ? dataCadastro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
    }
}
