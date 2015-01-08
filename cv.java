import javax.microedition.lcdui.Graphics;

public class cv
  extends aj
{
  private int v;
  protected int c;
  protected int d;
  protected aj e;
  protected boolean f;
  protected int t = cx.S;
  protected boolean u;
  
  public void a(aj paramaj, boolean paramBoolean)
  {
    if (paramaj != null)
    {
      this.e = paramaj;
      this.u = paramBoolean;
      this.f = true;
    }
  }
  
  public cv()
  {
    b();
  }
  
  public final void b()
  {
    this.v = 0;
    this.c = 0;
    this.d = 5;
    this.e = null;
    this.f = false;
  }
  
  protected void b(Graphics paramGraphics)
  {
    paramGraphics.setColor(16777215);
    paramGraphics.drawRect(0, this.c_ - 20, this.b_, 19);
    int i = this.c_ - 20;
    paramGraphics.setColor(15484506);
    paramGraphics.fillRect(0, i + 1, this.c * this.b_ / 100, 18);
    bz.c.a(paramGraphics, "Vui lòng chờ...", this.b_ / 2, i + 3, 1);
  }
  
  public void a()
  {
    if (this.c == 100)
    {
      if (this.e != null)
      {
        cz.a().a(this.e, false, this.u);
        this.e = null;
      }
      return;
    }
    if (this.f) {
      this.d += 3;
    }
    this.c += this.d;
    if (this.c == 95) {
      this.c = 90;
    }
    if (this.c > 100) {
      this.c = 100;
    }
  }
  
  protected void e(Graphics paramGraphics)
  {
    paramGraphics.setColor(this.t);
    paramGraphics.fillRect(0, 0, this.b_, this.c_);
  }
  
  public final void a(Graphics paramGraphics)
  {
    if (this.v <= 2)
    {
      e(paramGraphics);
      this.v += 1;
    }
    b(paramGraphics);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cv
 * JD-Core Version:    0.7.0.1
 */