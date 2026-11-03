package service;

import model.ClubInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Catálogo de los 30 participantes de Primera División argentina 2026. */
public class ClubesService {
    private final List<ClubInfo> clubes = List.of(
        new ClubInfo("Aldosivi", "ALD", "Mar del Plata", "aldosivi", "#D4E31E", "#087B42", 2),
        new ClubInfo("Argentinos Juniors", "ARG", "La Paternal", "argentinos-juniors", "#D92732", "#FFFFFF", 3),
        new ClubInfo("Atlético Tucumán", "ATU", "Tucumán", "atletico-tucuman", "#65BCEB", "#FFFFFF", 3),
        new ClubInfo("Banfield", "BAN", "Banfield", "banfield", "#087A45", "#FFFFFF", 3),
        new ClubInfo("Barracas Central", "BAR", "Barracas", "barracas-central", "#C8202C", "#FFFFFF", 2),
        new ClubInfo("Belgrano", "BEL", "Córdoba", "belgrano", "#5AB4E5", "#FFFFFF", 3),
        new ClubInfo("Boca Juniors", "BOC", "La Boca", "boca-juniors", "#082C69", "#F5C400", 5),
        new ClubInfo("Central Córdoba", "CCO", "Santiago del Estero", "central-cordoba", "#171717", "#FFFFFF", 2),
        new ClubInfo("Defensa y Justicia", "DYJ", "Florencio Varela", "defensa-y-justicia", "#D5E51B", "#13834A", 3),
        new ClubInfo("Deportivo Riestra", "RIE", "Nueva Pompeya", "deportivo-riestra", "#191919", "#6C6C6C", 2),
        new ClubInfo("Estudiantes de La Plata", "EDLP", "La Plata", "estudiantes-la-plata", "#C92330", "#FFFFFF", 4),
        new ClubInfo("Estudiantes de Río Cuarto", "ERC", "Río Cuarto", "estudiantes-rio-cuarto", "#55A8DB", "#FFFFFF", 2),
        new ClubInfo("Gimnasia de La Plata", "GELP", "La Plata", "gimnasia-la-plata", "#1A2B58", "#FFFFFF", 3),
        new ClubInfo("Gimnasia de Mendoza", "GDM", "Mendoza", "gimnasia-mendoza", "#172D59", "#FFFFFF", 2),
        new ClubInfo("Huracán", "HUR", "Parque Patricios", "huracan", "#F4F4F4", "#D52732", 3),
        new ClubInfo("Independiente", "IND", "Avellaneda", "independiente", "#D51E2B", "#FFFFFF", 4),
        new ClubInfo("Independiente Rivadavia", "IR", "Mendoza", "independiente-rivadavia", "#1479C9", "#FFFFFF", 3),
        new ClubInfo("Instituto", "INS", "Córdoba", "instituto", "#C92532", "#FFFFFF", 3),
        new ClubInfo("Lanús", "LAN", "Lanús", "lanus", "#8F1630", "#FFFFFF", 3),
        new ClubInfo("Newell's Old Boys", "NOB", "Rosario", "newells-old-boys", "#D52732", "#111111", 3),
        new ClubInfo("Platense", "PLA", "Vicente López", "platense", "#6B263A", "#FFFFFF", 2),
        new ClubInfo("Racing Club", "RAC", "Avellaneda", "racing-club", "#75BCE8", "#FFFFFF", 4),
        new ClubInfo("River Plate", "RIV", "Núñez", "river-plate", "#FFFFFF", "#D42132", 5),
        new ClubInfo("Rosario Central", "ROS", "Rosario", "rosario-central", "#53AEE1", "#F2C600", 4),
        new ClubInfo("San Lorenzo", "SLO", "Boedo", "san-lorenzo", "#12396D", "#D52A36", 4),
        new ClubInfo("Sarmiento", "SAR", "Junín", "sarmiento", "#1B8A4A", "#FFFFFF", 2),
        new ClubInfo("Talleres", "TAL", "Córdoba", "talleres", "#16366F", "#FFFFFF", 4),
        new ClubInfo("Tigre", "TIG", "Victoria", "tigre", "#17427F", "#D32937", 3),
        new ClubInfo("Unión", "UNI", "Santa Fe", "union", "#D52A36", "#FFFFFF", 3),
        new ClubInfo("Vélez Sarsfield", "VEL", "Liniers", "velez-sarsfield", "#153B80", "#FFFFFF", 4)
    );

    public List<ClubInfo> listarTodos() {
        List<ClubInfo> copia = new ArrayList<>(clubes);
        copia.sort(Comparator.comparing(ClubInfo::getNombre));
        return copia;
    }
    public Optional<ClubInfo> buscarPorNombre(String nombre) {
        if (nombre == null) return Optional.empty();
        return clubes.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre.trim())).findFirst();
    }
    public int cantidadClubes() { return clubes.size(); }
}
