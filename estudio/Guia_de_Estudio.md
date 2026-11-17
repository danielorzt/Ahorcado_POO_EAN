# Guía de estudio — Juego del Ahorcado (Java Swing)

**Universidad EAN · Programación Orientada a Objetos · noviembre de 2026**
**Integrante:** Daniel Esteban Ortiz Pinzón

Cómo usarla: lee cada concepto (definición corta, ejemplo real del código y cómo explicarlo en el video), y luego practica las preguntas del profesor. Todas las referencias son `archivo:línea` dentro de `codigo_fuente/src/`.

---

## Parte 1. Conceptos

### 1. Clase y objeto
**Definición.** Una clase es el molde: dice qué datos (atributos) y qué acciones (métodos) tiene algo. Un objeto es una instancia concreta de ese molde, creada con `new`.

**Ejemplo** (`modelo/BancoPalabras.java:30`): cada fila de datos se convierte en un objeto `Palabra`.
```java
Palabra palabra = new Palabra(fila[1], fila[2], fila[0]);
```
`Palabra` es la clase (`modelo/Palabra.java:10`); `palabra` es un objeto concreto, por ejemplo "MURCIÉLAGO".

**Cómo explicarlo en el video.** "`Partida` es una clase; cada vez que pulsamos Nueva partida se crea un objeto `Partida` nuevo con su propia palabra, letras y pistas."

### 2. Encapsulamiento
**Definición.** Ocultar los datos de un objeto (atributos `private`) y dejar que solo se cambien o lean a través de métodos públicos. Así el objeto protege sus reglas.

**Ejemplo** (`modelo/Partida.java:15-21`):
```java
// ENCAPSULAMIENTO: todo el estado es privado; solo se cambia con adivinar/usarPista.
private Palabra palabraSecreta;
// ARRAYLIST: listas dinámicas de letras acertadas, falladas y pistas de la partida.
private ArrayList<Character> letrasCorrectas;
private ArrayList<Character> letrasIncorrectas;
private ArrayList<Pista> pistas;
private ArrayList<String> pistasObtenidas;
```
Además `getLetrasCorrectas()` devuelve una copia (`modelo/Partida.java:176-178`), no la lista original.

**Cómo explicarlo en el video.** "Nadie de afuera puede agregar una letra a mano: tiene que pasar por `adivinar`, que revisa las reglas."

### 3. Constructor
**Definición.** Método especial con el mismo nombre de la clase, sin tipo de retorno, que se ejecuta al hacer `new` para dejar el objeto listo (inicializar atributos, validar).

**Ejemplo** (`modelo/Palabra.java:27-32`):
```java
public Palabra(String textoOriginal, String pista, String categoria) {
    if (textoOriginal == null || textoOriginal.isBlank()) {
        throw new IllegalArgumentException("La palabra no puede estar vacía.");
    }
    this.textoOriginal = textoOriginal.trim().toUpperCase();
    this.texto = Normalizador.normalizar(this.textoOriginal);
```
Valida, normaliza y al final calcula la dificultad (`modelo/Palabra.java:40`).

**Cómo explicarlo en el video.** "El constructor de `Palabra` hace todo el trabajo: valida, quita tildes y calcula la dificultad, así ninguna palabra queda mal construida."

### 3b. Constructor de `Partida` (con `this` y creación de objetos)
Tema opcional: `Partida(Palabra palabra)` (`modelo/Partida.java:29-40`) crea las listas y las tres pistas. Es útil para mostrar que un constructor puede crear otros objetos.

### 4. Atributo estático y constante
**Definición.** `static` pertenece a la clase, no a cada objeto. `final` significa que no se puede reasignar. `static final` = constante compartida.

**Ejemplo** (`modelo/Partida.java:13`) y (`vista/PanelTeclado.java:15`):
```java
public static final int MAX_ERRORES = 7;
public static final String LETRAS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
```
También hay un método estático: `Dificultad.paraLongitud(int)` (`modelo/Dificultad.java:33`), que se llama sin crear objeto.

**Cómo explicarlo en el video.** "El 7 no está escrito en todas partes: es la constante `MAX_ERRORES`. Si el profesor pide 8 errores, se cambia en un solo lugar."

### 5. Inmutabilidad
**Definición.** Un objeto inmutable no cambia después de creado: campos `private final`, sin setters y clase `final` (no se puede heredar).

