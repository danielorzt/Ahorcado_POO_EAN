package vista;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Panel que dibuja la horca y el ahorcado con Graphics2D (sin imágenes).
 * La horca (base, poste y viga) siempre es visible; las partes del cuerpo aparecen
 * una a una según el número de errores.
 */
public class PanelAhorcado extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Ancho del lienzo virtual en el que se dibuja (luego se escala al tamaño del panel). */
    private static final int LIENZO_ANCHO = 300;
    /** Alto del lienzo virtual. */
    private static final int LIENZO_ALTO = 400;
    private static final int ORIGEN_X = 210;
    private static final int ORIGEN_Y = 120;

    // ARRAYLIST: lista de partes del cuerpo, en el orden exacto en que se dibujan.
    private final ArrayList<ParteCuerpo> partes;
    private int errores;

    /** Crea el panel con las 7 partes en el orden de la especificación. */
    public PanelAhorcado() {
        setName("panelAhorcado"); // nombre estable para las pruebas de interfaz (AssertJ Swing)
        setBackground(Estilo.TARJETA);
        setBorder(Estilo.tarjeta("Ahorcado"));
        setPreferredSize(new Dimension(340, 420));
        setMinimumSize(new Dimension(220, 280));
        getAccessibleContext().setAccessibleName("Dibujo del ahorcado");

        partes = new ArrayList<>();
        // HERENCIA: siete subclases de ParteCuerpo guardadas en una lista del tipo padre.
        partes.add(new Cabeza(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new Torso(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new BrazoDerecho(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new BrazoIzquierdo(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new PiernaDerecha(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new PiernaIzquierda(ORIGEN_X, ORIGEN_Y, 1.0));
        partes.add(new Cuerda(ORIGEN_X, ORIGEN_Y, 1.0));
        setErrores(0);
    }

    /**
     * Cambia cuántas partes del cuerpo se muestran y vuelve a pintar.
     *
     * @param n cantidad de errores (0 a 7)
     */
    public void setErrores(int n) {
        this.errores = Math.max(0, Math.min(n, partes.size()));
        getAccessibleContext().setAccessibleDescription(
                "Errores: " + errores + " de " + partes.size() + ". "
                + (errores == 0 ? "Solo se ve la horca." : "Última parte dibujada: " + partes.get(errores - 1).getNombre() + "."));
        repaint();
    }

    /** @return cantidad de partes dibujadas actualmente */
    public int getErrores() {
        return errores;
    }

    /** @return nombres de las partes en el orden en que se dibujan */
    public ArrayList<String> getNombresPartes() {
        ArrayList<String> nombres = new ArrayList<>();
        for (ParteCuerpo parte : partes) {
            nombres.add(parte.getNombre());
        }
        return nombres;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Se centra y escala el lienzo virtual dentro del espacio disponible (sin el borde).
        int ancho = getWidth() - getInsets().left - getInsets().right;
        int alto = getHeight() - getInsets().top - getInsets().bottom;
        double escala = Math.min(ancho / (double) LIENZO_ANCHO, alto / (double) LIENZO_ALTO);
        double dx = getInsets().left + (ancho - LIENZO_ANCHO * escala) / 2.0;
        double dy = getInsets().top + (alto - LIENZO_ALTO * escala) / 2.0;
        g2.translate(dx, dy);
        g2.scale(escala, escala);

        dibujarHorca(g2);
        // POLIMORFISMO: cada elemento es una subclase distinta de ParteCuerpo y su dibujar() es propio.
        for (int i = 0; i < errores; i++) {
            partes.get(i).dibujar(g2);
        }
        g2.dispose();
    }

    /** Dibuja la horca fija: base, poste, viga y refuerzo. */
    private void dibujarHorca(Graphics2D g) {
        g.setColor(Estilo.MADERA);
        g.fillRoundRect(30, 360, 240, 20, 10, 10); // base
        g.fillRect(80, 60, 16, 300);               // poste
        g.fillRect(80, 52, 150, 16);               // viga
        g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(96, 120, 140, 68);              // refuerzo diagonal
    }
}
