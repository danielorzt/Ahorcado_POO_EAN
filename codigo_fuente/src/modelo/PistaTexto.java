package modelo;

/** Pista escrita propia de cada palabra (con tildes). */
public class PistaTexto extends Pista {

    /** Crea la pista escrita. */
    public PistaTexto() {
        super("Pista escrita", "Muestra una frase de ayuda propia de la palabra.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * HERENCIA: implementa el método abstracto de {@link Pista}.
     */
    @Override
    public String obtener(Partida p) {
        return p.getPalabra().getPista();
    }
}
