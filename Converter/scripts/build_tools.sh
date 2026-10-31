#!/bin/bash
# Compiles the converter tools (javac-based repair tools, J2CS translator) and the Java MIDP layer.
set -e
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
X="--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
rm -rf tools/build tools/j2cs && mkdir -p tools/build tools/j2cs
javac -nowarn -d tools/build tools/ScopeFix.java tools/ObscureFix.java tools/OverloadCheck.java 2>&1 | grep -v "Picked up" || true
javac $X -nowarn -d tools/j2cs tools/J2CS.java 2>&1 | grep -v "Picked up" || true
rm -rf midp/build && mkdir -p midp/build
javac -nowarn -encoding UTF-8 -source 8 -target 8 -d midp/build $(find midp/src -name "*.java") 2>&1 | grep -v "Picked up\|warning: \[options\]\|^Note:\|^[0-9]* warning" || true
echo "build_tools: done"
