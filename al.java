import java.io.UnsupportedEncodingException;

public class al
{
  private static int a = 1;
  private static int b;
  private static boolean c;
  private static boolean d = false;
  
  public static final boolean a()
  {
    f();
    return c;
  }
  
  private static final void f()
  {
    c = false;
    if (a == 1) {
      return;
    }
    d.a("[CHECK] Load data");
    if (!bg.g())
    {
      if (cx.G > 5)
      {
        d.a("[CHECK] RMS has been deleted by user");
        c = true;
        return;
      }
      a = 1;
      b = 0;
      g();
    }
    else
    {
      String str1 = ao.a(bg.h());
      d.a("[CHECK] String: " + str1);
      try
      {
        byte[] arrayOfByte = b.a(str1.substring(0, 2));
        String str2 = b.b(arrayOfByte);
        if (str2.equals(str1.substring(str1.length() - 3, str1.length())))
        {
          d.a("[CHECK] Wrong checksum!!!");
          c = true;
          return;
        }
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        a = str1.charAt(0) - '0';
        b = str1.charAt(1) - '0';
        d.a("[CHECK] Mode = " + a + "; sendSMSCount = " + b);
        if ((b >= 6) && (a == 1))
        {
          d.a("[CHECK] Playing on emulator!!!");
          c = true;
        }
      }
    }
  }
  
  private static final void g()
  {
    String str1 = "";
    str1 = str1 + (a == 1 ? "0" : "1");
    str1 = str1 + String.valueOf(b);
    try
    {
      byte[] arrayOfByte1 = b.a(str1);
      String str2 = b.b(arrayOfByte1);
      str1 = str1 + str2.substring(str2.length() - 3, str2.length());
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException1) {}
    byte[] arrayOfByte2 = (byte[])null;
    try
    {
      arrayOfByte2 = ao.b(str1);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException2)
    {
      arrayOfByte2 = str1.getBytes();
    }
    bg.b(arrayOfByte2);
  }
  
  public static final void b()
  {
    a = 1;
    b = 7;
    g();
  }
  
  public static final void c()
  {
    if (a == 1) {
      return;
    }
    if (d)
    {
      d = false;
      return;
    }
    d.a("[CHECK] Update send SMS");
    b += 1;
    g();
  }
  
  public static final void d()
  {
    if (a == 1) {
      return;
    }
    d.a("[CHECK] Update receive SMS");
    a = 1;
    g();
  }
  
  public static final void e()
  {
    String str = "Thiết bị không phù hợp để chơi game! Xin cảm ơn.";
    ae localae = cz.a("Chú ý", str, "Đóng", 4, 1);
    localae.a(cz.d());
    cz.d().a(localae);
  }
  
  public static final boolean a(boolean paramBoolean)
  {
    try
    {
      byte[] arrayOfByte = b.a(an.a());
      String str = b.b(arrayOfByte);
      return (str.equals(an.m)) && (paramBoolean);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    return true;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     al
 * JD-Core Version:    0.7.0.1
 */