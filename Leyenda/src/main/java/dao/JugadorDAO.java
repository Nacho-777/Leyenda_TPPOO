package dao;

import model.Jugador;

import java.sql.SQLException;
import java.util.List;

public interface JugadorDAO {
    int insertar(Jugador jugador) throws SQLException;
    Jugador buscarPorId(int id) throws SQLException;
    List<Jugador> listar() throws SQLException;
    void actualizar(Jugador jugador) throws SQLException;
    void eliminar(int id) throws SQLException;
}
