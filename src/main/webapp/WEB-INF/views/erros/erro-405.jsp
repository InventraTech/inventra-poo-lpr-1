<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Erro 405 - Método não permitido</title></head>
<body style="font-family: Arial, sans-serif; text-align: center; padding-top: 50px;">
    <h1>Método Não Permitido (405)</h1>
    <p>A forma de envio (GET/POST) utilizada para acessar esta rota não é suportada.</p>
    <a href="<%= request.getContextPath() %>/funcionario" style="padding: 10px 20px; background: #17a2b8; color: white; text-decoration: none; border-radius: 5px;">Voltar ao Início</a>
</body>
</html>