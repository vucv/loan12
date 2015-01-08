import com.mic.smsgame.suquan.ad;
import com.mic.smsgame.suquan.k;
import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bs
{
  private static bs K;
  public Image a;
  public Image b = Image.createImage(cx.f, cx.g);
  public Image[] c;
  public Image[] d;
  public Image[] e;
  public Image[] f;
  public Image[] g;
  public Image[] h;
  public Image[] i;
  public Image[] j;
  public Image[] k;
  public Image l;
  public Image m;
  public Image n;
  public Image o;
  public Image p;
  public Image q;
  public Image r;
  public Image s;
  public Image t;
  public Image u;
  public Image v;
  public Image w;
  public Image x;
  public Image y;
  public Image z;
  public Image A;
  public Image B;
  public Image C;
  public Image D;
  public Image E;
  public ad[] F;
  public ad G;
  public ad H;
  public ad I;
  public k J;
  
  public static bs a()
  {
    return K;
  }
  
  public static void b()
  {
    K = new bs();
  }
  
  public void a(int[] paramArrayOfInt)
  {
    for (int i1 = 0; i1 < paramArrayOfInt.length; i1++) {
      switch (paramArrayOfInt[i1])
      {
      case 0: 
      case 1: 
        if (this.r == null) {
          this.r = az.b("/fireball");
        }
        break;
      case 2: 
        if (this.s == null) {
          this.s = az.b("/hellfire");
        }
        break;
      case 5: 
        if (this.t == null) {
          this.t = az.b("/blastlighting");
        }
        break;
      case 8: 
        if (this.v == null) {
          this.v = az.b("/frozen");
        }
      case 6: 
        if (this.u == null) {
          this.u = az.b("/icebolt");
        }
        break;
      case 7: 
        if (this.w == null) {
          this.w = az.b("/healing");
        }
        break;
      case 4: 
        if (this.x == null) {
          this.x = az.b("/shieldlighting");
        }
        break;
      case 3: 
        if (this.y == null)
        {
          this.y = az.b("/chainlighting");
          this.h = new Image[2];
          this.h[0] = az.b("/chainlightingpart1");
          this.h[1] = az.b("/chainlightingpart2");
        }
        break;
      }
    }
  }
  
  public void c()
  {
    this.G = new ad("explodechess");
    if (!cx.V)
    {
      this.I = new ad("attack");
      this.H = new ad("useitem");
    }
    this.J = new k();
    this.J.a("win", 1);
  }
  
  public void d()
  {
    i();
    if (this.J != null)
    {
      this.J.a("win", 1);
      this.J.a(123015);
      this.J.d();
    }
  }
  
  public void e()
  {
    i();
    if (this.J != null)
    {
      this.J.a("lose", 1);
      this.J.d();
      this.J.a(155041);
    }
  }
  
  public void b(int[] paramArrayOfInt)
  {
    if (cx.V) {
      return;
    }
    for (int i1 = 0; i1 < paramArrayOfInt.length; i1++) {
      switch (paramArrayOfInt[i1])
      {
      case 0: 
        if (this.F[0] == null) {
          this.F[0] = new ad("fire1");
        }
        break;
      case 1: 
        if (this.F[1] == null) {
          this.F[1] = new ad("fire2");
        }
        break;
      case 2: 
        if (this.F[2] == null) {
          this.F[2] = new ad("fire3");
        }
        break;
      case 4: 
        if (this.F[4] == null) {
          this.F[4] = new ad("lightning2");
        }
        break;
      case 3: 
        if (this.F[3] == null) {
          this.F[3] = new ad("lightning1");
        }
        break;
      case 5: 
        if (this.F[5] == null) {
          this.F[5] = new ad("lightning3");
        }
        break;
      case 6: 
        if (this.F[6] == null) {
          this.F[6] = new ad("ice1");
        }
        break;
      case 7: 
        if (this.F[7] == null) {
          this.F[7] = new ad("ice2");
        }
        break;
      case 8: 
        if (this.F[8] == null) {
          this.F[8] = new ad("ice3");
        }
        break;
      }
    }
  }
  
  private void c(ad paramad)
  {
    if ((paramad != null) && (paramad.e())) {
      paramad.d();
    }
  }
  
  private boolean l()
  {
    for (int i1 = this.F.length - 1; i1 >= 0; i1--) {
      if ((this.F[i1] != null) && (this.F[i1].e())) {
        return true;
      }
    }
    return false;
  }
  
  public void a(int paramInt)
  {
    if (this.F == null) {
      return;
    }
    if (d(this.F[paramInt])) {
      return;
    }
    i();
    a(this.F[paramInt]);
  }
  
  public void f()
  {
    if (l()) {
      return;
    }
    if (d(this.I)) {
      return;
    }
    c(this.H);
    c(this.G);
    a(this.I);
  }
  
  public void g()
  {
    if (l()) {
      return;
    }
    if (d(this.I)) {
      return;
    }
    if (d(this.H)) {
      return;
    }
    if (!d(this.G)) {
      a(this.G);
    }
  }
  
  public void h()
  {
    if (l()) {
      return;
    }
    if (d(this.I)) {
      return;
    }
    if (d(this.H)) {
      return;
    }
    c(this.G);
    a(this.H);
  }
  
  public void i()
  {
    for (int i1 = this.F.length - 1; i1 >= 0; i1--) {
      c(this.F[i1]);
    }
    c(this.I);
    c(this.G);
    c(this.H);
  }
  
  private void m()
  {
    for (int i1 = this.F.length - 1; i1 >= 0; i1--) {
      b(this.F[i1]);
    }
    b(this.I);
    b(this.H);
    this.F = null;
    this.I = null;
    this.G = null;
    this.H = null;
  }
  
  public void j()
  {
    m();
    if (this.J != null) {
      this.J.b();
    }
  }
  
  private boolean d(ad paramad)
  {
    if (paramad == null) {
      return false;
    }
    return paramad.e();
  }
  
  public void a(ad paramad)
  {
    if (paramad == null) {
      return;
    }
    paramad.b();
  }
  
  public void b(ad paramad)
  {
    if (paramad == null) {
      return;
    }
    paramad.a();
  }
  
  public void c(int[] paramArrayOfInt)
  {
    this.i = new Image[paramArrayOfInt.length];
    for (int i1 = 0; i1 < paramArrayOfInt.length; i1++) {
      switch (paramArrayOfInt[i1])
      {
      case 0: 
        this.i[i1] = az.b("/fireballicon");
        break;
      case 1: 
        this.i[i1] = az.b("/meteoricon");
        break;
      case 2: 
        this.i[i1] = az.b("/hellfireicon");
        break;
      case 5: 
        this.i[i1] = az.b("/blastlightingicon");
        break;
      case 6: 
        this.i[i1] = az.b("/icebolticon");
        break;
      case 8: 
        this.i[i1] = az.b("/frozenicon");
        break;
      case 7: 
        this.i[i1] = az.b("/healingicon");
        break;
      case 4: 
        this.i[i1] = az.b("/shieldlightingicon");
        break;
      case 3: 
        this.i[i1] = az.b("/chainlightingicon");
      }
    }
  }
  
  protected bs()
  {
    Graphics localGraphics = this.b.getGraphics();
    Image localImage = az.b("/bkboard");
    localGraphics.drawImage(localImage, cx.l, cx.m, 0);
    localImage = null;
    String[] arrayOfString1 = { "/crystalgray", "/crystalgreen", "/crystalred", "/crystalblue", "/crystalyellow", "/crystalviolet", "" };
    String[] arrayOfString2 = { "/sword", "/rice", "/heart", "/yinyang", "/gold", "/book", "/swordred" };
    String[] arrayOfString3 = { "/explodesword", "/exploderice", "/explodeheart", "/explodeyinyang", "/explodegold", "/explodebook", "" };
    String[] arrayOfString4 = { "", "/explodericex2", "/explodeheartx2", "/explodeyinyangx2", "/explodegoldx2", "/explodebookx2", "" };
    this.e = new Image[7];
    this.f = new Image[7];
    this.d = new Image[7];
    this.c = new Image[7];
    for (int i1 = this.e.length - 1; i1 > -1; i1--)
    {
      this.d[i1] = az.b(arrayOfString2[i1]);
      this.e[i1] = az.b(arrayOfString3[i1]);
      this.f[i1] = az.b(arrayOfString4[i1]);
      this.c[i1] = az.b(arrayOfString1[i1]);
    }
    this.c[6] = this.c[0];
    Image tmp370_369 = (this.e[6] =  = this.e[0]);
    this.f[6] = tmp370_369;
    this.f[0] = tmp370_369;
    this.a = az.b("/star");
    this.l = az.b("/blood");
    this.m = az.b("/bloodext");
    this.n = az.b("/mana");
    this.o = az.b("/food");
    this.p = az.b("/bloodthrowaround");
    this.j = new Image[2];
    this.g = new Image[2];
    for (i1 = 0; i1 < this.j.length; i1++)
    {
      this.j[i1] = az.b("/arrowfocus" + (i1 + 1));
      this.g[i1] = az.b("/focus" + (i1 + 1));
    }
    this.q = az.b("/zoomfocus1");
    this.k = new Image[2];
    this.k[0] = az.b("/frame1");
    this.k[1] = az.b("/frame2");
    this.z = az.b("/frame3");
    this.A = az.b("/focusitem");
    this.B = az.b("/numbersmall");
    this.D = az.b("/powerattack");
    this.C = az.b("/defenceshield");
    this.E = az.b("/itemshops");
    this.F = new ad[9];
  }
  
  public Image[] a(int paramInt1, int paramInt2)
  {
    Image[] arrayOfImage = new Image[2];
    Image localImage = az.b("/avatar");
    arrayOfImage[0] = Image.createImage(localImage, paramInt1 % 10 * 30, paramInt1 / 10 * 30, 30, 30, 2);
    arrayOfImage[1] = Image.createImage(localImage, paramInt2 % 10 * 30, paramInt2 / 10 * 30, 30, 30, 0);
    return arrayOfImage;
  }
  
  public static void k()
  {
    System.out.println("clear");
    K.j();
    K = null;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bs
 * JD-Core Version:    0.7.0.1
 */