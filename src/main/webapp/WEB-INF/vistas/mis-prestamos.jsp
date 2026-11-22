<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Mis préstamos</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
  <jsp:include page="/WEB-INF/vistas/fragmentos/navbar.jsp"/>

  <main class="container py-4">
    <c:if test="${not empty mensaje}">
      <div class="alert alert-success"><c:out value="${mensaje}"/></div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not usuario.activo}">
      <div class="alert alert-warning">
        Tu cuenta está suspendida por una multa pendiente. Acércate a la biblioteca para pagarla.
      </div>
    </c:if>

    <c:if test="${not empty multas}">
      <h2 class="h5 mb-3">Multas pendientes</h2>
      <ul class="list-group mb-4">
        <c:forEach var="m" items="${multas}">
          <li class="list-group-item d-flex justify-content-between">
            <span><c:out value="${m.prestamo.ejemplar.libro.titulo}"/> · ${m.diasRetraso} día(s) de retraso</span>
            <strong>S/ ${m.monto}</strong>
          </li>
        </c:forEach>
      </ul>
    </c:if>

    <h1 class="h4 mb-3">Mis préstamos</h1>
    <div class="table-responsive bg-white shadow-sm rounded">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>Libro</th>
            <th>Prestado</th>
            <th>Vence</th>
            <th>Devuelto</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${prestamos}">
            <tr>
              <td><c:out value="${p.ejemplar.libro.titulo}"/></td>
              <td>${p.fechaPrestamo}</td>
              <td>${p.fechaVencimiento}</td>
              <td>${p.fechaDevolucion}</td>
              <td>
                <c:choose>
                  <c:when test="${p.fechaDevolucion != null}">
                    <span class="badge text-bg-secondary">Devuelto</span>
                  </c:when>
                  <c:when test="${p.vencido}">
                    <span class="badge text-bg-danger">Vencido · ${p.diasRetraso} día(s)</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge text-bg-success">Vigente</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="text-end">
                <c:if test="${p.fechaDevolucion == null and not p.vencido and p.renovaciones == 0}">
                  <form method="post" action="${pageContext.request.contextPath}/mis-prestamos" class="m-0">
                    <input type="hidden" name="idPrestamo" value="${p.id}">
                    <button type="submit" class="btn btn-outline-primary btn-sm">Renovar</button>
                  </form>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty prestamos}">
            <tr><td colspan="6" class="text-center text-muted py-4">Aún no tienes préstamos.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </main>
</body>
</html>
