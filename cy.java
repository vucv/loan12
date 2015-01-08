import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class cy
  extends bw
{
  public void a()
  {
    this.f = (this.c / 14 + 1);
    this.h = new int[this.f];
  }
  
  public void a(Graphics paramGraphics, Image paramImage)
  {
    int i = this.c;
    for (int j = 0; j < this.f; j++)
    {
      if (this.h[j] <= 0) {
        break;
      }
      int k = this.h[j];
      int m = i - k;
      if (j == this.f - 1)
      {
        if (m - k <= 0) {
          continue;
        }
        if (m < 0)
        {
          k = m + k;
          m = 0;
        }
      }
      y.a(paramGraphics, paramImage, m, 0, k, this.d, this.a + m, this.b, 20);
      i -= 14;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cy
 * JD-Core Version:    0.7.0.1
 */