public class ac
  extends ch
{
  public ac(bu parambu, int paramInt)
  {
    super(parambu, paramInt);
  }
  
  public void a(int paramInt)
  {
    switch (paramInt)
    {
    case 12: 
      this.b.m();
      break;
    case 13: 
      this.b.n();
      break;
    case 14: 
      this.b.o();
      break;
    case 15: 
      this.b.p();
      break;
    case 16: 
      this.b.i();
      break;
    case 17: 
      this.b.r();
      break;
    case 18: 
      this.b.g();
      break;
    case 11: 
      this.b.s();
    }
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    int j;
    if (paramInt2 >= cx.g - 15)
    {
      int i = cx.f / 3;
      j = i + i;
      if (paramInt1 > j) {
        this.b.g();
      } else if (paramInt1 > i) {
        this.b.s();
      } else {
        this.b.r();
      }
      return;
    }
    dl localdl = this.b.h();
    if (localdl.b(paramInt1, paramInt2))
    {
      j = (paramInt1 - localdl.a) / 21 + 2;
      int k = (paramInt2 - localdl.b) / 21 + 2;
      int m = this.b.k();
      int n = this.b.l();
      if ((k == m) && (j == n))
      {
        this.b.i();
      }
      else if (this.b.j())
      {
        int i1 = k - m;
        int i2 = j - n;
        if (i1 == 1) {
          this.b.n();
        } else if (i1 == -1) {
          this.b.m();
        } else if (i2 == 1) {
          this.b.p();
        } else if (i2 == -1) {
          this.b.o();
        }
      }
      else
      {
        this.b.c(k, j);
      }
    }
  }
  
  public void a() {}
  
  public void b()
  {
    this.b.c(1);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ac
 * JD-Core Version:    0.7.0.1
 */