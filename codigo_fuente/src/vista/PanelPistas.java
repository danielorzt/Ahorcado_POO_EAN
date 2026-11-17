package vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Panel con los tres botones de pista, el contador de pistas restantes y las pistas obtenidas. */
public class PanelPistas extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JButton[] botones = new JButton[3];
    private final JLabel etiquetaRestantes = new JLabel();
    private final JTextArea areaPistas = new JTextArea();
    private Consumer<Integer> escuchaPista;

    /** Crea el panel de pistas. */
    public PanelPistas() {
        super(new BorderLayout(0, 8));
        setName("panelPistas"); // nombre estable para las pruebas de interfaz (AssertJ Swing)
        setBackground(Estilo.TARJETA);
        setBorder(Estilo.tarjeta("Pistas"));

        String[] textos = {"Categoría", "Letra", "Pista escrita"};
        int[] mnemonicos = {java.awt.event.KeyEvent.VK_C, java.awt.event.KeyEvent.VK_L, java.awt.event.KeyEvent.VK_E};
        String[] descripciones = {
            "Muestra la categoría de la palabra secreta. Se puede usar una vez por partida.",
            "Revela una letra aún no descubierta sin contar como error. Se puede usar una vez por partida.",
            "Muestra una frase de ayuda propia de la palabra. Se puede usar una vez por partida."
        };

        String[] nombresBotones = {"btnPistaCategoria", "btnPistaLetra", "btnPistaTexto"};

        JPanel filaBotones = new JPanel(new GridLayout(1, 3, 6, 0));
        filaBotones.setOpaque(false);
        for (int i = 0; i < botones.length; i++) {
            final int indice = i;
            JButton boton = new JButton(textos[i]);
            boton.setName(nombresBotones[i]);
            Estilo.estilizarBoton(boton);
            boton.setMnemonic(mnemonicos[i]);
            boton.setToolTipText(descripciones[i]);
            boton.getAccessibleContext().setAccessibleName("Pista: " + textos[i]);
            boton.getAccessibleContext().setAccessibleDescription(descripciones[i]);
            boton.addActionListener(e -> {
                if (escuchaPista != null) {
                    escuchaPista.accept(indice);
                }
            });
            botones[i] = boton;
            filaBotones.add(boton);
        }

        etiquetaRestantes.setName("lblPistasRestantes");
        etiquetaRestantes.setFont(Estilo.SUBTITULO);
        etiquetaRestantes.setForeground(Estilo.TEXTO);
        etiquetaRestantes.getAccessibleContext().setAccessibleName("Pistas restantes");
        setPistasRestantes(3);

        JPanel superior = new JPanel(new GridLayout(2, 1, 0, 6));
        superior.setOpaque(false);
        superior.add(etiquetaRestantes);
        superior.add(filaBotones);

        areaPistas.setName("txtPistas");
        areaPistas.setEditable(false);
        areaPistas.setLineWrap(true);
        areaPistas.setWrapStyleWord(true);
        areaPistas.setFont(Estilo.NORMAL);
        areaPistas.setForeground(Estilo.TEXTO);
        areaPistas.setMargin(new java.awt.Insets(6, 8, 6, 8));
        areaPistas.getAccessibleContext().setAccessibleName("Pistas obtenidas");
        areaPistas.getAccessibleContext().setAccessibleDescription("Área de solo lectura con las pistas que ya usaste.");
        JScrollPane desplazamiento = new JScrollPane(areaPistas);
        desplazamiento.setBorder(javax.swing.BorderFactory.createLineBorder(Estilo.BORDE));

        add(superior, BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
    }

    /**
     * Define quién recibe el índice de la pista pulsada.
     *
     * @param escucha función que se llama con 0, 1 o 2
     */
    public void setEscuchaPista(Consumer<Integer> escucha) {
        this.escuchaPista = escucha;
    }

    /**
     * Deshabilita el botón de una pista ya usada.
     *
     * @param i índice de la pista (0, 1 o 2)
     */
    public void setPistaUsada(int i) {
        setPistaDisponible(i, false);
    }

    /**
     * Habilita o deshabilita el botón de una pista.
     *
     * @param i          índice de la pista
     * @param disponible {@code true} para habilitarlo
     */
    public void setPistaDisponible(int i, boolean disponible) {
        if (i >= 0 && i < botones.length) {
            botones[i].setEnabled(disponible);
        }
    }

    /**
     * Actualiza la etiqueta "Pistas restantes: N".
     *
     * @param n pistas que aún se pueden usar
     */
    public void setPistasRestantes(int n) {
        etiquetaRestantes.setText("Pistas restantes: " + n);
        etiquetaRestantes.getAccessibleContext().setAccessibleDescription("Te quedan " + n + " pistas por usar.");
    }

    /**
     * Muestra en el área de texto las pistas obtenidas (una por línea).
     *
     * @param textos pistas obtenidas, en orden de uso
     */
    public void setTextoPistas(ArrayList<String> textos) {
        areaPistas.setText(String.join("\n\n", textos));
        areaPistas.setCaretPosition(0);
    }

    /** @return los tres botones de pista (categoría, letra, pista escrita) */
    public ArrayList<JButton> getBotones() {
        ArrayList<JButton> lista = new ArrayList<>();
        for (JButton b : botones) {
            lista.add(b);
        }
        return lista;
    }
}
