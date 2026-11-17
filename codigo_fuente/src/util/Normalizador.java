package util;

import java.text.Normalizer;

/**
 * Utilidades para normalizar texto en español.
 * <p>
 * Decisión de diseño: las palabras se guardan en MAYÚSCULAS y SIN tildes ni diéresis
 * (Á→A, É→E, Í→I, Ó→O, Ú→U, Ü→U), pero se CONSERVA la Ñ, porque es una letra
 * distinta del alfabeto español y el teclado del juego tiene tecla Ñ.
 * El alfabeto válido es A–Z + Ñ.
 */
public final class Normalizador {

    /** Clase de utilidades: no se instancia. */
    private Normalizador() {
    }

    /**
     * Normaliza un texto: mayúsculas, sin tildes ni diéresis y conservando la Ñ.
     * Los caracteres que no son letras (espacios, dígitos...) se dejan tal cual.
     *
     * @param s texto original (puede ser {@code null})
     * @return texto normalizado (cadena vacía si {@code s} es {@code null})
     */
    public static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            char normalizada = normalizarLetra(c);
            resultado.append(normalizada != '\0' ? normalizada : c);
        }
        return resultado.toString();
    }

    /**
     * Convierte una letra a su forma normalizada (A–Z o Ñ).
     *
     * @param c carácter a normalizar
     * @return la letra normalizada en mayúscula, o {@code '\0'} si no es una letra válida
     */
    public static char normalizarLetra(char c) {
        char mayuscula = Character.toUpperCase(c);
        if (mayuscula == 'Ñ') {
            return 'Ñ';
        }
        // Se descompone la letra (p. ej. É = E + tilde) y se toma solo la base.
        String descompuesta = Normalizer.normalize(String.valueOf(mayuscula), Normalizer.Form.NFD);
        char base = descompuesta.charAt(0);
        if (base >= 'A' && base <= 'Z') {
            return base;
        }
        return '\0';
    }

    /**
     * Indica si un carácter es una letra válida del juego (A–Z, Ñ, con o sin tilde).
     *
     * @param c carácter a revisar
     * @return {@code true} si se puede normalizar a una letra A–Z o Ñ
     */
    public static boolean esLetraValida(char c) {
        return normalizarLetra(c) != '\0';
    }
}
