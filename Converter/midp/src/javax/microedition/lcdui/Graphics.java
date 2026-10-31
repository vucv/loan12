package javax.microedition.lcdui;

import j2me.BitmapFont;

/** MIDP 2.0 Graphics rendering into an Image's ARGB pixel array (software renderer). */
public class Graphics {
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;
    public static final int BASELINE = 64;
    public static final int SOLID = 0;
    public static final int DOTTED = 1;

    private final Image target;
    private final int tw;
    private final int th;
    private int tx;
    private int ty;
    // clip in absolute target coordinates, as set by the game
    private int clipX;
    private int clipY;
    private int clipW;
    private int clipH;
    // effective clip (intersected with the target) as [x0,x1) x [y0,y1)
    private int cx0;
    private int cy0;
    private int cx1;
    private int cy1;
    private int color = 0xff000000;
    private Font font;
    private int stroke = SOLID;

    Graphics(Image img) {
        target = img;
        tw = img.width;
        th = img.height;
        reset();
    }

    /** Resets translation, clip, colour, font and stroke (used by the runtime before paint). */
    public void reset() {
        tx = 0;
        ty = 0;
        clipX = 0;
        clipY = 0;
        clipW = tw;
        clipH = th;
        updateClip();
        color = 0xff000000;
        font = Font.getDefaultFont();
        stroke = SOLID;
    }

    private void updateClip() {
        cx0 = clipX < 0 ? 0 : clipX;
        cy0 = clipY < 0 ? 0 : clipY;
        int x1 = clipX + clipW;
        int y1 = clipY + clipH;
        cx1 = x1 > tw ? tw : x1;
        cy1 = y1 > th ? th : y1;
    }

    // ---- state ---------------------------------------------------------------------------

    public void translate(int x, int y) {
        tx += x;
        ty += y;
    }

    public int getTranslateX() {
        return tx;
    }

    public int getTranslateY() {
        return ty;
    }

    public void setClip(int x, int y, int width, int height) {
        clipX = x + tx;
        clipY = y + ty;
        clipW = width < 0 ? 0 : width;
        clipH = height < 0 ? 0 : height;
        updateClip();
    }

    public void clipRect(int x, int y, int width, int height) {
        int ax0 = x + tx;
        int ay0 = y + ty;
        int ax1 = ax0 + width;
        int ay1 = ay0 + height;
        int bx1 = clipX + clipW;
        int by1 = clipY + clipH;
        int nx0 = ax0 > clipX ? ax0 : clipX;
        int ny0 = ay0 > clipY ? ay0 : clipY;
        int nx1 = ax1 < bx1 ? ax1 : bx1;
        int ny1 = ay1 < by1 ? ay1 : by1;
        clipX = nx0;
        clipY = ny0;
        clipW = nx1 - nx0 < 0 ? 0 : nx1 - nx0;
        clipH = ny1 - ny0 < 0 ? 0 : ny1 - ny0;
        updateClip();
    }

    public int getClipX() {
        return clipX - tx;
    }

    public int getClipY() {
        return clipY - ty;
    }

    public int getClipWidth() {
        return clipW;
    }

    public int getClipHeight() {
        return clipH;
    }

    public void setColor(int rgb) {
        color = 0xff000000 | (rgb & 0xffffff);
    }

    public void setColor(int red, int green, int blue) {
        if ((red & ~0xff) != 0 || (green & ~0xff) != 0 || (blue & ~0xff) != 0) {
            throw new IllegalArgumentException("colour component out of range");
        }
        color = 0xff000000 | (red << 16) | (green << 8) | blue;
    }

    public int getColor() {
        return color & 0xffffff;
    }

    public int getRedComponent() {
        return (color >> 16) & 0xff;
    }

    public int getGreenComponent() {
        return (color >> 8) & 0xff;
    }

    public int getBlueComponent() {
        return color & 0xff;
    }

    public void setGrayScale(int value) {
        setColor(value, value, value);
    }

    public int getGrayScale() {
        return (getRedComponent() * 30 + getGreenComponent() * 59 + getBlueComponent() * 11) / 100;
    }

    public int getDisplayColor(int c) {
        return c & 0xffffff;
    }

    public void setFont(Font f) {
        font = f == null ? Font.getDefaultFont() : f;
    }

