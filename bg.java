import java.io.InputStream;

public class bg
{
  public static int a = 10;
  public static dr b = dr.a("16b", 16);
  public static dr c = dr.a("161b", 161);
  
  public static int a(byte[] paramArrayOfByte, InputStream paramInputStream, int paramInt)
  {
    int i = paramInt;
    while (i < paramArrayOfByte.length)
    {
      int j = paramInputStream.read(paramArrayOfByte, i, paramArrayOfByte.length - i);
      if (j < 0) {
        return -1;
      }
      i += j;
    }
    return i;
  }
  
  public static final boolean g()
  {
    return dr.a(b, 14);
  }
  
  public static final byte[] h()
  {
    return dr.b(b, 14);
  }
  
  public static void b(byte[] paramArrayOfByte)
  {
    dr.a(b, 14, paramArrayOfByte, 0, paramArrayOfByte.length);
    dr.a(b);
  }
  
  public static void i()
  {
    if (!cx.C) {
      return;
    }
    cx.G = 0;
    byte[] arrayOfByte = dr.b(b, 8);
    if ((arrayOfByte != null) && (arrayOfByte.length >= 4)) {
      cx.G = ai.a(arrayOfByte);
    }
    cx.G += 1;
    arrayOfByte = ai.a(cx.G);
    dr.a(b, 8, arrayOfByte, 0, arrayOfByte.length);
    dr.a(b);
  }
  
  private static final int b()
  {
    int i = 1;
    byte[] arrayOfByte = dr.b(b, 7);
    if (arrayOfByte != null) {
      i = ai.a(arrayOfByte);
    } else {
      i = cx.J;
    }
    if (i < 1) {
      i = 1;
    }
    if (i > cx.J) {
      i = cx.J;
    }
    dr.a(b, 7, ai.a(i > 1 ? i - 1 : 1), 0, 4);
    dr.a(b);
    return i;
  }
  
  public static boolean j()
  {
    return !dr.a(b, 4);
  }
  
  public static void k()
  {
    if (j()) {
      dr.a(b, 4, new byte[] { 1 }, 0, 1);
    }
    dr.a(b, 5, ai.a(b()), 0, 4);
    dr.a(b);
  }
  
  public static boolean l()
  {
    byte[] arrayOfByte = dr.b(b, 5);
    if (arrayOfByte == null) {
      return false;
    }
    int i = ai.a(arrayOfByte);
    return i >= 1;
  }
  
  public static int m()
  {
    byte[] arrayOfByte = dr.b(b, 5);
    if (arrayOfByte == null) {
      return 0;
    }
    int i = ai.a(arrayOfByte);
    if (i < 1) {
      return 0;
    }
    return i;
  }
  
  public static void n()
  {
    byte[] arrayOfByte = dr.b(b, 5);
    int i = 0;
    if (arrayOfByte != null) {
      i = ai.a(arrayOfByte);
    }
    i--;
    if (i < 0) {
      i = 0;
    }
    dr.a(b, 5, ai.a(i), 0, 4);
    dr.a(b);
  }
  
  public static void c(int paramInt)
  {
    byte[] arrayOfByte = dr.b(b, 5);
    int i = 0;
    if (arrayOfByte != null) {
      i = ai.a(arrayOfByte);
    }
    i += paramInt;
    dr.a(b, 5, ai.a(i), 0, 4);
    dr.a(b);
  }
  
  public static byte[] o()
  {
    return dr.b(b, 11);
  }
  
  public static void d(int paramInt)
  {
    dr.a(b, 11, ai.a(paramInt), 0, 4);
    dr.a(b);
  }
  
  public static void p()
  {
    if (!cx.C) {
      return;
    }
    cx.A = true;
    byte[] arrayOfByte = dr.b(b, 13);
    if (arrayOfByte != null)
    {
      if (arrayOfByte[0] <= 0) {
        cx.A = false;
      }
    }
    else {
      a(false);
    }
  }
  
  public static void a(boolean paramBoolean)
  {
    if (!cx.C) {
      return;
    }
    int i = 3;
    byte[] arrayOfByte = dr.b(b, 13);
    if (arrayOfByte != null) {
      i = arrayOfByte[0];
    }
    if (paramBoolean) {
      i = (byte)(i - 1);
    } else {
      i = 3;
    }
    if (i <= 0) {
      cx.A = false;
    } else {
      cx.A = true;
    }
    dr.a(b, 13, new byte[] { i }, 0, 1);
    dr.a(b);
  }
  