**Ejemplo** (`modelo/Palabra.java:10-17`):
```java
public final class Palabra {

    // ENCAPSULAMIENTO: campos privados y finales (clase inmutable), solo se leen con getters.
    private final String texto;
    private final String textoOriginal;
    private final String pista;
    private final String categoria;
    private final Dificultad dificultad;
```
**Cómo explicarlo en el video.** "`Palabra` es inmutable: una vez creada no se puede cambiar. Eso evita errores, porque la misma palabra se comparte entre el banco, la lista de usadas y la partida."

### 6. Herencia
**Definición.** Una clase (hija) reutiliza y especializa a otra (padre) con `extends`. Expresa una relación "es un".

**Ejemplo** (`modelo/PistaCategoria.java:4-9`):
```java
public class PistaCategoria extends Pista {

    /** Crea la pista de categoría. */
    public PistaCategoria() {
        super("Categoría", "Muestra la categoría de la palabra secreta.");
    }
```
Otras jerarquías: `PistaLetra` y `PistaTexto` extienden `Pista`; `Cabeza`, `Torso`, `BrazoDerecho`, `BrazoIzquierdo`, `PiernaDerecha`, `PiernaIzquierda` y `Cuerda` extienden `ParteCuerpo`.

**Cómo explicarlo en el video.** "Una `PistaCategoria` *es una* `Pista`. Hereda el control de 'usada una sola vez' y solo define qué entrega."

### 7. Clase abstracta
**Definición.** Clase que no se puede instanciar directamente y sirve de base para otras. Puede tener métodos con código y métodos abstractos.

**Ejemplo** (`modelo/Pista.java:7`) y (`vista/ParteCuerpo.java:11`):
```java
public abstract class Pista {
public abstract class ParteCuerpo {
```
No existe una "pista genérica": siempre es de categoría, de letra o de texto. Tampoco una "parte del cuerpo" genérica.

**Cómo explicarlo en el video.** "`Pista` y `ParteCuerpo` son abstractas porque son ideas generales; lo concreto está en las subclases."

### 8. Método abstracto (y método plantilla)
**Definición.** Método declarado sin cuerpo (`abstract`); obliga a cada subclase a implementarlo. Un método *plantilla* es uno concreto que llama a un método abstracto.

**Ejemplo** (`modelo/Pista.java:33-40` y `:48`):
```java
public String usar(Partida partida) {
    if (usada) {
        return null;
    }
    usada = true;
    // POLIMORFISMO: se llama a obtener() y se ejecuta la versión de la subclase real.
    return obtener(partida);
}
```
```java
public abstract String obtener(Partida partida);
```
El otro método abstracto es `ParteCuerpo.dibujar(Graphics2D g)` (`vista/ParteCuerpo.java:39`).

**Cómo explicarlo en el video.** "`usar` es igual para todas las pistas; lo único que cambia es `obtener`, y eso lo define cada subclase."

### 9. Polimorfismo
**Definición.** Un mismo mensaje (llamada a método) sobre una variable del tipo padre ejecuta comportamientos distintos según la clase real del objeto.

**Ejemplo** (`vista/PanelAhorcado.java:95-98`):
```java
// POLIMORFISMO: cada elemento es una subclase distinta de ParteCuerpo y su dibujar() es propio.
for (int i = 0; i < errores; i++) {
    partes.get(i).dibujar(g2);
}
```
Segundo ejemplo (`modelo/Partida.java:95-96`): `pistas.get(indice).usar(this)` ejecuta `obtener()` de la pista que esté en esa posición.

**Cómo explicarlo en el video.** "El panel llama `dibujar` sin saber si es una cabeza o una pierna; cada clase se dibuja a su manera. No hay ningún `if` por tipo."

### 10. Interfaz
**Definición.** Contrato: lista de métodos (sin implementación) que una clase se compromete a ofrecer con `implements`. Permite depender de "qué hace" y no de "quién lo hace".

**Ejemplo** (`controlador/VistaJuego.java:9`, `:16`, `:23`, `:30`) y (`vista/VentanaPrincipal.java:39`):
```java
public interface VistaJuego {
    void actualizar(Partida partida);
    void mostrarFin(Partida partida);
    void mostrarMensaje(String mensaje);
}
```
(Aquí se muestran solo las firmas; en el archivo hay Javadoc entre ellas.) La implementa `VentanaPrincipal` (`public class VentanaPrincipal extends JFrame implements VistaJuego`) y el controlador la guarda como tipo interfaz (`controlador/ControladorJuego.java:18-19`).

