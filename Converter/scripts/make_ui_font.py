#!/usr/bin/env python3
"""Bakes the small-text font of the touch interface into a 1-bit bitmap font for Unity.

Source: Galmuri9 by Lee Minseo (SIL Open Font License 1.1, fonts/Galmuri-OFL.md; fonts/Galmuri9-vi.ttf
keeps its Latin and Vietnamese letters only), drawn at its own
pixel size (10 px per em) without smoothing, then made bold like the game's letters (every stem
doubled one pixel to the right). Unity draws the text from these bitmaps at the same whole-number
scale as the game's pixel art, with a 1-pixel dark outline like the game's own fonts.

usage: make_ui_font.py [font.ttf] [out.bytes]

Format (little endian): "L12F", u8 version (1), u8 ascent (line top -> baseline), u8 line height,
u8 cap height, u16 glyph count, then per glyph: u32 code point, i8 x offset, i8 y offset (top row,
relative to the baseline), u8 width, u8 height, u8 advance, ceil(w*h/8) bytes of rows (MSB first).
"""
import os, struct, sys
from PIL import Image, ImageDraw, ImageFont
from fontTools.ttLib import TTFont

W = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
src = sys.argv[1] if len(sys.argv) > 1 else os.path.join(W, 'fonts', 'Galmuri9-vi.ttf')
out = sys.argv[2] if len(sys.argv) > 2 else os.path.join(W, 'fonts', 'ui_font.bytes')
SIZE = 10  # Galmuri9: 1000 units per em, 100 units per pixel

VI = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵz"
chars = [chr(c) for c in range(32, 127)] + list(VI) + list(VI.upper()) + list("·×–—…‘’“”«»°%")
cmap = TTFont(src).getBestCmap()
chars = sorted(set(c for c in chars if ord(c) in cmap))
font = ImageFont.truetype(src, SIZE)
B = 24  # baseline on the scratch canvas

glyphs = []
top, bottom = 0, 0
for ch in chars:
    img = Image.new('L', (40, 40), 0)
    d = ImageDraw.Draw(img)
    d.fontmode = '1'
    d.text((8, B), ch, font=font, fill=255, anchor='ls')
    adv = int(round(font.getlength(ch)))
    box = img.getbbox()
    if box is None:  # space
        glyphs.append((ord(ch), 0, 0, 0, 0, adv + 1, b''))
        continue
    x0, y0, x1, y1 = box
    g = img.crop((x0, y0, x1, y1))
    bold = Image.new('L', (g.width + 1, g.height), 0)
    bold.paste(g, (0, 0))
    bold.paste(255, (1, 0), g)  # stems two pixels wide, like the game's font
    w, h = bold.size
    bits = bytearray((w * h + 7) // 8)
    px = bold.load()
    for y in range(h):
        for x in range(w):
            if px[x, y] > 127:
                i = y * w + x
                bits[i >> 3] |= 0x80 >> (i & 7)
    glyphs.append((ord(ch), x0 - 8, y0 - B, w, h, adv + 1, bytes(bits)))
    top, bottom = min(top, y0 - B), max(bottom, y1 - B)

capbox = ImageFont.truetype(src, SIZE).getbbox('H', anchor='ls')
cap = -capbox[1]
# lines are 13 rows apart: 11 above the baseline (capitals are 9 tall, accents of small letters reach
# 12, stacked accents of capitals up to 15 - they may reach into the line above, as in print), 2 below
ascent, line = 11, 13
assert bottom <= 3, bottom
with open(out, 'wb') as f:
    f.write(b'L12F' + struct.pack('<BBBBH', 1, ascent, line, cap, len(glyphs)))
    for code, xo, yo, w, h, adv, bits in glyphs:
        f.write(struct.pack('<IbbBBB', code, xo, yo, w, h, adv) + bits)
print(f'make_ui_font: {len(glyphs)} glyphs, ascent {ascent}, line {line}, cap {cap} -> {out}')
