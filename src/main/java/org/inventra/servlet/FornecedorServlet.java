package org.inventra.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.inventra.dao.FornecedorDAO;
import org.inventra.model.FornecedorMolde;
import org.inventra.utils.NotificacaoLotes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.inventra.utils.ParametrosHttp;
import org.inventra.validation.FornecedorValidador;
import java.util.ArrayList;
import java.util.Locale;
/**
 * Controla a tela de fornecedores: listagem com paginação, filtros de ordenação, exportação em CSV e as ações
 * de cadastrar, editar (completo ou por coluna) e excluir. Os dados do formulário passam pelo FornecedorValidador antes de chegar ao DAO.
 */
@WebServlet(name = "FornecedorServlet", value = "/fornecedor")
public class FornecedorServlet extends HttpServlet{

    private FornecedorDAO fornecedorDAO;

    @Override
    public void init() {
        fornecedorDAO = new FornecedorDAO();
    }

    /**
     * Exibe a lista de fornecedores. Parâmetros: {@code filtro} (ordenação), {@code pagina} e {@code acao=exportar}.
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
            if ("exportar".equals(acao)){
                //Diz para o navegador do usuario que a resposta é em CSV e obriga ele a baixar como anexo
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"fornecedores.csv\"");

                List<FornecedorMolde> fornecedores;
                fornecedores = fornecedorDAO.listarFornecedorParaExportar(filtroSelecionado);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write('\ufeff'); //Para o excel reconhecer os acentos corretamente
                    writer.println("ID;Nome;Endereço;CNPJ;email;nota_vpq");

                    for (FornecedorMolde f : fornecedores) {
                        writer.print(f.getIdFornecedor() + ";");
                        writer.print(f.getNomeJuridico() + ";");
                        writer.print(f.getEndereco() + ";");
                        writer.print(f.getCnpj() + ";");
                        writer.print(f.getEmail() + ";");
                        writer.println(f.getNotaVpq());
                    }
                    writer.flush();
                }
                return;
            }

            List<FornecedorMolde> fornecedores = List.of();

            if (filtroSelecionado == null) {
                filtroSelecionado = "";
            }

            switch (filtroSelecionado) {
                case "notaVPQ_crescente" -> {
                    fornecedores = fornecedorDAO.listarFornecedorNotaVPQ(true, limite, offSet);
                }
                case "notaVPQ_decrescente" -> {
                    fornecedores = fornecedorDAO.listarFornecedorNotaVPQ(false, limite, offSet);
                }
                default -> {
                    fornecedores = fornecedorDAO.listarFornecedor(limite, offSet);
                }
            }

            int totalRegistros = fornecedorDAO.contarFornecedores();
            double mediaNotaVPQ = fornecedorDAO.mediaNotaVPQFornecedores();
            int totalFornecedoresComProduto = fornecedorDAO.contarProdutosVinculados();

            //Usamos o math ceil para ele arredondar o total de registros (double) para cima, tipo se tiver 21 registros são 3 páginas
            int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
            if (totalPaginas == 0){
                totalPaginas = 1;
            }

            request.setAttribute("fornecedores", fornecedores);
            request.setAttribute("paginaAtual", paginaAtual);
            request.setAttribute("totalPaginas", totalPaginas);
            request.setAttribute("totalRegistros", totalRegistros);
            request.setAttribute("totalFornecedoresComProduto", totalFornecedoresComProduto);
            request.setAttribute("mediaNotaVPQ", mediaNotaVPQ);
            request.setAttribute("filtroSelecionado", filtroSelecionado);

            //Notificações do sino do header (lotes mais próximos da validade)
            NotificacaoLotes.carregar(request);

            request.getRequestDispatcher(
                    "/WEB-INF/fornecedor/lista-fornecedores.jsp"
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

    /**
     * Trata o formulário de fornecedores. A ação vem em {@code acao}: {@code excluir}, {@code atualizar_coluna} ou
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

                fornecedorDAO.deletarFornecedor(id);

                response.sendRedirect(
                        request.getContextPath() + "/fornecedor"
                );
                return;
            }

            //Atualiza apenas UMA coluna
            else if (acao.equals("atualizar_coluna")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id"), "ID do fornecedor", erros);
                String coluna = request.getParameter("coluna");
                String novoValor = request.getParameter("novoValor");

                if (coluna == null || coluna.isBlank() || novoValor == null) {
                    erros.add("Coluna ou valor não informado.");
                }

                //Valida o novo valor (inclui o formato de CNPJ e e-mail) usando o FornecedorValidador
                if (erros.isEmpty()) {
                    validarColuna(coluna, novoValor, erros);
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/fornecedor", erros);
                    return;
                }

                fornecedorDAO.atualizarColunaFornecedor(coluna, novoValor, id);

                response.sendRedirect(request.getContextPath() + "/fornecedor");

                //Return faz com que pare aqui caso seja escolhido
                return;

            } else if (acao.equals("atualizar_completo")) {
                int id = ParametrosHttp.lerInt(request.getParameter("id_fornecedor"), "ID do fornecedor", erros);

                FornecedorMolde atualizarFornecedor = lerFornecedor(request, erros);

                if (erros.isEmpty()) {
                    erros.addAll(FornecedorValidador.validar(atualizarFornecedor));
                }

                if (!erros.isEmpty()) {
                    ParametrosHttp.redirecionarComErros(request, response, "/fornecedor", erros);
                    return;
                }

                fornecedorDAO.atualizarFornecedorCompleto(atualizarFornecedor, id);

                response.sendRedirect(request.getContextPath() + "/fornecedor");

                //Return faz com que pare aqui caso seja escolhido
                return;
            }

            FornecedorMolde novoFornecedor = lerFornecedor(request, erros);

            if (erros.isEmpty()) {
                erros.addAll(FornecedorValidador.validar(novoFornecedor));
            }

            if (!erros.isEmpty()) {
                ParametrosHttp.redirecionarComErros(request, response, "/fornecedor", erros);
                return;
            }

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

    //Lê os campos do formulário (cadastro e edição completa). Parâmetros ausentes ou malformados viram mensagens em "erros".
    private FornecedorMolde lerFornecedor(HttpServletRequest request, List<String> erros) {
        String nome = request.getParameter("nome");
        String cnpj = request.getParameter("cnpj");
        String email = request.getParameter("email");
        int notaVpq = ParametrosHttp.lerInt(request.getParameter("nota_vpq"), "Nota VPQ", erros);
        int fkEndereco = ParametrosHttp.lerInt(request.getParameter("fk_endereco"), "Endereço", erros);

        return new FornecedorMolde(fkEndereco, nome, cnpj, email, notaVpq);
    }

    //Edição por coluna: parte de um fornecedor "base" válido e troca só o campo editado, para o FornecedorValidador
    //avaliar apenas o novo valor. Só aceita as colunas que o DAO sabe atualizar.
    private void validarColuna(String coluna, String novoValor, List<String> erros) {
        FornecedorMolde candidato = new FornecedorMolde(1, "Fornecedor", "11222333000181", "valido@inventra.com", 0);

        // Lista fechada de colunas editáveis: o DAO concatena o nome da coluna no UPDATE (o "?" não vale para nome de coluna),
        // então qualquer nome fora daqui é recusado antes de chegar ao banco.
        switch (coluna.toLowerCase(Locale.ROOT)) {
            case "nome_juridico" -> {
                candidato.setNomeJuridico(novoValor);
            }
            case "fk_endereco" -> {
                candidato.setFkEndereco(ParametrosHttp.lerInt(novoValor, "Endereço", erros));
            }
            case "cnpj" -> {
                candidato.setCnpj(novoValor);
            }
            case "email" -> {
                candidato.setEmail(novoValor);
            }
            case "nota_vpq" -> {
                candidato.setNotaVpq(ParametrosHttp.lerInt(novoValor, "Nota VPQ", erros));
            }
            default -> {
                erros.add("A coluna informada não pode ser alterada.");
            }
        }

        if (erros.isEmpty()) {
            erros.addAll(FornecedorValidador.validar(candidato));
        }
    }
}
