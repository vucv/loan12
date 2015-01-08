import javax.microedition.lcdui.Graphics;

public abstract class ds
{
  protected int p;
  protected int q;
  protected int r;
  protected int s;
  protected int t;
  protected boolean u = true;
  
  public abstract void a(Graphics paramGraphics, int paramInt1, int paramInt2);
  
  public void a(Graphics paramGraphics)
  {
    a(paramGraphics, 0, 0);
  }
  
  public void d(int paramInt1, int paramInt2)
  {
    f(paramInt1);
    g(paramInt2);
  }
  
  public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    d(paramInt1, paramInt2);
    h(paramInt3);
    i(paramInt4);
  }
  
  public int i()
  {
    return this.p;
  }
  
  public void f(int paramInt)
  {
    this.p = paramInt;
  }
  
  public int j()
  {
    return this.q;
  }
  
  public void g(int paramInt)
  {
    this.q = paramInt;
  }
  
  public int k()
  {
    return this.r;
  }
  
  public void h(int paramInt)
  {
    this.r = paramInt;
  }
  
  public int l()
  {
    return this.s;
  }
  
  public void i(int paramInt)
  {
    this.s = paramInt;
  }
  
  public void j(int paramInt)
  {
    this.t = paramInt;
  }
  
  public void k(int paramInt)
  {
    if ((paramInt & 0x20) == 32) {
      this.q -= this.s;
    } else if ((paramInt & 0x2) == 2) {
      this.q -= this.s / 2;
    }
    if ((paramInt & 0x8) == 8) {
      this.p -= this.r;
    } else if ((paramInt & 0x1) == 1) {
      this.p -= this.r / 2;
    }
  }
  
  public boolean c(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool = e(paramInt1, paramInt3);
    bool &= f(paramInt2, paramInt4);
    return bool;
  }
  
  public final int b(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Math.abs(paramInt2 - paramInt1);
    if (i < paramInt3) {
      return 1;
    }
    return i / paramInt3;
  }
  
  public final boolean e(int paramInt1, int paramInt2)
  {
    int i = this.p > paramInt1 ? 1 : 0;
    if (i != 0) {
      this.p -= paramInt2;
    } else {
      this.p += paramInt2;
    }
    int j = this.p > paramInt1 ? 1 : 0;
    if (i != j) {
      this.p = paramInt1;
    }
    return this.p == paramInt1;
  }
  
  public final boolean f(int paramInt1, int paramInt2)
  {
    int i = this.q > paramInt1 ? 1 : 0;
    if (i != 0) {
      this.q -= paramInt2;
    } else {
      this.q += paramInt2;
    }
    int j = this.q > paramInt1 ? 1 : 0;
    if (i != j) {
      this.q = paramInt1;
    }
    return this.q == paramInt1;
  }
  
  public boolean m()
  {
    return this.u;
  }
  
  public void a(boolean paramBoolean)
  {
    this.u = paramBoolean;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ds
 * JD-Core Version:    0.7.0.1
 */