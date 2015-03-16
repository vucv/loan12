import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class aw
        extends dz {
    private Image a;
    private int b;
    private int c;
    private int d;

    public aw(Image paramImage, int paramInt1, int paramInt2, int paramInt3) {
        this.a = paramImage;
        this.b = paramInt1;
        this.c = paramInt2;
        this.d = paramInt3;
    }

    public void a(Graphics paramGraphics) {
        if (this.a != null) {
            paramGraphics.drawImage(this.a, this.b, this.c, this.d);
        }
    }

    public void a() {
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     aw

 * JD-Core Version:    0.7.0.1

 */