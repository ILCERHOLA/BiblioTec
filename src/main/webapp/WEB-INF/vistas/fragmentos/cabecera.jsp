<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Barra superior comun a P2 a P5. Parametro: activo = catalogo | prestamos | panel --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="barra">
  <a class="marca" href="${ctx}/${usuario.rol == 'BIBLIOTECARIO' or usuario.rol == 'ADMIN' ? 'prestamos' : 'catalogo'}">
    <svg viewBox="0 0 24 24" fill="none" stroke="#f0a030" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M4 19.5V5a2 2 0 0 1 2-2h13v16H6a2 2 0 0 0-2 2"/><path d="M4 19.5A2.5 2.5 0 0 0 6.5 22H19"/></svg>
    BiblioTec
    <c:if test="${usuario.rol == 'BIBLIOTECARIO' or usuario.rol == 'ADMIN'}">
      <span class="insignia">${usuario.rol == 'ADMIN' ? 'Administrador' : 'Bibliotecario'}</span>
    </c:if>
  </a>
  <nav class="barra-links">
    <c:choose>
      <c:when test="${usuario.rol == 'BIBLIOTECARIO' or usuario.rol == 'ADMIN'}">
        <a href="${ctx}/prestamos" class="${param.activo == 'panel' ? 'activo' : ''}">Panel</a>
        <a href="${ctx}/catalogo" class="${param.activo == 'catalogo' ? 'activo' : ''}">Ver catálogo</a>
      </c:when>
      <c:otherwise>
        <a href="${ctx}/catalogo" class="${param.activo == 'catalogo' ? 'activo' : ''}">Catálogo</a>
        <a href="${ctx}/mis-prestamos" class="${param.activo == 'prestamos' ? 'activo' : ''}">Mis préstamos</a>
      </c:otherwise>
    </c:choose>
    <form method="post" action="${ctx}/logout"><button type="submit">Salir</button></form>
  </nav>
</header>
