<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Inventra - Lista de Funcionários</title>
    <!-- Bootstrap CSS para estilização rápida -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Gerenciamento de Funcionários</h2>
        <!-- Botão de Exportar para CSV -->
        <a href="${pageContext.request.contextPath}/funcionario?acao=exportar&filtro=${not empty filtroSelecionado ? filtroSelecionado : ''}" class="btn btn-success">
            <i class="fas fa-file-excel"></i> Exportar CSV
        </a>
    </div>

    <!-- Mensagem de Erro (caso venha da sessão) -->
    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-danger" role="alert">
            ${mensagemErro}
        </div>
        <c:remove var="mensagemErro" scope="session"/>
    </c:if>

    <!-- Formulário de Filtros -->
    <form action="${pageContext.request.contextPath}/funcionario" method="get" class="row g-3 mb-4">
        <div class="col-auto">
            <select name="filtro" class="form-select">
                <option value="">Selecione a Ordenação...</option>
                <option value="az" ${filtroSelecionado == 'az' ? 'selected' : ''}>Alfabética (A-Z)</option>
                <option value="za" ${filtroSelecionado == 'za' ? 'selected' : ''}>Alfabética (Z-A)</option>
                <option value="setor" ${filtroSelecionado == 'setor' ? 'selected' : ''}>Por Setor</option>
                <option value="data_crescente" ${filtroSelecionado == 'data_crescente' ? 'selected' : ''}>Admissão (Mais recente)</option>
                <option value="data_decrescente" ${filtroSelecionado == 'data_decrescente' ? 'selected' : ''}>Admissão (Mais antiga)</option>
            </select>
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-primary">Filtrar</button>
        </div>
        <c:if test="${not empty filtroSelecionado}">
            <div class="col-auto">
                <a href="${pageContext.request.contextPath}/funcionario" class="btn btn-secondary">Limpar Filtro</a>
            </div>
        </c:if>
    </form>

    <!-- Tabela de Exibição -->
    <div class="table-responsive">
        <table class="table table-striped table-bordered align-middle">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>CPF</th>
                    <th>Admissão</th>
                    <th>Status</th>
                    <th>Setor</th>
                    <th>Admin</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty funcionarios}">
                        <c:forEach var="f" items="${funcionarios}">
                            <tr>
                                <td>${f.id_funcionario}</td>
                                <td>${f.nome}</td>
                                <td>${f.email}</td>
                                <td>${f.telefone}</td>
                                <td>${f.cpf}</td>
                                <td>${f.dt_admissao}</td>
                                <td>
                                    <span class="badge ${f.status == 'Ativo' ? 'bg-success' : 'bg-danger'}">
                                        ${f.status}
                                    </span>
                                </td>
                                <td>${f.setor}</td>
                                <td>${f.admin ? 'Sim' : 'Não'}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="9" class="text-center">Nenhum funcionário encontrado.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- Paginação -->
    <nav aria-label="Navegação de páginas" class="mt-4">
        <ul class="pagination justify-content-center">

            <!-- Botão Página Anterior -->
            <li class="page-item ${paginaAtual == 1 ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/funcionario?pagina=${paginaAtual - 1}&filtro=${filtroSelecionado}">Anterior</a>
            </li>

            <!-- Números das Páginas -->
            <c:forEach begin="1" end="${totalPaginas}" var="i">
                <li class="page-item ${paginaAtual == i ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/funcionario?pagina=${i}&filtro=${filtroSelecionado}">${i}</a>
                </li>
            </c:forEach>

            <!-- Botão Próxima Página -->
            <li class="page-item ${paginaAtual == totalPaginas ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/funcionario?pagina=${paginaAtual + 1}&filtro=${filtroSelecionado}">Próximo</a>
            </li>

        </ul>
    </nav>

</body>
</html>