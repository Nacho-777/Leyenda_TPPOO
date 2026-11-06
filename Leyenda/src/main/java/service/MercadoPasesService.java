package service;

import model.ClubInfo;
import model.Jugador;
import model.OfertaTransferencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Genera ofertas realistas de clubes argentinos y europeos según el nivel del futbolista. */
public class MercadoPasesService {
    private final ClubesService clubesService;

    public MercadoPasesService(ClubesService clubesService) {
        this.clubesService = clubesService;
    }

    public List<OfertaTransferencia> generarOfertas(Jugador jugador, String clubActual, Random random) {
        if (jugador == null) return List.of();
        Random rng = random == null ? new Random() : random;
        List<ClubInfo> todos = clubesService.listarTodos();
        ClubInfo actual = clubesService.buscarPorNombre(clubActual).orElse(null);
        String paisActual = actual == null ? "Argentina" : actual.getPais();
        int ovr = jugador.getOvr();

        // A mayor OVR, aparecen proyectos de mayor reputación y más opciones internacionales.
        int calidad = (ovr * 2 + jugador.getVelocidad() + jugador.getRemate() + jugador.getPase()
                + jugador.getRegate() + jugador.getFuerza() + jugador.getControl()) / 8;
        int reputacionMaxima = calidad >= 88 ? 5 : calidad >= 80 ? 4 : calidad >= 70 ? 3 : 2;
        List<ClubInfo> nacionales = new ArrayList<>();
        List<ClubInfo> internacionales = new ArrayList<>();
        for (ClubInfo club : todos) {
            if (club.getNombre().equalsIgnoreCase(clubActual)) continue;
            if (club.getReputacion() > reputacionMaxima) continue;
            if (club.getPais().equalsIgnoreCase(paisActual)) nacionales.add(club);
            else internacionales.add(club);
        }

        Collections.shuffle(nacionales, rng);
        Collections.shuffle(internacionales, rng);

        int cuposInternacionales = calidad >= 85 ? 3 : calidad >= 72 ? 2 : 1;
        List<ClubInfo> seleccionados = new ArrayList<>();
        tomar(internacionales, seleccionados, cuposInternacionales);
        tomar(nacionales, seleccionados, 5 - seleccionados.size());

        // No se agregan clubes fuera del nivel permitido: es preferible mostrar menos ofertas
        // antes que regalarle a un jugador de atributos bajos un fichaje de élite irreal.

        List<OfertaTransferencia> ofertas = new ArrayList<>();
        for (ClubInfo club : seleccionados) {
            double primaReputacion = 0.82 + club.getReputacion() * 0.065;
            double primaInternacional = club.getPais().equalsIgnoreCase(paisActual) ? 1.0 : 1.12;
            double variacion = 0.94 + rng.nextDouble() * 0.18;
            double valor = Math.max(100_000, jugador.getPrecio() * primaReputacion * primaInternacional * variacion);
            int salario = Math.max(18_000,
                    ovr * 7_500 + club.getReputacion() * 18_000 + (club.getPais().equalsIgnoreCase(paisActual) ? 0 : 35_000)
                            + rng.nextInt(30_000));
            int contrato = 2 + rng.nextInt(3);
            String motivo;
            if (!club.getPais().equalsIgnoreCase(paisActual)) {
                motivo = "Quiere llevarte a otra liga y darte una oportunidad internacional.";
            } else if (club.getReputacion() >= 4) {
                motivo = "Te busca para competir por títulos y torneos continentales.";
            } else if (club.getReputacion() == 3) {
                motivo = "Quiere reforzar el plantel con un jugador de tu proyección.";
            } else {
                motivo = "Te ofrece continuidad y protagonismo en el primer equipo.";
            }
            ofertas.add(new OfertaTransferencia(club, valor, salario, contrato, motivo));
        }
        ofertas.sort(Comparator.comparingInt((OfertaTransferencia o) -> o.getClub().getReputacion()).reversed());
        return ofertas;
    }

    private void tomar(List<ClubInfo> origen, List<ClubInfo> destino, int cantidad) {
        for (ClubInfo club : origen) {
            if (cantidad <= 0) break;
            if (!destino.contains(club)) {
                destino.add(club);
                cantidad--;
            }
        }
    }
}
