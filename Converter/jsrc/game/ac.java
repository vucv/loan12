package game;
public class ac
        extends ch {
    public ac(bu parambu, int paramInt) {
        super(parambu, paramInt);
    }

    public void a(int paramInt) {
        switch (paramInt) {
            case 12:
                this.b.m();
                break;
            case 13:
                this.b.n();
                break;
            case 14:
                this.b.o();
                break;
            case 15:
                this.b.p();
                break;
            case 16:
                this.b.i();
                break;
            case 17:
                this.b.r();
                break;
            case 18:
                this.b.g();
                break;
            case 11:
                this.b.s();
        }
    }

    public void a(int paramInt1, int paramInt2) {
        int j;
        this.portSwipe = false;
        this.portUnselect = false;
        if (paramInt2 >= cx.g - 15) {
            int i = cx.f / 3;
            j = i + i;
            if (paramInt1 > j) {
                this.b.g();
            } else if (paramInt1 > i) {
                this.b.s();
            } else {
                this.b.r();
            }
            return;
        }
        dl localdl = this.b.h();
        if (localdl.b(paramInt1, paramInt2)) {
            j = (paramInt1 - localdl.a) / 21 + 2;
            int k = (paramInt2 - localdl.b) / 21 + 2;
            int m = this.b.k();
            int n = this.b.l();
            if ((k == m) && (j == n)) {
                if (this.b.j()) {
                    this.portUnselect = true; // decided on release: a swipe keeps the selection
                } else {
                    this.b.i();
                }
            } else if (this.b.portTraining()) {
                // tutorial: original rules for the other gems (it checks every step)
                if (this.b.j()) {
                    int i1 = k - m;
                    int i2 = j - n;
                    if (i1 == 1) {
                        this.b.n();
                    } else if (i1 == -1) {
                        this.b.m();
                    } else if (i2 == 1) {
                        this.b.p();
                    } else if (i2 == -1) {
                        this.b.o();
                    }
                    return;
                } else {
                    this.b.c(k, j);
                }
            } else if ((this.b.j()) && (Math.abs(k - m) + Math.abs(j - n) == 1)) {
                if (k - m == 1) {
                    this.b.n();
                } else if (k - m == -1) {
                    this.b.m();
                } else if (j - n == 1) {
                    this.b.p();
                } else {
                    this.b.o();
                }
                return;
            } else {
                this.b.c(k, j);
                if ((this.b.k() == k) && (this.b.l() == j) && (!this.b.j())) {
                    this.b.i();
                }
            }
            this.portSwipe = (this.b.j()) && (this.b.k() == k) && (this.b.l() == j);
            this.portX = paramInt1;
            this.portY = paramInt2;
        }
    }

    // PORT-TOUCH: swipe state of the current press
    private boolean portSwipe;
    private boolean portUnselect;
    private int portX;
    private int portY;

    /** PORT-TOUCH: a drag of 8+ pixels from the selected gem swaps it with its neighbour that way. */
    public void portDrag(int paramInt1, int paramInt2) {
        if ((!this.portSwipe) || (!this.b.j())) {
            return;
        }
        int i = paramInt1 - this.portX;
        int j = paramInt2 - this.portY;
        int k = Math.abs(i);
        int m = Math.abs(j);
        if ((k < 8) && (m < 8)) {
            return;
        }
        this.portSwipe = false;
        this.portUnselect = false;
        if (k > m) {
            if (i > 0) {
                this.b.p();
            } else {
                this.b.o();
            }
        } else if (j > 0) {
            this.b.n();
        } else {
            this.b.m();
        }
    }

    /** PORT-TOUCH: releasing a tap on the already selected gem deselects it. */
    public void portRelease(int paramInt1, int paramInt2) {
        if ((this.portUnselect) && (this.b.j())) {
            this.b.i();
        }
        this.portUnselect = false;
        this.portSwipe = false;
    }

    public void a() {
    }

    public void b() {
        this.b.c(1);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ac

 * JD-Core Version:    0.7.0.1

 */