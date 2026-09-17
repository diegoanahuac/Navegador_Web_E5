package navegador.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Tema del navegador: base beige y un único color de acento (violeta neón).
 *
 * <p>La paleta es intencionalmente lineal: todo lo que resalta usa el mismo
 * tono {@link #ACENTO}. {@link #ACENTO_PROFUNDO} y {@link #ACENTO_TENUE} son
 * el mismo color más oscuro o más claro, nunca un color distinto.</p>
 */
public final class Paleta {

    // Base beige
    public static final Color FONDO          = new Color(0xEFE3D0);
    public static final Color TARJETA        = new Color(0xFBF4E9);
    public static final Color TARJETA_SUAVE  = new Color(0xF6EADA);
    public static final Color BORDE          = new Color(0xD9C3A5);

    // Único acento: violeta neón (estilo cyberpunk)
    public static final Color ACENTO          = new Color(0x6D28F5);
    public static final Color ACENTO_PROFUNDO = new Color(0x4C16B8);
    public static final Color ACENTO_TENUE    = new Color(0xE4D9FC);

    // Texto
    public static final Color TEXTO          = new Color(0x3E2B25);
    public static final Color TEXTO_SUAVE    = new Color(0x8A7361);
    public static final Color BLANCO_CALIDO  = new Color(0xFFFBF4);

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
