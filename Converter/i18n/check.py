#!/usr/bin/env python3
"""Checks one language's files against SPEC.md: python3 Converter/i18n/check.py <code>"""
import os
import re
import sys

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'Assets', 'Loan12', 'Resources', 'Loan12Lang')
VOICES = ['hero_fire', 'hero_storm', 'hero_water', 'hero_phoenix', 'hero_queen', 'hero_dragon',
          'ally', 'mentor', 'warlord', 'lady', 'soldier', 'bandit', 'critter', 'trickster', 'brute']
EVENTS = {'start': 6, 'taunt': 8, 'combo': 5, 'hurt': 6, 'low': 5, 'heal': 3, 'block': 3, 'skill': 4,
          'item': 3, 'win': 6, 'lose': 5, 'revive': 3}
PH = re.compile(r'\{[0-9ps]\}|\\n')


def keys(path):
    out = []
    for line in open(path, encoding='utf-8').read().split('\n'):
        if not line or line.startswith('#'):
            continue
        out.append(line.split('\t'))
    return out


def check_table(name, code, problems):
    src = keys(os.path.join(ROOT, name + '.txt'))
    path = os.path.join(ROOT, '%s_%s.txt' % (name, code))
    if not os.path.exists(path):
        return False
    got = {}
    for n, row in enumerate(keys(path)):
        if len(row) != 2:
            problems.append('%s: row %d has %d columns (want key TAB text): %r' % (path, n + 1, len(row), row[0][:40]))
            continue
        if row[0] in got:
            problems.append('%s: duplicate key %r' % (path, row[0][:40]))
        got[row[0]] = row[1]
    for row in src:
        k = row[0]
        if k not in got:
            problems.append('%s: missing key %r' % (path, k[:60]))
            continue
        t = got[k]
        if not t.strip():
            problems.append('%s: empty translation for %r' % (path, k[:40]))
        ref = row[1] if len(row) > 1 and k.startswith('[') else k
        want = sorted(PH.findall(ref if k.startswith('[') else k))
        have = sorted(PH.findall(t))
        if [p for p in want if p != '\\n'] != [p for p in have if p != '\\n']:
            problems.append('%s: placeholders differ for %r: want %s, have %s' % (path, k[:40], want, have))
        if ' · ' in k and ' · ' not in t and not k.startswith('['):
            problems.append('%s: " · " separator lost in %r' % (path, k[:40]))
    extra = set(got) - set(r[0] for r in src)
    for k in sorted(extra):
        problems.append('%s: key not in the source (typo?) %r' % (path, k[:60]))
    return True


def check_banter(code, problems):
    path = os.path.join(ROOT, 'banter_%s.txt' % code)
    if not os.path.exists(path):
        return False
    sec = None
    lines = {}
    seen = set()
    for raw in open(path, encoding='utf-8').read().split('\n'):
        line = raw.strip()
        if not line or line.startswith('#'):
            continue
        m = re.match(r'^\[([a-z_]+)\.([a-z]+)\]$', line)
        if m:
            sec = (m.group(1), m.group(2))
            if m.group(1) not in VOICES or m.group(2) not in EVENTS:
                problems.append('%s: unknown section [%s.%s]' % (path, sec[0], sec[1]))
            lines.setdefault(sec, [])
            continue
        if sec is None:
            problems.append('%s: line outside a section: %r' % (path, line[:40]))
            continue
        if line in seen:
            problems.append('%s: duplicate line %r' % (path, line[:40]))
        seen.add(line)
        for ph in re.findall(r'\{[a-z]+\}', line):
            if ph not in ('{enemy}', '{me}', '{skill}') or (ph == '{skill}' and sec[1] != 'skill'):
                problems.append('%s: bad placeholder %s in [%s.%s]' % (path, ph, sec[0], sec[1]))
        limit = 26 if code in ('ja', 'ko', 'zh', 'th') else 64
        if len(line) > limit:
            problems.append('%s: too long (%d > %d) in [%s.%s]: %r' % (path, len(line), limit, sec[0], sec[1], line[:40]))
        lines[sec].append(line)
    for v in VOICES:
        for e, n in EVENTS.items():
            if e == 'revive' and not v.startswith('hero_'):
                continue
            have = len(lines.get((v, e), []))
            if have < n:
                problems.append('%s: [%s.%s] has %d lines, want %d' % (path, v, e, have, n))
    return True


def main():
    code = sys.argv[1]
    problems = []
    found = []
    for name in ('strings', 'story'):
        if check_table(name, code, problems):
            found.append(name)
    if check_banter(code, problems):
        found.append('banter')
    if not found:
        print('no files for', code)
        sys.exit(1)
    for p in problems[:80]:
        print(p)
    if problems:
        print('%d problem(s) (%s)' % (len(problems), ', '.join(found)))
        sys.exit(1)
    print('OK (%s)' % ', '.join(found))


main()
