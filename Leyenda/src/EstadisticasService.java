package model;

import java.util.Random;

public class EstadisticasService {

    private int totalGoles = 0;
    private int totalAsistencias = 0;
    private int totalPartidos = 0;
    private int totalTitulos = 0;
    private int totalBalonesDeOro = 0;

    public void simularTemporada(model.Jugador miJugador, String clubActual, int anio, Random random) {
        int ovr = miJugador.getOvr();
        int partidos = 30 + random.nextInt(15);
        int goles = 0;
        int asistencias = 0;

        String pos = miJugador.getPosicion().toLowerCase();

        if (pos.contains("arquero")) {
            goles = 0;
            asistencias = random.nextInt(2);
        } else if (pos.contains("defensa") || pos.contains("lateral") || pos.contains("central")) {
            goles = random.nextInt((ovr / 20) + 1);
            asistencias = random.nextInt((ovr / 15) + 1);
        } else if (pos.contains("medio") || pos.contains("volante")) {
            goles = random.nextInt((ovr / 10) + 1);
            asistencias = random.nextInt((ovr / 8) + 1);
        } else { // Delanteros / Extremos
            goles = random.nextInt((ovr / 5) + 1);
            asistencias = random.nextInt((ovr / 10) + 1);
        }

        totalPartidos += partidos;
        totalGoles += goles;
        totalAsistencias += asistencias;

        System.out.println("\n📊 --- RESUMEN DE LA TEMPORADA " + anio + " ---");
        System.out.println("Partidos jugados: " + partidos);
        System.out.println("Goles marcados: " + goles);
        System.out.println("Asistencias: " + asistencias);

        // Lógica de títulos según OVR
        if (ovr >= 80 && random.nextInt(100) < 60) {
            totalTitulos++;
            System.out.println("🏆 ¡CAMPEÓN! Ganaste un título esta temporada con " + clubActual + "!");
        } else if (ovr >= 70 && random.nextInt(100) < 30) {
            totalTitulos++;
            System.out.println("🏆 ¡CAMPEÓN! Ganaste una copa con " + clubActual + "!");
        }

        // Balón de Oro (requiere alto OVR y buen rendimiento)
        if (ovr >= 88 && (goles + asistencias >= 25 || pos.contains("arquero")) && random.nextInt(100) < 40) {
            totalBalonesDeOro++;
            System.out.println("🥇 ¡¡FELICIDADES!! Ganaste el BALÓN DE ORO de esta temporada. 🥇");
        }
    }

    public void mostrarResumenCarrera(model.Jugador miJugador) {
        System.out.println("\n🏆 ================================================= 🏆");
        System.out.println("             ESTADÍSTICAS FINALES DE CARRERA          ");
        System.out.println("🏆 ================================================= 🏆");
        System.out.println("Jugador: " + miJugador.getNombre());
        System.out.println("Partidos totales: " + totalPartidos);
        System.out.println("Goles totales: " + totalGoles);
        System.out.println("Asistencias totales: " + totalAsistencias);
        System.out.println("Títulos colectivos: " + totalTitulos);
        System.out.println("Balones de Oro: " + totalBalonesDeOro);
        System.out.println("=====================================================\n");
    }
}
