package domain;

import java.time.LocalDateTime;

public class Stock {
    private int libroId;
    private int sucursalId;
    private int cantidad;
    private int umbral;
    private LocalDateTime updatedAt;

    public Stock(int libroId, int sucursalId, int cantidad, int umbral, LocalDateTime updatedAt) {
        this.libroId = libroId;
        this.sucursalId = sucursalId;
        this.cantidad = cantidad;
        this.umbral = umbral;
        this.updatedAt = updatedAt;
    }

    public int getLibroId() { return libroId; }
    public int getSucursalId() { return sucursalId; }
    public int getCantidad() { return cantidad; }
    public int getUmbral() { return umbral; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setUmbral(int umbral) { this.umbral = umbral; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Stock{libroId=" + libroId + ", sucursalId=" + sucursalId +
                ", cantidad=" + cantidad + ", umbral=" + umbral + ", updatedAt=" + updatedAt + "}";
    }
}