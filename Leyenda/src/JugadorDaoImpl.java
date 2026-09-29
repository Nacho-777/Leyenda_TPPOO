package model;

import model.JugadorDAO;
import model.Jugador;
import model.Arquero;
import model.Defensa;
import model.Mediocampista;
import model.Delantero;
import util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class JugadorDaoImpl implements model.JugadorDAO {

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

            // DATOS GENERALES
            ps.setString(1, jugador.getNombre());
            ps.setInt(2, jugador.getEdad());
            ps.setInt(3, jugador.getOvr());
            ps.setDouble(4, jugador.getPrecio());
            ps.setInt(5, jugador.getResistencia());
            ps.setString(6, jugador.getEquipo());
            ps.setInt(7, jugador.getDorsal());
            ps.setString(8, jugador.getPosicion());

            // ESTADÍSTICAS GENERALES
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

            // Por defecto, atributos específicos = NULL
            ps.setNull(19, Types.VARCHAR);
            ps.setNull(20, Types.INTEGER);
            ps.setNull(21, Types.INTEGER);
            ps.setNull(22, Types.INTEGER);
            ps.setNull(23, Types.INTEGER);
            ps.setNull(24, Types.INTEGER);
            ps.setNull(25, Types.INTEGER);
            ps.setNull(26, Types.INTEGER);

            // ARQUERO
            if (jugador instanceof Arquero) {

                Arquero arquero = (Arquero) jugador;

                ps.setString(19, "ARQUERO");
                ps.setInt(23, arquero.getReflejos());
                ps.setInt(24, arquero.getAtajadas());
                ps.setInt(25, arquero.getSalida());
                ps.setInt(26, arquero.getJuegoConPies());

                // DEFENSA
            } else if (jugador instanceof Defensa) {

                Defensa defensa = (Defensa) jugador;

                ps.setString(19, "DEFENSA");
                ps.setInt(20, defensa.getCabezazo());

                // MEDIOCAMPISTA
            } else if (jugador instanceof Mediocampista) {

                Mediocampista mediocampista = (Mediocampista) jugador;

                ps.setString(19, "MEDIOCAMPISTA");
                ps.setInt(21, mediocampista.getVision());
                ps.setInt(22, mediocampista.getRecuperacion());

                // DELANTERO
            } else if (jugador instanceof Delantero) {

                Delantero delantero = (Delantero) jugador;

                ps.setString(19, "DELANTERO");
                ps.setInt(20, delantero.getCabezazo());

            } else {
                throw new IllegalArgumentException(
                        "Tipo de jugador no reconocido."
                );
            }

            ps.executeUpdate();

            System.out.println("Jugador guardado correctamente en MySQL.");

        } catch (SQLException e) {
            System.out.println("Error al guardar el jugador.");
            e.printStackTrace();
        }
    }
}