    public Font getFont() {
        return font;
    }

    public void setStrokeStyle(int style) {
        stroke = style;
    }

    public int getStrokeStyle() {
        return stroke;
    }

    // ---- primitives ----------------------------------------------------------------------

    private void plot(int x, int y) {
        if (x >= cx0 && x < cx1 && y >= cy0 && y < cy1) {
            target.data[y * tw + x] = color;
        }
    }

    private void hline(int x0, int x1, int y) {
        if (y < cy0 || y >= cy1) {
            return;
        }
        if (x0 > x1) {
            int t = x0;
            x0 = x1;
            x1 = t;
        }
        if (x0 < cx0) {
            x0 = cx0;
        }
        if (x1 >= cx1) {
            x1 = cx1 - 1;
        }
        int[] d = target.data;
        int row = y * tw;
        for (int x = x0; x <= x1; x++) {
            d[row + x] = color;
        }
    }

    public void fillRect(int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int x0 = x + tx;
        int y0 = y + ty;
        int x1 = x0 + width;
        int y1 = y0 + height;
        if (x0 < cx0) {
            x0 = cx0;
        }
        if (y0 < cy0) {
            y0 = cy0;
        }
        if (x1 > cx1) {
            x1 = cx1;
        }
        if (y1 > cy1) {
            y1 = cy1;
        }
        if (x0 >= x1 || y0 >= y1) {
            return;
        }
        int[] d = target.data;
        int c = color;
        for (int yy = y0; yy < y1; yy++) {
            int row = yy * tw;
            for (int xx = x0; xx < x1; xx++) {
                d[row + xx] = c;
            }
        }
    }

    public void drawRect(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            return;
        }
        if (width == 0 || height == 0) {
            drawLine(x, y, x + width, y + height);
            return;
        }
        drawLine(x, y, x + width, y);
        drawLine(x, y + height, x + width, y + height);
        drawLine(x, y + 1, x, y + height - 1);
        drawLine(x + width, y + 1, x + width, y + height - 1);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        x1 += tx;
        y1 += ty;
        x2 += tx;
        y2 += ty;
        if (y1 == y2 && stroke == SOLID) {
            hline(x1, x2, y1);
            return;
        }
        int dx = x2 > x1 ? x2 - x1 : x1 - x2;
        int dy = y2 > y1 ? y2 - y1 : y1 - y2;
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int n = 0;
        while (true) {
            if (stroke == SOLID || (n & 2) == 0) {
                plot(x1, y1);
            }
            n++;
            if (x1 == x2 && y1 == y2) {
                break;
            }
            int e2 = err * 2;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        x1 += tx;
        y1 += ty;
        x2 += tx;
        y2 += ty;
        x3 += tx;
        y3 += ty;
        int minY = Math.min(y1, Math.min(y2, y3));
        int maxY = Math.max(y1, Math.max(y2, y3));
        if (minY < cy0) {
            minY = cy0;
        }
        if (maxY >= cy1) {
            maxY = cy1 - 1;
        }
        for (int y = minY; y <= maxY; y++) {
            int xa = Integer.MAX_VALUE;
            int xb = Integer.MIN_VALUE;
            int[] r = edge(x1, y1, x2, y2, y);
            if (r != null) {
                xa = Math.min(xa, r[0]);
                xb = Math.max(xb, r[1]);
            }
            r = edge(x2, y2, x3, y3, y);
            if (r != null) {
                xa = Math.min(xa, r[0]);
                xb = Math.max(xb, r[1]);
            }
            r = edge(x3, y3, x1, y1, y);
            if (r != null) {
                xa = Math.min(xa, r[0]);
                xb = Math.max(xb, r[1]);
            }
            if (xa <= xb) {
                hline(xa, xb, y);
            }
        }
    }

