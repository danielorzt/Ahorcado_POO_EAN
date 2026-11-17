#!/usr/bin/env python3
"""Rellena los numeros de pagina de la tabla de contenido del manual (dos pasadas).

Imprime el HTML con Chrome, ubica cada encabezado con pdftotext y escribe el numero
de pagina en Manual_de_Usuario.html. Uso: python3 fuentes_manual/paginar_toc.py
Requiere Google Chrome/Chromium y pdftotext (poppler). Solo hace falta si cambia el contenido.
"""
import html, re, subprocess, sys, tempfile, unicodedata
from pathlib import Path

AQUI = Path(__file__).resolve().parent
HTML = AQUI / "Manual_de_Usuario.html"
CHROMES = ["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "google-chrome", "chromium", "chromium-browser"]


def norm(s):
    s = html.unescape(re.sub(r"<[^>]+>", "", s))
    return re.sub(r"\s+", " ", unicodedata.normalize("NFC", s)).strip().lower()


def chrome():
    for c in CHROMES:
        if Path(c).exists() or subprocess.run(["which", c], capture_output=True).returncode == 0:
            return c
    sys.exit("No se encontro Chrome/Chromium")


def imprimir(src: Path, pdf: Path):
    subprocess.run([chrome(), "--headless=new", "--disable-gpu", "--no-pdf-header-footer",
                    f"--print-to-pdf={pdf}", f"file://{src}"], capture_output=True, check=True)


def main():
    t = HTML.read_text(encoding="utf-8")
    tmp = AQUI / ".paginar_tmp.html"
    pdf = Path(tempfile.mkdtemp()) / "m.pdf"
    try:
        tmp.write_text(t, encoding="utf-8")
        imprimir(tmp, pdf)
    finally:
        tmp.unlink(missing_ok=True)
    n = int(re.search(r"Pages:\s+(\d+)", subprocess.run(["pdfinfo", str(pdf)], capture_output=True, text=True).stdout).group(1))
    paginas = []
    for i in range(1, n + 1):
        txt = subprocess.run(["pdftotext", "-f", str(i), "-l", str(i), "-layout", str(pdf), "-"], capture_output=True, text=True).stdout
        paginas.append(norm(txt))
    # encabezados en orden de documento (despues de la tabla de contenido)
    cuerpo = t.split("</ul>\n</section>", 1)[1]
    encs = [(m.group(1) or m.group(3), norm(m.group(2) or m.group(4)))
            for m in re.finditer(r'<section id="([^"]+)"[^>]*>\s*<h1>(.*?)</h1>|<h2 id="([^"]+)">(.*?)</h2>', cuerpo, flags=re.S)]
    cur, res = 3, {}
    for ident, texto in encs:
        for p in range(cur, n + 1):
            if texto in paginas[p - 1]:
                res[ident] = p; cur = p; break
        else:
            sys.exit(f"no se hallo el encabezado: {texto}")
    for ident, p in res.items():
        t = re.sub(rf'(data-for="{re.escape(ident)}">)\d+', rf"\g<1>{p}", t)
    HTML.write_text(t, encoding="utf-8")
    print(f"{len(res)} encabezados paginados; PDF de prueba con {n} paginas")


if __name__ == "__main__":
    main()
