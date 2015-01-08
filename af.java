import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class af
  extends bw
{
  public void a()
  {
    this.f = (this.d / 14 + 1);
    this.h = new int[this.f];
  }
  
  public void a(Graphics paramGraphics, Image paramImage)
  {
    int i = 0;
    for (int j = 0; j < this.f; j++)
    {
      if (this.h[j] <= 0) {
        break;
      }
      int k = this.h[j];
      int m = i;
      if (j == this.f - 1)
      {
        if (m >= this.d) {
          continue;
        }
        if (m + k > this.d) {
          k = this.d - m;
        }
      }
      y.a(paramGraphics, paramImage, 0, m, this.c, k, this.a, this.b + m, 20);
      i += 14;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     af
 * JD-Core Version:    0.7.0.1
 */