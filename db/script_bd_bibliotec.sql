-- =====================================================================
-- BiblioTec - Script de creación de la base de datos bd_bibliotec
-- Motor: MySQL 8.0  |  Juego de caracteres: utf8mb4
-- Modelo: 9 entidades (Evidencia 1, sección 3.1)
-- ATENCIÓN: este script BORRA y vuelve a crear la base bd_bibliotec.
-- =====================================================================

DROP DATABASE IF EXISTS bd_bibliotec;
CREATE DATABASE bd_bibliotec
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE bd_bibliotec;

-- ---------------------------------------------------------------------
-- 1. CATEGORIA
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
  id_categoria  INT          NOT NULL AUTO_INCREMENT,
  nombre        VARCHAR(60)  NOT NULL,
  descripcion   VARCHAR(200) NULL,
  PRIMARY KEY (id_categoria),
  UNIQUE KEY uq_categoria_nombre (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. AUTOR
-- ---------------------------------------------------------------------
CREATE TABLE autor (
  id_autor         INT          NOT NULL AUTO_INCREMENT,
  nombre_completo  VARCHAR(100) NOT NULL,
  nacionalidad     VARCHAR(50)  NULL,
  PRIMARY KEY (id_autor)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. USUARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario  INT          NOT NULL AUTO_INCREMENT,
  codigo      VARCHAR(15)  NOT NULL,
  nombres     VARCHAR(60)  NOT NULL,
  apellidos   VARCHAR(60)  NOT NULL,
  correo      VARCHAR(100) NOT NULL,
  clave_hash  VARCHAR(255) NOT NULL,              -- BCrypt (RN-11)
  rol         ENUM('estudiante','docente','bibliotecario','admin') NOT NULL,
  estado      ENUM('activo','suspendido') NOT NULL DEFAULT 'activo',
  PRIMARY KEY (id_usuario),
  UNIQUE KEY uq_usuario_codigo (codigo),
  UNIQUE KEY uq_usuario_correo (correo)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. LIBRO
-- ---------------------------------------------------------------------
CREATE TABLE libro (
  id_libro          INT          NOT NULL AUTO_INCREMENT,
  isbn              VARCHAR(17)  NOT NULL,
  titulo            VARCHAR(150) NOT NULL,
  anio_publicacion  INT          NULL,
  editorial         VARCHAR(80)  NULL,
  id_categoria      INT          NOT NULL,
  PRIMARY KEY (id_libro),
  UNIQUE KEY uq_libro_isbn (isbn),
  KEY idx_libro_titulo (titulo),
  KEY idx_libro_categoria (id_categoria),
  CONSTRAINT fk_libro_categoria FOREIGN KEY (id_categoria)
    REFERENCES categoria (id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. LIBRO_AUTOR (relación muchos a muchos entre LIBRO y AUTOR)
-- ---------------------------------------------------------------------
CREATE TABLE libro_autor (
  id_libro  INT NOT NULL,
  id_autor  INT NOT NULL,
  PRIMARY KEY (id_libro, id_autor),
  KEY idx_libro_autor_autor (id_autor),
  CONSTRAINT fk_la_libro FOREIGN KEY (id_libro)
    REFERENCES libro (id_libro) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_la_autor FOREIGN KEY (id_autor)
    REFERENCES autor (id_autor) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. EJEMPLAR (copia física de un libro) - RN-07 y RN-13
-- ---------------------------------------------------------------------
CREATE TABLE ejemplar (
  id_ejemplar    INT         NOT NULL AUTO_INCREMENT,
  id_libro       INT         NOT NULL,
  codigo_barras  VARCHAR(20) NOT NULL,
  ubicacion      VARCHAR(40) NULL,
  estado         ENUM('disponible','prestado','reservado','baja') NOT NULL DEFAULT 'disponible',
  PRIMARY KEY (id_ejemplar),
  UNIQUE KEY uq_ejemplar_barras (codigo_barras),
  KEY idx_ejemplar_libro (id_libro),
  CONSTRAINT fk_ejemplar_libro FOREIGN KEY (id_libro)
    REFERENCES libro (id_libro) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. RESERVA (aparta un ejemplar disponible por 48 horas) - RN-08
-- ---------------------------------------------------------------------
CREATE TABLE reserva (
  id_reserva           INT      NOT NULL AUTO_INCREMENT,
  id_usuario           INT      NOT NULL,
  id_ejemplar          INT      NOT NULL,
  fecha_reserva        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_limite_recojo  DATETIME NOT NULL,
  estado               ENUM('activa','atendida','expirada','cancelada') NOT NULL DEFAULT 'activa',
  PRIMARY KEY (id_reserva),
  KEY idx_reserva_usuario (id_usuario),
  KEY idx_reserva_ejemplar (id_ejemplar),
  CONSTRAINT fk_reserva_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_reserva_ejemplar FOREIGN KEY (id_ejemplar)
    REFERENCES ejemplar (id_ejemplar) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. PRESTAMO - RN-02, RN-03, RN-04
--    id_reserva es opcional y único: un préstamo nace de 0 o 1 reserva.
-- ---------------------------------------------------------------------
CREATE TABLE prestamo (
  id_prestamo        INT        NOT NULL AUTO_INCREMENT,
  id_usuario         INT        NOT NULL,
  id_ejemplar        INT        NOT NULL,
  id_reserva         INT        NULL,
  fecha_prestamo     DATE       NOT NULL,
  fecha_vencimiento  DATE       NOT NULL,
  fecha_devolucion   DATE       NULL,
  renovaciones       TINYINT    NOT NULL DEFAULT 0,
  estado             ENUM('vigente','devuelto','vencido') NOT NULL DEFAULT 'vigente',
  PRIMARY KEY (id_prestamo),
  UNIQUE KEY uq_prestamo_reserva (id_reserva),
  KEY idx_prestamo_usuario (id_usuario),
  KEY idx_prestamo_ejemplar (id_ejemplar),
  CONSTRAINT ck_prestamo_renovaciones CHECK (renovaciones BETWEEN 0 AND 1),   -- RN-04
  CONSTRAINT fk_prestamo_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_prestamo_ejemplar FOREIGN KEY (id_ejemplar)
    REFERENCES ejemplar (id_ejemplar) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_prestamo_reserva FOREIGN KEY (id_reserva)
    REFERENCES reserva (id_reserva) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. MULTA - RN-05 (S/ 1.00 por día, tope S/ 60.00) y RN-06
-- ---------------------------------------------------------------------
CREATE TABLE multa (
  id_multa          INT           NOT NULL AUTO_INCREMENT,
  id_prestamo       INT           NOT NULL,
  dias_retraso      INT           NOT NULL,
  monto             DECIMAL(8,2)  NOT NULL,
  fecha_generacion  DATE          NOT NULL,
  estado            ENUM('pendiente','pagada') NOT NULL DEFAULT 'pendiente',
  PRIMARY KEY (id_multa),
  UNIQUE KEY uq_multa_prestamo (id_prestamo),
  CONSTRAINT ck_multa_monto CHECK (monto BETWEEN 0 AND 60.00),               -- RN-05              
  CONSTRAINT fk_multa_prestamo FOREIGN KEY (id_prestamo)
    REFERENCES prestamo (id_prestamo) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Verificación: debe listar 9 tablas
SHOW TABLES;
