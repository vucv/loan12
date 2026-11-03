package game;

/**
 * PORT-UI: the map and its tabs (plan/thiet-ke-ban-do.md 8) drawn by Unity over the game's map screen
 * (suquan.aa, which keeps walking, encounters, quests and saving). Read-only state and queued actions,
 * like PortUi / PortScreens.
 */
public final class PortMap {
    public static final int GO = 1;       // arg: node. Walk there along the open roads
    public static final int STOP = 2;     // stop at the end of the road being walked
    public static final int START = 3;    // run the "just arrived" step again at the node the hero is on
    public static final int MENU = 4;     // save the map and go to the menu
    public static final int TUTORIAL_OFF = 5;

    private static int reqKind;
    private static int reqArg;

    private PortMap() {
    }

    static com.mic.smsgame.suquan.aa map() {
        ar s = PortUi.screen();
        return s instanceof com.mic.smsgame.suquan.aa ? (com.mic.smsgame.suquan.aa) s : null;
    }

    /** the map waits for the player (standing or walking), no dialogue, VS box or popup on it */
    public static boolean free() {
        com.mic.smsgame.suquan.aa m = map();
        if ((m == null) || (PortUi.dialog() != null)) {
            return false;
        }
        int f = m.portState();
        return (f == 0) || (f == 1) || (f == 7) || (f == 8);
    }

