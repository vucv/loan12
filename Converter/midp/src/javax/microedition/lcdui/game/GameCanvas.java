package javax.microedition.lcdui.game;

import j2me.Host;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class GameCanvas extends Canvas {
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
    public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
    public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
    public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
    public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;

    private final Image buffer;
    private final boolean suppressKeys;
    private int keyStates;
    private int keyLatch;

    protected GameCanvas(boolean suppressKeyEvents) {
        suppressKeys = suppressKeyEvents;
        buffer = Image.createImage(Host.screenWidth, Host.screenHeight);
    }

    protected Graphics getGraphics() {
        return buffer.getGraphics();
    }

    public int getKeyStates() {
        int s = keyStates | keyLatch;
        keyLatch = 0;
        return s;
    }

    public void paint(Graphics g) {
        g.drawImage(buffer, 0, 0, Graphics.TOP | Graphics.LEFT);
    }

    public void flushGraphics() {
        if (!isShown()) {
            return;
        }
        Graphics g = Host.screenGraphics();
        g.drawImage(buffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        Host.markScreenDirty();
    }

    public void flushGraphics(int x, int y, int width, int height) {
        if (!isShown()) {
            return;
        }
        Graphics g = Host.screenGraphics();
        g.setClip(x, y, width, height);
        g.drawImage(buffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        Host.markScreenDirty();
    }

    public void dispatchEvent(int type, int a, int b) {
        if (type == Host.KEY_PRESSED || type == Host.KEY_RELEASED) {
            int action = getGameAction(a);
            if (action != 0) {
                int bit = 1 << action;
                if (type == Host.KEY_PRESSED) {
                    keyStates |= bit;
                    keyLatch |= bit;
                } else {
                    keyStates &= ~bit;
                }
                if (suppressKeys) {
                    return;
                }
            }
        }
        super.dispatchEvent(type, a, b);
    }
}
