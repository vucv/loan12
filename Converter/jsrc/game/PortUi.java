package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * PORT-UI: what the Unity touch interface needs from the game, and its queued actions.
 * Screens drawn by Unity: 5 preparing a battle (the VS box on the map), 6 the battle, 7 the result.
 * Actions are queued and run by the screen manager at the start of the game's next frame.
 */
public final class PortUi {
    public static final int SKILL = 1;       // arg: skill slot 0..2
    public static final int ITEM = 2;        // arg: item type (shop index)
    public static final int PAUSE = 3;
    public static final int SCREEN_KEY = 4;  // soft key of the current screen, arg: 0 left, 1 centre, 2 right
    public static final int DIALOG_KEY = 5;  // soft key of the dialog on top
    public static final int RESULT_SKIP = 6; // result panel: stop waiting
    public static final int PAUSE_RESUME = 7; // pause: back to the battle
    public static final int PAUSE_QUIT = 8;   // pause: save the battle and go to the menu (the game's "Có")
    public static final int END_RETRY = 9;    // lost battle: fight again
    public static final int END_BACK = 10;    // lost battle: back to the map
    /** the "Bấm tiếp tục để chơi tiếp" dialog the game opens when it comes back from the background */
    static ae resumeDialog;
    private static int reqKind;
    private static int reqArg;

    /** damage of each skill = attack * E / F (as the game's skill window computes it) */
    static final int[] SK_E = {1, 3, 5, 4, 0, 2, 4, 0, 3};
    static final int[] SK_F = {1, 2, 2, 5, 1, 1, 5, 5, 2};
    /** gold prices of the shop items; Long Thần Kiếm and Nhân Sâm were sold by SMS only (plan: 1500) */
    static final int[] PRICE = {1500, 1500, -1, 1000, 250, 500};

    private PortUi() {
    }

    // ---- screens ------------------------------------------------------------------------------

    static ar screen() {
        cz c = cz.a;
        return (c == null) || (c.e == null) ? null : c.e.b;
    }

    static ae dialog() {
        cz c = cz.a;
        return (c == null) || (c.e == null) ? null : c.e.d;
    }

    public static boolean transition() {
        bv v = cz.a.e;
        return v.l && (v.i != null);
    }

    /** the battle object while the battle screen is the current screen, else null */
    public static bu battle() {
        ar s = screen();
        if (s instanceof com.mic.smsgame.suquan.ab) {
            return ((com.mic.smsgame.suquan.ab) s).portBattle();
        }
        return null;
    }

    /**
     * 6: the battle is on screen (not the tutorial) with no dialog on top. Also while a dialog closes,
     * and while the result dialog opens after the end of the fight (the touch view keeps playing its
     * end animation instead of the classic dialog animation).
     */
    public static boolean battleView() {
        bu b = battle();
        if ((b == null) || (b.portState() == null) || b.portTraining()) {
            return false;
        }
        ae d = dialog();
        if (d == null) {
            return true;
        }
        return transition() && (b.portOutcome() != 0) && (d == b.portEndDialog);
    }

    /** 0 playing (or no battle), 1 won, 2 lost: from the end of the fight until its result closes */
    public static int outcome() {
        bu b = battle();
        return (b == null) || b.portTraining() ? 0 : b.portOutcome();
    }

    /** the pause of a battle: 0 none, 1 the game's "dừng game" dialog, 2 back from the background */
    public static int pauseKind() {
        bu b = battle();
        ae d = dialog();
        if ((b == null) || (d == null) || b.portTraining()) {
            return 0;
        }
        if (d == b.portPauseDialog) {
            return 1;
        }
        return d == resumeDialog ? 2 : 0;
    }

    public static boolean pauseView() {
        return pauseKind() != 0;
    }

    /** 5: the map shows the VS box of a coming battle, ready for "Đánh" / "Rút lui" (also while a dialog closes) */
    public static boolean prepareView() {
        ar s = screen();
        if (!(s instanceof com.mic.smsgame.suquan.aa) || (dialog() != null)) {
            return false;
        }
        return ((com.mic.smsgame.suquan.aa) s).portVsReady();
    }

