import com.mic.smsgame.suquan.t;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
// Skill
public class ad
        extends ae {
    private Image e;
    private Image f;
    private av t;
    private c u;
    private int v;
    private int w;
    private Image x;
    private Image y;
    private Image z;
    private int A;
    private int B;
    private t C;
    private int D;
    private int E;

    public ad(c paramc, int paramInt1, int paramInt2) {
        super(1);
        this.u = paramc;
        this.v = paramInt1;
        this.w = paramInt2;
        this.t = new av();
        this.C = new t();
        this.e = az.b("/gold");
        this.f = az.b("/m/isw");
        this.y = az.b("/m/bl");
        this.z = az.b("/f/divide");
        this.x = az.b("/m/lv");
        this.A = this.y.getWidth();
        this.B = (this.y.getHeight() / 2);
        this.b_ = 170;
        this.c_ = 80;
        this.g = (cx.f - this.b_ >> 1);
        this.a_ = (cx.g - this.c_ >> 1);
        this.D = dd.u(paramc.e + 1);
        this.E = dd.u(paramc.e);
    }

    private void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        paramGraphics.setColor(16777215);
        paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 34, 34);
        u.a(paramGraphics, this.u.a, paramInt1 + 4, paramInt2 + 4, true);
        bz.c.a(paramGraphics, u.a(this.u.a), paramInt1 + 40, paramInt2 + 0, 0);
        y.a(paramGraphics, this.y, 0, this.B, this.A, this.B, paramInt1 + 40, paramInt2 + 15, 0);
        y.a(paramGraphics, this.y, 0, 0, this.A * this.u.c / this.u.b, this.B, paramInt1 + 40, paramInt2 + 15, 0);
        this.C.a(paramGraphics, this.u.c, paramInt1 + 92, paramInt2 + 16, 2);
        this.C.a(paramGraphics, this.u.b, paramInt1 + 98, paramInt2 + 16, 0);
        paramGraphics.drawImage(this.z, paramInt1 + 93, paramInt2 + 14, 0);
        y.a(paramGraphics, this.x, 0, this.B, this.A, this.B, paramInt1 + 40, paramInt2 + 28, 0);
        y.a(paramGraphics, this.x, 0, 0, this.A * (this.v - this.E) / (this.D - this.E), this.B, paramInt1 + 40, paramInt2 + 28, 0);
        this.C.a(paramGraphics, this.v, paramInt1 + 92, paramInt2 + 29, 2);
        this.C.a(paramGraphics, this.D, paramInt1 + 98, paramInt2 + 29, 0);
        paramGraphics.drawImage(this.z, paramInt1 + 93, paramInt2 + 27, 0);
        paramGraphics.drawImage(this.e, paramInt1 + 132, paramInt2 + 36, 0);
        this.t.a(paramGraphics, this.w, paramInt1 + 130 - this.t.a(this.w), paramInt2 + 41);
        paramGraphics.drawImage(this.f, paramInt1 + 2, paramInt2 + 39, 0);
        this.t.a(paramGraphics, this.u.d, paramInt1 + 17, paramInt2 + 41);
        bz.d.a(paramGraphics, " " + this.u.e, paramInt1 + 151, paramInt2 + 0, 2);
    }

    private void b(Graphics paramGraphics, int paramInt1, int paramInt2) {
    }

    public void a(Graphics paramGraphics) {
        cz.a(paramGraphics, cx.P, cx.Q, this.g, this.a_, this.b_, this.c_);
        bz.c.a(paramGraphics, "Thông tin", cx.f >> 1, this.a_ + 5, 1);
        a(paramGraphics, this.g + 9, this.a_ + 20);
        b(paramGraphics, this.g + 9, this.a_ + 20 + 60);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ad

 * JD-Core Version:    0.7.0.1

 */