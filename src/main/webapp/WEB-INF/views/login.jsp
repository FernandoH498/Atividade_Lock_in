<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Login — Achados e Perdidos SESI</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
		</head>

		<body class="auth-page">

			<div class="auth-card">
				<div class="auth-logo">
					<span class="auth-icon">🔍</span>
					<h1>Achados e Perdidos</h1>
					<p>SESI Blumenau</p>
				</div>

				<h2 class="auth-title">Entrar na conta</h2>

				<c:if test="${param.logout eq '1'}">
					<div class="alert alert-success">Você saiu com segurança.</div>
				</c:if>
				<c:if test="${param.verificado eq '1'}">
					<div class="alert alert-success">E-mail verificado! Faça login.</div>
				</c:if>
				<c:if test="${not empty erro}">
					<div class="alert alert-danger">${erro}</div>
				</c:if>

				<form method="post" action="${pageContext.request.contextPath}/login" novalidate>
					<input type="hidden" name="csrf" value="${csrf}" />

					<div class="form-group">
						<label for="login">E-mail ou usuário</label>
						<input type="text" id="login" name="login" class="form-control" value="${loginVal}" required
							autofocus autocomplete="username" />
					</div>

					<div class="form-group">
						<label for="senha">Senha</label>
						<div class="input-eye">
							<input type="password" id="senha" name="senha" class="form-control" required
								autocomplete="current-password" />
							<button type="button" class="eye-btn" onclick="toggleSenha('senha')">👁</button>
						</div>
					</div>

					<button type="submit" class="btn btn-primary btn-block">Entrar</button>
				</form>

				<p class="auth-link">Não tem conta?
					<a href="${pageContext.request.contextPath}/register">Criar conta</a>
				</p>
			</div>

			<script>
				function toggleSenha(id) {
					const inp = document.getElementById(id);
					inp.type = inp.type === 'password' ? 'text' : 'password';
				}
			</script>
		</body>

		</html>