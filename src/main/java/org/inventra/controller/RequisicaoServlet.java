package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.RequisicaoDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.model.RequisicaoMolde;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "RequisicaoServlet", value = "/requisicao")
public class RequisicaoServlet extends HttpServlet{

    private RequisicaoDAO requisicaoDAO;

    @Override
    public void init() {
        requisicaoDAO = new RequisicaoDAO();
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
                response.setHeader("Content-Disposition", "attachment; filename=\"funcionarios.csv\"");

                List<RequisicaoMolde> requisicoes;
                requisicoes = requisicaoDAO.listarRequisicaoParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); //Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Tipo Requisicão;Funcionário Solicitante;Funcionário Aprovador;Produto;Quantidade;Motivo;Status");

                    for (RequisicaoMolde f : requisicoes) {
                        writer.print(f.getIdRequisicao() + ";");
                        writer.print(f.getIdTipoRequisicao() + ";");
                        writer.print(f.getFkFuncionarioSolicitante() + ";");
                        writer.print(f.getFkFuncionarioAprovador() + ";");
                        writer.print(f.getProduto() + ";");
                        writer.print(f.getQuantidadeProduto() + ";");
                        writer.print(f.getMotivo() + ";");
                        writer.print(f.getStatus());
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

            int totalRegistros = requisicaoDAO.contarRequisicoes();
            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            request.setAttribute("requisicoes", requisicoes);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

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

                requisicaoDAO.deletarRequisicao(id);

                response.sendRedirect(
                        request.getContextPath() + "/requisicao"
                );
                return;
            } else if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                requisicaoDAO.atualizarColunaRequisicao(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/requisicao");

                //Return faz com que pare aqui caso seja escolhido
                return;
            } else if (acao.equals("atualizar_completo")) {
                int id = Integer.parseInt(request.getParameter("id_requisicao"));

                int idTipoRequisicao =
                        Integer.parseInt(request.getParameter("idTipoRequisicao"));
                int quantidadeProduto =
                        Integer.parseInt(request.getParameter("quantidadeProduto"));
                String motivo =
                        request.getParameter("motivo");
                String status =
                        request.getParameter("status");

                String dataHora =
                        request.getParameter("dataHora");

                LocalDateTime dataHoraRequisicao = null;

                if (
                        dataHora != null &&
                                !dataHora.isBlank()
                ) {
                    dataHoraRequisicao = LocalDateTime.parse(dataHora);
                }

                int fkFuncionarioSolicitante =
                        Integer.parseInt(request.getParameter("fkFuncionarioSolicitante"));
                int fkFuncionarioAprovador =
                        Integer.parseInt(request.getParameter("fkFuncionarioAprovador"));
                int fkProduto =
                        Integer.parseInt(request.getParameter("fkProduto"));


                RequisicaoMolde atualizarRequisicao = new RequisicaoMolde(idTipoRequisicao, fkFuncionarioSolicitante, fkFuncionarioAprovador, fkProduto, quantidadeProduto, motivo, status, dataHoraRequisicao);

                requisicaoDAO.atualizarRequisicaoCompleta(atualizarRequisicao, id);

                response.sendRedirect(
                        request.getContextPath() + "/requisicao"
                );

                return;
            }


            int idTipoRequisicao =
                    Integer.parseInt(request.getParameter("idTipoRequisicao"));
            int quantidadeProduto =
                    Integer.parseInt(request.getParameter("quantidadeProduto"));
            String motivo =
                    request.getParameter("motivo");
            String status =
                    request.getParameter("status");

            String dataHora =
                    request.getParameter("dataHora");

            LocalDateTime dataHoraRequisicao = null;

            if (
                    dataHora != null &&
                            !dataHora.isBlank()
            ) {
                dataHoraRequisicao = LocalDateTime.parse(dataHora);
            }

            int fkFuncionarioSolicitante =
                    Integer.parseInt(request.getParameter("fkFuncionarioSolicitante"));
            int fkFuncionarioAprovador =
                    Integer.parseInt(request.getParameter("fkFuncionarioAprovador"));
            int fkProduto =
                    Integer.parseInt(request.getParameter("fkProduto"));


            RequisicaoMolde novaRequisicao = new RequisicaoMolde(idTipoRequisicao, fkFuncionarioSolicitante, fkFuncionarioAprovador, fkProduto, quantidadeProduto, motivo, status, dataHoraRequisicao);

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

}
