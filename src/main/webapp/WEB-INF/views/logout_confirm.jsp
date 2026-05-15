<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Sair — Achados e Perdidos SESI</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
		</head>

		<body class="auth-page">
			<div class="auth-card" style="text-align:center;">
				<span class="auth-icon">🚪</span>
				<h2>Deseja sair?</h2>
				<p>Sua sessão será encerrada com segurança.</p>
				<form method="post" action="${pageContext.request.contextPath}/logout">
					<input type="hidden" name="csrf" value="${csrf}" />
					<button type="submit" class="btn btn-danger" style="margin-right:8px;">Sair</button>
					<a href="${pageContext.request.contextPath}/itens" class="btn btn-secondary">Cancelar</a>
				</form>
			</div>
		</body>

		</html>