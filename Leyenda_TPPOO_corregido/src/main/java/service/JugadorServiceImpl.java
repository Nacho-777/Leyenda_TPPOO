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
