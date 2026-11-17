package util;

import controlador.ControladorJuego;
import controlador.VistaJuego;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import modelo.BancoPalabras;
import modelo.Categoria;
import modelo.Dificultad;
import modelo.EstadoJuego;
import modelo.Palabra;
import modelo.Partida;
import modelo.Pista;
import modelo.PistaCategoria;
import modelo.PistaLetra;
import modelo.PistaTexto;
import modelo.ResultadoLetra;
import vista.PanelAhorcado;

/**
 * Pruebas automáticas SIN JUnit. Cada prueba imprime {@code OK} o {@code FALLO};
 * si alguna falla, el programa termina con código de salida 1.
 * <p>
 * Ejecución: {@code java -cp out util.PruebasAutomaticas}
 */
public final class PruebasAutomaticas {

    private static int total = 0;
    private static int fallos = 0;

    /** Clase de utilidades: no se instancia. */
    private PruebasAutomaticas() {
    }

    /**
     * Ejecuta todas las pruebas.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        pruebasNormalizador();
        pruebasPalabra();
        pruebasBanco();
        pruebasPartida();
        pruebasPistas();
        pruebasControlador();
        pruebasDibujo();

        System.out.println();
        System.out.println("Pruebas ejecutadas: " + total + " | Fallos: " + fallos);
        if (fallos > 0) {
            System.out.println("RESULTADO: FALLO");
            System.exit(1);
        }
        System.out.println("RESULTADO: TODAS LAS PRUEBAS OK");
    }

    /** Registra el resultado de una comprobación. */
    private static void comprobar(String nombre, boolean condicion) {
        total++;
        if (condicion) {
            System.out.println("OK    - " + nombre);
        } else {
            fallos++;
            System.out.println("FALLO - " + nombre);
        }
    }

    private static Palabra palabraPrueba(String texto) {
        return new Palabra(texto, "pista de prueba", "Pruebas");
    }

    // ------------------------------------------------------------------ Normalizador

    private static void pruebasNormalizador() {
        comprobar("Normalizador: quita tildes (MURCIÉLAGO -> MURCIELAGO)", Normalizador.normalizar("MURCIÉLAGO").equals("MURCIELAGO"));
        comprobar("Normalizador: sube a mayúsculas y quita diéresis (pingüino -> PINGUINO)", Normalizador.normalizar("pingüino").equals("PINGUINO"));
        comprobar("Normalizador: conserva la Ñ (ñandú -> ÑANDU)", Normalizador.normalizar("ñandú").equals("ÑANDU"));
        comprobar("Normalizador: normalizarLetra('á') = 'A'", Normalizador.normalizarLetra('á') == 'A');
        comprobar("Normalizador: normalizarLetra('ñ') = 'Ñ'", Normalizador.normalizarLetra('ñ') == 'Ñ');
        comprobar("Normalizador: normalizarLetra('Ü') = 'U'", Normalizador.normalizarLetra('Ü') == 'U');
        comprobar("Normalizador: dígitos y espacios no son letras", Normalizador.normalizarLetra('7') == '\0'
                && Normalizador.normalizarLetra(' ') == '\0' && !Normalizador.esLetraValida('-'));
        comprobar("Normalizador: esLetraValida acepta A, Ñ y é", Normalizador.esLetraValida('A')
                && Normalizador.esLetraValida('Ñ') && Normalizador.esLetraValida('é'));
        comprobar("Normalizador: normalizar(null) devuelve vacío", Normalizador.normalizar(null).isEmpty());
    }

    // ------------------------------------------------------------------ Palabra y Dificultad

