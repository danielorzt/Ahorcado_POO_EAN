#!/usr/bin/env bash
# Genera Manual_de_Usuario.pdf a partir de fuentes_manual/Manual_de_Usuario.html.
# Uso: ./generar_manual.sh [URL_DE_YOUTUBE] [ruta_salida.pdf]
# El enlace del video ya esta en el HTML. Si se pasa una URL y el HTML contiene el marcador
# [PEGAR ENLACE AQUÍ], el marcador se reemplaza por esa URL; si no hay marcador, no se cambia nada.
# Por defecto escribe documentacion/Manual_de_Usuario.pdf
set -euo pipefail

URL="${1:-}"
AQUI="$(cd "$(dirname "$0")" && pwd)"
SALIDA="${2:-$AQUI/Manual_de_Usuario.pdf}"
MARCADOR='[PEGAR ENLACE AQUÍ]'

# Buscar Chrome/Chromium
CHROME=""
for c in "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" \
         "/Applications/Chromium.app/Contents/MacOS/Chromium" \
         google-chrome google-chrome-stable chromium chromium-browser; do
  if [ -x "$c" ] || command -v "$c" >/dev/null 2>&1; then CHROME="$c"; break; fi
done
if [ -z "$CHROME" ]; then
  echo "No se encontró Google Chrome ni Chromium." >&2
  exit 2
fi

# Copia temporal dentro de fuentes_manual para que las rutas ../capturas sigan funcionando
TMP="$AQUI/fuentes_manual/.manual_tmp_$$.html"
trap 'rm -f "$TMP"' EXIT

# Escapar la URL para HTML y construir el enlace
URL_HTML="${URL//&/&amp;}"
URL_HTML="${URL_HTML//\"/&quot;}"
URL_HTML="${URL_HTML//</&lt;}"
URL_HTML="${URL_HTML//>/&gt;}"
ENLACE="<a href=\"$URL_HTML\">$URL_HTML</a>"

CONTENIDO="$(<"$AQUI/fuentes_manual/Manual_de_Usuario.html")"
if [ -z "$URL" ]; then
  ENLACE="$MARCADOR" # sin URL: el contenido queda tal cual
fi
# Reemplazo por bucle (evita problemas con "&" en la sustitución de Bash 5.2+)
RESULTADO=""
while [[ "$CONTENIDO" == *"$MARCADOR"* ]]; do
  RESULTADO+="${CONTENIDO%%"$MARCADOR"*}$ENLACE"
  CONTENIDO="${CONTENIDO#*"$MARCADOR"}"
done
printf '%s\n' "$RESULTADO$CONTENIDO" > "$TMP"

"$CHROME" --headless=new --disable-gpu --no-pdf-header-footer \
  --print-to-pdf="$SALIDA" "file://$TMP" >/dev/null 2>&1

echo "PDF generado: $SALIDA"
