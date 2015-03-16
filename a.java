import javax.microedition.lcdui.Graphics;

public class a
        extends ds {
    private String a;
    private int b;
    private int c;
    private br d;
    private boolean e;
    private boolean f;

    public a(String paramString, int paramInt, br parambr) {
        this.d = parambr;
        a(paramString, paramInt);
        a(0, 0, parambr.a(paramString), parambr.a());
    }

    private void b() {
        if (this.t == 0) {
            this.c = 0;
            this.f = true;
        } else {
            this.c = (this.r - this.b);
            this.f = false;
        }
        this.e = (this.r < this.b + 2);
    }

    public void a(String paramString, int paramInt) {
        this.a = paramString;
        j(paramInt);
        this.b = this.d.a(paramString);
        b();
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        paramInt4 = this.d.a();
        super.a(paramInt1, paramInt2, paramInt3, paramInt4);
        b();
    }

    public void a() {
        if (!this.e) {
            return;
        }
        if (this.f) {
            this.c -= 1;
            if (this.c + this.b <= this.r) {
                this.f = false;
            }
        } else {
            this.c += 1;
            if (this.c >= 0) {
                this.f = true;
            }
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        dl localdl = new dl();
        y.a(paramGraphics, localdl);
        y.a(paramGraphics, paramInt1 + this.p, paramInt2 + this.q, this.r, this.s);
        this.d.a(paramGraphics, this.a, paramInt1 + this.c + this.p, paramInt2 + this.q, 0);
        y.b(paramGraphics, localdl);
    }
}