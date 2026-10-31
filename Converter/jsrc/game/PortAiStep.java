package game;

/** PORT: one step of the computer opponent (class t), scheduled after the pauses it used to sleep. */
public class PortAiStep implements Runnable {
    private final t ai;
    private final int step;

    public PortAiStep(t ai, int step) {
        this.ai = ai;
        this.step = step;
    }

    public void run() {
        this.ai.portStep(this.step);
    }
}
