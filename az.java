import javax.microedition.lcdui.Image;
import java.io.InputStream;

public class az
{
  public static boolean a = false;
  private static byte[] b = { -119, 80, 78, 71 };
  
  private static Image c(String paramString)
  {
    paramString = paramString + cx.U;
    try
    {
      InputStream localInputStream = "".getClass().getResourceAsStream(paramString);
      byte[] arrayOfByte1 = new byte[b.length];
      localInputStream.read(arrayOfByte1, 0, arrayOfByte1.length);
      int i = ai.a(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[i];
      bg.a(arrayOfByte2, localInputStream, b.length);
      localInputStream.close();
      localInputStream = null;
      for (int j = 0; j < b.length; j++) {
        arrayOfByte2[j] = b[j];
      }
      return Image.createImage(arrayOfByte2, 0, i);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  private static Image d(String paramString)
  {
    paramString = paramString + ".png";
    try
    {
      return Image.createImage(paramString);
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public static Image a(String paramString)
  {
    return c(paramString);
  }
  
  public static Image b(String paramString)
  {
    if (a) {
      return d(paramString);
    }
    return c(paramString);
  }
}



/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar

 * Qualified Name:     az

 * JD-Core Version:    0.7.0.1

 */