package modelo;

import java.util.ArrayList;
import util.Normalizador;

/**
 * Una partida del ahorcado: guarda la palabra secreta, las letras probadas, las pistas
 * y el estado. Toda la lógica de las reglas está aquí (no depende de Swing).
 */
public class Partida {

    /** Máximo de intentos fallidos permitidos. */
    public static final int MAX_ERRORES = 7;

    // ENCAPSULAMIENTO: todo el estado es privado; solo se cambia con adivinar/usarPista.
    private Palabra palabraSecreta;
    // ARRAYLIST: listas dinámicas de letras acertadas, falladas y pistas de la partida.
    private ArrayList<Character> letrasCorrectas;
    private ArrayList<Character> letrasIncorrectas;
    private ArrayList<Pista> pistas;
    private ArrayList<String> pistasObtenidas;
    private EstadoJuego estado;

    /**
     * Crea una partida nueva con sus tres pistas (Categoría, Letra y Texto, en ese orden).
     *
     * @param palabra palabra secreta
     */
    public Partida(Palabra palabra) {
        this.palabraSecreta = palabra;
        this.letrasCorrectas = new ArrayList<>();
        this.letrasIncorrectas = new ArrayList<>();
        this.pistas = new ArrayList<>();
        this.pistasObtenidas = new ArrayList<>();
        this.estado = EstadoJuego.EN_CURSO;
        // POLIMORFISMO: la lista guarda Pista, pero cada elemento es una subclase distinta.
        pistas.add(new PistaCategoria());
        pistas.add(new PistaLetra());
        pistas.add(new PistaTexto());
    }

    /**
     * Intenta una letra.
     *
     * @param letra letra escrita por el jugador (con o sin tilde, mayúscula o minúscula)
     * @return el resultado de la jugada
     */
    public ResultadoLetra adivinar(char letra) {
        char l = Normalizador.normalizarLetra(letra);
        if (l == '\0') {
            return ResultadoLetra.INVALIDA;
        }
        if (estado != EstadoJuego.EN_CURSO) {
            return ResultadoLetra.PARTIDA_TERMINADA;
        }
        if (letrasCorrectas.contains(l) || letrasIncorrectas.contains(l)) {
            return ResultadoLetra.REPETIDA;
        }
        if (palabraSecreta.contiene(l)) {
            letrasCorrectas.add(l);
            actualizarEstado();
            return ResultadoLetra.ACERTADA;
        }
        letrasIncorrectas.add(l);
        actualizarEstado();
        return ResultadoLetra.FALLADA;
    }

    /**
     * Revela una letra de la palabra SIN contar error (la usa la pista de letra).
     *
     * @param letra letra a revelar
     */
    public void revelarLetra(char letra) {
        char l = Normalizador.normalizarLetra(letra);
        if (l == '\0' || estado != EstadoJuego.EN_CURSO) {
            return;
        }
        if (palabraSecreta.contiene(l) && !letrasCorrectas.contains(l)) {
            letrasCorrectas.add(l);
            actualizarEstado();
        }
    }

    /**
     * Usa una pista.
     *
     * @param indice 0 = categoría, 1 = letra, 2 = pista escrita
     * @return el texto de la pista, o {@code null} si ya se usó, el índice es inválido o la partida terminó
     */
    public String usarPista(int indice) {
        if (indice < 0 || indice >= pistas.size() || estado != EstadoJuego.EN_CURSO) {
            return null;
        }
        // POLIMORFISMO: usar() ejecuta el obtener() de PistaCategoria, PistaLetra o PistaTexto.
        String texto = pistas.get(indice).usar(this);
        if (texto != null) {
            pistasObtenidas.add(texto);
        }
        return texto;
    }

    /**
     * Indica si una pista se puede usar todavía.
     *
     * @param indice 0, 1 o 2
     * @return {@code true} si no se ha usado y la partida sigue en curso
     */
    public boolean pistaDisponible(int indice) {
        return indice >= 0 && indice < pistas.size()
                && !pistas.get(indice).isUsada()
                && estado == EstadoJuego.EN_CURSO;
    }

    /** @return cantidad de pistas que aún no se han usado */
    public int pistasRestantes() {
        int restantes = 0;
        for (Pista p : pistas) {
            if (!p.isUsada()) {
                restantes++;
            }
        }
        return restantes;
    }

    /** @return cantidad de errores cometidos */
    public int getErrores() {
        return letrasIncorrectas.size();
    }

    /** @return intentos fallidos que aún quedan */
    public int intentosRestantes() {
        return MAX_ERRORES - getErrores();
    }

    /**
     * Palabra con guiones bajos para las letras no reveladas, separadas por un espacio.
     *
     * @return por ejemplo {@code _ _ A _}
     */
    public String palabraOculta() {
        String texto = palabraSecreta.getTexto();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            char c = texto.charAt(i);
            sb.append(letrasCorrectas.contains(c) ? c : '_');
        }
        return sb.toString();
    }

    /** @return {@code true} si todas las letras de la palabra ya fueron descubiertas */
    public boolean estaCompleta() {
        String texto = palabraSecreta.getTexto();
        for (int i = 0; i < texto.length(); i++) {
            if (!letrasCorrectas.contains(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** @return la palabra secreta */
    public Palabra getPalabra() {
        return palabraSecreta;
    }

    /** @return el estado actual de la partida */
    public EstadoJuego getEstado() {
        return estado;
    }

    /** @return copia de las letras acertadas, en orden de aparición */
    public ArrayList<Character> getLetrasCorrectas() {
        return new ArrayList<>(letrasCorrectas);
    }

    /** @return copia de las letras falladas, en orden de aparición */
    public ArrayList<Character> getLetrasIncorrectas() {
        return new ArrayList<>(letrasIncorrectas);
    }

    /** @return copia de los textos de las pistas ya obtenidas, en orden de uso */
    public ArrayList<String> getPistasObtenidas() {
        return new ArrayList<>(pistasObtenidas);
    }

    /** Recalcula si la partida se ganó o se perdió. */
    private void actualizarEstado() {
        if (estaCompleta()) {
            estado = EstadoJuego.GANADA;
        } else if (getErrores() >= MAX_ERRORES) {
            estado = EstadoJuego.PERDIDA;
        }
    }
}
