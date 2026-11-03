package game;

import javax.microedition.rms.RecordStore;

/**
 * PORT-UI, steps 2 and 3 of the touch redesign (plan/thiet-ke-giao-dien.md): the menu, the start-up
 * sound question, the character slots (plan/thiet-ke-nhieu-nhan-vat.md) and the hero choice.
 * Like PortUi: read-only state for the Unity views, and actions queued for the start of the game's
 * next frame (PortUi.process calls process()).
 *
 * Character slots: slot 0 keeps the game's own record stores "16b" / "161b" (saves of earlier
 * versions stay where they are), slots 1 and 2 use "s1_16b" ... The records shared by every character
 * (lives, volume, gold and items) live in "acct_16b" / "acct_161b" (bg.acct, bg.acctC).
 */
public final class PortScreens {
    public static final int SOUND_SKIP = 1;    // the start-up sound question: answer it (sound on)
    public static final int MENU_CONTINUE = 2; // arg: slot. Loads the slot and goes on like "Chơi tiếp"
    public static final int MENU_NEW = 3;      // arg: slot. Empties the slot and starts a new game in it
    public static final int HERO_OK = 4;       // hero screen: "Chọn" (asks for the name)
    public static final int HERO_BACK = 5;     // hero screen: "Trở về"
    public static final int EXIT = 6;          // quit the game
    public static final int SKIP = 7;          // story screen: "Bỏ qua"
    public static final int COACH_NEXT = 9;    // tutorial battle: go on after the coach's message
    public static final int FORFEIT = 8;       // battle pause: close the pause and give the battle up (a defeat)

    public static final int MAX_SLOTS = 3;

    /** the Unity touch interface draws the screens (set by the host; the test harness leaves it off) */
    public static boolean touch;

    /** character slot whose record stores bg.b / bg.c are */
    public static int slot;
    private static int reqKind;
    private static int reqArg;

    private PortScreens() {
    }

    // ---- screens ------------------------------------------------------------------------------

    /**
     * 0 other, 1 the sound question, 2 menu, 3 hero choice, 4 map, 5 story (intro / ending), 6 shop,
     * 7 battle, 8 splash
     */
    public static int screenKind() {
        ar s = PortUi.screen();
        if (s == null) {
            return 0;
        }
        if (s instanceof v) {
            return 1;
        }
        if (s instanceof com.mic.smsgame.suquan.ac) {
            return 2;
        }
        if (s instanceof com.mic.smsgame.suquan.z) {
            return 3;
        }
        if (s instanceof com.mic.smsgame.suquan.aa) {
            return 4;
        }
        if (s instanceof df) {
            return 5;
        }
        if (s instanceof com.mic.smsgame.suquan.q) {
            return 6;
        }
        if (s instanceof com.mic.smsgame.suquan.ab) {
            return 7;
        }
        if (s instanceof com.mic.smsgame.suquan.l) {
            return 8;
        }
        return 0;
    }

    public static boolean hasDialog() {
        return PortUi.dialog() != null;
    }

    /** a screen switch is under way, or an action waits for the next frame */
    public static boolean busy() {
        return PortUi.transition() || (reqKind != 0);
    }

    // ---- shared account: lives and gold -----------------------------------------------------------

    public static int lives() {
        return bg.m();
    }

    public static int maxLives() {
        return cx.J;
    }

    /** lives up to the maximum (the refill item) */
    public static void refillLives() {
        int n = cx.J - bg.m();
        if (n > 0) {
            bg.c(n);
        }
    }

    // tutorial battle
    public static boolean training() {
        bu b = PortUi.battle();
        return (b != null) && b.portTraining();
    }

    public static String coach() {
        bu b = PortUi.battle();
        return b == null ? null : b.portCoach();
    }

    public static int coachGem() {
        bu b = PortUi.battle();
        return b == null ? -1 : b.portCoachGem();
    }

    public static boolean coachWaits() {
        bu b = PortUi.battle();
        return (b != null) && b.portCoachWaits();
    }

    public static int gold() {
        return bg.f(2);
    }

    /** gold reward (ads, purchases): added to the shared wallet */
    public static void addGold(int n) {
        bg.a(2, n, 6);
    }

    public static void addItem(int type, int n) {
        bg.a(type, n, 6);
    }

    public static void addLives(int n) {
        bg.c(n);
    }

