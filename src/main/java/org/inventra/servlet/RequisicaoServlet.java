package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.RequisicaoDAO;
import org.inventra.model.RequisicaoMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.RequisicaoValidador;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Controla a tela de requisições: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo RequisicaoValidador antes de chegar ao DAO.
 */
@WebServlet(name = "RequisicaoServlet", value = "/requisicao")
public class RequisicaoServlet extends HttpServlet{

    private RequisicaoDAO requisicaoDAO;

    @Override
    public void init() {
        requisicaoDAO = new RequisicaoDAO();
    }

    /**
     * Exibe a lista de requisições. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
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

        try {
            if ("exportar".equals(acao)){
                //Diz para o navegador do usuario que a resposta é em CSV e obriga ele a baixar como anexo
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"requisicoes.csv\"");

                List<RequisicaoMolde> requisicoes;
                requisicoes = requisicaoDAO.listarRequisicaoParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); //Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Tipo Requisicão;Funcionário Solicitante;Funcionário Aprovador;Produto;Quantidade;Motivo;Status");

                    for (RequisicaoMolde f : requisicoes) {
                        writer.print(f.getIdRequisicao() + ";");
                        writer.print(f.getTipoRequisicao() + ";");
                        writer.print(f.getFuncionarioSolicitante() + ";");
                        writer.print(f.getFuncionarioAprovador() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.print(f.getQuantidadeProduto() + ";");
                        writer.print(f.getMotivo() + ";");
                        writer.println(f.getStatus());
                    }
                    writer.flush();
                }
                return;
            }

            List<RequisicaoMolde> requisicoes = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "tipo" -> {
                    requisicoes = requisicaoDAO.listarRequisicoesPorTipo(limite, offSet);
                }
                case "quantidade_crescente" -> {
                    requisicoes = requisicaoDAO.listarRequisicoesPorQuantidadeProduto(true, limite, offSet);
                }
                case "quantidade_decrescente" -> {
                    requisicoes = requisicaoDAO.listarRequisicoesPorQuantidadeProduto(false, limite, offSet);
                }
                case "data_proxima" -> {
                    requisicoes = requisicaoDAO.listarRequisicoesPorData(true, limite, offSet);
                }
                case "data_distante" -> {
                    requisicoes = requisicaoDAO.listarRequisicoesPorData(false, limite, offSet);
                }
                default -> {
                    requisicoes = requisicaoDAO.listarRequisicoes(limite, offSet);
                }
            }

            int totalRegistros = requisicaoDAO.contarRequisicoesPadrao();
            int totalAprovados = requisicaoDAO.contarRequisicoesFiltro(true);
            int totalPendentes = requisicaoDAO.contarRequisicoesFiltro(false);
            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            request.setAttribute("requisicoes", requisicoes);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalRegistros", totalRegistros);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistrosAprovados", totalAprovados);
            request.setAttribute("totalRegistrosPendentes", totalPendentes);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            //Notificações do sino do header (lotes mais próximos da validade)
            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/requisicao/lista-requisicoes.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/requisicao");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/requisicao");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500

    }

    /**
     * Trata o formulário de requisições. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
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

                requisicaoDAO.deletarRequisicao(id);

                response.sendRedirect(
                        request.getContextPath() + "/requisicao"
                );
                return;
            }

            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID da requisição", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Aplica o novo valor sobre a requisição atual e valida o resultado (inclui solicitante x aprovador)
                if (erros.isEmpty()) {
                    RequisicaoMolde requisicao = requisicaoDAO.buscarRequisicaoPorId(id);

                    if (requisicao == null) {
                        erros.add("Requisição não encontrada.");
                    } else {
                        aplicarColuna(requisicao, coluna, novoValor, erros);

                        if (erros.isEmpty()) {
                            erros.addAll(RequisicaoValidador.validar(requisicao));
                        }
                    }
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/requisicao", erros);
                    return;
                }

                requisicaoDAO.atualizarColunaRequisicao(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/requisicao");

                //Return faz com que pare aqui caso seja escolhido
                return;
            } else if (acao.equals("atualizar_completo")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id_requisicao"), "ID da requisição", erros);

                RequisicaoMolde atualizarRequisicao = lerRequisicao(request, erros);

                if (erros.isEmpty()) {
                    erros.addAll(RequisicaoValidador.validar(atualizarRequisicao));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/requisicao", erros);
                    return;
                }

                requisicaoDAO.atualizarRequisicaoCompleta(atualizarRequisicao, id);

                response.sendRedirect(
                        request.getContextPath() + "/requisicao"
                );

                return;
            }

            RequisicaoMolde novaRequisicao = lerRequisicao(request, erros);

            if (erros.isEmpty()) {
                erros.addAll(RequisicaoValidador.validar(novaRequisicao));
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/requisicao", erros);
                return;
            }

            requisicaoDAO.inserirRequisicao(novaRequisicao);

            response.sendRedirect(
                    request.getContextPath() + "/requisicao"
            );

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/requisicao");
        }
        catch (java.time.format.DateTimeParseException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Data inválida");
            response.sendRedirect(request.getContextPath() + "/requisicao");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/requisicao");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private RequisicaoMolde lerRequisicao(HttpServletRequest request, List<String> erros) {
        int idTipoRequisicao = ParametrosHttp.lerInt(request.getParameter("idTipoRequisicao"), "Tipo de requisição", erros);
        int quantidadeProduto = ParametrosHttp.lerInt(request.getParameter("quantidadeProduto"), "Quantidade", erros);
        String motivo = request.getParameter("motivo");
        String status = request.getParameter("status");
        LocalDateTime dataHoraRequisicao = ParametrosHttp.lerDataHora(request.getParameter("dataHora"), "requisição", erros);
        int fkFuncionarioSolicitante = ParametrosHttp.lerInt(request.getParameter("fkFuncionarioSolicitante"), "Solicitante", erros);
        int fkFuncionarioAprovador = ParametrosHttp.lerInt(request.getParameter("fkFuncionarioAprovador"), "Aprovador", erros);
        int fkProduto = ParametrosHttp.lerInt(request.getParameter("fkProduto"), "Produto", erros);

        return new RequisicaoMolde(idTipoRequisicao, fkFuncionarioSolicitante, fkFuncionarioAprovador, fkProduto, quantidadeProduto, motivo, status, dataHoraRequisicao);
    }

    //Edição por coluna: troca na requisição atual só o campo escolhido. Só aceita as colunas que o DAO sabe atualizar.
    private void aplicarColuna(RequisicaoMolde requisicao, String coluna, String novoValor, List<String> erros) {
        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "fk_tipo_requisicao" -> requisicao.setIdTipoRequisicao(ParametrosHttp.lerInt(novoValor, "Tipo de requisição", erros));
            case "quantidade_prod" -> requisicao.setQuantidadeProduto(ParametrosHttp.lerInt(novoValor, "Quantidade", erros));
            case "motivo" -> requisicao.setMotivo(novoValor);
            case "status" -> requisicao.setStatus(novoValor);
            case "dt_requisicao" -> {
                if (novoValor.isBlank()) {
                    erros.add("A data e hora da requisição é obrigatória.");
                } else {
                    requisicao.setDataHora(ParametrosHttp.lerDataHora(novoValor, "requisição", erros));
                }
            }
            case "fk_funcionario_solicitante" -> requisicao.setFkFuncionarioSolicitante(ParametrosHttp.lerInt(novoValor, "Solicitante", erros));
            case "fk_funcionario_aprovador" -> requisicao.setFkFuncionarioAprovador(ParametrosHttp.lerInt(novoValor, "Aprovador", erros));
            case "fk_produto" -> requisicao.setFkProduto(ParametrosHttp.lerInt(novoValor, "Produto", erros));
            default -> erros.add("A coluna informada não pode ser alterada.");
        }
    }
}
