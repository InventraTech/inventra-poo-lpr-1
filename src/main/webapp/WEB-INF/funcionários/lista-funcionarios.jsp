<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Funcionários</title>
  <link rel="icon" type="image/png" href="../ICONES/imagotipo.png">
  <link rel="stylesheet" href="../css/base.css" />
  <link rel="stylesheet" href="../css/funcionarios.css" />
</head>
<body>
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
        <img src="../ICONES/imagotipo.png" alt="Logo da empresa" class="logo-pagina">

        <div class="titulo-pagina">
          <h1>Funcionários</h1>
          <p>Gerencie os colaboradores da sua empresa</p>
        </div>
      </div>

      <div class="acoes-pagina">
        <a class="botao" href="#modal-funcionario">
          <img src="../ICONES/adicionar-funcionario.png" alt="" width="20" height="20" class="icone-adicionar">
          Novo funcionário
        </a>
      </div>
    </section>

    <section class="bignumeros bignumeros-quatro">
      <div class="bignumero">
        <div class="bignumero-icone"><img src="../ICONES/icone_funcionarios.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Total de funcionários</span>
          <strong class="bignumero-valor">—</strong>
        </div>
      </div>

      <div class="bignumero">
        <div class="bignumero-icone"><img src="../ICONES/ferramenta.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Em processo</span>
          <strong class="bignumero-valor">—</strong>
        </div>
      </div>

      <div class="bignumero">
        <div class="bignumero-icone"><img src="../ICONES/folhas.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Contratos ativos</span>
          <strong class="bignumero-valor">—</strong>
        </div>
      </div>

      <div class="bignumero">
        <div class="bignumero-icone"><img src="../ICONES/Agenda.png" alt=""></div>
        <div class="bignumero-info">
          <span class="bignumero-rotulo">Desligados</span>
          <strong class="bignumero-valor">—</strong>
        </div>
      </div>
    </section>

    <section class="filtros">
      <input
        type="search"
        class="busca"
        placeholder="Buscar por nome, CPF, cargo ou setor..."
        aria-label="Buscar"
      />

      <select class="ordenacao" aria-label="Ordenar">
        <option value="0">Ordenar por ID</option>
        <option value="1">Nome (A - Z)</option>
        <option value="1-desc">Nome (Z - A)</option>
        <option value="5">Setor</option>
        <option value="6">Admissão</option>
      </select>

      <button type="button" class="botao botao-exportar" id="botao-exportar">
        <svg class="icone-exportar" viewBox="0 0 24 24" aria-hidden="true">
          <rect x="8" y="3" width="14" height="18" rx="1.5" fill="#fff"/>
          <path d="M12 8h7M12 12h7M12 16h7" stroke="#107c41" stroke-width="1.4" stroke-linecap="round"/>
          <rect x="2" y="6.5" width="11" height="11" rx="1.5" fill="#107c41" stroke="#fff" stroke-width="1.2"/>
          <path d="M5 9.5l5 5M10 9.5l-5 5" stroke="#fff" stroke-width="1.4" stroke-linecap="round"/>
        </svg>
        Exportar (Excel)
      </button>
    </section>

    <section class="caixa-tabela">
      <table class="tabela" aria-label="Lista de funcionários" data-por-pagina="7">
        <thead>
          <tr><th>ID</th><th>Nome</th><th>CPF</th><th>Email</th><th>Cargo</th><th>Setor</th><th>Admissão</th><th>Status</th><th class="acoes">Ações</th></tr>
        </thead>
        <tbody>
          <tr id="linha-vazia">
            <td class="estado-vazio" colspan="9">
              <strong>Nenhum registro carregado</strong>
              <span>Os registros aparecerão aqui quando a tabela receber os dados.</span>
            </td>
          </tr>
        </tbody>
      </table>

      <template id="modelo-linha">
        <tr>
          <td></td><td></td><td></td><td></td><td></td><td></td><td></td><td><span class="selo-status">Ativo</span></td>
          <td class="acoes">
            <div class="tabela-acoes">
              <button class="botao-cartao botao-cartao-editar" type="button" aria-label="Editar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M4 20l1-4L16.5 4.5a2 2 0 0 1 3 3L8 19l-4 1z"/>
                  <path d="M14 7l3 3"/>
                </svg>
              </button>

              <button class="botao-cartao botao-cartao-excluir" type="button" aria-label="Excluir">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 3h6l1 2h4v2H4V5h4l1-2zM6 9h12l-1 11a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2L6 9zm4 2v8h1.5v-8H10zm3 0v8h1.5v-8H13z"/></svg>
              </button>
            </div>
          </td>
        </tr>
      </template>
    </section>

    <nav class="paginacao" aria-label="Paginação">
      <div class="paginacao-botoes">
        <button type="button" class="botao-pagina" id="pagina-anterior" aria-label="Página anterior" disabled><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg></button>
        <span class="paginacao-atual" id="pagina-numero" aria-current="page">1</span>
        <button type="button" class="botao-pagina" id="pagina-proxima" aria-label="Próxima página" disabled><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg></button>
      </div>

      <p class="paginacao-texto" id="paginacao-texto">Mostrando 0 de 0 funcionários</p>
    </nav>

  </div>

  <div class="menu-lateral" id="modal-menu">
    <a href="#" class="menu-fundo" aria-label="Fechar menu"></a>
    <nav class="menu-painel">
      <div class="menu-marca">
        <img src="../ICONES/imagotipo.png" alt="" class="menu-logo">
        <span class="menu-nome">Inventra</span>
      </div>

      <a href="#" class="menu-fechar" aria-label="Fechar menu">☰</a>

      <ul class="menu-itens">
        <li><a href="../HTML/funcionarios.html" class="menu-item ativo" aria-current="page">Funcionários</a></li>
        <li><a href="../HTML/fornecedores.html" class="menu-item">Fornecedores</a></li>
        <li><a href="../HTML/lotes.html" class="menu-item">Lotes</a></li>
        <li><a href="../HTML/preLista.html" class="menu-item">Pré Listas</a></li>
        <li><a href="../HTML/produtos.html" class="menu-item">Produtos</a></li>
        <li><a href="../HTML/requisicoes.html" class="menu-item">Requisições</a></li>
      </ul>

      <a href="#" class="menu-saida">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
        Saída
      </a>
    </nav>
  </div>

  <div class="modal" id="modal-funcionario">
    <a href="#" class="modal-fundo" aria-label="Fechar"></a>

    <form class="formulario" action="FuncionarioServlet" method="POST">
      <a href="#" class="modal-fechar" aria-label="Fechar">✕</a>
      <h2 class="modal-titulo">Novo Funcionário</h2>

      <label class="campo">
        Nome Completo
        <input type="text" name="nome" placeholder="Ex: Maria Oliveira" required />
      </label>

      <div class="linha-campos">
        <label class="campo">
          CPF
          <input type="text" name="cpf" placeholder="000.000.000-00" required />
        </label>

        <label class="campo">
          Telefone
          <input type="text" name="telefone" placeholder="(11) 90000-0000" required />
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo">
          E-mail
          <input type="email" name="email" placeholder="exemplo@empresa.com" required />
        </label>

        <label class="campo">
          Senha
          <input type="password" name="senha" placeholder="********" required />
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo">
          ID Setor (fk_setor)
          <input type="number" name="fk_setor" placeholder="Ex: 1" required />
        </label>

        <label class="campo">
          Data de Admissão
          <input type="date" name="dt_admissao" required />
        </label>
      </div>

      <div class="linha-campos">
        <label class="campo">
          Status
          <select name="status" required>
            <option value="" disabled selected>Selecionar</option>
            <option value="Ativo">Ativo</option>
            <option value="Inativo">Inativo</option>
          </select>
        </label>

        <label class="campo">
          Administrador?
          <select name="is_admin" required>
            <option value="" disabled selected>Selecionar</option>
            <option value="true">Sim</option>
            <option value="false">Não</option>
          </select>
        </label>
      </div>

      <div class="acoes-formulario">
        <button type="submit" class="botao-primario">Salvar Funcionário</button>
      </div>
    </form>
  </div>

  <div class="aviso" id="aviso-sucesso" role="status">
    <div class="aviso-icone">✓</div>
    <div class="aviso-texto">
      <strong>Funcionário cadastrado com sucesso!</strong>
      <span>O novo funcionário foi adicionado à lista e já está disponível para gerenciamento.</span>
    </div>
    <a href="#" class="aviso-fechar" aria-label="Fechar">✕</a>
  </div>

  <script>
    (() => {
      const tabela = document.querySelector('.tabela');
      const corpo = tabela.tBodies[0];
      const linhaVazia = document.getElementById('linha-vazia');
      const busca = document.querySelector('.busca');
      const ordem = document.querySelector('.ordenacao');
      const btnAnterior = document.getElementById('pagina-anterior');
      const btnProxima = document.getElementById('pagina-proxima');
      const numero = document.getElementById('pagina-numero');
      const texto = document.getElementById('paginacao-texto');
      const btnExportar = document.getElementById('botao-exportar');

      const porPagina = Number(tabela.dataset.porPagina) || 7;
      const unidade = 'funcionários';
      const colunasNumericas = [0];
      const colunasData = [6];

      let pagina = 1;
      let filtradas = [];

      const limpar = t => t.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
      const valorDe = (linha, col) => linha.cells[col]?.dataset.valor ?? linha.cells[col]?.textContent.trim() ?? '';

      function paraNumero(t) {
        const s = t.replace(/[^\d,.\-]/g, '');
        return Number(s.includes(',') ? s.replace(/\./g, '').replace(',', '.') : s);
      }

      const paraIso = t => (t.includes('/') ? t.split('/').reverse().join('-') : t);

      function comparar(a, b, col) {
        if (colunasNumericas.includes(col)) return paraNumero(valorDe(a, col)) - paraNumero(valorDe(b, col));
        if (colunasData.includes(col)) return paraIso(valorDe(a, col)).localeCompare(paraIso(valorDe(b, col)));
        return valorDe(a, col).localeCompare(valorDe(b, col), 'pt-BR', { numeric: true });
      }

      function atualizar() {
        const linhas = [...corpo.rows].filter(l => l !== linhaVazia);
        const termo = limpar(busca.value.trim());

        filtradas = linhas.filter(l => limpar([...l.cells].slice(0, -1).map(c => c.textContent).join(' ')).includes(termo));

        const [coluna, sentido] = ordem.value.split('-');
        filtradas.sort((a, b) => {
          const r = comparar(a, b, Number(coluna));
          return sentido === 'desc' ? -r : r;
        });

        const totalPaginas = Math.max(1, Math.ceil(filtradas.length / porPagina));
        pagina = Math.min(pagina, totalPaginas);
        const inicio = (pagina - 1) * porPagina;

        linhas.forEach(l => (l.hidden = true));
        filtradas.forEach((l, i) => {
          corpo.append(l);
          l.hidden = i < inicio || i >= inicio + porPagina;
        });

        linhaVazia.hidden = filtradas.length > 0;
        linhaVazia.querySelector('strong').textContent = linhas.length ? 'Nenhum resultado encontrado' : 'Nenhum registro carregado';
        linhaVazia.querySelector('span').textContent = linhas.length ? 'Tente outro termo de busca.' : 'Os registros aparecerão aqui quando a tabela receber os dados.';

        const mostrando = Math.max(0, Math.min(porPagina, filtradas.length - inicio));
        numero.textContent = pagina;
        btnAnterior.disabled = pagina <= 1;
        btnProxima.disabled = pagina >= totalPaginas;
        texto.textContent = `Mostrando ${mostrando} de ${filtradas.length} ${unidade}`;
        btnExportar.disabled = filtradas.length === 0;
      }

      busca.addEventListener('input', () => { pagina = 1; atualizar(); });
      ordem.addEventListener('change', () => { pagina = 1; atualizar(); });
      btnAnterior.addEventListener('click', () => { pagina--; atualizar(); });
      btnProxima.addEventListener('click', () => { pagina++; atualizar(); });

      btnExportar.addEventListener('click', () => {
        // exporta tudo que passou no filtro, não só a página aberta
        const aspas = t => '"' + (/^\s*[=+@-]/.test(t) ? "'" + t : t).replace(/"/g, '""') + '"';
        const textos = linha => [...linha.cells].slice(0, -1).map(c => c.textContent.trim());
        const dados = [textos(tabela.tHead.rows[0]), ...filtradas.map(textos)];
        const csv = '\ufeff' + dados.map(l => l.map(aspas).join(';')).join('\r\n');

        const link = document.createElement('a');
        link.href = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8;' }));
        link.download = 'funcionarios.csv';
        link.click();
        URL.revokeObjectURL(link.href);
      });

      const modalAberto = () => location.hash.startsWith('#modal-');

      document.addEventListener('keydown', e => {
        if (!modalAberto()) return;

        if (e.key === 'Escape') {
          location.hash = '';
          document.querySelector('.botao-menu').focus();
        }

        if (e.key === 'Tab') {
          const itens = [...document.getElementById(location.hash.slice(1)).querySelectorAll('a, button, input, select')]
            .filter(el => !el.disabled && !el.classList.contains('menu-fundo') && !el.classList.contains('modal-fundo'));
          if (e.shiftKey && document.activeElement === itens[0]) { e.preventDefault(); itens.at(-1).focus(); }
          else if (!e.shiftKey && document.activeElement === itens.at(-1)) { e.preventDefault(); itens[0].focus(); }
        }
      });

      function prenderFoco() {
        const aberto = modalAberto();
        document.querySelector('.conteudo').inert = aberto;
        document.querySelector('.barra-topo').inert = aberto;
        if (aberto) document.getElementById(location.hash.slice(1))?.querySelector('.menu-fechar, .modal-fechar')?.focus();
      }

      addEventListener('hashchange', prenderFoco);

      // chame de novo se mexer nas linhas por fora
      window.atualizarTabela = atualizar;

      atualizar();
      prenderFoco();
    })();
  </script>
</body>
</html>
