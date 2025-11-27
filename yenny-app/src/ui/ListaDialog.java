package ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import domain.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Locale;

public class ListaDialog extends JDialog {
    private JPanel contentPane;
    private JButton botonConfirmar;
    private JTable tabla;

    private List<Libro> libros;
    private Libro libroSeleccionado = null;

    public ListaDialog() {
        setContentPane(contentPane);
        setModal(true);

        getRootPane().registerKeyboardAction(
                e -> {
                    if (botonConfirmar != null) botonConfirmar.doClick();
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        contentPane.setPreferredSize(new Dimension(760, 440));

        Container parent = tabla.getParent();
        boolean yaEnScroll = (parent instanceof JViewport) || (parent instanceof JScrollPane);
        if (!yaEnScroll) {
            JScrollPane sp = new JScrollPane(tabla);
            if (parent != null) {
                parent.removeAll();
                if (!(parent.getLayout() instanceof BorderLayout)) {
                    parent.setLayout(new BorderLayout());
                }
                parent.add(sp, BorderLayout.CENTER);
                parent.validate();
            }
        }

        botonConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    onOK();
                }
            }
        });

        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(25);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(true);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (c instanceof JLabel lbl) {
                    lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
                    lbl.setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                }
                return c;
            }
        };
        tabla.setDefaultRenderer(Object.class, cell);

        JTableHeader h = tabla.getTableHeader();
        if (h != null) {
            h.setFont(h.getFont().deriveFont(Font.BOLD, 16f));
            h.setPreferredSize(new Dimension(h.getPreferredSize().width, 28));
        }
    }

    public void configurar(String titulo, List<Libro> libros) {
        this.libros = libros;
        setTitle(titulo);

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor", "Editorial", "Categoría"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Libro libro : libros) {
            modelo.addRow(new Object[]{
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getEditorial(),
                    libro.getCategoria()
            });
        }

        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void onOK() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tenés que seleccionar un libro.",
                    "Seleccionar libro",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        libroSeleccionado = libros.get(fila);
        dispose();
    }

    private void onCancel() {
        libroSeleccionado = null;
        dispose();
    }

    public Libro mostrar() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        return libroSeleccionado;
    }

    public static Libro seleccionarLibroDeLista(String titulo, List<Libro> libros) {
        ListaDialog dialog = new ListaDialog();
        dialog.configurar(titulo, libros);
        return dialog.mostrar();
    }

    public static void main(String[] args) {
        ListaDialog dialog = new ListaDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setBackground(new Color(-14865084));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-14865084));
        contentPane.add(panel1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(900, 400), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-14865084));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-14865084));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        botonConfirmar = new JButton();
        botonConfirmar.setIcon(new ImageIcon(getClass().getResource("/resources/icons/ok.png")));
        botonConfirmar.setText("OK");
        panel3.add(botonConfirmar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, 35), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setBackground(new Color(-14865084));
        panel1.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabla = new JTable();
        tabla.setBackground(new Color(-14865084));
        Font tablaFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 16, tabla.getFont());
        if (tablaFont != null) tabla.setFont(tablaFont);
        tabla.setForeground(new Color(-988200));
        tabla.setGridColor(new Color(-3356196));
        panel4.add(tabla, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
