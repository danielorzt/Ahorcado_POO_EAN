package modelo;

/**
 * Pista abstracta que el jugador puede usar UNA sola vez por partida.
 * Cada tipo de pista (categoría, letra, texto) es una subclase que decide qué entregar.
 */
public abstract class Pista {

    // ENCAPSULAMIENTO: estado privado; solo se cambia mediante usar().
    private final String nombre;
    private final String descripcion;
    private boolean usada;

    /**
     * Crea una pista sin usar.
     *
     * @param nombre      nombre corto (para el botón)
     * @param descripcion explicación de qué hace la pista
     */
    protected Pista(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usada = false;
    }

    /**
     * Método PLANTILLA: si la pista ya se usó devuelve {@code null}; si no, la marca como usada
     * y delega en {@link #obtener(Partida)}, que implementa cada subclase.
     *
     * @param partida partida sobre la que se usa la pista
     * @return el texto de la pista, o {@code null} si ya estaba usada
     */
    public String usar(Partida partida) {
        if (usada) {
            return null;
        }
        usada = true;
        // POLIMORFISMO: se llama a obtener() y se ejecuta la versión de la subclase real.
        return obtener(partida);
    }

    /**
     * Produce el contenido de la pista (lo define cada subclase).
     *
     * @param partida partida actual
     * @return texto que se muestra al jugador
     */
    public abstract String obtener(Partida partida);

    /** @return {@code true} si la pista ya fue usada */
    public boolean isUsada() {
        return usada;
    }

    /** @return nombre corto de la pista */
    public String getNombre() {
        return nombre;
    }

    /** @return descripción de la pista */
    public String getDescripcion() {
        return descripcion;
    }
}
