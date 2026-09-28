public class Arquero extends Jugador{
    private int reflejos;
    private int atajadas;
    private int salida;
    private int juegoConPies;

    public arquero() { super(); }

    public arquero(Long id, String nombre, int edad, int ovr, Long equipoId,int reflejos, int atajadas, int salida, int juegoConPies) {

        this.reflejos = reflejos;
        this.atajadas = atajadas;
        this.salida = salida;
        this.juegoConPies = juegoConPies;
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
