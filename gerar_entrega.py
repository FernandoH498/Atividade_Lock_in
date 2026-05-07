"""
Gerador de entregáveis — Achados & Perdidos (Equipe Lock in)
Gera: Diagrama de Classes, MER, CRUD, Planilha, Imagens das Telas
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import matplotlib.patheffects as pe
import numpy as np
import os, zipfile, csv

OUT = "/home/user/Atividade_Lock_in/entrega"
os.makedirs(OUT, exist_ok=True)

BLUE_DARK  = "#1a3a5c"
BLUE_MID   = "#2563EB"
BLUE_LIGHT = "#DBEAFE"
GREEN      = "#16a34a"
GREEN_LIGHT= "#DCFCE7"
ORANGE     = "#ea580c"
GRAY_DARK  = "#374151"
GRAY_MID   = "#6B7280"
GRAY_LIGHT = "#F3F4F6"
WHITE      = "#FFFFFF"
RED        = "#dc2626"

# ─── helpers ──────────────────────────────────────────────────────────────────

def save(fig, name):
    path = os.path.join(OUT, name)
    fig.savefig(path, dpi=150, bbox_inches='tight', facecolor=fig.get_facecolor())
    plt.close(fig)
    print(f"  ✓ {name}")
    return path


def uml_box(ax, x, y, w, h, title, header_color, rows_attrs, rows_methods, fontsize=7.5):
    """Draw a UML class box."""
    line_h = 0.018
    pad = 0.012

    # Shadow
    shadow = FancyBboxPatch((x+0.004, y-h-0.004), w, h,
                             boxstyle="round,pad=0.005", linewidth=0,
                             facecolor="#00000022", zorder=1)
    ax.add_patch(shadow)

    # Body
    body = FancyBboxPatch((x, y-h), w, h,
                           boxstyle="round,pad=0.005", linewidth=1.2,
                           edgecolor=header_color, facecolor=WHITE, zorder=2)
    ax.add_patch(body)

    # Header band
    n_header_lines = len(title) if isinstance(title, list) else 1
    hdr_h = pad*2 + line_h * n_header_lines
    hdr = FancyBboxPatch((x, y-hdr_h), w, hdr_h,
                          boxstyle="round,pad=0.005", linewidth=0,
                          facecolor=header_color, zorder=3)
    ax.add_patch(hdr)

    # Title
    if isinstance(title, list):
        for i, t in enumerate(title):
            ax.text(x + w/2, y - pad - line_h*(i+0.5), t,
                    ha='center', va='center', fontsize=fontsize+0.5,
                    fontweight='bold', color=WHITE, zorder=4)
    else:
        ax.text(x + w/2, y - hdr_h/2, title,
                ha='center', va='center', fontsize=fontsize+0.5,
                fontweight='bold', color=WHITE, zorder=4)

    cur_y = y - hdr_h
    # Attributes section
    if rows_attrs:
        ax.plot([x, x+w], [cur_y, cur_y], color=header_color, lw=0.8, zorder=4)
        for r in rows_attrs:
            cur_y -= line_h
            ax.text(x + pad, cur_y, r, ha='left', va='top',
                    fontsize=fontsize, color=GRAY_DARK, zorder=4,
                    fontfamily='monospace')

    # Methods section
    if rows_methods:
        cur_y -= pad
        ax.plot([x, x+w], [cur_y, cur_y], color=header_color, lw=0.8, zorder=4)
        for m in rows_methods:
            cur_y -= line_h
            ax.text(x + pad, cur_y, m, ha='left', va='top',
                    fontsize=fontsize, color=BLUE_MID, zorder=4,
                    fontfamily='monospace')


def arrow(ax, x1, y1, x2, y2, style='use', color=GRAY_MID, lw=1.2):
    arrowprops = dict(arrowstyle='->', color=color, lw=lw)
    if style == 'use':
        arrowprops['linestyle'] = 'dashed'
    elif style == 'assoc':
        arrowprops['linestyle'] = 'solid'
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=arrowprops, zorder=5)


# ══════════════════════════════════════════════════════════════════════════════
# 1 · DIAGRAMA DE CLASSES
# ══════════════════════════════════════════════════════════════════════════════

def gen_diagrama_classes():
    fig = plt.figure(figsize=(22, 14), facecolor=GRAY_LIGHT)
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')
    fig.text(0.5, 0.97, 'Diagrama de Classes — Achados & Perdidos',
             ha='center', va='top', fontsize=16, fontweight='bold', color=BLUE_DARK)
    fig.text(0.5, 0.945, 'Equipe Lock in · SESI Blumenau · Hackathon v0.1',
             ha='center', va='top', fontsize=10, color=GRAY_MID)

    # ── Item (model) ──────────────────────────────────────────────────────────
    uml_box(ax, 0.35, 0.90, 0.30, 0.58, '<<model>>\nItem', BLUE_DARK,
        rows_attrs=[
            '- id : int',
            '- descricao : String',
            '- categoria : String',
            '- localEncontrado : String',
            '- dataEncontrado : LocalDate',
            '- status : String',
            '- dataCadastro : LocalDateTime',
            '- observacoes : String',
            '- fotoPath : String',
        ],
        rows_methods=[
            '+ getId() : int',
            '+ getDescricao() : String',
            '+ getCategoria() : String',
            '+ getLocalEncontrado() : String',
            '+ getDataEncontrado() : LocalDate',
            '+ getStatus() : String',
            '+ getDataCadastro() : LocalDateTime',
            '+ getObservacoes() : String',
            '+ getFotoPath() : String',
            '+ getDataEncontradoFormatada() : String',
            '+ getDataEncontradoIso() : String',
            '+ getDataCadastroFormatada() : String',
        ])

    # ── ConnectionFactory (dao) ───────────────────────────────────────────────
    uml_box(ax, 0.02, 0.56, 0.28, 0.11, '<<dao>>\nConnectionFactory', "#7c3aed",
        rows_attrs=[],
        rows_methods=['+ getConnection() : Connection'])

    # ── ItemDAO (dao) ──────────────────────────────────────────────────────────
    uml_box(ax, 0.02, 0.40, 0.28, 0.25, '<<dao>>\nItemDAO', BLUE_MID,
        rows_attrs=['- SELECT_COLS : String'],
        rows_methods=[
            '+ findAll(status, busca, categoria) : List<Item>',
            '+ findById(id) : Item',
            '+ save(item) : void',
            '+ updateStatus(id, status) : void',
            '- mapRow(rs) : Item',
        ])

    # ── ItemService (service) ─────────────────────────────────────────────────
    uml_box(ax, 0.02, 0.10, 0.28, 0.27, '<<service>>\nItemService', GREEN,
        rows_attrs=[
            '- DESC_MAX_LEN : int = 255',
            '- CATEGORIAS_VALIDAS : List<String>',
            '- EXTENSOES_IMAGEM : Set<String>',
            '- dao : ItemDAO',
        ],
        rows_methods=[
            '+ listarItens(status, busca, cat) : List<Item>',
            '+ buscarItemPorId(id) : Item',
            '+ cadastrarItem(item) : void',
            '+ devolverItem(id) : void',
            '+ isExtensaoImagemValida(nome) : boolean',
            '- validar(item) : void',
        ])

    # ── Servlets ───────────────────────────────────────────────────────────────
    uml_box(ax, 0.70, 0.85, 0.28, 0.18, '<<servlet>>\nItemListServlet', ORANGE,
        rows_attrs=['- service : ItemService'],
        rows_methods=['+ doGet(req, resp) : void'])

    uml_box(ax, 0.70, 0.62, 0.28, 0.18, '<<servlet>>\nItemCreateServlet', ORANGE,
        rows_attrs=['- service : ItemService'],
        rows_methods=[
            '+ doGet(req, resp) : void',
            '+ doPost(req, resp) : void',
        ])

    uml_box(ax, 0.70, 0.38, 0.28, 0.14, '<<servlet>>\nItemUpdateServlet', ORANGE,
        rows_attrs=['- service : ItemService'],
        rows_methods=['+ doPost(req, resp) : void'])

    # ── Arrows ────────────────────────────────────────────────────────────────
    # ItemDAO uses ConnectionFactory
    ax.annotate('', xy=(0.16, 0.45), xytext=(0.16, 0.46),
                arrowprops=dict(arrowstyle='->', color="#7c3aed", lw=1.2, linestyle='dashed'), zorder=5)
    # ItemService uses ItemDAO
    ax.annotate('', xy=(0.16, 0.15), xytext=(0.16, 0.16),
                arrowprops=dict(arrowstyle='->', color=BLUE_MID, lw=1.2, linestyle='dashed'), zorder=5)

    # Servlets use ItemService
    for sy in [0.75, 0.52, 0.32]:
        ax.annotate('', xy=(0.30, 0.05), xytext=(0.70, sy),
                    arrowprops=dict(arrowstyle='->', color=GREEN, lw=1.0,
                                   linestyle='dashed',
                                   connectionstyle='arc3,rad=0.0'), zorder=5)

    # ItemDAO uses Item
    ax.annotate('', xy=(0.35, 0.65), xytext=(0.30, 0.33),
                arrowprops=dict(arrowstyle='->', color=BLUE_MID, lw=1.2, linestyle='solid'), zorder=5)

    # ItemService uses Item
    ax.annotate('', xy=(0.35, 0.50), xytext=(0.30, 0.07),
                arrowprops=dict(arrowstyle='->', color=GREEN, lw=1.0,
                               linestyle='solid',
                               connectionstyle='arc3,rad=-0.1'), zorder=5)

    # Legend
    legend_items = [
        mpatches.Patch(color=BLUE_DARK, label='model'),
        mpatches.Patch(color="#7c3aed", label='dao – ConnectionFactory'),
        mpatches.Patch(color=BLUE_MID,  label='dao – ItemDAO'),
        mpatches.Patch(color=GREEN,     label='service'),
        mpatches.Patch(color=ORANGE,    label='servlet (controller)'),
    ]
    ax.legend(handles=legend_items, loc='lower right', fontsize=8,
              framealpha=0.9, title='Camadas', title_fontsize=9)

    save(fig, "1_diagrama_classes.png")


# ══════════════════════════════════════════════════════════════════════════════
# 2 · MODELO ENTIDADE-RELACIONAMENTO
# ══════════════════════════════════════════════════════════════════════════════

def gen_mer():
    fig = plt.figure(figsize=(14, 9), facecolor=GRAY_LIGHT)
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')
    fig.text(0.5, 0.96, 'Modelo Entidade-Relacionamento — Achados & Perdidos',
             ha='center', va='top', fontsize=14, fontweight='bold', color=BLUE_DARK)
    fig.text(0.5, 0.92, 'Banco de dados: achados_perdidos · Engine: InnoDB · Charset: utf8mb4',
             ha='center', va='top', fontsize=9, color=GRAY_MID)

    # Table box
    cols = [
        ('id',               'INT',          'PK · AUTO_INCREMENT · NOT NULL', True),
        ('descricao',        'VARCHAR(255)',  'NOT NULL', False),
        ('categoria',        'VARCHAR(100)',  "NOT NULL · CHECK ENUM list", False),
        ('local_encontrado', 'VARCHAR(255)',  'NOT NULL', False),
        ('data_encontrado',  'DATE',          'NOT NULL', False),
        ('status',           "ENUM",          "'ACHADO' | 'DEVOLVIDO' · DEFAULT 'ACHADO'", False),
        ('data_cadastro',    'DATETIME',      'NOT NULL · DEFAULT CURRENT_TIMESTAMP', False),
        ('observacoes',      'TEXT',          'NULL', False),
        ('foto_path',        'VARCHAR(255)',  'NULL', False),
    ]

    bx, by, bw = 0.08, 0.87, 0.84
    row_h = 0.068
    header_h = 0.065
    total_h = header_h + row_h * len(cols)

    # Shadow
    ax.add_patch(FancyBboxPatch((bx+0.005, by-total_h-0.005), bw, total_h,
                                boxstyle="round,pad=0.01", linewidth=0,
                                facecolor="#00000022", zorder=1))
    # Box
    ax.add_patch(FancyBboxPatch((bx, by-total_h), bw, total_h,
                                boxstyle="round,pad=0.01", linewidth=1.5,
                                edgecolor=BLUE_DARK, facecolor=WHITE, zorder=2))
    # Header
    ax.add_patch(FancyBboxPatch((bx, by-header_h), bw, header_h,
                                boxstyle="round,pad=0.01", linewidth=0,
                                facecolor=BLUE_DARK, zorder=3))
    ax.text(bx + bw/2, by - header_h/2,
            'itens', ha='center', va='center',
            fontsize=13, fontweight='bold', color=WHITE, zorder=4)

    # Column header
    cy = by - header_h
    sub_h = 0.04
    ax.add_patch(plt.Rectangle((bx, cy-sub_h), bw, sub_h,
                               facecolor="#EFF6FF", zorder=3))
    for lbl, px in [('Coluna', 0.12), ('Tipo', 0.33), ('Restrições', 0.52)]:
        ax.text(px, cy - sub_h/2, lbl, ha='left', va='center',
                fontsize=8.5, fontweight='bold', color=BLUE_DARK, zorder=4)

    cy -= sub_h
    for i, (col, tipo, restricao, is_pk) in enumerate(cols):
        row_y = cy - row_h * i
        bg = "#FFFBEB" if is_pk else (WHITE if i % 2 == 0 else "#F8FAFC")
        ax.add_patch(plt.Rectangle((bx, row_y-row_h), bw, row_h,
                                   facecolor=bg, zorder=2))
        ax.plot([bx, bx+bw], [row_y, row_y], color="#E5E7EB", lw=0.5, zorder=3)

        icon = "🔑 " if is_pk else "   "
        ax.text(0.10, row_y - row_h/2, icon + col,
                ha='left', va='center', fontsize=9,
                fontweight='bold' if is_pk else 'normal',
                color=ORANGE if is_pk else GRAY_DARK, zorder=4,
                fontfamily='monospace')
        ax.text(0.33, row_y - row_h/2, tipo,
                ha='left', va='center', fontsize=8.5,
                color=BLUE_MID, zorder=4, fontfamily='monospace')
        ax.text(0.52, row_y - row_h/2, restricao,
                ha='left', va='center', fontsize=7.5,
                color=GRAY_MID, zorder=4)

    # Note about single-entity model
    note_y = by - total_h - 0.06
    ax.add_patch(FancyBboxPatch((0.08, note_y-0.08), 0.84, 0.07,
                                boxstyle="round,pad=0.01", linewidth=1,
                                edgecolor="#FCD34D", facecolor="#FFFBEB", zorder=2))
    ax.text(0.50, note_y - 0.04,
            'O sistema v0.1 possui uma única entidade (itens). '
            'A expansão prevista inclui entidades: Usuario, Reclamante e Historico.',
            ha='center', va='center', fontsize=8.5, color=GRAY_DARK,
            style='italic', zorder=3)

    save(fig, "2_modelo_er.png")


# ══════════════════════════════════════════════════════════════════════════════
# 3 · CRUD
# ══════════════════════════════════════════════════════════════════════════════

def gen_crud():
    fig = plt.figure(figsize=(20, 14), facecolor=GRAY_LIGHT)
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')

    fig.text(0.5, 0.975, 'Operações CRUD — Achados & Perdidos',
             ha='center', va='top', fontsize=15, fontweight='bold', color=BLUE_DARK)
    fig.text(0.5, 0.953, 'Create · Read · Update · Delete',
             ha='center', va='top', fontsize=10, color=GRAY_MID)

    ops = [
        {
            'letter': 'C',
            'name': 'CREATE — Cadastrar Item',
            'color': GREEN,
            'light': GREEN_LIGHT,
            'http': 'POST /itens/novo',
            'class': 'ItemCreateServlet → ItemService.cadastrarItem() → ItemDAO.save()',
            'sql': (
                'INSERT INTO itens\n'
                '  (descricao, categoria, local_encontrado,\n'
                '   data_encontrado, status, observacoes, foto_path)\n'
                'VALUES (?, ?, ?, ?, ?, ?, ?);'
            ),
            'notes': [
                '• Valida: descrição ≤ 255 chars, categoria válida, local preenchido, data ≤ hoje',
                '• Status padrão: ACHADO',
                '• Upload de foto opcional (jpg, png, webp, gif · máx 5MB)',
                '• PreparedStatement previne SQL Injection',
                '• Retorna ID gerado via getGeneratedKeys()',
            ],
        },
        {
            'letter': 'R',
            'name': 'READ — Listar / Buscar Itens',
            'color': BLUE_MID,
            'light': BLUE_LIGHT,
            'http': 'GET /itens  |  GET /itens?status=ACHADO&categoria=ROUPAS&busca=mochila',
            'class': 'ItemListServlet → ItemService.listarItens() → ItemDAO.findAll()',
            'sql': (
                'SELECT id, descricao, categoria, local_encontrado,\n'
                '       data_encontrado, status, data_cadastro,\n'
                '       observacoes, foto_path\n'
                'FROM   itens\n'
                'WHERE  1=1\n'
                '  [AND status = ?]          -- filtro opcional\n'
                '  [AND descricao LIKE ?]    -- busca opcional\n'
                '  [AND categoria = ?]       -- filtro opcional\n'
                'ORDER  BY data_cadastro DESC;'
            ),
            'notes': [
                '• Filtros combinados: status, categoria e busca por descrição',
                '• Também: ItemDAO.findById(id) para busca por PK',
                '• Resultados ordenados por data de cadastro (mais recentes primeiro)',
                '• Contadores totalItens / totalAchados / totalDevolvidos no hero',
            ],
        },
        {
            'letter': 'U',
            'name': 'UPDATE — Marcar como Devolvido',
            'color': ORANGE,
            'light': "#FEF3C7",
            'http': 'POST /itens/devolver  (body: id=<n>)',
            'class': 'ItemUpdateServlet → ItemService.devolverItem() → ItemDAO.updateStatus()',
            'sql': (
                'UPDATE itens\n'
                'SET    status = \'DEVOLVIDO\'\n'
                'WHERE  id = ?;'
            ),
            'notes': [
                '• Única transição de status suportada na v0.1: ACHADO → DEVOLVIDO',
                '• Operação idempotente (repetir não causa erro)',
                '• Confirmação visual via redirecionamento com ?sucesso=… na URL',
                '• Expansão prevista: edição completa de todos os campos',
            ],
        },
        {
            'letter': 'D',
            'name': 'DELETE — Excluir Item',
            'color': RED,
            'light': "#FEE2E2",
            'http': 'Não implementado na v0.1',
            'class': '— (previsto para versão futura)',
            'sql': (
                '-- Estrutura preparada:\n'
                'DELETE FROM itens WHERE id = ?;\n\n'
                '-- Alternativa recomendada (soft-delete):\n'
                'ALTER TABLE itens\n'
                '  ADD COLUMN deletado_em DATETIME NULL;\n'
                'UPDATE itens SET deletado_em = NOW() WHERE id = ?;'
            ),
            'notes': [
                '• Exclusão física não está disponível na interface v0.1',
                '• Soft-delete (campo deletado_em) recomendado para auditoria',
                '• Proteção contra exclusão acidental de itens com dono identificado',
                '• Schema e PreparedStatement já suportariam a operação quando necessário',
            ],
        },
    ]

    xs = [0.01, 0.26, 0.51, 0.76]
    top = 0.92
    col_w = 0.23

    for op, x in zip(ops, xs):
        # card background
        ax.add_patch(FancyBboxPatch((x, 0.02), col_w, top-0.02,
                                   boxstyle="round,pad=0.01",
                                   linewidth=1.5, edgecolor=op['color'],
                                   facecolor=WHITE, zorder=2))
        # header band
        ax.add_patch(FancyBboxPatch((x, top-0.07), col_w, 0.07,
                                   boxstyle="round,pad=0.01",
                                   linewidth=0, facecolor=op['color'], zorder=3))
        # big letter
        ax.text(x + 0.025, top-0.035, op['letter'],
                ha='center', va='center', fontsize=22,
                fontweight='bold', color=WHITE, zorder=4)
        ax.text(x + 0.13, top-0.035, op['name'],
                ha='center', va='center', fontsize=8,
                fontweight='bold', color=WHITE, zorder=4)

        cy = top - 0.10

        # HTTP
        ax.text(x+0.01, cy, 'Endpoint HTTP:', ha='left', va='top',
                fontsize=7.5, fontweight='bold', color=op['color'], zorder=4)
        cy -= 0.022
        ax.add_patch(plt.Rectangle((x+0.005, cy-0.018), col_w-0.01, 0.022,
                                   facecolor=op['light'], zorder=3))
        ax.text(x+0.012, cy-0.008, op['http'],
                ha='left', va='center', fontsize=6.5,
                color=GRAY_DARK, zorder=4, fontfamily='monospace')
        cy -= 0.035

        # Class
        ax.text(x+0.01, cy, 'Fluxo de Classes:', ha='left', va='top',
                fontsize=7.5, fontweight='bold', color=op['color'], zorder=4)
        cy -= 0.022
        ax.text(x+0.012, cy, op['class'],
                ha='left', va='top', fontsize=6.2,
                color=GRAY_DARK, zorder=4, fontfamily='monospace', wrap=True)
        cy -= 0.03

        # SQL
        ax.text(x+0.01, cy, 'SQL (PreparedStatement):', ha='left', va='top',
                fontsize=7.5, fontweight='bold', color=op['color'], zorder=4)
        cy -= 0.022
        sql_lines = op['sql'].split('\n')
        sql_h = 0.017 * len(sql_lines) + 0.014
        ax.add_patch(FancyBboxPatch((x+0.005, cy-sql_h), col_w-0.01, sql_h,
                                   boxstyle="round,pad=0.005",
                                   linewidth=0.5, edgecolor="#D1D5DB",
                                   facecolor="#1E293B", zorder=3))
        ax.text(x+0.012, cy-0.007, op['sql'],
                ha='left', va='top', fontsize=6,
                color="#86EFAC", zorder=4, fontfamily='monospace')
        cy -= sql_h + 0.02

        # Notes
        ax.text(x+0.01, cy, 'Observações:', ha='left', va='top',
                fontsize=7.5, fontweight='bold', color=op['color'], zorder=4)
        cy -= 0.022
        for note in op['notes']:
            ax.text(x+0.012, cy, note,
                    ha='left', va='top', fontsize=6.5, color=GRAY_DARK, zorder=4)
            cy -= 0.022

    save(fig, "3_crud.png")


# ══════════════════════════════════════════════════════════════════════════════
# 4 · PLANILHA (CSV + imagem)
# ══════════════════════════════════════════════════════════════════════════════

SEED_DATA = [
    (1, 'Mochila escolar preta sem identificação', 'ACESSÓRIOS',   'Ginásio Poliesportivo',     '28/04/2026', 'ACHADO',    'Mochila com estojo e caderno em branco'),
    (2, 'Carteira marrom de couro',                'DOCUMENTOS',   'Refeitório Central',         '29/04/2026', 'ACHADO',    'Contém cartões sem nome visível'),
    (3, 'Óculos de grau com armação azul',         'ACESSÓRIOS',   'Biblioteca',                 '25/04/2026', 'DEVOLVIDO', 'Devolvido ao portador no dia seguinte'),
    (4, 'Garrafa de água verde 500ml',             'OUTROS',       'Quadra Esportiva',           '30/04/2026', 'ACHADO',    '—'),
    (5, 'Fone de ouvido intra-auricular branco',   'ELETRÔNICOS',  'Laboratório de Informática', '01/05/2026', 'ACHADO',    'Encontrado embaixo de uma mesa no fundo da sala'),
]
HEADERS = ['ID', 'Descrição', 'Categoria', 'Local Encontrado', 'Data', 'Status', 'Observações']

def gen_sheet():
    # CSV
    csv_path = os.path.join(OUT, "4_planilha.csv")
    with open(csv_path, 'w', newline='', encoding='utf-8-sig') as f:
        w = csv.writer(f)
        w.writerow(HEADERS)
        w.writerows(SEED_DATA)
    print("  ✓ 4_planilha.csv")

    # PNG
    fig = plt.figure(figsize=(18, 6), facecolor=WHITE)
    ax  = fig.add_axes([0.01, 0.05, 0.98, 0.80])
    ax.axis('off')
    fig.text(0.5, 0.97, 'Planilha de Dados — itens (seed)',
             ha='center', va='top', fontsize=13, fontweight='bold', color=BLUE_DARK)
    fig.text(0.5, 0.90, 'Banco: achados_perdidos · Tabela: itens · 5 registros de exemplo',
             ha='center', va='top', fontsize=9, color=GRAY_MID)

    col_widths = [0.03, 0.22, 0.08, 0.16, 0.07, 0.07, 0.29]

    cell_data = [list(HEADERS)] + [list(r) for r in SEED_DATA]
    n_rows = len(cell_data)
    n_cols = len(HEADERS)

    row_h = 1.0 / n_rows
    xs = [sum(col_widths[:i]) for i in range(n_cols)]

    for r, row in enumerate(cell_data):
        for c, val in enumerate(row):
            is_header = (r == 0)
            is_pk     = (c == 0 and not is_header)
            is_devol  = (c == 5 and str(val) == 'DEVOLVIDO')
            is_achado = (c == 5 and str(val) == 'ACHADO')

            bg = BLUE_DARK if is_header else ("#F0FDF4" if r % 2 == 0 else WHITE)
            fc = WHITE if is_header else (ORANGE if is_pk else
                 (GREEN if is_devol else (BLUE_MID if is_achado else GRAY_DARK)))
            fw = 'bold' if (is_header or is_pk) else 'normal'
            fs = 8.5

            rect = plt.Rectangle((xs[c], 1 - row_h*(r+1)), col_widths[c], row_h,
                                  facecolor=bg, edgecolor="#D1D5DB", linewidth=0.5,
                                  transform=ax.transAxes, clip_on=False)
            ax.add_patch(rect)

            # Status badge
            if is_devol or is_achado:
                badge_c = "#DCFCE7" if is_devol else "#DBEAFE"
                bx = xs[c] + 0.002
                by = 1 - row_h*(r+1) + row_h*0.15
                bw2 = col_widths[c] - 0.004
                ax.add_patch(FancyBboxPatch((bx, by), bw2, row_h*0.65,
                                           boxstyle="round,pad=0.002",
                                           linewidth=0, facecolor=badge_c,
                                           transform=ax.transAxes, clip_on=False))

            ax.text(xs[c] + col_widths[c]/2, 1 - row_h*(r+0.5), str(val),
                    ha='center', va='center', fontsize=fs,
                    fontweight=fw, color=fc, transform=ax.transAxes,
                    clip_on=False)

    save(fig, "4_planilha.png")


# ══════════════════════════════════════════════════════════════════════════════
# 5 · IMAGENS DAS TELAS
# ══════════════════════════════════════════════════════════════════════════════

def _draw_header(ax, ctx_path='/achados-perdidos'):
    ax.add_patch(plt.Rectangle((0, 0.935), 1, 0.065,
                               facecolor=BLUE_DARK, transform=ax.transAxes))
    ax.text(0.02, 0.968, '🔍  Achados & Perdidos', transform=ax.transAxes,
            ha='left', va='center', fontsize=11, fontweight='bold', color=WHITE)
    ax.add_patch(FancyBboxPatch((0.80, 0.942), 0.18, 0.045,
                               boxstyle="round,pad=0.005",
                               facecolor="#f97316", linewidth=0,
                               transform=ax.transAxes))
    ax.text(0.89, 0.965, '+ Registrar Item', transform=ax.transAxes,
            ha='center', va='center', fontsize=8, color=WHITE)


def gen_tela_lista():
    fig = plt.figure(figsize=(16, 11), facecolor="#F8FAFC")
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')

    _draw_header(ax)

    # Hero
    ax.add_patch(plt.Rectangle((0, 0.75), 1, 0.185, facecolor=BLUE_DARK))
    ax.text(0.5, 0.900, 'Perdeu algo?  Encontrou algo?',
            ha='center', va='center', fontsize=14, fontweight='bold', color=WHITE)
    ax.text(0.5, 0.870, 'Plataforma de Achados & Perdidos — SESI Blumenau',
            ha='center', va='center', fontsize=9, color="#93C5FD")

    # Stats
    for val, lbl, sx in [('5','Itens Cadastrados',0.30),
                          ('4','Aguardando dono',0.50),
                          ('1','Devolvidos',0.70)]:
        ax.add_patch(FancyBboxPatch((sx-0.07, 0.760), 0.14, 0.065,
                                   boxstyle="round,pad=0.008",
                                   facecolor="#1e3a8a", linewidth=0))
        ax.text(sx, 0.800, val, ha='center', va='center',
                fontsize=14, fontweight='bold', color=WHITE)
        ax.text(sx, 0.775, lbl, ha='center', va='center',
                fontsize=7, color="#93C5FD")

    # Filter bar
    ax.add_patch(FancyBboxPatch((0.02, 0.68), 0.96, 0.065,
                               boxstyle="round,pad=0.005",
                               facecolor=WHITE, linewidth=0.8,
                               edgecolor="#E5E7EB"))
    for lbl, sx in [('Status ▼', 0.10), ('Categoria ▼', 0.30),
                    ('Buscar descrição...', 0.58)]:
        ax.add_patch(FancyBboxPatch((sx-0.08, 0.690), 0.20, 0.035,
                                   boxstyle="round,pad=0.003",
                                   facecolor="#F9FAFB", linewidth=0.5,
                                   edgecolor="#D1D5DB"))
        ax.text(sx, 0.708, lbl, ha='center', va='center',
                fontsize=7.5, color=GRAY_MID)
    ax.add_patch(FancyBboxPatch((0.82, 0.690), 0.07, 0.035,
                               boxstyle="round,pad=0.003",
                               facecolor=BLUE_MID, linewidth=0))
    ax.text(0.855, 0.708, 'Filtrar', ha='center', va='center',
            fontsize=7.5, color=WHITE)

    # Section title
    ax.text(0.04, 0.655, 'Itens', ha='left', va='center',
            fontsize=11, fontweight='bold', color=BLUE_DARK)
    ax.text(0.96, 0.655, '5 item(ns)', ha='right', va='center',
            fontsize=8, color=GRAY_MID)

    # Cards grid
    card_data = [
        ('ACESSÓRIOS',   'Mochila escolar preta...', 'Ginásio Poliesportivo', '28/04/2026', 'ACHADO'),
        ('DOCUMENTOS',   'Carteira marrom de couro', 'Refeitório Central',    '29/04/2026', 'ACHADO'),
        ('ACESSÓRIOS',   'Óculos de grau armação...','Biblioteca',            '25/04/2026', 'DEVOLVIDO'),
        ('OUTROS',       'Garrafa de água verde...', 'Quadra Esportiva',      '30/04/2026', 'ACHADO'),
        ('ELETRÔNICOS',  'Fone de ouvido branco',    'Lab. de Informática',   '01/05/2026', 'ACHADO'),
    ]
    cw, ch = 0.185, 0.24
    gap = 0.015
    start_x = 0.02

    for i, (cat, desc, local, data, status) in enumerate(card_data):
        cx = start_x + i*(cw+gap)
        cy = 0.385

        ax.add_patch(FancyBboxPatch((cx, cy), cw, ch,
                                   boxstyle="round,pad=0.006",
                                   facecolor=WHITE, linewidth=0.8,
                                   edgecolor="#E5E7EB", zorder=2))
        # Photo area
        ax.add_patch(plt.Rectangle((cx, cy+0.11), cw, 0.13,
                                   facecolor="#F1F5F9", zorder=3))
        ax.text(cx+cw/2, cy+0.175, '📦', ha='center', va='center',
                fontsize=16, zorder=4)

        # Badge
        bc = GREEN if status == 'DEVOLVIDO' else BLUE_MID
        ax.add_patch(FancyBboxPatch((cx+0.005, cy+0.218), 0.10, 0.018,
                                   boxstyle="round,pad=0.002",
                                   facecolor=bc, linewidth=0, zorder=5))
        ax.text(cx+0.055, cy+0.227, status, ha='center', va='center',
                fontsize=5.5, color=WHITE, fontweight='bold', zorder=6)

        # Text
        ax.text(cx+0.007, cy+0.100, cat, ha='left', va='top',
                fontsize=6, color=GRAY_MID, zorder=4)
        ax.text(cx+0.007, cy+0.082, desc, ha='left', va='top',
                fontsize=7, fontweight='bold', color=GRAY_DARK, zorder=4)
        ax.text(cx+0.007, cy+0.052, '📍 ' + local, ha='left', va='top',
                fontsize=6, color=GRAY_MID, zorder=4)
        ax.text(cx+0.007, cy+0.035, '📅 ' + data, ha='left', va='top',
                fontsize=6, color=GRAY_MID, zorder=4)

        if status == 'ACHADO':
            ax.add_patch(FancyBboxPatch((cx+0.005, cy+0.005), cw-0.01, 0.022,
                                       boxstyle="round,pad=0.002",
                                       facecolor=GREEN_LIGHT, linewidth=0.5,
                                       edgecolor=GREEN, zorder=4))
            ax.text(cx+cw/2, cy+0.016, '✓ Marcar como Devolvido',
                    ha='center', va='center', fontsize=6,
                    color=GREEN, fontweight='bold', zorder=5)

    # Footer
    ax.add_patch(plt.Rectangle((0, 0), 1, 0.045, facecolor="#1E293B"))
    ax.text(0.5, 0.022, 'Achados & Perdidos · v0.1',
            ha='center', va='center', fontsize=8, color=GRAY_MID)

    fig.text(0.5, 0.015, 'Tela 1: Lista de Itens — /achados-perdidos/itens',
             ha='center', fontsize=7, color=GRAY_MID, style='italic')

    save(fig, "5a_tela_lista.png")


def gen_tela_form():
    fig = plt.figure(figsize=(14, 12), facecolor="#F8FAFC")
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')

    _draw_header(ax)

    # Form card
    ax.add_patch(FancyBboxPatch((0.08, 0.05), 0.84, 0.87,
                               boxstyle="round,pad=0.01",
                               facecolor=WHITE, linewidth=1,
                               edgecolor="#E5E7EB", zorder=2))

    # Card header
    ax.add_patch(plt.Rectangle((0.08, 0.875), 0.84, 0.055,
                               facecolor="#F8FAFC", zorder=3))
    ax.plot([0.08, 0.92], [0.875, 0.875], color="#E5E7EB", lw=1, zorder=4)
    ax.text(0.12, 0.902, '📦  Registrar Item Achado',
            ha='left', va='center', fontsize=12, fontweight='bold',
            color=BLUE_DARK, zorder=4)

    # Photo upload
    ax.add_patch(FancyBboxPatch((0.10, 0.765), 0.80, 0.095,
                               boxstyle="round,pad=0.005",
                               facecolor="#F9FAFB", linewidth=1.5,
                               edgecolor="#D1D5DB", linestyle='dashed', zorder=3))
    ax.text(0.50, 0.820, '📷', ha='center', va='center', fontsize=20, zorder=4)
    ax.text(0.50, 0.790, 'Clique para adicionar uma foto',
            ha='center', va='center', fontsize=8, color=GRAY_MID, zorder=4)
    ax.text(0.50, 0.775, 'JPG, PNG, WEBP · máx. 5MB',
            ha='center', va='center', fontsize=7, color="#9CA3AF", zorder=4)
    ax.text(0.10, 0.862, 'Foto do Item', ha='left', va='center',
            fontsize=8, fontweight='bold', color=GRAY_DARK, zorder=4)

    # Field helper
    def field(lbl, placeholder, x, y, w, required=False, is_select=False):
        req = ' *' if required else ''
        ax.text(x, y+0.028, lbl+req, ha='left', va='center',
                fontsize=8, fontweight='bold', color=GRAY_DARK, zorder=4)
        ax.add_patch(FancyBboxPatch((x, y), w, 0.032,
                                   boxstyle="round,pad=0.004",
                                   facecolor=WHITE, linewidth=1,
                                   edgecolor="#D1D5DB", zorder=3))
        icon = ' ▼' if is_select else ''
        ax.text(x+0.01, y+0.016, placeholder+icon,
                ha='left', va='center', fontsize=7.5,
                color="#9CA3AF", zorder=4)

    field('Descrição do Item', 'Ex: Mochila escolar preta com zíper amarelo',
          0.10, 0.700, 0.80, required=True)

    # AI panel
    ax.add_patch(FancyBboxPatch((0.10, 0.648), 0.80, 0.043,
                               boxstyle="round,pad=0.005",
                               facecolor="#EFF6FF", linewidth=0.8,
                               edgecolor="#BFDBFE", zorder=3))
    ax.add_patch(FancyBboxPatch((0.11, 0.655), 0.03, 0.022,
                               boxstyle="round,pad=0.002",
                               facecolor=BLUE_MID, linewidth=0, zorder=4))
    ax.text(0.125, 0.666, 'IA', ha='center', va='center',
            fontsize=7, fontweight='bold', color=WHITE, zorder=5)
    ax.text(0.155, 0.666, 'Assistente de Categorização',
            ha='left', va='center', fontsize=7.5,
            fontweight='bold', color=BLUE_MID, zorder=5)
    ax.text(0.155, 0.654, 'Digite a descrição e aguarde a sugestão automática de categoria...',
            ha='left', va='center', fontsize=7, color=GRAY_MID, zorder=5)

    field('Categoria', 'Selecione...', 0.10, 0.578, 0.38, required=True, is_select=True)
    field('Local Encontrado', 'Ex: Refeitório Central', 0.52, 0.578, 0.38, required=True)
    field('Data Encontrado', '', 0.10, 0.508, 0.38, required=True)
    ax.text(0.11, 0.522, '📅  dd/mm/aaaa', ha='left', va='center',
            fontsize=7.5, color="#9CA3AF")

    # Observações
    ax.text(0.10, 0.488, 'Observações', ha='left', va='center',
            fontsize=8, fontweight='bold', color=GRAY_DARK)
    ax.add_patch(FancyBboxPatch((0.10, 0.390), 0.80, 0.090,
                               boxstyle="round,pad=0.004",
                               facecolor=WHITE, linewidth=1,
                               edgecolor="#D1D5DB"))
    ax.text(0.11, 0.465, 'Detalhes adicionais (opcional)...',
            ha='left', va='center', fontsize=7.5, color="#9CA3AF")
    ax.text(0.89, 0.395, '0/500', ha='right', va='center',
            fontsize=6.5, color=GRAY_MID)

    # Buttons
    ax.add_patch(FancyBboxPatch((0.10, 0.310), 0.15, 0.040,
                               boxstyle="round,pad=0.005",
                               facecolor=GRAY_LIGHT, linewidth=0.8,
                               edgecolor="#D1D5DB"))
    ax.text(0.175, 0.330, 'Cancelar', ha='center', va='center',
            fontsize=8.5, color=GRAY_DARK)

    ax.add_patch(FancyBboxPatch((0.68, 0.310), 0.22, 0.040,
                               boxstyle="round,pad=0.005",
                               facecolor="#f97316", linewidth=0))
    ax.text(0.79, 0.330, '✓  Registrar Item', ha='center', va='center',
            fontsize=8.5, color=WHITE, fontweight='bold')

    ax.text(0.50, 0.282, '* Campos obrigatórios · Dados armazenados em conformidade com LGPD e ECA',
            ha='center', va='center', fontsize=7, color=GRAY_MID, style='italic')

    # Footer
    ax.add_patch(plt.Rectangle((0, 0), 1, 0.040, facecolor="#1E293B"))
    ax.text(0.5, 0.020, 'Achados & Perdidos · v0.1',
            ha='center', va='center', fontsize=8, color=GRAY_MID)

    fig.text(0.5, 0.010, 'Tela 2: Formulário de Cadastro — POST /achados-perdidos/itens/novo',
             ha='center', fontsize=7, color=GRAY_MID, style='italic')

    save(fig, "5b_tela_formulario.png")


def gen_tela_erro():
    fig = plt.figure(figsize=(13, 8), facecolor="#F8FAFC")
    ax  = fig.add_axes([0, 0, 1, 1])
    ax.set_xlim(0, 1); ax.set_ylim(0, 1)
    ax.axis('off')

    _draw_header(ax)

    # Error card
    ax.add_patch(FancyBboxPatch((0.15, 0.28), 0.70, 0.55,
                               boxstyle="round,pad=0.015",
                               facecolor=WHITE, linewidth=1.5,
                               edgecolor=RED, zorder=2))

    # Top banner
    ax.add_patch(plt.Rectangle((0.15, 0.775), 0.70, 0.055,
                               facecolor="#FEF2F2", zorder=3))
    ax.plot([0.15, 0.85], [0.775, 0.775], color=RED, lw=1, zorder=4)
    ax.text(0.50, 0.802, '⚠  Ocorreu um Erro', ha='center', va='center',
            fontsize=13, fontweight='bold', color=RED, zorder=4)

    ax.text(0.50, 0.680, '😕', ha='center', va='center', fontsize=40, zorder=3)

    ax.text(0.50, 0.610, 'Não foi possível processar a requisição',
            ha='center', va='center', fontsize=11,
            fontweight='bold', color=GRAY_DARK, zorder=3)

    ax.text(0.50, 0.562,
            'Exemplo: "A descrição do item é obrigatória." ou\n'
            '"Categoria inválida." ou "A data não pode ser no futuro."',
            ha='center', va='center', fontsize=9,
            color=GRAY_MID, style='italic', zorder=3)

    # Stack trace box
    ax.add_patch(FancyBboxPatch((0.18, 0.370), 0.64, 0.13,
                               boxstyle="round,pad=0.006",
                               facecolor="#1E293B", linewidth=0, zorder=3))
    ax.text(0.205, 0.490, 'Detalhe técnico (visível apenas em dev):',
            ha='left', va='center', fontsize=7, color="#94A3B8", zorder=4)
    ax.text(0.205, 0.465,
            'IllegalArgumentException: A descrição do item é obrigatória.',
            ha='left', va='center', fontsize=7,
            color="#86EFAC", fontfamily='monospace', zorder=4)
    ax.text(0.205, 0.445,
            '  at ItemService.validar(ItemService.java:52)',
            ha='left', va='center', fontsize=7,
            color="#94A3B8", fontfamily='monospace', zorder=4)
    ax.text(0.205, 0.425,
            '  at ItemService.cadastrarItem(ItemService.java:36)',
            ha='left', va='center', fontsize=7,
            color="#94A3B8", fontfamily='monospace', zorder=4)
    ax.text(0.205, 0.405,
            '  at ItemCreateServlet.doPost(ItemCreateServlet.java:..)',
            ha='left', va='center', fontsize=7,
            color="#94A3B8", fontfamily='monospace', zorder=4)

    # Back button
    ax.add_patch(FancyBboxPatch((0.38, 0.305), 0.24, 0.042,
                               boxstyle="round,pad=0.006",
                               facecolor=BLUE_MID, linewidth=0, zorder=3))
    ax.text(0.50, 0.326, '← Voltar para a Lista',
            ha='center', va='center', fontsize=9,
            color=WHITE, fontweight='bold', zorder=4)

    # Footer
    ax.add_patch(plt.Rectangle((0, 0), 1, 0.04, facecolor="#1E293B"))
    ax.text(0.5, 0.020, 'Achados & Perdidos · v0.1',
            ha='center', va='center', fontsize=8, color=GRAY_MID)

    fig.text(0.5, 0.010, 'Tela 3: Página de Erro — /WEB-INF/views/erro.jsp',
             ha='center', fontsize=7, color=GRAY_MID, style='italic')

    save(fig, "5c_tela_erro.png")


# ══════════════════════════════════════════════════════════════════════════════
# ZIP
# ══════════════════════════════════════════════════════════════════════════════

def gen_zip():
    zip_path = "/home/user/Atividade_Lock_in/entrega_lock_in.zip"
    files = [
        "1_diagrama_classes.png",
        "2_modelo_er.png",
        "3_crud.png",
        "4_planilha.csv",
        "4_planilha.png",
        "5a_tela_lista.png",
        "5b_tela_formulario.png",
        "5c_tela_erro.png",
    ]
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as z:
        for f in files:
            full = os.path.join(OUT, f)
            if os.path.exists(full):
                z.write(full, f)
    print(f"\n  ✅ ZIP gerado: {zip_path}")
    return zip_path


# ── MAIN ──────────────────────────────────────────────────────────────────────

if __name__ == '__main__':
    print("Gerando entregáveis...")
    gen_diagrama_classes()
    gen_mer()
    gen_crud()
    gen_sheet()
    gen_tela_lista()
    gen_tela_form()
    gen_tela_erro()
    gen_zip()
    print("Concluído!")
