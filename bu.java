import com.mg.smsgame.main.b;
import com.mic.smsgame.suquan.a;
import com.mic.smsgame.suquan.e;
import com.mic.smsgame.suquan.f;

import java.io.PrintStream;

public class bu
        implements b, e, dy {
    final int a = 0;
    final int b = 6;
    final int c = 10;
    final int d = 7;
    final int e = 8;
    final int f = 1;
    final int g = 2;
    final int k = 3;
    final int l = 4;
    final int m = 5;
    final int n = 11;
    final int o = 9;
    final int p = 12;
    final int q = 13;
    final int r = 14;
    private cq s;
    private i t;
    private ch[] u;
    private int v;
    private int[] w;
    private int x;
    private cc[] y;
    private int z;
    private a A;
    private di B;
    private int C;
    private int D;
    private boolean E = false;
    private int F;
    private int G;
    private int H;
    private int[] I;
    private int[] J;
    private boolean K;
    private boolean L = false;
    private int[] M = {-1, -1, 0, 1, 1, 1, 0 - 1};
    private int[] N = {0, 1, 1, 1, 0, -1, -1, -1};

    public void a(byte[] paramArrayOfByte, int paramInt) {
        if (paramArrayOfByte == null) {
            return;
        }
        this.s.a(0);
        f(7);
        paramInt += this.s.b(paramArrayOfByte, paramInt);
        this.t.l();
        this.B = this.s.k();
        this.t.e(ai.a(paramArrayOfByte, paramInt));
        paramInt += 4;
        this.G = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.H = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        if (this.H > 0) {
            this.t.c(this.H, 6, 6);
        }
        for (int i = 0; i < this.I.length; i++) {
            this.I[i] = ai.a(paramArrayOfByte, paramInt);
            paramInt += 4;
            if (this.I[i] > 0) {
                this.t.a(4, i, this.y, 0);
                this.s.b(i).a(true);
            }
        }
        for (i = 0; i < this.J.length; i++) {
            this.J[i] = ai.a(paramArrayOfByte, paramInt);
            paramInt += 4;
            if (this.J[i] > 0) {
                this.t.a(8, (i + 1) % 2, null, 0);
            }
        }
        this.K = (ai.a(paramArrayOfByte, paramInt) == 1);
        paramInt += 4;
        this.L = (ai.a(paramArrayOfByte, paramInt) == 1);
        dx[] arrayOfdx = this.s.b(0).a;
        if (arrayOfdx[3].b >= 1) {
            this.t.e(true);
        }
        if (arrayOfdx[0].b >= 1) {
            this.t.d(true);
        }
    }

    private void u() {
        if (this.v != 8) {
            return;
        }
        int i = 0;
        byte[] arrayOfByte = new byte[400];
        System.arraycopy(ai.a(dd.c), 0, arrayOfByte, i, 4);
        i += 4;
        i += dd.e.a(arrayOfByte, i);
        i += dd.f.a(arrayOfByte, i);
        i += this.s.a(arrayOfByte, i);
        System.arraycopy(ai.a(this.t.o()), 0, arrayOfByte, i, 4);
        i += 4;
        System.arraycopy(ai.a(this.G), 0, arrayOfByte, i, 4);
        i += 4;
        System.arraycopy(ai.a(this.H), 0, arrayOfByte, i, 4);
        i += 4;
        for (int j = 0; j < this.I.length; j++) {
            System.arraycopy(ai.a(this.I[j]), 0, arrayOfByte, i, 4);
            i += 4;
        }
        for (j = 0; j < this.J.length; j++) {
            System.arraycopy(ai.a(this.J[j]), 0, arrayOfByte, i, 4);
            i += 4;
        }
        System.arraycopy(ai.a(this.K ? 1 : 0), 0, arrayOfByte, i, 4);
        i += 4;
        System.arraycopy(ai.a(this.L ? 1 : 0), 0, arrayOfByte, i, 4);
        i += 4;
        f.a(arrayOfByte, 0, i);
        bg.c(1);
    }

    public bu(cq paramcq, i parami) {
        this.s = paramcq;
        this.t = parami;
        this.w = new int[64];
        this.y = new cc[64];
        for (int i = 0; i < this.y.length; i++) {
            this.y[i] = new cc();
        }
        this.I = new int[2];
        this.J = new int[2];
        b();
        if (!this.s.d) {
            this.t.a("T.Dừng", 2);
            this.t.a("T.Chiêu", 0);
            if (cx.n) {
                this.t.a("Túi", 1);
            }
        }
        v();
        f(0);
    }

    public void a() {
        this.I = new int[2];
        this.J = new int[2];
        this.L = false;
        bn[] arrayOfbn = this.s.d();
        for (int i = 0; i < arrayOfbn.length; i++) {
            arrayOfbn[i].h();
        }
        this.s.b();
        this.t.n();
        this.t.e(dd.g);
        this.G = (this.H = 0);
        this.D = 0;
        dd.j = 0;
        dd.i = 0;
        v();
        f(10);
        dd.h();
    }

    private void v() {
        this.s.a(dd.h ? 0 : 1);
        this.G = (this.H = 1);
    }

    public void b() {
        bn[] arrayOfbn = this.s.d();
        this.u = new ch[2];
        this.u[0] = g(arrayOfbn[0].b(), 0);
        this.u[1] = g(arrayOfbn[1].b(), 1);
        int i = 3;
        arrayOfbn[0].c(arrayOfbn[1].m() + i);
        arrayOfbn[1].c(arrayOfbn[0].m() + i);
    }

    private ch g(int paramInt1, int paramInt2) {
        if (paramInt1 < 6) {
            return new ac(this, paramInt2);
        }
        return new t(this, paramInt2);
    }

    private void w() {
        this.z = 0;
        int i = 0;
        int j = 0;
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = this.A.a;
        int i5 = this.A.b;
        i = this.A.e[0];
        i1 = (i & 0xFF) >= 3 ? 1 : 0;
        if (i1 != 0) {
            i3 += (i & 0xFF);
        }
        j = this.A.e[1];
        i2 = (j & 0xFF) >= 3 ? 1 : 0;
        if (i2 != 0) {
            i3 += (j & 0xFF);
        }
        if (i1 != 0) {
            //(type,x)
            a(this.s.a(i4, i5), i4, i5 - ((i & 0xFF0000) >> 16), 1, i & 0xFF, i3);
        }
        if (i2 != 0) {
            a(this.s.a(i4, i5), i4 - ((j & 0xFF0000) >> 16), i5, j & 0xFF, 1, i3);
        }
        i = 0;
        j = 0;
        i1 = 0;
        i2 = 0;
        i3 = 0;
        i4 = this.A.c;
        i5 = this.A.d;
        i = this.A.e[2];
        i1 = (i & 0xFF) >= 3 ? 1 : 0;
        if (i1 != 0) {
            i3 += (i & 0xFF);
        }
        j = this.A.e[3];
        i2 = (j & 0xFF) >= 3 ? 1 : 0;
        if (i2 != 0) {
            i3 += (j & 0xFF);
        }
        if (i1 != 0) {
            a(this.s.a(i4, i5), i4, i5 - ((i & 0xFF0000) >> 16), 1, i & 0xFF, i3);
        }
        if (i2 != 0) {
            a(this.s.a(i4, i5), i4 - ((j & 0xFF0000) >> 16), i5, j & 0xFF, 1, i3);
        }
    }

    //Must call each frame
    //Start point
    public void c() {
        //delay here
        if (this.C > 0) {
            this.C -= 1;
            return;
        }
        switch (this.v) {
            case 9:
                f(12);
                this.C = this.t.b(this.E);
                if (this.E) {
                    bs.a().d();
                } else {
                    bs.a().e();
                }
                return;
            case 12:
                this.s.b(0).i();
                dd.i = this.s.b(0).f();
                dd.j = this.s.b(0).g();
                ae localae;
                if (this.E) {
                    localae = dd.a(false);
                    localae.c(new da("Đóng", -5));
                    localae.a(this);
                    cz.d().a(localae, true);
                } else {
                    localae = dd.g();
                    if ((dd.a >= 0) || (!bg.l())) {
                        localae.c(new da("Trở về", -4));
                    } else {
                        localae.a(new da("Có", -3));
                        localae.b(new da("Không", -4));
                    }
                    localae.a(this);
                    cz.d().a(localae, true);
                }
                break;
            case 0:
                if (this.s.d) {
                    f(13);
                } else {
                    f(6);
                }
                break;
            case 14:
                if (!this.t.g.m()) {
                    f(13);
                }
                break;
            case 11:
                f(3);
                break;
            case 10:
                f(0);
                break;
            case 6:
                break;
            case 7:
                this.t.c(this.s.a());
                this.t.c(false);
                f(8);
                if (this.s.a() == 0) {
                    this.t.f();
                }
                break;
            case 1:
                w();
                if (this.z > 0) {
                    f(3);
                } else {
                    f(2);
                }
                break;
            case 2:
                f(6);
                break;
            case 8:
                if ((this.t.o() == 1) && (y())) {
                    return;
                }
                int i = this.s.a();
                bn localbn = this.s.b(i);
                if ((!this.s.d) && (localbn.p() <= 0)) {
                    if (i == 0) {
                        this.E = false;
                    } else {
                        this.E = true;
                    }
                    f(9);
                }
                this.u[i].b();
                if ((i == 0) && (this.B != null)) {
                    this.F += 1;
                    if (this.F == 250) {
                        a locala = (a) this.B.b(ay.a(this.B.b()));
                        this.t.b(locala);
                    }
                }
                break;
            case 3:
                x();
                f(4);
                break;
            case 4:
                f(5);
                break;
            case 5:
                f(3);
        }
    }

    private void a(bn parambn, int paramInt) {
        parambn.k(paramInt);
    }

    private void b(bn parambn, int paramInt) {
        parambn.l(paramInt);
    }

    //Resume score
    private void x() {
        int i = this.s.a();
        bn[] arrayOfbn = this.s.d();
        for (int j = 0; j < this.z; j++) {
            cc localcc = this.y[j];
            int i1 = localcc.d > localcc.c ? localcc.d : localcc.c;
            int i2 = i1 * i1 - 2 * i1;
            if (i2 < 0) {
                i2 = 1;
            }
            int i3 = 0;
            switch (e.i[localcc.e]) {
                case 0:
                    int i4 = i == 1 ? 0 : 1;
                    a(arrayOfbn[i4], (i2 + i3) * this.D + arrayOfbn[i].m());
                    if ((this.s.d) && (i == 0) && (arrayOfbn[i].r() <= 0)) {
                        arrayOfbn[i].m(1);
                    }
                    break;
                case 2:
                    b(arrayOfbn[i], i2 * this.D + this.s.b);
                    break;
                case 1:
                    arrayOfbn[i].i(i2 * this.D + this.s.c);
                    break;
                case 3:
                    int i5 = arrayOfbn[i].o() / 12;
                    arrayOfbn[i].f(i2 * this.D);
                    if ((i == 0) && (arrayOfbn[i].o() / 12 != i5)) {
                        this.t.m();
                    }
                    break;
                case 4:
                    arrayOfbn[i].a(i2 * this.D);
                    break;
                case 5:
                    arrayOfbn[i].b(i2 * this.D);
            }
        }
    }

    //Must read here
    //switch (paramInt) int c()
    private void f(int paramInt) {
        this.t.a();
        this.t.h();
        this.t.i();
        int i;
        Object localObject;
        switch (paramInt) {
            case 9:
                this.C = 20;
                this.t.c(true);
                break;
            case 0:
                this.t.c(true);
                this.s.j();
                this.C = this.t.k();
                break;
            case 10:
                this.t.c(true);
                this.C = this.t.j();
                break;
            case 6:
                if (y()) {
                    return;
                }
                this.B = this.s.k();
                if (this.B.b() <= 0) {
                    f(10);
                } else {
                    f(7);
                }
                return;
            case 13:
                this.D = 0;
                this.t.c();
                this.t.a("Bỏ qua", 2);
                int[] arrayOfInt = this.t.g.b();
                this.s.c(arrayOfInt[0]);
                this.s.d(arrayOfInt[1]);
                w localw = this.t.g;
                if (localw != null) {
                    i = localw.g();
                    switch (i) {
                        case 0:
                            localw.d();
                            f(14);
                            localw.c();
                            break;
                        case 6:
                            this.B = null;
                            this.G = 1;
                            this.s.a(0);
                            f(7);
                            break;
                        case 7:
                            this.s.a(true);
                            this.G = 1;
                            this.s.a(0);
                            localObject = new a();
                            ((a) localObject).a = arrayOfInt[0];
                            ((a) localObject).b = arrayOfInt[1];
                            ((a) localObject).c = arrayOfInt[2];
                            ((a) localObject).d = arrayOfInt[3];
                            this.B = new di(1);
                            this.B.a(localObject);
                            f(8);
                            this.t.g();
                            break;
                        case 1:
                            localObject = new a();
                            ((a) localObject).a = arrayOfInt[0];
                            ((a) localObject).b = arrayOfInt[1];
                            ((a) localObject).c = arrayOfInt[2];
                            ((a) localObject).d = arrayOfInt[3];
                            this.B = new di(1);
                            this.B.a(localObject);
                            this.G = 1;
                            this.s.a(0);
                            f(7);
                            this.F = 249;
                            break;
                        case 2:
                            localObject = new a();
                            ((a) localObject).a = arrayOfInt[0];
                            ((a) localObject).b = arrayOfInt[1];
                            ((a) localObject).c = arrayOfInt[2];
                            ((a) localObject).d = arrayOfInt[3];
                            this.B = new di(1);
                            this.B.a(localObject);
                            this.G = 1;
                            this.s.a(1);
                            f(7);
                            break;
                        case 3:
                        case 5:
                            this.B = null;
                            this.G = 1;
                            this.s.a(0);
                            f(8);
                            this.t.i();
                            if (localw.g() == 3) {
                                this.t.a("T.Chiêu", 0);
                            } else if (cx.n) {
                                this.t.a("Túi", 1);
                            }
                            break;
                        case 4:
                            this.E = true;
                            f(12);
                    }
                    return;
                }
                break;
            case 7:
                this.F = 0;
                for (i = 0; i < 2; i++) {
                    if (this.J[i] > 0) {
                        this.J[i] -= 1;
                        if (this.J[i] == 0) {
                            this.C = 8;
                            this.t.a(i);
                        }
                    }
                    if (this.I[i] > 0) {
                        this.I[i] -= 1;
                        if (this.I[i] == 0) {
                            localObject = this.s.b(i);
                            ((bn) localObject).a(false);
                            this.t.b(i);
                        }
                    }
                }
                this.K = false;
                dx[] arrayOfdx = this.s.b(0).a;
                localObject = this.s.b(1).a;
                for (int j = 0; j < arrayOfdx.length; j++) {
                    arrayOfdx[j].a();
                    localObject[j].a();
                }
                if (arrayOfdx[3].b < 1) {
                    this.t.e(false);
                }
                if (arrayOfdx[0].b < 1) {
                    this.t.d(false);
                }
                this.s.a(false);
                if (this.G <= 0) {
                    this.G = 0;
                    j = (this.s.a() + 1) % this.u.length;
                    this.s.a(j);
                    this.C = 8;
                } else {
                    if ((this.G > 1) || (this.H > 1)) {
                        this.t.c(this.G, 6, 6);
                    }
                    this.H = this.G;
                    this.G -= 1;
                }
                this.D = 0;
                break;
            case 8:
                if (this.s.a() == 0) {
                    this.t.b();
                }
                this.u[this.s.a()].a();
                break;
            case 1:
                this.t.a(this.A.a, this.A.b, this.A.c, this.A.d, this.s.a(this.A.a, this.A.b), this.s.a(this.A.c, this.A.d));
                this.C = 10;
                //Swap
                this.s.b(this.A.a, this.A.b, this.A.c, this.A.d);
                this.s.a(false);
                break;
            case 2:
                this.t.a(this.A.a, this.A.b, this.A.c, this.A.d, this.s.a(this.A.a, this.A.b), this.s.a(this.A.c, this.A.d));
                this.s.b(this.A.a, this.A.b, this.A.c, this.A.d);
                this.C = 10;
                bn localbn = this.s.b(this.s.a());
                a(localbn, 3 + localbn.m());
                this.t.b(this.s.a(), 0, 3);
                break;
            case 3:
                System.out.println("********************************getScore**************************************");
                if (this.z <= 0) {
                    if (this.s.d) {
                        this.t.g.c();
                        this.t.g.a();
                        f(10);
                        return;
                    }
                    f(6);
                    return;
                }
                this.C = 0;
                if (this.z > 0) {
                    this.D += 1;
                }
                int[][] arrayOfInt1 = this.s.e();
                int i1 = this.z;
                cc localcc1;
                int i4;
                int i5;
                for (int i2 = 0; i2 < i1; i2++) {
                    localcc1 = this.y[i2];
                    for (i4 = localcc1.a + localcc1.c - 1; i4 >= localcc1.a; i4--) {
                        for (i5 = localcc1.b + localcc1.d - 1; i5 >= localcc1.b; i5--) {
                            if (arrayOfInt1[i4][i5] == 6) {
                                b(6, i4, i5, 1, 1, 1);
                            }
                            // Detroy at here
                            arrayOfInt1[i4][i5] = 7;
                        }
                    }
                }
                int i6;
                for (i2 = i1; i2 < this.z; i2++) {
                    localcc1 = this.y[i2];
                    if (localcc1.e == 6) {
                        for (i4 = 0; i4 < this.M.length; i4++) {
                            i5 = localcc1.a + this.M[i4];
                            i6 = localcc1.b + this.N[i4];
                            if (arrayOfInt1[i5][i6] < 7) {
                                b(arrayOfInt1[i5][i6], i5, i6, 1, 1, 1);
                                //detroy at here
                                arrayOfInt1[i5][i6] = 7;
                            }
                        }
                        localcc1.e = 0;
                    }
                    arrayOfInt1[localcc1.a][localcc1.b] = 7;
                }
                i2 = this.s.a();
                for (int i3 = 0; i3 < this.z; i3++) {
                    cc localcc2 = this.y[i3];
                    i5 = 0;
                    i5 = this.t.a(localcc2, this.s.a());
                    if (i5 > this.C) {
                        this.C = i5;
                    }
                    if (this.D > 1) {
                        this.t.b(this.D, localcc2.a, localcc2.b, localcc2.c, localcc2.d);
                    }
                    if (i2 == 0) {
                        i6 = localcc2.d > localcc2.c ? localcc2.d : localcc2.c;
                        if (i6 >= 5) {
                            int i7 = g(i6);
                            if (i7 >= 0) {
                                this.t.d(i7, localcc2.a + localcc2.c / 2, localcc2.b + localcc2.d / 2);
                            }
                        }
                    }
                }
                bs.a().g();
                break;
            case 4:
                e();
                break;
            case 5:
                f();
                break;
        }
        this.v = paramInt;
    }

    private int g(int paramInt) {
        int i = -1;
        if (this.s.d) {
            i = 1;
            this.s.a[i].b += 1;
        } else {
            int j = (paramInt - 5) * (paramInt - 5) * 15;
            int i1 = ay.a(100);
            if (i1 < 40 + j) {
                i1 = ay.a(100);
                if (i1 < 10 + j) {
                    if (ay.a(100) < 50) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                } else if (i1 < 25 + j) {
                    i = 3;
                } else if (i1 < 45 + j) {
                    i = 5;
                } else if (i1 < 65 + j) {
                    i = 4;
                }
            }
            if (i >= 0) {
                this.s.a[i].b += 1;
                bg.a(i, 1, 6);
            }
        }
        return i;
    }

    //
    public int d() {
        int i = this.s.a();
        bn localbn = this.s.b(i);
        if (localbn.d().length < 1) {
            return 0;
        }
        int j = localbn.o() * 3 / localbn.n();// /36
        return j;
    }

    //Start
    public void a(int paramInt) {
        int i = this.s.a();//Luot danh 0|1
        bn localbn1 = this.s.b(i);//get Info 1
        int j = (this.s.a() + 1) % 2;
        bn localbn2 = this.s.b(j);// get Info 2
        this.z = 0;
        int[] arrayOfInt = localbn1.d();
        if (paramInt > arrayOfInt.length - 1) {
            paramInt = arrayOfInt.length - 1;
        }
        if (paramInt < 0) {
            return;
        }
        int i1 = arrayOfInt[paramInt];
        localbn1.a(localbn1.n() * (paramInt + 1) / 3, true);
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i11;
        int i12;
        int i10;
        //9 skill
        switch (i1) {
            case 0://fire1
                a(localbn2, localbn1.m());
                i2 = ay.a(6) + 2;
                i3 = ay.a(6) + 2;
                for (i4 = i2 + 2; i4 >= i2; i4--) {
                    for (i5 = i3 + 2; i5 >= i3; i5--) {
                        a(this.s.a(i4, i5), i4, i5, 1, 1, 1);
                    }
                }
                break;
            case 1://fire2
                a(localbn2, localbn1.m() * 3 / 2);
                i4 = 0;
                i5 = 2;
                i3 = 2;
                for (i6 = 3; i6 >= 0; i6--) {
                    i4 = ay.a(4);
                    i2 = i4 * 2 + 2;
                    for (i7 = i2 + 1; i7 >= i2; i7--) {
                        for (i8 = i3 + 1; i8 >= i3; i8--) {
                            a(this.s.a(i7, i8), i7, i8, 1, 1, 1);
                        }
                    }
                    if (((ay.a(2) == 0) && (i5 > 0)) || (i5 > i6)) {
                        i5--;
                        i4 = (i4 + 1 + ay.a(3)) % 4;
                        i2 = i4 * 2 + 2;
                        for (i7 = i2 + 1; i7 >= i2; i7--) {
                            for (i8 = i3 + 1; i8 >= i3; i8--) {
                                a(this.s.a(i7, i8), i7, i8, 1, 1, 1);
                            }
                        }
                    }
                    i3 += 2;
                }
                i6 = 3 + ay.a(3);
                h(i6, 4);
                break;
            case 2://fire3
                a(localbn2, localbn1.m() * 5 / 2);
                i2 = 0;
                i3 = 2;
                for (i7 = 0; i7 < 2; i7++) {
                    i2 = ay.a(5) + 2;
                    for (i8 = i2 + 3; i8 >= i2; i8--) {
                        for (int i9 = i3 + 3; i9 >= i3; i9--) {
                            a(this.s.a(i8, i9), i8, i9, 1, 1, 1);
                        }
                    }
                    i3 += 4;
                }
                i6 = 2;
                h(i6, 16);
                break;
            case 3://lightning1
                a(localbn2, localbn1.m() * 8 / 10);
                for (i7 = ay.a(e.h.length); ; i7 = (i7 + 1 + ay.a(e.h.length - 2)) % e.h.length) {
                    i8 = 0;
                    int[][] arrayOfInt1 = this.s.e();
                    for (i11 = 2; i11 < 10; i11++) {
                        for (i12 = 2; i12 < 10; i12++) {
                            if (arrayOfInt1[i11][i12] == i7) {
                                a(i7, i11, i12, 1, 1, 1);
                                i8++;
                            }
                        }
                    }
                    if (i8 > 0) {
                        break;
                    }
                }
                i6 = 4 + ay.a(5);
                h(i6, 1);
                break;
            case 4://lightning2
                this.I[i] += 6;
                localbn1.a(true);
                break;
            case 5://lightning3
                a(localbn2, localbn1.m() * 2);
                for (i8 = 0; i8 < 8; i8 += 4) {
                    for (i10 = 0; i10 < 8; i10 += 4) {
                        i2 = 2 + i8 + ay.a(2);
                        i3 = 2 + i10 + ay.a(2);
                        for (i11 = i2 + 2; i11 >= i2; i11--) {
                            for (i12 = i3 + 2; i12 >= i3; i12--) {
                                a(this.s.a(i11, i12), i11, i12, 1, 1, 1);
                            }
                        }
                    }
                }
                i6 = 3 + ay.a(3);
                h(i6, 9);
                break;
            case 6://ice1
                a(localbn2, localbn1.m() * 8 / 10);
                localbn2.a(8, false);
                i5 = 0;
                i6 = ay.a(2) + 2;
                while (i5 < i6) {
                    i8 = ay.a(8) + 2;
                    i10 = ay.a(8) + 2;
                    i11 = 1;
                    for (i12 = this.z - 1; i12 >= 0; i12--) {
                        if ((this.y[i12].a == i8) && (this.y[i12].b == i10)) {
                            i11 = 0;
                            break;
                        }
                    }
                    if (i11 != 0) {
                        a(this.s.a(i8, i10), i8, i10, 1, 1, 1);
                        i5++;
                    }
                }
                break;
            case 7://ice2
                b(localbn1, localbn1.s() / 10 * 2);
                for (i8 = 2; i8 < 10; i8++) {
                    for (i10 = 2; i10 < 10; i10++) {
                        if (this.s.a(i8, i10) == 2) {
                            a(2, i8, i10, 1, 1, 1);
                        }
                    }
                }
                break;
            case 8://ice3
                a(localbn2, localbn1.m() * 3 / 2);
                localbn2.a(18, false);
                i5 = 0;
                i6 = ay.a(3) + 3;
                while (i5 < i6) {
                    i8 = ay.a(8) + 2;
                    i10 = ay.a(8) + 2;
                    i11 = 1;
                    for (i12 = this.z - 1; i12 >= 0; i12--) {
                        if ((this.y[i12].a == i8) && (this.y[i12].b == i10)) {
                            i11 = 0;
                            break;
                        }
                    }
                    if (i11 != 0) {
                        a(this.s.a(i8, i10), i8, i10, 1, 1, 1);
                        i5++;
                    }
                }
                if (!localbn2.k()) {
                    this.G += 2;
                    if (this.J[j] > 0) {
                        this.J[j] += 2;
                    } else {
                        this.J[j] += 3;
                    }
                }
                break;
        }
        this.C = this.t.a(i1, i, this.y, this.z);
        f(11);
        bs.a().a(i1);
    }

    private void h(int paramInt1, int paramInt2) {
        int i = this.z / paramInt2;
        int j = 0;
        while (j < this.z) {
            int i1 = ay.a(i) * paramInt2;
            for (int i2 = 0; i2 < paramInt2; i2++) {
                int i3 = j + i2;
                int i4 = i1 + i2;
                cc localcc = this.y[i3];
                this.y[i3] = this.y[i4];
                this.y[i4] = localcc;
            }
            j += paramInt2;
        }
        if (paramInt1 < i) {
            this.z = (paramInt1 * paramInt2);
        }
    }

    private boolean y() {
        bn[] arrayOfbn = this.s.d();
        int i = (arrayOfbn[0].r() > 0) && (arrayOfbn[1].r() > 0) ? 0 : 1;
        if ((i != 0) || (this.t.o() == 1)) {
            if (this.s.d) {
                this.E = true;
            } else if (i != 0) {
                if (arrayOfbn[0].r() <= 0) {
                    this.E = false;
                } else {
                    this.E = true;
                }
            } else {
                switch (this.s.c()) {
                    case 2:
                        this.E = false;
                        break;
                    case 3:
                        this.E = true;
                }
            }
            f(9);
            return true;
        }
        return false;
    }

    //Detroy and random new
    public void e() {
        this.x = 0;
        int[][] arrayOfInt = this.s.e();
        int[] arrayOfInt1 = new int[12];
        int[] arrayOfInt2 = new int[12];
        for (int i = 2; i < 10; i++) {
            arrayOfInt1[i] = 9;
        }
        i = 0;
        int i1;
        int i2;
        for (int j = 2; j < 10; j++) {
            i1 = 0;
            for (i2 = 9; i2 >= 2; i2--) {
                if (arrayOfInt[i2][j] < 7)//in board
                {
                    if (i2 != arrayOfInt1[j])//!=9 => in board
                    {
                        this.w[(this.x++)] = (arrayOfInt1[j] << 8 | j);
                        arrayOfInt[arrayOfInt1[j]][j] = arrayOfInt[i2][j];
                        arrayOfInt[i2][j] = 7;
                        this.t.b(i2, j, arrayOfInt1[j], j, arrayOfInt[arrayOfInt1[j]][j], arrayOfInt2[j] + i);
                        arrayOfInt2[j] += 1;
                        i1 = 1;
                    }
                    arrayOfInt1[j] -= 1;
                }
            }
            if (i1 != 0) {
                i++;
            }
        }
        i = 0;
        for (j = 2; j < 10; j++) {
            i1 = 0;
            if (arrayOfInt1[j] >= 2) {
                for (i2 = arrayOfInt1[j]; i2 >= 2; i2--) {
                    this.w[(this.x++)] = (i2 << 8 | j);
                    //Random new here
                    this.s.d(i2, j);
                    this.t.b(i1, j, i2, j, arrayOfInt[i2][j], arrayOfInt2[j] + i);
                    arrayOfInt2[j] += 1;
                    i1--;
                    this.C = 10;
                }
                i++;
            }
        }
    }

    public void f() {
        this.z = 0;
        int[][] arrayOfInt = new int[12][12];
        for (int i = 0; i < this.x; i++) {
            int j = (this.w[i] & 0xFF00) >> 8;
            int i1 = this.w[i] & 0xFF;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            if ((arrayOfInt[j][i1] & 0xFF) != 255) {
                i2 = this.s.e(j, i1);
                i4 = (i2 & 0xFF) >= 3 ? 1 : 0;
                if (i4 != 0) {
                    i6 += (i2 & 0xFF);
                }
                a(arrayOfInt, j, i1 - ((i2 & 0xFF0000) >> 16), i2 & 0xFF);
            }
            if ((arrayOfInt[j][i1] & 0xFF00) != 65280) {
                i3 = this.s.f(j, i1);
                i5 = (i3 & 0xFF) >= 3 ? 1 : 0;
                if (i5 != 0) {
                    i6 += (i3 & 0xFF);
                }
                b(arrayOfInt, j - ((i3 & 0xFF0000) >> 16), i1, i3 & 0xFF);
            }
            if (i4 != 0) {
                a(this.s.a(j, i1), j, i1 - ((i2 & 0xFF0000) >> 16), 1, i2 & 0xFF, i6);
            }
            if (i5 != 0) {
                a(this.s.a(j, i1), j - ((i3 & 0xFF0000) >> 16), i1, i3 & 0xFF, 1, i6);
            }
            arrayOfInt[j][i1] = 65535;
        }
    }

    private void a(int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
        int i = paramInt2 + paramInt3;
        for (int j = paramInt2; j < i; j++) {
            paramArrayOfInt[paramInt1][j] |= 0xFF;
        }
    }

    private void b(int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
        int i = paramInt1 + paramInt3;
        for (int j = paramInt1; j < i; j++) {
            paramArrayOfInt[j][paramInt2] |= 0xFF00;
        }
    }

    private void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        b(e.i[paramInt1], paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        if ((paramInt4 >= 4) || (paramInt5 >= 4)) {
            this.G += 1;
        }
    }

    private void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        cc localcc = this.y[this.z];
        localcc.e = paramInt1;
        localcc.a = paramInt2;
        localcc.c = paramInt4;
        localcc.b = paramInt3;
        localcc.d = paramInt5;
        localcc.f = paramInt6;
        this.z += 1;
    }

    public void b(int paramInt) {
        if (this.v == 14) {
            this.t.g.f();
        } else if ((this.v == 8) && (this.s.a() == 0)) {
            this.u[this.s.a()].a(paramInt);
        }
    }

    public void a(int paramInt1, int paramInt2) {
        if (this.v == 14) {
            this.t.g.f();
        } else if ((this.v == 8) && (this.s.a() == 0)) {
            this.u[this.s.a()].a(paramInt1, paramInt2);
        }
    }

    public void g() {
        if (this.s.d) {
            localae = cz.a("Thông tin", "Bạn có muốn bỏ qua phần luyện tập không?", "Có", -6, "Không", -2, 1);
            localae.a(this);
            this.t.a(false);
            cz.d().a(localae, true);
            return;
        }
        ae localae = cz.a("Thông tin", "Bạn muốn dừng game lại?", "Có", -1, "Không", -2, 1);
        localae.a(this);
        this.t.a(false);
        cz.d().a(localae, true);
    }

    public dl h() {
        return this.t.f;
    }

    //check score apter move a.e[1,2,3,4]: 12 for position 1, 34 for position 2
    public boolean a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        //Training
        if (this.s.d) {
            locala = (a) this.B.b(0);
            if (((locala.a != paramInt1) || (locala.b != paramInt2) || (locala.c != paramInt3) || (locala.d != paramInt4)) && ((locala.a != paramInt3) || (locala.b != paramInt4) || (locala.c != paramInt1) || (locala.d != paramInt2))) {
                this.t.g.e();
                f(14);
                return false;
            }
        }
        //Match
        a locala = this.s.a(paramInt1, paramInt2, paramInt3, paramInt4);
        this.A = locala;
        if (this.A == null) {
            this.A = new a();
            this.A.b = paramInt2;
            this.A.a = paramInt1;
            this.A.d = paramInt4;
            this.A.c = paramInt3;
        }
        //After check, call swap
        f(1);
        return locala != null;
    }

    public void i() {
        if (this.s.d) {
            if (this.t.g.g() == 6) {
                this.t.g.c();
                f(13);
                return;
            }
            if (this.t.g.g() != 1) {
                this.t.g.e();
                f(14);
                return;
            }
        }
        if (this.s.h()) {
            this.s.a(false);
            this.t.e();
        } else {
            this.s.a(true);
            this.t.g();
            this.t.h();
        }
    }

    public void c(int paramInt1, int paramInt2) {
        if ((this.s.d) && (!this.s.h())) {
            this.t.g.e();
            f(14);
            return;
        }
        this.s.c(paramInt1);
        this.s.d(paramInt2);
        this.s.a(false);
        this.t.e();
    }

    public boolean j() {
        return this.s.h();
    }

    public int k() {
        return this.s.f();
    }

    public int l() {
        return this.s.g();
    }

    public boolean m() {
        int i = this.s.f();
        int j = this.s.g();
        return b(i, j, i - 1, j);
    }

    public boolean n() {
        int i = this.s.f();
        int j = this.s.g();
        return b(i, j, i + 1, j);
    }

    public boolean o() {
        int i = this.s.f();
        int j = this.s.g();
        return b(i, j, i, j - 1);
    }

    public boolean p() {
        int i = this.s.f();
        int j = this.s.g();
        return b(i, j, i, j + 1);
    }

    public boolean b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if ((this.s.d) && (!this.s.h())) {
            this.t.g.e();
            f(14);
            return false;
        }
        if (!this.s.c(paramInt3, paramInt4)) {
            return false;
        }
        if (this.s.h()) {
            return a(paramInt1, paramInt2, paramInt3, paramInt4);
        }
        int i = this.s.f();
        int j = this.s.g();
        boolean bool = this.s.b(paramInt3, paramInt4);
        if (bool) {
            this.t.a(i, j);
        }
        return bool;
    }

    public void a(a parama) {
        this.t.a(parama);
    }

    public di q() {
        return this.B;
    }

    public int d(int paramInt1, int paramInt2) {
        return this.s.a(paramInt1, paramInt2);
    }

    public int e(int paramInt1, int paramInt2) {
        int i = this.s.a();
        int j = i == 0 ? 1 : 0;
        bn[] arrayOfbn = this.s.d();
        int i1 = 0;
        switch (paramInt1) {
            case 0:
            case 6:
                if (paramInt1 == 0) {
                    i1 = 9;
                } else {
                    i1 = 10;
                }
                if (arrayOfbn[j].r() <= paramInt2 * paramInt2 - 2 * paramInt2 + arrayOfbn[i].m()) {
                    return 120;
                }
                break;
            case 2:
                i1 = 4;
                int i2 = arrayOfbn[i].r() * 100 / arrayOfbn[i].s();
                if (i2 <= 10) {
                    i1 += 4;
                } else if (i2 <= 30) {
                    i1 += 2;
                }
                break;
            case 1:
                i1 = 3;
                int i3 = arrayOfbn[i].p() * 100 / arrayOfbn[i].q();
                if (i3 <= 10) {
                    i1 += 4;
                } else if (i3 <= 30) {
                    i1 += 2;
                }
                break;
            case 3:
                i1 = 2;
                break;
            case 4:
            case 5:
            default:
                i1 = 1;
        }
        i1 += e.j[paramInt2];
        return i1;
    }

    public void c(int paramInt) {
        bn localbn = this.s.b(this.s.a());
        localbn.j(paramInt);
    }

    public void f(int paramInt1, int paramInt2) {
    }

    public void a(String paramString, int paramInt) {
        if (paramString.equals("skill")) {
            a(paramInt);
        } else {
            d(paramInt);
            this.K = true;
            if (this.s.d) {
                this.t.g.c();
                f(13);
                System.out.println("Usinggggggggggsssssssssssssssssssssssssssssssssss");
            }
        }
    }

    public void d(int paramInt) {
        bn localbn = this.s.b(this.s.a());
        dx[] arrayOfdx = localbn.a;
        for (int i = 0; i < arrayOfdx.length; i++) {
            if (paramInt == arrayOfdx[i].a) {
                arrayOfdx[i].a();
                if (arrayOfdx[i].a == 1) {
                    localbn.n(localbn.e() * 2);
                    localbn.l(localbn.e());
                    this.L = true;
                    break;
                }
                if (arrayOfdx[i].a == 4) {
                    b(localbn, localbn.s() * 1 / 10);
                    this.t.d(this.s.a());
                    break;
                }
                if (paramInt == 3) {
                    arrayOfdx[i].b = 2;
                    this.t.e(true);
                    break;
                }
                if (paramInt != 0) {
                    break;
                }
                arrayOfdx[i].b = 1;
                this.t.d(true);
                break;
            }
        }
        bs.a().h();
    }

    public void r() {
        if ((this.s.d) && (this.t.g.g() != 3)) {
            return;
        }
        int i = d();
        this.t.a(i, this, this.s.b(this.s.a()));
    }

    public void s() {
        int i = this.s.c();
        if ((i == 1) && (this.t.g.g() != 5)) {
            return;
        }
        if (this.K) {
            return;
        }
        if (this.L) {
            this.s.a[1].b = 0;
        }
        this.t.e.a(this.s.a, this);
    }

    public void b(int paramInt1, int paramInt2) {
        switch (paramInt2) {
            case -1:
                cz.d().a(false);
                u();
                cz.a().a(5, 2, true, false);
                break;
            case -2:
                this.t.a(true);
                cz.d().a(true);
                break;
            case -3:
                dd.j();
                cz.d().a(false);
                a();
                break;
            case -5:
            case -4:
                cz.d().a(false);
                cz.a().a(5, 7, true, true);
                break;
            case -8:
                break;
            case -6:
                cz.d().a(false);
                this.E = true;
                f(12);
        }
    }

    public void t() {
        bn[] arrayOfbn = this.s.d();
        for (int i = 0; i < arrayOfbn.length; i++) {
            arrayOfbn[i].i();
        }
        f.f();
        if (this.E) {
            dd.i();
        } else {
            dd.j();
        }
    }

    public int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        if (paramInt1 != 0) {
            return paramInt1;
        }
        int[][] arrayOfInt = this.s.e();
        for (int i = paramInt3 - paramInt4; i < paramInt3; i++) {
            if (arrayOfInt[paramInt2][i] == 6) {
                return 6;
            }
        }
        for (i = paramInt3 + paramInt5; i > paramInt3; i--) {
            if (arrayOfInt[paramInt2][i] == 6) {
                return 6;
            }
        }
        return 0;
    }

    public int b(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        if (paramInt1 != 0) {
            return paramInt1;
        }
        int[][] arrayOfInt = this.s.e();
        for (int i = paramInt2 - paramInt4; i < paramInt2; i++) {
            if (arrayOfInt[i][paramInt3] == 6) {
                return 6;
            }
        }
        for (i = paramInt2 + paramInt5; i > paramInt2; i--) {
            if (arrayOfInt[i][paramInt3] == 6) {
                return 6;
            }
        }
        return 0;
    }

    public boolean e(int paramInt) {
        return ay.a(100) <= this.s.b(paramInt).c();
    }
}