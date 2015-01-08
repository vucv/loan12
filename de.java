import javax.microedition.lcdui.Graphics;

public class de
{
  protected int a;
  protected int b;
  protected int c;
  protected int d;
  protected int e;
  
  public int a()
  {
    return this.e;
  }
  
  public de(int paramInt)
  {
    this.e = paramInt;
  }
  
  public void a(int paramInt1, int paramInt2, int paramInt3)
  {
    this.b = paramInt1;
    switch (paramInt3)
    {
    case 24: 
      this.b = (paramInt1 - this.d);
      break;
    case 17: 
      this.b = (paramInt1 - this.d / 2);
    }
    this.c = paramInt2;
  }
  
  public final boolean a(int paramInt1, int paramInt2)
  {
    return (paramInt2 > this.c) && (paramInt1 >= this.b) && (paramInt1 < this.b + this.d);
  }
  
  public void b()
  {
    this.a = 3;
  }
  
  public void a(Graphics paramGraphics) {}
  
  public void c()
  {
    if (this.a > 0) {
      this.a -= 1;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     de
 * JD-Core Version:    0.7.0.1
 */