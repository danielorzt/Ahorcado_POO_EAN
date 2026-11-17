package vista;

import controlador.ControladorJuego;
import controlador.VistaJuego;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import modelo.Dificultad;
import modelo.EstadoJuego;
import modelo.Partida;
import util.Normalizador;

/**
 * Ventana principal del juego. Implementa {@link VistaJuego}: el controlador le habla
 * a través de la interfaz y no conoce los detalles de Swing.
 */
public class VentanaPrincipal extends JFrame implements VistaJuego {

    private static final long serialVersionUID = 1L;

    private final ControladorJuego controlador;
    private final PanelAhorcado panelAhorcado = new PanelAhorcado();
    private final PanelEstado panelEstado = new PanelEstado();
    private final PanelPistas panelPistas = new PanelPistas();
    private final PanelTeclado panelTeclado = new PanelTeclado();
    private final JLabel etiquetaMensaje = new JLabel(" ");
    private final JComboBox<String> comboDificultad =
            new JComboBox<>(new String[] {"ALEATORIA", "FÁCIL", "MEDIO", "DIFÍCIL"});
    private final JButton botonNueva = new JButton("Nueva partida");
    private final JButton botonAyuda = new JButton("Cómo jugar");

    /**
     * Construye la ventana y la conecta con el controlador.
     *
     * @param controlador controlador del juego
     */
    public VentanaPrincipal(ControladorJuego controlador) {
        super("Juego del Ahorcado - Universidad EAN");
        this.controlador = controlador;
        controlador.setVista(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Estilo.FONDO);
        setLayout(new BorderLayout(0, 0));

        setJMenuBar(crearMenu());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearCentro(), BorderLayout.CENTER);
        add(crearBarraMensaje(), BorderLayout.SOUTH);

        conectarEventos();
        configurarAccesibilidad();
        configurarTabulacion();
        instalarTecladoFisico();

        setSize(900, 680);
        setMinimumSize(new Dimension(780, 600));
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------ construcción

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuJuego = new JMenu("Juego");
        menuJuego.setMnemonic(KeyEvent.VK_J);
        JMenuItem itemNueva = new JMenuItem("Nueva partida", KeyEvent.VK_N);
        itemNueva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemNueva.addActionListener(e -> nuevaPartida());
        JMenuItem itemSalir = new JMenuItem("Salir", KeyEvent.VK_S);
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        itemSalir.addActionListener(e -> controlador.salir());
        menuJuego.add(itemNueva);
        menuJuego.addSeparator();
        menuJuego.add(itemSalir);

        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic(KeyEvent.VK_A);
        JMenuItem itemComoJugar = new JMenuItem("Cómo jugar", KeyEvent.VK_C);
        itemComoJugar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        itemComoJugar.addActionListener(e -> mostrarAyuda());
        menuAyuda.add(itemComoJugar);

        barra.add(menuJuego);
        barra.add(menuAyuda);

