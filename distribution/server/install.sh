#!/usr/bin/env sh
set -eu

MC_VERSION="${MC_VERSION:-1.21.11}"
FABRIC_LOADER_VERSION="${FABRIC_LOADER_VERSION:-0.18.2}"
FABRIC_API_VERSION="${FABRIC_API_VERSION:-0.139.4+1.21.11}"
INSTALL_DIR="${INSTALL_DIR:-.}"
META_URL="${META_URL:-https://meta.fabricmc.net/v2/versions/installer}"
FABRIC_MAVEN_BASE="${FABRIC_MAVEN_BASE:-https://maven.fabricmc.net}"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

detect_python() {
  if command -v python3 >/dev/null 2>&1; then
    echo "python3"
    return
  fi

  if command -v python >/dev/null 2>&1; then
    echo "python"
    return
  fi

  echo ""
}

require_command java
require_command curl

PYTHON_BIN="$(detect_python)"
if [ -z "$PYTHON_BIN" ]; then
  echo "Missing required command: python3 or python" >&2
  exit 1
fi

mkdir -p "$INSTALL_DIR/mods" "$INSTALL_DIR/config"

FABRIC_INSTALLER_VERSION="$(curl -fsSL "$META_URL" | "$PYTHON_BIN" -c 'import json,sys; data=json.load(sys.stdin); print(next((item["version"] for item in data if item.get("stable")), data[0]["version"]))')"
FABRIC_INSTALLER_JAR="fabric-installer-${FABRIC_INSTALLER_VERSION}.jar"
FABRIC_INSTALLER_URL="${FABRIC_MAVEN_BASE}/net/fabricmc/fabric-installer/${FABRIC_INSTALLER_VERSION}/${FABRIC_INSTALLER_JAR}"
FABRIC_API_JAR="fabric-api-${FABRIC_API_VERSION}.jar"
FABRIC_API_URL="${FABRIC_MAVEN_BASE}/net/fabricmc/fabric-api/fabric-api/${FABRIC_API_VERSION}/${FABRIC_API_JAR}"

echo "Downloading Fabric installer ${FABRIC_INSTALLER_VERSION}..."
curl -fL "$FABRIC_INSTALLER_URL" -o "$INSTALL_DIR/${FABRIC_INSTALLER_JAR}"

echo "Installing Fabric server for Minecraft ${MC_VERSION} with loader ${FABRIC_LOADER_VERSION}..."
java -jar "$INSTALL_DIR/${FABRIC_INSTALLER_JAR}" server -dir "$INSTALL_DIR" -mcversion "$MC_VERSION" -loader "$FABRIC_LOADER_VERSION" -downloadMinecraft

echo "Downloading Fabric API ${FABRIC_API_VERSION}..."
curl -fL "$FABRIC_API_URL" -o "$INSTALL_DIR/mods/${FABRIC_API_JAR}"

echo "Installation complete."
echo "If needed, place square-biomes-${MC_VERSION}.jar or square-biomes-1.0.0.jar into $INSTALL_DIR/mods/."
