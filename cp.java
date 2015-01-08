import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class cp
  extends e
{
  private dq d;
  
  public cp(Image paramImage, int paramInt)
  {
    this.d = new dq(0, 0, paramImage, paramInt);
  }
  
  public void a(byte[][] paramArrayOfByte)
  {
    if (this.d != null) {
      this.d.a(paramArrayOfByte);
    }
  }
  
  public void a(int paramInt)
  {
    if (this.d != null) {
      this.d.c(paramInt);
    }
  }
  
  public void a(boolean paramBoolean)
  {
    if (this.d != null) {
      this.d.b(paramBoolean);
    }
  }
  
  public void b()
  {
    if (this.d != null) {
      this.d.e();
    }
  }
  
  public void b(int paramInt)
  {
    if (this.d != null) {
      this.d.a_(paramInt);
    }
  }
  
  public int a()
  {
    return this.d.l();
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (this.d != null) {
      this.d.a(paramGraphics, paramInt1 + (this.c.c - this.d.k()) / 2, paramInt2);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cp
 * JD-Core Version:    0.7.0.1
 */