public abstract class bw
  extends bb
{
  protected int f;
  protected int g;
  protected int[] h;
  
  public bw()
  {
    a();
  }
  
  public void c()
  {
    this.g = 0;
    for (int i = this.h.length - 1; i > -1; i--) {
      this.h[i] = 0;
    }
    this.e = false;
  }
  
  public void b()
  {
    if (this.e) {
      return;
    }
    int i = this.g <= this.f ? this.g - 1 : this.f - 1;
    for (int j = i; j > -1; j--) {
      if (this.h[j] < 14) {
        this.h[j] += 1;
      }
    }
    if (this.g < this.f)
    {
      this.h[this.g] = 1;
      this.g += 1;
    }
    else if (this.h[(this.f - 1)] >= 14)
    {
      this.e = true;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bw
 * JD-Core Version:    0.7.0.1
 */