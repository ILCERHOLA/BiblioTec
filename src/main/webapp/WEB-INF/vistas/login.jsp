<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BiblioTec · Iniciar sesión</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
  <link href="${ctx}/assets/css/bibliotec.css" rel="stylesheet">
</head>
<body>
  <div class="login">
    <section class="login-izq">
      <div class="logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="#f0a030" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M4 19.5V5a2 2 0 0 1 2-2h13v16H6a2 2 0 0 0-2 2"/><path d="M4 19.5A2.5 2.5 0 0 0 6.5 22H19"/></svg>
        BiblioTec
      </div>
      <p>Sistema de préstamo de libros de la biblioteca institucional.</p>
      <ul>
        <li>Consulta el catálogo en línea</li>
        <li>Revisa y renueva tus préstamos</li>
        <li>Reserva un ejemplar disponible</li>
      </ul>
    </section>
    <section class="login-der">
      <form class="login-tarjeta" method="post" action="${ctx}/login">
        <h1>Iniciar sesión</h1>
        <p class="sub">Ingresa con tu código institucional.</p>

        <c:if test="${not empty error}">
          <div class="aviso aviso-error" role="alert"><c:out value="${error}"/></div>
        </c:if>

        <div class="campo">
          <label for="codigo">Código de usuario</label>
          <input type="text" id="codigo" name="codigo" value="<c:out value='${codigo}'/>"
                 placeholder="2024001234" required autofocus>
        </div>
        <div class="campo">
          <label for="clave">Contraseña</label>
          <input type="password" id="clave" name="clave" placeholder="••••••••••••" required>
        </div>
        <button type="submit" name="perfil" value="estudiante" class="btn btn-ancho">Ingresar como estudiante</button>
        <button type="submit" name="perfil" value="bibliotecario" class="btn btn-claro btn-ancho">Ingresar como bibliotecario</button>
      </form>
    </section>
  </div>
</body>
</html>
