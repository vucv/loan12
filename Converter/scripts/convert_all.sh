#!/bin/bash
# Full pipeline: decompiled Java (J2CS_SRC) -> repaired Java (jsrc/) -> C# (cs/) -> Unity project.
#   J2CS_SRC  the decompiled sources: branch `backup` of github.com/vucv/loan12 (default: ./decompiled)
#   PWSH      PowerShell 7 executable used for the Roslyn compile check (optional, default: pwsh)
#   argument  Unity project folder to update (default: the folder containing Converter/, else ./unity)
W="$(cd "$(dirname "$0")/.." && pwd)"
cd "$W"
fail() { echo "convert_all: $1" >&2; exit 1; }

scripts/build_tools.sh
[ -f tools/j2cs/J2CS.class ] && [ -f tools/build/ScopeFix.class ] || fail "tools did not compile"
scripts/prepare_java.sh || fail "prepare_java failed"
scripts/build_java.sh
[ "$(grep -c 'error:' scripts/javac.txt)" = "0" ] || fail "game Java does not compile (see scripts/javac.txt)"
scripts/j2cs.sh | tee scripts/j2cs.log
grep -q "^problems: 0" scripts/j2cs.log || fail "translator reported problems (see scripts/j2cs.log)"
if command -v "${PWSH:-pwsh}" > /dev/null 2>&1; then
  scripts/cs_build.sh | tee scripts/cs_build.log
  grep -q "errors: 0 " scripts/cs_build.log || fail "C# does not compile (see scripts/cs_build.log)"
else
  echo "(PowerShell not found: skipping the C# compile check)"
fi
python3 scripts/make_unity_project.py "$@" || fail "make_unity_project failed"
echo "convert_all: done"
