# Repositorio

El propósito de este repositorio es demostrar cómo se puede convertir un MSG a PDF, extraer sus archivos adjuntados y convertirlos a PDF antes de volver a adjuntarlos.

## Requisitos previos

1. **Recursos del SDK** — El repositorio de PDFNet SDK (paso 3 de [Apryse Downloads page](https://dev.apryse.com/)) contiene una carpeta `Resources/`. Cópiala a `demo/Resources/`.

2. **Módulos en `demo/lib/`** — Se necesitan dos módulos:
   - [HTML2PDF](https://docs.apryse.com/core/guides/info/modules#html2pdf-module)
   - [Structured Output](https://dev.apryse.com/)

3. **Clave de licencia** — Crea `demo/.env` con la clave que se encuentra en [Apryse Downloads page](https://dev.apryse.com/):

PDFTRON_TOKEN=tu_clave_aquí


## Uso

```bash
cd demo
.\run.bat