    /**
     * 7: the result of a battle (won: the reward panel; lost: the game's "mất 1 mạng" dialog) or a quest
     * reward. After a battle it shows once the dialog has opened (the battle view covers the opening).
     */
    public static boolean resultView() {
        ae d = dialog();
        bu b = battle();
        boolean ours = (d instanceof cr) || ((b != null) && (d != null) && (d == b.portEndDialog));
        return ours && ((b == null) || !transition());
    }

    /** experience taken by the last defeat (shown when it is not an arena fight) */
    public static int lossExp() {
        return dd.a >= 0 ? 0 : dd.portLossExp;
    }

    /** the lost battle can be fought again ("Có" of the game's dialog) */
    public static boolean lossRetry() {
        bu b = battle();
        return (b != null) && b.portEndRetry;
    }

    public static boolean arena() {
        return dd.a >= 0;
    }

    // ---- 5: preparing a battle ----------------------------------------------------------------

    static c side(int side) {
        return side == 0 ? dd.e : dd.f;
    }

    public static boolean canRetreat() {
        ar s = screen();
        return (s instanceof com.mic.smsgame.suquan.aa) && ((com.mic.smsgame.suquan.aa) s).portCanRetreat();
    }

    public static String vsName(int side) {
        c f = side(side);
        return f == null ? "" : u.a(f.a);
    }

    public static int vsPortrait(int side) {
        c f = side(side);
        return f == null ? 0 : f.a;
    }

    public static int vsLevel(int side) {
        c f = side(side);
        return f == null ? 0 : f.e;
    }

    public static int vsHp(int side) {
        c f = side(side);
        return f == null ? 0 : f.c;
    }

    public static int vsMaxHp(int side) {
        c f = side(side);
        return f == null ? 1 : Math.max(1, f.b);
    }

    public static int vsAttack(int side) {
        c f = side(side);
        return f == null ? 0 : f.d;
    }

    /** who moves first (decided when the VS box opens; Ngọc Ấn makes it the player) */
    public static boolean playerFirst() {
        return dd.h;
    }

    public static int owned(int type) {
        return bg.f(type);
    }

    public static int gold() {
        return bg.f(2);
    }

    public static int price(int type) {
        return PRICE[type];
    }

    public static int packSize(int type) {
        return com.mic.smsgame.suquan.q.c[type];
    }

    /** buys one pack with gold, as the shop does; false when there is not enough gold */
    public static boolean buy(int type) {
        int p = PRICE[type];
        if ((p <= 0) || (bg.f(2) < p)) {
            return false;
        }
        bg.b(2, p);
        bg.a(type, packSize(type), 6);
        return true;
    }