    /** x-range covered by segment (x0,y0)-(x1,y1) on scanline y, or null. */
    private static int[] edge(int x0, int y0, int x1, int y1, int y) {
        if (y0 == y1) {
            if (y != y0) {
                return null;
            }
            return new int[]{Math.min(x0, x1), Math.max(x0, x1)};
        }
        if (y < Math.min(y0, y1) || y > Math.max(y0, y1)) {
            return null;
        }
        int x = x0 + (int) ((long) (y - y0) * (x1 - x0) / (y1 - y0));
        return new int[]{x, x};
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (width < 0 || height < 0) {
            return;
        }
        int rx = Math.min(Math.abs(arcWidth) / 2, width / 2);
        int ry = Math.min(Math.abs(arcHeight) / 2, height / 2);
        drawLine(x + rx, y, x + width - rx, y);
        drawLine(x + rx, y + height, x + width - rx, y + height);
        drawLine(x, y + ry, x, y + height - ry);
        drawLine(x + width, y + ry, x + width, y + height - ry);
        if (rx > 0 && ry > 0) {
            drawArc(x, y, rx * 2, ry * 2, 90, 90);
            drawArc(x + width - rx * 2, y, rx * 2, ry * 2, 0, 90);
            drawArc(x, y + height - ry * 2, rx * 2, ry * 2, 180, 90);
            drawArc(x + width - rx * 2, y + height - ry * 2, rx * 2, ry * 2, 270, 90);
        }
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int rx = Math.min(Math.abs(arcWidth) / 2, width / 2);
        int ry = Math.min(Math.abs(arcHeight) / 2, height / 2);
        fillRect(x, y + ry, width, height - ry * 2);
        fillRect(x + rx, y, width - rx * 2, ry);
        fillRect(x + rx, y + height - ry, width - rx * 2, ry);
        if (rx > 0 && ry > 0) {
            fillArc(x, y, rx * 2, ry * 2, 90, 90);
            fillArc(x + width - rx * 2, y, rx * 2, ry * 2, 0, 90);
            fillArc(x, y + height - ry * 2, rx * 2, ry * 2, 180, 90);
            fillArc(x + width - rx * 2, y + height - ry * 2, rx * 2, ry * 2, 270, 90);
        }
    }

    private static boolean angleIn(double deg, int start, int arc) {
        if (arc >= 360 || arc <= -360) {
            return true;
        }
        if (arc < 0) {
            start += arc;
            arc = -arc;
        }
        double a = deg - start;
        while (a < 0) {
            a += 360;
        }
        while (a >= 360) {
            a -= 360;
        }
        return a <= arc;
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (width <= 0 || height <= 0 || arcAngle == 0) {
            return;
        }
        double rx = width / 2.0;
        double ry = height / 2.0;
        double mx = x + rx;
        double my = y + ry;
        for (int py = y; py < y + height; py++) {
            for (int px = x; px < x + width; px++) {
                double dx = (px + 0.5 - mx) / rx;
                double dy = (py + 0.5 - my) / ry;
                if (dx * dx + dy * dy <= 1.0) {
                    double deg = Math.atan2(-dy, dx) * 180.0 / Math.PI;
                    if (angleIn(deg, startAngle, arcAngle)) {
                        plot(px + tx, py + ty);
                    }
                }
            }
        }
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        if (width < 0 || height < 0 || arcAngle == 0) {
            return;
        }
        double rx = width / 2.0;
        double ry = height / 2.0;
        double mx = x + rx;
        double my = y + ry;
        int steps = (width + height) * 2 + 8;
        int lastX = Integer.MIN_VALUE;
        int lastY = Integer.MIN_VALUE;
        for (int i = 0; i <= steps; i++) {
            double deg = startAngle + (double) arcAngle * i / steps;
            double rad = deg * Math.PI / 180.0;
            int px = (int) Math.floor(mx + Math.cos(rad) * rx + 0.5);
            int py = (int) Math.floor(my - Math.sin(rad) * ry + 0.5);
            if (px != lastX || py != lastY) {
                plot(px + tx, py + ty);
                lastX = px;
                lastY = py;
            }
        }
    }

    // ---- images --------------------------------------------------------------------------

    public void drawImage(Image img, int x, int y, int anchor) {
        if (img == null) {
            throw new NullPointerException();
        }
        int w = img.width;
        int h = img.height;
        x = anchorX(x, w, anchor);
        y = anchorY(y, h, anchor);
        blit(img, 0, 0, w, h, 0, x + tx, y + ty);
    }

