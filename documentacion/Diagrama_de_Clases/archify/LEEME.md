# Diagramas interactivos (Archify)

Cuatro diagramas HTML autocontenidos que complementan el diagrama de clases UML.

| Archivo | Qué muestra |
|---|---|
| `01-estructura-clases.html` | Estructura MVC: paquetes `modelo`, `vista`, `controlador` y `util`, las clases clave y sus relaciones (composición, implementación, uso). Los pilares aparecen como etiquetas: Herencia, Polimorfismo, Encapsulamiento, ArrayList. |
| `02-secuencia-letra.html` | Jugada de una letra: `procesarLetra`, `adivinar`, `normalizarLetra`, `ResultadoLetra`, `actualizar`, `setErrores` y `mostrarFin` cuando la partida termina. |
| `03-secuencia-pista.html` | Uso de una pista y polimorfismo: `Partida.usarPista`, `Pista.usar`, `obtener(partida)` en `PistaCategoria`, `PistaLetra` (con `revelarLetra`, sin error) y `PistaTexto`. |
| `04-ciclo-partida.html` | Ciclo de vida de `Partida` con `EstadoJuego`: EN_CURSO, GANADA, PERDIDA y la guardia: tras terminar, `adivinar` devuelve `PARTIDA_TERMINADA`. |

Cada diagrama tiene su fuente `.json` junto al `.html`.

## Cómo abrirlos

Haz doble clic en el archivo `.html`. Se abre en el navegador, sin instalar nada ni conexión a internet. Se pueden mover, acercar, buscar y cambiar entre tema claro y oscuro. La interfaz fija del visor (botones y menús) está en inglés; el contenido de los diagramas está en español.

## Limitación importante

Archify **no** admite diagramas de clases UML con atributos, métodos y multiplicidades. Por eso el diagrama de clases UML completo sigue en los archivos PlantUML de esta carpeta. El diagrama `01` es un mapa de estructura (clases y relaciones), no un UML formal.

Detalles del `01`: por el límite de 12 nodos, `Categoria` se muestra dentro del nodo `BancoPalabras` (en su subtítulo) y `PanelTeclado`, `PanelPistas` y `PanelEstado` comparten un nodo. Las subclases de `Pista` y `ParteCuerpo` se agrupan en su clase abstracta.

## Capturas PNG

La carpeta `capturas/` contiene una imagen de cada diagrama (1600 × 1000 px), útil para pegarlas en el informe o en la presentación:

| Archivo | Diagrama |
| --- | --- |
| `capturas/01-estructura-clases.png` | Estructura de clases del Ahorcado (MVC) |
| `capturas/02-secuencia-letra.png` | Jugada de una letra (secuencia) |
| `capturas/03-secuencia-pista.png` | Uso de una pista, con polimorfismo (secuencia) |
| `capturas/04-ciclo-partida.png` | Ciclo de vida de una `Partida` (estados) |

Se tomaron con Playwright abriendo los `.html` en un navegador a 1600 × 1000 px (Chrome DevTools no pudo conectarse en esta máquina). Para regenerarlas, abre cada `.html` y usa el botón *Export* del visor.
