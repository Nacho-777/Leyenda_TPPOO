package model;

public class Arquero extends Jugador {
    private int reflejos;
    private int atajadas;
    private int salida;
    private int juegoConPies;

    public Arquero() {
        super();
    }

    public Arquero(String nombre, int edad, int ovr, double precio, int resistencia,
                   String equipo, int dorsal, String posicion, int velocidad,
                   int remate, int fuerza, int pase, int regate, int centros,
                   int marcaje, int definicion, int control, int entradas,
                   int reflejos, int atajadas, int salida, int juegoConPies) {
        super(nombre, edad, ovr, precio, resistencia, equipo, dorsal, posicion,
                velocidad, remate, fuerza, pase, regate, centros, marcaje,
                definicion, control, entradas);
        this.reflejos = reflejos;
        this.atajadas = atajadas;
        this.salida = salida;
        this.juegoConPies = juegoConPies;
    }

    @Override
    public void entrenar() {
        if (getOvr() < 99) setOvr(getOvr() + 1);
        reflejos = mejorarAtributo(reflejos);
        atajadas = mejorarAtributo(atajadas);
        salida = mejorarAtributo(salida);
        juegoConPies = mejorarAtributo(juegoConPies);
    }

    public int getReflejos() { return reflejos; }
    public void setReflejos(int reflejos) { this.reflejos = reflejos; }
    public int getAtajadas() { return atajadas; }
    public void setAtajadas(int atajadas) { this.atajadas = atajadas; }
    public int getSalida() { return salida; }
    public void setSalida(int salida) { this.salida = salida; }
    public int getJuegoConPies() { return juegoConPies; }
    public void setJuegoConPies(int juegoConPies) { this.juegoConPies = juegoConPies; }
}
