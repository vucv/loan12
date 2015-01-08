class dw
{
  int[] a = new int[4];
  long b = 0L;
  byte[] c = new byte[64];
  
  public dw()
  {
    this.a[0] = 1732584193;
    this.a[1] = -271733879;
    this.a[2] = -1732584194;
    this.a[3] = 271733878;
  }
  
  public dw(dw paramdw)
  {
    this();
    for (int i = 0; i < this.c.length; i++) {
      this.c[i] = paramdw.c[i];
    }
    for (i = 0; i < this.a.length; i++) {
      this.a[i] = paramdw.a[i];
    }
    this.b = paramdw.b;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     dw
 * JD-Core Version:    0.7.0.1
 */