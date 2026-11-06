package service;

import dao.JugadorDAO;
import dao.impl.JugadorDaoImpl;
import exceptions.DorsalInvalidoException;
import exceptions.EdadInvalidaException;
import exceptions.OvrInvalidoException;
import exceptions.PrecioInvalidoException;
import exceptions.ResistenciaInvalidaException;
import model.Arquero;
import model.Defensa;
import model.Delantero;
import model.Entrenable;
import model.Jugador;
import model.Mediocampista;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class JugadorServiceImpl implements JugadorService {
    private final JugadorDAO jugadorDAO;
    private final EstadisticasService estadisticasService;

    public JugadorServiceImpl() {
        this(new JugadorDaoImpl());
    }

    public JugadorServiceImpl(JugadorDAO jugadorDAO) {
        this.jugadorDAO = jugadorDAO;
        this.estadisticasService = new EstadisticasService();
    }

    @Override
    public int crearJugador(Jugador jugador) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException {
        validarJugador(jugador);
        int id = jugadorDAO.insertar(jugador);
        System.out.println("Jugador guardado correctamente. ID: " + id);
        return id;
    }

    @Override
    public Jugador buscarJugador(int id) throws SQLException {
        return jugadorDAO.buscarPorId(id);
    }

    @Override
    public List<Jugador> listarJugadores() throws SQLException {
        return jugadorDAO.listar();
    }

    @Override
    public void actualizarJugador(Jugador jugador) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException {
        if (jugador == null || jugador.getId() <= 0) {
            throw new IllegalArgumentException("Para actualizar se necesita un jugador con ID válido.");
        }
        validarJugador(jugador);
        jugadorDAO.actualizar(jugador);
    }

    @Override
    public void eliminarJugador(int id) throws SQLException {
        jugadorDAO.eliminar(id);
    }

    @Override
    public void entrenarJugador(Entrenable entrenable) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException {
        if (entrenable == null) {
            throw new IllegalArgumentException("No se puede entrenar un objeto null.");
        }
        // Polimorfismo: el entrenamiento concreto depende de si es Arquero, Defensa, etc.
        entrenable.entrenar();
        if (entrenable instanceof Jugador jugador && jugador.getId() > 0) {
            actualizarJugador(jugador);
        }
    }

    /** Devuelve cinco planes de mejora específicos para la posición del futbolista. */
    @Override
    public List<String> obtenerEnfoquesEntrenamiento(Jugador jugador) {
        if (jugador instanceof Arquero) {
            return List.of("Reflejos", "Atajadas", "Salidas", "Juego con los pies", "Resistencia");
        }
        if (jugador instanceof Defensa) {
            return List.of("Marcaje", "Entradas", "Cabezazo", "Fuerza", "Velocidad");
        }
        if (jugador instanceof Mediocampista) {
            return List.of("Pase", "Visión", "Control", "Recuperación", "Resistencia");
        }
        if (jugador instanceof Delantero) {
            return List.of("Remate", "Definición", "Regate", "Velocidad", "Cabezazo");
        }
        return List.of();
    }

    /**
     * Entrenamiento especializado: cada posición puede elegir cinco atributos principales.
     * Cada sesión suma +1 OVR hasta los 29 años; después siguen mejorando los atributos,
     * pero el OVR queda sujeto al declive normal por edad.
     */
    public String entrenarConEnfoque(Jugador jugador, String enfoque) throws Exception {
        if (jugador == null) throw new IllegalArgumentException("No hay un jugador activo.");
        String opcion = enfoque == null ? "" : enfoque.trim().toLowerCase(Locale.ROOT);
        boolean aplicado = true;

        if (jugador instanceof Arquero arquero) {
            switch (opcion) {
                case "reflejos" -> arquero.setReflejos(mejorar(arquero.getReflejos(), 3));
                case "atajadas" -> arquero.setAtajadas(mejorar(arquero.getAtajadas(), 3));
                case "salidas" -> arquero.setSalida(mejorar(arquero.getSalida(), 3));
                case "juego con los pies" -> {
                    arquero.setJuegoConPies(mejorar(arquero.getJuegoConPies(), 3));
                    jugador.setPase(mejorar(jugador.getPase(), 1));
                }
                case "resistencia" -> {
                    jugador.setResistencia(mejorar(jugador.getResistencia(), 3));
                    jugador.setFuerza(mejorar(jugador.getFuerza(), 1));
                }
                default -> aplicado = false;
            }
        } else if (jugador instanceof Defensa defensa) {
            switch (opcion) {
                case "marcaje" -> {
                    jugador.setMarcaje(mejorar(jugador.getMarcaje(), 3));
                    jugador.setEntradas(mejorar(jugador.getEntradas(), 1));
                }
                case "entradas" -> {
                    jugador.setEntradas(mejorar(jugador.getEntradas(), 3));
                    jugador.setMarcaje(mejorar(jugador.getMarcaje(), 1));
                }
                case "cabezazo" -> {
                    defensa.setCabezazo(mejorar(defensa.getCabezazo(), 3));
                    jugador.setFuerza(mejorar(jugador.getFuerza(), 1));
                }
                case "fuerza" -> {
                    jugador.setFuerza(mejorar(jugador.getFuerza(), 3));
                    jugador.setResistencia(mejorar(jugador.getResistencia(), 1));
                }
                case "velocidad" -> {
                    jugador.setVelocidad(mejorar(jugador.getVelocidad(), 3));
                    jugador.setResistencia(mejorar(jugador.getResistencia(), 1));
                }
                default -> aplicado = false;
            }
        } else if (jugador instanceof Mediocampista medio) {
            switch (opcion) {
                case "pase" -> {
                    jugador.setPase(mejorar(jugador.getPase(), 3));
                    jugador.setControl(mejorar(jugador.getControl(), 1));
                }
                case "visión" -> {
                    medio.setVision(mejorar(medio.getVision(), 3));
                    jugador.setPase(mejorar(jugador.getPase(), 1));
                }
                case "control" -> {
                    jugador.setControl(mejorar(jugador.getControl(), 3));
                    jugador.setRegate(mejorar(jugador.getRegate(), 1));
                }
                case "recuperación" -> {
                    medio.setRecuperacion(mejorar(medio.getRecuperacion(), 3));
                    jugador.setMarcaje(mejorar(jugador.getMarcaje(), 1));
                }
                case "resistencia" -> {
                    jugador.setResistencia(mejorar(jugador.getResistencia(), 3));
                    jugador.setFuerza(mejorar(jugador.getFuerza(), 1));
                }
                default -> aplicado = false;
            }
        } else if (jugador instanceof Delantero delantero) {
            switch (opcion) {
                case "remate" -> {
                    jugador.setRemate(mejorar(jugador.getRemate(), 3));
                    jugador.setDefinicion(mejorar(jugador.getDefinicion(), 1));
                }
                case "definición" -> {
                    jugador.setDefinicion(mejorar(jugador.getDefinicion(), 3));
                    jugador.setRemate(mejorar(jugador.getRemate(), 1));
                }
                case "regate" -> {
                    jugador.setRegate(mejorar(jugador.getRegate(), 3));
                    jugador.setControl(mejorar(jugador.getControl(), 1));
                }
                case "velocidad" -> {
                    jugador.setVelocidad(mejorar(jugador.getVelocidad(), 3));
                    jugador.setRegate(mejorar(jugador.getRegate(), 1));
                }
                case "cabezazo" -> {
                    delantero.setCabezazo(mejorar(delantero.getCabezazo(), 3));
                    jugador.setFuerza(mejorar(jugador.getFuerza(), 1));
                }
                default -> aplicado = false;
            }
        } else {
            aplicado = false;
        }

        if (!aplicado) {
            throw new IllegalArgumentException("Ese entrenamiento no corresponde a tu posición. Elegí una de las cinco opciones disponibles.");
        }

        String mensaje;
        if (jugador.getEdad() < 30) {
            int anterior = jugador.getOvr();
            recalcularOvrSegunAtributos(jugador);
            mensaje = "Entrenamiento de " + enfoque + ": mejoraron tus atributos principales. OVR "
                    + anterior + " → " + jugador.getOvr() + " según tus estadísticas reales.";
        } else {
            mensaje = "Entrenamiento de " + enfoque + ": mejoraron tus atributos, pero por edad el OVR no puede subir.";
        }
        if (jugador.getId() > 0) actualizarJugador(jugador);
        return mensaje;
    }

    private int mejorar(int actual, int puntos) {
        return Math.min(99, actual + puntos);
    }

    /** Premia temporadas sobresalientes: títulos, premios y producción ofensiva pueden impulsar el OVR. */
    public String aplicarProgresoPorRendimiento(Jugador jugador, ResultadoTemporada resultado) throws Exception {
        if (jugador == null || resultado == null || jugador.getEdad() >= 30) {
            return "";
        }
        int bonus = 0;
        String motivo = "";
        int produccion = resultado.getGoles() + resultado.getAsistencias();
        boolean campeonCompeticion = resultado.getCompeticiones().stream().anyMatch(model.ResultadoCompeticion::isCampeon);
        if (resultado.isBalonOro()) {
            bonus = 3;
            motivo = "Balón de Oro: +3 OVR.";
        } else if (resultado.isCampeon() || campeonCompeticion || produccion >= 18) {
            bonus = 2;
            motivo = (resultado.isCampeon() || campeonCompeticion) ? "Temporada con título: +2 OVR." : "Rendimiento destacado: +2 OVR.";
        } else if (resultado.getPartidos() >= 43) {
            bonus = 1;
            motivo = "Regularidad durante toda la temporada: +1 OVR.";
        }
        if (bonus == 0 || jugador.getOvr() >= 99) return "";
        int aplicado = Math.min(bonus, 3);
        aplicarBonusPorPosicion(jugador, aplicado);
        recalcularOvrSegunAtributos(jugador);
        if (jugador.getId() > 0) actualizarJugador(jugador);
        return motivo;
    }

    /** OVR calculado con los atributos importantes de cada posición; evita valoraciones infladas. */
    private void recalcularOvrSegunAtributos(Jugador j) {
        double score;
        if (j instanceof Arquero a) {
            score = a.getReflejos() * .30 + a.getAtajadas() * .30 + a.getSalida() * .18
                    + a.getJuegoConPies() * .10 + j.getResistencia() * .07 + j.getPase() * .05;
        } else if (j instanceof Defensa d) {
            score = j.getMarcaje() * .20 + j.getEntradas() * .20 + d.getCabezazo() * .12
                    + j.getFuerza() * .13 + j.getVelocidad() * .10 + j.getPase() * .08
                    + j.getControl() * .07 + j.getResistencia() * .10;
        } else if (j instanceof Delantero d) {
            score = j.getRemate() * .22 + j.getDefinicion() * .20 + d.getCabezazo() * .10
                    + j.getVelocidad() * .14 + j.getRegate() * .14 + j.getControl() * .08
                    + j.getFuerza() * .07 + j.getResistencia() * .05;
        } else if (j instanceof Mediocampista m) {
            score = j.getPase() * .20 + m.getVision() * .18 + j.getControl() * .16
                    + j.getRegate() * .12 + m.getRecuperacion() * .12 + j.getResistencia() * .08
                    + j.getMarcaje() * .07 + j.getVelocidad() * .07;
        } else {
            score = (j.getVelocidad() + j.getRemate() + j.getPase() + j.getRegate()
                    + j.getFuerza() + j.getControl()) / 6.0;
        }
        j.setOvr(Math.max(1, Math.min(99, (int)Math.round(score))));
        j.actualizarPrecioSegunOVR();
    }

    private void aplicarBonusPorPosicion(Jugador j, int puntos) {
        if (j instanceof Arquero a) {
            a.setReflejos(mejorar(a.getReflejos(), puntos));
            a.setAtajadas(mejorar(a.getAtajadas(), puntos));
        } else if (j instanceof Defensa d) {
            j.setMarcaje(mejorar(j.getMarcaje(), puntos));
            j.setEntradas(mejorar(j.getEntradas(), puntos));
            d.setCabezazo(mejorar(d.getCabezazo(), puntos));
        } else if (j instanceof Delantero d) {
            j.setRemate(mejorar(j.getRemate(), puntos));
            j.setDefinicion(mejorar(j.getDefinicion(), puntos));
            d.setCabezazo(mejorar(d.getCabezazo(), Math.max(1, puntos - 1)));
        } else if (j instanceof Mediocampista m) {
            j.setPase(mejorar(j.getPase(), puntos));
            m.setVision(mejorar(m.getVision(), puntos));
            j.setControl(mejorar(j.getControl(), Math.max(1, puntos - 1)));
        }
    }

    /** Aplica envejecimiento anual. Desde los 30 el OVR nunca aumenta y cae según edad y rendimiento. */
    public String aplicarDeclivePorTemporada(Jugador jugador, ResultadoTemporada resultado) throws Exception {
        if (jugador == null || resultado == null) return "";
        int edad = jugador.getEdad();
        if (edad < 30) return "Etapa de desarrollo: todavía podés seguir creciendo.";
        int participacion = resultado.getPartidos();
        int produccion = resultado.getGoles() + resultado.getAsistencias();
        boolean arquero = jugador.getPosicion().toLowerCase(Locale.ROOT).contains("arquero");
        boolean granTemporada = participacion >= 38 && (arquero || produccion >= 12);
        boolean temporadaBaja = participacion < 25 || (!arquero && produccion <= 3);
        int perdida = edad >= 36 ? 2 : 1;
        if (temporadaBaja) perdida++;
        // Incluso una gran temporada ya no permite subir el OVR: como máximo amortigua el desgaste físico.
        if (granTemporada && edad <= 32) perdida = 1;
        if (edad >= 39) perdida = Math.max(1, perdida);
        jugador.setOvr(Math.max(1, jugador.getOvr() - perdida));
        // El desgaste físico se refleja también en atributos que dependen de explosividad y recuperación.
        if (edad >= 32 && !granTemporada) {
            jugador.setVelocidad(Math.max(1, jugador.getVelocidad() - 1));
            jugador.setResistencia(Math.max(1, jugador.getResistencia() - 1));
        }
        if (jugador.getId() > 0) actualizarJugador(jugador);
        if (perdida == 0) return "Temporada sobresaliente: mantuviste tu OVR pese al paso de los años.";
        return "Declive por edad (" + edad + " años): -" + perdida + " OVR" + (temporadaBaja ? " por bajo rendimiento." : ".");
    }

    @Override
    public void tomarDecision(int id, String decision) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException {
        Jugador jugador = exigirJugador(id);
        String opcion = decision == null ? "" : decision.trim().toLowerCase(Locale.ROOT);
        if (opcion.equals("entrenar")) {
            entrenarJugador(jugador);
        } else if (opcion.equals("descansar")) {
            System.out.println("El jugador descansó. No se modificaron sus atributos.");
        } else {
            throw new IllegalArgumentException("La decisión debe ser 'entrenar' o 'descansar'.");
        }
    }

    @Override
    public void subirOVR(int id, int puntos) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException {
        Jugador jugador = exigirJugador(id);
        if (puntos > 0 && jugador.getEdad() >= 30) {
            throw new OvrInvalidoException("A partir de los 30 años el OVR ya no puede aumentar.");
        }
        int nuevoOvr = jugador.getOvr() + puntos;
        if (nuevoOvr < 1 || nuevoOvr > 99) {
            throw new OvrInvalidoException("El OVR resultante debe permanecer entre 1 y 99.");
        }
        jugador.setOvr(nuevoOvr);
        actualizarJugador(jugador);
    }

    @Override
    public void retirarJugador(int id) throws SQLException {
        eliminarJugador(id);
    }

    @Override
    public void simularTemporada(Jugador jugador, String clubActual, int anio, Random random) {
        estadisticasService.simularTemporada(jugador, clubActual, anio, random);
    }

    @Override
    public ResultadoTemporada simularTemporadaConResultado(Jugador jugador, String clubActual, int anio, Random random) {
        return estadisticasService.simularTemporadaConResultado(jugador, clubActual, anio, random);
    }

    @Override
    public void mostrarResumenCarrera(Jugador jugador) {
        estadisticasService.mostrarResumenCarrera(jugador);
    }

    private void validarJugador(Jugador jugador) throws EdadInvalidaException,
            OvrInvalidoException, PrecioInvalidoException, ResistenciaInvalidaException,
            DorsalInvalidoException, SQLException {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null.");
        }
        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            throw new IllegalArgumentException("El jugador debe tener nombre.");
        }
        if (jugador.getPosicion() == null || jugador.getPosicion().isBlank()) {
            throw new IllegalArgumentException("El jugador debe tener posición.");
        }
        if (jugador.getEdad() < 16 || jugador.getEdad() > 40) {
            throw new EdadInvalidaException("La edad debe estar entre 16 y 40.");
        }
        if (jugador.getOvr() < 1 || jugador.getOvr() > 99) {
            throw new OvrInvalidoException("El OVR debe estar entre 1 y 99.");
        }
        if (jugador.getPrecio() < 0) {
            throw new PrecioInvalidoException("El precio no puede ser negativo.");
        }
        if (jugador.getResistencia() < 1 || jugador.getResistencia() > 99) {
            throw new ResistenciaInvalidaException("La resistencia debe estar entre 1 y 99.");
        }
        if (jugador.getDorsal() < 1 || jugador.getDorsal() > 99) {
            throw new DorsalInvalidoException("El dorsal debe estar entre 1 y 99.");
        }
        if (!(jugador instanceof Arquero || jugador instanceof Defensa
                || jugador instanceof Mediocampista || jugador instanceof Delantero)) {
            throw new IllegalArgumentException("El tipo de jugador no está soportado.");
        }
    }

    private Jugador exigirJugador(int id) throws SQLException {
        Jugador jugador = jugadorDAO.buscarPorId(id);
        if (jugador == null) {
            throw new SQLException("No existe un jugador con ID " + id + ".");
        }
        return jugador;
    }
}