    private static void pruebasPalabra() {
        Palabra p = new Palabra("MURCIÉLAGO", "Vuela de noche", "Animales");
        comprobar("Palabra: texto normalizado sin tildes", p.getTexto().equals("MURCIELAGO"));
        comprobar("Palabra: conserva el texto original con tildes", p.getTextoOriginal().equals("MURCIÉLAGO"));
        comprobar("Palabra: dificultad por longitud (10 letras = DIFÍCIL)", p.getDificultad() == Dificultad.DIFICIL);
        comprobar("Palabra: contiene() ignora tildes", p.contiene('é') && p.contiene('E') && !p.contiene('Z'));
        comprobar("Dificultad: 3-5 FÁCIL, 6-8 MEDIO, 9+ DIFÍCIL", Dificultad.paraLongitud(3) == Dificultad.FACIL
                && Dificultad.paraLongitud(5) == Dificultad.FACIL && Dificultad.paraLongitud(6) == Dificultad.MEDIO
                && Dificultad.paraLongitud(8) == Dificultad.MEDIO && Dificultad.paraLongitud(9) == Dificultad.DIFICIL);
        boolean lanza = false;
        try {
            new Palabra("DOS PALABRAS", "x", "y");
        } catch (IllegalArgumentException e) {
            lanza = true;
        }
        comprobar("Palabra: rechaza espacios (IllegalArgumentException)", lanza);
    }

    // ------------------------------------------------------------------ Banco

    private static void pruebasBanco() {
        BancoPalabras banco = new BancoPalabras();
        comprobar("Banco: 200 palabras", banco.totalPalabras() == 200);
        comprobar("Banco: 8 categorías", banco.getCategorias().size() == 8);
        boolean veinticinco = true;
        for (Categoria c : banco.getCategorias()) {
            if (c.tamano() != 25) {
                veinticinco = false;
            }
        }
        comprobar("Banco: cada categoría tiene 25 palabras", veinticinco);

        HashSet<String> vistos = new HashSet<>();
        boolean sinDuplicados = true;
        boolean mayusculas = true;
        boolean sinEspacios = true;
        boolean sinTildes = true;
        boolean pistasLlenas = true;
        boolean reglaDificultad = true;
        for (Categoria c : banco.getCategorias()) {
            for (Palabra p : c.getPalabras()) {
                if (!vistos.add(p.getTexto())) {
                    sinDuplicados = false;
                }
                if (!p.getTexto().equals(p.getTexto().toUpperCase())) {
                    mayusculas = false;
                }
                if (p.getTexto().contains(" ") || p.getTextoOriginal().contains(" ")) {
                    sinEspacios = false;
                }
                for (int i = 0; i < p.getTexto().length(); i++) {
                    char ch = p.getTexto().charAt(i);
                    boolean basica = (ch >= 'A' && ch <= 'Z') || ch == 'Ñ';
                    if (!basica) {
                        sinTildes = false;
                    }
                }
                if (p.getPista() == null || p.getPista().isBlank()) {
                    pistasLlenas = false;
                }
                int n = p.longitud();
                Dificultad esperada = n <= 5 ? Dificultad.FACIL : (n <= 8 ? Dificultad.MEDIO : Dificultad.DIFICIL);
                if (n < 3 || p.getDificultad() != esperada) {
                    reglaDificultad = false;
                }
            }
        }
        comprobar("Banco: sin palabras duplicadas", sinDuplicados);
        comprobar("Banco: todas en mayúsculas", mayusculas);
        comprobar("Banco: ninguna con espacios", sinEspacios);
        comprobar("Banco: texto normalizado sin tildes (solo A-Z y Ñ)", sinTildes);
        comprobar("Banco: todas tienen pista no vacía", pistasLlenas);
        comprobar("Banco: regla de dificultad por longitud (mín. 3 letras)", reglaDificultad);
        comprobar("Banco: al menos 30 palabras FÁCIL", banco.contarPorDificultad(Dificultad.FACIL) >= 30);
        comprobar("Banco: al menos 30 palabras MEDIO", banco.contarPorDificultad(Dificultad.MEDIO) >= 30);
        comprobar("Banco: al menos 30 palabras DIFÍCIL", banco.contarPorDificultad(Dificultad.DIFICIL) >= 30);
        comprobar("Banco: la suma por dificultad es 200", banco.contarPorDificultad(Dificultad.FACIL)
                + banco.contarPorDificultad(Dificultad.MEDIO) + banco.contarPorDificultad(Dificultad.DIFICIL) == 200);
        comprobar("Banco: contiene() encuentra una palabra del banco",
                banco.contiene(banco.getCategorias().get(0).getPalabras().get(0).getTexto()) && !banco.contiene("ZZZZZZ"));
        comprobar("Banco: resumenPorCategoria tiene 8 líneas", banco.resumenPorCategoria().split("\n").length == 8);

        // Elegir sin repetir hasta agotar el conjunto
        BancoPalabras otro = new BancoPalabras();
        HashSet<String> elegidas = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            elegidas.add(otro.elegirAleatoria(null).getTexto());
        }
        comprobar("Banco: 200 sorteos aleatorios no repiten palabra", elegidas.size() == 200);
        comprobar("Banco: tras agotar las palabras reinicia y sigue sorteando", otro.elegirAleatoria(null) != null);

