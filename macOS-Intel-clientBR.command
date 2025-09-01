#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BIN="$SCRIPT_DIR/bin/Mac-Intel"
JAR="$BIN/clientBR.jar"

if [[ ! -f "$JAR" ]]; then
  echo "[ERR] JAR non trovato: $JAR"
  read -r -p "Premi INVIO per chiudere..."
  exit 1
fi

cd "$BIN"
java -jar "$JAR"
RC=$?
echo
echo "[INFO] Uscita applicazione (codice $RC)."
read -r -p "Premi INVIO per chiudere..."
exit $RC