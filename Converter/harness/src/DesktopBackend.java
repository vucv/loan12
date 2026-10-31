import j2me.Backend;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

/** Headless backend for running the port on a desktop JVM (tests / screenshots). */
public class DesktopBackend implements Backend {
    public long now = 1_400_000_000_000L;
    final File resRoot;
    final File j2meRoot;
    public final Map<String, byte[]> stores = new TreeMap<>();
    public final List<String> events = new ArrayList<>();
    public boolean exited;
    static final Map<String, Double> DUR = new HashMap<>();

    static {
        String[][] d = {{"intro", "25.25"}, {"menu", "50"}, {"m", "53.3"}, {"victory", "32"}, {"win", "6.27"},
                {"lose", "5.6"}, {"attack", "0.76"}, {"explodechess", "0.38"}, {"fire1", "1.34"}, {"fire2", "2.06"},
                {"fire3", "1.59"}, {"ice1", "2.08"}, {"ice2", "2.58"}, {"ice3", "2.04"}, {"lightning1", "2.1"},
                {"lightning2", "1.86"}, {"lightning3", "2.1"}, {"lvu", "2.04"}, {"useitem", "2.15"}, {"vs", "1.65"}};
        for (String[] e : d) DUR.put(e[0], Double.parseDouble(e[1]));
    }

    class Clip {
        String name;
        long start;
        int loops;
        boolean playing;
        long durUs;
    }

    public DesktopBackend(File resRoot, File j2meRoot) {
        this.resRoot = resRoot;
        this.j2meRoot = j2meRoot;
    }

    public byte[] getResource(String path) {
        try {
            File f = path.startsWith("/j2me/") ? new File(j2meRoot, path.substring(6)) : new File(resRoot, path);
            if (!f.isFile()) {
                System.out.println("[res] missing " + path);
                return null;
            }
            return Files.readAllBytes(f.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    public long currentTimeMillis() {
        return now;
    }

    public String getProperty(String key) {
        if (key.equals("microedition.platform")) return "J2ME-Unity";
        return null;
    }

    public String getAppProperty(String key) {
        return null;
    }

    public byte[] loadStore(String name) {
        byte[] b = stores.get(name);
        return b == null ? null : b.clone();
    }

    public void saveStore(String name, byte[] data) {
        stores.put(name, data.clone());
    }

    public void deleteStore(String name) {
        stores.remove(name);
    }

    public String[] listStores() {
        return stores.keySet().toArray(new String[0]);
    }

    public Object audioOpen(String resourcePath, String contentType) {
        Clip c = new Clip();
        c.name = resourcePath.substring(resourcePath.lastIndexOf('/') + 1, resourcePath.lastIndexOf('.'));
        Double d = DUR.get(c.name);
        c.durUs = (long) ((d == null ? 1.0 : d) * 1e6);
        events.add("audioOpen " + c.name);
        return c;
    }

    public void audioStart(Object h, int loopCount) {
        Clip c = (Clip) h;
        c.start = now;
        c.loops = loopCount;
        c.playing = true;
        events.add("audioStart " + c.name + " loops=" + loopCount);
    }

    public void audioStop(Object h) {
        ((Clip) h).playing = false;
    }

    public void audioClose(Object h) {
        ((Clip) h).playing = false;
    }

    public void audioVolume(Object h, int level, boolean muted) {
    }

    public void audioRate(Object h, int ratePermille) {
        if (ratePermille != 1000) events.add("audioRate " + ((Clip) h).name + " " + ratePermille);
    }

    public boolean audioIsPlaying(Object h) {
        Clip c = (Clip) h;
        if (!c.playing) return false;
        if (c.loops < 0) return true;
        long el = (now - c.start) * 1000;
        if (el >= c.durUs * c.loops) {
            c.playing = false;
            return false;
        }
        return true;
    }

    public long audioDuration(Object h) {
        return ((Clip) h).durUs;
    }

    public long audioPosition(Object h) {
        Clip c = (Clip) h;
        if (!c.playing) return 0;
        return ((now - c.start) * 1000) % c.durUs;
    }

    public void audioSetPosition(Object h, long micros) {
    }

    public boolean platformRequest(String url) {
        events.add("platformRequest " + url);
        return false;
    }

    public void exitRequested() {
        exited = true;
        events.add("exit");
    }

    public void vibrate(int millis) {
    }

    public void log(String message) {
        System.out.println("[log] " + message);
    }
}
