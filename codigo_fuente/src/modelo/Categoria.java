package modelo;

import java.util.ArrayList;

/** Grupo de palabras con un mismo tema (animales, países, deportes...). */
public class Categoria {

    // ENCAPSULAMIENTO: atributos privados; la lista nunca se entrega directamente.
    private String nombre;
    // ARRAYLIST: lista dinámica con las palabras de la categoría.
    private ArrayList<Palabra> palabras;

    /**
     * Crea una categoría vacía.
     *
     * @param nombre nombre visible de la categoría
     */
    public Categoria(String nombre) {
        this.nombre = nombre;
        this.palabras = new ArrayList<>();
    }

    /**
     * Agrega una palabra a la categoría.
     *
     * @param p palabra a agregar
     */
    public void agregar(Palabra p) {
        palabras.add(p);
    }

    /**
     * Devuelve una COPIA de la lista de palabras (así nadie de afuera puede modificar la original).
     *
     * @return copia de las palabras
     */
    public ArrayList<Palabra> getPalabras() {
        return new ArrayList<>(palabras);
    }

    /**
     * Filtra las palabras por dificultad.
     *
     * @param d dificultad buscada
     * @return nueva lista con las palabras de ese nivel
     */
    public ArrayList<Palabra> obtenerPorDificultad(Dificultad d) {
        ArrayList<Palabra> resultado = new ArrayList<>();
        // ARRAYLIST: se recorre la lista y se agregan a otra solo las que cumplen.
        for (Palabra p : palabras) {
            if (p.getDificultad() == d) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /** @return cantidad de palabras de la categoría */
    public int tamano() {
        return palabras.size();
    }

    /** @return nombre de la categoría */
    public String getNombre() {
        return nombre;
    }
}
