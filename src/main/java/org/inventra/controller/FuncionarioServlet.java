package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.FuncionarioDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.utils.FuncionarioREGEX;

import java.io.PrintWriter;
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
        String acao = request.getParameter("acao");
        List<FuncionarioMolde> funcionarios;
        funcionarios = funcionarioDAO.listarFuncionario();

        try {
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

            if ("exportar".equals(acao)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"funcionarios.csv\"");

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); // Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Nome;Email;Telefone;CPF;Data Admissão;Status;Setor;Admin");

                    for (FuncionarioMolde f : funcionarios) {
                        writer.print(f.getId_funcionario() + ";");
                        writer.print(f.getNome() + ";");
                        writer.print(f.getEmail() + ";");
                        writer.print(f.getTelefone() + ";");
                        writer.print(f.getCpf() + ";");
                        writer.print(f.getDt_admissao() + ";");
                        writer.print(f.getStatus() + ";");
                        writer.print(f.getSetor() + ";");
                        writer.println(f.isAdmin());
                    }
                    writer.flush();
                }
                return;
            }
            request.setAttribute("funcionarios", funcionarios);
            request.getRequestDispatcher(
                    "/WEB-INF/views/lista-funcionarios.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/funcionario");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/funcionario");
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

                funcionarioDAO.deletarFuncionario(id);

                response.sendRedirect(
                        request.getContextPath() + "/funcionario"
                );
                return;
            }

//        Atualiza apenas UMA coluna, chamando o método
            if (acao.equals("atualizar_coluna")) {
                int id = Integer.parseInt(request.getParameter("id"));
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Coluna ou valor não informado");
                    return;
                }

                boolean valorValido;

                switch (coluna) {
                    case "cpf" -> {
                        valorValido = FuncionarioREGEX.validarCpf(novoValor);
                    }
                    case "telefone" -> {
                        valorValido = FuncionarioREGEX.validarTelefone(novoValor);
                    }
                    case "email" -> {
                        valorValido = FuncionarioREGEX.validarEmail(novoValor);
                    }
                    default -> {
                        //Caso passe uma coluna que não precisa de validação ela passa TRUE (ex: nome)
                        valorValido = true;
                    }
                }

                if (!valorValido) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "O valor informado para " + coluna + " é inválido");
                    return;
                }

                funcionarioDAO.atualizarColunaFuncionario(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/funcionario");

                //Return faz com que pare aqui caso seja escolhido
                return;

            }
//        Atualizar todas as colunas de uma vez
            else if (acao.equals("atualizar_completo")) {
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

                if (!valoresValidos) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CPF, Email ou Telefone com formatos inválidos");
                    return;
                }

                //INSTANCIANDO OBJETO FUNCIONARIO
                FuncionarioMolde atualizandoTudo = new FuncionarioMolde(fk_setor, nome, senha, email, telefone, cpf, dataAdimissao, status, isAdmin);
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

            if (!valoresValidos) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erro: CPF, Email ou Telefone com formatos inválidos");
                return;
            }

            FuncionarioMolde novoFuncionario = new FuncionarioMolde(fk_setor, nome, senha, email, telefone, cpf, dataAdimissao, status, isAdmin);

            funcionarioDAO.inserirFuncionario(novoFuncionario);

            response.sendRedirect(
                    request.getContextPath() + "/funcionario"
            );

        } catch (NumberFormatException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Parâmetro numérico inválido");
            response.sendRedirect(request.getContextPath() + "/funcionario");
        }
        catch (java.time.format.DateTimeParseException e){
            request.getSession().setAttribute("mensagemErro", "Erro: Data inválida");
            response.sendRedirect(request.getContextPath() + "/funcionario");
        }
        catch (IllegalArgumentException e){
            request.getSession().setAttribute("mensagemErro", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/funcionario");
        }
        //Erros vindos do DAO, erros internos do servidor sobem direto pro tomcat que intercepta e lança a tela de erro 500
    }
}
