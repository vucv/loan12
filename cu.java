import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class cu
        extends ab {
    private int[] v;
    private int[] w;
    private int x;
    private Image[] y;
    private int z;
    private int A;

    public cu(Image paramImage, int paramInt, Image[] paramArrayOfImage) {
        a(paramImage, paramInt);
        this.y = paramArrayOfImage;
        this.t = 3;
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        this.x = 1;
        this.u = true;
        this.z = paramInt5;
        this.A = 5;
    }

    public void a(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
        this.v = paramArrayOfInt1;
        this.w = paramArrayOfInt2;
    }

    public void a() {
        if (this.A <= 0) {
            if (this.x < this.v.length) {
                this.x += 1;
            }
            this.A = 5;
        } else {
            this.A -= 1;
        }
        if (this.z > 0) {
            this.z -= 1;
            if (this.z == 0) {
                this.u = false;
            }
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u) {
            return;
        }
        for (int i = 0; i < this.x; i++) {
            super.a(paramGraphics, this.v[i], this.w[i]);
        }
        i = this.x - 1;
        for (int j = 0; j < i; j++) {
            a(paramGraphics, this.v[j], this.w[j], this.v[(j + 1)], this.w[(j + 1)]);
        }
    }

    private void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        int i = paramInt1;
        int j = paramInt2;
        int k = 0;
        int m = 0;
        int n = 1;
        int i1 = 1;
        do {
            if (n != 0) {
                n = i > paramInt3 ? 1 : 0;
                if (n != 0) {
                    k = i - 21;
                } else {
                    k = i + 21;
                }
                n = n == (k > paramInt3 ? 1 : 0) ? 1 : 0;
                if (n == 0) {
                    k = paramInt3;
                }
            }
            if (i1 != 0) {
                i1 = j > paramInt4 ? 1 : 0;
                if (i1 != 0) {
                    m = j - 21;
                } else {
                    m = j + 21;
                }
                i1 = i1 == (m > paramInt4 ? 1 : 0) ? 1 : 0;
                if (i1 == 0) {
                    m = paramInt4;
                }
            }
            if (i < k) {
                if (j < m) {
                    paramGraphics.drawImage(this.y[0], i, j, 20);
                } else if (j > m) {
                    paramGraphics.drawRegion(this.y[0], 0, 0, this.y[0].getWidth(), this.y[0].getHeight(), 2, i, m, 20);
                } else {
                    paramGraphics.drawImage(this.y[1], i, j, 6);
                }
            } else if (i > k) {
                if (j < m) {
                    paramGraphics.drawRegion(this.y[0], 0, 0, this.y[0].getWidth(), this.y[0].getHeight(), 2, k, j, 20);
                } else if (j > m) {
                    paramGraphics.drawImage(this.y[0], k, m, 20);
                } else {
                    paramGraphics.drawImage(this.y[1], k, m, 6);
                }
            } else if (j < m) {
                paramGraphics.drawRegion(this.y[1], 0, 0, this.y[1].getWidth(), this.y[1].getHeight(), 5, i, j, 17);
            } else if (j > m) {
                paramGraphics.drawRegion(this.y[1], 0, 0, this.y[1].getWidth(), this.y[1].getHeight(), 5, i, m, 17);
            }
            i = k;
            j = m;
        } while ((n != 0) || (i1 != 0));
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cu

 * JD-Core Version:    0.7.0.1

 */