package j2me;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

/**
 * Single-threaded runtime for the MIDP emulation.
 *
 * The original game ran a few Java threads (game loop, splash loop, a worker pool, AI, SMS).
 * In the port those were turned into scheduled tasks; the host calls {@link #tick()} once per
 * frame and everything (input, timers, game logic, painting) runs from there, on one thread.
 */
public final class Host {
    public static Backend backend;

    public static int screenWidth = 240;
    public static int screenHeight = 320;

    /** The emulated LCD, 0xAARRGGBB. Read it after {@link #tick()} when {@link #screenDirty} is set. */
    public static Image screenImage;
    public static int[] screen;
    public static boolean screenDirty;
    private static Graphics screenG;

    public static MIDlet midlet;
    public static Display display;
    public static boolean exitRequested;
    public static boolean paused;

    // ---- scheduler -------------------------------------------------------------------------
    private static Runnable[] taskRun = new Runnable[32];
    private static long[] taskDue = new long[32];
    private static int[] taskInterval = new int[32];
    private static int taskCount;
    private static int taskSerial;

    // ---- input queue -----------------------------------------------------------------------
    public static final int KEY_PRESSED = 1;
    public static final int KEY_RELEASED = 2;
    public static final int KEY_REPEATED = 3;
    public static final int POINTER_PRESSED = 4;
    public static final int POINTER_RELEASED = 5;
    public static final int POINTER_DRAGGED = 6;
    private static int[] evType = new int[64];
    private static int[] evA = new int[64];
    private static int[] evB = new int[64];
    private static int evCount;

    private static long frameTime;
    private static boolean inTick;

    private Host() {
    }

    public static void init(Backend b, int width, int height) {
        backend = b;
        screenWidth = width;
        screenHeight = height;
        screenImage = Image.createImage(width, height);
        screen = screenImage.pixels();
        screenG = screenImage.getGraphics();
        screenG.setColor(0);
        screenG.fillRect(0, 0, width, height);
        screenDirty = true;
        taskCount = 0;
        evCount = 0;
        exitRequested = false;
        paused = false;
        display = null;
        midlet = null;
        frameTime = b.currentTimeMillis();
    }

    public static long now() {
        return backend.currentTimeMillis();
    }

    /** Labels of the game's soft keys (left, centre, right; null = none), for the host's touch buttons. */
    public static String[] softKeyLabels = new String[3];

    /** Part of the screen the game wants zoomed on tall phones: {x, y, w, h}, or null for the whole screen. */
    public static int[] viewport;

    public static void setSoftKeys(String left, String centre, String right) {
        softKeyLabels[0] = left;
        softKeyLabels[1] = centre;
        softKeyLabels[2] = right;
    }

    /** System.getProperty for the game: answered by the backend (null when unknown). */
    public static String property(String key) {
        return backend != null ? backend.getProperty(key) : null;
    }

    public static void log(String s) {
        if (backend != null) {
            backend.log(s);
        }
    }

    /** Graphics for the LCD with clip reset (used for Canvas.paint and GameCanvas.flushGraphics). */
    public static Graphics screenGraphics() {
        screenG.reset();
        return screenG;
    }

    public static void markScreenDirty() {
        screenDirty = true;
    }

    // ---- MIDlet lifecycle -----------------------------------------------------------------

    /** Creates nothing: the caller constructs the MIDlet (its constructor may already set the display). */
    public static void start(MIDlet m) {
        midlet = m;
        try {
            m.callStartApp();
        } catch (Exception e) {
            logException("startApp", e);
        }
    }

    public static void pause() {
        if (midlet == null || paused) {
            return;
        }
        paused = true;
        Displayable cur = display != null ? display.getShown() : null;
        if (cur != null) {
            cur.callHideNotify();
        }
        try {
            midlet.callPauseApp();
        } catch (Exception e) {
            logException("pauseApp", e);
        }
        Media.pauseAll();
    }

