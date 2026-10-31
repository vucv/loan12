package j2me;

/**
 * Pixel font used for the MIDP system fonts (javax.microedition.lcdui.Font).
 * Resource format (/j2me/font_N.bin, big endian):
 *   u8 height, u8 baseline, u16 glyphCount,
 *   per glyph: u16 char, u8 advance, u8 w, u8 h, s8 xoff, s8 yoff, then ceil(w*h/8) bytes of 1-bit rows.
 */
public final class BitmapFont {
    public int height;
    public int baseline;
    private char[] chars;
    private int[] advance;
    private int[] gw;
    private int[] gh;
    private int[] gx;
    private int[] gy;
    private byte[][] bits;
    private int fallback = -1;

    private static BitmapFont[] cache = new BitmapFont[3];

    /** sizeIndex: 0 small, 1 medium, 2 large */
    public static BitmapFont get(int sizeIndex) {
        if (cache[sizeIndex] == null) {
            BitmapFont f = new BitmapFont();
            byte[] d = Host.backend != null ? Host.backend.getResource("/j2me/font_" + sizeIndex + ".bin") : null;
            if (d == null) {
                f.makeEmpty(10 + sizeIndex * 2);
            } else {
                f.load(d);
            }
            cache[sizeIndex] = f;
        }
        return cache[sizeIndex];
    }

    private void makeEmpty(int h) {
        height = h;
        baseline = h - 2;
        chars = new char[0];
    }

    private void load(byte[] d) {
        int p = 0;
        height = d[p++] & 0xff;
        baseline = d[p++] & 0xff;
        int n = ((d[p] & 0xff) << 8) | (d[p + 1] & 0xff);
        p += 2;
        chars = new char[n];
        advance = new int[n];
        gw = new int[n];
        gh = new int[n];
        gx = new int[n];
        gy = new int[n];
        bits = new byte[n][];
        for (int i = 0; i < n; i++) {
            chars[i] = (char) (((d[p] & 0xff) << 8) | (d[p + 1] & 0xff));
            p += 2;
            advance[i] = d[p++] & 0xff;
            gw[i] = d[p++] & 0xff;
            gh[i] = d[p++] & 0xff;
            gx[i] = d[p++];
            gy[i] = d[p++];
            int len = (gw[i] * gh[i] + 7) / 8;
            bits[i] = new byte[len];
            System.arraycopy(d, p, bits[i], 0, len);
            p += len;
            if (chars[i] == '?') {
                fallback = i;
            }
        }
    }

    /** chars are sorted, binary search */
    private int find(char c) {
        int lo = 0;
        int hi = chars.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >> 1;
            char m = chars[mid];
            if (m == c) {
                return mid;
            }
            if (m < c) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return fallback;
    }

    public int charWidth(char c) {
        int i = find(c);
        if (i < 0) {
            return height / 2;
        }
        return advance[i];
    }

    public int stringWidth(String s) {
        int w = 0;
        for (int i = 0; i < s.length(); i++) {
            w += charWidth(s.charAt(i));
        }
        return w;
    }

    public void draw(int[] dst, int dstW, int cx0, int cy0, int cx1, int cy1, String s, int x, int y,
                     int argb, boolean bold) {
        int pen = x;
        for (int k = 0; k < s.length(); k++) {
            int i = find(s.charAt(k));
            if (i < 0) {
                pen += height / 2;
                continue;
            }
            int w = gw[i];
            int h = gh[i];
            byte[] b = bits[i];
            int ox = pen + gx[i];
            int oy = y + gy[i];
            for (int py = 0; py < h; py++) {
                int yy = oy + py;
                if (yy < cy0 || yy >= cy1) {
                    continue;
                }
                for (int px = 0; px < w; px++) {
                    int bit = py * w + px;
                    if ((b[bit >> 3] & (0x80 >> (bit & 7))) == 0) {
                        continue;
                    }
                    int xx = ox + px;
                    if (xx >= cx0 && xx < cx1) {
                        dst[yy * dstW + xx] = argb;
                    }
                    if (bold && xx + 1 >= cx0 && xx + 1 < cx1) {
                        dst[yy * dstW + xx + 1] = argb;
                    }
                }
            }
            pen += advance[i] + (bold ? 1 : 0);
        }
    }
}
