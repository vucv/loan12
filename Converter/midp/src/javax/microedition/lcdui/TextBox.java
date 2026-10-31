package javax.microedition.lcdui;

/** Native text entry screen; the host shows it with its own UI (see Display.getCurrent()). */
public class TextBox extends Screen {
    private String text;
    private int maxSize;
    private int constraints;

    public TextBox(String title, String text, int maxSize, int constraints) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize");
        }
        this.title = title;
        this.maxSize = maxSize;
        this.constraints = constraints;
        setString(text);
    }

    public String getString() {
        return text;
    }

    public void setString(String s) {
        if (s == null) {
            s = "";
        }
        if (s.length() > maxSize) {
            s = s.substring(0, maxSize);
        }
        text = s;
    }

    public int size() {
        return text.length();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int setMaxSize(int m) {
        maxSize = m;
        setString(text);
        return maxSize;
    }

    public int getConstraints() {
        return constraints;
    }

    public void setConstraints(int c) {
        constraints = c;
    }

    public void insert(String src, int position) {
        String s = text.substring(0, position) + src + text.substring(position);
        setString(s);
    }

    public void delete(int offset, int length) {
        setString(text.substring(0, offset) + text.substring(offset + length));
    }

    public int getCaretPosition() {
        return text.length();
    }
}
