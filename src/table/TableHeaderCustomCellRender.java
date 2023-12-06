package table;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import static javax.swing.SwingConstants.*;

public class TableHeaderCustomCellRender extends DefaultTableCellRenderer {

    private final JTable table;

    public TableHeaderCustomCellRender(JTable table) {
        this.table = table;

    }



    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(new EmptyBorder(8, 10, 8, 10));
        com.setFont(table.getTableHeader().getFont());
        //com.setBackground(table.getTableHeader().getBackground());
        com.setBackground(new Color(160, 160, 160));
        return com;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(table.getGridColor());
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);
        g2.dispose();
    }
}
