package j2me;

/**
 * PNG decoder producing 0xAARRGGBB pixels. Supports every PNG colour type, bit depths 1..16,
 * palettes with tRNS alpha and Adam7 interlacing. CRCs are not checked (J2ME games sometimes
 * patch PLTE bytes at runtime without fixing them).
 */
public final class PngDecoder {
    public int width;
    public int height;
    public int[] argb;

    private int bitDepth;
    private int colorType;
    private int interlace;
    private int[] palette;      // ARGB
    private int trnsGray = -1;
    private int trnsR = -1;
    private int trnsG = -1;
    private int trnsB = -1;

    public static PngDecoder decode(byte[] d, int off, int len) {
        PngDecoder p = new PngDecoder();
        p.run(d, off, len);
        return p;
    }

    private static int u32(byte[] d, int o) {
        return ((d[o] & 0xff) << 24) | ((d[o + 1] & 0xff) << 16) | ((d[o + 2] & 0xff) << 8) | (d[o + 3] & 0xff);
    }

    private void run(byte[] d, int off, int len) {
        int end = off + len;
        if (len < 8 || (d[off] & 0xff) != 0x89 || d[off + 1] != 'P' || d[off + 2] != 'N' || d[off + 3] != 'G') {
            throw new IllegalArgumentException("not a PNG");
        }
        int p = off + 8;
        byte[] idat = new byte[len];
        int idatLen = 0;
        boolean gotHeader = false;
        while (p + 8 <= end) {
            int clen = u32(d, p);
            int type = u32(d, p + 4);
            int data = p + 8;
            if (clen < 0 || data + clen > end) {
                break;
            }
            if (type == 0x49484452) { // IHDR
                width = u32(d, data);
                height = u32(d, data + 4);
                bitDepth = d[data + 8] & 0xff;
                colorType = d[data + 9] & 0xff;
                interlace = d[data + 12] & 0xff;
                gotHeader = true;
            } else if (type == 0x504c5445) { // PLTE
                int n = clen / 3;
                palette = new int[256];
                for (int i = 0; i < 256; i++) {
                    palette[i] = 0xff000000;
                }
                for (int i = 0; i < n && i < 256; i++) {
                    palette[i] = 0xff000000 | ((d[data + i * 3] & 0xff) << 16)
                            | ((d[data + i * 3 + 1] & 0xff) << 8) | (d[data + i * 3 + 2] & 0xff);
                }
            } else if (type == 0x74524e53) { // tRNS
                if (colorType == 3 && palette != null) {
                    for (int i = 0; i < clen && i < 256; i++) {
                        palette[i] = (palette[i] & 0x00ffffff) | ((d[data + i] & 0xff) << 24);
                    }
                } else if (colorType == 0 && clen >= 2) {
                    trnsGray = ((d[data] & 0xff) << 8) | (d[data + 1] & 0xff);
                } else if (colorType == 2 && clen >= 6) {
                    trnsR = ((d[data] & 0xff) << 8) | (d[data + 1] & 0xff);
                    trnsG = ((d[data + 2] & 0xff) << 8) | (d[data + 3] & 0xff);
                    trnsB = ((d[data + 4] & 0xff) << 8) | (d[data + 5] & 0xff);
                }
            } else if (type == 0x49444154) { // IDAT
                System.arraycopy(d, data, idat, idatLen, clen);
                idatLen += clen;
            } else if (type == 0x49454e44) { // IEND
                break;
            }
            p = data + clen + 4;
        }
        if (!gotHeader || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("PNG: missing IHDR");
        }
        int channels = channelsOf(colorType);
        int bpp = (channels * bitDepth + 7) / 8;
        int rowBytes = (width * channels * bitDepth + 7) / 8;
        byte[] raw = Inflate.zlib(idat, 0, idatLen, height * (rowBytes + 1) + (interlace != 0 ? height * 8 : 0));
        argb = new int[width * height];
        if (interlace == 0) {
            unfilterAndStore(raw, 0, width, height, bpp, channels, 0, 0, 1, 1);
        } else {
            int[] xs = {0, 4, 0, 2, 0, 1, 0};
            int[] ys = {0, 0, 4, 0, 2, 0, 1};
            int[] dx = {8, 8, 4, 4, 2, 2, 1};
            int[] dy = {8, 8, 8, 4, 4, 2, 2};
            int rp = 0;
            for (int pass = 0; pass < 7; pass++) {
                int pw = (width - xs[pass] + dx[pass] - 1) / dx[pass];
                int ph = (height - ys[pass] + dy[pass] - 1) / dy[pass];
                if (pw <= 0 || ph <= 0) {
                    continue;
                }
                rp = unfilterAndStore(raw, rp, pw, ph, bpp, channels, xs[pass], ys[pass], dx[pass], dy[pass]);
            }
        }
    }

