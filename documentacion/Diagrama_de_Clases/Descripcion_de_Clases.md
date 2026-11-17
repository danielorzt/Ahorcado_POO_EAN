# Descripción de clases — Juego del Ahorcado

Universidad EAN · Programación Orientada a Objetos · noviembre de 2026

Describe cada tipo del diagrama (`Diagrama_de_Clases.puml`). Todo sale del código de `codigo_fuente/src/`. Visibilidad: `+` público, `-` privado, `#` protegido, `~` paquete. Los atributos marcados *final* no se reasignan.

Tipos del proyecto: 35 (4 enum/interfaz entre ellos). Clases externas del JDK que aparecen en el diagrama: `JFrame`, `JPanel`, `FocusTraversalPolicy`.

## Paquete `modelo` (lógica del juego, sin Swing)

| Clase | Tipo | Responsabilidad | Atributos | Métodos públicos |
|---|---|---|---|---|
| `Dificultad` | enum | Nivel de una palabra, calculado por su longitud (FÁCIL 3–5, MEDIO 6–8, DIFÍCIL 9+). | `-etiqueta : String` (final); constantes `FACIL`, `MEDIO`, `DIFICIL` | `getEtiqueta() : String`, `{static} paraLongitud(int) : Dificultad`, `toString() : String` |
| `EstadoJuego` | enum | Estado de una partida. | constantes `EN_CURSO`, `GANADA`, `PERDIDA` | — |
| `ResultadoLetra` | enum | Resultado de intentar una letra. | constantes `ACERTADA`, `FALLADA`, `REPETIDA`, `INVALIDA`, `PARTIDA_TERMINADA` | — |
| `Palabra` | clase `final` (inmutable) | Palabra secreta: texto normalizado, original con tildes, pista, categoría y dificultad. | `-texto`, `-textoOriginal`, `-pista`, `-categoria : String`; `-dificultad : Dificultad` (todos final) | `Palabra(String, String, String)`, `getTexto()`, `getTextoOriginal()`, `getPista()`, `getCategoria()`, `getDificultad()`, `longitud() : int`, `contiene(char) : boolean`, `equals(Object)`, `hashCode()`, `toString()` |
| `Categoria` | clase | Grupo de palabras de un mismo tema. | `-nombre : String`; `-palabras : ArrayList<Palabra>` | `Categoria(String)`, `agregar(Palabra) : void`, `getPalabras() : ArrayList<Palabra>` (copia), `obtenerPorDificultad(Dificultad) : ArrayList<Palabra>`, `tamano() : int`, `getNombre() : String` |
| `BancoPalabras` | clase | Carga las 200 palabras y elige una al azar sin repetir hasta agotar candidatas. | `-categorias : ArrayList<Categoria>`; `-usadas : ArrayList<Palabra>`; `-azar : Random` | `BancoPalabras()`, `elegirAleatoria(Dificultad) : Palabra`, `totalPalabras() : int`, `getCategorias() : ArrayList<Categoria>` (copia), `resumenPorCategoria() : String`, `contarPorDificultad(Dificultad) : int`, `contiene(String) : boolean` |
| `DatosPalabras` | clase `final`, paquete-privada | Solo datos: las filas `{categoría, palabra, pista}` del banco. | `~{static} DATOS : String[][]` (final) | — (constructor privado) |
| `Pista` | **abstracta** | Pista de uso único; `usar` es el método plantilla. | `-nombre`, `-descripcion : String` (final); `-usada : boolean` | `usar(Partida) : String`, `{abstract} obtener(Partida) : String`, `isUsada() : boolean`, `getNombre() : String`, `getDescripcion() : String` (constructor `#Pista(String, String)` protegido) |
| `PistaCategoria` | clase (hija de `Pista`) | Entrega la categoría de la palabra. | — | `PistaCategoria()`, `obtener(Partida) : String` |
| `PistaLetra` | clase (hija de `Pista`) | Revela una letra aún oculta, sin contar error. | `-azar : Random` (final) | `PistaLetra()`, `obtener(Partida) : String` |
| `PistaTexto` | clase (hija de `Pista`) | Entrega el texto de pista propio de la palabra. | — | `PistaTexto()`, `obtener(Partida) : String` |
| `Partida` | clase | Reglas del juego: letras probadas, pistas, errores y estado. | `+{static} MAX_ERRORES : int = 7` (final); `-palabraSecreta : Palabra`; `-letrasCorrectas`, `-letrasIncorrectas : ArrayList<Character>`; `-pistas : ArrayList<Pista>`; `-pistasObtenidas : ArrayList<String>`; `-estado : EstadoJuego` | `Partida(Palabra)`, `adivinar(char) : ResultadoLetra`, `revelarLetra(char) : void`, `usarPista(int) : String`, `pistaDisponible(int) : boolean`, `pistasRestantes() : int`, `getErrores() : int`, `intentosRestantes() : int`, `palabraOculta() : String`, `estaCompleta() : boolean`, `getPalabra() : Palabra`, `getEstado() : EstadoJuego`, `getLetrasCorrectas()`, `getLetrasIncorrectas() : ArrayList<Character>` (copias), `getPistasObtenidas() : ArrayList<String>` (copia) |

