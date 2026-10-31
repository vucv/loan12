import sys, glob
from PIL import Image
d=sys.argv[1]; cols=int(sys.argv[2]) if len(sys.argv)>2 else 5
fs=sorted(f for f in glob.glob(d+'/*.png') if not f.endswith('_sheet.png'))
ims=[Image.open(f) for f in fs]
if not ims: sys.exit()
rows=(len(ims)+cols-1)//cols
s=Image.new('RGB',(cols*250,rows*330),(255,0,255))
for k,im in enumerate(ims):
    s.paste(im,((k%cols)*250,(k//cols)*330))
s.save(d+'/_sheet.png'); print(len(ims),'shots')
