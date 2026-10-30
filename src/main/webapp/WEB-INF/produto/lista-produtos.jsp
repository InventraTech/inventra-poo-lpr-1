<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%-- Versão do CSS = data de modificação do arquivo. O navegador baixa o CSS de novo sempre que ele mudar (evita ficar com CSS antigo em cache). --%>
<%
  String pastaCss = application.getRealPath("/utils/css");
  request.setAttribute("vBase", pastaCss == null ? 0L : new java.io.File(pastaCss, "base.css").lastModified());
  request.setAttribute("vCartoes", pastaCss == null ? 0L : new java.io.File(pastaCss, "fornecedores.css").lastModified());
%>
<c:set var="partesNome" value="${fn:split(fn:trim(sessionScope.funcionarioLogado.nome), ' ')}" />
<c:set var="iniciais" value="${fn:toUpperCase(fn:substring(partesNome[0], 0, 1))}${fn:length(partesNome) gt 1 ? fn:toUpperCase(fn:substring(partesNome[fn:length(partesNome) - 1], 0, 1)) : ''}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Produtos | Inventra</title>
  <link rel="icon" href="${pageContext.request.contextPath}/utils/imagens/imagotipo.png">
  <link rel="stylesheet" href="${ctx}/utils/css/base.css?v=${vBase}">
  <link rel="stylesheet" href="${ctx}/utils/css/fornecedores.css?v=${vCartoes}">
  <link rel="stylesheet" href="${ctx}/utils/css/produtos.css">
  <style>
    .form-filtro { display: flex; gap: .5rem; }
    .form-exportar { display: flex; }
    .tabela-acoes { display: flex; align-items: center; gap: .8rem; }
    .tabela-acoes form { margin: 0; display: inline-flex; }
    .acoes-formulario { display: flex; gap: .65rem; justify-content: flex-end; flex-wrap: wrap; }
    .mensagem-erro { padding: 1rem; margin-bottom: 1rem; color: #842029; background: #fff0f1; border-radius: 12px; }
    .caixa-tabela { overflow-x: auto; }
    .campo textarea { font: inherit; padding: .75rem 1rem; border: 1px solid #e2e4ef; border-radius: 10px; }
    .filtros { flex-wrap: wrap; }
  </style>
</head>
<body>

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
      <div class="usuario">
        <div class="avatar"><c:out value="${iniciais}"/></div>
        <div class="usuario-info">
          <strong><c:out value="${sessionScope.funcionarioLogado.nome}"/></strong>
          <span>Administrador</span>
        </div>
      </div>
    </div>
  </header>

  <main class="conteudo">
    <section class="cabecalho-pagina">
      <div class="titulo-com-logo">
        <div class="titulo-pagina">
          <h1>Produtos</h1>
          <p>Gerencie os produtos do seu inventário</p>
        </div>
      </div>
      <div class="acoes-pagina">
        <a href="#modal-produto" class="botao">
          <img src="${ctx}/utils/imagens/adicionar.png" alt="" width="20" height="20">Novo produto
        </a>
      </div>
    </section>

    <c:if test="${not empty sessionScope.mensagemErro}">
      <div class="mensagem-erro" role="alert">
        <c:out value="${sessionScope.mensagemErro}"/>
      </div>
      <c:remove var="mensagemErro" scope="session"/>
    </c:if>

    <section class="bignumeros bignumeros-quatro">
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-caixa.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Produtos cadastrados</span>
          <strong class="bignumero-valor" title="Quantidade de produtos">${totalRegistros}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-predio.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Fornecedores vinculados</span>
          <strong class="bignumero-valor" title="Fornecedores vinculados">${totalFornecedores}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/icone-pagamento.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Marcas vinculadas</span>
          <strong class="bignumero-valor" title="Marcas vinculadas">${totalMarcas}</strong>
        </div>
      </div>
      <div class="bignumero">
        <div class="bignumero-icone"><img src="${ctx}/utils/imagens/estoque.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Categorias</span>
          <strong class="bignumero-valor" title="Categorias">${totalCategorias}</strong>
        </div>
      </div>
    </section>

    <section class="filtros" aria-label="Controles da lista">
      <input type="search" class="busca" disabled placeholder="Busca indisponível" aria-label="Busca indisponível" title="A pesquisa textual ainda não está disponível">

      <form action="${ctx}/produto" method="get" class="form-filtro">
        <input type="hidden" name="pagina" value="1">
        <select class="ordenacao" name="filtro" aria-label="Ordenar produtos" onchange="this.form.submit()">
          <option value="" ${empty filtroSelecionado ? 'selected' : ''}>Ordenar por ID</option>
          <option value="az" ${filtroSelecionado eq 'az' ? 'selected' : ''}>Nome (A - Z)</option>
          <option value="za" ${filtroSelecionado eq 'za' ? 'selected' : ''}>Nome (Z - A)</option>
          <option value="marca" ${filtroSelecionado eq 'marca' ? 'selected' : ''}>Marca (A - Z)</option>
          <option value="quantidade" ${filtroSelecionado eq 'quantidade' ? 'selected' : ''}>Quantidade</option>
          <option value="validade_proxima" ${filtroSelecionado eq 'validade_proxima' ? 'selected' : ''}>Validade: mais próxima</option>
          <option value="validade_distante" ${filtroSelecionado eq 'validade_distante' ? 'selected' : ''}>Validade: mais distante</option>
        </select>
      </form>

      <form action="${ctx}/produto" method="get" class="form-exportar">
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

    <section class="caixa-tabela">
      <table class="tabela" aria-label="Lista de produtos">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Categoria</th>
            <th>Marca</th>
            <th>Unidade</th>
            <th>Fornecedor</th>
            <th>Estoque mín.</th>
            <th>Estoque máx.</th>
            <th>Descrição</th>
            <th>Status</th>
            <th class="acoes">Ações</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${requestScope.produtos}">
            <tr data-id="${fn:escapeXml(p.idProduto)}"
                data-nome="${fn:escapeXml(p.nome)}"
                data-marca="${p.fk_marca gt 0 ? p.fk_marca : ''}"
                data-categoria="${p.fk_categoria gt 0 ? p.fk_categoria : ''}"
                data-fornecedor="${p.fk_fornecedor gt 0 ? p.fk_fornecedor : ''}"
                data-unidade="${fn:escapeXml(p.unidadeMedida)}"
                data-minimo="${fn:escapeXml(p.estoqueMin)}"
                data-maximo="${fn:escapeXml(p.estoqueMax)}"
                data-descricao="${fn:escapeXml(p.descricao)}"
                data-ativo="${fn:escapeXml(p.ativo)}">
              <td><c:out value="${p.idProduto}"/></td>
              <td><c:out value="${p.nome}"/></td>
              <td><c:out value="${p.nomeCategoria}"/></td>
              <td><c:out value="${p.nomeMarca}"/></td>
              <td><c:out value="${p.unidadeMedida}"/></td>
              <td><c:out value="${p.nomeFornecedor}"/></td>
              <td><c:out value="${p.estoqueMin}"/></td>
              <td><c:out value="${p.estoqueMax}"/></td>
              <td><c:out value="${p.descricao}"/></td>
              <td>
                <span class="selo-status ${p.ativo ? '' : 'selo-inativo'}">
                  ${p.ativo ? 'Ativo' : 'Inativo'}
                </span>
              </td>
              <td class="acoes">
                <div class="tabela-acoes">
                  <button type="button" class="botao-cartao botao-cartao-editar" aria-label="Editar produto" title="Editar produto">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/>
                      <path d="M14 7l3 3"/>
                    </svg>
                  </button>
                  <form action="${ctx}/produto" method="post" class="form-excluir">
                    <input type="hidden" name="acao" value="excluir">
                    <input type="hidden" name="id" value="${fn:escapeXml(p.idProduto)}">
                    <button type="submit" class="botao-cartao botao-cartao-excluir" aria-label="Excluir produto" title="Excluir produto">
                      <svg viewBox="0 0 24 24" fill="currentColor">
                        <path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/>
                      </svg>
                    </button>
                  </form>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty produtos}">
            <tr>
              <td class="estado-vazio" colspan="11">
                <strong>Nenhum produto nesta página</strong>
                <span>Use a paginação ou cadastre um produto.</span>
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <form action="${ctx}/produto" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual - 1}">
          <button type="submit" class="botao-pagina" aria-label="Página anterior" ${paginaAtual le 1 ? 'disabled' : ''}>❮</button>
        </form>
        <span class="paginacao-atual" aria-current="page"><c:out value="${paginaAtual}"/></span>
        <form action="${ctx}/produto" method="get">
          <input type="hidden" name="filtro" value="${fn:escapeXml(filtroSelecionado)}">
          <input type="hidden" name="pagina" value="${paginaAtual + 1}">
          <button type="submit" class="botao-pagina" aria-label="Próxima página" ${paginaAtual ge totalPaginas ? 'disabled' : ''}>❯</button>
        </form>
      </div>
      <p class="paginacao-texto">Mostrando ${fn:length(produtos)} de ${totalRegistros} registros - Página ${paginaAtual} de ${totalPaginas}</p>
    </nav>
  </main>

  <div class="menu-lateral" id="modal-menu">
    <a href="#" class="menu-fundo" aria-label="Fechar menu"></a>
    <nav class="menu-painel" aria-label="Menu principal">
      <div class="menu-marca">
        <img src="${ctx}/utils/imagens/imagotipo.png" alt="" class="menu-logo">
        <span class="menu-nome">Inventra</span>
      </div>
      <a href="#" class="menu-fechar" aria-label="Fechar menu">☰</a>
      <%-- Rotas dos outros módulos não foram fornecidas; caminhos visuais originais. --%>
      <ul class="menu-itens">
        <li><a href="${ctx}/funcionario" class="menu-item">Funcionários</a></li>
        <li><a href="${ctx}/fornecedor" class="menu-item">Fornecedores</a></li>
        <li><a href="${ctx}/lote" class="menu-item">Lotes</a></li>
        <li><a href="${ctx}/itemprelista" class="menu-item">Pré Listas</a></li>
        <li><a href="${ctx}/produto" class="menu-item ativo" aria-current="page">Produtos</a></li>
        <li><a href="${ctx}/requisicao" class="menu-item">Requisições</a></li>
      </ul>

      <a href="${ctx}/logout" class="menu-saida">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        Saída
      </a>
    </nav>
  </div>

  <div class="modal" id="modal-produto" role="dialog" aria-modal="true" aria-labelledby="titulo-produto">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/produto" method="post" class="formulario" id="form-produto">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-produto">Novo Produto</h2>
      <input type="hidden" name="acao" value="cadastrar">

      <label class="campo" for="produto-nome">Nome do Produto
        <input type="text" name="nome" id="produto-nome" required>
      </label>

      <div class="linha-campos">
        <label class="campo" for="produto-fornecedor">ID do Fornecedor
          <input type="number" name="fk_fornecedor" id="produto-fornecedor" min="1" required>
        </label>
        <label class="campo" for="produto-marca">ID da Marca
          <input type="number" name="fk_marca" id="produto-marca" min="1" required>
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo" for="produto-categoria">ID da Categoria
          <input type="number" name="fk_categoria" id="produto-categoria" min="1" required>
        </label>
        <label class="campo" for="produto-unidade">Unidade de Medida
          <input type="text" name="unidadeMedida" id="produto-unidade" required>
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo" for="produto-minimo">Estoque Mínimo
          <input type="number" name="estoqueMin" id="produto-minimo" required>
        </label>
        <label class="campo" for="produto-maximo">Estoque Máximo
          <input type="number" name="estoqueMax" id="produto-maximo" required>
        </label>
      </div>

      <label class="campo" for="produto-descricao">Descrição
        <textarea name="descricao" id="produto-descricao" rows="2"></textarea>
      </label>

      <label class="campo" for="produto-ativo">Status
        <select name="ativo" id="produto-ativo">
          <option value="true">Ativo</option>
          <option value="false">Inativo</option>
        </select>
      </label>

      <div class="acoes-formulario">
        <a href="#" class="botao-cancelar">Cancelar</a>
        <button type="submit" class="botao botao-primario">Salvar</button>
      </div>
    </form>
  </div>

  <div class="modal" id="modal-edicao" role="dialog" aria-modal="true" aria-labelledby="titulo-edicao">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>
    <form action="${ctx}/produto" method="post" class="formulario" id="form-edicao">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-edicao">Editar Produto</h2>
      <%-- A consulta original não preenche os IDs das FKs: esses campos exigem preenchimento manual. --%>
      <p>Preencha os IDs de fornecedor, marca e categoria para salvar a edição completa.</p>

      <input type="hidden" name="acao" value="atualizar_completo">
      <input type="hidden" name="id_produto" id="edicao-id">

      <label class="campo" for="edicao-nome">Nome do Produto
        <input type="text" name="nome" id="edicao-nome" required>
      </label>

      <div class="linha-campos">
        <label class="campo" for="edicao-fornecedor">ID do Fornecedor
          <input type="number" name="fk_fornecedor" id="edicao-fornecedor" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-marca">ID da Marca
          <input type="number" name="fk_marca" id="edicao-marca" placeholder="Informe o ID" min="1" required>
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo" for="edicao-categoria">ID da Categoria
          <input type="number" name="fk_categoria" id="edicao-categoria" placeholder="Informe o ID" min="1" required>
        </label>
        <label class="campo" for="edicao-unidade">Unidade de Medida
          <input type="text" name="unidadeMedida" id="edicao-unidade" required>
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo" for="edicao-minimo">Estoque Mínimo
          <input type="number" name="estoqueMin" id="edicao-minimo" required>
        </label>
        <label class="campo" for="edicao-maximo">Estoque Máximo
          <input type="number" name="estoqueMax" id="edicao-maximo" required>
        </label>
      </div>

      <label class="campo" for="edicao-descricao">Descrição
        <textarea name="descricao" id="edicao-descricao" rows="2"></textarea>
      </label>

      <label class="campo" for="edicao-ativo">Status
        <select name="ativo" id="edicao-ativo">
          <option value="true">Ativo</option>
          <option value="false">Inativo</option>
        </select>
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
    <form action="${ctx}/produto" method="post" class="formulario" id="form-coluna">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo" id="titulo-coluna">Editar campo</h2>
      <input type="hidden" name="acao" value="atualizar_coluna">
      <input type="hidden" name="id" id="coluna-id">

      <label class="campo" for="coluna">Campo
        <select name="coluna" id="coluna">
          <option value="nome">Nome</option>
          <option value="fk_marca">ID da Marca</option>
          <option value="fk_categoria">ID da Categoria</option>
          <option value="unidade_medida">Unidade de Medida</option>
          <option value="estoque_min">Estoque Mínimo</option>
          <option value="estoque_max">Estoque Máximo</option>
          <option value="fk_fornecedor">ID do Fornecedor</option>
          <option value="descricao">Descrição</option>
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
      // Ordenação, paginação e exportação continuam no servidor; a busca filtra no navegador.
      const tabela = document.querySelector('.tabela');

      // Busca: filtra as linhas da página atual (ID, nome, categoria, marca, unidade,
      // fornecedor, estoque e descrição), sem diferenciar maiúsculas nem acentos.
      const busca = document.querySelector('.busca');
      if (busca) {
        const normalizar = texto => (texto || '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
        const linhas = [...tabela.querySelectorAll('tbody tr[data-id]')];
        // Texto pesquisável de cada linha (as 9 primeiras colunas; ignora Status e Ações)
        const textos = linhas.map(linha =>
          normalizar([...linha.cells].slice(0, 9).map(celula => celula.textContent.trim()).join(' ')));

        const semResultado = document.createElement('tr');
        semResultado.hidden = true;
        semResultado.innerHTML = '<td class="estado-vazio" colspan="11">' +
          '<strong>Nenhum produto encontrado</strong><span>Tente outro termo de busca.</span></td>';
        tabela.tBodies[0].appendChild(semResultado);

        // O campo nasce desabilitado no HTML; a busca o libera aqui.
        busca.disabled = false;
        busca.placeholder = 'Buscar por nome, categoria, marca ou fornecedor...';
        busca.setAttribute('aria-label', 'Buscar produtos');
        busca.removeAttribute('title');

        function filtrar() {
          const termos = normalizar(busca.value.trim()).split(/\s+/).filter(Boolean);
          let visiveis = 0;
          linhas.forEach((linha, i) => {
            const combina = termos.every(termo => textos[i].includes(termo));
            linha.hidden = !combina;
            if (combina) visiveis++;
          });
          semResultado.hidden = visiveis > 0 || linhas.length === 0;
        }
        busca.addEventListener('input', filtrar);
        busca.addEventListener('search', filtrar); // botão "x" do campo type="search"
      }
      let selecionado = null;
      let retorno = null;
      const propriedades = ['nome', 'marca', 'categoria', 'fornecedor', 'unidade', 'minimo', 'maximo', 'descricao', 'ativo'];

      tabela.addEventListener('click', evento => {
        const botao = evento.target.closest('.botao-cartao-editar');
        if (!botao) return;
        selecionado = { ...botao.closest('tr').dataset };
        retorno = botao;
        document.getElementById('edicao-id').value = selecionado.id;
        propriedades.forEach(chave => {
          document.getElementById('edicao-' + chave).value = selecionado[chave] ?? '';
        });
        location.hash = 'modal-edicao';
      });

      document.querySelectorAll('.form-excluir').forEach(form => {
        form.addEventListener('submit', evento => {
          const nome = form.closest('tr').dataset.nome;
          if (!confirm('Deseja excluir o produto "' + nome + '"?')) evento.preventDefault();
        });
      });

      const campos = {
        nome: ['nome', 'text'],
        fk_marca: ['marca', 'number'],
        fk_categoria: ['categoria', 'number'],
        fk_fornecedor: ['fornecedor', 'number'],
        unidade_medida: ['unidade', 'text'],
        estoque_min: ['minimo', 'number'],
        estoque_max: ['maximo', 'number'],
        descricao: ['descricao', 'text']
      };
      const coluna = document.getElementById('coluna');
      const novoValor = document.getElementById('novoValor');
      function preencherCampo() {
        if (!selecionado) return;
        const [chave, tipo] = campos[coluna.value];
        novoValor.type = tipo;
        novoValor.value = selecionado[chave] ?? '';
        novoValor.required = coluna.value !== 'descricao';
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

      document.querySelectorAll('a[href="#modal-menu"], a[href="#modal-produto"]').forEach(link => {
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
        const itens = [...modal.querySelectorAll('a[href], button, input, select, textarea')].filter(el =>
          !el.disabled && el.type !== 'hidden' && el.getClientRects().length &&
          !el.matches('.modal-fundo, .menu-fundo')
        );
        if (!itens.length) return;
        const primeiro = itens[0], ultimo = itens[itens.length - 1];
        if (evento.shiftKey && (document.activeElement === primeiro || !itens.includes(document.activeElement))) {
          evento.preventDefault(); ultimo.focus();
        } else if (!evento.shiftKey && (document.activeElement === ultimo || !itens.includes(document.activeElement))) {
          evento.preventDefault(); primeiro.focus();
        }
      });
      // Uma URL com hash de edição sozinha não identifica qual produto editar.
      if (['#modal-edicao', '#modal-coluna'].includes(location.hash)) location.hash = '';
      foco();
    })();
  </script>
</body>
</html>