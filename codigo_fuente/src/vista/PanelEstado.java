package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import modelo.Partida;

/** Panel con la palabra oculta, las letras correctas e incorrectas y los intentos restantes. */
public class PanelEstado extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel etiquetaPalabra = new JLabel("", SwingConstants.CENTER);
    private final JLabel etiquetaDificultad = new JLabel();
    private final JLabel etiquetaCorrectas = new JLabel();
    private final JLabel etiquetaIncorrectas = new JLabel();
    private final JLabel etiquetaIntentos = new JLabel();

    /** Crea el panel de estado. */
    public PanelEstado() {
        super(new BorderLayout(0, 6));
        setName("panelEstado"); // nombres estables para las pruebas de interfaz (AssertJ Swing)
        etiquetaPalabra.setName("lblPalabraOculta");
        etiquetaCorrectas.setName("lblLetrasCorrectas");
        etiquetaIncorrectas.setName("lblLetrasIncorrectas");
        etiquetaIntentos.setName("lblIntentos");
        setBackground(Estilo.TARJETA);
        setBorder(Estilo.tarjeta("Palabra secreta"));

        etiquetaPalabra.setFont(Estilo.PALABRA);
        etiquetaPalabra.setForeground(Estilo.PRIMARIO);
        etiquetaPalabra.getAccessibleContext().setAccessibleName("Palabra secreta");

        etiquetaDificultad.setFont(Estilo.NORMAL);
        etiquetaDificultad.setForeground(Estilo.TEXTO);
        etiquetaCorrectas.setFont(Estilo.SUBTITULO);
        etiquetaCorrectas.setForeground(Estilo.ACIERTO);
        etiquetaIncorrectas.setFont(Estilo.SUBTITULO);
        etiquetaIncorrectas.setForeground(Estilo.ERROR);
        etiquetaIntentos.setFont(Estilo.SUBTITULO);
        etiquetaIntentos.setForeground(Estilo.TEXTO);

        etiquetaDificultad.getAccessibleContext().setAccessibleName("Dificultad de la palabra");
        etiquetaCorrectas.getAccessibleContext().setAccessibleName("Letras correctas");
        etiquetaIncorrectas.getAccessibleContext().setAccessibleName("Letras incorrectas");
        etiquetaIntentos.getAccessibleContext().setAccessibleName("Intentos restantes");

        JPanel datos = new JPanel(new GridLayout(4, 1, 0, 2));
        datos.setOpaque(false);
        datos.add(etiquetaDificultad);
        datos.add(etiquetaCorrectas);
        datos.add(etiquetaIncorrectas);
        datos.add(etiquetaIntentos);

        add(etiquetaPalabra, BorderLayout.NORTH);
        add(datos, BorderLayout.CENTER);
    }

    /**
     * Refresca todas las etiquetas con el estado de la partida.
     *
     * @param p partida a mostrar
     */
    public void actualizar(Partida p) {
        String oculta = p.palabraOculta();
        // Se reduce la fuente en palabras largas para que quepan en una sola línea.
        float tamano = Math.max(14f, Math.min(32f, 800f / Math.max(1, oculta.length())));
        etiquetaPalabra.setFont(Estilo.PALABRA.deriveFont(tamano));
        etiquetaPalabra.setText(oculta);
        etiquetaPalabra.getAccessibleContext().setAccessibleDescription(
                "Palabra de " + p.getPalabra().longitud() + " letras: " + oculta.replace("_", "guion"));

        etiquetaDificultad.setText("Dificultad: " + p.getPalabra().getDificultad().getEtiqueta()
                + "  (" + p.getPalabra().longitud() + " letras)");
        etiquetaCorrectas.setText("Letras correctas: " + unir(p.getLetrasCorrectas()));
        etiquetaIncorrectas.setText("Letras incorrectas: " + unir(p.getLetrasIncorrectas()));
        etiquetaIntentos.setText("Intentos restantes: " + p.intentosRestantes() + " de " + Partida.MAX_ERRORES);
    }

    /** Une las letras con comas; si no hay ninguna devuelve "ninguna". */
    private String unir(ArrayList<Character> letras) {
        if (letras.isEmpty()) {
            return "ninguna";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < letras.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(letras.get(i));
        }
        return sb.toString();
    }
}
