package modelo;

/** Estado en el que se encuentra una partida. */
public enum EstadoJuego {
    /** La partida sigue: aún hay intentos y faltan letras. */
    EN_CURSO,
    /** Se adivinaron todas las letras de la palabra. */
    GANADA,
    /** Se agotaron los 7 intentos fallidos. */
    PERDIDA
}
