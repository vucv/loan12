import javax.microedition.lcdui.Graphics;

public class h
  extends ds
{
  private int a;
  private int b;
  
  public h()
  {
    this(cx.S);
  }
  
  public h(int paramInt)
  {
    a(paramInt);
  }
  
  public void a(int paramInt)
  {
    this.a = (paramInt & 0xFFFFFF);
    int i = (paramInt & 0xFF0000) >> 16;
    int j = (paramInt & 0xFF00) >> 8;
    int k = paramInt & 0xFF;
    int m = 6;
    this.b = ((255 - i) / m + i << 16 | (255 - j) / m + j << 8 | (255 - k) / m + k);
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    cz.b(paramGraphics, this.a, this.b, 0, cx.g - 17, cx.f, 17);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     h
 * JD-Core Version:    0.7.0.1
 */