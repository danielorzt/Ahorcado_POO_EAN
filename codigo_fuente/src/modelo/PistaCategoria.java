package modelo;

/** Pista que revela la categoría de la palabra secreta. */
public class PistaCategoria extends Pista {

    /** Crea la pista de categoría. */
    public PistaCategoria() {
        super("Categoría", "Muestra la categoría de la palabra secreta.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * HERENCIA: implementa el método abstracto de {@link Pista}.
     */
    @Override
    public String obtener(Partida p) {
        // POLIMORFISMO: misma firma que las otras pistas, comportamiento propio.
        return "Categoría: " + p.getPalabra().getCategoria();
    }
}
