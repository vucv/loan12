package com.mic.smsgame.suquan;

import ay;
import cw;
import dq;
import ds;

import javax.microedition.lcdui.Graphics;

public class n
        extends dq {
    private boolean a = false;
    private int v;

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
        d(paramInt1, paramInt2);
        this.i = paramInt3;
        this.j = paramInt4;
        this.u = true;
        this.k = (b(this.p, paramInt3, 12) + ay.a(3));
        this.l = (b(this.q, paramInt4, 12) + ay.a(3));
        this.a = false;
        this.v = paramInt5;
    }

    public void a() {
        if (this.v > 0) {
            this.v -= 1;
            return;
        }
        if (this.a) {
            this.l += 1;
            this.k += 1;
        } else {
            this.k = (b(this.p, this.i, 4) + ay.a(2));
            this.l = (b(this.q, this.j, 8) + ay.a(2));
        }
        if (c(this.i, this.j, this.k, this.l)) {
            if (this.a) {
                this.u = false;
            } else {
                this.i = (this.p + ay.a(20));
                this.j = (this.q + 40 + ay.a(100));
                this.k = (b(this.p, this.i, 14) + ay.a(2));
                this.l = (b(this.q, this.j, 10) + ay.a(2));
                this.a = true;
                c(this.i, this.j, this.k, this.l);
            }
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (this.v > 0) {
            return;
        }
        super.a(paramGraphics, paramInt1, paramInt2);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.n

 * JD-Core Version:    0.7.0.1

 */