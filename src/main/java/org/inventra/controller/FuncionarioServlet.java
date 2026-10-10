package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.FuncionarioDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.utils.FuncionarioREGEX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "FuncionarioServlet", value = "/funcionario")
public class FuncionarioServlet extends HttpServlet {

    private FuncionarioDAO funcionarioDAO;

    @Override
    public void init() {
        funcionarioDAO = new FuncionarioDAO();
    }


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String filtroSelecionado = request.getParameter("filtro");

        List<FuncionarioMolde> funcionarios;
        funcionarios = funcionarioDAO.listarFuncionario();

        if (filtroSelecionado != null){
            switch (filtroSelecionado){
                case "az" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioAlfabetico(true);
                } case "za" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioAlfabetico(false);
                } case "setor" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorSetor();
                } case "data_crescente" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorData(true);
                } case "data_decrescente" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorData(false);
                }
                default -> {
                    funcionarios = funcionarioDAO.listarFuncionario();
                }
            }
        }

        request.setAttribute("funcionarios", funcionarios);

        request.getRequestDispatcher(
                "/WEB-INF/views/lista-funcionarios.jsp"
        ).forward(request, response);
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

        if (acao.equals("excluir")) {
            int id = Integer.parseInt(request.getParameter("id"));

            funcionarioDAO.deletarFuncionario(id);

            response.sendRedirect(
                    request.getContextPath() + "/funcionario"
            );
            return;
        }

//        Atualiza apenas UMA coluna, chamando o método
        if(acao.equals("atualizar_coluna")){
            int id = Integer.parseInt(request.getParameter("id"));
            String coluna = request.getParameter("coluna");
            String novoValor = request.getParameter("novoValor");

            boolean valorValido;

            switch (coluna){
                case "cpf" -> {
                    valorValido = FuncionarioREGEX.validarCpf(novoValor);
                } case "telefone" -> {
                    valorValido = FuncionarioREGEX.validarTelefone(novoValor);
                } case "email" -> {
                    valorValido = FuncionarioREGEX.validarEmail(novoValor);
                } default -> {
                    //Caso passe uma coluna que não precisa de validação ela passa TRUE (ex: nome)
                    valorValido = true;
                }
            }

            if(!valorValido){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O valor informado para " + coluna + " é inválido");
                return;
            }

            funcionarioDAO.atualizarColunaFuncionario(coluna, novoValor, id);

            response.sendRedirect(request.getContextPath() + "/funcionario");

            //Return faz com que pare aqui caso seja escolhido
            return;

        }
//        Atualizar todas as colunas de uma vez
        else if (acao.equals("atualizar_completo")){
            int id = Integer.parseInt(request.getParameter("id_funcionario"));


            String nome =
                    request.getParameter("nome");
            String senha =
                    request.getParameter("senha");
            String email =
                    request.getParameter("email");
            String telefone =
                    request.getParameter("telefone");
            String cpf =
                    request.getParameter("cpf");
            int fk_setor =
                    Integer.parseInt(request.getParameter("fk_setor"));

            String dt_adimissao =
                    request.getParameter("dt_admissao");

            LocalDate dataAdimissao = null;

            if (
                    dt_adimissao != null &&
                            !dt_adimissao.isBlank()
            ) {
                dataAdimissao = LocalDate.parse(dt_adimissao);
            }

            String status =
                    request.getParameter("status");

            boolean isAdmin =
                    Boolean.parseBoolean(request.getParameter("is_admin"));

            //APLICANDO REGEX
            valoresValidos = FuncionarioREGEX.validandoTodos(cpf, email, telefone);

            if (!valoresValidos){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CPF, Email ou Telefone com formatos inválidos");
                return;
            }

            //INSTANCIANDO OBJETO FUNCIONARIO
            FuncionarioMolde atualizandoTudo = new FuncionarioMolde(nome, senha, email, telefone, cpf, dataAdimissao, status, fk_setor, isAdmin);
            funcionarioDAO.atualizarFuncionarioCompleto(atualizandoTudo, id);

            response.sendRedirect(request.getContextPath() + "/funcionario");
            return;
        }

        //Fluxo de cadastro, só chegara aqui se não se encaixar em nenhuma das 2 de cima

        String nome =
                request.getParameter("nome");

        String senha =
                request.getParameter("senha");

        int fk_setor =
                Integer.parseInt(request.getParameter("fk_setor"));

        String email =
                request.getParameter("email");

        String telefone =
                request.getParameter("telefone");

        String cpf =
                request.getParameter("cpf");

        String dt_adimissao =
                request.getParameter("dt_admissao");

        LocalDate dataAdimissao = null;

        if (
                dt_adimissao != null &&
                        !dt_adimissao.isBlank()
        ) {
            dataAdimissao = LocalDate.parse(dt_adimissao);
        }

        String status =
                request.getParameter("status");

        boolean isAdmin =
                Boolean.parseBoolean(request.getParameter("is_admin"));

        //APLICANDO REGEX
        valoresValidos = FuncionarioREGEX.validandoTodos(cpf, email, telefone);

        if (!valoresValidos){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CPF, Email ou Telefone com formatos inválidos");
            return;
        }

        FuncionarioMolde novoFuncionario = new FuncionarioMolde(nome, senha, email, telefone, cpf, dataAdimissao, status, fk_setor, isAdmin);

        funcionarioDAO.inserirFuncionario(novoFuncionario);

        response.sendRedirect(
                request.getContextPath() + "/funcionario"
        );

    }
}
