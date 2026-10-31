#!/bin/bash
# usage: cs_run.sh script out_dir   (runs the C# translation headlessly, like run.sh does for Java)
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
SRC="${J2CS_SRC:-$W/decompiled}"
rm -rf $2; mkdir -p $2
export NO_COLOR=1
timeout 600 "${PWSH:-pwsh}" -NoProfile -Command "\$a=[Reflection.Assembly]::LoadFrom('$W/build/HarnessCs.dll'); [void][HarnessCs.Program]::Main(@('$1','$2','$SRC','assets/j2me'))" > $2.out.txt 2>$2.err.txt
echo "exit: $?"
