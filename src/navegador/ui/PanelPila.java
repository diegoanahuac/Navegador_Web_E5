package navegador.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import navegador.modelo.Pagina;

/**
 * Representación visual de una pila: casillas numeradas de abajo (fondo)
 * hacia arriba (cima) y un aviso emergente para "PILA LLENA" / "PILA VACÍA".
 */
public class PanelPila extends PanelRedondeado {

    private static final String TARJETA_CASILLAS = "casillas";
    private static final String TARJETA_AVISO = "aviso";

    private final int capacidad;
    private final Color acento;
    private final Fila[] filas;          // filas[0] = fondo de la pila
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final JLabel etiquetaAviso = new JLabel("", SwingConstants.CENTER);
    private final JLabel etiquetaResumen = new JLabel("", SwingConstants.RIGHT);
    private final Timer temporizadorAviso;

    public PanelPila(String titulo, int capacidad, Color acento) {
        super(16, Paleta.TARJETA, Paleta.BORDE);
        this.capacidad = capacidad;
        this.acento = acento;
        this.filas = new Fila[capacidad];

        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        add(construirEncabezado(titulo), BorderLayout.NORTH);

        JPanel casillas = new JPanel(new GridLayout(capacidad, 1, 0, 6));
        casillas.setOpaque(false);
        // Se agregan de arriba hacia abajo: la última fila del arreglo va primero.
        for (int i = capacidad - 1; i >= 0; i--) {
            filas[i] = new Fila(i + 1);
            casillas.add(filas[i]);
        }

        JPanel panelAviso = new JPanel(new BorderLayout());
        panelAviso.setOpaque(false);
        etiquetaAviso.setFont(Paleta.fuente(Font.BOLD, 16));
        panelAviso.add(etiquetaAviso, BorderLayout.CENTER);

        contenedor.setOpaque(false);
        contenedor.add(casillas, TARJETA_CASILLAS);
        contenedor.add(panelAviso, TARJETA_AVISO);
        add(contenedor, BorderLayout.CENTER);

        temporizadorAviso = new Timer(2600, e -> cartas.show(contenedor, TARJETA_CASILLAS));
        temporizadorAviso.setRepeats(false);
    }

    private JPanel construirEncabezado(String titulo) {
        JPanel encabezado = new JPanel(new BorderLayout(8, 0));
        encabezado.setOpaque(false);

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(Paleta.fuente(Font.BOLD, 13));
        etiquetaTitulo.setForeground(acento);
        etiquetaTitulo.setIcon(new IconoGlobo(14, acento));
        etiquetaTitulo.setIconTextGap(7);

        etiquetaResumen.setFont(Paleta.fuente(Font.PLAIN, 11));
        etiquetaResumen.setForeground(Paleta.TEXTO_SUAVE);

        encabezado.add(etiquetaTitulo, BorderLayout.WEST);
        encabezado.add(etiquetaResumen, BorderLayout.EAST);
        return encabezado;
    }

    /** Dibuja el contenido actual de la pila (lista desde el fondo hacia la cima). */
    public void actualizar(List<Pagina> desdeElFondo) {
        for (int i = 0; i < capacidad; i++) {
            if (i < desdeElFondo.size()) {
                boolean esCima = (i == desdeElFondo.size() - 1);
                filas[i].mostrar(desdeElFondo.get(i).getUrl(), esCima);
            } else {
                filas[i].limpiar();
            }
        }
        etiquetaResumen.setText(desdeElFondo.size() + " / " + capacidad);
        revalidate();
        repaint();
    }

    /** Muestra temporalmente el aviso "PILA LLENA" o "PILA VACÍA". */
    public void mostrarAviso(String texto) {
        etiquetaAviso.setText("<html><div style='text-align:center;'>&#10006; " + texto + "</div></html>");
        etiquetaAviso.setForeground(Paleta.ACENTO);
        cartas.show(contenedor, TARJETA_AVISO);
        temporizadorAviso.restart();
    }

    /** Fila (casilla) de la pila con su número de posición. */
    private class Fila extends JPanel {

        private final int posicion;
        private final JLabel texto = new JLabel();
        private final PanelRedondeado caja = new PanelRedondeado(9, Paleta.TARJETA_SUAVE, Paleta.BORDE);

        Fila(int posicion) {
            this.posicion = posicion;
            setOpaque(false);
            setLayout(new BorderLayout(8, 0));
            setPreferredSize(new Dimension(220, 28));

            JPanel numero = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    int d = Math.min(getWidth(), getHeight()) - 2;
                    int x = (getWidth() - d) / 2;
                    int y = (getHeight() - d) / 2;
                    g2.setColor(Paleta.TARJETA_SUAVE);
                    g2.fillOval(x, y, d, d);
                    g2.setColor(acento);
                    g2.drawOval(x, y, d, d);
                    g2.setFont(Paleta.fuente(Font.BOLD, 10));
                    String s = String.valueOf(Fila.this.posicion);
                    int ancho = g2.getFontMetrics().stringWidth(s);
                    g2.drawString(s, x + (d - ancho) / 2,
                            y + d / 2 + g2.getFontMetrics().getAscent() / 2 - 1);
                    g2.dispose();
                }
            };
            numero.setOpaque(false);
            numero.setPreferredSize(new Dimension(20, 20));

            texto.setFont(Paleta.fuente(Font.PLAIN, 12));
            texto.setForeground(Paleta.TEXTO);
            texto.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 9));

            caja.setLayout(new BorderLayout());
            caja.add(texto, BorderLayout.CENTER);

            add(numero, BorderLayout.WEST);
            add(caja, BorderLayout.CENTER);
        }

        void mostrar(String url, boolean esCima) {
            texto.setText(esCima ? url + "   ▲ cima" : url);
            texto.setForeground(esCima ? acento : Paleta.TEXTO);
            texto.setFont(Paleta.fuente(esCima ? Font.BOLD : Font.PLAIN, 12));
            caja.setColorFondo(esCima ? Paleta.ACENTO_TENUE : Paleta.TARJETA_SUAVE);
            caja.setColorBorde(esCima ? acento : Paleta.BORDE);
            caja.setGrosorBorde(esCima ? 2 : 1);
        }

        void limpiar() {
            texto.setText("");
            caja.setColorFondo(Paleta.TARJETA);
            caja.setColorBorde(Paleta.BORDE);
            caja.setGrosorBorde(1);
        }
    }
}
