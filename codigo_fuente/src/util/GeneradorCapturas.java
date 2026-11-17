package util;

import controlador.ControladorJuego;
import controlador.VistaJuego;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import modelo.BancoPalabras;
import modelo.Palabra;
import modelo.Partida;
import vista.Estilo;
import vista.VentanaPrincipal;

/**
 * Herramienta de desarrollo para el manual de usuario: genera capturas PNG de la interfaz
 * pintando el contenido de la ventana en una imagen (sin mostrar la ventana).
 * <p>
 * Uso: {@code java -cp out util.GeneradorCapturas <carpeta_de_salida>}
 */
public final class GeneradorCapturas {

    /** Palabra fija para que las capturas sean siempre iguales. */
    private static final Palabra PALABRA = new Palabra("MURCIÉLAGO",
            "Es el único mamífero capaz de volar y descansa colgado de cabeza.", "Animales");

    /** Clase de utilidades: no se instancia. */
    private GeneradorCapturas() {
    }

    /**
     * Genera las capturas en la carpeta indicada.
     *
     * @param args {@code args[0]} = carpeta de salida
     * @throws Exception si falla la creación de la interfaz o el guardado de las imágenes
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Uso: java -cp out util.GeneradorCapturas <carpeta_de_salida>");
            System.exit(1);
        }
        Path carpeta = Paths.get(args[0]);
        Files.createDirectories(carpeta);
        // Todo el trabajo con Swing se hace en el hilo de eventos.
        SwingUtilities.invokeAndWait(() -> {
            try {
                generar(carpeta);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        System.exit(0);
    }

    private static void generar(Path carpeta) throws IOException {
        Estilo.configurarLookAndFeel();
        ControladorJuego controlador = new ControladorJuego(new BancoPalabras());
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);

        // Se cambia la vista por un adaptador que ignora el diálogo modal (si no, bloquearía la ejecución).
        // Esto es posible porque el controlador solo depende de la interfaz VistaJuego.
        controlador.setVista(new VistaJuego() {
            @Override
            public void actualizar(Partida partida) {
                ventana.actualizar(partida);
            }

            @Override
            public void mostrarFin(Partida partida) {
                // No se abre el diálogo: se captura aparte con crearDialogoFin().
            }

            @Override
            public void mostrarMensaje(String mensaje) {
                ventana.mostrarMensaje(mensaje);
            }
        });

        ventana.pack(); // crea los componentes nativos sin mostrar la ventana
        ventana.setSize(900, 680);

        // 1. Inicio
        controlador.iniciarPartidaConPalabra(PALABRA);
        guardar(ventana, carpeta.resolve("01-inicio.png"));

        // 2. Partida en curso: 3 aciertos y 3 errores
        for (char c : new char[] {'M', 'E', 'A', 'W', 'Z', 'K'}) {
            controlador.procesarLetra(c);
        }
        guardar(ventana, carpeta.resolve("02-partida-en-curso.png"));

        // 3. Pista de categoría usada
        controlador.usarPista(0);
        guardar(ventana, carpeta.resolve("03-pista-categoria.png"));

        // 4. Victoria
        controlador.iniciarPartidaConPalabra(PALABRA);
        controlador.procesarLetra('X'); // un error antes de ganar, para que se vea el dibujo
        for (char c : "MURCIELAGO".toCharArray()) {
            controlador.procesarLetra(c);
        }
        guardar(ventana, carpeta.resolve("04-victoria.png"));
        guardar(ventana.crearDialogoFin(controlador.getPartida()), carpeta.resolve("06-dialogo-victoria.png"));

        // 5. Derrota (7 errores, cuerda visible)
        controlador.iniciarPartidaConPalabra(PALABRA);
        controlador.procesarLetra('M');
        for (char c : new char[] {'W', 'Z', 'K', 'X', 'Q', 'J', 'V'}) {
            controlador.procesarLetra(c);
        }
        guardar(ventana, carpeta.resolve("05-derrota.png"));
        guardar(ventana.crearDialogoFin(controlador.getPartida()), carpeta.resolve("07-dialogo-derrota.png"));

        ventana.dispose();
    }

    /** Pinta el contenido de una ventana o diálogo en un PNG. */
    private static void guardar(Object contenedor, Path archivo) throws IOException {
        Component raiz;
        if (contenedor instanceof VentanaPrincipal) {
            VentanaPrincipal v = (VentanaPrincipal) contenedor;
            v.validate();
            raiz = v.getContentPane();
        } else {
            JDialog d = (JDialog) contenedor;
            d.validate();
            raiz = d.getContentPane();
        }
        Dimension tam = raiz.getSize();
        BufferedImage imagen = new BufferedImage(tam.width, tam.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, tam.width, tam.height);
        raiz.printAll(g);
        g.dispose();
        ImageIO.write(imagen, "png", archivo.toFile());
        System.out.println("Captura generada: " + archivo.getFileName() + " (" + tam.width + "x" + tam.height + ")");
    }
}
