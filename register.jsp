<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Criar conta — Achados e Perdidos SESI</title>
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

				<h2 class="auth-title">Criar conta</h2>

				<c:if test="${not empty erro}">
					<div class="alert alert-danger">${erro}</div>
				</c:if>

				<form method="post" action="${pageContext.request.contextPath}/register" novalidate id="regForm">
					<input type="hidden" name="csrf" value="${csrf}" />

					<div class="form-group">
						<label for="nome">Nome completo</label>
						<input type="text" id="nome" name="nome" class="form-control" value="${nomeVal}" required
							autocomplete="name" minlength="2" />
					</div>

					<div class="form-group">
						<label for="email">E-mail Gmail</label>
						<input type="email" id="email" name="email" class="form-control" value="${emailVal}" required
							autocomplete="email" />
						<small class="form-hint">Um código de verificação será enviado para este e-mail.</small>
					</div>

					<div class="form-group">
						<label for="senha">Senha</label>
						<div class="input-eye">
							<input type="password" id="senha" name="senha" class="form-control" required minlength="8"
								autocomplete="new-password" />
							<button type="button" class="eye-btn" onclick="toggleSenha('senha')">👁</button>
						</div>
						<small class="form-hint">Mínimo 8 caracteres.</small>
					</div>

					<div class="form-group">
						<label for="senha_confirm">Confirmar senha</label>
						<div class="input-eye">
							<input type="password" id="senha_confirm" name="senha_confirm" class="form-control" required
								autocomplete="new-password" />
							<button type="button" class="eye-btn" onclick="toggleSenha('senha_confirm')">👁</button>
						</div>
					</div>

					<div id="senhaErro" class="alert alert-danger" style="display:none;">As senhas não coincidem.</div>

					<button type="submit" class="btn btn-primary btn-block">Criar conta</button>
				</form>

				<p class="auth-link">Já tem conta?
					<a href="${pageContext.request.contextPath}/login">Entrar</a>
				</p>
			</div>

			<script>
				function toggleSenha(id) {
					const inp = document.getElementById(id);
					inp.type = inp.type === 'password' ? 'text' : 'password';
				}
				document.getElementById('regForm').addEventListener('submit', function (e) {
					const s1 = document.getElementById('senha').value;
					const s2 = document.getElementById('senha_confirm').value;
					if (s1 !== s2) {
						e.preventDefault();
						document.getElementById('senhaErro').style.display = 'block';
					}
				});
			</script>
		</body>

		</html>