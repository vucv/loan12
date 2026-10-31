import os, sys, struct
from PIL import Image
src, dst = sys.argv[1], sys.argv[2]
ok = bad = 0
for root, _, files in os.walk(src):
    if '.git' in root: continue
    for fn in files:
        if not fn.endswith('.mg'): continue
        p = os.path.join(root, fn)
        b = open(p, 'rb').read()
        n = struct.unpack('>I', b[:4])[0]
        png = b'\x89PNG' + b[4:n]
        rel = os.path.relpath(p, src)[:-3] + '.png'
        out = os.path.join(dst, rel)
        os.makedirs(os.path.dirname(out), exist_ok=True)
        open(out, 'wb').write(png)
        try:
            im = Image.open(out); im.load()
            ok += 1
            print(f"{rel:32s} len={n:6d} file={len(b):6d} {im.size} {im.mode}")
        except Exception as e:
            bad += 1; print("BAD", rel, e)
print("ok", ok, "bad", bad)
