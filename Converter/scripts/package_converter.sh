#!/bin/bash
# Copies the converter (sources, tools, tests, converted assets) to <dest> (default: unity/Converter).
set -e
W="$(cd "$(dirname "$0")/.." && pwd)"
D="${1:-$W/unity/Converter}"
rm -rf "$D"; mkdir -p "$D"/{scripts,tools/cs,harness/scripts,assets,cs}
cd "$W"
cp scripts/{build_tools.sh,prepare_java.sh,fix_syntax.py,fix_manual.py,port_patches.py,build_java.sh,jc.sh,j2cs.sh,cs_build.sh,cs_run.sh,run.sh,cmp_frames.py,sheet.py,make_unity_project.py,convert_all.sh,package_converter.sh,mg2png.py,make_fonts.py,encode_audio.sh,MidiRender.java} "$D/scripts/"
cp tools/{J2CS.java,ScopeFix.java,ObscureFix.java,OverloadCheck.java} "$D/tools/"
cp -r tools/cs/cscheck.ps1 tools/cs/UnityStubs "$D/tools/cs/"
cp -r midp "$D/" && rm -rf "$D/midp/build"
cp -r harness/src harness/cs harness/test "$D/harness/"
cp runs/s?.txt "$D/harness/scripts/"
printf 'monkey 1 3000 100\n' > "$D/harness/scripts/monkey1.txt"
printf 'monkey 15 15000 100 win\n' > "$D/harness/scripts/bot15.txt"
cp -r assets/j2me assets/ogg assets/png "$D/assets/"
cp -r cs/Runtime "$D/cs/"
cp -r jsrc "$D/"
cp "$W/CONVERTER_README.md" "$D/README.md"
echo "converter packaged in $D"
