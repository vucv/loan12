import javax.microedition.lcdui.Graphics;

public class z
        extends by {
    private boolean j;

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        if (paramInt6 < 18) {
            paramInt6 = 18;
        }
        if (paramInt5 < 18) {
            paramInt5 = 18;
        }
        this.a = (paramInt1 + paramInt5 / 2);
        this.b = (paramInt2 + paramInt6 / 2);
        this.c = (paramInt3 + paramInt5 / 2);
        this.d = paramInt4;
        this.g = 18;
        this.h = 18;
        this.e = paramInt5;
        this.f = paramInt6;
        this.c = this.a;
        this.d = this.b;
        this.j = true;
        this.i = false;
    }

    public void a() {
        if (this.i) {
            return;
        }
        int i;
        int k;
        if (this.j) {
            i = 1;
            if (this.e != this.g) {
                k = this.g - this.e;
                if (Math.abs(k) < 5) {
                    this.e += k;
                } else {
                    this.e += k / 2;
                    i = 0;
                }
            }
            if (i != 0) {
                k = this.h - this.f;
                if (Math.abs(k) < 5) {
                    this.f += k;
                    this.i = true;
                } else {
                    this.f += k / 2;
                }
            }
        } else {
            i = this.d;
            k = i - this.b;
            if (Math.abs(k) < 2) {
                this.b += k;
                this.i = true;
            } else {
                this.b += k / 2;
            }
        }
    }

    public void a(Graphics paramGraphics) {
        cz.a(paramGraphics, cx.P, cx.Q, c(), d(), e(), f());
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     z

 * JD-Core Version:    0.7.0.1

 */