  public static byte[] q()
  {
    return dr.b(b, 12);
  }
  
  public static void r()
  {
    dr.a(b, 12, ai.a(1), 0, 4);
    dr.a(b);
  }
  
  private static byte[] c()
  {
    byte[] arrayOfByte1 = dr.b(c, 1);
    if (arrayOfByte1 == null)
    {
      arrayOfByte1 = new byte[(a + 1) * 4];
      byte[] arrayOfByte2 = ai.a(ay.a(4) + 1);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, arrayOfByte1.length - 4, 4);
      dr.a(c, 1, arrayOfByte1, 0, arrayOfByte1.length);
      dr.a(c);
    }
    return arrayOfByte1;
  }
  
  public static int[] s()
  {
    int[] arrayOfInt = new int[a];
    byte[] arrayOfByte = c();
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = ai.a(arrayOfByte, i * 4);
    }
    return arrayOfInt;
  }
  
  public static boolean a(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return false;
    }
    boolean[] arrayOfBoolean = new boolean[4];
    for (int i = paramArrayOfInt.length - 1; i > -1; i--) {
      if (paramArrayOfInt[i] > 0) {
        arrayOfBoolean[(paramArrayOfInt[i] - 1)] = true;
      }
    }
    i = 1;
    for (int j = 0; j < arrayOfBoolean.length; j++) {
      i &= arrayOfBoolean[j];
    }
    return i;
  }
  
  private static void b(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return;
    }
    byte[] arrayOfByte = c();
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      System.arraycopy(ai.a(paramArrayOfInt[i]), 0, arrayOfByte, i * 4, 4);
    }
    dr.a(c, 1, arrayOfByte, 0, arrayOfByte.length);
    dr.a(c);
  }
  
  public static void a(int paramInt1, int paramInt2, String paramString1, String paramString2)
  {
    if (Math.abs(paramInt1) >= a) {
      return;
    }
    byte[] arrayOfByte1 = dr.b(c, 1);
    if (arrayOfByte1 == null) {
      return;
    }
    int[] arrayOfInt1 = new int[a + 1];
    int[] arrayOfInt2 = new int[4];
    for (int i = 0; i < arrayOfInt1.length; i++)
    {
      arrayOfInt1[i] = ai.a(arrayOfByte1, i * 4);
      if ((i < arrayOfInt1.length - 1) && (arrayOfInt1[i] > 0)) {
        arrayOfInt2[(arrayOfInt1[i] - 1)] += 1;
      }
    }
    i = -1;
    if (paramInt2 != 1)
    {
      int j = Math.abs(arrayOfInt1[(arrayOfInt1.length - 1)]) - 1;
      int k = arrayOfInt2[0] + arrayOfInt2[1] + arrayOfInt2[2] + arrayOfInt2[3];
      if (k >= 10) {
        return;
      }
      if (k >= 7)
      {
        for (int m = 0; m < arrayOfInt2.length; m++) {
          if ((m != j) && (arrayOfInt2[m] <= 0))
          {
            i = m;
            break;
          }
        }
        if (i == -1) {
          do
          {
            i = ay.a(4);
          } while (i == j);
        }
      }
      else
      {
        do
        {
          i = ay.a(4);
        } while (i == j);
      }
      i++;
    }
    else
    {
      if (paramString1 == null) {
        return;
      }
      i = Math.abs(arrayOfInt1[(arrayOfInt1.length - 1)]);
      if (paramString1 != null)
      {
        arrayOfByte2 = paramString1.getBytes();
        dr.a(c, 2, arrayOfByte2, 0, arrayOfByte2.length);
      }
    }
    d.a("index=" + paramInt1 + ",luckyNum=" + paramInt2 + ",codeWin=" + paramString1 + "recordChoose=" + i);
    byte[] arrayOfByte2 = ai.a(i);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramInt1 * 4, 4);
    dr.a(c, 1, arrayOfByte1, 0, arrayOfByte1.length);
    if (paramString2 != null) {
      if ((paramInt2 == 1) && (paramString1 != null))
      {
        arrayOfByte1 = paramString2.trim().getBytes();
        dr.a(c, 3, arrayOfByte1, 0, arrayOfByte1.length);
        arrayOfByte1 = paramString1.trim().getBytes();
        dr.a(c, 2, arrayOfByte1, 0, arrayOfByte1.length);
      }
      else
      {
        arrayOfByte1 = dr.b(c, 2);
        if (arrayOfByte1 == null)
        {
          arrayOfByte1 = paramString2.trim().getBytes();
          dr.a(c, 3, arrayOfByte1, 0, arrayOfByte1.length);
        }
      }
    }
    dr.a(c);
  }
  
  private static boolean c(int[] paramArrayOfInt)
  {
    boolean[] arrayOfBoolean = new boolean[5];
    boolean bool = false;
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; j++) {
      if (paramArrayOfInt[j] > 0) {
        if (arrayOfBoolean[paramArrayOfInt[j]] != 0)
        {
          bool = true;
          paramArrayOfInt[j] = 0;
        }
        else
        {
          arrayOfBoolean[paramArrayOfInt[j]] = true;
        }
      }
    }
    return bool;
  }
  
  public static int t()
  {
    int[] arrayOfInt = s();
    int i = arrayOfInt.length;
    for (int j = 0; j < i; j++) {
      if (arrayOfInt[j] == 0)
      {
        arrayOfInt[j] = -1;
        b(arrayOfInt);
        return j;
      }
    }
    for (j = arrayOfInt.length - 1; j >= 0; j--) {
      if (arrayOfInt[j] == -1) {
        return j;
      }
    }
    if (c(arrayOfInt))
    {
      b(arrayOfInt);
      return t();
    }
    return -1;
  }
  
  public static void e(int paramInt)
  {
    int i = cx.G;
    an.c("reco" + paramInt, i, null);
  }
  
  public static byte[] u()
  {
    return dr.b(c, 2);
  }
  
  public static void a(int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte1 = (byte[])null;
    if (paramInt1 < paramInt3)
    {
      arrayOfByte1 = dr.b(c, 9);
      if (arrayOfByte1 != null) {
        paramInt2 += ai.a(arrayOfByte1, paramInt1 * 4);
      } else {
        arrayOfByte1 = new byte[paramInt3 * 4];
      }
    }
    paramInt2 = paramInt2 < 0 ? 0 : paramInt2;
    byte[] arrayOfByte2 = ai.a(paramInt2);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramInt1 * 4, 4);
    dr.a(c, 9, arrayOfByte1, 0, arrayOfByte1.length);
    dr.a(c);
  }
  
  public static int[] v()
  {
    byte[] arrayOfByte = (byte[])null;
    arrayOfByte = dr.b(c, 9);
    if (arrayOfByte == null) {
      return null;
    }
    int[] arrayOfInt = new int[arrayOfByte.length / 4];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = ai.a(arrayOfByte, i * 4);
    }
    return arrayOfInt;
  }
  
  public static int f(int paramInt)
  {
    byte[] arrayOfByte = (byte[])null;
    arrayOfByte = dr.b(c, 9);
    if (arrayOfByte == null) {
      return 0;
    }
    return ai.a(arrayOfByte, paramInt * 4);
  }
  
  public static void b(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte1 = (byte[])null;
    arrayOfByte1 = dr.b(c, 9);
    if (arrayOfByte1 == null) {
      return;
    }
    int i = ai.a(arrayOfByte1, paramInt1 * 4);
    i -= paramInt2;
    if (i < 0) {
      i = 0;
    }
    byte[] arrayOfByte2 = ai.a(i);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, paramInt1 * 4, 4);
    dr.a(c, 9, arrayOfByte1, 0, arrayOfByte1.length);
    dr.a(c);
  }
  
  public static void g(int paramInt)
  {
    b(paramInt, 1);
  }
  
  public static String w()
  {
    byte[] arrayOfByte = dr.b(c, 3);
    if (arrayOfByte == null) {
      return null;
    }
    return new String(arrayOfByte);
  }
  
  public static void x()
  {
    try
    {
      dr.c(c, 1);
      dr.c(c, 2);
      dr.c(c, 3);
      dr.a(c);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
  }
  
  public static void a()
  {
    try
    {
      dr.c(b, 10);
      dr.c(b, 6);
      dr.c(b, 5);
      dr.c(b, 16);
      dr.c(c, 9);
      dr.a(b);
      dr.a(c);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bg
 * JD-Core Version:    0.7.0.1
 */