package modelo;

/**
 * Nivel de dificultad de una palabra. Se calcula por la LONGITUD de la palabra normalizada:
 * FÁCIL = 3 a 5 letras, MEDIO = 6 a 8 letras, DIFÍCIL = 9 o más letras.
 */
public enum Dificultad {
    FACIL("FÁCIL"),
    MEDIO("MEDIO"),
    DIFICIL("DIFÍCIL");

    private final String etiqueta;

    Dificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Texto para mostrar en pantalla (con tilde).
     *
     * @return etiqueta del nivel
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Devuelve la dificultad que corresponde a una longitud de palabra.
     *
     * @param longitud cantidad de letras de la palabra normalizada
     * @return FACIL (hasta 5), MEDIO (6 a 8) o DIFICIL (9 o más)
     */
    public static Dificultad paraLongitud(int longitud) {
        if (longitud <= 5) {
            return FACIL;
        }
        if (longitud <= 8) {
            return MEDIO;
        }
        return DIFICIL;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
