package dao.impl;
import dao.JugadorDAO;
import model.Jugador;
import model.Arquero;
import model.Defensa;
import model.Mediocampista;
import model.Delantero;
import util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JugadorDaoImpl implements JugadorDAO{

    @Override
    public void insertar(Jugador jugador) {

        String sql = "INSERT INTO jugadores ("
                + "nombre, edad, ovr, precio, resistencia, equipo, "
                + "dorsal, posicion, velocidad, remate, fuerza, pase, "
                + "regate, centros, marcaje, definicion, control, entradas, "
                + "tipo, cabezazo, vision, recuperacion, reflejos, "
                + "atajada, salida, juego_con_los_pies"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setInt(2, jugador.getEdad());
            ps.setInt(3, jugador.getOvr());
            ps.setDouble(4, jugador.getPrecio());
            ps.setInt(5, jugador.getResistencia());
            ps.setString(6, jugador.getEquipo());
            ps.setInt(7, jugador.getDorsal());
            ps.setString(8, jugador.getPosicion());

            ps.setInt(9, jugador.getVelocidad());
            ps.setInt(10, jugador.getRemate());
            ps.setInt(11, jugador.getFuerza());
            ps.setInt(12, jugador.getPase());
            ps.setInt(13, jugador.getRegate());
            ps.setInt(14, jugador.getCentros());
            ps.setInt(15, jugador.getMarcaje());
            ps.setInt(16, jugador.getDefinicion());
            ps.setInt(17, jugador.getControl());
            ps.setInt(18, jugador.getEntradas());

            // Valores específicos: por defecto, quedan en NULL.
            ps.setString(19, null);
            ps.setObject(20, null);
            ps.setObject(21, null);
            ps.setObject(22, null);
            ps.setObject(23, null);
            ps.setObject(24, null);
            ps.setObject(25, null);
            ps.setObject(26, null);

            if (jugador instanceof Arquero) {
                Arquero arquero = (Arquero) jugador;

                ps.setString(19, "ARQUERO");
                ps.setObject(23, arquero.getReflejos());
                ps.setObject(24, arquero.getAtajadas());
                ps.setObject(25, arquero.getSalida());
                ps.setObject(26, arquero.getJuegoConPies());

            } else if (jugador instanceof Defensa) {
                Defensa defensa = (Defensa) jugador;

                ps.setString(19, "DEFENSA");
                ps.setObject(20, defensa.getCabezazo());

            } else if (jugador instanceof Mediocampista) {
                Mediocampista mediocampista = (Mediocampista) jugador;

                ps.setString(19, "MEDIOCAMPISTA");
                ps.setObject(21, mediocampista.getVision());
                ps.setObject(22, mediocampista.getRecuperacion());

            } else if (jugador instanceof Delantero) {
                Delantero delantero = (Delantero) jugador;

                ps.setString(19, "DELANTERO");
                ps.setObject(20, delantero.getCabezazo());

            } else {
                throw new IllegalArgumentException("Tipo de jugador no reconocido");
            }

            ps.executeUpdate();

            System.out.println("Jugador guardado correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al guardar el jugador.");
            e.printStackTrace();
        }
    }
}