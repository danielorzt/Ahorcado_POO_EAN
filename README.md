# Juego del Ahorcado Interactivo con Java Swing

Proyecto final de **Programación Orientada a Objetos** · Universidad EAN · noviembre de 2026.

Integrante: Daniel Esteban Ortiz Pinzón

Video del proyecto (YouTube): https://youtu.be/R6azp1shkIs

Juego del ahorcado en Java 17 con interfaz Swing, sin librerías externas. Tiene un banco de **200 palabras** en **8 categorías** (25 por categoría), tres niveles de dificultad, tres pistas por partida y un ahorcado dibujado con `Graphics2D`.

## Contenido del repositorio

| Carpeta | Qué contiene |
| --- | --- |
| [`codigo_fuente/`](codigo_fuente/) | Código fuente organizado en paquetes `modelo`, `vista`, `controlador` y `util`, más `Main`. Su `README.md` trae los comandos exactos para compilar, ejecutar y generar el JAR. |
| [`documentacion/`](documentacion/) | Manual de usuario (PDF), diagrama de clases (PlantUML y Mermaid), plan de pruebas y capturas de pantalla. |
| [`video/`](video/) | Guion del video (6 min 37 s) y enlace a YouTube. |
| [`estudio/`](estudio/) | Guía de estudio: cada concepto de POO explicado en lenguaje sencillo y dónde está en el código. |

## Inicio rápido

Requiere JDK 17 o superior.

```bash
cd codigo_fuente
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

Para comprobar el banco de palabras y correr las pruebas automáticas:

```bash
java -cp out Main --verificar
java -cp out util.PruebasAutomaticas
```

Los comandos para Windows y para generar el JAR están en [`codigo_fuente/README.md`](codigo_fuente/README.md).
