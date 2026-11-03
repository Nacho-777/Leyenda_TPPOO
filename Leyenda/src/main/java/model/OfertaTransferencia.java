package model;

/** Oferta de fichaje presentada al futbolista en el mercado de pases. */
public class OfertaTransferencia {
    private final ClubInfo club;
    private final double valorMercado;
    private final int salarioAnual;
    private final int anosContrato;
    private final String motivo;

    public OfertaTransferencia(ClubInfo club, double valorMercado, int salarioAnual,
                               int anosContrato, String motivo) {
        this.club = club;
        this.valorMercado = valorMercado;
        this.salarioAnual = salarioAnual;
        this.anosContrato = anosContrato;
        this.motivo = motivo;
    }
    public ClubInfo getClub() { return club; }
    public double getValorMercado() { return valorMercado; }
    public int getSalarioAnual() { return salarioAnual; }
    public int getAnosContrato() { return anosContrato; }
    public String getMotivo() { return motivo; }
}
