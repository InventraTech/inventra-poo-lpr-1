<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Erro 500 - Erro Interno</title></head>
<body style="font-family: Arial, sans-serif; text-align: center; padding-top: 50px;">
    <h1>Erro Interno no Servidor (500)</h1>
    <p>Ocorreu uma falha inesperada ou o banco de dados está temporariamente indisponível.</p>
    <a href="<%= request.getContextPath() %>/funcionario" style="padding: 10px 20px; background: #dc3545; color: white; text-decoration: none; border-radius: 5px;">Tentar Novamente</a>
</body>
</html>