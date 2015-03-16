import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class av {
    private int[] a = {0, 7, 11, 18, 25, 33, 40, 47, 54, 61};
    private int[] b = {7, 4, 7, 7, 8, 7, 7, 7, 7, 7};
    private Image c = az.b("/f/nb");
    private int d = this.c.getHeight();

    public int a(int paramInt) {
        int i = 0;
        int j = paramInt;
        do {
            int k = j % 10;
            j /= 10;
            i += this.b[k] + 1;
        } while (j > 0);
        return i;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
        int i = a(paramInt1);
        paramInt2 += i;
        do {
            int j = paramInt1 % 10;
            paramInt1 /= 10;
            paramInt2 -= this.b[j] + 1;
            y.a(paramGraphics, this.c, this.a[j], 0, this.b[j], this.d, paramInt2, paramInt3, 0);
        } while (paramInt1 > 0);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     av

 * JD-Core Version:    0.7.0.1

 */