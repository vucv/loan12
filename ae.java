public abstract class ae
  extends ar
{
  public int a = 0;
  protected boolean b = true;
  protected boolean c = true;
  protected boolean d = true;
  private int e;
  
  public void a(boolean paramBoolean)
  {
    this.b = paramBoolean;
  }
  
  public boolean b()
  {
    return this.b;
  }
  
  public boolean c()
  {
    return this.c;
  }
  
  public void b(boolean paramBoolean)
  {
    this.c = paramBoolean;
  }
  
  public boolean d()
  {
    return this.d;
  }
  
  public ae(int paramInt)
  {
    this.a = paramInt;
    if (this.a == 1) {
      this.e = -1;
    } else if (this.a == 0) {
      this.e = 2000;
    }
  }
  
  public final void a()
  {
    if (this.e > 0)
    {
      this.e -= 1;
      if (this.e == 1)
      {
        cz.d().g();
        return;
      }
    }
    e();
  }
  
  protected void e() {}
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ae
 * JD-Core Version:    0.7.0.1
 */