    public static int state() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? -1 : m.portState();
    }

    // ---- nodes and roads ----------------------------------------------------------------------

    public static int nodeCount() {
        return bq.a.length;
    }

    public static int nodeX(int n) {
        return bq.b[n][0];
    }

    public static int nodeY(int n) {
        return bq.b[n][1];
    }

    /** 0 city, 1 point on a road (post, guard station, fork) */
    public static int nodeType(int n) {
        return bq.b[n][2];
    }

    public static String nodeName(int n) {
        String s = bq.a[n];
        return s == null ? "" : s;
    }

    public static boolean nodeOpen(int n) {
        com.mic.smsgame.suquan.aa m = map();
        return (m != null) && m.portNodeOpen(n);
    }

    public static int roadCount() {
        return bq.d.length;
    }

    public static int roadA(int r) {
        return bq.d[r][0];
    }

    public static int roadB(int r) {
        return bq.d[r][1];
    }

    public static boolean roadOpen(int r) {
        com.mic.smsgame.suquan.aa m = map();
        return (m != null) && m.portRoadOpen(r);
    }

    /** 0 nothing, 1 main quest, 2 event, 3 guards, 4 arena */
    public static int nodeContent(int n) {
        com.mic.smsgame.suquan.aa m = map();
        if ((m == null) || !m.portNodeOpen(n)) {
            return 0;
        }
        if (m.portNodeQuest(n) >= 0) {
            return 1;
        }
        if (m.portNodeEvent(n) >= 0) {
            return 2;
        }
        if (m.portGuard(n) >= 0) {
            return 3;
        }
        if ((n == 24) && (m.portQuest() > 5)) {
            return 4;
        }
        return 0;
    }

    public static int nodeEvent(int n) {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? -1 : m.portNodeEvent(n);
    }

    // ---- the hero -----------------------------------------------------------------------------

    public static int playerNode() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? 0 : m.portNode();
    }

    public static int playerX() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? 0 : m.portX();
    }

    public static int playerY() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? 0 : m.portY();
    }

    public static boolean walking() {
        com.mic.smsgame.suquan.aa m = map();
        return (m != null) && m.portWalking();
    }

    /** node walked to, or -1 */
    public static int walkTarget() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? -1 : m.portTarget();
    }

    /** roads on the shortest open way to node n; 0 when standing there, -1 when it cannot be reached */
    public static int roadsTo(int n) {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? -1 : m.portRoute(n, null);
    }

    /** nodes of the shortest way to n (the first is the hero's node), into out; their number */
    public static int route(int n, int[] out) {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? 0 : m.portRoute(n, out);
    }

    // ---- quests -------------------------------------------------------------------------------

    public static int questIndex() {
        com.mic.smsgame.suquan.aa m = map();
        return m == null ? 0 : m.portQuest();
    }

    public static int questCount() {
        return dd.e();
    }

    /** 0 dialogue, 1 tutorial battle, 2 battle */
    public static int questType(int q) {
        return dd.d(q);
    }

    public static int questNode(int q) {
        return dd.e(q);
    }

    /** portrait of the enemy, -1 for a dialogue */
    public static int questEnemy(int q) {
        c x = dd.h(q);
        return x == null ? -1 : x.a;
    }

    public static int questEnemyLevel(int q) {
        c x = dd.h(q);
        return x == null ? 0 : x.e;
    }

    public static int questExp(int q) {
        return dd.i(q);
    }

    public static int questGold(int q) {
        return dd.j(q);
    }

    /** 0 none, 1 a relic box, 2 the last relic box */
    public static int questRelic(int q) {
        return dd.f(q) ? 1 : (dd.g(q) ? 2 : 0);
    }

    /** portrait of the first speaker of the quest's opening dialogue (player: own hero), -1 none */
    public static int questSpeaker(int q) {
        String[][] t = (q >= 0) && (q < ea.b.length) ? ea.b[q] : null;
        return speaker(t);
    }

    static int speaker(String[][] t) {
        if ((t == null) || (t.length == 0) || (t[0] == null)) {
            return -1;
        }
        for (int i = 0; i < t.length; i++) {
            if ((t[i] != null) && (t[i][0] != null)) {
                int v = Integer.parseInt(t[i][0]);
                if (v >= 6) {
                    return v;
                }
            }
        }
        return dd.d();
    }

    /** nodes the quest opens (dd.m), or null */
    public static int[] questOpens(int q) {
        return dd.l(q);
    }

    // events (dd.n): 8, done once each
    public static int eventCount() {
        return dd.f();
    }

    public static int eventNode(int e) {
        return dd.q(e);
    }

    /** 0 dialogue, 2 battle */
    public static int eventType(int e) {
        return dd.p(e);
    }

    public static int eventEnemy(int e) {
        c x = dd.s(e);
        return x == null ? -1 : x.a;
    }

    public static int eventEnemyLevel(int e) {
        c x = dd.s(e);
        return x == null ? 0 : x.e;
    }

    public static int eventAttack(int e) {
        return dd.n(e);
    }

    public static int eventHp(int e) {
        return dd.o(e);
    }

    /** 0 not open yet, 1 can be done, 2 done (plan 6.1) */
    public static int eventState(int e) {
        com.mic.smsgame.suquan.aa m = map();
        int n = dd.q(e);
        if ((m == null) || !m.portNodeOpen(n)) {
            return 0;
        }
        return m.portNodeEvent(n) == e ? 1 : 2;
    }

    // guards (dd.p): nodes with soldiers, gone once beaten
    public static boolean guardNode(int n) {
        return dd.z(n) >= 0;
    }

    /** 0 not open yet, 1 still there, 2 beaten */
    public static int guardState(int n) {
        com.mic.smsgame.suquan.aa m = map();
        if ((m == null) || !m.portNodeOpen(n)) {
            return 0;
        }
        return m.portGuard(n) >= 0 ? 1 : 2;
    }

    /** portrait of the soldiers of node n (75 = soldiers of the player's level) */
    public static int guardEnemy(int n) {
        c x = dd.a(n, Math.max(1, heroLevel()));
        return x == null ? -1 : x.a;
    }

    public static int guardLevel(int n) {
        c x = dd.a(n, Math.max(1, heroLevel()));
        return x == null ? 0 : x.e;
    }

    public static boolean guardScales(int n) {
        return dd.z(n) == 75;
    }

    public static boolean arenaOpen(int i) {
        com.mic.smsgame.suquan.aa m = map();
        return (m != null) && m.portArena(i);
    }

    // ---- the hero's sheet (bag tab) -------------------------------------------------------------

    public static int hero() {
        return dd.e == null ? dd.d() : dd.e.a;
    }

    public static String heroName() {
        return dd.c();
    }

    public static int heroLevel() {
        return dd.e == null ? 1 : dd.e.e;
    }

    public static int heroMaxHp() {
        return dd.e == null ? 0 : dd.e.b;
    }

    public static int heroAttack() {
        return dd.e == null ? 0 : dd.e.d;
    }

    public static int heroBonusHp() {
        return dd.e == null ? 0 : dd.e.g;
    }

    public static int heroBonusAttack() {
        return dd.e == null ? 0 : dd.e.h;
    }

    public static int heroSkill(int k) {
        return (dd.e == null) || (dd.e.f == null) || (k >= dd.e.f.length) ? 0 : dd.e.f[k];
    }

    public static int heroSkillCount() {
        return (dd.e == null) || (dd.e.f == null) ? 0 : dd.e.f.length;
    }

    public static int heroExp() {
        return dd.b();
    }

    /** experience needed to reach level L */
    public static int heroExpFor(int level) {
        return dd.u(level);
    }

    /** relic i (0..3) collected */
    public static boolean relicOwned(int i) {
        int[] r = bg.s();
        for (int j = 0; j < r.length; j++) {
            if (r[j] == i + 1) {
                return true;
            }
        }
        return false;
    }

    public static String relicName(int i) {
        return com.mic.smsgame.suquan.h.g[i];
    }

    // ---- shop ---------------------------------------------------------------------------------

    private static boolean lastBuyOk;

    /** buys packs of an item with gold; nothing when the gold is not enough for all of them */
    public static boolean buy(int type, int packs) {
        int p = PortUi.price(type) * packs;
        if ((packs <= 0) || (PortUi.price(type) <= 0) || (bg.f(2) < p)) {
            lastBuyOk = false;
            return false;
        }
        bg.b(2, p);
        bg.a(type, PortUi.packSize(type) * packs, 6);
        lastBuyOk = true;
        return true;
    }

    public static boolean lastBuyOk() {
        return lastBuyOk;
    }

    // ---- actions ------------------------------------------------------------------------------

    public static void request(int kind, int arg) {
        reqKind = kind;
        reqArg = arg;
    }

    static void process() {
        if (reqKind == 0) {
            return;
        }
        int kind = reqKind;
        int arg = reqArg;
        reqKind = 0;
        com.mic.smsgame.suquan.aa m = map();
        if ((m == null) || (PortUi.dialog() != null)) {
            return;
        }
        switch (kind) {
            case GO:
                m.portGo(arg);
                break;
            case STOP:
                m.portStop();
                break;
            case START:
                m.portArrive();
                break;
            case MENU:
                m.b(0, 1); // the game's "back to the menu" (saves the map)
                break;
        }
    }
}
