package controlador;

import modelo.BancoPalabras;
import modelo.Dificultad;
import modelo.EstadoJuego;
import modelo.Palabra;
import modelo.Partida;
import modelo.ResultadoLetra;
import util.Normalizador;

/**
 * Controlador (patrón MVC): recibe las acciones del jugador, las aplica sobre el modelo
 * ({@link Partida}) y le pide a la vista que se actualice mediante {@link VistaJuego}.
 */
public class ControladorJuego {

    private final BancoPalabras banco;
    // ENCAPSULAMIENTO: la vista se guarda como INTERFAZ, no como una ventana concreta.
    private VistaJuego vista;
    private Partida partida;

    /**
     * Crea el controlador.
     *
     * @param banco banco de palabras del que se eligen las palabras secretas
     */
    public ControladorJuego(BancoPalabras banco) {
        this.banco = banco;
    }

    /**
     * Conecta la vista con el controlador.
     *
     * @param vista vista que recibirá las actualizaciones
     */
    public void setVista(VistaJuego vista) {
        this.vista = vista;
    }

    /**
     * Inicia una partida nueva con una palabra elegida al azar.
     *
     * @param dificultad nivel pedido, o {@code null} para aleatoria
     */
    public void iniciarNuevaPartida(Dificultad dificultad) {
        iniciarPartidaConPalabra(banco.elegirAleatoria(dificultad));
    }

    /**
     * Gancho de pruebas y capturas: inicia una partida con una palabra concreta (sin azar).
     * Lo usan {@code util.PruebasAutomaticas} y {@code util.GeneradorCapturas}.
     *
     * @param palabra palabra secreta que se quiere usar
     */
    public void iniciarPartidaConPalabra(Palabra palabra) {
        partida = new Partida(palabra);
        if (vista != null) {
            vista.actualizar(partida);
            vista.mostrarMensaje("Nueva partida: la palabra tiene " + palabra.longitud() + " letras.");
        }
    }

    /**
     * Procesa una letra (del teclado en pantalla o del físico).
     *
     * @param letra letra jugada
     */
    public void procesarLetra(char letra) {
        if (partida == null) {
            return;
        }
        ResultadoLetra resultado = partida.adivinar(letra);
        char normalizada = Normalizador.normalizarLetra(letra);
        switch (resultado) {
            case ACERTADA:
                avisar("¡Bien! La letra " + normalizada + " está en la palabra.");
                break;
            case FALLADA:
                avisar("La letra " + normalizada + " no está. Intentos restantes: " + partida.intentosRestantes() + ".");
                break;
            case REPETIDA:
                avisar("Ya habías probado la letra " + normalizada + ". No se penaliza.");
                break;
            case INVALIDA:
                avisar("Solo se aceptan letras de la A a la Z y la Ñ.");
                return;
            case PARTIDA_TERMINADA:
            default:
                return; // se ignora la entrada
        }
        refrescar();
    }

    /**
     * Usa una pista de la partida actual.
     *
     * @param indice 0 = categoría, 1 = letra, 2 = pista escrita
     */
    public void usarPista(int indice) {
        if (partida == null) {
            return;
        }
        String texto = partida.usarPista(indice);
        if (texto == null) {
            avisar("Esa pista ya no está disponible.");
            return;
        }
        avisar("Pista: " + texto);
        refrescar();
    }

    /** @return la partida actual (puede ser {@code null} si aún no se inició ninguna) */
    public Partida getPartida() {
        return partida;
    }

    /** Cierra la aplicación. */
    public void salir() {
        System.exit(0);
    }

    /** Actualiza la vista y, si la partida terminó, muestra el final. */
    private void refrescar() {
        if (vista == null) {
            return;
        }
        vista.actualizar(partida);
        if (partida.getEstado() != EstadoJuego.EN_CURSO) {
            vista.mostrarFin(partida);
        }
    }

    private void avisar(String mensaje) {
        if (vista != null) {
            vista.mostrarMensaje(mensaje);
        }
    }
}