**Cómo explicarlo en el video.** "El controlador solo conoce `VistaJuego`. Por eso no depende de Swing y es fácil de probar."

### 11. Enum
**Definición.** Tipo con un conjunto fijo de valores con nombre. Puede tener atributos, constructor y métodos.

**Ejemplo** (`modelo/Dificultad.java:7-16`):
```java
public enum Dificultad {
    FACIL("FÁCIL"),
    MEDIO("MEDIO"),
    DIFICIL("DIFÍCIL");

    private final String etiqueta;

    Dificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }
```
Otros enums: `EstadoJuego` (`EN_CURSO`, `GANADA`, `PERDIDA`) y `ResultadoLetra` (`ACERTADA`, `FALLADA`, `REPETIDA`, `INVALIDA`, `PARTIDA_TERMINADA`).

**Cómo explicarlo en el video.** "Con enums evitamos números o textos 'mágicos'. `adivinar` devuelve un `ResultadoLetra` y el controlador decide qué mensaje mostrar con un `switch` (`controlador/ControladorJuego.java:74`)."

### 12. ArrayList y las operaciones usadas
**Definición.** Lista dinámica de la biblioteca estándar (`java.util.ArrayList<T>`): crece y se encoge sola, a diferencia del arreglo de tamaño fijo.

**Operaciones usadas en el proyecto** (todas existen en el código):
| Operación | Dónde |
|---|---|
| `add` | `modelo/Categoria.java:29` (`agregar`), `modelo/Partida.java:37-39` |
| `get` | `modelo/Partida.java:96`, `vista/PanelAhorcado.java:98` |
| `contains` | `modelo/Partida.java:56` |
| `removeAll` | `modelo/BancoPalabras.java:70` |
| `clear` | `modelo/BancoPalabras.java:72` |
| `size` / `isEmpty` | `modelo/BancoPalabras.java:75`, `:66` |
| `addAll` | `modelo/BancoPalabras.java:63` |
| copia `new ArrayList<>(...)` | `modelo/Categoria.java:38`, `modelo/Partida.java:177`, `:182`, `:187` |
| recorrido `for-each` | `modelo/Categoria.java:50` |

**Ejemplo** (`modelo/BancoPalabras.java:69-77`):
```java
ArrayList<Palabra> disponibles = new ArrayList<>(candidatas);
disponibles.removeAll(usadas);
if (disponibles.isEmpty()) {
    usadas.clear();
    disponibles = candidatas;
}
Palabra elegida = disponibles.get(azar.nextInt(disponibles.size()));
usadas.add(elegida);
return elegida;
```
**Cómo explicarlo en el video.** "Las listas cambian durante el juego: se agregan letras, se quitan palabras usadas. Por eso `ArrayList` y no un arreglo."

### 13. Asociación, composición y multiplicidad
**Definición.**
- *Asociación:* un objeto usa o conoce a otro.
- *Agregación/composición:* "tiene un"; en la composición (más fuerte) el contenedor crea y controla la vida de las partes.
- *Multiplicidad:* cuántos objetos se relacionan (1, 0..*, etc.).

**Ejemplo** (`modelo/Partida.java:37-39`): `Partida` **crea** sus tres pistas, que no existen fuera de ella: composición.
```java
pistas.add(new PistaCategoria());
pistas.add(new PistaLetra());
pistas.add(new PistaTexto());
```
**Relaciones del proyecto**
| Relación | Multiplicidad | Evidencia |
|---|---|---|
| `Partida` — `Palabra` | 1 a 1 | `modelo/Partida.java:16` |
| `Partida` — `Pista` | 1 a 3 (composición) | `modelo/Partida.java:37-39` |
| `BancoPalabras` — `Categoria` | 1 a muchas (8 en los datos) | `modelo/BancoPalabras.java:13` |
| `Categoria` — `Palabra` | 1 a muchas (25 en los datos) | `modelo/Categoria.java:11` |
| `ControladorJuego` — `Partida` | 1 a 1 (la actual) | `controlador/ControladorJuego.java:20` |
| `ControladorJuego` — `VistaJuego` | 1 a 1 (por interfaz) | `controlador/ControladorJuego.java:19` |
| `PanelAhorcado` — `ParteCuerpo` | 1 a 7 | `vista/PanelAhorcado.java:28` |

