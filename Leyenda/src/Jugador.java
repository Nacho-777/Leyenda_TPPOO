public abstract class Jugador {
    private int id;
    private String nombre;
    private int edad;
    private int ovr;
    private double precio;
    private int resistencia;
    private String equipo;
    private int dorsal;
    private String posicion;
    private int velocidad;
    private int remate;
    private int fuerza;
    private int pase;
    private int regate;
    private int centros;
    private int marcaje;
    private int definicion;
    private int control;
    private int entradas;

    public Jugador() {
    }

    public Jugador(String nombre, int edad, int ovr, double precio, int resistencia, String equipo, int dorsal, String posicion, int velocidad, int remate,
                   int fuerza, int pase, int regate, int centros, int marcaje, int definicion, int control, int entradas) {

        this.nombre = nombre;
        this.edad = edad;
        this.ovr = ovr;
        this.precio = precio;
        this.resistencia = resistencia;
        this.equipo = equipo;
        this.dorsal = dorsal;
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.remate = remate;
        this.fuerza = fuerza;
        this.pase = pase;
        this.regate = regate;
        this.centros = centros;
        this.marcaje = marcaje;
        this.definicion = definicion;
        this.control = control;
        this.entradas = entradas;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public int getOvr() { return ovr; }
    public void setOvr(int ovr) { this.ovr = ovr; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getResistencia() { return resistencia; }
    public void setResistencia(int resistencia) { this.resistencia = resistencia; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public int getDorsal() { return dorsal; }
    public void setDorsal(int dorsal) { this.dorsal = dorsal; }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }


    public int getVelocidad() { return velocidad; }
    public int getRemate() { return remate; }
    public int getFuerza() { return fuerza; }
    public int getPase() { return pase; }
    public int getRegate() { return regate; }
    public int getCentros() { return centros; }
    public int getMarcaje() { return marcaje; }
    public int getDefinicion() { return definicion; }
    public int getControl() { return control; }
    public int getEntradas() { return entradas; }
}
