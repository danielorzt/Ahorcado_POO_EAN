import controlador.ControladorJuego;
import javax.swing.SwingUtilities;
import modelo.BancoPalabras;
import util.VerificadorBanco;
import vista.Estilo;
import vista.VentanaPrincipal;

/**
 * Punto de entrada del Juego del Ahorcado (Universidad EAN, Programación Orientada a Objetos).
 * <p>
 * Uso: {@code java Main} abre el juego; {@code java Main --verificar} revisa el banco de palabras.
 */
public class Main {

    /**
     * Arranca el juego o verifica el banco.
     *
     * @param args {@code --verificar} para solo revisar el banco de palabras
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--verificar")) {
            VerificadorBanco.main(new String[0]);
            return;
        }

        BancoPalabras banco = new BancoPalabras();
        System.out.println("Banco cargado: " + banco.totalPalabras() + " palabras en "
                + banco.getCategorias().size() + " categorías");

        ControladorJuego controlador = new ControladorJuego(banco);
        SwingUtilities.invokeLater(() -> {
            Estilo.configurarLookAndFeel();
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            controlador.iniciarNuevaPartida(null);
            ventana.setVisible(true);
        });
    }
}
