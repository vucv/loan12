package game;

/** PORT: runs at.c() from the scheduler (replaces the worker-thread pool of class ci). */
public class PortTask implements Runnable {
    private final at task;

    public PortTask(at task) {
        this.task = task;
    }

    public void run() {
        this.task.c();
    }
}
