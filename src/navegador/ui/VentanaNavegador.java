package navegador.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import navegador.modelo.Navegador;
import navegador.modelo.Pila;
import navegador.modelo.Resultado;

/** Ventana principal del navegador simulado. */
public class VentanaNavegador extends JFrame {

    private final Navegador navegador = new Navegador(Pila.CAPACIDAD_POR_DEFECTO);

    private final JTextField campoUrl = new JTextField();
    private final PanelPaginaActual panelPaginaActual = new PanelPaginaActual();
    private final PanelPila panelAtras =
            new PanelPila("Pila atrás (Back stack)", Pila.CAPACIDAD_POR_DEFECTO, Paleta.GUINDA);
    private final PanelPila panelAdelante =
            new PanelPila("Pila adelante (Forward stack)", Pila.CAPACIDAD_POR_DEFECTO,
                    Paleta.GUINDA_CLARO);
    private final JLabel barraEstado = new JLabel();

    public VentanaNavegador() {
        setTitle("Navegador Web con Pilas (LIFO) — Proyecto E5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 700));

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Paleta.FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirCentro(), BorderLayout.CENTER);
        raiz.add(construirPie(), BorderLayout.SOUTH);

        setContentPane(raiz);
        registrarAtajos();

        panelPaginaActual.mostrar(null);
        actualizarVista();
        mostrarMensaje(Resultado.exito(
                "Escribe una URL y presiona Visitar. La página anterior se guardará en la pila atrás."));

        pack();
        setSize(new Dimension(1000, 780));
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------ UI

    private JComponent construirEncabezado() {
        PanelRedondeado barra = new PanelRedondeado(16, Paleta.GUINDA, Paleta.GUINDA_OSCURO);
        barra.setLayout(new BorderLayout(10, 0));
        barra.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel titulo = new JLabel("PROYECTO NAVEGADOR WEB");
        titulo.setFont(Paleta.fuente(Font.BOLD, 22));
        titulo.setForeground(Paleta.BLANCO_CALIDO);
        titulo.setIcon(new IconoGlobo(22, Paleta.BLANCO_CALIDO));
        titulo.setIconTextGap(12);

        JLabel subtitulo = new JLabel("Historial con dos pilas · LIFO · 100% local");
        subtitulo.setFont(Paleta.fuente(Font.PLAIN, 12));
        subtitulo.setForeground(new java.awt.Color(0xF0D9DC));
        subtitulo.setHorizontalAlignment(SwingConstants.RIGHT);

        barra.add(titulo, BorderLayout.WEST);
        barra.add(subtitulo, BorderLayout.EAST);
        return barra;
    }

    private JComponent construirCentro() {
        PanelRedondeado tarjeta = new PanelRedondeado(18, Paleta.TARJETA, Paleta.BORDE);
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel superior = new JPanel(new BorderLayout(0, 10));
        superior.setOpaque(false);
        superior.add(construirBarraUrl(), BorderLayout.NORTH);
        superior.add(construirBotones(), BorderLayout.CENTER);
        superior.add(construirSugerencias(), BorderLayout.SOUTH);

        JPanel actual = new JPanel(new BorderLayout(0, 6));
        actual.setOpaque(false);
        JLabel etiqueta = new JLabel("Página actual", SwingConstants.CENTER);
        etiqueta.setFont(Paleta.fuente(Font.BOLD, 11));
        etiqueta.setForeground(Paleta.TEXTO_SUAVE);
        actual.add(etiqueta, BorderLayout.NORTH);
        actual.add(panelPaginaActual, BorderLayout.CENTER);

        JPanel pilas = new JPanel(new GridLayout(1, 2, 14, 0));
        pilas.setOpaque(false);
        pilas.add(panelAtras);
        pilas.add(panelAdelante);

        tarjeta.add(superior, BorderLayout.NORTH);
        tarjeta.add(actual, BorderLayout.CENTER);
        tarjeta.add(pilas, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JComponent construirBarraUrl() {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);

        JLabel etiqueta = new JLabel("URL:");
        etiqueta.setFont(Paleta.fuente(Font.BOLD, 13));
        etiqueta.setForeground(Paleta.TEXTO);

        PanelRedondeado caja = new PanelRedondeado(12, Paleta.BLANCO_CALIDO, Paleta.BORDE);
        caja.setLayout(new BorderLayout());
        caja.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        campoUrl.setFont(Paleta.fuente(Font.PLAIN, 14));
        campoUrl.setForeground(Paleta.TEXTO);
        campoUrl.setCaretColor(Paleta.GUINDA);
        campoUrl.setOpaque(false);
        campoUrl.setBorder(BorderFactory.createEmptyBorder());
        campoUrl.setText("https://www.google.com");
        campoUrl.addActionListener(e -> visitar());
        caja.add(campoUrl, BorderLayout.CENTER);

        BotonPlano visitar = BotonPlano.solido("Visitar");
        visitar.addActionListener(e -> visitar());

        fila.add(etiqueta, BorderLayout.WEST);
        fila.add(caja, BorderLayout.CENTER);
        fila.add(visitar, BorderLayout.EAST);
        return fila;
    }

    private JComponent construirBotones() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        fila.setOpaque(false);

        BotonPlano atras = BotonPlano.contorno("←  Back");
        atras.setToolTipText("Regresar a la página anterior (Alt + ←)");
        atras.addActionListener(e -> retroceder());

        BotonPlano adelante = BotonPlano.contorno("Forward  →");
        adelante.setToolTipText("Avanzar a la página siguiente (Alt + →)");
        adelante.addActionListener(e -> avanzar());

        BotonPlano salir = BotonPlano.salida("✖  Exit");
        salir.setToolTipText("Salir del navegador");
        salir.addActionListener(e -> salir());

        fila.add(atras);
        fila.add(adelante);
        fila.add(Box.createHorizontalStrut(14));
        fila.add(salir);
        return fila;
    }

    private JComponent construirSugerencias() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        fila.setOpaque(false);

        JLabel etiqueta = new JLabel("Sitios locales:");
        etiqueta.setFont(Paleta.fuente(Font.PLAIN, 11));
        etiqueta.setForeground(Paleta.TEXTO_SUAVE);
        fila.add(etiqueta);

        for (String url : navegador.getCatalogo().urlsDisponibles()) {
            String nombre = url.replaceFirst("^https://(www\\.)?", "");
            BotonPlano chip = new BotonPlano(nombre, Paleta.TARJETA_SUAVE, Paleta.GUINDA_SUAVE,
                    Paleta.BORDE, Paleta.GUINDA, 10);
            chip.setFont(Paleta.fuente(Font.PLAIN, 11));
            chip.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            chip.setToolTipText("Copiar " + url + " a la barra de direcciones");
            chip.addActionListener(e -> {
                campoUrl.setText(url);
                campoUrl.requestFocusInWindow();
            });
            fila.add(chip);
        }
        return fila;
    }

