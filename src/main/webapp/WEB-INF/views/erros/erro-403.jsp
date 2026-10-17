<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Erro 403 - Acesso Negado</title></head>
<body style="font-family: Arial, sans-serif; text-align: center; padding-top: 50px;">
    <h1>Acesso Negado (403)</h1>
    <p>Você não tem permissão de Administrador para acessar esta página.</p>
    <a href="<%= request.getContextPath() %>/funcionario" style="padding: 10px 20px; background: #ffc107; color: black; text-decoration: none; border-radius: 5px;">Voltar ao Início</a>
</body>
</html>