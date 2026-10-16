package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.ProdutoDAO;
import org.inventra.model.ProdutoMolde;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProdutoServlet", value = "/produto")
public class ProdutoServlet extends HttpServlet {

    private ProdutoDAO produtoDAO;

    @Override
    public void init() {
        produtoDAO = new ProdutoDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String filtroSelecionado = request.getParameter("filtro");

        List<ProdutoMolde> produtos;
        produtos = produtoDAO.listarProdutos();

        if (filtroSelecionado != null){
            switch (filtroSelecionado){
                case "validade_proxima" -> {
                    produtos = produtoDAO.listarProdutosPorData(true);
                } case "validade_distante" -> {
                    produtos = produtoDAO.listarProdutosPorData(false);
                } case "az" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(true);
                } case "za" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(false);
                } case "quantidade" -> {
                    produtos = produtoDAO.listarProdutosQuantidade();
                } case "marca" -> {
                    produtos = produtoDAO.listarProdutosPorMarca();
                }
                default -> {
                    produtos = produtoDAO.listarProdutos();
                }
            }
        }

        request.setAttribute("produtos", produtos);

        request.getRequestDispatcher(
                "/WEB-INF/views/lista-produtos.jsp"
        ).forward(request, response);

    }

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

        if(acao.equals("excluir")) {
            int id = Integer.parseInt(request.getParameter("id"));

            produtoDAO.deletarProduto(id);

            response.sendRedirect(
                    request.getContextPath() + "/produto"
            );
            return;
        }

//        Atualiza apenas UMA coluna
        else if(acao.equals("atualizar_coluna")){
            int id = Integer.parseInt(request.getParameter("id"));
            String coluna = request.getParameter("coluna");
            String novoValor = request.getParameter("novoValor");

            produtoDAO.atualizarColunaProduto(coluna, novoValor, id);

            response.sendRedirect(request.getContextPath() + "/produto");

            //Return faz com que pare aqui caso seja escolhido
            return;
        }

        else if (acao.equals("atualizar_completo")){
            int id = Integer.parseInt(request.getParameter("id_produto"));

            String nome =
                    request.getParameter("nome");
            int fk_marca =
                    Integer.parseInt(request.getParameter("fk_marca"));
            int fk_categoria =
                    Integer.parseInt(request.getParameter("fk_categoria"));
            String unidadeMedida =
                    request.getParameter("unidadeMedida");
            int estoqueMin =
                    Integer.parseInt(request.getParameter("estoqueMin"));
            int estoqueMax =
                    Integer.parseInt(request.getParameter("estoqueMax"));
            int fk_fornecedor =
                    Integer.parseInt(request.getParameter("fk_fornecedor"));
            String descricao =
                    request.getParameter("descricao");
            boolean ativo =
                    Boolean.parseBoolean(request.getParameter("ativo"));

            ProdutoMolde atualizandoProduto = new ProdutoMolde(fk_fornecedor, fk_marca, fk_categoria, nome, unidadeMedida, estoqueMin, estoqueMax, descricao, ativo);

            produtoDAO.atualizarProdutoCompleto(atualizandoProduto, id);

            response.sendRedirect(
                    request.getContextPath() + "/produto"
            );

            return;
        }

        String nome =
                request.getParameter("nome");
        int fk_marca =
                Integer.parseInt(request.getParameter("fk_marca"));
        int fk_categoria =
                Integer.parseInt(request.getParameter("fk_categoria"));
        String unidadeMedida =
                request.getParameter("unidadeMedida");
        int estoqueMin =
                Integer.parseInt(request.getParameter("estoqueMin"));
        int estoqueMax =
                Integer.parseInt(request.getParameter("estoqueMax"));
        int fk_fornecedor =
                Integer.parseInt(request.getParameter("fk_fornecedor"));
        String descricao =
                request.getParameter("descricao");
        boolean ativo =
                Boolean.parseBoolean(request.getParameter("ativo"));

        ProdutoMolde novoProduto = new ProdutoMolde(fk_fornecedor, fk_marca, fk_categoria, nome, unidadeMedida, estoqueMin, estoqueMax, descricao, ativo);

        produtoDAO.inserirProduto(novoProduto);

        response.sendRedirect(
                request.getContextPath() + "/produto"
        );
    }
}