(Las cifras 8 y 25 vienen de los datos del banco, no de una restricción en el código.)

**Cómo explicarlo en el video.** "Una partida tiene exactamente una palabra y tres pistas; el banco tiene muchas categorías y cada categoría muchas palabras."

### 14. MVC y bajo acoplamiento
**Definición.** MVC separa **Modelo** (datos y reglas), **Vista** (lo que se ve) y **Controlador** (recibe acciones y coordina). Bajo acoplamiento: las clases dependen lo menos posible unas de otras (idealmente de interfaces).

**Mapa en el proyecto**
- Modelo: paquete `modelo` (sin Swing).
- Vista: paquete `vista` (`VentanaPrincipal` y paneles).
- Controlador: `controlador/ControladorJuego.java`.

**Ejemplo** (`controlador/ControladorJuego.java:122-128`): el controlador solo habla con la interfaz.
```java
/** Actualiza la vista y, si la partida terminó, muestra el final. */
private void refrescar() {
    if (vista == null) {
        return;
    }
    vista.actualizar(partida);
    if (partida.getEstado() != EstadoJuego.EN_CURSO) {
```
**Cómo explicarlo en el video.** "La ventana avisa al controlador, el controlador cambia el modelo y le pide a la vista que se actualice. Las reglas viven en `Partida`, no en la ventana."

### 15. Eventos de Swing y listeners
**Definición.** Swing avisa cuando el usuario hace algo (clic, tecla) llamando a un *listener* registrado con `addActionListener`. Aquí se usan expresiones lambda.

**Ejemplo** (`vista/VentanaPrincipal.java:196-206`):
```java
private void conectarEventos() {
    botonNueva.addActionListener(e -> nuevaPartida());
    botonAyuda.addActionListener(e -> mostrarAyuda());
    panelTeclado.setEscuchaLetra(letra -> controlador.procesarLetra(letra));
    panelPistas.setEscuchaPista(indice -> controlador.usarPista(indice));
}
```
`PanelTeclado` recibe un `Consumer<Character>` (`vista/PanelTeclado.java:53`) y lo llama al pulsar cada botón (`:38-42`). El teclado físico usa `KeyboardFocusManager.addKeyEventDispatcher` (`vista/VentanaPrincipal.java:239`) y se ignora si la ventana activa no es la principal (`:246`).

**Cómo explicarlo en el video.** "Cada botón avisa con un listener; el panel del teclado no sabe qué se hace con la letra, solo la entrega."

### 16. `paintComponent` y Graphics2D
**Definición.** Para dibujar sobre un `JPanel` se sobrescribe `paintComponent(Graphics g)`. `Graphics2D` ofrece figuras, trazos, antialiasing, traslación y escala.

**Ejemplo** (`vista/PanelAhorcado.java:79-83`, `:88`, `:92-93`):
```java
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
```
Se dibuja en un lienzo virtual de 300x400 (`:21-23`) que se escala al tamaño del panel con `g2.scale(escala, escala)` (`:93`). La horca fija está en `dibujarHorca` (`:104`) y las partes se dibujan con `dibujar(g2)`. No se usan imágenes.

**Cómo explicarlo en el video.** "El muñeco se dibuja con código: círculos y líneas. Con el lienzo virtual se ve bien aunque cambie el tamaño de la ventana."

### 17. Cadenas y normalización de tildes
**Definición.** `String` es inmutable; para armar texto se usa `StringBuilder`. Para quitar tildes se descompone la letra (NFD: "É" = "E" + tilde) y se toma la base.

**Ejemplo** (`util/Normalizador.java:45-56`):
```java
public static char normalizarLetra(char c) {
    char mayuscula = Character.toUpperCase(c);
    if (mayuscula == 'Ñ') {
        return 'Ñ';
    }
    // Se descompone la letra (p. ej. É = E + tilde) y se toma solo la base.
    String descompuesta = Normalizer.normalize(String.valueOf(mayuscula), Normalizer.Form.NFD);
    char base = descompuesta.charAt(0);
    if (base >= 'A' && base <= 'Z') {
        return base;
    }
    return '\0';
}
```
`StringBuilder` en `Partida.palabraOculta()` (`modelo/Partida.java:141-152`, p. ej. `_ _ A _`).

