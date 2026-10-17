<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Erro 404 - Página não encontrada</title></head>
<body style="font-family: Arial, sans-serif; text-align: center; padding-top: 50px;">
    <h1>Página não encontrada (404)</h1>
    <p>O endereço que você tentou acessar não existe ou foi removido.</p>
    <a href="<%= request.getContextPath() %>/funcionario" style="padding: 10px 20px; background: #28a745; color: white; text-decoration: none; border-radius: 5px;">Ir para a Página Inicial</a>
</body>
</html>