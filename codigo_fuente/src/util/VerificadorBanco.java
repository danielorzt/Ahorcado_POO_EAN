package util;

import modelo.BancoPalabras;
import modelo.Categoria;
import modelo.Dificultad;

/**
 * Herramienta de consola que revisa el banco de palabras: imprime el total y los conteos
 * por categoría y por dificultad. Termina con código 1 si no hay exactamente 200 palabras
 * repartidas en 8 categorías de 25.
 */
public final class VerificadorBanco {

    private static final int TOTAL_ESPERADO = 200;
    private static final int CATEGORIAS_ESPERADAS = 8;
    private static final int POR_CATEGORIA = 25;

    /** Clase de utilidades: no se instancia. */
    private VerificadorBanco() {
    }

    /**
     * Imprime el reporte del banco y sale con código 1 si algo no cumple.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        BancoPalabras banco;
        try {
            banco = new BancoPalabras();
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("ERROR al cargar el banco: " + e.getMessage());
            System.exit(1);
            return;
        }

        boolean correcto = true;
        System.out.println("Total de palabras: " + banco.totalPalabras());
        System.out.println("Categorías: " + banco.getCategorias().size());
        for (Categoria c : banco.getCategorias()) {
            System.out.println("  " + c.getNombre() + ": " + c.tamano());
            if (c.tamano() != POR_CATEGORIA) {
                correcto = false;
            }
        }
        System.out.println("Por dificultad (según la longitud de la palabra):");
        System.out.println("  FÁCIL (3 a 5 letras): " + banco.contarPorDificultad(Dificultad.FACIL));
        System.out.println("  MEDIO (6 a 8 letras): " + banco.contarPorDificultad(Dificultad.MEDIO));
        System.out.println("  DIFÍCIL (9 o más letras): " + banco.contarPorDificultad(Dificultad.DIFICIL));

        if (banco.totalPalabras() != TOTAL_ESPERADO || banco.getCategorias().size() != CATEGORIAS_ESPERADAS) {
            correcto = false;
        }
        if (correcto) {
            System.out.println("Resultado: CORRECTO (200 palabras, 8 categorías x 25)");
        } else {
            System.out.println("Resultado: INCORRECTO (se esperaban 200 palabras en 8 categorías x 25)");
            System.exit(1);
        }
    }
}
