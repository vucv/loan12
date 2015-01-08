package com.mic.smsgame.suquan;

import ai;
import bg;
import dr;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class f
  extends bg
{
  public static final void a()
  {
    dr.c(bg.b, 100);
    dr.c(bg.b, 101);
    dr.c(bg.b, 103);
    dr.c(bg.b, 104);
    dr.c(bg.b, 105);
    f();
    bg.a();
  }
  
  public static final void a(int paramInt, String paramString)
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
      localDataOutputStream.writeUTF(paramString);
      localDataOutputStream.flush();
      dr.a(bg.b, paramInt, localByteArrayOutputStream.toByteArray());
      dr.a(bg.b);
      localDataOutputStream.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  public static final String a(int paramInt)
  {
    try
    {
      byte[] arrayOfByte = dr.b(bg.b, 104);
      if (arrayOfByte == null) {
        return "";
      }
      DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
      String str = localDataInputStream.readUTF();
      localDataInputStream.close();
      return str;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return null;
  }
  
  public static final void a(int paramInt1, int paramInt2)
  {
    dr.a(bg.b, paramInt1, ai.a(paramInt2));
    dr.a(bg.b);
  }
  
  public static final int b(int paramInt)
  {
    byte[] arrayOfByte = dr.b(bg.b, paramInt);
    if (arrayOfByte == null) {
      return 0;
    }
    return ai.a(arrayOfByte);
  }
  
  public static final void a(byte[] paramArrayOfByte)
  {
    dr.a(bg.b, 100, paramArrayOfByte);
    dr.a(bg.b);
  }
  
  public static final byte[] b()
  {
    byte[] arrayOfByte = dr.b(bg.b, 100);
    if (arrayOfByte == null) {
      return null;
    }
    return arrayOfByte;
  }
  
  public static final boolean c()
  {
    return dr.a(bg.b, 100);
  }
  
  public static final void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    dr.a(bg.c, 106, paramArrayOfByte, paramInt1, paramInt2);
    dr.a(bg.c);
  }
  
  public static final byte[] d()
  {
    byte[] arrayOfByte = dr.b(bg.c, 106);
    return arrayOfByte;
  }
  
  public static final boolean e()
  {
    return dr.a(bg.c, 106);
  }
  
  public static final void f()
  {
    dr.c(bg.c, 106);
    dr.a(bg.c);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.f
 * JD-Core Version:    0.7.0.1
 */