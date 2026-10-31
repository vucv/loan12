#!/usr/bin/env python3
"""Repairs JD-Core output that does not even parse (garbled expressions / array initialisers)."""
import os
import re

ROOT = os.path.join(os.environ.get('J2CS_WORK') or os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'jsrc') + os.sep


def rd(p):
    return open(ROOT + p, encoding='utf-8').read()


def wr(p, s):
    open(ROOT + p, 'w', encoding='utf-8').write(s)


def must_replace(s, old, new, p):
    if old not in s:
        raise SystemExit('fix_syntax: pattern not found in %s: %r' % (p, old[:60]))
    return s.replace(old, new)


# MD5 round with a missing operand
p = 'game/b.java'
wr(p, must_replace(rd(p), "i = ( << 7 | i >>> 25)+j;", "i = (i << 7 | i >>> 25) + j;", p))

# chained assignment `f[0] = f[6] = e[6] = e[0]` printed as `tmp = (e[6] = = e[0])`
p = 'game/bs.java'
wr(p, must_replace(rd(p), """        Image tmp370_369 = (this.e[6] = = this.e[0]);
        this.f[6] = tmp370_369;
        this.f[0] = tmp370_369;""", """        // PORT-FIX: decompiler garbled `f[0] = f[6] = e[6] = e[0]`
        this.e[6] = this.e[0];
        this.f[6] = this.e[0];
        this.f[0] = this.e[0];""", p))

# board border value lost ("=.;"): row 0 mirrors row 11
p = 'game/cq.java'
wr(p, must_replace(rd(p), "            this.e[i][j] =.;",
                   "            this.e[i][j] = 8; // PORT-FIX: was garbled \"=.;\" (row 0 mirrors row 11)", p))

# `t[k] = {..}` element stores into a `new int[10][4]` table (missing trailing zeros restored)
for p in ['game/dc.java', 'com/mic/smsgame/suquan/m.java']:
    def row(m):
        vals = [v.strip() for v in m.group(2).split(',')]
        vals += ['0'] * (4 - len(vals))
        return m.group(1) + 'new int[]{' + ', '.join(vals) + '};'
    s = rd(p)
    s2 = re.sub(r'(this\.t\[\d\] = )\{([^}]*)\};', row, s)
    if s2 == s:
        raise SystemExit('fix_syntax: no t rows in ' + p)
    wr(p, s2)


def nulls_at_depth1(t):
    """bare 0 elements at depth 1 of an array initialiser -> null"""
    out = []
    depth = 0
    k = 0
    instr = False
    while k < len(t):
        c = t[k]
        if instr:
            out.append(c)
            if c == '\\':
                out.append(t[k + 1])
                k += 2
                continue
            if c == '"':
                instr = False
            k += 1
            continue
        if c == '"':
            instr = True
        elif c == '{':
            depth += 1
        elif c == '}':
            depth -= 1
        if c == '0' and depth == 1 and not t[k - 1].isalnum() and not t[k + 1].isalnum():
            out.append('null')
            k += 1
            continue
        out.append(c)
        k += 1
    return ''.join(out)


def count_top(t):
    body = t[t.index('{') + 1:t.rindex('}')]
    depth = 0
    cnt = 1
    instr = False
    i = 0
    while i < len(body):
        c = body[i]
        if instr:
            if c == '\\':
                i += 1
            elif c == '"':
                instr = False
        else:
            if c == '"':
                instr = True
            elif c == '{':
                depth += 1
            elif c == '}':
                depth -= 1
            elif c == ',' and depth == 0:
                cnt += 1
        i += 1
    return cnt


def pad(t, n):
    c = count_top(t)
    if c > n:
        raise SystemExit('pad: already %d > %d' % (c, n))
    return t[:t.rindex('}')] + ''.join([', null'] * (n - c)) + t[t.rindex('}'):]


# dd.m: per-stage unlock lists (46 stages like table l). JD-Core printed null as 0, tail garbled "00040}"
p = 'game/dd.java'
s = rd(p)
i = s.index('private static final int[][] m = {')
j = s.index(';', i) + 1
t = s[i:j].replace('00040}}', '0, 0, 0, {40}}')
t = nulls_at_depth1(t[:-1]) + ';'
t = pad(t[:-1], 46) + ';'
s = s[:i] + ('// PORT-FIX: JD-Core printed null as 0 and garbled the tail ("00040"); '
             'restored to 46 entries like table l\n    ') + t + s[j:]
