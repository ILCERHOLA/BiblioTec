# BiblioTec

Aplicación web para la gestión de una biblioteca institucional: catálogo, préstamos, devoluciones, renovaciones, reservas y multas.

Proyecto del **Grupo 2 · CodeFusion** — UD Implementación de Soluciones para Plataformas Web.

## Tecnologías

| Componente | Versión |
|---|---|
| Java (JDK) | 25 |
| Jakarta Servlet / JSP / JSTL | 6.0 / 3.1 / 3.0 |
| Apache Tomcat | 10.1 |
| Hibernate ORM (JPA) + HikariCP | 7.4 |
| MySQL | 8.0 |
| Bootstrap | 5.3 |
| Maven | 3.9 |

## Arquitectura

Capas: `controller` (servlets) → `service` (reglas de negocio) → `dao` (acceso a datos) → MySQL. El filtro `AuthFilter` protege todas las páginas, y las vistas JSP están en `WEB-INF/vistas`.

## Cómo ejecutarlo

1. **Base de datos:** en MySQL Workbench ejecutar `db/script_bd_bibliotec.sql` y luego `db/datos_prueba.sql`.
2. **Configuración:** copiar `src/main/resources/config.properties.example` como `config.properties` y poner la contraseña propia de MySQL. Este archivo **no se sube** al repositorio.
3. **Compilar:** `mvn clean package` (genera `target/bibliotec.war`).
4. **Desplegar:** copiar el WAR a la carpeta `webapps` de Tomcat 10.1 e iniciar Tomcat.
5. **Abrir:** `http://localhost:8080/bibliotec/`

Usuarios de prueba (ficticios), todos con la clave `Clave2026`:

| Código | Rol |
|---|---|
| 2024001234 | Estudiante |
| D-0001 | Docente (suspendido) |
| B-0001 | Bibliotecario |

## Trabajo en equipo

Ver [CONTRIBUTING.md](CONTRIBUTING.md): ramas, commits, estándares de código y revisión de pull requests.

## Integración continua

Cada `push` y cada pull request ejecuta `mvn clean verify` en GitHub Actions y guarda el WAR generado como artefacto.
