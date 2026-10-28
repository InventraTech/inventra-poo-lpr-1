<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Lotes | Inventra</title>
  <link rel="icon" href="${ctx}/utils/imagens/imagotipo.png">
  <link rel="stylesheet" href="${ctx}/utils/css/base.css">
  <link rel="stylesheet" href="${ctx}/utils/css/fornecedores.css">
  <link rel="stylesheet" href="${ctx}/utils/css/lote.css">
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
  <fmt:setLocale value="pt_BR"/>
  <%-- Atributos enviados pelo LoteServlet: lotes, paginaAtual, totalPaginas, filtroSelecionado.
       Propriedades do LoteMolde (getters): id_lote, produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_compra, nota_fiscal. --%>

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
          <h1>Lotes</h1>
          <p>Validade, quantidade e status de cada lote</p>
        </div>
      </div>
      <div class="acoes-pagina">
        <a class="botao" href="#modal-lote">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>Novo lote
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
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 2 2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Lotes cadastrados</span>
          <strong class="bignumero-valor" title="Quantidade Lostes">${totalRegistros}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Vencendo em 30 dias</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="m4.9 4.9 14.2 14.2"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Vencidos</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
    </section>

    <section class="filtros" aria-label="Controles da lista">
      <input type="search" class="busca" placeholder="Buscar por lote, produto ou nota fiscal..." aria-label="Buscar nesta página">

      <form action="${ctx}/lote" method="get" class="form-filtro">
        <input type="hidden" name="pagina" value="1">
        <select class="ordenacao" name="filtro" aria-label="Ordenar" onchange="this.form.submit()">
          <option value="" ${empty filtroSelecionado ? 'selected' : ''}>Ordenar por ID</option>
          <option value="entrada_crescente" ${filtroSelecionado eq 'entrada_crescente' ? 'selected' : ''}>Entrada: mais antiga</option>
          <option value="entrada_decrescente" ${filtroSelecionado eq 'entrada_decrescente' ? 'selected' : ''}>Entrada: mais recente</option>
          <option value="validade_crescente" ${filtroSelecionado eq 'validade_crescente' ? 'selected' : ''}>Validade (mais próxima)</option>
          <option value="validade_decrescente" ${filtroSelecionado eq 'validade_decrescente' ? 'selected' : ''}>Validade (mais distante)</option>
          <option value="produtos_atual" ${filtroSelecionado eq 'produtos_atual' ? 'selected' : ''}>Quantidade atual</option>
        </select>
      </form>

      <form action="${ctx}/lote" method="get" class="form-exportar">
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

    <section class="lista-cartoes" aria-label="Lista de lotes">
      <c:forEach var="l" items="${requestScope.lotes}">
        <article class="cartao"
                 data-id="${l.id_lote}"
                 data-rotulo="${fn:escapeXml(l.numero_lote)}"
                 data-numero="${fn:escapeXml(l.numero_lote)}"
                 data-qtdinicial="${l.qtd_inicial}"
                 data-qtdatual="${l.qtd_atual}"
                 data-entrada="${l.dt_entrada}"
                 data-validade="${l.dt_validade}"
                 data-valor="${l.valor_compra}"
                 data-nota="${fn:escapeXml(l.nota_fiscal)}">
          <header class="cartao-topo">
            <div class="cartao-logo">L<c:out value="${l.id_lote}"/></div>
            <div class="cartao-identificacao">
              <h2 class="cartao-nome">Lote <c:out value="${l.numero_lote}"/></h2>
              <p class="cartao-cnpj"><c:out value="${l.produto}"/></p>
            </div>
            <%-- O status (vigente / vencendo / vencido) é calculado pelo script a partir de data-validade. --%>
            <span class="selo-status">—</span>
          </header>

          <ul class="cartao-itens">
            <li><span>Quantidade inicial</span><span><c:out value="${l.qtd_inicial}"/> un</span></li>
            <li><span>Quantidade atual</span><span><c:out value="${l.qtd_atual}"/> un</span></li>
            <li><span>Validade</span><span><c:if test="${not empty l.dt_validade}">${fn:substring(l.dt_validade, 8, 10)}/${fn:substring(l.dt_validade, 5, 7)}/${fn:substring(l.dt_validade, 0, 4)}</c:if></span></li>
            <li><span>Valor de compra</span><span><fmt:formatNumber value="${l.valor_compra}" type="currency"/></span></li>
          </ul>

          <footer class="cartao-rodape">
            <div class="cartao-info">
              <p class="cartao-responsavel">Nota fiscal: <c:out value="${l.nota_fiscal}"/></p>
              <p class="cartao-produtos">Entrada em <c:if test="${not empty l.dt_entrada}">${fn:substring(l.dt_entrada, 8, 10)}/${fn:substring(l.dt_entrada, 5, 7)}/${fn:substring(l.dt_entrada, 0, 4)}</c:if></p>
            </div>
            <div class="cartao-acoes">
              <button type="button" class="botao-cartao botao-cartao-editar" aria-label="Editar" title="Editar"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/><path d="M14 7l3 3"/></svg></button>
              <form action="${ctx}/lote" method="post" class="form-excluir">
                <input type="hidden" name="acao" value="excluir">
                <input type="hidden" name="id" value="${l.id_lote}">
                <button type="submit" class="botao-cartao botao-cartao-excluir" aria-label="Excluir" title="Excluir"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/></svg></button>
              </form>
            </div>
          </footer>
        </article>
      </c:forEach>
      <c:if test="${empty lotes}">
        <p class="vazio"><strong>Nenhum lote nesta página</strong><br>Use a paginação ou cadastre um lote.</p>
      </c:if>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <form action="${ctx}/lote" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual - 1}">
          <button type="submit" class="botao-pagina" aria-label="Página anterior" ${paginaAtual le 1 ? 'disabled' : ''}>❮</button>
        </form>
        <span class="paginacao-atual" aria-current="page"><c:out value="${paginaAtual}"/></span>
        <form action="${ctx}/lote" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual + 1}">
          <button type="submit" class="botao-pagina" aria-label="Próxima página" ${paginaAtual ge totalPaginas ? 'disabled' : ''}>❯</button>
        </form>
      </div>
      <p class="paginacao-texto">Mostrando ${fn:length(lotes)} de ${totalRegistros} registros - Página ${paginaAtual} de ${totalPaginas}</p>
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
        <li><a href="${ctx}/lote" class="menu-item ativo" aria-current="page">Lotes</a></li>
        <li><a href="${ctx}/itemprelista" class="menu-item">Pré Listas</a></li>
        <li><a href="${ctx}/produto" class="menu-item">Produtos</a></li>
        <li><a href="${ctx}/requisicao" class="menu-item">Requisições</a></li>
      </ul>

      <a href="${ctx}/logout" class="menu-saida">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        Saída
      </a>
    </nav>
  </div>

  <div class="modal" id="modal-lote" role="dialog" aria-modal="true" aria-labelledby="modal-lote-titulo">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/lote" method="post" class="formulario">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="modal-lote-titulo">Novo Lote</h2>
      <input type="hidden" name="acao" value="cadastrar">

      <div class="linha-campos">
        <label class="campo" for="lote-numero">Número do Lote
          <input type="text" name="numero_lote" id="lote-numero" placeholder="Ex: L-2026-007" required>
        </label>
        <label class="campo" for="lote-produto">ID Produto (fk_produto)
          <input type="number" name="fk_produto" id="lote-produto" placeholder="Ex: 1" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="lote-qtd-inicial">Quantidade Inicial
          <input type="number" name="qtd_inicial" id="lote-qtd-inicial" placeholder="Ex: 50" min="0" required>
        </label>
        <label class="campo" for="lote-qtd-atual">Quantidade Atual
          <input type="number" name="qtd_atual" id="lote-qtd-atual" placeholder="Ex: 50" min="0" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="lote-entrada">Data de Entrada
          <input type="date" name="dt_entrada" id="lote-entrada" required>
        </label>
        <label class="campo" for="lote-validade">Data de Validade
          <input type="date" name="dt_validade" id="lote-validade" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="lote-valor">Valor de Compra (R$)
          <input type="text" name="valor_compra" id="lote-valor" placeholder="Ex: 150.00" required>
        </label>
        <label class="campo" for="lote-nota">Nota Fiscal
          <input type="text" name="nota_fiscal" id="lote-nota" placeholder="Ex: NF-98421" required>
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
    <form action="${ctx}/lote" method="post" class="formulario" id="form-edicao">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-edicao">Editar Lote</h2>
      <%-- A consulta da listagem (JOIN) não devolve os IDs das chaves estrangeiras: esses campos exigem preenchimento manual. --%>
      <p>Preencha o ID do produto para salvar a edição completa.</p>

      <input type="hidden" name="acao" value="atualizar_completo">
      <input type="hidden" name="id_lote" id="edicao-id">

      <div class="linha-campos">
        <label class="campo" for="edicao-numero">Número do Lote
          <input type="text" name="numero_lote" id="edicao-numero" required>
        </label>
        <label class="campo" for="edicao-produto">ID Produto (fk_produto)
          <input type="number" name="fk_produto" id="edicao-produto" placeholder="Informe o ID" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-qtdinicial">Quantidade Inicial
          <input type="number" name="qtd_inicial" id="edicao-qtdinicial" min="0" required>
        </label>
        <label class="campo" for="edicao-qtdatual">Quantidade Atual
          <input type="number" name="qtd_atual" id="edicao-qtdatual" min="0" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-entrada">Data de Entrada
          <input type="date" name="dt_entrada" id="edicao-entrada" required>
        </label>
        <label class="campo" for="edicao-validade">Data de Validade
          <input type="date" name="dt_validade" id="edicao-validade" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-valor">Valor de Compra (R$)
          <input type="text" name="valor_compra" id="edicao-valor" placeholder="Ex: 150.00" required>
        </label>
        <label class="campo" for="edicao-nota">Nota Fiscal
          <input type="text" name="nota_fiscal" id="edicao-nota" required>
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
    <form action="${ctx}/lote" method="post" class="formulario" id="form-coluna">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-coluna">Editar campo</h2>
      <input type="hidden" name="acao" value="atualizar_coluna">
      <input type="hidden" name="id" id="coluna-id">

      <label class="campo" for="coluna">Campo
        <select name="coluna" id="coluna">
          <option value="fk_produto">ID do Produto</option>
          <option value="numero_lote">Número do Lote</option>
          <option value="qtd_inicial">Quantidade Inicial</option>
          <option value="qtd_atual">Quantidade Atual</option>
          <option value="dt_entrada">Data de Entrada</option>
          <option value="dt_validade">Data de Validade</option>
          <option value="valor_compra">Valor de Compra</option>
          <option value="nota_fiscal">Nota Fiscal</option>
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
      const propriedades = ["numero", "qtdinicial", "qtdatual", "entrada", "validade", "valor", "nota"];

      // ---- Status do lote (a partir da validade) ----
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);
      const rotulos = { vigente: 'Vigente', vencendo: 'Vencendo', vencido: 'Vencido' };
      itens.forEach(cartao => {
        let status = 'vigente';
        if (cartao.dataset.validade) {
          const dias = Math.round((new Date(cartao.dataset.validade + 'T00:00:00') - hoje) / 86400000);
          status = dias < 0 ? 'vencido' : dias <= 30 ? 'vencendo' : 'vigente';
        }
        cartao.dataset.status = status;
        const selo = cartao.querySelector('.selo-status');
        selo.textContent = rotulos[status];
        selo.className = 'selo-status' + (status === 'vencendo' ? ' selo-pendente' : status === 'vencido' ? ' selo-inativo' : '');
      });

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
        "fk_produto": [
                null,
                "number",
                "Informe o ID"
        ],
        "numero_lote": [
                "numero",
                "text",
                ""
        ],
        "qtd_inicial": [
                "qtdinicial",
                "number",
                ""
        ],
        "qtd_atual": [
                "qtdatual",
                "number",
                ""
        ],
        "dt_entrada": [
                "entrada",
                "date",
                ""
        ],
        "dt_validade": [
                "validade",
                "date",
                ""
        ],
        "valor_compra": [
                "valor",
                "text",
                "Use ponto: 150.00"
        ],
        "nota_fiscal": [
                "nota",
                "text",
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
