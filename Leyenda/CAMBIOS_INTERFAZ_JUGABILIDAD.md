# Cambios de interfaz y jugabilidad - Leyenda

## Interfaz adaptable
- Se redujo el ancho mínimo de la ventana y se organizaron paneles, métricas y botones en contenedores que se reacomodan.
- Las páginas conservan desplazamiento vertical y el campo 1 vs 1 se escala al espacio disponible.

## Mercado de pases
- Las ofertas de fin de temporada ya no abren un diálogo emergente.
- Las ofertas quedan disponibles en la página Mercado de pases y se pueden aceptar desde sus tarjetas.

## Modo carrera
- Se quitó la selección Fácil/Normal. La carrera nueva utiliza dificultad Difícil y avanza una temporada por etapa.
- Se agregó la acción Retirarme ahora en el panel de carrera.

## Partido 1 vs 1
- Se corrigió la validación de gol: la pelota debe cruzar la línea del arco con su volumen completo por debajo del travesaño.
- Se agregó rebote en el travesaño para evitar que una pelota que pasó por arriba cuente al caer.
- Se añadieron piernas, botines, animación de carrera y remates desde la zona del pie.
- Cuando la temporada simula participación en una copa continental (Libertadores, Sudamericana o competiciones UEFA), aparece la opción de jugar el partido decisivo 1 vs 1 contra la IA en dificultad Leyenda. El resultado se registra en el historial y actualiza el estado de la copa.

## Pruebas
El entorno de edición no tiene Maven ni las bibliotecas JavaFX del proyecto instaladas. Se revisó la estructura y sintaxis del código, pero la compilación y prueba visual deben completarse en un equipo con el JDK y JavaFX configurados según `pom.xml`.

## Correcciones adicionales
- La pelota tiene una recuperación de seguridad: si sale de los límites por un rebote extremo o un salto de frame, vuelve al centro y el partido continúa.
- Se agregó el botón `SALIR DEL 1 VS 1` en la barra superior durante el partido. Detiene el temporizador y vuelve a la pantalla de origen.
- La aplicación inicia maximizada y admite ventanas más pequeñas; las escenas del juego se adaptan a la resolución disponible.
- El entrenamiento suma +2 OVR por sesión antes de los 30 años. Las temporadas destacadas pueden sumar +2 y el Balón de Oro +3. Desde los 30 se mantiene el declive sin aumentos.
- El precio estimado se calcula con el OVR y la edad. El valor base es $650.000 a 52 OVR y aumenta exponencialmente con la valoración; la edad reduce el valor de reventa.
- Se agregó una tribuna dibujada por código detrás del campo.

## Personalizar el fondo del estadio
El fondo se dibuja en `src/main/java/ui/OneVsOneGame.java`, dentro del método `drawStadium(GraphicsContext g)`. Ahí se pueden cambiar los colores, las filas de tribunas, los puntos del público y el cartel `LEYENDA STADIUM`. El método se llama desde `draw()`, antes de dibujar las líneas de la cancha y los jugadores. Para usar una imagen propia, se puede cargar un `Image` desde `src/main/resources/images/` y dibujarla con `g.drawImage(...)` dentro de `drawStadium`.