    public void drawRegion(Image src, int xSrc, int ySrc, int width, int height, int transform,
                           int xDest, int yDest, int anchor) {
        if (src == null) {
            throw new NullPointerException();
        }
        if (width <= 0 || height <= 0) {
            return;
        }
        if (xSrc < 0 || ySrc < 0 || xSrc + width > src.width || ySrc + height > src.height
                || transform < 0 || transform > 7) {
            throw new IllegalArgumentException("drawRegion: bad region");
        }
        boolean swap = transform == 4 || transform == 5 || transform == 6 || transform == 7;
        int dw = swap ? height : width;
        int dh = swap ? width : height;
        int x = anchorX(xDest, dw, anchor);
        int y = anchorY(yDest, dh, anchor);
        blit(src, xSrc, ySrc, width, height, transform, x + tx, y + ty);
    }

    static int anchorX(int x, int w, int anchor) {
        if ((anchor & HCENTER) != 0) {
            return x - w / 2;
        }
        if ((anchor & RIGHT) != 0) {
            return x - w;
        }
        return x;
    }

    static int anchorY(int y, int h, int anchor) {
        if ((anchor & VCENTER) != 0) {
            return y - h / 2;
        }
        if ((anchor & BOTTOM) != 0) {
            return y - h;
        }
        return y;
    }

    /** Destination x of source pixel (sx,sy) inside a w*h region for a Sprite transform. */
    static int mapX(int t, int sx, int sy, int w, int h) {
        switch (t) {
            case 0:
                return sx;
            case 1:
                return sx;
            case 2:
                return w - 1 - sx;
            case 3:
                return w - 1 - sx;
            case 4:
                return sy;
            case 5:
                return h - 1 - sy;
            case 6:
                return sy;
            default:
                return h - 1 - sy;
        }
    }

    static int mapY(int t, int sx, int sy, int w, int h) {
        switch (t) {
            case 0:
                return sy;
            case 1:
                return h - 1 - sy;
            case 2:
                return sy;
            case 3:
                return h - 1 - sy;
            case 4:
                return sx;
            case 5:
                return sx;
            case 6:
                return w - 1 - sx;
            default:
                return w - 1 - sx;
        }
    }

    private static int blend(int s, int d) {
        int a = s >>> 24;
        if (a == 255) {
            return s;
        }
        if (a == 0) {
            return d;
        }
        int ia = 255 - a;
        int r = (((s >> 16) & 0xff) * a + ((d >> 16) & 0xff) * ia) / 255;
        int g = (((s >> 8) & 0xff) * a + ((d >> 8) & 0xff) * ia) / 255;
        int b = ((s & 0xff) * a + (d & 0xff) * ia) / 255;
        int da = d >>> 24;
        int oa = a + da * ia / 255;
        return (oa << 24) | (r << 16) | (g << 8) | b;
    }

