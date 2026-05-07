<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Item — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/itens" class="header-logo">
            <span class="icon">🔍</span>
            <div>
                <h1>Achados &amp; Perdidos</h1>
            </div>
        </a>
        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/itens" class="btn btn-ghost btn-sm">
                &#8592; Voltar
            </a>
        </nav>
    </div>
</header>

<main class="container form-container">

    <c:if test="${not empty erro}">
        <div class="alert alert-error">&#9888; <c:out value="${erro}"/></div>
    </c:if>

    <div class="card">
        <div class="card-header">
            <span style="font-size:1.4rem;">&#128230;</span>
            <h2>Registrar Item Achado</h2>
        </div>
        <div class="card-body">
            <form id="itemForm" method="post" enctype="multipart/form-data"
                  action="${pageContext.request.contextPath}/itens/novo">

                <div class="form-grid">

                    <!-- Foto -->
                    <div class="form-group full-width">
                        <label class="form-label">Foto do Item</label>
                        <div class="photo-upload-area" id="photoUploadArea">
                            <img id="fotoPreview" src="" alt="Preview da foto">
                            <div class="photo-placeholder" id="photoPlaceholder">
                                <span class="icon">&#128247;</span>
                                <p>Clique para adicionar uma foto</p>
                                <small>JPG, PNG, WEBP &middot; máx. 5MB</small>
                            </div>
                            <input type="file" id="foto" name="foto"
                                   accept="image/*" class="photo-input">
                        </div>
                        <span class="form-hint">Foto opcional — mas ajuda muito quem perdeu o item!</span>
                    </div>

                    <!-- Descrição + IA -->
                    <div class="form-group full-width">
                        <label class="form-label" for="descricao">
                            Descrição do Item <span class="required">*</span>
                        </label>
                        <input type="text" id="descricao" name="descricao"
                               class="form-control"
                               placeholder="Ex: Mochila escolar preta com zíper amarelo"
                               maxlength="255"
                               value="<c:out value="${item.descricao}"/>">
                        <span class="form-hint">Seja objetivo (máx. 255 caracteres)</span>

                        <div class="ai-panel" id="aiPanel">
                            <div class="ai-panel-header">
                                <span class="ai-badge">IA</span>
                                Assistente de Categorização
                            </div>
                            <p class="ai-suggestion-text" id="aiSuggestionText"></p>
                            <button type="button" class="btn btn-sm btn-primary" id="aiAccept">
                                Aceitar Sugestão
                            </button>
                        </div>
                    </div>

                    <!-- Categoria -->
                    <div class="form-group">
                        <label class="form-label" for="categoria">
                            Categoria <span class="required">*</span>
                        </label>
                        <select id="categoria" name="categoria" class="form-control">
                            <option value="">Selecione...</option>
                            <c:set var="cat" value="${item.categoria}"/>
                            <option value="ELETRÔNICOS"  <c:if test="${cat == 'ELETRÔNICOS'}">selected</c:if>>Eletrônicos</option>
                            <option value="DOCUMENTOS"   <c:if test="${cat == 'DOCUMENTOS'}">selected</c:if>>Documentos</option>
                            <option value="ROUPAS"        <c:if test="${cat == 'ROUPAS'}">selected</c:if>>Roupas</option>
                            <option value="ACESSÓRIOS"   <c:if test="${cat == 'ACESSÓRIOS'}">selected</c:if>>Acessórios</option>
                            <option value="OUTROS"        <c:if test="${cat == 'OUTROS'}">selected</c:if>>Outros</option>
                        </select>
                    </div>

                    <!-- Local -->
                    <div class="form-group">
                        <label class="form-label" for="localEncontrado">
                            Local Encontrado <span class="required">*</span>
                        </label>
                        <input type="text" id="localEncontrado" name="localEncontrado"
                               class="form-control"
                               placeholder="Ex: Refeitório Central"
                               value="<c:out value="${item.localEncontrado}"/>">
                    </div>

                    <!-- Data -->
                    <div class="form-group">
                        <label class="form-label" for="dataEncontrado">
                            Data Encontrado <span class="required">*</span>
                        </label>
                        <input type="date" id="dataEncontrado" name="dataEncontrado"
                               class="form-control"
                               value="${item.dataEncontradoIso}">
                        <span class="form-hint">Não pode ser data futura</span>
                    </div>

                    <!-- Observações -->
                    <div class="form-group full-width">
                        <label class="form-label" for="observacoes">Observações</label>
                        <textarea id="observacoes" name="observacoes"
                                  class="form-control" rows="3" maxlength="500"
                                  placeholder="Detalhes adicionais (opcional)"><c:out value="${item.observacoes}"/></textarea>
                        <span class="char-counter" id="charCounter">0/500</span>
                    </div>

                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/itens" class="btn btn-ghost">
                        Cancelar
                    </a>
                    <button type="submit" class="btn btn-accent">
                        &#10003; Registrar Item
                    </button>
                </div>

            </form>
        </div>
    </div>

    <p class="form-disclaimer">
        * Campos obrigatórios &middot; Dados armazenados em conformidade com LGPD e ECA
    </p>

</main>

<footer class="footer">
    Achados &amp; Perdidos &middot; v0.1
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
