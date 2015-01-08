public abstract class aj
  extends ar
{
  protected boolean a = true;
  protected bk b;
  
  public boolean c()
  {
    return this.a;
  }
  
  public void a(bk parambk)
  {
    this.b = parambk;
  }
  
  public int c(int paramInt1, int paramInt2)
  {
    if (this.b != null) {
      return this.b.b(paramInt1, paramInt2);
    }
    return -1;
  }
  
  public int d()
  {
    if (this.b != null) {
      return this.b.c();
    }
    return -1;
  }
  
  public void e() {}
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     aj
 * JD-Core Version:    0.7.0.1
 */