import javax.microedition.lcdui.Graphics;

public class dp
        extends aj {
    private String[] t;
    private int u;
    private int v;
    private String w;
    private int x = 0;
    private int y = 2;
    private boolean z = false;
    private int A = 20;
    private int B = 0;
    protected int c = 6;
    protected int d = cx.P;
    protected int e;
    protected int f;

    public dp(String paramString1, String paramString2, int paramInt) {
        this.w = paramString1;
        a(paramString2);
        this.B = paramInt;
        b();
    }

    private void b() {
        if (this.B == 0) {
            this.u = 0;
            this.v = (cx.g - 17);
        } else {
            int i = this.t.length;
            int j = (i + 2) * bz.d.a();
            this.u = ((this.c_ - 17 - j) / 2);
            this.v = ((this.c_ - 17 + j) / 2 + bz.d.a());
            if (this.u < 0) {
                this.u = 0;
            }
            if (this.v > cx.g - 17) {
                this.v = (cx.g - 17);
            }
        }
        this.g = 0;
        this.a_ = this.u;
        this.e = cx.f;
        this.f = (this.v - this.u);
        if (this.B != 0) {
            this.b_ = this.e;
            this.c_ = this.f;
        }
    }

    protected void b(Graphics paramGraphics) {
        cz.a(paramGraphics, cx.P, cx.Q, this.g, this.a_, this.e, this.f);
    }

    protected void e(Graphics paramGraphics) {
        this.c = 6;
        if ((this.w != null) && (this.w.length() > 0)) {
            bz.c.a(paramGraphics, this.w, this.b_ / 2, this.u + bz.c.a() - 5, 1);
            bz.a(bz.d);
            paramGraphics.setClip(this.c, this.u + bz.d.a() * 2 - 5, this.e - this.c * 2, this.f - 2 * bz.d.a());
            bz.a(paramGraphics, bz.d, this.t, this.c, this.u + bz.d.a() * 2 - 5 + this.x, this.e - this.c * 2, this.f - 2 * bz.d.a() + 5 - this.x, 1);
        } else {
            paramGraphics.setClip(this.c, this.u + bz.d.a() - 5, this.e - this.c * 2, this.f - bz.d.a());
            bz.a(paramGraphics, bz.d, this.t, this.c, this.u + bz.d.a() - 5 + this.x, this.e - this.c * 2, this.f - bz.d.a() + 5 - this.x, 1);
        }
        paramGraphics.setClip(0, 0, cx.f, cx.g);
    }

    public void a() {
        if ((this.A == 0) && (this.z)) {
            this.x -= this.y / 2;
        }
        if (this.A > 0) {
            this.A -= 1;
        }
    }

    public void a(String paramString) {
        this.t = bz.a(paramString, this.b_ - this.c * 2);
    }

    public void a(int paramInt) {
        if (paramInt == 12) {
            this.A = 20;
            if (this.x < 0) {
                this.x += this.y;
            }
        } else if (paramInt == 13) {
            this.A = 20;
            if (this.z) {
                this.x -= this.y;
            }
        }
        super.a(paramInt);
    }

    public void a(int paramInt1, int paramInt2) {
        this.A = 20;
        super.a(paramInt1, paramInt2);
    }

    public void c(int paramInt) {
        this.A = 20;
        if (paramInt > 0) {
            if (this.x < 0) {
                this.x += this.y * paramInt;
                if (this.x > 0) {
                    this.x = 0;
                }
            }
        } else if (this.z) {
            this.x -= -paramInt * this.y;
        }
    }

    public void r() {
        this.w = null;
        this.t = null;
        super.r();
    }

    public void a(Graphics paramGraphics) {
        b(paramGraphics);
        e(paramGraphics);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dp

 * JD-Core Version:    0.7.0.1

 */