import javax.microedition.rms.RecordStore;

public class l {
    public static RecordStore a(String paramString, boolean paramBoolean) {
        try {
            return RecordStore.openRecordStore(paramString, paramBoolean);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return null;
    }

    public static void a(RecordStore paramRecordStore) {
        try {
            paramRecordStore.closeRecordStore();
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    public static byte[] a(RecordStore paramRecordStore, int paramInt) {
        byte[] arrayOfByte = (byte[]) null;
        try {
            arrayOfByte = paramRecordStore.getRecord(paramInt);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            arrayOfByte = (byte[]) null;
        }
        return arrayOfByte;
    }

    public static int a(RecordStore paramRecordStore, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
        try {
            paramRecordStore.setRecord(paramInt1, paramArrayOfByte, paramInt2, paramInt3);
            return paramInt1;
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return -1;
    }

    public static int a(RecordStore paramRecordStore, int paramInt, byte[] paramArrayOfByte) {
        return a(paramRecordStore, paramInt, paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static int a(RecordStore paramRecordStore, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        try {
            return paramRecordStore.addRecord(paramArrayOfByte, paramInt1, paramInt2);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return -1;
    }

    public static int a(RecordStore paramRecordStore, byte[] paramArrayOfByte) {
        return a(paramRecordStore, paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static int b(RecordStore paramRecordStore) {
        try {
            return paramRecordStore.getNumRecords();
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return 0;
    }

    public static void a(String paramString) {
        try {
            RecordStore.deleteRecordStore(paramString);
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     l

 * JD-Core Version:    0.7.0.1

 */