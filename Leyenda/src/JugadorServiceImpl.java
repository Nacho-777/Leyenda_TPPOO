///
package model;

import model.JugadorDAO;
import model.JugadorDaoImpl;
import model.Jugador;

public class JugadorServiceImpl implements service.JugadorService {

    private model.JugadorDAO jugadorDAO;

    public JugadorServiceImpl() {
        jugadorDAO = new model.JugadorDaoImpl();
    }

    @Override
    public void crearJugador(Jugador jugador) {

        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null.");
        }

        if (jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            throw new IllegalArgumentException("El jugador debe tener un nombre.");
        }

        if (jugador.getPosicion() == null || jugador.getPosicion().isBlank()) {
            throw new IllegalArgumentException("El jugador debe tener una posición.");
        }

        jugadorDAO.insertar(jugador);

        System.out.println("Jugador creado y guardado en la base de datos.");
    }
}