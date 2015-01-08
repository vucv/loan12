import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class u
{
  private Image a = az.b("/avatar");
  private static u b = null;
  private String c = null;
  
  public static final u a()
  {
    if (b == null) {
      b = new u();
    }
    return b;
  }
  
  public static final void a(u paramu)
  {
    b = paramu;
    if (paramu == null) {
      System.gc();
    }
  }
  
  private String b(int paramInt)
  {
    String str = ea.a[paramInt];
    if (str.equals("PLAYER"))
    {
      if (this.c == null) {
        this.c = dd.c();
      }
      str = this.c;
    }
    return str;
  }
  
  public static final String a(int paramInt)
  {
    return a().b(paramInt);
  }
  
  public static final void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    a().b(paramGraphics, paramInt1, paramInt2, paramInt3, paramBoolean);
  }
  
  private void b(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (!paramBoolean) {
      y.a(paramGraphics, this.a, paramInt1 % 10 * 30, paramInt1 / 10 * 30, 30, 30, paramInt2, paramInt3, 0);
    } else {
      paramGraphics.drawRegion(this.a, paramInt1 % 10 * 30, paramInt1 / 10 * 30, 30, 30, 2, paramInt2, paramInt3, 0);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     u
 * JD-Core Version:    0.7.0.1
 */