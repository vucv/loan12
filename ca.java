import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ca
  extends ds
{
  private int[] a;
  private String[] b;
  private int c;
  private Image d;
  private String[] e;
  private int f;
  private int g;
  private String h;
  private cm i = new cm("/f/ari_b");
  private int j;
  private int[] k;
  private int[] l;
  private int m;
  private int n;
  private int o = dd.d();
  private String[] v;
  
  public ca(String[][] paramArrayOfString)
  {
    h(cx.f - 10);
    i(7 * this.i.a() + 15);
    this.j = ((cx.g >> 1) + 20);
    f(cx.f - k() >> 1);
    g(this.j - this.s - 15);
    this.a = new int[paramArrayOfString.length];
    this.b = new String[paramArrayOfString.length];
    for (int i1 = 0; i1 < paramArrayOfString.length; i1++)
    {
      if (paramArrayOfString[i1][0] == null)
      {
        this.a[i1] = -1;
      }
      else
      {
        this.a[i1] = Integer.parseInt(paramArrayOfString[i1][0]);
        if (this.a[i1] == 0) {
          this.a[i1] = this.o;
        }
      }
      this.b[i1] = paramArrayOfString[i1][1];
    }
    this.c = 0;
    this.h = dd.c();
    this.k = new int[4];
    i1 = cx.f / 4;
    this.k[0] = (i1 / 2 - 15);
    this.k[2] = (this.k[0] + i1);
    this.k[3] = (this.k[2] + i1);
    this.k[1] = (this.k[3] + i1);
    this.l = new int[4];
    this.n = 0;
    this.m = -1;
    a(this.a[0], this.b[0], true);
  }
  
  public boolean a()
  {
    this.f += 6;
    if (this.f >= this.e.length)
    {
      this.c += 1;
      if (this.c >= this.a.length) {
        return false;
      }
      a(this.a[this.c], this.b[this.c], true);
    }
    this.g = (this.f + 6 - 1);
    if (this.g >= this.e.length) {
      this.g = (this.e.length - 1);
    }
    int i1 = this.g - this.f + 1;
    h(cx.f - 10);
    i((i1 + 1) * this.i.a() + 15);
    this.j = ((cx.g >> 1) + 20);
    f(cx.f - k() >> 1);
    g(this.j - this.s - 15);
    c();
    return true;
  }
  
  private final void a(int paramInt)
  {
    if (paramInt >= 0)
    {
      this.m = -1;
      for (int i1 = 0; i1 < this.n; i1++) {
        if (this.l[i1] == paramInt) {
          this.m = i1;
        }
      }
      if (this.m < 0)
      {
        this.m = this.n;
        this.l[(this.n++)] = paramInt;
      }
    }
    else
    {
      this.m = -1;
    }
  }
  
  private void a(int paramInt, String paramString, boolean paramBoolean)
  {
    if (paramInt >= 0)
    {
      a(paramInt);
      this.v = ao.b(u.a(paramInt), " ");
    }
    paramString = ao.a(paramString, "{p}", this.h);
    this.e = this.i.a(paramString, this.r - 12);
    this.f = 0;
    this.g = 5;
    if (this.g >= this.e.length) {
      this.g = (this.e.length - 1);
    }
    int i1 = this.g - this.f + 1;
    h(cx.f - 10);
    i((i1 + 1) * this.i.a() + 15);
    this.j = ((cx.g >> 1) + 20);
    f(cx.f - k() >> 1);
    g(this.j - this.s - 15);
    c();
  }
  
  private void c()
  {
    this.d = Image.createImage(this.r - 10, this.s - 10);
    Graphics localGraphics = this.d.getGraphics();
    localGraphics.setColor(16777215);
    localGraphics.fillRect(0, 0, this.r, this.s);
    int i1 = 3;
    int i2 = 5;
    for (int i3 = this.f; i3 <= this.g; i3++)
    {
      this.i.a(localGraphics, this.e[i3], i1, i2, 0);
      i2 += this.i.a();
    }
  }
  
  private final void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramInt1 >= 0)
    {
      paramGraphics.setColor(16777215);
      paramGraphics.fillRect(paramInt2, paramInt3, 34, 34);
      u.a(paramGraphics, paramInt1, paramInt2 + 2, paramInt3 + 2, paramBoolean1);
      if (paramBoolean2)
      {
        paramInt2 += 17;
        paramInt3 += 35;
        for (int i1 = 0; i1 < this.v.length; i1++)
        {
          bz.c.a(paramGraphics, this.v[i1], paramInt2, paramInt3, 1);
          paramInt3 += bz.c.a();
        }
      }
    }
  }
  
  private final void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramGraphics.setColor(paramInt7);
    paramGraphics.fillRect(paramInt1 + 4, paramInt2, paramInt3 - 8, paramInt4);
    paramGraphics.fillRect(paramInt1, paramInt2 + 4, paramInt3, paramInt4 - 8);
    paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 2, paramInt3 - 2, paramInt4 - 4);
    paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 1, paramInt3 - 4, paramInt4 - 2);
    paramGraphics.setColor(paramInt6);
    paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, paramInt3 - 4, paramInt4 - 4);
    paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 1, paramInt3 - 8, paramInt4 - 2);
    paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 4, paramInt3 - 2, paramInt4 - 8);
    if (paramInt5 >= 0)
    {
      paramGraphics.fillTriangle(paramInt5 - 5, paramInt2 + paramInt4 - 1, paramInt5 + 5, paramInt2 + paramInt4 - 1, paramInt5, paramInt2 + paramInt4 + 7);
      paramGraphics.setColor(paramInt7);
      paramGraphics.drawLine(paramInt5, paramInt2 + paramInt4 + 7, paramInt5 - 5, paramInt2 + paramInt4 - 1);
      paramGraphics.drawLine(paramInt5, paramInt2 + paramInt4 + 7, paramInt5 + 5, paramInt2 + paramInt4 - 1);
    }
  }
  
  public void b() {}
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (this.d == null) {
      return;
    }
    if (this.a[this.c] >= 0) {
      a(paramGraphics, this.p, this.q, this.r, this.s, this.k[this.m] + 15, 16777215, 0);
    } else {
      a(paramGraphics, this.p, this.q, this.r, this.s, -1, 16777215, 0);
    }
    paramGraphics.drawImage(this.d, this.p + 5, this.q + 5, 0);
    for (int i1 = 0; i1 < this.n; i1++) {
      a(paramGraphics, this.l[i1], this.k[i1], this.j + 2, i1 % 2 == 0, i1 == this.m);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ca
 * JD-Core Version:    0.7.0.1
 */