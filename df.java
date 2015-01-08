import com.mg.smsgame.main.MGMIDlet;
import com.mic.smsgame.suquan.f;
import com.mic.smsgame.suquan.h;
import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class df
  extends aj
  implements com.mg.smsgame.main.b
{
  private int c = -1;
  private static final int[][] d = new int [] []{{0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0},
          {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, { 60, 338 }, {0}, {0}, { 186, 229 }, {0}, {0}, { 303, 287 }, {0}, {0}, { 103, 76 }, {0}, {0}, { 148, 116 }, {0}, {0}, { 108, 183 }, {0}, {0}, { 265, 160 }, {0}, {0}, { 318, 107 }, 0, {0}, { 243, 205 }, {0}, {0}, { 178, 170 }, {0}, {0}, { 29, 88 }, {0}, {0}, { 313, 228 }, {00000170}, {295} };
  private Image e;
  private Image f;
  private Image t;
  private int u;
  private int v;
  private int w;
  private int x;
  private cl y;
  private dl z;
  private dl A;
  private dl B;
  private dl C;
  private int D;
  private com.mic.smsgame.suquan.b[] E;
  private int F;
  private boolean G;
  private boolean H = false;
  private int I;
  private int J;
  private dq K;
  
  public df(boolean paramBoolean)
  {
    a(this);
    a(null);
    this.b_ = 240;
    this.c_ = 320;
    this.g = (cx.f - this.b_ >> 1);
    this.a_ = (cx.g - this.c_ >> 1);
    this.t = az.b("/m/m");
    this.w = this.t.getWidth();
    this.x = this.t.getHeight();
    this.A = new dl(this.g + 20, this.a_ + 30, 200, 130);
    this.C = new dl(this.A.a + 100, this.A.b + 65, 0, 0);
    this.B = new dl(this.g, this.a_ + 165, this.b_, cx.g - 20 - (this.a_ + 165));
    this.z = new dl();
    this.K = new dq(az.b("/m/ar"), 6);
    this.K.c(1);
    this.K.b(true);
    this.K.a(new byte[][] { { 0, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1 } });
    this.K.a_(0);
    if (paramBoolean) {
      e(3);
    } else {
      e(0);
    }
    if (paramBoolean)
    {
      cf.a().a("intro", -1);
      cf.a().a(120000);
    }
    else
    {
      cf.a().a("victory", -1);
      cf.a().a(120000);
    }
    cf.a().e();
  }
  
  private boolean a(int paramInt1, int paramInt2, dl paramdl)
  {
    int k = (paramdl.c >> 1) + paramdl.a - this.u - this.g;
    int m = (paramdl.d >> 1) + paramdl.b - this.v - this.a_ + 15;
    int n = paramdl.c >> 1;
    int i1 = paramdl.d >> 1;
    if (paramInt1 > this.w - n) {
      paramInt1 = this.w - n;
    }
    if (paramInt1 < n) {
      paramInt1 = n;
    }
    if (paramInt2 > this.x - i1) {
      paramInt2 = this.x - i1;
    }
    if (paramInt2 < i1) {
      paramInt2 = i1;
    }
    int i;
    if ((paramInt1 - k > 4) || (paramInt1 - k < 4)) {
      i = (paramInt1 - k) / 4;
    } else if (paramInt1 == k) {
      i = 0;
    } else {
      i = paramInt1 - k > 0 ? 1 : -1;
    }
    int j;
    if ((paramInt2 - m > 4) || (paramInt2 - m < 4)) {
      j = (paramInt2 - m) / 4;
    } else if (paramInt2 == m) {
      j = 0;
    } else {
      j = paramInt2 - m > 0 ? 1 : -1;
    }
    if ((i == 0) && (j == 0)) {
      return false;
    }
    System.out.println(">>" + i + " " + j);
    this.u -= i;
    this.v -= j;
    System.out.println(">>>>" + this.u + " " + this.v);
    return true;
  }
  
  private void e(int paramInt)
  {
    this.c = paramInt;
    switch (this.c)
    {
    case 3: 
      this.u = this.A.a;
      this.v = this.A.b;
      break;
    case 4: 
      this.y = new cl(h.h, this.B);
      this.D = 1;
      b(new da("Bỏ qua", 102));
      break;
    case 0: 
      this.u = -43;
      this.v = -154;
      break;
    case 1: 
      String[] arrayOfString = h.i;
      String str = dd.c();
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = ao.a(arrayOfString[i], "{s}", str);
      }
      this.y = new cl(arrayOfString, this.B);
      this.E = new com.mic.smsgame.suquan.b[5];
      this.E[0] = new com.mic.smsgame.suquan.b(az.b("/crystalviolet"));
      this.E[1] = new com.mic.smsgame.suquan.b(az.b("/crystalblue"));
      this.E[2] = new com.mic.smsgame.suquan.b(az.b("/crystalgreen"));
      this.E[3] = new com.mic.smsgame.suquan.b(az.b("/crystalred"));
      this.E[4] = new com.mic.smsgame.suquan.b(az.b("/crystalyellow"));
      for (i = 0; i < this.E.length; i++) {
        this.E[i].a_(false);
      }
      this.F = 30;
      b(new da("Bỏ qua", 103));
      break;
    case 2: 
      this.e = az.b("/m/e1");
      this.f = az.b("/m/e2");
      this.F = 0;
      b(null);
      c(new da("Kết thúc", 101));
    }
  }
  
  public void a()
  {
    int i;
    switch (this.c)
    {
    case 3: 
      i = 0;
      if (this.C.a > this.A.a)
      {
        i = 1;
        this.C.a -= 2;
        this.C.c += 4;
      }
      if (this.C.b > this.A.b)
      {
        i = 1;
        this.C.b -= 2;
        this.C.d += 4;
      }
      if (i == 0) {
        e(4);
      }
      break;
    case 4: 
      this.y.b();
      if (this.y.a()) {
        e(5);
      }
      int j = this.y.c();
      if (d[j] != null)
      {
        this.H = true;
        this.I = d[j][0];
        this.J = d[j][1];
      }
      if (!this.H)
      {
        switch (this.D)
        {
        case 1: 
          this.u -= 1;
          if (this.g + this.u + this.t.getWidth() > this.A.a + this.A.c) {
            break;
          }
          this.D = 2;
          break;
        case 2: 
          this.v -= 1;
          if (this.a_ + this.v + this.t.getHeight() > this.A.b + this.A.d) {
            break;
          }
          this.D = 3;
          break;
        case 3: 
          this.u += 1;
          if (this.g + this.u < this.A.a) {
            break;
          }
          this.D = 4;
          break;
        case 4: 
          this.v += 1;
          if (this.a_ + this.v < this.A.b) {
            break;
          }
          this.D = 1;
        }
      }
      else
      {
        this.K.e();
        a(this.I, this.J, this.A);
      }
      break;
    case 5: 
      cz.a().a(-1, 8, false);
      break;
    case 0: 
      i = 0;
      if (this.C.a > this.A.a)
      {
        i = 1;
        this.C.a -= 2;
        this.C.c += 4;
      }
      if (this.C.b > this.A.b)
      {
        i = 1;
        this.C.b -= 2;
        this.C.d += 4;
      }
      if (i == 0) {
        e(1);
      }
      break;
    case 1: 
      this.y.b();
      if (this.y.a()) {
        e(2);
      }
      this.F -= 1;
      if (this.F == 0)
      {
        this.F = 20;
        this.E[ay.a(5)].a(ay.a(cx.f), ay.a(cx.g), 6, "");
      }
      for (int k = 0; k < this.E.length; k++) {
        this.E[k].b();
      }
      break;
    case 2: 
      this.F += 1;
      if (this.F % 3 == 0) {
        this.G = (!this.G);
      }
      break;
    }
  }
  
  private void b(Graphics paramGraphics)
  {
    y.a(paramGraphics, this.z);
    y.a(paramGraphics, this.B.a, this.B.b, this.B.c, this.B.d);
    this.y.a(paramGraphics, this.g, this.a_);
    y.b(paramGraphics, this.z);
  }
  
  private void a(Graphics paramGraphics, dl paramdl)
  {
    y.a(paramGraphics, this.z);
    y.a(paramGraphics, paramdl.a, paramdl.b, paramdl.c, paramdl.d);
    paramGraphics.drawImage(this.t, this.g + this.u, this.a_ + this.v, 0);
    if (this.H) {
      this.K.a(paramGraphics, this.g + this.u + this.I - 10, this.a_ + this.v + this.J - 30);
    }
    y.b(paramGraphics, this.z);
  }
  
  public void a(Graphics paramGraphics)
  {
    paramGraphics.setColor(0);
    paramGraphics.fillRect(this.g, this.a_, this.b_, this.c_);
    switch (this.c)
    {
    case 3: 
      a(paramGraphics, this.C);
      break;
    case 4: 
      a(paramGraphics, this.A);
      b(paramGraphics);
      break;
    case 0: 
      a(paramGraphics, this.C);
      break;
    case 1: 
      a(paramGraphics, this.A);
      b(paramGraphics);
      for (int i = 0; i < this.E.length; i++) {
        this.E[i].a(paramGraphics);
      }
      break;
    case 2: 
      if (this.F < 10)
      {
        if (this.F % 2 == 0) {
          paramGraphics.drawImage(this.e, (cx.f >> 1) + 2, (cx.g >> 1) - 2, 3);
        } else {
          paramGraphics.drawImage(this.e, (cx.f >> 1) - 2, (cx.g >> 1) + 2, 3);
        }
      }
      else {
        paramGraphics.drawImage(this.e, cx.f >> 1, cx.g >> 1, 3);
      }
      if (this.G) {
        paramGraphics.drawImage(this.f, cx.f >> 1, (cx.g >> 1) + 40, 3);
      }
      break;
    }
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    case 101: 
      f.a();
      bg.k();
      ae localae = cz.a("Chúc mừng", "Bạn đã hoàn thành trò chơi. Bạn có muốn chơi các trò chơi khác không? (Trò chơi có khả năng bị đóng lại nếu chọn Có).", "Có", 105, "Không", 104, 1);
      localae.a(this);
      cz.d().a(localae, true);
      break;
    case 104: 
      cz.d().a(false);
      cz.d().e.a(-1, 2, true);
      break;
    case 105: 
      cz.d().a(false);
      try
      {
        MGMIDlet.f().a("Http://PhuThoBay.Pro");
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      cz.d().e.a(-1, 2, true);
      break;
    case 102: 
      e(5);
      break;
    case 103: 
      e(2);
    }
  }
  
  public void e() {}
  
  public void r()
  {
    cf.a().d();
  }
}



/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar

 * Qualified Name:     df

 * JD-Core Version:    0.7.0.1

 */