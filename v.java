import com.mg.smsgame.main.b;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class v
        extends aj
        implements b {
    private int c = 1;
    private int d = 2;
    private int e = 3;
    private int f;
    private de t;
    private de u;
    private Image v;
    private int w;

    public v() {
        this(2);
    }

    public v(int paramInt) {
        a(null);
        byte[] arrayOfByte = bg.o();
        if (arrayOfByte == null) {
            cx.y = 80;
        } else {
            cx.y = ai.a(arrayOfByte);
        }
        this.f = cx.y;
        this.v = az.a("/_sound");
        c(new da("Chọn", this.c));
        this.t = new da("Mở", this.e);
        this.u = new da("Tắt", this.d);
        this.w = paramInt;
        if (this.f <= 0) {
            b(this.t);
        } else {
            b(this.u);
        }
        a(this);
    }

    public void a(int paramInt) {
        super.a(paramInt);
        if ((paramInt == 14) || (paramInt == 4) || (paramInt == 13) || (paramInt == 8)) {
            this.f -= 5;
            if (this.f <= 0) {
                this.f = 0;
                b(this.t);
            }
        } else if ((paramInt == 15) || (paramInt == 6) || (paramInt == 12) || (paramInt == 2)) {
            if (this.f <= 0) {
                b(this.u);
            }
            this.f += 5;
            if (this.f > 100) {
                this.f = 100;
            }
        }
    }

    public void a(int paramInt1, int paramInt2) {
        super.a(paramInt1, paramInt2);
        int i = this.c_ / 2 + this.v.getHeight() / 2 + 5;
        int j = i + 18;
        int k = (this.b_ - 100) / 2 - 10;
        int m = k + 10;
        if ((paramInt2 > i) && (paramInt2 < j)) {
            if ((paramInt1 > k) && (paramInt1 < m)) {
                cx.u[14] = 2;
            } else if ((paramInt1 > k + 113) && (paramInt1 < m + 113)) {
                cx.u[15] = 2;
            } else if ((paramInt1 >= m) && (paramInt1 <= k + 113)) {
                this.f = (paramInt1 - m);
                if (this.f > 100) {
                    this.f = 100;
                }
                if (this.f < 0) {
                    this.f = 0;
                }
                if (this.f <= 0) {
                    b(this.t);
                } else {
                    b(this.u);
                }
            }
        }
    }

    public void a() {
    }

    public void a(Graphics paramGraphics) {
        paramGraphics.setColor(0);
        paramGraphics.fillRect(0, 0, this.b_, this.c_);
        int i = this.c_ / 2 + this.v.getHeight() / 2 + 5;
        int j = (this.b_ - 100) / 2;
        bz.c.a(paramGraphics, "-", j - 2, i, 2);
        bz.c.a(paramGraphics, "+", j + 103, i, 0);
        paramGraphics.setColor(255);
        paramGraphics.drawRect(j - 1, i, 101, 10);
        if (this.f > 0) {
            y.a(paramGraphics, this.v, 0, 0, 100, 100, this.b_ / 2, this.c_ / 2 - 5, 3);
            paramGraphics.setColor(6998052);
            paramGraphics.fillRect(j, i + 1, this.f, 9);
        } else {
            y.a(paramGraphics, this.v, 100, 0, 100, 100, this.b_ / 2, this.c_ / 2 - 5, 3);
        }
    }

    public void b(int paramInt1, int paramInt2) {
        if (paramInt2 == this.c) {
            if (cx.y != this.f) {
                bg.d(this.f);
            }
            cx.y = this.f;
            if (cx.y <= 0) {
                cx.x = false;
            } else {
                cx.x = true;
            }
            b();
        } else if (paramInt2 == this.d) {
            this.f = 0;
            b(this.t);
        } else if (paramInt2 == this.e) {
            this.f = 80;
            b(this.u);
        }
    }

    public void b() {
        cz.d().e.a(1, this.w, false);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     v

 * JD-Core Version:    0.7.0.1

 */