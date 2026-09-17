package navegador.modelo;

/**
 * Lógica del navegador. Administra el historial con dos pilas:
 *
 * <ul>
 *   <li><b>Pila atrás</b>: páginas visitadas anteriormente.</li>
 *   <li><b>Pila adelante</b>: páginas a las que se puede avanzar
 *       después de haber retrocedido.</li>
 * </ul>
 *
 * Reglas implementadas:
 * <ol>
 *   <li>Visitar una página nueva: la página actual se apila en <i>atrás</i>,
 *       la nueva pasa a ser la actual y la pila <i>adelante</i> se vacía.</li>
 *   <li>Back: la página actual se apila en <i>adelante</i> y la cima de
 *       <i>atrás</i> se convierte en la página actual.</li>
 *   <li>Forward: la página actual se apila en <i>atrás</i> y la cima de
 *       <i>adelante</i> se convierte en la página actual.</li>
 * </ol>
 */
public class Navegador {

    private final Pila<Pagina> pilaAtras;
    private final Pila<Pagina> pilaAdelante;
    private final CatalogoLocal catalogo = new CatalogoLocal();
    private Pagina paginaActual;

    public Navegador() {
        this(Pila.CAPACIDAD_POR_DEFECTO);
    }

    public Navegador(int capacidad) {
        this.pilaAtras = new Pila<>(capacidad);
        this.pilaAdelante = new Pila<>(capacidad);
        this.paginaActual = null;
    }

    /** Visita una página nueva a partir del texto escrito por el usuario. */
    public Resultado visitar(String textoUrl) {
        String url = CatalogoLocal.normalizar(textoUrl);
        if (url.isEmpty()) {
            return Resultado.error("Escribe una URL para poder visitar una página.",
                    Resultado.Aviso.NINGUNO);
        }

        Pagina nueva = catalogo.abrir(url);
        boolean desbordo = false;
        if (paginaActual != null) {
            // La página actual pasa a la pila de retroceso.
            desbordo = pilaAtras.apilarConDescarte(paginaActual);
        }
        paginaActual = nueva;
        // Al visitar una página nueva la pila de avance se limpia.
        pilaAdelante.vaciar();

        if (desbordo) {
            return Resultado.aviso("Pila atrás llena (" + pilaAtras.capacidad()
                    + "): se descartó la página más antigua del historial.",
                    Resultado.Aviso.PILA_ATRAS_LLENA);
        }
        if (!nueva.fueEncontrada()) {
            return Resultado.aviso("Error 404: la página " + url
                    + " no existe en el catálogo local.", Resultado.Aviso.NINGUNO);
        }
        return Resultado.exito("Visitando " + url + " — la pila adelante se vació.");
    }

    /** Retrocede a la página anterior (Back). */
    public Resultado atras() {
        if (pilaAtras.estaVacia()) {
            return Resultado.error("No hay páginas anteriores: la pila atrás está vacía.",
                    Resultado.Aviso.PILA_ATRAS_VACIA);
        }
        boolean desbordo = false;
        if (paginaActual != null) {
            desbordo = pilaAdelante.apilarConDescarte(paginaActual);
        }
        paginaActual = pilaAtras.desapilar();

        if (desbordo) {
            return Resultado.aviso("Pila adelante llena (" + pilaAdelante.capacidad()
                    + "): se descartó la página más antigua. Ahora estás en "
                    + paginaActual.getUrl() + ".", Resultado.Aviso.PILA_ADELANTE_LLENA);
        }
        return Resultado.exito("Back: regresaste a " + paginaActual.getUrl() + ".");
    }

    /** Avanza a la página siguiente (Forward). */
    public Resultado adelante() {
        if (pilaAdelante.estaVacia()) {
            return Resultado.error("No hay páginas siguientes: la pila adelante está vacía.",
                    Resultado.Aviso.PILA_ADELANTE_VACIA);
        }
        boolean desbordo = false;
        if (paginaActual != null) {
            desbordo = pilaAtras.apilarConDescarte(paginaActual);
        }
        paginaActual = pilaAdelante.desapilar();

        if (desbordo) {
            return Resultado.aviso("Pila atrás llena (" + pilaAtras.capacidad()
                    + "): se descartó la página más antigua. Ahora estás en "
                    + paginaActual.getUrl() + ".", Resultado.Aviso.PILA_ATRAS_LLENA);
        }
        return Resultado.exito("Forward: avanzaste a " + paginaActual.getUrl() + ".");
    }

    public Pagina getPaginaActual() {
        return paginaActual;
    }

    public Pila<Pagina> getPilaAtras() {
        return pilaAtras;
    }

    public Pila<Pagina> getPilaAdelante() {
        return pilaAdelante;
    }

    public CatalogoLocal getCatalogo() {
        return catalogo;
    }

    public boolean puedeRetroceder() {
        return !pilaAtras.estaVacia();
    }

    public boolean puedeAvanzar() {
        return !pilaAdelante.estaVacia();
    }
}
