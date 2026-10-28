<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Pré-listas | Inventra</title>
  <link rel="icon" href="${ctx}/utils/imagens/imagotipo.png">
  <link rel="stylesheet" href="${ctx}/utils/css/base.css">
  <link rel="stylesheet" href="${ctx}/utils/css/fornecedores.css">
  <link rel="stylesheet" href="${ctx}/utils/css/preLista.css">
  <style>
    .form-filtro { display: flex; gap: .5rem; }
    .form-exportar { display: flex; }
    .acoes-formulario { display: flex; gap: .65rem; justify-content: flex-end; flex-wrap: wrap; }
    .mensagem-erro { padding: 1rem; margin-bottom: 1rem; color: #842029; background: #fff0f1; border-radius: 12px; }
    .filtros { flex-wrap: wrap; }
    .cartao-acoes { flex-wrap: wrap; }
    .cartao-acoes form { margin: 0; display: inline-flex; }
    .vazio { grid-column: 1 / -1; padding: 2.5rem 1rem; text-align: center; color: #666; }
  </style>
</head>
<body>
  <%-- Atributos enviados pelo ItemPreListaServlet: itensprelista, paginaAtual, totalPaginas, filtroSelecionado.
       Propriedades do ItemPreListaMolde (getters): id_item_prelista, fornecedor, produto, qtd.
       ATENÇÃO: o servlet lê os parâmetros "fk_prduto" (sem o "o") e, na edição completa, o ID do item em "id_fornecedor". --%>

  <header class="barra-topo">
    <a href="#modal-menu" class="botao-menu" aria-label="Abrir menu">☰</a>
    <div class="usuario">
      <span>Nelson Junior</span>
      <div class="avatar">NJ</div>
    </div>
  </header>

  <div class="conteudo">

    <section class="cabecalho-pagina">
      <div class="titulo-com-logo">
        <img src="${ctx}/utils/imagens/imagotipo.png" alt="Logo da empresa" class="logo-pagina">
        <div class="titulo-pagina">
          <h1>Pré-lista</h1>
          <p>Sugestões de reposição para produtos com estoque baixo</p>
        </div>
      </div>
      <div class="acoes-pagina">
        <a class="botao" href="#modal-prelista">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>Adicionar produto
        </a>
      </div>
    </section>

    <c:if test="${not empty sessionScope.mensagemErro}">
      <div class="mensagem-erro" role="alert">
        <c:out value="${sessionScope.mensagemErro}"/>
      </div>
      <c:remove var="mensagemErro" scope="session"/>
    </c:if>

    <section class="bignumeros">
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2 2h2l2.7 12.4a2 2 0 0 0 2 1.6h9.8a2 2 0 0 0 2-1.6L22 7H5"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Produtos para repor</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2"/><path d="M15 18H9"/><path d="M19 18h2a1 1 0 0 0 1-1v-3.7a1 1 0 0 0-.2-.6l-3.5-4.4A1 1 0 0 0 17.5 8H14"/><circle cx="17" cy="18" r="2"/><circle cx="7" cy="18" r="2"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Fornecedores</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Custo estimado</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
    </section>

    <section class="filtros" aria-label="Controles da lista">
      <input type="search" class="busca" placeholder="Buscar por produto ou fornecedor..." aria-label="Buscar nesta página">

      <form action="${ctx}/itemprelista" method="get" class="form-filtro">
        <input type="hidden" name="pagina" value="1">
        <select class="ordenacao" name="filtro" aria-label="Ordenar" onchange="this.form.submit()">
          <option value="" ${empty filtroSelecionado ? 'selected' : ''}>Ordenar por ID</option>
          <option value="fornecedor" ${filtroSelecionado eq 'fornecedor' ? 'selected' : ''}>Fornecedor</option>
          <option value="produto" ${filtroSelecionado eq 'produto' ? 'selected' : ''}>Produto (A - Z)</option>
          <option value="quantidade_crescente" ${filtroSelecionado eq 'quantidade_crescente' ? 'selected' : ''}>Quantidade (menor primeiro)</option>
          <option value="quantidade_decrescente" ${filtroSelecionado eq 'quantidade_decrescente' ? 'selected' : ''}>Quantidade (maior primeiro)</option>
        </select>
      </form>

      <form action="${ctx}/itemprelista" method="get" class="form-exportar">
        <input type="hidden" name="acao" value="exportar">
        <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
        <button class="botao botao-exportar" type="submit" title="Baixar CSV compatível com Excel">
          <svg class="icone-exportar" viewBox="0 0 24 24" aria-hidden="true">
            <rect x="8" y="3" width="14" height="18" rx="1.5" fill="#fff"/>
            <path d="M12 8h7M12 12h7M12 16h7" stroke="#107c41" stroke-width="1.4"/>
            <rect x="2" y="6.5" width="11" height="11" rx="1.5" fill="#107c41" stroke="#fff"/>
            <path d="M5 9.5l5 5M10 9.5l-5 5" stroke="#fff" stroke-width="1.4"/>
          </svg>
          Exportar (Excel)
        </button>
      </form>
    </section>

    <section class="lista-cartoes" aria-label="Itens da pré-lista">
      <c:forEach var="i" items="${requestScope.itensprelista}">
        <article class="cartao"
                 data-id="${i.id_item_prelista}"
                 data-rotulo="${fn:escapeXml(i.produto)}"
                 data-qtd="${i.qtd}">
          <header class="cartao-topo">
            <div class="cartao-logo"><c:out value="${fn:toUpperCase(fn:substring(i.fornecedor, 0, 2))}"/></div>
            <div class="cartao-identificacao">
              <h2 class="cartao-nome"><c:out value="${i.fornecedor}"/></h2>
              <p class="cartao-cnpj">Item #<c:out value="${i.id_item_prelista}"/></p>
            </div>
          </header>

          <ul class="cartao-itens">
            <li><span>Produto</span><span><c:out value="${i.produto}"/></span></li>
            <li><span>Quantidade a repor</span><span><c:out value="${i.qtd}"/> un</span></li>
          </ul>

          <footer class="cartao-rodape">
            <div class="cartao-info">
              <p class="cartao-responsavel">Fornecedor</p>
              <p class="cartao-produtos"><c:out value="${i.fornecedor}"/></p>
            </div>
            <div class="cartao-acoes">
              <button type="button" class="botao-cartao botao-cartao-editar" aria-label="Editar" title="Editar"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/><path d="M14 7l3 3"/></svg></button>
              <form action="${ctx}/itemprelista" method="post" class="form-excluir">
                <input type="hidden" name="acao" value="excluir">
                <input type="hidden" name="id" value="${i.id_item_prelista}">
                <button type="submit" class="botao-cartao botao-cartao-excluir" aria-label="Excluir" title="Excluir"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/></svg></button>
              </form>
            </div>
          </footer>
        </article>
      </c:forEach>
      <c:if test="${empty itensprelista}">
        <p class="vazio"><strong>Nenhum item nesta página</strong><br>Use a paginação ou adicione um produto à pré-lista.</p>
      </c:if>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <form action="${ctx}/itemprelista" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual - 1}">
          <button type="submit" class="botao-pagina" aria-label="Página anterior" ${paginaAtual le 1 ? 'disabled' : ''}>❮</button>
        </form>
        <span class="paginacao-atual" aria-current="page"><c:out value="${paginaAtual}"/></span>
        <form action="${ctx}/itemprelista" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual + 1}">
          <button type="submit" class="botao-pagina" aria-label="Próxima página" ${paginaAtual ge totalPaginas ? 'disabled' : ''}>❯</button>
        </form>
      </div>
      <p class="paginacao-texto">Mostrando ${fn:length(itensprelista)} de ${totalRegistros} registros - Página ${paginaAtual} de ${totalPaginas}</p>
    </nav>
  </div>

  <div class="menu-lateral" id="modal-menu">
    <a href="#" class="menu-fundo" aria-label="Fechar menu"></a>
    <nav class="menu-painel" aria-label="Menu principal">
      <div class="menu-marca">
        <img src="${ctx}/utils/imagens/imagotipo.png" alt="" class="menu-logo">
        <span class="menu-nome">Inventra</span>
      </div>
      <a href="#" class="menu-fechar" aria-label="Fechar menu">☰</a>
      <ul class="menu-itens">
        <li><a href="${ctx}/funcionario" class="menu-item">Funcionários</a></li>
        <li><a href="${ctx}/fornecedor" class="menu-item">Fornecedores</a></li>
        <li><a href="${ctx}/lote" class="menu-item">Lotes</a></li>
        <li><a href="${ctx}/itemprelista" class="menu-item ativo" aria-current="page">Pré Listas</a></li>
        <li><a href="${ctx}/produto" class="menu-item">Produtos</a></li>
        <li><a href="${ctx}/requisicao" class="menu-item">Requisições</a></li>
      </ul>

      <a href="${ctx}/logout" class="menu-saida">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        Saída
      </a>
    </nav>
  </div>

  <div class="modal" id="modal-prelista" role="dialog" aria-modal="true" aria-labelledby="modal-prelista-titulo">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/itemprelista" method="post" class="formulario">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="modal-prelista-titulo">Adicionar à Pré-lista</h2>
      <input type="hidden" name="acao" value="cadastrar">

      <div class="linha-campos">
        <label class="campo" for="item-prelista">ID da Pré-lista (fk_pre_lista)
          <input type="number" name="fk_pre_lista" id="item-prelista" placeholder="Ex: 1" min="1" required>
        </label>
        <label class="campo" for="item-fornecedor">ID do Fornecedor (fk_fornecedor)
          <input type="number" name="fk_fornecedor" id="item-fornecedor" placeholder="Ex: 1" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="item-produto">ID do Produto (fk_produto)
          <input type="number" name="fk_prduto" id="item-produto" placeholder="Ex: 1" min="1" required>
        </label>
        <label class="campo" for="item-quantidade">Quantidade a repor
          <input type="number" name="quantidade" id="item-quantidade" placeholder="0" min="1" required>
        </label>
      </div>

      <div class="acoes-formulario">
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <div class="modal" id="modal-edicao" role="dialog" aria-modal="true" aria-labelledby="titulo-edicao">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/itemprelista" method="post" class="formulario" id="form-edicao">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-edicao">Editar Item da Pré-lista</h2>
      <%-- A consulta da listagem (JOIN) não devolve os IDs das chaves estrangeiras: esses campos exigem preenchimento manual. --%>
      <p>Preencha os IDs de pré-lista, fornecedor e produto para salvar a edição completa.</p>

      <input type="hidden" name="acao" value="atualizar_completo">
      <input type="hidden" name="id_fornecedor" id="edicao-id">

      <div class="linha-campos">
        <label class="campo" for="edicao-prelista">ID da Pré-lista (fk_pre_lista)
          <input type="number" name="fk_pre_lista" id="edicao-prelista" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-fornecedor">ID do Fornecedor (fk_fornecedor)
          <input type="number" name="fk_fornecedor" id="edicao-fornecedor" placeholder="Informe o ID" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-produto">ID do Produto (fk_produto)
          <input type="number" name="fk_prduto" id="edicao-produto" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-qtd">Quantidade a repor
          <input type="number" name="quantidade" id="edicao-qtd" min="1" required>
        </label>
      </div>

      <div class="acoes-formulario">
        <button type="button" class="botao-cancelar" id="editar-um-campo">Editar somente um campo</button>
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <div class="modal" id="modal-coluna" role="dialog" aria-modal="true" aria-labelledby="titulo-coluna">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/itemprelista" method="post" class="formulario" id="form-coluna">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-coluna">Editar campo</h2>
      <input type="hidden" name="acao" value="atualizar_coluna">
      <input type="hidden" name="id" id="coluna-id">

      <label class="campo" for="coluna">Campo
        <select name="coluna" id="coluna">
          <option value="fk_pre_lista">ID da Pré-lista</option>
          <option value="fk_fornecedor">ID do Fornecedor</option>
          <option value="fk_produto">ID do Produto</option>
          <option value="qtd">Quantidade</option>
        </select>
      </label>

      <label class="campo" for="novoValor">Novo valor
        <input type="text" id="novoValor" name="novoValor" required>
      </label>

      <div class="acoes-formulario">
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <script>
    (() => {
      'use strict';
      // Paginação, ordenação e exportação (CSV) são feitas no servidor; a busca filtra a página atual.
      const lista = document.querySelector('.lista-cartoes');
      const itens = [...document.querySelectorAll('.lista-cartoes [data-id]')];
      let selecionado = null;
      let retorno = null;
      const propriedades = ["qtd"];

      // ---- Busca (sem diferenciar maiúsculas nem acentos) ----
      const busca = document.querySelector('.busca');
      const normalizar = texto => (texto || '').toLowerCase().normalize('NFD').replace(/\p{M}/gu, '');
      const textoDe = cartao => [...cartao.querySelectorAll('.cartao-topo, .cartao-itens, .cartao-info')].map(el => el.textContent).join(' ');
      const vazio = document.createElement('p');
      vazio.className = 'vazio';
      vazio.hidden = true;
      vazio.textContent = 'Nenhum resultado encontrado.';
      lista.appendChild(vazio);
      let statusAtivo = 'todos';
      function filtrar() {
        const termos = normalizar(busca.value.trim()).split(/\s+/).filter(Boolean);
        let visiveis = 0;
        itens.forEach(item => {
          const texto = normalizar(textoDe(item));
          const combina = termos.every(termo => texto.includes(termo)) &&
            (statusAtivo === 'todos' || item.dataset.status === statusAtivo);
          item.hidden = !combina;
          if (combina) visiveis++;
        });
        vazio.hidden = visiveis > 0 || itens.length === 0;
      }
      busca.addEventListener('input', filtrar);
      busca.addEventListener('search', filtrar); // botão "x" do campo type="search"
      document.querySelectorAll('.chip').forEach(chip => chip.addEventListener('click', () => {
        document.querySelectorAll('.chip').forEach(outro => outro.classList.remove('ativo'));
        chip.classList.add('ativo');
        statusAtivo = chip.dataset.filtro;
        filtrar();
      }));

      // ---- Editar (formulário completo) ----
      lista.addEventListener('click', evento => {
        const botao = evento.target.closest('.botao-cartao-editar');
        if (!botao) return;
        selecionado = { ...botao.closest('[data-id]').dataset };
        retorno = botao;
        document.getElementById('form-edicao').reset();
        document.getElementById('edicao-id').value = selecionado.id;
        propriedades.forEach(chave => {
          document.getElementById('edicao-' + chave).value = selecionado[chave] ?? '';
        });
        location.hash = 'modal-edicao';
      });

      // ---- Excluir (confirmação antes do POST) ----
      document.querySelectorAll('.form-excluir').forEach(form => {
        form.addEventListener('submit', evento => {
          const rotulo = form.closest('[data-id]').dataset.rotulo;
          if (!confirm('Deseja excluir "' + rotulo + '"?')) evento.preventDefault();
        });
      });

      // ---- Editar somente um campo (acao=atualizar_coluna) ----
      // coluna do banco: [chave do data-*, tipo do input, dica]
      const campos = {
        "fk_pre_lista": [
                null,
                "number",
                "Informe o ID"
        ],
        "fk_fornecedor": [
                null,
                "number",
                "Informe o ID"
        ],
        "fk_produto": [
                null,
                "number",
                "Informe o ID"
        ],
        "qtd": [
                "qtd",
                "number",
                ""
        ]
      };
      const coluna = document.getElementById('coluna');
      const novoValor = document.getElementById('novoValor');
      function preencherCampo() {
        if (!selecionado) return;
        const [chave, tipo, dica] = campos[coluna.value];
        novoValor.type = tipo;
        novoValor.placeholder = dica || '';
        novoValor.value = (chave && selecionado[chave]) || '';
        if (coluna.value.startsWith('fk_')) novoValor.min = '1';
        else novoValor.removeAttribute('min');
      }
      document.getElementById('editar-um-campo').addEventListener('click', () => {
        if (!selecionado) return;
        document.getElementById('form-coluna').reset();
        document.getElementById('coluna-id').value = selecionado.id;
        preencherCampo();
        location.hash = 'modal-coluna';
      });
      coluna.addEventListener('change', preencherCampo);

      // ---- Modais (:target) e foco, igual à tela de produtos ----
      document.querySelectorAll('a[href^="#modal-"]').forEach(link => {
        link.addEventListener('click', () => { retorno = link; });
      });
      function modalAtual() {
        const el = document.getElementById(location.hash.slice(1));
        return el?.matches('.modal, .menu-lateral') ? el : null;
      }
      function foco() {
        const modal = modalAtual();
        document.querySelector('.conteudo').inert = Boolean(modal);
        document.querySelector('.barra-topo').inert = Boolean(modal);
        if (modal) modal.querySelector('.modal-fechar, .menu-fechar')?.focus();
        else if (retorno?.isConnected) { retorno.focus(); retorno = null; }
      }
      addEventListener('hashchange', foco);
      document.addEventListener('keydown', evento => {
        const modal = modalAtual();
        if (!modal) return;
        if (evento.key === 'Escape') { evento.preventDefault(); location.hash = ''; }
        if (evento.key !== 'Tab') return;
        const focaveis = [...modal.querySelectorAll('a[href], button, input, select, textarea')].filter(el =>
          !el.disabled && el.type !== 'hidden' && el.getClientRects().length &&
          !el.matches('.modal-fundo, .menu-fundo')
        );
        if (!focaveis.length) return;
        const primeiro = focaveis[0], ultimo = focaveis[focaveis.length - 1];
        if (evento.shiftKey && (document.activeElement === primeiro || !focaveis.includes(document.activeElement))) {
          evento.preventDefault(); ultimo.focus();
        } else if (!evento.shiftKey && (document.activeElement === ultimo || !focaveis.includes(document.activeElement))) {
          evento.preventDefault(); primeiro.focus();
        }
      });
      // Uma URL com hash de edição sozinha não identifica qual registro editar.
      if (['#modal-edicao', '#modal-coluna'].includes(location.hash)) location.hash = '';
      foco();
    })();
  </script>
</body>
</html>
