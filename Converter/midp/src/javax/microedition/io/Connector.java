package javax.microedition.io;

import j2me.SmsConnection;

import java.io.IOException;

/** Only sms:// is emulated (messages are accepted but never leave the device). */
public class Connector {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = 3;

    private Connector() {
    }

    public static Connection open(String name) throws IOException {
        return open(name, READ_WRITE, false);
    }

    public static Connection open(String name, int mode) throws IOException {
        return open(name, mode, false);
    }

    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("null URL");
        }
        if (name.startsWith("sms://")) {
            return new SmsConnection(name);
        }
        throw new ConnectionNotFoundException(name);
    }
}