    /** Copies a (transformed) source region with its top-left at absolute (dx,dy). */
    private void blit(Image src, int sx, int sy, int w, int h, int transform, int dx, int dy) {
        int[] s = src.data;
        int sw = src.width;
        int[] d = target.data;
        if (transform == 0) {
            int x0 = dx < cx0 ? cx0 : dx;
            int y0 = dy < cy0 ? cy0 : dy;
            int x1 = dx + w > cx1 ? cx1 : dx + w;
            int y1 = dy + h > cy1 ? cy1 : dy + h;
            if (x0 >= x1 || y0 >= y1) {
                return;
            }
            int n = x1 - x0;
            for (int y = y0; y < y1; y++) {
                int si = (sy + y - dy) * sw + sx + (x0 - dx);
                int di = y * tw + x0;
                if (!src.hasAlpha) {
                    System.arraycopy(s, si, d, di, n);
                } else {
                    for (int i = 0; i < n; i++) {
                        int c = s[si + i];
                        int a = c >>> 24;
                        if (a == 255) {
                            d[di + i] = c;
                        } else if (a != 0) {
                            d[di + i] = blend(c, d[di + i]);
                        }
                    }
                }
            }
            return;
        }
        boolean swap = transform == 4 || transform == 5 || transform == 6 || transform == 7;
        int dw = swap ? h : w;
        int dh = swap ? w : h;
        int x0 = dx < cx0 ? cx0 : dx;
        int y0 = dy < cy0 ? cy0 : dy;
        int x1 = dx + dw > cx1 ? cx1 : dx + dw;
        int y1 = dy + dh > cy1 ? cy1 : dy + dh;
        if (x0 >= x1 || y0 >= y1) {
            return;
        }
        // invert the mapping: for each destination pixel find its source pixel
        for (int y = y0; y < y1; y++) {
            int ry = y - dy;
            for (int x = x0; x < x1; x++) {
                int rx = x - dx;
                int px;
                int py;
                switch (transform) {
                    case 1: // MIRROR_ROT180: (sx, h-1-sy)
                        px = rx;
                        py = h - 1 - ry;
                        break;
                    case 2: // MIRROR: (w-1-sx, sy)
                        px = w - 1 - rx;
                        py = ry;
                        break;
                    case 3: // ROT180
                        px = w - 1 - rx;
                        py = h - 1 - ry;
                        break;
                    case 4: // MIRROR_ROT270: (sy, sx)
                        px = ry;
                        py = rx;
                        break;
                    case 5: // ROT90: (h-1-sy, sx)
                        px = ry;
                        py = h - 1 - rx;
                        break;
                    case 6: // ROT270: (sy, w-1-sx)
                        px = w - 1 - ry;
                        py = rx;
                        break;
                    default: // MIRROR_ROT90: (h-1-sy, w-1-sx)
                        px = w - 1 - ry;
                        py = h - 1 - rx;
                        break;
                }
                int c = s[(sy + py) * sw + sx + px];
                int a = c >>> 24;
                int di = y * tw + x;
                if (a == 255) {
                    d[di] = c;
                } else if (a != 0) {
                    d[di] = blend(c, d[di]);
                }
            }
        }
    }

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height,
                        boolean processAlpha) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int ax = x + tx;
        int ay = y + ty;
        int[] d = target.data;
        for (int j = 0; j < height; j++) {
            int yy = ay + j;
            if (yy < cy0 || yy >= cy1) {
                continue;
            }
            for (int i = 0; i < width; i++) {
                int xx = ax + i;
                if (xx < cx0 || xx >= cx1) {
                    continue;
                }
                int c = rgbData[offset + j * scanlength + i];
                int di = yy * tw + xx;
                if (!processAlpha) {
                    d[di] = c | 0xff000000;
                } else {
                    d[di] = blend(c, d[di]);
                }
            }
        }
    }

    public void copyArea(int xSrc, int ySrc, int width, int height, int xDest, int yDest, int anchor) {
        if (!target.mutable) {
            throw new IllegalStateException("copyArea on immutable");
        }
        int[] tmp = new int[width * height];
        int sx = xSrc + tx;
        int sy = ySrc + ty;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int px = sx + i;
                int py = sy + j;
                tmp[j * width + i] = (px >= 0 && py >= 0 && px < tw && py < th) ? target.data[py * tw + px] : 0;
            }
        }
        int dx = anchorX(xDest, width, anchor) + tx;
        int dy = anchorY(yDest, height, anchor) + ty;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int px = dx + i;
                int py = dy + j;
                if (px >= cx0 && px < cx1 && py >= cy0 && py < cy1) {
                    target.data[py * tw + px] = tmp[j * width + i];
                }
            }
        }
    }

    // ---- text ----------------------------------------------------------------------------

    public void drawString(String str, int x, int y, int anchor) {
        if (str == null) {
            throw new NullPointerException();
        }
        BitmapFont bf = font.bitmap();
        int w = font.stringWidth(str);
        int ax = anchorX(x, w, anchor);
        int ay;
        if ((anchor & BASELINE) != 0) {
            ay = y - bf.baseline;
        } else if ((anchor & BOTTOM) != 0) {
            ay = y - bf.height;
        } else {
            ay = y;
        }
        bf.draw(target.data, tw, cx0, cy0, cx1, cy1, str, ax + tx, ay + ty, color, font.isBold());
        if (font.isUnderlined()) {
            int uy = ay + bf.baseline + 1;
            drawLine(ax, uy, ax + w - 1, uy);
        }
    }

    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        drawString(str.substring(offset, offset + len), x, y, anchor);
    }

    public void drawChar(char character, int x, int y, int anchor) {
        drawString(String.valueOf(character), x, y, anchor);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
        drawString(new String(data, offset, length), x, y, anchor);
    }
}
