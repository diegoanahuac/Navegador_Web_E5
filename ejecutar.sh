#!/usr/bin/env bash
# Compila y ejecuta el navegador. Uso:
#   ./ejecutar.sh             -> interfaz gráfica
#   ./ejecutar.sh --consola   -> versión de consola
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/navegador/*.java src/navegador/*/*.java
java -cp out navegador.Main "$@"
