import com.mic.smsgame.suquan.b;
import com.mic.smsgame.suquan.t;
import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class cr
  extends ae
{
  private Image e;
  private Image f;
  private Image t;
  private av u;
  private c v;
  private int w;
  private int x;
  private Image y;
  private Image z;
  private Image A;
  private int B;
  private int C;
  private t D;
  private int E;
  private int F;
  private int G;
  private int H;
  private int I;
  private int J;
  private int K;
  private int L;
  private de M;
  private boolean N = false;
  private b O;
  private dk P;
  
  public cr(boolean paramBoolean)
  {
    super(1);
    this.N = paramBoolean;
    this.b_ = 170;
    this.c_ = 200;
    this.g = (cx.f - this.b_ >> 1);
    this.a_ = ((cx.g - this.c_ >> 1) - 10);
    if (dd.a >= 0) {
      dd.e.c = dd.b;
    }
    this.v = dd.e.c();
    this.v.c = (this.v.c > this.v.b ? this.v.b : this.v.c);
    this.w = dd.b();
    this.x = bg.f(2);
    this.u = new av();
    this.D = new t();
    this.e = az.b("/gold");
    this.t = az.b("/book");
    this.f = az.b("/m/isw");
    this.z = az.b("/m/bl");
    this.A = az.b("/f/divide");
    this.y = az.b("/m/lv");
    this.B = this.z.getWidth();
    this.C = (this.z.getHeight() / 2);
    if (dd.a >= 0) {
      dd.j = dd.f(dd.a, dd.e.e);
    }
    this.E = dd.u(this.v.e + 1);
    this.F = dd.u(this.v.e);
    if (dd.a < 0) {
      this.G = dd.i;
    }
    if (dd.a < 0) {
      this.H = dd.j(dd.c);
    }
    this.K = dd.j;
    if (dd.a < 0) {
      this.J = dd.i(dd.c);
    }
    if (dd.a < 0) {
      this.I = dd.a(dd.e, dd.f);
    }
    this.O = new b(az.b("/crystalviolet"));
    this.O.a_(false);
    this.L = 50;
    this.P = new dk("lvu");
  }
  
  public cr()
  {
    this(false);
  }
  
  public void c(de paramde)
  {
    this.M = paramde;
  }
  
  public void a(int paramInt)
  {
    if (this.L > 0) {
      this.L = 1;
    }
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    if (this.L > 0) {
      this.L = 1;
    }
  }
  
  protected void e()
  {
    this.c = true;
    if (this.L >= 0)
    {
      this.L -= 1;
      if (this.L == 0) {
        super.c(this.M);
      }
    }
    else
    {
      int i = 0;
      if (!this.N)
      {
        if (this.G > 0)
        {
          i = this.G / 10;
          i = i == 0 ? 1 : i;
          this.G -= i;
          this.x += i;
        }
        if (this.K > 0)
        {
          i = this.K / 10;
          i = i == 0 ? 1 : i;
          this.K -= i;
          this.w += i;
        }
        if (this.I > 0)
        {
          i = this.I / 10;
          i = i == 0 ? 1 : i;
          this.I -= i;
          this.w += i;
        }
      }
      else
      {
        if (this.J > 0)
        {
          i = this.J / 10;
          i = i == 0 ? 1 : i;
          this.J -= i;
          this.w += i;
        }
        if (this.H > 0)
        {
          i = this.H / 10;
          i = i == 0 ? 1 : i;
          this.H -= i;
          this.x += i;
        }
      }
      while (dd.c(this.v.e + 1, this.w))
      {
        this.v.a();
        this.E = dd.u(this.v.e + 1);
        this.F = dd.u(this.v.e);
        System.out.println(this.E + " " + this.F);
        this.O.a(this.g + 150, this.a_ + 20, 1, "");
        if (!this.P.e()) {
          this.P.b();
        }
      }
      this.O.b();
    }
  }
  
  private void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    paramGraphics.setColor(16777215);
    paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, 34, 34);
    u.a(paramGraphics, this.v.a, paramInt1 + 4, paramInt2 + 4, true);
    bz.c.a(paramGraphics, u.a(this.v.a), paramInt1 + 40, paramInt2 + 0, 0);
    y.a(paramGraphics, this.z, 0, this.C, this.B, this.C, paramInt1 + 40, paramInt2 + 15, 0);
    y.a(paramGraphics, this.z, 0, 0, this.B * this.v.c / this.v.b, this.C, paramInt1 + 40, paramInt2 + 15, 0);
    this.D.a(paramGraphics, this.v.c, paramInt1 + 92, paramInt2 + 16, 2);
    this.D.a(paramGraphics, this.v.b, paramInt1 + 98, paramInt2 + 16, 0);
    paramGraphics.drawImage(this.A, paramInt1 + 93, paramInt2 + 14, 0);
    y.a(paramGraphics, this.y, 0, this.C, this.B, this.C, paramInt1 + 40, paramInt2 + 28, 0);
    y.a(paramGraphics, this.y, 0, 0, this.B * (this.w - this.F) / (this.E - this.F), this.C, paramInt1 + 40, paramInt2 + 28, 0);
    this.D.a(paramGraphics, this.w, paramInt1 + 92, paramInt2 + 29, 2);
    this.D.a(paramGraphics, this.E, paramInt1 + 98, paramInt2 + 29, 0);
    paramGraphics.drawImage(this.A, paramInt1 + 93, paramInt2 + 27, 0);
    paramGraphics.drawImage(this.e, paramInt1 + 132, paramInt2 + 36, 0);
    this.u.a(paramGraphics, this.x, paramInt1 + 130 - this.u.a(this.x), paramInt2 + 41);
    paramGraphics.drawImage(this.f, paramInt1 + 2, paramInt2 + 39, 0);
    this.u.a(paramGraphics, this.v.d, paramInt1 + 17, paramInt2 + 41);
    bz.d.a(paramGraphics, " " + this.v.e, paramInt1 + 151, paramInt2 + 0, 2);
    this.O.a(paramGraphics);
  }
  
  private void b(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (dd.a < 0)
    {
      paramGraphics.drawImage(this.e, paramInt1 + 4, paramInt2, 0);
      bz.c.a(paramGraphics, "Vàng", paramInt1 + 25, paramInt2 + 6, 0);
      if (this.N)
      {
        bz.d.a(paramGraphics, "-Thưởng N.Vụ", paramInt1 + 4, paramInt2 + 21, 0);
        bz.c.a(paramGraphics, this.H, paramInt1 + 160, paramInt2 + 21, 2);
      }
      else
      {
        bz.d.a(paramGraphics, "-Thu thập", paramInt1 + 4, paramInt2 + 21, 0);
        bz.c.a(paramGraphics, this.G, paramInt1 + 160, paramInt2 + 21, 2);
      }
      paramGraphics.drawImage(this.t, paramInt1 + 2, paramInt2 + 52, 0);
      bz.c.a(paramGraphics, "Kinh nghiệm", paramInt1 + 25, paramInt2 + 55, 0);
      if (this.N)
      {
        bz.d.a(paramGraphics, "-Thưởng N.Vụ", paramInt1 + 4, paramInt2 + 70, 0);
        bz.c.a(paramGraphics, this.J, paramInt1 + 160, paramInt2 + 70, 2);
      }
      else
      {
        bz.d.a(paramGraphics, "-Thu thập", paramInt1 + 4, paramInt2 + 70, 0);
        bz.c.a(paramGraphics, this.K, paramInt1 + 160, paramInt2 + 70, 2);
        bz.d.a(paramGraphics, "-Thắng trận", paramInt1 + 4, paramInt2 + 85, 0);
        bz.c.a(paramGraphics, this.I, paramInt1 + 160, paramInt2 + 85, 2);
      }
    }
    else
    {
      paramGraphics.drawImage(this.t, paramInt1 + 2, paramInt2 + 3, 0);
      bz.c.a(paramGraphics, "Kinh nghiệm", paramInt1 + 25, paramInt2 + 6, 0);
      bz.d.a(paramGraphics, "-Thắng trận", paramInt1 + 4, paramInt2 + 21, 0);
      bz.c.a(paramGraphics, this.K, paramInt1 + 160, paramInt2 + 21, 2);
    }
  }
  
  public void a(Graphics paramGraphics)
  {
    cz.a(paramGraphics, cx.P, cx.Q, this.g, this.a_, this.b_, this.c_);
    bz.c.a(paramGraphics, "Kết quả", cx.f >> 1, this.a_ + 5, 1);
    a(paramGraphics, this.g + 9, this.a_ + 20);
    b(paramGraphics, this.g + 5, this.a_ + 78);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cr
 * JD-Core Version:    0.7.0.1
 */