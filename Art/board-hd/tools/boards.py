from PIL import Image
G='gold rice rice yinyang gold rice yinyang sword|heart sword yinyang book yinyang rice book book|book gold heart heart rice heart rice rice|heart heart gold heart yinyang book sword book|heart book heart yinyang heart gold rice rice|rice yinyang book gold sword sword yinyang yinyang|book gold rice book rice yinyang book gold|heart gold swordred rice yinyang rice sword book'
grid=[r.split() for r in G.split('|')]
def board(src,k,resample):
    cell,gem,foc=21*k,22*k,26*k; N=8*cell
    import os
    if os.path.exists(src+'/bkboard.png'):
        f=1 if src=='orig' else 4
        out=Image.open(src+'/bkboard.png').convert('RGBA').crop((35*f,53*f,203*f,221*f)).resize((N,N),Image.NEAREST)
    else:
        out=Image.new('RGBA',(N,N))
        cs=[Image.open(src+'/cell_%s.png'%x).convert('RGBA').resize((cell,cell),resample) for x in 'ab']
        for r in range(8):
            for c in range(8): out.alpha_composite(cs[(r+c)%2],(c*cell,r*cell))
    cache={}
    for r in range(8):
        for c in range(8):
            n=grid[r][c]
            if n not in cache: cache[n]=Image.open('%s/%s.png'%(src,n)).convert('RGBA').resize((gem,gem),resample)
            out.alpha_composite(cache[n],(c*cell+(cell-gem)//2,r*cell+(cell-gem)//2))
    f=Image.open('%s/focus1.png'%src).convert('RGBA').resize((foc,foc),resample)
    out.alpha_composite(f,(3*cell+(cell-foc)//2,3*cell+(cell-foc)//2))
    return out.convert('RGB')
import sys
for src in sys.argv[1:]:
    board(src,4,Image.NEAREST).save('board_%s.png'%src)
