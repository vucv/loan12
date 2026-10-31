#!/bin/bash
# compiles MIDP layer + game + harness
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
rm -rf midp/build && mkdir -p midp/build
javac -nowarn -encoding UTF-8 -source 8 -target 8 -d midp/build $(find midp/src -name "*.java") 2>&1 | grep -v "Picked up\|warning: \[options\]\|^Note:\|^[0-9]* warning"
scripts/jc.sh
javac -nowarn -encoding UTF-8 -cp midp/build:jbuild -d harness/build harness/src/*.java 2>&1 | grep -v "Picked up"
