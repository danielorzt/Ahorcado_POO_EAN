# Juego del Ahorcado interactivo (Java Swing)

Proyecto de **Programación Orientada a Objetos** — Universidad EAN (noviembre de 2026).
Integrante: Daniel Esteban Ortiz Pinzón.

## Requisitos

- **JDK 17 o superior** (`javac` y `java` en el PATH). No se usan librerías externas.
- Un entorno gráfico para abrir la ventana del juego.
- Todos los archivos están en UTF-8; se compila siempre con `-encoding UTF-8`.

Comprobar la versión: `javac -version` (debe decir 17 o mayor).

## Compilar y ejecutar

Todos los comandos se ejecutan desde la carpeta `codigo_fuente`.

### macOS / Linux

```bash
cd codigo_fuente
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

### Windows (cmd)

```bat
cd codigo_fuente
mkdir out
dir /s /b src\*.java > fuentes.txt
javac --release 17 -encoding UTF-8 -d out @fuentes.txt
java -cp out Main
```

(En PowerShell: `javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName`.)

## Verificar el banco de palabras

Imprime el total, el conteo por categoría y por dificultad. Termina con código 1 si no hay exactamente 200 palabras en 8 categorías de 25.

```bash
java -cp out Main --verificar
```

## Pruebas automáticas (sin JUnit)

Imprime `OK` o `FALLO` por cada prueba y termina con código 1 si alguna falla.

```bash
java -cp out util.PruebasAutomaticas
```

## Generar las capturas del manual (opcional)

Pinta la interfaz en imágenes PNG sin mostrar la ventana (necesita entorno gráfico, no funciona con `-Djava.awt.headless=true`).

```bash
java -cp out util.GeneradorCapturas ../documentacion/capturas
```

## Pruebas de interfaz opcionales con AssertJ Swing

Extra **opcional** en `pruebas_gui/` (el juego sigue usando solo el JDK): automatiza la mayoría de los casos de interfaz de `documentacion/Plan_de_Pruebas.md` con JUnit 4.13.2 y AssertJ Swing 3.17.1. Necesita descargar 6 JAR a `lib_test/` (se verifican con el SHA-1 de Maven Central y no se suben al repositorio).

```bash
cd codigo_fuente
pruebas_gui/descargar_dependencias.sh    # una vez (curl + shasum)
pruebas_gui/ejecutar_pruebas_gui.sh      # compila y ejecuta; código 0 si todo pasa
```

> **Advertencia:** las pruebas manejan el **ratón y el teclado reales** (`java.awt.Robot`) durante 1 a 3 minutos. Avisa a tu equipo y no uses el computador mientras corren. En macOS concede **Accesibilidad** a la terminal o al IDE (Ajustes del Sistema > Privacidad y seguridad). Detalles en `pruebas_gui/LEEME.md`.

## Crear el JAR ejecutable

macOS / Linux / Windows (el comando es el mismo):

```bash
jar --create --file ProyectoAhorcado.jar --main-class Main -C out .
java -jar ProyectoAhorcado.jar
```

## Estructura de paquetes

```
codigo_fuente/
├── README.md
└── src/
    ├── Main.java                  Punto de entrada (paquete por defecto)
    ├── modelo/                    Lógica del juego (sin Swing)
    │   ├── Dificultad, EstadoJuego, ResultadoLetra   (enum)
    │   ├── Palabra                (inmutable)
    │   ├── Categoria, BancoPalabras, DatosPalabras   (200 palabras, 8 x 25)
    │   ├── Pista (abstracta) -> PistaCategoria, PistaLetra, PistaTexto
    │   └── Partida
    ├── controlador/
    │   ├── VistaJuego             (interfaz que desacopla controlador y vista)
    │   └── ControladorJuego
    ├── vista/                     Interfaz Swing
    │   ├── VentanaPrincipal       (implementa VistaJuego)
    │   ├── PanelAhorcado, PanelTeclado, PanelPistas, PanelEstado, Estilo
    │   └── ParteCuerpo (abstracta) -> Cabeza, Torso, BrazoDerecho,
    │                                  BrazoIzquierdo, PiernaDerecha, PiernaIzquierda, Cuerda
    └── util/
        ├── Normalizador, VerificadorBanco
        └── PruebasAutomaticas, GeneradorCapturas
```

Carpetas opcionales: `pruebas_gui/` (pruebas AssertJ Swing y sus scripts) y `lib_test/` (JAR descargados, ignorados por git).

## Conceptos de POO que se pueden señalar en el código

Busque los comentarios `// POLIMORFISMO:`, `// ENCAPSULAMIENTO:`, `// HERENCIA:` y `// ARRAYLIST:`.

- **Herencia y polimorfismo (1):** `Pista` (abstracta) con tres subclases guardadas en un `ArrayList<Pista>` dentro de `Partida`.
- **Herencia y polimorfismo (2):** `ParteCuerpo` (abstracta) con siete subclases guardadas en un `ArrayList<ParteCuerpo>` que `PanelAhorcado` dibuja con `partes.get(i).dibujar(g)`.
- **Encapsulamiento:** campos privados, `Palabra` inmutable y getters que devuelven copias de las listas.
- **Interfaz:** `VistaJuego` evita que el controlador dependa de Swing.

## Decisiones de diseño

**Tildes y Ñ.** La palabra se guarda normalizada: mayúsculas y sin tildes ni diéresis (Á→A, É→E, Í→I, Ó→O, Ú→U, Ü→U), pero **conservando la Ñ**, porque es una letra distinta del alfabeto español y el teclado tiene tecla Ñ. La palabra original con tildes (`textoOriginal`) se guarda aparte y se muestra al terminar la partida. El teclado físico normaliza cualquier letra con tilde a su base antes de comparar. Alfabeto válido: A–Z y Ñ.

**Dificultad por longitud.** Se calcula con la longitud de la palabra normalizada: **FÁCIL** = 3 a 5 letras, **MEDIO** = 6 a 8 letras, **DIFÍCIL** = 9 o más letras. Es una regla única y comprobable (`Dificultad.paraLongitud`). El selector ofrece ALEATORIA, FÁCIL, MEDIO y DIFÍCIL.

**Cuerda.** La horca (base, poste y viga) está siempre visible. Las partes aparecen en este orden: cabeza, torso, brazo derecho, brazo izquierdo, pierna derecha, pierna izquierda y, con el 7.º error, la cuerda, que termina la partida como perdida. "Derecho/izquierdo" se refiere al lado de la pantalla.

## Atajos de teclado

Control+N nueva partida · Control+Q salir · F1 cómo jugar · Alt+J / Alt+A abren los menús · Alt+D dificultad · Alt+C / Alt+L / Alt+E pistas · letras A–Z y Ñ del teclado físico.
