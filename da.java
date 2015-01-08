import javax.microedition.lcdui.Graphics;

public class da
  extends de
{
  private String f;
  private int g = 16777215;
  
  public da(String paramString, int paramInt)
  {
    super(paramInt);
    this.f = paramString;
    this.d = bz.c.a(paramString);
  }
  
  public void a(Graphics paramGraphics)
  {
    if (this.a > 0) {
      bz.c.a(paramGraphics, this.f, this.b, this.c + 1, 0);
    } else {
      bz.d.a(paramGraphics, this.f, this.b, this.c, 0);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     da
 * JD-Core Version:    0.7.0.1
 */