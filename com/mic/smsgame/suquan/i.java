package com.mic.smsgame.suquan;

import ay;
import cw;
import dq;
import ds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import y;

public class i
        extends dq {
    private int a = 0;
    private int v;
    private int w;
    private int x;
    private int y;
    private int z;
    private int A;
    private boolean B;

    public void a(int paramInt1, int paramInt2) {
        this.w = paramInt1;
        this.y = paramInt2;
    }

    public i(int paramInt1, int paramInt2) {
        super(paramInt1, paramInt2);
        b(false);
    }

    public void a(int paramInt1, int paramInt2, Image paramImage) {
        d(paramInt1, paramInt2);
        a(paramImage, 1);
        a_(1);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage) {
        b(paramInt1, paramInt2, paramInt3, paramInt4);
        a(paramImage, 1);
        a_(4);
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, int paramInt5) {
        b(paramInt1, paramInt2, paramInt3, paramInt4);
        a(paramImage, 1);
        a_(3);
        this.a = paramInt5;
    }

    public void a(int paramInt1, int paramInt2, Image paramImage, int paramInt3) {
        d(paramInt1, paramInt2);
        a(paramImage, paramInt3);
        a_(2);
    }

    public void b(int paramInt1, int paramInt2) {
        this.v = paramInt1;
        a_(5);
        this.a = paramInt2;
    }

    private void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        d(paramInt1, paramInt2);
        c(paramInt3, paramInt4);
    }

    public void a_(int paramInt) {
        super.a_(paramInt);
        this.g = 0;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (this.c == 5) {
            int i = this.p - this.v / 2;
            int j = this.q - this.v / 2;
            y.a(paramGraphics, y.d);
            y.a(paramGraphics, y.d, i, j, this.v, this.v);
            super.a(paramGraphics, paramInt1, paramInt2);
            y.b(paramGraphics, y.d);
        } else {
            if ((this.B) && (this.c == 1)) {
                paramInt1 += this.z;
                paramInt2 += this.A;
            }
            super.a(paramGraphics, paramInt1, paramInt2);
        }
    }

    public void a() {
        if (this.x < this.y) {
            this.x += 1;
            if (this.x == this.w) {
                this.B = true;
            }
            if (this.B) {
                this.z = (ay.a() % 2);
                this.A = (ay.a() % 2);
                if (this.x == this.w + 3) {
                    this.B = false;
                }
            }
        } else {
            this.x = 0;
        }
        if (this.a > 0) {
            this.a -= 1;
            return;
        }
        switch (this.c) {
            case 1:
                break;
            case 6:
                a_(0);
                break;
            case 2:
                if (this.g >= this.d[2].length - 1) {
                    a_(0);
                }
                break;
            case 3:
                if (f(this.j, b(this.q, this.j, 2))) {
                    a_(1);
                }
                break;
            case 4:
                if (c(this.i, this.j, b(this.p, this.i, 2), b(this.q, this.j, 2))) {
                    a_(1);
                }
                break;
            case 0:
                break;
            case 5:
                if (this.v <= 0) {
                    a_(0);
                    return;
                }
                this.v -= 3;
                if (this.v < 0) {
                    this.v = 0;
                }
                break;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.i

 * JD-Core Version:    0.7.0.1

 */