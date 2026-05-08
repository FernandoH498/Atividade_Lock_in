<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalhe do Item — Achados &amp; Perdidos</title>
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
            <a href="${pageContext.request.contextPath}/itens/novo" class="btn btn-accent btn-sm">
                + Registrar Item
            </a>
        </nav>
    </div>
</header>

<main class="container" style="max-width: 700px; margin: 2rem auto; padding: 0 1rem;">

    <a href="${pageContext.request.contextPath}/itens" class="btn btn-secondary btn-sm"
       style="margin-bottom: 1.5rem; display: inline-block;">&larr; Voltar para a lista</a>

    <div class="card" style="padding: 2rem;">
        <h2 style="margin-top: 0; margin-bottom: 1.5rem;">Detalhe do Item #${item.id}</h2>

        <table style="width: 100%; border-collapse: collapse;">
            <tr>
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; width: 40%; color: #555;">Descrição</th>
                <td style="padding: 0.6rem 0;"><c:out value="${item.descricao}"/></td>
            </tr>
            <tr style="background: #f9f9f9;">
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555;">Categoria</th>
                <td style="padding: 0.6rem 0;"><c:out value="${item.categoria}"/></td>
            </tr>
            <tr>
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555;">Local encontrado</th>
                <td style="padding: 0.6rem 0;"><c:out value="${item.localEncontrado}"/></td>
            </tr>
            <tr style="background: #f9f9f9;">
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555;">Data encontrado</th>
                <td style="padding: 0.6rem 0;">${item.dataEncontradoFormatada}</td>
            </tr>
            <tr>
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555;">Status</th>
                <td style="padding: 0.6rem 0;">
                    <span class="badge <c:choose><c:when test="${item.status == 'ACHADO'}">badge-achado</c:when><c:otherwise>badge-devolvido</c:otherwise></c:choose>">
                        ${item.status}
                    </span>
                </td>
            </tr>
            <tr style="background: #f9f9f9;">
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555;">Data de cadastro</th>
                <td style="padding: 0.6rem 0;">${item.dataCadastroFormatada}</td>
            </tr>
            <c:if test="${not empty item.observacoes}">
            <tr>
                <th style="text-align:left; padding: 0.6rem 1rem 0.6rem 0; color: #555; vertical-align: top;">Observações</th>
                <td style="padding: 0.6rem 0;"><c:out value="${item.observacoes}"/></td>
            </tr>
            </c:if>
        </table>

        <c:if test="${not empty item.fotoPath}">
        <div style="margin-top: 1.5rem;">
            <p style="color: #555; margin-bottom: 0.5rem;">Foto:</p>
            <img src="${pageContext.request.contextPath}/uploads/${item.fotoPath}"
                 alt="Foto do item" style="max-width: 100%; max-height: 300px; border-radius: 6px; border: 1px solid #ddd;">
        </div>
        </c:if>

        <div style="margin-top: 2rem; display: flex; gap: 1rem; flex-wrap: wrap;">

            <c:if test="${item.status == 'ACHADO'}">
            <form action="${pageContext.request.contextPath}/itens/devolver" method="post"
                  onsubmit="return confirm('Confirmar devolução do item #${item.id}?');">
                <input type="hidden" name="id" value="${item.id}">
                <button type="submit" class="btn btn-accent">Marcar como Devolvido</button>
            </form>
            </c:if>

            <form action="${pageContext.request.contextPath}/itens/excluir" method="post"
                  onsubmit="return confirm('Tem certeza que deseja excluir o item #${item.id}? Esta ação não pode ser desfeita.');">
                <input type="hidden" name="id" value="${item.id}">
                <button type="submit" class="btn btn-danger">Excluir Item</button>
            </form>

        </div>
    </div>

</main>

<footer style="text-align: center; padding: 1.5rem; color: #888; font-size: 0.85rem; margin-top: 2rem;">
    Achados &amp; Perdidos — SESI Blumenau | Hackathon v0.1
</footer>

</body>
</html>
