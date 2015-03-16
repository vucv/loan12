public class dl {
    public int a;
    public int b;
    public int c;
    public int d;

    public dl() {
    }

    public dl(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.a = paramInt1;
        this.b = paramInt2;
        this.c = paramInt3;
        this.d = paramInt4;
    }

    public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.a = paramInt1;
        this.b = paramInt2;
        this.c = paramInt3;
        this.d = paramInt4;
    }

    public boolean a(int paramInt1, int paramInt2) {
        return (paramInt1 > this.a) && (paramInt1 < this.a + this.c) && (paramInt2 > this.b) && (paramInt2 < this.b + this.d);
    }

    public boolean a(dl paramdl) {
        return b(paramdl.a, paramdl.b, paramdl.c, paramdl.d);
    }

    public boolean b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        int i = this.c;
        int j = this.d;
        int k = paramInt3;
        int m = paramInt4;
        if ((k <= 0) || (m <= 0) || (i <= 0) || (j <= 0)) {
            return false;
        }
        int n = this.a;
        int i1 = this.b;
        int i2 = paramInt1;
        int i3 = paramInt2;
        k += i2;
        m += i3;
        i += n;
        j += i1;
        return ((k < i2) || (k > n)) && ((m < i3) || (m > i1)) && ((i < n) || (i > i2)) && ((j < i1) || (j > i3));
    }

    public boolean b(int paramInt1, int paramInt2) {
        int i = this.c;
        int j = this.d;
        if ((i | j) < 0) {
            return false;
        }
        int k = this.a;
        int m = this.b;
        if ((paramInt1 < k) || (paramInt2 < m)) {
            return false;
        }
        i += k;
        j += m;
        return ((i < k) || (i > paramInt1)) && ((j < m) || (j > paramInt2));
    }

    public String toString() {
        String str = "[X]: " + this.a + " [Y]: " + this.b + " [W]: " + this.c + " [H]: " + this.d;
        return str;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dl

 * JD-Core Version:    0.7.0.1

 */