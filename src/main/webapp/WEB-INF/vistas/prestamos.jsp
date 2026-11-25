<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Panel del bibliotecario</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/bibliotec.css" rel="stylesheet">
</head>
<body>
  <jsp:include page="/WEB-INF/vistas/fragmentos/cabecera.jsp"><jsp:param name="activo" value="panel"/></jsp:include>
  <div class="panel">
    <aside class="lateral">
      <a href="${ctx}/prestamos?vista=prestamo" class="${vista == 'prestamo' ? 'activo' : ''}">Registrar préstamo</a>
      <a href="${ctx}/prestamos?vista=devolucion" class="${vista == 'devolucion' ? 'activo' : ''}">Registrar devolución</a>
      <span title="Disponible en una próxima versión">Gestionar libros <small>Pronto</small></span>
      <span title="Disponible en una próxima versión">Gestionar usuarios <small>Pronto</small></span>
      <a href="${ctx}/prestamos?vista=multas" class="${vista == 'multas' ? 'activo' : ''}">Multas</a>
      <span title="Disponible en una próxima versión">Reportes <small>Pronto</small></span>
    </aside>

    <main class="panel-contenido">
      <c:if test="${not empty mensaje}"><div class="aviso aviso-ok"><c:out value="${mensaje}"/></div></c:if>
      <c:if test="${not empty error}"><div class="aviso aviso-error"><c:out value="${error}"/></div></c:if>

      <c:choose>
        <%-- ===== Registrar préstamo (con verificación previa) ===== --%>
        <c:when test="${vista == 'prestamo'}">
          <h1 class="titulo">Registrar préstamo</h1>
          <p class="subtitulo">Ingrese el código del usuario y el código de barras del ejemplar.</p>

          <form method="post" action="${ctx}/prestamos" id="formVerificar">
            <input type="hidden" name="accion" value="verificar">
            <div class="fila-campos">
              <div>
                <label for="codigoUsuario">Código del usuario</label>
                <input type="text" id="codigoUsuario" name="codigoUsuario"
                       value="<c:out value='${codigoUsuario}'/>" placeholder="2024001234" required>
              </div>
              <div>
                <label for="codigoBarras">Código de barras del ejemplar</label>
                <input type="text" id="codigoBarras" name="codigoBarras"
                       value="<c:out value='${codigoBarras}'/>" placeholder="LIB-000145" required>
              </div>
            </div>
            <c:if test="${empty verificacion}">
              <button type="submit" class="btn btn-verde">Verificar</button>
            </c:if>
          </form>

          <c:if test="${not empty verificacion}">
            <div class="verificacion" style="margin-top:14px">
              <div>
                <strong>Usuario:</strong> <c:out value="${verificacion.nombreUsuario}"/> · <c:out value="${verificacion.rol}"/> ·
                Préstamos vigentes: ${verificacion.prestamosVigentes} de ${verificacion.limite} ·
                ${verificacion.sinMultas ? 'Sin multas' : 'Con multa pendiente'}
              </div>
              <div>
                <strong>Libro:</strong> <c:out value="${verificacion.tituloLibro}"/> · Plazo: ${verificacion.plazoDias} días
              </div>
              <c:choose>
                <c:when test="${verificacion.cumple}">
                  <div class="ok">&#10003; Cumple las reglas de negocio RN-01, RN-02, RN-03 y RN-07</div>
                </c:when>
                <c:otherwise>
                  <div class="mal">&#10007; No cumple: <c:out value="${verificacion.motivo}"/></div>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="botones">
              <form method="post" action="${ctx}/prestamos">
                <input type="hidden" name="accion" value="registrar">
                <input type="hidden" name="codigoUsuario" value="<c:out value='${verificacion.codigoUsuario}'/>">
                <input type="hidden" name="codigoBarras" value="<c:out value='${verificacion.codigoBarras}'/>">
                <button type="submit" class="btn btn-verde" ${verificacion.cumple ? '' : 'disabled'}>Confirmar préstamo</button>
              </form>
              <a class="btn btn-claro" href="${ctx}/prestamos?vista=prestamo">Cancelar</a>
            </div>
          </c:if>
        </c:when>

        <%-- ===== Registrar devolución ===== --%>
        <c:when test="${vista == 'devolucion'}">
          <h1 class="titulo">Registrar devolución</h1>
          <p class="subtitulo">Préstamos sin devolver. Si hay retraso se genera la multa (S/ 1.00 por día) y se suspende al usuario.</p>
          <table class="tabla">
            <thead>
              <tr><th>Usuario</th><th>Libro</th><th>Ejemplar</th><th>Vence</th><th>Estado</th><th class="der">Acción</th></tr>
            </thead>
            <tbody>
              <c:forEach var="p" items="${prestamos}">
                <tr>
                  <td><c:out value="${p.usuario.nombreCompleto}"/> (<c:out value="${p.usuario.codigo}"/>)</td>
                  <td><c:out value="${p.ejemplar.libro.titulo}"/></td>
                  <td><c:out value="${p.ejemplar.codigoBarras}"/></td>
                  <td>${p.fechaVencimientoTexto}</td>
                  <td>
                    <c:choose>
                      <c:when test="${p.vencido}"><span class="est-vencido">Vencido · ${p.diasRetraso} día(s)</span></c:when>
                      <c:otherwise><span class="est-vigente">Vigente</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td class="der">
                    <form method="post" action="${ctx}/prestamos">
                      <input type="hidden" name="accion" value="devolver">
                      <input type="hidden" name="idPrestamo" value="${p.id}">
                      <button type="submit" class="btn btn-chico">Registrar devolución</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty prestamos}">
                <tr><td colspan="6" style="text-align:center;color:#6b7488;padding:24px">No hay préstamos sin devolver.</td></tr>
              </c:if>
            </tbody>
          </table>
        </c:when>

        <%-- ===== Multas ===== --%>
        <c:otherwise>
          <h1 class="titulo">Multas pendientes</h1>
          <p class="subtitulo">Al marcar una multa como pagada se reactiva la cuenta del usuario si no tiene otras.</p>
          <table class="tabla">
            <thead>
              <tr><th>Usuario</th><th>Libro</th><th>Días de retraso</th><th>Monto</th><th class="der">Acción</th></tr>
            </thead>
            <tbody>
              <c:forEach var="m" items="${multas}">
                <tr>
                  <td><c:out value="${m.prestamo.usuario.nombreCompleto}"/></td>
                  <td><c:out value="${m.prestamo.ejemplar.libro.titulo}"/></td>
                  <td>${m.diasRetraso}</td>
                  <td>S/ ${m.monto}</td>
                  <td class="der">
                    <form method="post" action="${ctx}/prestamos">
                      <input type="hidden" name="accion" value="pagarMulta">
                      <input type="hidden" name="idMulta" value="${m.id}">
                      <button type="submit" class="btn btn-verde btn-chico">Marcar como pagada</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty multas}">
                <tr><td colspan="5" style="text-align:center;color:#6b7488;padding:24px">No hay multas pendientes.</td></tr>
              </c:if>
            </tbody>
          </table>
        </c:otherwise>
      </c:choose>
    </main>
  </div>
</body>
</html>
