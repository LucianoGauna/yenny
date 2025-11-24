package ui;

import domain.StockResumen;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StockTableModel extends AbstractTableModel {
    private final String[] cols = {"Título", "Cantidad", "Umbral"};
    private final List<StockResumen> data;

    public StockTableModel(List<StockResumen> data) { this.data = data; }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public boolean isCellEditable(int r, int c) { return false; }

    @Override public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 1, 2 -> Integer.class;
            default -> String.class;
        };
    }

    @Override public Object getValueAt(int r, int c) {
        StockResumen s = data.get(r);
        return switch (c) {
            case 0 -> s.titulo();
            case 1 -> s.cantidad();
            case 2 -> s.umbral();
            default -> null;
        };
    }
}

