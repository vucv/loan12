public class ci
        implements Runnable {
    private static ci a;
    private static Object b;
    private boolean c = true;
    private at[] d;
    private int e;

    private ci() {
        b = new Object();
        this.c = false;
    }

    public void a(int paramInt) {
        if (paramInt <= 0) {
            throw new RuntimeException("Size of pool cannot be less than or equal 0");
        }
        this.d = new at[paramInt];
        this.c = false;
        new Thread(this).start();
    }

    public void a(at paramat) {
        if (this.d == null) {
            return;
        }
        for (int i = 0; i < this.d.length; i++) {
            if (this.d[i] == null) {
                this.d[i] = paramat;
                this.e += 1;
                try {
                    synchronized (b) {
                        b.notify();
                    }
                } catch (Exception localException1) {
                    try {
                        synchronized (b) {
                            b.notify();
                        }
                    } catch (Exception localException2) {
                    }
                }
            }
        }
    }

    public void run() {
        while (!this.c) {
            for (int i = 0; i < this.d.length; i++) {
                if (this.d[i] != null) {
                    this.d[i].c();
                    this.d[i] = null;
                    this.e -= 1;
                }
            }
            if (this.e <= 0) {
                try {
                    synchronized (b) {
                        b.wait();
                    }
                } catch (Exception localException) {
                }
            }
        }
    }

    public static ci a() {
        if (a == null) {
            a = new ci();
        }
        return a;
    }

    public void b() {
        this.c = true;
        synchronized (b) {
            b.notify();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ci

 * JD-Core Version:    0.7.0.1

 */