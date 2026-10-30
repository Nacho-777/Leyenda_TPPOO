package service;

import model.Jugador;

import java.util.Locale;
import java.util.Random;

public class EstadisticasService {
    private int totalGoles = 0;
    private int totalAsistencias = 0;
    private int totalPartidos = 0;
    private int totalTitulos = 0;
    private int totalBalonesDeOro = 0;

    /** Mantiene el método usado por la versión de consola. */
    public void simularTemporada(Jugador jugador, String clubActual, int anio, Random random) {
        ResultadoTemporada resultado = simularTemporadaConResultado(jugador, clubActual, anio, random);
        imprimirResultado(resultado);
    }

    /** Simula una temporada y devuelve los datos para mostrarlos en JavaFX. */
    public ResultadoTemporada simularTemporadaConResultado(
            Jugador jugador, String clubActual, int anio, Random random) {
        if (jugador == null) throw new IllegalArgumentException("El jugador no puede ser null.");
        if (random == null) random = new Random();

        int ovr = jugador.getOvr();
        int partidos = 30 + random.nextInt(15);
        int goles;
        int asistencias;
        String posicion = jugador.getPosicion();
        String pos = posicion == null ? "" : posicion.toLowerCase(Locale.ROOT);

        if (pos.contains("arquero")) {
            goles = 0;
            asistencias = random.nextInt(2);
        } else if (pos.contains("defensa") || pos.contains("lateral") || pos.contains("central")) {
            goles = random.nextInt((ovr / 20) + 1);
            asistencias = random.nextInt((ovr / 15) + 1);
        } else if (pos.contains("medio") || pos.contains("volante")) {
            goles = random.nextInt((ovr / 10) + 1);
            asistencias = random.nextInt((ovr / 8) + 1);
        } else {
            goles = random.nextInt((ovr / 5) + 1);
            asistencias = random.nextInt((ovr / 10) + 1);
        }

        boolean campeon = false;
        if (ovr >= 80 && random.nextInt(100) < 60) {
            campeon = true;
        } else if (ovr >= 70 && random.nextInt(100) < 30) {
            campeon = true;
        }

        boolean balonOro = ovr >= 88
                && (goles + asistencias >= 25 || pos.contains("arquero"))
                && random.nextInt(100) < 40;

        totalPartidos += partidos;
        totalGoles += goles;
        totalAsistencias += asistencias;
        if (campeon) totalTitulos++;
        if (balonOro) totalBalonesDeOro++;

        return new ResultadoTemporada(anio, clubActual, partidos, goles, asistencias, campeon, balonOro);
    }

    private void imprimirResultado(ResultadoTemporada resultado) {
        System.out.println("\n--- RESUMEN DE LA TEMPORADA " + resultado.getAnio() + " ---");
        System.out.println("Partidos jugados: " + resultado.getPartidos());
        System.out.println("Goles marcados: " + resultado.getGoles());
        System.out.println("Asistencias: " + resultado.getAsistencias());
        if (resultado.isCampeon()) {
            System.out.println("¡CAMPEÓN con " + resultado.getClub() + "!");
        }
        if (resultado.isBalonOro()) {
            System.out.println("¡Ganaste el BALÓN DE ORO!");
        }
    }

    public void mostrarResumenCarrera(Jugador jugador) {
        System.out.println("\n--- ESTADÍSTICAS FINALES DE CARRERA ---");
        System.out.println("Jugador: " + jugador.getNombre());
        System.out.println("Partidos totales: " + totalPartidos);
        System.out.println("Goles totales: " + totalGoles);
        System.out.println("Asistencias totales: " + totalAsistencias);
        System.out.println("Títulos colectivos: " + totalTitulos);
        System.out.println("Balones de Oro: " + totalBalonesDeOro);
    }
}
