#!/usr/bin/env python3
"""Generates the pixel fonts used for MIDP system fonts (Font.getFont) - format documented in j2me/BitmapFont."""
import glob, os, struct, sys
from PIL import Image, ImageDraw, ImageFont

OUT = sys.argv[1]
SRC = sys.argv[2]  # java sources, to collect every character the game can print
TTF = '/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'

chars = set(chr(c) for c in range(32, 127))
for f in glob.glob(SRC + '/**/*.java', recursive=True):
    for ch in open(f, encoding='utf-8').read():
        if ord(ch) >= 128 and ord(ch) < 0xFFFF and ch.isprintable():
            chars.add(ch)
for ch in '“”‘’–—…•©®°·':
    chars.add(ch)
chars = sorted(chars)

# size index -> (point size, line height)
SIZES = [(11, 13), (12, 15), (14, 17)]
os.makedirs(OUT, exist_ok=True)
for idx, (pt, line_h) in enumerate(SIZES):
    font = ImageFont.truetype(TTF, pt)
    ascent, descent = font.getmetrics()
    # place the baseline so that ascent fits; Vietnamese stacked marks may be clipped slightly at the top
    baseline = min(ascent, line_h - max(2, min(descent, 4)))
    glyphs = []
    for ch in chars:
        adv = int(round(font.getlength(ch)))
        img = Image.new('L', (pt * 3, line_h + 8), 0)
        d = ImageDraw.Draw(img)
        d.text((pt, 4 + baseline), ch, font=font, fill=255, anchor='ls')
        bbox = img.point(lambda v: 255 if v >= 110 else 0).getbbox()
        if bbox is None:
            glyphs.append((ch, adv, 0, 0, 0, 0, b''))
            continue
        x0, y0, x1, y1 = bbox
        g = img.crop(bbox).point(lambda v: 1 if v >= 110 else 0)
        w, h = g.size
        bits = 0
        out = bytearray((w * h + 7) // 8)
        px = list(g.getdata())
        for i, v in enumerate(px):
            if v:
                out[i >> 3] |= 0x80 >> (i & 7)
        xoff = x0 - pt
        yoff = y0 - 4
        glyphs.append((ch, max(adv, 1), w, h, xoff, yoff, bytes(out)))
    data = bytearray()
    data += struct.pack('>BBH', line_h, baseline, len(glyphs))
    for ch, adv, w, h, xo, yo, bits in glyphs:
        data += struct.pack('>HBBBbb', ord(ch), min(adv, 255), w, h, max(-128, min(127, xo)), max(-128, min(127, yo)))
        data += bits
    open(os.path.join(OUT, 'font_%d.bin' % idx), 'wb').write(data)
    print('font_%d.bin: %d glyphs, height %d, baseline %d, %d bytes' % (idx, len(glyphs), line_h, baseline, len(data)))
