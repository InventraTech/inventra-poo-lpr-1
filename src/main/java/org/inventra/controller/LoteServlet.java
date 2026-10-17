package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.LoteDAO;
import org.inventra.model.LoteMolde;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "LoteServlet", value = "/lote")
public class LoteServlet extends HttpServlet{

    private LoteDAO loteDao;

    @Override
    public void init() {
        loteDao = new LoteDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String filtroSelecionado = request.getParameter("filtro");

        try{
        List<LoteMolde> lotes;
        lotes = loteDao.listarLote();

        if(filtroSelecionado != null){
            switch (filtroSelecionado){
                case "entrada_crescente" -> {
                    lotes = loteDao.listarLoteDataEntrada(true);
                }
                case "entrada_decrescente" -> {
                    lotes = loteDao.listarLoteDataEntrada(false);
                }
                case "validade_crescente" -> {
                    lotes = loteDao.listarLoteDataValidade(true);
                }
                case "validade_decrescente" -> {
                    lotes = loteDao.listarLoteDataValidade(false);
                }
                case "produtos_atual" -> {
                    lotes = loteDao.listarLoteQuantidadeProdutosAtual();
                }
                default -> {
                    lotes = loteDao.listarLote();
                }
            }
        }

        request.setAttribute("lotes", lotes);

        request.getRequestDispatcher(
                "/WEB-INF/views/lista-lotes.jsp"
        ).forward(request, response);

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/lote");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/lote");
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
        if (acao == null) {
            acao = "";
        }

        try {
            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                loteDao.deletarLote(id);

                response.sendRedirect(
                        request.getContextPath() + "/lote"
                );
                return;
            } else if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                loteDao.atualizarColunaLote(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/lote");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                int id = Integer.parseInt(request.getParameter("id_lote"));

                int fk_produto =
                        Integer.parseInt(request.getParameter("fk_produto"));
                String numero_lote =
                        request.getParameter("numero_lote");
                int qtd_inicial =
                        Integer.parseInt(request.getParameter("qtd_inicial"));
                int qtd_atual =
                        Integer.parseInt(request.getParameter("qtd_atual"));
                String dt_entrada =
                        request.getParameter("dt_entrada");
                LocalDate data_entrada = null;

                if (
                        dt_entrada != null &&
                                !dt_entrada.isBlank()
                ) {
                    data_entrada = LocalDate.parse(dt_entrada);
                }

                String dt_validade =
                        request.getParameter("dt_validade");
                LocalDate data_validade = null;

                if (
                        dt_validade != null &&
                                !dt_validade.isBlank()
                ) {
                    data_validade = LocalDate.parse(dt_validade);
                }

                BigDecimal valor_compra = BigDecimal.ZERO;
                String valor_compraSTRING = request.getParameter("valor_compra");

                try {
                    if (valor_compraSTRING != null && !valor_compraSTRING.isBlank()) {
                        valor_compra = new BigDecimal(valor_compraSTRING.replace(",", "."));
                    }
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("mensagemErro", "Erro: Valor de compra inválido");
                    response.sendRedirect(request.getContextPath() + "/lote");
                    return;
                }

                String nota_fiscal =
                        request.getParameter("nota_fiscal");

                LoteMolde atualizarLote = new LoteMolde(fk_produto, numero_lote, qtd_inicial, qtd_atual, data_entrada, data_validade, valor_compra, nota_fiscal);

                loteDao.atualizarLoteCompleto(atualizarLote, id);

                response.sendRedirect(
                        request.getContextPath() + "/lote"
                );

                return;
            }

            int fk_produto =
                    Integer.parseInt(request.getParameter("fk_produto"));
            String numero_lote =
                    request.getParameter("numero_lote");
            int qtd_inicial =
                    Integer.parseInt(request.getParameter("qtd_inicial"));
            int qtd_atual =
                    Integer.parseInt(request.getParameter("qtd_atual"));
            String dt_entrada =
                    request.getParameter("dt_entrada");
            LocalDate data_entrada = null;

            if (
                    dt_entrada != null &&
                            !dt_entrada.isBlank()
            ) {
                data_entrada = LocalDate.parse(dt_entrada);
            }

            String dt_validade =
                    request.getParameter("dt_validade");
            LocalDate data_validade = null;

            if (
                    dt_validade != null &&
                            !dt_validade.isBlank()
            ) {
                data_validade = LocalDate.parse(dt_validade);
            }

            BigDecimal valor_compra = BigDecimal.ZERO;
            String valor_compraSTRING = request.getParameter("valor_compra");

            try {
                if (valor_compraSTRING != null && !valor_compraSTRING.isBlank()) {
                    valor_compra = new BigDecimal(valor_compraSTRING.replace(",", "."));
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("mensagemErro", "Erro: Valor de compra inválido");
                response.sendRedirect(request.getContextPath() + "/lote");
                return;
            }

            String nota_fiscal =
                    request.getParameter("nota_fiscal");


            LoteMolde novoLote = new LoteMolde(fk_produto, numero_lote, qtd_inicial, qtd_atual, data_entrada, data_validade, valor_compra, nota_fiscal);

            loteDao.inserirLote(novoLote);

            response.sendRedirect(
                    request.getContextPath() + "/lote"
            );
        } catch (NumberFormatException e){
        request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
        response.sendRedirect(request.getContextPath() + "/lote");
    }
        catch (java.time.format.DateTimeParseException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Data inválida");
            response.sendRedirect(request.getContextPath() + "/lote");
        }
        catch (IllegalArgumentException e){
        request.getSession().setAttribute("mensagemErro", e.getMessage());
        response.sendRedirect(request.getContextPath() + "/lote");
    }
    //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500

    }
}

