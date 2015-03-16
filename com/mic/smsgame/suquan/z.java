package com.mic.smsgame.suquan;

import ae;
import aj;
import ao;
import ar;
import az;
import bv;
import bz;
import com.mg.smsgame.main.MGMIDlet;
import com.mg.smsgame.main.b;
import cx;
import cz;
import da;
import dd;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;

import y;

public class z
        extends aj
        implements b, CommandListener {
    private Image[] c;
    private Image d;
    private Image e;
    private int f;
    private Image[] t;
    private Image u;
    private String[] v;
    private String[] w;
    private TextBox x;
    private int y;

    public z() {
        b();
        this.g = (this.b_ - this.t[0].getWidth() >> 1);
        this.a_ = ((this.c_ - 180 >> 1) - 18);
        this.w = h.e;
        this.v = bz.a(this.w[this.f], this.t[0].getWidth() - 15);
        a(new da("Chọn", -1));
        c(new da(" ", -1));
        b(new da("Trở về", -2));
        a(null);
        a(this);
        this.x = new TextBox("Nhap ten nhan vat", "", 10, 1048576);
        this.x.addCommand(new Command("Xong", 4, 1));
        this.x.addCommand(new Command("Trở về", 2, 1));
        this.x.setCommandListener(this);
    }

    private void b() {
        Image localImage1 = az.b("/avatar");
        this.c = new Image[6];
        for (int i = 0; i < 6; i++) {
            this.c[i] = Image.createImage(localImage1, i * 30, 0, 30, 30, 0);
        }
        localImage1 = null;
        this.d = az.b("/focusitem");
        this.t = new Image[2];
        this.t[0] = az.b("/frame1");
        this.t[1] = az.b("/frame2");
        this.u = az.b("/frame3");
        this.e = Image.createImage(this.b_, this.c_);
        Image localImage2 = az.b("/bkmenu");
        Graphics localGraphics = this.e.getGraphics();
        localGraphics.drawImage(localImage2, cx.l, cx.m, 0);
        localImage2 = null;
        y.a(localGraphics, 8553090, 0, 0, this.b_, this.c_);
    }

    public void a(int paramInt) {
        int i = this.f;
        switch (paramInt) {
            case 14:
                this.f = ((this.f - 1 + this.w.length) % this.w.length);
                break;
            case 15:
                this.f = ((this.f + 1) % this.w.length);
                break;
            case 12:
                this.f = ((this.f - 3 + this.w.length) % this.w.length);
                break;
            case 13:
                this.f = ((this.f + 3) % this.w.length);
                break;
        }
        if (this.f != i) {
            this.v = bz.a(this.w[this.f], this.t[0].getWidth() - 15);
        }
    }

    public void a(int paramInt1, int paramInt2) {
        int i = this.g;
        int j = this.a_ + 12;
        int k = 0;
        for (int m = 0; m < 2; m++) {
            i = this.g + 25;
            for (int n = 0; n < 3; n++) {
                if ((paramInt2 >= j) && (paramInt2 <= j + 36) && (paramInt1 >= i) && (paramInt1 <= i + 38) && (k != this.f)) {
                    this.f = k;
                    this.v = bz.a(this.w[this.f], this.t[0].getWidth() - 15);
                    return;
                }
                i += 45;
                k++;
            }
            j += 38;
        }
    }

    public void a(Graphics paramGraphics) {
        paramGraphics.drawImage(this.e, 0, 0, 0);
        u.a(paramGraphics, this.g, this.a_, 200, 7814005, this.t);
        int i = this.g;
        int j = this.a_ + 12;
        int k = 0;
        for (int m = 0; m < 2; m++) {
            i = this.g + 25;
            for (int n = 0; n < 3; n++) {
                if ((this.f == k) && (this.y % 2 == 0)) {
                    y.a(paramGraphics, this.d, 0, 0, 36, 36, i, j, 0);
                } else {
                    y.a(paramGraphics, this.d, 36, 0, 36, 36, i, j, 0);
                }
                paramGraphics.drawImage(this.c[k], i + 3, j + 3, 0);
                i += 45;
                k++;
            }
            j += 38;
        }
        paramGraphics.drawImage(this.u, this.g + this.t[0].getWidth() / 2, j, 17);
        bz.a(paramGraphics, bz.d, this.v, this.g + 11, j + 15, this.b_, 120, 0);
    }

    public void a() {
        if (this.y < 1) {
            this.y = 8;
        } else {
            this.y -= 1;
        }
    }

    private void u() {
        this.x.setString("");
        MGMIDlet.f().a.setCurrent(this.x);
    }

    public void b(int paramInt1, int paramInt2) {
        switch (paramInt2) {
            case -1:
                u();
                break;
            case -2:
                cz.d().e.a(8, 2, true);
                break;
            case -3:
                cz.d().a(false);
                u();
                break;
            case -4:
                cz.d().a(false);
                MGMIDlet.f().a.setCurrent(cz.d());
        }
    }

    public void commandAction(Command paramCommand, Displayable paramDisplayable) {
        if (ao.a(paramCommand.getLabel(), "Xong")) {
            MGMIDlet.f().a.setCurrent(cz.d());
            String str = this.x.getString().trim();
            if ((str == null) || (str.length() < 2)) {
                ae localae = cz.a("Thông tin", "Nhập tên nhân vật có ít nhất 2 kí tự và nhiều nhất 10 kí tự. Bạn có muốn tiếp tục không?", "Có", -3, "Không", -4, 1);
                localae.a(this);
                cz.d().a(localae, false);
                return;
            }
            dd.a(str.toUpperCase());
            dd.c(this.f);
            cz.d().e.a(8, 7, true);
        } else if (ao.a(paramCommand.getLabel(), "Trở về")) {
            MGMIDlet.f().a.setCurrent(cz.d());
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.z

 * JD-Core Version:    0.7.0.1

 */