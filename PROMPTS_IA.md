# Registro de Uso de Inteligência Artificial

> Conforme exigido pelo Manual do Hackathon, este arquivo documenta **todos** os usos de IA no projeto de forma transparente.

---

## Ferramenta Utilizada

**Claude (Anthropic)** — acessado via Claude Code CLI  
Modelo: Claude Sonnet 4.6

---

## Sprint 1 — Marco v0.1 (Semana 1)

### Uso 1 — Geração da Estrutura Completa do Projeto

**Data:** 07/05/2026  
**Prompt enviado:**

> Atue como um Desenvolvedor Java Sênior. Inicie o desenvolvimento da Semana 1 do projeto "Achados & Perdidos (Simulado)" para um Hackathon técnico de 3 semanas. O foco é criar um MVP robusto, ético e rastreável, seguindo rigorosamente o manual_hackathon_achados_perdidos_sem_capa.pdf e o criterios_avaliacao.xlsx. [...]

**Artefatos gerados pela IA:**

| Arquivo | Descrição |
|---------|-----------|
| `schema.sql` | Schema do banco de dados |
| `seed.sql` | 5 registros fictícios de teste |
| `Item.java` | Model da entidade |
| `ConnectionFactory.java` | Fábrica de conexão JDBC |
| `ItemDAO.java` | Acesso a dados com PreparedStatement |
| `ItemService.java` | Regras de negócio e validações |
| `ItemListServlet.java` | Servlet de listagem (F02) |
| `ItemCreateServlet.java` | Servlet de cadastro (F01) |
| `web.xml` | Configuração do servlet container |
| `list.jsp` | Interface de listagem |
| `form.jsp` | Formulário de cadastro |
| `style.css` | Estilização completa |
| `main.js` | Interatividade e validação client-side |
| `DoD.md` | Definition of Done |
| `README.md` | Instruções de setup |
| `PROMPTS_IA.md` | Este arquivo |

**Revisão humana aplicada:**
- Verificação dos nomes de tabela e colunas conforme o manual (pág. 8)
- Ajuste das credenciais no `config.properties` para o ambiente local
- Validação das regras de negócio no `ItemService.java`
- Teste dos filtros de listagem (F02)

---

### Uso 2 — Simulação de IA no Front-end (Sugestão de Categoria)

**Funcionalidade:** O formulário de cadastro exibe um painel "Assistente de Categorização" que sugere automaticamente a categoria do item conforme o usuário digita a descrição.

**Como funciona:** O algoritmo em `main.js` (função `suggestCategory`) compara as palavras da descrição com um dicionário de palavras-chave mapeadas por categoria. A sugestão aparece após 450ms de inatividade (debounce) e o usuário pode aceitar com um clique.

**Importante:** Esta funcionalidade é uma **simulação** — não utiliza uma API de IA real. O objetivo é demonstrar o conceito de assistência por IA dentro dos limites técnicos do hackathon.

**Mapeamento de palavras-chave:**

| Categoria | Palavras-chave exemplo |
|-----------|----------------------|
| ELETRÔNICOS | celular, fone, headphone, carregador, notebook, tablet, câmera |
| DOCUMENTOS | carteira, rg, cpf, documento, identidade, cartão, habilitação |
| ROUPAS | casaco, jaqueta, blusa, camiseta, tênis, sapato, uniforme |
| ACESSÓRIOS | mochila, bolsa, relógio, guarda-chuva, óculos, garrafa |
| OUTROS | (fallback para itens não classificados) |

---

## Considerações Éticas

- Nenhum dado pessoal foi fornecido à IA durante os prompts
- Os dados de seed gerados são completamente fictícios (conformidade ECA/LGPD)
- O código gerado foi revisado para garantir `PreparedStatement` em 100% das queries
- O uso de IA está documentado de forma transparente neste arquivo
