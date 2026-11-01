import j2me.Host;

import javax.imageio.ImageIO;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Drives the game headlessly from a small script and saves screenshots. */
public class Run {
    static DesktopBackend b;
    static File outDir;
    static String lastShown = "";

    public static void main(String[] args) throws Exception {
        File script = new File(args[0]);
        outDir = new File(args[1]);
        outDir.mkdirs();
        b = new DesktopBackend(new File(System.getProperty("j2cs.src", "decompiled")), new File(System.getProperty("j2cs.fonts", "assets/j2me")));
        Host.init(b, 240, 320);
        Object m = Class.forName("com.mic.smsgame.suquan.SQMidlet").getDeclaredConstructor().newInstance();
        Host.start((javax.microedition.midlet.MIDlet) m);
        for (String line : Files.readAllLines(script.toPath())) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] p = line.split("\\s+", 2);
            String arg = p.length > 1 ? p[1] : "";
            switch (p[0]) {
                case "wait":
                    advance(Integer.parseInt(arg));
                    break;
                case "key": {
                    for (String k : arg.split("\\s+")) {
                        int code = Integer.parseInt(k);
                        Host.keyPressed(code);
                        advance(60);
                        Host.keyReleased(code);
                        advance(200);
                    }
                    break;
                }
                case "tap": {
                    String[] xy = arg.split("\\s+");
                    int x = Integer.parseInt(xy[0]), y = Integer.parseInt(xy[1]);
                    Host.pointerPressed(x, y);
                    advance(60);
                    Host.pointerReleased(x, y);
                    advance(200);
                    break;
                }
                case "drag": {
                    // drag x1 y1 x2 y2: press, move in 5 steps, release (a finger swipe)
                    String[] d = arg.split("\\s+");
                    int x1 = Integer.parseInt(d[0]), y1 = Integer.parseInt(d[1]);
                    int x2 = Integer.parseInt(d[2]), y2 = Integer.parseInt(d[3]);
                    Host.pointerPressed(x1, y1);
                    advance(40);
                    for (int st = 1; st <= 5; st++) {
                        Host.pointerDragged(x1 + (x2 - x1) * st / 5, y1 + (y2 - y1) * st / 5);
                        advance(20);
                    }
                    Host.pointerReleased(x2, y2);
                    advance(200);
                    break;
                }
                case "board": {
                    // debug: battle board rectangle and cursor
                    try {
                        Object canvas = game.cz.a;
                        Object screens = fld(game.cz.class, "e").get(canvas);
                        Object cur = fld(game.bv.class, "b").get(screens);
                        if (cur instanceof com.mic.smsgame.suquan.ab) {
                            game.bu bu = (game.bu) fld(com.mic.smsgame.suquan.ab.class, "e").get(cur);
                            game.dl r = bu.h();
                            System.out.println("board: x=" + fld(game.dl.class, "a").get(r) + " y=" + fld(game.dl.class, "b").get(r)
                                    + " w=" + fld(game.dl.class, "c").get(r) + " h=" + fld(game.dl.class, "d").get(r)
                                    + " cursor=" + bu.k() + "," + bu.l() + " selected=" + bu.j() + " state=" + fld(game.bu.class, "v").get(bu));
                        } else System.out.println("board: not in battle (" + cur + ")");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case "maptap": {
                    // debug: on the map, tap the visible town farthest (in roads) from the hero, then report
                    try {
                        Object canvas = game.cz.a;
                        Object screens = fld(game.cz.class, "e").get(canvas);
                        Object cur = fld(game.bv.class, "b").get(screens);
                        if (!(cur instanceof com.mic.smsgame.suquan.aa)) {
                            System.out.println("maptap: not on the map (" + cur + ")");
                            break;
                        }
                        Class<?> ac = com.mic.smsgame.suquan.aa.class;
                        game.bo[] z = (game.bo[]) fld(ac, "z").get(cur);
                        int gx = fld(game.ar.class, "g").getInt(cur), gy = fld(game.ar.class, "a_").getInt(cur);
                        int from = fld(ac, "B").getInt(cur);
                        // farthest town (in roads) reachable over open roads and visible on screen
                        game.bl[] A = (game.bl[]) fld(ac, "A").get(cur);
                        int[] dist = new int[z.length];
                        java.util.Arrays.fill(dist, -1);
                        int[] queue = new int[z.length];
                        int qh = 0, qt = 0;
                        queue[qt++] = from;
                        dist[from] = 0;
                        while (qh < qt) {
                            int u = queue[qh++];
                            for (int dir = 0; dir < 4; dir++) {
                                int e = game.bq.c[u][dir];
                                if (e < 0 || e >= A.length || !A[e].c) continue;
                                int v = A[e].a == u ? A[e].b : A[e].a;
                                if (v >= 0 && v < z.length && dist[v] < 0) { dist[v] = dist[u] + 1; queue[qt++] = v; }
                            }
                        }
                        int best = -1, bestD = 0;
                        for (int i = 0; i < z.length; i++) {
                            int sx = gx + z[i].b, sy = gy + z[i].c;
                            if (!z[i].d || dist[i] <= 0 || sx < 10 || sx > 230 || sy < 20 || sy > 290) continue;
                            if (dist[i] > bestD) { bestD = dist[i]; best = i; }
                        }
                        System.out.println("maptap: reachable towns " + java.util.Arrays.toString(dist));
                        if (best < 0) { System.out.println("maptap: no visible town"); break; }
                        int tx = gx + z[best].b, ty = gy + z[best].c;
                        System.out.println("maptap: hero at town " + from + ", tapping town " + best + " at " + tx + "," + ty);
                        Host.pointerPressed(tx, ty);
                        advance(60);
                        Host.pointerReleased(tx, ty);
                        advance(200);
                        System.out.println("maptap: path length " + fld(ac, "portPathLen").getInt(cur) + ", state " + fld(ac, "f").getInt(cur)
                                + ", pending press " + game.cx.o + "," + game.cx.p + " busy=" + fld(game.bv.class, "a").get(screens)
                                + " dialog=" + fld(game.bv.class, "d").get(screens));

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case "maphero": {
                    try {
                        Object cur = fld(game.bv.class, "b").get(fld(game.cz.class, "e").get(game.cz.a));
                        if (cur instanceof com.mic.smsgame.suquan.aa)
                            System.out.println("maphero: town " + fld(com.mic.smsgame.suquan.aa.class, "B").getInt(cur) + ", state " + fld(com.mic.smsgame.suquan.aa.class, "f").getInt(cur));
                        else System.out.println("maphero: not on the map (" + cur + ")");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case "softkeys":
                    System.out.println("softkeys: " + Host.softKeyLabels[0] + " | " + Host.softKeyLabels[1] + " | " + Host.softKeyLabels[2]);
                    break;
                case "shot":
                    shot(arg);
                    break;
                case "burst": {
                    String[] q = arg.split("\\s+");
                    int n = Integer.parseInt(q[1]), iv = Integer.parseInt(q[2]);
                    for (int k = 0; k < n; k++) {
                        shot(q[0] + "_" + (k < 10 ? "0" : "") + k);
                        advance(iv);
                    }
                    break;
                }
                case "text": {
                    Displayable d = Host.display.getShown();
                    if (d instanceof TextBox) {
                        ((TextBox) d).setString(arg);
                        System.out.println("TextBox <- " + arg);
                    } else System.out.println("WARN: no TextBox shown (" + d + ")");
                    break;
                }
                case "cmd": {
                    Displayable d = Host.display.getShown();
                    boolean ok = false;
                    for (int i = 0; d != null && i < d.getCommandCount(); i++) {
                        Command c = d.getCommand(i);
                        if (c.getLabel().equals(arg)) {
                            Host.queueCommand(d, c);
                            ok = true;
                        }
                    }
                    if (!ok) System.out.println("WARN: no command " + arg);
                    advance(100);
                    break;
                }
                case "monkey":
                    monkey(arg);
                    break;
                case "stores":
                    for (Map.Entry<String, byte[]> e : b.stores.entrySet())
                        System.out.println("store " + e.getKey() + " " + e.getValue().length + " bytes");
                    break;
                default:
                    System.out.println("unknown: " + line);
            }
        }
        System.out.println("events: " + b.events);
    }

    static boolean monkeyMode;
    static int exitsIgnored;
    static final int[] MONKEY_KEYS = {-1, -2, -3, -4, -5, -5, -5, 53, 50, 56, 52, 54, -6, -7, 35, 42, 48, 49, 51, 55, 57};

    /** monkey <seed> <steps> <shotEvery>: random keys / taps / pauses (same sequence as the C# harness). */
    static void monkey(String arg) throws IOException {
        String[] q = arg.split("\\s+");
        long seed = Long.parseLong(q[0]);
        int steps = Integer.parseInt(q[1]);
        int shotEvery = Integer.parseInt(q[2]);
        boolean win = q.length > 3 && (q[3].equals("win") || q[3].equals("touchwin"));
        touchBot = q.length > 3 && q[3].equals("touchwin");
        java.util.Random r = new java.util.Random(seed);
        monkeyMode = true;
        for (int k = 0; k < steps; k++) {
            Displayable d = Host.display.getShown();
            if (d != null && !(d instanceof javax.microedition.lcdui.Canvas)) {
                // high-level screen: TextBox -> type a name and confirm; other screens (debug log Form) -> back
                boolean tb = d instanceof TextBox;
                if (tb) ((TextBox) d).setString("Monkey");
                int want = tb ? Command.OK : Command.BACK;
                Command pick = null;
                for (int i = 0; i < d.getCommandCount(); i++) {
                    Command c = d.getCommand(i);
                    if (pick == null || c.getCommandType() == want) pick = c;
                    if (c.getCommandType() == want) break;
                }
                if (pick != null) Host.queueCommand(d, pick);
                advance(100);
                continue;
            }
            if (win) {
                weakenEnemy();
                if (botMove()) {
                    advance(300);
                    continue;
                }
            }
            int choice = r.nextInt(100);
            if (choice < 85) {
                int code = MONKEY_KEYS[r.nextInt(MONKEY_KEYS.length)];
                Host.keyPressed(code);
                advance(60);
                Host.keyReleased(code);
                advance(40 + r.nextInt(400));
            } else if (choice < 95) {
                int x = r.nextInt(240), y = r.nextInt(320);
                Host.pointerPressed(x, y);
                advance(60);
                Host.pointerReleased(x, y);
                advance(100 + r.nextInt(300));
            } else {
                advance(500 + r.nextInt(2000));
            }
            if (shotEvery > 0 && k % shotEvery == 0) shot("m" + seed + "_" + String.format("%05d", k));
            if (System.getProperty("ui") != null && k % 10 == 0) ui("m" + k);
        }
        monkeyMode = false;
        System.out.println("monkey: exits ignored " + exitsIgnored + ", enemies weakened " + weakened + ", bot moves " + botMoves);
        shot("m" + seed + "_end");
    }

    static java.lang.reflect.Field fld(Class<?> c, String name) throws Exception {
        java.lang.reflect.Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    static int weakened;
    static int botMoves;
    static boolean touchBot;

    /** test bot: on the player's turn (board waiting for input) play a valid move, preferring sword matches. */
    static boolean botMove() {
        try {
            Object canvas = game.cz.a;
            if (canvas == null) return false;
            Object screens = fld(game.cz.class, "e").get(canvas);
            Object cur = screens == null ? null : fld(game.bv.class, "b").get(screens);
            if (!(cur instanceof com.mic.smsgame.suquan.ab)) return false;
            game.bu board = (game.bu) fld(com.mic.smsgame.suquan.ab.class, "e").get(cur);
            game.cq st = (game.cq) fld(com.mic.smsgame.suquan.ab.class, "c").get(cur);
            if (board == null || st == null) return false;
            if (fld(game.bu.class, "v").getInt(board) != 8 || st.a() != 0) return false;
            game.di moves = board.q();
            if (moves == null || moves.b() == 0) return false;
            int pick = 0;
            search:
            for (int k = 0; k < moves.b(); k++) {
                com.mic.smsgame.suquan.a m = (com.mic.smsgame.suquan.a) moves.b(k);
                int t1 = board.d(m.a, m.b), t2 = board.d(m.c, m.d);
                int[] types = {t2, t2, t1, t1};
                for (int i = 0; i < m.e.length; i++) {
                    if ((m.e[i] & 0xFF) >= 3 && (types[i] == 0 || types[i] == 6)) {
                        pick = k;
                        break search;
                    }
                }
            }
            com.mic.smsgame.suquan.a m = (com.mic.smsgame.suquan.a) moves.b(pick);
            if (touchBot) {
                // play the move with the finger: alternately a swipe, or a tap on the gem and a tap on its neighbour
                game.dl r = board.h();
                int x1 = r.a + (m.b - 2) * 21 + 10, y1 = r.b + (m.a - 2) * 21 + 10;
                int x2 = r.a + (m.d - 2) * 21 + 10, y2 = r.b + (m.c - 2) * 21 + 10;
                if (botMoves % 2 == 0) {
                    Host.pointerPressed(x1, y1);
                    advance(40);
                    for (int s4 = 1; s4 <= 4; s4++) {
                        Host.pointerDragged(x1 + (x2 - x1) * s4 / 4, y1 + (y2 - y1) * s4 / 4);
                        advance(20);
                    }
                    Host.pointerReleased(x2, y2);
                } else {
                    Host.pointerPressed(x1, y1);
                    advance(60);
                    Host.pointerReleased(x1, y1);
                    advance(120);
                    Host.pointerPressed(x2, y2);
                    advance(60);
                    Host.pointerReleased(x2, y2);
                }
            } else {
                board.a(m.a, m.b, m.c, m.d);
            }
            botMoves++;
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** test cheat for exploring later stages: in a battle, the computer opponent is left with 1 HP. */
    static void weakenEnemy() {
        try {
            Object canvas = game.cz.a; // the instance, if created (cz.d() would create it)
            if (canvas == null) return;
            Object screens = fld(game.cz.class, "e").get(canvas);
            Object cur = screens == null ? null : fld(game.bv.class, "b").get(screens);
            if (!(cur instanceof com.mic.smsgame.suquan.ab)) return;
            Object board = fld(com.mic.smsgame.suquan.ab.class, "c").get(cur);
            if (board == null) return;
            game.bn[] fighters = (game.bn[]) fld(game.cq.class, "f").get(board);
            java.lang.reflect.Field hp = fld(game.bn.class, "c");
            for (game.bn f : fighters) {
                if (f != null && f.b() >= 6 && hp.getInt(f) > 1) {
                    hp.setInt(f, 1);
                    weakened++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void advance(int ms) {
        for (int t = 0; t < ms; t += 20) {
            b.now += 20;
            Host.tick();
            Displayable d = Host.display != null ? Host.display.getShown() : null;
            String name = d == null ? "null" : d.getClass().getName();
            if (!name.equals(lastShown)) {
                System.out.println("t=" + (b.now - 1_400_000_000_000L) + " shown: " + name);
                lastShown = name;
            }
            if (monkeyMode && (b.exited || Host.exitRequested)) {
                // keep exploring: the game asked to quit, pretend it did not happen
                b.exited = false;
                Host.exitRequested = false;
                exitsIgnored++;
            }
            if (b.exited) {
                System.out.println("EXIT requested");
                return;
            }
        }
    }

    /** PORT-UI: what the touch interface would show (and, with -Dui=act, try its actions). */
    static void ui(String tag) {
        if (game.PortUi.prepareView()) {
            System.out.println("ui " + tag + ": prepare " + game.PortUi.vsName(0) + " lv" + game.PortUi.vsLevel(0) + " hp" + game.PortUi.vsHp(0) + "/" + game.PortUi.vsMaxHp(0)
                + " atk" + game.PortUi.vsAttack(0) + " vs " + game.PortUi.vsName(1) + " lv" + game.PortUi.vsLevel(1) + " pic" + game.PortUi.vsPortrait(1)
                + " first=" + (game.PortUi.playerFirst() ? "player" : "enemy") + " retreat=" + game.PortUi.canRetreat() + " gold=" + game.PortUi.gold()
                + " owned " + game.PortUi.owned(0) + "," + game.PortUi.owned(1) + "," + game.PortUi.owned(3) + "," + game.PortUi.owned(4) + "," + game.PortUi.owned(5)
                + " text " + game.PortUi.text("XUẤT TRẬN", 1).getWidth() + "x" + game.PortUi.text("XUẤT TRẬN", 1).getHeight());
            if ("act".equals(System.getProperty("ui"))) {
                if (game.PortUi.gold() < 250) game.bg.a(2, 1000, 6);
                boolean bought = game.PortUi.buy(4);
                game.PortUi.request(game.PortUi.SCREEN_KEY, 0);
                System.out.println("ui action: buy potion " + bought + " -> owned " + game.PortUi.owned(4) + " gold " + game.PortUi.gold());
            }
        }
        if (game.PortUi.resultView()) {
            System.out.println("ui " + tag + ": result mode " + game.PortUi.resultMode() + " " + game.PortUi.resultName() + " lv" + game.PortUi.resultLevel()
                + " exp " + game.PortUi.resultExp() + " [" + game.PortUi.resultExpLo() + ".." + game.PortUi.resultExpHi() + "] gold " + game.PortUi.resultGold()
                + " +gold " + game.PortUi.resultGoldGain() + " +exp " + game.PortUi.resultExpGain() + " +win " + game.PortUi.resultWinBonus() + " ready " + game.PortUi.resultReady());
        }
        if (!game.PortUi.battleView()) return;
        StringBuilder sb = new StringBuilder("ui " + tag + ": ");
        for (int side = 0; side < 2; side++) {
            sb.append(game.PortUi.name(side)).append(" lv").append(game.PortUi.level(side))
              .append(" hp ").append(game.PortUi.hp(side)).append('/').append(game.PortUi.maxHp(side))
              .append(" mana ").append(game.PortUi.mana(side)).append('/').append(game.PortUi.maxMana(side))
              .append(" pic ").append(game.PortUi.portrait(side)).append(" | ");
        }
        sb.append("turn ").append(game.PortUi.turn()).append(game.PortUi.waiting() ? " waiting" : "")
          .append(" skills ").append(game.PortUi.skillsUsable()).append('/').append(game.PortUi.skillCount());
        for (int k = 0; k < game.PortUi.skillCount(); k++) sb.append(' ').append(game.PortUi.skillName(k)).append(':').append(game.PortUi.skillCost(k));
        sb.append(" items");
        for (int t = 0; t < 6; t++) sb.append(' ').append(game.PortUi.itemCount(t));
        if (game.PortUi.skillCount() > 0) sb.append(" desc0=").append(game.PortUi.skillDesc(0));
        sb.append(" board ").append(game.PortUi.board(0)).append(',').append(game.PortUi.board(1)).append(',').append(game.PortUi.board(2)).append(',').append(game.PortUi.board(3));
        System.out.println(sb);
        if ("act".equals(System.getProperty("ui")) && game.PortUi.waiting()) {
            // test feed: full mana and 3 potions so the actions can run
            game.cq st = game.PortUi.battle().portState();
            st.b(0).e(36);
            for (game.dx d : st.a) if (d.a == 4 && d.b == 0) d.b = 3;
            if (game.PortUi.skillsUsable() > 0) {
                game.PortUi.request(game.PortUi.SKILL, game.PortUi.skillsUsable() - 1);
                System.out.println("ui action: skill " + (game.PortUi.skillsUsable() - 1));
            } else if (game.PortUi.itemCount(4) > 0 && !game.PortUi.itemUsed()) {
                game.PortUi.request(game.PortUi.ITEM, 4);
                System.out.println("ui action: item 4");
            }
        }
    }

    static void shot(String name) throws IOException {
        BufferedImage img = new BufferedImage(Host.screenWidth, Host.screenHeight, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, Host.screenWidth, Host.screenHeight, Host.screen, 0, Host.screenWidth);
        ImageIO.write(img, "png", new File(outDir, name + ".png"));
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(Host.screen.length * 4).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        for (int v : Host.screen) bb.putInt(v);
        Files.write(new File(outDir, name + ".raw").toPath(), bb.array());
        System.out.println("shot " + name);
    }
}
