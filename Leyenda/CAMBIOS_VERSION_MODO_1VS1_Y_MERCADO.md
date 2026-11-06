# Leyenda - Ajustes del modo 1 vs 1, competiciones y mercado

## 1 vs 1
- El bot ya no salta continuamente detrás de una pelota que quedó por encima del travesaño.
- La IA vuelve a una posición defensiva cuando la pelota está fuera de una altura jugable y tiene enfriamiento de salto.
- Los remates de ambos futbolistas salen desde el pie. Se quitó el impulso de pelota por choque del pecho/cuerpo.
- El cabezazo del usuario solo se habilita para defensores y delanteros con atributo Cabezazo >= 65; el valor entrenado modifica alcance y potencia.
- Se agregaron postes/travesaños blancos y red, además de rebote del travesaño y recuperación anti-bloqueo.
- F11 alterna pantalla completa.
- El botón X MIL NOCHES abre una búsqueda en YouTube. El volumen se maneja en el reproductor de YouTube.

## Pelota 2026
- Copiar `ball-2026.png` en `src/main/resources/images/` para que se cargue automáticamente.
- Alternativamente, usar `-Dleyenda.ballImageUrl=https://.../imagen.png` con una URL directa de imagen.

## Competiciones
- Se quitó el catálogo fijo de todas las copas. La pantalla muestra solo resultados reales del historial y un partido decisivo disponible.
- Argentina solo puede ofrecer Libertadores/Sudamericana; Europa solo Champions/Europa/Conference.
- El partido importante muestra el nombre del torneo y la temporada antes de comenzar.
- Si el resultado corresponde a una final, se muestra campeón o subcampeón con el mensaje correspondiente.

## OVR y mercado
- El OVR se calcula usando atributos ponderados según la posición: arquero, defensa, mediocampista o delantero.
- Los premios de temporada mejoran atributos propios de la posición y el OVR vuelve a calcularse desde esos atributos.
- Las ofertas de clubes respetan un límite de reputación según la calidad real del jugador. Ya no se completa la lista con clubes de élite fuera de rango.
- Al terminar una temporada, si hay ofertas, el mercado interrumpe el flujo y muestra los clubes interesados; se puede aceptar o continuar sin fichar.

## Música
El botón abre la búsqueda de “X Mil Noches” en YouTube. Un enlace normal `youtube.com/watch` no es una fuente de audio directa para JavaFX MediaPlayer; el volumen se ajusta desde YouTube. Para integrar una pista dentro del juego se necesita un archivo de audio con licencia/permiso o una URL directa compatible.
