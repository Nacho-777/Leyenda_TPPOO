package service;

import model.ClubInfo;
import model.Jugador;
import model.ResultadoCompeticion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/** Simula las competiciones nacionales e internacionales según el país del club actual. */
public class CompeticionesService {
    private boolean disputarRecopaPorLibertadores;
    private boolean disputarRecopaPorSudamericana;
    private boolean disputarRecopaDeCampeones;
    private boolean disputarSupercopaEuropa;

    public void reiniciarCarrera() {
        disputarRecopaPorLibertadores = false;
        disputarRecopaPorSudamericana = false;
        disputarRecopaDeCampeones = false;
        disputarSupercopaEuropa = false;
    }

    public List<ResultadoCompeticion> simularTemporada(Jugador jugador, ClubInfo club, Random random) {
        Random rng = random == null ? new Random() : random;
        List<ResultadoCompeticion> resultados = new ArrayList<>();
        int ovr = jugador == null ? 50 : jugador.getOvr();
        int reputacion = club == null ? 2 : club.getReputacion();
        int fuerza = ovr + reputacion * 4;
        String pais = club == null ? "Argentina" : club.getPais();

        switch (pais.toLowerCase(Locale.ROOT)) {
            case "españa" -> simularEspaña(resultados, fuerza, reputacion, ovr, rng);
            case "inglaterra" -> simularInglaterra(resultados, fuerza, reputacion, ovr, rng);
            case "italia" -> simularItalia(resultados, fuerza, reputacion, ovr, rng);
            case "francia" -> simularFrancia(resultados, fuerza, reputacion, ovr, rng);
            case "portugal" -> simularPortugal(resultados, fuerza, reputacion, ovr, rng);
            default -> simularArgentina(resultados, fuerza, reputacion, ovr, rng);
        }
        return resultados;
    }

    private void simularArgentina(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion apertura = simularLiga("Torneo Apertura", fuerza, rng, 9);
        ResultadoCompeticion clausura = simularLiga("Torneo Clausura", fuerza, rng, 9);
        ResultadoCompeticion copa = simularCopa("Copa Argentina", fuerza, rng, 7);
        r.add(apertura); r.add(clausura); r.add(copa);
        ResultadoCompeticion liga = simularLiga("Campeón de Liga (tabla anual)", fuerza, rng, 5);
        r.add(liga);

        if (disputarRecopaPorLibertadores || disputarRecopaPorSudamericana) {
            r.add(simularContinental("Recopa Sudamericana", fuerza + 9, rng, 15));
        }
        disputarRecopaPorLibertadores = false;
        disputarRecopaPorSudamericana = false;

        if (disputarRecopaDeCampeones) {
            r.add(simularCopa("Recopa de Campeones", fuerza + 8, rng, 15));
        }
        disputarRecopaDeCampeones = false;

        if (apertura.isCampeon() || clausura.isCampeon()) r.add(simularCopa("Trofeo de Campeones", fuerza + 5, rng, 12));
        if (copa.isCampeon()) r.add(simularCopa("Supercopa Argentina", fuerza + 4, rng, 14));
        if (liga.isCampeon() && (apertura.isCampeon() || clausura.isCampeon())) {
            r.add(simularCopa("Supercopa Internacional", fuerza + 7, rng, 18));
        }

        int chanceLibertadores = limitar(12, 72, 4 + reputacion * 8 + Math.max(0, ovr - 60));
        int chanceSudamericana = limitar(12, 55, 10 + reputacion * 6 + Math.max(0, ovr - 55) / 2);
        int sorteo = rng.nextInt(100);
        int acumulada = limitar(0, 90, chanceLibertadores + chanceSudamericana);
        if (sorteo < chanceLibertadores) {
            ResultadoCompeticion continental = simularContinental("Copa Libertadores", fuerza + 8, rng, 4);
            r.add(continental);
            disputarRecopaPorLibertadores = continental.isCampeon();
        } else if (sorteo < acumulada) {
            ResultadoCompeticion continental = simularContinental("Copa Sudamericana", fuerza + 3, rng, 9);
            r.add(continental);
            disputarRecopaPorSudamericana = continental.isCampeon();
        }
        disputarRecopaDeCampeones = apertura.isCampeon() || clausura.isCampeon()
                || copa.isCampeon() || liga.isCampeon();
    }

    private void simularEspaña(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion liga = simularLiga("LaLiga", fuerza, rng, 9);
        ResultadoCompeticion copa = simularCopa("Copa del Rey", fuerza, rng, 7);
        r.add(liga); r.add(copa);
        if (liga.isCampeon() || copa.isCampeon()) r.add(simularCopa("Supercopa de España", fuerza + 5, rng, 13));
        simularEuropa(r, fuerza, reputacion, ovr, rng);
    }

    private void simularInglaterra(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion liga = simularLiga("Premier League", fuerza, rng, 8);
        ResultadoCompeticion fa = simularCopa("FA Cup", fuerza, rng, 8);
        ResultadoCompeticion efl = simularCopa("EFL Cup", fuerza, rng, 5);
        r.add(liga); r.add(fa); r.add(efl);
        if (liga.isCampeon() || fa.isCampeon()) r.add(simularCopa("FA Community Shield", fuerza + 5, rng, 12));
        simularEuropa(r, fuerza, reputacion, ovr, rng);
    }

