<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Requisições | Inventra</title>
  <link rel="icon" href="${ctx}/utils/imagens/imagotipo.png">
  <link rel="stylesheet" href="${ctx}/utils/css/base.css">
  <link rel="stylesheet" href="${ctx}/utils/css/fornecedores.css">
  <link rel="stylesheet" href="${ctx}/utils/css/requisicoes.css">
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
  <%-- Atributos enviados pelo RequisicaoServlet: requisicoes, paginaAtual, totalPaginas, filtroSelecionado.
       Propriedades do RequisicaoMolde (getters): idRequisicao, tipoRequisicao, funcionarioSolicitante, funcionarioAprovador,
       produto, quantidadeProduto, motivo, status, dataHora (LocalDateTime). --%>

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
          <h1>Requisições</h1>
          <p>Pedidos internos dos setores para retirada de itens</p>
        </div>
      </div>
      <div class="acoes-pagina">
        <a class="botao" href="#modal-requisicao">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>Nova requisição
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
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="8" y="2" width="8" height="4" rx="1"/><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><path d="M12 11h4M12 16h4M8 11h.01M8 16h.01"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Requisições</span>
          <strong class="bignumero-valor" title="Quantidade Requisições">${totalRegistros}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Pendentes</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Aprovadas</span>
          <strong class="bignumero-valor" title="Informação não disponível">—</strong>
        </div>
      </div>
    </section>

    <section class="filtros" aria-label="Controles da lista">
      <input type="search" class="busca" placeholder="Buscar por número, solicitante, produto ou motivo..." aria-label="Buscar nesta página">

      <form action="${ctx}/requisicao" method="get" class="form-filtro">
        <input type="hidden" name="pagina" value="1">
        <select class="ordenacao" name="filtro" aria-label="Ordenar" onchange="this.form.submit()">
          <option value="" ${empty filtroSelecionado ? 'selected' : ''}>Ordenar por número</option>
          <option value="tipo" ${filtroSelecionado eq 'tipo' ? 'selected' : ''}>Tipo de requisição</option>
          <option value="quantidade_crescente" ${filtroSelecionado eq 'quantidade_crescente' ? 'selected' : ''}>Quantidade (menor primeiro)</option>
          <option value="quantidade_decrescente" ${filtroSelecionado eq 'quantidade_decrescente' ? 'selected' : ''}>Quantidade (maior primeiro)</option>
          <option value="data_proxima" ${filtroSelecionado eq 'data_proxima' ? 'selected' : ''}>Data: mais antigas primeiro</option>
          <option value="data_distante" ${filtroSelecionado eq 'data_distante' ? 'selected' : ''}>Data: mais recentes primeiro</option>
        </select>
      </form>

      <form action="${ctx}/requisicao" method="get" class="form-exportar">
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

    <section class="lista-cartoes" aria-label="Lista de requisições">
      <c:forEach var="r" items="${requestScope.requisicoes}">
        <article class="cartao"
                 data-id="${r.idRequisicao}"
                 data-rotulo="Requisição #${r.idRequisicao}"
                 data-qtd="${r.quantidadeProduto}"
                 data-motivo="${fn:escapeXml(r.motivo)}"
                 data-status="${fn:escapeXml(r.status)}"
                 data-datahora="${fn:substring(r.dataHora, 0, 16)}">
          <header class="cartao-topo">
            <div class="cartao-logo"><c:out value="${r.idRequisicao}"/></div>
            <div class="cartao-identificacao">
              <h2 class="cartao-nome">Requisição #<c:out value="${r.idRequisicao}"/></h2>
              <p class="cartao-cnpj"><c:out value="${r.funcionarioSolicitante}"/> - <c:out value="${r.tipoRequisicao}"/> - <c:if test="${not empty r.dataHora}">${fn:substring(r.dataHora, 8, 10)}/${fn:substring(r.dataHora, 5, 7)}/${fn:substring(r.dataHora, 0, 4)}</c:if></p>
            </div>
            <span class="selo-status ${fn:toLowerCase(r.status) eq 'pendente' ? 'selo-pendente' : (fn:toLowerCase(r.status) eq 'recusada' ? 'selo-inativo' : '')}"><c:out value="${r.status}"/></span>
          </header>

          <ul class="cartao-itens">
            <li><span><c:out value="${r.produto}"/></span><span><c:out value="${r.quantidadeProduto}"/> unidades</span></li>
            <li><span>Aprovador</span><span><c:out value="${r.funcionarioAprovador}"/></span></li>
            <li><span>Motivo</span><span><c:out value="${r.motivo}"/></span></li>
          </ul>

          <footer class="cartao-rodape">
            <div class="cartao-info">
              <p class="cartao-responsavel">Total solicitado</p>
              <p class="cartao-produtos"><c:out value="${r.quantidadeProduto}"/> unidades</p>
            </div>
            <div class="cartao-acoes">
              <c:if test="${fn:toLowerCase(r.status) eq 'pendente'}">
                <form action="${ctx}/requisicao" method="post">
                  <input type="hidden" name="acao" value="atualizar_coluna">
                  <input type="hidden" name="id" value="${r.idRequisicao}">
                  <input type="hidden" name="coluna" value="status">
                  <input type="hidden" name="novoValor" value="Recusada">
                  <button type="submit" class="botao-contorno"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="m4.9 4.9 14.2 14.2"/></svg>Recusar</button>
                </form>
                <form action="${ctx}/requisicao" method="post">
                  <input type="hidden" name="acao" value="atualizar_coluna">
                  <input type="hidden" name="id" value="${r.idRequisicao}">
                  <input type="hidden" name="coluna" value="status">
                  <input type="hidden" name="novoValor" value="Aprovada">
                  <button type="submit" class="botao botao-pequeno"><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>Aprovar</button>
                </form>
              </c:if>
              <button type="button" class="botao-cartao botao-cartao-editar" aria-label="Editar" title="Editar"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/><path d="M14 7l3 3"/></svg></button>
              <form action="${ctx}/requisicao" method="post" class="form-excluir">
                <input type="hidden" name="acao" value="excluir">
                <input type="hidden" name="id" value="${r.idRequisicao}">
                <button type="submit" class="botao-cartao botao-cartao-excluir" aria-label="Excluir" title="Excluir"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/></svg></button>
              </form>
            </div>
          </footer>
        </article>
      </c:forEach>
      <c:if test="${empty requisicoes}">
        <p class="vazio"><strong>Nenhuma requisição nesta página</strong><br>Use a paginação ou cadastre uma requisição.</p>
      </c:if>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <form action="${ctx}/requisicao" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual - 1}">
          <button type="submit" class="botao-pagina" aria-label="Página anterior" ${paginaAtual le 1 ? 'disabled' : ''}>❮</button>
        </form>
        <span class="paginacao-atual" aria-current="page"><c:out value="${paginaAtual}"/></span>
        <form action="${ctx}/requisicao" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual + 1}">
          <button type="submit" class="botao-pagina" aria-label="Próxima página" ${paginaAtual ge totalPaginas ? 'disabled' : ''}>❯</button>
        </form>
      </div>
      <p class="paginacao-texto">Mostrando ${fn:length(requisicoes)}  de ${totalRegistros} registros - Página ${paginaAtual} de ${totalPaginas}</p>
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
        <li><a href="${ctx}/itemprelista" class="menu-item">Pré Listas</a></li>
        <li><a href="${ctx}/produto" class="menu-item">Produtos</a></li>
        <li><a href="${ctx}/requisicao" class="menu-item ativo" aria-current="page">Requisições</a></li>
      </ul>

      <a href="${ctx}/logout" class="menu-saida">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        Saída
      </a>
    </nav>
  </div>

  <div class="modal" id="modal-requisicao" role="dialog" aria-modal="true" aria-labelledby="modal-requisicao-titulo">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/requisicao" method="post" class="formulario">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="modal-requisicao-titulo">Nova Requisição</h2>
      <input type="hidden" name="acao" value="cadastrar">

      <div class="linha-campos">
        <label class="campo" for="req-tipo">ID Tipo Requisição
          <input type="number" name="idTipoRequisicao" id="req-tipo" placeholder="Ex: 1" min="1" required>
        </label>
        <label class="campo" for="req-solicitante">ID Funcionário Solicitante
          <input type="number" name="fkFuncionarioSolicitante" id="req-solicitante" placeholder="Ex: 2" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="req-aprovador">ID Funcionário Aprovador
          <input type="number" name="fkFuncionarioAprovador" id="req-aprovador" placeholder="Ex: 1" min="1" required>
        </label>
        <label class="campo" for="req-produto">ID Produto (fk_produto)
          <input type="number" name="fkProduto" id="req-produto" placeholder="Ex: 5" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="req-quantidade">Quantidade do Produto
          <input type="number" name="quantidadeProduto" id="req-quantidade" placeholder="Ex: 10" min="1" required>
        </label>
        <label class="campo" for="req-status">Status Inicial
          <select name="status" id="req-status" required><option value="Pendente">Pendente</option><option value="Aprovada">Aprovada</option><option value="Recusada">Recusada</option></select>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="req-datahora">Data e Hora
          <input type="datetime-local" name="dataHora" id="req-datahora" required>
        </label>
        <label class="campo" for="req-motivo">Motivo
          <input type="text" name="motivo" id="req-motivo" placeholder="Ex: Reposição de estoque" required>
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
    <form action="${ctx}/requisicao" method="post" class="formulario" id="form-edicao">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-edicao">Editar Requisição</h2>
      <%-- A consulta da listagem (JOIN) não devolve os IDs das chaves estrangeiras: esses campos exigem preenchimento manual. --%>
      <p>Preencha os IDs de tipo, solicitante, aprovador e produto para salvar a edição completa.</p>

      <input type="hidden" name="acao" value="atualizar_completo">
      <input type="hidden" name="id_requisicao" id="edicao-id">

      <div class="linha-campos">
        <label class="campo" for="edicao-tipo">ID Tipo Requisição
          <input type="number" name="idTipoRequisicao" id="edicao-tipo" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-solicitante">ID Funcionário Solicitante
          <input type="number" name="fkFuncionarioSolicitante" id="edicao-solicitante" placeholder="Informe o ID" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-aprovador">ID Funcionário Aprovador
          <input type="number" name="fkFuncionarioAprovador" id="edicao-aprovador" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-produto">ID Produto (fk_produto)
          <input type="number" name="fkProduto" id="edicao-produto" placeholder="Informe o ID" min="1" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-qtd">Quantidade do Produto
          <input type="number" name="quantidadeProduto" id="edicao-qtd" min="1" required>
        </label>
        <label class="campo" for="edicao-status">Status
          <select name="status" id="edicao-status" required><option value="Pendente">Pendente</option><option value="Aprovada">Aprovada</option><option value="Recusada">Recusada</option></select>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-datahora">Data e Hora
          <input type="datetime-local" name="dataHora" id="edicao-datahora" required>
        </label>
        <label class="campo" for="edicao-motivo">Motivo
          <input type="text" name="motivo" id="edicao-motivo" required>
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
    <form action="${ctx}/requisicao" method="post" class="formulario" id="form-coluna">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-coluna">Editar campo</h2>
      <input type="hidden" name="acao" value="atualizar_coluna">
      <input type="hidden" name="id" id="coluna-id">

      <label class="campo" for="coluna">Campo
        <select name="coluna" id="coluna">
          <option value="fk_tipo_requisicao">ID do Tipo</option>
          <option value="quantidade_prod">Quantidade</option>
          <option value="motivo">Motivo</option>
          <option value="status">Status</option>
          <option value="dt_requisicao">Data e Hora</option>
          <option value="fk_funcionario_solicitante">ID do Solicitante</option>
          <option value="fk_funcionario_aprovador">ID do Aprovador</option>
          <option value="fk_produto">ID do Produto</option>
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
      const propriedades = ["qtd", "motivo", "status", "datahora"];

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
        "fk_tipo_requisicao": [
                null,
                "number",
                "Informe o ID"
        ],
        "quantidade_prod": [
                "qtd",
                "number",
                ""
        ],
        "motivo": [
                "motivo",
                "text",
                ""
        ],
        "status": [
                "status",
                "text",
                "Pendente, Aprovada ou Recusada"
        ],
        "dt_requisicao": [
                "datahora",
                "datetime-local",
                ""
        ],
        "fk_funcionario_solicitante": [
                null,
                "number",
                "Informe o ID"
        ],
        "fk_funcionario_aprovador": [
                null,
                "number",
                "Informe o ID"
        ],
        "fk_produto": [
                null,
                "number",
                "Informe o ID"
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
