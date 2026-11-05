import sys
from PIL import Image
# sheet of orig (nearest 4x) | new for a list of names; paint shows orig | light | medium
out=sys.argv[1]; g=sys.argv[2]; names=sys.argv[3:]
rows=[]
for n in names:
    o=Image.open('all/%s.png'%n).convert('RGBA'); o4=o.resize((o.width*4,o.height*4),Image.NEAREST)
    import os
    if os.path.exists('HD/%s/%s.light.png'%(g,n)):
        ims=[o4,Image.open('HD/%s/%s.light.png'%(g,n)),Image.open('HD/%s/%s.medium.png'%(g,n))]
    else: ims=[o4,Image.open('HD/%s/%s.png'%(g,n))]
    rows.append(ims)
W=max(sum(i.width for i in r)+10*len(r) for r in rows)+10; H=sum(max(i.height for i in r)+10 for r in rows)+10
sh=Image.new('RGBA',(W,H),(60,48,30,255)); y=10
for r in rows:
    x=10
    for i in r: sh.alpha_composite(i,(x,y)); x+=i.width+10
    y+=max(i.height for i in r)+10
sh.save(out); print(sh.size)
