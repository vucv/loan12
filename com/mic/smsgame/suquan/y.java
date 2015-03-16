package com.mic.smsgame.suquan;

import af;
import ag;
import aj;
import ar;
import bb;
import bi;
import bv;
import cf;
import cx;
import cy;
import dc;
import j;
import v;

public class y
        extends bv {
    public y() {
        super(true);
    }

    public dc k() {
        if (cx.T) {
            return null;
        }
        int i = 2;
        if (this.b != null) {
            i = this.b.k();
        }
        if (aa.c) {
            i = 7;
        }
        if (i < 0) {
            return null;
        }
        if (i == 0) {
            i = 2;
        }
        return new m(i);
    }

    public void l() {
        this.g = new bb[4];
        this.g[0] = new j();
        this.g[1] = new af();
        this.g[2] = new bi();
        this.g[3] = new cy();
    }

    public void m() {
        this.k = new z();
        this.j = new ag();
    }

    protected aj d(int paramInt1, int paramInt2) {
        cx.T = false;
        switch (paramInt2) {
            case 1:
                return new v(6);
            case 6:
                return new l();
            case 2:
                return new ac();
            case 3:
                return new m(paramInt1);
            case 5:
                ab localab = new ab();
                cx.T = true;
                return localab;
            case 7:
                if (paramInt1 == 2) {
                    aa.d = true;
                }
                return new aa();
            case 4:
                return new q(paramInt1);
            case 8:
                return new z();
        }
        return null;
    }

    public void b(int paramInt1, int paramInt2) {
    }

    public void h() {
        super.h();
        if ((!cx.T) && (!cx.H)) {
            cf.a().e();
        }
    }

    public void g() {
        super.g();
        if ((!cx.T) && (!cx.H)) {
            cf.a().b();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.y

 * JD-Core Version:    0.7.0.1

 */