package vista;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/** Primer error: la cabeza (círculo con ojos y boca). */
public class Cabeza extends ParteCuerpo {

    /**
     * Crea la cabeza.
     *
     * @param origenX X del centro de la cabeza
     * @param origenY Y del centro de la cabeza
     * @param escala  factor de tamaño
     */
    public Cabeza(int origenX, int origenY, double escala) {
        super("Cabeza", origenX, origenY, escala);
    }

    /** {@inheritDoc} */
    @Override
    public void dibujar(Graphics2D g) {
        // HERENCIA: Cabeza hereda px(), py() y largo() de ParteCuerpo.
        g.setColor(Estilo.PRIMARIO);
        g.setStroke(new BasicStroke(grosor(5), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int r = largo(28);
        g.drawOval(px(-28), py(-28), 2 * r, 2 * r);
        // Ojos y boca
        g.fillOval(px(-12), py(-9), largo(6), largo(6));
        g.fillOval(px(6), py(-9), largo(6), largo(6));
        g.setStroke(new BasicStroke(grosor(3), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(px(-11), py(4), largo(22), largo(14), 200, 140);
    }
}
