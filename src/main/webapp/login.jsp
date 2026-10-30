<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventra-ADMIN</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/utils/css/style_login.css">
    <link rel="icon" href="${pageContext.request.contextPath}/utils/imagens/imagotipo.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=K2D:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800&display=swap" rel="stylesheet">

    <style>
        .error-msg {
            background-color: #ffebee;
            color: #c62828;
            padding: 10px;
            border-radius: 6px;
            font-size: 13px;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>
<body>

<div id="login">
    <header>
        <a href="landing_1.html" id="img_voltar"></a>
        <img src="${pageContext.request.contextPath}/utils/imagens/logo-inventra-icon.png" width="65" alt="Logo Inventra">
        <h1 id="title">Acesse o <span id="roxo">Inventra</span></h1>
    </header>

    <div id="separador">
        <div class="linha"></div>
    </div>

    <%-- Bloco de lógica do JSP para exibição de erros --%>
    <%
        String erro = (String) request.getAttribute("erro");
        if (erro != null) {
    %>
    <div class="error-msg"><%= erro %></div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="email">E-mail</label><br>
        <input name="email" type="email" placeholder="Digite seu e-mail" required><br>

        <label for="senha">Senha</label><br>
        <input name="senha" type="password" placeholder="Digite sua senha" required>

        <button id="entrar_site" type="submit">
            <p>Entrar</p>
        </button>
    </form>
</div>

</body>
</html>