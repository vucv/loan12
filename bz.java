import javax.microedition.lcdui.Graphics;

public class bz
{
  private static bz e = null;
  public static br a;
  public static as b;
  public static dh c;
  public static dh d;
  
  private bz()
  {
    b = new as();
    c = new dh("/_font1");
    d = new dh("/_font2");
    a(c);
  }
  
  public static void a()
  {
    e = new bz();
  }
  
  public static void a(br parambr)
  {
    a = parambr;
  }
  
  public static String[] a(String paramString, int paramInt)
  {
    di localdi = new di();
    int i = 0;
    String str1 = "";
    int j = 0;
    int i1;
    while (i < paramString.length())
    {
      for (;;)
      {
        if ((i >= paramString.length()) || (paramString.charAt(i) != ' '))
        {
          if ((i <= 0) || (localdi.b() != 0) || (i * a.a(' ') >= paramInt)) {
            break;
          }
          String str2 = "";
          for (m = 0; m < i; m++) {
            str2 = str2 + ' ';
          }
          localdi.a(str2);
          break;
        }
        i++;
      }
      for (;;)
      {
        if ((i >= paramString.length()) || (paramString.charAt(i) == ' '))
        {
          if ((str1 != null) && (str1.length() > 0))
          {
            str1 = str1 + " ";
            int k = a.a(str1);
            if (k < paramInt)
            {
              localdi.a(str1);
            }
            else
            {
              m = 0;
              String str4 = "";
              i1 = 0;
              while (m < str1.length()) {
                if (i1 + a.a(str1.charAt(m)) + a.a(' ') < paramInt)
                {
                  i1 += a.a(str1.charAt(m));
                  str4 = str4 + str1.charAt(m);
                  m++;
                  if (m >= str1.length())
                  {
                    if ((str4 == null) || (str4.length() <= 0)) {
                      break;
                    }
                    localdi.a(str4);
                    break;
                  }
                }
                else
                {
                  localdi.a(str4);
                  i1 = 0;
                  str4 = "";
                }
              }
            }
          }
          str1 = "";
          break;
        }
        str1 = str1 + paramString.charAt(i);
        i++;
      }
    }
    String[] arrayOfString1 = new String[localdi.b()];
    for (int m = 0; m < arrayOfString1.length; m++) {
      arrayOfString1[m] = ((String)localdi.b(m));
    }
    localdi = new di();
    String str3 = "";
    int n = 0;
    i = 0;
    for (;;)
    {
      if (i >= arrayOfString1.length)
      {
        if ((str3 == null) || (str3.length() <= 0)) {
          break;
        }
        localdi.a(str3);
        break;
      }
      i1 = a.a(arrayOfString1[i]);
      if (n + i1 - a.a(' ') < paramInt)
      {
        n += i1;
        str3 = str3 + arrayOfString1[i];
        if (j != 0)
        {
          localdi.a(str3);
          str3 = "";
          n = 0;
        }
        i++;
      }
      else
      {
        if (i1 > paramInt)
        {
          localdi.a(arrayOfString1[i]);
          i++;
        }
        else
        {
          localdi.a(str3);
        }
        str3 = "";
        n = 0;
      }
    }
    String[] arrayOfString2 = new String[localdi.b()];
    for (int i2 = 0; i2 < arrayOfString2.length; i2++) {
      arrayOfString2[i2] = ((String)localdi.b(i2));
    }
    return arrayOfString2;
  }
  
  public static void a(Graphics paramGraphics, br parambr, String[] paramArrayOfString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    int i = paramInt3;
    if (paramInt7 == 1) {
      i += paramInt5 / 2;
    } else if (paramInt7 == 2) {
      i += paramInt5;
    }
    int j = paramInt1 + paramInt2;
    int k = 0;
    for (int m = paramInt1; m < j; m++)
    {
      if (k + parambr.a() > paramInt6) {
        break;
      }
      parambr.a(paramGraphics, paramArrayOfString[m], i, paramInt4 + k, paramInt7);
      k += parambr.a();
    }
  }
  
  public static void a(Graphics paramGraphics, br parambr, String[] paramArrayOfString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    a(paramGraphics, parambr, paramArrayOfString, 0, paramArrayOfString.length, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bz
 * JD-Core Version:    0.7.0.1
 */