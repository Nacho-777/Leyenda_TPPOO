# LEYENDA — Modo Carrera de Fútbol

Proyecto JavaFX + Maven + MySQL. La interfaz permite crear futbolistas, elegir la dificultad de simulación, entrenar, avanzar temporadas, revisar competiciones y aceptar ofertas de transferencia.

## Requisitos

- JDK 27 (el `pom.xml` está configurado para Java 27 y JavaFX 27).
- IntelliJ IDEA con soporte Maven.
- MySQL o MariaDB encendido.

## Ejecutar en IntelliJ

1. Abrí la carpeta raíz que contiene `pom.xml` como proyecto Maven.
2. Verificá que el Project SDK y el JRE de Maven sean JDK 27.
3. Recargá Maven.
4. Revisá el puerto, usuario y contraseña en `src/main/java/util/Conexion.java`.
5. Si es una instalación nueva, importá `sql/leyenda.sql` en phpMyAdmin. Si ya tenés la base `leyenda` y querés conservar tus jugadores, ejecutá solamente `sql/actualizar_equipos_2026.sql` después de seleccionar la base.
6. En Maven, ejecutá `Plugins > javafx > javafx:run`.

La clase de entrada visual es `ui.MainApp`. El modo consola anterior sigue disponible desde `app.Main`.

## Estructura

- `app`: modo consola anterior.
- `ui`: interfaz JavaFX.
- `model`: futbolista, subclases, clubes, ofertas y resultados.
- `dao` / `dao.impl`: CRUD de jugadores y consultas MySQL.
- `service`: validaciones, desarrollo, estadísticas, clubes, transferencias y competiciones.
- `exceptions`: excepciones personalizadas.
- `util`: conexión JDBC.
- `src/main/resources/styles`: tema visual azul/celeste.
- `src/main/resources/images/clubs`: insignias estilizadas locales para los clubes.
- `sql/leyenda.sql`: estructura de la BDD y carga inicial (incluye dos jugadores de ejemplo del volcado original).
- `sql/actualizar_equipos_2026.sql`: agrega/actualiza solamente los 30 equipos sin tocar jugadores existentes.

## Funciones incluidas

- Dificultades: difícil (1 temporada por avance), normal (2) y fácil (4).
- Edad de carrera de 16 a 40 años.
- Entrenamientos: velocidad, definición, pase/visión, regate/control, fuerza, resistencia, marcaje/entradas, centros, técnica avanzada, preparación física, análisis táctico, entrenamiento intensivo, mentoría, cabezazo y atajadas (según posición), además de descanso.
- El OVR puede mejorar por entrenamientos, mentoría, temporadas destacadas, títulos y Balón de Oro, con tope de 99. Desde los 30 años ya no sube y empieza el declive.
- Mercado de pases con ofertas por reputación y nivel del futbolista. Aceptar una oferta actualiza el club y valor del jugador en MySQL.
- Competiciones nacionales simuladas: Apertura, Clausura, Copa Argentina, tabla anual/Campeón de Liga y supercopas cuando corresponda.
- Competiciones internacionales: Libertadores o Sudamericana según clasificación simulada. La Recopa Sudamericana se habilita en la temporada siguiente cuando se gana una copa continental.
- Historial de competiciones durante la sesión.
- Catálogo de 30 equipos de Primera División argentina 2026, con insignias de diseño local.

## Clubes incluidos (Primera División 2026)

Aldosivi, Argentinos Juniors, Atlético Tucumán, Banfield, Barracas Central, Belgrano, Boca Juniors, Central Córdoba, Defensa y Justicia, Deportivo Riestra, Estudiantes de La Plata, Estudiantes de Río Cuarto, Gimnasia de La Plata, Gimnasia de Mendoza, Huracán, Independiente, Independiente Rivadavia, Instituto, Lanús, Newell's Old Boys, Platense, Racing Club, River Plate, Rosario Central, San Lorenzo, Sarmiento, Talleres, Tigre, Unión y Vélez Sarsfield.

## Importante antes de importar SQL

Si ya existe tu base `leyenda` con jugadores guardados, no importes encima todo `leyenda.sql` sin hacer una copia de seguridad. Para actualizar el catálogo de equipos, usá `actualizar_equipos_2026.sql`; no modifica la tabla `jugadores`.

## Importante sobre los escudos

Los PNG de `images/clubs` son insignias estilizadas propias para el videojuego, con colores e iniciales representativas. No son los escudos oficiales de los clubes. Se pueden reemplazar por imágenes oficiales autorizadas conservando los nombres de archivo.

## Persistencia y límites actuales

- El jugador, su club, atributos, OVR y valor de mercado se guardan en MySQL.
- El historial de temporadas/competiciones y la dificultad seleccionada se mantienen en memoria mientras la aplicación está abierta. Todavía no hay guardado completo de una partida para cerrarla y retomarla más tarde.
- La clasificación a torneos y los resultados están simplificados para el videojuego; no son un fixture oficial ni una predicción deportiva.
- La reputación de cada club es una escala interna del juego, no un ranking oficial.

## Referencias para la temporada 2026

- AFA, sorteo y grupos de la Liga Profesional 2026: https://www.afa.com.ar/es/posts/sorteo-de-la-liga-profesional-2026-se-definen-los-grupos-del-torneo-apertura-y-torneo-clausura
- LPF, competencias previstas para 2026: https://www.lpf.org.ar/notas/institucional/2025/11/20/las-competencias-de-2026/
- Reglamento LPF Primera División 2026: https://www.ligaprofesional.ar/wp-content/uploads/2026/01/Reglamento-Torneos-LPF-Primera-2026-1.pdf

La simulación de temporadas toma como referencia esas competiciones, pero simplifica sus formatos y clasificaciones para que funcionen como modo carrera.
