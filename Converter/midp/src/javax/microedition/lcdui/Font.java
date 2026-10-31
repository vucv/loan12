package javax.microedition.lcdui;

import j2me.BitmapFont;

/** MIDP system font backed by bundled pixel fonts. */
public final class Font {
    public static final int FACE_SYSTEM = 0;
    public static final int FACE_MONOSPACE = 32;
    public static final int FACE_PROPORTIONAL = 64;
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_UNDERLINED = 4;
    public static final int SIZE_SMALL = 8;
    public static final int SIZE_MEDIUM = 0;
    public static final int SIZE_LARGE = 16;
    public static final int FONT_STATIC_TEXT = 0;
    public static final int FONT_INPUT_TEXT = 1;

    private static Font[] fonts = new Font[3 * 3 * 8];
    private static Font defaultFont;

    private final int face;
    private final int style;
    private final int size;
    private BitmapFont bmp;

    private Font(int face, int style, int size) {
        this.face = face;
        this.style = style;
        this.size = size;
    }

    BitmapFont bitmap() {
        if (bmp == null) {
            bmp = BitmapFont.get(size == SIZE_SMALL ? 0 : (size == SIZE_LARGE ? 2 : 1));
        }
        return bmp;
    }

    public static Font getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = getFont(FACE_SYSTEM, STYLE_PLAIN, SIZE_MEDIUM);
        }
        return defaultFont;
    }

    public static Font getFont(int fontSpecifier) {
        return getDefaultFont();
    }

    public static Font getFont(int face, int style, int size) {
        if ((face != FACE_SYSTEM && face != FACE_MONOSPACE && face != FACE_PROPORTIONAL)
                || (style & ~7) != 0
                || (size != SIZE_SMALL && size != SIZE_MEDIUM && size != SIZE_LARGE)) {
            throw new IllegalArgumentException("bad font");
        }
        int fi = face == FACE_SYSTEM ? 0 : (face == FACE_MONOSPACE ? 1 : 2);
        int si = size == SIZE_SMALL ? 0 : (size == SIZE_MEDIUM ? 1 : 2);
        int idx = (fi * 3 + si) * 8 + style;
        if (fonts[idx] == null) {
            fonts[idx] = new Font(face, style, size);
        }
        return fonts[idx];
    }

    public int getFace() {
        return face;
    }

    public int getStyle() {
        return style;
    }

    public int getSize() {
        return size;
    }

    public boolean isPlain() {
        return style == STYLE_PLAIN;
    }

    public boolean isBold() {
        return (style & STYLE_BOLD) != 0;
    }

    public boolean isItalic() {
        return (style & STYLE_ITALIC) != 0;
    }

    public boolean isUnderlined() {
        return (style & STYLE_UNDERLINED) != 0;
    }

    public int getHeight() {
        return bitmap().height;
    }

    public int getBaselinePosition() {
        return bitmap().baseline;
    }

    public int charWidth(char ch) {
        return bitmap().charWidth(ch) + (isBold() ? 1 : 0);
    }

    public int charsWidth(char[] ch, int offset, int length) {
        return stringWidth(new String(ch, offset, length));
    }

    public int stringWidth(String str) {
        return bitmap().stringWidth(str) + (isBold() ? str.length() : 0);
    }

    public int substringWidth(String str, int offset, int len) {
        return stringWidth(str.substring(offset, offset + len));
    }
}
