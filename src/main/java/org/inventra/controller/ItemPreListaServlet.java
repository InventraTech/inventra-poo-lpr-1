package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.ItemPreListaDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.model.ItemPreListaMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ItemPreListaServlet", value = "/itemprelista")
public class ItemPreListaServlet extends HttpServlet {

    private ItemPreListaDAO itemPreListaDAO;

    @Override
    public void init() {
        itemPreListaDAO = new ItemPreListaDAO();
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
            if ("exportar".equals(acao)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"itens-pre-lista.csv\"");

                List<ItemPreListaMolde> itensPreLista;
                itensPreLista = itemPreListaDAO.listarItensPreLista(limite, offSet);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); // Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Fornecedor;Produto;Quantidade");

                    for (ItemPreListaMolde f : itensPreLista) {
                        writer.print(f.getId_item_prelista() + ";");
                        writer.print(f.getFornecedor() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.print(f.getQtd() + ";");
                    }
                    writer.flush();
                }
                return;
            }
            List<ItemPreListaMolde> itensPreLista = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "fornecedor" -> {
                    itensPreLista = itemPreListaDAO.listarItensPreListaPorFornecedores(limite, offSet);
                }
                case "produto" -> {
                    itensPreLista = itemPreListaDAO.listarItensPreListaPorProduto(limite, offSet);
                }
                case "quantidade_crescente" -> {
                    itensPreLista = itemPreListaDAO.listarItensPreListaPorQuantidade(true, limite, offSet);
                }
                case "quantidade_decrescente" -> {
                    itensPreLista = itemPreListaDAO.listarItensPreListaPorQuantidade(false, limite, offSet);
                }
                default -> {
                    itensPreLista = itemPreListaDAO.listarItensPreLista(limite, offSet);
                }
            }

            int totalRegistros = itemPreListaDAO.contarItensPreListaPadrao();
            int totalRegistrosSomados = itemPreListaDAO.somarUnidadesItensPreListaPadrao();
            int totalFornecedoresVinculados = itemPreListaDAO.contarFornecedoresVinculados();

            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            request.setAttribute("itensprelista", itensPreLista);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistros", totalRegistros );
            request.setAttribute("totalRegistrosSomados", totalRegistrosSomados);
            request.setAttribute("totalFornecedoresVinculados", totalFornecedoresVinculados);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            //Notificações do sino do header (lotes mais próximos da validade)
            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/pre-lista/lista-itemprelista.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e){
        request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
        response.sendRedirect(request.getContextPath() + "/itemprelista");
    }
        catch (IllegalArgumentException e){
        request.getSession().setAttribute("mensagemErro", e.getMessage());
        response.sendRedirect(request.getContextPath() + "/itemprelista");
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

        if (acao == null){
            acao = "";
        }

        try {

            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                itemPreListaDAO.deletarItemPreLista(id);

                response.sendRedirect(
                        request.getContextPath() + "/itemprelista"
                );
                return;
            } else if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                itemPreListaDAO.atualizarColunaItemPreLista(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/itemprelista");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                int id = Integer.parseInt(request.getParameter("id_fornecedor"));

                int fk_pre_lista =
                        Integer.parseInt(request.getParameter("fk_pre_lista"));
                int fk_fornecedor =
                        Integer.parseInt(request.getParameter("fk_fornecedor"));
                int fk_produto =
                        Integer.parseInt(request.getParameter("fk_prduto"));
                int qtd =
                        Integer.parseInt(request.getParameter("quantidade"));


                ItemPreListaMolde atualizarItemPreLista = new ItemPreListaMolde(fk_pre_lista, fk_fornecedor, fk_produto, qtd);

                itemPreListaDAO.atualizarItemPreListaCompleto(atualizarItemPreLista, id);

                response.sendRedirect(
                        request.getContextPath() + "/itemprelista"
                );

                return;
            }

            int fk_pre_lista =
                    Integer.parseInt(request.getParameter("fk_pre_lista"));
            int fk_fornecedor =
                    Integer.parseInt(request.getParameter("fk_fornecedor"));
            int fk_produto =
                    Integer.parseInt(request.getParameter("fk_prduto"));
            int qtd =
                    Integer.parseInt(request.getParameter("quantidade"));


            ItemPreListaMolde novoItemPreLista = new ItemPreListaMolde(fk_pre_lista, fk_fornecedor, fk_produto, qtd);

            itemPreListaDAO.inserirItemPreLista(novoItemPreLista);

            response.sendRedirect(
                    request.getContextPath() + "/itemprelista"
            );
        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/itemprelista");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/itemprelista");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }
}

