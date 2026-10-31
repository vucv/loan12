package javax.microedition.lcdui;

import j2me.Host;
import j2me.PngDecoder;
import j2me.ResourceStream;

import java.io.IOException;
import java.io.InputStream;

/** MIDP 2.0 Image backed by a 0xAARRGGBB int array. */
public class Image {
    int width;
    int height;
    int[] data;
    boolean mutable;
    /** True when some pixel is not fully opaque (lets drawImage take the fast copy path when false). */
    boolean hasAlpha;
    Graphics graphics;

    Image(int w, int h, int[] pixels, boolean isMutable) {
        width = w;
        height = h;
        data = pixels;
        mutable = isMutable;
    }

    /** Raw pixel access for the port runtime. */
    public int[] pixels() {
        return data;
    }

    void updateAlphaFlag() {
        hasAlpha = false;
        int n = width * height;
        for (int i = 0; i < n; i++) {
            if ((data[i] >>> 24) != 0xff) {
                hasAlpha = true;
                return;
            }
        }
    }

    public static Image createImage(int w, int h) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Image size");
        }
        int[] px = new int[w * h];
        for (int i = 0; i < px.length; i++) {
            px[i] = 0xffffffff;
        }
        return new Image(w, h, px, true);
    }

    public static Image createImage(Image source) {
        if (!source.mutable) {
            return source;
        }
        int[] px = new int[source.width * source.height];
        System.arraycopy(source.data, 0, px, 0, px.length);
        Image img = new Image(source.width, source.height, px, false);
        img.hasAlpha = source.hasAlpha;
        return img;
    }

    public static Image createImage(String name) throws IOException {
        byte[] b = Host.backend.getResource(name);
        if (b == null) {
            throw new IOException("resource not found: " + name);
        }
        return createImage(b, 0, b.length);
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        PngDecoder p;
        try {
            p = PngDecoder.decode(imageData, imageOffset, imageLength);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Image decode failed: " + e.getMessage());
        }
        Image img = new Image(p.width, p.height, p.argb, false);
        img.updateAlphaFlag();
        return img;
    }

    public static Image createImage(InputStream stream) throws IOException {
        byte[] b;
        if (stream instanceof ResourceStream) {
            b = ((ResourceStream) stream).remainingBytes();
        } else {
            b = ResourceStream.readAll(stream);
        }
        return createImage(b, 0, b.length);
    }

    public static Image createImage(Image image, int x, int y, int w, int h, int transform) {
        if (w <= 0 || h <= 0 || x < 0 || y < 0 || x + w > image.width || y + h > image.height) {
            throw new IllegalArgumentException("Image region");
        }
        boolean swap = transform == Sprite_TRANS_ROT90 || transform == Sprite_TRANS_ROT270
                || transform == Sprite_TRANS_MIRROR_ROT90 || transform == Sprite_TRANS_MIRROR_ROT270;
        int dw = swap ? h : w;
        int dh = swap ? w : h;
        int[] px = new int[dw * dh];
        for (int sy = 0; sy < h; sy++) {
            for (int sx = 0; sx < w; sx++) {
                int c = image.data[(y + sy) * image.width + x + sx];
                int tx = Graphics.mapX(transform, sx, sy, w, h);
                int ty = Graphics.mapY(transform, sx, sy, w, h);
                px[ty * dw + tx] = c;
            }
        }
        Image img = new Image(dw, dh, px, false);
        img.updateAlphaFlag();
        return img;
    }

    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image size");
        }
        int[] px = new int[width * height];
        for (int i = 0; i < px.length; i++) {
            px[i] = processAlpha ? rgb[i] : (rgb[i] | 0xff000000);
        }
        Image img = new Image(width, height, px, false);
        img.updateAlphaFlag();
        return img;
    }

    // Sprite transform constants (javax.microedition.lcdui.game.Sprite)
    static final int Sprite_TRANS_NONE = 0;
    static final int Sprite_TRANS_MIRROR_ROT180 = 1;
    static final int Sprite_TRANS_MIRROR = 2;
    static final int Sprite_TRANS_ROT180 = 3;
    static final int Sprite_TRANS_MIRROR_ROT270 = 4;
    static final int Sprite_TRANS_ROT90 = 5;
    static final int Sprite_TRANS_ROT270 = 6;
    static final int Sprite_TRANS_MIRROR_ROT90 = 7;

    public Graphics getGraphics() {
        if (!mutable) {
            throw new IllegalStateException("Image is immutable");
        }
        return new Graphics(this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > width || y + h > height) {
            throw new IllegalArgumentException("getRGB region");
        }
        for (int j = 0; j < h; j++) {
            System.arraycopy(data, (y + j) * width + x, rgbData, offset + j * scanlength, w);
        }
    }
}
