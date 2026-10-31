package j2me;

/**
 * Small, dependency-free DEFLATE/zlib decoder (RFC 1950/1951), used by the PNG decoder.
 * Written in plain Java so the same code is translated to C# for Unity.
 */
public final class Inflate {
    private static final int[] LEN_BASE = {3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
            35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258};
    private static final int[] LEN_EXTRA = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
            3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0};
    private static final int[] DIST_BASE = {1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193,
            257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577};
    private static final int[] DIST_EXTRA = {0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6,
            7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13};
    private static final int[] CL_ORDER = {16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};

    private final byte[] in;
    private int pos;
    private final int end;
    private int bitBuf;
    private int bitCnt;

    private byte[] out;
    private int outLen;

    // Huffman tables: counts per length + symbols sorted by code
    private final int[] lenCount = new int[16];
    private final int[] lenSym = new int[288];
    private final int[] distCount = new int[16];
    private final int[] distSym = new int[32];

    private Inflate(byte[] data, int off, int len, int sizeHint) {
        in = data;
        pos = off;
        end = off + len;
        out = new byte[sizeHint > 0 ? sizeHint : (len * 4 + 64)];
    }

    /** Decodes a zlib stream (2-byte header, deflate data, adler32). */
    public static byte[] zlib(byte[] data, int off, int len, int sizeHint) {
        if (len < 2) {
            throw new IllegalArgumentException("zlib: truncated");
        }
        int cmf = data[off] & 0xff;
        int flg = data[off + 1] & 0xff;
        if ((cmf & 0x0f) != 8 || ((cmf << 8) | flg) % 31 != 0 || (flg & 0x20) != 0) {
            throw new IllegalArgumentException("zlib: bad header");
        }
        Inflate f = new Inflate(data, off + 2, len - 2, sizeHint);
        f.run();
        if (f.outLen == f.out.length) {
            return f.out;
        }
        byte[] r = new byte[f.outLen];
        System.arraycopy(f.out, 0, r, 0, f.outLen);
        return r;
    }

    private int bits(int need) {
        int v = bitBuf;
        while (bitCnt < need) {
            if (pos >= end) {
                throw new IllegalArgumentException("inflate: out of input");
            }
            v |= (in[pos++] & 0xff) << bitCnt;
            bitCnt += 8;
        }
        bitBuf = v >> need;
        bitCnt -= need;
        return v & ((1 << need) - 1);
    }

    private void put(int b) {
        if (outLen == out.length) {
            byte[] n = new byte[out.length * 2 + 1024];
            System.arraycopy(out, 0, n, 0, outLen);
            out = n;
        }
        out[outLen++] = (byte) b;
    }

    private void run() {
        int last;
        do {
            last = bits(1);
            int type = bits(2);
            if (type == 0) {
                stored();
            } else if (type == 1) {
                fixedTables();
                codes();
            } else if (type == 2) {
                dynamicTables();
                codes();
            } else {
                throw new IllegalArgumentException("inflate: bad block type");
            }
        } while (last == 0);
    }

    private void stored() {
        bitBuf = 0;
        bitCnt = 0;
        if (pos + 4 > end) {
            throw new IllegalArgumentException("inflate: truncated stored block");
        }
        int len = (in[pos] & 0xff) | ((in[pos + 1] & 0xff) << 8);
        pos += 4;
        if (pos + len > end) {
            throw new IllegalArgumentException("inflate: truncated stored block");
        }
        for (int i = 0; i < len; i++) {
            put(in[pos++]);
        }
    }

    private static void build(int[] count, int[] sym, int[] lengths, int off, int n) {
        for (int i = 0; i < 16; i++) {
            count[i] = 0;
        }
        for (int i = 0; i < n; i++) {
            count[lengths[off + i]]++;
        }
        count[0] = 0;
        int[] offs = new int[16];
        for (int i = 1; i < 16; i++) {
            offs[i] = offs[i - 1] + count[i - 1];
        }
        for (int i = 0; i < n; i++) {
            int l = lengths[off + i];
            if (l != 0) {
                sym[offs[l]++] = i;
            }
        }
    }

    private int decode(int[] count, int[] sym) {
        int code = 0;
        int first = 0;
        int index = 0;
        for (int len = 1; len < 16; len++) {
            code |= bits(1);
            int c = count[len];
            if (code - c < first) {
                return sym[index + (code - first)];
            }
            index += c;
            first += c;
            first <<= 1;
            code <<= 1;
        }
        throw new IllegalArgumentException("inflate: bad code");
    }

    private void fixedTables() {
        int[] l = new int[320];
        int i = 0;
        for (; i < 144; i++) {
            l[i] = 8;
        }
        for (; i < 256; i++) {
            l[i] = 9;
        }
        for (; i < 280; i++) {
            l[i] = 7;
        }
        for (; i < 288; i++) {
            l[i] = 8;
        }
        build(lenCount, lenSym, l, 0, 288);
        for (i = 0; i < 30; i++) {
            l[288 + i] = 5;
        }
        build(distCount, distSym, l, 288, 30);
    }

    private void dynamicTables() {
        int nlen = bits(5) + 257;
        int ndist = bits(5) + 1;
        int ncode = bits(4) + 4;
        int[] lengths = new int[320];
        for (int i = 0; i < ncode; i++) {
            lengths[CL_ORDER[i]] = bits(3);
        }
        int[] cc = new int[16];
        int[] cs = new int[19];
        build(cc, cs, lengths, 0, 19);
        for (int i = 0; i < 19; i++) {
            lengths[i] = 0;
        }
        int idx = 0;
        while (idx < nlen + ndist) {
            int s = decode(cc, cs);
            if (s < 16) {
                lengths[idx++] = s;
            } else {
                int rep;
                int val = 0;
                if (s == 16) {
                    if (idx == 0) {
                        throw new IllegalArgumentException("inflate: repeat with no length");
                    }
                    val = lengths[idx - 1];
                    rep = 3 + bits(2);
                } else if (s == 17) {
                    rep = 3 + bits(3);
                } else {
                    rep = 11 + bits(7);
                }
                if (idx + rep > nlen + ndist) {
                    throw new IllegalArgumentException("inflate: too many lengths");
                }
                while (rep-- > 0) {
                    lengths[idx++] = val;
                }
            }
        }
        build(lenCount, lenSym, lengths, 0, nlen);
        build(distCount, distSym, lengths, nlen, ndist);
    }

    private void codes() {
        while (true) {
            int s = decode(lenCount, lenSym);
            if (s < 256) {
                put(s);
            } else if (s == 256) {
                return;
            } else {
                s -= 257;
                if (s >= 29) {
                    throw new IllegalArgumentException("inflate: bad length symbol");
                }
                int len = LEN_BASE[s] + bits(LEN_EXTRA[s]);
                int ds = decode(distCount, distSym);
                if (ds >= 30) {
                    throw new IllegalArgumentException("inflate: bad distance symbol");
                }
                int dist = DIST_BASE[ds] + bits(DIST_EXTRA[ds]);
                if (dist > outLen) {
                    throw new IllegalArgumentException("inflate: distance too far");
                }
                for (int i = 0; i < len; i++) {
                    put(out[outLen - dist]);
                }
            }
        }
    }
}