    public static void resume() {
        if (midlet == null || !paused) {
            return;
        }
        paused = false;
        Media.resumeAll();
        try {
            midlet.callStartApp();
        } catch (Exception e) {
            logException("startApp", e);
        }
        Displayable cur = display != null ? display.getShown() : null;
        if (cur != null) {
            cur.callShowNotify();
            if (cur instanceof Canvas) {
                ((Canvas) cur).repaint();
            }
        }
    }

    public static void destroy() {
        if (midlet == null) {
            return;
        }
        try {
            midlet.callDestroyApp(true);
        } catch (Exception e) {
            logException("destroyApp", e);
        }
        Media.closeAll();
        midlet = null;
    }

    // ---- scheduler ------------------------------------------------------------------------

    /** Runs r once, on the next tick. */
    public static void post(Runnable r) {
        addTask(r, 0, 0);
    }

    /** Runs r once after the delay. */
    public static void postDelayed(Runnable r, int delayMs) {
        addTask(r, delayMs, 0);
    }

    /** Runs r repeatedly every intervalMs (first run on the next tick). Replaces an existing loop for r. */
    public static void startLoop(Runnable r, int intervalMs) {
        stopLoop(r);
        addTask(r, 0, intervalMs < 1 ? 1 : intervalMs);
    }

    public static void stopLoop(Runnable r) {
        for (int i = 0; i < taskCount; i++) {
            if (taskRun[i] == r && taskInterval[i] > 0) {
                removeTask(i);
                i--;
            }
        }
    }

    public static void cancel(Runnable r) {
        for (int i = 0; i < taskCount; i++) {
            if (taskRun[i] == r) {
                removeTask(i);
                i--;
            }
        }
    }

    private static void addTask(Runnable r, int delay, int interval) {
        if (r == null) {
            return;
        }
        if (taskCount == taskRun.length) {
            int n = taskRun.length * 2;
            Runnable[] nr = new Runnable[n];
            long[] nd = new long[n];
            int[] ni = new int[n];
            System.arraycopy(taskRun, 0, nr, 0, taskCount);
            System.arraycopy(taskDue, 0, nd, 0, taskCount);
            System.arraycopy(taskInterval, 0, ni, 0, taskCount);
            taskRun = nr;
            taskDue = nd;
            taskInterval = ni;
        }
        taskRun[taskCount] = r;
        taskDue[taskCount] = now() + delay;
        taskInterval[taskCount] = interval;
        taskCount++;
        taskSerial++;
    }

    private static void removeTask(int i) {
        taskCount--;
        for (int k = i; k < taskCount; k++) {
            taskRun[k] = taskRun[k + 1];
            taskDue[k] = taskDue[k + 1];
            taskInterval[k] = taskInterval[k + 1];
        }
        taskRun[taskCount] = null;
        taskSerial++;
    }

    // ---- input ----------------------------------------------------------------------------

    public static void queueEvent(int type, int a, int b) {
        if (evCount == evType.length) {
            return; // drop: the game is not keeping up
        }
        evType[evCount] = type;
        evA[evCount] = a;
        evB[evCount] = b;
        evCount++;
    }

    public static void keyPressed(int keyCode) {
        queueEvent(KEY_PRESSED, keyCode, 0);
    }

    public static void keyReleased(int keyCode) {
        queueEvent(KEY_RELEASED, keyCode, 0);
    }

    public static void pointerPressed(int x, int y) {
        queueEvent(POINTER_PRESSED, x, y);
    }

    public static void pointerReleased(int x, int y) {
        queueEvent(POINTER_RELEASED, x, y);
    }

    public static void pointerDragged(int x, int y) {
        queueEvent(POINTER_DRAGGED, x, y);
    }

    private static void applyDisplay() {
        if (display != null) {
            display.applyPending();
        }
    }

    // ---- commands (TextBox / Form soft buttons pressed in the host UI) -------------------
    private static Displayable[] cmdTarget = new Displayable[8];
    private static Command[] cmdCommand = new Command[8];
    private static int cmdCount;

    public static void queueCommand(Displayable d, Command c) {
        if (cmdCount == cmdTarget.length || d == null || c == null) {
            return;
        }
        cmdTarget[cmdCount] = d;
        cmdCommand[cmdCount] = c;
        cmdCount++;
    }

