package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import util.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

import service.JugadorService;
import model.JugadorServiceImpl;

public class Main {

        static class Equipo {
            String nombre;
            String pais;
            int tier;

            public Equipo(String nombre, String pais, int tier) {
                this.nombre = nombre;
                this.pais = pais;
                this.tier = tier;
            }
        }

        public static void main (String[]args){
            //conexionn
            try (Connection con = Conexion.obtenerConexion()) {
                System.out.println("¡Conexión exitosa a la base de datos!");

            } catch (SQLException e) {
                System.out.println("Error al conectar con la base de datos.");
                e.printStackTrace();
            }

            Scanner sc = new Scanner(System.in);
            Random random = new Random();

            // Servicio de estadísticas
            EstadisticasService estadisticasService = new EstadisticasService();

            List<Equipo> baseEquipos = cargarEquiposLocales();

            System.out.println("=================================================");
            System.out.println("      BIENVENIDO AL MODO CARRERA DE FÚTBOL      ");
            System.out.println("=================================================");

            System.out.println("\nSelecciona el nivel de dificultad:");
            System.out.println("1. Difícil (Avance temporada a temporada - 24 etapas)");
            System.out.println("2. Normal  (Avance cada 2 temporadas - 12 etapas)");
            System.out.println("3. Fácil   (Avance cada 4 temporadas - 6 etapas)");
            System.out.print("Opción: ");
            int opcDificultad = sc.nextInt();
            sc.nextLine();

            int saltoAños = (opcDificultad == 2) ? 2 : (opcDificultad == 3) ? 4 : 1;

            System.out.print("\nIngrese su Nombre y Apellido: ");
            String nombreJugador = sc.nextLine();

            System.out.print("Ingrese el dorsal deseado (1 al 99): ");
            int dorsalJugador = sc.nextInt();
            sc.nextLine();

            System.out.println("\nSeleccione la posición en la que desea jugar:");
            System.out.println("1. Arquero\n2. Lateral Derecho\n3. Central Derecho\n4. Central Izquierdo");
            System.out.println("5. Lateral Izquierdo\n6. Mediocampista\n7. Volante Derecho\n8. Volante Izquierdo");
            System.out.println("9. Extremo Derecho\n10. Delantero Centro\n11. Extremo Izquierdo");
            System.out.print("Opción: ");
            int opcPos = sc.nextInt();
            sc.nextLine();

            String nombrePosicion = obtenerNombrePosicion(opcPos);

            model.Jugador miJugador = null;

            switch (opcPos) {
                case 1:
                    miJugador = new Arquero(1L, nombreJugador, 16, 50, 0L, 50, 50, 50, 50);
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    miJugador = new Defensa(nombreJugador, 16, 50, 500000, 50, "Agente Libre", dorsalJugador, nombrePosicion, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50);
                    break;
                case 6:
                case 7:
                case 8:
                    miJugador = new Mediocampista(nombreJugador, 16, 50, 500000, 50, "Agente Libre", dorsalJugador, nombrePosicion, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50);
                    break;
                default:
                    miJugador = new Delantero(nombreJugador, 16, 50, 500000, 50, "Agente Libre", dorsalJugador, nombrePosicion, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50);
                    break;
            }
            miJugador.setNombre(nombreJugador);
            miJugador.setPosicion(nombrePosicion);
            miJugador.setDorsal(dorsalJugador);

            List<Equipo> clubesArgentinos = filtrarEquiposPorPais(baseEquipos, "Argentina");

            System.out.println("\n🇦🇷 Comienzas tu carrera a los 16 años en la Liga Argentina.");
            System.out.println("Elige tu club de inicio:");
            for (int i = 0; i < 5 && i < clubesArgentinos.size(); i++) {
                System.out.println((i + 1) + ". " + clubesArgentinos.get(i).nombre);
            }
            System.out.print("Opción: ");
            int opcClub = sc.nextInt();
            sc.nextLine();

            String clubActual = (opcClub >= 1 && opcClub <= 5) ? clubesArgentinos.get(opcClub - 1).nombre : clubesArgentinos.get(0).nombre;


            // Guardamos el club elegido en el jugador
            miJugador.setEquipo(clubActual);

            // Creamos el Service
            JugadorService jugadorService = new model.JugadorServiceImpl();

            // Guardamos el jugador en la base de datos
            jugadorService.crearJugador(miJugador);

            System.out.println("\n¡Carrera iniciada!");
            System.out.println("Jugador: " + miJugador.getNombre() + " | Posición: " + miJugador.getPosicion() + " | Club: " + clubActual + " | OVR Inicial: " + miJugador.getOvr());

            int edadActual = 16;
            int etapa = 1;

            while (edadActual < 40) {
                System.out.println("\n-------------------------------------------------");
                System.out.println(" ETAPA " + etapa + " - EDAD: " + edadActual + " AÑOS | OVR: " + miJugador.getOvr() + " | CLUB: " + clubActual);
                System.out.println("-------------------------------------------------");

                int evento = random.nextInt(3);
                if (evento == 0) {
                    int bajada = random.nextInt(2) + 1;
                    actualizarOvrJugador(miJugador, Math.max(50, miJugador.getOvr() - bajada));
                    System.out.println("⚠️ Evento: Lesión leve/bajón físico. Bajaste " + bajada + " pts de OVR.");
                } else if (evento == 1) {
                    int subida = random.nextInt(3) + 1;
                    actualizarOvrJugador(miJugador, Math.min(99, miJugador.getOvr() + subida));
                    System.out.println(" Evento: Racha goleadora / buen rendimiento. Subiste " + subida + " pts de OVR.");
                } else {
                    System.out.println("ℹEvento: Temporada regular sin sobresaltos.");
                }

                System.out.println("\n¿En qué deseas enfocar tu desarrollo esta etapa?");
                System.out.println("1. Entrenamiento intensivo");
                System.out.println("2. Descanso y recuperación");
                System.out.print("Opción: ");
                int decision = sc.nextInt();
                sc.nextLine();

                if (decision == 1) {
                    int incremento = random.nextInt(3) + 2;
                    actualizarOvrJugador(miJugador, Math.min(99, miJugador.getOvr() + incremento));
                    System.out.println(" ¡Entrenamiento completado! OVR Actual: " + miJugador.getOvr());
                } else {
                    System.out.println(" Decidiste priorizar la recuperación física.");
                }

                // SIMULACIÓN DE ESTADÍSTICAS POR TEMPORADA
                estadisticasService.simularTemporada(miJugador, clubActual, 2026 + (etapa - 1), random);

                if (etapa % 3 == 0) {
                    System.out.println("\n ¡Llegó el mercado de pases!");
                    List<Equipo> ofertas = obtenerOfertasPorOvr(baseEquipos, miJugador.getOvr(), clubActual, random);
                    System.out.println("1. Quedarte en " + clubActual);
                    for (int i = 0; i < ofertas.size(); i++) {
                        System.out.println((i + 2) + ". Ir a " + ofertas.get(i).nombre + " (" + ofertas.get(i).pais + ")");
                    }
                    System.out.print("Opción: ");
                    int opcionTraspaso = sc.nextInt();
                    sc.nextLine();

                    if (opcionTraspaso >= 2 && opcionTraspaso <= ofertas.size() + 1) {
                        clubActual = ofertas.get(opcionTraspaso - 2).nombre;
                        System.out.println("✍ ¡Firmaste contrato con " + clubActual + "!");
                    } else {
                        System.out.println("Te quedas en " + clubActual + ".");
                    }
                }

                edadActual += saltoAños;
                etapa++;
            }

            System.out.println("\n=================================================");
            System.out.println("         ¡CARRERA FINALIZADA A LOS 40 AÑOS!      ");
            System.out.println("=================================================");
            System.out.println("Jugador: " + miJugador.getNombre());
            System.out.println("Posición: " + miJugador.getPosicion());
            System.out.println("Último Club: " + clubActual);
            System.out.println("OVR Final: " + miJugador.getOvr() + " / 99");

            // RESUMEN GENERAL AL RETIRARSE
            estadisticasService.mostrarResumenCarrera(miJugador);
        }

