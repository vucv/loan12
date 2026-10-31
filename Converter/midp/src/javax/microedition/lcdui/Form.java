package javax.microedition.lcdui;

/** Minimal Form: only string items (enough for the game's debug log screen). */
public class Form extends Screen {
    private String[] items = new String[8];
    private int count;

    public Form(String title) {
        this.title = title;
    }

    public int append(String str) {
        if (count == items.length) {
            String[] n = new String[count * 2];
            System.arraycopy(items, 0, n, 0, count);
            items = n;
        }
        items[count] = str;
        return count++;
    }

    public void delete(int itemNum) {
        if (itemNum < 0 || itemNum >= count) {
            throw new IndexOutOfBoundsException();
        }
        count--;
        for (int i = itemNum; i < count; i++) {
            items[i] = items[i + 1];
        }
        items[count] = null;
    }

    public void deleteAll() {
        for (int i = 0; i < count; i++) {
            items[i] = null;
        }
        count = 0;
    }

    public int size() {
        return count;
    }

    public String getText(int i) {
        return items[i];
    }
}
