import com.mic.smsgame.suquan.n;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class bx
  extends ds
{
  bn[] a;
  Image b;
  Image c;
  Image d;
  Image e;
  private int g;
  private int[] h = { 81208, 85576, 23637 };
  private int[] i = { 5046585, 6095173, 7209297 };
  ax[] f;
  private int j;
  private int k;
  private int l;
  private Image m;
  private int n = 0;
  private boolean o;
  private int v;
  private n[] w;
  private n[] x;
  private int y;
  private int z;
  private int[] A;
  private dq[] B;
  
  public bx(bn[] paramArrayOfbn)
  {
    this.a = paramArrayOfbn;
    d();
    this.f = new ax[2];
    this.f[0] = new ax();
    this.f[0].d(cx.l + 35, cx.m + 237);
    this.f[1] = new ax();
    this.f[1].d(cx.l + 175, cx.m + 237);
    byte[][] arrayOfByte = { { 0, 0, 0, 0, 1, 1, 1, 2, 2, 1 } };
    Image localImage = bs.a().c[2];
    this.w = new n[25];
    for (int i1 = 0; i1 < this.w.length; i1++)
    {
      this.w[i1] = new n();
      this.w[i1].a(arrayOfByte);
      this.w[i1].a(localImage, 3);
      this.w[i1].a(false);
    }
    localImage = bs.a().c[3];
    this.x = new n[10];
    for (i1 = 0; i1 < this.x.length; i1++)
    {
      this.x[i1] = new n();
      this.x[i1].a(arrayOfByte);
      this.x[i1].a(localImage, 3);
      this.x[i1].a(false);
    }
    this.A = new int[2];
    this.B = new dq[2];
    this.B[0] = new dq(bs.a().C, 3);
    this.B[0].d(this.f[0].i() + this.f[0].k() / 2, this.f[0].j() + this.f[0].l() / 2);
    this.B[0].k(3);
    this.B[1] = new dq(bs.a().D, 3);
    this.B[1].d(this.f[0].i() + this.f[0].k() / 2, this.f[0].j() + this.f[0].l());
    this.B[1].k(33);
    arrayOfByte = new byte[][] { { 0, 0, 1, 1, 2, 2 } };
    for (i1 = 0; i1 < this.B.length; i1++)
    {
      this.B[i1].a(false);
      this.B[i1].a(arrayOfByte);
    }
  }
  
  private void d()
  {
    this.b = bs.a().l;
    this.d = bs.a().n;
    this.e = bs.a().o;
    this.c = bs.a().m;
    this.m = bs.a().p;
  }
  
  public ax[] a()
  {
    return this.f;
  }
  
  public void b(boolean paramBoolean)
  {
    this.B[1].a(paramBoolean);
  }
  
  public void c(boolean paramBoolean)
  {
    this.B[0].a(paramBoolean);
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    bn localbn = this.a[0];
    int i1 = this.f[0].i() + this.f[0].k() + 6;
    int i2 = this.f[0].j();
    int i3 = localbn.l();
    int i5;
    if ((i3 > localbn.j()) || (this.A[0] % 2 == 0)) {
      if (i3 > localbn.e())
      {
        y.a(paramGraphics, this.b, 0, 0, this.b.getWidth(), this.b.getHeight(), i1, i2, 0);
        i4 = i3 - localbn.e();
        i5 = this.b.getWidth() * i4 / localbn.e();
        y.a(paramGraphics, this.c, 0, 0, i5, this.c.getHeight(), i1, i2, 0);
      }
      else
      {
        i4 = this.b.getWidth() * i3 / localbn.e();
        if ((localbn.l() > 0) && (i4 < 1)) {
          i4 = 1;
        }
        y.a(paramGraphics, this.b, 0, 0, i4, this.b.getHeight(), i1, i2, 0);
      }
    }
    i2 += 11;
    y.a(paramGraphics, this.e, 0, 0, this.e.getWidth() * localbn.p() / localbn.q(), this.e.getHeight(), i1, i2, 0);
    i2 += 11;
    if (this.z % 2 == 0) {
      y.a(paramGraphics, this.d, 0, 0, this.d.getWidth() * localbn.o() / localbn.n(), this.d.getHeight(), i1, i2, 0);
    }
    localbn = this.a[1];
    i1 = this.f[1].i() - 5;
    i2 = this.f[0].j();
    int i4 = 0;
    i3 = localbn.l();
    if ((i3 > localbn.j()) || (this.A[1] % 2 == 0)) {
      if (i3 > localbn.e())
      {
        paramGraphics.drawRegion(this.b, 0, 0, this.b.getWidth(), this.b.getHeight(), 2, i1 - this.b.getWidth(), i2, 0);
        i5 = i3 - localbn.e();
        i4 = this.b.getWidth() * i5 / localbn.e();
        paramGraphics.drawRegion(this.c, 0, 0, i4, this.c.getHeight(), 2, i1 - i4, i2, 0);
      }
      else
      {
        i4 = this.b.getWidth() * i3 / localbn.e();
        if ((localbn.l() > 0) && (i4 < 1)) {
          i4 = 1;
        }
        paramGraphics.drawRegion(this.b, 0, 0, i4, this.b.getHeight(), 2, i1 - i4, i2, 0);
      }
    }
    i2 += 11;
    i4 = this.e.getWidth() * localbn.p() / localbn.q();
    paramGraphics.drawRegion(this.e, 0, 0, i4, this.e.getHeight(), 2, i1 - i4, i2, 0);
    i2 += 11;
    i4 = this.d.getWidth() * localbn.o() / localbn.n();
    paramGraphics.drawRegion(this.d, 0, 0, i4, this.d.getHeight(), 2, i1 - i4, i2, 0);
    Object localObject;
    int i7;
    int i8;
    int i9;
    if ((this.k > 0) && (this.k <= 10))
    {
      localObject = this.f[this.j];
      if (this.k > 6)
      {
        if (this.k % 2 == 0)
        {
          paramGraphics.setColor(16777215);
          paramGraphics.fillRect(((ds)localObject).i(), ((ds)localObject).j(), ((ds)localObject).k(), ((ds)localObject).l());
        }
      }
      else
      {
        i7 = this.m.getWidth() / 5;
        i8 = this.m.getHeight();
        i9 = (6 - this.k) % 5;
        int i10 = 2;
        if (this.j == 0) {
          i10 = 0;
        }
        paramGraphics.drawRegion(this.m, i9 * i7, 0, i7, i8, i10, ((ds)localObject).i() + ((ds)localObject).k() / 2, ((ds)localObject).j() + ((ds)localObject).l() / 2 - 7, 3);
      }
    }
    i1 = this.f[0].i() + this.f[0].k() + 5;
    i2 = this.f[0].j();
    i4 = 0;
    if (!this.o)
    {
      i1 = this.f[this.n].i() - 2;
      i2 = this.f[this.n].j() - 2;
      localObject = this.h;
      i4 = 83;
      if (this.n == 1)
      {
        i1 = i1 + this.f[this.n].k() - i4 + 5;
        localObject = this.i;
      }
      i4 = 82;
      i7 = 34;
      for (i8 = 0; i8 < localObject.length; i8++)
      {
        y.b(paramGraphics, localObject[i8], i1, i2, i4, i7);
        i1--;
        i2--;
        i4 += 2;
        i7 += 2;
      }
      i8 = 0;
      if (this.g < 10)
      {
        if (this.g % 2 == 0) {
          i8 = 3;
        }
      }
      else if (this.g < 18) {
        i8 = (this.g - 3) % 4;
      } else {
        i8 = 3;
      }
      i1 = this.f[0].i() - 2;
      i2 = this.f[0].j() - 186;
      i4 = 172;
      i7 = 172;
      for (i9 = 0; i9 < i8; i9++)
      {
        paramGraphics.setColor(localObject[i9]);
        paramGraphics.drawRect(i1, i2, i4, i7);
        i1--;
        i2--;
        i4 += 2;
        i7 += 2;
      }
    }
    for (int i6 = 0; i6 < this.B.length; i6++) {
      this.B[i6].a(paramGraphics);
    }
    for (i6 = this.x.length - 1; i6 > -1; i6--) {
      this.x[i6].a(paramGraphics);
    }
    for (i6 = this.w.length - 1; i6 > -1; i6--) {
      this.w[i6].a(paramGraphics);
    }
  }
  
  public void b()
  {
    if (!this.o)
    {
      this.v += 1;
      if (this.v == 10) {
        this.v = 0;
      }
      if (this.g < 20) {
        this.g += 1;
      }
    }
    if (this.k > 0)
    {
      this.k -= 1;
      if (this.k == 7)
      {
        bs.a().f();
        if (this.j == 0) {
          cz.d(250);
        }
      }
      else if (this.k == 0)
      {
        this.k = this.l;
        this.l = 0;
      }
    }
    for (int i1 = 0; i1 < this.a.length; i1++) {
      if ((this.j == i1) && (this.y > 0))
      {
        this.y -= 1;
      }
      else
      {
        int i2 = this.a[i1].l();
        int i3 = this.a[i1].r() - i2;
        if (i3 != 0)
        {
          if (Math.abs(i3) >= 6) {
            i3 /= 6;
          }
          this.a[i1].d(i2 + i3);
        }
      }
    }
    for (i1 = 0; i1 < this.A.length; i1++) {
      if (this.a[i1].l() <= this.a[i1].j())
      {
        this.A[i1] += 1;
        if (this.A[i1] > 1) {
          this.A[i1] = 0;
        }
      }
    }
    for (i1 = this.w.length - 1; i1 > -1; i1--) {
      this.w[i1].e();
    }
    for (i1 = this.x.length - 1; i1 > -1; i1--) {
      this.x[i1].e();
    }
    if (this.z > 0) {
      this.z -= 1;
    }
    for (i1 = 0; i1 < this.B.length; i1++) {
      this.B[i1].e();
    }
  }
  
  public void a(int paramInt)
  {
    if (this.y == 0) {
      this.y = paramInt;
    } else if (this.y < paramInt) {
      this.y = (this.y + paramInt >> 1);
    }
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    bn localbn = this.a[this.j];
    int i2 = this.f[paramInt1].j() + 10;
    int i1;
    int i3;
    if (paramInt1 == 0)
    {
      i1 = this.f[paramInt1].i() - 3 + this.f[paramInt1].k() + this.d.getWidth() * localbn.o() / localbn.n();
      for (i3 = 0; i3 < this.x.length; i3++) {
        if (!this.x[i3].m())
        {
          this.x[i3].a(i1, i2, i1 - 5 + ay.a(80), i2 - 80 + ay.a(100), paramInt2);
          this.x[i3].b(ay.a(10));
        }
      }
    }
    else
    {
      i1 = this.f[paramInt1].i() - 5 - this.d.getWidth() * localbn.o() / localbn.n();
      for (i3 = 0; i3 < this.x.length; i3++) {
        if (!this.x[i3].m())
        {
          this.x[i3].a(i1, i2, i1 + 5 - ay.a(80), i2 - 80 + ay.a(100), paramInt2);
          this.x[i3].b(ay.a(10));
        }
      }
    }
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    a(this.y);
    bn localbn = this.a[paramInt1];
    if (localbn.k()) {
      return;
    }
    if (this.k < 1) {
      this.k = paramInt2;
    }
    if (this.k < paramInt2)
    {
      this.l = (paramInt2 - this.k);
      if (this.l < 6) {
        this.l = 0;
      }
    }
    else
    {
      this.l = (this.k - paramInt2);
      this.k = paramInt2;
    }
    paramInt2 -= 5;
    if (paramInt2 < 0) {
      paramInt2 = 0;
    }
    this.j = paramInt1;
    int i2 = this.f[paramInt1].j();
    int i1;
    int i3;
    int i4;
    if (paramInt1 == 0)
    {
      i1 = this.f[paramInt1].i() - 3 + this.f[paramInt1].k() + this.b.getWidth() * localbn.r() / localbn.s();
      i3 = 15;
      for (i4 = 0; i4 < this.w.length; i4++) {
        if (!this.w[i4].m())
        {
          this.w[i4].a(i1, i2, i1 - 5 + ay.a(80), i2 - 80 + ay.a(100), paramInt2);
          this.w[i4].b(ay.a(10));
          i3--;
          if (i3 <= 0) {
            break;
          }
        }
      }
    }
    else
    {
      i1 = this.f[paramInt1].i() - 5 - this.b.getWidth() * localbn.r() / localbn.s();
      i3 = 15;
      for (i4 = 0; i4 < this.w.length; i4++) {
        if (!this.w[i4].m())
        {
          this.w[i4].a(i1, i2, i1 + 5 - ay.a(80), i2 - 80 + ay.a(100), paramInt2);
          this.w[i4].b(ay.a(10));
          i3--;
          if (i3 <= 0) {
            break;
          }
        }
      }
    }
  }
  
  public void b(int paramInt)
  {
    if (this.n != paramInt) {
      this.g = 0;
    }
    this.n = paramInt;
  }
  
  public void d(boolean paramBoolean)
  {
    this.o = paramBoolean;
  }
  
  public void c()
  {
    this.z = 40;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     bx
 * JD-Core Version:    0.7.0.1
 */