-- Actualiza solamente el catalogo de equipos de Leyenda para la temporada 2026.
-- Ejecutar despues de seleccionar la base de datos leyenda en phpMyAdmin.
-- No borra ni modifica la tabla jugadores.

CREATE TABLE IF NOT EXISTS equipos (
    id INT NOT NULL PRIMARY KEY,
    nombre VARCHAR(50) DEFAULT NULL,
    pais VARCHAR(50) DEFAULT NULL,
    tier VARCHAR(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO equipos (id, nombre, pais, tier) VALUES
(1, 'Aldosivi', 'Argentina', '2'),
(2, 'Argentinos Juniors', 'Argentina', '3'),
(3, 'Atlético Tucumán', 'Argentina', '3'),
(4, 'Banfield', 'Argentina', '3'),
(5, 'Barracas Central', 'Argentina', '2'),
(6, 'Belgrano', 'Argentina', '3'),
(7, 'Boca Juniors', 'Argentina', '5'),
(8, 'Central Córdoba', 'Argentina', '2'),
(9, 'Defensa y Justicia', 'Argentina', '3'),
(10, 'Deportivo Riestra', 'Argentina', '2'),
(11, 'Estudiantes de La Plata', 'Argentina', '4'),
(12, 'Estudiantes de Río Cuarto', 'Argentina', '2'),
(13, 'Gimnasia de La Plata', 'Argentina', '3'),
(14, 'Gimnasia de Mendoza', 'Argentina', '2'),
(15, 'Huracán', 'Argentina', '3'),
(16, 'Independiente', 'Argentina', '4'),
(17, 'Independiente Rivadavia', 'Argentina', '3'),
(18, 'Instituto', 'Argentina', '3'),
(19, 'Lanús', 'Argentina', '3'),
(20, 'Newell''s Old Boys', 'Argentina', '3'),
(21, 'Platense', 'Argentina', '2'),
(22, 'Racing Club', 'Argentina', '4'),
(23, 'River Plate', 'Argentina', '5'),
(24, 'Rosario Central', 'Argentina', '4'),
(25, 'San Lorenzo', 'Argentina', '4'),
(26, 'Sarmiento', 'Argentina', '2'),
(27, 'Talleres', 'Argentina', '4'),
(28, 'Tigre', 'Argentina', '3'),
(29, 'Unión', 'Argentina', '3'),
(30, 'Vélez Sarsfield', 'Argentina', '4')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    pais = VALUES(pais),
    tier = VALUES(tier);
