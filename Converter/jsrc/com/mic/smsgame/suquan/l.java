package com.mic.smsgame.suquan;

import game.aj;
import game.ay;
import game.az;
import game.bv;
import game.cx;
import game.cz;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class l
        extends aj {
    private Image c = az.b("/bksplashscreen");
    private int d;

    public l() {
        cz.d().a(30);
    }

    public void a(Graphics paramGraphics) {
        paramGraphics.drawImage(this.c, cx.l, cx.m, 20);
    }

    public void a() {
        this.d += 1;
        if ((this.d == 30) || ((this.d == 2) && game.PortScreens.touch)) { // PORT-UI: no wait
            cz.a().f = cz.a().d(ay.a(cz.a().j()));
            cz.d().a(new ac(), true);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.l

 * JD-Core Version:    0.7.0.1

 */