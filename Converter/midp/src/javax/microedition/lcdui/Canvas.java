package javax.microedition.lcdui;

import j2me.Host;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;
    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;
    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;

    /** Device key codes used by the port (Nokia/Series 40 layout). */
    public static final int KEYCODE_UP = -1;
    public static final int KEYCODE_DOWN = -2;
    public static final int KEYCODE_LEFT = -3;
    public static final int KEYCODE_RIGHT = -4;
    public static final int KEYCODE_FIRE = -5;
    public static final int KEYCODE_SOFT_LEFT = -6;
    public static final int KEYCODE_SOFT_RIGHT = -7;
    public static final int KEYCODE_CLEAR = -8;

    boolean repaintPending;
    boolean fullScreen;

    protected Canvas() {
    }

    public int getWidth() {
        return Host.screenWidth;
    }

    public int getHeight() {
        return Host.screenHeight;
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    public boolean hasPointerEvents() {
        return true;
    }

    public boolean hasPointerMotionEvents() {
        return true;
    }

    public boolean hasRepeatEvents() {
        return false;
    }

    public int getKeyCode(int gameAction) {
        switch (gameAction) {
            case UP:
                return KEYCODE_UP;
            case DOWN:
                return KEYCODE_DOWN;
            case LEFT:
                return KEYCODE_LEFT;
            case RIGHT:
                return KEYCODE_RIGHT;
            case FIRE:
                return KEYCODE_FIRE;
            case GAME_A:
                return KEY_NUM7;
            case GAME_B:
                return KEY_NUM9;
            case GAME_C:
                return KEY_STAR;
            case GAME_D:
                return KEY_POUND;
            default:
                throw new IllegalArgumentException("gameAction");
        }
    }

    public int getGameAction(int keyCode) {
        switch (keyCode) {
            case KEYCODE_UP:
            case KEY_NUM2:
                return UP;
            case KEYCODE_DOWN:
            case KEY_NUM8:
                return DOWN;
            case KEYCODE_LEFT:
            case KEY_NUM4:
                return LEFT;
            case KEYCODE_RIGHT:
            case KEY_NUM6:
                return RIGHT;
            case KEYCODE_FIRE:
            case KEY_NUM5:
                return FIRE;
            case KEY_NUM7:
                return GAME_A;
            case KEY_NUM9:
                return GAME_B;
            case KEY_STAR:
                return GAME_C;
            case KEY_POUND:
                return GAME_D;
            default:
                return 0;
        }
    }

    public String getKeyName(int keyCode) {
        if (keyCode >= KEY_NUM0 && keyCode <= KEY_NUM9) {
            return String.valueOf((char) keyCode);
        }
        switch (keyCode) {
            case KEY_STAR:
                return "*";
            case KEY_POUND:
                return "#";
            case KEYCODE_UP:
                return "Up";
            case KEYCODE_DOWN:
                return "Down";
            case KEYCODE_LEFT:
                return "Left";
            case KEYCODE_RIGHT:
                return "Right";
            case KEYCODE_FIRE:
                return "Select";
            case KEYCODE_SOFT_LEFT:
                return "Soft1";
            case KEYCODE_SOFT_RIGHT:
                return "Soft2";
            case KEYCODE_CLEAR:
                return "Clear";
            default:
                return "Key" + keyCode;
        }
    }

    protected void keyPressed(int keyCode) {
    }

    protected void keyRepeated(int keyCode) {
    }

    protected void keyReleased(int keyCode) {
    }

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }

    protected void pointerDragged(int x, int y) {
    }

    protected void showNotify() {
    }

    protected void hideNotify() {
    }

    protected void sizeChanged(int w, int h) {
    }

    protected abstract void paint(Graphics g);

    public final void repaint() {
        repaintPending = true;
    }

    public final void repaint(int x, int y, int width, int height) {
        repaintPending = true;
    }

    public final void serviceRepaints() {
        if (!repaintPending || !isShown()) {
            return;
        }
        repaintPending = false;
        Graphics g = Host.screenGraphics();
        paint(g);
        Host.markScreenDirty();
    }

    public void setFullScreenMode(boolean mode) {
        fullScreen = mode;
    }

    // ---- port runtime hooks ------------------------------------------------------------

    public void dispatchEvent(int type, int a, int b) {
        switch (type) {
            case Host.KEY_PRESSED:
                keyPressed(a);
                break;
            case Host.KEY_RELEASED:
                keyReleased(a);
                break;
            case Host.KEY_REPEATED:
                keyRepeated(a);
                break;
            case Host.POINTER_PRESSED:
                pointerPressed(a, b);
                break;
            case Host.POINTER_RELEASED:
                pointerReleased(a, b);
                break;
            case Host.POINTER_DRAGGED:
                pointerDragged(a, b);
                break;
            default:
                break;
        }
    }

    public void callShowNotify() {
        super.callShowNotify();
        showNotify();
        repaintPending = true;
    }

    public void callHideNotify() {
        super.callHideNotify();
        hideNotify();
    }
}
