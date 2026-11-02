package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.FuncionarioDAO;
import org.inventra.model.FuncionarioMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.PrintWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.FuncionarioValidador;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Controla a tela de funcionários: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo FuncionarioValidador antes de chegar ao DAO.
 */
@WebServlet(name = "FuncionarioServlet", value = "/funcionario")
public class FuncionarioServlet extends HttpServlet {

    private FuncionarioDAO funcionarioDAO;

    @Override
    public void init() {
        funcionarioDAO = new FuncionarioDAO();
    }


    /**
     * Exibe a lista de funcionários. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
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
            if ("exportar".equals(acao)) {
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"funcionarios.csv\"");

                List<FuncionarioMolde> funcionarios;
                funcionarios = funcionarioDAO.listarFuncionarioParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); // Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Nome;Email;CPF;Data Admissão;Status;Setor;Admin");

                    for (FuncionarioMolde f : funcionarios) {
                        writer.print(f.getIdFuncionario() + ";");
                        writer.print(f.getNome() + ";");
                        writer.print(f.getEmail() + ";");
                        writer.print(f.getCpf() + ";");
                        writer.print(f.getDtAdmissao() + ";");
                        writer.print(f.getStatus() + ";");
                        writer.print(f.getSetor() + ";");
                        writer.println(f.isAdmin());
                    }
                    writer.flush();
                }
                return;
            }

            List<FuncionarioMolde> funcionarios = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "az" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioAlfabetico(true, limite, offSet);
                }
                case "za" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioAlfabetico(false, limite, offSet);
                }
                case "setor" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorSetor(limite, offSet);
                }
                case "data_crescente" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorData(true, limite, offSet);
                }
                case "data_decrescente" -> {
                    funcionarios = funcionarioDAO.listarFuncionarioPorData(false, limite, offSet);
                }
                default -> {
                    funcionarios = funcionarioDAO.listarFuncionario(limite, offSet);
                }
            }

            int totalRegistros = funcionarioDAO.contarFuncionariosPadrao();
            int totalAdmins = funcionarioDAO.contarFuncionariosAdmins();
            int totalSetores = funcionarioDAO.contarSetores();
            int totalAtivos = funcionarioDAO.contarAtivos();

            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

           //Enviando as informações principais para a tela do usuario
            request.setAttribute("funcionarios", funcionarios);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalRegistros", totalRegistros);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalSetores", totalSetores);
            request.setAttribute("totalAtivos", totalAtivos);
            request.setAttribute("totalAdmins", totalAdmins);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/funcionarios/lista-funcionarios.jsp"
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

    /**
     * Trata o formulário de funcionários. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
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

                funcionarioDAO.deletarFuncionario(id);

                response.sendRedirect(
                        request.getContextPath() + "/funcionario"
                );
                return;
            }

            //Atualiza apenas UMA coluna, chamando o método
            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID do funcionário", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Valida o novo valor (inclui o formato de CPF e e-mail) usando o FuncionarioValidador
                if (erros.isEmpty()) {
                    validarColuna(coluna, novoValor, erros);
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/funcionario", erros);
                    return;
                }

                funcionarioDAO.atualizarColunaFuncionario(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/funcionario");

                //Return faz com que pare aqui caso seja escolhido
                return;

            }
            //Atualizar todas as colunas de uma vez
            else if (acao.equals("atualizar_completo")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id_funcionario"), "ID do funcionário", erros);

                //INSTANCIANDO OBJETO FUNCIONARIO
                FuncionarioMolde atualizandoTudo = lerFuncionario(request, erros);

                //APLICANDO VALIDAÇÕES (nome, setor, e-mail, CPF, admissão e status)
                if (erros.isEmpty()) {
                    erros.addAll(FuncionarioValidador.validar(atualizandoTudo));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/funcionario", erros);
                    return;
                }

                //Senha em branco na edição completa = mantém a senha atual (regra do DAO)
                funcionarioDAO.atualizarFuncionarioCompleto(atualizandoTudo, id);

                response.sendRedirect(request.getContextPath() + "/funcionario");
                return;
            }

            //Fluxo de cadastro, só chegara aqui se não se encaixar em nenhuma das 2 de cima
            FuncionarioMolde novoFuncionario = lerFuncionario(request, erros);

            //APLICANDO VALIDAÇÕES
            if (erros.isEmpty()) {
                erros.addAll(FuncionarioValidador.validar(novoFuncionario));
            }

            //No cadastro a senha é obrigatória (o DAO gera o hash dela)
            if (novoFuncionario.getSenha() == null || novoFuncionario.getSenha().isBlank()) {
                erros.add("A senha é obrigatória.");
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/funcionario", erros);
                return;
            }

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

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private FuncionarioMolde lerFuncionario(HttpServletRequest request, List<String> erros) {
        String nome = request.getParameter("nome");
        String senha = request.getParameter("senha");
        int fkSetor = ParametrosHttp.lerInt(request.getParameter("fk_setor"), "Setor", erros);
        String email = request.getParameter("email");
        String cpf = request.getParameter("cpf");
        LocalDate dataAdmissao = ParametrosHttp.lerData(request.getParameter("dt_admissao"), "admissão", erros);
        String status = request.getParameter("status");
        boolean isAdmin = Boolean.parseBoolean(request.getParameter("is_admin"));

        return new FuncionarioMolde(fkSetor, nome, senha, email, cpf, dataAdmissao, status, isAdmin);
    }

    //Edição por coluna: parte de um funcionário "base" válido e troca só o campo editado, para o FuncionarioValidador
    //avaliar apenas o novo valor. A senha não passa pelo validador (o DAO a transforma em hash e recusa vazio).
    private void validarColuna(String coluna, String novoValor, List<String> erros) {
        FuncionarioMolde funcionario = new FuncionarioMolde(1, "Nome", null, "valido@inventra.com",
                "12345678909", LocalDate.of(2000, 1, 1), "Ativo", false);

        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "nome" -> funcionario.setNome(novoValor);
            case "senha" -> {
                if (novoValor.isBlank()) {
                    erros.add("A senha não pode ser vazia.");
                }
                return;
            }
            case "fk_setor" -> {
                 funcionario.setFkSetor(ParametrosHttp.lerInt(novoValor, "Setor", erros));
            }
            case "email" -> {
                funcionario.setEmail(novoValor);
            }
            case "cpf" -> {
                funcionario.setCpf(novoValor);
            }
            case "dt_admissao" -> {
                funcionario.setDtAdmissao(ParametrosHttp.lerData(novoValor, "admissão", erros));
            }
            case "status" -> {
                funcionario.setStatus(novoValor);
            }
            case "is_admin" -> {
                funcionario.setAdmin(ParametrosHttp.lerBooleano(novoValor, "Administrador", erros));
            }
            default -> {
                erros.add("A coluna informada não pode ser alterada.");
                return;
            }
        }

        if (erros.isEmpty()) {
            erros.addAll(FuncionarioValidador.validar(funcionario));
        }
    }
}
