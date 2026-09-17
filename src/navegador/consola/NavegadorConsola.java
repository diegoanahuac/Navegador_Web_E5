package navegador.consola;

import java.util.List;
import java.util.Scanner;
import navegador.modelo.Navegador;
import navegador.modelo.Pagina;
import navegador.modelo.Pila;
import navegador.modelo.Resultado;

/**
 * Versión de consola con menú (misma lógica de pilas que la interfaz gráfica).
 * Útil para probar el proyecto sin entorno gráfico.
 */
public class NavegadorConsola {

    private final Navegador navegador = new Navegador();
    private final Scanner entrada = new Scanner(System.in);

    public void ejecutar() {
        System.out.println("=== NAVEGADOR WEB CON PILAS (LIFO) ===");
        boolean salir = false;
        while (!salir) {
            mostrarEstado();
            System.out.println("\n1) Visitar URL   2) Back   3) Forward   4) Exit");
            System.out.print("Opción: ");
            String opcion = entrada.hasNextLine() ? entrada.nextLine().trim() : "4";
            switch (opcion) {
                case "1":
                    System.out.print("URL: ");
                    String url = entrada.hasNextLine() ? entrada.nextLine() : "";
                    imprimir(navegador.visitar(url));
                    break;
                case "2":
                    imprimir(navegador.atras());
                    break;
                case "3":
                    imprimir(navegador.adelante());
                    break;
                case "4":
                    salir = true;
                    System.out.println("Saliendo del navegador. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida, intenta de nuevo.");
            }
        }
    }

    private void mostrarEstado() {
        Pagina actual = navegador.getPaginaActual();
        System.out.println("\n--------------------------------------------------");
        System.out.println("Página actual : "
                + (actual == null ? "(ninguna)" : actual.getUrl() + "  [" + actual.getTitulo() + "]"));
        System.out.println("Pila atrás    : " + describir(navegador.getPilaAtras()));
        System.out.println("Pila adelante : " + describir(navegador.getPilaAdelante()));
        System.out.println("--------------------------------------------------");
    }

    private String describir(Pila<Pagina> pila) {
        if (pila.estaVacia()) {
            return "(vacía)";
        }
        List<Pagina> paginas = pila.elementosDesdeElFondo();
        StringBuilder sb = new StringBuilder();
        for (int i = paginas.size() - 1; i >= 0; i--) {
            sb.append(i == paginas.size() - 1 ? "cima -> " : "        ")
              .append(paginas.get(i).getUrl());
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    private void imprimir(Resultado resultado) {
        System.out.println(">> " + resultado.getMensaje());
    }
}
