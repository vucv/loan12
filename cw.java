public abstract class cw
        extends ds {
    protected int b = 0;
    protected int c;
    protected byte[][] d;
    protected int e;
    protected int f;
    protected int g;
    protected boolean h = true;
    protected int i;
    protected int j;
    protected int k;
    protected int l;

    public void c(int paramInt1, int paramInt2) {
        this.i = paramInt1;
        this.j = paramInt2;
    }

    public byte[][] d() {
        return this.d;
    }

    public void a(byte[][] paramArrayOfByte) {
        this.d = paramArrayOfByte;
    }

    public void b(int paramInt) {
        this.g = paramInt;
    }

    public void c(int paramInt) {
        this.e = paramInt;
        this.f = 0;
    }

    public void b(boolean paramBoolean) {
        this.h = paramBoolean;
    }

    public void a_(int paramInt) {
        this.c = paramInt;
        this.g %= this.d[paramInt].length;
    }

    public final void e() {
        if (!this.u) {
            return;
        }
        a();
        if (this.f >= this.e) {
            if ((this.d != null) && (this.d[this.c] != null)) {
                if (this.h) {
                    this.g = ((this.g + 1) % this.d[this.c].length);
                } else {
                    this.g += 1;
                    if (this.g >= this.d[this.c].length) {
                        this.g = (this.d[this.c].length - 1);
                    }
                }
            } else {
                this.g = -1;
            }
            this.f = 0;
        } else {
            this.f += 1;
        }
    }

    public abstract void a();
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cw

 * JD-Core Version:    0.7.0.1

 */