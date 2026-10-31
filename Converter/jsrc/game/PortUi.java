package game;

import javax.microedition.lcdui.Image;

/** PORT-UI: state of the battle for the Unity touch interface, and its queued actions. */
public final class PortUi {
    public static final int SKILL = 1;
    public static final int ITEM = 2;
    public static final int PAUSE = 3;
    private static int reqKind;
    private static int reqArg;

    private PortUi() {
    }

    /** the battle object while the battle screen is the current screen, else null */
    public static bu battle() {
        cz c = cz.a;
        if ((c == null) || (c.e == null)) {
            return null;
        }
        if (c.e.b instanceof com.mic.smsgame.suquan.ab) {
            return ((com.mic.smsgame.suquan.ab) c.e.b).portBattle();
        }
        return null;
    }

    /** the plain battle is on screen: no dialog on top, no screen transition, not the tutorial */
    public static boolean battleView() {
        bu b = battle();
        if ((b == null) || (b.portState() == null)) {
            return false;
        }
        bv v = cz.a.e;
        if (v.d != null) {
            return false;
        }
        if (v.l && (v.i != null)) {
            return false;
        }
        return !b.portTraining();
    }

    static bn fighter(int side) {
        bu b = battle();
        if ((b == null) || (b.portState() == null)) {
            return null;
        }
        return b.portState().b(side);
    }

    /** side 0 = player, 1 = enemy */
    public static String name(int side) {
        bn f = fighter(side);
        return f == null ? "" : u.a(f.b());
    }

    /** index of the 30x30 portrait in /avatar (10 per row) */
    public static int portrait(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.b();
    }

    public static int level(int side) {
        c f = side == 0 ? dd.e : dd.f;
        return f == null ? 0 : f.e;
    }

    public static int hp(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.r();
    }

    public static int maxHp(int side) {
        bn f = fighter(side);
        return f == null ? 1 : Math.max(1, f.s());
    }

    public static int mana(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.o();
    }

    public static int maxMana(int side) {
        bn f = fighter(side);
        return f == null ? 1 : Math.max(1, f.n());
    }

    /** turns left of an active item effect (type 0 sword, 3 shield ...) on a fighter */
    public static int effect(int side, int type) {
        bn f = fighter(side);
        if ((f == null) || (type < 0) || (type >= f.a.length)) {
            return 0;
        }
        return f.a[type].b;
    }

    /** 0 = the player, 1 = the enemy */
    public static int turn() {
        bu b = battle();
        return b == null ? -1 : b.portState().a();
    }

    /** the board waits for the player's move */
    public static boolean waiting() {
        bu b = battle();
        return (b != null) && b.portWaiting();
    }

    public static int skillCount() {
        bn f = fighter(0);
        return f == null ? 0 : f.d().length;
    }

    public static String skillName(int k) {
        bn f = fighter(0);
        return f == null ? "" : com.mic.smsgame.suquan.h.a[f.d()[k]];
    }

    /** mana the k-th skill costs (the game: max mana * (k + 1) / 3) */
    public static int skillCost(int k) {
        bn f = fighter(0);
        return f == null ? 0 : f.n() * (k + 1) / 3;
    }

    /** number of skills the player can afford now (the first ones) */
    public static int skillsUsable() {
        bn f = fighter(0);
        if ((f == null) || (f.d().length < 1)) {
            return 0;
        }
        return f.o() * 3 / f.n();
    }

    public static Image skillIcon(int k) {
        Image[] icons = bs.a().i;
        return (icons == null) || (k >= icons.length) ? null : icons[k];
    }

    /** how many of an item (shop index 0..5) the player can still use in this battle */
    public static int itemCount(int type) {
        bu b = battle();
        if ((b == null) || (type == 2)) {
            return 0;
        }
        if ((type == 1) && b.portGinsengUsed()) {
            return 0;
        }
        dx[] arr = b.portState().a;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].a == type) {
                return arr[i].b;
            }
        }
        return 0;
    }

    /** an item was already used in this turn (one per turn) */
    public static boolean itemUsed() {
        bu b = battle();
        return (b != null) && b.portItemUsed();
    }

    /** the board on the 240x320 screen: x, y, w, h */
    public static int board(int i) {
        bu b = battle();
        dl r = b == null ? null : b.h();
        if (r == null) {
            return i < 2 ? 36 + (i == 1 ? 18 : 0) : 168;
        }
        switch (i) {
            case 0:
                return r.a;
            case 1:
                return r.b;
            case 2:
                return r.c > 0 ? r.c : 168;
            default:
                return r.d > 0 ? r.d : 168;
        }
    }

    public static Image image(String name) {
        return az.b(name);
    }

    /** queue an action (SKILL k, ITEM type, PAUSE) for the next frame of the game */
    public static void request(int kind, int arg) {
        reqKind = kind;
        reqArg = arg;
    }

    /** runs the queued action; called by the screen manager at the start of a frame */
    public static void process() {
        if (reqKind == 0) {
            return;
        }
        int kind = reqKind;
        int arg = reqArg;
        reqKind = 0;
        bu b = battle();
        if ((b == null) || !battleView()) {
            return;
        }
        switch (kind) {
            case SKILL:
                b.portUseSkill(arg);
                break;
            case ITEM:
                b.portUseItem(arg);
                break;
            case PAUSE:
                b.g();
                break;
        }
    }
}
