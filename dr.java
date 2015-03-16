import javax.microedition.rms.RecordStore;

public class dr {
    private String a;
    private int b;
    private int c;
    private di d;
    private di e;
    private di f;
    private int g;

    private dr(String paramString, int paramInt) {
        this(paramString, paramInt, 2048);
    }

    private dr(String paramString, int paramInt1, int paramInt2) {
        this.c = paramInt2;
        this.a = paramString;
        this.b = paramInt1;
        this.d = new di();
        this.e = new di();
        this.f = new di();
        a();
    }

    private byte[] a(RecordStore paramRecordStore) {
        byte[] arrayOfByte = new byte[this.c];
        l.a(paramRecordStore, arrayOfByte);
        l.a(paramRecordStore, arrayOfByte);
        l.a(paramRecordStore, new byte[]{1});
        return arrayOfByte;
    }

    private void a() {
        RecordStore localRecordStore = l.a(this.a, false);
        try {
            byte[] arrayOfByte1 = (byte[]) null;
            if ((localRecordStore == null) || (l.b(localRecordStore) < 3)) {
                l.a(this.a);
                localRecordStore = l.a(this.a, true);
                arrayOfByte1 = a(localRecordStore);
                this.g = 1;
            } else {
                this.g = 0;
                byte[] arrayOfByte2 = l.a(localRecordStore, 3);
                if (arrayOfByte2 != null) {
                    this.g = arrayOfByte2[0];
                }
                if ((this.g < 1) || (this.g > 2)) {
                    this.g = 1;
                }
                arrayOfByte1 = l.a(localRecordStore, this.g);
            }
            int i = l.b(localRecordStore);
            int j = 3;
            l.a(localRecordStore);
            int k = 0;
            int m = ai.a(arrayOfByte1, k);
            k += 4;
            for (int n = 0; n < m; n++) {
                bd localbd = new bd();
                k += localbd.a(arrayOfByte1, k);
                this.d.a(localbd);
                j += localbd.c.length;
            }
            n = ai.a(arrayOfByte1, k);
            k += 4;
            for (int i1 = 0; i1 < n; i1++) {
                this.e.a(new Integer(ai.a(arrayOfByte1, k)));
                k += 4;
            }
            j += this.e.b();
            if (i - 3 > j) {
                a(i);
            }
        } catch (Throwable localThrowable) {
            l.a(localRecordStore);
            localThrowable.printStackTrace();
        }
    }

    private void a(int paramInt) {
        boolean[] arrayOfBoolean = new boolean[paramInt];
        for (int i = this.d.b() - 1; i > -1; i--) {
            bd localbd = (bd) this.d.b(i);
            for (int k = localbd.c.length - 1; k > -1; k--) {
                arrayOfBoolean[localbd.c[k]] = true;
            }
        }
        for (i = this.e.b() - 1; i > -1; i--) {
            int j = ((Integer) this.e.b(i)).intValue();
            arrayOfBoolean[j] = true;
        }
        for (i = arrayOfBoolean.length - 1; i > 2; i--) {
            if (arrayOfBoolean[i] == 0) {
                this.e.a(new Integer(i));
            }
        }
    }

    private synchronized byte[] b(int paramInt) {
        RecordStore localRecordStore = l.a(this.a, false);
        try {
            bd localbd = d(paramInt);
            if ((localbd == null) || (localbd.c == null) || (localRecordStore == null)) {
                return null;
            }
            if (localbd.d != null) {
                return localbd.d;
            }
            byte[] arrayOfByte1 = new byte[localbd.b];
            int i = 0;
            int[] arrayOfInt = localbd.c;
            for (int j = 0; j < arrayOfInt.length; j++) {
                byte[] arrayOfByte2 = l.a(localRecordStore, arrayOfInt[j]);
                if (arrayOfByte2 == null) {
                    this.d.b(localbd);
                    b(localbd.c, 0, localbd.c.length);
                    return null;
                }
                int k = arrayOfByte2.length;
                if (k > arrayOfByte1.length - i) {
                    k = arrayOfByte1.length - i;
                }
                System.arraycopy(arrayOfByte2, 0, arrayOfByte1, i, k);
                i += k;
            }
            l.a(localRecordStore);
            return arrayOfByte1;
        } catch (Throwable localThrowable) {
            l.a(localRecordStore);
            localThrowable.printStackTrace();
        }
        return null;
    }

    private synchronized void a(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
        if (paramArrayOfByte == null) {
            return;
        }
        try {
            bd localbd = d(paramInt1);
            if (localbd != null) {
                if (localbd.c != null) {
                    b(localbd.c, 0, localbd.c.length);
                }
            } else {
                localbd = new bd();
                this.d.a(localbd);
            }
            localbd.a = paramInt1;
            localbd.b = paramInt3;
            int i = paramInt3 / this.b;
            if (paramInt3 % this.b != 0) {
                i++;
            }
            int[] arrayOfInt = new int[i];
            for (int j = 0; j < arrayOfInt.length; j++) {
                int k = this.e.b() - 1;
                if (k > -1) {
                    arrayOfInt[j] = ((Integer) this.e.b(k)).intValue();
                    this.e.a(k);
                } else {
                    arrayOfInt[j] = -1;
                }
            }
            localbd.c = arrayOfInt;
            localbd.d = new byte[paramInt3];
            System.arraycopy(paramArrayOfByte, paramInt2, localbd.d, 0, paramInt3);
        } catch (Throwable localThrowable) {
            c(paramInt1);
            localThrowable.printStackTrace();
        }
    }

