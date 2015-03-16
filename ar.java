import com.mg.smsgame.main.b;

import javax.microedition.lcdui.Graphics;

public abstract class ar {
    protected int g = 0;
    protected int a_ = 0;
    protected int b_ = cx.f;
    protected int c_ = cx.g;
    protected boolean k = true;
    protected h l = new h();
    private de[] a = new de[3];
    private int[] b = new int[3];
    protected int m;
    protected int n;
    protected int o = 13;
    protected boolean p = true;
    protected int q;
    protected dz r = null;
    protected b s;

    public boolean f() {
        return this.k;
    }

    public void a(dz paramdz) {
        this.r = paramdz;
    }

    public int g() {
        return this.g;
    }

    public int h() {
        return this.a_;
    }

    public int i() {
        return this.b_;
    }

    public int j() {
        return this.c_;
    }

    public int k() {
        return this.q;
    }

    public void a(b paramb) {
        this.s = paramb;
    }

    public void c(boolean paramBoolean) {
        this.p = paramBoolean;
    }

    public h l() {
        return this.l;
    }

    public void a(h paramh) {
        this.l = paramh;
    }

    public void m() {
        for (int i = 0; i < this.a.length; i++) {
            this.a[i] = null;
        }
    }

    private boolean b() {
        if (this.l != null) {
            for (int i = 0; i < this.a.length; i++) {
                if (this.a[i] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    protected final void c(Graphics paramGraphics) {
        if (b()) {
            this.l.a(paramGraphics);
        }
        for (int i = 0; i < this.a.length; i++) {
            if (this.a[i] != null) {
                this.a[i].a(paramGraphics);
            }
        }
    }

    public final void d(Graphics paramGraphics) {
        if (this.r != null) {
            this.r.a(paramGraphics);
        }
        a(paramGraphics);
        c(paramGraphics);
    }

    public final void a(Graphics paramGraphics, boolean paramBoolean) {
        if (this.r != null) {
            this.r.a(paramGraphics);
        }
        a(paramGraphics);
        if (paramBoolean) {
            c(paramGraphics);
        }
    }

    public final void n() {
        if (this.r != null) {
            this.r.a();
        }
        for (int i = 0; i < this.a.length; i++) {
            if (this.a[i] != null) {
                this.a[i].c();
                if (this.b[i] > 0) {
                    this.b[i] -= 1;
                    if ((this.b[i] == 0) && (this.s != null)) {
                        this.s.b(this.q, this.a[i].a());
                    }
                }
            }
        }
        c();
        d();
        a();
    }

    public boolean o() {
        return this.a[0] != null;
    }

    public boolean p() {
        return this.a[2] != null;
    }

    public boolean q() {
        return this.a[1] != null;
    }

    public void a(de paramde) {
        this.a[0] = paramde;
        this.b[0] = 0;
        if (paramde == null) {
            return;
        }
        this.a[0].a(2, cx.g - 17 + 4, 20);
    }

    public void b(de paramde) {
        this.a[2] = paramde;
        this.b[2] = 0;
        if (paramde == null) {
            return;
        }
        this.a[2].a(cx.f - 2, cx.g - 17 + 4, 24);
    }

    public void c(de paramde) {
        this.a[1] = paramde;
        this.b[1] = 0;
        if (paramde == null) {
            return;
        }
        this.a[1].a(cx.f / 2 - 2, cx.g - 17 + 4, 17);
    }

    private void c() {
        if (cx.n) {
            int i = cx.o;
            int j = cx.p;
            if ((i > -1) && (j > -1)) {
                d(i, j);
                cx.o = cx.p = -1;
            }
            i = cx.q;
            j = cx.q;
            if ((i > -1) && (j > -1)) {
                e(i, j);
                cx.q = cx.r = -1;
            }
            i = cx.s;
            j = cx.t;
            if ((i > -1) && (j > -1)) {
                a_(i, j);
                cx.s = cx.t = -1;
            }
        }
    }

    private void d() {
        for (int i = 0; i < 21; i++) {
            if (cx.u[i] == 1) {
                b(i);
            } else if (cx.u[i] == 2) {
                if (!e(i)) {
                    a(i);
                }
            } else if (cx.u[i] >= 3) {
                if ((cx.u[i] > this.o) || (cx.u[i] == 3)) {
                    if (!e(i)) {
                        a(i);
                    }
                    if (!this.p) {
                        cx.u[i] = 0;
                    }
                }
                if (cx.u[i] < 2147483647) {
                    cx.u[i] += 1;
                }
                return;
            }
            cx.u[i] = 0;
        }
    }

    private boolean e(int paramInt) {
        if (this.s == null) {
            return false;
        }
        boolean bool = false;
        if ((o()) && (paramInt == 17)) {
            if (this.b[0] == 0) {
                this.a[0].b();
                this.b[0] = 3;
                bool = true;
            }
        } else if ((p()) && (paramInt == 18)) {
            if (this.b[2] == 0) {
                this.a[2].b();
                this.b[2] = 3;
                bool = true;
            }
        } else if ((q()) && (paramInt == 16) && (this.b[1] == 0)) {
            this.a[1].b();
            this.b[1] = 3;
            bool = true;
        }
        return bool;
    }

    public final void d(int paramInt1, int paramInt2) {
        int i = 0;
        this.m = paramInt2;
        this.n = paramInt1;
        if ((o()) && (this.a[0].a(paramInt1, paramInt2))) {
            cx.u[17] = 2;
            i = 1;
        }
        if ((p()) && (this.a[2].a(paramInt1, paramInt2))) {
            cx.u[18] = 2;
            i = 1;
        }
        if ((q()) && (this.a[1].a(paramInt1, paramInt2))) {
            cx.u[16] = 2;
            i = 1;
        }
        if (i == 0) {
            a(paramInt1, paramInt2);
        }
    }

    public final void e(int paramInt1, int paramInt2) {
        g(paramInt1, paramInt2);
    }

    public final void a_(int paramInt1, int paramInt2) {
        c(paramInt2 - this.m);
        d(paramInt1 - this.n);
        this.m = paramInt2;
        this.n = paramInt1;
    }

    public void a(int paramInt) {
    }

    public void b(int paramInt) {
    }

    public void a(int paramInt1, int paramInt2) {
    }

    public void g(int paramInt1, int paramInt2) {
    }

    public void c(int paramInt) {
    }

    public void d(int paramInt) {
    }

    public void r() {
    }

    public void s() {
    }

    public void t() {
    }

    public abstract void a();

    public abstract void a(Graphics paramGraphics);
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ar

 * JD-Core Version:    0.7.0.1

 */