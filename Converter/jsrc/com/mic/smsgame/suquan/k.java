package com.mic.smsgame.suquan;

import game.at;
import game.ci;
import game.cx;
import game.d;

import javax.microedition.media.Controllable;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.TempoControl;
import javax.microedition.media.control.VolumeControl;

public class k
        implements at, PlayerListener {
    private Player a;
    private String b = "";
    private VolumeControl c;
    private TempoControl d;
    private int e = 100;

    public void a(String paramString, int paramInt) {
        try {
            b(paramString, paramInt);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            game.d.a("Create Back ground 1: " + localThrowable);
            b();
            try {
                b(paramString, paramInt);
            } catch (Exception localException) {
                game.d.a("Back ground 2: " + localException);
            }
        }
    }

    private void b(String paramString, int paramInt) throws Exception {
        if (!cx.x) {
            return;
        }
        if ((this.a != null) && (this.b.equals(paramString))) {
            this.e = cx.z;
            return;
        }
        b();
        this.a = Manager.createPlayer(j2me.Res.open("/audio/" + paramString + ".mid"), "audio/midi");
        this.a.setLoopCount(paramInt);
        this.a.realize();
        this.a.addPlayerListener(this);
        this.c = ((VolumeControl) this.a.getControl("VolumeControl"));
        if (this.c != null) {
            this.c.setLevel(cx.y);
            this.c.setMute(!cx.x);
        }
        this.d = ((TempoControl) this.a.getControl("TempoControl"));
        if (this.d != null) {
            cx.z = this.d.getTempo();
            this.e = cx.z;
            this.d.setTempo(this.e);
            game.d.a("Temp when create=" + this.e);
        }
        this.b = paramString;
    }

    public void a() {
        if (this.a != null) {
            try {
                this.a.stop();
                this.a.deallocate();
            } catch (MediaException localMediaException) {
                b();
                game.d.a("[Destroy BK Music]: " + localMediaException);
            }
        }
    }

    public void b() {
        if (this.a != null) {
            try {
                this.a.stop();
                this.a.deallocate();
                this.a.close();
                this.a = null;
                this.c = null;
            } catch (MediaException localMediaException) {
                this.a = null;
                this.c = null;
                game.d.a("[Destroy BK Music]: " + localMediaException);
            }
        }
        this.b = "";
    }

    public void d() {
        if (!cx.x) {
            return;
        }
        ci.a().a(this);
    }

    public void c() {
        try {
            if (this.a != null) {
                this.a.prefetch();
                this.a.setMediaTime(0L);
                if (this.c != null) {
                    this.c.setMute(!cx.x);
                    this.c.setLevel(cx.y);
                }
                this.a.start();
            }
        } catch (Exception localException) {
            game.d.a("[Player BK Music]: " + localException);
            b();
        }
    }

    public void a(int paramInt) {
        this.e = paramInt;
        if (this.d != null) {
            try {
                this.d.setTempo(paramInt);
            } catch (Exception localException) {
                this.e = cx.z;
                this.d.setTempo(this.e);
            }
        }
    }

    public void playerUpdate(Player paramPlayer, String paramString, Object paramObject) {
        if ((cx.x) && (paramString.equals("endOfMedia"))) {
            a();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.k

 * JD-Core Version:    0.7.0.1

 */