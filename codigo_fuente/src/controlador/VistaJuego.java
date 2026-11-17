package controlador;

import modelo.Partida;

/**
 * Contrato que debe cumplir cualquier vista del juego.
 * El controlador solo conoce esta interfaz, así NO depende de Swing (bajo acoplamiento).
 */
public interface VistaJuego {

    /**
     * Redibuja la pantalla con el estado actual de la partida.
     *
     * @param partida partida a mostrar
     */
    void actualizar(Partida partida);

    /**
     * Muestra el final de la partida (ganó o perdió) y la palabra revelada.
     *
     * @param partida partida terminada
     */
    void mostrarFin(Partida partida);

    /**
     * Muestra un mensaje corto al jugador.
     *
     * @param mensaje texto del mensaje
     */
    void mostrarMensaje(String mensaje);
}
