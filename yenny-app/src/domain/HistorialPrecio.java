package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialPrecio {
    private int id;
    private int precioLibroId;
    private int usuarioId;
    private BigDecimal precioAnterior;
    private BigDecimal precioNuevo;
    private String motivo;
    private LocalDateTime fechaHora;

    public HistorialPrecio(int id, int precioLibroId, int usuarioId,
                           BigDecimal precioAnterior, BigDecimal precioNuevo,
                           String motivo, LocalDateTime fechaHora) {
        this.id = id;
        this.precioLibroId = precioLibroId;
        this.usuarioId = usuarioId;
        this.precioAnterior = precioAnterior;
        this.precioNuevo = precioNuevo;
        this.motivo = motivo;
        this.fechaHora = fechaHora;
    }

    public int getId() { return id; }
    public int getPrecioLibroId() { return precioLibroId; }
    public int getUsuarioId() { return usuarioId; }
    public BigDecimal getPrecioAnterior() { return precioAnterior; }
    public BigDecimal getPrecioNuevo() { return precioNuevo; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    @Override
    public String toString() {
        return "HistorialPrecio{id=" + id + ", precioLibroId=" + precioLibroId +
                ", usuarioId=" + usuarioId + ", precioAnterior=" + precioAnterior +
                ", precioNuevo=" + precioNuevo + ", motivo='" + motivo + "', fechaHora=" + fechaHora + "}";
    }
}
