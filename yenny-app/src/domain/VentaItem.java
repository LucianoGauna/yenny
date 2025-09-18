package domain;

import java.math.BigDecimal;

public class VentaItem {
    private int id;
    private int ventaId;
    private int libroId;
    private Tapa tapa;
    private boolean firmado;
    private int cantidad;
    private BigDecimal precioUnitarioAplicado;

    public VentaItem(int id, int ventaId, int libroId, Tapa tapa, boolean firmado,
                     int cantidad, BigDecimal precioUnitarioAplicado) {
        this.id = id;
        this.ventaId = ventaId;
        this.libroId = libroId;
        this.tapa = tapa;
        this.firmado = firmado;
        this.cantidad = cantidad;
        this.precioUnitarioAplicado = precioUnitarioAplicado;
    }

    public int getId() { return id; }
    public int getVentaId() { return ventaId; }
    public int getLibroId() { return libroId; }
    public Tapa getTapa() { return tapa; }
    public boolean isFirmado() { return firmado; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitarioAplicado() { return precioUnitarioAplicado; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPrecioUnitarioAplicado(BigDecimal precioUnitarioAplicado) { this.precioUnitarioAplicado = precioUnitarioAplicado; }

    @Override
    public String toString() {
        return "VentaItem{id=" + id + ", ventaId=" + ventaId + ", libroId=" + libroId +
                ", tapa=" + tapa + ", firmado=" + firmado + ", cantidad=" + cantidad +
                ", precioUnitarioAplicado=" + precioUnitarioAplicado + "}";
    }
}
