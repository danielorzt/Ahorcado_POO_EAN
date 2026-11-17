package modelo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Banco de palabras del juego: agrupa las palabras por categoría y elige una palabra al azar
 * sin repetirla hasta agotar las candidatas.
 */
public class BancoPalabras {

    // ARRAYLIST: lista de categorías y lista de palabras ya usadas (crecen y se vacían en ejecución).
    private ArrayList<Categoria> categorias;
    private ArrayList<Palabra> usadas;
    private Random azar;

    /**
     * Carga el banco desde {@code DatosPalabras.DATOS}.
     *
     * @throws IllegalStateException si hay una palabra duplicada o una fila mal formada
     */
    public BancoPalabras() {
        categorias = new ArrayList<>();
        usadas = new ArrayList<>();
        azar = new Random();
        for (String[] fila : DatosPalabras.DATOS) {
            if (fila.length < 3) {
                throw new IllegalStateException("Fila de datos incompleta: " + String.join(",", fila));
            }
            Palabra palabra = new Palabra(fila[1], fila[2], fila[0]);
            if (contiene(palabra.getTexto())) {
                throw new IllegalStateException("Palabra duplicada en el banco: " + palabra.getTexto());
            }
            buscarOCrearCategoria(fila[0]).agregar(palabra);
        }
    }

    private Categoria buscarOCrearCategoria(String nombre) {
        for (Categoria c : categorias) {
            if (c.getNombre().equals(nombre)) {
                return c;
            }
        }
        Categoria nueva = new Categoria(nombre);
        categorias.add(nueva);
        return nueva;
    }

    /**
     * Elige una palabra al azar. No repite palabras ya usadas hasta agotar el conjunto candidato;
     * entonces reinicia la lista de usadas.
     *
     * @param dificultad nivel pedido, o {@code null} para ALEATORIA (cualquier nivel)
     * @return una palabra del banco
     */
    public Palabra elegirAleatoria(Dificultad dificultad) {
        // ARRAYLIST: se arma la lista de candidatas, se quitan las usadas y se escoge un índice al azar.
        ArrayList<Palabra> candidatas = new ArrayList<>();
        for (Categoria c : categorias) {
            if (dificultad == null) {
                candidatas.addAll(c.getPalabras());
            } else {
                candidatas.addAll(c.obtenerPorDificultad(dificultad));
            }
        }
        if (candidatas.isEmpty()) {
            throw new IllegalStateException("No hay palabras para la dificultad pedida.");
        }
        ArrayList<Palabra> disponibles = new ArrayList<>(candidatas);
        disponibles.removeAll(usadas);
        if (disponibles.isEmpty()) {
            usadas.clear();
            disponibles = candidatas;
        }
        Palabra elegida = disponibles.get(azar.nextInt(disponibles.size()));
        usadas.add(elegida);
        return elegida;
    }

    /** @return total de palabras en todas las categorías */
    public int totalPalabras() {
        int total = 0;
        for (Categoria c : categorias) {
            total += c.tamano();
        }
        return total;
    }

    /** @return copia de la lista de categorías */
    public ArrayList<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    /**
     * Resumen en texto con una línea por categoría, p. ej. {@code Animales: 25}.
     *
     * @return texto multilínea con el conteo
     */
    public String resumenPorCategoria() {
        StringBuilder sb = new StringBuilder();
        for (Categoria c : categorias) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(c.getNombre()).append(": ").append(c.tamano());
        }
        return sb.toString();
    }

    /**
     * Cuenta las palabras de un nivel de dificultad.
     *
     * @param d dificultad
     * @return cantidad de palabras de ese nivel
     */
    public int contarPorDificultad(Dificultad d) {
        int cuenta = 0;
        for (Categoria c : categorias) {
            cuenta += c.obtenerPorDificultad(d).size();
        }
        return cuenta;
    }

    /**
     * Indica si el banco ya tiene una palabra con ese texto normalizado.
     *
     * @param textoNormalizado texto en mayúsculas y sin tildes
     * @return {@code true} si existe
     */
    public boolean contiene(String textoNormalizado) {
        for (Categoria c : categorias) {
            for (Palabra p : c.getPalabras()) {
                if (p.getTexto().equals(textoNormalizado)) {
                    return true;
                }
            }
        }
        return false;
    }
}
