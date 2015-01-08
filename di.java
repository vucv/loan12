public class di
{
  Object[] a;
  private int b = 0;
  
  public di() {}
  
  public di(int paramInt)
  {
    this();
    this.a = new Object[paramInt];
  }
  
  public void a(Object paramObject)
  {
    if (this.a == null) {
      this.a = new Object[10];
    }
    if (this.b >= this.a.length)
    {
      Object[] arrayOfObject = new Object[this.a.length + 10];
      System.arraycopy(this.a, 0, arrayOfObject, 0, this.a.length);
      this.a = arrayOfObject;
      arrayOfObject = (Object[])null;
    }
    this.a[this.b] = paramObject;
    this.b += 1;
  }
  
  public Object a(int paramInt)
  {
    if (this.a == null) {
      return null;
    }
    if ((paramInt < 0) || (paramInt > b())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    Object localObject = this.a[paramInt];
    this.a[paramInt] = null;
    this.b -= 1;
    if (paramInt < this.a.length - 1) {
      System.arraycopy(this.a, paramInt + 1, this.a, paramInt, this.b - paramInt);
    }
    return localObject;
  }
  
  public void a()
  {
    this.a = null;
    this.b = 0;
  }
  
  public void b(Object paramObject)
  {
    if (this.a == null) {
      return;
    }
    for (int i = 0; i < this.b; i++) {
      if (this.a[i].equals(paramObject))
      {
        a(i);
        break;
      }
    }
  }
  
  public Object b(int paramInt)
  {
    if (this.a == null) {
      return null;
    }
    if ((paramInt >= this.b) || (paramInt < 0)) {
      return null;
    }
    return this.a[paramInt];
  }
  
  public int b()
  {
    return this.b;
  }
  
  public String toString()
  {
    String str = "";
    if (b() == 0) {
      str = "[Size]: 0";
    } else {
      for (int i = 0; i < this.b; i++) {
        if (this.a[i] != null) {
          str = str + "\n[Index]: " + i + " [Object]: " + this.a[i].toString();
        } else {
          str = str + "\n[Index]: " + i + " [Object]: NULL";
        }
      }
    }
    return str;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     di
 * JD-Core Version:    0.7.0.1
 */