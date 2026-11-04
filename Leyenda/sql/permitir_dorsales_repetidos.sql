-- Permite que varios jugadores utilicen el mismo dorsal.
-- La estructura original de Leyenda solo necesita que `id` sea PRIMARY KEY.
-- Este script elimina (si existen) indices UNIQUE secundarios que incluyan `dorsal`.
-- No borra jugadores ni modifica sus dorsales.

DROP PROCEDURE IF EXISTS leyenda_quitar_unicidad_dorsal;
DELIMITER $$
CREATE PROCEDURE leyenda_quitar_unicidad_dorsal()
BEGIN
    DECLARE terminado INT DEFAULT 0;
    DECLARE nombre_indice VARCHAR(64);
    DECLARE indices CURSOR FOR
        SELECT INDEX_NAME
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'jugadores'
          AND NON_UNIQUE = 0
          AND INDEX_NAME <> 'PRIMARY'
        GROUP BY INDEX_NAME
        HAVING SUM(CASE WHEN COLUMN_NAME = 'dorsal' THEN 1 ELSE 0 END) > 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET terminado = 1;

    OPEN indices;
    leer_indices: LOOP
        FETCH indices INTO nombre_indice;
        IF terminado = 1 THEN
            LEAVE leer_indices;
        END IF;
        SET @sql_borrar = CONCAT('ALTER TABLE `jugadores` DROP INDEX `', REPLACE(nombre_indice, '`', '``'), '`');
        PREPARE sentencia FROM @sql_borrar;
        EXECUTE sentencia;
        DEALLOCATE PREPARE sentencia;
    END LOOP;
    CLOSE indices;
END$$
DELIMITER ;

CALL leyenda_quitar_unicidad_dorsal();
DROP PROCEDURE leyenda_quitar_unicidad_dorsal;

-- Comprobación: esta consulta debería devolver 0 filas.
SELECT INDEX_NAME, COLUMN_NAME
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'jugadores'
  AND NON_UNIQUE = 0
  AND INDEX_NAME <> 'PRIMARY'
  AND COLUMN_NAME = 'dorsal';
