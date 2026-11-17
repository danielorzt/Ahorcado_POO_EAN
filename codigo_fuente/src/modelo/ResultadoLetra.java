package modelo;

/** Resultado de intentar una letra en una partida. */
public enum ResultadoLetra {
    /** La letra está en la palabra. */
    ACERTADA,
    /** La letra NO está en la palabra (cuenta como error). */
    FALLADA,
    /** La letra ya se había probado (no se penaliza). */
    REPETIDA,
    /** El carácter no es una letra válida (A–Z, Ñ). */
    INVALIDA,
    /** La partida ya terminó: la entrada se ignora. */
    PARTIDA_TERMINADA
}
