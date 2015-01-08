import com.mg.smsgame.main.a;
import com.mg.smsgame.main.b;
import java.util.Vector;
import javax.wireless.messaging.MessageConnection;

public class an
  implements ba, b
{
  public static String a = "TESTGAME";
  public static String b = "3";
  public static String c = "1";
  public static String d = "19003";
  public static int e = 0;
  public static int f = 6;
  public static int g = 5;
  public static int h = 5;
  public static int i = 5;
  public static int j = 7;
  public static String k = a + b;
  public static String l = null;
  public static String m = "3ef9f437ab2ed01315d84fc1d7c6718a";
  private static an n;
  private static a o;
  
  public static String a()
  {
    return (cx.F ? 1 : 0) + e + f + g + h + i + j;
  }
  
  public static void a(String paramString, int paramInt, a parama)
  {
    String str1 = null;
    String str2 = System.getProperty("microedition.platform");
    if (str2 == null) {
      str2 = "Unknown";
    } else if (str2.length() >= 80) {
      str2 = str2.substring(0, 80);
    }
    String str3 = " \"m" + str2 + "\"" + " e" + String.valueOf(System.currentTimeMillis());
    str1 = k + " v" + String.valueOf(2) + " a2" + " o" + String.valueOf(paramInt) + " d" + b + " p" + l;
    if (paramString != null) {
      str1 = str1 + " r" + paramString;
    }
    str1 = str1 + str3;
    dm.a(str1, g, new o(parama));
  }
  
  public static void b(String paramString, int paramInt, a parama)
  {
    try
    {
      String str1 = null;
      String str2 = System.getProperty("microedition.platform");
      if (str2 == null) {
        str2 = "Unknown";
      } else if (str2.length() >= 80) {
        str2 = str2.substring(0, 80);
      }
      String str3 = " \"m" + str2 + "\"" + " e" + String.valueOf(System.currentTimeMillis());
      str1 = k + " v" + String.valueOf(2) + " a2" + " o" + String.valueOf(paramInt) + " d" + b + " p" + l;
      if (paramString != null) {
        str1 = str1 + " r" + paramString;
      }
      str1 = str1 + str3;
      dm.a(str1, h, new n(parama));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public static void c(String paramString, int paramInt, a parama)
  {
    String str1 = null;
    String str2 = System.getProperty("microedition.platform");
    if (str2 == null) {
      str2 = "Unknown";
    } else if (str2.length() >= 80) {
      str2 = str2.substring(0, 80);
    }
    String str3 = " \"m" + str2 + "\"" + " e" + String.valueOf(System.currentTimeMillis());
    str1 = k + " v" + String.valueOf(2) + " a4" + " o" + String.valueOf(paramInt) + " d" + b + " p" + l;
    if (paramString != null) {
      str1 = str1 + " r" + paramString;
    }
    str1 = str1 + str3;
    dm.a(str1, i, new p(parama));
  }
  
  public static void a(String paramString, a parama)
  {
    String str1 = null;
    String str2 = System.getProperty("microedition.platform");
    if (str2 == null) {
      str2 = "Unknown";
    } else if (str2.length() >= 80) {
      str2 = str2.substring(0, 80);
    }
    String str3 = " \"m" + str2 + "\"" + " e" + String.valueOf(System.currentTimeMillis());
    str1 = k + " v" + String.valueOf(2) + " a5" + " c" + paramString + " p" + l + str3;
    dm.a(str1, e, new r(parama));
  }
  
  public static void a(String paramString)
  {
    dm.a("CSE " + c + " " + paramString, j, new q());
  }
  
  public static an b()
  {
    if (n == null) {
      n = new an();
    }
    return n;
  }
  
  private static String[] b(String paramString)
  {
    return a(paramString, " ", "\"", "\"", -1);
  }
  
  public static String[] a(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
  {
    int i1 = 0;
    if (paramInt == 0) {
      return null;
    }
    String[] arrayOfString = (String[])null;
    int i2 = 0;
    Vector localVector = new Vector();
    for (int i3 = 0; i3 < paramString1.length() - paramString2.length() + 1; i3++) {
      if ((paramString2.equals(paramString1.substring(i3, i3 + paramString2.length()))) && (i1 == 0))
      {
        localVector.addElement(paramString1.substring(i2, i3));
        i2 = i3 + paramString2.length();
        if ((paramInt > 0) && (localVector.size() >= paramInt - 1))
        {
          localVector.addElement(paramString1.substring(i2));
          break;
        }
        i3 = i2 - 1;
      }
      else if ((paramString3 != null) && (paramString4 != null))
      {
        if ((paramString3.equals(paramString1.substring(i3, i3 + paramString3.length()))) && (i1 == 0))
        {
          localVector.addElement(paramString1.substring(i2, i3));
          i2 = i3 + paramString3.length();
          if ((paramInt > 0) && (localVector.size() >= paramInt - 1))
          {
            localVector.addElement(paramString1.substring(i2));
            break;
          }
          i3 = i2 - 1;
          i1 = 1;
        }
        else if ((paramString4.equals(paramString1.substring(i3, i3 + paramString4.length()))) && (i1 != 0))
        {
          localVector.addElement(paramString1.substring(i2, i3));
          i2 = i3 + paramString4.length();
          if ((paramInt > 0) && (localVector.size() >= paramInt - 1))
          {
            localVector.addElement(paramString1.substring(i2));
            break;
          }
          i3 = i2 - 1;
          i1 = 0;
        }
      }
    }
    if (i2 < paramString1.length()) {
      if (paramInt < 0) {
        localVector.addElement(paramString1.substring(i2));
      } else if (localVector.size() < paramInt) {
        localVector.addElement(paramString1.substring(i2));
      }
    }
    if (localVector.size() > 0)
    {
      arrayOfString = new String[localVector.size()];
      for (i3 = 0; i3 < localVector.size(); i3++) {
        arrayOfString[i3] = ((String)localVector.elementAt(i3));
      }
      return arrayOfString;
    }
    return null;
  }
  
  public void a(String paramString1, String paramString2)
  {
    String[] arrayOfString = b(paramString1);
    if (!arrayOfString[0].toUpperCase().equals(k.toUpperCase())) {
      return;
    }
    try
    {
      int i1 = 0;
      localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      int i2 = 0;
      for (int i3 = 1; i3 < arrayOfString.length; i3++) {
        if (arrayOfString[i3].length() != 0)
        {
          int i4 = arrayOfString[i3].charAt(0);
          localObject6 = null;
          if (arrayOfString[i3].length() > 1) {
            localObject6 = arrayOfString[i3].substring(1);
          }
          switch (i4)
          {
          case 97: 
            i1 = Integer.parseInt((String)localObject6);
            break;
          case 108: 
            localObject1 = localObject6;
            break;
          case 99: 
            localObject2 = localObject6;
            break;
          case 113: 
            localObject3 = localObject6;
            break;
          case 114: 
            localObject4 = localObject6;
            break;
          case 105: 
            try
            {
              i2 = Integer.parseInt((String)localObject6);
            }
            catch (Exception localException2)
            {
              i2 = 0;
            }
          }
        }
      }
      al.d();
      if (i1 == 2)
      {
        cz.d().e.a(i2, localObject4, localObject3);
        return;
      }
      i3 = i1;
      Object localObject5 = localObject1;
      Object localObject6 = localObject2;
      Object localObject7 = localObject4;
      if (!cx.B)
      {
        cz.d().e.a(i3, localObject5, (String)localObject6, localObject7);
        cx.B = false;
      }
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
      cz.d().a(false);
      Object localObject1 = cz.a("Có lỗi", "Tin sai cú pháp", "Có", 3, 1);
      ((ar)localObject1).a(b());
      cz.d().a((ae)localObject1);
    }
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1)
    {
      if (o != null) {
        o.a();
      }
      cz.d().a(true);
    }
    else if (paramInt2 == 2)
    {
      cz.d().a(true);
    }
    else if (paramInt2 == 3)
    {
      cz.d().a(true);
    }
  }
  
  public static void a(MessageConnection paramMessageConnection)
  {
    dm.a(paramMessageConnection);
  }
  
  static void a(a parama)
  {
    o = parama;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     an
 * JD-Core Version:    0.7.0.1
 */