    public static String itemName(int type) {
        String s = com.mic.smsgame.suquan.h.c[type];
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    public static String itemDesc(int type) {
        return com.mic.smsgame.suquan.h.d[type];
    }

    // ---- 6: the battle ------------------------------------------------------------------------

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
        c f = side(side);
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

    public static int skillCount(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.d().length;
    }

    /** skill id 0..8 (0-2 fire, 3-5 lightning, 6-8 water) */
    public static int skillId(int side, int k) {
        bn f = fighter(side);
        return (f == null) || (k >= f.d().length) ? 0 : f.d()[k];
    }

    public static String skillName(int k) {
        return skillName(0, k);
    }

    public static String skillName(int side, int k) {
        bn f = fighter(side);
        return (f == null) || (k >= f.d().length) ? "" : com.mic.smsgame.suquan.h.a[f.d()[k]];
    }

    /** the game's description of the player's k-th skill with its damage filled in */
    public static String skillDesc(int k) {
        bn f = fighter(0);
        if ((f == null) || (k >= f.d().length)) {
            return "";
        }
        int id = f.d()[k];
        String str = com.mic.smsgame.suquan.h.b[id];
        int j = str.indexOf("=");
        if (j >= 0) {
            str = str.substring(0, j) + (f.m() * SK_E[id] / SK_F[id]) + str.substring(j + 1);
        }
        return str;
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

    // ---- 7: result ----------------------------------------------------------------------------

    static cr result() {
        ae d = dialog();
        return d instanceof cr ? (cr) d : null;
    }

    /** 0 battle won, 1 quest reward, 2 arena */
    /** 0 battle won, 1 quest reward, 2 arena, 3 battle lost */
    public static int resultMode() {
        cr r = result();
        return r == null ? 3 : r.portMode();
    }

    public static String resultName() {
        cr r = result();
        return r == null ? "" : u.a(r.portPortrait());
    }

    public static int resultPortrait() {
        cr r = result();
        return r == null ? 0 : r.portPortrait();
    }

    public static int resultLevel() {
        cr r = result();
        return r == null ? 0 : r.portLevel();
    }

    /** experience now (counts up while the rewards are added) and the bounds of the current level */
    public static int resultExp() {
        cr r = result();
        return r == null ? 0 : r.portExp();
    }

    public static int resultExpLo() {
        cr r = result();
        return r == null ? 0 : r.portExpLo();
    }

    public static int resultExpHi() {
        cr r = result();
        return r == null ? 1 : r.portExpHi();
    }

    public static int resultHp() {
        cr r = result();
        return r == null ? 0 : r.portHp();
    }

    public static int resultMaxHp() {
        cr r = result();
        return r == null ? 1 : Math.max(1, r.portMaxHp());
    }

    public static int resultAttack() {
        cr r = result();
        return r == null ? 0 : r.portAttack();
    }

    /** gold owned now (counts up while the rewards are added) */
    public static int resultGold() {
        cr r = result();
        return r == null ? 0 : r.portGold();
    }

    public static int resultGoldGain() {
        cr r = result();
        return r == null ? 0 : r.portGoldGain();
    }

    public static int resultExpGain() {
        cr r = result();
        return r == null ? 0 : r.portExpGain();
    }

    public static int resultWinBonus() {
        cr r = result();
        return r == null ? 0 : r.portWinBonus();
    }

    /** the panel accepts "Đóng" (its short wait is over) */
    public static boolean resultReady() {
        cr r = result();
        return (r != null) && r.portReady();
    }

    // ---- shared -------------------------------------------------------------------------------

    public static Image image(String name) {
        return az.b(name);
    }

    /** text in one of the game's bitmap fonts (1 = gold, 2 = white), on magenta (= transparent) */
    public static Image text(String s, int font) {
        dh f = font == 1 ? bz.c : bz.d;
        int w = Math.max(1, f.a(s) + 2);
        int h = f.a() + 4;
        Image img = Image.createImage(w, h);
        Graphics g = img.getGraphics();
        g.setColor(0xFF00FF);
        g.fillRect(0, 0, w, h);
        f.a(g, s, 1, 1, 0);
        return img;
    }

    /** queue an action for the next frame of the game */
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
        switch (kind) {
            case PAUSE_RESUME:
            case PAUSE_QUIT: {
                int k = pauseKind();
                if (k == 1) {
                    dialog().portPress(kind == PAUSE_RESUME ? 2 : 0); // "Không" / "Có"
                } else if ((k == 2) && (kind == PAUSE_RESUME)) {
                    dialog().portPress(1); // "Tiếp tục"
                }
                break;
            }
            case END_RETRY:
            case END_BACK: {
                bu b = battle();
                if ((b == null) || (dialog() == null) || (dialog() != b.portEndDialog) || (b.portOutcome() != 2)) {
                    break;
                }
                if (b.portEndRetry) {
                    dialog().portPress(kind == END_RETRY ? 0 : 2); // "Có" / "Không"
                } else if (kind == END_BACK) {
                    dialog().portPress(1); // "Trở về"
                }
                break;
            }
            case SKILL:
            case ITEM:
            case PAUSE: {
                bu b = battle();
                if ((b == null) || !battleView()) {
                    return;
                }
                if (kind == SKILL) {
                    b.portUseSkill(arg);
                } else if (kind == ITEM) {
                    b.portUseItem(arg);
                } else {
                    b.g();
                }
                break;
            }
            case SCREEN_KEY: {
                ar s = screen();
                if ((s != null) && (dialog() == null)) {
                    s.portPress(arg);
                }
                break;
            }
            case DIALOG_KEY: {
                ae d = dialog();
                if (d != null) {
                    d.portPress(arg);
                }
                break;
            }
            case RESULT_SKIP: {
                cr r = result();
                if (r != null) {
                    r.portSkip();
                }
                break;
            }
        }
    }
}
