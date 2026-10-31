package javax.microedition.lcdui;

import j2me.Host;

import javax.microedition.midlet.MIDlet;

public class Display {
    public static final int LIST_ELEMENT = 1;
    public static final int CHOICE_GROUP_ELEMENT = 2;
    public static final int ALERT = 3;
    public static final int COLOR_BACKGROUND = 0;
    public static final int COLOR_FOREGROUND = 1;
    public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
    public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
    public static final int COLOR_BORDER = 4;
    public static final int COLOR_HIGHLIGHTED_BORDER = 5;

    private Displayable current;
    private Displayable pending;
    private boolean hasPending;

    private Display() {
    }

    public static Display getDisplay(MIDlet m) {
        if (Host.display == null) {
            Host.display = new Display();
        }
        return Host.display;
    }

    /** What the game asked to show (the switch itself happens on the next tick, like on a phone). */
    public Displayable getCurrent() {
        return hasPending ? pending : current;
    }

    /** What is on screen right now. */
    public Displayable getShown() {
        return current;
    }

    public void setCurrent(Displayable next) {
        if (next == null) {
            return;
        }
        pending = next;
        hasPending = true;
    }

    public void setCurrent(Alert alert, Displayable nextDisplayable) {
        setCurrent(nextDisplayable);
    }

    /** Called by the host at the start of a tick. */
    public void applyPending() {
        if (!hasPending) {
            return;
        }
        Displayable next = pending;
        pending = null;
        hasPending = false;
        if (next == current) {
            return;
        }
        Displayable old = current;
        current = next;
        if (old != null) {
            old.callHideNotify();
        }
        next.callShowNotify();
        Host.markScreenDirty();
    }

    public void callSerially(Runnable r) {
        Host.post(r);
    }

    public boolean vibrate(int duration) {
        if (Host.backend != null) {
            Host.backend.vibrate(duration);
        }
        return true;
    }

    public boolean flashBacklight(int duration) {
        return false;
    }

    public boolean isColor() {
        return true;
    }

    public int numColors() {
        return 1 << 24;
    }

    public int numAlphaLevels() {
        return 256;
    }

    public int getColor(int colorSpecifier) {
        return colorSpecifier == COLOR_BACKGROUND ? 0xffffff : 0;
    }
}