## Paquete `controlador`

| Clase | Tipo | Responsabilidad | Atributos | Métodos públicos |
|---|---|---|---|---|
| `VistaJuego` | interfaz | Contrato que debe cumplir cualquier vista (el controlador no conoce Swing). | — | `actualizar(Partida)`, `mostrarFin(Partida)`, `mostrarMensaje(String)` (todos `void`, abstractos) |
| `ControladorJuego` | clase | Recibe las acciones del jugador, las aplica al modelo y pide a la vista que se actualice. | `-banco : BancoPalabras` (final); `-vista : VistaJuego`; `-partida : Partida` | `ControladorJuego(BancoPalabras)`, `setVista(VistaJuego)`, `iniciarNuevaPartida(Dificultad)`, `iniciarPartidaConPalabra(Palabra)`, `procesarLetra(char)`, `usarPista(int)`, `getPartida() : Partida`, `salir()` |

## Paquete `vista` (Swing)

| Clase | Tipo | Responsabilidad | Atributos | Métodos públicos |
|---|---|---|---|---|
| `Estilo` | clase `final` (utilitaria) | Constantes de colores y fuentes y utilidades de estilo. | 10 `Color` y 6 `Font` estáticos finales públicos (`FONDO`, `PRIMARIO`, `ACENTO`, `TEXTO`, `ACIERTO`, `ERROR`, `TARJETA`, `BORDE`, `MADERA`, `CUERDA`; `TITULO`, `SUBTITULO`, `NORMAL`, `BOTON`, `TECLA`, `PALABRA`) | `{static} configurarLookAndFeel()`, `{static} margen(int) : Border`, `{static} tarjeta(String) : Border`, `{static} estilizarBoton(JButton)` |
| `ParteCuerpo` | **abstracta** | Una parte del ahorcado; cada subclase sabe dibujarse. | `-nombre : String`; `-origenX`, `-origenY : int`; `-escala : double` (todos final) | `{abstract} dibujar(Graphics2D) : void`, `getNombre() : String` (protegidos: `px`, `py`, `largo`, `grosor`; constructor protegido) |
| `Cabeza`, `Torso`, `BrazoDerecho`, `BrazoIzquierdo`, `PiernaDerecha`, `PiernaIzquierda`, `Cuerda` | clases (hijas de `ParteCuerpo`) | Dibujan el 1.º, 2.º, 3.º, 4.º, 5.º, 6.º y 7.º error respectivamente. | — | constructor `(int origenX, int origenY, double escala)`, `dibujar(Graphics2D) : void` |
| `PanelAhorcado` | clase (hija de `JPanel`) | Dibuja la horca fija y las partes según los errores, con `Graphics2D`. | `-partes : ArrayList<ParteCuerpo>` (final); `-errores : int`; constantes privadas del lienzo | `PanelAhorcado()`, `setErrores(int)`, `getErrores() : int`, `getNombresPartes() : ArrayList<String>` (`#paintComponent(Graphics)`) |
| `PanelTeclado` | clase (hija de `JPanel`) | Teclado en pantalla A–Z y Ñ (27 botones). | `+{static} LETRAS : String`; `-botones : ArrayList<JButton>`; `-escuchaLetra : Consumer<Character>` | `PanelTeclado()`, `setEscuchaLetra(Consumer<Character>)`, `setEnabledLetras(ArrayList<Character>, boolean)`, `getBotones() : ArrayList<JButton>` |
| `PanelPistas` | clase (hija de `JPanel`) | Tres botones de pista, contador de restantes y área con las pistas obtenidas. | `-botones : JButton[3]`; `-etiquetaRestantes : JLabel`; `-areaPistas : JTextArea`; `-escuchaPista : Consumer<Integer>` | `PanelPistas()`, `setEscuchaPista(Consumer<Integer>)`, `setPistaUsada(int)`, `setPistaDisponible(int, boolean)`, `setPistasRestantes(int)`, `setTextoPistas(ArrayList<String>)`, `getBotones() : ArrayList<JButton>` |
| `PanelEstado` | clase (hija de `JPanel`) | Muestra palabra oculta, dificultad, letras correctas e incorrectas e intentos. | 5 `JLabel` privadas (`etiquetaPalabra`, `etiquetaDificultad`, `etiquetaCorrectas`, `etiquetaIncorrectas`, `etiquetaIntentos`) | `PanelEstado()`, `actualizar(Partida)` |
| `VentanaPrincipal` | clase (hija de `JFrame`, implementa `VistaJuego`) | Ventana del juego: menú, selector de dificultad, paneles, teclado físico y diálogo de fin. | `-controlador : ControladorJuego`; `-panelAhorcado`, `-panelEstado`, `-panelPistas`, `-panelTeclado`; `-etiquetaMensaje : JLabel`; `-comboDificultad : JComboBox<String>`; `-botonNueva`, `-botonAyuda : JButton` | `VentanaPrincipal(ControladorJuego)`, `actualizar(Partida)`, `mostrarFin(Partida)`, `mostrarMensaje(String)`, `crearDialogoFin(Partida) : JDialog` |
| `OrdenTabulacion` (anidada en `VentanaPrincipal`, privada estática) | clase (hija de `FocusTraversalPolicy`) | Política de foco con orden explícito de tabulación. | `-orden : ArrayList<Component>` | `getComponentAfter`, `getComponentBefore`, `getFirstComponent`, `getLastComponent`, `getDefaultComponent` |

