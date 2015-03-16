package com.mic.smsgame.suquan;

import at;
import ci;
import cx;
import d;

import javax.microedition.media.Controllable;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

public class ad
        implements at, PlayerListener {
    private Player a;
    private VolumeControl b;
    private String c = "";
    private boolean d;
    private long e;
    private int f;
    private int g = 5000;

    public ad() {
    }

    public ad(String paramString) {
        a(paramString);
    }

    public void a(String paramString) {
        this.d = false;
        d.a("Play " + paramString + " - " + this.c + "...");
        if ((this.a != null) && (paramString.equals(this.c))) {
            d();
            return;
        }
        try {
            if (!cx.x) {
                return;
            }
            a();
            this.a = Manager.createPlayer("".getClass().getResourceAsStream("/audio/" + paramString + ".amr"), "audio/amr");
            this.a.setLoopCount(1);
            this.a.realize();
            this.a.prefetch();
            this.a.addPlayerListener(this);
            this.b = ((VolumeControl) this.a.getControl("VolumeControl"));
            if (this.b != null) {
                this.b.setMute(!cx.x);
                this.b.setLevel(cx.y);
            }
            this.c = paramString;
            d.a("Write currFile " + this.c);
        } catch (Throwable localThrowable) {
            d.a("[Create Effect Sound" + localThrowable);
            a();
        }
    }

    public void a() {
        if (this.a != null) {
            try {
                this.a.stop();
                this.a.deallocate();
                this.a.close();
                this.a = null;
                this.b = null;
                d.a("destroy " + this.c);
            } catch (MediaException localMediaException) {
                this.a = null;
                this.b = null;
                d.a("[Destroy Effect Sound" + localMediaException);
            }
        }
    }

    public void b() {
        if (!cx.x) {
            return;
        }
        this.d = true;
        if ((this.a == null) && (!this.c.equals(""))) {
            a(this.c);
        }
        this.e = System.currentTimeMillis();
        ci.a().a(this);
    }

    public void d() {
        if (this.a != null) {
            try {
                this.a.stop();
                this.d = false;
            } catch (Throwable localThrowable) {
                a();
                d.a("[STOP EFFECT Music]: " + localThrowable);
            }
        }
    }

    public void c() {
        if (!this.d) {
            return;
        }
        try {
            if (cx.V) {
                this.a.stop();
            }
            this.a.setMediaTime(0L);
            if (this.b != null) {
                this.b.setMute(!cx.x);
                this.b.setLevel(cx.y);
            }
            if (this.d) {
                this.a.start();
            }
        } catch (Exception localException) {
            a();
            this.d = false;
            d.a("[Play Effect Sound " + localException);
        }
    }

    public boolean e() {
        if (this.d) {
            if (this.f > 3) {
                d();
            } else if (System.currentTimeMillis() - this.e > this.g) {
                this.f += 1;
                d();
            }
        }
        return this.d;
    }

    public void playerUpdate(Player paramPlayer, String paramString, Object paramObject) {
        if (cx.x) {
            if (paramString.equals("endOfMedia")) {
                this.d = false;
            } else if ((paramString.equals("started")) && (!this.d)) {
                d();
            }
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.ad

 * JD-Core Version:    0.7.0.1

 */