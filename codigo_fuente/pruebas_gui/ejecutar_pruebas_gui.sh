#!/usr/bin/env bash
# Compila el juego y las pruebas de interfaz OPCIONALES (JUnit 4 + AssertJ Swing) y las ejecuta.
#
# *** ADVERTENCIA: estas pruebas CONTROLAN TU RATON Y TU TECLADO REALES (java.awt.Robot) ***
# Duran entre 1 y 3 minutos. Avisa a tu equipo, no uses el computador mientras corren y no
# tengas otra ventana importante abierta (podrian recibir pulsaciones si pierden el foco).
# macOS: concede "Accesibilidad" (y a veces "Grabacion de pantalla") a la terminal o al IDE que
# ejecuta este script: Ajustes del Sistema > Privacidad y seguridad. Ver LEEME.md.
#
# La JVM se lanza con -Djava.security.manager=allow para que las pruebas de "Salir" (pm22, pm23a, pm23b)
# puedan interceptar System.exit sin cerrar la suite.
# Requisitos: JDK 17, un entorno grafico y haber corrido antes ./descargar_dependencias.sh
# Uso (desde cualquier carpeta):  codigo_fuente/pruebas_gui/ejecutar_pruebas_gui.sh
# Variable opcional: LIMITE_SEGUNDOS (por defecto 900) = tiempo maximo de toda la suite.
# Codigo de salida: 0 si todas pasan; distinto de 0 si alguna falla, si faltan los JAR o si se agota el tiempo.
set -u

DIR="$(cd "$(dirname "$0")" && pwd)"
RAIZ="$(cd "$DIR/.." && pwd)"            # codigo_fuente
LIB="$RAIZ/lib_test"
LIMITE="${LIMITE_SEGUNDOS:-900}"

if ! ls "$LIB"/*.jar >/dev/null 2>&1; then
  echo "ERROR: faltan los JAR en $LIB. Ejecuta primero: $DIR/descargar_dependencias.sh" >&2
  exit 2
fi

SALIDA="$(mktemp -d "${TMPDIR:-/tmp}/ahorcado_gui.XXXXXX")" || exit 2
trap 'rm -rf "$SALIDA"' EXIT
mkdir -p "$SALIDA/juego" "$SALIDA/pruebas"

echo "==> Compilando el juego (JDK 17) en $SALIDA/juego"
javac --release 17 -encoding UTF-8 -d "$SALIDA/juego" $(find "$RAIZ/src" -name "*.java") || exit 2

echo "==> Compilando las pruebas de interfaz"
javac --release 17 -encoding UTF-8 -d "$SALIDA/pruebas" -cp "$SALIDA/juego:$LIB/*" "$DIR/AhorcadoGUITest.java" || exit 2

echo "==> Ejecutando (el raton y el teclado se moveran solos; limite $LIMITE s)..."
java -Djava.security.manager=allow --add-opens java.base/java.util=ALL-UNNAMED -cp "$SALIDA/pruebas:$SALIDA/juego:$LIB/*" AhorcadoGUITest &
PID=$!

# Vigilante: si la suite se cuelga (por ejemplo por falta de permisos de macOS) se termina el proceso.
( sleep "$LIMITE"; echo "ERROR: se agoto el limite de $LIMITE s; se termina la suite." >&2; kill "$PID" 2>/dev/null ) &
VIGILANTE=$!

wait "$PID"
CODIGO=$?
kill "$VIGILANTE" 2>/dev/null
wait "$VIGILANTE" 2>/dev/null

if [ "$CODIGO" -eq 0 ]; then
  echo "==> Resultado final: TODAS LAS PRUEBAS DE INTERFAZ PASARON (codigo 0)"
else
  echo "==> Resultado final: HAY FALLOS o la ejecucion no termino bien (codigo $CODIGO)" >&2
fi
exit "$CODIGO"
