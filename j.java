import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class j
  extends bw
{
  public void a()
  {
    this.f = (this.d / 14 + 1);
    this.h = new int[this.f];
  }
  
  public void a(Graphics paramGraphics, Image paramImage)
  {
    int i = this.d;
    for (int j = 0; j < this.f; j++)
    {
      if (this.h[j] <= 0) {
        break;
      }
      int k = this.h[j];
      int m = i - k;
      if (j == this.f - 1)
      {
        if (m + k <= 0) {
          continue;
        }
        if (m < 0)
        {
          k = m + k;
          m = 0;
        }
      }
      y.a(paramGraphics, paramImage, 0, m, this.c, k, this.a, this.b + m, 20);
      i -= 14;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     j
 * JD-Core Version:    0.7.0.1
 */