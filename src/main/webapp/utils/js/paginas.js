/* Busca, filtro por status, ordenação, paginação, exportação e ações dos cards */
(function () {
    const norm = s => s.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');

    /* --- popup de requisição: adicionar / remover linhas de produto --- */
    const linhas = document.getElementById('linhas-produtos');
    if (linhas) {
        document.getElementById('adicionar-produto').addEventListener('click', () => {
            const nova = linhas.firstElementChild.cloneNode(true);
            nova.querySelectorAll('input').forEach(i => (i.value = ''));
            linhas.appendChild(nova);
        });
        linhas.addEventListener('click', e => {
            if (e.target.closest('[data-remover]') && linhas.children.length > 1) {
                e.target.closest('.linha-produto').remove();
            }
        });
    }

    const lista = document.querySelector('.lista-cartoes');
    if (!lista) return;

    const POR_PAGINA = 6;
    const cards = [...lista.querySelectorAll('.cartao')];
    const busca = document.querySelector('.busca');
    const ordem = document.querySelector('.ordenacao');
    const chips = [...document.querySelectorAll('.chip')];
    const btnAnt = document.getElementById('pagina-anterior');
    const btnProx = document.getElementById('pagina-proxima');
    const numero = document.getElementById('pagina-numero');
    const texto = document.getElementById('paginacao-texto');
    const unidade = lista.dataset.unidade || 'itens';

    const vazio = document.createElement('p');
    vazio.className = 'vazio';
    vazio.textContent = 'Nenhum resultado encontrado.';
    vazio.hidden = true;
    lista.appendChild(vazio);

    let filtro = 'todos';
    let pagina = 1;

    const nomeDe = c => c.querySelector('.cartao-nome').textContent.trim();

    function filtrados() {
        const q = norm(busca.value.trim());
        const r = cards.filter(c =>
            norm(c.textContent).includes(q) && (filtro === 'todos' || c.dataset.status === filtro));
        const modo = ordem.value;
        r.sort((a, b) => {
            if (modo === 'az') return nomeDe(a).localeCompare(nomeDe(b), 'pt-BR');
            if (modo === 'za') return nomeDe(b).localeCompare(nomeDe(a), 'pt-BR');
            if (modo === 'data-asc') return (a.dataset.data || '').localeCompare(b.dataset.data || '');
            if (modo === 'data-desc') return (b.dataset.data || '').localeCompare(a.dataset.data || '');
            return cards.indexOf(a) - cards.indexOf(b);
        });
        return r;
    }

    function contadores() {
        document.querySelectorAll('[data-contador]').forEach(el => {
            const k = el.dataset.contador;
            el.textContent = k === 'total' ? cards.length : cards.filter(c => c.dataset.status === k).length;
        });
    }

    function render() {
        const r = filtrados();
        const total = Math.max(1, Math.ceil(r.length / POR_PAGINA));
        pagina = Math.min(pagina, total);
        const ini = (pagina - 1) * POR_PAGINA;

        cards.forEach(c => (c.hidden = true));
        r.forEach((c, i) => {
            lista.insertBefore(c, vazio);
            c.hidden = !(i >= ini && i < ini + POR_PAGINA);
        });

        const mostrando = Math.max(0, Math.min(POR_PAGINA, r.length - ini));
        vazio.hidden = r.length > 0;
        numero.textContent = pagina;
        btnAnt.disabled = pagina <= 1;
        btnProx.disabled = pagina >= total;
        texto.textContent = `Mostrando ${mostrando} de ${r.length} ${unidade}`;
        contadores();
    }

    busca.addEventListener('input', () => { pagina = 1; render(); });
    ordem.addEventListener('change', () => { pagina = 1; render(); });
    chips.forEach(ch => ch.addEventListener('click', () => {
        chips.forEach(x => x.classList.remove('ativo'));
        ch.classList.add('ativo');
        filtro = ch.dataset.filtro;
        pagina = 1;
        render();
    }));
    btnAnt.addEventListener('click', () => { pagina--; render(); });
    btnProx.addEventListener('click', () => { pagina++; render(); });

    /* --- ações nos cards: aprovar / recusar / marcar como recebido --- */
    lista.addEventListener('click', e => {
        const b = e.target.closest('[data-acao]');
        if (!b) return;
        const card = b.closest('.cartao');
        const acao = b.dataset.acao;
        const selo = card.querySelector('.selo-status');

        if (acao === 'aprovada' || acao === 'recusada') {
            card.dataset.status = acao;
            selo.className = 'selo-status' + (acao === 'recusada' ? ' selo-inativo' : '');
            selo.textContent = acao === 'aprovada' ? 'Aprovada' : 'Recusada';
            card.querySelector('.cartao-acoes').remove();
        }
        if (acao === 'recebido') {
            card.dataset.status = 'recebido';
            selo.className = 'selo-status';
            selo.textContent = 'Recebido';
            b.disabled = true;
            b.lastChild.textContent = 'Recebido';
        }
        render();
    });

    /* --- exportar (CSV aberto pelo Excel) --- */
    document.getElementById('botao-exportar').addEventListener('click', () => {
        const linhasCsv = filtrados().map(c => {
            const cols = [nomeDe(c), c.querySelector('.cartao-cnpj').textContent.trim(),
                c.querySelector('.selo-status').textContent.trim(),
                ...[...c.querySelectorAll('.cartao-itens li')].map(li =>
                    [...li.children].map(s => s.textContent.trim()).join(': '))];
            return cols.map(v => `"${v.replace(/"/g, '""')}"`).join(';');
        });
        const blob = new Blob(['\ufeff' + linhasCsv.join('\r\n')], { type: 'text/csv;charset=utf-8' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = document.title.toLowerCase().replace(/\s+/g, '-') + '.csv';
        a.click();
        URL.revokeObjectURL(a.href);
    });

    render();
})();