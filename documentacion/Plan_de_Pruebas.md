# Plan de Pruebas — Juego del Ahorcado interactivo (Java Swing)

Universidad EAN · Programación Orientada a Objetos · noviembre de 2026
Integrante: Daniel Esteban Ortiz Pinzón

> Criterio de estados: la columna **Estado** dice `APROBADA` solo cuando la comprobación imprimió `OK` en la ejecución registrada en la sección 5, y `FALLA` en caso contrario. Los casos de interfaz gráfica que cubre la suite OPCIONAL con AssertJ Swing (sección 3.3) muestran `APROBADA`/`FALLA` según la ejecución registrada en la sección 5.2. Lo que no está cubierto por una comprobación automática (aspecto visual, tildes del teclado físico, Alt+letra, VoiceOver) se indica de forma explícita: el aspecto visual se revisa en las capturas y el resto figura como limitación en la sección 7.

---

## 1. Objetivo, alcance y entorno

**Objetivo.** Comprobar que el juego cumple la especificación funcional del enunciado: palabra secreta de un banco de 200 palabras, 7 errores con el ahorcado dibujado en orden, tres pistas, entrada por teclado físico y en pantalla, accesibilidad, diálogo de fin y selector de dificultad.

**Alcance.**
- Incluido: modelo (`Partida`, `Palabra`, `Pista*`, `BancoPalabras`, `Dificultad`), `Normalizador`, controlador (`ControladorJuego`) y el orden de las partes del dibujo (`PanelAhorcado`).
- Incluido (interfaz): comportamiento de la ventana real (`VentanaPrincipal`): botones deshabilitados, teclado físico, foco/tabulación, mnemónicos, diálogos, tamaño mínimo.
- Fuera de alcance: rendimiento, pruebas de carga, otros sistemas operativos distintos de los listados.

**Entorno.**

| Elemento | Valor |
|---|---|
| JDK | 17 (`javac`/`java` 17, compilación con `--release 17`) |
| Sistema operativo | macOS (entorno gráfico para las pruebas de interfaz) |
| Librerías | Ninguna externa para el juego ni para `util.PruebasAutomaticas`. Solo la suite OPCIONAL de interfaz (carpeta `codigo_fuente/pruebas_gui/`) usa JUnit 4.13.2 y AssertJ Swing 3.17.1 como JAR descargados a `codigo_fuente/lib_test/` (no se suben al repositorio) |
| Codificación | UTF-8 (`-encoding UTF-8`) |

**Cómo ejecutar (desde la carpeta `codigo_fuente`).**

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java")

