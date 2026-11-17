package vista;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Sexto error: la pierna izquierda (la que queda a la izquierda de la pantalla). */
public class PiernaIzquierda extends ParteCuerpo {

    /**
     * Crea la pierna izquierda.
     *
     * @param origenX X del centro de la cabeza
     * @param origenY Y del centro de la cabeza
     * @param escala  factor de tamaño
     */
    public PiernaIzquierda(int origenX, int origenY, double escala) {
        super("Pierna izquierda", origenX, origenY, escala);
    }

    /** {@inheritDoc} */
    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Estilo.PRIMARIO);
        g.setStroke(new BasicStroke(grosor(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(px(0), py(110), px(-40), py(172));
    }
}
