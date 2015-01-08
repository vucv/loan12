import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class dq
  extends cw
{
  protected int m = 0;
  protected Image n;
  protected int o;
  
  public dq() {}
  
  public dq(int paramInt1, int paramInt2, Image paramImage, int paramInt3)
  {
    this(paramInt1, paramInt2);
    a(paramImage, paramInt3);
  }
  
  public dq(Image paramImage, int paramInt)
  {
    a(paramImage, paramInt);
  }
  
  public dq(int paramInt1, int paramInt2)
  {
    d(paramInt1, paramInt2);
  }
  
  public void a(Image paramImage, int paramInt)
  {
    this.o = paramInt;
    this.g = -1;
    if (paramImage != null)
    {
      this.r = (paramImage.getWidth() / this.o);
      this.s = paramImage.getHeight();
      this.n = paramImage;
    }
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (!this.u) {
      return;
    }
    if ((this.n != null) && (this.g > -1) && (this.d[this.c][this.g] > -1)) {
      paramGraphics.drawRegion(this.n, this.d[this.c][this.g] * this.r, 0, this.r, this.s, this.m, this.p + paramInt1, this.q + paramInt2, this.t);
    }
  }
  
  public void a() {}
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     dq
 * JD-Core Version:    0.7.0.1
 */