package vista;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;

/** Teclado en pantalla con las letras A–Z y la Ñ. Cada tecla se deshabilita al usarse. */
public class PanelTeclado extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Letras del teclado, en orden del alfabeto español. */
    public static final String LETRAS = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";

    // ARRAYLIST: lista con los 27 botones para poder habilitarlos/deshabilitarlos.
    private final ArrayList<JButton> botones = new ArrayList<>();
    private Consumer<Character> escuchaLetra;

    /** Crea el teclado (3 filas de 9 teclas). */
    public PanelTeclado() {
        super(new GridLayout(3, 9, 5, 5));
        setName("panelTeclado"); // nombre estable para las pruebas de interfaz (AssertJ Swing)
        setBackground(Estilo.TARJETA);
        setBorder(Estilo.tarjeta("Teclado"));
        getAccessibleContext().setAccessibleName("Teclado en pantalla");
        getAccessibleContext().setAccessibleDescription("Letras de la A a la Z y la Ñ para jugar.");
        for (int i = 0; i < LETRAS.length(); i++) {
            final char letra = LETRAS.charAt(i);
            JButton boton = new JButton(String.valueOf(letra));
            boton.setName("btn_" + letra); // btn_A ... btn_Z y btn_Ñ
            boton.setFont(Estilo.TECLA);
            boton.setMargin(new java.awt.Insets(2, 2, 2, 2));
            boton.setToolTipText("Probar la letra " + letra);
            boton.getAccessibleContext().setAccessibleName("Letra " + letra);
            boton.getAccessibleContext().setAccessibleDescription("Prueba la letra " + letra + " en la palabra secreta.");
            boton.addActionListener(e -> {
                if (escuchaLetra != null) {
                    escuchaLetra.accept(letra);
                }
            });
            botones.add(boton);
            add(boton);
        }
    }

    /**
     * Define quién recibe la letra pulsada.
     *
     * @param escucha función que se llama con la letra elegida
     */
    public void setEscuchaLetra(Consumer<Character> escucha) {
        this.escuchaLetra = escucha;
    }

    /**
     * Habilita o deshabilita las teclas.
     *
     * @param usadas letras ya jugadas (quedan deshabilitadas)
     * @param activo {@code false} para deshabilitar todo el teclado (partida terminada)
     */
    public void setEnabledLetras(ArrayList<Character> usadas, boolean activo) {
        for (JButton boton : botones) {
            char letra = boton.getText().charAt(0);
            boton.setEnabled(activo && !usadas.contains(letra));
        }
    }

    /** @return copia de la lista de botones (en orden A–Z, Ñ) */
    public ArrayList<JButton> getBotones() {
        return new ArrayList<>(botones);
    }
}
