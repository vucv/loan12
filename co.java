import com.mic.smsgame.suquan.t;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class co
{
  private Image[] a;
  private Image b;
  private int c;
  private int d;
  private int e;
  private int f;
  private int[] g;
  private boolean[] h;
  private int[] i;
  private int j;
  private t k;
  private av l;
  private Image m;
  private int n;
  private int o;
  private dq p;
  private Image q;
  private int r;
  private c s;
  private int t;
  private Image u;
  private Image v;
  private Image w;
  private Image x;
  private String y;
  private boolean[] z;
  private Image A;
  
  public co(boolean[] paramArrayOfBoolean)
  {
    this.z = paramArrayOfBoolean;
    this.a = new Image[2];
    this.a[0] = az.b("/frame1");
    this.a[1] = az.b("/frame2");
    this.b = az.b("/frame3");
    this.A = az.b("/m/lock");
    this.m = az.b("/m/g");
    this.n = (this.m.getWidth() / 5);
    this.o = this.m.getHeight();
    this.q = az.b("/m/b");
    this.k = new t();
    this.l = new av();
    this.e = this.a[0].getWidth();
    this.f = 200;
    this.c = (cx.f - this.e >> 1);
    this.d = (cx.g - this.f >> 1);
    this.r = bg.f(2);
    this.j = -1;
    this.g = dd.k();
    this.h = new boolean[this.g.length];
    this.i = new int[this.g.length];
    for (int i1 = 0; i1 < this.h.length; i1++)
    {
      this.i[i1] = dd.A(i1);
      if ((this.r >= this.i[i1]) && (paramArrayOfBoolean[i1] != 0))
      {
        this.j = 0;
        this.h[i1] = true;
      }
    }
    this.u = az.b("/m/af");
    this.v = az.b("/m/ih");
    this.w = az.b("/m/isw");
    this.x = az.b("/gold");
    b(this.j);
    this.p = new dq(az.b("/focusitem"), 2);
    this.p.c(2);
    this.p.b(true);
    this.p.a(new byte[][] { { 0, 1 } });
    this.p.a_(0);
  }
  
  public boolean a()
  {
    return this.j >= 0;
  }
  
  private void b(int paramInt)
  {
    if (paramInt < 0) {
      return;
    }
    dd.f = dd.e(paramInt, dd.e.e);
    this.s = dd.f;
    this.t = dd.f(paramInt, dd.e.e);
    this.y = u.a(this.g[paramInt]);
  }
  
  public void b()
  {
    if (this.j < 0) {
      return;
    }
    if (bg.f(5) > 0)
    {
      dd.h = true;
      bg.g(5);
    }
    else if (dd.e.e > dd.f.e)
    {
      dd.h = true;
    }
    else if (dd.e.e < dd.f.e)
    {
      dd.h = false;
    }
    else
    {
      dd.h = ay.a(2) == 0;
    }
    dd.a = this.j;
    bg.b(2, dd.A(this.j));
  }
  
  public void c()
  {
    if (this.j >= 0) {
      this.p.e();
    }
  }
  
  public void a(int paramInt)
  {
    if (this.j < 0) {
      return;
    }
    switch (paramInt)
    {
    case 14: 
      do
      {
        this.j = ((this.j - 1 + this.g.length) % this.g.length);
      } while (this.h[this.j] == 0);
      break;
    case 15: 
      do
      {
        this.j = ((this.j + 1) % this.g.length);
      } while (this.h[this.j] == 0);
    }
    b(this.j);
  }
  
  private void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    u.a(paramGraphics, this.s.a, paramInt1 + 10, paramInt2 + 3, true);
    bz.c.a(paramGraphics, this.y, paramInt1 + 45, paramInt2 + 2, 0);
    paramGraphics.drawImage(this.u, paramInt1 + 8, paramInt2 + 1, 0);
    paramGraphics.drawImage(this.v, paramInt1 + 54, paramInt2 + 20, 0);
    paramGraphics.drawImage(this.w, paramInt1 + 111, paramInt2 + 20, 0);
    this.l.a(paramGraphics, this.s.c, paramInt1 + 68, paramInt2 + 22);
    this.l.a(paramGraphics, this.s.d, paramInt1 + 126, paramInt2 + 22);
    bz.c.a(paramGraphics, "Cấp " + this.s.e, paramInt1 + 8, paramInt2 + 34, 0);
  }
  
  private void b(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (this.j < 0)
    {
      bz.d.a(paramGraphics, "Bạn không đủ vàng", cx.f >> 1, paramInt2 + 10, 1);
    }
    else
    {
      a(paramGraphics, paramInt1, paramInt2);
      bz.d.a(paramGraphics, "K.Nghiệm thu được:", paramInt1 + 5, paramInt2 + 55, 0);
      bz.c.a(paramGraphics, this.t + " (" + dd.B(this.j) + "%)", cx.f >> 1, paramInt2 + 70, 1);
    }
    paramGraphics.drawImage(this.x, paramInt1 + 132, paramInt2 + 100, 0);
    this.l.a(paramGraphics, this.r, paramInt1 + 130 - this.l.a(this.r), paramInt2 + 105);
  }
  
  public void a(Graphics paramGraphics)
  {
    com.mic.smsgame.suquan.u.a(paramGraphics, this.c, this.d, this.f, 7814005, this.a);
    bz.c.a(paramGraphics, "Đấu trường", cx.f >> 1, this.d + 12, 1);
    paramGraphics.drawImage(this.b, this.c + this.a[0].getWidth() / 2, this.d + 62, 17);
    paramGraphics.drawImage(this.b, this.c + this.a[0].getWidth() / 2, this.d + 117, 17);
    int i1 = this.c + 9;
    for (int i2 = 0; i2 < this.g.length; i2++)
    {
      if (this.z[i2] != 0)
      {
        y.a(paramGraphics, this.m, i2 * this.n, 0, this.n, this.o, i1, this.d + 30, 0);
        this.k.a(paramGraphics, this.i[i2], i1 + (this.n >> 1), this.d + 53, 1);
        if (this.h[i2] == 0) {
          paramGraphics.drawImage(this.q, i1, this.d + 30, 0);
        }
      }
      else
      {
        paramGraphics.drawImage(this.A, i1 + 15, this.d + 45, 3);
      }
      i1 += 32;
    }
    if (this.j >= 0)
    {
      i2 = this.c + 9 + this.j * 32;
      this.p.a(paramGraphics, i2 - 3, this.d + 30 - 3);
      y.a(paramGraphics, this.m, this.j * this.n, 0, this.n, this.o, i2, this.d + 30, 0);
      this.k.a(paramGraphics, this.i[this.j], i2 + (this.n >> 1), this.d + 53, 1);
    }
    b(paramGraphics, this.c + 9, this.d + 70);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     co
 * JD-Core Version:    0.7.0.1
 */