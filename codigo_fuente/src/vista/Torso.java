package vista;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Segundo error: el torso (línea vertical bajo la cabeza). */
public class Torso extends ParteCuerpo {

    /**
     * Crea el torso.
     *
     * @param origenX X del centro de la cabeza
     * @param origenY Y del centro de la cabeza
     * @param escala  factor de tamaño
     */
    public Torso(int origenX, int origenY, double escala) {
        super("Torso", origenX, origenY, escala);
    }

    /** {@inheritDoc} */
    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Estilo.PRIMARIO);
        g.setStroke(new BasicStroke(grosor(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(px(0), py(28), px(0), py(110));
    }
}
