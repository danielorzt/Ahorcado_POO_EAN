# Cómo exportar el diagrama de clases a PNG / PDF

> **Aviso honesto:** los diagramas `.puml` y `.mmd` fueron escritos a partir del código real, pero **NO fueron renderizados** por quien los generó (no había PlantUML instalado y no se descargó nada). Lo que sí se hizo fue una verificación estática con un script (`verificar_uml.py`: tipos, `extends`/`implements`, métodos, atributos, visibilidad y sintaxis básica). **Los archivos PNG/PDF aún no existen: debes exportarlos tú**, revisar que se vean bien y guardarlos en esta misma carpeta (`documentacion/Diagrama_de_Clases/`).

Archivos fuente de esta carpeta:

| Archivo | Contenido |
|---|---|
| `Diagrama_de_Clases.puml` | Diagrama COMPLETO (PlantUML). **Este es el que se entrega.** |
| `Diagrama_modelo.puml` | Solo paquete `modelo`. |
| `Diagrama_vista_controlador.puml` | Paquetes `controlador` y `vista`. |
| `Diagrama_util.puml` | Paquete `util` y `Main`. |
| `Diagrama_de_Clases.mmd` | Respaldo en Mermaid del diagrama completo. |
| `Descripcion_de_Clases.md` | Tabla de clases, multiplicidades y pilares de POO. |

Si el diagrama completo sale muy pequeño al imprimirlo, exporta también los tres por área (son legibles en una hoja cada uno).

## Opción A — VS Code + extensión PlantUML (recomendada)

1. Instala **Java** (JDK 17 ya lo tienes para el proyecto). Comprueba con `java -version`.
2. En VS Code instala la extensión **PlantUML** (autor: jebbs).
3. Para el dibujo de clases PlantUML necesita **Graphviz** (`brew install graphviz` en macOS). Si no quieres instalar Graphviz, añade esta línea bajo `@startuml` y usa el motor interno Smetana (en Java puro):
   ```
   !pragma layout smetana
   ```
   (Smetana puede acomodar las cajas distinto a Graphviz; si se ve apretado, usa Graphviz.)
4. Abre `Diagrama_de_Clases.puml` y pulsa `Alt+D` (vista previa).
5. Exporta: `Cmd/Ctrl+Shift+P` → **PlantUML: Export Current Diagram** → elige `png`, `svg` o `pdf`. Se crea una carpeta `out/` junto al archivo; copia ahí el resultado a esta carpeta.

## Opción B — Servidor público https://www.plantuml.com/plantuml

1. Abre la página, pega el contenido de `Diagrama_de_Clases.puml` y pulsa **Submit**.
2. Descarga **PNG** o **SVG** desde los enlaces bajo la imagen.

> **ADVERTENCIA:** esta opción **envía el texto del diagrama a un servidor de terceros**. Aquí es aceptable porque el proyecto no contiene información sensible (solo nombres de clases del juego). No uses este servidor con diagramas confidenciales.

## Opción C — PlantUML por línea de comandos (jar)

1. Descarga `plantuml.jar` desde https://plantuml.com/download (descárgalo tú, desde el sitio oficial).
2. Desde esta carpeta:
   ```bash
   java -jar plantuml.jar -tpng Diagrama_de_Clases.puml
   java -jar plantuml.jar -tpdf Diagrama_de_Clases.puml
   java -jar plantuml.jar -tsvg Diagrama_de_Clases.puml
   ```
   Para los tres por área: `java -jar plantuml.jar -tpng Diagrama_modelo.puml Diagrama_vista_controlador.puml Diagrama_util.puml`
3. Si el PNG sale recortado o borroso, sube la resolución con `-Sdpi=200`, o usa SVG/PDF (vectoriales, no pierden calidad al ampliar).
4. Si no tienes Graphviz, añade `-Playout=smetana` o la línea `!pragma layout smetana` al `.puml`.

## Opción D — Mermaid (respaldo)

- En línea: pega `Diagrama_de_Clases.mmd` en https://mermaid.live y descarga PNG/SVG. (También envía el texto a un tercero; mismo aviso de la opción B.)
- Local con Node.js:
  ```bash
  npx @mermaid-js/mermaid-cli -i Diagrama_de_Clases.mmd -o Diagrama_de_Clases.png
  ```
  (Para PDF cambia la extensión de salida a `.pdf`.)
- Limitaciones: Mermaid no muestra `{readOnly}`, los genéricos van con `~`, y los bloques `namespace` requieren Mermaid 10 o superior. Si el visor es antiguo, borra las líneas `namespace ... {` y sus `}`. Las clases anidadas (`OrdenTabulacion`, `VistaFalsa`) se dibujan como clases normales con relación de composición.

## Dónde guardar las imágenes

En esta misma carpeta, con estos nombres:

```
documentacion/Diagrama_de_Clases/
├── Diagrama_de_Clases.png      ← entregable principal
├── Diagrama_de_Clases.pdf      ← mismo diagrama en PDF
├── Diagrama_modelo.png         (opcional, por área)
├── Diagrama_vista_controlador.png
└── Diagrama_util.png
```

## Lista de verificación para la calificación (revisa sobre la imagen ya exportada)

- [ ] Aparecen **todas** las clases, interfaces y enums (35 tipos del proyecto + `JFrame`, `JPanel`, `FocusTraversalPolicy`).
- [ ] Cada clase muestra sus **atributos con tipo** (por ejemplo `-letrasCorrectas : ArrayList<Character>`).
- [ ] Cada clase muestra sus **métodos con parámetros y tipo de retorno**.
- [ ] Se ve la **visibilidad** (`+`, `-`, `#`, `~`) y `{static}` / `{abstract}` donde corresponde.
- [ ] **Herencia** visible: `Pista` ← 3 pistas; `ParteCuerpo` ← 7 partes; `JPanel` ← 4 paneles; `JFrame` ← `VentanaPrincipal`.
- [ ] **Interfaz** `VistaJuego` con su realización por `VentanaPrincipal` (flecha punteada con triángulo hueco).
- [ ] **Asociaciones, composiciones y agregaciones** con **multiplicidades** (1, 3, 7, 8, 25, 0..1).
- [ ] La nota que explica que 8 y 25 vienen de los datos (200 = 8 × 25) es legible.
- [ ] Los estereotipos `<<abstract>>`, `<<interface>>`, `<<enumeration>>` se leen.
- [ ] El texto es legible al tamaño de impresión (si no, usa PDF/SVG o los tres diagramas por área).
- [ ] La imagen está guardada en `documentacion/Diagrama_de_Clases/` y se menciona en el manual.

## Verificación estática (opcional)

El script `verificar_uml.py` (en `/tmp/ahorcado/` del equipo que generó los archivos) compara el `.puml` con el código Java. Si cambias el código, actualiza el diagrama y vuelve a ejecutarlo con `python3 verificar_uml.py`. Recuerda: **solo revisa el texto, no el dibujo**.
