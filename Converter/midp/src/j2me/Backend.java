package j2me;

/**
 * Platform services the MIDP emulation needs from its host (Unity in production,
 * a headless desktop harness for tests). Everything else in the emulation is pure code.
 */
public interface Backend {
    /** Returns the bytes of a JAR resource ("/audio/menu.mid"), or null when it does not exist. */
    byte[] getResource(String path);

    /** Wall clock in milliseconds (used for System.currentTimeMillis()). */
    long currentTimeMillis();

    /** System.getProperty / MIDlet.getAppProperty. May return null. */
    String getProperty(String key);

    String getAppProperty(String key);

    /** Persistent storage for one record store (already serialized). Null when missing. */
    byte[] loadStore(String name);

    void saveStore(String name, byte[] data);

    void deleteStore(String name);

    String[] listStores();

    /** Audio: returns an opaque handle or null when the clip is unavailable. */
    Object audioOpen(String resourcePath, String contentType);

    /** loopCount follows MIDP: -1 = forever, n = play n times. */
    void audioStart(Object handle, int loopCount);

    void audioStop(Object handle);

    void audioClose(Object handle);

    /** level 0..100 */
    void audioVolume(Object handle, int level, boolean muted);

    /** Tempo/rate multiplier, 1.0 = normal. */
    void audioRate(Object handle, int ratePermille);

    boolean audioIsPlaying(Object handle);

    /** Duration in microseconds, -1 when unknown. */
    long audioDuration(Object handle);

    long audioPosition(Object handle);

    void audioSetPosition(Object handle, long micros);

    /** MIDlet.platformRequest: open a URL. Returns true when the MIDlet must exit first (never for us). */
    boolean platformRequest(String url);

    /** MIDlet.notifyDestroyed */
    void exitRequested();

    void vibrate(int millis);

    void log(String message);
}
