<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Detalle del libro</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/bibliotec.css" rel="stylesheet">
</head>
<body>
  <jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"><jsp:param name="activo" value="catalogo"/></jsp:include>
  <main class="contenedor">
    <a class="volver" href="${ctx}/catalogo">&#8249; Volver al catálogo</a>

    <c:if test="${not empty mensaje}"><div class="aviso aviso-ok"><c:out value="${mensaje}"/></div></c:if>
    <c:if test="${not empty error}"><div class="aviso aviso-error"><c:out value="${error}"/></div></c:if>

    <article class="detalle">
      <div class="portada ${libro.claseCubierta}"><c:out value="${libro.titulo}"/></div>
      <div class="info">
        <div class="categoria"><c:out value="${libro.categoria}"/></div>
        <h1><c:out value="${libro.titulo}"/></h1>
        <div class="autores"><c:out value="${libro.autores}"/></div>

        <dl class="datos">
          <div><dt>Editorial</dt><dd><c:out value="${libro.editorial}"/></dd></div>
          <div><dt>Año de publicación</dt><dd><c:out value="${libro.anio}"/></dd></div>
          <div><dt>ISBN</dt><dd><c:out value="${libro.isbn}"/></dd></div>
          <div><dt>Ubicación</dt><dd><c:out value="${libro.ubicacion}"/></dd></div>
        </dl>

        <div class="estado-libro ${libro.disponibles > 0 ? '' : 'sin'}">
          <c:choose>
            <c:when test="${libro.disponibles > 0}">
              <strong>${libro.disponibles} de ${libro.ejemplares} ejemplares disponibles</strong>
            </c:when>
            <c:otherwise>
              <strong>Sin ejemplares disponibles (${libro.ejemplares} en total)</strong>
            </c:otherwise>
          </c:choose>
          <div>Plazo de préstamo: ${plazoDias} días · Renovaciones permitidas: 1</div>
        </div>

        <c:if test="${usuario.rol == 'ESTUDIANTE' or usuario.rol == 'DOCENTE'}">
          <div class="acciones">
            <form method="post" action="${ctx}/libro">
              <input type="hidden" name="id" value="${libro.id}">
              <button type="submit" name="accion" value="prestamo" class="btn btn-verde"
                      ${libro.disponibles > 0 and usuario.activo ? '' : 'disabled'}>Solicitar préstamo</button>
              <button type="submit" name="accion" value="reserva" class="btn btn-claro"
                      ${libro.disponibles > 0 and usuario.activo ? '' : 'disabled'}>Reservar</button>
            </form>
          </div>
          <p class="nota">Reservar aparta un ejemplar disponible durante 48 horas. La confirmación te lleva a «Mis préstamos».</p>
        </c:if>
      </div>
    </article>
  </main>
</body>
</html>
