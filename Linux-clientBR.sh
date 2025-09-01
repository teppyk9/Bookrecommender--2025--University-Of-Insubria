set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BIN="$SCRIPT_DIR/bin/Linux"
JAR="$BIN/clientBR.jar"

if [[ ! -f "$JAR" ]]; then
  echo "[ERR] JAR non trovato: $JAR" >&2
  exit 1
fi

cd "$BIN"
exec java -jar "$JAR"
