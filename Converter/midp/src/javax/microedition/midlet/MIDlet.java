package javax.microedition.midlet;

import j2me.Host;

import javax.microedition.io.ConnectionNotFoundException;

public abstract class MIDlet {
    protected MIDlet() {
        Host.midlet = this;
    }

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final void notifyDestroyed() {
        Host.exitRequested = true;
        if (Host.backend != null) {
            Host.backend.exitRequested();
        }
    }

    public final void notifyPaused() {
    }

    public final void resumeRequest() {
    }

    public final String getAppProperty(String key) {
        return Host.backend != null ? Host.backend.getAppProperty(key) : null;
    }

    public final boolean platformRequest(String url) throws ConnectionNotFoundException {
        if (Host.backend == null) {
            throw new ConnectionNotFoundException("no platform");
        }
        return Host.backend.platformRequest(url);
    }

    public final int checkPermission(String permission) {
        return 1;
    }

    // ---- port runtime hooks ------------------------------------------------------------

    public void callStartApp() throws MIDletStateChangeException {
        startApp();
    }

    public void callPauseApp() {
        pauseApp();
    }

    public void callDestroyApp(boolean unconditional) throws MIDletStateChangeException {
        destroyApp(unconditional);
    }
}
