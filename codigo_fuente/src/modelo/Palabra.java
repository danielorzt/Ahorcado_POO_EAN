package modelo;

import java.util.Objects;
import util.Normalizador;

/**
 * Palabra secreta del juego. Es una clase INMUTABLE: todos sus campos son privados y finales
 * y no existen métodos que los modifiquen.
 */
public final class Palabra {

    // ENCAPSULAMIENTO: campos privados y finales (clase inmutable), solo se leen con getters.
    private final String texto;
    private final String textoOriginal;
    private final String pista;
    private final String categoria;
    private final Dificultad dificultad;

    /**
     * Crea una palabra normalizando el texto y calculando su dificultad por longitud.
     *
     * @param textoOriginal palabra con tildes, p. ej. "MURCIÉLAGO"
     * @param pista         texto de pista propio de la palabra
     * @param categoria     nombre de la categoría
     * @throws IllegalArgumentException si la palabra está vacía o tiene caracteres no válidos
     */
    public Palabra(String textoOriginal, String pista, String categoria) {
        if (textoOriginal == null || textoOriginal.isBlank()) {
            throw new IllegalArgumentException("La palabra no puede estar vacía.");
        }
        this.textoOriginal = textoOriginal.trim().toUpperCase();
        this.texto = Normalizador.normalizar(this.textoOriginal);
        for (int i = 0; i < texto.length(); i++) {
            if (!Normalizador.esLetraValida(texto.charAt(i))) {
                throw new IllegalArgumentException("Carácter no válido en la palabra: " + textoOriginal);
            }
        }
        this.pista = pista == null ? "" : pista;
        this.categoria = categoria == null ? "" : categoria;
        this.dificultad = Dificultad.paraLongitud(texto.length());
    }

    /** @return la palabra normalizada (mayúsculas, sin tildes, con Ñ) */
    public String getTexto() {
        return texto;
    }

    /** @return la palabra original con tildes, para mostrarla al terminar la partida */
    public String getTextoOriginal() {
        return textoOriginal;
    }

    /** @return el texto de pista propio de la palabra */
    public String getPista() {
        return pista;
    }

    /** @return el nombre de la categoría de la palabra */
    public String getCategoria() {
        return categoria;
    }

    /** @return la dificultad calculada según la longitud */
    public Dificultad getDificultad() {
        return dificultad;
    }

    /** @return cantidad de letras de la palabra normalizada */
    public int longitud() {
        return texto.length();
    }

    /**
     * Indica si la palabra contiene una letra (se normaliza antes de comparar).
     *
     * @param letra letra a buscar, con o sin tilde
     * @return {@code true} si la letra aparece en la palabra
     */
    public boolean contiene(char letra) {
        char normalizada = Normalizador.normalizarLetra(letra);
        return normalizada != '\0' && texto.indexOf(normalizada) >= 0;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Palabra)) {
            return false;
        }
        return texto.equals(((Palabra) otro).texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto);
    }

    @Override
    public String toString() {
        return textoOriginal;
    }
}
