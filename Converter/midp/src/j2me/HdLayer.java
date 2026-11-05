package j2me;

import javax.microedition.lcdui.Image;

/**
 * High-resolution layer of the battle board.
 *
 * Images that have a high-resolution version (gems, selection frames, explosions, sparkles) are not
 * drawn into the game's buffer while they fall inside the board: each draw is recorded instead (image,
 * source region, transform, position, clip) and the host draws the high-resolution picture there.
 * Everything else drawn into the buffer after the first recorded image is also copied into
 * {@link #over} (transparent elsewhere), so the host can put skill effects back above the gems.
 * A frame ends when the buffer is drawn onto the screen: the recording is published (pub*) and a new
 * one starts. A frame that recorded nothing keeps the last published one (the game left the board as
 * it was).
 */
public final class HdLayer {
    /** Names of the images that have a high-resolution version (as the game loads them). */
    public static final String[] NAMES = {
        "/sword", "/rice", "/heart", "/yinyang", "/gold", "/book", "/swordred",
        "/focus1", "/focus2", "/zoomfocus1", "/focusitem", "/swordfocus", "/star",
        "/crystalred", "/crystalblue", "/crystalgreen", "/crystalyellow", "/crystalviolet", "/crystalgray",
        "/explodesword", "/explodeheart", "/exploderice", "/explodegold", "/explodebook", "/explodeyinyang",
        "/explodeheartx2", "/explodegoldx2", "/explodericex2", "/explodebookx2", "/explodeyinyangx2"
    };
    /** Values per recorded draw in {@link #ops}. */
    public static final int OP = 12;
    static final int MAX = 400;
    /** How far (px) a recorded image may stick out of the board (the part outside is not shown). */
    static final int EDGE = 3;

    /** Set by the host: high-resolution board on (the setting is on and a battle is shown). */
    public static boolean enabled;
    /** The game's buffer (the image that is drawn onto the screen each frame). */
    public static Image target;
    /** The board in buffer coordinates, [x0,x1) x [y0,y1); set by the host. */
    public static int rx0, ry0, rx1, ry1;
    /** What is drawn into the buffer after the first recorded image, on transparent pixels. */
    public static Image over;

    // the frame being recorded
    public static int count;
    static Image[] imgs = new Image[MAX];
    static int[] ops = new int[MAX * OP];

    // the last published frame
    public static int pubCount;
    public static Image[] pubImgs = new Image[MAX];
    public static int[] pubOps = new int[MAX * OP];
    /** Where the buffer was drawn on the screen (shake offset) when it was published. */
    public static int pubDx, pubDy;
    /** The board part of {@link #over} when it was published, rows of (rx1-rx0). */
    public static int[] pubOver = new int[0];
    public static int pubX0, pubY0, pubW, pubH;
    /** Counts the published frames (the host redraws its layer when it changes). */
    public static int serial;

    private HdLayer() {
    }

    /** True when the image name has a high-resolution version. */
    public static boolean has(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** Pixels of {@link #over} to copy this frame's drawing into, or null when not recording. */
    public static int[] mirror(Image into) {
        return (count > 0 && into == target && over != null) ? over.pixels() : null;
    }

    /**
     * Records a draw of an image with a high-resolution version into the buffer. Returns false when it
     * is not recorded (layer off, not on the board, rotated): the caller then draws it as usual.
     * Coordinates are absolute in the buffer; (cx0..cy1) is the clip.
     */
    public static boolean record(Image into, Image src, int sx, int sy, int w, int h, int transform, int dx, int dy,
                                 int cx0, int cy0, int cx1, int cy1) {
        if (!enabled || into != target || src.hdName == null || transform >= 4) {
            return false;
        }
        int x0 = cx0 > rx0 ? cx0 : rx0;
        int y0 = cy0 > ry0 ? cy0 : ry0;
        int x1 = cx1 < rx1 ? cx1 : rx1;
        int y1 = cy1 < ry1 ? cy1 : ry1;
        // the part drawn: the image rectangle inside the clip and the board
        int ix0 = dx > x0 ? dx : x0;
        int iy0 = dy > y0 ? dy : y0;
        int ix1 = dx + w < x1 ? dx + w : x1;
        int iy1 = dy + h < y1 ? dy + h : y1;
        if (ix0 >= ix1 || iy0 >= iy1) {
            return false;
        }
        // only images inside the board are taken over (gems are 22 px in 21 px cells: those on the edge
        // stick out by a pixel or two, which is cut); an image across the edge is drawn as usual
        int cx0b = cx0 > dx ? cx0 : dx;
        int cy0b = cy0 > dy ? cy0 : dy;
        int cx1b = cx1 < dx + w ? cx1 : dx + w;
        int cy1b = cy1 < dy + h ? cy1 : dy + h;
        if (cx0b < rx0 - EDGE || cy0b < ry0 - EDGE || cx1b > rx1 + EDGE || cy1b > ry1 + EDGE) {
            return false;
        }
        if (count >= MAX) {
            return false;
        }
        if (count == 0 && over != null) {
            clearOver();
        }
        imgs[count] = src;
        int o = count * OP;
        ops[o] = sx;
        ops[o + 1] = sy;
        ops[o + 2] = w;
        ops[o + 3] = h;
        ops[o + 4] = transform;
        ops[o + 5] = dx;
        ops[o + 6] = dy;
        ops[o + 7] = ix0;
        ops[o + 8] = iy0;
        ops[o + 9] = ix1;
        ops[o + 10] = iy1;
        ops[o + 11] = 0;
        count++;
        return true;
    }

    private static void clearOver() {
        int[] d = over.pixels();
        int tw = over.getWidth();
        int y0 = ry0 < 0 ? 0 : ry0;
        int y1 = ry1 > over.getHeight() ? over.getHeight() : ry1;
        for (int y = y0; y < y1; y++) {
            int row = y * tw;
            for (int x = rx0 < 0 ? 0 : rx0; x < rx1 && x < tw; x++) {
                d[row + x] = 0;
            }
        }
    }

    /** The buffer was drawn onto the screen at (dx,dy): publish this frame and start the next one. */
    public static void frame(Image src, int dx, int dy) {
        if (src != target || target == null) {
            return;
        }
        if (over == null || over.getWidth() != target.getWidth() || over.getHeight() != target.getHeight()) {
            over = Image.createImage(target.getWidth(), target.getHeight());
            int[] d = over.pixels();
            for (int i = 0; i < d.length; i++) {
                d[i] = 0;
            }
        }
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                pubImgs[i] = imgs[i];
                imgs[i] = null;
            }
            for (int i = count; i < pubCount; i++) {
                pubImgs[i] = null;
            }
            System.arraycopy(ops, 0, pubOps, 0, count * OP);
            pubCount = count;
            pubDx = dx;
            pubDy = dy;
            pubX0 = rx0;
            pubY0 = ry0;
            pubW = rx1 - rx0;
            pubH = ry1 - ry0;
            if (pubW > 0 && pubH > 0) {
                if (pubOver.length != pubW * pubH) {
                    pubOver = new int[pubW * pubH];
                }
                for (int y = 0; y < pubH; y++) {
                    System.arraycopy(over.pixels(), (pubY0 + y) * over.getWidth() + pubX0, pubOver, y * pubW, pubW);
                }
            }
            serial++;
        } else if (!enabled) {
            pubCount = 0;
        }
        count = 0;
    }

    /** Forgets the published frame (a new battle, or the layer turned off). */
    public static void clear() {
        count = 0;
        pubCount = 0;
        serial++;
    }
}
