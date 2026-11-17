# Guion del video — Juego del Ahorcado (Java Swing)

**Universidad EAN · Programación Orientada a Objetos · noviembre de 2026**
**Autor y presentador único:** Daniel Esteban Ortiz Pinzón (primera persona, singular).
**Video publicado:** https://youtu.be/R6azp1shkIs
**Duración:** 6 min 37 s; el guion tiene 1111 palabras (a tres palabras por segundo, unos 370 segundos de voz).

> **Enlace del video:** https://youtu.be/R6azp1shkIs

---

## Resumen de segmentos

| N | Segmento | Clips | Palabras | Segundos estimados |
|---|---|---|---|---|
| 1 | Introduccion | C00, C01 | 65 | 22 |
| 2 | Estructura del proyecto | C02, C03 | 74 | 25 |
| 3 | Arquitectura M V C | C04 | 80 | 27 |
| 4 | Demo: ganar una partida | C05 | 75 | 25 |
| 5 | Demo: perder y las siete partes | C06 | 99 | 33 |
| 6 | Pistas, teclado y dificultad | C07, C08, C09 | 145 | 48 |
| 7 | Encapsulación | C10 | 64 | 21 |
| 8 | Herencia y polimorfismo: las pistas | C11 | 86 | 29 |
| 9 | Polimorfismo en el dibujo | C12 | 113 | 38 |
| 10 | Array List | C13 | 79 | 26 |
| 11 | Pruebas | C14, C15 | 118 | 39 |
| 12 | Documentación | C16 | 50 | 17 |
| 13 | Cierre | C17 | 63 | 21 |

El texto de cada segmento es exactamente el enviado al narrador (sin símbolos ni dígitos, con los números en palabras). Las líneas de código citadas abajo se verificaron con `rg -n` el 2026-11-16.

---

## Segmento 1 - Introduccion

| Clip | Texto en pantalla |
|---|---|
| C00, C01 | Daniel Esteban Ortiz Pinzón · Universidad EAN; 1 · Introducción |

> Hola, soy Daniel Esteban Ortiz Pinzón, estudiante de la Universidad EAN, en el curso de Programación Orientada a Objetos. En este video presento mi Juego del Ahorcado, hecho con Java Swing, usando solo el J D K y sin librerías externas. Primero muestro cómo funciona. Después explico cómo apliqué encapsulación, herencia, polimorfismo y Array List. Y al final, cómo comprobé que todo funciona con pruebas.

## Segmento 2 - Estructura del proyecto

| Clip | Texto en pantalla |
|---|---|
| C02, C03 | 2 · Estructura del proyecto; modelo · controlador · vista · util |

> El proyecto vive en mi repositorio de GitHub, con cuatro carpetas: código fuente, documentación, estudio y video. Dentro del código fuente, la carpeta de fuentes tiene cuatro paquetes: modelo, controlador, vista y util, más la clase Main, que arranca el juego. Además del código, el repositorio incluye el manual de usuario, el plan de pruebas y una guía de estudio. El banco tiene doscientas palabras, repartidas en ocho categorías de veinticinco palabras cada una.

## Segmento 3 - Arquitectura M V C

| Clip | Texto en pantalla |
|---|---|
| C04 | 3 · Arquitectura MVC |

> Uso el patrón M V C. En el paquete modelo está la lógica: Palabra, Partida y las pistas, y ahí no hay nada de Swing. El controlador, Controlador Juego, recibe las acciones del jugador y cambia el modelo. La vista dibuja todo con Swing y Graphics dos D, sin imágenes. Entre el controlador y la ventana hay una interfaz, Vista Juego, así que el controlador no conoce la ventana concreta. En este diagrama de clases se ve cómo se conectan.

## Segmento 4 - Demo: ganar una partida

| Clip | Texto en pantalla |
|---|---|
| C05 | 4 · Demo: ganar |

> Esta es la pantalla principal. A la izquierda está la horca, y a la derecha el estado con la palabra en guiones, las pistas y el teclado en pantalla. Elijo letras con el ratón. Si la letra está en la palabra, aparece en su lugar; si no, se dibuja una parte del muñeco. Cada tecla usada queda deshabilitada. Al completar la palabra aparece el diálogo Ganaste, con la palabra original, y puedo jugar de nuevo.

## Segmento 5 - Demo: perder y las siete partes

| Clip | Texto en pantalla |
|---|---|
| C06 | 5 · Demo: perder; 7 errores = partida perdida |

