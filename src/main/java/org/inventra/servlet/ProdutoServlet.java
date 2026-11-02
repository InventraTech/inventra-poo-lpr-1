package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.ProdutoDAO;
import org.inventra.dao.OpcoesDAO;
import org.inventra.model.ProdutoMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.ProdutoValidador;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Controla a tela de produtos: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo ProdutoValidador antes de chegar ao DAO.
 */
@WebServlet(name = "ProdutoServlet", value = "/produto")
public class ProdutoServlet extends HttpServlet {

    private ProdutoDAO produtoDAO;

    @Override
    public void init() {
        produtoDAO = new ProdutoDAO();
    }

    /**
     * Exibe a lista de produtos. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
     * Na exportação o CSV sai completo, sem paginação, respeitando o mesmo filtro da tela.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String filtroSelecionado = request.getParameter("filtro");
        String acao = request.getParameter("acao");

        //Definindo a páginação:
        int limite = 6;
        int paginaAtual = 1;

        String pagina = request.getParameter("pagina");
        if (pagina != null && !pagina.isEmpty()){
            try {
                paginaAtual = Integer.parseInt(pagina);
                if (paginaAtual < 1) {
                    paginaAtual = 1;
                }
            } catch (NumberFormatException e){
                paginaAtual = 1;
            }
        }

        int offSet = (paginaAtual - 1) * limite;

        try {
            if ("exportar".equals(acao)){
                //Diz para o navegador do usuario que a resposta é em CSV e obriga ele a baixar como anexo
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"produtos.csv\"");

                List<ProdutoMolde> produtos;
                produtos = produtoDAO.listarProdutosParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); //Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Nome;Marca;Categoria;Medida;Estoque Min;Estoque Max;Fornecedor;Descricao;Ativo");

                    for (ProdutoMolde f : produtos) {
                        writer.print(f.getIdProduto() + ";");
                        writer.print(f.getNome() + ";");
                        writer.print(f.getNomeMarca() + ";");
                        writer.print(f.getNomeCategoria() + ";");
                        writer.print(f.getUnidadeMedida() + ";");
                        writer.print(f.getEstoqueMin() + ";");
                        writer.print(f.getEstoqueMax() + ";");
                        writer.print(f.getNomeFornecedor() + ";");
                        writer.print(f.getDescricao() + ";");
                        writer.println(f.isAtivo());
                    }
                    writer.flush();
                }
                return;
            }

            List<ProdutoMolde> produtos = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "az" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(true, limite, offSet);
                }
                case "za" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(false, limite, offSet);
                }
                case "marca" -> {
                    produtos = produtoDAO.listarProdutosPorMarca(limite, offSet);
                }
                default -> {
                    produtos = produtoDAO.listarProdutos(limite, offSet);
                }
            }

            int totalRegistros = produtoDAO.contarProdutosPadrao();
            int totalCategorias = produtoDAO.contarCategorias();
            int totalFornecedores = produtoDAO.contarFornecedores();
            int totalMarcas = produtoDAO.contarMarcas();

            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            //Enviando as informações principais para a tela do usuarios
            request.setAttribute("produtos", produtos);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistros", totalRegistros);
            request.setAttribute("totalCategorias", totalCategorias);
            request.setAttribute("totalFornecedores", totalFornecedores);
            request.setAttribute("totalMarcas", totalMarcas);
            request.setAttribute("opcoesFornecedores", OpcoesDAO.fornecedores());
            request.setAttribute("opcoesMarcas", OpcoesDAO.marcas());
            request.setAttribute("opcoesCategorias", OpcoesDAO.categorias());
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            //Notificações do sino do header (lotes mais próximos da validade)
            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/produto/lista-produtos.jsp"
            ).forward(request, response);
        }
        catch (NumberFormatException e){
        request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
        response.sendRedirect(request.getContextPath() + "/produto");
    }
        catch (IllegalArgumentException e){
        request.getSession().setAttribute("mensagemErro", e.getMessage());
        response.sendRedirect(request.getContextPath() + "/produto");
    }
    //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }

    /**
     * Trata o formulário de produtos. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
     * {@code atualizar_completo}; qualquer outro valor (ou ausência) é tratado como cadastro.
     * Se houver erro de leitura ou de validação, as mensagens vão para a sessão ({@code mensagemErro}) e o usuário
     * volta para a lista, sem nada ser gravado.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");

        //Validando ação para evitar erro por ser NULL
        if (acao == null){
            acao = "";
        }

        //Mensagens específicas para o usuário: erros de leitura dos parâmetros + lista devolvida pelo validador
        List<String> erros = new ArrayList<>();

        try {
            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                produtoDAO.deletarProduto(id);

                response.sendRedirect(
                        request.getContextPath() + "/produto"
                );
                return;
            }

            //Atualiza apenas UMA coluna
            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID do produto", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Aplica o novo valor sobre o produto atual e valida o resultado (inclui estoque mínimo x máximo)
                if (erros.isEmpty()) {
                    ProdutoMolde produto = produtoDAO.buscarProdutoPorId(id);

                    if (produto == null) {
                        erros.add("Produto não encontrado.");
                    } else {
                        aplicarColuna(produto, coluna, novoValor, erros);

                        if (erros.isEmpty()) {
                            erros.addAll(ProdutoValidador.validar(produto));
                        }
                    }
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/produto", erros);
                    return;
                }

                produtoDAO.atualizarColunaProduto(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/produto");

                //Return faz com que pare aqui caso seja escolhido
                return;
            } else if (acao.equals("atualizar_completo")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id_produto"), "ID do produto", erros);

                ProdutoMolde atualizandoProduto = lerProduto(request, erros);

                if (erros.isEmpty()) {
                    erros.addAll(ProdutoValidador.validar(atualizandoProduto));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/produto", erros);
                    return;
                }

                produtoDAO.atualizarProdutoCompleto(atualizandoProduto, id);

                response.sendRedirect(
                        request.getContextPath() + "/produto"
                );

                return;
            }

            ProdutoMolde novoProduto = lerProduto(request, erros);

            if (erros.isEmpty()) {
                erros.addAll(ProdutoValidador.validar(novoProduto));
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/produto", erros);
                return;
            }

            produtoDAO.inserirProduto(novoProduto);

            response.sendRedirect(
                    request.getContextPath() + "/produto"
            );

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/produto");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/produto");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private ProdutoMolde lerProduto(HttpServletRequest request, List<String> erros) {
        String nome = request.getParameter("nome");
        int fkMarca = ParametrosHttp.lerInt(request.getParameter("fk_marca"), "Marca", erros);
        int fkCategoria = ParametrosHttp.lerInt(request.getParameter("fk_categoria"), "Categoria", erros);
        String unidadeMedida = request.getParameter("unidadeMedida");
        int estoqueMin = ParametrosHttp.lerInt(request.getParameter("estoqueMin"), "Estoque mínimo", erros);
        int estoqueMax = ParametrosHttp.lerInt(request.getParameter("estoqueMax"), "Estoque máximo", erros);
        int fkFornecedor = ParametrosHttp.lerInt(request.getParameter("fk_fornecedor"), "Fornecedor", erros);
        String descricao = request.getParameter("descricao");
        boolean ativo = Boolean.parseBoolean(request.getParameter("ativo"));

        return new ProdutoMolde(fkFornecedor, fkMarca, fkCategoria, nome, unidadeMedida, estoqueMin, estoqueMax, descricao, ativo);
    }

    //Edição por coluna: troca no produto atual só o campo escolhido. Só aceita as colunas que o DAO sabe atualizar.
    private void aplicarColuna(ProdutoMolde produto, String coluna, String novoValor, List<String> erros) {
        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "fk_fornecedor" -> produto.setFkFornecedor(ParametrosHttp.lerInt(novoValor, "Fornecedor", erros));
            case "fk_marca" -> produto.setFkMarca(ParametrosHttp.lerInt(novoValor, "Marca", erros));
            case "fk_categoria" -> produto.setFkCategoria(ParametrosHttp.lerInt(novoValor, "Categoria", erros));
            case "nome" -> produto.setNome(novoValor);
            case "unidade_medida" -> produto.setUnidadeMedida(novoValor);
            case "estoque_min" -> produto.setEstoqueMin(ParametrosHttp.lerInt(novoValor, "Estoque mínimo", erros));
            case "estoque_max" -> produto.setEstoqueMax(ParametrosHttp.lerInt(novoValor, "Estoque máximo", erros));
            case "descricao" -> produto.setDescricao(novoValor);
            default -> erros.add("A coluna informada não pode ser alterada.");
        }
    }
}
