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

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.LoteValidador;
import java.util.ArrayList;
import java.util.Locale;
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
                    writer.println("ID;Produto;Número Lote;Qtd Inicial;Qtd Atual;Data Entrada;Data Validade;Valor Compra;Nota Fiscal");

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
        if (acao == null){
            acao = "";
        }

        //Mensagens específicas para o usuário: erros de leitura dos parâmetros + lista devolvida pelo validador
        List<String> erros = new ArrayList<>();

        try {
            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                loteDao.deletarLote(id);

                response.sendRedirect(
                        request.getContextPath() + "/lote"
                );
                return;
            }

            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID do lote", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Aplica o novo valor sobre o lote atual e valida o resultado (inclui qtd atual x inicial e entrada x validade)
                if (erros.isEmpty()) {
                    LoteMolde lote = loteDao.buscarLotePorId(id);

                    if (lote == null) {
                        erros.add("Lote não encontrado.");
                    } else {
                        aplicarColuna(lote, coluna, novoValor, erros);

                        if (erros.isEmpty()) {
                            erros.addAll(LoteValidador.validar(lote));
                        }
                    }
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/lote", erros);
                    return;
                }

                //O DAO converte o valor com BigDecimal, que só aceita ponto como separador decimal
                if (coluna.equalsIgnoreCase("valor_compra")) {
                    novoValor = novoValor.replace(",", ".");
                }

                loteDao.atualizarColunaLote(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/lote");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id_lote"), "ID do lote", erros);

                LoteMolde atualizarLote = lerLote(request, erros);

                if (erros.isEmpty()) {
                    erros.addAll(LoteValidador.validar(atualizarLote));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/lote", erros);
                    return;
                }

                loteDao.atualizarLoteCompleto(atualizarLote, id);

                response.sendRedirect(
                        request.getContextPath() + "/lote"
                );

                return;
            }

            LoteMolde novoLote = lerLote(request, erros);

            if (erros.isEmpty()) {
                erros.addAll(LoteValidador.validar(novoLote));
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/lote", erros);
                return;
            }

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

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private LoteMolde lerLote(HttpServletRequest request, List<String> erros) {
        int fk_produto = ParametrosHttp.lerInt(request.getParameter("fk_produto"), "Produto", erros);
        String numero_lote = request.getParameter("numero_lote");
        int qtd_inicial = ParametrosHttp.lerInt(request.getParameter("qtd_inicial"), "Quantidade inicial", erros);
        int qtd_atual = ParametrosHttp.lerInt(request.getParameter("qtd_atual"), "Quantidade atual", erros);
        LocalDate data_entrada = ParametrosHttp.lerData(request.getParameter("dt_entrada"), "entrada", erros);
        LocalDate data_validade = ParametrosHttp.lerData(request.getParameter("dt_validade"), "validade", erros);

        //Valor de compra em branco continua valendo zero (regra que já existia); vírgula ou ponto são aceitos
        BigDecimal valor_compra = ParametrosHttp.lerDecimal(request.getParameter("valor_compra"), "compra", erros);
        if (valor_compra == null) {
            valor_compra = BigDecimal.ZERO;
        }

        String nota_fiscal = request.getParameter("nota_fiscal");

        return new LoteMolde(fk_produto, numero_lote, qtd_inicial, qtd_atual, data_entrada, data_validade, valor_compra, nota_fiscal);
    }

    //Edição por coluna: troca no lote atual só o campo escolhido. Só aceita as colunas que o DAO sabe atualizar.
    private void aplicarColuna(LoteMolde lote, String coluna, String novoValor, List<String> erros) {
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "fk_produto" -> lote.setFk_produto(ParametrosHttp.lerInt(novoValor, "Produto", erros));
            case "numero_lote" -> lote.setNumero_lote(novoValor);
            case "qtd_inicial" -> lote.setQtd_inicial(ParametrosHttp.lerInt(novoValor, "Quantidade inicial", erros));
            case "qtd_atual" -> lote.setQtd_atual(ParametrosHttp.lerInt(novoValor, "Quantidade atual", erros));
            case "dt_entrada" -> lote.setDt_entrada(ParametrosHttp.lerData(novoValor, "entrada", erros));
            case "dt_validade" -> lote.setDt_validade(ParametrosHttp.lerData(novoValor, "validade", erros));
            case "valor_compra" -> lote.setValor_compra(ParametrosHttp.lerDecimal(novoValor, "compra", erros));
            case "nota_fiscal" -> lote.setNota_fiscal(novoValor);
            default -> erros.add("A coluna informada não pode ser alterada.");
        }
    }
}
