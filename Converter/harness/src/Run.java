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
        boolean win = q.length > 3 && q[3].equals("win");
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
            board.a(m.a, m.b, m.c, m.d);
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
