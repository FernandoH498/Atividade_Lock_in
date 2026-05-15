# PR Checklist — v0.2

> Preencha antes de abrir o Pull Request. Marque cada item após verificar.

---

## Banco de Dados

- [ ] `schema_v02.sql` executado com sucesso no MySQL 8+ / MariaDB 10+ (`ALTER TABLE itens ADD COLUMN foto_path ...`)
- [ ] Coluna `foto_path VARCHAR(255) NULL` presente na tabela `itens`
- [ ] `seed.sql` continua funcionando normalmente (registros sem foto mantêm `foto_path = NULL`)

---

## Back-end

- [ ] `ItemCreateServlet` — `@MultipartConfig` configurado (5 MB por arquivo, 10 MB por requisição)
- [ ] Upload de foto salva o arquivo em `/uploads/` com nome gerado por `UUID` (nunca o nome original)
- [ ] `ItemService.isExtensaoImagemValida()` rejeita extensões fora de: `.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`
- [ ] `ItemUpdateServlet` responde em `POST /itens/devolver` e redireciona para `/itens` após sucesso
- [ ] `ItemService.devolverItem()` chama `ItemDAO.updateStatus()` corretamente
- [ ] `ItemDAO.updateStatus()` usa `PreparedStatement` (sem concatenação de SQL)
- [ ] `ItemDAO.findAll()` aceita o parâmetro `categoria` e filtra corretamente
- [ ] Todos os `PreparedStatement` do DAO continuam sem concatenação de string SQL

---

## Front-end

- [ ] Preview da foto aparece antes do envio do formulário
- [ ] Arquivo > 5 MB exibe toast de erro no cliente e não envia
- [ ] Arquivo de tipo inválido (não-imagem) exibe toast de erro e não envia
- [ ] Cards com foto exibem a imagem; cards sem foto exibem o placeholder com ícone
- [ ] Badge "Achado" / "Devolvido" sobreposto à foto do card com cor correta (azul / verde)
- [ ] Botão "Marcar como Devolvido" aparece apenas em cards com `status = 'ACHADO'`
- [ ] Após clicar "Marcar como Devolvido" a listagem é atualizada e o item passa a exibir badge verde
- [ ] Contadores do hero (`totalItens`, `totalAchados`, `totalDevolvidos`) refletem o estado real do banco
- [ ] Filtro de categoria na barra de filtros funciona isolado e combinado com status/busca
- [ ] Alertas de sucesso/erro somem automaticamente após 5 segundos
- [ ] Toast de confirmação aparece ao aceitar sugestão de categoria da IA

---

## Segurança

- [ ] Extensão de arquivo validada **no servidor** (`ItemService`) além do cliente
- [ ] Nome do arquivo em disco é sempre UUID gerado internamente (sem path traversal)
- [ ] Nenhum stack trace exposto ao usuário final (somente mensagens amigáveis)
- [ ] Zero concatenação de SQL — 100% `PreparedStatement`

---

## Documentação

- [ ] `CHANGELOG.md` atualizado com todas as mudanças da v0.2
- [ ] `PROMPTS_IA.md` com seção Sprint 2 documentando os prompts usados nesta versão
- [ ] `DoD.md` revisado — novos critérios de upload de foto e devolução adicionados se necessário
- [ ] `README.md` menciona `schema_v02.sql` e o diretório `/uploads/` como pré-requisito de deploy

---

## Build & Deploy

- [ ] `mvn clean package` conclui sem erros nem warnings
- [ ] WAR deployado no Tomcat 9 sem erros no `catalina.out`
- [ ] Diretório `/uploads/` criado com permissões de escrita no servidor (ou criado automaticamente pelo servlet)
- [ ] `config.properties` ausente do repositório (apenas `config.properties.example` commitado)

---

## Testes Manuais (Golden Path)

| Cenário | Resultado Esperado | ✓ |
|---|---|---|
| Cadastrar item **com** foto válida (JPG ≤ 5 MB) | Item salvo, foto exibida no card | ☐ |
| Cadastrar item **sem** foto | Item salvo, placeholder exibido no card | ☐ |
| Tentar enviar foto > 5 MB | Toast de erro no cliente, formulário não enviado | ☐ |
| Tentar enviar arquivo `.pdf` como foto | Toast de erro no cliente | ☐ |
| Clicar "Marcar como Devolvido" em item ACHADO | Badge muda para verde, botão some | ☐ |
| Filtrar por categoria "ELETRÔNICOS" | Apenas itens da categoria exibidos | ☐ |
| Combinar filtro status + categoria + busca | Resultado correto para todos os filtros | ☐ |
| Acessar `/itens` sem registros no banco | Empty state com link para cadastro | ☐ |
