<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Catálogo</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/bibliotec.css" rel="stylesheet">
</head>
<body>
  <jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"><jsp:param name="activo" value="catalogo"/></jsp:include>
  <main class="contenedor">
    <c:if test="${not empty mensaje}"><div class="aviso aviso-ok"><c:out value="${mensaje}"/></div></c:if>
    <c:if test="${not empty error}"><div class="aviso aviso-error"><c:out value="${error}"/></div></c:if>
    <c:if test="${not usuario.activo}">
      <div class="aviso aviso-info">
        Tu cuenta está suspendida por una multa pendiente. Puedes consultar el catálogo,
        pero no solicitar préstamos hasta regularizarla.
      </div>
    </c:if>

    <h1 class="titulo">Catálogo de libros</h1>
    <p class="subtitulo">Busca por título, autor o ISBN y elige un libro para ver su disponibilidad.</p>

    <div class="filtros">
      <form method="get" action="${ctx}/catalogo">
        <div>
          <label for="q">Buscar</label>
          <input type="search" id="q" name="q" placeholder="Título, autor o ISBN" value="<c:out value='${q}'/>">
        </div>
        <div>
          <label for="categoria">Categoría</label>
          <select id="categoria" name="categoria" onchange="this.form.submit()">
            <option value="">Todas</option>
            <c:forEach var="cat" items="${categorias}">
              <option value="${cat.id}" ${cat.id == categoriaSel ? 'selected' : ''}><c:out value="${cat.nombre}"/></option>
            </c:forEach>
          </select>
        </div>
      </form>
    </div>

    <div class="rejilla">
      <c:forEach var="l" items="${libros}">
        <a class="tarjeta-libro" href="${ctx}/libro?id=${l.id}">
          <div class="portada ${l.claseCubierta}"><c:out value="${l.titulo}"/></div>
          <div class="cuerpo">
            <div class="nombre"><c:out value="${l.titulo}"/></div>
            <div class="autor"><c:out value="${l.autores}"/></div>
            <c:choose>
              <c:when test="${l.disponibles > 0}">
                <div class="disp disp-si">Disponible: ${l.disponibles} ${l.disponibles == 1 ? 'ejemplar' : 'ejemplares'}</div>
              </c:when>
              <c:otherwise>
                <div class="disp disp-no">Sin ejemplares disponibles</div>
              </c:otherwise>
            </c:choose>
          </div>
        </a>
      </c:forEach>
      <c:if test="${empty libros}">
        <p class="vacio">No se encontraron libros con ese criterio.</p>
      </c:if>
    </div>
  </main>
</body>
</html>
