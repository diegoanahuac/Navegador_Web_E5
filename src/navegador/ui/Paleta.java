package navegador.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Colores y tipografías del tema beige con acentos vino / guinda. */
public final class Paleta {

    // Fondos beige
    public static final Color FONDO          = new Color(0xEFE3D0);
    public static final Color TARJETA        = new Color(0xFBF4E9);
    public static final Color TARJETA_SUAVE  = new Color(0xF6EADA);
    public static final Color BORDE          = new Color(0xD9C3A5);

    // Acentos vino / guinda
    public static final Color GUINDA         = new Color(0x7B1E2B);
    public static final Color GUINDA_OSCURO  = new Color(0x571119);
    public static final Color GUINDA_CLARO   = new Color(0xA33247);
    public static final Color GUINDA_SUAVE   = new Color(0xE8D3D3);

    // Texto
    public static final Color TEXTO          = new Color(0x3E2B25);
    public static final Color TEXTO_SUAVE    = new Color(0x8A7361);
    public static final Color BLANCO_CALIDO  = new Color(0xFFFBF4);

    // Estados
    public static final Color OK             = new Color(0x4F6D3A);
    public static final Color ALERTA         = new Color(0xB4761B);

    private static final String FAMILIA = elegirFamilia();

    private Paleta() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(FAMILIA, estilo, tamano);
    }

    private static String elegirFamilia() {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidata : new String[] {"Segoe UI", "Inter", "Helvetica Neue",
                "DejaVu Sans", "Liberation Sans", "Arial"}) {
            if (disponibles.contains(candidata)) {
                return candidata;
            }
        }
        return Font.SANS_SERIF;
    }
}
