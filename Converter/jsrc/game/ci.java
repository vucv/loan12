package game;
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
        this.c = false; // PORT: no worker thread
    }

    public void a(at paramat) {
        if (this.d == null) {
            return;
        }
        j2me.Host.post(new PortTask(paramat)); // PORT: runs on the next tick instead of a worker thread
    }

    public void run() {
        // PORT: unused (tasks are posted to the scheduler)
    }

    public static ci a() {
        if (a == null) {
            a = new ci();
        }
        return a;
    }

    public void b() {
        this.c = true;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ci

 * JD-Core Version:    0.7.0.1

 */