package navegador.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Pila (Stack) genérica de capacidad fija implementada sobre un arreglo.
 *
 * <p>Estructura LIFO (Last In, First Out): el último elemento apilado es el
 * primero en salir. Es la estructura que sostiene todo el historial del
 * navegador: una pila para "atrás" y otra para "adelante".</p>
 *
 * @param <T> tipo de dato almacenado en la pila
 */
public class Pila<T> {

    /** Capacidad usada por el navegador cuando no se indica otra. */
    public static final int CAPACIDAD_POR_DEFECTO = 5;

    private final Object[] elementos;
    private int tope; // cantidad de elementos almacenados

    public Pila() {
        this(CAPACIDAD_POR_DEFECTO);
    }

    public Pila(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad de la pila debe ser mayor a cero.");
        }
        this.elementos = new Object[capacidad];
        this.tope = 0;
    }

    /**
     * Apila un elemento. Si la pila está llena lanza excepción; para el caso
     * del navegador se usa {@link #apilarConDescarte(Object)}.
     */
    public void apilar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se pueden apilar elementos nulos.");
        }
        if (estaLlena()) {
            throw new IllegalStateException("Pila llena: no hay espacio disponible.");
        }
        elementos[tope++] = elemento;
    }

    /**
     * Apila un elemento aunque la pila esté llena: en ese caso descarta el
     * elemento del fondo (el más antiguo) y recorre el resto una posición.
     *
     * @return {@code true} si hubo desbordamiento y se descartó el más antiguo
     */
    public boolean apilarConDescarte(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se pueden apilar elementos nulos.");
        }
        boolean desbordo = false;
        if (estaLlena()) {
            System.arraycopy(elementos, 1, elementos, 0, tope - 1);
            tope--;
            desbordo = true;
        }
        elementos[tope++] = elemento;
        return desbordo;
    }

    /** Retira y devuelve el elemento de la cima. */
    @SuppressWarnings("unchecked")
    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacía: no hay elementos para desapilar.");
        }
        T elemento = (T) elementos[--tope];
        elementos[tope] = null; // se libera la referencia
        return elemento;
    }

    /** Devuelve (sin retirar) el elemento de la cima, o {@code null} si está vacía. */
    @SuppressWarnings("unchecked")
    public T cima() {
        return estaVacia() ? null : (T) elementos[tope - 1];
    }

    public boolean estaVacia() {
        return tope == 0;
    }

    public boolean estaLlena() {
        return tope == elementos.length;
    }

    public int tamano() {
        return tope;
    }

    public int capacidad() {
        return elementos.length;
    }

    /** Vacía la pila por completo (se usa al visitar una página nueva). */
    public void vaciar() {
        for (int i = 0; i < tope; i++) {
            elementos[i] = null;
        }
        tope = 0;
    }

    /**
     * Copia de los elementos desde el fondo hacia la cima.
     * El índice 0 es el elemento más antiguo y el último es la cima.
     */
    @SuppressWarnings("unchecked")
    public List<T> elementosDesdeElFondo() {
        List<T> copia = new ArrayList<>(tope);
        for (int i = 0; i < tope; i++) {
            copia.add((T) elementos[i]);
        }
        return copia;
    }

    @Override
    public String toString() {
        return elementosDesdeElFondo().toString();
    }
}
