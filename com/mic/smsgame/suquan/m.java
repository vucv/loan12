package com.mic.smsgame.suquan;

import ar;
import aw;
import az;
import cx;
import dc;
import ds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import y;

public class m
        extends dc {
    private Image E = az.b("/r/br");
    private int F = 240 - this.E.getWidth() >> 1;
    private int G = 320 - this.E.getHeight() >> 1;

    public m(int paramInt) {
        super(paramInt, h.g, 3);
        this.C.a(null);
        this.C.a(null);
        this.q = 3;
    }

    protected void b() {
        this.B = new Image[4];
        try {
            this.z = az.b("/r/q");
            this.A = az.b("/r/g");
            for (int i = 0; i < this.B.length; i++) {
                this.B[i] = az.b("/r/" + (i + 1));
            }
            Image localImage1 = Image.createImage(this.b_, this.c_);
            Image localImage2 = az.b("/bkmenu");
            Graphics localGraphics = localImage1.getGraphics();
            localGraphics.drawImage(localImage2, cx.l, cx.m, 0);
            localImage2 = null;
            y.a(localGraphics, 8553090, 0, 0, this.b_, this.c_);
            a(new aw(localImage1, this.b_ >> 1, this.c_ >> 1, 3));
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    protected void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        paramInt1 = this.w[this.e].i() + this.w[this.e].k() / 2;
        paramInt2 = this.w[this.e].j() + this.w[this.e].l() / 2;
        paramGraphics.setColor(16776960);
        paramGraphics.fillTriangle(paramInt1, paramInt2 - 10 - this.f, paramInt1 - 4, paramInt2 - 14 - this.f, paramInt1 + 4, paramInt2 - 14 - this.f);
        paramGraphics.fillTriangle(paramInt1, paramInt2 + 10 + this.f, paramInt1 - 5, paramInt2 + 15 + this.f, paramInt1 + 4, paramInt2 + 15 + this.f);
        paramGraphics.fillTriangle(paramInt1 - 10 - this.f, paramInt2, paramInt1 - 14 - this.f, paramInt2 - 4, paramInt1 - 14 - this.f, paramInt2 + 4);
        paramGraphics.fillTriangle(paramInt1 + 10 + this.f, paramInt2, paramInt1 + 15 + this.f, paramInt2 - 5, paramInt1 + 15 + this.f, paramInt2 + 4);
    }

    protected void b(Graphics paramGraphics) {
        paramGraphics.drawImage(this.E, this.g + this.F, this.a_ + this.G, 0);
        super.b(paramGraphics);
    }

    protected void u() {
        this.t = new int[10][4];
        this.t[0] = {-1, 1, 4, 9};
        this.t[1] = {-1, 2, 5};
        this.t[2] = {-1, 3, 6, 1};
        this.t[3] = {-1, 4, 7, 2};
        this.t[4] = {0, 5, -1, 3};
        this.t[5] = {1, 6, 8, 4};
        this.t[6] = {2, 7, 9, 5};
        this.t[7] = {3, 8, -1, 6};
        this.t[8] = {5, 9, -1, 7};
        this.t[9] = {6, 0, -1, 8};
        int i = 34;
        int j = 34;
        this.g = cx.l;
        this.a_ = cx.m;
        int k = this.g + 68;
        int m = this.a_ + 124;
        this.x = new int[this.t.length];
        this.y = new int[this.t.length];
        for (int n = 0; n < this.x.length; n++) {
            this.x[n] = (k + n % 4 * i);
            this.y[n] = (m + n / 4 * j);
        }
        for (n = this.x.length - 2; n < this.x.length; n++) {
            this.x[n] += i;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.m

 * JD-Core Version:    0.7.0.1

 */