java -cp out util.PruebasAutomaticas     # pruebas automáticas (imprime OK/FALLO; código 1 si falla alguna)
java -cp out Main --verificar            # verificación del banco (200 palabras, 8 x 25)
java -cp out Main                        # juego (pruebas de interfaz)
```

---

## 2. Estrategia de pruebas

| Tipo | Qué cubre | Cómo se ejecuta | Evidencia |
|---|---|---|---|
| **Automatizadas** (sin JUnit, solo JDK) | Reglas del juego, banco de palabras, normalización de tildes/Ñ, pistas, controlador con una vista falsa (`VistaFalsa`), orden de las partes del dibujo | `java -cp out util.PruebasAutomaticas` y `java -cp out Main --verificar` | Salida por consola: `OK` / `FALLO` por comprobación y resumen final |
| **Automatizadas de interfaz (OPCIONALES)** | Lo mismo que varios casos de interfaz, pero con la ventana real manejada por `java.awt.Robot`: clics, teclado físico, foco/Tab, diálogo de fin, redimensionado | `codigo_fuente/pruebas_gui/descargar_dependencias.sh` (una vez) y `codigo_fuente/pruebas_gui/ejecutar_pruebas_gui.sh`. **Toman el control del ratón y del teclado 1 a 3 minutos** | Salida por consola: `OK`/`FALLA` por método `pmNN_...` y resumen (sección 5.2) |
| **Interfaz (ventana real)** | Lo que depende de la ventana real: clics, teclado físico, botones deshabilitados, foco, mnemónicos, diálogos, tamaño mínimo | El autor sigue la sección 8 con el juego abierto | Casilla marcada + captura/video |

Criterio: toda regla de negocio se verifica automáticamente (es determinista y repetible, con `iniciarPartidaConPalabra` para evitar el azar); lo visual se revisa en las capturas y la interacción real se ejecuta con la suite de interfaz.

---

## 3. Casos de prueba

Estados: `APROBADA` (automatizada que imprimió `OK` en la ejecución registrada) · `APROBADA (inspección visual)` (revisión de las capturas) · `FALLA`.
Los nombres entre comillas son los nombres **exactos** de las comprobaciones en `util.PruebasAutomaticas`.

### 3.1 Casos automatizados

| ID | Requisito | Tipo | Entrada / pasos | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|---|---|
| PA-01 | R2 Victoria (adivinar todas las letras = GANADA) | Automatizada | Palabra `MURCIÉLAGO`; se juegan `M,U,R,C,I,E,L,A,G` y luego `o` (minúscula). Con el controlador: palabra `SOL`, letras `S,O,L` | Antes de la última letra `estaCompleta()` es falso; después estado `GANADA`, `palabraOculta()` = `M U R C I E L A G O`; el controlador llama a `mostrarFin` | OK en: "Partida: estaCompleta es falso con una letra pendiente"; "Partida: adivinar acepta minúsculas (ACERTADA)"; "Partida: ganar al descubrir todas las letras"; "Partida: palabraOculta final muestra la palabra"; "Controlador: al ganar avisa a la vista (mostrarFin)" | APROBADA |
| PA-02 | R2 Derrota con 7 errores y orden exacto de las partes | Automatizada | Palabra `GATO`; letras erróneas `X,Z,Q,W,K,J,V` en ese orden. `PanelAhorcado.getNombresPartes()` | Cada letra es `FALLADA` y el error n = n; solo el 7.º error pone `PERDIDA`; 0 intentos restantes; letras incorrectas en orden de ingreso; las 7 partes quedan en el orden Cabeza, Torso, Brazo derecho, Brazo izquierdo, Pierna derecha, Pierna izquierda, Cuerda | OK en: "Partida: 7 errores en orden, solo el 7.º pierde"; "Partida: derrota deja 0 intentos restantes"; "Partida: letras incorrectas conservan el orden"; "Controlador: 7 errores terminan la partida como PERDIDA"; "Dibujo: las 7 partes están en el orden de la especificación" | APROBADA |
| PA-03 | R5 Letra repetida acertada sin penalizar | Automatizada | `GATO`: `G` y luego `g` | Resultado `REPETIDA`; letras correctas sigue en 1; el controlador no altera la partida | OK en: "Partida: letra repetida acertada = REPETIDA sin cambios"; "Controlador: letra repetida no cambia la partida" | APROBADA |
| PA-04 | R5 Letra repetida fallada sin penalizar | Automatizada | `GATO`: `X` y luego `x` | Resultado `REPETIDA`; errores sigue en 1 | OK en: "Partida: letra repetida fallada no suma error" | APROBADA |
| PA-05 | R5 Caracteres no válidos (dígito, símbolo, espacio) | Automatizada | `GATO`: `'1'`, `' '`, `'-'`, `'!'`; controlador: `'5'` | Todos `INVALIDA`, 0 errores; el controlador muestra el mensaje "Solo se aceptan letras…" y no penaliza | OK en: "Partida: caracteres inválidos (1, espacio, -, !) son INVALIDA y no penalizan"; "Controlador: carácter inválido muestra mensaje y no penaliza"; "Normalizador: dígitos y espacios no son letras" | APROBADA |
| PA-06 | R5 Teclas tras terminar la partida | Automatizada | Tras perder y tras ganar, se envía una letra válida; controlador: `Z` tras ganar | `PARTIDA_TERMINADA`; errores y estado no cambian; la vista no se actualiza | OK en: "Partida: tras perder, una letra válida es PARTIDA_TERMINADA y no cambia nada"; "Partida: tras ganar, una letra válida es PARTIDA_TERMINADA"; "Controlador: teclas tras el fin se ignoran" | APROBADA |
| PA-07 | R4(a) Pista de categoría | Automatizada | `MURCIÉLAGO` (Animales): `usarPista(0)` | Devuelve `Categoría: Animales`; quedan 2 pistas | OK en: "Pistas: al inicio quedan 3"; "Pistas: la de categoría devuelve 'Categoría: Animales'"; "Pistas: tras usar una quedan 2"; "Controlador: usarPista muestra la pista y baja el contador" | APROBADA |
| PA-08 | R4(b) Pista de letra: no cuenta como error y no repite una ya revelada | Automatizada | `MURCIÉLAGO`: se juegan `M`,`U`; luego `usarPista(1)` | Se añade exactamente 1 letra nueva a las correctas (distinta de `M`,`U`); errores = 0; mensaje `Se reveló la letra: X` | OK en: "PistaLetra: no cuenta como error"; "PistaLetra: revela una letra nueva (no repite las ya reveladas)"; "PistaLetra: el mensaje indica la letra revelada" | APROBADA |
| PA-09 | R4(c) Pista escrita | Automatizada | `MURCIÉLAGO` con pista `Mamífero volador nocturno`: `usarPista(2)` | Devuelve el texto exacto con tilde | OK en: "Pistas: la pista escrita devuelve el texto con tildes" | APROBADA |
| PA-10 | R4 Reutilizar una pista (lógica) | Automatizada | `usarPista(0)` dos veces; `usarPista(1)` dos veces; índices `-1` y `3` | La segunda vez devuelve `null` y `pistaDisponible` es falso; índice inválido → `null` (el botón deshabilitado se prueba en M-09) | OK en: "Pistas: una pista usada no se puede repetir (null)"; "PistaLetra: solo se puede usar una vez"; "Pistas: índice inválido devuelve null"; "Pista (polimorfismo): usar() es plantilla y funciona igual en las 3 subclases" | APROBADA |
| PA-11 | R4/R2 Pista de letra que completa la palabra (gana) | Automatizada | Palabra `OSO`; se juega `O`; `usarPista(1)` (única letra pendiente: `S`) | Estado `GANADA` con 0 errores; después ya no se pueden usar pistas (`null`) | OK en: "PistaLetra: si revela la última letra, la partida se gana"; "Pistas: no se usan pistas con la partida terminada" | APROBADA |
| PA-12 | R6 Reinicio / nueva partida (estado limpio, pistas restauradas) | Automatizada | Partida con 1 error y 1 pista usada → `new Partida(...)`; controlador: `iniciarNuevaPartida(FACIL)` | 0 errores, sin letras, 3 pistas restantes, `EN_CURSO` | OK en: "Partida: una partida nueva reinicia errores, letras y pistas"; "Controlador: iniciar partida actualiza la vista"; "Controlador: nueva partida con dificultad FÁCIL reinicia todo" | APROBADA |
| PA-13 | R1 Selector de dificultad (FÁCIL/MEDIO/DIFÍCIL/ALEATORIA devuelven palabras del nivel) | Automatizada | `elegirAleatoria(d)` 20 veces por cada dificultad; `elegirAleatoria(null)` (ALEATORIA) | Cada palabra devuelta tiene la dificultad pedida; ALEATORIA devuelve palabras del banco | OK en: "Banco: elegirAleatoria(dificultad) respeta el nivel pedido"; "Banco: 200 sorteos aleatorios no repiten palabra" (ALEATORIA). El texto del combo (4 opciones) se comprueba en M-12 | APROBADA |
| PA-14 | R1 No repetir palabra hasta agotar el conjunto | Automatizada | 200 sorteos `elegirAleatoria(null)` en un banco nuevo; luego uno más | 200 palabras distintas; el sorteo 201 reinicia y devuelve una palabra | OK en: "Banco: 200 sorteos aleatorios no repiten palabra"; "Banco: tras agotar las palabras reinicia y sigue sorteando". Verificación complementaria por nivel (fuera de la suite, ver H-1): FÁCIL 56/56, MEDIO 73/73, DIFÍCIL 71/71 distintas | APROBADA |
| PA-15 | R1 Banco de 200 palabras, 8 categorías x 25 | Automatizada | `new BancoPalabras()`; `Main --verificar` | 200 palabras, 8 categorías, 25 cada una | OK en: "Banco: 200 palabras"; "Banco: 8 categorías"; "Banco: cada categoría tiene 25 palabras"; "Banco: resumenPorCategoria tiene 8 líneas"; `--verificar` imprimió `Resultado: CORRECTO (200 palabras, 8 categorías x 25)` | APROBADA |
| PA-16 | R1 Sin duplicados | Automatizada | Recorrer las 200 palabras con un `HashSet` | Ningún texto normalizado repetido | OK en: "Banco: sin palabras duplicadas"; "Banco: contiene() encuentra una palabra del banco" | APROBADA |
| PA-17 | R1 Mayúsculas y sin espacios | Automatizada | Recorrer las 200 palabras | Todas en mayúsculas; ninguna con espacios (ni original ni normalizada); `new Palabra("DOS PALABRAS",…)` lanza `IllegalArgumentException` | OK en: "Banco: todas en mayúsculas"; "Banco: ninguna con espacios"; "Palabra: rechaza espacios (IllegalArgumentException)" | APROBADA |
| PA-18 | R1/D1 Sin tildes en el texto normalizado (conserva Ñ) | Automatizada | Cada carácter de `getTexto()` debe estar en A–Z o ser Ñ | Ningún carácter fuera del alfabeto válido | OK en: "Banco: texto normalizado sin tildes (solo A-Z y Ñ)"; "Palabra: texto normalizado sin tildes"; "Palabra: conserva el texto original con tildes" | APROBADA |
| PA-19 | R4(c) Las 200 palabras tienen pista no vacía | Automatizada | Recorrer las 200 palabras | `getPista()` no nulo ni en blanco | OK en: "Banco: todas tienen pista no vacía" | APROBADA |
| PA-20 | R1/D2 Regla de dificultad por longitud y mínimo 30 por nivel | Automatizada | Recorrer las 200 palabras; `Dificultad.paraLongitud(3,5,6,8,9)`; `contarPorDificultad` | FÁCIL = 3–5, MEDIO = 6–8, DIFÍCIL ≥ 9 letras; cada nivel con ≥ 30; la suma es 200 | OK en: "Banco: regla de dificultad por longitud (mín. 3 letras)"; "Dificultad: 3-5 FÁCIL, 6-8 MEDIO, 9+ DIFÍCIL"; "Palabra: dificultad por longitud (10 letras = DIFÍCIL)"; "Banco: al menos 30 palabras FÁCIL / MEDIO / DIFÍCIL"; "Banco: la suma por dificultad es 200". Real (`--verificar`): FÁCIL 56, MEDIO 73, DIFÍCIL 71 | APROBADA |
| PA-21 | R5/D1 Tildes y Ñ (lógica): `á` cuenta como A, Ñ distinta de N | Automatizada | `PIÑA`: `N` y `ñ`; `GATO`: `á`; `Normalizador.normalizarLetra('á','ñ','Ü')`; `contiene('é')` | `N` es `FALLADA`, `ñ` es `ACERTADA`; `á` → `A` acertada; `Ü` → `U`; `ñ` → `Ñ` | OK en: "Partida: la Ñ es una letra distinta de la N"; "Partida: letra con tilde se normaliza (á cuenta como A)"; "Normalizador: normalizarLetra('á') = 'A'"; "Normalizador: normalizarLetra('ñ') = 'Ñ'"; "Normalizador: normalizarLetra('Ü') = 'U'"; "Palabra: contiene() ignora tildes" | APROBADA |
| PA-22 | D1 Normalizador (cadenas) | Automatizada | `normalizar("MURCIÉLAGO")`, `("pingüino")`, `("ñandú")`, `(null)`; `esLetraValida` | `MURCIELAGO`, `PINGUINO`, `ÑANDU`, cadena vacía; A, Ñ y é válidas, `-` no | OK en: "Normalizador: quita tildes (MURCIÉLAGO -> MURCIELAGO)"; "Normalizador: sube a mayúsculas y quita diéresis (pingüino -> PINGUINO)"; "Normalizador: conserva la Ñ (ñandú -> ÑANDU)"; "Normalizador: normalizar(null) devuelve vacío"; "Normalizador: esLetraValida acepta A, Ñ y é" | APROBADA |
| PA-23 | R2/R7 Dibujo: límites del contador de errores del panel | Automatizada | `PanelAhorcado.setErrores(99)` y `setErrores(-3)` | Se limita a 7 y a 0 | OK en: "Dibujo: setErrores limita a 7"; "Dibujo: setErrores no baja de 0" | APROBADA |
| PA-24 | R5 Estado inicial y encapsulamiento de la partida | Automatizada | `new Partida("MURCIÉLAGO")`; modificar la lista devuelta por `getLetrasCorrectas()` | `EN_CURSO`, 0 errores, 7 intentos, `_ _ _ _ _ _ _ _ _ _`; la lista devuelta es una copia | OK en: "Partida: empieza EN_CURSO con 0 errores"; "Partida: palabraOculta inicial son guiones separados"; "Partida: getLetrasCorrectas devuelve una copia (encapsulamiento)" | APROBADA |
| PA-25 | R5 Controlador: la vista se actualiza al jugar | Automatizada | `VistaFalsa` + `iniciarPartidaConPalabra(SOL)`; letra `S` | `actualizar()` se invoca en cada jugada válida; sin `mostrarFin` mientras la partida sigue | OK en: "Controlador: letra acertada actualiza la vista" | APROBADA |

### 3.2 Casos de interfaz gráfica (PM)

> Los casos con `(auto)` o `(parcial)` fueron ejecutados por la suite opcional de la sección 3.3 (ejecución registrada en la sección 5.2: 40 pruebas, 0 fallos). PM-26 se revisa en las capturas. Lo que no se verificó dentro de los casos `parcial` figura como limitación en la sección 7. Para no cerrar la aplicación durante la suite, las pruebas de «Salir» interceptan `System.exit` con un `SecurityManager` instalado solo dentro de esas pruebas.

| ID | Requisito | Tipo | Entrada / pasos | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|---|---|
| PM-01 | R3 Pantalla inicial: horca, palabra con guiones, listas, intentos | Interfaz | Abrir el juego (`java -cp out Main`) | Se ve la horca (base, poste y viga) sin cuerpo; la palabra como `_ _ _`; listas de letras correctas e incorrectas vacías; "intentos restantes" = 7; mensaje "Nueva partida: la palabra tiene N letras." | OK en `AhorcadoGUITest`: `pm01_pantallaInicial` (el aspecto visual de la horca se revisa en la captura `01-inicio.png`) | APROBADA (auto + inspección visual) |
| PM-02 | R2 Victoria en la interfaz | Interfaz | Jugar acertando todas las letras (con teclado en pantalla) | Las letras acertadas aparecen en la palabra y en la lista de correctas; al completar, se abre el diálogo "¡Ganaste!" con la palabra revelada | OK en `AhorcadoGUITest`: `pm02_victoriaPorClicsEnLetras` | APROBADA (auto) |
| PM-03 | R2 Derrota: dibujo de las 7 partes en orden | Interfaz | Pulsar letras que no estén en la palabra, de una en una, y mirar el dibujo tras cada error | Orden: 1 cabeza, 2 torso, 3 brazo derecho, 4 brazo izquierdo, 5 pierna derecha, 6 pierna izquierda, 7 cuerda; la horca ya estaba desde el inicio; al 7.º error aparece "¡Perdiste!" con la palabra | OK en `AhorcadoGUITest`: `pm03_derrotaEnElSeptimoErrorYOrdenDePartes`, `pm03b_derrotaMuestraLaPalabraRealConTildes` (orden leído de `PanelAhorcado.getNombresPartes()/getErrores()`; que el trazo se vea bien se revisa en las capturas `04` a `07`) | APROBADA (auto + inspección visual) |
| PM-04 | R3 Listas e intentos se actualizan | Interfaz | Acertar 1 letra y fallar 2 | La letra acertada va a "correctas", las 2 falladas a "incorrectas" y los intentos restantes bajan a 5 | OK en `AhorcadoGUITest`: `pm04a_letrasCorrectasAparecenEnSuPosicion`, `pm04b_letraIncorrectaAumentaLosErrores` | APROBADA (auto) |
| PM-05 | R5 Letra repetida (teclas deshabilitadas / teclado físico) | Interfaz | Pulsar una letra con el ratón; ver que su tecla queda deshabilitada; escribir la misma letra en el teclado físico | La tecla en pantalla se deshabilita; al repetir con el teclado físico el mensaje dice "Ya habías probado la letra X. No se penaliza." y los intentos no bajan | OK en `AhorcadoGUITest`: `pm05a_letraRepetidaPorClicNoPenaliza`, `pm05b_letraRepetidaPorTeclaFisicaNoPenaliza` | APROBADA (auto) |
| PM-06 | R5 Caracteres no válidos en el teclado físico | Interfaz | Con la ventana activa teclear `7`, `-`, `!`, barra espaciadora | Nada cambia en la partida (0 errores nuevos); los dígitos/símbolos no se consumen como letras | OK en `AhorcadoGUITest`: `pm06_teclasFisicasQueNoSonLetrasSeIgnoran` (dígitos, `-`, `,`, `!`, espacio) | APROBADA (auto) |
| PM-07 | R5 Teclas tras terminar la partida | Interfaz | Perder o ganar, cerrar el diálogo con Esc y teclear letras / pulsar teclas en pantalla | El teclado en pantalla está deshabilitado; el teclado físico no cambia nada; hay que usar "Nueva partida" (ver H-2) | OK en `AhorcadoGUITest`: `pm07a_trasPerderTodoQuedaDeshabilitadoYLasTeclasSeIgnoran`, `pm07b_trasGanarTodoQuedaDeshabilitadoYLasTeclasSeIgnoran` | APROBADA (auto) |
| PM-08 | R4(a) Pista de categoría en la interfaz | Interfaz | Pulsar el botón "Categoría" | El área de pistas muestra `Categoría: <nombre>`; el botón se deshabilita; "Pistas restantes: 2" | OK en `AhorcadoGUITest`: `pm08_pistaDeCategoriaMuestraTextoYDeshabilitaElBoton` | APROBADA (auto) |
| PM-09 | R4(b) Pista de letra en la interfaz | Interfaz | Acertar 1 letra; pulsar el botón "Letra" | Se revela una letra nueva en la palabra y su tecla se deshabilita; los intentos restantes NO cambian; mensaje "Se reveló la letra: X"; botón deshabilitado | OK en `AhorcadoGUITest`: `pm09_pistaDeLetraRevelaUnaLetraSinSumarError` | APROBADA (auto) |
| PM-10 | R4(c) Pista escrita en la interfaz | Interfaz | Pulsar "Pista escrita" | El área de pistas muestra el texto de la palabra (con tildes); botón deshabilitado; "Pistas restantes" baja | OK en `AhorcadoGUITest`: `pm10_pistaEscritaMuestraElTextoDeLaPalabra` | APROBADA (auto) |
| PM-11 | R4 Reutilizar una pista: botón deshabilitado | Interfaz | Usar una pista e intentar pulsarla otra vez (ratón y Tab/Espacio) | El botón está deshabilitado y no responde; la pista no se repite; el contador no baja más | OK en `AhorcadoGUITest`: `pm11_ningunaPistaSePuedeReutilizar` (y el segundo uso en `pm08`, `pm09`, `pm10`) | APROBADA (auto) |
| PM-12 | R4 Pista de letra que completa la palabra (gana) | Interfaz | Dejar la palabra con una sola letra pendiente y pulsar "Letra" | Aparece el diálogo "¡Ganaste!" (0 errores) | OK en `AhorcadoGUITest`: `pm12_pistaDeLetraQueCompletaLaPalabraGana` (palabra `OSO`) | APROBADA (auto) |
| PM-13 | R4 Pistas restantes y área de pistas acumulativa | Interfaz | Usar las 3 pistas | "Pistas restantes: 0", los 3 botones deshabilitados y las 3 pistas listadas en el área de solo lectura | OK en `AhorcadoGUITest`: `pm13_tresPistasUsadasQuedanListadasYDeshabilitadas` | APROBADA (auto) |
| PM-14 | R6 Nueva partida (botón, menú, Ctrl+N): estado limpio y pistas restauradas | Interfaz | A mitad de partida, con errores y pistas usadas, pulsar "Nueva partida" (probar también Juego > Nueva partida y Ctrl+N) | Dibujo sin cuerpo, listas vacías, intentos = 7, teclado habilitado, 3 botones de pista habilitados, "Pistas restantes: 3", área de pistas vacía | OK en `AhorcadoGUITest`: `pm14_nuevaPartidaReiniciaTodoYCargaOtraPalabra` (botón) y `pm14b_menuNuevaPartidaYCtrlNReiniciaLaPartida` (menú Juego > Nueva partida y Ctrl+N) | APROBADA (auto) |
| PM-15 | R1 Selector de dificultad en la interfaz | Interfaz | Elegir cada opción (ALEATORIA, FÁCIL, MEDIO, DIFÍCIL) y pulsar "Nueva partida" varias veces | El combo ofrece exactamente 4 opciones; la longitud indicada en el mensaje inicial es 3–5 (FÁCIL), 6–8 (MEDIO) o ≥ 9 (DIFÍCIL); ALEATORIA mezcla longitudes | OK en `AhorcadoGUITest`: `pm15a_selectorDeDificultadTieneLasCuatroOpciones`, `pm15b_cadaDificultadSorteaPalabrasDeSuLongitud` | APROBADA (auto) |
| PM-16 | R5/D1 Tildes y Ñ con el teclado físico | Interfaz | Teclear una vocal con tilde (`á`, `é`, `ó`) y `ñ` | La vocal con tilde cuenta como su vocal base (A, E, O); la Ñ se trata como letra distinta de la N; en el teclado en pantalla existe la tecla Ñ (entre N y O) | OK en `AhorcadoGUITest`: `pm16_teclaEnneEnPantallaEsDistintaDeLaN` (solo la tecla Ñ en pantalla). Las teclas con tilde del teclado físico NO se automatizan (dependen de la distribución del teclado/teclas muertas del sistema): no se verifican en esta entrega (limitación L-1) | APROBADA (parcial) · tildes físicas: no verificado (L-1) |
| PM-17 | R5 Teclado físico vs teclado en pantalla | Interfaz | Jugar una partida mezclando teclado físico y ratón; probar con foco en distintos controles | Ambos métodos producen el mismo resultado; con el foco en un botón o en el combo, las letras siguen jugando y no cambian el combo (ver H-3); con Ctrl/Alt pulsado no se interpretan como letras | `pm17a_letrasFisicasFuncionan` OK; `pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad` FALLÓ antes de la corrección (con el foco en el combo, la letra `M` jugó pero ADEMÁS cambió el selector a MEDIO; ver H-6) y APRUEBA tras corregir `VentanaPrincipal` (sección 5.2) | APROBADA (auto) · H-6 corregido |
| PM-18 | R5 Accesibilidad: orden de tabulación | Interfaz | Pulsar Tab repetidamente desde la ventana | Orden: Dificultad → Nueva partida → Cómo jugar → Categoría → Letra → Pista escrita → teclas A…Z (con Ñ); se saltan los controles deshabilitados; Shift+Tab recorre al revés | OK en `AhorcadoGUITest`: `pm18a_tabulacionHaciaAdelanteEsLogica`, `pm18b_shiftTabVaAtrasYSeSaltanLosDeshabilitados`, `pm18c_espacioActivaUnaLetraEnfocada`, `pm18d_espacioActivaUnaPistaEnfocada`, `pm18e_enterActivaUnaLetraEnfocada`, `pm18f_enterActivaUnaPistaEnfocada` | APROBADA (auto) |
| PM-19 | R5 Accesibilidad: mnemónicos y atajos | Interfaz | Probar Alt+J, Alt+A, Alt+D, Alt+N, Alt+M, Alt+C / Alt+L / Alt+E, Ctrl+N, Ctrl+Q, F1 (en macOS el mnemónico puede requerir la tecla Option) | Cada combinación activa su control; los mnemónicos se ven subrayados | OK en `AhorcadoGUITest`: `pm19_mnemonicosConfigurados` (solo comprueba que los mnemónicos están definidos; Ctrl+N, Ctrl+Q y F1 los cubren `pm14b`, `pm23b` y `pm24b`; pulsar Alt+letra depende del sistema operativo (limitación L-2)) | APROBADA (parcial) · Alt+letra: no verificado (L-2) |
| PM-20 | R5 Accesibilidad: nombres y descripciones accesibles | Interfaz | Activar VoiceOver (macOS) o inspeccionar con un lector; recorrer los controles | Cada control anuncia su nombre (p. ej. "Dificultad", "Pista: Categoría", "Pistas restantes", "Mensaje del juego") y su descripción | OK en `AhorcadoGUITest`: `pm20_controlesTienenNombreYDescripcionAccesibles` (solo comprueba que hay nombre accesible; lo que anuncia VoiceOver no se verifica en esta entrega (limitación L-3)) | APROBADA (parcial) · VoiceOver: no verificado (L-3) |
| PM-21 | R6 Diálogo de fin: "Jugar de nuevo" | Interfaz | Terminar una partida y pulsar "Jugar de nuevo" (o Enter, botón por defecto) | El diálogo muestra ganó/perdió, la palabra original con tildes, errores y categoría; se cierra y comienza una partida nueva con estado limpio | OK en `AhorcadoGUITest`: `pm21_jugarDeNuevoCierraElDialogoYReiniciaLaPartida` (+ contenido del diálogo en `pm02`, `pm03`, `pm03b`; Enter como botón por defecto no se probó) | APROBADA (auto) |
| PM-22 | R6 Diálogo de fin: "Salir" | Interfaz | Terminar una partida y pulsar "Salir" | La aplicación se cierra | OK en `AhorcadoGUITest`: `pm22_salirDelDialogoDeFinCierraLaAplicacion` (se pulsa «Salir» del diálogo de fin y se comprueba que se solicita `System.exit(0)`; la salida se intercepta para no cerrar la prueba) | APROBADA (auto) |
| PM-23 | R6 Menú Juego > Salir / Ctrl+Q | Interfaz | Elegir Juego > Salir y, en otra ejecución, Ctrl+Q | La aplicación se cierra | OK en `AhorcadoGUITest`: `pm23a_menuJuegoSalirCierraLaAplicacion` y `pm23b_ctrlQCierraLaAplicacion` (se solicita `System.exit(0)` por el menú y por Ctrl+Q; salida interceptada) | APROBADA (auto) |
| PM-24 | R6 Cómo jugar | Interfaz | Pulsar el botón "Cómo jugar", Ayuda > Cómo jugar y F1 | Se muestra la ventana con las reglas (7 errores y su orden, teclados, pistas, dificultades, atajos) por las 3 vías | OK en `AhorcadoGUITest`: `pm24_botonComoJugarMuestraLaAyuda` (botón) y `pm24b_menuAyudaComoJugarYF1MuestranLaAyuda` (menú Ayuda > Cómo jugar y F1) | APROBADA (auto) |
| PM-25 | R7 Ventana con tamaño mínimo | Interfaz | Reducir la ventana arrastrando el borde; luego maximizarla | No se puede reducir por debajo de 780 x 600 px; tamaño inicial 900 x 680; el contenido no se corta y se reajusta al maximizar | OK en `AhorcadoGUITest`: `pm25a_tamanoInicialYMinimo` (900 x 680 inicial; al arrastrar el borde con Robot a 500 x 400 la ventana quedó en 780 x 600), `pm25b_redimensionarNoSolapaNiDesbordaElTeclado` (780x600, 900x680, 1000x650, 1100x800, 1400x900) | APROBADA (auto) |
| PM-26 | R7 Diseño y dibujo con Graphics2D | Interfaz | Observar la ventana y los 7 estados del dibujo | Colores, fuentes y espaciado consistentes; el ahorcado se dibuja con líneas suavizadas (no son imágenes) | Inspección visual de las capturas `documentacion/capturas/01` a `07` y del video: colores, fuentes y espaciado consistentes; el muñeco se ve con trazo suavizado. En el código, `PanelAhorcado` activa `KEY_ANTIALIASING` y `KEY_STROKE_CONTROL` (líneas 83 y 84) y dibuja con `Graphics2D`, sin imágenes | APROBADA (inspección visual) |

### 3.3 Pruebas de interfaz automatizadas (AssertJ Swing)

Suite OPCIONAL: `codigo_fuente/pruebas_gui/AhorcadoGUITest.java` (JUnit 4.13.2 + AssertJ Swing 3.17.1; ver `codigo_fuente/pruebas_gui/LEEME.md`). El juego sigue siendo solo JDK. Cada prueba construye `BancoPalabras`, `ControladorJuego` y `VentanaPrincipal` en el EDT y arranca con una palabra conocida mediante `iniciarPartidaConPalabra(...)` (sin azar). Los componentes se localizan por su nombre (`setName`) y todas las lecturas se hacen en el EDT con `GuiActionRunner`.

| Caso de interfaz | Método de prueba (`AhorcadoGUITest`) | Resultado (ejecución real, sección 5.2) | Estado |
|---|---|---|---|
| PM-01 | `pm01_pantallaInicial` | OK | APROBADA (parcial) |
| PM-02 | `pm02_victoriaPorClicsEnLetras` | OK | APROBADA |
| PM-03 | `pm03_derrotaEnElSeptimoErrorYOrdenDePartes`, `pm03b_derrotaMuestraLaPalabraRealConTildes` | OK, OK | APROBADA (parcial) |
| PM-04 | `pm04a_letrasCorrectasAparecenEnSuPosicion`, `pm04b_letraIncorrectaAumentaLosErrores` | OK, OK | APROBADA |
| PM-05 | `pm05a_letraRepetidaPorClicNoPenaliza`, `pm05b_letraRepetidaPorTeclaFisicaNoPenaliza` | OK, OK | APROBADA |
| PM-06 | `pm06_teclasFisicasQueNoSonLetrasSeIgnoran` | OK | APROBADA |
| PM-07 | `pm07a_...`, `pm07b_...` (tras perder / tras ganar) | OK, OK | APROBADA |
| PM-08 | `pm08_pistaDeCategoriaMuestraTextoYDeshabilitaElBoton` | OK | APROBADA |
| PM-09 | `pm09_pistaDeLetraRevelaUnaLetraSinSumarError` | OK | APROBADA |
| PM-10 | `pm10_pistaEscritaMuestraElTextoDeLaPalabra` | OK | APROBADA |
| PM-11 | `pm11_ningunaPistaSePuedeReutilizar` | OK | APROBADA |
| PM-12 | `pm12_pistaDeLetraQueCompletaLaPalabraGana` | OK | APROBADA |
| PM-13 | `pm13_tresPistasUsadasQuedanListadasYDeshabilitadas` | OK | APROBADA |
| PM-14 | `pm14_nuevaPartidaReiniciaTodoYCargaOtraPalabra`, `pm14b_menuNuevaPartidaYCtrlNReiniciaLaPartida` | OK, OK | APROBADA |
| PM-15 | `pm15a_selectorDeDificultadTieneLasCuatroOpciones`, `pm15b_cadaDificultadSorteaPalabrasDeSuLongitud` | OK, OK | APROBADA |
| PM-16 | `pm16_teclaEnneEnPantallaEsDistintaDeLaN` | OK | APROBADA (parcial) |
| PM-17 | `pm17a_letrasFisicasFuncionan` / `pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad` | OK / FALLÓ antes del arreglo, OK después (H-6) | APROBADA |
| PM-18 | `pm18a_...`, `pm18b_...`, `pm18c_...`, `pm18d_...`, `pm18e_...`, `pm18f_...` (Tab, Shift+Tab, Espacio y Enter) | OK x6 | APROBADA |
| PM-19 | `pm19_mnemonicosConfigurados` | OK | APROBADA (parcial) |
| PM-20 | `pm20_controlesTienenNombreYDescripcionAccesibles` | OK | APROBADA (parcial) |
| PM-21 | `pm21_jugarDeNuevoCierraElDialogoYReiniciaLaPartida` | OK | APROBADA |
| PM-22 | `pm22_salirDelDialogoDeFinCierraLaAplicacion` (salida interceptada) | OK | APROBADA |
| PM-23 | `pm23a_menuJuegoSalirCierraLaAplicacion`, `pm23b_ctrlQCierraLaAplicacion` (salida interceptada) | OK, OK | APROBADA |
| PM-24 | `pm24_botonComoJugarMuestraLaAyuda`, `pm24b_menuAyudaComoJugarYF1MuestranLaAyuda` | OK, OK | APROBADA |
| PM-25 | `pm25a_tamanoInicialYMinimo`, `pm25b_redimensionarNoSolapaNiDesbordaElTeclado` | OK, OK | APROBADA |
| PM-26 | (diseño/estética: no se compara por código) | Inspección visual de las capturas | APROBADA (inspección visual) |

Qué NO se automatizó y por qué: las teclas con tilde del teclado físico (PM-16) dependen de la distribución del teclado y de las teclas muertas del sistema, por lo que no son reproducibles con `Robot`; el trazo visual del dibujo y la estética (PM-01, PM-03, PM-26) no se comparan por código; `Salir`/Ctrl+Q (PM-22, PM-23) se automatizan interceptando `System.exit` con un `SecurityManager` instalado solo dentro de esas pruebas; Alt+letra y VoiceOver (PM-19, PM-20) dependen del sistema operativo. `APROBADA (parcial)` significa que lo automatizable pasó y el resto queda como limitación documentada en la sección 7 (L-1 a L-3).

---

## 4. Matriz de trazabilidad

| Requisito del enunciado | Casos automatizados | Casos de interfaz |
|---|---|---|
| R1 Banco de 200 palabras (8 x 25), mayúsculas, sin espacios, sin duplicados, dificultad y selector | PA-13, PA-14, PA-15, PA-16, PA-17, PA-18, PA-20 | PM-15 |
| R2 Máximo 7 errores, orden de partes, horca visible, victoria/derrota | PA-01, PA-02, PA-11, PA-23 | PM-01, PM-02, PM-03, PM-12, PM-26 |
| R3 Pantalla: palabra con guiones, correctas, incorrectas, intentos, dibujo | PA-24 | PM-01, PM-04 |
| R4 Tres pistas, una vez cada una, contador y botón deshabilitado | PA-07, PA-08, PA-09, PA-10, PA-11, PA-19 | PM-08, PM-09, PM-10, PM-11, PM-12, PM-13 |
| R5 Entrada: repetidas, no letras, tras el fin, teclado en pantalla y físico, accesibilidad | PA-03, PA-04, PA-05, PA-06, PA-21, PA-22, PA-24, PA-25 | PM-05, PM-06, PM-07, PM-16, PM-17, PM-18, PM-19, PM-20 |
| R6 Diálogo de fin, Nueva partida, Salir, Cómo jugar, reinicio | PA-12 | PM-14, PM-21, PM-22, PM-23, PM-24 |
| R7 Diseño claro, Graphics2D, tamaño mínimo | PA-23 | PM-25, PM-26 |
| D1 Decisión de diseño: tildes y Ñ | PA-18, PA-21, PA-22 | PM-16 |
| D2 Decisión de diseño: dificultad por longitud (3–5 / 6–8 / ≥ 9) | PA-13, PA-20 | PM-15 |

---

## 5. Resultados de la ejecución automática

### 5.1 Pruebas del modelo/controlador (JDK solo)

Ejecución realizada el 2026-11-16 en macOS con JDK 17, compilando a una carpeta temporal:

```bash
javac --release 17 -encoding UTF-8 -d /tmp/ahorcado/out_plan $(fd -e java . src)   # sin errores
java -cp /tmp/ahorcado/out_plan util.PruebasAutomaticas                             # código de salida 0
java -cp /tmp/ahorcado/out_plan Main --verificar                                    # código de salida 0
```

Resumen de `util.PruebasAutomaticas` (las 76 comprobaciones imprimieron `OK`; ninguna `FALLO`):

```
Pruebas ejecutadas: 76 | Fallos: 0
RESULTADO: TODAS LAS PRUEBAS OK
```

Salida de `Main --verificar`:

```
Total de palabras: 200
Categorías: 8
  Animales: 25
  Países: 25
  Deportes: 25
  Objetos: 25
  Comidas: 25
  Profesiones: 25
  Frutas y verduras: 25
  Tecnología: 25
