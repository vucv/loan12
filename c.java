public class c
{
  public int a;
  public int b;
  public int c;
  public int d;
  public int e;
  public int[] f;
  public int g;
  public int h;
  
  public void a()
  {
    b();
    this.e += 1;
    this.d = (this.h + dd.v(this.e));
    int i = dd.w(this.e) + this.g - this.b;
    this.b = (this.g + dd.w(this.e));
    this.c += i;
  }
  
  public void b()
  {
    this.d = (this.h + dd.v(this.e));
    this.b = (this.g + dd.w(this.e));
  }
  
  public c c()
  {
    c localc = new c();
    localc.a = this.a;
    localc.b = this.b;
    localc.c = this.c;
    localc.d = this.d;
    localc.e = this.e;
    localc.h = this.h;
    localc.g = this.g;
    return localc;
  }
  
  public int a(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt;
    System.arraycopy(ai.a(this.a), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.b), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.c), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.d), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.e), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.g), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.h), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.f.length), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    for (int j = 0; j < this.f.length; j++)
    {
      System.arraycopy(ai.a(this.f[j]), 0, paramArrayOfByte, paramInt, 4);
      paramInt += 4;
    }
    return paramInt - i;
  }
  
  public int b(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt;
    this.a = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.b = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.c = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.d = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.e = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.g = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.h = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.f = new int[ai.a(paramArrayOfByte, paramInt)];
    paramInt += 4;
    for (int j = 0; j < this.f.length; j++)
    {
      this.f[j] = ai.a(paramArrayOfByte, paramInt);
      paramInt += 4;
    }
    return paramInt - i;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     c
 * JD-Core Version:    0.7.0.1
 */