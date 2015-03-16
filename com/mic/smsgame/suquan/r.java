package com.mic.smsgame.suquan;

import ae;
import an;
import ar;
import com.mg.smsgame.main.MGMIDlet;
import cz;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

class r
        implements CommandListener {
    final ac a;
    private final TextBox b;

    r(ac paramac, TextBox paramTextBox) {
        this.a = paramac;
        this.b = paramTextBox;
    }

    public void commandAction(Command paramCommand, Displayable paramDisplayable) {
        if (paramCommand.getLabel().equals("Gửi")) {
            if ((this.b.getString() == null) || ("".equals(this.b.getString()))) {
                ae localae = cz.a("Chú ý", "Bạn phải nhập vào số điện thoại người nhận!", "Đóng", 5, 1);
                localae.a(this.a);
                cz.d().a(localae, false);
            } else {
                an.a(this.b.getString());
            }
            MGMIDlet.f().a.setCurrent(cz.d());
        } else {
            MGMIDlet.f().a.setCurrent(cz.d());
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.r

 * JD-Core Version:    0.7.0.1

 */