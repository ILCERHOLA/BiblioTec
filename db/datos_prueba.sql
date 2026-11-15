-- =====================================================================
-- BiblioTec - Datos de prueba para bd_bibliotec
-- Ejecutar DESPUÉS de script_bd_bibliotec.sql
-- Todos los datos son ficticios (nombres, correos, ISBN y códigos).
-- Contraseña de los 3 usuarios de prueba: Clave2026  (guardada con BCrypt)
-- Las fechas de préstamos y reservas se calculan respecto a hoy.
-- =====================================================================
USE bd_bibliotec;

-- ---------------------------------------------------------------------
-- Categorías (las mismas del filtro del prototipo P2)
-- ---------------------------------------------------------------------
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Informática',    'Fundamentos y gestión de la informática'),
(2, 'Programación',   'Lenguajes, buenas prácticas y desarrollo de software'),
(3, 'Bases de datos', 'Modelado, diseño y administración de datos'),
(4, 'Redes',          'Redes de computadoras y comunicaciones');

-- ---------------------------------------------------------------------
-- Autores
-- ---------------------------------------------------------------------
INSERT INTO autor (id_autor, nombre_completo, nacionalidad) VALUES
(1, 'Ian Sommerville',      'Británica'),
(2, 'Paul Deitel',          'Estadounidense'),
(3, 'Abraham Silberschatz', 'Estadounidense'),
(4, 'Andrew Tanenbaum',     'Estadounidense'),
(5, 'Robert C. Martin',     'Estadounidense'),
(6, 'Erich Gamma',          'Suiza'),
(7, 'Richard Helm',         'Australiana'),
(8, 'Ralph Johnson',        'Estadounidense'),
(9, 'John Vlissides',       'Estadounidense');

-- ---------------------------------------------------------------------
-- Usuarios: 1 estudiante, 1 docente y 1 bibliotecario (clave: Clave2026)
-- Carlos queda suspendido porque tiene una multa pendiente (RN-06).
-- ---------------------------------------------------------------------
INSERT INTO usuario (id_usuario, codigo, nombres, apellidos, correo, clave_hash, rol, estado) VALUES
(1, '2024001234', 'Ana',    'Torres Ruiz',     'ana.torres@bibliotec.test',
    '$2a$10$NqLwxtTlNrSwhhwzhOhGlOzWzisrZRD5XqvJVCpptLLndslKL4ys6', 'estudiante',    'activo'),
(2, 'D-0001',     'Carlos', 'Mendoza Vega',    'carlos.mendoza@bibliotec.test',
    '$2a$10$cUIEEVZmVyd0oD7mmSrrheSIAcVDoMTOr2gEhNBNWU2DHytV.QT6K', 'docente',       'suspendido'),
(3, 'B-0001',     'Lucía',  'Paredes Rojas',   'lucia.paredes@bibliotec.test',
    '$2a$10$dtuw4xGD9JNWrltXP6efR..OxLgNsowZHUkCvRXKPcJ5TGrgc88zS', 'bibliotecario', 'activo');

-- ---------------------------------------------------------------------
-- Libros (6) - ISBN ficticios para pruebas
-- ---------------------------------------------------------------------
INSERT INTO libro (id_libro, isbn, titulo, anio_publicacion, editorial, id_categoria) VALUES
(1, '978-607-442-032-7', 'Ingeniería de Software',         2011, 'Pearson',         1),
(2, '978-607-442-100-1', 'Java: Cómo programar',           2016, 'Pearson',         2),
(3, '978-607-150-200-3', 'Fundamentos de Bases de Datos',  2014, 'McGraw-Hill',     3),
(4, '978-607-442-300-5', 'Redes de Computadoras',          2012, 'Pearson',         4),
(5, '978-013-235-088-4', 'Clean Code',                     2008, 'Prentice Hall',   2),
(6, '978-020-163-361-0', 'Patrones de diseño',             1994, 'Addison-Wesley',  1);

-- Relación libro - autor (Patrones de diseño tiene 4 autores: relación N:M)
INSERT INTO libro_autor (id_libro, id_autor) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (6, 7), (6, 8), (6, 9);

