package j2me;

/** Registry of live players + asynchronous PlayerListener event queue. */
public final class Media {
    private static PlayerImpl[] players = new PlayerImpl[8];
    private static int playerCount;

    private static PlayerImpl[] evPlayer = new PlayerImpl[16];
    private static String[] evName = new String[16];
    private static Object[] evData = new Object[16];
    private static int evCount;

    private Media() {
    }

    static void register(PlayerImpl p) {
        if (playerCount == players.length) {
            PlayerImpl[] n = new PlayerImpl[playerCount * 2];
            System.arraycopy(players, 0, n, 0, playerCount);
            players = n;
        }
        players[playerCount++] = p;
    }

    static void unregister(PlayerImpl p) {
        for (int i = 0; i < playerCount; i++) {
            if (players[i] == p) {
                playerCount--;
                for (int k = i; k < playerCount; k++) {
                    players[k] = players[k + 1];
                }
                players[playerCount] = null;
                return;
            }
        }
    }

    static void postEvent(PlayerImpl p, String name, Object data) {
        if (evCount == evPlayer.length) {
            int n = evCount * 2;
            PlayerImpl[] np = new PlayerImpl[n];
            String[] nn = new String[n];
            Object[] nd = new Object[n];
            System.arraycopy(evPlayer, 0, np, 0, evCount);
            System.arraycopy(evName, 0, nn, 0, evCount);
            System.arraycopy(evData, 0, nd, 0, evCount);
            evPlayer = np;
            evName = nn;
            evData = nd;
        }
        evPlayer[evCount] = p;
        evName[evCount] = name;
        evData[evCount] = data;
        evCount++;
    }

    /** Called once per tick by Host. */
    public static void poll() {
        for (int i = 0; i < playerCount; i++) {
            players[i].poll();
        }
        int n = evCount;
        if (n == 0) {
            return;
        }
        PlayerImpl[] p = new PlayerImpl[n];
        String[] nm = new String[n];
        Object[] d = new Object[n];
        System.arraycopy(evPlayer, 0, p, 0, n);
        System.arraycopy(evName, 0, nm, 0, n);
        System.arraycopy(evData, 0, d, 0, n);
        for (int i = 0; i < n; i++) {
            evPlayer[i] = null;
            evData[i] = null;
        }
        evCount = 0;
        for (int i = 0; i < n; i++) {
            p[i].deliver(nm[i], d[i]);
        }
    }

    public static void pauseAll() {
        for (int i = 0; i < playerCount; i++) {
            players[i].hostPause();
        }
    }

    public static void resumeAll() {
        for (int i = 0; i < playerCount; i++) {
            players[i].hostResume();
        }
    }

    public static void closeAll() {
        while (playerCount > 0) {
            players[playerCount - 1].close();
        }
    }
}