    private JComponent construirPie() {
        PanelRedondeado pie = new PanelRedondeado(12, Paleta.TARJETA_SUAVE, Paleta.BORDE);
        pie.setLayout(new BorderLayout());
        pie.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        barraEstado.setFont(Paleta.fuente(Font.PLAIN, 12));
        barraEstado.setForeground(Paleta.TEXTO);
        pie.add(barraEstado, BorderLayout.CENTER);
        return pie;
    }

    private void registrarAtajos() {
        JComponent raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "atras");
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), "adelante");
        raiz.getActionMap().put("atras", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retroceder();
            }
        });
        raiz.getActionMap().put("adelante", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avanzar();
            }
        });
    }

    // ------------------------------------------------------------- Acciones

    private void visitar() {
        procesar(navegador.visitar(campoUrl.getText()));
    }

    private void retroceder() {
        procesar(navegador.atras());
    }

    private void avanzar() {
        procesar(navegador.adelante());
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Deseas salir del navegador?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void procesar(Resultado resultado) {
        actualizarVista();
        mostrarMensaje(resultado);

        switch (resultado.getAviso()) {
            case PILA_ATRAS_LLENA:
                panelAtras.mostrarAviso("PILA LLENA", false);
                break;
            case PILA_ADELANTE_LLENA:
                panelAdelante.mostrarAviso("PILA LLENA", false);
                break;
            case PILA_ATRAS_VACIA:
                panelAtras.mostrarAviso("PILA VACÍA", true);
                break;
            case PILA_ADELANTE_VACIA:
                panelAdelante.mostrarAviso("PILA VACÍA", true);
                break;
            default:
                break;
        }
    }

    private void actualizarVista() {
        panelPaginaActual.mostrar(navegador.getPaginaActual());
        panelAtras.actualizar(navegador.getPilaAtras().elementosDesdeElFondo());
        panelAdelante.actualizar(navegador.getPilaAdelante().elementosDesdeElFondo());
        if (navegador.getPaginaActual() != null) {
            campoUrl.setText(navegador.getPaginaActual().getUrl());
        }
    }

    private void mostrarMensaje(Resultado resultado) {
        String simbolo;
        switch (resultado.getTipo()) {
            case ERROR:
                simbolo = "⚠  ";
                barraEstado.setForeground(Paleta.GUINDA);
                break;
            case AVISO:
                simbolo = "⚑  ";
                barraEstado.setForeground(Paleta.ALERTA);
                break;
            default:
                simbolo = "✓  ";
                barraEstado.setForeground(Paleta.OK);
                break;
        }
        barraEstado.setText(simbolo + resultado.getMensaje());
    }
}
