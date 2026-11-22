<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand navbar-dark bg-dark">
  <div class="container">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/catalogo">BiblioTec</a>
    <ul class="navbar-nav me-auto">
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/catalogo">Catálogo</a></li>
      <c:if test="${usuario.rol == 'ESTUDIANTE' or usuario.rol == 'DOCENTE'}">
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/mis-prestamos">Mis préstamos</a></li>
      </c:if>
      <c:if test="${usuario.rol == 'BIBLIOTECARIO' or usuario.rol == 'ADMIN'}">
        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/prestamos">Préstamos</a></li>
      </c:if>
    </ul>
    <form method="post" action="${pageContext.request.contextPath}/logout" class="d-flex align-items-center gap-3 m-0">
      <span class="text-white-50 small">
        <c:out value="${usuario.nombreCompleto}"/> · <c:out value="${usuario.rol}"/>
      </span>
      <button type="submit" class="btn btn-outline-light btn-sm">Salir</button>
    </form>
  </div>
</nav>
