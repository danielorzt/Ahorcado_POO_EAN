// Pruebas de interfaz OPCIONALES (JUnit 4 + AssertJ Swing) del juego del Ahorcado.
//
// Paquete: ninguno (paquete por defecto), igual que Main.java. Asi se compila y se ejecuta
// con un solo comando y no hace falta tocar el codigo del juego. Las clases del juego
// (modelo, controlador, vista) se importan normalmente.
//
// ATENCION: AssertJ Swing maneja el RATON y el TECLADO REALES (java.awt.Robot). Mientras
// corre la suite (1 a 3 minutos) no uses el computador. En macOS hace falta dar permiso de
// "Accesibilidad" a la terminal o al IDE (Ajustes del Sistema > Privacidad y seguridad).
// Consulta LEEME.md. "Salir" (boton, menu y Ctrl+Q) llama a System.exit: esas pruebas (pm22, pm23a,
// pm23b) instalan un SecurityManager solo mientras dura la accion, que anota el codigo de salida y
// lanza una excepcion para que la JVM NO termine (hace falta -Djava.security.manager=allow).

import static org.assertj.core.api.Assertions.assertThat;

import controlador.ControladorJuego;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.security.Permission;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import modelo.BancoPalabras;
import modelo.Dificultad;
import modelo.EstadoJuego;
import modelo.Palabra;
import modelo.Partida;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Pause;
import org.assertj.swing.timing.Timeout;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.MethodSorters;
import vista.Estilo;
import vista.PanelAhorcado;
import vista.PanelTeclado;
import vista.VentanaPrincipal;

