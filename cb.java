import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class cb
  extends ds
{
  private Image a;
  private int b;
  private int c;
  
  public cb(Image paramImage, int paramInt1, int paramInt2)
  {
    this.p = paramInt1;
    this.q = paramInt2;
    this.r = paramImage.getWidth();
    this.s = paramImage.getHeight();
    this.a = paramImage;
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    paramGraphics.drawImage(this.a, this.p, this.q + this.b, this.c);
  }
  
  public void a() {}
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cb
 * JD-Core Version:    0.7.0.1
 */