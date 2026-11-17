#!/usr/bin/env bash
# Descarga (solo con curl + shasum) los JAR de las pruebas de interfaz opcionales.
# Destino: ../lib_test/  (los *.jar estan en .gitignore: no se suben al repositorio).
# Cada JAR se verifica contra el archivo .sha1 que Maven Central publica junto a el.
# Es idempotente: si el JAR ya existe y su SHA-1 coincide, no se vuelve a descargar.
#
# Cierre transitivo (leido de los POM en https://repo1.maven.org/maven2/):
#   assertj-swing-junit 3.17.1 -> assertj-swing 3.17.1 -> assertj-core 3.17.2, fest-util 1.2.5
#   assertj-swing-junit 3.17.1 -> junit 4.13.2 -> hamcrest-core 1.3
# (Se omiten las dependencias marcadas optional/provided/test en esos POM.)
set -u

DIR="$(cd "$(dirname "$0")" && pwd)"
LIB="$DIR/../lib_test"
BASE="https://repo1.maven.org/maven2"

# grupo/artefacto/version (la ruta en Maven Central)
JARS=(
  "org/assertj/assertj-swing-junit/3.17.1/assertj-swing-junit-3.17.1.jar"
  "org/assertj/assertj-swing/3.17.1/assertj-swing-3.17.1.jar"
  "org/assertj/assertj-core/3.17.2/assertj-core-3.17.2.jar"
  "org/easytesting/fest-util/1.2.5/fest-util-1.2.5.jar"
  "junit/junit/4.13.2/junit-4.13.2.jar"
  "org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
)

mkdir -p "$LIB" || { echo "ERROR: no se pudo crear $LIB" >&2; exit 1; }
descargados=0; omitidos=0

for ruta in "${JARS[@]}"; do
  nombre="$(basename "$ruta")"
  destino="$LIB/$nombre"

  esperado="$(curl -fsSL "$BASE/$ruta.sha1" | tr -d '[:space:]' | cut -c1-40)"
  if [ "${#esperado}" -ne 40 ]; then
    echo "ERROR: no se pudo leer el SHA-1 publicado de $nombre" >&2
    exit 1
  fi

  if [ -f "$destino" ] && [ "$(shasum -a 1 "$destino" | cut -d' ' -f1)" = "$esperado" ]; then
    echo "OMITIDO   $nombre (ya existe, SHA-1 verificado)"
    omitidos=$((omitidos + 1))
    continue
  fi

  echo "DESCARGA  $nombre"
  if ! curl -fsSL -o "$destino.tmp" "$BASE/$ruta"; then
    rm -f "$destino.tmp"
    echo "ERROR: fallo la descarga de $nombre" >&2
    exit 1
  fi
  real="$(shasum -a 1 "$destino.tmp" | cut -d' ' -f1)"
  if [ "$real" != "$esperado" ]; then
    rm -f "$destino.tmp"
    echo "ERROR: SHA-1 NO COINCIDE para $nombre" >&2
    echo "  publicado: $esperado" >&2
    echo "  descargado: $real" >&2
    exit 1
  fi
  mv "$destino.tmp" "$destino"
  echo "VERIFICADO $nombre (SHA-1 $real)"
  descargados=$((descargados + 1))
done

echo "Listo: $descargados descargado(s), $omitidos omitido(s). JAR en $LIB"
