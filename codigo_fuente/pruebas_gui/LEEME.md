# Pruebas de interfaz OPCIONALES (JUnit 4 + AssertJ Swing)

El juego **no** necesita nada de esto: sigue compilando y ejecutándose solo con el JDK. Esta carpeta es un extra que automatiza la mayoría de los casos de interfaz `PM-xx` del `documentacion/Plan_de_Pruebas.md`.

> **ADVERTENCIA: estas pruebas toman el control de tu ratón y de tu teclado reales** (`java.awt.Robot`) durante **1 a 3 minutos** (la suite completa tarda unos 75 segundos). Avisa a tu equipo, no uses el computador mientras corren y cierra las ventanas importantes: si otra ventana recibe el foco, podría recibir pulsaciones. Para detenerlas, cierra la terminal o pulsa `Ctrl+C`.

## Qué contiene

| Archivo | Para qué sirve |
|---|---|
| `AhorcadoGUITest.java` | Las pruebas (40 métodos `pmNN_...`; el número es el caso manual que reemplaza). Está en el **paquete por defecto**, igual que `Main.java`, para compilarlo con un solo comando |
| `descargar_dependencias.sh` | Descarga los JAR a `../lib_test/` (solo `curl` y `shasum`) y verifica cada uno con el `.sha1` de Maven Central. Es idempotente |
| `ejecutar_pruebas_gui.sh` | Compila el juego y las pruebas en una carpeta temporal, las ejecuta y muestra `OK`/`FALLA` por prueba |

Dependencias (cierre transitivo leído de los POM en Maven Central): `assertj-swing-junit 3.17.1`, `assertj-swing 3.17.1`, `assertj-core 3.17.2`, `fest-util 1.2.5`, `junit 4.13.2` y `hamcrest-core 1.3`. Los `*.jar` están en `.gitignore`: **no se suben al repositorio**.

## Cómo ejecutarlas

Requisitos: JDK 17, entorno gráfico (no sirve `-Djava.awt.headless=true`) y conexión a internet la primera vez.

```bash
cd codigo_fuente
pruebas_gui/descargar_dependencias.sh   # una sola vez
pruebas_gui/ejecutar_pruebas_gui.sh     # toma el ratón y el teclado
```

El segundo script termina con código 0 si todas pasan y distinto de 0 si alguna falla, si faltan los JAR o si se agota el tiempo (`LIMITE_SEGUNDOS`, 900 por defecto). Cada prueba tiene además un plazo de 90 s (`@Test(timeout = ...)`), de modo que un problema de permisos no deja la suite colgada para siempre.

Para ejecutar solo algunas pruebas (por prefijo del nombre), compila con el script una vez y luego, con las clases en una carpeta `out_gui`:

```bash
javac --release 17 -encoding UTF-8 -d out_gui $(find src -name "*.java")
javac --release 17 -encoding UTF-8 -d out_gui -cp "out_gui:lib_test/*" pruebas_gui/AhorcadoGUITest.java
java -Djava.security.manager=allow -cp "out_gui:lib_test/*" AhorcadoGUITest pm02 pm18   # solo pm02* y pm18*
```

(También funciona `java -Djava.security.manager=allow -cp "out_gui:lib_test/*" org.junit.runner.JUnitCore AhorcadoGUITest`, pero sin el resumen por prueba.)

## macOS: permisos

Si el Robot no puede mover el ratón o teclear, las pruebas fallan por tiempo o por no ver cambios. Concede permiso a la **terminal o al IDE** desde el que ejecutas el script:

- Ajustes del Sistema > Privacidad y seguridad > **Accesibilidad** (imprescindible).
- A veces también **Grabación de pantalla** (si se piden capturas).

Cierra y vuelve a abrir la terminal después de conceder el permiso. En Linux hace falta un servidor gráfico (X11); en Windows funciona sin pasos extra.

## Qué cubre y qué no

Cubre (con una palabra conocida, sin azar): victoria, derrota exactamente al 7.º error y orden de las partes del dibujo, letras en su posición, letras repetidas (clic y teclado), teclas que no son letras, teclado físico, tres pistas (incluida la que completa la palabra), "Nueva partida", "Jugar de nuevo", selector de dificultad, Tab/Shift+Tab/Espacio/Enter, mnemónicos y nombres accesibles definidos, ayuda y redimensionado (sin solapes ni desbordes del teclado, tamaño mínimo).

También cubre **Salir** (botón del diálogo de fin, menú Juego > Salir y Ctrl+Q), Juego > Nueva partida, Ctrl+N, Ayuda > Cómo jugar y F1. Como `Salir` llama a `System.exit`, esas tres pruebas (`pm22`, `pm23a`, `pm23b`) instalan un `SecurityManager` solo mientras dura la acción: anota el estado pedido y lanza una excepción propia para que la JVM no termine. Por eso la JVM se lanza con `-Djava.security.manager=allow` (Java 17 lo exige; el script ya lo incluye). Swing imprime esa excepción en la consola durante esas pruebas: es esperado.

**No** automatiza: teclas con tilde del teclado físico (dependen de la distribución del teclado), pulsar Alt+letra, VoiceOver, y el aspecto visual (colores, trazo del dibujo).

## Nombres de componentes (`setName`)

Para poder localizarlos, el juego les pone nombres estables (cambio solo aditivo, sin alterar el comportamiento): `btn_A`…`btn_Z` y `btn_Ñ`; `btnPistaCategoria`, `btnPistaLetra`, `btnPistaTexto`; `btnNuevaPartida`, `btnComoJugar`; `cmbDificultad`; `lblPalabraOculta`, `lblLetrasCorrectas`, `lblLetrasIncorrectas`, `lblIntentos`, `lblPistasRestantes`, `lblMensaje`; `txtPistas`; `panelAhorcado`, `panelTeclado`, `panelPistas`, `panelEstado`; y en el diálogo de fin `dlgFinPartida`, `lblMensajeFin`, `lblPalabraFin`, `btnJugarDeNuevo`, `btnSalir`.

## Resultado conocido

La última ejecución registrada está en `documentacion/Plan_de_Pruebas.md` (sección 5.2): 40 pruebas, 40 `OK`, 0 fallos (el hallazgo H-6, que hacía fallar `pm17b`, ya está corregido).
