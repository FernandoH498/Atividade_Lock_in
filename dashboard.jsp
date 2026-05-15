<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Painel Admin — Achados e Perdidos SESI</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
		</head>

		<body>

			<nav class="admin-nav">
				<div class="admin-nav-brand">⚙️ Painel Admin</div>
				<div class="admin-nav-links">
					<a href="${pageContext.request.contextPath}/admin" class="active">Dashboard</a>
					<a href="${pageContext.request.contextPath}/admin/users">Usuários</a>
					<a href="${pageContext.request.contextPath}/admin/items">Itens</a>
					<a href="${pageContext.request.contextPath}/logout">Sair</a>
				</div>
			</nav>

			<main class="admin-main">
				<h1 class="admin-page-title">Dashboard</h1>

				<div class="admin-stats">
					<div class="stat-card">
						<div class="stat-number">${totalUsuarios}</div>
						<div class="stat-label">Usuários cadastrados</div>
					</div>
					<div class="stat-card stat-warning">
						<div class="stat-number">${bloqueados}</div>
						<div class="stat-label">Contas bloqueadas</div>
					</div>
					<div class="stat-card stat-info">
						<div class="stat-number">${pendentes}</div>
						<div class="stat-label">Aguardando verificação</div>
					</div>
					<div class="stat-card stat-success">
						<div class="stat-number">${totalItens}</div>
						<div class="stat-label">Itens no sistema</div>
					</div>
				</div>

				<div class="admin-quick-links">
					<a href="${pageContext.request.contextPath}/admin/users" class="admin-quick-btn">
						👥 Gerenciar usuários
					</a>
					<a href="${pageContext.request.contextPath}/admin/items" class="admin-quick-btn">
						📦 Gerenciar itens
					</a>
				</div>
			</main>

		</body>

		</html>