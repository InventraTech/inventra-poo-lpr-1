<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Versão do CSS = data de modificação do arquivo. O navegador baixa o CSS de novo sempre que ele mudar (evita ficar com CSS antigo em cache). --%>
<%
  String pastaCss = application.getRealPath("/utils/css");
  request.setAttribute("vBase", pastaCss == null ? 0L : new java.io.File(pastaCss, "base.css").lastModified());
  request.setAttribute("vPagina", pastaCss == null ? 0L : new java.io.File(pastaCss, "fornecedores.css").lastModified());
%>
<c:set var="partesNome" value="${fn:split(fn:trim(sessionScope.funcionarioLogado.nome), ' ')}" />
<!-- Gera as fotos de perfis para o usuario ter uma foto personalizada !-->
<c:set var="iniciais" value="${fn:toUpperCase(fn:substring(partesNome[0], 0, 1))}${fn:length(partesNome) gt 1 ? fn:toUpperCase(fn:substring(partesNome[fn:length(partesNome) - 1], 0, 1)) : ''}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Fornecedores | Inventra</title>
  <link rel="icon" href="${pageContext.request.contextPath}/utils/imagens/imagotipo.png">
  <link rel="stylesheet" href="${ctx}/utils/css/base.css?v=${vBase}">
  <link rel="stylesheet" href="${ctx}/utils/css/fornecedores.css?v=${vPagina}">
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
  <%-- Atributos enviados pelo FornecedorServlet: fornecedores, paginaAtual, totalPaginas, filtroSelecionado.
       Propriedades do FornecedorMolde (getters): id_fornecedor, nome_juridico, cnpj, email, nota_vpq, endereco. --%>

  <header class="barra-topo barra-inventra">
    <div class="topo-marca">
      <a href="#modal-menu" class="botao-menu" aria-label="Abrir menu">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" aria-hidden="true"><path d="M4 7h16M4 12h16M4 17h16"/></svg>
      </a>
      <a href="${ctx}/fornecedor" class="topo-logo" aria-label="Inventra">
        <img src="${ctx}/utils/imagens/imagotipo.png" alt="" class="topo-logo-img">
        <span class="topo-logo-nome">Inventra</span>
      </a>
    </div>

    <div class="topo-direita">
      <a href="#modal-notificacoes" class="topo-sino" aria-label="Notificações" title="Notificações">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></svg>
      </a>
      <div class="usuario">
        <div class="avatar"><c:out value="${iniciais}"/></div>
        <div class="usuario-info">
          <strong><c:out value="${sessionScope.funcionarioLogado.nome}"/></strong>
          <span>Administrador</span>
        </div>
      </div>
    </div>
  </header>

  <div class="conteudo">

    <section class="cabecalho-pagina">
      <div class="titulo-com-logo">
        <div class="titulo-pagina">
          <h1>Fornecedores</h1>
          <p>Gerencie quem abastece o seu estoque</p>
        </div>
      </div>
      <div class="acoes-pagina">
        <a class="botao" href="#modal-fornecedor">
          <img src="${ctx}/utils/imagens/adicionar.png" alt="" width="20" height="20" class="icone-adicionar">Novo fornecedor
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
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-predio.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Fornecedores</span>
          <strong class="bignumero-valor" title="Quantidade Fornecedores">${totalRegistros}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-check.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Média VPQ</span>
          <strong class="bignumero-valor" title="média vpq">${mediaNotaVPQ}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-caixa.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Produtos vinculados</span>
          <strong class="bignumero-valor" title="Produtos vinculados">${totalFornecedoresComProduto}</strong>
        </div>
      </div>
    </section>

    <section class="filtros" aria-label="Controles da lista">
      <input type="search" class="busca" placeholder="Buscar por nome, CNPJ, e-mail ou cidade..." aria-label="Buscar nesta página">

      <form action="${ctx}/fornecedor" method="get" class="form-filtro">
        <input type="hidden" name="pagina" value="1">
        <select class="ordenacao" name="filtro" aria-label="Ordenar" onchange="this.form.submit()">
          <option value="" ${empty filtroSelecionado ? 'selected' : ''}>Ordenar por ID</option>
          <option value="notaVPQ_crescente" ${filtroSelecionado eq 'notaVPQ_crescente' ? 'selected' : ''}>Nota VPQ (menor primeiro)</option>
          <option value="notaVPQ_decrescente" ${filtroSelecionado eq 'notaVPQ_decrescente' ? 'selected' : ''}>Nota VPQ (maior primeiro)</option>
        </select>
      </form>

      <form action="${ctx}/fornecedor" method="get" class="form-exportar">
        <input type="hidden" name="acao" value="exportar">
        <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
        <button class="botao botao-exportar" type="submit" title="Baixar CSV compatível com Excel">
          <svg class="icone-exportar" viewBox="0 0 24 24" aria-hidden="true">
            <rect x="8" y="3" width="14" height="18" rx="1.5" fill="#fff" stroke="#107c41" stroke-width="1.4"/>
            <path d="M12 8h7M12 12h7M12 16h7" stroke="#107c41" stroke-width="1.4"/>
            <rect x="2" y="6.5" width="11" height="11" rx="1.5" fill="#107c41" stroke="#fff"/>
            <path d="M5 9.5l5 5M10 9.5l-5 5" stroke="#fff" stroke-width="1.4"/>
          </svg>
          Exportar (Excel)
        </button>
      </form>
    </section>

    <section class="lista-cartoes" aria-label="Lista de fornecedores">
      <c:forEach var="f" items="${requestScope.fornecedores}">
        <article class="cartao"
                 data-id="${f.id_fornecedor}"
                 data-rotulo="${fn:escapeXml(f.nome_juridico)}"
                 data-nome="${fn:escapeXml(f.nome_juridico)}"
                 data-cnpj="${fn:escapeXml(f.cnpj)}"
                 data-email="${fn:escapeXml(f.email)}"
                 data-nota="${f.nota_vpq}">
          <header class="cartao-topo">
            <div class="cartao-logo"><c:out value="${fn:toUpperCase(fn:substring(f.nome_juridico, 0, 2))}"/></div>
            <div class="cartao-identificacao">
              <h2 class="cartao-nome"><c:out value="${f.nome_juridico}"/></h2>
              <p class="cartao-cnpj"><c:out value="${f.cnpj}"/></p>
            </div>
          </header>

          <ul class="cartao-contatos">
            <li class="cartao-contato">
              <svg class="cartao-icone" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="5" width="18" height="14" rx="2"/>
                <path d="M3 7l9 6 9-6"/>
              </svg>
              <span><c:out value="${f.email}"/></span>
            </li>
          </ul>

          <footer class="cartao-rodape">
            <div class="cartao-info">
              <p class="cartao-responsavel"><c:out value="${f.endereco}"/></p>
              <p class="cartao-produtos">Nota VPQ: <c:out value="${f.nota_vpq}"/></p>
            </div>
            <div class="cartao-acoes">
              <button type="button" class="botao-cartao botao-cartao-editar" aria-label="Editar" title="Editar"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/><path d="M14 7l3 3"/></svg></button>
              <form action="${ctx}/fornecedor" method="post" class="form-excluir">
                <input type="hidden" name="acao" value="excluir">
                <input type="hidden" name="id" value="${f.id_fornecedor}">
                <button type="submit" class="botao-cartao botao-cartao-excluir" aria-label="Excluir" title="Excluir"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/></svg></button>
              </form>
            </div>
          </footer>
        </article>
      </c:forEach>
      <c:if test="${empty fornecedores}">
        <p class="vazio"><strong>Nenhum fornecedor nesta página</strong><br>Use a paginação ou cadastre um fornecedor.</p>
      </c:if>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <form action="${ctx}/fornecedor" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual - 1}">
          <button type="submit" class="botao-pagina" aria-label="Página anterior" ${paginaAtual le 1 ? 'disabled' : ''}>❮</button>
        </form>
        <span class="paginacao-atual" aria-current="page"><c:out value="${paginaAtual}"/></span>
        <form action="${ctx}/fornecedor" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual + 1}">
          <button type="submit" class="botao-pagina" aria-label="Próxima página" ${paginaAtual ge totalPaginas ? 'disabled' : ''}>❯</button>
        </form>
      </div>
      <p class="paginacao-texto">Mostrando ${fn:length(fornecedores)} de ${totalRegistros} registros - Página ${paginaAtual} de ${totalPaginas}</p>
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
        <li><a href="${ctx}/fornecedor" class="menu-item ativo" aria-current="page">Fornecedores</a></li>
        <li><a href="${ctx}/lote" class="menu-item">Lotes</a></li>
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

  <%-- Painel do sino. Atributos enviados por NotificacaoLotes.carregar(request), chamado no servlet da tela: lotesProximosValidade (LoteDAO.listarLoteDataValidade), totalLotesVencidos, totalLotesVencendo30dias.
       Abre/fecha por :target, igual aos outros modais. O prazo ("Em 12 dias") é calculado pelo script a partir de data-validade. --%>
  <div class="modal modal-notificacoes" id="modal-notificacoes" role="dialog" aria-modal="true" aria-labelledby="modal-notificacoes-titulo">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <section class="painel-notificacoes">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="notificacoes-titulo" id="modal-notificacoes-titulo">Notificações</h2>
      <p class="notificacoes-resumo">
        <strong><c:out value="${totalLotesVencidos}"/></strong> vencido(s) &middot;
        <strong><c:out value="${totalLotesVencendo30dias}"/></strong> vencendo em até 30 dias
      </p>

      <ul class="notificacoes-lista">
        <c:forEach var="l" items="${requestScope.lotesProximosValidade}">
          <c:if test="${not empty l.dt_validade}">
            <li class="notificacao" data-validade="${l.dt_validade}">
              <div class="notificacao-texto">
                <strong><c:out value="${l.produto}"/></strong>
                <span>Lote <c:out value="${l.numero_lote}"/> &middot; <c:out value="${l.qtd_atual}"/> un.</span>
                <span>Válido até ${fn:substring(l.dt_validade, 8, 10)}/${fn:substring(l.dt_validade, 5, 7)}/${fn:substring(l.dt_validade, 0, 4)}</span>
              </div>
              <span class="notificacao-prazo"></span>
            </li>
          </c:if>
        </c:forEach>
      </ul>
      <c:if test="${empty requestScope.lotesProximosValidade}">
        <p class="notificacoes-vazio">Nenhum lote com validade cadastrada.</p>
      </c:if>

      <a href="${ctx}/lote?filtro=validade_crescente" class="notificacoes-ver-todos">Ver todos os lotes</a>
    </section>
  </div>

  <div class="modal" id="modal-fornecedor" role="dialog" aria-modal="true" aria-labelledby="modal-fornecedor-titulo">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/fornecedor" method="post" class="formulario">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="modal-fornecedor-titulo">Novo Fornecedor</h2>
      <input type="hidden" name="acao" value="cadastrar">

      <label class="campo" for="forn-nome">Nome Jurídico
          <input type="text" name="nome" id="forn-nome" placeholder="Ex: Distribuidora Alfa LTDA" required>
        </label>
      <div class="linha-campos">
        <label class="campo" for="forn-cnpj">CNPJ
          <input type="text" name="cnpj" id="forn-cnpj" placeholder="00.000.000/0000-00" required>
        </label>
        <label class="campo" for="forn-email">E-mail
          <input type="email" name="email" id="forn-email" placeholder="contato@fornecedor.com" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="forn-telefone">Telefone
          <input type="text" name="telefone" id="forn-telefone" placeholder="(11) 99999-9999" required>
        </label>
        <label class="campo" for="forn-nota">Nota VPQ
          <input type="number" name="nota_vpq" id="forn-nota" placeholder="Ex: 8" min="0" max="10" required>
        </label>
      </div>
      <label class="campo" for="forn-endereco">ID Endereço (fk_endereco)
          <input type="number" name="fk_regiao" id="forn-endereco" placeholder="Ex: 1" min="1" required>
        </label>

      <div class="acoes-formulario">
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <div class="modal" id="modal-edicao" role="dialog" aria-modal="true" aria-labelledby="titulo-edicao">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/fornecedor" method="post" class="formulario" id="form-edicao">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-edicao">Editar Fornecedor</h2>
      <%-- A consulta da listagem (JOIN) não devolve os IDs das chaves estrangeiras: esses campos exigem preenchimento manual. --%>
      <p>Preencha o ID do endereço e o telefone para salvar a edição completa.</p>

      <input type="hidden" name="acao" value="atualizar_completo">
      <input type="hidden" name="id_fornecedor" id="edicao-id">

      <label class="campo" for="edicao-nome">Nome Jurídico
          <input type="text" name="nome" id="edicao-nome" required>
        </label>
      <div class="linha-campos">
        <label class="campo" for="edicao-cnpj">CNPJ
          <input type="text" name="cnpj" id="edicao-cnpj" placeholder="00.000.000/0000-00" required>
        </label>
        <label class="campo" for="edicao-email">E-mail
          <input type="email" name="email" id="edicao-email" required>
        </label>
      </div>
      <div class="linha-campos">
        <label class="campo" for="edicao-telefone">Telefone
          <input type="text" name="telefone" id="edicao-telefone" placeholder="Informe o telefone" required>
        </label>
        <label class="campo" for="edicao-nota">Nota VPQ
          <input type="number" name="nota_vpq" id="edicao-nota" min="0" max="10" required>
        </label>
      </div>
      <label class="campo" for="edicao-endereco">ID Endereço (fk_endereco)
          <input type="number" name="fk_endereco" id="edicao-endereco" placeholder="Informe o ID" min="1" required>
        </label>

      <div class="acoes-formulario">
        <button type="button" class="botao-cancelar" id="editar-um-campo">Editar somente um campo</button>
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <div class="modal" id="modal-coluna" role="dialog" aria-modal="true" aria-labelledby="titulo-coluna">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/fornecedor" method="post" class="formulario" id="form-coluna">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-coluna">Editar campo</h2>
      <input type="hidden" name="acao" value="atualizar_coluna">
      <input type="hidden" name="id" id="coluna-id">

      <label class="campo" for="coluna">Campo
        <select name="coluna" id="coluna">
          <option value="nome_juridico">Nome Jurídico</option>
          <option value="fk_endereco">ID do Endereço</option>
          <option value="cnpj">CNPJ</option>
          <option value="email">E-mail</option>
          <option value="nota_vpq">Nota VPQ</option>
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
      const propriedades = ["nome", "cnpj", "email", "nota"];

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
        "nome_juridico": [
                "nome",
                "text",
                ""
        ],
        "fk_endereco": [
                null,
                "number",
                "Informe o ID"
        ],
        "cnpj": [
                "cnpj",
                "text",
                "00.000.000/0000-00"
        ],
        "email": [
                "email",
                "email",
                ""
        ],
        "nota_vpq": [
                "nota",
                "number",
                "0 a 10"
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

      // ---- Notificações: prazo de cada lote (mesmos limites da tela de lotes: vencido < 0, vencendo até 30 dias) ----
      {
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        document.querySelectorAll('.notificacao[data-validade]').forEach(item => {
          const [ano, mes, dia] = item.dataset.validade.split('-').map(Number);
          const dias = Math.round((new Date(ano, mes - 1, dia) - hoje) / 86400000);
          const plural = n => n + (n === 1 ? ' dia' : ' dias');
          item.dataset.estado = dias < 0 ? 'vencido' : dias <= 30 ? 'vencendo' : 'vigente';
          item.querySelector('.notificacao-prazo').textContent =
            dias < 0 ? 'Venceu há ' + plural(-dias) :
            dias === 0 ? 'Vence hoje' :
            dias === 1 ? 'Vence amanhã' : 'Em ' + plural(dias);
        });
      }

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