wr(p, s)

# df.d: map focus per intro-text line (108 lines in suquan.h.h); tail "00000170, 295}" garbled
p = 'game/df.java'
s = rd(p)
i = s.index('private static final int[][] d = {')
j = s.index('};', i) + 2
t = s[i:j].replace('00000170, 295}\n};', '0, 0, 0, 0, 0, {170, 295}};')
t = nulls_at_depth1(t[:-1]) + ';'
t = pad(t[:-1], 108) + ';'
s = s[:i] + ('// PORT-FIX: JD-Core printed null as 0, garbled the tail ("00000170") and dropped trailing nulls;\n'
             '    // the table is indexed by the intro-text line (108 lines in suquan.h.h).\n    ') + t + s[j:]
wr(p, s)

# ea: dialog scripts. `0` printed for null, runs like `000"40"` lost their separators.
p = 'game/ea.java'
s = rd(p)
s = re.sub(r'(?<=[{,\s])(0+)"', lambda m: ', '.join(['null'] * len(m.group(1))) + ', {{"', s)
out = []
i = 0
n = len(s)
while i < n:
    c = s[i]
    if c == '"':
        j = i + 1
        while s[j] != '"':
            if s[j] == '\\':
                j += 1
            j += 1
        out.append(s[i:j + 1])
        i = j + 1
        continue
    if s.startswith('/*', i):
        j = s.index('*/', i) + 2
        out.append(s[i:j])
        i = j
        continue
    if c == '0' and not (s[i - 1].isalnum() or s[i - 1] == '_') and not (s[i + 1].isalnum() or s[i + 1] == '_'):
        out.append('null')
        i += 1
        continue
    out.append(c)
    i += 1
s = ''.join(out)
for name, target in [('b', 46), ('c', 46), ('d', 46), ('e', 8), ('f', 8), ('g', 8)]:
    m = re.search(r'String\[\]\[\]\[\]\s*%s\s*=\s*\{' % name, s)
    a = m.end() - 1
    depth = 0
    k = a
    instr = False
    while True:
        c = s[k]
        if instr:
            if c == '\\':
                k += 1
            elif c == '"':
                instr = False
        else:
            if c == '"':
                instr = True
            elif c == '{':
                depth += 1
            elif c == '}':
                depth -= 1
                if depth == 0:
                    break
        k += 1
    arr = pad(s[a:k + 1], target)
    s = s[:a] + arr + s[k + 1:]
s = s.replace('public class ea {', '// PORT-FIX: JD-Core printed null as 0 and garbled runs like `000"40"`; restored nulls and\n'
              '// padded the per-stage dialog tables with trailing nulls (46 stages, 8 side quests).\npublic class ea {', 1)
wr(p, s)

# ------------------------------------------------------------------------------------------------
# JD-Core 0.7 drops TRAILING default values (0 / null) of array initialisers (it sizes the literal by
# the last stored element and ignores the `newarray` length). Restored where usage proves the size.
def edit(p, old, new):
    s = rd(p)
    if old not in s:
        raise SystemExit('fix_syntax: pattern not found in %s: %r' % (p, old[:60]))
    wr(p, s.replace(old, new, 1))

# separators lost between trailing elements: "03" = "0, 3", "04" = "0, 4", "0 - 1" = "0, -1"
edit('game/ah.java', 'int[] e = {1, 3, 5, 4, 0, 2, 4, 03};', 'int[] e = {1, 3, 5, 4, 0, 2, 4, 0, 3}; // PORT-FIX: "03" was "0, 3" (9 entries like f)')
edit('game/w.java', '0, 5, 04};', '0, 5, 0, 4}; // PORT-FIX: "04" was "0, 4"')
edit('game/bu.java', 'private int[] M = {-1, -1, 0, 1, 1, 1, 0 - 1};', 'private int[] M = {-1, -1, 0, 1, 1, 1, 0, -1}; // PORT-FIX: "0 - 1" was "0, -1" (8 directions like N)')
edit('com/mic/smsgame/suquan/c.java', 'private int[] d = {-2, -6, 0, -3, 0, -2, 0 - 1};', 'private int[] d = {-2, -6, 0, -3, 0, -2, 0, -1}; // PORT-FIX: "0 - 1" was "0, -1"')

