package navegador.modelo;

/**
 * Respuesta de cada operación del navegador: qué pasó, con qué mensaje
 * amigable y qué aviso debe resaltarse sobre las pilas en la interfaz.
 */
public class Resultado {

    /** Tono del mensaje que se muestra al usuario. */
    public enum Tipo {
        EXITO, AVISO, ERROR
    }

    /** Aviso visual que se dibuja encima de una de las pilas. */
    public enum Aviso {
        NINGUNO,
        PILA_ATRAS_LLENA,
        PILA_ADELANTE_LLENA,
        PILA_ATRAS_VACIA,
        PILA_ADELANTE_VACIA
    }

    private final Tipo tipo;
    private final String mensaje;
    private final Aviso aviso;

    public Resultado(Tipo tipo, String mensaje, Aviso aviso) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.aviso = aviso;
    }

    public static Resultado exito(String mensaje) {
        return new Resultado(Tipo.EXITO, mensaje, Aviso.NINGUNO);
    }

    public static Resultado aviso(String mensaje, Aviso aviso) {
        return new Resultado(Tipo.AVISO, mensaje, aviso);
    }

    public static Resultado error(String mensaje, Aviso aviso) {
        return new Resultado(Tipo.ERROR, mensaje, aviso);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Aviso getAviso() {
        return aviso;
    }

    @Override
    public String toString() {
        return tipo + ": " + mensaje;
    }
}
