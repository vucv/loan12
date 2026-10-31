package j2me;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/** InputStream over a JAR resource that remembers its path (so Manager.createPlayer can find the clip). */
public class ResourceStream extends ByteArrayInputStream {
    private final String path;

    public ResourceStream(String path, byte[] data) {
        super(data);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public byte[] remainingBytes() {
        int n = available();
        byte[] r = new byte[n];
        read(r, 0, n);
        return r;
    }

    public static byte[] readAll(InputStream in) throws IOException {
        byte[] buf = new byte[4096];
        byte[] out = new byte[0];
        int total = 0;
        while (true) {
            int r = in.read(buf, 0, buf.length);
            if (r < 0) {
                break;
            }
            byte[] n = new byte[total + r];
            System.arraycopy(out, 0, n, 0, total);
            System.arraycopy(buf, 0, n, total, r);
            out = n;
            total += r;
        }
        return out;
    }
}
