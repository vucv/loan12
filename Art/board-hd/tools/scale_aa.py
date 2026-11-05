import sys
from PIL import Image, ImageFilter
sys.path.insert(0,'.')
from importlib import util
spec=util.spec_from_file_location('s','scale.py')
def scale2x(im):
    w,h=im.size; p=im.load(); o=Image.new('RGBA',(w*2,h*2)); q=o.load()
    g=lambda x,y:p[min(max(x,0),w-1),min(max(y,0),h-1)]
    for y in range(h):
        for x in range(w):
            E=g(x,y);B=g(x,y-1);D=g(x-1,y);F=g(x+1,y);H=g(x,y+1)
            if B!=H and D!=F:
                e0=D if D==B else E; e1=F if B==F else E; e2=D if D==H else E; e3=F if H==F else E
            else: e0=e1=e2=e3=E
            q[2*x,2*y]=e0;q[2*x+1,2*y]=e1;q[2*x,2*y+1]=e2;q[2*x+1,2*y+1]=e3
    return o
out=sys.argv[1]; up=int(sys.argv[2])  # up: number of scale2x passes (4 -> 16x)
for n in sys.argv[3:]:
    im=Image.open('orig/%s.png'%n).convert('RGBA')
    big=im
    for _ in range(up): big=scale2x(big)
    w,h=im.size
    sm=big.convert('RGBa').resize((w*4,h*4),Image.BOX).convert('RGBA')
    sm.save('%s/%s.png'%(out,n))
