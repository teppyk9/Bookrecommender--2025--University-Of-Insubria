set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BIN="$SCRIPT_DIR/bin"
DATA="$SCRIPT_DIR/data"
JAR="$BIN/DBCreator.jar"

if [[ ! -f "$JAR" ]]; then
  echo "[ERR] JAR non trovato: $JAR" >&2
  exit 1
fi

cd "$BIN"
exec java -Dsql.dir="$DATA" -jar "$JAR"
