import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class y {
    static int a = 255;
    public static byte[][] b = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    public static byte[][] c = {{0, 1, 2, 1}, {1, 3, 5, 3, 1}, {2, 5, 9, 5, 2}, {1, 3, 5, 3, 1}, {0, 1, 2, 1}};
    public static dl d = new dl(0, 0, cx.f, cx.g);

    public static void a(Graphics paramGraphics, dl paramdl) {
        paramdl.a = paramGraphics.getClipX();
        paramdl.b = paramGraphics.getClipY();
        paramdl.c = paramGraphics.getClipWidth();
        paramdl.d = paramGraphics.getClipHeight();
    }

    public static void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
        int i = paramInt1;
        int j = paramInt2;
        int k = paramInt5;
        int m = paramInt6;
        long l1 = i;
        l1 += paramInt3;
        long l2 = j;
        l2 += paramInt4;
        long l3 = k;
        l3 += paramInt7;
        long l4 = m;
        l4 += paramInt8;
        if (i < k) {
            i = k;
        }
        if (j < m) {
            j = m;
        }
        if (l1 > l3) {
            l1 = l3;
        }
        if (l2 > l4) {
            l2 = l4;
        }
        l1 -= i;
        l2 -= j;
        if (l1 < -2147483648L) {
            l1 = -2147483648L;
        }
        if (l2 < -2147483648L) {
            l2 = -2147483648L;
        }
        paramGraphics.setClip(i, j, (int) l1, (int) l2);
    }

    public static void b(Graphics paramGraphics, dl paramdl) {
        paramGraphics.setClip(paramdl.a, paramdl.b, paramdl.c, paramdl.d);
    }

    public static void a(Graphics paramGraphics, dl paramdl, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        a(paramGraphics, paramdl.a, paramdl.b, paramdl.c, paramdl.d, paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public static void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        a(paramGraphics, paramGraphics.getClipX(), paramGraphics.getClipY(), paramGraphics.getClipWidth(), paramGraphics.getClipHeight(), paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public static final void a(Graphics paramGraphics, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        if ((paramInt7 & 0x1) > 0) {
            paramInt5 -= (paramInt3 >> 1);
        } else if ((paramInt7 & 0x8) > 0) {
            paramInt5 -= paramInt3;
        }
        if ((paramInt7 & 0x2) > 0) {
            paramInt6 -= (paramInt4 >> 1);
        } else if ((paramInt7 & 0x20) > 0) {
            paramInt6 -= paramInt4;
        }
        a(paramGraphics, d);
        a(paramGraphics, d, paramInt5, paramInt6, paramInt3, paramInt4);
        paramGraphics.drawImage(paramImage, paramInt5 - paramInt1, paramInt6 - paramInt2, 0);
        b(paramGraphics, d);
    }

    public static void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        paramGraphics.setColor(paramInt1);
        int i = paramInt2;
        int j = paramInt3;
        int k = paramInt2;
        int m = paramInt3;
        do {
            for (; ; ) {
                paramGraphics.drawLine(i, j, k, m);
                if (j < paramInt3 + paramInt5) {
                    j += 2;
                } else {
                    i += 2;
                    if (i > paramInt2 + paramInt4) {
                        return;
                    }
                }
                if (k >= paramInt2 + paramInt4) {
                    break;
                }
                k += 2;
            }
            m += 2;
        } while (m <= paramInt3 + paramInt5);
    }

    public static void b(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        paramGraphics.setColor(paramInt1);
        paramGraphics.fillRect(paramInt2 + 2, paramInt3, paramInt4 - 4, 1);
        paramGraphics.fillRect(paramInt2 + 2, paramInt3 + paramInt5 - 1, paramInt4 - 4, 1);
        paramGraphics.fillRect(paramInt2, paramInt3 + 2, 1, paramInt5 - 4);
        paramGraphics.fillRect(paramInt2 + paramInt4 - 1, paramInt3 + 2, 1, paramInt5 - 4);
        paramGraphics.fillRect(paramInt2 + 1, paramInt3 + 1, 1, 1);
        paramGraphics.fillRect(paramInt2 + 1, paramInt3 + paramInt5 - 2, 1, 1);
        paramGraphics.fillRect(paramInt2 + paramInt4 - 2, paramInt3 + 1, 1, 1);
        paramGraphics.fillRect(paramInt2 + paramInt4 - 2, paramInt3 + paramInt5 - 2, 1, 1);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     y

 * JD-Core Version:    0.7.0.1

 */