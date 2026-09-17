package navegador.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Panel con esquinas redondeadas y borde de un solo color. */
public class PanelRedondeado extends JPanel {

    private final int radio;
    private Color colorFondo;
    private Color colorBorde;
    private int grosorBorde = 1;

    public PanelRedondeado(int radio, Color colorFondo, Color colorBorde) {
        this.radio = radio;
        this.colorFondo = colorFondo;
        this.colorBorde = colorBorde;
        setOpaque(false);
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        repaint();
    }

    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde;
        repaint();
    }

    public void setGrosorBorde(int grosorBorde) {
        this.grosorBorde = grosorBorde;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (colorFondo != null) {
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        }
        if (colorBorde != null) {
            g2.setColor(colorBorde);
            for (int i = 0; i < grosorBorde; i++) {
                g2.drawRoundRect(i, i, getWidth() - 1 - 2 * i, getHeight() - 1 - 2 * i,
                        radio, radio);
            }
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
