#!/usr/bin/env python3
"""Compares raw ARGB frames of two harness runs (Java vs C#)."""
import sys, os, struct
a, b = sys.argv[1], sys.argv[2]
names = sorted(f for f in os.listdir(a) if f.endswith('.raw'))
bad = 0
for n in names:
    pa, pb = os.path.join(a, n), os.path.join(b, n)
    if not os.path.exists(pb):
        print('%-28s MISSING in %s' % (n, b)); bad += 1; continue
    da, db = open(pa, 'rb').read(), open(pb, 'rb').read()
    if da == db:
        print('%-28s identical' % n); continue
    ia = struct.unpack('<%di' % (len(da) // 4), da); ib = struct.unpack('<%di' % (len(db) // 4), db)
    diff = [i for i in range(min(len(ia), len(ib))) if (ia[i] & 0xffffff) != (ib[i] & 0xffffff)]
    if not diff:
        print('%-28s identical (alpha only)' % n); continue
    bad += 1
    ys = [i // 240 for i in diff]; xs = [i % 240 for i in diff]
    print('%-28s %6d px differ, box x %d..%d y %d..%d' % (n, len(diff), min(xs), max(xs), min(ys), max(ys)))
print('frames: %d, differing: %d' % (len(names), bad))
