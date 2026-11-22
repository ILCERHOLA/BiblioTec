<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Catálogo</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
  <jsp:include page="/WEB-INF/vistas/fragmentos/navbar.jsp"/>

  <main class="container py-4">
    <c:if test="${not usuario.activo}">
      <div class="alert alert-warning">
        Tu cuenta está suspendida por una multa pendiente. Puedes consultar el catálogo,
        pero no solicitar préstamos hasta regularizarla.
      </div>
    </c:if>

    <h1 class="h4 mb-3">Catálogo de libros</h1>

    <form method="get" action="${pageContext.request.contextPath}/catalogo" class="row g-2 mb-3">
      <div class="col-sm-8 col-md-6">
        <input type="search" class="form-control" name="q" placeholder="Buscar por título"
               value="<c:out value='${q}'/>">
      </div>
      <div class="col-auto">
        <button type="submit" class="btn btn-primary">Buscar</button>
      </div>
    </form>

    <div class="table-responsive bg-white shadow-sm rounded">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>Título</th>
            <th>Autores</th>
            <th>Categoría</th>
            <th class="text-center">Ejemplares</th>
            <th class="text-center">Disponibilidad</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="l" items="${libros}">
            <tr>
              <td><c:out value="${l.titulo}"/></td>
              <td><c:out value="${l.autores}"/></td>
              <td><c:out value="${l.categoria}"/></td>
              <td class="text-center">${l.ejemplares}</td>
              <td class="text-center">
                <c:choose>
                  <c:when test="${l.disponibles > 0}">
                    <span class="badge text-bg-success">${l.disponibles} disponible(s)</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge text-bg-secondary">No disponible</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty libros}">
            <tr><td colspan="5" class="text-center text-muted py-4">No se encontraron libros.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </main>
</body>
</html>