import javax.microedition.lcdui.Image;

public class k
        extends dq {
    private final int a = 0;
    private final int v = 1;
    private int w;

    public k(int paramInt1, int paramInt2) {
        d(paramInt1, paramInt2);
        this.d = new byte[][]{{0, 0, 0, 2, 1, 2}, {2}};
        this.u = false;
        this.t = 3;
    }

    public int a(Image paramImage) {
        a(paramImage, 3);
        this.u = true;
        a_(0);
        this.g = 0;
        this.w = 45;
        return this.d[0].length + this.w;
    }

    public void a() {
        switch (this.c) {
            case 0:
                if (this.g >= this.d[0].length - 1) {
                    a_(1);
                }
                break;
            case 1:
                if (this.w > 0) {
                    this.w -= 1;
                    if (this.w == 0) {
                        this.n = null;
                        this.u = false;
                    }
                }
                break;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     k

 * JD-Core Version:    0.7.0.1

 */