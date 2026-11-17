package modelo;

import java.util.ArrayList;
import java.util.Random;

/** Pista que revela una letra al azar que aún no estaba descubierta (no cuenta como error). */
public class PistaLetra extends Pista {

    private final Random azar = new Random();

    /** Crea la pista de letra. */
    public PistaLetra() {
        super("Letra", "Revela una letra aún no descubierta, sin contar como error.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * HERENCIA: implementa el método abstracto de {@link Pista}.
     */
    @Override
    public String obtener(Partida p) {
        String texto = p.getPalabra().getTexto();
        ArrayList<Character> correctas = p.getLetrasCorrectas();
        // ARRAYLIST: lista de letras candidatas (las de la palabra que aún no se han revelado).
        ArrayList<Character> candidatas = new ArrayList<>();
        for (int i = 0; i < texto.length(); i++) {
            Character letra = texto.charAt(i);
            if (!correctas.contains(letra) && !candidatas.contains(letra)) {
                candidatas.add(letra);
            }
        }
        if (candidatas.isEmpty()) {
            return "No quedan letras por revelar.";
        }
        char elegida = candidatas.get(azar.nextInt(candidatas.size()));
        p.revelarLetra(elegida); // no suma errores
        return "Se reveló la letra: " + elegida;
    }
}
