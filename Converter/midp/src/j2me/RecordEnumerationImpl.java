package j2me;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RecordEnumerationImpl implements RecordEnumeration {
    private final RecordStore store;
    private final RecordFilter filter;
    private final RecordComparator comparator;
    private int[] ids;
    private int index;

    public RecordEnumerationImpl(RecordStore store, RecordFilter filter, RecordComparator comparator) {
        this.store = store;
        this.filter = filter;
        this.comparator = comparator;
        rebuild();
    }

    public void rebuild() {
        int[] all = store.recordIds();
        int[] keep = new int[all.length];
        byte[][] data = new byte[all.length][];
        int n = 0;
        for (int i = 0; i < all.length; i++) {
            byte[] r = null;
            try {
                r = store.getRecord(all[i]);
            } catch (RecordStoreException e) {
                continue;
            }
            if (filter != null && !filter.matches(r)) {
                continue;
            }
            keep[n] = all[i];
            data[n] = r;
            n++;
        }
        if (comparator != null) {
            for (int i = 1; i < n; i++) {
                for (int j = i; j > 0 && comparator.compare(data[j - 1], data[j]) == RecordComparator.FOLLOWS; j--) {
                    int ti = keep[j];
                    keep[j] = keep[j - 1];
                    keep[j - 1] = ti;
                    byte[] td = data[j];
                    data[j] = data[j - 1];
                    data[j - 1] = td;
                }
            }
        }
        ids = new int[n];
        System.arraycopy(keep, 0, ids, 0, n);
        index = 0;
    }

    public int numRecords() {
        return ids.length;
    }

    public byte[] nextRecord() throws RecordStoreException {
        return store.getRecord(nextRecordId());
    }

    public int nextRecordId() throws InvalidRecordIDException {
        if (index >= ids.length) {
            throw new InvalidRecordIDException("end");
        }
        return ids[index++];
    }

    public byte[] previousRecord() throws RecordStoreException {
        return store.getRecord(previousRecordId());
    }

    public int previousRecordId() throws InvalidRecordIDException {
        if (index <= 0) {
            throw new InvalidRecordIDException("start");
        }
        return ids[--index];
    }

    public boolean hasNextElement() {
        return index < ids.length;
    }

    public boolean hasPreviousElement() {
        return index > 0;
    }

    public void reset() {
        index = 0;
    }

    public void keepUpdated(boolean keepUpdated) {
    }

    public boolean isKeptUpdated() {
        return false;
    }

    public void destroy() {
    }
}