        menuJuego.getAccessibleContext().setAccessibleName("Menú Juego");
        menuJuego.getAccessibleContext().setAccessibleDescription("Opciones para iniciar una partida nueva o salir.");
        menuAyuda.getAccessibleContext().setAccessibleName("Menú Ayuda");
        menuAyuda.getAccessibleContext().setAccessibleDescription("Explica cómo se juega.");
        itemNueva.getAccessibleContext().setAccessibleName("Nueva partida");
        itemNueva.getAccessibleContext().setAccessibleDescription("Inicia una partida nueva. Atajo Control N.");
        itemSalir.getAccessibleContext().setAccessibleName("Salir");
        itemSalir.getAccessibleContext().setAccessibleDescription("Cierra el juego. Atajo Control Q.");
        itemComoJugar.getAccessibleContext().setAccessibleName("Cómo jugar");
        itemComoJugar.getAccessibleContext().setAccessibleDescription("Muestra las reglas del juego. Atajo F1.");
        return barra;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.PRIMARIO);
        barra.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel titulo = new JLabel("AHORCADO");
        titulo.setFont(Estilo.TITULO);
        titulo.setForeground(java.awt.Color.WHITE);
        titulo.getAccessibleContext().setAccessibleName("Título del juego");

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setOpaque(false);
        JLabel etiquetaDificultad = new JLabel("Dificultad:");
        etiquetaDificultad.setFont(Estilo.BOTON);
        etiquetaDificultad.setForeground(java.awt.Color.WHITE);
        etiquetaDificultad.setDisplayedMnemonic(KeyEvent.VK_D);
        etiquetaDificultad.setLabelFor(comboDificultad);
        comboDificultad.setFont(Estilo.NORMAL);
        // Las letras del teclado son solo para jugar: se desactiva la selección por inicial del JComboBox
        // para que escribir una letra con el foco en el selector no cambie la dificultad (hallazgo H-6).
        comboDificultad.setKeySelectionManager((tecla, modelo) -> -1);
        Estilo.estilizarBoton(botonNueva);
        botonNueva.setMnemonic(KeyEvent.VK_N);
        Estilo.estilizarBoton(botonAyuda);
        botonAyuda.setMnemonic(KeyEvent.VK_M);

        controles.add(etiquetaDificultad);
        controles.add(comboDificultad);
        controles.add(botonNueva);
        controles.add(botonAyuda);

        barra.add(titulo, BorderLayout.WEST);
        barra.add(controles, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearCentro() {
        JPanel derecha = new JPanel(new BorderLayout(0, 10));
        derecha.setOpaque(false);
        derecha.add(panelEstado, BorderLayout.NORTH);
        derecha.add(panelPistas, BorderLayout.CENTER);
        derecha.add(panelTeclado, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(Estilo.FONDO);
        centro.setBorder(Estilo.margen(12));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 0.4;
        c.insets = new java.awt.Insets(0, 0, 0, 6);
        centro.add(panelAhorcado, c);
        c.gridx = 1;
        c.weightx = 0.6;
        c.insets = new java.awt.Insets(0, 6, 0, 0);
        centro.add(derecha, c);
        return centro;
    }

    private JPanel crearBarraMensaje() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.TARJETA);
        barra.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE));
        etiquetaMensaje.setFont(Estilo.NORMAL);
        etiquetaMensaje.setForeground(Estilo.TEXTO);
        etiquetaMensaje.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 16, 8, 16));
        etiquetaMensaje.getAccessibleContext().setAccessibleName("Mensaje del juego");
        barra.add(etiquetaMensaje, BorderLayout.CENTER);
        return barra;
    }

    private void conectarEventos() {
        // Nombres estables para localizar los componentes en las pruebas de interfaz (AssertJ Swing).
        comboDificultad.setName("cmbDificultad");
        botonNueva.setName("btnNuevaPartida");
        botonAyuda.setName("btnComoJugar");
        etiquetaMensaje.setName("lblMensaje");
        botonNueva.addActionListener(e -> nuevaPartida());
        botonAyuda.addActionListener(e -> mostrarAyuda());
        panelTeclado.setEscuchaLetra(letra -> controlador.procesarLetra(letra));
        panelPistas.setEscuchaPista(indice -> controlador.usarPista(indice));
    }

    private void configurarAccesibilidad() {
        comboDificultad.setToolTipText("Elige la dificultad de la próxima palabra");
        comboDificultad.getAccessibleContext().setAccessibleName("Dificultad");
        comboDificultad.getAccessibleContext().setAccessibleDescription(
                "Selector de dificultad: aleatoria, fácil (3 a 5 letras), medio (6 a 8) o difícil (9 o más).");
        botonNueva.setToolTipText("Inicia una partida nueva (Control N)");
        botonNueva.getAccessibleContext().setAccessibleName("Nueva partida");
        botonNueva.getAccessibleContext().setAccessibleDescription("Inicia una partida nueva con la dificultad elegida.");
        botonAyuda.setToolTipText("Muestra las reglas del juego (F1)");
        botonAyuda.getAccessibleContext().setAccessibleName("Cómo jugar");
        botonAyuda.getAccessibleContext().setAccessibleDescription("Abre una ventana con las reglas del juego.");
        getAccessibleContext().setAccessibleName("Juego del Ahorcado");
        getAccessibleContext().setAccessibleDescription("Ventana principal del juego del ahorcado.");
    }

    /** Orden de tabulación lógico: dificultad, nueva partida, ayuda, pistas y teclado. */
    private void configurarTabulacion() {
        ArrayList<Component> orden = new ArrayList<>();
        orden.add(comboDificultad);
        orden.add(botonNueva);
        orden.add(botonAyuda);
        orden.addAll(panelPistas.getBotones());
        orden.addAll(panelTeclado.getBotones());
        setFocusTraversalPolicy(new OrdenTabulacion(orden));
    }

    /**
     * Captura el teclado físico (A–Z y Ñ, normalizando tildes) sin importar qué componente tenga el foco.
     * Se ignora cuando hay un diálogo modal abierto (la ventana activa no es esta).
     */
    private void instalarTecladoFisico() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_TYPED) {
                return false;
            }
            if (e.isControlDown() || e.isMetaDown() || e.isAltDown()) {
                return false; // atajos de menú y mnemónicos
            }
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() != this) {
                return false; // hay un diálogo modal abierto u otra ventana activa
            }
            char letra = Normalizador.normalizarLetra(e.getKeyChar());
            if (letra == '\0') {
                return false; // no es letra: se deja pasar (Tab, espacio, Enter...)
            }
            Partida partida = controlador.getPartida();
            if (partida != null && partida.getEstado() == EstadoJuego.EN_CURSO) {
                controlador.procesarLetra(letra);
            }
            return true; // se consume para que no cambie, por ejemplo, el selector de dificultad
        });
    }

    // ------------------------------------------------------------------ acciones

    /** Inicia una partida nueva con la dificultad elegida en el selector. */
    private void nuevaPartida() {
        controlador.iniciarNuevaPartida(dificultadSeleccionada());
    }

    /** @return la dificultad elegida, o {@code null} si es ALEATORIA */
    private Dificultad dificultadSeleccionada() {
        int indice = comboDificultad.getSelectedIndex();
        return indice <= 0 ? null : Dificultad.values()[indice - 1];
    }

    private void mostrarAyuda() {
        String texto = "Adivina la palabra secreta letra por letra.\n\n"
                + "- Puedes fallar hasta 7 veces. Cada error dibuja una parte del ahorcado:\n"
                + "  cabeza, torso, brazo derecho, brazo izquierdo, pierna derecha,\n"
                + "  pierna izquierda y, al final, la cuerda (la partida se pierde).\n"
                + "- Usa el teclado en pantalla o el teclado de tu computador (A-Z y Ñ).\n"
                + "- Repetir una letra no te penaliza. Las tildes se ignoran.\n"
                + "- Tienes 3 pistas, una vez cada una: categoría, una letra y una pista escrita.\n"
                + "- Elige la dificultad: fácil (3 a 5 letras), medio (6 a 8) o difícil (9 o más).\n"
                + "- Atajos: Control+N nueva partida, Control+Q salir, F1 esta ayuda.";
        JOptionPane.showMessageDialog(this, texto, "Cómo jugar", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------ VistaJuego

    /** {@inheritDoc} */
    @Override
    public void actualizar(Partida partida) {
        panelAhorcado.setErrores(partida.getErrores());
        panelEstado.actualizar(partida);

        ArrayList<Character> usadas = partida.getLetrasCorrectas();
        usadas.addAll(partida.getLetrasIncorrectas());
        panelTeclado.setEnabledLetras(usadas, partida.getEstado() == EstadoJuego.EN_CURSO);

        for (int i = 0; i < 3; i++) {
            panelPistas.setPistaDisponible(i, partida.pistaDisponible(i));
        }
        panelPistas.setPistasRestantes(partida.pistasRestantes());
        panelPistas.setTextoPistas(partida.getPistasObtenidas());
    }

    /** {@inheritDoc} */
    @Override
    public void mostrarFin(Partida partida) {
        crearDialogoFin(partida).setVisible(true);
    }

    /** {@inheritDoc} */
    @Override
    public void mostrarMensaje(String mensaje) {
        etiquetaMensaje.setText(mensaje);
        etiquetaMensaje.getAccessibleContext().setAccessibleDescription(mensaje);
    }

    /**
     * Construye (sin mostrar) el diálogo de fin de partida con el resultado y la palabra revelada
     * CON tildes. Es público para que {@code util.GeneradorCapturas} pueda fotografiarlo.
     *
     * @param partida partida terminada
     * @return diálogo modal listo para hacer {@code setVisible(true)}
     */
    public JDialog crearDialogoFin(Partida partida) {
        boolean gano = partida.getEstado() == EstadoJuego.GANADA;
        JDialog dialogo = new JDialog(this, gano ? "¡Victoria!" : "Fin de la partida", true);

        JLabel titulo = new JLabel(gano ? "¡Ganaste!" : "¡Perdiste!", SwingConstants.CENTER);
        titulo.setFont(Estilo.TITULO);
        titulo.setForeground(gano ? Estilo.ACIERTO : Estilo.ERROR);
        titulo.setName("lblMensajeFin");
        titulo.getAccessibleContext().setAccessibleName("Resultado de la partida");

        JLabel etiqueta = new JLabel("La palabra era:", SwingConstants.CENTER);
        etiqueta.setFont(Estilo.NORMAL);
        JLabel palabra = new JLabel(partida.getPalabra().getTextoOriginal(), SwingConstants.CENTER);
        palabra.setFont(Estilo.PALABRA.deriveFont(28f));
        palabra.setForeground(Estilo.PRIMARIO);
        palabra.setName("lblPalabraFin");
        palabra.getAccessibleContext().setAccessibleName("Palabra revelada");
        JLabel detalle = new JLabel("Errores: " + partida.getErrores() + " de " + Partida.MAX_ERRORES
                + "  |  Categoría: " + partida.getPalabra().getCategoria(), SwingConstants.CENTER);
        detalle.setFont(Estilo.NORMAL);

        JPanel centro = new JPanel(new java.awt.GridLayout(4, 1, 0, 4));
        centro.setOpaque(false);
        centro.add(titulo);
        centro.add(etiqueta);
        centro.add(palabra);
        centro.add(detalle);

        JButton botonOtra = new JButton("Jugar de nuevo");
        Estilo.estilizarBoton(botonOtra);
        botonOtra.setName("btnJugarDeNuevo");
        botonOtra.setMnemonic(KeyEvent.VK_J);
        botonOtra.getAccessibleContext().setAccessibleName("Jugar de nuevo");
        botonOtra.getAccessibleContext().setAccessibleDescription("Cierra este mensaje e inicia una partida nueva.");
        botonOtra.addActionListener(e -> {
            dialogo.dispose();
            nuevaPartida();
        });
        JButton botonSalir = new JButton("Salir");
        Estilo.estilizarBoton(botonSalir);
        botonSalir.setName("btnSalir");
        botonSalir.setMnemonic(KeyEvent.VK_S);
        botonSalir.getAccessibleContext().setAccessibleName("Salir");
        botonSalir.getAccessibleContext().setAccessibleDescription("Cierra el juego.");
        botonSalir.addActionListener(e -> controlador.salir());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(botonOtra);
        botones.add(botonSalir);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(Estilo.TARJETA);
        contenido.setBorder(Estilo.margen(22));
        contenido.setPreferredSize(new Dimension(440, 250));
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        dialogo.setName("dlgFinPartida");
        dialogo.setContentPane(contenido);
        dialogo.getRootPane().setDefaultButton(botonOtra);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogo.getAccessibleContext().setAccessibleName("Fin de la partida");
        dialogo.getAccessibleContext().setAccessibleDescription(
                (gano ? "Ganaste. " : "Perdiste. ") + "La palabra era " + partida.getPalabra().getTextoOriginal() + ".");
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setResizable(false);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        return dialogo;
    }

    // ------------------------------------------------------------------ tabulación

    /** Política de foco con un orden explícito; salta los componentes deshabilitados. */
    private static class OrdenTabulacion extends FocusTraversalPolicy {

        private final ArrayList<Component> orden;

        OrdenTabulacion(ArrayList<Component> orden) {
            this.orden = orden;
        }

        private Component buscar(Component actual, int paso) {
            int n = orden.size();
            int posicion = orden.indexOf(actual);
            if (posicion < 0) {
                posicion = paso > 0 ? -1 : 0; // componente fuera de la lista: se empieza por un extremo
            }
            for (int k = 1; k <= n; k++) {
                Component candidato = orden.get(((posicion + paso * k) % n + n) % n);
                if (candidato.isEnabled() && candidato.isFocusable()) {
                    return candidato;
                }
            }
            return null;
        }

        @Override
        public Component getComponentAfter(Container raiz, Component actual) {
            return buscar(actual, 1);
        }

        @Override
        public Component getComponentBefore(Container raiz, Component actual) {
            return buscar(actual, -1);
        }

        @Override
        public Component getFirstComponent(Container raiz) {
            return orden.get(0);
        }

        @Override
        public Component getLastComponent(Container raiz) {
            return orden.get(orden.size() - 1);
        }

        @Override
        public Component getDefaultComponent(Container raiz) {
            return buscar(orden.get(orden.size() - 1), 1);
        }
    }
}
