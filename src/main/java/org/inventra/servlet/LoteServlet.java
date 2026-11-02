package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.LoteDAO;
import org.inventra.dao.OpcoesDAO;
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
/**
 * Controla a tela de lotes: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo LoteValidador antes de chegar ao DAO.
 */
@WebServlet(name = "LoteServlet", value = "/lote")
public class LoteServlet extends HttpServlet{

    private LoteDAO loteDao;

    @Override
    public void init() {
        loteDao = new LoteDAO();
    }

    /**
     * Exibe a lista de lotes. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
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
                        writer.print(f.getIdLote() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.print(f.getNumeroLote() + ";");
                        writer.print(f.getQtdInicial() + ";");
                        writer.print(f.getQtdAtual() + ";");
                        writer.print(f.getDtEntrada() + ";");
                        writer.print(f.getDtValidade() + ";");
                        writer.print(f.getValorCompra() + ";");
                        writer.println(f.getNotaFiscal());
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
            request.setAttribute("opcoesProdutos", OpcoesDAO.produtos());
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

    /**
     * Trata o formulário de lotes. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
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
        int fkProduto = ParametrosHttp.lerInt(request.getParameter("fk_produto"), "Produto", erros);
        String numeroLote = request.getParameter("numero_lote");
        int qtdInicial = ParametrosHttp.lerInt(request.getParameter("qtd_inicial"), "Quantidade inicial", erros);
        int qtdAtual = ParametrosHttp.lerInt(request.getParameter("qtd_atual"), "Quantidade atual", erros);
        LocalDate dataEntrada = ParametrosHttp.lerData(request.getParameter("dt_entrada"), "entrada", erros);
        LocalDate dataValidade = ParametrosHttp.lerData(request.getParameter("dt_validade"), "validade", erros);

        //Valor de compra em branco continua valendo zero (regra que já existia); vírgula ou ponto são aceitos
        BigDecimal valorCompra = ParametrosHttp.lerDecimal(request.getParameter("valor_compra"), "compra", erros);
        if (valorCompra == null) {
            valorCompra = BigDecimal.ZERO;
        }

        String notaFiscal = request.getParameter("nota_fiscal");

        return new LoteMolde(fkProduto, numeroLote, qtdInicial, qtdAtual, dataEntrada, dataValidade, valorCompra, notaFiscal);
    }

    //Edição por coluna: troca no lote atual só o campo escolhido. Só aceita as colunas que o DAO sabe atualizar.
    private void aplicarColuna(LoteMolde lote, String coluna, String novoValor, List<String> erros) {
        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "fk_produto" -> lote.setFkProduto(ParametrosHttp.lerInt(novoValor, "Produto", erros));
            case "numero_lote" -> lote.setNumeroLote(novoValor);
            case "qtd_inicial" -> lote.setQtdInicial(ParametrosHttp.lerInt(novoValor, "Quantidade inicial", erros));
            case "qtd_atual" -> lote.setQtdAtual(ParametrosHttp.lerInt(novoValor, "Quantidade atual", erros));
            case "dt_entrada" -> lote.setDtEntrada(ParametrosHttp.lerData(novoValor, "entrada", erros));
            case "dt_validade" -> lote.setDtValidade(ParametrosHttp.lerData(novoValor, "validade", erros));
            case "valor_compra" -> lote.setValorCompra(ParametrosHttp.lerDecimal(novoValor, "compra", erros));
            case "nota_fiscal" -> lote.setNotaFiscal(novoValor);
            default -> erros.add("A coluna informada não pode ser alterada.");
        }
    }
}
