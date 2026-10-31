package javax.microedition.rms;

import j2me.Host;
import j2me.RecordEnumerationImpl;

/**
 * Record store kept in memory and written through to the host's persistent storage
 * (PlayerPrefs / files in Unity) after every change.
 */
public class RecordStore {
    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;

    private static RecordStore[] open = new RecordStore[8];
    private static int openCount;

    private final String name;
    private int refCount;
    private int nextId = 1;
    private int version;
    private long lastModified;
    private int[] ids = new int[8];
    private byte[][] records = new byte[8][];
    private int count;

    private RecordStore(String name) {
        this.name = name;
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
            throws RecordStoreException {
        if (recordStoreName == null || recordStoreName.length() == 0 || recordStoreName.length() > 32) {
            throw new IllegalArgumentException("record store name");
        }
        for (int i = 0; i < openCount; i++) {
            if (open[i].name.equals(recordStoreName)) {
                open[i].refCount++;
                return open[i];
            }
        }
        byte[] data = Host.backend.loadStore(recordStoreName);
        if (data == null && !createIfNecessary) {
            throw new RecordStoreNotFoundException(recordStoreName);
        }
        RecordStore rs = new RecordStore(recordStoreName);
        if (data != null) {
            rs.deserialize(data);
        } else {
            rs.save();
        }
        rs.refCount = 1;
        if (openCount == open.length) {
            RecordStore[] n = new RecordStore[openCount * 2];
            System.arraycopy(open, 0, n, 0, openCount);
            open = n;
        }
        open[openCount++] = rs;
        return rs;
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode,
                                              boolean writable) throws RecordStoreException {
        return openRecordStore(recordStoreName, createIfNecessary);
    }

    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException {
        for (int i = 0; i < openCount; i++) {
            if (open[i].name.equals(recordStoreName)) {
                throw new RecordStoreException("record store is open");
            }
        }
        if (Host.backend.loadStore(recordStoreName) == null) {
            throw new RecordStoreNotFoundException(recordStoreName);
        }
        Host.backend.deleteStore(recordStoreName);
    }

    public static String[] listRecordStores() {
        String[] l = Host.backend.listStores();
        if (l == null || l.length == 0) {
            return null;
        }
        return l;
    }

    public void closeRecordStore() throws RecordStoreException {
        checkOpen();
        refCount--;
        if (refCount > 0) {
            return;
        }
        for (int i = 0; i < openCount; i++) {
            if (open[i] == this) {
                openCount--;
                for (int k = i; k < openCount; k++) {
                    open[k] = open[k + 1];
                }
                open[openCount] = null;
                break;
            }
        }
    }

    private void checkOpen() throws RecordStoreNotOpenException {
        if (refCount <= 0) {
            throw new RecordStoreNotOpenException(name);
        }
    }

    private int indexOf(int id) {
        for (int i = 0; i < count; i++) {
            if (ids[i] == id) {
                return i;
            }
        }
        return -1;
    }

    public String getName() throws RecordStoreNotOpenException {
        checkOpen();
        return name;
    }

    public int getVersion() throws RecordStoreNotOpenException {
        checkOpen();
        return version;
    }

    public long getLastModified() throws RecordStoreNotOpenException {
        checkOpen();
        return lastModified;
    }

    public int getNumRecords() throws RecordStoreNotOpenException {
        checkOpen();
        return count;
    }

    public int getSize() throws RecordStoreNotOpenException {
        checkOpen();
        int s = 64;
        for (int i = 0; i < count; i++) {
            s += 8 + records[i].length;
        }
        return s;
    }

    public int getSizeAvailable() throws RecordStoreNotOpenException {
        checkOpen();
        return 1024 * 1024;
    }

    public int getNextRecordID() throws RecordStoreException {
        checkOpen();
        return nextId;
    }

    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreException {
        checkOpen();
        byte[] copy = new byte[numBytes];
        if (numBytes > 0) {
            System.arraycopy(data, offset, copy, 0, numBytes);
        }
        if (count == ids.length) {
            int[] ni = new int[count * 2];
            byte[][] nr = new byte[count * 2][];
            System.arraycopy(ids, 0, ni, 0, count);
            System.arraycopy(records, 0, nr, 0, count);
            ids = ni;
            records = nr;
        }
        int id = nextId++;
        ids[count] = id;
        records[count] = copy;
        count++;
        changed();
        return id;
    }

    public void deleteRecord(int recordId) throws RecordStoreException {
        checkOpen();
        int i = indexOf(recordId);
        if (i < 0) {
            throw new InvalidRecordIDException("" + recordId);
        }
        count--;
        for (int k = i; k < count; k++) {
            ids[k] = ids[k + 1];
            records[k] = records[k + 1];
        }
        records[count] = null;
        changed();
    }

    public int getRecordSize(int recordId) throws RecordStoreException {
        checkOpen();
        int i = indexOf(recordId);
        if (i < 0) {
            throw new InvalidRecordIDException("" + recordId);
        }
        return records[i].length;
    }

    public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreException {
        checkOpen();
        int i = indexOf(recordId);
        if (i < 0) {
            throw new InvalidRecordIDException("" + recordId);
        }
        byte[] r = records[i];
        System.arraycopy(r, 0, buffer, offset, r.length);
        return r.length;
    }

    public byte[] getRecord(int recordId) throws RecordStoreException {
        checkOpen();
        int i = indexOf(recordId);
        if (i < 0) {
            throw new InvalidRecordIDException("" + recordId);
        }
        byte[] r = records[i];
        if (r.length == 0) {
            return null;
        }
        byte[] copy = new byte[r.length];
        System.arraycopy(r, 0, copy, 0, r.length);
        return copy;
    }

    public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreException {
        checkOpen();
        int i = indexOf(recordId);
        if (i < 0) {
            throw new InvalidRecordIDException("" + recordId);
        }
        byte[] copy = new byte[numBytes];
        if (numBytes > 0) {
            System.arraycopy(newData, offset, copy, 0, numBytes);
        }
        records[i] = copy;
        changed();
    }

    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator,
                                              boolean keepUpdated) throws RecordStoreNotOpenException {
        checkOpen();
        return new RecordEnumerationImpl(this, filter, comparator);
    }

