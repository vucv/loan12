public class dx
{
  public int a;
  public int b;
  
  public boolean a()
  {
    if (this.b > 0)
    {
      this.b -= 1;
      return true;
    }
    return false;
  }
  
  public int a(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt;
    System.arraycopy(ai.a(this.a), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    System.arraycopy(ai.a(this.b), 0, paramArrayOfByte, paramInt, 4);
    paramInt += 4;
    return paramInt - i;
  }
  
  public int b(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramInt;
    this.a = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    this.b = ai.a(paramArrayOfByte, paramInt);
    paramInt += 4;
    return paramInt - i;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     dx
 * JD-Core Version:    0.7.0.1
 */