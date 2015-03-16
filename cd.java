import javax.microedition.lcdui.Graphics;

public class cd
        extends ds {
    protected ds[] a;
    protected int b;
    protected boolean c = true;
    protected dy d;

    public void a(ds[] paramArrayOfds, int paramInt) {
        this.a = paramArrayOfds;
        this.b = paramInt;
        b(0, paramInt);
    }

    public void a(int paramInt) {
        if (this.a == null) {
            return;
        }
        int i = this.b;
        switch (paramInt) {
            case 12:
            case 14:
                this.b -= 1;
                if (this.b < 0) {
                    if (this.c) {
                        this.b = (this.a.length - 1);
                    } else {
                        this.b = 0;
                    }
                }
                break;
            case 13:
            case 15:
                this.b += 1;
                if (this.b >= this.a.length) {
                    if (this.c) {
                        this.b = 0;
                    } else {
                        this.b = (this.a.length - 1);
                    }
                }
                break;
            case 16:
                b(this.b);
                return;
        }
        if (i != this.b) {
            b(i, this.b);
        }
    }

    public void a(int paramInt1, int paramInt2) {
        if (this.a == null) {
            return;
        }
        int i = this.b;
        for (int j = 0; j < this.a.length; j++) {
            ds localds = this.a[j];
            if (paramInt1 >= localds.i()) {
                if ((((paramInt1 <= localds.i() + localds.k() ? 1 : 0) & (paramInt2 >= localds.j() ? 1 : 0)) != 0) && (paramInt2 <= localds.j() + localds.l())) {
                    this.b = j;
                    if (i == this.b) {
                        b(this.b);
                        break;
                    }
                    b(i, this.b);
                    break;
                }
            }
        }
    }

    public ds a() {
        return this.a[this.b];
    }

    public void b() {
    }

    public final void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        b(paramGraphics);
    }

    protected void b(Graphics paramGraphics) {
        for (int i = 0; i < this.a.length; i++) {
            this.a[i].a(paramGraphics);
        }
    }

    protected void b(int paramInt) {
        if (this.d != null) {
            this.d.a(this.a[paramInt].toString(), this.b);
        }
    }

    protected void b(int paramInt1, int paramInt2) {
        this.a[paramInt1].a(false);
        this.a[this.b].a(true);
        if (this.d != null) {
            this.d.f(paramInt1, this.b);
        }
    }

    public void a(dy paramdy) {
        this.d = paramdy;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cd

 * JD-Core Version:    0.7.0.1

 */