    /** the records shared by every character move from slot 0 to the account stores (once) */
    static void migrateAccount() {
        if (dr.a(bg.acct, 50)) {
            return;
        }
        int[] shared = {4, 5, 6, 7, 8, 11, 13, 14};
        for (int i = 0; i < shared.length; i++) {
            byte[] d = dr.b(bg.b, shared[i]);
            if (d != null) {
                dr.a(bg.acct, shared[i], d);
            }
        }
        byte[] wallet = dr.b(bg.c, 9);
        if (wallet != null) {
            dr.a(bg.acctC, 9, wallet);
            dr.a(bg.acctC);
        }
        dr.a(bg.acct, 50, new byte[]{1});
        dr.a(bg.acct);
    }

    // ---- character slots ----------------------------------------------------------------------

    static String store16(int n) {
        return n == 0 ? "16b" : "s" + n + "_16b";
    }

    static String store161(int n) {
        return n == 0 ? "161b" : "s" + n + "_161b";
    }

    static boolean exists(String name) {
        String[] names = RecordStore.listRecordStores();
        if (names == null) {
            return false;
        }
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                return true;
            }
        }
        return false;
    }

    /** the game reads and writes the character of slot n from now on */
    public static void useSlot(int n) {
        if ((n < 0) || (n >= MAX_SLOTS)) {
            return;
        }
        if ((n == slot) && (bg.b != null)) {
            return;
        }
        bg.b = dr.a(store16(n), 16);
        bg.c = dr.a(store161(n), 161);
        slot = n;
        dd.e = null;
        dd.f = null;
        dd.b = 0;
    }

    /** deletes the character of slot n (its record stores); shared gold, items and lives stay */
    public static void wipeSlot(int n) {
        if ((n < 0) || (n >= MAX_SLOTS)) {
            return;
        }
        l.a(store16(n));
        l.a(store161(n));
        if (n == slot) {
            bg.b = dr.a(store16(n), 16);
            bg.c = dr.a(store161(n), 161);
            dd.e = null;
            dd.f = null;
            dd.b = 0;
        }
    }

    static dr open16(int n) {
        if (n == slot) {
            return bg.b;
        }
        return exists(store16(n)) ? dr.a(store16(n), 16) : null;
    }

    static dr open161(int n) {
        if (n == slot) {
            return bg.c;
        }
        return exists(store161(n)) ? dr.a(store161(n), 161) : null;
    }

    /** the slot holds a created character (a name, or a map save) */
    public static boolean slotUsed(int n) {
        dr s = open16(n);
        return (s != null) && (dr.a(s, 104) || dr.a(s, 100));
    }

    public static String slotName(int n) {
        dr s = open16(n);
        byte[] b = s == null ? null : dr.b(s, 104);
        if (b == null) {
            return "PLAYER";
        }
        try {
            java.io.DataInputStream in = new java.io.DataInputStream(new java.io.ByteArrayInputStream(b));
            String str = in.readUTF();
            in.close();
            return ao.a(str) ? "PLAYER" : str;
        } catch (java.io.IOException e) {
            return "PLAYER";
        }
    }

    public static int slotHero(int n) {
        dr s = open16(n);
        byte[] b = s == null ? null : dr.b(s, 105);
        return (b == null) || (b.length < 4) ? 0 : ai.a(b);
    }

    /** offset of the hero's level in the map save (see aa.w()) */
    static int levelOffset() {
        return 13 + bq.a.length * 4 + bq.d.length + 3 + 16;
    }

    /** level from the map save; 1 before the first map save */
    public static int slotLevel(int n) {
        dr s = open16(n);
        byte[] b = s == null ? null : dr.b(s, 100);
        int at = levelOffset();
        if ((b == null) || (b.length <= at)) {
            return 1;
        }
        return Math.max(1, b[at]);
    }

    /** main quests done (aa.T) from the map save */
    public static int slotQuest(int n) {
        dr s = open16(n);
        byte[] b = s == null ? null : dr.b(s, 100);
        if ((b == null) || (b.length < 13)) {
            return 0;
        }
        return ai.a(b, 9);
    }

    /** relics collected by the character of slot n (161b #1) */
    public static int slotRelics(int n) {
        dr s = open161(n);
        byte[] b = s == null ? null : dr.b(s, 1);
        if (b == null) {
            return 0;
        }
        boolean[] got = new boolean[4];
        for (int i = 0; (i < bg.a) && (i * 4 + 4 <= b.length); i++) {
            int v = ai.a(b, i * 4);
            if ((v > 0) && (v <= 4)) {
                got[v - 1] = true;
            }
        }
        int c = 0;
        for (int i = 0; i < 4; i++) {
            if (got[i]) {
                c++;
            }
        }
        return c;
    }

    // ---- heroes -------------------------------------------------------------------------------

    public static int heroCount() {
        return 6;
    }

    public static String heroName(int h) {
        return u.a(h);
    }

    public static String heroDesc(int h) {
        return com.mic.smsgame.suquan.h.e[h];
    }

    /** 0 fire (Hỏa), 1 lightning (Lôi), 2 water (Thủy) */
    public static int heroElement(int h) {
        c x = dd.t(h);
        return (x == null) || (x.f.length == 0) ? 0 : x.f[0] / 3;
    }

    public static int heroSkill(int h, int k) {
        c x = dd.t(h);
        return (x == null) || (k >= x.f.length) ? 0 : x.f[k];
    }

    public static int heroHp(int h) {
        c x = dd.t(h);
        return x == null ? 0 : x.b;
    }

    public static int heroAttack(int h) {
        c x = dd.t(h);
        return x == null ? 0 : x.d;
    }

    public static String skillTitle(int id) {
        return com.mic.smsgame.suquan.h.a[id];
    }

    /** the game's description of a skill, its damage filled in for an attack value */
    public static String skillText(int id, int attack) {
        String str = com.mic.smsgame.suquan.h.b[id];
        int j = str.indexOf("=");
        if (j >= 0) {
            str = str.substring(0, j) + (attack * PortUi.SK_E[id] / PortUi.SK_F[id]) + str.substring(j + 1);
        }
        return str;
    }

    /** hero highlighted on the game's hero screen */
    public static int heroSelected() {
        ar s = PortUi.screen();
        return s instanceof com.mic.smsgame.suquan.z ? ((com.mic.smsgame.suquan.z) s).portHero() : 0;
    }

    public static void heroSelect(int h) {
        ar s = PortUi.screen();
        if (s instanceof com.mic.smsgame.suquan.z) {
            ((com.mic.smsgame.suquan.z) s).portSelect(h);
        }
    }

    // ---- texts --------------------------------------------------------------------------------

    /** the game's credits (h.f): rows of strings */
    public static int creditCount() {
        return com.mic.smsgame.suquan.h.f.length;
    }

    public static String credit(int i, int j) {
        String[] r = com.mic.smsgame.suquan.h.f[i];
        return (r == null) || (j >= r.length) || (r[j] == null) ? "" : r[j];
    }

    public static int creditLength(int i) {
        String[] r = com.mic.smsgame.suquan.h.f[i];
        return r == null ? 0 : r.length;
    }

    // ---- actions ------------------------------------------------------------------------------

    public static void request(int kind, int arg) {
        reqKind = kind;
        reqArg = arg;
    }

    /** runs the queued action; called by PortUi.process at the start of a frame */
    static void process() {
        PortMap.process();
        if (reqKind == 0) {
            return;
        }
        int kind = reqKind;
        int arg = reqArg;
        reqKind = 0;
        ar s = PortUi.screen();
        switch (kind) {
            case SOUND_SKIP:
                if ((s instanceof v) && (PortUi.dialog() == null)) {
                    ((v) s).portSkip();
                }
                break;
            case MENU_CONTINUE:
                if (!(s instanceof com.mic.smsgame.suquan.ac) || (PortUi.dialog() != null)) {
                    break;
                }
                useSlot(arg);
                cf.a().d();
                if (com.mic.smsgame.suquan.f.e()) {
                    cz.d().e.a(2, 5, false); // a saved battle
                } else {
                    cz.d().e.a(2, 7, false); // the map
                }
                break;
            case MENU_NEW:
                if (!(s instanceof com.mic.smsgame.suquan.ac) || (PortUi.dialog() != null)) {
                    break;
                }
                useSlot(arg);
                wipeSlot(arg);
                cf.a().d();
                if (bg.j()) {
                    bg.k(); // the first game on this phone: its lives
                }
                cz.d().a(new df(true), false);
                break;
            case HERO_OK:
            case HERO_BACK:
                if ((s instanceof com.mic.smsgame.suquan.z) && (PortUi.dialog() == null)) {
                    ((com.mic.smsgame.suquan.z) s).b(0, kind == HERO_OK ? -1 : -2);
                }
                break;
            case SKIP:
                if ((s instanceof df) && (PortUi.dialog() == null)) {
                    s.portPress(2);
                }
                break;
            case FORFEIT: {
                bu b = PortUi.battle();
                if ((b != null) && (PortUi.pauseKind() == 1)) {
                    b.b(0, -2); // the pause's "Không": back to the battle
                    b.portForfeit = true;
                }
                break;
            }
            case COACH_NEXT: {
                bu b = PortUi.battle();
                if (b != null) {
                    b.portCoachNext();
                }
                break;
            }
            case EXIT:
                com.mg.smsgame.main.MGMIDlet.f().d();
                break;
        }
    }
}
