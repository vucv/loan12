import javax.microedition.lcdui.Graphics;

public class db
        extends aq {
    private boolean g;

    public void a() {
        super.a();
        if (this.e % 3 == 0) {
            this.g = (!this.g);
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.c) {
            return;
        }
        if (this.g) {
            bz.c.a(paramGraphics, this.d, paramInt1 + this.a, paramInt2 + this.b, 1);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     db

 * JD-Core Version:    0.7.0.1

 */