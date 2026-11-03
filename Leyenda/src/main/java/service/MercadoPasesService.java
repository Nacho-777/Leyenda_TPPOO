package service;

import model.ClubInfo;
import model.Jugador;
import model.OfertaTransferencia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Genera ofertas de clubes de Primera según el nivel del futbolista. */
public class MercadoPasesService {
    private final ClubesService clubesService;
    public MercadoPasesService(ClubesService clubesService) { this.clubesService = clubesService; }

    public List<OfertaTransferencia> generarOfertas(Jugador jugador, String clubActual, Random random) {
        if (jugador == null) return List.of();
        Random rng = random == null ? new Random() : random;
        int reputacionMinima = jugador.getOvr() >= 85 ? 3 : jugador.getOvr() >= 72 ? 2 : 1;
        int reputacionMaxima = jugador.getOvr() >= 90 ? 5 : jugador.getOvr() >= 80 ? 5 : jugador.getOvr() >= 65 ? 4 : 3;
        List<ClubInfo> candidatos = new ArrayList<>();
        for (ClubInfo club : clubesService.listarTodos()) {
            if (!club.getNombre().equalsIgnoreCase(clubActual)
                    && club.getReputacion() >= reputacionMinima
                    && club.getReputacion() <= reputacionMaxima) candidatos.add(club);
        }
        // Si el nivel es muy bajo o el jugador está en el club más grande, no dejar el mercado vacío.
        if (candidatos.size() < 3) {
            for (ClubInfo club : clubesService.listarTodos()) {
                if (!club.getNombre().equalsIgnoreCase(clubActual) && !candidatos.contains(club)) candidatos.add(club);
            }
        }
        java.util.Collections.shuffle(candidatos, rng);
        List<OfertaTransferencia> ofertas = new ArrayList<>();
        for (ClubInfo club : candidatos.stream().limit(5).toList()) {
            double factor = 0.85 + club.getReputacion() * 0.055 + rng.nextDouble() * 0.12;
            double valor = Math.max(100_000, jugador.getPrecio() * factor);
            int salario = Math.max(18_000, jugador.getOvr() * 7_500 + club.getReputacion() * 18_000 + rng.nextInt(30_000));
            int contrato = 2 + rng.nextInt(3);
            String motivo = club.getReputacion() >= 4
                    ? "Te busca para competir por títulos y torneos continentales."
                    : club.getReputacion() == 3
                    ? "Quiere reforzar el plantel con un jugador de tu proyección."
                    : "Te ofrece continuidad y protagonismo en el primer equipo.";
            ofertas.add(new OfertaTransferencia(club, valor, salario, contrato, motivo));
        }
        ofertas.sort(Comparator.comparingInt((OfertaTransferencia o) -> o.getClub().getReputacion()).reversed());
        return ofertas;
    }
}
