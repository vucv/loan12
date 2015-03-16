package com.mg.smsgame.main;

import al;
import an;
import bg;
import ci;
import cn;
import cx;
import cz;
import d;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.wireless.messaging.MessageConnection;

public abstract class MGMIDlet
        extends MIDlet {
    public Display a;
    public static boolean b = false;
    private boolean f = false;
    protected long c = 0L;
    public boolean d;
    protected static MGMIDlet e;

    public MGMIDlet(boolean paramBoolean, String paramString1, String paramString2, String paramString3, String paramString4) {
        cx.F = paramBoolean;
        if (cx.F) {
            an.h = 0;
            an.g = 0;
            an.f = 0;
            an.e = 0;
            an.i = 0;
            an.j = 0;
        }
        if (paramString1 != null) {
            an.a = paramString1;
        }
        if (paramString2 != null) {
            an.b = paramString2;
        }
        if (paramString3 != null) {
            an.d = paramString3;
        }
        if (paramString4 != null) {
            an.c = paramString4;
        }
        an.k = an.a + an.b;
        a();
        this.f = false;
        String str = getAppProperty("Distributor-Name");
        if ((str == null) || (str.length() == 0)) {
            str = "asao";
        }
        an.l = str;
        try {
            g();
            h();
        } catch (Throwable localThrowable) {
        }
        i();
        this.a = Display.getDisplay(this);
        e = this;
        this.a.setCurrent(new cn());
    }

    public void a(String paramString) {
        f().platformRequest(paramString);
        f().notifyDestroyed();
    }

    private void g() {
        String str = System.getProperty("microedition.platform");
        cx.V = false;
        if (str != null) {
            int i = str.toLowerCase().trim().indexOf("nokia");
            if (i >= 0) {
                cx.V = true;
                if ((System.getProperty("com.nokia.mid.imei") != null) || (System.getProperty("com.nokia.mid.imsi") != null) || (System.getProperty("com.nokia.mid.networkid") != null) || (System.getProperty("com.nokia.mid.networksignal") != null) || (System.getProperty("com.nokia.mid.networkavailability") != null) || (System.getProperty("com.nokia.mid.batterylevel") != null) || (System.getProperty("com.nokia.mid.countrycode") != null)) {
                    cx.V = false;
                }
            }
        }
        d.a("NokiaLessThanSerials60=" + cx.V);
    }

    private void h() {
        cx.D = false;
        String str = System.getProperty("com.sonyericsson.java.platform");
        str = str.substring(3);
        int i = str.indexOf(".");
        if (i > 0) {
            str = str.substring(0, i);
        }
        int j = Integer.parseInt(str);
        if (j < 8) {
            cx.D = true;
        }
    }

    protected void destroyApp(boolean paramBoolean) {
        ci.a().b();
    }

    protected void pauseApp() {
        this.c = System.currentTimeMillis();
        b();
    }

    protected void startApp() {
        if (!this.f) {
            this.f = true;
            if (cx.C) {
                j();
            }
            this.d = true;
        } else {
            long l = System.currentTimeMillis() - this.c;
            a(l);
        }
    }

    private void i() {
        String[] arrayOfString = PushRegistry.listConnections(false);
        if ((arrayOfString == null) || (arrayOfString.length == 0)) {
            try {
                String str1 = "sms://:" + an.d;
                String str2 = getClass().getName();
                PushRegistry.registerConnection(str1, str2, "*");
            } catch (Exception localException) {
            }
        }
    }

    private void j() {
        if (b) {
            return;
        }
        String[] arrayOfString = PushRegistry.listConnections(true);
        if ((arrayOfString != null) && (arrayOfString.length > 0)) {
            try {
                MessageConnection localMessageConnection1 = (MessageConnection) Connector.open("sms://:" + an.d);
                an.a(localMessageConnection1);
                b = true;
            } catch (IOException localIOException1) {
            }
        } else {
            try {
                MessageConnection localMessageConnection2 = (MessageConnection) Connector.open("sms://:" + an.d);
                an.a(localMessageConnection2);
                b = false;
            } catch (IOException localIOException2) {
            }
        }
    }

    public final void c() {
        bg.i();
        this.a.setCurrent(cz.d());
        a(b);
        if ((!cx.F) && (al.a())) {
            al.e();
        }
        if (!al.a(true)) {
            al.e();
        }
        cz.d().b();
    }

    public void d() {
        notifyDestroyed();
    }

    public void e() {
        this.a.setCurrent(d.a());
    }

    public static MGMIDlet f() {
        return e;
    }

    protected abstract void a();

    protected abstract void a(boolean paramBoolean);

    protected abstract void b();

    protected abstract void a(long paramLong);
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mg.smsgame.main.MGMIDlet

 * JD-Core Version:    0.7.0.1

 */