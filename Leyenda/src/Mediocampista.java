package model;
public class Mediocampista extends model.Jugador{
    private int vision;
    private int recuperacion;
    public Mediocampista() {
        super();
    }
    public Mediocampista(String nombre, int edad, int ovr, double precio, int resistencia, String equipo, int dorsal,
                         String posicion, int velocidad, int remate, int fuerza, int pase, int regate, int centros,
                         int marcaje, int definicion, int control, int entradas, int vision, int recuperacion) {
        super(nombre, edad, ovr, precio, resistencia, equipo, dorsal, posicion, velocidad, remate,
                fuerza, pase, regate, centros, marcaje, definicion, control, entradas);
        this.vision = vision;
        this.recuperacion = recuperacion;
    }
    public int getVision() {
        return vision;
    }
    public void setVision(int vision) {
        this.vision = vision;
    }
    public int getRecuperacion() {
        return recuperacion;
    }
    public void setRecuperacion(int recuperacion) {
        this.recuperacion = recuperacion;
    }
}
