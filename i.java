import com.mic.smsgame.suquan.a;
import com.mic.smsgame.suquan.b;
import com.mic.smsgame.suquan.e;
import com.mic.smsgame.suquan.p;
import com.mic.smsgame.suquan.s;

import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class i
        implements e {
    cq a;
    bm b;
    bh c;
    private Image k;
    private di l;
    private Image m;
    private Image[] n;
    private di o;
    private di p;
    private g q;
    public bx d;
    private dl r;
    private di s;
    private dn t;
    private k u;
    private String[] v = new String[3];
    private int w;
    private int x;
    private db y;
    private ah z;
    public ck e;
    private cj A;
    private boolean B = true;
    public dl f;
    public w g;

    public void a(String paramString, int paramInt) {
        this.v[paramInt] = paramString;
    }

    public void a() {
        this.w = 0;
    }

    public void b() {
        this.x = 15;
        this.w = (30 + this.x);
        if (cx.n) {
            this.w = -1;
        }
    }

    public void c() {
        this.v = new String[3];
    }

    public i(cq paramcq) {
        try {
            //Board
            this.a = paramcq;
            p();
            this.r = new dl();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void p() {
        this.l = new di();
        this.o = new di();
        this.p = new di();
        this.s = new di();
        this.u = new k(cx.f / 2, cx.g / 2 - 15);
        this.y = new db();
        this.y.a(30);
        this.m = bs.a().b;
        this.n = bs.a().c;
        this.b = new bm(cx.l + 36 + 10, cx.m + 54 + 10);
        this.c = new bh();
        com.mic.smsgame.suquan.i locali = this.b.a(0, 0);
        this.c.a(locali.i(), locali.j());
        bn[] arrayOfbn = this.a.d();
        this.d = new bx(arrayOfbn);
        this.d.d(this.b.i() - 18, this.b.j() + 167);
        ax[] arrayOfax = this.d.a();
        Image[] arrayOfImage = bs.a().a(arrayOfbn[0].b(), arrayOfbn[1].b());
        Graphics localGraphics = this.m.getGraphics();
        for (int i = 0; i < arrayOfImage.length; i++) {
            localGraphics.drawImage(arrayOfImage[i], arrayOfax[i].i(), arrayOfax[i].j(), 0);
        }
        arrayOfImage = (Image[]) null;
        this.k = bs.a().a;
        this.q = new g();
        bs.a().a(arrayOfbn[0].d());
        bs.a().a(arrayOfbn[1].d());
        bs.a().c(arrayOfbn[0].d());
        this.z = new ah(this.a.b(0).d());
        this.e = new ck();
        this.A = new cj(cx.f >> 1, this.b.j() + 163);
        this.A.b(dd.g);
        this.t = new dn(bs.a().E);
        this.f = new dl(this.b.i() - 10, this.b.j() - 10, this.b.k(), this.b.l());
        System.out.println("+++++++idMath+++++++++=" + this.a.c());
        if (this.a.d) {
            this.g = new w();
        }
    }

    public void d() {
        if (!this.B) {
            return;
        }
        this.b.a();
        this.c.a();
        for (int i = 0; i < this.l.b(); i++) {
            ((b) this.l.b(i)).b();
        }
        for (i = 0; i < this.o.b(); i++) {
            ((p) this.o.b(i)).a();
        }
        for (i = 0; i < this.p.b(); i++) {
            ((ab) this.p.b(i)).e();
        }
        for (i = this.s.b() - 1; i >= 0; i--) {
            ((s) this.s.b(i)).a();
        }
        this.q.b();
        this.d.b();
        this.u.e();
        if (this.w > 0) {
            this.w -= 1;
            if (this.w < 15) {
                this.x -= 1;
            }
        }
        this.y.a();
        this.A.b();
        this.t.a();
        if (this.g != null) {
            this.g.h();
        }
    }

    public void a(Graphics paramGraphics) {
        paramGraphics.drawImage(this.m, 0, 0, 0);
        if (this.B) {
            y.a(paramGraphics, this.r);
            y.a(paramGraphics, this.r, cx.l + 30, cx.m + 51, 180, cx.g);
            this.b.a(paramGraphics);
            y.b(paramGraphics, this.r);
            this.c.a(paramGraphics);
            this.q.a(paramGraphics);
        }
        this.d.a(paramGraphics);
        if (!this.B) {
            return;
        }
        this.y.a(paramGraphics, 0, 0);
        for (int i = 0; i < this.l.b(); i++) {
            ((b) this.l.b(i)).a(paramGraphics, 0, 0);
        }
        for (i = this.s.b() - 1; i >= 0; i--) {
            ((s) this.s.b(i)).a(paramGraphics);
        }
        for (i = 0; i < this.o.b(); i++) {
            ((p) this.o.b(i)).a(paramGraphics, 0, 0);
        }
        for (i = 0; i < this.p.b(); i++) {
            ((ab) this.p.b(i)).a(paramGraphics);
        }
        this.u.a(paramGraphics);
        this.A.a(paramGraphics);
        this.t.a(paramGraphics);
        if (this.g != null) {
            this.g.a(paramGraphics);
        }
        if (this.w != 0) {
            if (this.v[0] != null) {
                bz.d.a(paramGraphics, this.v[0], 0, cx.g - this.x, 0);
            }
            if (this.v[1] != null) {
                bz.d.a(paramGraphics, this.v[1], cx.f / 2, cx.g - this.x, 1);
            }
            if (this.v[2] != null) {
                bz.d.a(paramGraphics, this.v[2], cx.f, cx.g - this.x, 2);
            }
        }
    }

    public void e() {
        this.c.b(0);
        com.mic.smsgame.suquan.i locali = this.b.a(this.a.f() - 2, this.a.g() - 2);
        this.c.a(locali.i(), locali.j());
    }

    public void f() {
        this.c.b(0);
        com.mic.smsgame.suquan.i locali = this.b.a(this.a.f() - 2, this.a.g() - 2);
        this.c.b(locali.i(), locali.j());
    }

    public void g() {
        this.c.b(0);
        int i = this.a.f();
        int j = this.a.g();
        com.mic.smsgame.suquan.i locali = this.b.a(i - 2, j - 2);
        int i1 = 0;
        if (j > 2) {
            i1 |= 0x1;
        }
        if (j < 9) {
            i1 |= 0x2;
        }
        if (i > 2) {
            i1 |= 0x4;
        }
        if (i < 9) {
            i1 |= 0x8;
        }
        this.c.a(i1, locali.i(), locali.j());
    }

    private int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        int i = 0;
        if (paramInt2 < paramInt4) {
            i = 2;
        } else if (paramInt2 > paramInt4) {
            i = 1;
        } else if (paramInt1 < paramInt3) {
            i = 8;
        } else if (paramInt1 > paramInt3) {
            i = 4;
        }
        return i;
    }

    public void a(int paramInt1, int paramInt2) {
        this.c.b(0);
        int i = this.a.f();
        int j = this.a.g();
        com.mic.smsgame.suquan.i locali1 = this.b.a(i - 2, j - 2);
        com.mic.smsgame.suquan.i locali2 = this.b.a(paramInt1 - 2, paramInt2 - 2);
        this.c.b(locali2.i(), locali2.j(), locali1.i(), locali1.j());
    }

    public void a(a parama) {
        int i = parama.a;
        int j = parama.b;
        int i1;
        if (((parama.e[0] & 0xFF) >= 3) || ((parama.e[1] & 0xFF) >= 3)) {
            i1 = a(parama.c, parama.d, parama.a, parama.b);
            i = parama.c;
            j = parama.d;
        } else {
            i1 = a(parama.a, parama.b, parama.c, parama.d);
        }
        this.c.b(1);
        com.mic.smsgame.suquan.i locali = this.b.a(i - 2, j - 2);
        this.c.a(i1, locali.i(), locali.j());
    }

    public void b(a parama) {
        int i = parama.a;
        int j = parama.b;
        int i1;
        if (((parama.e[0] & 0xFF) >= 3) || ((parama.e[1] & 0xFF) >= 3)) {
            i1 = a(parama.c, parama.d, parama.a, parama.b);
            i = parama.c;
            j = parama.d;
        } else {
            i1 = a(parama.a, parama.b, parama.c, parama.d);
        }
        int i2 = (j - 2) * 21 + this.b.i();
        int i3 = (i - 2) * 21 + this.b.j();
        this.q.a(i2, i3, i1);
    }

    public void h() {
        this.q.a();
    }

    public void i() {
        this.c.b();
    }

    public int j() {
        int i = 0;
        for (int j = 0; j < 8; j++) {
            int i1 = 0 + i;
            for (int i2 = 0; i2 < 8; i2++) {
                com.mic.smsgame.suquan.i locali = this.b.a(j, i2);
                locali.b(21, i1);
                i1++;
            }
            i++;
        }
        return 22;
    }

    public int k() {
        int i = 0;
        int[][] arrayOfInt = this.a.e();
        int[] arrayOfInt1 = new int[12];
        for (int j = 2; j < 10; j++) {
            int i1 = 0;
            for (int i2 = 9; i2 >= 2; i2--) {
                b(i1, j, i2, j, arrayOfInt[i2][j], arrayOfInt1[j] + i);
                arrayOfInt1[j] += 1;
                i1--;
            }
            i++;
        }
        return 21;
    }

    public void l() {
        int[][] arrayOfInt = this.a.e();
        for (int i = 2; i < 10; i++) {
            for (int j = 9; j >= 2; j--) {
                a(j, i, arrayOfInt[j][i]);
            }
        }
    }

    public void a(boolean paramBoolean) {
        this.B = paramBoolean;
    }

    public void a(int paramInt1, int paramInt2, int paramInt3) {
        this.b.a(paramInt1, paramInt2, paramInt3);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        this.b.a(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        this.b.b(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public int a(cc paramcc, int paramInt) {
        if (e.i[paramcc.e] == 0) {
            paramInt = (paramInt + 1) % 2;
            b(paramcc, paramInt);
        }
        ax localax = this.d.a()[paramInt];
        int i = localax.i() + localax.k() / 2;
        int j = 35;
        if ((paramcc.e != 4) && (paramcc.e != 5)) {
            if (paramInt == 0) {
                i += j;
            } else {
                i -= j + 20;
            }
        }
        int i1 = localax.j();
        switch (paramcc.e) {
            case 4:
            case 5:
                i1 += localax.l() / 2;
                break;
            case 2:
                this.d.a(12);
                i1 += 0;
                break;
            case 1:
                i1 += 8;
                break;
            case 3:
                i1 += 16;
        }
        int i2 = this.b.i();
        int i3 = this.b.j();
        int i4 = paramcc.a - 2;
        int i5 = paramcc.b - 2;
        int i6 = 0;
        int i7 = i3 + i4 * 21;
        int i8 = i4 + paramcc.c;
        int i9 = i5 + paramcc.d;
        for (int i10 = i4; i10 < i8; i10++) {
            i6 = i2 + i5 * 21;
            for (int i11 = i5; i11 < i9; i11++) {
                a(i6, i7, i, i1, paramcc.e);
                i6 += 21;
            }
            i7 += 21;
        }
        return this.b.a(paramcc, i, i1);
    }

    public int b(boolean paramBoolean) {
        Image localImage;
        if (paramBoolean) {
            localImage = az.b("/strwin");
        } else {
            localImage = az.b("/strlose");
        }
        return this.u.a(localImage);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        if ((paramInt5 == 0) || (paramInt5 > 5)) {
            return;
        }
        Object localObject = null;
        Image localImage = this.n[paramInt5];
        if (localImage == null) {
            return;
        }
        for (int i = this.s.b() - 1; i > -1; i--) {
            s locals = (s) this.s.b(i);
            if (!locals.m()) {
                localObject = locals;
                break;
            }
        }
        if (localObject == null) {
            localObject = new s();
            this.s.a(localObject);
        }
        ((s) localObject).a(paramInt1, paramInt2, paramInt3, paramInt4, localImage);
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        Object localObject = null;
        for (int i = this.l.b() - 1; i > -1; i--) {
            b localb = (b) this.l.b(i);
            if (!localb.a()) {
                localObject = localb;
                break;
            }
        }
        if (localObject == null) {
            localObject = new b(this.k);
            this.l.a(localObject);
        }
        i = (paramInt3 + paramInt5 / 2 - 2) * 21 - (paramInt5 % 2 == 0 ? 10 : 4) + this.b.i();
        int j = (paramInt2 + paramInt4 / 2 - 2) * 21 - (paramInt4 % 2 == 0 ? 10 : 5) + this.b.j();
        int i1 = paramInt1 - 1;
        ((b) localObject).a(i, j, i1, "x" + paramInt1);
    }

    public void b(cc paramcc, int paramInt) {
        Image localImage = this.n[0];
        ax[] arrayOfax = this.d.a();
        int i = arrayOfax[paramInt].i() + 3 + ay.a(arrayOfax[paramInt].k() - 6);
        int j = arrayOfax[paramInt].j() + 3 + ay.a(arrayOfax[paramInt].l() - 6);
        Object localObject = null;
        int i1 = paramcc.a + paramcc.c;
        int i2 = paramcc.b + paramcc.d;
        for (int i3 = paramcc.a; i3 < i1; i3++) {
            for (i4 = paramcc.b; i4 < i2; i4++) {
                localObject = null;
                com.mic.smsgame.suquan.i locali = this.b.a(i3 - 2, i4 - 2);
                locali.a_(0);
                for (int i6 = this.o.b() - 1; i6 > -1; i6--) {
                    p localp = (p) this.o.b(i6);
                    if (!localp.m()) {
                        localObject = localp;
                        break;
                    }
                }
                if (localObject == null) {
                    localObject = new p(localImage);
                    this.o.a(localObject);
                }
                ((p) localObject).b(locali.i(), locali.j(), i, j);
            }
        }
        this.d.b(paramInt, 36);
        this.d.a(26);
        ax localax = arrayOfax[paramInt];
        int i4 = localax.i() + localax.k() / 2;
        for (int i5 = 0; i5 < this.p.b(); i5++) {
            ab localab = (ab) this.p.b(i5);
            if ((localab.b() == 4) && (localab.i() == i4)) {
                (( do)localab).e(26);
            }
        }
    }

    public void a(int paramInt) {
        ax localax = this.d.a()[paramInt];
        int i = localax.i() + localax.k() / 2;
        for (int j = 0; j < this.p.b(); j++) {
            ab localab = (ab) this.p.b(j);
            if ((localab.m()) && (localab.b() == 8) && (localab.i() == i)) {
                localab.a_(1);
            }
        }
    }

    public void b(int paramInt) {
        ax localax = this.d.a()[paramInt];
        int i = localax.i() + localax.k() / 2;
        for (int j = 0; j < this.p.b(); j++) {
            ab localab = (ab) this.p.b(j);
            if ((localab.b() == 4) && (localab.i() == i)) {
                (( do)localab).d(15);
            }
        }
    }

    public void b(int paramInt1, int paramInt2, int paramInt3) {
        this.d.b(paramInt1, paramInt2);
        this.d.a(paramInt3);
    }

    public int a(int paramInt1, int paramInt2, cc[] paramArrayOfcc, int paramInt3) {
        int i = 0;
        int j = 0;
        int i1 = (paramInt2 + 1) % 2;
        int i2;
        int i3;
        ax localax;
        int i4;
        int i5;
        com.mic.smsgame.suquan.i locali;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        bn localbn;
        int i13;
        switch (paramInt1) {
            case 0:
                j = 0;
                i = 0;
                i2 = -300;
                i3 = 300;
                if (paramInt2 == 0) {
                    i2 = -i2;
                }
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                i = a(i, paramInt1, i4 - i2, i5 - i3, i4, i5, j);
                b(i1, j + 28, j + 18);
                j = ay.a(5) + 7;
                locali = this.b.a(paramArrayOfcc[4].a - 2, paramArrayOfcc[4].b - 2);
                i4 = locali.i();
                i5 = locali.j();
                i = a(i, paramInt1, i4 - i2, i5 - i3, i4, i5, j);
                j += 18;
                break;
            case 2:
                j = 0;
                i = 0;
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                i = a(i, paramInt1, i4, i5, i4, i5, j);
                b(i1, j + 10, j + 4);
                int i6 = paramInt3 / 16;
                int i7 = 5;
                for (i8 = 0; i8 < i6; i8++) {
                    locali = this.b.a(paramArrayOfcc[i7].a - 2, paramArrayOfcc[i7].b - 2);
                    i4 = locali.i();
                    i5 = locali.j();
                    i9 = ay.a(15) + 4;
                    i = a(i, paramInt1, i4, i5, i4, i5, i9);
                    if (i9 > j) {
                        j = i9;
                    }
                    i7 += 16;
                }
                j += 6;
                break;
            case 1:
                j = 0;
                i = 0;
                i2 = -300;
                i3 = 300;
                i4 = 0;
                i5 = 0;
                if (paramInt2 == 0) {
                    i2 = -i2;
                }
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                i = a(i, paramInt1, i4 - i2, i5 - i3, i4, i5, j);
                b(i1, j + 28, j + 18);
                i8 = 0;
                i9 = paramInt3 / 4;
                for (i10 = 0; i10 < i9; i10++) {
                    locali = this.b.a(paramArrayOfcc[i8].a - 2, paramArrayOfcc[i8].b - 2);
                    i4 = locali.i() - 10;
                    i5 = locali.j() - 10;
                    i11 = ay.a(15) + 7;
                    i = a(i, paramInt1, i4 - i2, i5 - i3, i4, i5, i11);
                    if (i11 > j) {
                        j = i11;
                    }
                    i8 += 4;
                }
                j += 16;
                break;
            case 5:
                j = 0;
                i = 0;
                i4 = 0;
                i5 = 0;
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                i = a(i, paramInt1, i4, i5, i4, i5, j);
                b(i1, j + 12, j + 10);
                i8 = 4;
                i10 = paramInt3 / 9;
                for (i11 = 0; i11 < i10; i11++) {
                    locali = this.b.a(paramArrayOfcc[i8].a - 2, paramArrayOfcc[i8].b - 2);
                    i4 = locali.i();
                    i5 = locali.j();
                    i12 = ay.a(20) + 6;
                    i = a(i, paramInt1, i4, i5, i4, i5, i12);
                    if (i12 > j) {
                        j = i12;
                    }
                    i8 += 9;
                }
                j += 5;
                break;
            case 6:
                j = 0;
                i = 0;
                i2 = -55;
                i3 = 300;
                i4 = 0;
                i5 = 0;
                if (paramInt2 == 0) {
                    i2 = -i2;
                }
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                i = a(i, 6, i4 - i2, i5 - i3, i4, i5, j);
                b(i1, j + 28, j + 18);
                j = ay.a(5) + 7;
                i5 = localax.j() + 28;
                localbn = this.a.b(i1);
                if (i1 == 0) {
                    i4 = localax.i() - 1 + localax.k() + 44 * localbn.o() / localbn.n();
                } else {
                    i4 = localax.i() - 7 - 44 * localbn.o() / localbn.n();
                }
                i = a(i, 6, i4 - i2, i5 - i3, i4, i5, j);
                j += 18;
                this.d.a(i1, j - 4);
                for (i12 = 0; i12 < paramInt3; i12++) {
                    locali = this.b.a(paramArrayOfcc[i12].a - 2, paramArrayOfcc[i12].b - 2);
                    i4 = locali.i() - 10;
                    i5 = locali.j() - 10;
                    i13 = ay.a(15) + 7;
                    i = a(i, 6, i4 - i2, i5 - i3, i4, i5, i13);
                    if (i13 > j) {
                        j = i13;
                    }
                }
                j += 3;
                break;
            case 8:
                j = 0;
                i = 0;
                i2 = -55;
                i3 = 300;
                i4 = 0;
                i5 = 0;
                if (paramInt2 == 0) {
                    i2 = -i2;
                }
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                if (paramArrayOfcc != null) {
                    i = a(i, 6, i4 - i2, i5 - i3, i4, i5, j);
                    b(i1, j + 23, j + 13);
                }
                i = a(i, 8, i4, i5, i4, i5, j + 28);
                localbn = this.a.b(i1);
                if (localbn.k()) {
                    ((bf) this.p.b(i)).c();
                }
                if (paramArrayOfcc != null) {
                    j = ay.a(5) + 7;
                    i5 = localax.j() + 27;
                    if (i1 == 0) {
                        i4 = localax.i() - 3 + localax.k() + 44 * localbn.o() / localbn.n();
                    } else {
                        i4 = localax.i() - 5 - 44 * localbn.o() / localbn.n();
                    }
                    i = a(i, 6, i4 - i2, i5 - i3, i4, i5, j);
                    j += 18;
                    this.d.a(i1, j - 4);
                }
                for (i12 = 0; i12 < paramInt3; i12++) {
                    locali = this.b.a(paramArrayOfcc[i12].a - 2, paramArrayOfcc[i12].b - 2);
                    i4 = locali.i() - 10;
                    i5 = locali.j() - 10;
                    i13 = ay.a(15) + 7;
                    i = a(i, 6, i4 - i2, i5 - i3, i4, i5, i13);
                    if (i13 > j) {
                        j = i13;
                    }
                }
                j = 36;
                break;
            case 7:
                i = 0;
                j = 14;
                this.d.a(j - 2);
                j = 0;
                localax = this.d.a()[paramInt2];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                a(i, paramInt1, i4, i5 - 15, i4, i5, 0);
                break;
            case 4:
                j = 0;
                i = 0;
                localax = this.d.a()[paramInt2];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                a(i, paramInt1, i4, i5, i4, i5, 0);
                break;
            case 3:
                j = 0;
                i = 0;
                int[] arrayOfInt1 = new int[paramInt3 + 1];
                int[] arrayOfInt2 = new int[paramInt3 + 1];
                int i14 = arrayOfInt2.length - 1;
                for (int i15 = 0; i15 < i14; i15++) {
                    locali = this.b.a(paramArrayOfcc[i15].a - 2, paramArrayOfcc[i15].b - 2);
                    arrayOfInt1[i15] = locali.i();
                    arrayOfInt2[i15] = locali.j();
                }
                j = arrayOfInt2.length * 5 + 10;
                b(i1, j, j - 6);
                localax = this.d.a()[i1];
                i4 = localax.i() + localax.k() / 2;
                i5 = localax.j() + localax.l() / 2;
                arrayOfInt1[(arrayOfInt1.length - 1)] = i4;
                arrayOfInt2[(arrayOfInt1.length - 1)] = i5;
                i = a(i, paramInt1, i4, i5, i4, i5, j);
                cu localcu = (cu) this.p.b(i);
                localcu.a(arrayOfInt1, arrayOfInt2);
        }
        return j;
    }

    private int b(int paramInt1, int paramInt2) {
        if (paramInt1 < 0) {
            return -1;
        }
        for (int i = paramInt1; i < this.p.b(); i++) {
            ab localab = (ab) this.p.b(i);
            if ((!localab.m()) && (localab.b() == paramInt2)) {
                return i;
            }
        }
        return -1;
    }

    private int a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        paramInt1 = b(paramInt1, paramInt2);
        Object localObject = null;
        if (paramInt1 > 0) {
            localObject = (ab) this.p.b(paramInt1);
        } else {
            switch (paramInt2) {
                case 0:
                case 1:
                    localObject = new bt(bs.a().r, 9);
                    ((cw) localObject).a(new byte[][]{{0, 1, 2, 3}, {4, 4, 5, 5, 6, 6, 7, 7, 8, 8}});
                    break;
                case 2:
                    localObject = new bt(bs.a().s, 4);
                    ((cw) localObject).a(new byte[][]{new byte[1], {0, 0, 1, 1, 2, 2, 3, 3, 3}});
                    break;
                case 5:
                    localObject = new au(bs.a().t, 5);
                    ((cw) localObject).a(new byte[][]{{0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4}});
                    break;
                case 6:
                    localObject = new ce(bs.a().u, 5);
                    ((cw) localObject).a(new byte[][]{new byte[1], {1, 1, 2, 2, 3, 3, 4, 4}});
                    break;
                case 8:
                    localObject = new bf(bs.a().v, 9);
                    ((cw) localObject).a(new byte[][]{{0, 0, 1, 1, 2, 2, 3, 3, 4, 4}, {5, 5, 6, 6, 7, 7, 8, 8}});
                    break;
                case 7:
                    localObject = new am(bs.a().w, 3);
                    ((cw) localObject).a(new byte[][]{{0, 0, 0, 1, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2}});
                    break;
                case 4:
                    localObject = new do (bs.a().x, 7);
                    ((cw) localObject).a(new byte[][]{{0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4}, {5, 5, 6, 6, 5, 5, 6, 6}});
                    break;
                case 3:
                    localObject = new cu(bs.a().y, 3, bs.a().h);
                    ((cw) localObject).a(new byte[][]{{0, 1, 2, -1}});
            }
            ((ab) localObject).a(paramInt2);
            this.p.a(localObject);
            paramInt1 = this.p.b() - 1;
        }
        ((ab) localObject).a(paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
        return paramInt1;
    }

    public void c(boolean paramBoolean) {
        this.d.d(paramBoolean);
    }

    public void c(int paramInt) {
        this.d.b(paramInt);
    }

    public void a(int paramInt, dy paramdy, bn parambn) {
        this.z.a(paramInt, paramdy, parambn);
    }

    public void m() {
        this.d.c();
    }

    public void n() {
        for (int i = this.p.b() - 1; i > -1; i--) {
            ab localab = (ab) this.p.b(i);
            localab.a(false);
        }
        this.A.b(dd.g);
        this.d.b(false);
        this.d.c(false);
    }

    public void c(int paramInt1, int paramInt2, int paramInt3) {
        int i = (paramInt3 - 2) * 21 + this.b.i();
        int j = (paramInt2 - 2) * 21 + this.b.j();
        this.y.a("Còn " + paramInt1 + " lượt", i, j);
    }

    public void d(boolean paramBoolean) {
        this.d.b(paramBoolean);
    }

    public void e(boolean paramBoolean) {
        this.d.c(paramBoolean);
    }

    public void d(int paramInt) {
        ax localax = this.d.a()[paramInt];
        int i = localax.i() + localax.k() / 2;
        int j = 35;
        if (paramInt == 0) {
            i += j;
        } else {
            i -= j + 20;
        }
        int i1 = localax.j();
        int[] arrayOfInt1 = {-20, -10, 10, 20, -10, 10};
        int[] arrayOfInt2 = {0, -20, -20, 0, 20, 20};
        for (int i2 = 0; i2 < arrayOfInt2.length; i2++) {
            a(i + arrayOfInt1[i2], i1 + arrayOfInt2[i2], i, i1, 2);
        }
    }

    public int o() {
        return this.A.a();
    }

    public void e(int paramInt) {
        this.A.a(paramInt);
    }

    public void d(int paramInt1, int paramInt2, int paramInt3) {
        int i = (paramInt3 - 2) * 21 + this.b.i();
        int j = (paramInt2 - 2) * 21 + this.b.j();
        this.t.a(paramInt1, i, j);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     i

 * JD-Core Version:    0.7.0.1

 */