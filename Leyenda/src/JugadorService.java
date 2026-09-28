public interface JugadorService  {
    void crearJugador(model.Jugador j);
    void actualizarJugador(model.Jugador j);
    void eliminarJugador(int id);
    void entrenarJugador(int id);
    void simularTemporada();
    void tomarDecision(int id, String decision);
    void subirOVR(int id, int puntos);
    void retirarJugador(int id);
}
