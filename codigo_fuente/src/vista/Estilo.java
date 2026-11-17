package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Constantes de colores y fuentes, y utilidades de estilo para que toda la interfaz
 * tenga un aspecto consistente.
 */
public final class Estilo {

    /** Color de fondo general de la ventana. */
    public static final Color FONDO = new Color(0xF4F6FB);
    /** Color principal (barra superior, figura). */
    public static final Color PRIMARIO = new Color(0x2C3E70);
    /** Color de acento (resaltes). */
    public static final Color ACENTO = new Color(0xF39C12);
    /** Color del texto normal. */
    public static final Color TEXTO = new Color(0x22263A);
    /** Color para aciertos y victoria. */
    public static final Color ACIERTO = new Color(0x1E8449);
    /** Color para errores y derrota. */
    public static final Color ERROR = new Color(0xC0392B);
    /** Fondo de las tarjetas (paneles). */
    public static final Color TARJETA = Color.WHITE;
    /** Color de los bordes. */
    public static final Color BORDE = new Color(0xC9D1E6);
    /** Color de la madera de la horca. */
    public static final Color MADERA = new Color(0x7A5230);
    /** Color de la cuerda. */
    public static final Color CUERDA = new Color(0xB8860B);

    /** Fuente del título. */
    public static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    /** Fuente de subtítulos y encabezados. */
    public static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Fuente de texto normal. */
    public static final Font NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    /** Fuente de botones. */
    public static final Font BOTON = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    /** Fuente de las teclas del teclado en pantalla. */
    public static final Font TECLA = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    /** Fuente de la palabra oculta (monoespaciada). */
    public static final Font PALABRA = new Font(Font.MONOSPACED, Font.BOLD, 32);

    /** Clase de utilidades: no se instancia. */
    private Estilo() {
    }

    /** Activa el aspecto Nimbus si existe; si no, deja el predeterminado. */
    public static void configurarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            // Si falla, se usa el aspecto predeterminado de Swing.
        }
    }

    /**
     * Borde vacío uniforme para dar espaciado.
     *
     * @param px píxeles de margen
     * @return borde vacío
     */
    public static Border margen(int px) {
        return BorderFactory.createEmptyBorder(px, px, px, px);
    }

    /**
     * Borde de tarjeta: línea fina con título y margen interior.
     *
     * @param titulo título del recuadro
     * @return borde compuesto
     */
    public static Border tarjeta(String titulo) {
        Border linea = BorderFactory.createLineBorder(BORDE);
        Border conTitulo = BorderFactory.createTitledBorder(linea, titulo,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, SUBTITULO, PRIMARIO);
        return BorderFactory.createCompoundBorder(conTitulo, margen(6));
    }

    /**
     * Aplica la fuente de botón y quita el borde de foco para un aspecto uniforme.
     *
     * @param boton botón a estilizar
     */
    public static void estilizarBoton(JButton boton) {
        boton.setFont(BOTON);
        boton.setFocusPainted(true);
    }
}
