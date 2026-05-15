<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Verificar e-mail — Achados e Perdidos SESI</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
		</head>

		<body class="auth-page">

			<div class="auth-card">
				<div class="auth-logo">
					<span class="auth-icon">✉️</span>
					<h1>Verificar e-mail</h1>
				</div>

				<c:if test="${novo eq '1'}">
					<div class="alert alert-success">
						Conta criada! Enviamos um código de 6 dígitos para <strong>${email}</strong>.
						Verifique sua caixa de entrada (e spam).
					</div>
				</c:if>
				<c:if test="${not empty sucesso}">
					<div class="alert alert-success">${sucesso}</div>
				</c:if>
				<c:if test="${not empty erro}">
					<div class="alert alert-danger">${erro}</div>
				</c:if>

				<p>Digite o código enviado para <strong>${email}</strong>:</p>

				<form method="post" action="${pageContext.request.contextPath}/verify">
					<input type="hidden" name="csrf" value="${csrf}" />
					<input type="hidden" name="email" value="${email}" />
					<input type="hidden" name="action" value="verificar" />

					<div class="form-group">
						<label for="codigo">Código de verificação</label>
						<input type="text" id="codigo" name="codigo" class="form-control codigo-input" maxlength="6"
							pattern="\d{6}" placeholder="000000" required autofocus autocomplete="one-time-code" />
					</div>

					<button type="submit" class="btn btn-primary btn-block">Confirmar</button>
				</form>

				<form method="post" action="${pageContext.request.contextPath}/verify" style="margin-top:12px;">
					<input type="hidden" name="csrf" value="${csrf}" />
					<input type="hidden" name="email" value="${email}" />
					<input type="hidden" name="action" value="reenviar" />
					<button type="submit" class="btn btn-secondary btn-block">Reenviar código</button>
				</form>

				<p class="auth-link">
					<a href="${pageContext.request.contextPath}/login">Voltar ao login</a>
				</p>
			</div>
		</body>

		</html>