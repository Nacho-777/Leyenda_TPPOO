package service;

import model.ResultadoCompeticion;
import java.util.ArrayList;
import java.util.List;

/** Resultado visible de una temporada simulada. */
public class ResultadoTemporada {
    private final int anio;
    private final String club;
    private final int partidos;
    private final int goles;
    private final int asistencias;
    private final boolean campeon;
    private final boolean balonOro;
    private final List<ResultadoCompeticion> competiciones = new ArrayList<>();

    public ResultadoTemporada(int anio, String club, int partidos, int goles,
                              int asistencias, boolean campeon, boolean balonOro) {
        this.anio = anio;
        this.club = club;
        this.partidos = partidos;
        this.goles = goles;
        this.asistencias = asistencias;
        this.campeon = campeon;
        this.balonOro = balonOro;
    }

    public int getAnio() { return anio; }
    public String getClub() { return club; }
    public int getPartidos() { return partidos; }
    public int getGoles() { return goles; }
    public int getAsistencias() { return asistencias; }
    public boolean isCampeon() { return campeon; }
    public boolean isBalonOro() { return balonOro; }
    public List<ResultadoCompeticion> getCompeticiones() { return List.copyOf(competiciones); }
    public void setCompeticiones(List<ResultadoCompeticion> resultados) {
        competiciones.clear();
        if (resultados != null) competiciones.addAll(resultados);
    }
}
