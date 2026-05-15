<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Usuários — Admin</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
		</head>

		<body>

			<nav class="admin-nav">
				<div class="admin-nav-brand">⚙️ Painel Admin</div>
				<div class="admin-nav-links">
					<a href="${pageContext.request.contextPath}/admin">Dashboard</a>
					<a href="${pageContext.request.contextPath}/admin/users" class="active">Usuários</a>
					<a href="${pageContext.request.contextPath}/admin/items">Itens</a>
					<a href="${pageContext.request.contextPath}/logout">Sair</a>
				</div>
			</nav>

			<main class="admin-main">
				<h1 class="admin-page-title">Gerenciar Usuários</h1>

				<c:if test="${param.ok eq '1'}">
					<div class="alert alert-success">Operação realizada com sucesso.</div>
				</c:if>
				<c:if test="${not empty param.erro}">
					<div class="alert alert-danger">Erro: ${param.erro}</div>
				</c:if>
				<c:if test="${not empty erro}">
					<div class="alert alert-danger">${erro}</div>
				</c:if>

				<div class="admin-table-wrap">
					<table class="admin-table">
						<thead>
							<tr>
								<th>#</th>
								<th>Nome</th>
								<th>Login</th>
								<th>Papel</th>
								<th>Status</th>
								<th>Tentativas</th>
								<th>Criado em</th>
								<th>Último login</th>
								<th>Ações</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="u" items="${usuarios}">
								<tr class="${u.bloqueado ? 'row-blocked' : ''}${u.pendente ? 'row-pending' : ''}">
									<td>${u.id}</td>
									<td>${u.nome}</td>
									<td>${u.login}</td>
									<td>
										<c:choose>
											<c:when test="${u.admin}"><span class="badge badge-admin">ADMIN</span>
											</c:when>
											<c:otherwise><span class="badge badge-user">USUÁRIO</span></c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${u.status eq 'ATIVO'}"><span
													class="badge badge-ativo">ATIVO</span></c:when>
											<c:when test="${u.status eq 'BLOQUEADO'}"><span
													class="badge badge-bloqueado">BLOQUEADO</span></c:when>
											<c:otherwise><span class="badge badge-pendente">PENDENTE</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>${u.tentativasFalhas}</td>
									<td>${u.criadoEmFormatado}</td>
									<td>${u.ultimoLoginFormatado}</td>
									<td class="action-cell">
										<c:if test="${!u.admin}">
											<c:if test="${u.bloqueado}">
												<form method="post"
													action="${pageContext.request.contextPath}/admin/users"
													style="display:inline">
													<input type="hidden" name="csrf" value="${csrf}" />
													<input type="hidden" name="userId" value="${u.id}" />
													<input type="hidden" name="action" value="unlock" />
													<button type="submit"
														class="btn-sm btn-success">Desbloquear</button>
												</form>
											</c:if>
											<c:if test="${!u.bloqueado}">
												<form method="post"
													action="${pageContext.request.contextPath}/admin/users"
													style="display:inline"
													onsubmit="return confirm('Bloquear esta conta?')">
													<input type="hidden" name="csrf" value="${csrf}" />
													<input type="hidden" name="userId" value="${u.id}" />
													<input type="hidden" name="action" value="block" />
													<button type="submit" class="btn-sm btn-warning">Bloquear</button>
												</form>
											</c:if>
											<button class="btn-sm btn-info"
												onclick="openEdit(${u.id}, '${u.nome}')">Editar</button>
											<form method="post" action="${pageContext.request.contextPath}/admin/users"
												style="display:inline"
												onsubmit="return confirm('Excluir esta conta permanentemente?')">
												<input type="hidden" name="csrf" value="${csrf}" />
												<input type="hidden" name="userId" value="${u.id}" />
												<input type="hidden" name="action" value="delete" />
												<button type="submit" class="btn-sm btn-danger">Excluir</button>
											</form>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</main>

			<!-- Modal edição de usuário -->
			<div id="editModal" class="modal" style="display:none;">
				<div class="modal-content">
					<h3>Editar usuário</h3>
					<form method="post" action="${pageContext.request.contextPath}/admin/users">
						<input type="hidden" name="csrf" value="${csrf}" />
						<input type="hidden" name="action" value="edit" />
						<input type="hidden" id="editUserId" name="userId" value="" />
						<div class="form-group">
							<label>Nome</label>
							<input type="text" id="editNome" name="nome" class="form-control" minlength="2" />
						</div>
						<div class="form-group">
							<label>Nova senha <small>(deixe em branco para não alterar)</small></label>
							<input type="password" name="nova_senha" class="form-control" minlength="8"
								autocomplete="new-password" />
						</div>
						<div style="margin-top:16px;">
							<button type="submit" class="btn btn-primary">Salvar</button>
							<button type="button" class="btn btn-secondary" onclick="closeEdit()">Cancelar</button>
						</div>
					</form>
				</div>
			</div>

			<script>
				function openEdit(id, nome) {
					document.getElementById('editUserId').value = id;
					document.getElementById('editNome').value = nome;
					document.getElementById('editModal').style.display = 'flex';
				}
				function closeEdit() {
					document.getElementById('editModal').style.display = 'none';
				}
			</script>
		</body>

		</html>