    private static void actualizarOvrJugador(model.Jugador j, int nuevoOvr) {
        j.setOvr(nuevoOvr);
    }

    private static List<Equipo> obtenerOfertasPorOvr(List<Equipo> base, int ovr, String clubActual, Random random) {
        int minTier = 5, maxTier = 5;
        if (ovr >= 86) { minTier = 1; maxTier = 2; }
        else if (ovr >= 80) { minTier = 2; maxTier = 3; }
        else if (ovr >= 72) { minTier = 3; maxTier = 4; }
        else if (ovr >= 62) { minTier = 4; maxTier = 5; }

        List<Equipo> candidatos = new ArrayList<>();
        for (Equipo eq : base) {
            if (eq.tier >= minTier && eq.tier <= maxTier && !eq.nombre.equalsIgnoreCase(clubActual)) {
                candidatos.add(eq);
            }
        }

        List<Equipo> seleccionados = new ArrayList<>();
        while (seleccionados.size() < 3 && !candidatos.isEmpty()) {
            int idx = random.nextInt(candidatos.size());
            seleccionados.add(candidatos.remove(idx));
        }
        return seleccionados;
    }

    private static List<Equipo> filtrarEquiposPorPais(List<Equipo> base, String pais) {
        List<Equipo> res = new ArrayList<>();
        for (Equipo eq : base) {
            if (eq.pais.equalsIgnoreCase(pais)) res.add(eq);
        }
        return res;
    }

