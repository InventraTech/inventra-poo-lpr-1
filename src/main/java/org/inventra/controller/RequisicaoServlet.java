package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.RequisicaoDAO;
import org.inventra.model.RequisicaoMolde;

import java.io.IOException;
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

        List<RequisicaoMolde> requisicoes = requisicaoDAO.listarRequisicoes();

        request.setAttribute("requisicoes", requisicoes);

        request.getRequestDispatcher(
                "/WEB-INF/views/lista-requisicoes.jsp"
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

            requisicaoDAO.deletarRequisicao(id);

            response.sendRedirect(
                    request.getContextPath() + "/requisicao"
            );
            return;
        }

        else if (acao.equals("atualizar_coluna")){
            int id = Integer.parseInt(request.getParameter("id"));
            String coluna = request.getParameter("coluna");
            String novoValor = request.getParameter("novoValor");

            requisicaoDAO.atualizarColunaRequisicao(coluna, novoValor, id);

            response.sendRedirect(request.getContextPath() + "/requisicao");

            //Return faz com que pare aqui caso seja escolhido
            return;
        }

        else if (acao.equals("atualizar_completo")){
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


            RequisicaoMolde atualizarRequisicao = new RequisicaoMolde(idTipoRequisicao, quantidadeProduto, motivo, status, dataHoraRequisicao, fkFuncionarioSolicitante, fkFuncionarioAprovador, fkProduto );

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


        RequisicaoMolde novaRequisicao = new RequisicaoMolde(idTipoRequisicao, quantidadeProduto, motivo, status, dataHoraRequisicao, fkFuncionarioSolicitante, fkFuncionarioAprovador, fkProduto );

        requisicaoDAO.inserirRequisicao(novaRequisicao);

        response.sendRedirect(
                request.getContextPath() + "/requisicao"
        );
    }

}
