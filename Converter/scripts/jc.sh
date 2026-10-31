#!/bin/bash
# compile game java against the MIDP layer; print errors
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
rm -rf jbuild; mkdir -p jbuild
javac -J-Dstdout.encoding=UTF-8 -J-Dstderr.encoding=UTF-8 -Xmaxerrs 1000 -nowarn -encoding UTF-8 -source 8 -target 8 -cp midp/build -d jbuild $(find jsrc -name "*.java") 2>&1 | grep -v "Picked up\|warning: \[options\]\|^Note:" > scripts/javac.txt
echo "errors: $(grep -c 'error:' scripts/javac.txt)"