Por dificultad (según la longitud de la palabra):
  FÁCIL (3 a 5 letras): 56
  MEDIO (6 a 8 letras): 73
  DIFÍCIL (9 o más letras): 71
Resultado: CORRECTO (200 palabras, 8 categorías x 25)
```

> El autor debe volver a ejecutar estos comandos en su máquina antes de la entrega y pegar su propia salida si difiere.

Tras añadir los nombres (`setName`) a los componentes para las pruebas de interfaz se recompiló y se volvió a ejecutar `util.PruebasAutomaticas` (2026-11-16): `Pruebas ejecutadas: 76 | Fallos: 0`, `RESULTADO: TODAS LAS PRUEBAS OK`.

### 5.2 Pruebas de interfaz con AssertJ Swing (opcionales)

Ejecución real el 2026-11-16 en macOS (Darwin 25.6, JDK 17.0.21, pantalla 1512 x 982, Look and Feel Nimbus), desde un proceso lanzado por línea de comandos; el `java.awt.Robot` pudo mover el ratón y teclear (no hubo bloqueo de permisos). Se ejecutó la suite completa **tres veces** (una primera con problemas propios de las pruebas ya corregidos, y dos finales idénticas):

```bash
cd codigo_fuente
pruebas_gui/descargar_dependencias.sh     # 6 JAR verificados con su SHA-1 de Maven Central
pruebas_gui/ejecutar_pruebas_gui.sh       # código de salida 1 (hay 1 FALLA real, ver H-6)
```

Resultado de las dos ejecuciones finales (iguales):

```
Pruebas de interfaz ejecutadas: 35 | Fallos: 1 | Omitidas: 0 | Tiempo: 75 s
RESULTADO: HAY FALLOS
```

34 pruebas `OK` y 1 `FALLA`: `pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad` (el selector pasó de ALEATORIA a MEDIO al teclear `M`). Todas las demás (`pm01` … `pm25b`) imprimieron `OK`.

Notas de la ejecución: (1) en la primera pasada completa, 7 pruebas fallaron por defectos de las propias pruebas, no del juego (`FrameFixture.show()` hace `pack()` y cambia el tamaño real de la ventana; `pressAndReleaseKeys` del fixture mueve el foco; la ventana tardaba en quedar activa), y se corrigieron antes de registrar los resultados de arriba; (2) al arrastrar el borde con Robot a 500 x 400, la ventana quedó en 780 x 600 (tamaño mínimo respetado); (3) las comprobaciones de distribución (sin solapes ni desbordes del teclado) pasaron en los 5 tamaños probados.

**Ejecuciones tras corregir H-6** (2026-11-16, misma máquina; se añadió `comboDificultad.setKeySelectionManager((tecla, modelo) -> -1)` en `VentanaPrincipal`):

- `pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad` ejecutada sola: `ejecutadas=1 fallos=0`.
- `util.PruebasAutomaticas` (modelo/controlador) tras el cambio: `Pruebas ejecutadas: 76 | Fallos: 0`.
- Suite de interfaz completa tras el cambio (una ejecución): `Pruebas de interfaz ejecutadas: 35 | Fallos: 1 | Omitidas: 0 | Tiempo: 101 s`. La prueba `pm17b` quedó `OK`. El único fallo fue **`pm07b_trasGanarTodoQuedaDeshabilitadoYLasTeclasSeIgnoran`**: no encontró el diálogo de fin en 10 s, aunque el estado de la ventana mostraba la última letra jugada. Ejecutada sola dos veces, `pm07b` dio `fallos=0` en ambas, y también había dado `OK` en las dos ejecuciones completas anteriores. Se trata como **prueba intermitente** (ver H-9), no como un defecto confirmado del juego.

**Ejecución completa de la suite con los casos de menú, atajos y salida** (2026-11-16, misma máquina y JDK 17, con `-Djava.security.manager=allow`): `Pruebas de interfaz ejecutadas: 40 | Fallos: 0 | Omitidas: 0 | Tiempo: 90 s`, `RESULTADO: TODAS LAS PRUEBAS DE INTERFAZ OK`. Los 5 métodos nuevos dieron `OK`: `pm14b_menuNuevaPartidaYCtrlNReiniciaLaPartida`, `pm22_salirDelDialogoDeFinCierraLaAplicacion`, `pm23a_menuJuegoSalirCierraLaAplicacion`, `pm23b_ctrlQCierraLaAplicacion` y `pm24b_menuAyudaComoJugarYF1MuestranLaAyuda`. `pm07b` (H-9) pasó en esta ejecución. En las pruebas de «Salir», Swing imprime en la consola el seguimiento de la excepción privada con la que se interceptó `System.exit`; es esperado.

---

## 6. Resumen numérico

| Concepto | Cantidad |
|---|---|
| Casos automatizados (modelo/controlador) | 25 |
| Casos automatizados APROBADOS (ejecución real, sección 5.1) | **25 de 25** |
| Comprobaciones individuales de `PruebasAutomaticas` con `OK` | 76 de 76 (0 fallos) |
| Casos de interfaz (PM) | 26 |
| PM cubiertos por la suite OPCIONAL de interfaz (40 pruebas AssertJ Swing; última ejecución completa: 40 `OK`, 0 fallos; la anterior tuvo 1 intermitente, `pm07b`, ver H-9) | 25 de 26 |
| PM `APROBADA (auto)` (cobertura completa por la suite de interfaz) | 20 (PM-02, 04, 05, 06, 07, 08, 09, 10, 11, 12, 13, 14, 15, 17, 18, 21, 22, 23, 24, 25) |
| PM `APROBADA (parcial)` y `APROBADA (auto + inspección visual)` (lo automatizable pasó; el resto es una limitación de la sección 7) | 5 (PM-01, 03, 16, 19, 20) |
| PM `FALLA` | 0 (H-6 corregido; PM-17 aprobada) |
| PM con inspección visual (sin prueba automática) | 1 (PM-26, `APROBADA (inspección visual)`) |
| Total de casos | 51 |

Las partes que no se verificaron dentro de los casos `parcial` (tildes del teclado físico, Alt+letra, VoiceOver) quedan como limitaciones L-1 a L-3 en la sección 7.

---

## 7. Hallazgos

Resultado de ejecutar las pruebas y revisar el código fuente. Ninguno es un defecto que haga fallar la especificación; son observaciones y mejoras.

| ID | Tipo | Descripción | Recomendación |
|---|---|---|---|
| H-1 | Cobertura | La suite automática solo comprueba "no repetir hasta agotar" con ALEATORIA (200 sorteos). Para cada nivel (FÁCIL/MEDIO/DIFÍCIL) se verificó aparte, con un script temporal fuera del proyecto, que sortear tantas veces como palabras candidatas da palabras distintas (56/56, 73/73, 71/71, todas OK). | Añadir esa comprobación a `PruebasAutomaticas` si se quiere dejar constancia en el repositorio. |
| H-2 | Comportamiento | En el diálogo de fin, la tecla Esc lo cierra sin iniciar otra partida (código: `registerKeyboardAction` con `dispose()`). La partida terminada queda con el teclado deshabilitado y se continúa con "Nueva partida" / Ctrl+N. No contradice el enunciado. | Documentarlo (ya cubierto en PM-07) o, si se prefiere, hacer que Esc equivalga a "Salir"/"Jugar de nuevo". |
| H-3 | Usabilidad | El teclado físico consume todas las letras en cualquier control (la intención era que no cambien el selector de dificultad), de modo que no se puede elegir una dificultad tecleando su inicial; se usan las flechas del combo. **La prueba automática mostró que la intención no se cumple del todo: ver H-6.** | Anotar en el manual: elegir dificultad con flechas. |
| H-4 | Cobertura | `util.PruebasAutomaticas` (JDK solo) no prueba la capa de ventana. Esa capa se cubre ahora, de forma OPCIONAL, con la suite AssertJ Swing (sección 3.3); PM-26 se revisa en las capturas y las partes `parcial` figuran como limitaciones L-1 a L-3. | Ejecutar la suite de interfaz (toma el ratón y el teclado unos 2 minutos). |
| H-5 | Plataforma | En macOS los mnemónicos `Alt+letra` pueden requerir la tecla Option y depender del Look and Feel (se usa Nimbus si existe). No verificado: queda en PM-19. | Registrar el resultado real de PM-19 en macOS. |
| H-6 | **Defecto (menor, usabilidad)** | Con el foco en el selector de dificultad, teclear una letra (p. ej. `M`) la juega **y además** cambia el selector (ALEATORIA pasó a MEDIO). `VentanaPrincipal.instalarTecladoFisico` consume el `KEY_TYPED`, pero el combo sigue seleccionando por inicial (causa probable, no investigada a fondo: la selección por inicial se dispara con el `KEY_PRESSED`). Evidencia: `pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad` y una reproducción con `java.awt.Robot` sin AssertJ (`sel=MEDIO errores=1`). **Corregido** el 2026-11-16 con `comboDificultad.setKeySelectionManager((tecla, modelo) -> -1)` (deshabilita la selección por inicial del combo); `pm17b` pasó a `OK`. | Decisión del autor: consumir también el `KEY_PRESSED` de letras, o quitar el foco inicial del combo, o aceptar el comportamiento y documentarlo. Corregido; `pm17b` aprueba. |
| H-7 | Observación | En Nimbus, Enter activa un botón con el foco (no solo Espacio): `pm18e`/`pm18f` pasan. El orden de tabulación coincide con el documentado y salta los controles deshabilitados. | Ninguna. |
| H-8 | Nota técnica | AssertJ Swing: `FrameFixture.show()` hace `pack()` (cambia el tamaño real 900 x 680) y `pressAndReleaseKeys` del fixture mueve el foco; la suite usa `setVisible(true)` y `robot().pressAndReleaseKey(...)` para probar el comportamiento real. | Tenerlo en cuenta si se amplían las pruebas. |
| H-9 | Prueba intermitente | `pm07b_trasGanarTodoQuedaDeshabilitadoYLasTeclasSeIgnoran` falló una vez en una suite completa (no halló el diálogo de fin en 10 s) y pasó 2 de 2 sola y en las dos ejecuciones completas previas. Causa probable: una pulsación del `java.awt.Robot` se perdió porque la ventana cedió el foco un instante (depende del sistema operativo y de lo que haga el usuario durante la ejecución). No se reprodujo como defecto del juego. | No tocar el ratón ni el teclado durante la ejecución; si una prueba falla, repetirla sola antes de concluir que hay un defecto. |
| L-1 | Limitación | Las teclas con tilde del teclado físico (PM-16) dependen de la distribución del teclado y de las teclas muertas del sistema; no son reproducibles con `Robot`. | No verificadas en esta entrega. La normalización de tildes sí está cubierta por `Normalizador` en `PruebasAutomaticas`. |
| L-2 | Limitación | La activación con Alt+letra (PM-19) depende del sistema operativo y del Look and Feel (en macOS puede requerir Option, ver H-5). | No verificada. Los mnemónicos configurados sí se comprueban (`pm19`) y Ctrl+N, Ctrl+Q y F1 están cubiertos. |
| L-3 | Limitación | Lo que anuncia VoiceOver (PM-20) depende del lector de pantalla. | No verificado. Los nombres y descripciones accesibles de los controles sí se comprueban (`pm20`). |

Salvo H-6 (una molestia de uso ya corregida, no una violación del enunciado), no se encontró ningún caso en que el código contradiga el enunciado en lo que las pruebas pueden comprobar.

---

## 8. Cómo ejecutar las pruebas de interfaz

Orden sugerido para repetir las comprobaciones de interfaz con el juego abierto:

1. **Arranque** (PM-01, PM-25, PM-26): abrir el juego; mostrar horca, guiones, intentos 7; probar el tamaño mínimo.
2. **Victoria** (PM-02, PM-04): acertar la palabra con el teclado en pantalla; mostrar el diálogo "¡Ganaste!". Pulsar "Jugar de nuevo" (PM-21).
3. **Derrota** (PM-03): fallar 7 letras viendo el dibujo en orden (cabeza, torso, brazo derecho, brazo izquierdo, pierna derecha, pierna izquierda, cuerda); mostrar "¡Perdiste!" y la palabra. Pulsar "Salir" en otra pasada (PM-22).
4. **Letras repetidas y no válidas** (PM-05, PM-06): repetir una letra; teclear `7`, `-`, espacio.
5. **Teclas tras el fin** (PM-07): intentar jugar tras terminar.
6. **Pista de categoría** (PM-08), **pista de letra** (PM-09), **pista escrita** (PM-10): usar cada una y mostrar el contador y el área de pistas (PM-13).
7. **Reutilizar pista** (PM-11) y **pista de letra que gana** (PM-12).
8. **Nueva partida** (PM-14): con errores y pistas usadas, reiniciar y mostrar el estado limpio; probar botón, menú y Ctrl+N.
9. **Selector de dificultad** (PM-15): probar las 4 opciones y comparar la longitud.
10. **Tildes y Ñ; teclado físico vs pantalla** (PM-16, PM-17).
11. **Accesibilidad** (PM-18, PM-19, PM-20): Tab, mnemónicos y atajos, VoiceOver.
12. **Menú y ayuda** (PM-23, PM-24): Juego > Salir / Ctrl+Q; Cómo jugar (botón, menú, F1).

Los pasos cubiertos por la suite automática de interfaz (sección 3.3) se pueden usar solo como comprobación visual.


---

## 9. Nota opcional: adaptación a JUnit 5

> **Opcional. No forma parte del proyecto entregado** (el proyecto usa solo el JDK, sin JUnit). Solo se incluye por si el curso exigiera JUnit 5.

Cada llamada `comprobar("nombre", condicion)` de `util.PruebasAutomaticas` pasa a ser un método `@Test` con `assertTrue`/`assertEquals`. Ejemplo equivalente a "Partida: letra repetida fallada no suma error":

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import modelo.Palabra;
import modelo.Partida;
import modelo.ResultadoLetra;
import org.junit.jupiter.api.Test;

class PartidaTest {

    @Test
    void letraRepetidaFalladaNoSumaError() {
        Partida partida = new Partida(new Palabra("GATO", "pista de prueba", "Pruebas"));
        partida.adivinar('X');

        assertEquals(ResultadoLetra.REPETIDA, partida.adivinar('x'));
        assertEquals(1, partida.getErrores());
    }
}
```

Requeriría añadir la dependencia `org.junit.jupiter:junit-jupiter` (por ejemplo con Maven o Gradle) y ejecutar con `mvn test` / `gradle test`.
