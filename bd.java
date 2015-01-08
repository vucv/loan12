class bd
{
  int a;
  int b;
  int[] c;
  byte[] d;
  
  byte[] a()
  {
    int i = 12 + this.c.length * 4;
    byte[] arrayOfByte = new byte[i];
    int j = 0;
    System.arraycopy(ai.a(this.a), 0, arrayOfByte, j, 4);
    j += 4;
    System.arraycopy(ai.a(this.b), 0, arrayOfByte, j, 4);
    j += 4;
    System.arraycopy(ai.a(this.c.length), 0, arrayOfByte, j, 4);
    j += 4;
    for (int k = 0; k < this.c.length; k++)
    {
      System.arraycopy(ai.a(this.c[k]), 0, arrayOfByte, j, 4);
      j += 4;
    }
    return arrayOfByte;
  }
  
  int a(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt;
    this.a = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.b = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.c = new int[ai.a(paramArrayOfByte, paramInt)];
    paramInt += 4;
    for (int j = 0; j < this.c.length; j++)
    {
      this.c[j] = ai.a(paramArrayOfByte, paramInt);
      paramInt += 4;
    }
    return paramInt - i;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bd
 * JD-Core Version:    0.7.0.1
 */