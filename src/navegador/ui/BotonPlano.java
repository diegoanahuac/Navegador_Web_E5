package navegador.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/** Botón plano redondeado con relleno o contorno, en tonos vino sobre beige. */
public class BotonPlano extends JButton {

    private final Color fondo;
    private final Color fondoHover;
    private final Color borde;
    private final int radio;
    private boolean encima;

    public BotonPlano(String texto, Color fondo, Color fondoHover, Color borde,
                      Color colorTexto, int radio) {
        super(texto);
        this.fondo = fondo;
        this.fondoHover = fondoHover;
        this.borde = borde;
        this.radio = radio;

        setForeground(colorTexto);
        setFont(Paleta.fuente(java.awt.Font.BOLD, 13));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    /** Botón principal: relleno del color de acento con texto claro. */
    public static BotonPlano solido(String texto) {
        return new BotonPlano(texto, Paleta.ACENTO, Paleta.ACENTO_PROFUNDO,
                Paleta.ACENTO, Paleta.BLANCO_CALIDO, 12);
    }

    /** Botón secundario: beige con contorno del color de acento. */
    public static BotonPlano contorno(String texto) {
        return new BotonPlano(texto, Paleta.TARJETA, Paleta.ACENTO_TENUE,
                Paleta.ACENTO, Paleta.ACENTO, 12);
    }

    /** Botón de salida: mismo acento que el botón principal. */
    public static BotonPlano salida(String texto) {
        return solido(texto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color relleno = getModel().isPressed() ? fondoHover : (encima ? fondoHover : fondo);
        g2.setColor(relleno);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        g2.setColor(borde);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        g2.dispose();
        super.paintComponent(g);
    }
}
