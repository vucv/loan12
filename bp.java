import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bp
{
  private c a;
  private c b;
  private Image c;
  private Image d;
  private Image e;
  private Image f;
  private a g;
  private a h;
  private int i;
  private int j;
  private int k;
  private int l;
  private dq m;
  
  public bp(c paramc1, c paramc2)
  {
    if (dd.h)
    {
      this.a = paramc1;
      this.b = paramc2;
    }
    else
    {
      this.a = paramc2;
      this.b = paramc1;
    }
    b();
    this.l = (cx.f >> 1);
    int n = this.e.getHeight() * 2 + this.f.getHeight();
    this.i = (cx.g - n >> 1);
    this.j = (this.i + this.e.getHeight() + 2);
    this.k = (this.j + this.f.getHeight());
    this.m = new dq(az.b("/crystalblue"), 3);
    this.m.a(new byte[][] { { 0, 1, 2, 1 } });
    this.m.c(4);
    this.m.b(true);
    this.m.d(this.l - (this.m.k() >> 1), this.j + (this.m.l() >> 1));
  }
  
  private void b()
  {
    Graphics localGraphics = null;
    this.e = az.b("/m/ff");
    this.f = az.b("/m/sw");
    Image localImage1 = az.b("/m/af");
    Image localImage2 = az.b("/m/ih");
    Image localImage3 = az.b("/m/isw");
    av localav = new av();
    int n = 160;
    int i1 = 46;
    this.g = new a(u.a(this.a.a), 0, bz.c);
    this.g.a(45, 2, 112, 14);
    this.h = new a(u.a(this.b.a), 1, bz.c);
    this.h.a(3, 2, 112, 14);
    this.c = Image.createImage(n, i1);
    localGraphics = this.c.getGraphics();
    localGraphics.setColor(8388608);
    localGraphics.fillRect(0, 0, n, i1);
    u.a(localGraphics, this.a.a, 10, 3, true);
    localGraphics.drawImage(localImage1, 8, 1, 0);
    localGraphics.drawImage(localImage2, 54, 20, 0);
    localGraphics.drawImage(localImage3, 111, 20, 0);
    localav.a(localGraphics, this.a.c, 68, 22);
    localav.a(localGraphics, this.a.d, 126, 22);
    bz.c.a(localGraphics, "Cấp " + this.a.e, 8, 34, 0);
    this.d = Image.createImage(n, i1);
    localGraphics = this.d.getGraphics();
    localGraphics.setColor(8388608);
    localGraphics.fillRect(0, 0, n, i1);
    u.a(localGraphics, this.b.a, 120, 3, false);
    localGraphics.drawImage(localImage1, 118, 1, 0);
    localGraphics.drawImage(localImage3, 14, 20, 0);
    localGraphics.drawImage(localImage2, 71, 20, 0);
    localav.a(localGraphics, this.b.d, 28, 22);
    localav.a(localGraphics, this.b.c, 86, 22);
    bz.c.a(localGraphics, "Cấp " + this.b.e, 150, 34, 2);
  }
  
  public void a()
  {
    this.m.e();
    this.g.a();
    this.h.a();
  }
  
  public void a(Graphics paramGraphics)
  {
    paramGraphics.drawImage(this.e, this.l, this.i, 17);
    paramGraphics.drawImage(this.c, this.l, this.i + 11, 17);
    this.g.a(paramGraphics, this.l - 80, this.i + 11);
    paramGraphics.drawImage(this.f, this.l, this.j, 17);
    this.m.a(paramGraphics);
    paramGraphics.drawImage(this.e, this.l, this.k, 17);
    paramGraphics.drawImage(this.d, this.l, this.k + 11, 17);
    this.h.a(paramGraphics, this.l - 80, this.k + 11);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bp
 * JD-Core Version:    0.7.0.1
 */