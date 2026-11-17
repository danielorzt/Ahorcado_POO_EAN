package vista;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 * Séptimo error: la cuerda final, que baja desde la viga hasta la cabeza
 * y rodea el cuello. Con ella termina la partida como perdida.
 */
public class Cuerda extends ParteCuerpo {

    /**
     * Crea la cuerda.
     *
     * @param origenX X del centro de la cabeza
     * @param origenY Y del centro de la cabeza
     * @param escala  factor de tamaño
     */
    public Cuerda(int origenX, int origenY, double escala) {
        super("Cuerda", origenX, origenY, escala);
    }

    /** {@inheritDoc} */
    @Override
    public void dibujar(Graphics2D g) {
        g.setColor(Estilo.CUERDA);
        g.setStroke(new BasicStroke(grosor(4), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Soga desde la viga (52 sobre la cabeza) hasta la parte alta de la cabeza.
        g.drawLine(px(0), py(-52), px(0), py(-28));
        // Lazo alrededor del cuello.
        g.drawOval(px(-20), py(24), largo(40), largo(14));
    }
}
