package com.mic.smsgame.suquan;

import ae;
import aj;
import ar;
import aw;
import az;
import bg;
import bv;
import bz;
import cd;
import cf;
import com.mg.smsgame.main.MGMIDlet;
import com.mg.smsgame.main.b;
import cx;
import cz;
import d;
import da;
import df;
import di;
import dj;
import ds;
import dt;
import dv;
import dy;
import dz;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;

import x;
import y;

public class ac
        extends aj
        implements b, dy {
    protected da c;
    protected da d;
    final int e = 0;
    final int f = 1;
    final int t = 2;
    dv u;
    int[] v;
    private boolean w;
    private ds[] x;
    private c y;
    private Image z;
    private int A;
    private boolean B;
    private dz C;

    public ac() {
        cx.B = false;
        cx.N = true;
        b();
        this.u = new dv(true);
        this.u.a(this);
        E();
        this.C = new aw(az.b("/bkmenu"), cx.l, cx.m, 20);
        this.y = new c(this.b_ / 2, cx.m + 110);
        this.z = az.b("/swordfocus");
        this.w = (!f.c());
        if (this.w) {
            e(0);
        } else {
            e(1);
        }
        a(this.C);
        a(this);
        a(null);
        cf.a().a("menu", -1);
        d.a("+++++++++++Tempomenu=" + cf.a().f());
        cf.a().a(120000);
        cf.a().e();
        this.q = 7;
    }

    private void E() {
        String[] arrayOfString = {"strcontinuegame", "strnewgame", "strrecord", "strshop", "strinformation", "strgivegame", "strchatola", "strothergame", "strguide", "strauthor"};
        this.x = new v[arrayOfString.length];
        Image[] arrayOfImage = new Image[2];
        for (int i = 0; i < this.x.length; i++) {
            arrayOfImage = new Image[2];
            arrayOfImage[0] = az.b("/" + arrayOfString[i]);
            arrayOfImage[1] = az.b("/" + arrayOfString[i] + "focus");
            this.x[i] = new v(arrayOfImage);
        }
    }

    protected void e(int paramInt) {
        switch (paramInt) {
            case 0:
                this.v = new int[]{1, 2, 3, 4};
                break;
            case 1:
                this.v = new int[]{0, 1, 2, 3, 4};
                break;
            case 2:
                this.v = new int[]{5, 6, 7, 8, 9};
        }
        if (paramInt == 2) {
            b(this.d);
        } else {
            b(this.c);
        }
        u();
    }

    protected void b() {
        this.c = new da("Thoát", 1);
        this.d = new da("Trở về", 2);
    }

    protected void u() {
        ds[] arrayOfds = new ds[this.v.length];
        int i = 130 + cx.m;
        int j = this.b_ / 2;
        for (int k = 0; k < arrayOfds.length; k++) {
            arrayOfds[k] = this.x[this.v[k]];
            arrayOfds[k].d(j, i);
            arrayOfds[k].k(17);
            arrayOfds[k].a(false);
            i += 24;
        }
        this.u.a(arrayOfds, 0);
    }

    public final void a(Graphics paramGraphics) {
        b(paramGraphics);
        e(paramGraphics);
    }

    protected void b(Graphics paramGraphics) {
        this.y.a(paramGraphics);
    }

    protected void e(Graphics paramGraphics) {
        this.u.a(paramGraphics);
        if (!this.u.c()) {
            ds localds = this.u.a();
            int i = localds.i();
            int j = localds.j() + 3;
            y.a(paramGraphics, this.z, 0, 0, this.z.getWidth(), this.z.getHeight(), i - 2 - this.A, j, 24);
            paramGraphics.drawRegion(this.z, 0, 0, this.z.getWidth(), this.z.getHeight(), 2, i + localds.k() + 2 + this.A, j, 20);
        }
    }

    public void a() {
        if (this.y.m()) {
            this.y.a();
        }
        this.u.b();
        if (this.B) {
            this.A -= 1;
            if (this.A == 0) {
                this.B = false;
            }
        } else {
            this.A += 1;
            if (this.A == 3) {
                this.B = true;
            }
        }
    }

    protected void v() {
        ae localae = cz.a("Chú ý", "Bạn có muốn thoát khỏi trò chơi?", "Có", 3, "Không", 4, 1);
        localae.a(this);
        cz.d().a(localae, true);
    }

    protected void w() {
        MGMIDlet.f().d();
    }

    protected void x() {
        cz.d().e.a(2, 3, true);
    }

    protected void y() {
        cz.d().e.a(2, 4, true);
    }

    protected void z() {
        TextBox localTextBox = new TextBox("Nhap so dien thoai nguoi nhan", "", 20, 3);
        localTextBox.addCommand(new Command("Gửi", 4, 1));
        localTextBox.addCommand(new Command("Đóng", 7, 1));
        localTextBox.setCommandListener(new r(this, localTextBox));
        MGMIDlet.f().a.setCurrent(localTextBox);
    }

    protected void A() {
        cz.d().a(false);
        z();
    }

    protected void a(Image paramImage) {
        dj localdj = new dj("Chat Ola", dj.a("007#sfl- Phần mềm chat trên điện thoại đi động với chi phí cực thấp.#sfl- Kết nối Yahoo.#sfl- Chia sẻ hình ảnh, video clip trực tiếp.#sfl- Đọc báo Online.#sfl- Tra từ điển tiếng anh trực tuyến.#sfl- Tra bài hát Karaoke.#sfl- Chơi game trực tuyến...", null, null, bz.d, bz.c));
        localdj.b(new da("Đóng", 6));
        localdj.a(new da("Tải về", 8));
        localdj.a(null);
        localdj.a(this.C);
        localdj.a(this);
        cz.d().a(localdj);
    }

    protected void B() {
        try {
            try {
                if (Class.forName("javax.microedition.io.file.FileConnection") != null) {
                    MGMIDlet.f().a("Http://PhuThoBay.Pro");
                }
            } catch (Throwable localThrowable) {
                MGMIDlet.f().a("Http://PhuThoBay.Pro");
            }
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    protected void a(String[][] paramArrayOfString, Image paramImage) {
        if (paramArrayOfString == null) {
            return;
        }
        dt[] arrayOfdt = new dt[paramArrayOfString.length];
        for (int i = 0; i < arrayOfdt.length; i++) {
            arrayOfdt[i] = new dt(paramArrayOfString[i], true, 1);
            arrayOfdt[i].d = -1;
        }
        dj localdj = new dj("Thông tin", arrayOfdt);
        localdj.c(new da("Đóng", 6));
        localdj.a(null);
        localdj.a(this.C);
        localdj.a(this);
        cz.d().a(localdj);
    }

    protected void h(int paramInt1, int paramInt2) {
        ae localae = cz.a("Chú ý", "bạn có muốn chơi lại từ đầu không?", "Có", paramInt1, "Không", paramInt2, 1);
        localae.a(this);
        cz.d().a(localae, true);
    }

    protected void C() {
        ae localae = cz.a("Chú ý", "Game có khả năng bị đóng lại. Bạn có tiếp tục không?", "Có", 7, "Không", 4, 1);
        localae.a(this);
        cz.d().a(localae, true);
    }

    protected void D() {
        cz.d().a(true);
        try {
            MGMIDlet.f().a("http://game.ola.vn");
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void a(int paramInt) {
        this.u.a(paramInt);
    }

    public void a(int paramInt1, int paramInt2) {
        this.u.a(paramInt1, paramInt2);
    }

    public void b(int paramInt1, int paramInt2) {
        switch (paramInt2) {
            case 1:
                v();
                break;
            case 4:
                cz.d().a(true);
                break;
            case 6:
                cz.d().a(false);
                break;
            case 3:
                w();
                break;
            case 2:
                if (!this.w) {
                    e(1);
                } else {
                    e(0);
                }
                break;
            case 7:
                D();
                break;
            case 8:
                B();
                break;
            case 5:
                A();
                break;
            case 9:
                f.a();
                bg.k();
                bg.x();
                cz.d().a(false);
                cf.a().d();
                cz.d().a(new df(true), false);
        }
    }

    private void F() {
        di localdi = new di();
        localdi.a(new x(az.b("/sword")));
        localdi.a(new x(az.b("/yinyang")));
        localdi.a(new x(az.b("/rice")));
        localdi.a(new x(az.b("/gold")));
        localdi.a(new x(az.b("/book")));
        localdi.a(new x(az.b("/heart")));
        localdi.a(new x(az.b("/swordred")));
        dj localdj = dj.a("Hướng dẫn", "**Tình tiết, cốt truyện và tên nhân vật phụ của trò chơi đều dựa vào sự kiện Loạn 12 Sứ Quân trong lịch sử Việt Nam ở thế kỷ X.@*Luật chơi:@- Một lượt đi phải xếp ít nhất 3 biểu tượng cùng loại nằm sát nhau theo hàng ngang hoặc dọc.@- Xếp 4 biểu tượng trở lên được thưởng thêm 1 lượt đi.@- Xếp 5 biểu tượng trở lên được thưởng 1 vật phẩm hỗ trợ.@{i}@- Xếp biểu tượng thanh kiếm để tấn công đối phương.@{i}@- Xếp biểu tượng âm dương để lấy năng lượng sử dụng tuyệt chiêu.@{i}@- Xếp biểu tượng bánh chưng để lấy lương thực.@{i}@- Xếp biểu tượng thỏi vàng để lấy tiền vàng.@{i}@- Xếp biểu tượng cuốn sách để lấy kinh nghiệm.@{i}@- Xếp biểu tượng trái tim để phục hồi sinh lực (máu).@{i}@- Kiếm lửa có thể xếp cùng với kiếm thường.@- Kiếm lửa có lực tấn công mạnh hơn và làm nổ 1 ô bao quanh nó.@- Hết sinh lực sẽ bị thua trận.@- Lương thực tiêu hao trong lượt đi của mỗi đối phương.@- Điểm kinh nghiệm dùng để tăng cấp độ.@- Cấp độ càng cao thì lực tấn công và sinh lực càng lớn.@- Tiền vàng dùng để mua vật dụng hỗ trợ trong cửa hàng.@- Nếu xếp sai biểu tượng sẽ bị mất lượt đi và mất sinh lực.@*Chú ý: Vật dụng trong cửa hàng rất hữu dụng trong các trận chiến. Bạn nên mua thường xuyên và sử dụng khi cần thiết.", null, localdi, null, null);
        localdj.c(new da("Đóng", 4));
        localdj.a(this);
        cz.d().a(localdj, true);
    }

    public void f(int paramInt1, int paramInt2) {
    }

    public void a(String paramString, int paramInt) {
        int i = this.v[paramInt];
        switch (i) {
            case 0:
                if (f.e()) {
                    cf.a().d();
                    cz.d().e.a(2, 5, false);
                } else {
                    cf.a().d();
                    cz.d().e.a(2, 7, false);
                }
                break;
            case 1:
                if (!this.w) {
                    h(9, 4);
                    return;
                }
                cf.a().d();
                bg.k();
                bg.x();
                cz.d().a(new df(true), false);
                break;
            case 2:
                x();
                break;
            case 3:
                y();
                break;
            case 4:
                e(2);
                break;
            case 5:
                z();
                break;
            case 6:
                a(null);
                break;
            case 7:
                C();
                break;
            case 8:
                F();
                break;
            default:
                a(h.f, null);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.ac

 * JD-Core Version:    0.7.0.1

 */