import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bi
        extends bw {
    public void a() {
        this.f = (this.c / 14 + 1);
        this.h = new int[this.f];
    }

    public void a(Graphics paramGraphics, Image paramImage) {
        int i = 0;
        for (int j = 0; j < this.f; j++) {
            if (this.h[j] <= 0) {
                break;
            }
            int k = this.h[j];
            int m = i;
            if (j == this.f - 1) {
                if (m >= this.c) {
                    continue;
                }
                if (m + k > this.c) {
                    k = this.c - m;
                }
            }
            y.a(paramGraphics, paramImage, m, 0, k, this.d, this.a + m, this.b, 20);
            i += 14;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bi

 * JD-Core Version:    0.7.0.1

 */