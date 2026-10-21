package org.inventra.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.inventra.dao.FuncionarioDAO;
import org.inventra.model.FuncionarioMolde;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    private FuncionarioDAO funcionarioDAO;

    @Override
    public void init(){
        funcionarioDAO = new FuncionarioDAO();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String senha = request.getParameter("senha");
        String email = request.getParameter("email");

        //Campos vazios ou ausentes
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            request.setAttribute("erro", "Preencha e-mail e senha.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        FuncionarioMolde funcionario = funcionarioDAO.validarLogin(email, senha);

        if (funcionario != null){
            HttpSession sessao = request.getSession();
            //Troca o ID da sessão ao logar (evita session fixation)
            request.changeSessionId();
            //Sessão expira após 30 minutos parada
            sessao.setMaxInactiveInterval(30 * 60);

            //A senha não fica guardada na sessão
            funcionario.setSenha(null);
            sessao.setAttribute("funcionarioLogado", funcionario);

            response.sendRedirect(
                    request.getContextPath() + "/index.jsp"
            );
        } else {
            request.setAttribute("erro", "E-mail ou senha incorretos!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se alguém tentar abrir pelo GET na URL do servlet, redireciona de volta para a tela de login
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

}
