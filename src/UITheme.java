import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

class UITheme {
    static final Color ACCENT = new Color(26,188,156);
    static final Font TITLE = new Font("Segoe UI", Font.BOLD, 20);
    static final Font HEADING = new Font("Segoe UI", Font.BOLD, 18);
    static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    static void styleButton(JButton b){
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    static void styleTable(JTable table){
        table.setRowHeight(28);
        table.setFont(NORMAL);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(214,234,248));
    }

    static Border cardBorder(){
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,1,1,1,new Color(220,220,220)),
                new EmptyBorder(16,16,16,16)
        );
    }
    static Border padding() {
        return new EmptyBorder(16, 16, 16, 16);
    }
}