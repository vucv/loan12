package com.mic.smsgame.suquan;

import ay;
import cx;
import ds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class p
        extends ds {
    private int a;
    private int b;
    private int c;
    private int d;
    private boolean e = false;
    private Image f;
    private int g;
    private int h;
    private int i;
    private int j;

    public p(Image paramImage) {
        this.f = paramImage;
        this.r = (paramImage.getWidth() / 3);
        this.s = paramImage.getHeight();
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        d(paramInt1, paramInt2);
        this.a = paramInt3;
        this.b = paramInt4;
        this.c = paramInt1;
        this.d = paramInt2;
        int k = 20 + ay.a(40);
        int m = Math.abs(paramInt1 - paramInt3);
        int n = Math.abs(paramInt2 - paramInt4);
        if (m > n) {
            this.d -= m - n;
            this.d -= k;
            if (paramInt3 < cx.f / 2) {
                this.c += k;
            } else {
                this.c -= k;
            }
        } else {
            this.d -= k;
            k += n - m;
            if (paramInt3 < cx.f / 2) {
                this.c += k;
            } else {
                this.c -= k;
            }
        }
        if (paramInt3 < cx.f / 2) {
            this.j = 2;
        } else {
            this.j = 0;
        }
        this.i = 0;
        this.e = false;
        this.u = true;
        this.g = (b(this.p, this.c, 14) + ay.a(4));
        this.h = (b(this.q, this.d, 14) + ay.a(4));
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u) {
            return;
        }
        paramGraphics.drawRegion(this.f, this.i * this.r, 0, this.r, this.s, this.j, this.p, this.q, 3);
    }

    public void a() {
        if (!this.u) {
            return;
        }
        if (this.e) {
            if (this.i < 2) {
                this.i += 1;
            }
            this.g += 1;
            this.h += 1;
            if (c(this.a, this.b, this.g, this.h)) {
                this.u = false;
                this.i = 1;
            }
        } else if (c(this.c, this.d, this.g, this.h)) {
            this.e = true;
            this.g = b(this.p, this.a, 28);
            this.h = b(this.q, this.b, 28);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.p

 * JD-Core Version:    0.7.0.1

 */