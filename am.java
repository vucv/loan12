import javax.microedition.lcdui.Image;

public class am
  extends ab
{
  public am(Image paramImage, int paramInt)
  {
    a(paramImage, paramInt);
    b(false);
    this.u = true;
    this.t = 3;
  }
  
  public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    d(paramInt1, paramInt2);
    c(paramInt3, paramInt4);
    this.u = true;
    this.k = b(paramInt1, paramInt3, 20);
    this.l = b(paramInt2, paramInt4, 20);
    this.g = 0;
  }
  
  public void a()
  {
    c(this.i, this.j, this.k, this.l);
    if (this.g >= this.d[0].length - 1) {
      this.u = false;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     am
 * JD-Core Version:    0.7.0.1
 */