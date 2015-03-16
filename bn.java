import java.io.PrintStream;

public class bn {
    private int b; // Max health
    private int c;// Heath
    private int d;
    private boolean e = false;
    private int f = 7500;
    private int g = 0;
    private int h = 36;// Max mana
    private int i = 0;// mana
    public dx[] a;// item sport
    private int j;
    private int k;
    private int l;
    private c m;
    private int n;

    public int a(byte[] paramArrayOfByte, int paramInt) {
        int i1 = paramInt;
        System.arraycopy(ai.a(this.c), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.b), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.i), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.g), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.j), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.k), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        for (int i2 = 0; i2 < this.a.length; i2++) {
            paramInt += this.a[i2].a(paramArrayOfByte, paramInt);
            System.out.println("offset=" + paramInt);
        }
        return paramInt - i1;
    }

    public int b(byte[] paramArrayOfByte, int paramInt) {
        int i1 = paramInt;
        this.c = (this.d = ai.a(paramArrayOfByte, paramInt));
        paramInt += 4;
        this.b = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.i = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.g = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.j = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.k = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        for (int i2 = 0; i2 < this.a.length; i2++) {
            paramInt += this.a[i2].b(paramArrayOfByte, paramInt);
        }
        return paramInt - i1;
    }

    public bn(c paramc) {
        this.m = paramc;
        int i1 = paramc.e;
        if (i1 < 30) {
            this.n = 70;
        } else if (i1 < 60) {
            this.n = 50;
        } else {
            this.n = 30;
        }
        a();
    }

    public void a() {
        this.b = this.m.b;
        this.c = (this.d = this.m.c);
        this.i = 0;
        this.g = this.f;
        this.e = false;
        this.a = new dx[6];
        for (int i1 = 0; i1 < this.a.length; i1++) {
            this.a[i1] = new dx();
            this.a[i1].a = i1;
        }
        this.k = 0;
        this.j = 0;
    }

    public int b() {
        return this.m.a;
    }

    public int c() {
        return this.n;
    }

    public int[] d() {
        return this.m.f;
    }

    public int e() {
        return this.m.b;
    }

    public int f() {
        return this.j;
    }

    public void a(int paramInt) {
        this.j += paramInt;
    }

    public int g() {
        return this.k;
    }

    public void b(int paramInt) {
        this.k += paramInt;
    }

    public void h() {
        this.m.c = this.m.b;
    }

    public void i() {
        if (this.c > this.m.b) {
            this.c = this.m.b;
        }
        this.m.c = this.c;
    }

    public int j() {
        return this.l;
    }

    public void c(int paramInt) {
        this.l = paramInt;
    }

    public boolean k() {
        return this.e;
    }

    public void a(boolean paramBoolean) {
        this.e = paramBoolean;
    }

    public int l() {
        return this.d;
    }

    public void d(int paramInt) {
        this.d = paramInt;
    }

    public int m() {
        if (this.a[0].b > 0) {
            return this.m.d + this.m.d;
        }
        return this.m.d;
    }

    public int n() {
        return this.h;
    }

    public int o() {
        return this.i;
    }

    public void e(int paramInt) {
        if (paramInt < 0) {
            paramInt = 0;
        } else if (paramInt > this.h) {
            paramInt = this.h;
        }
        this.i = paramInt;
    }

    public void f(int paramInt) {
        e(this.i + paramInt);
    }

    public void a(int paramInt, boolean paramBoolean) {
        if ((this.e) && (!paramBoolean)) {
            return;
        }
        e(this.i - paramInt);
    }

    public int p() {
        return this.g;
    }

    public void g(int paramInt) {
        if (paramInt < 0) {
            paramInt = 0;
        } else if (paramInt > this.f) {
            paramInt = this.f;
        }
        this.g = paramInt;
    }

    public int q() {
        return this.f;
    }

    public void h(int paramInt) {
        this.f = paramInt;
        this.g = this.f;
    }

    public void i(int paramInt) {
        g(this.g + paramInt);
    }

    public void j(int paramInt) {
        g(this.g - paramInt);
    }

    public int r() {
        return this.c;
    }

    public void k(int paramInt) {
        if (this.e) {
            return;
        }
        if (this.a[3].b > 0) {//Shield
            paramInt = paramInt * 1 / 4;
        }
        m(this.c - paramInt);
    }

    public void l(int paramInt) {
        m(this.c + paramInt);
    }

    public void m(int paramInt) {
        if (paramInt < 0) {
            paramInt = 0;
        } else if (paramInt > this.b) {
            paramInt = this.b;
        }
        this.c = paramInt;
    }

    public int s() {
        return this.b;
    }

    public void n(int paramInt) {
        this.b = paramInt;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     bn

 * JD-Core Version:    0.7.0.1

 */