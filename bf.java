import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bf
        extends ab {
    private int v;
    private boolean w;

    public bf(Image paramImage, int paramInt) {
        a(paramImage, paramInt);
        b(false);
        this.u = false;
        this.t = 3;
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        d(paramInt1, paramInt2);
        c(paramInt3, paramInt4);
        this.v = paramInt5;
        a_(0);
        this.u = true;
        this.h = false;
        this.w = false;
    }

    public void c() {
        this.w = true;
    }

    public void a() {
        if (this.v > 0) {
            this.v -= 1;
            return;
        }
        if (this.c == 0) {
            if ((this.w) && (this.g >= this.d[0].length - 1)) {
                a_(1);
            }
        } else if ((this.c == 1) && (this.g >= this.d[1].length - 1)) {
            this.u = false;
        }
    }

    public void a(Graphics paramGraphics) {
        if (this.v > 0) {
            return;
        }
        super.a(paramGraphics);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bf

 * JD-Core Version:    0.7.0.1

 */