> Ahora pierdo a propósito. La horca ya está visible desde el inicio: base, poste y viga. Elijo letras que no están en la palabra, una por una, y me detengo en cada parte. Con cada error aparece una parte, siempre en este orden: la cabeza, el torso, el brazo derecho, el brazo izquierdo, la pierna derecha y la pierna izquierda. El séptimo error dibuja la cuerda y termina la partida. El diálogo dice Perdiste y muestra la palabra, los errores y la categoría. Ese orden no lo decide la ventana: lo decide la lista de partes de Panel Ahorcado.

## Segmento 6 - Pistas, teclado y dificultad

| Clip | Texto en pantalla |
|---|---|
| C07, C08, C09 | 6 · Pistas, teclado y dificultad |

> Ahora las pistas. Hay tres y cada una se usa una sola vez por partida. La de categoría dice a qué grupo pertenece la palabra. La de letra revela una letra y no cuenta como error: el muñeco no cambia. La pista escrita muestra una frase propia de cada palabra. Al usar las tres, los botones quedan deshabilitados y las pistas restantes llegan a cero. Si repito una letra, el juego avisa que no se penaliza. También puedo jugar con el teclado físico, incluidas las letras con tilde y la eñe, porque un normalizador quita las tildes pero conserva la eñe. Si abro la ayuda con la tecla F uno, el juego ignora el teclado mientras el diálogo esté abierto. Y con el selector cambio la dificultad: fácil es de tres a cinco letras, medio de seis a ocho, y difícil de nueve en adelante.

## Segmento 7 - Encapsulación

| Clip | Texto en pantalla |
|---|---|
| C10 | 7 · Encapsulación |

> Primero, encapsulación. Palabra es una clase inmutable: sus campos son privados y finales, solo se leen con getters, y una vez creada no cambia. En Categoría, la lista de palabras es privada y el getter devuelve una copia, para que nadie de afuera la modifique. En Partida todo el estado es privado y solo cambia mediante sus métodos públicos, como adivinar y usar pista.

## Segmento 8 - Herencia y polimorfismo: las pistas

| Clip | Texto en pantalla |
|---|---|
| C11 | 8 · Herencia y polimorfismo |

> Ahora, herencia y polimorfismo en las pistas. Pista es una clase abstracta. Su método usar es una plantilla: si la pista ya se usó devuelve nulo; si no, la marca como usada y llama a obtener. Obtener es abstracto, y lo implementan tres subclases: Pista Categoría, Pista Letra y Pista Texto. En Partida guardo una lista de Pista con las tres. Cuando llamo usar sobre un elemento, Java ejecuta la versión de obtener de la subclase real, y Partida no necesita saber de qué tipo es.

## Segmento 9 - Polimorfismo en el dibujo

| Clip | Texto en pantalla |
|---|---|
| C12 | 9 · Polimorfismo en el dibujo |

> El mismo patrón aparece en la vista. Parte Cuerpo es una clase abstracta con un método dibujar abstracto, y tiene siete subclases: Cabeza, Torso, los dos brazos, las dos piernas y la Cuerda. Panel Ahorcado las guarda en una lista del tipo padre. Al pintar, recorre solo las primeras partes, tantas como errores, y llama a dibujar en cada una. El panel no tiene ningún if por tipo de parte. Dibuja sobre un lienzo virtual de trescientos por cuatrocientos que se escala al tamaño del panel, así el muñeco se ve bien aunque cambie el tamaño de la ventana. Y la ventana implementa Vista Juego, por eso el controlador no depende de Swing.

## Segmento 10 - Array List

| Clip | Texto en pantalla |
|---|---|
| C13 | 10 · ArrayList |

> Uso Array List en varias partes del proyecto, porque las colecciones cambian de tamaño mientras se juega. Categoría guarda sus palabras. Banco Palabras guarda las categorías y una lista de palabras ya usadas. Partida guarda las letras correctas, las incorrectas y las pistas. Y Panel Ahorcado guarda las partes del cuerpo en el orden en que se dibujan. Para no repetir palabras armo las candidatas, quito las usadas y elijo una al azar; cuando se agotan, reinicio la lista.

## Segmento 11 - Pruebas

| Clip | Texto en pantalla |
|---|---|
| C14, C15 | 11 · Pruebas; 76 pruebas, 0 fallos |

> Para probar sin librerías externas escribí pruebas automáticas que revisan el banco, el modelo, el controlador y el normalizador. Esta es la salida real: setenta y seis pruebas ejecutadas y cero fallos. El comando verificar confirma doscientas palabras, en ocho categorías de veinticinco. Además hay una suite opcional de interfaz con Assert J Swing, con treinta y cinco pruebas. Esa suite encontró un defecto real: con el foco en el selector de dificultad, teclear una letra la jugaba y además cambiaba la dificultad. Lo corregí desactivando la selección por inicial del selector, y esa prueba pasó a verde. El plan de pruebas también documenta una prueba intermitente, que a veces no encuentra el diálogo de fin a tiempo.

