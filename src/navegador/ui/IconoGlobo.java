package navegador.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

/** Icono de globo terráqueo dibujado a mano (sin archivos externos). */
public class IconoGlobo implements Icon {

    private final int tamano;
    private final Color color;

    public IconoGlobo(int tamano, Color color) {
        this.tamano = tamano;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new java.awt.BasicStroke(Math.max(1.4f, tamano / 18f)));

        int d = tamano - 1;
        g2.drawOval(0, 0, d, d);
        g2.drawLine(0, d / 2, d, d / 2);              // ecuador
        g2.drawOval(d / 4, 0, d / 2, d);              // meridiano central
        g2.drawArc(0, d / 6, d, d / 2, 180, 180);     // paralelo superior
        g2.drawArc(0, d / 3, d, d / 2, 0, 180);       // paralelo inferior

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return tamano;
    }

    @Override
    public int getIconHeight() {
        return tamano;
    }
}
