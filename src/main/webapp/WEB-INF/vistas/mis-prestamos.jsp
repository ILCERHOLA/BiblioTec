<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Mis préstamos</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/bibliotec.css" rel="stylesheet">
</head>
<body>
  <jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"><jsp:param name="activo" value="prestamos"/></jsp:include>
  <main class="contenedor">
    <c:if test="${not empty mensaje}"><div class="aviso aviso-ok"><c:out value="${mensaje}"/></div></c:if>
    <c:if test="${not empty error}"><div class="aviso aviso-error"><c:out value="${error}"/></div></c:if>

    <h1 class="titulo">Mis préstamos</h1>
    <p class="subtitulo">
      <c:out value="${usuario.nombreCompleto}"/> · <c:out value="${usuario.rolEtiqueta}"/> ·
      Límite: ${limite} préstamos simultáneos
    </p>

    <table class="tabla">
      <thead>
        <tr><th>Título</th><th>Prestado</th><th>Vence</th><th>Estado</th><th class="der">Acción</th></tr>
      </thead>
      <tbody>
        <c:forEach var="p" items="${prestamos}">
          <tr>
            <td><c:out value="${p.ejemplar.libro.titulo}"/></td>
            <td>${p.fechaPrestamoTexto}</td>
            <td>${p.fechaVencimientoTexto}</td>
            <td>
              <c:choose>
                <c:when test="${p.vencido}"><span class="est-vencido">Vencido</span></c:when>
                <c:otherwise><span class="est-vigente">Vigente</span></c:otherwise>
              </c:choose>
            </td>
            <td class="der">
              <c:choose>
                <c:when test="${p.vencido}">
                  <a class="btn btn-naranja btn-chico" href="#multas">Ver multa</a>
                </c:when>
                <c:when test="${p.renovaciones == 0}">
                  <form method="post" action="${ctx}/mis-prestamos">
                    <input type="hidden" name="id" value="${p.id}">
                    <button type="submit" class="btn btn-chico">Renovar</button>
                  </form>
                </c:when>
                <c:otherwise><span class="subtitulo" style="margin:0">Ya renovado</span></c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty prestamos}">
          <tr><td colspan="5" style="text-align:center;color:#6b7488;padding:24px">No tienes préstamos activos.</td></tr>
        </c:if>
      </tbody>
    </table>

    <c:if test="${not empty reservas}">
      <h2 class="subseccion">Mis reservas</h2>
      <table class="tabla">
        <thead>
          <tr><th>Título</th><th>Ejemplar</th><th>Recoger hasta</th><th class="der">Acción</th></tr>
        </thead>
        <tbody>
          <c:forEach var="r" items="${reservas}">
            <tr>
              <td><c:out value="${r.ejemplar.libro.titulo}"/></td>
              <td><c:out value="${r.ejemplar.codigoBarras}"/></td>
              <td>${r.limiteTexto}</td>
              <td class="der">
                <form method="post" action="${ctx}/mis-prestamos">
                  <input type="hidden" name="accion" value="cancelarReserva">
                  <input type="hidden" name="id" value="${r.id}">
                  <button type="submit" class="btn btn-claro btn-chico">Cancelar</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:if>

    <c:if test="${not empty multas}">
      <section class="multas" id="multas">
        <h2>Multas pendientes</h2>
        <div class="resumen">
          ${multas.size()} ${multas.size() == 1 ? 'multa' : 'multas'} · ${multasDias} días de retraso ·
          <strong>Total: S/ ${multasTotal}</strong>
        </div>
        <div class="detalle-multa">S/ 1.00 por día de retraso. Devuelve el libro y paga la multa en mostrador para reactivar tu cuenta.</div>
      </section>
    </c:if>

    <div class="pie-acciones"><a class="btn" href="${ctx}/catalogo">Ir al catálogo</a></div>
  </main>
</body>
</html>
