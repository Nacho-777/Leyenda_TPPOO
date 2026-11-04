package service;

import model.ClubInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Catálogo local de clubes para el modo carrera.
 * Argentina: Primera División 2026. Europa: participantes 2026/27.
 * Los escudos que no tengan imagen propia se representan con abreviatura y colores del club.
 */
public class ClubesService {
    private final List<ClubInfo> clubes = List.of(
        // ARGENTINA - LIGA PROFESIONAL 2026 (30 clubes)
        c("Aldosivi", "ALD", "Mar del Plata", "aldosivi", "#D4E31E", "#087B42", 2, "Argentina", "Liga Profesional Argentina"),
        c("Argentinos Juniors", "ARG", "La Paternal", "argentinos-juniors", "#D92732", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Atlético Tucumán", "ATU", "Tucumán", "atletico-tucuman", "#65BCEB", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Banfield", "BAN", "Banfield", "banfield", "#087A45", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Barracas Central", "BAR", "Barracas", "barracas-central", "#C8202C", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Belgrano", "BEL", "Córdoba", "belgrano", "#5AB4E5", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Boca Juniors", "BOC", "La Boca", "boca-juniors", "#082C69", "#F5C400", 5, "Argentina", "Liga Profesional Argentina"),
        c("Central Córdoba", "CCO", "Santiago del Estero", "central-cordoba", "#171717", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Defensa y Justicia", "DYJ", "Florencio Varela", "defensa-y-justicia", "#D5E51B", "#13834A", 3, "Argentina", "Liga Profesional Argentina"),
        c("Deportivo Riestra", "RIE", "Nueva Pompeya", "deportivo-riestra", "#191919", "#6C6C6C", 2, "Argentina", "Liga Profesional Argentina"),
        c("Estudiantes de La Plata", "EDLP", "La Plata", "estudiantes-la-plata", "#C92330", "#FFFFFF", 4, "Argentina", "Liga Profesional Argentina"),
        c("Estudiantes de Río Cuarto", "ERC", "Río Cuarto", "estudiantes-rio-cuarto", "#55A8DB", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Gimnasia de La Plata", "GELP", "La Plata", "gimnasia-la-plata", "#1A2B58", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Gimnasia de Mendoza", "GDM", "Mendoza", "gimnasia-mendoza", "#172D59", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Huracán", "HUR", "Parque Patricios", "huracan", "#F4F4F4", "#D52732", 3, "Argentina", "Liga Profesional Argentina"),
        c("Independiente", "IND", "Avellaneda", "independiente", "#D51E2B", "#FFFFFF", 4, "Argentina", "Liga Profesional Argentina"),
        c("Independiente Rivadavia", "IR", "Mendoza", "independiente-rivadavia", "#1479C9", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Instituto", "INS", "Córdoba", "instituto", "#C92532", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Lanús", "LAN", "Lanús", "lanus", "#8F1630", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Newell's Old Boys", "NOB", "Rosario", "newells-old-boys", "#D52732", "#111111", 3, "Argentina", "Liga Profesional Argentina"),
        c("Platense", "PLA", "Vicente López", "platense", "#6B263A", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Racing Club", "RAC", "Avellaneda", "racing-club", "#75BCE8", "#FFFFFF", 4, "Argentina", "Liga Profesional Argentina"),
        c("River Plate", "RIV", "Núñez", "river-plate", "#FFFFFF", "#D42132", 5, "Argentina", "Liga Profesional Argentina"),
        c("Rosario Central", "ROS", "Rosario", "rosario-central", "#53AEE1", "#F2C600", 4, "Argentina", "Liga Profesional Argentina"),
        c("San Lorenzo", "SLO", "Boedo", "san-lorenzo", "#12396D", "#D52A36", 4, "Argentina", "Liga Profesional Argentina"),
        c("Sarmiento", "SAR", "Junín", "sarmiento", "#1B8A4A", "#FFFFFF", 2, "Argentina", "Liga Profesional Argentina"),
        c("Talleres", "TAL", "Córdoba", "talleres", "#16366F", "#FFFFFF", 4, "Argentina", "Liga Profesional Argentina"),
        c("Tigre", "TIG", "Victoria", "tigre", "#17427F", "#D32937", 3, "Argentina", "Liga Profesional Argentina"),
        c("Unión", "UNI", "Santa Fe", "union", "#D52A36", "#FFFFFF", 3, "Argentina", "Liga Profesional Argentina"),
        c("Vélez Sarsfield", "VEL", "Liniers", "velez-sarsfield", "#153B80", "#FFFFFF", 4, "Argentina", "Liga Profesional Argentina"),

        // ESPAÑA - LALIGA EA SPORTS 2026/27 (20 clubes)
        c("Athletic Club", "ATH", "Bilbao", "athletic-club", "#D71920", "#FFFFFF", 4, "España", "LaLiga"),
        c("Atlético de Madrid", "ATM", "Madrid", "atletico-madrid", "#C8102E", "#FFFFFF", 5, "España", "LaLiga"),
        c("CA Osasuna", "OSA", "Pamplona", "osasuna", "#C8102E", "#142B55", 3, "España", "LaLiga"),
        c("Celta de Vigo", "CEL", "Vigo", "celta-vigo", "#8CC8E8", "#FFFFFF", 4, "España", "LaLiga"),
        c("Deportivo Alavés", "ALA", "Vitoria-Gasteiz", "alaves", "#1D3F8B", "#FFFFFF", 3, "España", "LaLiga"),
        c("Elche CF", "ELC", "Elche", "elche", "#0B7A3E", "#FFFFFF", 3, "España", "LaLiga"),
        c("FC Barcelona", "BAR", "Barcelona", "barcelona", "#A50044", "#004D98", 5, "España", "LaLiga"),
        c("Getafe CF", "GET", "Getafe", "getafe", "#00529F", "#FFFFFF", 3, "España", "LaLiga"),
        c("Levante UD", "LEV", "Valencia", "levante", "#D71920", "#00529F", 3, "España", "LaLiga"),
        c("Málaga CF", "MAL", "Málaga", "malaga", "#79BDE8", "#FFFFFF", 3, "España", "LaLiga"),
        c("Racing de Santander", "RAC", "Santander", "racing-santander", "#16834A", "#FFFFFF", 3, "España", "LaLiga"),
        c("Rayo Vallecano", "RAY", "Madrid", "rayo-vallecano", "#FFFFFF", "#D71920", 3, "España", "LaLiga"),
        c("RC Deportivo", "DEP", "A Coruña", "deportivo-coruna", "#76BDE8", "#FFFFFF", 3, "España", "LaLiga"),
        c("RCD Espanyol", "ESP", "Barcelona", "espanyol", "#007FC8", "#FFFFFF", 3, "España", "LaLiga"),
        c("Real Betis", "BET", "Sevilla", "real-betis", "#0B8F4D", "#FFFFFF", 4, "España", "LaLiga"),
        c("Real Madrid", "RMA", "Madrid", "real-madrid", "#FFFFFF", "#174A8B", 5, "España", "LaLiga"),
        c("Real Sociedad", "RSO", "San Sebastián", "real-sociedad", "#1D4F91", "#FFFFFF", 4, "España", "LaLiga"),
        c("Sevilla FC", "SEV", "Sevilla", "sevilla", "#D71920", "#FFFFFF", 4, "España", "LaLiga"),
        c("Valencia CF", "VAL", "Valencia", "valencia", "#FFFFFF", "#E87500", 4, "España", "LaLiga"),
        c("Villarreal CF", "VIL", "Vila-real", "villarreal", "#F7D117", "#003B7A", 4, "España", "LaLiga"),

        // INGLATERRA - PREMIER LEAGUE 2026/27 (20 clubes)
        c("AFC Bournemouth", "BOU", "Bournemouth", "bournemouth", "#D71920", "#111111", 3, "Inglaterra", "Premier League"),
        c("Arsenal", "ARS", "Londres", "arsenal", "#EF0107", "#FFFFFF", 5, "Inglaterra", "Premier League"),
        c("Aston Villa", "AVL", "Birmingham", "aston-villa", "#670E36", "#95BFE5", 4, "Inglaterra", "Premier League"),
        c("Brentford", "BRE", "Londres", "brentford", "#E30613", "#FFFFFF", 3, "Inglaterra", "Premier League"),
        c("Brighton & Hove Albion", "BHA", "Brighton", "brighton", "#0057B8", "#FFFFFF", 4, "Inglaterra", "Premier League"),
        c("Chelsea", "CHE", "Londres", "chelsea", "#034694", "#FFFFFF", 5, "Inglaterra", "Premier League"),
        c("Coventry City", "COV", "Coventry", "coventry", "#77B9E8", "#FFFFFF", 2, "Inglaterra", "Premier League"),
        c("Crystal Palace", "CRY", "Londres", "crystal-palace", "#1B458F", "#C4122E", 3, "Inglaterra", "Premier League"),
        c("Everton", "EVE", "Liverpool", "everton", "#003399", "#FFFFFF", 3, "Inglaterra", "Premier League"),
        c("Fulham", "FUL", "Londres", "fulham", "#FFFFFF", "#111111", 3, "Inglaterra", "Premier League"),
        c("Hull City", "HUL", "Hull", "hull-city", "#F5A623", "#111111", 2, "Inglaterra", "Premier League"),
        c("Ipswich Town", "IPS", "Ipswich", "ipswich", "#0055A5", "#D71920", 2, "Inglaterra", "Premier League"),
        c("Leeds United", "LEE", "Leeds", "leeds", "#FFFFFF", "#1D428A", 3, "Inglaterra", "Premier League"),
        c("Liverpool", "LIV", "Liverpool", "liverpool", "#C8102E", "#F6EB61", 5, "Inglaterra", "Premier League"),
        c("Manchester City", "MCI", "Manchester", "manchester-city", "#6CABDD", "#FFFFFF", 5, "Inglaterra", "Premier League"),
        c("Manchester United", "MUN", "Manchester", "manchester-united", "#DA291C", "#FBE122", 5, "Inglaterra", "Premier League"),
        c("Newcastle United", "NEW", "Newcastle", "newcastle", "#111111", "#FFFFFF", 4, "Inglaterra", "Premier League"),
        c("Nottingham Forest", "NFO", "Nottingham", "nottingham-forest", "#DD0000", "#FFFFFF", 3, "Inglaterra", "Premier League"),
        c("Sunderland", "SUN", "Sunderland", "sunderland", "#D71920", "#FFFFFF", 3, "Inglaterra", "Premier League"),
        c("Tottenham Hotspur", "TOT", "Londres", "tottenham", "#FFFFFF", "#132257", 4, "Inglaterra", "Premier League"),

        // ITALIA - SERIE A 2026/27 (20 clubes)
        c("AC Milan", "MIL", "Milán", "ac-milan", "#FB090B", "#111111", 5, "Italia", "Serie A"),
        c("Atalanta", "ATA", "Bérgamo", "atalanta", "#1A4B9B", "#111111", 4, "Italia", "Serie A"),
        c("Bologna", "BOL", "Bolonia", "bologna", "#1A2F6B", "#C8102E", 3, "Italia", "Serie A"),
        c("Cagliari", "CAG", "Cagliari", "cagliari", "#C8102E", "#0B3C75", 3, "Italia", "Serie A"),
        c("Como", "COM", "Como", "como", "#1C75BC", "#FFFFFF", 3, "Italia", "Serie A"),
        c("Fiorentina", "FIO", "Florencia", "fiorentina", "#592C82", "#FFFFFF", 4, "Italia", "Serie A"),
        c("Frosinone", "FRO", "Frosinone", "frosinone", "#F4D21F", "#1D3F8B", 2, "Italia", "Serie A"),
        c("Genoa", "GEN", "Génova", "genoa", "#C8102E", "#1D3F8B", 3, "Italia", "Serie A"),
        c("Inter", "INT", "Milán", "inter", "#00529F", "#111111", 5, "Italia", "Serie A"),
        c("Juventus", "JUV", "Turín", "juventus", "#111111", "#FFFFFF", 5, "Italia", "Serie A"),
        c("Lazio", "LAZ", "Roma", "lazio", "#87D8F7", "#FFFFFF", 4, "Italia", "Serie A"),
        c("Lecce", "LEC", "Lecce", "lecce", "#F5D21F", "#D71920", 2, "Italia", "Serie A"),
        c("Monza", "MON", "Monza", "monza", "#D71920", "#FFFFFF", 2, "Italia", "Serie A"),
        c("Napoli", "NAP", "Nápoles", "napoli", "#12A0D7", "#FFFFFF", 5, "Italia", "Serie A"),
        c("Parma", "PAR", "Parma", "parma", "#F5D21F", "#1D3F8B", 3, "Italia", "Serie A"),
        c("Roma", "ROM", "Roma", "roma", "#8E1F2F", "#F2B12A", 5, "Italia", "Serie A"),
        c("Sassuolo", "SAS", "Sassuolo", "sassuolo", "#087A45", "#111111", 3, "Italia", "Serie A"),
        c("Torino", "TOR", "Turín", "torino", "#7A263A", "#FFFFFF", 3, "Italia", "Serie A"),
        c("Udinese", "UDI", "Udine", "udinese", "#111111", "#FFFFFF", 3, "Italia", "Serie A"),
        c("Venezia", "VEN", "Venecia", "venezia", "#F36C21", "#111111", 2, "Italia", "Serie A"),

        // FRANCIA - LIGUE 1 2026/27 (18 clubes)
        c("Angers SCO", "ANG", "Angers", "angers", "#111111", "#FFFFFF", 2, "Francia", "Ligue 1"),
        c("AJ Auxerre", "AUX", "Auxerre", "auxerre", "#FFFFFF", "#1C4D91", 2, "Francia", "Ligue 1"),
        c("Stade Brestois 29", "BRE", "Brest", "brest", "#D71920", "#FFFFFF", 3, "Francia", "Ligue 1"),
        c("Le Havre AC", "HAV", "Le Havre", "le-havre", "#1D3F8B", "#87D8F7", 2, "Francia", "Ligue 1"),
        c("Le Mans FC", "LEM", "Le Mans", "le-mans", "#D71920", "#F5D21F", 2, "Francia", "Ligue 1"),
        c("RC Lens", "LEN", "Lens", "lens", "#D71920", "#F5D21F", 4, "Francia", "Ligue 1"),
        c("FC Lorient", "LOR", "Lorient", "lorient", "#F36C21", "#111111", 3, "Francia", "Ligue 1"),
        c("LOSC Lille", "LIL", "Lille", "lille", "#D71920", "#1D3F8B", 4, "Francia", "Ligue 1"),
        c("Olympique Lyonnais", "LYO", "Lyon", "lyon", "#1D3F8B", "#D71920", 4, "Francia", "Ligue 1"),
        c("Olympique de Marseille", "OM", "Marsella", "marseille", "#87D8F7", "#FFFFFF", 4, "Francia", "Ligue 1"),
        c("AS Monaco", "MON", "Mónaco", "monaco", "#D71920", "#FFFFFF", 4, "Francia", "Ligue 1"),
        c("OGC Nice", "NIC", "Niza", "nice", "#D71920", "#111111", 3, "Francia", "Ligue 1"),
        c("Paris FC", "PFC", "París", "paris-fc", "#1D3F8B", "#FFFFFF", 3, "Francia", "Ligue 1"),
        c("Paris Saint-Germain", "PSG", "París", "paris-saint-germain", "#004170", "#D71920", 5, "Francia", "Ligue 1"),
        c("Stade Rennais", "REN", "Rennes", "rennes", "#D71920", "#111111", 4, "Francia", "Ligue 1"),
        c("RC Strasbourg Alsace", "STR", "Estrasburgo", "strasbourg", "#1D3F8B", "#87D8F7", 3, "Francia", "Ligue 1"),
        c("Toulouse FC", "TOU", "Toulouse", "toulouse", "#6E3B9C", "#FFFFFF", 3, "Francia", "Ligue 1"),
        c("ESTAC Troyes", "TRO", "Troyes", "troyes", "#1D3F8B", "#FFFFFF", 2, "Francia", "Ligue 1"),

        // PORTUGAL - LIGA PORTUGAL BETCLIC 2026/27 (18 clubes)
        c("FC Porto", "FCP", "Oporto", "porto", "#00529F", "#FFFFFF", 5, "Portugal", "Liga Portugal"),
        c("SL Benfica", "BEN", "Lisboa", "benfica", "#D71920", "#FFFFFF", 5, "Portugal", "Liga Portugal"),
        c("Sporting CP", "SPO", "Lisboa", "sporting", "#087A45", "#FFFFFF", 5, "Portugal", "Liga Portugal"),
        c("Santa Clara", "SCL", "Ponta Delgada", "santa-clara", "#D71920", "#FFFFFF", 3, "Portugal", "Liga Portugal"),
        c("FC Arouca", "ARO", "Arouca", "arouca", "#F5D21F", "#111111", 2, "Portugal", "Liga Portugal"),
        c("Académico de Viseu", "ACV", "Viseu", "academico-viseu", "#F5D21F", "#111111", 2, "Portugal", "Liga Portugal"),
        c("Gil Vicente FC", "GIL", "Barcelos", "gil-vicente", "#D71920", "#111111", 3, "Portugal", "Liga Portugal"),
        c("SC Braga", "BRA", "Braga", "braga", "#D71920", "#FFFFFF", 4, "Portugal", "Liga Portugal"),
        c("Moreirense FC", "MOR", "Moreira de Cónegos", "moreirense", "#087A45", "#111111", 2, "Portugal", "Liga Portugal"),
        c("FC Alverca", "ALV", "Alverca", "alverca", "#D71920", "#FFFFFF", 2, "Portugal", "Liga Portugal"),
        c("FC Famalicão", "FAM", "Vila Nova de Famalicão", "famalicao", "#1D3F8B", "#FFFFFF", 3, "Portugal", "Liga Portugal"),
        c("Estrela Amadora", "EST", "Amadora", "estrela-amadora", "#D71920", "#FFFFFF", 3, "Portugal", "Liga Portugal"),
        c("Vitória SC", "VSC", "Guimarães", "vitoria-guimaraes", "#111111", "#FFFFFF", 4, "Portugal", "Liga Portugal"),
        c("Marítimo", "MAR", "Funchal", "maritimo", "#087A45", "#D71920", 3, "Portugal", "Liga Portugal"),
        c("CD Nacional", "NAC", "Funchal", "nacional", "#111111", "#FFFFFF", 2, "Portugal", "Liga Portugal"),
        c("Casa Pia AC", "CAS", "Lisboa", "casa-pia", "#111111", "#FFFFFF", 2, "Portugal", "Liga Portugal"),
        c("Rio Ave FC", "RIO", "Vila do Conde", "rio-ave", "#087A45", "#FFFFFF", 3, "Portugal", "Liga Portugal"),
        c("Estoril Praia", "EST", "Estoril", "estoril", "#F5D21F", "#00529F", 3, "Portugal", "Liga Portugal")
    );

    private static ClubInfo c(String nombre, String abbr, String ciudad, String slug,
                              String primario, String secundario, int reputacion,
                              String pais, String liga) {
        return new ClubInfo(nombre, abbr, ciudad, slug, primario, secundario,
                reputacion, pais, liga);
    }

    public List<ClubInfo> listarTodos() {
        List<ClubInfo> copia = new ArrayList<>(clubes);
        copia.sort(Comparator.comparing(ClubInfo::getPais).thenComparing(ClubInfo::getNombre));
        return copia;
    }

    public List<String> listarCompeticiones() {
        return clubes.stream().map(ClubInfo::getCompeticion).distinct().sorted().toList();
    }

    public List<ClubInfo> listarPorCompeticion(String competicion) {
        if (competicion == null || competicion.isBlank() || "Todas las ligas".equalsIgnoreCase(competicion)) {
            return listarTodos();
        }
        return clubes.stream()
                .filter(c -> c.getCompeticion().equalsIgnoreCase(competicion.trim()))
                .sorted(Comparator.comparing(ClubInfo::getNombre))
                .toList();
    }

    public List<ClubInfo> listarPorPais(String pais) {
        if (pais == null || pais.isBlank()) return listarTodos();
        return clubes.stream().filter(c -> c.getPais().equalsIgnoreCase(pais.trim()))
                .sorted(Comparator.comparing(ClubInfo::getNombre)).toList();
    }

    public Optional<ClubInfo> buscarPorNombre(String nombre) {
        if (nombre == null) return Optional.empty();
        return clubes.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre.trim())).findFirst();
    }

    public int cantidadClubes() { return clubes.size(); }
}