## Segmento 12 - Documentación

| Clip | Texto en pantalla |
|---|---|
| C16 | 12 · Documentación |

> La documentación está en su propia carpeta: el manual de usuario, el plan de pruebas, los diagramas de clases en U M L y cuatro diagramas hechos con Archify, de la estructura de clases, la secuencia de una letra, la secuencia de una pista y el ciclo de la partida.

## Segmento 13 - Cierre

| Clip | Texto en pantalla |
|---|---|
| C17 | 13 · Cierre; github.com/danielorzt/Ahorcado_POO_EAN |

> Con este proyecto apliqué encapsulación, herencia y polimorfismo en el modelo y en la vista, usé Array List para el banco, las letras, las pistas y las partes del cuerpo, y lo comprobé con pruebas automáticas. El código, el manual y el plan de pruebas están en el repositorio. Gracias por ver mi proyecto. Soy Daniel Esteban Ortiz Pinzón, de la Universidad EAN.

---

## Referencias de código verificadas (`rg -n`, 2026-11-16)

| Concepto | Dónde mostrarlo |
|---|---|
| Encapsulación: `Palabra` inmutable | `modelo/Palabra.java:12-17` |
| Encapsulación: lista privada y copia | `modelo/Categoria.java:11, :37-39` |
| Encapsulación: estado de `Partida` | `modelo/Partida.java:15-21` |
| Herencia: `Pista` abstracta y método plantilla | `modelo/Pista.java:7, :33, :48` |
| Herencia: subclases de `Pista` | `modelo/PistaCategoria.java:4; modelo/PistaLetra.java:7; modelo/PistaTexto.java:4` |
| Polimorfismo (pistas) | `modelo/Partida.java:36-39, :96; modelo/Pista.java:38` |
| Herencia: `ParteCuerpo` y siete subclases | `vista/ParteCuerpo.java:11, :39; vista/PanelAhorcado.java:42-48` |
| Polimorfismo (dibujo) | `vista/PanelAhorcado.java:98` |
| Interfaz `VistaJuego` | `controlador/VistaJuego.java:9; controlador/ControladorJuego.java:19; vista/VentanaPrincipal.java:39, :62` |
| ArrayList | `modelo/Categoria.java:11; modelo/BancoPalabras.java:13, :70, :72; modelo/Partida.java:18; vista/PanelAhorcado.java:28; vista/PanelTeclado.java:18` |
| Teclado físico frente a diálogo modal | `vista/VentanaPrincipal.java:239 (dispatcher), :246 (ventana activa)` |
| Lienzo virtual de 300 x 400 | `vista/PanelAhorcado.java:21-23, :93` |
| No repetir palabras | `modelo/BancoPalabras.java:70-76` |
| Tildes y Ñ (normalizador) | `util/Normalizador.java:45, :47` |
| Defecto H-6 corregido | `vista/VentanaPrincipal.java:143 (`setKeySelectionManager`)` |

## Afirmaciones verificadas

| Afirmación | Comando | Resultado (2026-11-16) |
|---|---|---|
| 76 pruebas automáticas, 0 fallos | `java -cp out util.PruebasAutomaticas` | `Pruebas ejecutadas: 76 | Fallos: 0` |
| 200 palabras, 8 categorías de 25 | `java -cp out Main --verificar` | `Resultado: CORRECTO (200 palabras, 8 categorías x 25)` |
| Dificultad por longitud | `rg -n "paraLongitud" codigo_fuente/src/modelo/Dificultad.java` | FÁCIL 3 a 5, MEDIO 6 a 8, DIFÍCIL 9 o más |
| Pruebas de interfaz | `rg -c "void pm[0-9]{2}[a-z]?_" codigo_fuente/pruebas_gui/AhorcadoGUITest.java` | 40 (el video dice 35, la cifra al grabar; después se agregaron `pm14b`, `pm22`, `pm23a`, `pm23b` y `pm24b`) |
| Siete errores pierden | `rg -n "MAX_ERRORES = 7" codigo_fuente/src/modelo/Partida.java` | línea 13 |
| H-6 hallado por pruebas y corregido | `rg -n "H-6" documentacion/Plan_de_Pruebas.md codigo_fuente/src/vista/VentanaPrincipal.java` | Plan de pruebas, sección 5.2 y hallazgos; corrección en VentanaPrincipal.java:143 |

