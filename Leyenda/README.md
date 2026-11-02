# Leyenda - simulador de carrera futbolística

Proyecto Java organizado con Maven, JDBC/MySQL, capas DAO y Service, CRUD y excepciones personalizadas.

## Estructura

- `src/main/java/app`: punto de entrada (`Main`).
- `src/main/java/model`: `Jugador` y sus subclases, e interfaz `Entrenable`.
- `src/main/java/dao`: contrato del DAO.
- `src/main/java/dao/impl`: consultas SQL y mapeo de subclases.
- `src/main/java/service`: lógica de validación, operaciones del juego y estadísticas.
- `src/main/java/exceptions`: excepciones de las reglas del TP.
- `src/main/java/util`: conexión a MySQL.
- `sql/leyenda.sql`: script para una base nueva.

## Preparar MySQL

1. Iniciá MySQL/MariaDB.
2. En phpMyAdmin, importá `sql/leyenda.sql`. El archivo crea y selecciona la base `leyenda`.
3. Abrí `src/main/java/util/Conexion.java` y verificá el puerto, el usuario y la contraseña. La configuración incluida es `localhost:3306`, usuario `root` y contraseña vacía. Si tu servidor usa otro puerto, cambiá la URL.
4. En IntelliJ, abrí la carpeta que contiene `pom.xml` como proyecto Maven y recargá Maven para descargar Connector/J.
5. Ejecutá `app.Main`.

> Si ya existe una tabla `jugadores` de una versión anterior, `CREATE TABLE IF NOT EXISTS` no la modifica. Hacé una copia de seguridad antes de cambiar una tabla existente. Para probar desde cero, usá una base vacía o aplicá la estructura del script manualmente.

## CRUD

`JugadorDAO` declara `insertar`, `buscarPorId`, `listar`, `actualizar`, `eliminar` y una consulta auxiliar para comprobar dorsales. `JugadorDaoImpl` implementa las operaciones con `PreparedStatement`. `JugadorServiceImpl` aplica las validaciones del TP antes de acceder al DAO.

## Excepciones del TP

- `EdadInvalidaException`: edad de 16 a 40.
- `OvrInvalidoException`: OVR de 1 a 99.
- `PrecioInvalidoException`: precio no negativo.
- `ResistenciaInvalidaException`: resistencia de 1 a 99.
- `DorsalOcupadoException`: dorsal de 1 a 99 y único en la tabla.


## Modo carrera: progresión y eventos

- Los entrenamientos disponibles son velocidad, definición, pase y visión, regate y control, fuerza, resistencia, marcaje y entradas y, para arqueros, atajadas. También se puede elegir descanso.
- Antes de los 30 años, entrenar puede otorgar +1 OVR. Algunos eventos positivos también pueden impulsar la valoración.
- Desde los 30 años el OVR nunca aumenta. Cada temporada se aplica un desgaste base y las temporadas de bajo rendimiento pueden acelerar la caída. Una gran temporada ayuda a conservar atributos físicos, pero no elimina el declive del OVR.
- Los eventos aleatorios incluyen molestias musculares, recuperación, consejos de referentes, planes físicos, entrenamientos extra y rachas de confianza. Cada evento modifica atributos, resistencia o valoración.
- Los cambios de atributos, edad y OVR se guardan mediante el DAO existente en MySQL.
