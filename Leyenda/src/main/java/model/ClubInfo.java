package model;

/** Datos locales de presentación de un club de la Primera División 2026. */
public class ClubInfo {
    private final String nombre;
    private final String abreviatura;
    private final String provincia;
    private final String slug;
    private final String colorPrimario;
    private final String colorSecundario;
    private final int reputacion; // escala interna del juego: 1 a 5

    public ClubInfo(String nombre, String abreviatura, String provincia, String slug,
                    String colorPrimario, String colorSecundario, int reputacion) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.provincia = provincia;
        this.slug = slug;
        this.colorPrimario = colorPrimario;
        this.colorSecundario = colorSecundario;
        this.reputacion = reputacion;
    }

    public String getNombre() { return nombre; }
    public String getAbreviatura() { return abreviatura; }
    public String getProvincia() { return provincia; }
    public String getSlug() { return slug; }
    public String getColorPrimario() { return colorPrimario; }
    public String getColorSecundario() { return colorSecundario; }
    public int getReputacion() { return reputacion; }
    public String getLogoResource() { return "/images/clubs/" + slug + ".png"; }
    @Override public String toString() { return nombre; }
}
