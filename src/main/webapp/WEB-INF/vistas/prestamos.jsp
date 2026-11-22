<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Préstamos</title>
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

    <h1 class="h4 mb-3">Registrar préstamo</h1>
    <form method="post" action="${pageContext.request.contextPath}/prestamos" class="row g-2 mb-4">
      <input type="hidden" name="accion" value="registrar">
      <div class="col-sm-4">
        <input type="text" class="form-control" name="codigoUsuario" placeholder="Código del usuario" required>
      </div>
      <div class="col-sm-4">
        <input type="text" class="form-control" name="codigoBarras" placeholder="Código de barras del ejemplar" required>
      </div>
      <div class="col-auto">
        <button type="submit" class="btn btn-primary">Registrar</button>
      </div>
    </form>

    <h2 class="h5 mb-3">Préstamos sin devolver</h2>
    <div class="table-responsive bg-white shadow-sm rounded mb-4">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>Usuario</th>
            <th>Libro</th>
            <th>Ejemplar</th>
            <th>Vence</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${prestamos}">
            <tr>
              <td><c:out value="${p.usuario.nombreCompleto}"/> <span class="text-muted small">(<c:out value="${p.usuario.codigo}"/>)</span></td>
              <td><c:out value="${p.ejemplar.libro.titulo}"/></td>
              <td><c:out value="${p.ejemplar.codigoBarras}"/></td>
              <td>${p.fechaVencimiento}</td>
              <td>
                <c:choose>
                  <c:when test="${p.vencido}">
                    <span class="badge text-bg-danger">Vencido · ${p.diasRetraso} día(s)</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge text-bg-success">Vigente</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="text-end">
                <form method="post" action="${pageContext.request.contextPath}/prestamos" class="m-0">
                  <input type="hidden" name="accion" value="devolver">
                  <input type="hidden" name="idPrestamo" value="${p.id}">
                  <button type="submit" class="btn btn-outline-primary btn-sm">Registrar devolución</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty prestamos}">
            <tr><td colspan="6" class="text-center text-muted py-4">No hay préstamos sin devolver.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <h2 class="h5 mb-3">Multas pendientes</h2>
    <div class="table-responsive bg-white shadow-sm rounded">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>Usuario</th>
            <th>Libro</th>
            <th class="text-center">Días de retraso</th>
            <th class="text-end">Monto</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="m" items="${multas}">
            <tr>
              <td><c:out value="${m.prestamo.usuario.nombreCompleto}"/></td>
              <td><c:out value="${m.prestamo.ejemplar.libro.titulo}"/></td>
              <td class="text-center">${m.diasRetraso}</td>
              <td class="text-end">S/ ${m.monto}</td>
              <td class="text-end">
                <form method="post" action="${pageContext.request.contextPath}/prestamos" class="m-0">
                  <input type="hidden" name="accion" value="pagarMulta">
                  <input type="hidden" name="idMulta" value="${m.id}">
                  <button type="submit" class="btn btn-outline-success btn-sm">Marcar como pagada</button>
                </form>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty multas}">
            <tr><td colspan="5" class="text-center text-muted py-4">No hay multas pendientes.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </main>
</body>
</html>
