import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ce
        extends ab {
    private int v;

    public ce(Image paramImage, int paramInt) {
        a(paramImage, paramInt);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        d(paramInt1, paramInt2);
        c(paramInt3, paramInt4);
        this.k = (b(paramInt1, paramInt3, 15) + 1);
        this.l = b(paramInt2, paramInt4, 20);
        this.v = paramInt5;
        this.u = true;
        a_(0);
        this.t = 33;
        if (paramInt1 > paramInt3) {
            this.m = 2;
        } else {
            this.m = 0;
        }
    }

    public void a() {
        if (this.v > 0) {
            this.v -= 1;
            return;
        }
        switch (this.c) {
            case 0:
                if (c(this.i, this.j, this.k, this.l)) {
                    cz.a().c(10);
                    a_(1);
                    this.t = 33;
                } else {
                    this.l += 1;
                }
                break;
            case 1:
                if (this.g >= this.d[1].length - 1) {
                    this.u = false;
                }
                break;
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (this.v < 1) {
            super.a(paramGraphics, paramInt1, paramInt2);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ce

 * JD-Core Version:    0.7.0.1

 */