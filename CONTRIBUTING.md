# Guía de contribución

## Flujo de ramas

- `main`: versión estable (una etiqueta por sprint).
- `develop`: integración del sprint.
- `feature/<componente>`: una rama por funcionalidad, creada desde `develop`.

Ejemplo: `git checkout develop`, luego `git pull`, luego `git checkout -b feature/prestamos`.

## Pull requests

1. Subir la rama `feature/...` y abrir un pull request hacia `develop`.
2. El CI (**Compilar y verificar**) debe salir en verde.
3. Al menos un compañero revisa y aprueba antes de unir.
4. No se hace `push` directo a `develop` ni a `main`.

## Mensajes de commit

Formato `tipo(módulo): descripción`, en minúsculas y en presente.

| Tipo | Uso |
|---|---|
| `feat` | funcionalidad nueva |
| `fix` | corrección de error |
| `docs` | documentación |
| `test` | pruebas |
| `ci` | integración continua |
| `refactor` | mejora interna sin cambiar el comportamiento |

Ejemplo: `feat(prestamo): valida el límite de préstamos por rol`.

## Estándares de programación

**Organización**

- Paquetes: `model`, `dao`, `service`, `controller`, `filter`, `util`.
- Los servlets no contienen reglas de negocio: solo reciben, llaman al servicio y reenvían a la vista.
- Las reglas de negocio (RN-01 a RN-13) viven en las clases `service`.
- Las páginas JSP están en `WEB-INF/vistas` y no contienen código Java (se usa JSTL).

**Nombres**

- Clases en `UpperCamelCase`; métodos, atributos y variables en `lowerCamelCase`; constantes en `MAYUSCULAS_CON_GUION_BAJO`.
- Nombres descriptivos en español, sin abreviaturas confusas.

**Calidad**

- Cada método tiene una sola responsabilidad.
- Sin código duplicado ni código comentado sin usar.
- Javadoc en las clases y en los métodos públicos de `service` y `dao`.
- El código se entrega **sin advertencias del compilador**.

**Seguridad**

- Contraseñas siempre con BCrypt.
- Consultas con parámetros (nunca concatenar texto del usuario en una consulta).
- Salida en las JSP con `<c:out>` para evitar XSS.
- Nunca subir credenciales: `config.properties` está en `.gitignore`.

**Pruebas**

- Cada regla de negocio tiene al menos una prueba JUnit en `src/test/java`.
