package vista;

import java.awt.Graphics2D;

/**
 * Parte del cuerpo del ahorcado (clase abstracta). Cada subclase sabe dibujarse a sí misma.
 * <p>
 * Todas las partes usan el mismo origen (el centro de la cabeza) y una escala; las coordenadas
 * de cada figura se calculan relativas a ese origen.
 */
public abstract class ParteCuerpo {

    // ENCAPSULAMIENTO: datos privados; las subclases los usan mediante px() y py().
    private final String nombre;
    private final int origenX;
    private final int origenY;
    private final double escala;

    /**
     * Crea una parte del cuerpo.
     *
     * @param nombre  nombre de la parte (p. ej. "Cabeza")
     * @param origenX coordenada X del centro de la cabeza
     * @param origenY coordenada Y del centro de la cabeza
     * @param escala  factor de tamaño (1.0 = tamaño normal)
     */
    protected ParteCuerpo(String nombre, int origenX, int origenY, double escala) {
        this.nombre = nombre;
        this.origenX = origenX;
        this.origenY = origenY;
        this.escala = escala;
    }

    /**
     * Dibuja la parte. Cada subclase implementa su propio dibujo.
     *
     * @param g contexto gráfico
     */
    public abstract void dibujar(Graphics2D g);

    /** @return nombre de la parte del cuerpo */
    public String getNombre() {
        return nombre;
    }

    /**
     * Convierte un desplazamiento horizontal relativo al origen en coordenada X real.
     *
     * @param desplazamiento distancia desde el origen en unidades sin escalar
     * @return coordenada X en píxeles
     */
    protected int px(double desplazamiento) {
        return (int) Math.round(origenX + desplazamiento * escala);
    }

    /**
     * Convierte un desplazamiento vertical relativo al origen en coordenada Y real.
     *
     * @param desplazamiento distancia desde el origen en unidades sin escalar
     * @return coordenada Y en píxeles
     */
    protected int py(double desplazamiento) {
        return (int) Math.round(origenY + desplazamiento * escala);
    }

    /**
     * Escala una longitud (radio, grosor...).
     *
     * @param valor longitud sin escalar
     * @return longitud escalada
     */
    protected int largo(double valor) {
        return (int) Math.round(valor * escala);
    }

    /**
     * Escala un grosor de trazo.
     *
     * @param valor grosor sin escalar
     * @return grosor escalado
     */
    protected float grosor(double valor) {
        return (float) (valor * escala);
    }
}