    private synchronized void b() {
        RecordStore localRecordStore = l.a(this.a, false);
        try {
            if (localRecordStore == null) {
                localRecordStore = l.a(this.a, true);
                a(localRecordStore);
            }
            for (int i = this.d.b() - 1; i > -1; i--) {
                bd localbd1 = (bd) this.d.b(i);
                if (localbd1.d != null) {
                    a(localRecordStore, localbd1.d, localbd1.c);
                    localbd1.d = null;
                }
            }
            byte[] arrayOfByte1 = new byte[this.c];
            int j = 0;
            System.arraycopy(ai.a(this.d.b()), 0, arrayOfByte1, j, 4);
            j += 4;
            for (int k = this.d.b() - 1; k > -1; k--) {
                bd localbd2 = (bd) this.d.b(k);
                byte[] arrayOfByte2 = localbd2.a();
                System.arraycopy(arrayOfByte2, 0, arrayOfByte1, j, arrayOfByte2.length);
                j += arrayOfByte2.length;
            }
            for (k = this.f.b() - 1; k > -1; k--) {
                this.e.a(this.f.b(k));
            }
            this.f.a();
            System.arraycopy(ai.a(this.e.b()), 0, arrayOfByte1, j, 4);
            j += 4;
            for (k = this.e.b() - 1; k > -1; k--) {
                int m = ((Integer) this.e.b(k)).intValue();
                System.arraycopy(ai.a(m), 0, arrayOfByte1, j, 4);
                j += 4;
            }
            k = this.g == 1 ? 2 : 1;
            l.a(localRecordStore, k, arrayOfByte1);
            l.a(localRecordStore, 3, new byte[]{(byte) k});
            l.a(localRecordStore);
            this.g = k;
        } catch (Throwable localThrowable) {
            l.a(localRecordStore);
            localThrowable.printStackTrace();
        }
    }

    private void c(int paramInt) {
        try {
            for (int i = this.d.b() - 1; i > -1; i--) {
                bd localbd = (bd) this.d.b(i);
                if (localbd.a == paramInt) {
                    this.d.a(i);
                    if (localbd.c != null) {
                        a(localbd.c, 0, localbd.c.length);
                    }
                }
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    private bd d(int paramInt) {
        try {
            for (int i = this.d.b() - 1; i > -1; i--) {
                bd localbd = (bd) this.d.b(i);
                if (localbd.a == paramInt) {
                    return localbd;
                }
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return null;
    }

    private void a(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        try {
            if (paramArrayOfInt == null) {
                return;
            }
            paramInt2 = paramInt1 + paramInt2;
            for (int i = paramInt1; i < paramInt2; i++) {
                this.e.a(new Integer(paramArrayOfInt[i]));
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    private void b(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        try {
            if (paramArrayOfInt == null) {
                return;
            }
            paramInt2 = paramInt1 + paramInt2;
            for (int i = paramInt1; i < paramInt2; i++) {
                this.f.a(new Integer(paramArrayOfInt[i]));
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    private void a(RecordStore paramRecordStore, byte[] paramArrayOfByte, int[] paramArrayOfInt) {
        try {
            byte[] arrayOfByte = new byte[this.b];
            int i = 0;
            for (int j = 0; j < paramArrayOfInt.length; j++) {
                int k = paramArrayOfByte.length - i;
                if (k > arrayOfByte.length) {
                    k = arrayOfByte.length;
                }
                System.arraycopy(paramArrayOfByte, i, arrayOfByte, 0, k);
                i += k;
                if (paramArrayOfInt[j] > 3) {
                    l.a(paramRecordStore, paramArrayOfInt[j], arrayOfByte);
                } else {
                    paramArrayOfInt[j] = l.a(paramRecordStore, arrayOfByte);
                }
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
    }

    private boolean e(int paramInt) {
        try {
            for (int i = this.d.b() - 1; i > -1; i--) {
                bd localbd = (bd) this.d.b(i);
                if (localbd.a == paramInt) {
                    return true;
                }
            }
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
        }
        return false;
    }

    public static dr a(String paramString, int paramInt) {
        return new dr(paramString, paramInt);
    }

    public static boolean a(dr paramdr, int paramInt) {
        return paramdr.e(paramInt);
    }

    public static void a(dr paramdr, int paramInt, byte[] paramArrayOfByte) {
        paramdr.a(paramInt, paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static void a(dr paramdr, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
        paramdr.a(paramInt1, paramArrayOfByte, paramInt2, paramInt3);
    }

    public static byte[] b(dr paramdr, int paramInt) {
        return paramdr.b(paramInt);
    }

    public static void c(dr paramdr, int paramInt) {
        paramdr.c(paramInt);
    }

    public static void a(dr paramdr) {
        paramdr.b();
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dr

 * JD-Core Version:    0.7.0.1

 */