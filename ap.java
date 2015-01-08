import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ap
  extends br
{
  protected Image a;
  protected int[] b;
  protected int[] c;
  protected int[] d;
  protected int[] e;
  protected int[] f;
  protected String g = " 0123456789.,:!?()+-*/#$%abcdefghijklmnopqrstuvwxyzáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđABCDĐEFGHIJKLMNOPQRSTUVWXYZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ\\\"@<=>;_&'`^~{}";
  protected String h = "";
  protected int i;
  
  public void a(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    int m = paramString.length();
    int n = a(paramString);
    int k;
    if (paramInt3 == 0) {
      k = paramInt1;
    } else if (paramInt3 == 2) {
      k = paramInt1 - n;
    } else {
      k = paramInt1 - (n >> 1);
    }
    for (int i1 = 0; i1 < m; i1++)
    {
      int j = this.g.indexOf(paramString.charAt(i1));
      if (j == -1) {
        j = this.h.indexOf(paramString.charAt(i1));
      }
      if (j == -1) {
        j = 0;
      }
      if (j > -1) {
        y.a(paramGraphics, this.a, this.b[j], this.c[j], this.d[j], this.e[j], k, paramInt2 - 1 + this.f[j], 20);
      }
      k += this.d[j] + 1;
    }
  }
  
  public int a()
  {
    return this.i;
  }
  
  public int a(String paramString)
  {
    int j = 0;
    for (int k = 0; k < paramString.length(); k++) {
      j = j + 1 + a(paramString.charAt(k));
    }
    return j;
  }
  
  public int a(char paramChar)
  {
    int j = this.g.indexOf(paramChar);
    if (j == -1) {
      j = this.h.indexOf(paramChar);
    }
    if (j > -1) {
      return this.d[j];
    }
    return this.d[0];
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ap
 * JD-Core Version:    0.7.0.1
 */