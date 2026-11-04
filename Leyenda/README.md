# LEYENDA 2026 — Modo carrera JavaFX

Proyecto Java con Maven, JavaFX y MySQL. La interfaz está inspirada en juegos de gestión/carrera futbolística: panel lateral, tarjetas, OVR, estadísticas y mercado. Usa una paleta azul/celeste con detalles verdes; no incorpora logos oficiales de los clubes. Las insignias son gráficos locales genéricos con abreviaturas y colores para identificar a cada club.

## Funciones agregadas en esta versión

- **Dorsales repetibles:** varios jugadores pueden usar el mismo número, incluso el 10. La validación de `JugadorServiceImpl` solo controla que el dorsal esté entre 1 y 99; DAO ya no busca un dorsal ocupado. El esquema base solo requiere `id` como clave primaria.
- **Cinco entrenamientos principales por posición:**
  - Arquero: Reflejos, Atajadas, Salidas, Juego con los pies, Resistencia.
  - Defensa: Marcaje, Entradas, Cabezazo, Fuerza, Velocidad.
  - Mediocampista: Pase, Visión, Control, Recuperación, Resistencia.
  - Delantero: Remate, Definición, Regate, Velocidad, Cabezazo.
- Un entrenamiento especializado mejora el atributo principal (+3), un atributo relacionado (+1) y puede aumentar +1 OVR hasta los 29 años. También hay descanso, eventos aleatorios, progresión por rendimiento, títulos y premios. El OVR tiene máximo 99 y empieza a declinar desde los 30.
- **Mercado al final de cada temporada:** al completar cada año aparece una ventana para quedarse o aceptar una oferta. En dificultad NORMAL y FÁCIL también aparece entre cada uno de los años agrupados dentro de la etapa. Se generan hasta cinco ofertas, con oportunidades nacionales e internacionales según el OVR.
- **126 clubes en seis ligas:** 30 de la Liga Profesional Argentina 2026, más 20 de LaLiga 2026/27, 20 de Premier League 2026/27, 20 de Serie A 2026/27, 18 de Ligue 1 2026/27 y 18 de Liga Portugal 2026/27.
- Las competiciones nacionales se adaptan al país del club; el juego simula CONMEBOL para carreras en Argentina y UEFA para carreras europeas.

Las reputaciones de los clubes (1 a 5) son valores de balance del videojuego, no una clasificación oficial.

## Actualizar MySQL sin perder jugadores

1. Hacer una exportación de seguridad de la base `leyenda` desde phpMyAdmin.
2. Seleccionar la base `leyenda` en phpMyAdmin.
3. Importar `sql/actualizar_equipos_completo_2026_27.sql`. Este archivo crea/actualiza solamente la tabla `equipos` y agrega el campo `competicion` si falta; no borra ni modifica `jugadores`.
4. Importar opcionalmente `sql/permitir_dorsales_repetidos.sql` si alguna versión anterior había creado un índice UNIQUE para `dorsal`. El script elimina solamente índices UNIQUE secundarios que incluyen esa columna. En el esquema original del proyecto no existe tal índice, así que normalmente no hace falta ejecutarlo.

**No importar `sql/leyenda.sql` encima de tu base existente**: es un script de creación de ejemplo y puede contener datos de muestra.

## Ejecutar en IntelliJ

- Abrir la carpeta que contiene `pom.xml`.
- Configurar Project SDK y Maven Runner con el JDK 27, si se conserva `maven.compiler.release=27`.
- Revisar el puerto/usuario/contraseña en `src/main/java/util/Conexion.java`.
- Recargar Maven.
- Ejecutar `org.openjfx:javafx-maven-plugin:0.0.8:run` o el goal `javafx:run`.

La aplicación actualiza en MySQL los datos del jugador que ya tiene ID. El historial completo de carrera (años, competiciones pasadas y dificultad) aún no está persistido como una partida reanudable: permanece en memoria mientras la aplicación está abierta.

## Fuentes de los catálogos 2026/27

- Premier League: https://www.premierleague.com/en/news/4365156
- Liga Profesional Argentina: https://www.afa.com.ar/Torneos_Locales/posts/se-realizo-el-sorteo-de-la-liga-profesional-2026-ya-se-conocen-los-grupos-del-torneo-apertura-y-torneo-clausura
- LaLiga: https://www.laliga.com/es-NG/laliga-easports/clubes
- Serie A: https://www.legaseriea.it/serie-a/statistiche/clubs
- Ligue 1: https://www.lequipe.fr/collection/guide-ligue-1-2026-2027/
- Liga Portugal: https://www.ligaportugal.pt/competition/618/liga-portugal-betclic/round/20262027

Los datos de participantes corresponden al catálogo consultado para 2026/27; las probabilidades de clasificación y los resultados que genera el juego son ficticios y simplificados.
