import sys,os
from playwright.sync_api import sync_playwright
S=sys.argv[1]
sizes={'focus1':156,'focus2':156,'cell_a':126,'cell_b':126}
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page()
    for f in sorted(os.listdir(S+'/svg')):
        n=f[:-4]; sz=sizes.get(n,132)
        svg=open(S+'/svg/'+f).read().replace('<svg ','<svg width="%d" height="%d" '%(sz,sz),1)
        pg.set_content('<html><body style="margin:0;background:transparent">%s</body></html>'%svg)
        pg.locator('svg').screenshot(path=S+'/B/'+n+'.png',omit_background=True)
    b.close()
