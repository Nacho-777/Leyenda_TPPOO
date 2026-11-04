package model;

/** Datos locales de presentación y clasificación de un club del modo carrera. */
public class ClubInfo {
    private final String nombre;
    private final String abreviatura;
    private final String provincia;
    private final String slug;
    private final String colorPrimario;
    private final String colorSecundario;
    private final int reputacion;
    private final String pais;
    private final String competicion;

    /** Constructor compatible con los clubes argentinos de versiones anteriores. */
    public ClubInfo(String nombre, String abreviatura, String provincia, String slug,
                    String colorPrimario, String colorSecundario, int reputacion) {
        this(nombre, abreviatura, provincia, slug, colorPrimario, colorSecundario,
                reputacion, "Argentina", "Liga Profesional Argentina");
    }

    public ClubInfo(String nombre, String abreviatura, String provincia, String slug,
                    String colorPrimario, String colorSecundario, int reputacion,
                    String pais, String competicion) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.provincia = provincia;
        this.slug = slug;
        this.colorPrimario = colorPrimario;
        this.colorSecundario = colorSecundario;
        this.reputacion = Math.max(1, Math.min(5, reputacion));
        this.pais = pais;
        this.competicion = competicion;
    }

    public String getNombre() { return nombre; }
    public String getAbreviatura() { return abreviatura; }
    /** Ciudad o localidad del club; se conserva el nombre por compatibilidad con la UI existente. */
    public String getProvincia() { return provincia; }
    public String getSlug() { return slug; }
    public String getColorPrimario() { return colorPrimario; }
    public String getColorSecundario() { return colorSecundario; }
    public int getReputacion() { return reputacion; }
    public String getPais() { return pais; }
    public String getCompeticion() { return competicion; }
    public String getLogoResource() { return "/images/clubs/" + slug + ".png"; }

    @Override
    public String toString() { return nombre + " · " + pais; }
}
