# Navegador Web con Pilas (LIFO) — Proyecto E5

Aplicación de escritorio en **Java (Swing)** que simula el funcionamiento básico de un
navegador web. Todo el historial se administra con la estructura de datos **Pila (Stack)**:

- **Pila atrás (Back stack):** almacena las páginas visitadas anteriormente.
- **Pila adelante (Forward stack):** almacena las páginas a las que se puede avanzar
  después de haber retrocedido.

El proyecto es **100 % local**: no realiza ninguna conexión a internet, las páginas se
simulan con un catálogo interno y cualquier URL desconocida produce un **error 404**.

![Navegar](docs/capturas/01-visitar.png)

> **¿Necesitas entender el código?** En [`EXPLICACION.md`](EXPLICACION.md) está el
> desglose completo: fase por fase, clase por clase y método por método.

## Diagrama de clases (UML)

```mermaid
classDiagram
    direction LR

    class Pila~T~ {
        +int CAPACIDAD_POR_DEFECTO$
        -Object[] elementos
        -int tope
        +Pila()
        +Pila(int capacidad)
        +apilar(T elemento) void
        +apilarConDescarte(T elemento) boolean
        +desapilar() T
        +cima() T
        +estaVacia() boolean
        +estaLlena() boolean
        +tamano() int
        +capacidad() int
        +vaciar() void
        +elementosDesdeElFondo() List~T~
    }

    class Pagina {
        -String url
        -String titulo
        -String descripcion
        -boolean encontrada
        +Pagina(String url, String titulo, String descripcion)
        +noEncontrada(String url) Pagina$
        +getUrl() String
        +getTitulo() String
        +getDescripcion() String
        +fueEncontrada() boolean
        +equals(Object otro) boolean
        +hashCode() int
    }

    class CatalogoLocal {
        -Map~String, Pagina~ sitios
        +CatalogoLocal()
        -registrar(String url, String titulo, String descripcion) void
        +abrir(String urlNormalizada) Pagina
        +urlsDisponibles() List~String~
        +normalizar(String textoUsuario) String$
        -clave(String url) String$
    }

    class Navegador {
        -Pila~Pagina~ pilaAtras
        -Pila~Pagina~ pilaAdelante
        -CatalogoLocal catalogo
        -Pagina paginaActual
        +Navegador()
        +Navegador(int capacidad)
        +visitar(String textoUrl) Resultado
        +atras() Resultado
        +adelante() Resultado
        +getPaginaActual() Pagina
        +getPilaAtras() Pila~Pagina~
        +getPilaAdelante() Pila~Pagina~
        +getCatalogo() CatalogoLocal
        +puedeRetroceder() boolean
        +puedeAvanzar() boolean
    }

    class Resultado {
        -Tipo tipo
        -String mensaje
        -Aviso aviso
        +exito(String mensaje) Resultado$
        +aviso(String mensaje, Aviso aviso) Resultado$
        +error(String mensaje, Aviso aviso) Resultado$
        +getTipo() Tipo
        +getMensaje() String
        +getAviso() Aviso
    }

    class Tipo {
        <<enumeration>>
        EXITO
        AVISO
        ERROR
    }

    class Aviso {
        <<enumeration>>
        NINGUNO
        PILA_ATRAS_LLENA
        PILA_ADELANTE_LLENA
        PILA_ATRAS_VACIA
        PILA_ADELANTE_VACIA
    }

    class VentanaNavegador {
        -Navegador navegador
        -JTextField campoUrl
        -PanelPaginaActual panelPaginaActual
        -PanelPila panelAtras
        -PanelPila panelAdelante
        -JLabel barraEstado
        +VentanaNavegador()
        -visitar() void
        -retroceder() void
        -avanzar() void
        -salir() void
        -procesar(Resultado resultado) void
        -actualizarVista() void
        -mostrarMensaje(Resultado resultado) void
    }

    class PanelPila {
        -int capacidad
        -Fila[] filas
        -CardLayout cartas
        -Timer temporizadorAviso
        +PanelPila(String titulo, int capacidad, Color acento)
        +actualizar(List~Pagina~ desdeElFondo) void
        +mostrarAviso(String texto) void
    }

    class PanelPaginaActual {
        +PanelPaginaActual()
        +mostrar(Pagina pagina) void
    }

    class PanelRedondeado {
        -int radio
        -Color colorFondo
        -Color colorBorde
        +paintComponent(Graphics g) void
    }

    class BotonPlano {
        +solido(String texto) BotonPlano$
        +contorno(String texto) BotonPlano$
        +salida(String texto) BotonPlano$
        +paintComponent(Graphics g) void
    }

    class IconoGlobo {
        +paintIcon(Component c, Graphics g, int x, int y) void
    }

    class Paleta {
        +Color FONDO$
        +Color ACENTO$
        +fuente(int estilo, int tamano) Font$
    }

    class NavegadorConsola {
        -Navegador navegador
        +ejecutar() void
        -mostrarEstado() void
    }

    class Main {
        +main(String[] args) void$
    }

    Navegador o-- "2" Pila : pilaAtras y pilaAdelante
    Navegador *-- CatalogoLocal : catalogo
    Navegador --> Pagina : paginaActual
    Navegador ..> Resultado : devuelve
    Pila ..> Pagina : almacena
    CatalogoLocal *-- Pagina : sitios
    Resultado *-- Tipo
    Resultado *-- Aviso

    VentanaNavegador *-- Navegador : usa la logica
    VentanaNavegador *-- PanelPila : dos paneles
    VentanaNavegador *-- PanelPaginaActual
    VentanaNavegador ..> Resultado : interpreta
    PanelPila --|> PanelRedondeado
    PanelPaginaActual --|> PanelRedondeado
    PanelPila ..> Pagina : dibuja
    PanelPaginaActual ..> Pagina : dibuja
    VentanaNavegador ..> BotonPlano
    VentanaNavegador ..> Paleta
    PanelPila ..> IconoGlobo
    NavegadorConsola *-- Navegador : misma logica
    Main ..> VentanaNavegador : modo grafico
    Main ..> NavegadorConsola : modo --consola
```

