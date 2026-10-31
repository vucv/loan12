package game;
import javax.microedition.lcdui.Graphics;

public class de {
    protected int a;
    protected int b;
    protected int c;
    protected int d;
    protected int e;
    private int portSlot = -1; // PORT-TOUCH: 0 left, 1 centre, 2 right

    public int a() {
        return this.e;
    }

    public de(int paramInt) {
        this.e = paramInt;
    }

    public void a(int paramInt1, int paramInt2, int paramInt3) {
        this.portSlot = paramInt3 == 20 ? 0 : (paramInt3 == 17 ? 1 : (paramInt3 == 24 ? 2 : -1)); // PORT-TOUCH
        this.b = paramInt1;
        switch (paramInt3) {
            case 24:
                this.b = (paramInt1 - this.d);
                break;
            case 17:
                this.b = (paramInt1 - this.d / 2);
        }
        this.c = paramInt2;
    }

    public final boolean a(int paramInt1, int paramInt2) {
        if ((this.portSlot >= 0) && (this.c >= cx.g - 20)) {
            // PORT-TOUCH: soft-key bar button = its third of the whole bar
            int i = cx.f / 3;
            if (paramInt2 <= this.c - 4) {
                return false;
            }
            if (this.portSlot == 0) {
                return paramInt1 < i;
            }
            if (this.portSlot == 2) {
                return paramInt1 >= cx.f - i;
            }
            return (paramInt1 >= i) && (paramInt1 < cx.f - i);
        }
        return (paramInt2 > this.c) && (paramInt1 >= this.b) && (paramInt1 < this.b + this.d);
    }

    public void b() {
        this.a = 3;
    }

    public void a(Graphics paramGraphics) {
    }

    public void c() {
        if (this.a > 0) {
            this.a -= 1;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     de

 * JD-Core Version:    0.7.0.1

 */