    private void simularItalia(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion liga = simularLiga("Serie A", fuerza, rng, 8);
        ResultadoCompeticion copa = simularCopa("Coppa Italia", fuerza, rng, 8);
        r.add(liga); r.add(copa);
        if (liga.isCampeon() || copa.isCampeon()) r.add(simularCopa("Supercoppa Italiana", fuerza + 5, rng, 12));
        simularEuropa(r, fuerza, reputacion, ovr, rng);
    }

    private void simularFrancia(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion liga = simularLiga("Ligue 1", fuerza, rng, 8);
        ResultadoCompeticion copa = simularCopa("Coupe de France", fuerza, rng, 8);
        r.add(liga); r.add(copa);
        if (liga.isCampeon() || copa.isCampeon()) r.add(simularCopa("Trophée des Champions", fuerza + 5, rng, 12));
        simularEuropa(r, fuerza, reputacion, ovr, rng);
    }

    private void simularPortugal(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        ResultadoCompeticion liga = simularLiga("Liga Portugal", fuerza, rng, 8);
        ResultadoCompeticion copa = simularCopa("Taça de Portugal", fuerza, rng, 8);
        ResultadoCompeticion copaLiga = simularCopa("Taça da Liga", fuerza, rng, 5);
        r.add(liga); r.add(copa); r.add(copaLiga);
        if (liga.isCampeon() || copa.isCampeon()) r.add(simularCopa("Supertaça Cândido de Oliveira", fuerza + 5, rng, 12));
        simularEuropa(r, fuerza, reputacion, ovr, rng);
    }

    /** Sortea cupos UEFA y premia al club con torneos europeos según OVR y reputación. */
    private void simularEuropa(List<ResultadoCompeticion> r, int fuerza, int reputacion, int ovr, Random rng) {
        if (disputarSupercopaEuropa) {
            r.add(simularContinental("UEFA Super Cup", fuerza + 10, rng, 14));
        }
        disputarSupercopaEuropa = false;

        int champions = limitar(8, 68, 5 + reputacion * 9 + Math.max(0, ovr - 60) / 2);
        int europa = limitar(8, 32, 6 + reputacion * 4 + Math.max(0, ovr - 55) / 3);
        int conference = limitar(5, 22, 4 + reputacion * 3 + Math.max(0, ovr - 55) / 4);
        int sorteo = rng.nextInt(100);
        ResultadoCompeticion continental = null;
        if (sorteo < champions) {
            continental = simularContinental("UEFA Champions League", fuerza + 10, rng, 4);
        } else if (sorteo < champions + europa) {
            continental = simularContinental("UEFA Europa League", fuerza + 6, rng, 8);
        } else if (sorteo < champions + europa + conference) {
            continental = simularContinental("UEFA Conference League", fuerza + 2, rng, 12);
        }
        if (continental != null) {
            r.add(continental);
            disputarSupercopaEuropa = continental.isCampeon()
                    && !"UEFA Conference League".equals(continental.getNombre());
        }
    }

    private ResultadoCompeticion simularLiga(String nombre, int fuerza, Random random, int ajuste) {
        int probabilidad = limitar(3, 42, 3 + (fuerza - 55) / 2 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN";
        else if (fuerza >= 92 && random.nextInt(100) < 75) fase = "FINAL / SEMIFINAL";
        else if (fuerza >= 78) fase = "ZONA ALTA / PLAYOFFS";
        else if (fuerza >= 65) fase = "MITAD DE TABLA";
        else fase = "FASE REGULAR";
        return new ResultadoCompeticion(nombre, "NACIONAL", fase, campeon,
                campeon ? "¡Tu club se quedó con el título!" : "Temporada competitiva: " + fase.toLowerCase(Locale.ROOT) + ".");
    }

    private ResultadoCompeticion simularCopa(String nombre, int fuerza, Random random, int ajuste) {
        int probabilidad = limitar(2, 38, 2 + (fuerza - 55) / 2 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN";
        else if (fuerza >= 90 && random.nextInt(100) < 70) fase = "SEMIFINAL";
        else if (fuerza >= 75) fase = "CUARTOS DE FINAL";
        else if (fuerza >= 60) fase = "OCTAVOS DE FINAL";
        else fase = "PRIMERAS RONDAS";
        return new ResultadoCompeticion(nombre, "NACIONAL", fase, campeon,
                campeon ? "¡Levantaste la copa!" : "El equipo llegó hasta " + fase.toLowerCase(Locale.ROOT) + ".");
    }

    private ResultadoCompeticion simularContinental(String nombre, int fuerza, Random random, int ajuste) {
        int probabilidad = limitar(1, 28, 1 + (fuerza - 62) / 3 + ajuste);
        boolean campeon = random.nextInt(100) < probabilidad;
        String fase;
        if (campeon) fase = "CAMPEÓN INTERNACIONAL";
        else if (fuerza >= 95 && random.nextInt(100) < 45) fase = "FINALISTA";
        else if (fuerza >= 90) fase = "SEMIFINAL";
        else if (fuerza >= 83) fase = "CUARTOS DE FINAL";
        else if (fuerza >= 70) fase = "OCTAVOS DE FINAL";
        else fase = "FASE DE GRUPOS";
        return new ResultadoCompeticion(nombre, "INTERNACIONAL", fase, campeon,
                campeon ? "¡Tu equipo conquistó el torneo!" : "Participación internacional: " + fase.toLowerCase(Locale.ROOT) + ".");
    }

    private int limitar(int min, int max, int valor) {
        return Math.max(min, Math.min(max, valor));
    }
}
