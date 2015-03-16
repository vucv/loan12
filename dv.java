import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;

public class dv
        extends cd {
    protected int[] e;
    protected boolean f;
    protected boolean g = true;
    protected dl h;
    protected dl i = new dl();
    protected int j;
    protected int k;
    private boolean n;
    private boolean o;
    protected int[] l = {0, -1, 1, -2, 2, -3, 3};
    protected int m;

    public boolean c() {
        return this.n;
    }

    public dv() {
        this(false);
    }

    public dv(boolean paramBoolean) {
        this(paramBoolean, null);
    }

    public dv(boolean paramBoolean, dl paramdl) {
        this.f = paramBoolean;
        if (paramBoolean) {
            if (paramdl == null) {
                paramdl = new dl(0, 0, cx.f, cx.g);
            }
            this.h = paramdl;
        }
    }

    public void a(ds[] paramArrayOfds, int paramInt) {
        super.a(paramArrayOfds, paramInt);
        if (this.f) {
            d();
        }
    }

    public void d() {
        this.n = true;
        this.o = true;
        this.e = new int[this.a.length];
        for (int i1 = 0; i1 < this.e.length; i1++) {
            this.e[i1] = this.a[i1].i();
            System.out.println("xStop=" + this.e[i1]);
        }
        i1 = 0;
        int i2 = 2147483647;
        for (int i3 = 0; i3 < this.a.length; i3++) {
            if (this.a[i3].k() > i1) {
                i1 = this.a[i3].k();
            }
            if (this.a[i3].i() < i2) {
                i2 = this.a[i3].i();
            }
        }
        i3 = this.h.a + this.h.c - i2;
        if (i2 + i1 - i3 > this.h.a) {
            i3 += i2 + i1 - i3 - this.h.a;
        }
        this.j = (i2 - i3);
        this.k = (i2 + i3);
        int i4 = 1;
        for (int i5 = 0; i5 < this.a.length; i5++) {
            if (i4 != 0) {
                this.a[i5].f(this.j);
            } else {
                this.a[i5].f(this.k);
            }
            i4 = i4 != 0 ? 0 : 1;
        }
    }

    protected void b(int paramInt1, int paramInt2) {
        if (this.g) {
            this.m = 0;
        }
        super.b(paramInt1, paramInt2);
    }

    public void a(int paramInt) {
        if (this.n) {
            return;
        }
        super.a(paramInt);
    }

    public void a(int paramInt1, int paramInt2) {
        if (this.n) {
            return;
        }
        super.a(paramInt1, paramInt2);
    }

    public void b() {
        if (this.n) {
            int i1 = 0;
            for (int i2 = 0; i2 < this.a.length; i2++) {
                int i3 = this.a[i2].i();
                int i4 = this.e[i2] - i3;
                if (Math.abs(i4) < 2) {
                    i3 += i4;
                    i1++;
                } else {
                    i3 += i4 / 2;
                }
                this.a[i2].f(i3);
            }
            if (i1 >= this.a.length) {
                this.n = false;
                if (!this.o) {
                    super.b(this.b);
                }
            }
        }
        if (this.m < this.l.length - 1) {
            this.m += 1;
        }
    }

    protected void b(Graphics paramGraphics) {
        y.a(paramGraphics, this.i);
        y.a(paramGraphics, this.i, this.h.a, this.h.b, this.h.c, this.h.d);
        if (this.n) {
            super.b(paramGraphics);
        } else {
            for (int i1 = 0; i1 < this.a.length; i1++) {
                if (this.b != i1) {
                    this.a[i1].a(paramGraphics);
                } else {
                    this.a[i1].a(paramGraphics, this.l[this.m], 0);
                }
            }
        }
        y.b(paramGraphics, this.i);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dv

 * JD-Core Version:    0.7.0.1

 */