<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<!DOCTYPE html>
		<html lang="pt-BR">

		<head>
			<meta charset="UTF-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1.0" />
			<title>Itens — Admin</title>
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css" />
			<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" />
		</head>

		<body>

			<nav class="admin-nav">
				<div class="admin-nav-brand">⚙️ Painel Admin</div>
				<div class="admin-nav-links">
					<a href="${pageContext.request.contextPath}/admin">Dashboard</a>
					<a href="${pageContext.request.contextPath}/admin/users">Usuários</a>
					<a href="${pageContext.request.contextPath}/admin/items" class="active">Itens</a>
					<a href="${pageContext.request.contextPath}/logout">Sair</a>
				</div>
			</nav>

			<main class="admin-main">
				<h1 class="admin-page-title">Gerenciar Itens</h1>

				<c:if test="${param.ok eq '1'}">
					<div class="alert alert-success">Operação realizada com sucesso.</div>
				</c:if>
				<c:if test="${not empty param.erro}">
					<div class="alert alert-danger">Erro: ${param.erro}</div>
				</c:if>

				<form method="get" action="${pageContext.request.contextPath}/admin/items" class="admin-filter-bar">
					<input type="text" name="busca" value="${buscaVal}" placeholder="Buscar por descrição…"
						class="form-control" />
					<select name="status" class="form-control">
						<option value="">Todos os status</option>
						<option value="ACHADO" ${statusVal eq 'ACHADO' ? 'selected' : '' }>Achado</option>
						<option value="DEVOLVIDO" ${statusVal eq 'DEVOLVIDO' ? 'selected' : '' }>Devolvido</option>
					</select>
					<select name="categoria" class="form-control">
						<option value="">Todas as categorias</option>
						<option value="ELETRÔNICOS" ${catVal eq 'ELETRÔNICOS' ? 'selected' : '' }>Eletrônicos</option>
						<option value="DOCUMENTOS" ${catVal eq 'DOCUMENTOS' ? 'selected' : '' }>Documentos</option>
						<option value="ROUPAS" ${catVal eq 'ROUPAS' ? 'selected' : '' }>Roupas</option>
						<option value="ACESSÓRIOS" ${catVal eq 'ACESSÓRIOS' ? 'selected' : '' }>Acessórios</option>
						<option value="OUTROS" ${catVal eq 'OUTROS' ? 'selected' : '' }>Outros</option>
					</select>
					<button type="submit" class="btn btn-primary">Filtrar</button>
				</form>

				<div class="admin-table-wrap">
					<table class="admin-table">
						<thead>
							<tr>
								<th>#</th>
								<th>Descrição</th>
								<th>Categoria</th>
								<th>Local</th>
								<th>Data encontrado</th>
								<th>Status</th>
								<th>Cadastrado em</th>
								<th>Ações</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${itens}">
								<tr>
									<td>${item.id}</td>
									<td>${item.descricao}</td>
									<td>${item.categoria}</td>
									<td>${item.localEncontrado}</td>
									<td>${item.dataEncontradoFormatada}</td>
									<td>
										<c:choose>
											<c:when test="${item.status eq 'ACHADO'}">
												<span class="badge badge-ativo">ACHADO</span>
											</c:when>
											<c:otherwise>
												<span class="badge badge-pendente">DEVOLVIDO</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>${item.dataCadastroFormatada}</td>
									<td class="action-cell">
										<button class="btn-sm btn-info" onclick="openItemEdit(${item.id},'${item.descricao}','${item.categoria}',
                               '${item.localEncontrado}','${item.dataEncontradoIso}',
                               '${item.status}','${item.observacoes}')">Editar</button>
										<c:if test="${item.status eq 'ACHADO'}">
											<form method="post" action="${pageContext.request.contextPath}/admin/items"
												style="display:inline">
												<input type="hidden" name="csrf" value="${csrf}" />
												<input type="hidden" name="itemId" value="${item.id}" />
												<input type="hidden" name="action" value="devolver" />
												<button type="submit" class="btn-sm btn-success">Devolver</button>
											</form>
										</c:if>
										<form method="post" action="${pageContext.request.contextPath}/admin/items"
											style="display:inline"
											onsubmit="return confirm('Excluir este item permanentemente?')">
											<input type="hidden" name="csrf" value="${csrf}" />
											<input type="hidden" name="itemId" value="${item.id}" />
											<input type="hidden" name="action" value="delete" />
											<button type="submit" class="btn-sm btn-danger">Excluir</button>
										</form>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</main>

			<!-- Modal edição de item -->
			<div id="itemEditModal" class="modal" style="display:none;">
				<div class="modal-content modal-wide">
					<h3>Editar item</h3>
					<form method="post" action="${pageContext.request.contextPath}/admin/items">
						<input type="hidden" name="csrf" value="${csrf}" />
						<input type="hidden" name="action" value="edit" />
						<input type="hidden" id="itemEditId" name="itemId" value="" />

						<div class="form-group">
							<label>Descrição</label>
							<input type="text" id="itemEditDesc" name="descricao" class="form-control"
								maxlength="255" />
						</div>
						<div class="form-row">
							<div class="form-group">
								<label>Categoria</label>
								<select id="itemEditCat" name="categoria" class="form-control">
									<option value="ELETRÔNICOS">Eletrônicos</option>
									<option value="DOCUMENTOS">Documentos</option>
									<option value="ROUPAS">Roupas</option>
									<option value="ACESSÓRIOS">Acessórios</option>
									<option value="OUTROS">Outros</option>
								</select>
							</div>
							<div class="form-group">
								<label>Status</label>
								<select id="itemEditStatus" name="status" class="form-control">
									<option value="ACHADO">Achado</option>
									<option value="DEVOLVIDO">Devolvido</option>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label>Local encontrado</label>
							<input type="text" id="itemEditLocal" name="local_encontrado" class="form-control" />
						</div>
						<div class="form-group">
							<label>Data encontrado</label>
							<input type="date" id="itemEditData" name="data_encontrado" class="form-control" />
						</div>
						<div class="form-group">
							<label>Observações</label>
							<textarea id="itemEditObs" name="observacoes" class="form-control" rows="3"></textarea>
						</div>
						<div style="margin-top:16px;">
							<button type="submit" class="btn btn-primary">Salvar</button>
							<button type="button" class="btn btn-secondary" onclick="closeItemEdit()">Cancelar</button>
						</div>
					</form>
				</div>
			</div>

			<script>
				function openItemEdit(id, desc, cat, local, data, status, obs) {
					document.getElementById('itemEditId').value = id;
					document.getElementById('itemEditDesc').value = desc;
					document.getElementById('itemEditLocal').value = local;
					document.getElementById('itemEditData').value = data;
					document.getElementById('itemEditObs').value = obs || '';
					const catSel = document.getElementById('itemEditCat');
					const statSel = document.getElementById('itemEditStatus');
					for (let o of catSel.options) o.selected = o.value === cat;
					for (let o of statSel.options) o.selected = o.value === status;
					document.getElementById('itemEditModal').style.display = 'flex';
				}
				function closeItemEdit() {
					document.getElementById('itemEditModal').style.display = 'none';
				}
			</script>
		</body>

		</html>