> Si tu visor no dibuja diagramas Mermaid, aquí está la misma imagen ya renderizada:
> [`docs/uml/diagrama-clases.png`](docs/uml/diagrama-clases.png).

**Código fuente de los diagramas** (para abrirlos o editarlos en otra herramienta):

| Archivo | Formato | Dónde pegarlo |
|---|---|---|
| [`docs/uml/diagrama-clases.puml`](docs/uml/diagrama-clases.puml) | PlantUML | [plantuml.com/plantuml](https://www.plantuml.com/plantuml), extensión *PlantUML* de VS Code, draw.io (*Arrange → Insert → PlantUML*), Visual Paradigm, StarUML |
| [`docs/uml/diagrama-clases.mmd`](docs/uml/diagrama-clases.mmd) | Mermaid | [mermaid.live](https://mermaid.live), draw.io (*Arrange → Insert → Mermaid*), Notion, Obsidian, GitHub |
| [`docs/uml/diagrama-secuencia-back.puml`](docs/uml/diagrama-secuencia-back.puml) | PlantUML | igual que arriba |
| [`docs/uml/diagrama-secuencia-back.mmd`](docs/uml/diagrama-secuencia-back.mmd) | Mermaid | igual que arriba |

### Diagrama de secuencia — qué pasa al presionar **Back**

```mermaid
sequenceDiagram
    actor Usuario
    participant V as VentanaNavegador
    participant N as Navegador
    participant A as pilaAtras
    participant F as pilaAdelante

    Usuario->>V: clic en "Back"
    V->>N: atras()
    N->>A: estaVacia()
    A-->>N: false
    N->>F: apilarConDescarte(paginaActual)
    N->>A: desapilar()
    A-->>N: pagina anterior
    N-->>V: Resultado(EXITO, "Back: regresaste a ...")
    V->>V: actualizarVista()
    V-->>Usuario: pagina actual y pilas redibujadas

    Note over N,A: Si pilaAtras esta vacia, atras() no modifica nada<br/>y devuelve Resultado(ERROR, PILA_ATRAS_VACIA)
```

## Reglas de navegación implementadas

| Acción | Qué ocurre |
|---|---|
| **Visitar** una página nueva | La página actual se apila en la **pila atrás**, la nueva pasa a ser la actual y la **pila adelante se vacía**. |
| **Back** | La página actual se apila en la **pila adelante** y la cima de la **pila atrás** se convierte en la página actual. |
| **Forward** | La página actual se apila en la **pila atrás** y la cima de la **pila adelante** se convierte en la página actual. |
| **Exit** | Pide confirmación y cierra el navegador. |

Casos especiales, siempre con mensajes claros y sin que el programa falle:

- Back con la pila atrás vacía → aviso **PILA VACÍA** y no se modifica nada.
- Forward con la pila adelante vacía → aviso **PILA VACÍA** y no se modifica nada.
- Pila llena (capacidad 5) → aviso **PILA LLENA**; se descarta la página más antigua
  (el fondo de la pila) para poder guardar la más reciente.
- URL que no existe en el catálogo local → se muestra la página **404**.
- Campo de URL vacío → mensaje pidiendo escribir una dirección.

| Back | Forward | 404 |
|---|---|---|
| ![Back](docs/capturas/02-back.png) | ![Forward](docs/capturas/03-forward.png) | ![404](docs/capturas/04-error-404.png) |

| Pila vacía | Pila llena |
|---|---|
| ![Pila vacía](docs/capturas/05-pila-vacia.png) | ![Pila llena](docs/capturas/06-pila-llena.png) |

## Cómo ejecutar

Requisito: **JDK 8 o superior** (probado con JDK 21).

Primero clona el repositorio y entra a la carpeta; el script vive dentro del proyecto:

```bash
git clone -b explicacion_codigo https://github.com/diegoanahuac/Navegador_Web_E5.git
cd Navegador_Web_E5
```

Y ya dentro de la carpeta:

Linux / macOS:

```bash
./ejecutar.sh              # interfaz gráfica
./ejecutar.sh --consola    # versión de consola con menú
```

Windows (en PowerShell hay que anteponer `.\`):

```powershell
.\ejecutar.bat
.\ejecutar.bat --consola
```

Manualmente:

```bash
javac -encoding UTF-8 -d out src/navegador/*.java src/navegador/*/*.java
java -cp out navegador.Main
```

## Uso de la interfaz

1. Escribe una URL en la barra de direcciones (o haz clic en uno de los *chips* de
   **Sitios locales**) y presiona **Visitar** o la tecla **Enter**.
2. Usa **← Back** y **Forward →** para moverte por el historial
   (atajos: `Alt + ←` y `Alt + →`).
3. Las dos pilas se dibujan abajo: la casilla **1** es el fondo de la pila y la casilla
   marcada con **▲ cima** es el siguiente elemento que saldría.
4. **✖ Exit** cierra la aplicación.

Sitios disponibles en el catálogo local: `google.com`, `mozilla.org`, `openai.com`,
`stackoverflow.com`, `github.com`, `merida.anahuac.mx`, `wikipedia.org`, `youtube.com`,
`java.com`.

## Tema visual

La interfaz usa una base **beige** y un **único color de acento violeta neón**
(`#6D28F5`), definido en `ui/Paleta.java`. Toda la aplicación —encabezado, botones,
bordes, números de las casillas, avisos y mensajes de estado— usa ese mismo tono;
`ACENTO_PROFUNDO` y `ACENTO_TENUE` son el mismo color más oscuro (solo para el
*hover* de los botones) y más claro (relleno de la cima). Para cambiar todo el
tema basta con modificar esas tres constantes.

## Estructura del proyecto

```
src/navegador/
├── Main.java                      Punto de entrada (gráfico o --consola)
├── modelo/
│   ├── Pila.java                  Pila genérica LIFO de capacidad fija (arreglo)
│   ├── Pagina.java                Página simulada (URL, título, descripción, 404)
│   ├── CatalogoLocal.java         Sitios locales y normalización de URLs
│   ├── Resultado.java             Mensaje y aviso devueltos por cada operación
│   └── Navegador.java             Lógica del historial con las dos pilas
├── ui/
│   ├── VentanaNavegador.java      Ventana principal
│   ├── PanelPaginaActual.java     Tarjeta de la página actual / 404
│   ├── PanelPila.java             Dibujo de una pila y sus avisos
│   ├── Paleta.java                Color: base beige + un único acento violeta
│   ├── PanelRedondeado.java       Panel con esquinas redondeadas
│   ├── BotonPlano.java            Botón plano redondeado
│   └── IconoGlobo.java            Icono de globo dibujado en código
└── consola/
    └── NavegadorConsola.java      Versión de consola con menú (misma lógica)
```

La clase `Pila` es propia (no usa `java.util.Stack`) e implementa las operaciones
clásicas: `apilar`, `desapilar`, `cima`, `estaVacia`, `estaLlena`, `tamano` y `vaciar`,
más `apilarConDescarte` para el manejo del desbordamiento.
