package navegador;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import navegador.consola.NavegadorConsola;
import navegador.ui.VentanaNavegador;

/**
 * Punto de entrada.
 *
 * <pre>
 *   java -cp out navegador.Main            → interfaz gráfica (Swing)
 *   java -cp out navegador.Main --consola  → versión de consola con menú
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        boolean consola = args.length > 0
                && ("--consola".equalsIgnoreCase(args[0]) || "-c".equalsIgnoreCase(args[0]));

        if (consola) {
            new NavegadorConsola().ejecutar();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UIManager.put("ToolTip.background", new javax.swing.plaf.ColorUIResource(0xFBF4E9));
            } catch (Exception ignorado) {
                // Si falla, se usa el look and feel por omisión.
            }
            new VentanaNavegador().setVisible(true);
        });
    }
}
