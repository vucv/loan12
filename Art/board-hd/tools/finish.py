# A mới: phóng 16 lần bằng Scale2x rồi thu về 4 lần (khử răng cưa); hình có lưới (lúa) giữ nét bên trong
import sys
from PIL import Image, ImageEnhance
out=sys.argv[1]
GRID={'rice'}
for n in sys.argv[2:]:
    a=Image.open('%s/%s.png'%(out,n)).convert('RGBA')
    if n in GRID:
        o=Image.open('orig/%s.png'%n).convert('RGBA')
        nn=o.resize(a.size,Image.NEAREST)
        r,g,b,_=nn.split(); a=Image.merge('RGBA',(r,g,b,a.split()[3]))
    rgb=a.convert('RGB'); al=a.split()[3]
    rgb=ImageEnhance.Color(rgb).enhance(1.08); rgb=ImageEnhance.Contrast(rgb).enhance(1.04)
    rgb.putalpha(al); rgb.save('%s/%s.png'%(out,n))
