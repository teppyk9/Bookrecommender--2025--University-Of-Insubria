#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BIN="$SCRIPT_DIR/bin"
DATA="$SCRIPT_DIR/data"
JAR="$BIN/DBCreator.jar"

if [[ ! -f "$JAR" ]]; then
  echo "[ERR] JAR non trovato: $JAR"
  read -r -p "Premi INVIO per chiudere..."
  exit 1
fi

cd "$BIN"
java -Dsql.dir="$DATA" -jar "$JAR"
RC=$?
echo
echo "[INFO] Uscita applicazione (codice $RC)."
read -r -p "Premi INVIO per chiudere..."
exit $RC
