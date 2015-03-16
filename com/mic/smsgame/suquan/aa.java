package com.mic.smsgame.suquan;

import ad;
import aj;
import an;
import ao;
import ap;
import ar;
import ay;
import az;
import bg;
import bk;
import bl;
import bo;
import bp;
import bq;
import bv;
import bz;
import c;
import ca;
import cf;
import co;
import com.mg.smsgame.main.b;
import cp;
import cw;
import cx;
import cz;
import d;
import da;
import dd;
import df;
import dg;
import di;
import dj;
import dk;
import dl;
import dq;
import ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import m;
import s;
import u;
import y;

//Map layer
public class aa
        extends aj
        implements b {
    private int f = -1;
    private Image t;
    private Image u;
    private Image v;
    private Image w;
    private int x;
    private int y;
    private bo[] z;
    private bl[] A;
    private int B = 0;
    private int C;
    private int D;
    private int E;
    private int F;
    private int G;
    private int H;
    private int I;
    private int J;
    private int K;
    private int L;
    private int M;
    private bp N;
    private ca O;
    private co P;
    private dq Q;
    private int R;
    private int[] S;
    private int T;
    private dl U;
    private int V;
    private int W;
    private int X;
    private int Y;
    private s Z;
    private boolean aa;
    private int ab;
    private int ac;
    private int ad;
    private int ae;
    private dq af;
    private int ag;
    private boolean ah = true;
    private boolean ai;
    private dk aj;
    private boolean ak;
    private boolean al;
    private boolean am;
    private int an;
    public static boolean c;
    private Image ao;
    private int ap;
    private boolean[] aq;
    private bk ar;
    public static boolean d = false;
    public static boolean e = false;

    public aa() {
        System.gc();
        c = false;
        c(false);
        a(null);
        a(this);
        this.t = az.b("/m/hm");
        this.u = az.b("/m/m");
        this.v = az.b("/m/b");
        this.w = az.b("/m/r");
        this.x = (this.w.getWidth() / 4);
        this.y = this.w.getHeight();
        this.b_ = this.u.getWidth();
        this.c_ = this.u.getHeight();
        this.U = new dl(0, 0, cx.f, cx.g);
        this.Q = new dq(az.b("/m/mt1"), 3);
        this.Q.c(3);
        this.Q.b(true);
        this.Q.a(new byte[][]{{0, 1, 2, 1}});
        this.Q.a_(0);
        this.af = new dq(az.b("/m/ar"), 6);
        this.af.c(1);
        this.af.b(true);
        this.af.a(new byte[][]{{0, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1}});
        this.af.a_(0);
        u.a(null);
        this.ak = false;
        this.al = false;
        this.am = false;
        this.ar = new bk("/m/vk", cx.f - 55, 0);
        this.ap = bg.m();
        this.ao = az.b("/m/l");
        this.ah = false;
        this.ag = 0;
        u();
        v();
        D();
        f(7);
        this.aj = new dk("vs");
        cf.a().a("m", -1);
        cf.a().a(180184);
        cf.a().e();
        this.q = 7;
    }

    private void u() {
        //Main node
        this.z = new bo[bq.a.length];
        for (int i = 0; i < this.z.length; i++) {
            this.z[i] = new bo();
            this.z[i].b = bq.b[i][0];
            this.z[i].c = bq.b[i][1];
            this.z[i].a = bq.b[i][2];
            this.z[i].d = false;
        }
        this.z[0].d = true;
        this.z[0].e = 0;
        i = 0;
        int j = dd.f();//
        while (i < j) {
            this.z[dd.q(i)].f = i;
            i++;
        }
        this.A = new bl[bq.d.length];//
        for (i = 0; i < this.A.length; i++) {
            this.A[i] = new bl();
            this.A[i].a = bq.d[i][0];
            this.A[i].b = bq.d[i][1];
            this.A[i].c = false;
        }
        this.S = new int[bq.a()];//
        for (i = 0; i < this.S.length; i++) {
            this.S[i] = dd.z(i);
        }
        this.B = 0;
        this.D = -1;
        this.C = -1;
        this.R = dd.a();
        this.T = 0;
        this.ae = ay.a(3, 10);
        this.W = dd.b();
        this.X = bg.f(2);
        this.ai = false;
        this.aq = new boolean[dd.k().length];
        for (i = 0; i < this.aq.length; i++) {
            this.aq[i] = false;
        }
        int tmp370_369 = 1;
        this.aq[1] = tmp370_369;
        this.aq[0] = tmp370_369;
        this.ab = -1;
        this.ac = -1;
    }

    private void a(int[] paramArrayOfInt) {
        if (paramArrayOfInt == null) {
            return;
        }
        for (int i = 0; i < paramArrayOfInt.length; i++) {
            this.z[paramArrayOfInt[i]].d = true;
        }
        for (i = 0; i < this.A.length; i++) {
            if ((!this.A[i].c) && (this.z[this.A[i].a].d) && (this.z[this.A[i].b].d)) {
                this.A[i].c = true;
            }
        }
    }

    private void v() {
        if (!f.c()) {
            dd.e = dd.t(dd.d());
            return;
        }
        try {
            byte[] arrayOfByte = f.b();
            ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
            DataInputStream localDataInputStream = new DataInputStream(localByteArrayInputStream);
            this.B = localDataInputStream.readInt();
            this.E = localDataInputStream.readByte();
            this.D = localDataInputStream.readInt();
            this.T = localDataInputStream.readInt();
            for (int i = 0; i < this.z.length; i++) {
                this.z[i].d = localDataInputStream.readBoolean();
                this.z[i].e = localDataInputStream.readByte();
                this.z[i].f = localDataInputStream.readByte();
                this.S[i] = localDataInputStream.readByte();
            }
            for (i = 0; i < this.A.length; i++) {
                this.A[i].c = localDataInputStream.readBoolean();
            }
            this.ae = localDataInputStream.readByte();
            this.L = localDataInputStream.readByte();
            this.M = localDataInputStream.readByte();
            this.J = localDataInputStream.readInt();
            this.K = localDataInputStream.readInt();
            this.F = localDataInputStream.readInt();
            this.G = localDataInputStream.readInt();
            c localc = new c();
            localc.e = localDataInputStream.readByte();
            localc.c = localDataInputStream.readInt();
            localc.g = localDataInputStream.readInt();
            localc.h = localDataInputStream.readInt();
            dd.b = localDataInputStream.readInt();
            this.ai = localDataInputStream.readBoolean();
            for (int j = 0; j < this.aq.length; j++) {
                this.aq[j] = localDataInputStream.readBoolean();
            }
            if (dd.e == null) {
                dd.e = dd.t(dd.d());
                dd.e.e = localc.e;
                dd.e.c = localc.c;
                dd.e.h = localc.h;
                dd.e.g = localc.g;
                dd.e.b();
            }
            localDataInputStream.close();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    private void w() {
        try {
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
            localDataOutputStream.writeInt(this.B);
            localDataOutputStream.writeByte(this.E);
            localDataOutputStream.writeInt(this.D);
            localDataOutputStream.writeInt(this.T);
            for (int i = 0; i < this.z.length; i++) {
                localDataOutputStream.writeBoolean(this.z[i].d);
                localDataOutputStream.writeByte(this.z[i].e);
                localDataOutputStream.writeByte(this.z[i].f);
                localDataOutputStream.writeByte(this.S[i]);
            }
            for (i = 0; i < this.A.length; i++) {
                localDataOutputStream.writeBoolean(this.A[i].c);
            }
            localDataOutputStream.writeByte(this.ae);
            localDataOutputStream.writeByte(this.L);
            localDataOutputStream.writeByte(this.M);
            localDataOutputStream.writeInt(this.J);
            localDataOutputStream.writeInt(this.K);
            localDataOutputStream.writeInt(this.F);
            localDataOutputStream.writeInt(this.G);
            localDataOutputStream.writeByte(dd.e.e);
            localDataOutputStream.writeInt(dd.e.c);
            localDataOutputStream.writeInt(dd.e.g);
            localDataOutputStream.writeInt(dd.e.h);
            localDataOutputStream.writeInt(dd.b);
            localDataOutputStream.writeBoolean(this.ai);
            for (i = 0; i < this.aq.length; i++) {
                localDataOutputStream.writeBoolean(this.aq[i]);
            }
            localDataOutputStream.flush();
            f.a(localByteArrayOutputStream.toByteArray());
            localDataOutputStream.close();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    private void x() {
        if (this.L == 0) {
            if (this.D < 0) {
                return;
            }
            switch (this.D) {
                case 0:
                    this.C = 1;
                    break;
                case 1:
                    this.C = 0;
                    break;
                case 2:
                    this.C = 3;
                    break;
                case 3:
                    this.C = 2;
            }
            if (this.C >= 0) {
                this.Y = bq.c[this.B][this.C];
                if ((this.Y >= 0) && (this.A[this.Y].c)) {
                    this.ae += 1;
                    f(1);
                }
            } else {
                f(0);
            }
        } else {
            this.J = (-this.J);
            this.K = (-this.K);
            this.E = this.B;
            this.ae += 1;
            f(1);
        }
    }

    private void a(Graphics paramGraphics, bo parambo1, bo parambo2) {
        if ((this.U.a(parambo1.b + this.g, parambo1.c + this.a_)) || (this.U.a(parambo2.b + this.g, parambo2.c + this.a_))) {
            int i = dg.a(parambo1.b);
            int j = dg.a(parambo1.c);
            int k = dg.a(parambo2.b) - i;
            int m = dg.a(parambo2.c) - j;
            int n = dg.c(dg.a(k, k) + dg.a(m, m));
            int i1 = dg.b(dg.a(dg.a(10), k), n);
            int i2 = dg.b(dg.a(dg.a(10), m), n);
            int i3 = dg.b(dg.b(n, dg.a(10)));
            for (int i4 = 0; i4 < i3; i4++) {
                i += i1;
                j += i2;
                y.a(paramGraphics, this.w, 0, 0, this.x, this.y, dg.b(i) + this.g, dg.b(j) + this.a_, 3);
            }
        }
    }

    //Paint
    private void b(Graphics paramGraphics) {
        for (int i = 0; i < this.A.length; i++) {
            if (this.A[i].c) {
                a(paramGraphics, this.z[this.A[i].a], this.z[this.A[i].b]);
            }
        }
        for (i = 0; i < this.z.length; i++) {
            if (this.z[i].d) {
                if (this.z[i].a == 1) {
                    if (this.S[i] < 0) {
                        y.a(paramGraphics, this.w, 9, 0, this.x, this.y, this.g + this.z[i].b, this.a_ + this.z[i].c, 3);
                    } else {
                        y.a(paramGraphics, this.w, 18, 0, this.x, this.y, this.g + this.z[i].b, this.a_ + this.z[i].c, 3);
                    }
                } else if (this.z[i].a == 0) {
                    y.a(paramGraphics, this.w, 27, 0, this.x, this.y, this.g + this.z[i].b, this.a_ + this.z[i].c, 3);
                }
            }
        }
    }

    private boolean h(int paramInt1, int paramInt2) {
        int k = (cx.f >> 1) - this.g;
        int m = (cx.g >> 1) - this.a_;
        int n = cx.f >> 1;
        int i1 = cx.g >> 1;
        if (paramInt1 > this.b_ - n) {
            paramInt1 = this.b_ - n;
        }
        if (paramInt1 < n) {
            paramInt1 = n;
        }
        if (paramInt2 > this.c_ - i1) {
            paramInt2 = this.c_ - i1;
        }
        if (paramInt2 < i1) {
            paramInt2 = i1;
        }
        int i;
        if ((paramInt1 - k > 4) || (paramInt1 - k < 4)) {
            i = (paramInt1 - k) / 4;
        } else if (paramInt1 == k) {
            i = 0;
        } else {
            i = paramInt1 - k > 0 ? 1 : -1;
        }
        int j;
        if ((paramInt2 - m > 4) || (paramInt2 - m < 4)) {
            j = (paramInt2 - m) / 4;
        } else if (paramInt2 == m) {
            j = 0;
        } else {
            j = paramInt2 - m > 0 ? 1 : -1;
        }
        if ((i == 0) && (j == 0)) {
            return false;
        }
        this.g -= i;
        this.a_ -= j;
        return true;
    }

    public void a(int paramInt) {
        if (this.f == 1) {
            return;
        }
        switch (this.f) {
            case 0:
                this.C = -1;
                this.Y = -1;
                switch (paramInt) {
                    case 14:
                        this.C = 2;
                        break;
                    case 15:
                        this.C = 3;
                        break;
                    case 12:
                        this.C = 0;
                        break;
                    case 13:
                        this.C = 1;
                }
                if (this.C >= 0) {
                    this.Y = bq.c[this.B][this.C];
                    if ((this.Y >= 0) && (this.A[this.Y].c)) {
                        f(1);
                    }
                }
                break;
            case 9:
                if (this.P != null) {
                    this.P.a(paramInt);
                }
                break;
        }
    }

    private boolean e(int paramInt) {
        bo localbo = this.z[paramInt];
        if (localbo.e >= 0) {
            return true;
        }
        if (localbo.f >= 0) {
            return true;
        }
        return this.S[paramInt] >= 0;
    }

    private void y() {
        this.O = null;
        switch (this.R) {
            case 0:
                int i;
                if (this.z[this.B].e >= 0) {
                    i = dd.d(this.z[this.B].e);
                    switch (i) {
                        case 0:
                            this.R = 2;
                            f(2);
                            dd.a(this.R);
                            break;
                        default:
                            dd.f = dd.h(this.z[this.B].e);
                            f(3);
                            break;
                    }
                } else if (this.z[this.B].f >= 0) {
                    i = dd.p(this.z[this.B].f);
                    switch (i) {
                        case 0:
                            this.R = 2;
                            f(2);
                            dd.a(this.R);
                            break;
                        default:
                            dd.f = dd.s(this.z[this.B].f);
                            f(3);
                            break;
                    }
                } else if (this.S[this.B] >= 0) {
                    dd.f = dd.a(this.B, dd.e.e);
                    f(3);
                }
                break;
            case 2:
                f(5);
                break;
            case 3:
                f(6);
        }
    }

    private void z() {
        ae localae = cz.a("Chúc mừng", "Bạn đã mở thêm được 1 mức luyện tập tại đấu trường. Hãy vào đó thử sức!", "Đồng ý", 5, 1);
        localae.a(this);
        cz.d().a(localae, true);
    }

    private void A() {
        this.R = 0;
        dd.a(this.R);
        this.W = dd.b();
        int i = 0;
        this.ab = -1;
        this.ac = -1;
        if (this.L == 0) {
            if (this.z[this.B].e >= 0) {
                this.W += dd.i(this.z[this.B].e);
                i += dd.j(this.z[this.B].e);
                this.ak = dd.f(this.z[this.B].e);
                this.am = dd.g(this.z[this.B].e);
                if (this.T == 0) {
                    e = true;
                }
                C();
            } else if (this.z[this.B].f >= 0) {
                dd.e.h += dd.n(this.z[this.B].f);
                dd.e.g += dd.o(this.z[this.B].f);
                dd.e.c += dd.o(this.z[this.B].f);
                dd.e.b();
                if (dd.r(this.z[this.B].f) == 9) {
                    if (this.aq[2] == 0) {
                        this.aq[2] = true;
                        this.al = true;
                    }
                } else if ((dd.r(this.z[this.B].f) == 14) && (this.aq[3] == 0)) {
                    this.aq[3] = true;
                    this.al = true;
                }
                this.z[this.B].f = -1;
            }
            this.S[this.B] = -1;
        }
        dd.b(this.W);
        bg.a(2, i, 6);
        this.X += i;
        cz.a().a(true);
        if (dd.c(dd.e.e + 1, this.W)) {
            while (dd.c(dd.e.e + 1, this.W)) {
                dd.e.a();
            }
            f(7);
        } else if (this.L > 0) {
            f(1);
        } else {
            f(7);
        }
        dd.k = false;
        dd.e.b();
        dd.j = 0;
        dd.i = 0;
        if ((dd.e.e >= 30) && (this.aq[4] == 0)) {
            this.aq[4] = true;
            this.al = true;
        }
        if (this.T >= dd.e()) {
            f(10);
        } else if (this.L == 0) {
            w();
        }
    }

    private void B() {
        this.R = 0;
        dd.a(this.R);
        this.W = dd.b();
        x();
        dd.k = false;
        cz.a().a(true);
        if (this.L == 0) {
            w();
        }
    }

    private void C() {
        if (this.T < dd.e()) {
            a(dd.l(this.z[this.B].e));
            this.z[this.B].e = -1;
            this.T += 1;
            this.z[dd.e(this.T)].e = this.T;
            d = true;
            D();
        }
    }

    private void D() {
        if (!d) {
            this.ab = -1;
            this.ac = -1;
        } else if ((this.T >= 0) && (this.T < dd.e())) {
            this.ab = this.z[dd.e(this.T)].b;
            this.ac = this.z[dd.e(this.T)].c;
            if (this.U.b(this.g + this.ab, this.a_ + this.ac)) {
                this.ab = -1;
                this.ac = -1;
            }
        }
    }

    private void f(int paramInt) {
        //Resest
        E();
        this.f = paramInt;
        switch (paramInt) {
            case 7:
                if ((this.ab >= 0) && (this.ac >= 0)) {
                    f(8);
                }
                if (this.L == 0) {
                    this.F = this.z[this.B].b;
                    this.G = this.z[this.B].c;
                }
                this.V = 20;
                break;
            case 0:
                a(this.ar);
                if (this.L > 0) {
                    if (this.R == 1) {
                        this.R = 0;
                        dd.a(this.R);
                        this.L = 0;
                        this.F = this.z[this.B].b;
                        this.G = this.z[this.B].c;
                        f(0);
                    } else if (this.R == 2) {
                        f(5);
                    } else if (this.R == 3) {
                        f(6);
                    } else {
                        f(1);
                    }
                } else if (this.R == 1) {
                    this.R = 0;
                    dd.a(this.R);
                    if (this.D >= 0) {
                        x();
                    } else {
                        f(0);
                    }
                } else {
                    a(new da("C.Hàng", 0));
                    c(new da("T.Tin", 8));
                    b(new da("Thoát", 7));
                    if (e(this.B)) {
                        dd.d = this.z[this.B].f;
                        dd.c = this.z[this.B].e;
                        f(2);
                    } else if (this.B == 24) {
                        this.R = 0;
                        dd.a(this.R);
                        f(9);
                    }
                }
                break;
            case 2:
                this.O = null;
                String[][] arrayOfString = (String[][]) null;
                if (this.z[this.B].e >= 0) {
                    switch (this.R) {
                        case 0:
                            arrayOfString = ea.b[this.z[this.B].e];
                            break;
                        case 2:
                            arrayOfString = ea.c[this.z[this.B].e];
                            break;
                        case 3:
                            arrayOfString = ea.d[this.z[this.B].e];
                    }
                } else if (this.z[this.B].f >= 0) {
                    switch (this.R) {
                        case 0:
                            arrayOfString = ea.e[this.z[this.B].f];
                            break;
                        case 2:
                            arrayOfString = ea.f[this.z[this.B].f];
                            break;
                        case 3:
                            arrayOfString = ea.g[this.z[this.B].f];
                    }
                }
                if (arrayOfString != null) {
                    b(new da("Bỏ qua", 3));
                    c(new da("Tiếp", 13));
                    this.O = new ca(arrayOfString);
                } else {
                    y();
                }
                break;
            case 8:
                this.aa = true;
                this.ad = 15;
                a(null);
                c(null);
                b(new da("Bỏ qua", 6));
                break;
            case 1:
                d = false;
                this.H = (this.F * 10000);
                this.I = (this.G * 10000);
                if (this.ah) {
                    this.ag = 0;
                    this.ah = false;
                } else {
                    this.ag = 75;
                }
                if (this.L <= 0) {
                    this.D = this.C;
                    this.E = this.A[this.Y].a;
                    if (this.E == this.B) {
                        this.E = this.A[this.Y].b;
                    }
                    this.H = (this.F * 10000);
                    this.I = (this.G * 10000);
                    long l1 = (this.z[this.E].b - this.F) * 10000;
                    long l2 = (this.z[this.E].c - this.G) * 10000;
                    long l3 = m.a(l1 * l1 + l2 * l2);
                    this.J = ((int) (2L * l1 * 10000L / l3));
                    this.K = ((int) (2L * l2 * 10000L / l3));
                    this.L = ((int) (l3 / 20000L));
                    this.M = (this.L >> 1);
                    this.ae -= 1;
                }
                break;
            case 3:
                this.Z = new s();
                this.Z.d(this.F, this.G);
                this.Z.a();
                if (bg.f(5) > 0) {
                    dd.h = true;
                    bg.g(5);
                } else if (dd.e.e > dd.f.e) {
                    dd.h = true;
                } else if (dd.e.e < dd.f.e) {
                    dd.h = false;
                } else {
                    dd.h = ay.a(2) == 0;
                }
                this.N = new bp(dd.e, dd.f);
                this.aj.b();
                break;
            case 9:
                this.P = new co(this.aq);
                if (this.P.a()) {
                    a(new da("Đánh", 2));
                    c(new da(" ", 2));
                }
                b(new da("Rút lui", 4));
                break;
            case 6:
                B();
        }
    }

    private void E() {
        a(null);
        c(null);
        b(null);
        dd.a = -1;
        switch (this.f) {
            case 2:
                this.O = null;
                break;
            case 3:
                this.N = null;
                this.Z = null;
                break;
            case 9:
                this.P = null;
        }
    }

    public void b() {
        ad localad = new ad(dd.e, this.W, this.X);
        localad.a(this);
        localad.c(new da("Đóng", 5));
        cz.d().a(localad, true);
    }

    private void F() {
        dd.c = this.T;
        if (this.P != null) {
            dd.b = dd.e.c;
            dd.e.c = dd.e.b;
            this.P.b();
        }
        u.a(null);
        cz.d().e.a(7, 5, true, true);
        cf.a().d();
        this.aj.a();
    }

    public void a() {
        Object localObject;
        if (this.ak) {
            this.ak = false;
            this.an = bg.t();
            if (this.an >= 0) {
                localObject = "Bạn đã nhận được 1 vật phẩm. Bạn có muốn mở vật phẩm ngay không? (Tham khảo giá tại http://free.ola.vn.)";
                if (this.am) {
                    localObject = "Bạn đã nhận được một vật phẩm. Bạn có muốn mở hay không? (*Chú ý* Đây là vật phẩm cuối cùng. Hãy mở hết các vật phẩm còn lại trước khi chọn Chơi mới)";
                }
                ae localae1 = cz.a("Chúc mừng", (String) localObject, "Có", 11, "Không", 5, 1);
                localae1.a(this);
                cz.d().a(localae1, true);
            }
        }
        if (this.al) {
            this.al = false;
            z();
        }
        this.Q.e();
        if (this.ag == 0) {
            this.af.e();
        }
        switch (this.f) {
            case 7:
                this.V -= 1;
                if (this.V <= 0) {
                    f(0);
                }
                break;
            case 0:
                if (e) {
                    e = false;
                    G();
                }
                if (this.ag > 0) {
                    this.ag -= 1;
                }
                break;
            case 1:
                this.H += this.J;
                this.I += this.K;
                this.F = (this.H / 10000);
                this.G = (this.I / 10000);
                this.L -= 1;
                if ((this.ae == 0) && (this.L == this.M)) {
                    dd.d = -1;
                    dd.c = -1;
                    this.ae = ay.a(3, 10);
                    dd.f = dd.x(this.Y);
                    f(3);
                    f(3);
                }
                if (this.L <= 0) {
                    this.M = -1;
                    this.B = this.E;
                    f(0);
                }
                break;
            case 8:
                if (this.aa) {
                    this.aa = h(this.ab, this.ac);
                } else {
                    this.ad -= 1;
                    if (this.ad <= 0) {
                        f(0);
                    }
                }
                break;
            case 3:
                if (this.Z != null) {
                    this.Z.b();
                    //If
                    if (this.Z.c()) {
                        this.Z = null;
                        a(new da("Đánh", 2));
                        c(new da(" ", 2));
                        if (this.D >= 0) {
                            b(new da("Rút lui", 4));
                        }
                    }
                } else if (this.N != null) {
                    this.N.a();
                }
                break;
            case 9:
                if (!this.ai) {
                    this.ai = true;
                    localObject = dj.a("Đấu trường", "Từ bây giờ, bạn có thể vào đấu trường để luyện điểm kinh nghiệm.@Nếu bạn đánh thắng, bạn sẽ được một số điểm kinh nghiệm tùy theo cấp độ hiện tại.@Nếu thua, bạn sẽ bị mất 1 mạng chơi, nhưng không bị trừ điểm kinh nghiệm.@*Lưu ý:@*-Bạn phải trả vàng để có thể tham gia đấu trường.@*-Vàng và điểm kinh nghiệm thu thập được trong quá trình đánh sẽ không có hiệu lực.@*-Máu của bạn sẽ không bị ảnh hưởng khi tham gia đấu trường.@*-Hiện tại chỉ có 2 mức luyện tập, hãy tìm khám phá để unlock thêm 3 mức luyện tập cao hơn.", null, null, null, null);
                    ((ar) localObject).c(new da("Đồng ý", 5));
                    ((ar) localObject).a(this);
                    ((ar) localObject).a(null);
                    cz.d().a((ae) localObject, true);
                }
                if (this.P != null) {
                    this.P.c();
                }
                break;
            case 2:
                if (this.O != null) {
                    this.O.b();
                }
                break;
            case 5:
                localObject = "";
                if (this.L == 0) {
                    int i;
                    int j;
                    if (dd.k(this.z[this.B].e)) {
                        i = dd.i(this.z[this.B].e);
                        j = dd.j(this.z[this.B].e);
                        localObject = localObject + "Bạn đã hoàn thành nhiệm vụ. Bạn được thưởng thêm {e} kinh nghiệm, {g} vàng.";
                        localObject = ao.a((String) localObject, "{e}", i);
                        localObject = ao.a((String) localObject, "{g}", j);
                    } else if (dd.m(this.z[this.B].f)) {
                        i = dd.o(this.z[this.B].f);
                        j = dd.n(this.z[this.B].f);
                        localObject = localObject + "Bạn đã hoàn thành sự kiện. Bạn được thưởng thêm ";
                        int k = 0;
                        if (i > 0) {
                            localObject = localObject + "{h} máu";
                            k = 1;
                        }
                        if (j > 0) {
                            if (k != 0) {
                                localObject = localObject + ", ";
                            }
                            localObject = localObject + "{a} tấn công";
                        }
                        localObject = ao.a((String) localObject, "{h}", i);
                        localObject = ao.a((String) localObject, "{a}", j);
                    }
                }
                if (!((String) localObject).equals("")) {
                    ae localae2 = cz.a("Chúc mừng", (String) localObject, "Đồng ý", 9, 1);
                    localae2.a(this);
                    cz.d().a(localae2, true);
                } else {
                    A();
                }
                break;
            case 10:
                cz.d().a(new df(false), false);
        }
        if (this.f != 8) {
            h(this.F, this.G);
        }
    }

    private void G() {
        di localdi = new di();
        cp localcp = new cp(az.b("/m/mt1"), 3);
        localcp.a(new byte[][]{{0, 1, 2, 1}});
        localcp.a(4);
        localcp.a(true);
        localcp.b(0);
        localdi.a(localcp);
        dj localdj = dj.a("Hướng dẫn", "- Địa điểm có nhiệm vụ sẽ có dấu hiệu nhận biết:@{i}@- Di chuyển nhân vật bằng cách bấm các phím 2, 4, 6, 8 tương ứng với hướng muốn đi trên bản đồ.@*Hãy vào của hàng để mua thêm vật phẩm hỗ trợ, giúp tăng thêm sức tấn công, và máu cho bạn.", null, localdi, null, null);
        localdj.c(new da("Đóng", 5));
        localdj.a(this);
        cz.d().a(localdj, true);
    }

    private void e(Graphics paramGraphics) {
        int i = 0;
        int j = 0;
        int k = this.v.getWidth();
        int m = this.v.getHeight();
        while (j < cx.g) {
            i = 0;
            while (i < cx.f) {
                paramGraphics.drawImage(this.v, i, j, 0);
                i += k;
            }
            j += m;
        }
    }

    public void a(Graphics paramGraphics) {
        paramGraphics.setClip(0, 0, cx.f, cx.g);
        paramGraphics.drawImage(this.u, this.g, this.a_, 0);
        b(paramGraphics);
        for (int i = 0; i < this.z.length; i++) {
            if ((this.z[i].d) && (this.z[i].e != -1)) {
                this.Q.a(paramGraphics, this.g + this.z[i].b, this.a_ + this.z[i].c - 27);
            }
        }
        paramGraphics.drawImage(this.t, this.g + this.F, this.a_ + this.G, 3);
        paramGraphics.drawImage(this.ao, 1, 2, 0);
        bz.d.a(paramGraphics, "x" + this.ap, 13, 1, 0);
        switch (this.f) {
            case 0:
                if (cx.n) {
                    this.ar.a(this.g + this.F - (this.ar.a() >> 1), this.a_ + this.G - (this.ar.b() >> 1));
                    this.ar.a(paramGraphics);
                }
            case 7:
                if (this.ag == 0) {
                    this.af.a(paramGraphics, this.g + this.F - 5, this.a_ + this.G - 20);
                }
                break;
            case 2:
                e(paramGraphics);
                if (this.O != null) {
                    this.O.a(paramGraphics);
                }
                break;
            case 3:
                if (this.Z != null) {
                    this.Z.a(paramGraphics, this.g, this.a_);
                } else {
                    e(paramGraphics);
                    if (this.N != null) {
                        this.N.a(paramGraphics);
                    }
                }
                break;
            case 9:
                e(paramGraphics);
                if (this.P != null) {
                    this.P.a(paramGraphics);
                }
                break;
        }
    }

    public void b(int paramInt1, int paramInt2) {
        ae localae1;
        switch (paramInt2) {
            case 0:
                u.a(null);
                cz.a().a(7, 4, true, true);
                break;
            case 1:
                cz.d().a(false);
                u.a(null);
                cz.a().a(7, 2, true, true);
                break;
            case 3:
                y();
                break;
            case 6:
                this.ab = (this.ac = -1);
                f(0);
                break;
            case 2:
                if (!bg.l()) {
                    localae1 = cz.a("Chú ý", "Bạn đã hết mạng chơi. Bạn có muốn mua thêm mạng chơi không?. Tham khảo giá tại http://free.ola.vn.", "Có", 12, "Không", 14, 1);
                    localae1.a(this);
                    cz.d().a(localae1, true);
                } else {
                    F();
                }
                break;
            case 4:
                if (this.L == 0) {
                    x();
                } else {
                    this.J = (-this.J);
                    this.K = (-this.K);
                    this.E = this.B;
                    f(1);
                }
                break;
            case 5:
                cz.d().a(true);
                break;
            case 7:
                localae1 = cz.a("Chú ý", "Bạn có muốn trở về menu hay không?", "Có", 1, "Không", 5, 1);
                localae1.a(this);
                cz.d().a(localae1, true);
                break;
            case 8:
                b();
                break;
            case 9:
                if (dd.m(dd.d)) {
                    A();
                } else {
                    ae localae2 = dd.a(true);
                    localae2.a(this);
                    localae2.c(new da("Đóng", 10));
                    cz.d().a(true);
                    cz.d().a(localae2, true);
                }
                break;
            case 10:
                cz.d().a(true);
                A();
                break;
            case 11:
                c = true;
                bg.e(this.an);
                break;
            case 12:
                cz.d().a(false);
                an.b("", cx.G, new ae(this));
                break;
            case 13:
                if (!this.O.a()) {
                    y();
                }
                break;
            case 14:
                cz.d().a(false);
        }
    }

    public void e() {
        long l = System.currentTimeMillis();
        if (this.f != 10) {
            w();
        }
        d.a("DONE SAVEMAP." + (System.currentTimeMillis() - l));
    }

    static void a(aa paramaa) {
        paramaa.F();
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.aa

 * JD-Core Version:    0.7.0.1

 */