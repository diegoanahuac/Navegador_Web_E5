package navegador.modelo;

import java.util.Objects;

/**
 * Página web simulada (todo local: no existe conexión a internet).
 */
public class Pagina {

    private final String url;
    private final String titulo;
    private final String descripcion;
    private final boolean encontrada;

    public Pagina(String url, String titulo, String descripcion) {
        this(url, titulo, descripcion, true);
    }

    private Pagina(String url, String titulo, String descripcion, boolean encontrada) {
        this.url = url;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.encontrada = encontrada;
    }

    /** Página de error 404 para una URL que no existe en el catálogo local. */
    public static Pagina noEncontrada(String url) {
        return new Pagina(url, "404", "Página no encontrada en el catálogo local", false);
    }

    public String getUrl() {
        return url;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean fueEncontrada() {
        return encontrada;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Pagina)) {
            return false;
        }
        return url.equalsIgnoreCase(((Pagina) otro).url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url.toLowerCase());
    }

    @Override
    public String toString() {
        return url;
    }
}
