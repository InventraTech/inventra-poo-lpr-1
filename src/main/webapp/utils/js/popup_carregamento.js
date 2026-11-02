/*
 * Pop-up de carregamento do Inventra.
 *
 * Uso manual (landing): PopupCarregamento.mostrar() / PopupCarregamento.ocultar()
 *
 * Uso automático (páginas do CRUD): o <script> tem data-auto="true". Nesse caso o pop-up
 * aparece sozinho ao enviar um formulário ou clicar em um link que troca de página, e some
 * quando a nova página carrega. Cliques que não navegam (abrir modal, Editar, Cancelar) não mostram o pop-up.
 */
(function () {
    const script = document.currentScript;

    // Tempo máximo do pop-up. Protege contra travar a tela se a requisição nunca voltar.
    const TEMPO_LIMITE = 30000;

    // Formulários que não trocam de página (exportar CSV): o pop-up ficaria preso.
    const FORMULARIOS_SEM_POPUP = ['form-exportar'];

    // Carrega o CSS do pop-up a partir da pasta utils/css, relativa a este arquivo.
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = new URL('../css/style_popup.css', script.src).href;
    document.head.appendChild(link);

    // Monta o markup do pop-up no fim da página.
    const popup = document.createElement('div');
    popup.id = 'popup_carregamento';
    popup.setAttribute('role', 'status');
    popup.setAttribute('aria-live', 'polite');
    popup.innerHTML =
        '<div id="popup_caixa">' +
            '<p id="popup_texto">Carregando...</p>' +
            '<svg id="carregador" viewBox="0 0 240 240" aria-hidden="true">' +
                '<g id="hexagono">' +
                    '<path id="hexagono_tracos" d="M 34.7 70.75 L 120 21.5 L 205.3 70.75 L 205.3 169.25 L 120 218.5 L 34.7 169.25 Z"/>' +
                '</g>' +
            '</svg>' +
        '</div>';
    document.body.appendChild(popup);

    let limite = null;

    // Fecha qualquer modal aberto (os modais abrem pelo :target da URL, ex.: #modal-funcionario).
    // Assim o formulário some e só o pop-up fica na tela.
    function fecharModaisAbertos() {
        document.querySelectorAll('.modal:target').forEach(function (modal) {
            modal.style.display = 'none';
        });
        if (location.hash) {
            history.replaceState(null, '', location.pathname + location.search);
        }
    }

    function mostrar() {
        clearTimeout(limite);
        popup.classList.add('ativo');
        limite = setTimeout(ocultar, TEMPO_LIMITE);
    }

    function ocultar() {
        clearTimeout(limite);
        popup.classList.remove('ativo');
    }

    window.PopupCarregamento = { mostrar: mostrar, ocultar: ocultar };

    // Ao voltar pelo botão "voltar" do navegador, a página pode vir do cache com o pop-up aberto.
    window.addEventListener('pageshow', function (evento) {
        if (evento.persisted) ocultar();
    });

    // data-auto="true": envios de formulário + links que trocam de página (CRUDs).
    // data-auto="envios": só envios de formulário (tela de login, onde a seta "voltar" não deve mostrar o pop-up).
    const modo = script.dataset.auto;

    if (modo === 'true' || modo === 'envios') {
        // Envio de formulário: se algum script cancelou o envio (ex.: confirm() de exclusão), não mostra.
        document.addEventListener('submit', function (evento) {
            // Envio cancelado por algum script (ex.: confirm() de exclusão): não fecha nada e não mostra o pop-up.
            if (evento.defaultPrevented) return;
            fecharModaisAbertos();
            const form = evento.target;
            if (FORMULARIOS_SEM_POPUP.some(classe => form.classList.contains(classe))) return;
            mostrar();
        });
    }

    if (modo === 'true') {
        // Clique em link que troca de página. Ignora âncoras de modal (#...), links externos e cliques com tecla modificadora.
        document.addEventListener('click', function (evento) {
            if (evento.defaultPrevented || evento.button !== 0) return;
            if (evento.ctrlKey || evento.metaKey || evento.shiftKey || evento.altKey) return;
            const link = evento.target.closest('a[href]');
            if (!link) return;
            const destino = link.getAttribute('href');
            if (destino.startsWith('#') || destino.startsWith('javascript:') || link.target === '_blank') return;
            mostrar();
        });
    }
})();
