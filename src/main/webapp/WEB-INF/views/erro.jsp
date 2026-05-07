<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erro — Achados &amp; Perdidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="header">
    <div class="header-inner">
        <div class="header-logo">
            <span class="icon">🔍</span>
            <div><h1>Achados &amp; Perdidos</h1><p>SESI Blumenau</p></div>
        </div>
    </div>
</header>
<main class="container" style="text-align:center;padding-top:60px;">
    <div class="empty-state">
        <div class="empty-icon">⚠️</div>
        <h2 style="margin-bottom:12px;">Algo deu errado</h2>
        <p>Ocorreu um erro inesperado. Por favor, tente novamente.</p>
        <br>
        <a href="${pageContext.request.contextPath}/itens" class="btn btn-primary">
            Voltar à listagem
        </a>
    </div>
</main>
</body>
</html>
