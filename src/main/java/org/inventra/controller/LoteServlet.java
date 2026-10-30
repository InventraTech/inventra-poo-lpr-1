package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.LoteDAO;
import org.inventra.model.LoteMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
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
        String acao = request.getParameter("acao");
        //Definindo a páginação:
        int limite = 4;
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

        try{
            if ("exportar".equals(acao)) {
                //Diz para o navegador do usuario que a resposta é em CSV e obriga ele a baixar como anexo
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"lotes.csv\"");

                List<LoteMolde> lotes;
                lotes = loteDao.listarLoteParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); //Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Nome;Email;Telefone;CPF;Data Admissão;Status;Setor;Admin");

                    for (LoteMolde f : lotes) {
                        writer.print(f.getId_lote() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.print(f.getNumero_lote() + ";");
                        writer.print(f.getQtd_inicial() + ";");
                        writer.print(f.getQtd_atual() + ";");
                        writer.print(f.getDt_entrada() + ";");
                        writer.print(f.getDt_validade() + ";");
                        writer.print(f.getValor_compra() + ";");
                        writer.println(f.getNota_fiscal());
                    }
                    writer.flush();
                }
                return;
            }
            List<LoteMolde> lotes = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "entrada_crescente" -> {
                    lotes = loteDao.listarLoteDataEntrada(true, limite, offSet);
                }
                case "entrada_decrescente" -> {
                    lotes = loteDao.listarLoteDataEntrada(false, limite, offSet);
                }
                case "validade_crescente" -> {
                    lotes = loteDao.listarLoteDataValidade(true, limite, offSet);
                }
                case "validade_decrescente" -> {
                    lotes = loteDao.listarLoteDataValidade(false, limite, offSet);
                }
                case "produtos_atual" -> {
                    lotes = loteDao.listarLoteQuantidadeProdutosAtual(limite, offSet);
                }
                default -> {
                    lotes = loteDao.listarLote(limite, offSet);
                }
            }

            int totalRegistros = loteDao.contarLotesPadrao();
            int totalRegistrosVencidos = loteDao.contarLotesVencidos();
            int totalRegistrosVencendo30dias = loteDao.contarLotesVencendo30dias();

            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            request.setAttribute("lotes", lotes);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistros", totalRegistros);
            request.setAttribute("totalRegistrosVencidos", totalRegistrosVencidos);
            request.setAttribute("totalRegistrosVencendo30dias", totalRegistrosVencendo30dias);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            //Notificações do sino do header (lotes mais próximos da validade)
            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/lotes/lista-lotes.jsp"
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