**Cómo explicarlo en el video.** "Guardamos la palabra sin tildes para comparar fácil, pero conservamos la Ñ porque es otra letra, y guardamos también el original con tildes para mostrarlo al final."

### 18. Random
**Definición.** `java.util.Random` genera números pseudoaleatorios; `nextInt(n)` da un entero de 0 a n-1.

**Ejemplo** (`modelo/BancoPalabras.java:75`) y (`modelo/PistaLetra.java:36`):
```java
Palabra elegida = disponibles.get(azar.nextInt(disponibles.size()));
char elegida = candidatas.get(azar.nextInt(candidatas.size()));
```
`BancoPalabras` crea su `Random` en `:25`; `PistaLetra` en `:9`.

**Cómo explicarlo en el video.** "Se elige un índice al azar de la lista de candidatas; así cada palabra y cada letra revelada son aleatorias."

---

## Parte 2. Preguntas típicas del profesor y cómo responderlas

**1. ¿Por qué `Palabra` es inmutable?**
Porque sus campos son `private final`, no tiene setters y la clase es `final` (`modelo/Palabra.java:10-17`). La misma palabra se usa en el banco, en la lista de usadas y en la partida; si pudiera cambiar, un cambio afectaría a todos. Inmutable = segura de compartir.

**2. ¿Dónde hay polimorfismo?**
En dos lugares: (a) `partes.get(i).dibujar(g2)` en `vista/PanelAhorcado.java:98`, donde cada `ParteCuerpo` se dibuja distinto; (b) `pistas.get(indice).usar(this)` en `modelo/Partida.java:96`, que acaba ejecutando el `obtener()` de la subclase real (`modelo/Pista.java:39`).

**3. ¿Qué pasa si el jugador usa la pista de letra y solo falta una letra?**
`PistaLetra.obtener` arma la lista de letras candidatas (las que aún no están en `letrasCorrectas`); con una sola faltante, la elige, llama a `p.revelarLetra(...)` y devuelve "Se reveló la letra: X" (`modelo/PistaLetra.java:23-38`). `revelarLetra` la agrega a las correctas y llama a `actualizarEstado()` (`modelo/Partida.java:74-83`); como la palabra queda completa, el estado pasa a `GANADA` y el controlador muestra el diálogo de fin (`controlador/ControladorJuego.java:127-129`). No cuenta como error. Si no quedaran letras, devolvería "No quedan letras por revelar." (`modelo/PistaLetra.java:33-35`).

**4. ¿Por qué usar `ArrayList` y no un arreglo?**
Porque el tamaño cambia durante la ejecución: las letras se agregan según se juega, las palabras usadas se agregan y se vacían, y las candidatas se filtran. Con `ArrayList` tenemos `add`, `contains`, `removeAll`, `clear` ya hechos (`modelo/BancoPalabras.java:69-76`). Un arreglo exigiría manejar el tamaño y los desplazamientos a mano. (Para los datos del banco sí se usa un arreglo fijo de textos, `DatosPalabras.DATOS`, porque son datos constantes de carga.)

**5. ¿Cómo evitas repetir palabras?**
`BancoPalabras` guarda `usadas`. En `elegirAleatoria` se arman las candidatas, se hace `disponibles.removeAll(usadas)` y se elige entre las disponibles; la elegida se agrega a `usadas`. Cuando no quedan disponibles, `usadas.clear()` y se vuelve a empezar (`modelo/BancoPalabras.java:69-77`). `removeAll` funciona porque `Palabra` define `equals` por su texto (`modelo/Palabra.java:85-92`).

**6. ¿Cómo manejas la Ñ y las tildes?**
`Normalizador.normalizarLetra` pasa a mayúscula, devuelve la Ñ tal cual y para el resto descompone con NFD y toma la base si está entre A y Z (`util/Normalizador.java:45-56`). La palabra se guarda normalizada (`texto`) y también con tildes (`textoOriginal`, `modelo/Palabra.java:31-32`), que es lo que se muestra al terminar la partida. Cualquier letra que llegue del teclado, físico o en pantalla, se normaliza antes de compararla (`modelo/Partida.java:49`).

**7. ¿Por qué `VistaJuego` es una interfaz?**
Para que `ControladorJuego` dependa de un contrato (`actualizar`, `mostrarFin`, `mostrarMensaje`) y no de `VentanaPrincipal` ni de Swing (`controlador/VistaJuego.java:9`, `controlador/ControladorJuego.java:18-19`). Es bajo acoplamiento: se podría cambiar la ventana, o usar una vista falsa para pruebas, sin modificar el controlador.

