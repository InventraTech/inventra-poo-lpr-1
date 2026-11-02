package org.inventra.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.inventra.model.FuncionarioMolde;

import java.io.IOException;
import java.util.Set;

/**
 * Barreira de autenticação e autorização de todo o sistema. Libera só as rotas públicas (login, página de erro 403
 * e arquivos estáticos); as demais exigem um funcionário logado na sessão e com perfil de administrador.
 */
//O padrão "/*" significa que ele vai interceptar todas as requisições
@WebFilter("/*")
//Classe para autenticar o funcionário logado para garantir que seja ADMIN
public class FiltroAutenticacao implements Filter {

    // Rotas liberadas sem login: a raiz, a tela de login, o servlet que a processa e a página de erro 403.
    private static final Set<String> PUBLICAS =
            Set.of("", "/", "/index.html", "/login", "/login.jsp", "/erro-403.jsp");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Caminho sem o contexto e sem parâmetros tipo ";login", o que impede o bypass pela URL
        String caminho = httpRequest.getServletPath();

        // Recursos estáticos e páginas públicas também passam sem login; senão a própria tela de login ficaria sem estilo.
        boolean paginasPublicas = PUBLICAS.contains(caminho) ||
                caminho.startsWith("/css/") ||
                caminho.startsWith("/js/") ||
                caminho.startsWith("/paginas/") ||
                caminho.toLowerCase().startsWith("/utils/");


        if (paginasPublicas) {
            chain.doFilter(request, response);
            return;
        }

        // Recupera a sessão atual do usuário (sem criar uma nova se não existir)
        HttpSession session = httpRequest.getSession(false);
        Object atributo = (session == null) ? null : session.getAttribute("funcionarioLogado");

        if (!(atributo instanceof FuncionarioMolde funcionario)) {
            //Redireciona para o Login pois o usuário tentou escrever na url
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        // Segunda trava: mesmo logado, só ADMIN passa
        if (!funcionario.isAdmin()) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN); // web.xml mostra /WEB-INF/views/erros/erro-403.jsp
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
