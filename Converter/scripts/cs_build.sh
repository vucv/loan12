#!/bin/bash
# compile translated game + runtime + C# harness into build/HarnessCs.dll
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
export NO_COLOR=1
mkdir -p build
"${PWSH:-pwsh}" -NoProfile -File tools/cs/cscheck.ps1 -Out build/HarnessCs.dll -Dirs cs/J2ME,cs/Game,cs/Runtime,harness/cs "$@" 2>&1 | sed "s|$W/||"
