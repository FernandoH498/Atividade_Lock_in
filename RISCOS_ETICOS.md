# RISCOS_ETICOS — Achados & Perdidos

Identificação de riscos éticos e técnicos do projeto e as mitigações aplicadas.

---

## Risco 1 — SQL Injection

**Descrição:** Queries montadas por concatenação de strings permitem que um atacante injete código SQL malicioso, comprometendo ou destruindo o banco de dados.

**Probabilidade:** Alta (é o vetor de ataque mais comum em aplicações web).

**Mitigação aplicada:**
- 100% das queries em `ItemDAO.java` utilizam `PreparedStatement` com parâmetros vinculados (`?`).
- Nenhuma concatenação de SQL existe no código (verificado em `findAll`, `findById`, `save`, `updateStatus`, `delete`).

---

## Risco 2 — Exposição de dados pessoais (ECA / LGPD)

**Descrição:** O sistema de Achados & Perdidos pode registrar informações que, combinadas, identificam pessoas reais (ex.: "Carteira com CPF João Silva"). Isso viola o ECA (proteção de menores) e a LGPD.

**Probabilidade:** Média (depende do comportamento do usuário).

**Mitigação aplicada:**
- Os dados de `seed.sql` são completamente fictícios e genéricos (sem nomes, CPFs, telefones ou endereços reais).
- O sistema não coleta nem armazena dados de identificação pessoal — apenas descrição do objeto, categoria, local e data.
- Instruções no `README.md` orientam a não inserir dados pessoais reais.

---

## Risco 3 — Credenciais vazadas no repositório Git

**Descrição:** Commitar `config.properties` com usuário e senha do banco de dados expõe as credenciais publicamente no histórico Git, mesmo que o arquivo seja removido depois.

**Probabilidade:** Alta (erro comum de desenvolvedores iniciantes).

**Mitigação aplicada:**
- `config.properties` está listado no `.gitignore` e nunca foi commitado.
- O repositório contém apenas `config.properties.example` com valores fictícios (`SEU_USUARIO`, `SUA_SENHA`).
- O `README.md` instrui explicitamente o desenvolvedor a copiar o arquivo de exemplo e preencher localmente.