        boolean nivelCorrecto = true;
        for (Dificultad d : Dificultad.values()) {
            for (int i = 0; i < 20; i++) {
                if (banco.elegirAleatoria(d).getDificultad() != d) {
                    nivelCorrecto = false;
                }
            }
        }
        comprobar("Banco: elegirAleatoria(dificultad) respeta el nivel pedido", nivelCorrecto);
    }

    // ------------------------------------------------------------------ Partida

    private static void pruebasPartida() {
        // Victoria
        Partida victoria = new Partida(palabraPrueba("MURCIÉLAGO"));
        comprobar("Partida: empieza EN_CURSO con 0 errores", victoria.getEstado() == EstadoJuego.EN_CURSO
                && victoria.getErrores() == 0 && victoria.intentosRestantes() == 7);
        comprobar("Partida: palabraOculta inicial son guiones separados", victoria.palabraOculta().equals("_ _ _ _ _ _ _ _ _ _"));
        for (char c : "MURCIELAG".toCharArray()) {
            victoria.adivinar(c);
        }
        comprobar("Partida: estaCompleta es falso con una letra pendiente", !victoria.estaCompleta());
        ResultadoLetra ultima = victoria.adivinar('o'); // minúscula
        comprobar("Partida: adivinar acepta minúsculas (ACERTADA)", ultima == ResultadoLetra.ACERTADA);
        comprobar("Partida: ganar al descubrir todas las letras", victoria.getEstado() == EstadoJuego.GANADA && victoria.estaCompleta());
        comprobar("Partida: palabraOculta final muestra la palabra", victoria.palabraOculta().equals("M U R C I E L A G O"));

        // Derrota con 7 errores en orden
        Partida derrota = new Partida(palabraPrueba("GATO"));
        char[] malas = {'X', 'Z', 'Q', 'W', 'K', 'J', 'V'};
        boolean enOrden = true;
        for (int i = 0; i < malas.length; i++) {
            ResultadoLetra r = derrota.adivinar(malas[i]);
            boolean esUltima = i == malas.length - 1;
            if (r != ResultadoLetra.FALLADA || derrota.getErrores() != i + 1) {
                enOrden = false;
            }
            if (esUltima != (derrota.getEstado() == EstadoJuego.PERDIDA)) {
                enOrden = false; // solo el 7.º error pierde
            }
        }
        comprobar("Partida: 7 errores en orden, solo el 7.º pierde", enOrden);
        comprobar("Partida: derrota deja 0 intentos restantes", derrota.getEstado() == EstadoJuego.PERDIDA && derrota.intentosRestantes() == 0);
        comprobar("Partida: letras incorrectas conservan el orden", derrota.getLetrasIncorrectas().equals(
                new ArrayList<>(Arrays.asList('X', 'Z', 'Q', 'W', 'K', 'J', 'V'))));

        // Letra repetida
        Partida repetida = new Partida(palabraPrueba("GATO"));
        repetida.adivinar('G');
        comprobar("Partida: letra repetida acertada = REPETIDA sin cambios", repetida.adivinar('g') == ResultadoLetra.REPETIDA
                && repetida.getLetrasCorrectas().size() == 1);
        repetida.adivinar('X');
        comprobar("Partida: letra repetida fallada no suma error", repetida.adivinar('x') == ResultadoLetra.REPETIDA
                && repetida.getErrores() == 1);

        // Caracteres inválidos
        Partida invalida = new Partida(palabraPrueba("GATO"));
        comprobar("Partida: caracteres inválidos (1, espacio, -, !) son INVALIDA y no penalizan",
                invalida.adivinar('1') == ResultadoLetra.INVALIDA && invalida.adivinar(' ') == ResultadoLetra.INVALIDA
                && invalida.adivinar('-') == ResultadoLetra.INVALIDA && invalida.adivinar('!') == ResultadoLetra.INVALIDA
                && invalida.getErrores() == 0);
        comprobar("Partida: letra con tilde se normaliza (á cuenta como A)", invalida.adivinar('á') == ResultadoLetra.ACERTADA);

        // Ñ
        Partida enie = new Partida(palabraPrueba("PIÑA"));
        comprobar("Partida: la Ñ es una letra distinta de la N", enie.adivinar('N') == ResultadoLetra.FALLADA
                && enie.adivinar('ñ') == ResultadoLetra.ACERTADA);

        // Teclas tras el fin
        int erroresAntes = derrota.getErrores();
        comprobar("Partida: tras perder, una letra válida es PARTIDA_TERMINADA y no cambia nada",
                derrota.adivinar('A') == ResultadoLetra.PARTIDA_TERMINADA && derrota.getErrores() == erroresAntes
                && derrota.getEstado() == EstadoJuego.PERDIDA);
        comprobar("Partida: tras ganar, una letra válida es PARTIDA_TERMINADA",
                victoria.adivinar('Z') == ResultadoLetra.PARTIDA_TERMINADA && victoria.getErrores() == 0);

        // Copias defensivas (encapsulamiento)
        Partida copia = new Partida(palabraPrueba("GATO"));
        copia.adivinar('G');
        copia.getLetrasCorrectas().add('Z');
        comprobar("Partida: getLetrasCorrectas devuelve una copia (encapsulamiento)", copia.getLetrasCorrectas().size() == 1);

        // Reinicio
        Palabra gato = palabraPrueba("GATO");
        Partida vieja = new Partida(gato);
        vieja.adivinar('X');
        vieja.usarPista(0);
        Partida nueva = new Partida(gato);
        comprobar("Partida: una partida nueva reinicia errores, letras y pistas", nueva.getErrores() == 0
                && nueva.getLetrasCorrectas().isEmpty() && nueva.pistasRestantes() == 3 && nueva.getEstado() == EstadoJuego.EN_CURSO);
    }

    // ------------------------------------------------------------------ Pistas

    private static void pruebasPistas() {
        Partida p = new Partida(new Palabra("MURCIÉLAGO", "Mamífero volador nocturno", "Animales"));
        comprobar("Pistas: al inicio quedan 3", p.pistasRestantes() == 3 && p.pistaDisponible(0) && p.pistaDisponible(1) && p.pistaDisponible(2));
        comprobar("Pistas: la de categoría devuelve 'Categoría: Animales'", "Categoría: Animales".equals(p.usarPista(0)));
        comprobar("Pistas: una pista usada no se puede repetir (null)", p.usarPista(0) == null && !p.pistaDisponible(0));
        comprobar("Pistas: tras usar una quedan 2", p.pistasRestantes() == 2);
        comprobar("Pistas: la pista escrita devuelve el texto con tildes", "Mamífero volador nocturno".equals(p.usarPista(2)));
        comprobar("Pistas: índice inválido devuelve null", p.usarPista(-1) == null && p.usarPista(3) == null);

        Partida q = new Partida(new Palabra("MURCIÉLAGO", "x", "Animales"));
        q.adivinar('M');
        q.adivinar('U');
        ArrayList<Character> antes = q.getLetrasCorrectas();
        String texto = q.usarPista(1);
        ArrayList<Character> despues = q.getLetrasCorrectas();
        boolean nuevaLetra = despues.size() == antes.size() + 1 && !antes.contains(despues.get(despues.size() - 1));
        comprobar("PistaLetra: no cuenta como error", q.getErrores() == 0);
        comprobar("PistaLetra: revela una letra nueva (no repite las ya reveladas)", nuevaLetra);
        comprobar("PistaLetra: el mensaje indica la letra revelada", texto != null
                && texto.equals("Se reveló la letra: " + despues.get(despues.size() - 1)));
        comprobar("PistaLetra: solo se puede usar una vez", q.usarPista(1) == null);

        Partida r = new Partida(palabraPrueba("OSO"));
        r.adivinar('O');
        r.usarPista(1);
        comprobar("PistaLetra: si revela la última letra, la partida se gana", r.getEstado() == EstadoJuego.GANADA && r.getErrores() == 0);
        comprobar("Pistas: no se usan pistas con la partida terminada", r.usarPista(0) == null);

        // Polimorfismo: tres subclases tratadas como Pista
        Pista[] tipos = {new PistaCategoria(), new PistaLetra(), new PistaTexto()};
        Partida s = new Partida(palabraPrueba("GATO"));
        boolean todasDevuelven = true;
        for (Pista pista : tipos) {
            if (pista.usar(s) == null || !pista.isUsada() || pista.usar(s) != null) {
                todasDevuelven = false;
            }
        }
        comprobar("Pista (polimorfismo): usar() es plantilla y funciona igual en las 3 subclases", todasDevuelven);
    }

    // ------------------------------------------------------------------ Controlador

    /** Vista falsa que solo cuenta las llamadas (demuestra el desacople por interfaz). */
    private static class VistaFalsa implements VistaJuego {
        int actualizaciones = 0;
        int finales = 0;
        String ultimoMensaje = "";

        @Override
        public void actualizar(Partida partida) {
            actualizaciones++;
        }

        @Override
        public void mostrarFin(Partida partida) {
            finales++;
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            ultimoMensaje = mensaje;
        }
    }

    private static void pruebasControlador() {
        VistaFalsa vista = new VistaFalsa();
        ControladorJuego controlador = new ControladorJuego(new BancoPalabras());
        controlador.setVista(vista);
        controlador.iniciarPartidaConPalabra(palabraPrueba("SOL"));
        comprobar("Controlador: iniciar partida actualiza la vista", vista.actualizaciones == 1 && controlador.getPartida() != null);
        controlador.procesarLetra('S');
        comprobar("Controlador: letra acertada actualiza la vista", vista.actualizaciones == 2 && vista.finales == 0);
        controlador.procesarLetra('s');
        comprobar("Controlador: letra repetida no cambia la partida", controlador.getPartida().getLetrasCorrectas().size() == 1);
        controlador.procesarLetra('5');
        comprobar("Controlador: carácter inválido muestra mensaje y no penaliza", controlador.getPartida().getErrores() == 0
                && vista.ultimoMensaje.contains("letras"));
        controlador.procesarLetra('O');
        controlador.procesarLetra('L');
        comprobar("Controlador: al ganar avisa a la vista (mostrarFin)", vista.finales == 1
                && controlador.getPartida().getEstado() == EstadoJuego.GANADA);
        int act = vista.actualizaciones;
        controlador.procesarLetra('Z');
        comprobar("Controlador: teclas tras el fin se ignoran", vista.actualizaciones == act && vista.finales == 1);

        controlador.iniciarPartidaConPalabra(palabraPrueba("SOL"));
        for (char c : new char[] {'X', 'Z', 'Q', 'W', 'K', 'J', 'V'}) {
            controlador.procesarLetra(c);
        }
        comprobar("Controlador: 7 errores terminan la partida como PERDIDA", controlador.getPartida().getEstado() == EstadoJuego.PERDIDA
                && vista.finales == 2);

        controlador.iniciarNuevaPartida(Dificultad.FACIL);
        comprobar("Controlador: nueva partida con dificultad FÁCIL reinicia todo", controlador.getPartida().getErrores() == 0
                && controlador.getPartida().getPalabra().getDificultad() == Dificultad.FACIL
                && controlador.getPartida().getEstado() == EstadoJuego.EN_CURSO);
        controlador.usarPista(0);
        comprobar("Controlador: usarPista muestra la pista y baja el contador", controlador.getPartida().pistasRestantes() == 2
                && vista.ultimoMensaje.startsWith("Pista: Categoría"));
    }

    // ------------------------------------------------------------------ Dibujo

    private static void pruebasDibujo() {
        PanelAhorcado panel = new PanelAhorcado();
        ArrayList<String> esperado = new ArrayList<>(Arrays.asList("Cabeza", "Torso", "Brazo derecho",
                "Brazo izquierdo", "Pierna derecha", "Pierna izquierda", "Cuerda"));
        comprobar("Dibujo: las 7 partes están en el orden de la especificación", panel.getNombresPartes().equals(esperado));
        panel.setErrores(99);
        comprobar("Dibujo: setErrores limita a 7", panel.getErrores() == 7);
        panel.setErrores(-3);
        comprobar("Dibujo: setErrores no baja de 0", panel.getErrores() == 0);
    }
}
