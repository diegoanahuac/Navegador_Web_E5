# Explicación completa del proyecto — Navegador Web con Pilas (LIFO)

> **Cómo usar este documento:** está pensado para dos cosas.
> 1. Leerlo de arriba hacia abajo para entender el proyecto completo.
> 2. Subirlo a Claude (o a cualquier asistente) y preguntarle cosas como
>    *"¿qué hace `apilarConDescarte`?"*, *"¿por qué la pila usa `Object[]` y no `T[]`?"*,
>    *"explícame qué pasa cuando presiono Back"*. El documento contiene el código real
>    y la explicación de **todos** los métodos, así que el asistente puede responder
>    sin necesidad de ver el repositorio.
>
> Proyecto: aplicación de escritorio en **Java + Swing** que simula un navegador web.
> Todo el historial se maneja con **dos pilas** (estructura LIFO) escritas a mano.
> No hay internet, no hay base de datos, no hay librerías externas.

---

## Índice

0. [Antes de empezar: clonar y ejecutar el proyecto](#0-antes-de-empezar-clonar-y-ejecutar-el-proyecto)
1. [Resumen en 30 segundos](#1-resumen-en-30-segundos)
2. [Fases de construcción del proyecto](#2-fases-de-construcción-del-proyecto)
3. [Arquitectura: cómo se conectan las clases](#3-arquitectura-cómo-se-conectan-las-clases)
4. [Paquete `modelo` — la lógica](#4-paquete-modelo--la-lógica)
   - [4.1 `Pila.java`](#41-pilajava--el-corazón-del-proyecto)
   - [4.2 `Pagina.java`](#42-paginajava--el-dato-que-se-apila)
   - [4.3 `CatalogoLocal.java`](#43-catalogolocaljava--el-internet-falso)
   - [4.4 `Resultado.java`](#44-resultadojava--la-respuesta-de-cada-operación)
   - [4.5 `Navegador.java`](#45-navegadorjava--las-reglas-del-historial)
5. [Paquete `ui` — la interfaz](#5-paquete-ui--la-interfaz)
6. [`Main.java` y la versión de consola](#6-mainjava-y-la-versión-de-consola)
7. [Trazas paso a paso (qué pasa en cada clic)](#7-trazas-paso-a-paso-qué-pasa-en-cada-clic)
8. [Preguntas que les pueden hacer (y sus respuestas)](#8-preguntas-que-les-pueden-hacer-y-sus-respuestas)
9. [Glosario de Java usado en el proyecto](#9-glosario-de-java-usado-en-el-proyecto)
10. [Dónde tocar el código para cambiar cosas](#10-dónde-tocar-el-código-para-cambiar-cosas)

---

## 0. Antes de empezar: clonar y ejecutar el proyecto

**Orden obligatorio: primero `git clone`, luego `cd`, y hasta entonces el script.**
El `ejecutar.bat` / `ejecutar.sh` vive *dentro* del repositorio: no existe en tu
computadora hasta que descargas el proyecto, y solo funciona si la terminal está
parada dentro de la carpeta `Navegador_Web_E5`.

### Requisito único

Tener instalado un **JDK 8 o superior**. Compruébalo con:

```
javac -version
```

Si responde algo como `javac 21.0.2`, estás listo. Si dice *"no se reconoce el
comando"*, instala el JDK (por ejemplo Temurin u Oracle JDK) y vuelve a abrir la
terminal.

### Paso 1 — Clonar el repositorio

Colócate en la carpeta donde quieras guardar el proyecto y clona la rama:

```
git clone -b explicacion_codigo https://github.com/diegoanahuac/Navegador_Web_E5.git
```

> `-b explicacion_codigo` descarga la rama que incluye este documento. La rama
> principal `main` trae el mismo código pero sin este archivo; si ya clonaste sin
> `-b`, cámbiate con `git fetch origin` y `git checkout explicacion_codigo`.

### Paso 2 — Entrar a la carpeta del proyecto

```
cd Navegador_Web_E5
```

Este paso **no es opcional**: si ejecutas el script desde afuera, la terminal no
encuentra el archivo. Para confirmar que estás en el lugar correcto, lista el
contenido (`dir` en Windows, `ls` en Linux/macOS): deben aparecer `src`,
`ejecutar.bat`, `ejecutar.sh` y `README.md`.

### Paso 3 — Ejecutar

**Windows (PowerShell):** hay que escribir `.\` antes del nombre; PowerShell no
ejecuta archivos de la carpeta actual sin ese prefijo.

```
.\ejecutar.bat              # interfaz gráfica
.\ejecutar.bat --consola    # versión de consola con menú
```

**Linux / macOS (o Git Bash en Windows):**

```
chmod +x ejecutar.sh        # solo la primera vez
./ejecutar.sh               # interfaz gráfica
./ejecutar.sh --consola     # versión de consola con menú
```

El script compila con `javac` y abre la ventana. Todo junto se ve así:

```
git clone -b explicacion_codigo https://github.com/diegoanahuac/Navegador_Web_E5.git
cd Navegador_Web_E5
.\ejecutar.bat
```

### Si algo falla

| Mensaje | Causa | Solución |
|---|---|---|
| `El término 'ejecutar.bat' no se reconoce...` | Falta el prefijo `.\` en PowerShell | Escribe `.\ejecutar.bat` |
| `No such file or directory` / `no se encuentra la ruta` | La terminal no está dentro de la carpeta del proyecto | Haz `cd Navegador_Web_E5` |
| `javac: command not found` | No hay JDK instalado (o no está en el PATH) | Instala el JDK y reabre la terminal |
| `Permission denied` al correr `./ejecutar.sh` | Falta el permiso de ejecución | `chmod +x ejecutar.sh` |

### Sin usar los scripts

Los scripts solo son un atajo. Estando dentro de la carpeta, esto hace exactamente
lo mismo:

```
javac -encoding UTF-8 -d out src/navegador/*.java src/navegador/*/*.java
java -cp out navegador.Main
```

---

## 1. Resumen en 30 segundos

El programa guarda **una página actual** y **dos pilas**:

```
        PILA ATRÁS                PÁGINA ACTUAL              PILA ADELANTE
   (páginas ya visitadas)                              (páginas de las que regresé)
        ┌──────────┐              ┌──────────┐              ┌──────────┐
  cima →│ mozilla  │              │  openai  │              │  (vacía) │
        │ google   │              └──────────┘              └──────────┘
        └──────────┘
```

Tres reglas, y nada más:

| Acción | Qué se hace con la página actual | De dónde sale la nueva actual | Efecto extra |
|---|---|---|---|
| **Visitar** | se **apila** en *atrás* | del catálogo local | la pila *adelante* **se vacía** |
| **Back** | se **apila** en *adelante* | se **desapila** de *atrás* | — |
| **Forward** | se **apila** en *atrás* | se **desapila** de *adelante* | — |

Todo lo demás (la ventana, los colores, los mensajes) es presentación de esas tres reglas.

---

## 2. Fases de construcción del proyecto

El proyecto se construyó **de adentro hacia afuera**: primero el dato, luego la
estructura, luego la lógica, luego la interfaz. Esta es la razón de cada fase.

### Fase 1 — Definir *qué* se va a apilar → `modelo/Pagina.java`
Antes de programar la pila hay que saber qué guarda. Una pila de `String` (solo la URL)
habría bastado, pero se creó la clase `Pagina` para poder mostrar también un título,
una descripción y si la página existe o es un **404**.

### Fase 2 — Construir la estructura de datos → `modelo/Pila.java`
Es el objetivo académico del proyecto, así que **no se usó `java.util.Stack`**: la pila
está escrita a mano sobre un arreglo, con sus operaciones clásicas (`apilar`,
`desapilar`, `cima`, `estaVacia`, `estaLlena`, `vaciar`). Es **genérica** (`Pila<T>`),
así que sirve para cualquier tipo; el navegador la usa como `Pila<Pagina>`.

### Fase 3 — Simular el internet → `modelo/CatalogoLocal.java`
Como todo es local, se necesitaba algo que "responda" cuando se escribe una URL. Es un
mapa de sitios conocidos; si la URL no está, devuelve una página **404**. También
normaliza lo que el usuario escribe (`google.com` → `https://www.google.com`).

### Fase 4 — Las reglas del historial → `modelo/Navegador.java` + `modelo/Resultado.java`
Aquí viven las tres reglas de la tabla anterior. `Navegador` **no sabe nada de ventanas**:
recibe texto, mueve páginas entre las dos pilas y devuelve un objeto `Resultado` con el
mensaje y el aviso que corresponda. Así la misma lógica sirve para la interfaz gráfica
y para la consola.

### Fase 5 — Probar sin interfaz → `consola/NavegadorConsola.java`
Una versión con menú de texto (`1 Visitar, 2 Back, 3 Forward, 4 Exit`) que usa
exactamente el mismo `Navegador`. Sirvió para verificar que las pilas se comportan bien
antes de invertir tiempo en la ventana, y sigue en el proyecto como prueba rápida.

### Fase 6 — La interfaz → paquete `ui`
Se armó por capas:
1. `Paleta.java` — colores y tipografía en un solo lugar.
2. `PanelRedondeado`, `BotonPlano`, `IconoGlobo` — piezas visuales reutilizables,
   dibujadas a mano con `Graphics2D` (sin imágenes ni librerías).
3. `PanelPila` y `PanelPaginaActual` — los bloques que muestran el estado del modelo.
4. `VentanaNavegador` — arma la ventana, conecta los botones con el `Navegador` y
   después de cada acción **vuelve a leer el modelo y repinta todo**.

### Fase 7 — Empaquetado → `Main.java`, `ejecutar.sh`, `ejecutar.bat`, `README.md`
Un único punto de entrada (`Main`) que abre la ventana, o la consola si se pasa
`--consola`, más scripts que compilan y ejecutan con un solo comando.

### Fase 8 — Ajuste visual
La paleta se cambió a **beige + un único acento violeta** (`#6D28F5`). Como todos los
colores salen de `Paleta.java`, fue cambiar tres constantes y toda la app cambió.

---

## 3. Arquitectura: cómo se conectan las clases

> El **diagrama de clases UML** completo está en el [README](README.md#diagrama-de-clases-uml),
> y su código fuente (PlantUML y Mermaid) en la carpeta `docs/uml/`.

```
                    ┌──────────────────────────┐
  clic del usuario  │    ui/VentanaNavegador   │  (Swing: ventana, botones, campos)
  ─────────────────>│  visitar() retroceder()  │
                    │  avanzar()  salir()      │
                    └───────────┬──────────────┘
                                │ llama y recibe un Resultado
                                v
                    ┌──────────────────────────┐
                    │    modelo/Navegador      │  (las 3 reglas del historial)
                    │  pilaAtras  pilaAdelante │
                    │  paginaActual            │
                    └───┬───────────┬──────────┘
                        │           │
              usa       │           │  consulta
                        v           v
            ┌────────────────┐   ┌──────────────────────┐
            │  modelo/Pila   │   │ modelo/CatalogoLocal │
            │  (LIFO propia) │   │  (sitios simulados)  │
            └───────┬────────┘   └──────────┬───────────┘
                    │ guarda                │ devuelve
                    v                       v
                            ┌────────────────┐
                            │ modelo/Pagina  │
                            └────────────────┘

  El Navegador devuelve   modelo/Resultado  ──> la ventana lo convierte en
  (mensaje + tipo + aviso)                      texto de la barra de estado y
                                                en el cartel PILA LLENA / VACÍA
```

**Regla de oro del diseño:** el paquete `modelo` **no importa nada de Swing**. Si mañana
quieren hacer la misma práctica en web o en consola pura, el paquete `modelo` se
reutiliza tal cual.

---

## 4. Paquete `modelo` — la lógica

### 4.1 `Pila.java` — el corazón del proyecto

Es una **pila genérica de capacidad fija implementada sobre un arreglo**.

#### Los dos campos que lo sostienen todo

```java
private final Object[] elementos;   // el arreglo donde se guardan los datos
private int tope;                   // cuántos elementos hay (y dónde va el siguiente)
```

- `tope` es **la cantidad de elementos**, no el índice del último. El elemento de la
  cima está en `elementos[tope - 1]` y el siguiente que se apile va en `elementos[tope]`.
- Por eso `estaVacia()` es `tope == 0` y `estaLlena()` es `tope == elementos.length`.

> **¿Por qué `Object[]` y no `T[]`?** Java no permite escribir `new T[capacidad]` por el
> *type erasure* (en tiempo de ejecución el tipo genérico desaparece). La solución
> estándar es guardar en `Object[]` y hacer el *cast* a `T` al leer, marcando el método
> con `@SuppressWarnings("unchecked")`. Es seguro porque solo esta clase mete y saca
> elementos del arreglo.

#### Constantes y constructores

```java
public static final int CAPACIDAD_POR_DEFECTO = 5;

public Pila()               { this(CAPACIDAD_POR_DEFECTO); }
public Pila(int capacidad)  { valida > 0; elementos = new Object[capacidad]; tope = 0; }
```

- `CAPACIDAD_POR_DEFECTO = 5` es el tamaño que se ve en la interfaz (las 5 casillas).
- El constructor con capacidad lanza `IllegalArgumentException` si le pasan 0 o negativo:
  una pila de tamaño cero no tiene sentido y es mejor fallar de inmediato.

#### Métodos, uno por uno

| Método | Qué hace | Detalle importante |
|---|---|---|
| `apilar(T)` | Mete un elemento en la cima | Lanza `IllegalStateException` si está llena. **El navegador no lo usa** (usa el de abajo), pero está porque es la operación clásica de una pila |
| `apilarConDescarte(T)` | Igual, pero si está llena **descarta el más antiguo** | Devuelve `true` si hubo descarte → así la interfaz sabe cuándo mostrar *PILA LLENA* |
| `desapilar()` | Saca y devuelve el elemento de la cima | Lanza `IllegalStateException` si está vacía; pone `null` en la posición liberada |
| `cima()` | Mira la cima **sin sacarla** | Devuelve `null` si está vacía |
| `estaVacia()` | `tope == 0` | Lo consulta el `Navegador` **antes** de desapilar, para nunca provocar la excepción |
| `estaLlena()` | `tope == elementos.length` | |
| `tamano()` | Cuántos elementos hay | Se muestra como "3 / 5" en la interfaz |
| `capacidad()` | Tamaño máximo | Se usa en los mensajes ("Pila atrás llena (5)") |
| `vaciar()` | Deja la pila en cero | Se llama al visitar una página nueva (regla del enunciado) |
| `elementosDesdeElFondo()` | Copia en una `List`, del fondo a la cima | La interfaz la usa para dibujar; al ser **copia**, nadie puede modificar la pila desde afuera |
| `toString()` | Texto de la pila | Para depurar |

#### Los tres métodos que hay que saber explicar

**`apilar`** — la operación clásica *push*:

```java
public void apilar(T elemento) {
    if (elemento == null)  throw new IllegalArgumentException(...);
    if (estaLlena())       throw new IllegalStateException("Pila llena: ...");
    elementos[tope++] = elemento;   // guarda en 'tope' y DESPUÉS incrementa
}
```

**`desapilar`** — la operación clásica *pop*:

```java
public T desapilar() {
    if (estaVacia()) throw new IllegalStateException("Pila vacía: ...");
    T elemento = (T) elementos[--tope];  // PRIMERO decrementa, luego lee
    elementos[tope] = null;              // libera la referencia (ayuda al recolector de basura)
    return elemento;
}
```

**`apilarConDescarte`** — la variante que usa el navegador:

```java
public boolean apilarConDescarte(T elemento) {
    boolean desbordo = false;
    if (estaLlena()) {
        System.arraycopy(elementos, 1, elementos, 0, tope - 1); // recorre todo 1 lugar abajo
        tope--;                                                 // se perdió el del fondo
        desbordo = true;
    }
    elementos[tope++] = elemento;
    return desbordo;
}
```

Visualmente, con capacidad 5 y la pila llena, al apilar `F`:

```
   antes            arraycopy(1→0)          después de apilar F
  [A B C D E]   →   [B C D E _]  tope=4  →  [B C D E F]  tope=5   (se perdió A)
   ↑ fondo                                              ↑ cima
```

> **Decisión de diseño que pueden defender:** cuando la pila se llena hay dos caminos:
> (a) bloquear la navegación, o (b) descartar la página más antigua. Se eligió **(b)**
> porque un navegador real nunca deja de funcionar por tener historial lleno; el aviso
> *PILA LLENA* se muestra para que el usuario sepa que se perdió la más vieja.

---

### 4.2 `Pagina.java` — el dato que se apila

Clase **inmutable**: los cuatro campos son `final`, así que una página nunca cambia
después de crearse (esto evita errores: la misma página puede estar referenciada desde
la pila y desde la vista sin riesgo).

```java
private final String url, titulo, descripcion;
private final boolean encontrada;   // false = es un 404
```

| Método | Qué hace |
|---|---|
| `Pagina(url, titulo, descripcion)` | Constructor **público**: crea una página que sí existe (`encontrada = true`) |
| `Pagina(url, titulo, descripcion, encontrada)` | Constructor **privado**: el único que puede marcar `encontrada = false` |
| `static noEncontrada(url)` | *Fábrica* que arma la página **404** (`titulo = "404"`) |
| `getUrl()`, `getTitulo()`, `getDescripcion()` | Lectura de los datos |
| `fueEncontrada()` | `true` si la página existe en el catálogo; la vista lo usa para pintar el 404 |
| `equals(Object)` | Dos páginas son iguales si su URL es igual **ignorando mayúsculas** |
| `hashCode()` | Coherente con `equals` (se calcula sobre la URL en minúsculas) |
| `toString()` | Devuelve la URL (por eso los `System.out.println` de la consola se ven limpios) |

> **¿Por qué el constructor con `encontrada` es privado?** Para que nadie pueda crear por
> error una página "que no existe" desde fuera. La única forma de obtener un 404 es
> llamando a `Pagina.noEncontrada(...)`, y eso solo lo hace el catálogo.

---

### 4.3 `CatalogoLocal.java` — el "internet" falso

```java
private final Map<String, Pagina> sitios = new LinkedHashMap<>();
```

Se usa `LinkedHashMap` (no `HashMap`) porque **conserva el orden de inserción**: así los
botones de *Sitios locales* en la interfaz salen en el mismo orden en que se registraron.

| Método | Qué hace |
|---|---|
| `CatalogoLocal()` | El constructor registra los 9 sitios simulados (google, mozilla, openai, stackoverflow, github, merida.anahuac.mx, wikipedia, youtube, java) |
| `registrar(url, titulo, descripcion)` *(privado)* | Crea la `Pagina` y la mete al mapa usando `clave(url)` |
| `abrir(urlNormalizada)` | Busca en el mapa. **Si no encuentra, devuelve `Pagina.noEncontrada(url)`** — nunca devuelve `null`, por eso el navegador jamás truena con una URL rara |
| `urlsDisponibles()` | Lista de URLs, para dibujar los *chips* de sugerencias |
| `static normalizar(String)` | Limpia lo que escribió el usuario |
| `static clave(String)` *(privado)* | Construye la llave de búsqueda del mapa |

**`normalizar`** — convierte lo que el usuario teclea en una URL presentable:

```java
url = texto.trim();                              // quita espacios
if (url.isEmpty()) return "";                    // vacío → el Navegador avisa
if (!url.contains("://")) url = "https://" + url;// "google.com" → "https://google.com"
while (url.endsWith("/")) url = url.substring(0, url.length() - 1);  // quita "/" finales
```

**`clave`** — hace que varias formas de escribir lo mismo encuentren el mismo sitio:

```java
clave = normalizar(url).toLowerCase()
        .replaceFirst("^https?://", "")   // quita http:// o https://
        .replaceFirst("^www\\.", "");     // quita www.
```

Por eso `google.com`, `www.google.com`, `HTTPS://WWW.GOOGLE.COM` y `https://google.com/`
son **la misma llave**: `google.com`.

> Nota: `normalizar` y `clave` son `static` porque no dependen de ningún dato del objeto;
> son funciones puras de texto. Por eso el `Navegador` puede llamar
> `CatalogoLocal.normalizar(...)` sin instanciar nada.

---

### 4.4 `Resultado.java` — la respuesta de cada operación

Es un objeto pequeño que responde tres preguntas: **¿salió bien?**, **¿qué le digo al
usuario?** y **¿tengo que resaltar alguna pila?**

```java
public enum Tipo  { EXITO, AVISO, ERROR }                    // tono del mensaje
public enum Aviso { NINGUNO, PILA_ATRAS_LLENA, PILA_ADELANTE_LLENA,
                    PILA_ATRAS_VACIA, PILA_ADELANTE_VACIA }  // cartel a mostrar
```

| Método | Qué hace |
|---|---|
| `Resultado(tipo, mensaje, aviso)` | Constructor general |
| `static exito(mensaje)` | Atajo para un resultado correcto, sin aviso |
| `static aviso(mensaje, aviso)` | Operación que sí se hizo, pero con algo que advertir (pila llena, 404) |
| `static error(mensaje, aviso)` | Operación que **no** se pudo hacer (pila vacía, URL vacía) |
| `getTipo()`, `getMensaje()`, `getAviso()` | Lectura |

> **¿Por qué existe esta clase?** Para que `Navegador` no tenga que llamar a
> `JOptionPane` ni pintar nada. El modelo **describe** lo que pasó; la vista **decide**
> cómo mostrarlo. Es lo que permite que la versión de consola y la gráfica compartan
> la misma lógica.

---

### 4.5 `Navegador.java` — las reglas del historial

```java
private final Pila<Pagina> pilaAtras;      // páginas anteriores
private final Pila<Pagina> pilaAdelante;   // páginas a las que puedo avanzar
private final CatalogoLocal catalogo = new CatalogoLocal();
private Pagina paginaActual;               // empieza en null: no hay página abierta
```

#### `visitar(String textoUrl)` — regla 1

```java
String url = CatalogoLocal.normalizar(textoUrl);
if (url.isEmpty()) return Resultado.error("Escribe una URL...", NINGUNO);   // 1

Pagina nueva = catalogo.abrir(url);                                          // 2
boolean desbordo = false;
if (paginaActual != null) desbordo = pilaAtras.apilarConDescarte(paginaActual); // 3
paginaActual = nueva;                                                        // 4
pilaAdelante.vaciar();                                                       // 5

if (desbordo)               return Resultado.aviso("Pila atrás llena...", PILA_ATRAS_LLENA);
if (!nueva.fueEncontrada()) return Resultado.aviso("Error 404...", NINGUNO);
return Resultado.exito("Visitando " + url + " — la pila adelante se vació.");
```

1. Si el campo está vacío no se navega: se devuelve un error amable.
2. Se resuelve la página (existente o 404).
3. **La página actual se guarda en la pila atrás.** El `if` es para la primera visita,
   cuando todavía no hay página actual (no se apila `null`).
4. La nueva pasa a ser la actual.
5. **La pila adelante se vacía**, tal como pide el enunciado: al tomar un camino nuevo,
   el "futuro" que existía deja de tener sentido.

#### `atras()` — regla 2 (Back)

```java
if (pilaAtras.estaVacia())
    return Resultado.error("No hay páginas anteriores...", PILA_ATRAS_VACIA);  // no cambia nada

if (paginaActual != null) desbordo = pilaAdelante.apilarConDescarte(paginaActual);
paginaActual = pilaAtras.desapilar();
```

La comprobación `estaVacia()` **antes** de `desapilar()` es lo que cumple el requisito
"evitar errores cuando no existan páginas disponibles": nunca se llega a la excepción,
el usuario solo ve el cartel *PILA VACÍA*.

#### `adelante()` — regla 3 (Forward)

Es el espejo exacto de `atras()`: revisa `pilaAdelante.estaVacia()`, apila la actual en
**atrás** y desapila de **adelante**.

#### Métodos de consulta

| Método | Qué hace |
|---|---|
| `getPaginaActual()` | La página que se está viendo (o `null` al arrancar) |
| `getPilaAtras()`, `getPilaAdelante()` | Dan acceso a las pilas para que la vista las dibuje |
| `getCatalogo()` | Lo usa la ventana para armar los *chips* de sitios |
| `puedeRetroceder()`, `puedeAvanzar()` | `true` si la pila correspondiente tiene algo. **Hoy la interfaz no los usa** (los botones quedan siempre activos a propósito, para que al presionarlos aparezca el mensaje *PILA VACÍA*). Están disponibles si algún día quieren deshabilitar los botones |

---

## 5. Paquete `ui` — la interfaz

Toda la interfaz es **Swing puro** (viene con Java, no se descargó nada) y todo lo que se
ve dibujado —esquinas redondeadas, botones, el globo terráqueo— está **pintado a mano**
con `Graphics2D`. No hay imágenes en el proyecto salvo las capturas del README.

### 5.1 `Paleta.java` — colores y tipografía en un solo lugar

Clase de utilidades: **todos los miembros son `static`** y el constructor es privado
(nadie debe crear un objeto `Paleta`).

```java
public static final Color FONDO   = new Color(0xEFE3D0);   // beige de la ventana
public static final Color TARJETA = new Color(0xFBF4E9);   // beige claro de las tarjetas
public static final Color BORDE   = new Color(0xD9C3A5);   // líneas suaves

public static final Color ACENTO          = new Color(0x6D28F5);  // violeta (único acento)
public static final Color ACENTO_PROFUNDO = new Color(0x4C16B8);  // el mismo, más oscuro (hover)
public static final Color ACENTO_TENUE    = new Color(0xE4D9FC);  // el mismo, muy claro (relleno)
```

| Método | Qué hace |
|---|---|
| `fuente(int estilo, int tamano)` | Devuelve un `Font` con la familia elegida. Todas las clases piden aquí su tipografía, así toda la app se ve consistente |
| `elegirFamilia()` *(privado, static)* | Pregunta al sistema qué fuentes hay instaladas (`GraphicsEnvironment`) y elige la primera disponible de una lista (Segoe UI, Inter, Helvetica Neue, DejaVu Sans, Liberation Sans, Arial); si no hay ninguna, usa la genérica `SansSerif`. **Esto evita que el proyecto se vea distinto en Windows, Mac y Linux** |

### 5.2 `PanelRedondeado.java` — el panel con esquinas redondeadas

`JPanel` normal no tiene esquinas redondeadas, así que se hereda y se redibuja el fondo.

```java
@Override
protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();                     // copia del lápiz
    g2.setRenderingHint(ANTIALIASING, ON);                       // bordes suaves
    g2.setColor(colorFondo);  g2.fillRoundRect(...);             // relleno
    g2.setColor(colorBorde);  g2.drawRoundRect(...);             // contorno
    g2.dispose();                                                // se libera la copia
    super.paintComponent(g);
}
```

| Miembro | Qué hace |
|---|---|
| `PanelRedondeado(radio, colorFondo, colorBorde)` | Constructor. Hace `setOpaque(false)` para que Swing **no** pinte el rectángulo cuadrado de fondo y se vea la esquina redondeada |
| `setColorFondo`, `setColorBorde`, `setGrosorBorde` | Cambian el aspecto y llaman `repaint()` (se usan para resaltar la casilla de la cima) |
| `paintComponent(Graphics)` | El dibujo explicado arriba |

> Detalle de Swing: siempre se trabaja sobre `g.create()` y se llama `g2.dispose()`, para
> no dejar configurado el antialias o el color para otros componentes.

### 5.3 `BotonPlano.java` — los botones

Hereda de `JButton` y apaga el pintado por defecto para dibujar el suyo:

```java
setContentAreaFilled(false);  // que Swing no pinte el fondo estándar
setBorderPainted(false);      // ni el borde estándar
setFocusPainted(false);       // ni el recuadro de foco
setCursor(HAND_CURSOR);       // manita al pasar encima
```

| Miembro | Qué hace |
|---|---|
| `BotonPlano(texto, fondo, fondoHover, borde, colorTexto, radio)` | Constructor general. Registra un `MouseAdapter` que enciende/apaga la bandera `encima` y llama `repaint()` → ese es el efecto *hover* |
| `static solido(texto)` | Botón principal: relleno violeta, texto claro (**Visitar**, **Exit**) |
| `static contorno(texto)` | Botón secundario: beige con contorno violeta (**Back**, **Forward**) |
| `static salida(texto)` | Devuelve `solido(texto)`: el botón Exit usa exactamente el mismo estilo (paleta uniforme) |
| `paintComponent(Graphics)` | Elige el color según el estado (`pressed` / `hover` / normal), pinta el rectángulo redondeado y **luego** llama `super.paintComponent(g)` para que `JButton` escriba el texto encima |

### 5.4 `IconoGlobo.java` — el globo terráqueo

Implementa la interfaz `Icon`, así que se puede usar en cualquier `JLabel` o botón con
`setIcon(...)`. Está dibujado con óvalos y arcos:

| Método | Qué hace |
|---|---|
| `IconoGlobo(tamano, color)` | Guarda tamaño y color |
| `paintIcon(Component, Graphics, x, y)` | Dibuja el círculo exterior, el ecuador, el meridiano central (un óvalo angosto) y dos arcos como paralelos |
| `getIconWidth()`, `getIconHeight()` | Swing pregunta cuánto mide para reservar el espacio |

### 5.5 `PanelPila.java` — el dibujo de una pila

Es el componente más interesante de la interfaz. Hereda de `PanelRedondeado` y muestra
**una** pila: sus casillas numeradas y, cuando toca, el cartel *PILA LLENA* / *PILA VACÍA*.

#### Cómo está armado

```java
private final Fila[] filas;                       // filas[0] = FONDO de la pila
private final CardLayout cartas = new CardLayout();
private final JPanel contenedor = new JPanel(cartas);   // dos "tarjetas" superpuestas
private final Timer temporizadorAviso;                  // 2600 ms
```

`CardLayout` permite tener dos paneles en el mismo espacio y mostrar solo uno:
la **tarjeta de casillas** (lo normal) y la **tarjeta de aviso** (el cartel).

En el constructor las filas se agregan **en orden inverso**:

```java
for (int i = capacidad - 1; i >= 0; i--) { filas[i] = new Fila(i + 1); casillas.add(filas[i]); }
```

Como `GridLayout` coloca de arriba hacia abajo, empezar en la última posición hace que la
casilla **1 quede hasta abajo** (el fondo de la pila) y la 5 hasta arriba — igual que se
dibuja una pila en clase.

| Método | Qué hace |
|---|---|
| `PanelPila(titulo, capacidad, acento)` | Arma el encabezado, las 5 filas, la tarjeta de aviso y el `Timer` |
| `construirEncabezado(titulo)` *(privado)* | Título con el icono del globo a la izquierda y el contador "3 / 5" a la derecha |
| `actualizar(List<Pagina> desdeElFondo)` | **El método clave.** Recibe el contenido real de la pila y recorre las 5 filas: si hay dato, `fila.mostrar(url, esCima)`; si no, `fila.limpiar()`. Marca como cima el último elemento de la lista y actualiza el contador |
| `mostrarAviso(String texto)` | Cambia a la tarjeta del cartel y arranca el `Timer`; a los 2.6 s el `Timer` vuelve solo a las casillas |
| *(clase interna)* `Fila` | Una casilla: el círculo con el número + la caja redondeada con la URL |
| `Fila.mostrar(url, esCima)` | Escribe la URL; si es la cima, la pone en negrita, le agrega "▲ cima", borde más grueso y relleno `ACENTO_TENUE` |
| `Fila.limpiar()` | Deja la casilla vacía con su color normal |

> El número dentro del círculo se dibuja con un `JPanel` anónimo que sobreescribe
> `paintComponent`: pinta el óvalo y centra el texto usando `FontMetrics` (mide el ancho
> de la cadena para calcular la posición exacta).

### 5.6 `PanelPaginaActual.java` — la tarjeta de la página abierta

| Método | Qué hace |
|---|---|
| `PanelPaginaActual()` | Arma la tarjeta: globo grande a la izquierda y, al centro, título + descripción agrupados con `BoxLayout` |
| `mostrar(Pagina pagina)` | Tiene **tres casos**: <br>• `pagina == null` (al arrancar) → "Sin página abierta" + instrucción <br>• `!pagina.fueEncontrada()` → muestra **404** y la URL que falló <br>• normal → la URL en grande y la descripción del sitio |

### 5.7 `VentanaNavegador.java` — la ventana y el pegamento

Hereda de `JFrame`. Tiene **un solo `Navegador`** y los componentes que lo muestran:

```java
private final Navegador navegador = new Navegador(Pila.CAPACIDAD_POR_DEFECTO);
private final JTextField campoUrl;
private final PanelPaginaActual panelPaginaActual;
private final PanelPila panelAtras, panelAdelante;
private final JLabel barraEstado;
```

#### Métodos que construyen la interfaz

| Método | Qué hace |
|---|---|
| `VentanaNavegador()` | Título, tamaño mínimo, arma el panel raíz con `BorderLayout` (encabezado arriba, tarjeta al centro, barra de estado abajo), registra los atajos, pinta el estado inicial y centra la ventana con `setLocationRelativeTo(null)` |
| `construirEncabezado()` | La barra violeta con "PROYECTO NAVEGADOR WEB" y el subtítulo |
| `construirCentro()` | La tarjeta grande: arriba la barra de URL + botones + chips, al centro la página actual, abajo las dos pilas en un `GridLayout(1, 2)` |
| `construirBarraUrl()` | Etiqueta "URL:", el `JTextField` dentro de una caja redondeada y el botón **Visitar**. `campoUrl.addActionListener(e -> visitar())` hace que **Enter** también navegue |
| `construirBotones()` | Los tres botones: **← Back**, **Forward →**, **✖ Exit**, cada uno conectado con su método |
| `construirSugerencias()` | Recorre `navegador.getCatalogo().urlsDisponibles()` y crea un *chip* por sitio; al hacer clic copia la URL al campo |
| `construirPie()` | La barra de estado (un `JLabel` dentro de un panel redondeado) |
| `registrarAtajos()` | Registra `Alt + ←` y `Alt + →` usando `InputMap` / `ActionMap`, el mecanismo estándar de Swing para atajos de teclado |

#### Métodos que responden a las acciones

| Método | Qué hace |
|---|---|
| `visitar()` | `procesar(navegador.visitar(campoUrl.getText()))` |
| `retroceder()` | `procesar(navegador.atras())` |
| `avanzar()` | `procesar(navegador.adelante())` |
| `salir()` | Muestra un `JOptionPane` de confirmación; si aceptan, `dispose()` y `System.exit(0)` |
| `procesar(Resultado)` | **El método central de la vista.** Hace tres cosas: (1) `actualizarVista()`, (2) `mostrarMensaje(resultado)`, (3) un `switch` sobre `resultado.getAviso()` que muestra *PILA LLENA* / *PILA VACÍA* en el panel que corresponda |
| `actualizarVista()` | Vuelve a **leer el modelo** y repinta: la página actual, las dos pilas (`elementosDesdeElFondo()`) y el texto del campo URL |
| `mostrarMensaje(Resultado)` | Antepone un símbolo según el `Tipo` (`✓` éxito, `⚑` aviso, `⚠` error) y lo escribe en la barra de estado |

> **Patrón que conviene mencionar en la exposición:** la vista **nunca** calcula nada del
> historial. Después de cada acción hace *"pregúntale al modelo cómo quedó todo y
> vuelve a dibujar"*. Por eso es imposible que lo que se ve en pantalla se desincronice
> de las pilas reales.

---

## 6. `Main.java` y la versión de consola

### `Main.java`

| Parte | Qué hace |
|---|---|
| `main(String[] args)` | Si el primer argumento es `--consola` o `-c`, ejecuta `new NavegadorConsola().ejecutar()` y termina; si no, abre la ventana |
| `SwingUtilities.invokeLater(...)` | Crea la ventana en el **EDT** (*Event Dispatch Thread*), el hilo que Swing usa para la interfaz. Es la forma correcta de arrancar cualquier app Swing |
| `UIManager.setLookAndFeel(...)` | Fija el *look and feel* multiplataforma para que se vea igual en todos los sistemas; si falla, se ignora y usa el del sistema |
| `private Main() {}` | Constructor privado: es una clase de arranque, no se instancia |

### `consola/NavegadorConsola.java`

Misma lógica, sin ventana. Sirve para probar rápido y para demostrar que el modelo es
independiente de la interfaz.

| Método | Qué hace |
|---|---|
| `ejecutar()` | Bucle del menú: lee la opción con `Scanner` y llama `visitar` / `atras` / `adelante`, o termina con la opción 4 |
| `mostrarEstado()` | Imprime la página actual y el contenido de ambas pilas antes de cada menú |
| `describir(Pila<Pagina>)` | Convierte una pila en texto, de la **cima hacia abajo**, marcando cuál es la cima |
| `imprimir(Resultado)` | Imprime el mensaje que devolvió el modelo |

---

## 7. Trazas paso a paso (qué pasa en cada clic)

### Traza A — cuatro visitas seguidas

| Paso | Acción | Página actual | Pila atrás (fondo→cima) | Pila adelante |
|---|---|---|---|---|
| 0 | (arranque) | *(ninguna)* | vacía | vacía |
| 1 | Visitar google | google | vacía | vacía |
| 2 | Visitar mozilla | mozilla | google | vacía |
| 3 | Visitar openai | openai | google, mozilla | vacía |
| 4 | Visitar stackoverflow | stackoverflow | google, mozilla, openai | vacía |

En el paso 2, `visitar` hizo: `pilaAtras.apilarConDescarte(google)` → `paginaActual = mozilla`
→ `pilaAdelante.vaciar()`.

### Traza B — Back dos veces y Forward una

| Paso | Acción | Página actual | Pila atrás | Pila adelante |
|---|---|---|---|---|
| 4 | *(viene de arriba)* | stackoverflow | google, mozilla, openai | vacía |
| 5 | **Back** | openai | google, mozilla | stackoverflow |
| 6 | **Back** | mozilla | google | stackoverflow, openai |
| 7 | **Forward** | openai | google, mozilla | stackoverflow |

En el paso 5: `pilaAdelante.apilarConDescarte(stackoverflow)` y
`paginaActual = pilaAtras.desapilar()` → salió `openai` (la cima).

### Traza C — visitar después de retroceder (aquí se ve la regla del vaciado)

| Paso | Acción | Página actual | Pila atrás | Pila adelante |
|---|---|---|---|---|
| — | *(estado del paso 6 de la traza B)* | mozilla | google | stackoverflow, openai |
| 7b | Visitar github | github | google, mozilla | **vacía** ← se limpió |

Ya no se puede avanzar a openai ni a stackoverflow: se tomó un camino nuevo.

### Traza D — Forward con la pila vacía

`adelante()` ve `pilaAdelante.estaVacia() == true`, **no toca nada** y devuelve
`Resultado.error(..., PILA_ADELANTE_VACIA)`. La ventana escribe el mensaje en la barra
de estado y `PanelPila.mostrarAviso("PILA VACÍA")` muestra el cartel 2.6 segundos.

### Traza E — URL inexistente (404)

`catalogo.abrir("https://loquesea.com")` no encuentra la llave → devuelve
`Pagina.noEncontrada(url)`. La navegación **sí ocurre** (el 404 se apila como cualquier
página, igual que en un navegador real) y `PanelPaginaActual.mostrar` detecta
`fueEncontrada() == false` y pinta el **404**.

### Traza F — pila llena (capacidad 5)

Con la pila atrás en 5 elementos, al visitar una página nueva `apilarConDescarte`
descarta el fondo, devuelve `true`, y el `Resultado` trae `PILA_ATRAS_LLENA` → cartel
*PILA LLENA* y mensaje "se descartó la página más antigua del historial".

---

## 8. Preguntas que les pueden hacer (y sus respuestas)

**¿Por qué una pila y no una lista o un arreglo?**
Porque el historial solo necesita entrar y salir por un extremo: la última página
visitada es la primera a la que se regresa. Eso es exactamente LIFO.

**¿Usaron `java.util.Stack`?**
No. `modelo/Pila.java` está implementada desde cero sobre un arreglo, con `tope`,
`apilar`, `desapilar`, `cima`, `estaVacia`, `estaLlena` y `vaciar`.

**¿Por qué dos pilas y no una lista con un índice?**
Porque el enunciado pide modelar el comportamiento con pilas, y porque así cada regla se
traduce en una sola operación: *Back* = un `pop` en atrás + un `push` en adelante.

**¿Qué pasa si presiono Back y no hay nada?**
`Navegador.atras()` pregunta `pilaAtras.estaVacia()` **antes** de desapilar. Si está
vacía, no modifica nada y devuelve un `Resultado` de error; la interfaz muestra
*PILA VACÍA*. Nunca se lanza una excepción al usuario.

**¿Por qué se vacía la pila adelante al visitar?**
Porque el "futuro" guardado pertenece al camino anterior. Si estando en mozilla (con
openai adelante) visito github, ya no tiene sentido poder avanzar a openai. Es el
comportamiento de Chrome, Firefox, etc.

**¿Qué pasa cuando la pila se llena?**
Se descarta el elemento del fondo (el más antiguo) con `System.arraycopy`, se avisa con
*PILA LLENA* y la navegación continúa. Es una decisión de diseño: un navegador no debe
dejar de funcionar por tener el historial lleno.

**¿Dónde está la complejidad algorítmica?**
`apilar`, `desapilar`, `cima`, `estaVacia` y `estaLlena` son **O(1)**. Solo
`apilarConDescarte` es **O(n)** cuando la pila está llena, por el `arraycopy`, y con n = 5
es despreciable.

**¿Por qué la interfaz y la lógica están separadas?**
`modelo` no importa ni una clase de Swing. Gracias a eso la versión de consola
(`--consola`) usa exactamente el mismo `Navegador` sin cambiar una línea.

**¿Se conecta a internet?**
No. `CatalogoLocal` es un `LinkedHashMap` con 9 sitios simulados; cualquier otra URL
produce un 404.

---

## 9. Glosario de Java usado en el proyecto

| Concepto | Dónde aparece | Qué significa |
|---|---|---|
| **LIFO** | `Pila` | *Last In, First Out*: el último en entrar es el primero en salir |
| **Genéricos** `<T>` | `Pila<T>` | La clase funciona con cualquier tipo; el navegador la usa como `Pila<Pagina>` |
| **Type erasure** | `Object[] elementos` | En ejecución el tipo genérico desaparece, por eso no se puede hacer `new T[]` |
| **`static`** | `CatalogoLocal.normalizar`, `Paleta.*` | Pertenece a la clase, no al objeto: se llama sin crear instancias |
| **`final`** | campos de `Pagina`, `Pila` | El valor no se puede reasignar; hace las clases más seguras |
| **Método fábrica** | `Pagina.noEncontrada`, `Resultado.exito` | Un `static` que construye el objeto ya configurado, con nombre legible |
| **`enum`** | `Resultado.Tipo`, `Resultado.Aviso` | Lista cerrada de valores posibles; se usa con `switch` |
| **Clase inmutable** | `Pagina` | No cambia después de crearse |
| **Herencia** | `PanelPila extends PanelRedondeado` | Reutiliza el panel redondeado y le agrega comportamiento |
| **Sobrescritura** `@Override` | `paintComponent`, `equals` | Se reemplaza el comportamiento del padre |
| **Clase interna** | `PanelPila.Fila` | Clase dentro de otra; puede usar los campos de la externa (`acento`) |
| **Clase anónima** | el `JPanel` del número, los `AbstractAction` | Clase sin nombre creada en el momento |
| **Lambda** `e -> visitar()` | botones | Forma corta de escribir un `ActionListener` |
| **`Graphics2D`** | todo el paquete `ui` | API de dibujo 2D de Java (rectángulos, óvalos, arcos, antialias) |
| **EDT** | `SwingUtilities.invokeLater` | Hilo de eventos de Swing: toda la interfaz debe crearse y modificarse ahí |
| **`CardLayout`** | `PanelPila` | Varias "tarjetas" en el mismo espacio, se muestra una a la vez |
| **`Timer` de Swing** | `PanelPila` | Ejecuta algo después de N milisegundos, en el EDT (por eso puede tocar la interfaz) |
| **`InputMap` / `ActionMap`** | `registrarAtajos` | Mecanismo estándar para atajos de teclado |

---

## 10. Dónde tocar el código para cambiar cosas

| Quiero… | Archivo | Qué cambiar |
|---|---|---|
| Que las pilas guarden más o menos páginas | `modelo/Pila.java` | `CAPACIDAD_POR_DEFECTO = 5` |
| Agregar o quitar sitios simulados | `modelo/CatalogoLocal.java` | Una línea `registrar(url, titulo, descripcion)` en el constructor |
| Cambiar los colores | `ui/Paleta.java` | `ACENTO`, `ACENTO_PROFUNDO`, `ACENTO_TENUE` (y los beige si quieren otra base) |
| Cambiar los textos de los mensajes | `modelo/Navegador.java` | Las cadenas dentro de `Resultado.exito/aviso/error` |
| Que el cartel dure más o menos tiempo | `ui/PanelPila.java` | `new Timer(2600, ...)` (milisegundos) |
| Deshabilitar Back/Forward cuando no se puede | `ui/VentanaNavegador.java` | En `actualizarVista()`, usar `navegador.puedeRetroceder()` / `puedeAvanzar()` con `setEnabled(...)` (habría que guardar los botones como campos) |
| Que al llenarse la pila **no** se descarte nada | `modelo/Navegador.java` | Usar `pilaAtras.apilar(...)` dentro de un `if (!pilaAtras.estaLlena())` y devolver un error |

---

## Cómo compilar y ejecutar (recordatorio)

Siempre en este orden: **clonar → entrar a la carpeta → ejecutar el script**
(detalle completo en la [sección 0](#0-antes-de-empezar-clonar-y-ejecutar-el-proyecto)).

```bash
# 1. Clonar (una sola vez)
git clone -b explicacion_codigo https://github.com/diegoanahuac/Navegador_Web_E5.git

# 2. Entrar a la carpeta (siempre, en cada terminal nueva)
cd Navegador_Web_E5

# 3a. Ejecutar en Windows (PowerShell)
.\ejecutar.bat
.\ejecutar.bat --consola

# 3b. Ejecutar en Linux / macOS
./ejecutar.sh
./ejecutar.sh --consola

# 3c. O a mano, sin scripts
javac -encoding UTF-8 -d out src/navegador/*.java src/navegador/*/*.java
java -cp out navegador.Main
```

Requisito único: **JDK 8 o superior** (probado con JDK 21 y 24).
