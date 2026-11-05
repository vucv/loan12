# Vẽ lại bằng hình học chính xác (vẽ ở 16x rồi thu về 4x để khử răng cưa) các hình chỉ gồm đoạn thẳng / đường tròn:
# khung chọn (3 dải tối - sáng - tối, bo góc) và vòng nổ của kiếm (các vòng tròn đồng tâm).
import sys
from PIL import Image, ImageDraw, ImageEnhance
SS = 16
def canvas(w, h): return Image.new('RGBA', (w * SS, h * SS), (0, 0, 0, 0))
def down(im, w, h):
    im = im.convert('RGBa').resize((w * 4, h * 4), Image.BOX).convert('RGBA')
    rgb = im.convert('RGB'); al = im.split()[3]
    rgb = ImageEnhance.Color(rgb).enhance(1.08); rgb = ImageEnhance.Contrast(rgb).enhance(1.04); rgb.putalpha(al)
    return rgb
def frame(d, x0, y0, x1, y1, dark, light):
    # x0..x1 tính theo cạnh điểm ảnh gốc; 3 dải mỗi dải 1 điểm ảnh gốc
    bands = [(0, dark, 3.0), (1, light, 2.2), (2, dark, 1.4)]
    for i, col, r in bands:
        d.rounded_rectangle([(x0 + i) * SS, (y0 + i) * SS, (x1 - i) * SS - 1, (y1 - i) * SS - 1], radius=r * SS, fill=col)
    d.rounded_rectangle([(x0 + 3) * SS, (y0 + 3) * SS, (x1 - 3) * SS - 1, (y1 - 3) * SS - 1], radius=0.8 * SS, fill=(0, 0, 0, 0))
def rings(d, cx, cy, bands):
    # bands: (bán kính ngoài, màu) từ ngoài vào trong; màu None = khoét trong suốt
    for r, col in bands:
        d.ellipse([(cx - r) * SS, (cy - r) * SS, (cx + r) * SS - 1, (cy + r) * SS - 1], fill=col if col else (0, 0, 0, 0))
CYAN = ((0, 92, 85, 255), (0, 255, 211, 255)); PINK = ((92, 0, 68, 255), (255, 0, 212, 255))
Y, W, O = (255, 246, 0, 255), (255, 255, 255, 255), (255, 126, 0, 255)
def make(n):
    if n in ('focus1', 'focus2'):
        im = canvas(26, 26); frame(ImageDraw.Draw(im), 0, 0, 26, 26, *(CYAN if n == 'focus1' else PINK)); return down(im, 26, 26)
    if n == 'zoomfocus1':
        im = canvas(84, 42); d = ImageDraw.Draw(im)
        frame(d, 0, 0, 42, 42, *CYAN); frame(d, 47, 5, 79, 37, *CYAN); return down(im, 84, 42)
    if n == 'explodesword':
        im = canvas(68, 17); d = ImageDraw.Draw(im)
        rings(d, 8.5, 8.5, [(8.5, Y), (7.5, W), (6.4, O), (5.4, None)])
        rings(d, 26, 9, [(5, O), (4, W), (3, Y), (1.3, None)])
        rings(d, 42.5, 8.5, [(2.5, W), (1.5, None)])
        rings(d, 60, 9, [(1.1, W)])
        return down(im, 68, 17)
if __name__ == '__main__':
    out = sys.argv[1]
    for n in sys.argv[2:]: make(n).save('%s/%s.png' % (out, n))
