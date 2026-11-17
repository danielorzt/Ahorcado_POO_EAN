package vista;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Cuarto error: el brazo izquierdo (el que queda a la izquierda de la pantalla). */
public class BrazoIzquierdo extends ParteCuerpo {

    /**
     * Crea el brazo izquierdo.
     *
     * @param origenX X del centro de la cabeza
     * @param origenY Y del centro de la cabeza
     * @param escala  factor de tamaño
     */
    public BrazoIzquierdo(int origenX, int origenY, double escala) {
        super("Brazo izquierdo", origenX, origenY, escala);
    }

    /** {@inheritDoc} */
    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Estilo.PRIMARIO);
        g.setStroke(new BasicStroke(grosor(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(px(0), py(48), px(-46), py(88));
    }
}