    private static void dispatchCommands() {
        int n = cmdCount;
        for (int i = 0; i < n; i++) {
            Displayable d = cmdTarget[i];
            Command c = cmdCommand[i];
            cmdTarget[i] = null;
            cmdCommand[i] = null;
            if (display == null || display.getShown() != d) {
                continue;
            }
            try {
                d.fireCommand(c);
            } catch (Exception e) {
                logException("command", e);
            }
        }
        cmdCount = 0;
    }

    private static void dispatchEvents() {
        int n = evCount;
        if (n == 0) {
            return;
        }
        int[] t = new int[n];
        int[] a = new int[n];
        int[] b = new int[n];
        System.arraycopy(evType, 0, t, 0, n);
        System.arraycopy(evA, 0, a, 0, n);
        System.arraycopy(evB, 0, b, 0, n);
        evCount = 0;
        for (int i = 0; i < n; i++) {
            Displayable cur = display != null ? display.getShown() : null;
            if (!(cur instanceof Canvas)) {
                continue;
            }
            Canvas c = (Canvas) cur;
            try {
                c.dispatchEvent(t[i], a[i], b[i]);
            } catch (Exception e) {
                logException("input", e);
            }
        }
    }

    // ---- frame ----------------------------------------------------------------------------

    /** Advances the emulation: input, due tasks, then painting. Call once per host frame. */
    public static void tick() {
        if (inTick || paused || midlet == null) {
            return;
        }
        inTick = true;
        try {
            frameTime = now();
            applyDisplay();
            dispatchCommands();
            dispatchEvents();
            runDueTasks();
            Media.poll();
            applyDisplay();
            paintIfNeeded();
        } finally {
            inTick = false;
        }
    }

    private static void runDueTasks() {
        // snapshot so tasks added during this tick wait for the next one
        int n = taskCount;
        Runnable[] run = new Runnable[n];
        System.arraycopy(taskRun, 0, run, 0, n);
        for (int i = 0; i < n; i++) {
            Runnable r = run[i];
            int idx = indexOfTask(r, i);
            if (idx < 0) {
                continue; // cancelled meanwhile
            }
            long due = taskDue[idx];
            if (frameTime < due) {
                continue;
            }
            int interval = taskInterval[idx];
            if (interval > 0) {
                long next = due + interval;
                if (next < frameTime - interval * 4) {
                    next = frameTime; // fell far behind (e.g. app was suspended): do not fast-forward
                }
                taskDue[idx] = next;
            } else {
                removeTask(idx);
            }
            try {
                r.run();
            } catch (Exception e) {
                logException("task", e);
            }
            if (exitRequested || midlet == null) {
                return;
            }
        }
    }

    private static int indexOfTask(Runnable r, int hint) {
        if (hint < taskCount && taskRun[hint] == r) {
            return hint;
        }
        for (int i = 0; i < taskCount; i++) {
            if (taskRun[i] == r) {
                return i;
            }
        }
        return -1;
    }

    private static void paintIfNeeded() {
        Displayable cur = display != null ? display.getShown() : null;
        if (cur instanceof Canvas) {
            try {
                ((Canvas) cur).serviceRepaints();
            } catch (Exception e) {
                // like the phones' event thread: report it and keep the MIDlet running
                logException("paint", e);
            }
        }
    }

    private static String lastException;
    private static int lastExceptionRepeats;

    /** Logs an exception the emulation caught; an exception repeating every frame is logged sparsely. */
    public static void logException(String where, Exception e) {
        String text = "[" + where + "] " + e.toString();
        if (text.equals(lastException)) {
            lastExceptionRepeats++;
            if ((lastExceptionRepeats & (lastExceptionRepeats - 1)) != 0) {
                return; // log repeats 1, 2, 4, 8, ... only
            }
            text = text + " (repeated " + lastExceptionRepeats + " times)";
        } else {
            lastException = text;
            lastExceptionRepeats = 0;
        }
        log(text);
        e.printStackTrace();
    }
}
