import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class aa
{
  protected int a;
  protected int[] b;
  protected int[] c;
  protected Image d;
  
  protected aa(String paramString)
  {
    this.d = az.b(paramString);
  }
  
  public int a()
  {
    return this.d.getHeight();
  }
  
  public int a(String paramString)
  {
    int j = 0;
    for (int k = paramString.length() - 1; k >= 0; k--)
    {
      int i = " 0123456789.,:!?()+-*/#$%abcdefghijklmnopqrstuvwxyzáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđABCDEFGHIJKLMNOPQRSTUVWXYZÁÀẢÃẶĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ".indexOf(paramString.charAt(k));
      if (i == -1) {
        i = 0;
      }
      j += this.b[i] + this.a;
    }
    return j - this.a;
  }
  
  public void a(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    int j = 0;
    int k = paramString.length();
    switch (paramInt3)
    {
    case 1: 
      j = paramInt1 - a(paramString);
      break;
    case 2: 
      j = paramInt1 - (a(paramString) >> 1);
      break;
    default: 
      j = paramInt1;
    }
    for (int m = 0; m < k; m++)
    {
      int i = " 0123456789.,:!?()+-*/#$%abcdefghijklmnopqrstuvwxyzáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđABCDEFGHIJKLMNOPQRSTUVWXYZÁÀẢÃẶĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ".indexOf(paramString.charAt(m));
      if (i == -1) {
        i = 0;
      } else {
        paramGraphics.drawRegion(this.d, this.c[i], 0, this.b[i], a(), 0, j, paramInt2, 20);
      }
      j += this.b[i] + this.a;
    }
  }
  
  public int a(char paramChar)
  {
    int i = " 0123456789.,:!?()+-*/#$%abcdefghijklmnopqrstuvwxyzáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđABCDEFGHIJKLMNOPQRSTUVWXYZÁÀẢÃẶĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴĐ".indexOf(paramChar);
    if (i > -1) {
      return this.b[i];
    }
    return this.b[0];
  }
  
  public String[] a(String paramString, int paramInt)
  {
    int i = a(' ');
    di localdi = new di();
    String str1 = "";
    int j = 0;
    int k = 0;
    int i2;
    while (k < paramString.length())
    {
      for (;;)
      {
        if ((k >= paramString.length()) || (paramString.charAt(k) != ' '))
        {
          if ((k <= 0) || (localdi.b() != 0) || (k * i >= paramInt)) {
            break;
          }
          String str2 = "";
          for (n = 0; n < k; n++) {
            str2 = str2 + ' ';
          }
          localdi.a(str2);
          break;
        }
        k++;
      }
      for (;;)
      {
        if ((k >= paramString.length()) || (paramString.charAt(k) == ' '))
        {
          if ((str1 != null) && (str1.length() > 0))
          {
            str1 = str1 + " ";
            int m = a(str1);
            if (m < paramInt)
            {
              localdi.a(str1);
            }
            else
            {
              n = 0;
              String str4 = "";
              i2 = 0;
              while (n < str1.length()) {
                if (i2 + a(str1.charAt(n)) + i < paramInt)
                {
                  i2 += a(str1.charAt(n));
                  str4 = str4 + str1.charAt(n);
                  n++;
                  if (n >= str1.length())
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
                  i2 = 0;
                  str4 = "";
                }
              }
            }
          }
          str1 = "";
          break;
        }
        str1 = str1 + paramString.charAt(k);
        k++;
      }
    }
    String[] arrayOfString1 = new String[localdi.b()];
    for (int n = 0; n < arrayOfString1.length; n++) {
      arrayOfString1[n] = ((String)localdi.b(n));
    }
    localdi = new di();
    String str3 = "";
    int i1 = 0;
    k = 0;
    for (;;)
    {
      if (k >= arrayOfString1.length)
      {
        if ((str3 == null) || (str3.length() <= 0)) {
          break;
        }
        localdi.a(str3);
        break;
      }
      i2 = a(arrayOfString1[k]);
      if (i1 + i2 - i < paramInt)
      {
        str3 = str3 + arrayOfString1[k];
        i1 = a(str3);
        if (j != 0)
        {
          localdi.a(str3);
          str3 = "";
          i1 = 0;
        }
        k++;
      }
      else
      {
        if (i2 > paramInt)
        {
          localdi.a(arrayOfString1[k]);
          k++;
        }
        else
        {
          localdi.a(str3);
        }
        str3 = "";
        i1 = 0;
      }
    }
    String[] arrayOfString2 = new String[localdi.b()];
    for (int i3 = 0; i3 < arrayOfString2.length; i3++) {
      arrayOfString2[i3] = ((String)localdi.b(i3));
    }
    return arrayOfString2;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     aa
 * JD-Core Version:    0.7.0.1
 */