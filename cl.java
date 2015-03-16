import javax.microedition.lcdui.Graphics;

public class cl
        extends ds {
    private String[] a;
    private int b;
    private int c;
    private int d;
    private int e;

    public cl(String[] paramArrayOfString, dl paramdl) {
        this.p = paramdl.a;
        this.q = paramdl.b;
        this.r = paramdl.c;
        this.s = paramdl.d;
        this.a = paramArrayOfString;
        this.b = 0;
        this.c = (this.q + this.s << 10);
        this.e = (this.c >> 10);
        this.d = (this.p + (this.r >> 1));
    }

    public boolean a() {
        return this.b >= this.a.length;
    }

    public void b() {
        if (this.b >= this.a.length) {
            return;
        }
        this.c -= 512;
        this.e = (this.c >> 10);
        if (this.e + 15 <= this.q) {
            this.b += 1;
            this.c += 15360;
            this.e = (this.c >> 10);
        }
    }

    public int c() {
        return this.b;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (this.b >= this.a.length) {
            return;
        }
        int i = this.e;
        int j = this.b;
        while ((j < this.a.length) && (i < this.q + this.s)) {
            bz.d.a(paramGraphics, this.a[j], this.d, i, 1);
            j++;
            i += 15;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cl

 * JD-Core Version:    0.7.0.1

 */