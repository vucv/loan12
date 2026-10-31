#!/bin/bash
# translate MIDP layer + game to C# (output: cs/)
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
X="--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
rm -rf cs/J2ME cs/Game && mkdir -p cs
java $X -cp tools/j2cs J2CS cs $(find midp/src jsrc -name "*.java" | sort) 2>&1 | grep -v "Picked up"