## Paquete `util` y `Main`

| Clase | Tipo | Responsabilidad | Atributos | Métodos públicos |
|---|---|---|---|---|
| `Normalizador` | clase `final` (utilitaria) | Mayúsculas, sin tildes/diéresis, conserva Ñ. | — | `{static} normalizar(String) : String`, `{static} normalizarLetra(char) : char`, `{static} esLetraValida(char) : boolean` |
| `VerificadorBanco` | clase `final` | Herramienta de consola que revisa el banco (200 palabras, 8×25). | constantes privadas `TOTAL_ESPERADO = 200`, `CATEGORIAS_ESPERADAS = 8`, `POR_CATEGORIA = 25` | `{static} main(String[])` |
| `PruebasAutomaticas` | clase `final` | Pruebas automáticas sin JUnit. | `-{static} total`, `-{static} fallos : int` | `{static} main(String[])` |
| `VistaFalsa` (anidada en `PruebasAutomaticas`, privada estática) | clase (implementa `VistaJuego`) | Vista falsa que cuenta llamadas; demuestra el desacople por interfaz. | `~actualizaciones`, `~finales : int`; `~ultimoMensaje : String` | `actualizar`, `mostrarFin`, `mostrarMensaje` |
| `GeneradorCapturas` | clase `final` | Genera las capturas PNG del manual (sin abrir ventanas). | `-{static} PALABRA : Palabra` (final) | `{static} main(String[])` |
| `Main` (paquete por defecto) | clase | Punto de entrada; con `--verificar` revisa el banco. | — | `{static} main(String[])` |

