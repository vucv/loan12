package j2me;

import java.io.InputStream;

/** Replacement for Class.getResourceAsStream (the port rewrites those calls to Res.open). */
public final class Res {
    private Res() {
    }

    public static InputStream open(String path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        byte[] b = Host.backend.getResource(path);
        if (b == null) {
            return null;
        }
        return new ResourceStream(path, b);
    }
}
