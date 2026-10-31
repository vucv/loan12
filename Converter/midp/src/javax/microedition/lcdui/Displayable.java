package javax.microedition.lcdui;

import j2me.Host;

public abstract class Displayable {
    private Command[] commands = new Command[4];
    private int commandCount;
    CommandListener listener;
    String title;
    boolean shown;

    Displayable() {
    }

    public void addCommand(Command cmd) {
        if (cmd == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < commandCount; i++) {
            if (commands[i] == cmd) {
                return;
            }
        }
        if (commandCount == commands.length) {
            Command[] n = new Command[commandCount * 2];
            System.arraycopy(commands, 0, n, 0, commandCount);
            commands = n;
        }
        commands[commandCount++] = cmd;
    }

    public void removeCommand(Command cmd) {
        for (int i = 0; i < commandCount; i++) {
            if (commands[i] == cmd) {
                commandCount--;
                for (int k = i; k < commandCount; k++) {
                    commands[k] = commands[k + 1];
                }
                commands[commandCount] = null;
                return;
            }
        }
    }

    public void setCommandListener(CommandListener l) {
        listener = l;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        title = s;
    }

    public boolean isShown() {
        return shown && !Host.paused;
    }

    public int getWidth() {
        return Host.screenWidth;
    }

    public int getHeight() {
        return Host.screenHeight;
    }

    protected void sizeChanged(int w, int h) {
    }

    // ---- port runtime hooks ------------------------------------------------------------

    public int getCommandCount() {
        return commandCount;
    }

    public Command getCommand(int i) {
        return commands[i];
    }

    /** Invokes the CommandListener (call from inside Host.tick, e.g. via Host.queueCommand). */
    public void fireCommand(Command c) {
        if (listener != null) {
            listener.commandAction(c, this);
        }
    }

    public void callShowNotify() {
        shown = true;
    }

    public void callHideNotify() {
        shown = false;
    }
}
