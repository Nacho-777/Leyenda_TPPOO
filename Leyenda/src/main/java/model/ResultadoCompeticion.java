package model;

/** Resultado de una competición dentro de una temporada simulada. */
public class ResultadoCompeticion {
    private final String nombre;
    private final String categoria;
    private final String fase;
    private final boolean campeon;
    private final String detalle;

    public ResultadoCompeticion(String nombre, String categoria, String fase,
                                boolean campeon, String detalle) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.fase = fase;
        this.campeon = campeon;
        this.detalle = detalle;
    }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getFase() { return fase; }
    public boolean isCampeon() { return campeon; }
    public String getDetalle() { return detalle; }
}
