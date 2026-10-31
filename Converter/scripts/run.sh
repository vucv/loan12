#!/bin/bash
# usage: run.sh script out_dir
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
SRC="${J2CS_SRC:-$W/decompiled}"
rm -rf "$2"; mkdir -p "$(dirname "$2")"
timeout 300 java -Djava.awt.headless=true -Dj2cs.src="$SRC" -Dj2cs.fonts=assets/j2me -cp midp/build:jbuild:harness/build Run $1 $2 > $2.out.txt 2>$2.err.txt
grep -v "Picked up" $2.out.txt
grep -v "^\s*at \|RecordStoreNotFound\|Picked up" $2.err.txt | head -20
python3 -I scripts/sheet.py $2 ${3:-5}
