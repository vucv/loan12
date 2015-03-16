import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bh
        extends ds {
    private int a;
    private Image[] b;
    private Image[] c;
    private Image[] d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;

    public void a(int paramInt) {
        this.a = paramInt;
    }

    public bh() {
        c();
    }

    private void c() {
        this.c = bs.a().g;
        this.d = bs.a().j;
        this.b = new Image[1];
        this.b[0] = bs.a().q;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        switch (this.a) {
            case 2:
            case 3:
                paramGraphics.drawImage(this.c[this.g], this.p, this.q, 3);
                break;
            case 1:
                paramGraphics.drawImage(this.c[this.g], this.p, this.q, 3);
                int k = 14;
                if (this.e >= 3) {
                    k++;
                }
                if ((this.f & 0x1) == 1) {
                    paramGraphics.drawRegion(this.d[this.g], 0, 0, this.d[this.g].getWidth(), this.d[this.g].getHeight(), 6, this.p - k, this.q, 10);
                }
                if ((this.f & 0x2) == 2) {
                    paramGraphics.drawRegion(this.d[this.g], 0, 0, this.d[this.g].getWidth(), this.d[this.g].getHeight(), 5, this.p + k, this.q, 6);
                }
                if ((this.f & 0x4) == 4) {
                    paramGraphics.drawRegion(this.d[this.g], 0, 0, this.d[this.g].getWidth(), this.d[this.g].getHeight(), 0, this.p, this.q - k, 33);
                }
                if ((this.f & 0x8) == 8) {
                    paramGraphics.drawRegion(this.d[this.g], 0, 0, this.d[this.g].getWidth(), this.d[this.g].getHeight(), 3, this.p, this.q + k, 17);
                }
                break;
            case 4:
                int m = this.b[this.g].getWidth() >> 1;
                int n = 0;
                if (this.j >= 2) {
                    n = 1;
                }
                y.a(paramGraphics, this.b[this.g], n * m, 0, m, this.b[this.g].getHeight(), this.p, this.q, 3);
        }
    }

    public void a() {
        switch (this.a) {
            case 4:
                this.j += 1;
                if (this.j >= 4) {
                    a(2);
                }
                break;
            case 3:
                int k = Math.abs(this.i - this.q);
                if (k < 2) {
                    k = 2;
                } else {
                    k /= 2;
                }
                boolean bool = f(this.i, k);
                k = Math.abs(this.h - this.p);
                if (k < 2) {
                    k = 2;
                } else {
                    k /= 2;
                }
                bool &= e(this.h, k);
                if (bool) {
                    a(2);
                }
            case 1:
                this.e += 1;
                if (this.e > 6) {
                    this.e = 0;
                }
                break;
        }
    }

    public void b() {
        this.a = 0;
    }

    public void b(int paramInt) {
        this.g = paramInt;
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.a = 3;
        this.h = paramInt3;
        this.i = paramInt4;
        d(paramInt1, paramInt2);
    }

    public void a(int paramInt1, int paramInt2) {
        this.a = 2;
        d(paramInt1, paramInt2);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3) {
        this.a = 1;
        this.f = paramInt1;
        d(paramInt2, paramInt3);
    }

    public void b(int paramInt1, int paramInt2) {
        this.a = 4;
        d(paramInt1, paramInt2);
        this.j = 0;
        this.e = 0;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bh

 * JD-Core Version:    0.7.0.1

 */