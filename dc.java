import com.mg.smsgame.main.MGMIDlet;
import com.mg.smsgame.main.b;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class dc
        extends aj
        implements b {
    private int E = 1;
    private int F = 2;
    private int G = 3;
    private int H = 4;
    private int I = 5;
    private int J = 6;
    private int K = 7;
    protected int[] c;
    protected int d;
    protected int e = 0;
    protected int f;
    protected int[][] t;
    private String L = null;
    private String[] M;
    protected boolean u = false;
    private boolean N;
    private String[] O;
    protected da v;
    private boolean P;
    private int Q;
    protected cb[] w;
    protected int[] x;
    protected int[] y;
    private boolean R;
    private int S;
    private int T;
    protected Image z;
    protected Image A;
    protected Image[] B;
    protected dj C;
    protected int D = 33;

    private boolean w() {
        byte[] arrayOfByte = bg.q();
        return arrayOfByte == null;
    }

    public dc(int paramInt1, String[] paramArrayOfString, int paramInt2) {
        this.q = 3;
        this.D = paramInt2;
        this.Q = paramInt1;
        if (paramArrayOfString == null) {
            paramArrayOfString = new String[]{"1", "2", "3", "4"};
        }
        this.O = paramArrayOfString;
        b();
        u();
        this.C = new dj("Phần thưởng");
        this.C.a(new da("Đóng", this.K));
        v();
        a(null);
        a(this);
    }

    public final void v() {
        this.L = bg.w();
        this.c = bg.s();
        this.e = 0;
        for (int i = this.c.length - 1; i > -1; i--) {
            if (this.c[i] < 0) {
                this.e = i;
                break;
            }
        }
        z();
        this.P = bg.a(this.c);
        if (this.P) {
            byte[] arrayOfByte = bg.u();
            if (arrayOfByte == null) {
                bg.x();
                MGMIDlet.f().d();
                return;
            }
            this.R = false;
            this.u = true;
            if (this.L != null) {
                this.M = bz.a("Chúc mừng bạn đã sưu tập đủ bộ Vật phẩm. Hãy bấm nút gửi để nhận phần thưởng: " + this.L, this.b_ - 10);
            }
            b(null);
            c(null);
            a(null);
            c(new da("Gửi", this.F));
        }
    }

    protected abstract void b();

    private void x() {
        a(new da("Cần biết", this.J));
    }

    private void y() {
        b(new da("Trở về", this.I));
    }

    protected void u() {
        this.t = new int[10][4];
        this.t[0] = {-1, -1, 1, -1};
        this.t[1] = {0, 3, 8, 2};
        this.t[2] = {1, 1, 4, -1};
        this.t[3] = {1, -1, 5, 1};
        this.t[4] = {2, 5, 6, -1};
        this.t[5] = {3, -1, 7, 4};
        this.t[6] = {4, 8, 8, -1};
        this.t[7] = {5, -1, 8, 8};
        this.t[8] = {1, 7, 9, 6};
        this.t[9] = {8, -1, -1, -1};
        int i = this.b_ / 2 - 10;
        int j = this.c_ / 2 - 10;
        this.x = new int[]{i, i, i - 40, i + 40, i - 50, i + 50, i - 40, i + 40, i, i};
        this.y = new int[]{j - 80, j - 50, j - 40, j - 40, j, j, j + 40, j + 40, j + 50, j + 80};
    }

    private void z() {
        this.N = w();
        this.w = new cb[this.x.length];
        for (int i = 0; i < this.w.length; i++) {
            Image localImage = null;
            if (this.c[i] == -1) {
                localImage = this.A;
            } else if (this.c[i] == 0) {
                localImage = this.z;
            } else {
                localImage = this.B[(this.c[i] - 1)];
            }
            this.w[i] = new cb(localImage, this.x[i], this.y[i]);
            this.w[i].k(this.D);
        }
        if (!this.N) {
            y();
        }
        x();
        this.v = new da("Gửi", this.E);
        c(null);
        if (this.c[this.e] < 0) {
            c(this.v);
        }
    }

    public void a(int paramInt) {
        if ((this.P) || (this.R)) {
            return;
        }
        if (paramInt == 14) {
            if (this.t[this.e][3] > -1) {
                this.e = this.t[this.e][3];
            }
        } else if (paramInt == 15) {
            if (this.t[this.e][1] > -1) {
                this.e = this.t[this.e][1];
            }
        } else if (paramInt == 12) {
            if (this.t[this.e][0] > -1) {
                this.e = this.t[this.e][0];
            }
        } else if ((paramInt == 13) && (this.t[this.e][2] > -1)) {
            this.e = this.t[this.e][2];
        }
        if (this.c[this.e] < 0) {
            c(this.v);
        } else {
            c(null);
        }
    }

    public void a(int paramInt1, int paramInt2) {
        if ((this.P) || (this.R)) {
            return;
        }
        for (int i = 0; i < this.w.length; i++) {
            if ((paramInt1 > this.w[i].i()) && (paramInt1 < this.w[i].i() + this.w[i].l()) && (paramInt2 > this.w[i].j()) && (paramInt2 < this.w[i].j() + this.w[i].l())) {
                if (this.e == i) {
                    cx.u[16] = 2;
                } else {
                    this.e = i;
                }
            }
        }
    }

    public void a(Graphics paramGraphics) {
        e(paramGraphics);
        b(paramGraphics);
    }

    protected void e(Graphics paramGraphics) {
        if (this.r == null) {
            paramGraphics.setColor(cx.S);
            paramGraphics.fillRect(0, 0, this.b_, this.c_);
        }
    }

    protected void b(Graphics paramGraphics) {
        for (int i = 0; i < this.w.length; i++) {
            if ((this.R) && (i == this.d)) {
                if (this.T > 0) {
                    if (this.T % 2 == 0) {
                        this.w[i].a(paramGraphics);
                    }
                } else {
                    int j = this.w[i].k();
                    int k = this.w[i].l();
                    paramGraphics.setClip(this.x[i] + j / 2 - j * this.S / 20, this.y[i] + k / 2 - k * this.S / 20, j * this.S / 10, k * this.S / 10);
                    this.w[i].a(paramGraphics);
                    paramGraphics.setClip(0, 0, this.b_, this.c_);
                }
            } else {
                this.w[i].a(paramGraphics);
            }
        }
        if (this.R) {
            return;
        }
        a(paramGraphics, this.w[this.e].i() + this.w[this.e].l() / 2, this.w[this.e].j() + this.w[this.e].l() / 2);
        if (this.u) {
            bz.a(bz.c);
            if (this.M != null) {
                bz.a(paramGraphics, bz.d, this.M, 5, 5, this.b_ - 10, this.c_, 1);
            }
        }
    }

    protected void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        paramGraphics.setColor(16776960);
        paramGraphics.fillTriangle(paramInt1, paramInt2 - 10 - this.f, paramInt1 - 4, paramInt2 - 14 - this.f, paramInt1 + 4, paramInt2 - 14 - this.f);
        paramGraphics.fillTriangle(paramInt1, paramInt2 + 10 + this.f, paramInt1 - 5, paramInt2 + 15 + this.f, paramInt1 + 4, paramInt2 + 15 + this.f);
        paramGraphics.fillTriangle(paramInt1 - 10 - this.f, paramInt2, paramInt1 - 14 - this.f, paramInt2 - 4, paramInt1 - 14 - this.f, paramInt2 + 4);
        paramGraphics.fillTriangle(paramInt1 + 10 + this.f, paramInt2, paramInt1 + 15 + this.f, paramInt2 - 5, paramInt1 + 15 + this.f, paramInt2 + 4);
    }

    public void a() {
        for (int i = 0; i < this.w.length; i++) {
            this.w[i].a();
        }
        if (this.R) {
            if (this.T > 0) {
                this.T -= 1;
            } else if (this.S < 10) {
                this.S += 1;
            } else {
                this.R = false;
            }
        }
        if (this.f < 4) {
            this.f += 1;
        } else {
            this.f = 0;
        }
    }

    public void e(int paramInt) {
        this.R = true;
        this.T = 10;
        this.S = 0;
        this.d = paramInt;
    }

    public void b(int paramInt1, int paramInt2) {
        Object localObject1;
        if (paramInt2 == this.E) {
            if (this.c[this.e] < 0) {
                localObject1 = cz.a("Thông tin", "Có phải bạn muốn đổi vật phẩm? Tham khảo giá tại http://free.ola.vn.", "Có", this.G, "Không", this.H, 1);
                ((ar) localObject1).a(this);
                cz.d().a((ae) localObject1);
            }
        } else {
            Object localObject2;
            if (paramInt2 == this.F) {
                localObject1 = bg.u();
                if (localObject1 == null) {
                    bg.x();
                    MGMIDlet.f().d();
                    return;
                }
                localObject2 = new String((byte[]) localObject1);
                an.a((String) localObject2, new f(this));
            } else if (paramInt2 == this.G) {
                bg.e(this.e);
            } else if (paramInt2 == this.H) {
                cz.d().a(true);
            } else if (paramInt2 == this.I) {
                if (this.Q == -1) {
                    this.Q = 2;
                }
                f(this.Q);
            } else if (paramInt2 == this.J) {
                localObject1 = "Bạn cố gắng sưu tập đủ bộ vật phẩm như sau để nhận ";
                if (this.L == null) {
                    localObject1 = localObject1 + "các phần quà giá trị";
                } else {
                    localObject1 = localObject1 + this.L;
                }
                localObject2 = new dt((String) localObject1, 0);
                dt localdt1 = new dt(this.O[0], 0);
                x localx1 = new x(this.B[0]);
                dt localdt2 = new dt(this.O[1], 0);
                x localx2 = new x(this.B[1]);
                dt localdt3 = new dt(this.O[2], 0);
                x localx3 = new x(this.B[2]);
                dt localdt4 = new dt(this.O[3], 0);
                x localx4 = new x(this.B[3]);
                dt localdt5 = new dt("Chú Ý: Phần quà sẽ thay đổi khi bạn nhận được vật phẩm mới. Để tránh bị mất quà thưởng, bạn không nên thay đổi số điện thoại trong quá trình chơi.", 0);
                localdt5.a(bz.c);
                this.C.m();
                this.C.b(new da("Đóng", this.K));
                this.C.a(new e[]{localObject2, localdt1, localx1, localdt2, localx2, localdt3, localx3, localdt4, localx4, localdt5});
                this.C.a(this);
                cz.d().a(this.C);
                if (this.N) {
                    bg.r();
                    this.N = false;
                    y();
                }
            } else if (paramInt2 == this.K) {
                cz.d().a(false);
            }
        }
    }

    public void f(int paramInt) {
        cz.d().e.a(3, this.Q, true);
    }

    static void a(dc paramdc, String paramString) {
        paramdc.L = paramString;
    }

    static void a(dc paramdc, boolean paramBoolean) {
        paramdc.P = paramBoolean;
    }

    static void a(dc paramdc) {
        paramdc.z();
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dc

 * JD-Core Version:    0.7.0.1

 */