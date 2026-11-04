package dao.impl;

import dao.JugadorDAO;
import model.Arquero;
import model.Defensa;
import model.Delantero;
import model.Jugador;
import model.Mediocampista;
import util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class JugadorDaoImpl implements JugadorDAO {
    private static final String COLUMNAS = "nombre, edad, ovr, precio, resistencia, equipo, "
            + "dorsal, posicion, velocidad, remate, fuerza, pase, regate, centros, "
            + "marcaje, definicion, control, entradas, tipo, cabezazo, vision, "
            + "recuperacion, reflejos, atajada, salida, juego_con_los_pies";

    private static final String SIGNOS = "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

    @Override
    public int insertar(Jugador jugador) throws SQLException {
        String sql = "INSERT INTO jugadores (" + COLUMNAS + ") VALUES (" + SIGNOS + ")";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, jugador);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    int idGenerado = claves.getInt(1);
                    jugador.setId(idGenerado);
                    return idGenerado;
                }
            }
            return jugador.getId();
        }
    }

    @Override
    public Jugador buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM jugadores WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearJugador(rs) : null;
            }
        }
    }

    @Override
    public List<Jugador> listar() throws SQLException {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugadores ORDER BY id";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jugadores.add(mapearJugador(rs));
            }
        }
        return jugadores;
    }

    @Override
    public void actualizar(Jugador jugador) throws SQLException {
        String sql = "UPDATE jugadores SET nombre=?, edad=?, ovr=?, precio=?, resistencia=?, equipo=?, "
                + "dorsal=?, posicion=?, velocidad=?, remate=?, fuerza=?, pase=?, regate=?, centros=?, "
                + "marcaje=?, definicion=?, control=?, entradas=?, tipo=?, cabezazo=?, vision=?, "
                + "recuperacion=?, reflejos=?, atajada=?, salida=?, juego_con_los_pies=? WHERE id=?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, jugador);
            ps.setInt(27, jugador.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un jugador con ID " + jugador.getId() + ".");
            }
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM jugadores WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un jugador con ID " + id + ".");
            }
        }
    }

    private void cargarParametros(PreparedStatement ps, Jugador jugador) throws SQLException {
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

        String tipo;
        if (jugador instanceof Arquero) tipo = "ARQUERO";
        else if (jugador instanceof Defensa) tipo = "DEFENSA";
        else if (jugador instanceof Mediocampista) tipo = "MEDIOCAMPISTA";
        else if (jugador instanceof Delantero) tipo = "DELANTERO";
        else throw new SQLException("El tipo de jugador no está reconocido.");
        ps.setString(19, tipo);

        for (int indice = 20; indice <= 26; indice++) {
            ps.setNull(indice, Types.INTEGER);
        }
        if (jugador instanceof Arquero arquero) {
            ps.setInt(23, arquero.getReflejos());
            ps.setInt(24, arquero.getAtajadas());
            ps.setInt(25, arquero.getSalida());
            ps.setInt(26, arquero.getJuegoConPies());
        } else if (jugador instanceof Defensa defensa) {
            ps.setInt(20, defensa.getCabezazo());
        } else if (jugador instanceof Mediocampista mediocampista) {
            ps.setInt(21, mediocampista.getVision());
            ps.setInt(22, mediocampista.getRecuperacion());
        } else if (jugador instanceof Delantero delantero) {
            ps.setInt(20, delantero.getCabezazo());
        }
    }

    private Jugador mapearJugador(ResultSet rs) throws SQLException {
        String nombre = rs.getString("nombre");
        int edad = rs.getInt("edad");
        int ovr = rs.getInt("ovr");
        double precio = rs.getDouble("precio");
        int resistencia = rs.getInt("resistencia");
        String equipo = rs.getString("equipo");
        int dorsal = rs.getInt("dorsal");
        String posicion = rs.getString("posicion");
        int velocidad = rs.getInt("velocidad");
        int remate = rs.getInt("remate");
        int fuerza = rs.getInt("fuerza");
        int pase = rs.getInt("pase");
        int regate = rs.getInt("regate");
        int centros = rs.getInt("centros");
        int marcaje = rs.getInt("marcaje");
        int definicion = rs.getInt("definicion");
        int control = rs.getInt("control");
        int entradas = rs.getInt("entradas");

        Jugador jugador;
        String tipo = rs.getString("tipo");
        switch (tipo == null ? "" : tipo.toUpperCase()) {
            case "ARQUERO" -> jugador = new Arquero(nombre, edad, ovr, precio, resistencia,
                    equipo, dorsal, posicion, velocidad, remate, fuerza, pase, regate,
                    centros, marcaje, definicion, control, entradas,
                    rs.getInt("reflejos"), rs.getInt("atajada"), rs.getInt("salida"), rs.getInt("juego_con_los_pies"));
            case "DEFENSA" -> jugador = new Defensa(nombre, edad, ovr, precio, resistencia,
                    equipo, dorsal, posicion, velocidad, remate, fuerza, pase, regate,
                    centros, marcaje, definicion, control, entradas, rs.getInt("cabezazo"));
            case "MEDIOCAMPISTA" -> jugador = new Mediocampista(nombre, edad, ovr, precio, resistencia,
                    equipo, dorsal, posicion, velocidad, remate, fuerza, pase, regate,
                    centros, marcaje, definicion, control, entradas,
                    rs.getInt("vision"), rs.getInt("recuperacion"));
            case "DELANTERO" -> jugador = new Delantero(nombre, edad, ovr, precio, resistencia,
                    equipo, dorsal, posicion, velocidad, remate, fuerza, pase, regate,
                    centros, marcaje, definicion, control, entradas, rs.getInt("cabezazo"));
            default -> throw new SQLException("Tipo de jugador desconocido en la base de datos: " + tipo);
        }
        jugador.setId(rs.getInt("id"));
        return jugador;
    }
}
