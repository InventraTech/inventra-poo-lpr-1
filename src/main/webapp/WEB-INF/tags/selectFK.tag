<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Lista suspensa de uma chave estrangeira: mostra a descrição e envia o ID.
     opcoes: mapa ID -> descrição, carregado pelo servlet.
     coluna: usado só na edição de um campo; o select começa oculto e desabilitado (o JavaScript da tela o ativa). --%>
<%@ attribute name="name" required="true" rtexprvalue="true" %>
<%@ attribute name="id" required="true" rtexprvalue="true" %>
<%@ attribute name="opcoes" required="true" rtexprvalue="true" type="java.util.Map" %>
<%@ attribute name="coluna" required="false" rtexprvalue="true" %>
<select name="${name}" id="${id}" required
  <c:if test="${not empty coluna}">class="novo-valor-fk" data-coluna="${coluna}" hidden disabled</c:if>>
  <option value="" disabled selected>Selecione...</option>
  <c:forEach items="${opcoes}" var="opcao">
    <option value="${opcao.key}">${fn:escapeXml(opcao.value)}</option>
  </c:forEach>
</select>