-- ---------------------------------------------------------------------
-- Ejemplares (12 copias físicas)
--  Ingeniería de Software: 3 disponibles + 1 prestado
--  Java: 2 disponibles | Redes: 1 disponible
--  Clean Code: 2 disponibles + 1 reservado
--  Fundamentos de BD y Patrones de diseño: sin ejemplares disponibles
-- ---------------------------------------------------------------------
INSERT INTO ejemplar (id_ejemplar, id_libro, codigo_barras, ubicacion, estado) VALUES
( 1, 1, 'LIB-000145', 'Estante B-12', 'disponible'),
( 2, 1, 'LIB-000146', 'Estante B-12', 'disponible'),
( 3, 1, 'LIB-000147', 'Estante B-12', 'disponible'),
( 4, 1, 'LIB-000148', 'Estante B-12', 'prestado'),
( 5, 2, 'LIB-000201', 'Estante C-03', 'disponible'),
( 6, 2, 'LIB-000202', 'Estante C-03', 'disponible'),
( 7, 3, 'LIB-000301', 'Estante D-07', 'prestado'),
( 8, 4, 'LIB-000401', 'Estante A-05', 'disponible'),
( 9, 5, 'LIB-000501', 'Estante C-04', 'disponible'),
(10, 5, 'LIB-000502', 'Estante C-04', 'disponible'),
(11, 5, 'LIB-000503', 'Estante C-04', 'reservado'),
(12, 6, 'LIB-000601', 'Estante B-02', 'prestado');

-- ---------------------------------------------------------------------
-- Reserva activa de Ana sobre un ejemplar de Clean Code (48 horas)
-- ---------------------------------------------------------------------
INSERT INTO reserva (id_reserva, id_usuario, id_ejemplar, fecha_reserva, fecha_limite_recojo, estado) VALUES
(1, 1, 11, NOW(), DATE_ADD(NOW(), INTERVAL 48 HOUR), 'activa');

-- ---------------------------------------------------------------------
-- Préstamos
--  1) Ana (estudiante, 7 días): Ingeniería de Software, vigente
--  2) Carlos (docente, 15 días): Fundamentos de BD, vencido hace 4 días
--  3) Carlos: Patrones de diseño, vigente
-- ---------------------------------------------------------------------
INSERT INTO prestamo (id_prestamo, id_usuario, id_ejemplar, id_reserva,
                      fecha_prestamo, fecha_vencimiento, fecha_devolucion, renovaciones, estado) VALUES
(1, 1,  4, NULL, DATE_SUB(CURDATE(), INTERVAL 2 DAY),  DATE_ADD(CURDATE(), INTERVAL 5 DAY),  NULL, 0, 'vigente'),
(2, 2,  7, NULL, DATE_SUB(CURDATE(), INTERVAL 19 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY),  NULL, 0, 'vencido'),
(3, 2, 12, NULL, DATE_SUB(CURDATE(), INTERVAL 3 DAY),  DATE_ADD(CURDATE(), INTERVAL 12 DAY), NULL, 0, 'vigente');

-- ---------------------------------------------------------------------
-- Multa de Carlos: 4 días x S/ 1.00 = S/ 4.00 (RN-05), pendiente
-- ---------------------------------------------------------------------
INSERT INTO multa (id_multa, id_prestamo, dias_retraso, monto, fecha_generacion, estado) VALUES
(1, 2, 4, 4.00, CURDATE(), 'pendiente');

-- =====================================================================
-- VERIFICACIÓN
-- =====================================================================
-- Cantidad de filas por tabla (esperado: 4, 9, 3, 6, 9, 12, 1, 3, 1)
SELECT 'categoria' AS tabla, COUNT(*) AS filas FROM categoria
UNION ALL SELECT 'autor',       COUNT(*) FROM autor
UNION ALL SELECT 'usuario',     COUNT(*) FROM usuario
UNION ALL SELECT 'libro',       COUNT(*) FROM libro
UNION ALL SELECT 'libro_autor', COUNT(*) FROM libro_autor
UNION ALL SELECT 'ejemplar',    COUNT(*) FROM ejemplar
UNION ALL SELECT 'reserva',     COUNT(*) FROM reserva
UNION ALL SELECT 'prestamo',    COUNT(*) FROM prestamo
UNION ALL SELECT 'multa',       COUNT(*) FROM multa;

-- Catálogo con disponibilidad (es la consulta que usará la pantalla P2)
SELECT l.titulo,
       GROUP_CONCAT(DISTINCT a.nombre_completo ORDER BY a.nombre_completo SEPARATOR ', ') AS autores,
       COUNT(DISTINCT e.id_ejemplar) AS ejemplares,
       COUNT(DISTINCT CASE WHEN e.estado = 'disponible' THEN e.id_ejemplar END) AS disponibles
FROM libro l
JOIN libro_autor la ON la.id_libro = l.id_libro
JOIN autor a        ON a.id_autor  = la.id_autor
JOIN ejemplar e     ON e.id_libro  = l.id_libro
GROUP BY l.id_libro, l.titulo
ORDER BY l.id_libro;
