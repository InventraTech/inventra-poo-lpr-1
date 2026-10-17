package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.FornecedorDAO;
import org.inventra.model.FornecedorMolde;
import org.inventra.utils.FornecedorREGEX;
import org.inventra.utils.FuncionarioREGEX;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "FornecedorServlet", value = "/fornecedor")
public class FornecedorServlet extends HttpServlet{

    private FornecedorDAO fornecedorDAO;

    @Override
    public void init() {
        fornecedorDAO = new FornecedorDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String filtroSelecionado = request.getParameter("filtro");

        try {

            List<FornecedorMolde> fornecedores;
            fornecedores = fornecedorDAO.listarFornecedor();

            if (filtroSelecionado != null) {
                switch (filtroSelecionado) {
                    case "notaVPQ_crescente" -> {
                        fornecedores = fornecedorDAO.listarFornecedorNotaVPQ(true);
                    }
                    case "notaVPQ_decrescente" -> {
                        fornecedores = fornecedorDAO.listarFornecedorNotaVPQ(false);
                    }
                    default -> {
                        fornecedores = fornecedorDAO.listarFornecedor();
                    }
                }
            }


            request.setAttribute("fornecedores", fornecedores);

            request.getRequestDispatcher(
                    "/WEB-INF/views/lista-fornecedores.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e){
        request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
        response.sendRedirect(request.getContextPath() + "/fornecedor");
    }
        catch (IllegalArgumentException e){
        request.getSession().setAttribute("mensagemErro", e.getMessage());
        response.sendRedirect(request.getContextPath() + "/fornecedor");
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
        boolean valoresValidos;

        //Validando ação para evitar erro por ser NULL
        if (acao == null){
            acao = "";
        }

        try {

            if (acao.equals("excluir")) {
                int id = Integer.parseInt(request.getParameter("id"));

                fornecedorDAO.deletarFornecedor(id);

                response.sendRedirect(
                        request.getContextPath() + "/fornecedor"
                );
                return;
            }

            //Atualiza apenas UMA coluna
            else if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Coluna ou valor não informado");
                    return;
                }

                boolean valorValido;

                switch (coluna) {
                    case "cnpj" -> {
                        valorValido = FornecedorREGEX.validarCNPJ(novoValor);
                    }
                    case "telefone" -> {
                        valorValido = FornecedorREGEX.validarTelefone(novoValor);
                    }
                    case "email" -> {
                        valorValido = FornecedorREGEX.validarEmail(novoValor);
                    }
                    default -> {
                        valorValido = true;
                    }
                }

                if (!valorValido) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O valor informado para " + coluna + " é inválido");
                    return;
                }

                fornecedorDAO.atualizarColunaFornecedor(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/fornecedor");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                int id = Integer.parseInt(request.getParameter("id_fornecedor"));

                int fk_endereco =
                        Integer.parseInt(request.getParameter("fk_endereco"));
                String nome =
                        request.getParameter("nome");
                String cnpj =
                        request.getParameter("cnpj");
                String email =
                        request.getParameter("email");
                String telefone =
                        request.getParameter("telefone");
                int nota_vpq =
                        Integer.parseInt(request.getParameter("nota_vpq"));

                valoresValidos = FornecedorREGEX.validandoTodos(cnpj, email, telefone);

                if (!valoresValidos) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CNPJ, Email ou Telefone com formatos inválidos");
                    return;
                }

                FornecedorMolde atualizarFornecedor = new FornecedorMolde(fk_endereco, nome, cnpj, email, nota_vpq);

                fornecedorDAO.atualizarFornecedorCompleto(atualizarFornecedor, id);

                response.sendRedirect(request.getContextPath() + "/fornecedor");

                //Return faz com que pare aqui caso seja escolhido
                return;
            }

            String nome =
                    request.getParameter("nome");
            String cnpj =
                    request.getParameter("cnpj");
            String email =
                    request.getParameter("email");
            String telefone =
                    request.getParameter("telefone");
            int nota_vpq =
                    Integer.parseInt(request.getParameter("nota_vpq"));
            int fk_regiao =
                    Integer.parseInt(request.getParameter("fk_regiao"));

            valoresValidos = FornecedorREGEX.validandoTodos(cnpj, email, telefone);

            if (!valoresValidos) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CNPJ, Email ou Telefone com formatos inválidos");
                return;
            }

            FornecedorMolde novoFornecedor = new FornecedorMolde(fk_regiao, nome, cnpj, email, nota_vpq);

            fornecedorDAO.inserirFornecedor(novoFornecedor);

            response.sendRedirect(
                    request.getContextPath() + "/fornecedor"
            );

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/fornecedor");
        }
            catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/fornecedor");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }
}
