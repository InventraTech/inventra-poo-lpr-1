package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.ProdutoDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.model.ProdutoMolde;

import java.io.IOException;
import java.io.PrintWriter;
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
                    writer.println("ID;Nome;Email;Telefone;CPF;Data Admissão;Status;Setor;Admin");

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
                        writer.print(f.isAtivo());
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
                case "validade_proxima" -> {
                    produtos = produtoDAO.listarProdutosPorData(true, limite, offSet);
                }
                case "validade_distante" -> {
                    produtos = produtoDAO.listarProdutosPorData(false, limite, offSet);
                }
                case "az" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(true, limite, offSet);
                }
                case "za" -> {
                    produtos = produtoDAO.listarProdutosAlfabetica(false, limite, offSet);
                }
                case "quantidade" -> {
                    produtos = produtoDAO.listarProdutosQuantidade(limite, offSet);
                }
                case "marca" -> {
                    produtos = produtoDAO.listarProdutosPorMarca(limite, offSet);
                }
                default -> {
                    produtos = produtoDAO.listarProdutos(limite, offSet);
                }
            }

            int totalRegistros = produtoDAO.contarProdutos();
            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            //Enviando as informações principais para a tela do usuarios
            request.setAttribute("produtos", produtos);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            request.getRequestDispatcher(
                    "/WEB-INF/views/lista-produtos.jsp"
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

        try {

            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                produtoDAO.deletarProduto(id);

                response.sendRedirect(
                        request.getContextPath() + "/produto"
                );
                return;
            }

//        Atualiza apenas UMA coluna
            else if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                produtoDAO.atualizarColunaProduto(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/produto");

                //Return faz com que pare aqui caso seja escolhido
                return;
            } else if (acao.equals("atualizar_completo")) {
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
}
