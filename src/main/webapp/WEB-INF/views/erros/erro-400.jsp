<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>Erro 400 - Requisição Inválida</title></head>
<body style="font-family: Arial, sans-serif; text-align: center; padding-top: 50px;">
    <h1>Ops! Requisição Inválida (400)</h1>
    <p>Os dados enviados para o servidor estão incorretos ou incompletos.</p>
    <a href="<%= request.getContextPath() %>/funcionario" style="padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 5px;">Voltar aos Funcionários</a>
</body>
</html>