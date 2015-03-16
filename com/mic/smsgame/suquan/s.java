package com.mic.smsgame.suquan;

import ay;
import cw;
import dq;
import ds;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class s
        extends ds {
    private x[] a = new x[5];
    private byte[][] b = {{0, 0, 0, 0, 1, 1, 1, 2, 2, 2}};

    public s() {
        for (int i = 0; i < this.a.length; i++) {
            this.a[i] = new x();
            this.a[i].a(this.b);
        }
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage) {
        for (int i = 0; i < this.a.length; i++) {
            this.a[i].a(paramImage, 3);
            this.a[i].b(ay.a(this.b[0].length));
            this.a[i].b(paramInt1 + ay.a() % 13, paramInt2 + ay.a() % 13, paramInt3 + ay.a() % 20, paramInt4 + ay.a() % 3);
        }
        this.u = true;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u) {
            return;
        }
        for (int i = this.a.length - 1; i >= 0; i--) {
            this.a[i].a(paramGraphics);
        }
    }

    public void a() {
        if (!this.u) {
            return;
        }
        int i = 0;
        for (int j = this.a.length - 1; j >= 0; j--) {
            if (this.a[j].m()) {
                this.a[j].e();
            } else {
                i++;
            }
        }
        if (i >= this.a.length) {
            this.u = false;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.s

 * JD-Core Version:    0.7.0.1

 */