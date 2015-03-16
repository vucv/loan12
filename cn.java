import com.mg.smsgame.main.MGMIDlet;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

public class cn
        extends GameCanvas
        implements Runnable {
    private int a = 0;
    private Graphics b;
    private Image c;
    private int d;
    private int e;
    private int f;
    private int g = 0;
    private int h = 0;
    private int i = 0;
    private int j = 10;
    private int k = 1;
    private int l;
    private int m = 1;
    private int n = 160 / this.j;
    private boolean o = true;
    private boolean p = false;
    private int q = 0;
    private Font r = null;

    public cn() {
        super(false);
        setFullScreenMode(true);
        this.r = Font.getFont(0, 0, 8);
        this.q = 0;
        this.b = getGraphics();
        this.e = getWidth();
        this.d = getHeight();
        bz.a();
        this.c = az.a("/_mglogo");
        this.f = (this.c.getHeight() / 5);
        this.n = (this.c.getHeight() / 2 / this.j + 1);
        new Thread(this).start();
    }

    public void run() {
        if (!cx.F) {
            while ((this.o) || (!MGMIDlet.f().d)) {
                d();
                if (this.q == 0) {
                    if ((!this.p) && (this.g < this.n)) {
                        e();
                    }
                    flushGraphics();
                    this.a += 1;
                    if (this.a > 30) {
                        this.p = false;
                    }
                    if (this.a > 60) {
                        a();
                        this.q = 1;
                        this.a = 0;
                    }
                } else {
                    flushGraphics();
                    this.a += 1;
                    if (this.a > 40) {
                        this.o = false;
                    }
                }
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }
            }
            d();
        }
        b();
    }

    private void a() {
        this.c = az.a("/_partnerLogo");
    }

    private void b() {
        cx.g = getHeight();
        cx.f = getWidth();
        cx.h = cx.f;
        cx.i = cx.g;
        if (cx.f > 240) {
            cx.e = true;
            cx.f = 240;
            cx.j = (cx.h - cx.f) / 2;
        }
        if (cx.g > 320) {
            cx.e = true;
            cx.g = 320;
            cx.k = (cx.i - cx.g) / 2;
        }
        if ((cx.g == 320) && (cx.f == 240)) {
            cx.E = true;
        }
        cx.l = (cx.f - 240) / 2;
        cx.m = (cx.g - 320) / 2;
        MGMIDlet.f().c();
        c();
    }

    private void c() {
        this.c = null;
        this.r = null;
        System.gc();
    }

    private void d() {
        this.e = getWidth();
        this.d = getHeight();
        this.b.setClip(0, 0, this.e, this.d);
        this.b.setColor(16777215);
        this.b.fillRect(0, 0, this.e, this.d);
        if (this.q == 0) {
            this.b.setColor(6710886);
            this.b.setFont(this.r);
            this.b.drawString("www.microgame.com.vn", this.e / 2, this.d - 5, 65);
            this.b.drawImage(this.c, this.e / 2, this.d / 2 - this.f, 3);
        } else {
            this.b.drawImage(this.c, (this.e - this.c.getWidth()) / 2, (this.d - this.c.getHeight()) / 2, 0);
        }
    }

    protected void keyPressed(int paramInt) {
        if (this.q == 1) {
            this.a = 40;
        }
    }

    private void e() {
        this.l = (this.d / 2 - this.f);
        this.b.setColor(16777215);
        this.b.fillRect(0, 0, this.e, this.l - this.j / 2 - this.g * this.j);
        this.b.fillRect(0, this.l + this.j / 2 + this.g * this.j, this.e, this.d - this.l + this.j / 2 + this.g * this.j);
        int i1 = this.l - this.j / 2 - this.g * this.j;
        this.b.fillRect(0, i1, this.e, this.j / 2 - this.k);
        i1 = i1 + 10 - (this.j / 2 - this.k);
        this.b.fillRect(0, i1, this.e, this.j / 2 - this.k);
        i1 = this.l - this.j / 2 + this.g * this.j;
        this.b.fillRect(0, i1, this.e, this.j / 2 - this.k);
        i1 = i1 + 10 - (this.j / 2 - this.k);
        this.b.fillRect(0, i1, this.e, this.j / 2 - this.k);
        this.k += this.m;
        if ((this.k >= this.j / 2) || (this.k <= 0)) {
            if (this.i >= this.h) {
                this.k = 0;
                this.g += 1;
                this.m = 1;
                this.i = 0;
            } else {
                this.i += 1;
                this.m = (-this.m);
            }
        }
    }

    protected void pointerPressed(int paramInt1, int paramInt2) {
        if (this.q == 1) {
            this.a = 40;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cn

 * JD-Core Version:    0.7.0.1

 */