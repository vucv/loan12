package j2me;

/** Tiny MIDI header reader (only what TempoControl needs). */
public final class Midi {
    private Midi() {
    }

    /** First tempo meta event as milli-beats per minute, or 120000. */
    public static int initialTempo(byte[] d) {
        if (d == null || d.length < 22 || d[0] != 'M' || d[1] != 'T' || d[2] != 'h' || d[3] != 'd') {
            return 120000;
        }
        for (int i = 14; i + 5 < d.length; i++) {
            if ((d[i] & 0xff) == 0xff && d[i + 1] == 0x51 && d[i + 2] == 0x03) {
                int mpq = ((d[i + 3] & 0xff) << 16) | ((d[i + 4] & 0xff) << 8) | (d[i + 5] & 0xff);
                if (mpq > 0) {
                    return (int) (60000000000L / mpq);
                }
            }
        }
        return 120000;
    }
}
