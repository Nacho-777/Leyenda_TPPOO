package service;

import exceptions.DorsalInvalidoException;
import exceptions.EdadInvalidaException;
import exceptions.OvrInvalidoException;
import exceptions.PrecioInvalidoException;
import exceptions.ResistenciaInvalidaException;
import model.Entrenable;
import model.Jugador;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public interface JugadorService {
    int crearJugador(Jugador jugador) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException;
    Jugador buscarJugador(int id) throws SQLException;
    List<Jugador> listarJugadores() throws SQLException;
    void actualizarJugador(Jugador jugador) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException;
    void eliminarJugador(int id) throws SQLException;
    List<String> obtenerEnfoquesEntrenamiento(Jugador jugador);
    void entrenarJugador(Entrenable jugador) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException;
    void tomarDecision(int id, String decision) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException;
    void subirOVR(int id, int puntos) throws EdadInvalidaException, OvrInvalidoException,
            PrecioInvalidoException, ResistenciaInvalidaException, DorsalInvalidoException, SQLException;
    void retirarJugador(int id) throws SQLException;
    void simularTemporada(Jugador jugador, String clubActual, int anio, Random random);
    ResultadoTemporada simularTemporadaConResultado(Jugador jugador, String clubActual, int anio, Random random);
    void mostrarResumenCarrera(Jugador jugador);
}