# sine tables: sin(180) = 0 dropped (be.a(int) reads c[180])
edit('game/be.java', ', 1712, 1427, 1142, 857, 571, 285};', ', 1712, 1427, 1142, 857, 571, 285, 0}; // PORT-FIX: trailing 0 restored (index 180)')
edit('game/be.java', '2845, 1427};', '2845, 1427, 0}; // PORT-FIX: trailing 0 restored (37 entries like b)')
s = rd('game/m.java'); i = s.index('public static final int[] a = {'); j = s.index('};', i)
wr('game/m.java', s[:j] + ', 0' + s[j:])

# map: 41 nodes (27 named towns + unnamed waypoints) - names list lost its trailing nulls,
# node rows lost a trailing 0 flag, node 13's 4th neighbour edge (edge 0) was dropped
s = rd('game/bq.java')
i = s.index('public static final String[] a = {'); j = s.index('};', i)
s = s[:j] + ''.join([', null'] * 14) + s[j:]
i = s.index('public static final int[][] b = {'); j = s.index('};', i)
seg = re.sub(r'\{(-?\d+), (-?\d+)\}', r'{\1, \2, 0}', s[i:j])
s = s[:i] + seg + s[j:]
s = s.replace('{37, 36, 37, 36}, {-1, -1, -1}, {-1, -1, 15, -1}', '{37, 36, 37, 36}, {-1, -1, -1, 0}, {-1, -1, 15, -1}')
s = s.replace('public class bq {', '// PORT-FIX: trailing zeros / nulls dropped by JD-Core restored (41 map nodes, rows of 3 / 4).\npublic class bq {')
wr('game/bq.java', s)

# blur kernel rows 0 and 4 lost their last 0
edit('game/y.java', '{{0, 1, 2, 1}, {1, 3, 5, 3, 1}, {2, 5, 9, 5, 2}, {1, 3, 5, 3, 1}, {0, 1, 2, 1}}',
     '{{0, 1, 2, 1, 0}, {1, 3, 5, 3, 1}, {2, 5, 9, 5, 2}, {1, 3, 5, 3, 1}, {0, 1, 2, 1, 0}} /* PORT-FIX: trailing 0s */')

# font glyph x positions: last glyph sits at x = 0 (126 glyphs like c..f)
s = rd('game/dh.java'); i = s.index('this.b = new int[]{'); j = s.index('};', i)
wr('game/dh.java', s[:j] + ', 0' + s[j:])

# lightning sprite frame table: frame 4 (x = 0, anchor 0) lost from y / A
edit('game/au.java', 'private static int[] y = {19, 20, 17, 12};', 'private static int[] y = {19, 20, 17, 12, 0}; // PORT-FIX: trailing 0 restored')
edit('game/au.java', 'private static int[] A = {91, 74, 56, 1};', 'private static int[] A = {91, 74, 56, 1, 0}; // PORT-FIX: trailing 0 restored')

# enemy table: rows need >= 3 entries (b() allocates length-3 skills); level 3/7/1 rows lost their
# trailing zeros - restored as one skill (#0), following the arena progression (lv3 #0, lv6 #3, lv9 #6)
s = rd('game/dd.java')
for old in ['        {7},', '        {3},', '        {1},']:
    if old not in s:
        raise SystemExit('dd.o row missing ' + old)
    s = s.replace(old, old[:-2] + ', 0, 0, 0}, // PORT-FIX: trailing zeros restored', 1)
wr('game/dd.java', s)

# MD5 padding block: {0x80, 0 x 63} - the 63 zeros were dropped, so the digest was never computed
edit('game/b.java', 'static byte[] c = {-128};', 'static byte[] c = new byte[64]; // PORT-FIX: was {-128} (63 trailing zeros dropped by JD-Core)\n\n    static {\n        c[0] = -128;\n    }')

print('fix_syntax: ok')