**8. ¿Qué haría falta para añadir una 4.ª pista?**
(1) Crear una clase `PistaNueva extends Pista` e implementar `obtener(Partida)`; (2) agregarla en el constructor de `Partida` junto a las otras tres (`modelo/Partida.java:37-39`); (3) en la vista, `PanelPistas` tiene un arreglo de 3 botones (`vista/PanelPistas.java:18`) y `VentanaPrincipal.actualizar` recorre `i < 3` (`vista/VentanaPrincipal.java:291`), así que habría que añadir un cuarto botón y ajustar ese tres. La lógica del modelo (`usarPista`, `pistasRestantes`) no cambia porque trabajan sobre la lista.

**9. ¿Qué haría falta para añadir una nueva categoría?**
Agregar filas con ese nombre de categoría en `DatosPalabras.DATOS`; `BancoPalabras` crea la categoría automáticamente con `buscarOCrearCategoria` (`modelo/BancoPalabras.java:38-47`). Ojo: `Main --verificar` y las pruebas esperan exactamente 200 palabras en 8 categorías de 25, así que habría que actualizar esas verificaciones.

**10. ¿Cómo se ve el orden de las 7 partes del cuerpo y quién lo decide?**
Lo decide el orden en que se agregan a la lista en `PanelAhorcado` (`vista/PanelAhorcado.java:42-48`): cabeza, torso, brazo derecho, brazo izquierdo, pierna derecha, pierna izquierda, cuerda. `paintComponent` dibuja las primeras `errores` (`:97-98`) y `Partida.getErrores()` es el tamaño de `letrasIncorrectas` (`modelo/Partida.java:127-129`).

**11. ¿Cómo se decide si se gana o se pierde?**
`Partida.actualizarEstado()` (`modelo/Partida.java:191`): si `estaCompleta()` pasa a `GANADA`; si no y los errores llegan a `MAX_ERRORES` (7), pasa a `PERDIDA`. Se llama después de cada letra acertada o fallada y al revelar una letra.

**12. ¿Qué pasa si el jugador repite una letra, escribe un símbolo o sigue tecleando tras terminar?**
`adivinar` devuelve `INVALIDA` (no es letra), `PARTIDA_TERMINADA` (estado ≠ EN_CURSO) o `REPETIDA` (ya estaba en correctas o incorrectas); en ningún caso suma error (`modelo/Partida.java:48-57`). El controlador muestra un mensaje en los casos que corresponde o ignora la entrada (`controlador/ControladorJuego.java:74-90`).

**13. ¿Cómo se calcula la dificultad de una palabra?**
Por su longitud normalizada: hasta 5 letras = FÁCIL, 6 a 8 = MEDIO, 9 o más = DIFÍCIL (`modelo/Dificultad.java:33-41`). Se calcula en el constructor de `Palabra` (`modelo/Palabra.java:40`). El selector "ALEATORIA" corresponde a pasar `null` a `elegirAleatoria` (`vista/VentanaPrincipal.java`, método `dificultadSeleccionada`).

**14. ¿Por qué el teclado físico no funciona cuando hay un diálogo abierto?**
El dispatcher global comprueba que la ventana activa sea la principal: si no lo es (por ejemplo, un diálogo modal), devuelve `false` y no procesa la letra (`vista/VentanaPrincipal.java:246`). También ignora combinaciones con Ctrl/Alt/Meta para no romper atajos (`:243`).

**15. ¿Qué es el método plantilla de `Pista`?**
`usar` (`modelo/Pista.java:33-40`) tiene un algoritmo fijo: si ya se usó devuelve `null`; si no, marca `usada = true` y llama a `obtener`, que cada subclase define. Garantiza en un solo lugar que cada pista se use una vez.

**16. ¿Cómo sabes que el programa funciona?**
Con `Main --verificar` (cuenta el banco) y con `util.PruebasAutomaticas`, que imprime `OK`/`FALLO` por prueba y termina con código 1 si alguna falla (`util/PruebasAutomaticas.java`; el brief indica 76 pruebas, confirma el número al ejecutarlas).

