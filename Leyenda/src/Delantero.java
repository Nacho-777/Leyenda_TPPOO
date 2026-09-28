package model;
public class Delantero extends model.Jugador{
    private int cabezazo;
    public Delantero() {
        super();
    }
    public Delantero(String nombre, int edad, int ovr, double precio, int resistencia, String equipo, int dorsal,String posicion, int velocidad, int remate, int fuerza, int pase, int regate, int centros,
                     int marcaje, int definicion, int control, int entradas, int cabezazo) {
        super(nombre, edad, ovr, precio, resistencia, equipo, dorsal, posicion, velocidad, remate,fuerza, pase, regate, centros, marcaje, definicion, control, entradas);
        this.cabezazo = cabezazo;
    }
    public int getCabezazo() {
        return cabezazo;
    }
    public void setCabezazo(int cabezazo) {
        this.cabezazo = cabezazo;
    }
}