    private static String obtenerNombrePosicion(int opc) {
        return switch (opc) {
            case 1 -> "Arquero";
            case 2 -> "Lateral Derecho";
            case 3 -> "Central Derecho";
            case 4 -> "Central Izquierdo";
            case 5 -> "Lateral Izquierdo";
            case 6 -> "Mediocampista";
            case 7 -> "Volante Derecho";
            case 8 -> "Volante Izquierdo";
            case 9 -> "Extremo Derecho";
            case 10 -> "Delantero Centro";
            case 11 -> "Extremo Izquierdo";
            default -> "Delantero Centro";
        };
    }

    private static List<Equipo> cargarEquiposLocales() {
        return new ArrayList<>(Arrays.asList(
                // Argentina
                new Equipo("Boca Juniors", "Argentina", 3), new Equipo("River Plate", "Argentina", 3),
                new Equipo("Racing Club", "Argentina", 3), new Equipo("Independiente", "Argentina", 3),
                new Equipo("San Lorenzo", "Argentina", 3), new Equipo("Estudiantes", "Argentina", 4),
                new Equipo("Talleres", "Argentina", 4), new Equipo("Vélez", "Argentina", 4),
                new Equipo("Godoy Cruz", "Argentina", 4), new Equipo("Rosario Central", "Argentina", 4),
                new Equipo("Aldosivi", "Argentina", 5), new Equipo("Banfield", "Argentina", 5),
                new Equipo("Belgrano", "Argentina", 5), new Equipo("Platense", "Argentina", 5),
                new Equipo("Tigre", "Argentina", 5), new Equipo("Unión", "Argentina", 5),

                // Inglaterra
                new Equipo("Arsenal", "Inglaterra", 1), new Equipo("Chelsea", "Inglaterra", 1),
                new Equipo("Liverpool", "Inglaterra", 1), new Equipo("Manchester City", "Inglaterra", 1),
                new Equipo("Manchester United", "Inglaterra", 1), new Equipo("Aston Villa", "Inglaterra", 2),
                new Equipo("Tottenham Hotspur", "Inglaterra", 2), new Equipo("Newcastle United", "Inglaterra", 2),
                new Equipo("Brighton", "Inglaterra", 3), new Equipo("West Ham United", "Inglaterra", 3),
                new Equipo("Everton", "Inglaterra", 3), new Equipo("Fulham", "Inglaterra", 4),

                // España
                new Equipo("Real Madrid", "España", 1), new Equipo("Barcelona", "España", 1),
                new Equipo("Atlético de Madrid", "España", 1), new Equipo("Sevilla", "España", 2),
                new Equipo("Villarreal", "España", 2), new Equipo("Real Sociedad", "España", 2),
                new Equipo("Real Betis", "España", 3), new Equipo("Valencia", "España", 3),
                new Equipo("Girona", "España", 3), new Equipo("Celta de Vigo", "España", 4),

                // Italia
                new Equipo("Inter", "Italia", 1), new Equipo("Juventus", "Italia", 1),
                new Equipo("Milan", "Italia", 1), new Equipo("Napoli", "Italia", 2),
                new Equipo("Roma", "Italia", 2), new Equipo("Atalanta", "Italia", 2),
                new Equipo("Lazio", "Italia", 2), new Equipo("Fiorentina", "Italia", 3),

                // Alemania
                new Equipo("Bayern Múnich", "Alemania", 1), new Equipo("Bayer Leverkusen", "Alemania", 1),
                new Equipo("Borussia Dortmund", "Alemania", 1), new Equipo("RB Leipzig", "Alemania", 2),
                new Equipo("Eintracht Frankfurt", "Alemania", 2), new Equipo("Stuttgart", "Alemania", 2),

                // Francia
                new Equipo("Paris Saint-Germain", "Francia", 1), new Equipo("Marseille", "Francia", 2),
                new Equipo("Monaco", "Francia", 2), new Equipo("Lille", "Francia", 2),
                new Equipo("Lyon", "Francia", 2), new Equipo("Rennes", "Francia", 3),

                // MLS & Otros
                new Equipo("Inter Miami", "EEUU", 3), new Equipo("LA Galaxy", "EEUU", 3),
                new Equipo("Los Angeles FC", "EEUU", 3), new Equipo("Ajax", "Países Bajos", 2),
                new Equipo("Benfica", "Portugal", 2), new Equipo("Porto", "Portugal", 2)
        ));
    }
}