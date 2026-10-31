package com.mic.smsgame.suquan;

import game.ai;
import game.aj;
import game.ar;
import game.bs;
import game.bu;
import game.bv;
import game.c;
import game.cq;
import game.cz;
import game.d;
import game.dd;
import game.i;

import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;

public class ab
        extends aj
        implements e {
    cq c;
    i d;
    bu e;

    public ab() {
        long l = j2me.Host.now() /* PORT */;
        try {
            bs.b();
            try {
                byte[] arrayOfByte = (byte[]) null;
                int i = 0;
                if (f.e()) {
                    System.out.println("khasc nullllllllllllll");
                    arrayOfByte = f.d();
                    f.f();
                    dd.c = ai.a(arrayOfByte, i);
                    i += 4;
                    dd.e = new c();
                    i += dd.e.b(arrayOfByte, i);
                    dd.f = new c();
                    i += dd.f.b(arrayOfByte, i);
                }
                this.c = new cq(dd.d(dd.c), dd.e, dd.f);
                this.d = new i(this.c);
                this.e = new bu(this.c, this.d);
                this.e.a(arrayOfByte, i);
            } catch (Exception localException1) {
                localException1.printStackTrace();
                game.d.a(localException1);
                f.f();
                cz.a().a(5, 7, false);
                return;
            }
            a(cz.a());
            a((game.h) null);
            dd.h();
            try {
                bs.a().c();
                bs.a().b(dd.e.f);
                bs.a().b(dd.f.f);
            } catch (Exception localException2) {
                bs.a().j();
                localException2.printStackTrace();
                game.d.a(localException2);
            }
            game.d.a("DONE LOADPLAY" + (j2me.Host.now() /* PORT */ - l));
        } catch (Exception localException3) {
            localException3.printStackTrace();
            game.d.a(localException3);
            cz.a().a(5, 7, false);
        }
        this.q = 5;
    }

    public void r() {
    }

    public void a(int paramInt) {
        this.e.b(paramInt);
    }

    public void a(int paramInt1, int paramInt2) {
        this.e.a(paramInt1, paramInt2);
    }

    public void portDragTo(int paramInt1, int paramInt2) { // PORT-TOUCH
        if (this.e != null) {
            this.e.portDrag(paramInt1, paramInt2);
        }
    }

    public void g(int paramInt1, int paramInt2) { // PORT-TOUCH: pointer released
        if (this.e != null) {
            this.e.portRelease(paramInt1, paramInt2);
        }
    }

    public String portSoftLabel(int paramInt) { // PORT-TOUCH: the battle draws its own soft-key bar
        if (this.e != null) {
            return this.e.portBarLabel(paramInt);
        }
        return null;
    }

    /** PORT-TOUCH: the battle zooms on the board and the two portraits (the frame and the soft-key row are cut;
     *  the host draws the soft keys as big buttons below instead). */
    private static final int[] PORT_VIEWPORT = {28, 0, 184, 286};

    public int[] portViewport() {
        return this.e != null ? PORT_VIEWPORT : null;
    }

    public void a() {
        this.e.c();
        this.d.d();
    }

    public void a(Graphics paramGraphics) {
        this.d.a(paramGraphics);
    }

    public void e() {
        this.e.t();
    }

    public void t() {
        this.d.a(false);
    }

    public void s() {
        this.d.a(true);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.ab

 * JD-Core Version:    0.7.0.1

 */