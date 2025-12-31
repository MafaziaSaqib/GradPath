import javax.swing.*;
import java.awt.*;

class GradientPanel extends JPanel {
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(236,240,241),
                0, getHeight(), new Color(224,234,252));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}