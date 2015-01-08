import com.mg.smsgame.main.b;
import com.mic.smsgame.suquan.u;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ah
  extends ae
  implements b
{
  private Image[] t = bs.a().k;
  private Image u = bs.a().z;
  private Image v = bs.a().A;
  private Image[] w;
  private int x;
  private dy y;
  private int z;
  private int A;
  private String[] B;
  private String[] C;
  private String[] D;
  private int E;
  private int F;
  private int[] G;
  int[] e = { 1, 3, 5, 4, 0, 2, 4, 03 };
  int[] f = { 1, 2, 2, 5, 1, 1, 5, 5, 2 };
  
  public ah(int[] paramArrayOfInt)
  {
    super(1);
    this.b_ = this.t[0].getWidth();
    this.c_ = 180;
    this.g = (cx.f - 176 >> 1);
    this.a_ = (cx.g - 180 >> 1);
    this.a_ -= 25;
    this.x = 0;
    this.z = 0;
    this.w = bs.a().i;
    this.C = new String[3];
    this.B = new String[3];
    this.G = paramArrayOfInt;
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      this.B[i] = com.mic.smsgame.suquan.h.a[paramArrayOfInt[i]];
      this.C[i] = com.mic.smsgame.suquan.h.b[paramArrayOfInt[i]];
    }
    this.D = bz.a(this.C[this.z], this.b_ - 15);
    this.A = this.w.length;
    a(this);
  }
  
  protected void e()
  {
    if (this.E < 1) {
      this.E = 8;
    } else {
      this.E -= 1;
    }
  }
  
  public void a(int paramInt, dy paramdy, bn parambn)
  {
    if (this.F != parambn.m())
    {
      this.F = parambn.m();
      for (int i = this.C.length - 1; i > -1; i--)
      {
        String str = com.mic.smsgame.suquan.h.b[this.G[i]];
        int j = str.indexOf("=");
        if (j >= 0) {
          this.C[i] = (str.substring(0, j) + this.F * this.e[this.G[i]] / this.f[this.G[i]] + str.substring(j + 1));
        }
      }
    }
    this.x = paramInt;
    this.y = paramdy;
    this.z = 0;
    m();
    cz.d().a(this, false);
    this.c = true;
    b(new da("Đóng", -2));
    if (this.z < paramInt)
    {
      a(new da("Dùng", -1));
      c(new da(" ", -1));
    }
    this.D = bz.a(this.C[this.z], this.b_ - 15);
  }
  
  public void a(int paramInt)
  {
    int i = this.z;
    switch (paramInt)
    {
    case 14: 
      this.z = ((this.z - 1 + this.A) % this.A);
      break;
    case 15: 
      this.z = ((this.z + 1) % this.A);
    }
    if (i != this.z) {
      u();
    }
  }
  
  private void u()
  {
    if (this.z < this.x)
    {
      a(new da("Dùng", -1));
      c(new da(" ", -1));
    }
    else
    {
      if (o()) {
        this.c = true;
      }
      a(null);
      c(null);
    }
    this.D = bz.a(this.C[this.z], this.b_ - 15);
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    int i = this.g + 25;
    int j = this.a_ + 12;
    if ((paramInt2 >= j) && (paramInt2 <= j + 36)) {
      for (int k = 0; k < this.A; k++)
      {
        if ((paramInt1 >= i) && (paramInt1 <= i + 36) && (k != this.z))
        {
          this.z = k;
          u();
        }
        i += 45;
      }
    }
  }
  
  public void a(Graphics paramGraphics)
  {
    int i = this.g;
    int j = this.a_;
    u.a(paramGraphics, i, j, 180, 7814005, this.t);
    paramGraphics.drawImage(this.u, i + this.b_ / 2, j + 50, 17);
    int k = i + 25;
    int m = j + 12;
    for (int n = 0; n < this.A; n++)
    {
      if ((this.z == n) && (this.E % 2 == 0)) {
        y.a(paramGraphics, this.v, 0, 0, 36, 36, k, m, 0);
      } else {
        y.a(paramGraphics, this.v, 36, 0, 36, 36, k, m, 0);
      }
      paramGraphics.drawImage(this.w[n], k + 18, m + 18, 3);
      k += 45;
    }
    k = i + 25 + 45 * this.x;
    m = j + 12;
    for (n = this.x; n < this.A; n++)
    {
      y.a(paramGraphics, 16755884, k + 2, m + 2, 30, 30);
      k += 45;
    }
    k = i + this.b_ / 2;
    m = j + 58;
    bz.c.a(paramGraphics, this.B[this.z], k, m, 1);
    bz.a(paramGraphics, bz.d, this.D, this.g + 11, m + 15, this.b_, 120, 0);
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    case -1: 
      this.y.a("skill", this.z);
    case -2: 
      cz.d().a(false);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ah
 * JD-Core Version:    0.7.0.1
 */