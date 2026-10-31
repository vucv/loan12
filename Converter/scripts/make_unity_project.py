#!/usr/bin/env python3
"""
Assembles the Unity project from the converter outputs:

  cs/J2ME     (generated MIDP emulation layer)  -> Assets/Loan12/J2ME/MIDP
  cs/Runtime  (hand-written java.* shims)       -> Assets/Loan12/J2ME/Java
  cs/Game     (generated game code)             -> Assets/Loan12/Game
  JAR resources (decompiled repo)               -> Assets/Loan12/Resources/J2ME/<path, '.' -> '_'>.bytes
  bitmap fonts of the MIDP layer                -> Assets/Loan12/Resources/J2ME/j2me/font_N_bin.bytes
  converted audio (OGG)                         -> Assets/Loan12/Resources/J2ME/clips/<name>.ogg

The Unity glue (Assets/Loan12/Unity) and editor tools (Assets/Loan12/Editor) are hand-written and are
left untouched. Generated folders are replaced completely.

usage: make_unity_project.py [unity_project_dir]   (environment: J2CS_WORK, J2CS_SRC)
"""
import json
import os
import shutil
import sys

WORK = os.environ.get('J2CS_WORK', os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SRC = os.environ.get('J2CS_SRC', os.path.join(WORK, 'decompiled'))
if len(sys.argv) > 1:
    UNITY = sys.argv[1]
elif os.path.isdir(os.path.join(os.path.dirname(WORK), 'Assets')):
    UNITY = os.path.dirname(WORK)  # Converter/ inside the Unity project
else:
    UNITY = os.path.join(WORK, 'unity')
ASSETS = os.path.join(UNITY, 'Assets', 'Loan12')

SKIP_DIRS = {'.git', '.idea', 'META-INF', 'com'}
SKIP_EXT = {'.java', '.class', '.iml', '.ipr', '.iws'}


def replace_dir(dst):
    if os.path.isdir(dst):
        shutil.rmtree(dst)
    os.makedirs(dst)


def copy_cs(src, dst):
    replace_dir(dst)
    n = 0
    for f in sorted(os.listdir(src)):
        if f.endswith('.cs'):
            shutil.copy2(os.path.join(src, f), os.path.join(dst, f))
            n += 1
    return n


def asmdef(path, name, refs, no_engine=False, editor_only=False):
    data = {
        "name": name,
        "rootNamespace": "",
        "references": refs,
        "includePlatforms": ["Editor"] if editor_only else [],
        "excludePlatforms": [],
        "allowUnsafeCode": False,
        "overrideReferences": False,
        "precompiledReferences": [],
        "autoReferenced": True,
        "defineConstraints": [],
        "versionDefines": [],
        "noEngineReferences": no_engine,
    }
    with open(path, 'w', encoding='utf-8') as fh:
        json.dump(data, fh, indent=4)
        fh.write('\n')


def main():
    os.makedirs(ASSETS, exist_ok=True)
    n_midp = copy_cs(os.path.join(WORK, 'cs', 'J2ME'), os.path.join(ASSETS, 'J2ME', 'MIDP'))
    n_java = copy_cs(os.path.join(WORK, 'cs', 'Runtime'), os.path.join(ASSETS, 'J2ME', 'Java'))
    n_game = copy_cs(os.path.join(WORK, 'cs', 'Game'), os.path.join(ASSETS, 'Game'))
    asmdef(os.path.join(ASSETS, 'J2ME', 'Loan12.J2ME.asmdef'), 'Loan12.J2ME', [], no_engine=True)
    asmdef(os.path.join(ASSETS, 'Game', 'Loan12.Game.asmdef'), 'Loan12.Game', ['Loan12.J2ME'], no_engine=True)
    asmdef(os.path.join(ASSETS, 'Unity', 'Loan12.Unity.asmdef'), 'Loan12.Unity', ['Loan12.J2ME', 'Loan12.Game'])
    asmdef(os.path.join(ASSETS, 'Editor', 'Loan12.Editor.asmdef'), 'Loan12.Editor',
           ['Loan12.J2ME', 'Loan12.Game', 'Loan12.Unity'], editor_only=True)

    res = os.path.join(ASSETS, 'Resources', 'J2ME')
    replace_dir(res)
    n_res = 0
    for dirpath, dirnames, files in os.walk(SRC):
        rel_dir = os.path.relpath(dirpath, SRC)
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for f in files:
            if os.path.splitext(f)[1].lower() in SKIP_EXT:
                continue
            rel = os.path.normpath(os.path.join(rel_dir, f)).replace('\\', '/')
            if rel.startswith('./'):
                rel = rel[2:]
            dst = os.path.join(res, rel.replace('.', '_') + '.bytes')
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copy2(os.path.join(dirpath, f), dst)
            n_res += 1
    fonts = os.path.join(WORK, 'assets', 'j2me')
    os.makedirs(os.path.join(res, 'j2me'), exist_ok=True)
    for f in sorted(os.listdir(fonts)):
        shutil.copy2(os.path.join(fonts, f), os.path.join(res, 'j2me', f.replace('.', '_') + '.bytes'))
        n_res += 1
    clips = os.path.join(res, 'clips')
    os.makedirs(clips)
    n_ogg = 0
    for f in sorted(os.listdir(os.path.join(WORK, 'assets', 'ogg'))):
        if f.endswith('.ogg'):
            shutil.copy2(os.path.join(WORK, 'assets', 'ogg', f), os.path.join(clips, f))
            n_ogg += 1
    print('unity project: %s' % UNITY)
    print('  MIDP layer %d files, java shims %d, game %d, resources %d, audio clips %d'
          % (n_midp, n_java, n_game, n_res, n_ogg))


if __name__ == '__main__':
    main()