**17. ¿Por qué la lista de `Partida` se devuelve como copia?**
`getLetrasCorrectas()` devuelve `new ArrayList<>(letrasCorrectas)` (`modelo/Partida.java:177`), igual que `Categoria.getPalabras()` (`modelo/Categoria.java:38`). Así el código de afuera puede leer o modificar su copia sin dañar el estado interno: es encapsulación.

---

## Parte 3. Mapa del código (una página)

Raíz de fuentes: `codigo_fuente/src/`

### `Main.java` (paquete por defecto)
Punto de entrada. Con `--verificar` llama a `VerificadorBanco`; si no, carga el `BancoPalabras`, crea el `ControladorJuego` y abre la `VentanaPrincipal` dentro de `SwingUtilities.invokeLater` (`Main.java:32`).

### `modelo` — reglas del juego (sin Swing)
| Clase | Responsabilidad |
|---|---|
| `Palabra` | Palabra secreta inmutable: texto normalizado, texto original, pista, categoría y dificultad. `contiene(char)`, `longitud()`. |
| `Dificultad` (enum) | FÁCIL / MEDIO / DIFÍCIL; `paraLongitud(int)`. |
| `EstadoJuego` (enum) | `EN_CURSO`, `GANADA`, `PERDIDA`. |
| `ResultadoLetra` (enum) | `ACERTADA`, `FALLADA`, `REPETIDA`, `INVALIDA`, `PARTIDA_TERMINADA`. |
| `Categoria` | Grupo de palabras (`ArrayList<Palabra>`); filtra por dificultad. |
| `BancoPalabras` | Carga los datos, agrupa en categorías y elige palabras al azar sin repetir. |
| `DatosPalabras` | Solo datos: filas `{categoría, palabra, pista}`. |
| `Pista` (abstracta) | Pista de un solo uso; método plantilla `usar` + `obtener` abstracto. |
| `PistaCategoria`, `PistaLetra`, `PistaTexto` | Cada una define qué entrega `obtener`. |
| `Partida` | Estado y reglas de una partida: letras, errores, pistas, estado. |

### `controlador` — coordinación
| Clase | Responsabilidad |
|---|---|
| `VistaJuego` (interfaz) | Contrato de la vista: `actualizar`, `mostrarFin`, `mostrarMensaje`. |
| `ControladorJuego` | Recibe letras y pistas, usa la `Partida`, avisa a la vista. Inicia partidas. |

### `vista` — interfaz Swing
| Clase | Responsabilidad |
|---|---|
| `VentanaPrincipal` | Ventana, menú, selector de dificultad, diálogo de fin, teclado físico, tabulación; implementa `VistaJuego`. |
| `PanelAhorcado` | Dibuja la horca y las partes con Graphics2D (`paintComponent`). |
| `ParteCuerpo` (abstracta) | Base de las partes; `dibujar` abstracto. |
| `Cabeza`, `Torso`, `BrazoDerecho`, `BrazoIzquierdo`, `PiernaDerecha`, `PiernaIzquierda`, `Cuerda` | Cada una se dibuja a sí misma. |
| `PanelTeclado` | Botones A–Z y Ñ. |
| `PanelPistas` | Tres botones de pista, contador y texto de pistas obtenidas. |
| `PanelEstado` | Palabra oculta, letras correctas/incorrectas, intentos. |
| `Estilo` | Colores, fuentes y Look and Feel (Nimbus si existe). |

### `util` — herramientas
| Clase | Responsabilidad |
|---|---|
| `Normalizador` | Mayúsculas, sin tildes, conserva la Ñ. |
| `VerificadorBanco` | Imprime el conteo del banco (total, categorías, dificultades). |
| `PruebasAutomaticas` | Pruebas sin JUnit; `OK`/`FALLO`. |
| `GeneradorCapturas` | Herramienta de desarrollo: genera las capturas del manual. |

### Flujo de una jugada
Clic o tecla → listener en la vista → `ControladorJuego.procesarLetra` → `Partida.adivinar` (devuelve `ResultadoLetra`) → el controlador avisa y llama `vista.actualizar(partida)` → si la partida terminó, `vista.mostrarFin(partida)` (`controlador/ControladorJuego.java:68-90`, `:123-130`).

---

## Consejos finales para responder
- Responde primero con la idea en una frase y luego señala el archivo y la línea.
- Si no sabes algo, dilo y ve al código: "déjame mostrarlo en `Partida.java`".
- No afirmes comportamientos que no hayas visto en el código o ejecutado en el juego.
