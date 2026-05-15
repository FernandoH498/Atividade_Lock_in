# TESTS.md — Achados & Perdidos

Casos de teste manuais para o marco v0.1. Executar após build e deploy no Tomcat.

**Ambiente:** `http://localhost:8080/achados-perdidos/`

---

## F01 — Cadastrar Item

### T01 — Cadastro válido
| Campo | Valor |
|-------|-------|
| Descrição | Mochila preta com alça vermelha |
| Categoria | OUTROS |
| Local | Corredor B - 2º andar |
| Data encontrado | (data de hoje) |
| Observações | (vazio) |

**Passos:** Preencher formulário em `/itens/novo` → clicar em Cadastrar.  
**Resultado esperado:** Redirecionamento para `/itens` com mensagem "Item cadastrado com sucesso!". Item aparece no topo da lista.

---

### T02 — Cadastro sem descrição (campo obrigatório)
**Passos:** Acessar `/itens/novo` → deixar descrição em branco → submeter.  
**Resultado esperado:** Formulário reexibido com mensagem de erro "A descrição do item é obrigatória." Nenhum item salvo no banco.

---

### T03 — Cadastro com data futura
**Passos:** Acessar `/itens/novo` → informar data futura (ex.: amanhã) → submeter.  
**Resultado esperado:** Formulário reexibido com mensagem "A data não pode ser no futuro." Nenhum item salvo.

---

### T04 — Cadastro com categoria inválida (via manipulação de requisição)
**Passos:** Enviar POST para `/itens/novo` com `categoria=INVALIDA` (via ferramenta como curl ou Postman).  
**Resultado esperado:** Formulário reexibido com mensagem "Categoria inválida." Nenhum item salvo.

---

## F02 — Listar Itens

### T05 — Listagem sem filtros
**Passos:** Acessar `/itens`.  
**Resultado esperado:** HTTP 200. Todos os itens exibidos ordenados por `data_cadastro DESC`. Contadores de totais corretos.

---

### T06 — Filtro por status ACHADO
**Passos:** Acessar `/itens?status=ACHADO`.  
**Resultado esperado:** Apenas itens com status "ACHADO" são exibidos.

---

### T07 — Filtro por status DEVOLVIDO
**Passos:** Acessar `/itens?status=DEVOLVIDO`.  
**Resultado esperado:** Apenas itens com status "DEVOLVIDO" são exibidos. (Se não houver, exibe mensagem amigável.)

---

### T08 — Busca por descrição (parcial, sem distinção de maiúsculas)
**Passos:** Acessar `/itens?busca=mochila`.  
**Resultado esperado:** Apenas itens cuja descrição contém "mochila" (case-insensitive) são exibidos.

---

## F03 — Detalhar Item

### T09 — Detalhe de item existente
**Passos:** Clicar em "Detalhes" de um item na listagem → acessar `/itens/detalhe?id=1`.  
**Resultado esperado:** HTTP 200. Página exibe todos os campos: descrição, categoria, local, data encontrado, status, observações, data de cadastro.

---

### T10 — Detalhe com ID inexistente
**Passos:** Acessar `/itens/detalhe?id=99999`.  
**Resultado esperado:** HTTP 404 ou redirecionamento para `/itens` com mensagem de erro amigável. Stack trace não exibido.

---

## F04 — Marcar como Devolvido

### T11 — Marcar item como devolvido
**Passos:** Na página de detalhe de um item com status "ACHADO" → clicar em "Marcar como Devolvido" → confirmar.  
**Resultado esperado:** Redirecionamento para `/itens` com mensagem "Item marcado como devolvido!". Status do item atualizado para "DEVOLVIDO" na listagem.

---

## F05 — Excluir Item

### T12 — Excluir item
**Passos:** Na página de detalhe de um item → clicar em "Excluir" → confirmar.  
**Resultado esperado:** Redirecionamento para `/itens` com mensagem "Item excluído com sucesso!". Item não aparece mais na listagem.

---

## Resultado Consolidado v0.1

| Teste | Funcionalidade | Status |
|-------|---------------|--------|
| T01 | F01 — Cadastro válido | ⬜ Pendente |
| T02 | F01 — Descrição obrigatória | ⬜ Pendente |
| T03 | F01 — Data futura | ⬜ Pendente |
| T04 | F01 — Categoria inválida | ⬜ Pendente |
| T05 | F02 — Listagem sem filtros | ⬜ Pendente |
| T06 | F02 — Filtro ACHADO | ⬜ Pendente |
| T07 | F02 — Filtro DEVOLVIDO | ⬜ Pendente |
| T08 | F02 — Busca por descrição | ⬜ Pendente |
| T09 | F03 — Detalhe existente | ⬜ Pendente |
| T10 | F03 — ID inexistente | ⬜ Pendente |
| T11 | F04 — Marcar devolvido | ⬜ Pendente |
| T12 | F05 — Excluir item | ⬜ Pendente |

> Preencher ✅ (passou) ou ❌ (falhou) após executar cada teste manualmente.