    public void addRecordListener(RecordListener listener) {
    }

    public void removeRecordListener(RecordListener listener) {
    }

    public void setMode(int authmode, boolean writable) throws RecordStoreException {
    }

    // ---- port runtime --------------------------------------------------------------------

    /** Record ids in insertion order (for RecordEnumerationImpl). */
    public int[] recordIds() {
        int[] r = new int[count];
        System.arraycopy(ids, 0, r, 0, count);
        return r;
    }

    private void changed() {
        version++;
        lastModified = Host.now();
        save();
    }

    private void save() {
        int size = 12;
        for (int i = 0; i < count; i++) {
            size += 8 + records[i].length;
        }
        byte[] b = new byte[size];
        int p = putInt(b, 0, nextId);
        p = putInt(b, p, version);
        p = putInt(b, p, count);
        for (int i = 0; i < count; i++) {
            p = putInt(b, p, ids[i]);
            p = putInt(b, p, records[i].length);
            System.arraycopy(records[i], 0, b, p, records[i].length);
            p += records[i].length;
        }
        Host.backend.saveStore(name, b);
    }

    private void deserialize(byte[] b) {
        nextId = getInt(b, 0);
        version = getInt(b, 4);
        int n = getInt(b, 8);
        int p = 12;
        ids = new int[n < 8 ? 8 : n];
        records = new byte[ids.length][];
        count = 0;
        for (int i = 0; i < n; i++) {
            int id = getInt(b, p);
            int len = getInt(b, p + 4);
            p += 8;
            byte[] r = new byte[len];
            System.arraycopy(b, p, r, 0, len);
            p += len;
            ids[count] = id;
            records[count] = r;
            count++;
        }
    }

    private static int putInt(byte[] b, int p, int v) {
        b[p] = (byte) (v >> 24);
        b[p + 1] = (byte) (v >> 16);
        b[p + 2] = (byte) (v >> 8);
        b[p + 3] = (byte) v;
        return p + 4;
    }

    private static int getInt(byte[] b, int p) {
        return ((b[p] & 0xff) << 24) | ((b[p + 1] & 0xff) << 16) | ((b[p + 2] & 0xff) << 8) | (b[p + 3] & 0xff);
    }
}
