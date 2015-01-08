package com.mic.smsgame.suquan;

import ae;
import aj;
import an;
import ap;
import ar;
import az;
import bg;
import bv;
import bz;
import com.mg.smsgame.main.b;
import cx;
import cz;
import da;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import y;

public class q
  extends aj
  implements b
{
  public static final int[] c = { 3, 3, 1000, 3, 3, 3 };
  private Image d;
  private Image e;
  private Image[] f;
  private Image t;
  private Image u;
  private Image v;
  private final int[] w = { -1, -1, -1, 1000, 250, 500 };
  private boolean x;
  private String[] y;
  private int z;
  private String[] A;
  private int B;
  private int C;
  private t D;
  private int[] E;
  private String F;
  
  public q(int paramInt)
  {
    b();
    this.g = (this.b_ - this.f[0].getWidth() >> 1);
    this.a_ = ((this.c_ - 180 >> 1) - 14);
    this.A = h.d;
    v();
    u();
    this.C = paramInt;
    this.E = bg.v();
    if (this.E == null) {
      this.E = new int[6];
    }
    this.D = new t();
    this.q = 4;
  }
  
  private void b()
  {
    this.d = Image.createImage(this.b_, this.c_);
    Image localImage = az.b("/bkmenu");
    Graphics localGraphics = this.d.getGraphics();
    localGraphics.drawImage(localImage, cx.l, cx.m, 0);
    localImage = null;
    y.a(localGraphics, 8553090, 0, 0, this.b_, this.c_);
    this.e = az.b("/itemshops");
    this.f = new Image[2];
    this.f[0] = az.b("/frame1");
    this.f[1] = az.b("/frame2");
    this.t = az.b("/frame3");
    this.u = az.b("/focusitem");
    this.v = az.b("/gold");
    a(null);
    a(this);
  }
  
  public void a(Graphics paramGraphics)
  {
    paramGraphics.drawImage(this.d, 0, 0, 0);
    u.a(paramGraphics, this.g, this.a_, 200, 7814005, this.f);
    if (this.x) {
      return;
    }
    int i = this.g;
    int j = this.a_ + 12;
    int k = 0;
    for (int m = 0; m < 2; m++)
    {
      i = this.g + 25;
      for (int n = 0; n < 3; n++)
      {
        if (this.z == k) {
          y.a(paramGraphics, this.u, 0, 0, 36, 36, i, j, 0);
        } else {
          y.a(paramGraphics, this.u, 36, 0, 36, 36, i, j, 0);
        }
        y.a(paramGraphics, this.e, k * 28, 0, 28, 28, i + 4, j + 4, 0);
        if (k != 2) {
          this.D.a(paramGraphics, this.E[k], i + 34, j + 29, 2);
        }
        i += 45;
        k++;
      }
      j += 38;
    }
    paramGraphics.drawImage(this.t, this.g + this.f[0].getWidth() / 2, j, 17);
    bz.c.a(paramGraphics, h.c[this.z], this.g + this.f[0].getWidth() / 2, j + 8, 1);
    bz.a(paramGraphics, bz.d, this.y, this.g + 11, j + 22, this.b_, 120, 0);
    bz.c.a(paramGraphics, this.F, this.g + this.f[0].getWidth() / 2, this.a_ + 180, 1);
    paramGraphics.drawImage(this.v, 0, -4, 0);
    bz.d.a(paramGraphics, "= " + this.E[2], this.v.getWidth() + 1, 0, 0);
  }
  
  private void u()
  {
    m();
    if (!this.x)
    {
      a(new da("Mua", -1));
      b(new da("Trở về", -3));
      c(new da(" ", -1));
    }
  }
  
  public void a() {}
  
  public void a(int paramInt)
  {
    if (this.x) {
      return;
    }
    int i = this.z;
    switch (paramInt)
    {
    case 14: 
      this.z = ((this.z - 1 + this.w.length) % this.w.length);
      break;
    case 15: 
      this.z = ((this.z + 1) % this.w.length);
      break;
    case 12: 
      this.z = ((this.z - 3 + this.w.length) % this.w.length);
      break;
    case 13: 
      this.z = ((this.z + 3) % this.w.length);
    }
    if (this.z != i) {
      v();
    }
  }
  
  private void v()
  {
    this.y = bz.a(this.A[this.z], this.f[0].getWidth() - 15);
    if (this.w[this.z] > 0) {
      this.F = (" Giá " + this.w[this.z] + " vàng");
    } else {
      this.F = " ";
    }
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    int i = this.a_ + 12;
    int j = this.g + 25;
    if ((paramInt2 >= i) && (paramInt2 <= i + 76)) {
      for (int k = 0; k < 3; k++)
      {
        if ((paramInt1 >= j) && (paramInt1 <= j + 36))
        {
          int m = (paramInt2 - i) / 38 * 3 + k;
          if (m != this.z)
          {
            this.z = m;
            v();
          }
        }
        j += 45;
      }
    }
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    ae localae;
    switch (paramInt2)
    {
    case -1: 
      String str = this.A[this.z] + " " + "Bạn có muốn mua không?";
      if (this.z <= 2) {
        str = str + " Tham khảo giá tại http://free.ola.vn.";
      }
      localae = cz.a("Thông tin", str, "Có", -4, "Không", -5, 1);
      localae.a(this);
      cz.d().a(localae, true);
      break;
    case -2: 
      break;
    case -3: 
      cz.a().a(4, this.C, false);
      break;
    case -4: 
      cz.d().a(false);
      if (this.w[this.z] > 0)
      {
        if (this.E[2] < this.w[this.z])
        {
          localae = cz.a("Thông tin", "Không đủ vàng để mua vật phẩm này? Bạn có muốn mua thêm vàng không? Tham khảo giá tại http://free.ola.vn.", "Có", -6, "Không", -5, 1);
          localae.a(this);
          cz.d().a(localae, true);
        }
        else
        {
          this.E[2] -= this.w[this.z];
          bg.b(2, this.w[this.z]);
          this.B = this.z;
          w();
        }
      }
      else
      {
        this.B = this.z;
        e(this.B);
      }
      break;
    case -5: 
      cz.d().a(false);
      break;
    case -6: 
      cz.d().a(false);
      this.B = 2;
      e(this.B);
    }
  }
  
  private void e(int paramInt)
  {
    an.a("shop", cx.G, new o(this));
  }
  
  private void w()
  {
    this.E[this.B] += c[this.B];
    bg.a(this.B, c[this.B], 6);
    ae localae = cz.a("Thông tin", "Đã mua vật phẩm thành công!", "Đóng", -5, 1);
    localae.a(this);
    cz.d().a(localae, true);
  }
  
  static void a(q paramq)
  {
    paramq.w();
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.q
 * JD-Core Version:    0.7.0.1
 */