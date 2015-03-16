import com.mic.smsgame.suquan.e;
import com.mic.smsgame.suquan.i;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bm
        extends ds
        implements e {
    private Image[] a;
    private Image[] b;
    private Image[] c;
    private i[][] d;
    private byte[][] e;
    private byte[] f;
    private int[] g;

    public bm(int paramInt1, int paramInt2) {
        b();
        this.d = new i[8][8];
        this.e = new byte[][]{{0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3}, new byte[1]};
        this.g = new int[]{4, 4, 4, 4, 4, 4, 4};
        this.f = new byte[]{0, 0, 1, 1, 2, 2, 3, 3};
        byte[][] arrayOfByte = {{-1}, new byte[1], {0, 0, 1, 1, 2, 2, 3, 3}, new byte[1], new byte[1], new byte[1], new byte[1]};
        this.p = paramInt1;
        this.q = paramInt2;
        this.r = 168;
        this.s = 168;
        paramInt2 += 10;
        for (int i = 0; i < 8; i++) {
            j = paramInt1 + 10;
            for (k = 0; k < 8; k++) {
                this.d[i][k] = new i(j, paramInt2);
                this.d[i][k].j(3);
                this.d[i][k].a(arrayOfByte);
                j += 21;
            }
            paramInt2 += 21;
        }
        i = 4;
        int j = 4;
        int k = 0;
        int m = 1000;
        int n = m + 32 + 3;
        for (int i1 = i - 1; i1 > -1; i1--) {
            int i2 = 0;
            int i3 = m;
            for (int i4 = j - 1; i4 > -1; i4--) {
                this.d[i1][i4].a(i3, n);
                this.d[(i + k)][i4].a(i3, n);
                this.d[i1][(j + i2)].a(i3, n);
                this.d[(i + k)][(j + i2)].a(i3, n);
                i3 += 2;
                i2++;
            }
            k++;
            m += 2;
        }
    }

    public void a(int paramInt1, int paramInt2, int paramInt3) {
        paramInt1 -= 2;
        paramInt2 -= 2;
        this.d[paramInt1][paramInt2].a(this.p + paramInt2 * 21, this.q + paramInt1 * 21, this.a[paramInt3]);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        paramInt1 -= 2;
        paramInt2 -= 2;
        paramInt3 -= 2;
        paramInt4 -= 2;
        int i = this.p + paramInt2 * 21;
        int j = this.q + paramInt1 * 21;
        int k = this.p + paramInt4 * 21;
        int m = this.q + paramInt3 * 21;
        this.d[paramInt1][paramInt2].a(k, m, i, j, this.a[paramInt6]);
        this.d[paramInt3][paramInt4].a(i, j, k, m, this.a[paramInt5]);
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        paramInt1 -= 2;
        paramInt2 -= 2;
        paramInt3 -= 2;
        paramInt4 -= 2;
        int i = this.p + paramInt2 * 21;
        int j = this.q + paramInt1 * 21;
        int k = this.p + paramInt4 * 21;
        int m = this.q + paramInt3 * 21;
        this.d[paramInt3][paramInt4].a(i, j, k, m, this.a[paramInt5], paramInt6);
    }

    public int a(cc paramcc, int paramInt1, int paramInt2) {
        int i = paramcc.a - 2;
        int j = paramcc.b - 2;
        int k = 0;
        int m = this.q + i * 21;
        int n = paramcc.d > paramcc.c ? paramcc.d : paramcc.c;
        byte[] arrayOfByte = (byte[]) null;
        int i1 = 4;
        Image localImage = null;
        if ((n > 3) || (paramcc.f >= 7)) {
            arrayOfByte = this.e[0];
            i1 = this.g[paramcc.e];
            localImage = this.c[paramcc.e];
        } else {
            arrayOfByte = this.f;
            localImage = this.b[paramcc.e];
            i1 = 4;
        }
        int i2 = i + paramcc.c;
        int i3 = j + paramcc.d;
        for (int i4 = i; i4 < i2; i4++) {
            k = this.p + j * 21;
            for (int i5 = j; i5 < i3; i5++) {
                byte[][] arrayOfByte1 = this.d[i4][i5].d();
                arrayOfByte1[2] = arrayOfByte;
                this.d[i4][i5].a(k, m, localImage, i1);
                k += 21;
            }
            m += 21;
        }
        return arrayOfByte.length;
    }

    public i a(int paramInt1, int paramInt2) {
        return this.d[paramInt1][paramInt2];
    }

    private void b() {
        this.a = bs.a().d;
        this.b = bs.a().e;
        this.c = bs.a().f;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.d[i][j].a(paramGraphics);
            }
        }
    }

    public void a() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.d[i][j].e();
            }
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bm

 * JD-Core Version:    0.7.0.1

 */