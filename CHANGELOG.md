# Changelog — Achados & Perdidos

Todas as mudanças notáveis deste projeto são documentadas aqui.  
Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [0.2] — 2026-05-07

### Adicionado

#### Back-end
- **`ItemCreateServlet`** — anotação `@MultipartConfig` (máx. 5 MB por arquivo, 10 MB por requisição) para suporte a `multipart/form-data`
- **`ItemCreateServlet`** — lógica de upload de foto: valida extensão via `ItemService.isExtensaoImagemValida()`, gera nome único com `UUID.randomUUID()`, salva em `/uploads/`
- **`ItemUpdateServlet`** *(novo)* — servlet `POST /itens/devolver` que recebe `id` e delega a `ItemService.devolverItem()`
- **`ItemService.devolverItem(int id)`** *(novo)* — encapsula a transição de status `ACHADO → DEVOLVIDO`
- **`ItemService.isExtensaoImagemValida(String nomeArquivo)`** *(novo)* — valida extensões permitidas: `.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`
- **`ItemDAO.updateStatus(int id, String status)`** *(novo)* — executa `UPDATE itens SET status = ? WHERE id = ?` via `PreparedStatement`
- **`ItemDAO.findAll()`** — adicionado terceiro parâmetro `categoria` para filtragem por categoria

#### Banco de Dados
- **`schema_v02.sql`** *(novo)* — migração que adiciona `ALTER TABLE itens ADD COLUMN foto_path VARCHAR(255) NULL AFTER observacoes`

#### Front-end
- **`list.jsp`** — cards exibem foto do item (`<img>`) ou placeholder com ícone quando `fotoPath` é nulo
- **`list.jsp`** — badge overlay (`ACHADO` / `DEVOLVIDO`) sobreposto à foto do card
- **`list.jsp`** — botão "Marcar como Devolvido" exibido apenas para itens com `status = 'ACHADO'`
- **`list.jsp`** — hero section com contadores: `totalItens`, `totalAchados`, `totalDevolvidos`
- **`list.jsp`** — filtro de categoria adicionado à barra de filtros
- **`form.jsp`** — área de upload com preview da imagem antes do envio
- **`main.js`** — `setupPhotoPreview()`: valida tipo e tamanho (≤ 5 MB) no cliente, exibe preview via `FileReader`
- **`main.js`** — `showToast(message, isError)`: notificação flutuante (bottom-right) com auto-remoção em 3s
- **`main.js`** — `setupAlertAutoDismiss()`: alertas de sucesso/erro desaparecem automaticamente após 5s

### Alterado
- **`ItemDAO.SELECT_COLS`** — inclui `foto_path` na projeção do `SELECT`
- **`ItemDAO.mapRow()`** — mapeia `foto_path` para `item.setFotoPath()`
- **`ItemDAO.save()`** — inclui `foto_path` no `INSERT`
- **`main.js`** — versão atualizada de v0.1 para v0.2 no cabeçalho do arquivo

### Segurança
- Upload de foto valida extensão no servidor (`ItemService`) **e** no cliente (`main.js`) — dupla camada
- Nome do arquivo salvo em disco é sempre um UUID gerado pelo servidor, nunca o nome original enviado pelo usuário

---

## [0.1] — 2026-05-07

### Adicionado
- Estrutura completa do projeto Maven com Servlets 4.0 / JSP 2.3 / JDBC
- `schema.sql` — criação do banco `achados_perdidos` e tabela `itens`
- `seed.sql` — 5 registros fictícios sem dados pessoais reais (conformidade ECA/LGPD)
- `Item.java` — model com getters/setters e formatadores de data
- `ConnectionFactory.java` — singleton de conexão JDBC com `config.properties`
- `ItemDAO.java` — `findAll()` (com filtros status e busca), `findById()`, `save()`
- `ItemService.java` — validações de negócio (`validar()`, `cadastrarItem()`, `listarItens()`, `buscarItemPorId()`)
- `ItemListServlet.java` — `GET /itens` com filtros de status e busca por descrição
- `ItemCreateServlet.java` — `GET /itens/novo` (formulário) e `POST /itens/novo` (persistência)
- `list.jsp` — listagem em grid de cards com filtros
- `form.jsp` — formulário de cadastro com painel de sugestão de categoria por IA simulada
- `erro.jsp` — página de erro amigável
- `style.css` — estilização completa (hero, cards, filtros, badges, formulário)
- `main.js` — validação client-side, sugestão de categoria (IA simulada via palavras-chave), contador de caracteres
- `DoD.md`, `README.md`, `PROMPTS_IA.md` — documentação inicial
