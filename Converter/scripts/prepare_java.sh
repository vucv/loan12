#!/bin/bash
# Rebuilds the cleaned Java tree (jsrc/) from the decompiled repository, step by step.
set -e
W="$(cd "$(dirname "$0")/.." && pwd)"
SRC="${J2CS_SRC:-$W/decompiled}"   # branch `backup` of github.com/vucv/loan12 (decompiled JAR)
cd "$W"
[ -d "$SRC/com" ] || { echo "decompiled sources not found in $SRC (set J2CS_SRC)"; exit 1; }
rm -rf jsrc
mkdir -p jsrc/game
cp "$SRC"/*.java jsrc/game/
cp -r "$SRC/com" jsrc/

# 1. default-package classes -> package `game` (javac cannot import from the unnamed package)
for f in jsrc/game/*.java; do sed -i '1i package game;' "$f"; done
for f in $(find jsrc/com -name "*.java"); do
  python3 -I - "$f" <<'EOF'
import re,sys
p=sys.argv[1]; s=open(p,encoding='utf-8').read()
s=re.sub(r'^import ([a-z][a-z0-9]*);$', lambda m: 'import game.'+m.group(1)+';', s, flags=re.M)
open(p,'w',encoding='utf-8').write(s)
EOF
done

# 2. class `do` is a Java keyword -> `do_`
mv jsrc/game/do.java jsrc/game/do_.java
sed -i 's/public class do$/public class do_/; s/public do(Image/public do_(Image/' jsrc/game/do_.java
sed -i 's/(( do)localab)/((do_) localab)/g; s/new do (bs/new do_(bs/g' jsrc/game/i.java

# 3. resource loading goes through the port runtime (keeps the resource path for the audio backend)
grep -rl 'getClass().getResourceAsStream' jsrc | xargs sed -i 's/"".getClass().getResourceAsStream(/j2me.Res.open(/g'

# 4. decompiler repairs
python3 -I scripts/fix_syntax.py
java -cp tools/build ScopeFix $(find jsrc -name "*.java") | grep -v "^Picked up" > scripts/scopefix.log
java -cp tools/build ObscureFix --apply -cp midp/build $(find jsrc -name "*.java") 2>/dev/null | grep -v "^Picked up" > scripts/obscurefix.log
python3 -I scripts/fix_manual.py

# 5. port changes (threads -> scheduler, SMS, URLs)
if [ -f scripts/port_patches.py ]; then python3 -I scripts/port_patches.py; fi
# 6. touch screens drawn by Unity (bridge classes in port/)
if [ -f scripts/port_screens.py ]; then python3 -I scripts/port_screens.py; fi
echo "prepare_java: done"
