import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bk {
    protected int a;
    protected int b;
    protected int c;
    protected int d;
    protected Image e;
    private boolean h;
    protected int[] f;
    protected dl[] g;
    private int i;

    public int a() {
        return this.c;
    }

    public int b() {
        return this.d;
    }

    public bk(String paramString, int paramInt1, int paramInt2) {
        a(paramInt1, paramInt2);
        this.e = az.b(paramString);
        this.c = (this.e.getWidth() / 2);
        this.d = this.e.getHeight();
        this.f = new int[]{14, 15, 12, 13, 16};
        this.g = new dl[5];
        this.g[0] = new dl(0, 13, 18, 28);
        this.g[1] = new dl(36, 13, 18, 28);
        this.g[2] = new dl(13, 0, 28, 18);
        this.g[3] = new dl(13, 36, 28, 18);
        this.g[4] = new dl(20, 20, 14, 14);
        this.i = -1;
        this.h = false;
    }

    public void a(int paramInt1, int paramInt2) {
        this.a = paramInt1;
        this.b = paramInt2;
    }

    public int b(int paramInt1, int paramInt2) {
        if (this.h) {
            return -1;
        }
        if ((paramInt1 >= this.a) && (paramInt1 <= this.a + this.c) && (paramInt2 >= this.b) && (paramInt2 <= this.b + this.d)) {
            this.h = true;
            paramInt1 -= this.a;
            paramInt2 -= this.b;
            for (int j = 0; j < this.g.length; j++) {
                if (this.g[j].a(paramInt1, paramInt2)) {
                    this.i = j;
                    return this.f[j];
                }
            }
        }
        this.i = -1;
        return -1;
    }

    public int c() {
        this.h = false;
        if (this.i > -1) {
            int j = this.i;
            this.i = -1;
            return this.f[j];
        }
        return -1;
    }

    public void a(Graphics paramGraphics) {
        y.a(paramGraphics, this.e, 0, 0, this.c, this.d, this.a, this.b, 20);
        if (this.i > -1) {
            y.a(paramGraphics, this.e, this.c + this.g[this.i].a, this.g[this.i].b, this.g[this.i].c, this.g[this.i].d, this.a + this.g[this.i].a, this.b + this.g[this.i].b, 20);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bk

 * JD-Core Version:    0.7.0.1

 */