<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erro 404 - Página não encontrada</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/utils/css/style_erro.css">
    <link rel="icon" href="<%= request.getContextPath() %>/utils/imagens/imagotipo.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=K2D:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800&display=swap" rel="stylesheet">
</head>
<body>
<div id="corpo">

    <div id="banner_principal">
        <!-- Link dinâmico com request.getContextPath() apontando para a rota inicial/fornecedor -->
        <a href="<%= request.getContextPath() %>/fornecedor" id="voltar">
            <img src="<%= request.getContextPath() %>/utils/imagens/voltar-log-icon-v2.jpg" alt="Voltar">
        </a>
        <h1 id="frase">ERRO 404!</h1>
        <div id="texto">
            <p>Página não encontrada. O endereço que você tentou acessar não existe ou foi removido.</p>
        </div>
    </div>

    <div id="faixa_roxa"></div>
    <div id="meio_circulo"></div>
    <img id="personagem" src="<%= request.getContextPath() %>/utils/imagens/caixito-tela-erro-icon.png" alt="Caixito confuso">

</div>
</body>
</html>