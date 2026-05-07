/* ============================================================
   Achados & Perdidos — SESI Blumenau | v0.2
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {
    setDateInputMax();
    setupCharCounter();
    setupPhotoPreview();
    setupAISuggestion();
    setupFormValidation();
    setupAlertAutoDismiss();
});

function setDateInputMax() {
    var el = document.getElementById('dataEncontrado');
    if (el) el.max = new Date().toISOString().split('T')[0];
}

function setupCharCounter() {
    var ta  = document.getElementById('observacoes');
    var ctr = document.getElementById('charCounter');
    if (!ta || !ctr) return;
    function update() {
        var n = ta.value.length;
        ctr.textContent = n + '/500';
        ctr.style.color = n > 450 ? '#DC2626' : '#94A3B8';
    }
    ta.addEventListener('input', update);
    update();
}

/* ── Photo preview ────────────────────────────────────────── */
function setupPhotoPreview() {
    var input   = document.getElementById('foto');
    var preview = document.getElementById('fotoPreview');
    var area    = document.getElementById('photoUploadArea');
    var holder  = document.getElementById('photoPlaceholder');
    if (!input || !preview || !area) return;

    input.addEventListener('change', function () {
        var file = this.files[0];
        if (!file) return;
        if (!file.type.startsWith('image/')) {
            showToast('Selecione apenas imagens (JPG, PNG, WEBP)', true);
            this.value = '';
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            showToast('Imagem maior que 5MB. Escolha uma menor.', true);
            this.value = '';
            return;
        }
        var reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
            if (holder) holder.style.display = 'none';
            area.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    });
}

/* ── AI category suggestion ──────────────────────────────── */
var CATEGORY_KEYWORDS = {
    'ELETRÔNICOS': ['celular','phone','fone','headphone','headset','carregador',
                    'notebook','tablet','câmera','camera','teclado','mouse',
                    'pendrive','cabo','computador','bateria','fio'],
    'DOCUMENTOS':  ['carteira','rg','cpf','documento','identidade','cartão',
                    'passaporte','habilitação','cnh','boleto','registro'],
    'ROUPAS':      ['casaco','jaqueta','blusa','camiseta','calça','tênis',
                    'sapato','sandália','boné','chapéu','uniforme','meias',
                    'bermuda','vestido'],
    'ACESSÓRIOS':  ['mochila','bolsa','cinto','relógio','pulseira','colar',
                    'brinco','anel','guarda-chuva','garrafa','óculos','oculos',
                    'estojo','cartucheira'],
    'OUTROS':      []
};

function suggestCategory(desc) {
    var lower = desc.toLowerCase();
    for (var cat in CATEGORY_KEYWORDS) {
        if (cat === 'OUTROS') continue;
        var kws = CATEGORY_KEYWORDS[cat];
        for (var i = 0; i < kws.length; i++) {
            if (lower.indexOf(kws[i]) !== -1) return cat;
        }
    }
    return null;
}

function setupAISuggestion() {
    var descInput   = document.getElementById('descricao');
    var aiPanel     = document.getElementById('aiPanel');
    var aiText      = document.getElementById('aiSuggestionText');
    var aiAcceptBtn = document.getElementById('aiAccept');
    var catSelect   = document.getElementById('categoria');
    if (!descInput || !aiPanel) return;

    var timer;
    descInput.addEventListener('input', function () {
        clearTimeout(timer);
        var val = this.value.trim();
        if (val.length < 4) { aiPanel.classList.remove('visible'); return; }
        timer = setTimeout(function () {
            var s = suggestCategory(val);
            if (s) {
                aiText.innerHTML = 'Categoria sugerida: <strong>' + s + '</strong>';
                aiPanel.classList.add('visible');
            } else {
                aiPanel.classList.remove('visible');
            }
        }, 450);
    });

    if (aiAcceptBtn && catSelect) {
        aiAcceptBtn.addEventListener('click', function () {
            var s = suggestCategory(descInput.value.trim());
            if (s) { catSelect.value = s; aiPanel.classList.remove('visible'); showToast('Sugestão aceita: ' + s); }
        });
    }
}

/* ── Form validation ─────────────────────────────────────── */
function setupFormValidation() {
    var form = document.getElementById('itemForm');
    if (!form) return;
    form.addEventListener('submit', function (e) {
        var valid = true;
        ['descricao','categoria','localEncontrado','dataEncontrado'].forEach(function (name) {
            var el = document.getElementById(name);
            if (!el) return;
            if (!el.value || !el.value.trim()) { markError(el, 'Campo obrigatório.'); valid = false; }
            else clearError(el);
        });
        var dateEl = document.getElementById('dataEncontrado');
        if (dateEl && dateEl.value) {
            var p = dateEl.value.split('-');
            var sel = new Date(+p[0], +p[1]-1, +p[2]);
            var today = new Date(); today.setHours(23,59,59,999);
            if (sel > today) { markError(dateEl, 'A data não pode ser no futuro.'); valid = false; }
        }
        if (!valid) e.preventDefault();
    });
}

function markError(el, msg) {
    el.classList.add('error');
    var ex = el.parentNode.querySelector('.field-error');
    if (!ex) {
        ex = document.createElement('span');
        ex.className = 'field-error';
        el.insertAdjacentElement('afterend', ex);
    }
    ex.textContent = msg;
}
function clearError(el) {
    el.classList.remove('error');
    var h = el.parentNode.querySelector('.field-error');
    if (h) h.remove();
}

/* ── Auto-dismiss alerts ─────────────────────────────────── */
function setupAlertAutoDismiss() {
    document.querySelectorAll('.alert').forEach(function (a) {
        setTimeout(function () {
            a.style.transition = 'opacity .4s';
            a.style.opacity = '0';
            setTimeout(function () { a.remove(); }, 450);
        }, 5000);
    });
}

/* ── Toast ───────────────────────────────────────────────── */
function showToast(message, isError) {
    var t = document.createElement('div');
    t.className = 'alert ' + (isError ? 'alert-error' : 'alert-success');
    t.style.cssText = 'position:fixed;bottom:24px;right:24px;z-index:9999;min-width:220px;animation:fadeIn .25s ease;box-shadow:0 8px 30px rgba(0,0,0,.15)';
    t.textContent = message;
    document.body.appendChild(t);
    setTimeout(function () {
        t.style.transition = 'opacity .4s'; t.style.opacity = '0';
        setTimeout(function () { t.remove(); }, 450);
    }, 3000);
}
