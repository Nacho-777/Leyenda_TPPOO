package service;

import model.ClubInfo;
import model.Jugador;
import model.ResultadoCompeticion;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Simulación simplificada de competiciones nacionales e internacionales del modo carrera. */
public class CompeticionesService {
    private boolean disputarRecopaPorLibertadores;
    private boolean disputarRecopaPorSudamericana;

    public void reiniciarCarrera() {
        disputarRecopaPorLibertadores = false;
        disputarRecopaPorSudamericana = false;
    }

    public List<ResultadoCompeticion> simularTemporada(Jugador jugador, ClubInfo club, Random random) {
        Random rng = random == null ? new Random() : random;
        List<ResultadoCompeticion> resultados = new ArrayList<>();
        int ovr = jugador == null ? 50 : jugador.getOvr();
        int reputacion = club == null ? 2 : club.getReputacion();
        int fuerza = ovr + reputacion * 4;

        ResultadoCompeticion apertura = simularLiga("Torneo Apertura", "NACIONAL", fuerza, rng, 9);
        ResultadoCompeticion clausura = simularLiga("Torneo Clausura", "NACIONAL", fuerza, rng, 9);
        ResultadoCompeticion copaArgentina = simularCopa("Copa Argentina", "NACIONAL", fuerza, rng, 7);
        resultados.add(apertura);
        resultados.add(clausura);
        resultados.add(copaArgentina);

        boolean campeonApertura = apertura.isCampeon();
        boolean campeonClausura = clausura.isCampeon();
        boolean campeonCopa = copaArgentina.isCampeon();
        ResultadoCompeticion liga = simularLiga("Campeón de Liga (tabla anual)", "NACIONAL", fuerza, rng, 5);
        resultados.add(liga);

        if (disputarRecopaPorLibertadores || disputarRecopaPorSudamericana) {
            resultados.add(simularContinental("Recopa Sudamericana", fuerza + 9, rng, 15));
        }
        disputarRecopaPorLibertadores = false;
        disputarRecopaPorSudamericana = false;

        if (campeonApertura || campeonClausura) {
            resultados.add(simularCopa("Trofeo de Campeones", "NACIONAL", fuerza + 5, rng, 12));
        }
        if (campeonCopa) {
            resultados.add(simularCopa("Supercopa Argentina", "NACIONAL", fuerza + 4, rng, 14));
        }
        if (liga.isCampeon() && (campeonApertura || campeonClausura)) {
            resultados.add(simularCopa("Supercopa Internacional", "NACIONAL", fuerza + 7, rng, 18));
        }

        // La clasificación continental se modela con reputación del club y OVR del futbolista.
        // No todos los equipos clasifican todos los años: primero se sortea el cupo Libertadores,
        // luego Sudamericana y, si no se alcanza, el equipo no juega torneo continental.
        int chanceLibertadores = limitar(12, 72, 4 + reputacion * 8 + Math.max(0, ovr - 60));
        int chanceSudamericana = limitar(12, 55, 10 + reputacion * 6 + Math.max(0, ovr - 55) / 2);
        int sorteoContinental = rng.nextInt(100);
        int chanceDeClasificar = limitar(0, 90, chanceLibertadores + chanceSudamericana);
        boolean libertadores = sorteoContinental < chanceLibertadores;
        boolean sudamericana = !libertadores && sorteoContinental < chanceDeClasificar;
        if (libertadores) {
            ResultadoCompeticion copa = simularContinental("Copa Libertadores", fuerza + 8, rng, 4);
            resultados.add(copa);
            disputarRecopaPorLibertadores = copa.isCampeon();
        } else if (sudamericana) {
            ResultadoCompeticion copa = simularContinental("Copa Sudamericana", fuerza + 3, rng, 9);
            resultados.add(copa);
            disputarRecopaPorSudamericana = copa.isCampeon();
        }
        return resultados;
    }

    private ResultadoCompeticion simularLiga(String nombre, String categoria, int fuerza,
                                               Random random, int ajuste) {
        int probabilidad = limitar(3, 42, 3 + (fuerza - 55) / 2 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN";
        else if (fuerza >= 92 && random.nextInt(100) < 75) fase = "FINAL / SEMIFINAL";
        else if (fuerza >= 78) fase = "PLAYOFFS / CUARTOS";
        else if (fuerza >= 65) fase = "FASE DE GRUPOS";
        else fase = "FASE REGULAR";
        return new ResultadoCompeticion(nombre, categoria, fase, campeon,
                campeon ? "¡Tu club se quedó con el título!" : "Temporada competitiva: " + fase.toLowerCase() + ".");
    }

    private ResultadoCompeticion simularCopa(String nombre, String categoria, int fuerza,
                                               Random random, int ajuste) {
        int probabilidad = limitar(2, 38, 2 + (fuerza - 55) / 2 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN";
        else if (fuerza >= 90 && random.nextInt(100) < 70) fase = "SEMIFINAL";
        else if (fuerza >= 75) fase = "CUARTOS DE FINAL";
        else if (fuerza >= 60) fase = "OCTAVOS DE FINAL";
        else fase = "PRIMERAS RONDAS";
        return new ResultadoCompeticion(nombre, categoria, fase, campeon,
                campeon ? "¡Levantaste la copa!" : "El equipo llegó hasta " + fase.toLowerCase() + ".");
    }

    private ResultadoCompeticion simularContinental(String nombre, int fuerza,
                                                      Random random, int ajuste) {
        int probabilidad = limitar(1, 28, 1 + (fuerza - 62) / 3 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN CONTINENTAL";
        else if (fuerza >= 95) fase = "SEMIFINAL";
        else if (fuerza >= 83) fase = "CUARTOS DE FINAL";
        else if (fuerza >= 70) fase = "OCTAVOS DE FINAL";
        else fase = "FASE DE GRUPOS";
        return new ResultadoCompeticion(nombre, "INTERNACIONAL", fase, campeon,
                campeon ? "¡Tu equipo conquistó América!" : "Participación continental: " + fase.toLowerCase() + ".");
    }

    private int limitar(int min, int max, int valor) { return Math.max(min, Math.min(max, valor)); }
}
