package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venta {
    private int id;
    private int sucursalId;
    private int cajeroId;
    private Integer clienteId;
    private LocalDateTime fecha;
    private BigDecimal total;
    private MedioPago medioPago;

    public Venta(int id, int sucursalId, int cajeroId, Integer clienteId,
                 LocalDateTime fecha, BigDecimal total, MedioPago medioPago) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.cajeroId = cajeroId;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.total = total;
        this.medioPago = medioPago;
    }

    public int getId() { return id; }
    public int getSucursalId() { return sucursalId; }
    public int getCajeroId() { return cajeroId; }
    public Integer getClienteId() { return clienteId; }
    public LocalDateTime getFechaHora() { return fecha; }
    public BigDecimal getTotal() { return total; }
    public MedioPago getMedioPago() { return medioPago; }

    public void setTotal(BigDecimal total) { this.total = total; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", sucursalId=" + sucursalId + ", cajeroId=" + cajeroId +
                ", clienteId=" + clienteId + ", fechaHora=" + fecha + ", total=" + total +
                ", medioPago=" + medioPago + "}";
    }
}
