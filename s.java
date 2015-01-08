import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class s
  extends ds
{
  private Image a = az.b("/m/fsw");
  private Image b = az.b("/crystalblue");
  private int c;
  private int d = this.b.getWidth() / 3;
  private int e = this.b.getHeight();
  private int f;
  private int g;
  private int h;
  private boolean i;
  private boolean j;
  
  public void a()
  {
    this.g = 15;
    this.h = 0;
    this.j = false;
    this.i = false;
    this.c = 0;
  }
  
  public void b()
  {
    if (this.i) {
      return;
    }
    this.c += 1;
    if (this.c % 2 == 0) {
      return;
    }
    if (this.g > 0) {
      this.g -= 3;
    }
    if (!this.j)
    {
      this.f += 1;
      if (this.f >= 3)
      {
        this.f = 3;
        this.j = true;
      }
    }
    else
    {
      this.h += 5;
      this.f -= 1;
      if (this.f < 0)
      {
        this.f = 0;
        this.i = true;
      }
    }
  }
  
  public boolean c()
  {
    return this.i;
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (this.i) {
      return;
    }
    paramGraphics.drawImage(this.a, paramInt1 + this.p + this.g, paramInt2 + this.q + this.g, 3);
    paramGraphics.drawRegion(this.a, 0, 0, 24, 24, 2, paramInt1 + this.p - this.g, paramInt2 + this.q + this.g, 3);
    if (!this.j)
    {
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p, paramInt2 + this.q, 3);
    }
    else
    {
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p + this.h, paramInt2 + this.q, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p, paramInt2 + this.q + this.h, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p - this.h, paramInt2 + this.q, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p, paramInt2 + this.q - this.h, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p + this.h, paramInt2 + this.q + this.h, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p + this.h, paramInt2 + this.q - this.h, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p - this.h, paramInt2 + this.q + this.h, 3);
      y.a(paramGraphics, this.b, this.d * this.f, 0, this.d, this.e, paramInt1 + this.p - this.h, paramInt2 + this.q - this.h, 3);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     s
 * JD-Core Version:    0.7.0.1
 */