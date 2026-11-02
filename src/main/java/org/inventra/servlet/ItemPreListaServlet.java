package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.ItemPreListaDAO;
import org.inventra.model.ItemPreListaMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.ItemPreListaValidador;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Controla a tela de itens da pré-lista: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo ItemPreListaValidador antes de chegar ao DAO.
 */
@WebServlet(name = "ItemPreListaServlet", value = "/itemprelista")
public class ItemPreListaServlet extends HttpServlet {

    private ItemPreListaDAO itemPreListaDAO;

    @Override
    public void init() {
        itemPreListaDAO = new ItemPreListaDAO();
    }

    /**
     * Exibe a lista de itens da pré-lista. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
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
            if ("exportar".equals(acao)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"itens-pre-lista.csv\"");

                List<ItemPreListaMolde> itensPreLista;
                itensPreLista = itemPreListaDAO.listarItemPreListaParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); // Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Fornecedor;Produto;Quantidade");

                    for (ItemPreListaMolde f : itensPreLista) {
                        writer.print(f.getIdItemPreLista() + ";");
                        writer.print(f.getFornecedor() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.println(f.getQtd());
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

    /**
     * Trata o formulário de itens da pré-lista. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
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

                itemPreListaDAO.deletarItemPreLista(id);

                response.sendRedirect(
                        request.getContextPath() + "/itemprelista"
                );
                return;
            }

            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID do item", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Aplica o novo valor sobre o item atual e valida o resultado
                if (erros.isEmpty()) {
                    ItemPreListaMolde item = itemPreListaDAO.buscarItemPreListaPorId(id);

                    if (item == null) {
                        erros.add("Item da pré-lista não encontrado.");
                    } else {
                        aplicarColuna(item, coluna, novoValor, erros);

                        if (erros.isEmpty()) {
                            erros.addAll(ItemPreListaValidador.validar(item));
                        }
                    }
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/itemprelista", erros);
                    return;
                }

                itemPreListaDAO.atualizarColunaItemPreLista(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/itemprelista");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                //O formulário de edição envia o ID do item no parâmetro "id_item_lista"
                int id = ParametrosHttp.lerInt(request.getParameter("id_item_lista"), "ID do item", erros);

                ItemPreListaMolde atualizarItemPreLista = lerItemPreLista(request, erros);

                if (erros.isEmpty()) {
                    erros.addAll(ItemPreListaValidador.validar(atualizarItemPreLista));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/itemprelista", erros);
                    return;
                }

                itemPreListaDAO.atualizarItemPreListaCompleto(atualizarItemPreLista, id);

                response.sendRedirect(
                        request.getContextPath() + "/itemprelista"
                );

                return;
            }

            ItemPreListaMolde novoItemPreLista = lerItemPreLista(request, erros);

            if (erros.isEmpty()) {
                erros.addAll(ItemPreListaValidador.validar(novoItemPreLista));
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/itemprelista", erros);
                return;
            }

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

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private ItemPreListaMolde lerItemPreLista(HttpServletRequest request, List<String> erros) {
        int fkPreLista = ParametrosHttp.lerInt(request.getParameter("fk_pre_lista"), "Pré-lista", erros);

        int fkFornecedor = ParametrosHttp.lerInt(request.getParameter("fk_fornecedor"), "Fornecedor", erros);

        int fkProduto = ParametrosHttp.lerInt(request.getParameter("fk_produto"), "Produto", erros);

        int qtd = ParametrosHttp.lerInt(request.getParameter("quantidade"), "Quantidade", erros);

        return new ItemPreListaMolde(fkPreLista, fkFornecedor, fkProduto, qtd);
    }

    //Edição por coluna: troca no item atual só o campo escolhido. Só aceita as colunas que o DAO sabe atualizar.
    private void aplicarColuna(ItemPreListaMolde item, String coluna, String novoValor, List<String> erros) {
        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "fk_pre_lista" -> {
                item.setFkPreLista(ParametrosHttp.lerInt(novoValor, "Pré-lista", erros));
            }
            case "fk_fornecedor" -> {
                item.setFkFornecedor(ParametrosHttp.lerInt(novoValor, "Fornecedor", erros));
            }
            case "fk_produto" -> {
                item.setFkProduto(ParametrosHttp.lerInt(novoValor, "Produto", erros));
            }
            case "qtd" -> {
                item.setQtd(ParametrosHttp.lerInt(novoValor, "Quantidade", erros));
            }
            default -> erros.add("A coluna informada não pode ser alterada.");
        }
    }
}
