<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"     prefix="c"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<!-- HEADER -->
<header class="header">
    <div class="header-inner">
        <a href="${pageContext.request.contextPath}/itens" class="header-logo">
            <span class="icon">🔍</span>
            <div>
                <h1>Achados &amp; Perdidos</h1>
            </div>
        </a>
        <nav class="header-nav">
            <a href="${pageContext.request.contextPath}/itens/novo" class="btn btn-accent btn-sm">
                + Registrar Item
            </a>
        </nav>
    </div>
</header>

<!-- HERO -->
<section class="hero">
    <div class="hero-inner">
        <span class="hero-badge">v0.1</span>
        <h2>Perdeu algo?<br>Encontrou algo?</h2>
        <p>Plataforma de Achados &amp; Perdidos</p>

        <div class="hero-stats">
            <div class="stat">
                <span class="stat-number">${totalItens}</span>
                <span class="stat-label">Itens Cadastrados</span>
            </div>
            <div class="stat">
                <span class="stat-number">${totalAchados}</span>
                <span class="stat-label">Aguardando dono</span>
            </div>
            <div class="stat">
                <span class="stat-number">${totalDevolvidos}</span>
                <span class="stat-label">Devolvidos</span>
            </div>
        </div>

        <div class="hero-actions">
            <a href="#itens" class="btn-hero-primary">🔍 Perdi algo</a>
            <a href="${pageContext.request.contextPath}/itens/novo" class="btn-hero-secondary">📦 Achei algo</a>
        </div>
    </div>
</section>

<!-- CONTEÚDO -->
<main class="container" id="itens">

    <c:if test="${not empty param.sucesso}">
        <div class="alert alert-success">&#10003; <c:out value="${param.sucesso}"/></div>
    </c:if>
    <c:if test="${not empty param.erro}">
        <div class="alert alert-error">&#9888; <c:out value="${param.erro}"/></div>
    </c:if>
    <c:if test="${not empty erro}">
        <div class="alert alert-error">&#9888; <c:out value="${erro}"/></div>
    </c:if>

    <!-- Filtros -->
    <div class="filter-section">
        <h3>Filtrar itens</h3>
        <form method="get" action="${pageContext.request.contextPath}/itens" class="filter-bar">
            <div class="filter-group">
                <label for="status">Status</label>
                <select name="status" id="status">
                    <option value="">Todos</option>
                    <option value="ACHADO"    <c:if test="${filtroStatus == 'ACHADO'}">selected</c:if>>Achado</option>
                    <option value="DEVOLVIDO" <c:if test="${filtroStatus == 'DEVOLVIDO'}">selected</c:if>>Devolvido</option>
                </select>
            </div>
            <div class="filter-group">
                <label for="categoria">Categoria</label>
                <select name="categoria" id="categoria">
                    <option value="">Todas</option>
                    <option value="ELETRÔNICOS"  <c:if test="${filtroCategoria == 'ELETRÔNICOS'}">selected</c:if>>Eletrônicos</option>
                    <option value="DOCUMENTOS"   <c:if test="${filtroCategoria == 'DOCUMENTOS'}">selected</c:if>>Documentos</option>
                    <option value="ROUPAS"        <c:if test="${filtroCategoria == 'ROUPAS'}">selected</c:if>>Roupas</option>
                    <option value="ACESSÓRIOS"   <c:if test="${filtroCategoria == 'ACESSÓRIOS'}">selected</c:if>>Acessórios</option>
                    <option value="OUTROS"        <c:if test="${filtroCategoria == 'OUTROS'}">selected</c:if>>Outros</option>
                </select>
            </div>
            <div class="filter-group">
                <label for="busca">Buscar descrição</label>
                <input type="text" name="busca" id="busca"
                       placeholder="Ex: mochila, carteira..."
                       value="<c:out value="${filtroBusca}"/>">
            </div>
            <button type="submit" class="btn btn-primary">Filtrar</button>
            <a href="${pageContext.request.contextPath}/itens" class="btn btn-ghost">Limpar</a>
        </form>
    </div>

    <!-- Grid de cards -->
    <div class="section-header">
        <h2>Itens</h2>
        <span class="item-count">
            <c:choose>
                <c:when test="${empty itens}">Nenhum resultado</c:when>
                <c:otherwise>${fn:length(itens)} item(ns)</c:otherwise>
            </c:choose>
        </span>
    </div>

    <div class="cards-grid">
        <c:choose>
            <c:when test="${empty itens}">
                <div class="empty-state">
                    <span class="empty-icon">&#128229;</span>
                    <p>Nenhum item encontrado com os filtros aplicados.</p>
                    <a href="${pageContext.request.contextPath}/itens/novo" class="btn btn-accent">
                        + Registrar Primeiro Item
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="item" items="${itens}">
                    <article class="item-card">

                        <div class="item-card-photo">
                            <c:choose>
                                <c:when test="${not empty item.fotoPath}">
                                    <img src="${pageContext.request.contextPath}/uploads/<c:out value="${item.fotoPath}"/>"
                                         alt="Foto do item">
                                </c:when>
                                <c:otherwise>
                                    <div class="item-card-no-photo">
                                        &#128230;
                                        <span>Sem foto</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${item.status == 'ACHADO'}">
                                    <span class="badge badge-achado badge-overlay">&#128269; Achado</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-devolvido badge-overlay">&#10003; Devolvido</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="item-card-body">
                            <span class="item-card-category"><c:out value="${item.categoria}"/></span>
                            <h3 class="item-card-title"><c:out value="${item.descricao}"/></h3>
                            <div class="item-card-meta">
                                <span>&#128205; <c:out value="${item.localEncontrado}"/></span>
                                <span>&#128197; ${item.dataEncontradoFormatada}</span>
                            </div>
                        </div>

                        <div class="item-card-footer">
                            <a href="${pageContext.request.contextPath}/itens/detalhe?id=${item.id}"
                               class="btn btn-secondary btn-sm btn-full">
                                &#128269; Ver Detalhes
                            </a>
                        </div>

                    </article>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

</main>

<footer class="footer">
    Achados &amp; Perdidos &middot; v0.1
</footer>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
