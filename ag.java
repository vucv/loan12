import javax.microedition.lcdui.Graphics;

public class ag
        extends by {
    private boolean j;
    private boolean k;

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        if (paramInt6 < 18) {
            paramInt6 = 18;
        }
        if (paramInt5 < 18) {
            paramInt5 = 18;
        }
        this.a = (paramInt1 + paramInt5 / 2);
        this.b = paramInt2;
        this.c = (paramInt3 + paramInt5 / 2);
        this.d = (paramInt4 + paramInt6 / 2);
        this.g = paramInt5;
        this.h = paramInt6;
        this.f = 18;
        this.e = 18;
        this.a = this.c;
        this.b = this.d;
        if (this.b > paramInt4) {
            this.k = true;
        } else {
            this.k = false;
        }
        this.j = false;
        this.i = false;
    }

    public void a() {
        if (this.i) {
            return;
        }
        int i;
        int m;
        if (this.j) {
            this.b = this.d;
            i = 1;
            if (this.f != this.h) {
                m = this.h - this.f;
                if (Math.abs(m) < 5) {
                    this.f += m;
                } else {
                    this.f += m / 2;
                    i = 0;
                }
            }
            if (i != 0) {
                m = this.g - this.e;
                if (Math.abs(m) < 5) {
                    this.e += m;
                    this.i = true;
                } else {
                    this.e += m / 2;
                }
            }
        } else {
            i = this.k ? this.d - 2 : this.d + 2;
            m = i - this.b;
            if (Math.abs(m) < 2) {
                this.b += m;
                this.j = true;
            } else {
                this.b += m / 2;
            }
        }
    }

    public void a(Graphics paramGraphics) {
        cz.a(paramGraphics, cx.P, cx.Q, c(), d(), e(), f());
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ag

 * JD-Core Version:    0.7.0.1

 */