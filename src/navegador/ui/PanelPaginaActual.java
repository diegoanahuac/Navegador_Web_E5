package navegador.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import navegador.modelo.Pagina;

/** Tarjeta que muestra la página actual (o el 404 cuando no existe). */
public class PanelPaginaActual extends PanelRedondeado {

    private final JLabel icono = new JLabel();
    private final JLabel titulo = new JLabel("", SwingConstants.CENTER);
    private final JLabel descripcion = new JLabel("", SwingConstants.CENTER);

    public PanelPaginaActual() {
        super(16, Paleta.TARJETA_SUAVE, Paleta.BORDE);
        setLayout(new BorderLayout(18, 0));
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        icono.setIcon(new IconoGlobo(54, Paleta.ACENTO));

        titulo.setFont(Paleta.fuente(Font.BOLD, 21));
        titulo.setForeground(Paleta.ACENTO);
        descripcion.setFont(Paleta.fuente(Font.PLAIN, 13));
        descripcion.setForeground(Paleta.TEXTO_SUAVE);

        titulo.setAlignmentX(CENTER_ALIGNMENT);
        descripcion.setAlignmentX(CENTER_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(10));
        textos.add(descripcion);
        textos.add(Box.createVerticalGlue());

        add(icono, BorderLayout.WEST);
        add(textos, BorderLayout.CENTER);
    }

    public void mostrar(Pagina pagina) {
        if (pagina == null) {
            titulo.setText("Sin página abierta");
            titulo.setForeground(Paleta.TEXTO_SUAVE);
            descripcion.setText("Escribe una URL y presiona Visitar para comenzar a navegar.");
            return;
        }
        if (!pagina.fueEncontrada()) {
            titulo.setText("404");
            titulo.setForeground(Paleta.ACENTO_PROFUNDO);
            descripcion.setText(pagina.getUrl() + " — página no encontrada en el catálogo local");
            return;
        }
        titulo.setText(pagina.getUrl());
        titulo.setForeground(Paleta.ACENTO);
        descripcion.setText(pagina.getDescripcion());
    }
}