## Multiplicidades elegidas (y por qué)

| Relación | Multiplicidad | Origen |
|---|---|---|
| `BancoPalabras` ◆— `Categoria` | 1 a **8** | **Datos**: `DatosPalabras.DATOS` tiene 8 categorías (el código usa `ArrayList`, no fija 8; lo comprueba `VerificadorBanco`). |
| `Categoria` ◆— `Palabra` | 1 a **25** | **Datos**: 25 palabras por categoría (200 = 8 × 25). |
| `BancoPalabras` ◇— `Palabra` (`usadas`) | 1 a 0..200 | Crece al elegir y se vacía al agotar las candidatas. |
| `Partida` ◆— `Pista` | 1 a **3** | **Código**: el constructor de `Partida` agrega `PistaCategoria`, `PistaLetra`, `PistaTexto`. |
| `Partida` → `Palabra` | 1 a 1 | `palabraSecreta`. |
| `PanelAhorcado` ◆— `ParteCuerpo` | 1 a **7** | **Código**: el constructor agrega 7 partes. |
| `ControladorJuego` → `Partida` / `VistaJuego` | 1 a 0..1 | Son `null` hasta que se inicia una partida / se conecta una vista. |
| `VentanaPrincipal` ◆— cada panel | 1 a 1 | Un panel de cada tipo. |

## Dónde se ve cada pilar de POO

**Encapsulamiento** — atributos privados y acceso solo por métodos.
- `modelo/Palabra.java`: clase `final` inmutable, campos `private final`, solo getters.
- `modelo/Categoria.java`: `getPalabras()` devuelve una **copia** de la lista.
- `modelo/Partida.java`: todo el estado es privado; solo cambia con `adivinar`/`usarPista`; los getters de listas devuelven copias.
- `modelo/Pista.java` (`usada` privado, solo cambia en `usar`) y `vista/ParteCuerpo.java` (origen y escala privados; las hijas usan `px()`/`py()`).
- `controlador/ControladorJuego.java`: guarda la vista como **interfaz**.

**Herencia** — clases hijas que reutilizan y especializan a una clase padre.
- `modelo/Pista.java` ← `PistaCategoria.java`, `PistaLetra.java`, `PistaTexto.java`.
- `vista/ParteCuerpo.java` ← `Cabeza`, `Torso`, `BrazoDerecho`, `BrazoIzquierdo`, `PiernaDerecha`, `PiernaIzquierda`, `Cuerda` (cada una en su `.java`).
- `vista/PanelAhorcado`, `PanelTeclado`, `PanelPistas`, `PanelEstado` ← `JPanel`; `vista/VentanaPrincipal` ← `JFrame` (e implementa la interfaz `VistaJuego`).

**Polimorfismo** — la misma llamada ejecuta código distinto según el objeto real.
- `modelo/Partida.java`, método `usarPista`: `pistas.get(indice).usar(this)` y, dentro de `Pista.usar`, `obtener(partida)` ejecuta la versión de la subclase real.
- `vista/PanelAhorcado.java`, `paintComponent`: `partes.get(i).dibujar(g2)` sobre una lista de `ParteCuerpo`.
- `controlador/VistaJuego.java`: el controlador llama a `actualizar/mostrarFin/mostrarMensaje` sin saber si la vista es `VentanaPrincipal`, `VistaFalsa` (`util/PruebasAutomaticas.java`) o la clase anónima de `util/GeneradorCapturas.java`.

**ArrayList** — listas dinámicas del lenguaje.
- `modelo/BancoPalabras.java`: `categorias`, `usadas` y la lista local de candidatas en `elegirAleatoria`.
- `modelo/Categoria.java`: `palabras`; `obtenerPorDificultad` filtra a otra lista.
- `modelo/Partida.java`: `letrasCorrectas`, `letrasIncorrectas`, `pistas`, `pistasObtenidas`.
- `modelo/PistaLetra.java`: lista de letras candidatas.
- `vista/PanelAhorcado.java` (`partes`), `vista/PanelTeclado.java` (`botones`), `vista/VentanaPrincipal.java` (orden de tabulación).
