package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteQuincenalResumen(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        int cantidadVentas,
        int unidadesVendidas,
        BigDecimal totalRecaudado,
        BigDecimal ticketPromedio
) {}
