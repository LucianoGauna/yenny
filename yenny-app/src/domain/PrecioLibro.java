package domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrecioLibro {
    private int id;
    private int libroId;
    private Tapa tapa;
    private boolean firmado;
    private BigDecimal precio;
    private LocalDateTime vigenteDesde;
    private LocalDateTime vigenteHasta;

    public PrecioLibro(int id, int libroId, Tapa tapa, boolean firmado,
                       BigDecimal precio, LocalDateTime vigenteDesde, LocalDateTime vigenteHasta) {
        this.id = id;
        this.libroId = libroId;
        this.tapa = tapa;
        this.firmado = firmado;
        this.precio = precio;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
    }

    public int getId() { return id; }
    public int getLibroId() { return libroId; }
    public Tapa getTapa() { return tapa; }
    public boolean isFirmado() { return firmado; }
    public BigDecimal getPrecio() { return precio; }
    public LocalDateTime getVigenteDesde() { return vigenteDesde; }
    public LocalDateTime getVigenteHasta() { return vigenteHasta; }

    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public void setVigenteHasta(LocalDateTime vigenteHasta) { this.vigenteHasta = vigenteHasta; }

    @Override
    public String toString() {
        return "PrecioLibro{id=" + id + ", libroId=" + libroId + ", tapa=" + tapa +
                ", firmado=" + firmado + ", precio=" + precio + ", vigenteDesde=" + vigenteDesde +
                ", vigenteHasta=" + vigenteHasta + "}";
    }
}
