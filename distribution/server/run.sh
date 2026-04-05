#!/usr/bin/env sh
set -eu

JAVA_BIN="${JAVA_BIN:-java}"
JAVA_FLAGS="${JAVA_FLAGS:--Xms2G -Xmx2G}"

exec "$JAVA_BIN" $JAVA_FLAGS -jar fabric-server-launch.jar nogui