/**
 * Pruebas de la ventana real del juego. Cada metodo se llama como el caso manual del
 * Plan de Pruebas que reemplaza (pmNN = PM-NN).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AhorcadoGUITest extends AssertJSwingJUnitTestCase {

    /** Plazo maximo por prueba: si Robot o los permisos de macOS la bloquean, falla en vez de colgarse. */
    private static final long PLAZO = 90_000;

    private static final List<String> PARTES_EN_ORDEN = Arrays.asList(
            "Cabeza", "Torso", "Brazo derecho", "Brazo izquierdo",
            "Pierna derecha", "Pierna izquierda", "Cuerda");

    /** Letras que NO estan en GATO, MURCIELAGO ni PIÑA (para provocar errores). */
    private static final char[] LETRAS_MALAS = {'X', 'Z', 'Q', 'W', 'K', 'J', 'V'};

    private BancoPalabras banco;
    private ControladorJuego controlador;
    private VentanaPrincipal ventana;
    private FrameFixture window;

    // ------------------------------------------------------------------ ciclo de vida

    @BeforeClass
    public static void instalarVigilanteDeHilos() {
        FailOnThreadViolationRepaintManager.install();
        GuiActionRunner.execute(() -> {
            Estilo.configurarLookAndFeel(); // el mismo aspecto (Nimbus) que usa Main
        });
    }

    @Override
    protected void onSetUp() {
        banco = GuiActionRunner.execute(() -> new BancoPalabras());
        controlador = GuiActionRunner.execute(() -> new ControladorJuego(banco));
        ventana = GuiActionRunner.execute(() -> new VentanaPrincipal(controlador));
        window = new FrameFixture(robot(), ventana);
        // No se usa window.show(): FrameFixture.show() hace pack() y cambiaría el tamaño real (900 x 680) de la ventana.
        GuiActionRunner.execute(() -> {
            ventana.setVisible(true);
        });
        robot().waitForIdle();
        iniciar("GATO", "Maúlla y caza ratones", "Animales");
        activarVentana();
    }

    // ------------------------------------------------------------------ PM-01

    /** PM-01: pantalla inicial (sin cuerpo, guiones, listas vacias, 7 intentos, todo habilitado). */
    @Test(timeout = PLAZO)
    public void pm01_pantallaInicial() {
        assertThat(errores()).isZero();
        window.label("lblPalabraOculta").requireText("_ _ _ _");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
        window.label("lblPistasRestantes").requireText("Pistas restantes: 3");
        window.label("lblMensaje").requireText("Nueva partida: la palabra tiene 4 letras.");
        assertThat(teclasHabilitadas()).isEqualTo(27);
        window.button("btnPistaCategoria").requireEnabled();
        window.button("btnPistaLetra").requireEnabled();
        window.button("btnPistaTexto").requireEnabled();
        window.textBox("txtPistas").requireText("");
        // Los 27 botones existen con su nombre estable (btn_A ... btn_Z y btn_Ñ).
        for (char c : PanelTeclado.LETRAS.toCharArray()) {
            window.button("btn_" + c).requireVisible();
        }
    }

    // ------------------------------------------------------------------ PM-02

    /** PM-02: victoria acertando todas las letras con el teclado en pantalla. */
    @Test(timeout = PLAZO)
    public void pm02_victoriaPorClicsEnLetras() {
        clic('G');
        window.label("lblPalabraOculta").requireText("G _ _ _");
        clic('A');
        window.label("lblPalabraOculta").requireText("G A _ _");
        clic('T');
        window.label("lblPalabraOculta").requireText("G A T _");
        assertThat(hayDialogoAbierto()).as("no hay diálogo antes de la última letra").isFalse();
        clic('O'); // la última letra abre el diálogo modal de fin
        DialogFixture dialogo = dialogoFin();
        dialogo.label("lblMensajeFin").requireText("¡Ganaste!");
        dialogo.label("lblPalabraFin").requireText("GATO");
        assertThat(estado()).isEqualTo(EstadoJuego.GANADA);
        assertThat(errores()).isZero();
        window.label("lblLetrasCorrectas").requireText("Letras correctas: G, A, T, O");
        cerrarDialogoFin(dialogo);
    }

    // ------------------------------------------------------------------ PM-03

    /** PM-03: derrota exactamente en el 7.º error y orden exacto de las partes tras cada error. */
    @Test(timeout = PLAZO)
    public void pm03_derrotaEnElSeptimoErrorYOrdenDePartes() {
        assertThat(nombresPartes()).containsExactlyElementsOf(PARTES_EN_ORDEN);
        for (int i = 0; i < 6; i++) {
            clic(LETRAS_MALAS[i]);
            assertThat(errores()).as("errores tras la letra %s", LETRAS_MALAS[i]).isEqualTo(i + 1);
            // la parte recién dibujada (la última visible) es la esperada por la especificación
            assertThat(nombresPartes().get(errores() - 1)).isEqualTo(PARTES_EN_ORDEN.get(i));
            assertThat(estado()).as("sigue en curso tras el error %d", i + 1).isEqualTo(EstadoJuego.EN_CURSO);
            assertThat(hayDialogoAbierto()).as("sin diálogo antes del 7.º error").isFalse();
            window.label("lblIntentos").requireText("Intentos restantes: " + (6 - i) + " de 7");
        }
        clic(LETRAS_MALAS[6]); // 7.º error: aparece la cuerda y se pierde
        DialogFixture dialogo = dialogoFin();
        assertThat(errores()).isEqualTo(7);
        assertThat(nombresPartes().get(errores() - 1)).isEqualTo("Cuerda");
        assertThat(estado()).isEqualTo(EstadoJuego.PERDIDA);
        dialogo.label("lblMensajeFin").requireText("¡Perdiste!");
        dialogo.label("lblPalabraFin").requireText("GATO");
        dialogo.button("btnJugarDeNuevo").requireEnabled();
        dialogo.button("btnSalir").requireEnabled(); // aquí solo se comprueba; pm22 lo pulsa con la salida interceptada
        window.label("lblIntentos").requireText("Intentos restantes: 0 de 7");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: X, Z, Q, W, K, J, V");
        cerrarDialogoFin(dialogo);
    }

    /** PM-03/PM-21: el diálogo de derrota muestra la palabra real CON tildes. */
    @Test(timeout = PLAZO)
    public void pm03b_derrotaMuestraLaPalabraRealConTildes() {
        iniciar("MURCIÉLAGO", "Mamífero volador nocturno", "Animales");
        for (char mala : LETRAS_MALAS) {
            clic(mala);
        }
        DialogFixture dialogo = dialogoFin();
        dialogo.label("lblMensajeFin").requireText("¡Perdiste!");
        dialogo.label("lblPalabraFin").requireText("MURCIÉLAGO");
        cerrarDialogoFin(dialogo);
    }

    // ------------------------------------------------------------------ PM-04

    /** PM-04: las letras correctas aparecen en su posición en la palabra oculta y en la lista. */
    @Test(timeout = PLAZO)
    public void pm04a_letrasCorrectasAparecenEnSuPosicion() {
        iniciar("BANANA", "Fruta amarilla", "Frutas y verduras");
        window.label("lblPalabraOculta").requireText("_ _ _ _ _ _");
        clic('A');
        window.label("lblPalabraOculta").requireText("_ A _ A _ A"); // todas las apariciones
        window.label("lblLetrasCorrectas").requireText("Letras correctas: A");
        clic('N');
        window.label("lblPalabraOculta").requireText("_ A N A N A");
        assertThat(errores()).isZero();
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
    }

    /** PM-04: una letra incorrecta suma un error, baja los intentos y va a la lista de incorrectas. */
    @Test(timeout = PLAZO)
    public void pm04b_letraIncorrectaAumentaLosErrores() {
        clic('G'); // acierto
        clic('X'); // error 1
        clic('Z'); // error 2
        assertThat(errores()).isEqualTo(2);
        window.label("lblIntentos").requireText("Intentos restantes: 5 de 7");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: G");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: X, Z");
        window.button("btn_X").requireDisabled();
        window.button("btn_Z").requireDisabled();
        window.button("btn_G").requireDisabled();
    }

    // ------------------------------------------------------------------ PM-05

    /** PM-05: repetir una letra con el ratón no penaliza (la tecla queda deshabilitada). */
    @Test(timeout = PLAZO)
    public void pm05a_letraRepetidaPorClicNoPenaliza() {
        clic('X');
        assertThat(errores()).isEqualTo(1);
        window.button("btn_X").requireDisabled();
        intentarClicEnDeshabilitado("btn_X");
        assertThat(errores()).as("repetir X por clic no suma error").isEqualTo(1);
        clic('G');
        window.button("btn_G").requireDisabled();
        intentarClicEnDeshabilitado("btn_G");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: G");
        window.label("lblIntentos").requireText("Intentos restantes: 6 de 7");
    }

    /** PM-05: repetir una letra con el teclado físico no penaliza y avisa en el mensaje. */
    @Test(timeout = PLAZO)
    public void pm05b_letraRepetidaPorTeclaFisicaNoPenaliza() {
        enfocarAreaDePistas();
        clic('X');
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_X);
        assertThat(errores()).as("repetir X con el teclado físico no suma error").isEqualTo(1);
        window.label("lblMensaje").requireText("Ya habías probado la letra X. No se penaliza.");
        window.label("lblIntentos").requireText("Intentos restantes: 6 de 7");
        clic('G');
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_G);
        window.label("lblLetrasCorrectas").requireText("Letras correctas: G");
        assertThat(errores()).isEqualTo(1);
    }

    // ------------------------------------------------------------------ PM-06

    /** PM-06: las teclas físicas que no son letras (dígitos, símbolos, espacio) se ignoran. */
    @Test(timeout = PLAZO)
    public void pm06_teclasFisicasQueNoSonLetrasSeIgnoran() {
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_7);
        tecla(KeyEvent.VK_5);
        tecla(KeyEvent.VK_MINUS);
        tecla(KeyEvent.VK_SPACE);
        tecla(KeyEvent.VK_COMMA);
        teclaConMayuscula(KeyEvent.VK_1); // '!'
        robot().waitForIdle();
        assertThat(errores()).isZero();
        assertThat(estado()).isEqualTo(EstadoJuego.EN_CURSO);
        window.label("lblPalabraOculta").requireText("_ _ _ _");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
        assertThat(teclasHabilitadas()).isEqualTo(27);
        window.label("lblMensaje").requireText("Nueva partida: la palabra tiene 4 letras.");
    }

    // ------------------------------------------------------------------ PM-07

    /** PM-07: tras perder, teclado en pantalla y pistas deshabilitados y el teclado físico se ignora. */
    @Test(timeout = PLAZO)
    public void pm07a_trasPerderTodoQuedaDeshabilitadoYLasTeclasSeIgnoran() {
        for (char mala : LETRAS_MALAS) {
            clic(mala);
        }
        DialogFixture dialogo = dialogoFin();
        // con el diálogo abierto ya se puede inspeccionar la ventana principal
        verificarJuegoTerminadoDeshabilitado();
        cerrarDialogoFin(dialogo);
        verificarJuegoTerminadoDeshabilitado();
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_A);
        tecla(KeyEvent.VK_G);
        tecla(KeyEvent.VK_B);
        robot().waitForIdle();
        assertThat(errores()).isEqualTo(7);
        assertThat(estado()).isEqualTo(EstadoJuego.PERDIDA);
        window.label("lblPalabraOculta").requireText("_ _ _ _");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: X, Z, Q, W, K, J, V");
        assertThat(hayDialogoAbierto()).as("las teclas tras el fin no reabren el diálogo").isFalse();
    }

    /** PM-07: igual tras ganar. */
    @Test(timeout = PLAZO)
    public void pm07b_trasGanarTodoQuedaDeshabilitadoYLasTeclasSeIgnoran() {
        for (char c : "GATO".toCharArray()) {
            clic(c);
        }
        DialogFixture dialogo = dialogoFin();
        verificarJuegoTerminadoDeshabilitado();
        cerrarDialogoFin(dialogo);
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_X);
        tecla(KeyEvent.VK_Z);
        robot().waitForIdle();
        assertThat(errores()).isZero();
        assertThat(estado()).isEqualTo(EstadoJuego.GANADA);
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        assertThat(hayDialogoAbierto()).isFalse();
    }

    // ------------------------------------------------------------------ PM-08 a PM-13: pistas

    /** PM-08 y PM-11: la pista de categoría muestra el texto, deshabilita su botón y no se repite. */
    @Test(timeout = PLAZO)
    public void pm08_pistaDeCategoriaMuestraTextoYDeshabilitaElBoton() {
        window.button("btnPistaCategoria").click();
        assertThat(window.textBox("txtPistas").text()).contains("Categoría: Animales");
        window.button("btnPistaCategoria").requireDisabled();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 2");
        window.button("btnPistaLetra").requireEnabled();
        window.button("btnPistaTexto").requireEnabled();
        // PM-11: segundo uso imposible
        String antes = window.textBox("txtPistas").text();
        intentarClicEnDeshabilitado("btnPistaCategoria");
        assertThat(window.textBox("txtPistas").text()).isEqualTo(antes);
        window.label("lblPistasRestantes").requireText("Pistas restantes: 2");
    }

    /** PM-09: la pista de letra revela una letra no revelada, sin error, y deshabilita su botón. */
    @Test(timeout = PLAZO)
    public void pm09_pistaDeLetraRevelaUnaLetraSinSumarError() {
        clic('G');
        window.button("btnPistaLetra").click();
        String pistas = window.textBox("txtPistas").text();
        Matcher m = Pattern.compile("Se reveló la letra: (\\p{L})").matcher(pistas);
        assertThat(m.find()).as("el área de pistas dice qué letra se reveló: '%s'", pistas).isTrue();
        char revelada = m.group(1).charAt(0);
        assertThat("ATO").as("la letra revelada es una de las no reveladas").contains(String.valueOf(revelada));
        assertThat(errores()).isZero();
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        window.button("btnPistaLetra").requireDisabled();
        window.button("btn_" + revelada).requireDisabled();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 2");
        String oculta = window.label("lblPalabraOculta").text();
        assertThat(oculta.replace(" ", "").replace("_", "")).as("G + la letra revelada").hasSize(2);
        assertThat(oculta).contains(String.valueOf(revelada));
        window.label("lblMensaje").requireText("Pista: Se reveló la letra: " + revelada);
        // PM-11: no se puede reutilizar
        intentarClicEnDeshabilitado("btnPistaLetra");
        window.label("lblPistasRestantes").requireText("Pistas restantes: 2");
    }

    /** PM-10: la pista escrita muestra el texto de la palabra y deshabilita su botón. */
    @Test(timeout = PLAZO)
    public void pm10_pistaEscritaMuestraElTextoDeLaPalabra() {
        window.button("btnPistaTexto").click();
        window.textBox("txtPistas").requireText("Maúlla y caza ratones");
        window.button("btnPistaTexto").requireDisabled();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 2");
        assertThat(errores()).isZero();
        intentarClicEnDeshabilitado("btnPistaTexto");
        window.textBox("txtPistas").requireText("Maúlla y caza ratones");
    }

    /** PM-11: ninguna pista se puede usar dos veces (las tres). */
    @Test(timeout = PLAZO)
    public void pm11_ningunaPistaSePuedeReutilizar() {
        for (String boton : new String[] {"btnPistaCategoria", "btnPistaLetra", "btnPistaTexto"}) {
            window.button(boton).click();
            window.button(boton).requireDisabled();
        }
        String texto = window.textBox("txtPistas").text();
        for (String boton : new String[] {"btnPistaCategoria", "btnPistaLetra", "btnPistaTexto"}) {
            intentarClicEnDeshabilitado(boton);
        }
        assertThat(window.textBox("txtPistas").text()).isEqualTo(texto);
        window.label("lblPistasRestantes").requireText("Pistas restantes: 0");
    }

    /** PM-12: la pista de letra que revela la última letra pendiente gana la partida. */
    @Test(timeout = PLAZO)
    public void pm12_pistaDeLetraQueCompletaLaPalabraGana() {
        iniciar("OSO", "Mamífero de invierno", "Animales");
        clic('O');
        window.label("lblPalabraOculta").requireText("O _ O");
        window.button("btnPistaLetra").click(); // la única letra pendiente es la S
        DialogFixture dialogo = dialogoFin();
        dialogo.label("lblMensajeFin").requireText("¡Ganaste!");
        dialogo.label("lblPalabraFin").requireText("OSO");
        assertThat(estado()).isEqualTo(EstadoJuego.GANADA);
        assertThat(errores()).isZero();
        window.label("lblPalabraOculta").requireText("O S O");
        verificarJuegoTerminadoDeshabilitado();
        cerrarDialogoFin(dialogo);
    }

    /** PM-13: con las tres pistas usadas quedan 0, los botones deshabilitados y el área acumula las 3. */
    @Test(timeout = PLAZO)
    public void pm13_tresPistasUsadasQuedanListadasYDeshabilitadas() {
        window.button("btnPistaCategoria").click();
        window.button("btnPistaLetra").click();
        window.button("btnPistaTexto").click();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 0");
        window.button("btnPistaCategoria").requireDisabled();
        window.button("btnPistaLetra").requireDisabled();
        window.button("btnPistaTexto").requireDisabled();
        String texto = window.textBox("txtPistas").text();
        assertThat(texto).contains("Categoría: Animales").contains("Se reveló la letra: ")
                .contains("Maúlla y caza ratones");
        assertThat(texto.indexOf("Categoría")).isLessThan(texto.indexOf("Se reveló"));
        assertThat(texto.indexOf("Se reveló")).isLessThan(texto.indexOf("Maúlla"));
        window.textBox("txtPistas").requireNotEditable();
    }

    // ------------------------------------------------------------------ PM-14

    /** PM-14: "Nueva partida" reinicia dibujo, errores, teclado y pistas y carga otra palabra del banco. */
    @Test(timeout = PLAZO)
    public void pm14_nuevaPartidaReiniciaTodoYCargaOtraPalabra() {
        Partida anterior = partida();
        clic('X');
        clic('Z');
        clic('A');
        window.button("btnPistaCategoria").click();
        window.button("btnPistaLetra").click();
        assertThat(errores()).isEqualTo(2);

        window.button("btnNuevaPartida").click();

        Partida nueva = partida();
        assertThat(nueva).as("es otra partida").isNotSameAs(anterior);
        assertThat(banco.contiene(nueva.getPalabra().getTexto())).as("la palabra sale del banco").isTrue();
        assertThat(errores()).isZero();
        assertThat(nombresPartes()).containsExactlyElementsOf(PARTES_EN_ORDEN);
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        String esperada = String.join(" ", java.util.Collections.nCopies(nueva.getPalabra().longitud(), "_"));
        window.label("lblPalabraOculta").requireText(esperada);
        assertThat(teclasHabilitadas()).isEqualTo(27);
        window.button("btnPistaCategoria").requireEnabled();
        window.button("btnPistaLetra").requireEnabled();
        window.button("btnPistaTexto").requireEnabled();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 3");
        window.textBox("txtPistas").requireText("");
        window.label("lblMensaje").requireText(
                "Nueva partida: la palabra tiene " + nueva.getPalabra().longitud() + " letras.");
    }

    /** PM-14 (menú y atajo): Juego > Nueva partida y Ctrl+N dejan el estado limpio tras jugar y pedir pista. */
    @Test(timeout = PLAZO)
    public void pm14b_menuNuevaPartidaYCtrlNReiniciaLaPartida() {
        // vía menú
        Partida anterior = partida();
        ensuciarPartida();
        window.menuItemWithPath("Juego", "Nueva partida").click();
        robot().waitForIdle();
        verificarPartidaLimpia(anterior);

        // vía atajo Ctrl+N
        anterior = partida();
        ensuciarPartida();
        enfocarAreaDePistas();
        teclaConControl(KeyEvent.VK_N);
        verificarPartidaLimpia(anterior);
    }

    // ------------------------------------------------------------------ PM-15

    /** PM-15: el selector ofrece exactamente ALEATORIA, FÁCIL, MEDIO y DIFÍCIL. */
    @Test(timeout = PLAZO)
    public void pm15a_selectorDeDificultadTieneLasCuatroOpciones() {
        window.comboBox("cmbDificultad").requireItemCount(4);
        assertThat(window.comboBox("cmbDificultad").contents())
                .containsExactly("ALEATORIA", "FÁCIL", "MEDIO", "DIFÍCIL");
        window.comboBox("cmbDificultad").requireSelection("ALEATORIA");
    }

    /** PM-15: cada nivel elegido produce palabras de la longitud correspondiente. */
    @Test(timeout = PLAZO)
    public void pm15b_cadaDificultadSorteaPalabrasDeSuLongitud() {
        Dificultad[] niveles = {Dificultad.FACIL, Dificultad.MEDIO, Dificultad.DIFICIL};
        for (int i = 0; i < niveles.length; i++) {
            window.comboBox("cmbDificultad").selectItem(i + 1);
            for (int repeticion = 0; repeticion < 3; repeticion++) {
                window.button("btnNuevaPartida").click();
                Palabra p = partida().getPalabra();
                assertThat(p.getDificultad()).as("palabra %s", p.getTexto()).isEqualTo(niveles[i]);
                int n = p.longitud();
                switch (niveles[i]) {
                    case FACIL:
                        assertThat(n).isBetween(3, 5);
                        break;
                    case MEDIO:
                        assertThat(n).isBetween(6, 8);
                        break;
                    default:
                        assertThat(n).isGreaterThanOrEqualTo(9);
                }
                window.label("lblMensaje").requireText("Nueva partida: la palabra tiene " + n + " letras.");
            }
        }
        window.comboBox("cmbDificultad").selectItem(0); // ALEATORIA
        window.button("btnNuevaPartida").click();
        assertThat(banco.contiene(partida().getPalabra().getTexto())).isTrue();
    }

    // ------------------------------------------------------------------ PM-16 (parte en pantalla)

    /** PM-16 (solo teclado en pantalla): existe la tecla Ñ y es distinta de la N. */
    @Test(timeout = PLAZO)
    public void pm16_teclaEnneEnPantallaEsDistintaDeLaN() {
        iniciar("PIÑA", "Fruta tropical con corona", "Frutas y verduras");
        window.label("lblPalabraOculta").requireText("_ _ _ _");
        window.button("btn_Ñ").requireEnabled();
        clic('N'); // la N NO está en PIÑA: es un error
        assertThat(errores()).isEqualTo(1);
        window.label("lblPalabraOculta").requireText("_ _ _ _");
        clic('Ñ');
        window.label("lblPalabraOculta").requireText("_ _ Ñ _");
        assertThat(errores()).isEqualTo(1);
        window.button("btn_Ñ").requireDisabled();
        // posición del teclado: Ñ queda entre N y O
        List<String> orden = new ArrayList<>();
        for (JButton b : botonesTeclado()) {
            orden.add(textoDe(b));
        }
        assertThat(orden.indexOf("Ñ")).isEqualTo(orden.indexOf("N") + 1);
        assertThat(orden.indexOf("O")).isEqualTo(orden.indexOf("Ñ") + 1);
    }

    // ------------------------------------------------------------------ PM-17

    /** PM-17: el teclado físico juega letras (acierto y error) igual que el de pantalla. */
    @Test(timeout = PLAZO)
    public void pm17a_letrasFisicasFuncionan() {
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_G);
        window.label("lblPalabraOculta").requireText("G _ _ _");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: G");
        window.button("btn_G").requireDisabled();
        tecla(KeyEvent.VK_X);
        assertThat(errores()).isEqualTo(1);
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: X");
        window.button("btn_X").requireDisabled();
        window.label("lblIntentos").requireText("Intentos restantes: 6 de 7");
        // mezcla: una letra con ratón y otra con teclado
        clic('A');
        enfocarAreaDePistas();
        tecla(KeyEvent.VK_T);
        window.label("lblPalabraOculta").requireText("G A T _");
    }

    /** PM-17: con el foco en el combo de dificultad las letras juegan y no cambian la selección. */
    @Test(timeout = PLAZO)
    public void pm17b_letraFisicaConFocoEnElComboNoCambiaLaDificultad() {
        window.comboBox("cmbDificultad").focus();
        tecla(KeyEvent.VK_M); // MEDIO empieza por M: el combo la seleccionaría si no se consumiera
        robot().waitForIdle();
        window.comboBox("cmbDificultad").requireSelection("ALEATORIA");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: M"); // M no está en GATO
        assertThat(errores()).isEqualTo(1);
    }

    // ------------------------------------------------------------------ PM-18

    /** PM-18: Tab recorre Dificultad, Nueva partida, Cómo jugar, 3 pistas y las teclas A, B... en orden. */
    @Test(timeout = PLAZO)
    public void pm18a_tabulacionHaciaAdelanteEsLogica() {
        window.comboBox("cmbDificultad").focus();
        assertThat(nombreConFoco()).isEqualTo("cmbDificultad");
        String[] esperado = {"btnNuevaPartida", "btnComoJugar", "btnPistaCategoria", "btnPistaLetra",
            "btnPistaTexto", "btn_A", "btn_B", "btn_C"};
        for (String nombre : esperado) {
            tecla(KeyEvent.VK_TAB);
            robot().waitForIdle();
            assertThat(nombreConFoco()).isEqualTo(nombre);
        }
    }

    /** PM-18: Shift+Tab recorre al revés y Tab salta los controles deshabilitados. */
    @Test(timeout = PLAZO)
    public void pm18b_shiftTabVaAtrasYSeSaltanLosDeshabilitados() {
        window.button("btn_B").focus();
        teclaConMayuscula(KeyEvent.VK_TAB);
        robot().waitForIdle();
        assertThat(nombreConFoco()).isEqualTo("btn_A");
        teclaConMayuscula(KeyEvent.VK_TAB);
        robot().waitForIdle();
        assertThat(nombreConFoco()).isEqualTo("btnPistaTexto");

        // con la pista de categoría usada, Tab desde "Cómo jugar" salta directo a "Letra"
        window.button("btnPistaCategoria").click();
        window.button("btnComoJugar").focus();
        tecla(KeyEvent.VK_TAB);
        robot().waitForIdle();
        assertThat(nombreConFoco()).isEqualTo("btnPistaLetra");
        // con la A usada (GATO la contiene), Tab desde "Pista escrita" salta a la B
        clic('A');
        window.button("btnPistaTexto").focus();
        tecla(KeyEvent.VK_TAB);
        robot().waitForIdle();
        assertThat(nombreConFoco()).isEqualTo("btn_B");
    }

    /** PM-18: la barra espaciadora activa una tecla de letra con el foco. */
    @Test(timeout = PLAZO)
    public void pm18c_espacioActivaUnaLetraEnfocada() {
        window.button("btn_G").focus();
        tecla(KeyEvent.VK_SPACE);
        robot().waitForIdle();
        window.label("lblPalabraOculta").requireText("G _ _ _");
        window.button("btn_G").requireDisabled();
        assertThat(errores()).isZero();
    }

    /** PM-18: la barra espaciadora activa un botón de pista con el foco. */
    @Test(timeout = PLAZO)
    public void pm18d_espacioActivaUnaPistaEnfocada() {
        window.button("btnPistaCategoria").focus();
        tecla(KeyEvent.VK_SPACE);
        robot().waitForIdle();
        assertThat(window.textBox("txtPistas").text()).contains("Categoría: Animales");
        window.button("btnPistaCategoria").requireDisabled();
    }

    /** PM-18: Enter también activa una tecla de letra con el foco (en Nimbus los JButton responden a Enter). */
    @Test(timeout = PLAZO)
    public void pm18e_enterActivaUnaLetraEnfocada() {
        window.button("btn_A").focus();
        tecla(KeyEvent.VK_ENTER);
        window.label("lblPalabraOculta").requireText("_ A _ _");
        window.button("btn_A").requireDisabled();
        assertThat(errores()).isZero();
    }

    /** PM-18: Enter activa un botón de pista con el foco. */
    @Test(timeout = PLAZO)
    public void pm18f_enterActivaUnaPistaEnfocada() {
        window.button("btnPistaTexto").focus();
        tecla(KeyEvent.VK_ENTER);
        window.textBox("txtPistas").requireText("Maúlla y caza ratones");
        window.button("btnPistaTexto").requireDisabled();
    }

    // ------------------------------------------------------------------ PM-19

    /** PM-19 (solo configuración): mnemónicos definidos en botones y combo; Alt+tecla sigue siendo manual. */
    @Test(timeout = PLAZO)
    public void pm19_mnemonicosConfigurados() {
        assertThat(mnemonico("btnNuevaPartida")).isEqualTo(KeyEvent.VK_N);
        assertThat(mnemonico("btnComoJugar")).isEqualTo(KeyEvent.VK_M);
        assertThat(mnemonico("btnPistaCategoria")).isEqualTo(KeyEvent.VK_C);
        assertThat(mnemonico("btnPistaLetra")).isEqualTo(KeyEvent.VK_L);
        assertThat(mnemonico("btnPistaTexto")).isEqualTo(KeyEvent.VK_E);
    }

    // ------------------------------------------------------------------ PM-20

    /** PM-20 (solo configuración): todos los controles tienen nombre y descripción accesibles. */
    @Test(timeout = PLAZO)
    public void pm20_controlesTienenNombreYDescripcionAccesibles() {
        List<Component> controles = new ArrayList<>(botonesTeclado());
        controles.add(window.button("btnNuevaPartida").target());
        controles.add(window.button("btnComoJugar").target());
        controles.add(window.button("btnPistaCategoria").target());
        controles.add(window.button("btnPistaLetra").target());
        controles.add(window.button("btnPistaTexto").target());
        controles.add(window.comboBox("cmbDificultad").target());
        controles.add(window.textBox("txtPistas").target());
        controles.add(window.label("lblPistasRestantes").target());
        controles.add(window.label("lblMensaje").target());
        controles.add(window.panel("panelTeclado").target());
        controles.add(window.panel("panelAhorcado").target());
        List<String> faltan = GuiActionRunner.execute(() -> {
            List<String> sinNombre = new ArrayList<>();
            for (Component c : controles) {
                javax.accessibility.AccessibleContext ctx = ((javax.accessibility.Accessible) c).getAccessibleContext();
                String nombre = ctx.getAccessibleName();
                if (nombre == null || nombre.trim().isEmpty()) {
                    sinNombre.add(c.getName());
                }
            }
            return sinNombre;
        });
        assertThat(faltan).as("controles sin nombre accesible").isEmpty();
    }

    // ------------------------------------------------------------------ PM-21

    /** PM-21: "Jugar de nuevo" cierra el diálogo y empieza una partida limpia (sin pulsar "Salir"). */
    @Test(timeout = PLAZO)
    public void pm21_jugarDeNuevoCierraElDialogoYReiniciaLaPartida() {
        Partida anterior = partida();
        for (char c : "GATO".toCharArray()) {
            clic(c);
        }
        DialogFixture dialogo = dialogoFin();
        dialogo.label("lblMensajeFin").requireText("¡Ganaste!");
        dialogo.button("btnJugarDeNuevo").click();
        robot().waitForIdle();
        assertThat(hayDialogoAbierto()).isFalse();
        assertThat(partida()).isNotSameAs(anterior);
        assertThat(estado()).isEqualTo(EstadoJuego.EN_CURSO);
        assertThat(errores()).isZero();
        assertThat(teclasHabilitadas()).isEqualTo(27);
        window.label("lblPistasRestantes").requireText("Pistas restantes: 3");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
    }

    // ------------------------------------------------------------------ PM-22 y PM-23: salir

    /** PM-22: "Salir" del diálogo de fin pide System.exit(0) (la salida se intercepta, la JVM sigue viva). */
    @Test(timeout = PLAZO)
    public void pm22_salirDelDialogoDeFinCierraLaAplicacion() {
        for (char mala : LETRAS_MALAS) {
            clic(mala);
        }
        DialogFixture dialogo = dialogoFin();
        dialogo.label("lblMensajeFin").requireText("¡Perdiste!");
        Integer estadoSalida = interceptarSalida(() -> dialogo.button("btnSalir").click());
        assertThat(estadoSalida).as("el botón Salir pidió System.exit").isNotNull().isEqualTo(0);
        cerrarDialogoFin(dialogo); // la excepción interrumpió el cierre normal: se cierra a mano
    }

    /** PM-23 (menú): Juego > Salir pide System.exit(0). */
    @Test(timeout = PLAZO)
    public void pm23a_menuJuegoSalirCierraLaAplicacion() {
        Integer estadoSalida = interceptarSalida(() -> window.menuItemWithPath("Juego", "Salir").click());
        assertThat(estadoSalida).as("Juego > Salir pidió System.exit").isNotNull().isEqualTo(0);
        assertThat(ventana.isShowing()).as("la ventana sigue viva: la salida fue interceptada").isTrue();
    }

    /** PM-23 (atajo): Ctrl+Q pide System.exit(0). */
    @Test(timeout = PLAZO)
    public void pm23b_ctrlQCierraLaAplicacion() {
        enfocarAreaDePistas();
        Integer estadoSalida = interceptarSalida(() -> teclaConControl(KeyEvent.VK_Q));
        assertThat(estadoSalida).as("Ctrl+Q pidió System.exit").isNotNull().isEqualTo(0);
        assertThat(ventana.isShowing()).isTrue();
    }

    // ------------------------------------------------------------------ PM-24

    /** PM-24 (solo el botón): "Cómo jugar" abre la ayuda con las reglas. */
    @Test(timeout = PLAZO)
    public void pm24_botonComoJugarMuestraLaAyuda() {
        window.button("btnComoJugar").click();
        org.assertj.swing.fixture.JOptionPaneFixture ayuda = window.optionPane(Timeout.timeout(10_000));
        ayuda.requireTitle("Cómo jugar");
        assertThat(ayuda.target().getMessage().toString()).contains("7 veces").contains("Control+N");
        ayuda.okButton().click();
        robot().waitForIdle();
        assertThat(hayDialogoAbierto()).isFalse();
    }

    /** PM-24 (menú y atajo): Ayuda > Cómo jugar y F1 muestran la ayuda; se cierra cada vez. */
    @Test(timeout = PLAZO)
    public void pm24b_menuAyudaComoJugarYF1MuestranLaAyuda() {
        window.menuItemWithPath("Ayuda", "Cómo jugar").click();
        verificarAyudaYCerrar();

        enfocarAreaDePistas();
        tecla(KeyEvent.VK_F1);
        verificarAyudaYCerrar();
    }

    // ------------------------------------------------------------------ PM-25

    /** PM-25: tamaño inicial 900 x 680 y tamaño mínimo 780 x 600 (propiedad y por reducción real). */
    @Test(timeout = PLAZO)
    public void pm25a_tamanoInicialYMinimo() {
        assertThat(tamano()).isEqualTo(new Dimension(900, 680));
        assertThat(GuiActionRunner.execute(() -> ventana.getMinimumSize())).isEqualTo(new Dimension(780, 600));
        // por programa: pedir menos que el mínimo no lo reduce
        GuiActionRunner.execute(() -> {
            ventana.setSize(400, 300);
        });
        robot().waitForIdle();
        Dimension porPrograma = tamano();
        assertThat(porPrograma.width).isGreaterThanOrEqualTo(780);
        assertThat(porPrograma.height).isGreaterThanOrEqualTo(600);
        // con arrastre real del borde con Robot
        window.resizeTo(new Dimension(900, 680));
        robot().waitForIdle();
        window.resizeTo(new Dimension(500, 400));
        robot().waitForIdle();
        Dimension porArrastre = tamano();
        System.out.println("    [info] tras arrastrar a 500x400 el tamaño real es " + porArrastre.width + "x" + porArrastre.height);
        assertThat(porArrastre.width).isGreaterThanOrEqualTo(780);
        assertThat(porArrastre.height).isGreaterThanOrEqualTo(600);
        verificarTecladoSinSolapesNiDesbordes("tamaño mínimo");
    }

    /** PM-25: a varios tamaños los botones del teclado no se solapan ni se salen de su panel. */
    @Test(timeout = PLAZO)
    public void pm25b_redimensionarNoSolapaNiDesbordaElTeclado() {
        verificarTecladoSinSolapesNiDesbordes("tamaño inicial");
        int[][] tamanos = {{780, 600}, {1000, 650}, {1100, 800}, {1400, 900}, {900, 680}};
        for (int[] t : tamanos) {
            redimensionar(t[0], t[1]);
            verificarTecladoSinSolapesNiDesbordes(t[0] + "x" + t[1]);
        }
    }

    // ------------------------------------------------------------------ utilidades: acciones

    private void iniciar(String texto, String pista, String categoria) {
        GuiActionRunner.execute(() -> {
            controlador.iniciarPartidaConPalabra(new Palabra(texto, pista, categoria));
        });
        robot().waitForIdle();
    }

    private void clic(char letra) {
        window.button("btn_" + letra).click();
    }

    /** Intenta pulsar un botón deshabilitado: AssertJ Swing lo rechaza; el estado no debe cambiar. */
    private void intentarClicEnDeshabilitado(String nombre) {
        window.button(nombre).requireDisabled();
        try {
            window.button(nombre).click();
        } catch (IllegalStateException esperado) {
            // AssertJ Swing no deja pulsar un componente deshabilitado: es lo que se quiere comprobar
        }
        robot().waitForIdle();
    }

    /** Da el foco al área de pistas (solo lectura): ahí Espacio y Enter no hacen nada por sí mismos. */
    private void enfocarAreaDePistas() {
        activarVentana();
        window.textBox("txtPistas").focus();
    }

    /** Espera (sin fallar) a que la ventana principal sea la ventana activa del sistema. */
    private void activarVentana() {
        GuiActionRunner.execute(() -> {
            ventana.toFront();
        });
        window.focus();
        try {
            Pause.pause(new Condition("ventana principal activa") {
                @Override
                public boolean test() {
                    return GuiActionRunner.execute(() -> ventana.isActive());
                }
            }, Timeout.timeout(5_000));
        } catch (org.assertj.swing.exception.WaitTimedOutError e) {
            System.out.println("    [aviso] la ventana no llegó a ser la ventana activa (permiso/foco del sistema)");
        }
    }

    /**
     * Pulsa y suelta una tecla con el Robot SIN cambiar el foco. (Los métodos
     * {@code window.pressAndReleaseKeys(...)} del fixture primero dan el foco a la ventana, lo que
     * desharía el foco puesto en un botón y falsearía las pruebas de teclado.)
     */
    private void tecla(int codigo) {
        robot().pressAndReleaseKey(codigo);
        robot().waitForIdle();
    }

    /** Igual que {@link #tecla(int)} pero con Mayúscula (Shift) pulsada. */
    private void teclaConMayuscula(int codigo) {
        robot().pressKey(KeyEvent.VK_SHIFT);
        robot().pressAndReleaseKey(codigo);
        robot().releaseKey(KeyEvent.VK_SHIFT);
        robot().waitForIdle();
    }

    /** Juega unas letras y usa una pista para que la partida no esté limpia. */
    private void ensuciarPartida() {
        clic('X');
        clic('Z');
        clic('A');
        window.button("btnPistaCategoria").click();
        window.button("btnPistaLetra").click();
        assertThat(errores()).isEqualTo(2);
    }

    /** Estado de partida recién iniciada (distinta de {@code anterior}). */
    private void verificarPartidaLimpia(Partida anterior) {
        Partida nueva = partida();
        assertThat(nueva).as("es otra partida").isNotSameAs(anterior);
        assertThat(errores()).isZero();
        assertThat(estado()).isEqualTo(EstadoJuego.EN_CURSO);
        assertThat(nombresPartes()).containsExactlyElementsOf(PARTES_EN_ORDEN);
        window.label("lblIntentos").requireText("Intentos restantes: 7 de 7");
        window.label("lblLetrasCorrectas").requireText("Letras correctas: ninguna");
        window.label("lblLetrasIncorrectas").requireText("Letras incorrectas: ninguna");
        String esperada = String.join(" ", java.util.Collections.nCopies(nueva.getPalabra().longitud(), "_"));
        window.label("lblPalabraOculta").requireText(esperada);
        assertThat(teclasHabilitadas()).isEqualTo(27);
        window.button("btnPistaCategoria").requireEnabled();
        window.button("btnPistaLetra").requireEnabled();
        window.button("btnPistaTexto").requireEnabled();
        window.label("lblPistasRestantes").requireText("Pistas restantes: 3");
        window.textBox("txtPistas").requireText("");
    }

    private void verificarAyudaYCerrar() {
        org.assertj.swing.fixture.JOptionPaneFixture ayuda = window.optionPane(Timeout.timeout(10_000));
        ayuda.requireTitle("Cómo jugar");
        assertThat(ayuda.target().getMessage().toString()).contains("7 veces").contains("Control+N");
        ayuda.okButton().click();
        robot().waitForIdle();
        assertThat(hayDialogoAbierto()).isFalse();
        activarVentana();
    }

    /** Igual que {@link #tecla(int)} pero con Control pulsada (atajos Ctrl+N y Ctrl+Q). */
    private void teclaConControl(int codigo) {
        robot().pressKey(KeyEvent.VK_CONTROL);
        robot().pressAndReleaseKey(codigo);
        robot().releaseKey(KeyEvent.VK_CONTROL);
        robot().waitForIdle();
    }

    /** Excepción propia que corta System.exit sin terminar la JVM. */
    private static final class SalidaInterceptada extends RuntimeException {
        private static final long serialVersionUID = 1L;
        SalidaInterceptada(int estado) {
            super("System.exit(" + estado + ") interceptado por la prueba");
        }
    }

    /**
     * Ejecuta la acción con un SecurityManager que anota el estado pedido a System.exit y lanza
     * {@link SalidaInterceptada} para que la JVM no termine. El administrador se instala solo aquí y se
     * restaura siempre. La excepción sale del manejador del botón o del menú en el hilo de eventos
     * (Swing la imprime y sigue despachando eventos), por eso el estado se anota antes de lanzar.
     *
     * @return el estado pasado a System.exit, o {@code null} si nadie pidió salir
     */
    @SuppressWarnings({"removal", "deprecation"})
    private Integer interceptarSalida(Runnable accion) {
        AtomicReference<Integer> pedido = new AtomicReference<>();
        SecurityManager previo = System.getSecurityManager();
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkExit(int estado) {
                pedido.compareAndSet(null, estado);
                throw new SalidaInterceptada(estado);
            }

            @Override
            public void checkPermission(Permission permiso) {
                // todo lo demás está permitido
            }

            @Override
            public void checkPermission(Permission permiso, Object contexto) {
                // todo lo demás está permitido
            }
        });
        try {
            accion.run();
            robot().waitForIdle();
        } finally {
            System.setSecurityManager(previo);
        }
        return pedido.get();
    }

    private DialogFixture dialogoFin() {
        return window.dialog("dlgFinPartida", Timeout.timeout(10_000));
    }

    private void cerrarDialogoFin(DialogFixture dialogo) {
        dialogo.close(); // DISPOSE_ON_CLOSE
        robot().waitForIdle();
        assertThat(hayDialogoAbierto()).as("el diálogo se cerró").isFalse();
        activarVentana();
    }

    private void redimensionar(int ancho, int alto) {
        window.resizeTo(new Dimension(ancho, alto));
        robot().waitForIdle();
        Dimension real = tamano();
        if (real.width != ancho || real.height != alto) {
            System.out.println("    [info] el arrastre real dejó " + real.width + "x" + real.height
                    + "; se usa setSize(" + ancho + ", " + alto + ")");
            GuiActionRunner.execute(() -> {
                ventana.setSize(ancho, alto);
            });
            robot().waitForIdle();
        }
        Dimension finalSize = tamano();
        assertThat(finalSize).as("tamaño tras redimensionar").isEqualTo(new Dimension(ancho, alto));
    }

    // ------------------------------------------------------------------ utilidades: lectura (siempre en el EDT)

    private PanelAhorcado panelAhorcado() {
        return (PanelAhorcado) window.panel("panelAhorcado").target();
    }

    private int errores() {
        final PanelAhorcado panel = panelAhorcado();
        return GuiActionRunner.execute(() -> panel.getErrores());
    }

    private List<String> nombresPartes() {
        final PanelAhorcado panel = panelAhorcado();
        return GuiActionRunner.execute(() -> panel.getNombresPartes());
    }

    private Partida partida() {
        return GuiActionRunner.execute(() -> controlador.getPartida());
    }

    private EstadoJuego estado() {
        return GuiActionRunner.execute(() -> controlador.getPartida().getEstado());
    }

    private List<JButton> botonesTeclado() {
        final PanelTeclado panel = (PanelTeclado) window.panel("panelTeclado").target();
        return GuiActionRunner.execute(() -> panel.getBotones());
    }

    private String textoDe(JButton boton) {
        return GuiActionRunner.execute(() -> boton.getText());
    }

    private int teclasHabilitadas() {
        final PanelTeclado panel = (PanelTeclado) window.panel("panelTeclado").target();
        return GuiActionRunner.execute(() -> {
            int n = 0;
            for (JButton b : panel.getBotones()) {
                if (b.isEnabled()) {
                    n++;
                }
            }
            return n;
        });
    }

    private int mnemonico(String nombreBoton) {
        final JButton boton = window.button(nombreBoton).target();
        return GuiActionRunner.execute(() -> boton.getMnemonic());
    }

    private Dimension tamano() {
        return GuiActionRunner.execute(() -> ventana.getSize());
    }

    private String nombreConFoco() {
        return GuiActionRunner.execute(() -> {
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            return c == null ? null : c.getName();
        });
    }

    private boolean hayDialogoAbierto() {
        return GuiActionRunner.execute(() -> {
            for (Window w : ventana.getOwnedWindows()) {
                if (w.isShowing()) {
                    return true;
                }
            }
            return false;
        });
    }

    /** Partida terminada: 27 teclas y 3 pistas deshabilitadas. */
    private void verificarJuegoTerminadoDeshabilitado() {
        assertThat(teclasHabilitadas()).as("teclas en pantalla habilitadas tras el fin").isZero();
        window.button("btnPistaCategoria").requireDisabled();
        window.button("btnPistaLetra").requireDisabled();
        window.button("btnPistaTexto").requireDisabled();
    }

    /**
     * Comprueba, con los límites reales de los componentes: los 27 botones están dentro del área
     * útil de panelTeclado, no se solapan entre sí, y panelTeclado está dentro de la ventana y no
     * invade panelEstado ni panelPistas.
     */
    private void verificarTecladoSinSolapesNiDesbordes(String contexto) {
        final PanelTeclado panel = (PanelTeclado) window.panel("panelTeclado").target();
        final Component estadoPanel = window.panel("panelEstado").target();
        final Component pistasPanel = window.panel("panelPistas").target();
        List<String> problemas = GuiActionRunner.execute(() -> {
            List<String> lista = new ArrayList<>();
            Rectangle util = new Rectangle(panel.getInsets().left, panel.getInsets().top,
                    panel.getWidth() - panel.getInsets().left - panel.getInsets().right,
                    panel.getHeight() - panel.getInsets().top - panel.getInsets().bottom);
            List<JButton> botones = panel.getBotones();
            for (JButton b : botones) {
                if (b.getWidth() <= 0 || b.getHeight() <= 0) {
                    lista.add(b.getName() + " sin tamaño");
                }
                if (!util.contains(b.getBounds())) {
                    lista.add(b.getName() + " fuera del panel: " + b.getBounds() + " vs " + util);
                }
            }
            for (int i = 0; i < botones.size(); i++) {
                for (int j = i + 1; j < botones.size(); j++) {
                    if (botones.get(i).getBounds().intersects(botones.get(j).getBounds())) {
                        lista.add("solape " + botones.get(i).getName() + " / " + botones.get(j).getName());
                    }
                }
            }
            java.awt.Container raiz = ventana.getContentPane();
            Rectangle contenido = new Rectangle(0, 0, raiz.getWidth(), raiz.getHeight());
            Rectangle rTeclado = SwingUtilities.convertRectangle(panel.getParent(), panel.getBounds(), raiz);
            if (!contenido.contains(rTeclado)) {
                lista.add("panelTeclado fuera de la ventana: " + rTeclado + " vs " + contenido);
            }
            for (Component c : new Component[] {estadoPanel, pistasPanel}) {
                Rectangle r = SwingUtilities.convertRectangle(c.getParent(), c.getBounds(), raiz);
                if (r.intersects(rTeclado)) {
                    lista.add("panelTeclado solapa " + c.getName() + ": " + rTeclado + " / " + r);
                }
            }
            return lista;
        });
        assertThat(problemas).as("distribución del teclado en %s", contexto).isEmpty();
    }

    // ------------------------------------------------------------------ ejecución directa con resumen por prueba

    /**
     * Ejecuta la suite con {@link JUnitCore} e imprime una línea por prueba y un resumen.
     * (Equivale a {@code java org.junit.runner.JUnitCore AhorcadoGUITest}, pero con más detalle.)
     *
     * @param args opcional: prefijos de nombres de método a ejecutar (por defecto, toda la suite)
     */
    public static void main(String[] args) {
        final List<String> detalles = new ArrayList<>();
        JUnitCore nucleo = new JUnitCore();
        nucleo.addListener(new RunListener() {
            private boolean fallo;
            private long inicio;

            @Override
            public void testStarted(Description d) {
                fallo = false;
                inicio = System.currentTimeMillis();
                System.out.println("  ... " + d.getMethodName());
            }

            @Override
            public void testFailure(Failure f) {
                fallo = true;
                String msg = String.valueOf(f.getMessage()).split("\n")[0];
                System.out.println("  FALLA " + f.getDescription().getMethodName() + " -> " + msg);
                detalles.add(f.getTestHeader() + "\n" + f.getTrace());
            }

            @Override
            public void testAssumptionFailure(Failure f) {
                System.out.println("  OMITIDA " + f.getDescription().getMethodName() + " -> " + f.getMessage());
            }

            @Override
            public void testFinished(Description d) {
                long ms = System.currentTimeMillis() - inicio;
                System.out.println("  " + (fallo ? "FALLA" : "OK   ") + " " + d.getMethodName() + " (" + ms + " ms)");
            }
        });
        Result r;
        if (args.length == 0) {
            r = nucleo.run(AhorcadoGUITest.class);
        } else { // opcional: nombres de métodos para ejecutar solo algunas pruebas
            r = nucleo.run(Request.aClass(AhorcadoGUITest.class).filterWith(new Filter() {
                @Override
                public boolean shouldRun(Description d) {
                    if (d.getMethodName() == null) {
                        return true;
                    }
                    for (String a : args) {
                        if (d.getMethodName().startsWith(a)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public String describe() {
                    return "métodos pedidos por argumento";
                }
            }));
        }
        for (String d : detalles) {
            System.out.println("\n---- detalle ----\n" + d);
        }
        System.out.println("\nPruebas de interfaz ejecutadas: " + r.getRunCount()
                + " | Fallos: " + r.getFailureCount() + " | Omitidas: " + r.getIgnoreCount()
                + " | Tiempo: " + (r.getRunTime() / 1000) + " s");
        System.out.println(r.wasSuccessful() ? "RESULTADO: TODAS LAS PRUEBAS DE INTERFAZ OK" : "RESULTADO: HAY FALLOS");
        System.exit(r.wasSuccessful() ? 0 : 1);
    }
}