    private static int channelsOf(int ct) {
        if (ct == 0 || ct == 3) {
            return 1;
        }
        if (ct == 2) {
            return 3;
        }
        if (ct == 4) {
            return 2;
        }
        if (ct == 6) {
            return 4;
        }
        throw new IllegalArgumentException("PNG: bad colour type");
    }

    private int unfilterAndStore(byte[] raw, int rp, int w, int h, int bpp, int channels,
                                 int x0, int y0, int dx, int dy) {
        int rowBytes = (w * channels * bitDepth + 7) / 8;
        byte[] prev = new byte[rowBytes];
        byte[] cur = new byte[rowBytes];
        for (int y = 0; y < h; y++) {
            int filter = raw[rp++] & 0xff;
            System.arraycopy(raw, rp, cur, 0, rowBytes);
            rp += rowBytes;
            for (int i = 0; i < rowBytes; i++) {
                int a = i >= bpp ? cur[i - bpp] & 0xff : 0;
                int b = prev[i] & 0xff;
                int c = i >= bpp ? prev[i - bpp] & 0xff : 0;
                int x = cur[i] & 0xff;
                if (filter == 1) {
                    x += a;
                } else if (filter == 2) {
                    x += b;
                } else if (filter == 3) {
                    x += (a + b) >> 1;
                } else if (filter == 4) {
                    int pp = a + b - c;
                    int pa = pp > a ? pp - a : a - pp;
                    int pb = pp > b ? pp - b : b - pp;
                    int pc = pp > c ? pp - c : c - pp;
                    if (pa <= pb && pa <= pc) {
                        x += a;
                    } else if (pb <= pc) {
                        x += b;
                    } else {
                        x += c;
                    }
                }
                cur[i] = (byte) x;
            }
            int row = (y0 + y * dy) * width;
            for (int i = 0; i < w; i++) {
                argb[row + x0 + i * dx] = pixel(cur, i, channels);
            }
            byte[] t = prev;
            prev = cur;
            cur = t;
        }
        return rp;
    }

    private int sample(byte[] row, int index) {
        if (bitDepth == 8) {
            return row[index] & 0xff;
        }
        if (bitDepth == 16) {
            return ((row[index * 2] & 0xff) << 8) | (row[index * 2 + 1] & 0xff);
        }
        int perByte = 8 / bitDepth;
        int b = row[index / perByte] & 0xff;
        int shift = 8 - bitDepth * (index % perByte + 1);
        return (b >> shift) & ((1 << bitDepth) - 1);
    }

    private int to8(int v) {
        if (bitDepth == 8) {
            return v;
        }
        if (bitDepth == 16) {
            return v >> 8;
        }
        if (bitDepth == 1) {
            return v * 255;
        }
        if (bitDepth == 2) {
            return v * 85;
        }
        return v * 17; // 4 bit
    }

    private int pixel(byte[] row, int i, int ch) {
        if (colorType == 3) {
            int idx = sample(row, i);
            return palette != null ? palette[idx & 0xff] : 0xff000000;
        }
        if (colorType == 0) {
            int g = sample(row, i);
            int a = (g == trnsGray) ? 0 : 255;
            int v = to8(g);
            return (a << 24) | (v << 16) | (v << 8) | v;
        }
        if (colorType == 4) {
            int v = to8(sample(row, i * 2));
            int a = to8(sample(row, i * 2 + 1));
            return (a << 24) | (v << 16) | (v << 8) | v;
        }
        if (colorType == 2) {
            int r = sample(row, i * 3);
            int g = sample(row, i * 3 + 1);
            int b = sample(row, i * 3 + 2);
            int a = (r == trnsR && g == trnsG && b == trnsB) ? 0 : 255;
            return (a << 24) | (to8(r) << 16) | (to8(g) << 8) | to8(b);
        }
        int r = to8(sample(row, i * 4));
        int g = to8(sample(row, i * 4 + 1));
        int b = to8(sample(row, i * 4 + 2));
        int a = to8(sample(row, i * 4 + 3));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
