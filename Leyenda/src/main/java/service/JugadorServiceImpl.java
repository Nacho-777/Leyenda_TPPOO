package service;

import dao.JugadorDAO;
import dao.impl.JugadorDaoImpl;
import exceptions.DorsalOcupadoException;
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
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalOcupadoException, SQLException {
        validarJugador(jugador, 0);
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
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalOcupadoException, SQLException {
        if (jugador == null || jugador.getId() <= 0) {
            throw new IllegalArgumentException("Para actualizar se necesita un jugador con ID válido.");
        }
        validarJugador(jugador, jugador.getId());
        jugadorDAO.actualizar(jugador);
    }

    @Override
    public void eliminarJugador(int id) throws SQLException {
        jugadorDAO.eliminar(id);
    }

    @Override
    public void entrenarJugador(Entrenable entrenable) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalOcupadoException, SQLException {
        if (entrenable == null) {
            throw new IllegalArgumentException("No se puede entrenar un objeto null.");
        }
        // Polimorfismo: el entrenamiento concreto depende de si es Arquero, Defensa, etc.
        entrenable.entrenar();
        if (entrenable instanceof Jugador jugador && jugador.getId() > 0) {
            actualizarJugador(jugador);
        }
    }

    /** Entrenamiento elegido por el usuario. El OVR solo puede crecer antes de los 30 años. */
    public String entrenarConEnfoque(Jugador jugador, String enfoque) throws Exception {
        if (jugador == null) throw new IllegalArgumentException("No hay un jugador activo.");
        String opcion = enfoque == null ? "" : enfoque.trim().toLowerCase(Locale.ROOT);
        switch (opcion) {
            case "velocidad" -> jugador.setVelocidad(Math.min(99, jugador.getVelocidad() + 2));
            case "definición" -> {
                jugador.setRemate(Math.min(99, jugador.getRemate() + 1));
                jugador.setDefinicion(Math.min(99, jugador.getDefinicion() + 2));
            }
            case "pase y visión" -> {
                jugador.setPase(Math.min(99, jugador.getPase() + 2));
                jugador.setControl(Math.min(99, jugador.getControl() + 1));
                if (jugador instanceof Mediocampista m) m.setVision(Math.min(99, m.getVision() + 2));
            }
            case "regate y control" -> {
                jugador.setRegate(Math.min(99, jugador.getRegate() + 2));
                jugador.setControl(Math.min(99, jugador.getControl() + 1));
            }
            case "fuerza" -> jugador.setFuerza(Math.min(99, jugador.getFuerza() + 2));
            case "resistencia" -> jugador.setResistencia(Math.min(99, jugador.getResistencia() + 3));
            case "marcaje y entradas" -> {
                jugador.setMarcaje(Math.min(99, jugador.getMarcaje() + 2));
                jugador.setEntradas(Math.min(99, jugador.getEntradas() + 2));
            }
            case "atajadas" -> {
                if (jugador instanceof Arquero a) {
                    a.setReflejos(Math.min(99, a.getReflejos() + 2));
                    a.setAtajadas(Math.min(99, a.getAtajadas() + 2));
                } else {
                    jugador.setControl(Math.min(99, jugador.getControl() + 1));
                    jugador.setFuerza(Math.min(99, jugador.getFuerza() + 1));
                }
            }
            default -> throw new IllegalArgumentException("Elegí un enfoque de entrenamiento válido.");
        }
        String mensaje;
        if (jugador.getEdad() < 30 && jugador.getOvr() < 99) {
            jugador.setOvr(Math.min(99, jugador.getOvr() + 1));
            mensaje = "Entrenamiento de " + enfoque + ": mejoraron tus atributos y ganaste +1 OVR.";
        } else {
            mensaje = "Entrenamiento de " + enfoque + ": mejoraron tus atributos. A partir de los 30, el OVR ya no aumenta.";
        }
        if (jugador.getId() > 0) actualizarJugador(jugador);
        return mensaje;
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
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalOcupadoException, SQLException {
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
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalOcupadoException, SQLException {
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

    private void validarJugador(Jugador jugador, int idExcluir) throws EdadInvalidaException,
            OvrInvalidoException, PrecioInvalidoException, ResistenciaInvalidaException,
            DorsalOcupadoException, SQLException {
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
            throw new DorsalOcupadoException("El dorsal debe estar entre 1 y 99.");
        }
        if (jugadorDAO.existeDorsal(jugador.getDorsal(), idExcluir)) {
            throw new DorsalOcupadoException("El dorsal " + jugador.getDorsal() + " ya está ocupado.");
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
