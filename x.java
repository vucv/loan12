import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class x
  extends e
{
  protected Image d;
  
  public x(Image paramImage)
  {
    this.d = paramImage;
    if (this.d != null)
    {
      this.a = this.d.getWidth();
      this.b = this.d.getHeight();
    }
  }
  
  public void a(dl paramdl)
  {
    super.a(paramdl);
    if (this.b > paramdl.d) {
      this.b = paramdl.d;
    }
    if (this.a > paramdl.c) {
      this.a = paramdl.c;
    }
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    y.a(paramGraphics, this.d, 0, 0, this.a, this.b, paramInt1 + this.c.c / 2, paramInt2, 17);
  }
  
  public void b() {}
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     x
 * JD-Core Version:    0.7.0.1
 */