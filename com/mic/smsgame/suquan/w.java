package com.mic.smsgame.suquan;

import ay;

public class w
        extends j {
    //Random board
    public void a(int[][] paramArrayOfInt) {
        b(paramArrayOfInt);
        int i = e.h.length;
        int j = 0;
        for (int k = 2; k < 10; k++) {
            m = j != 0 ? 3 : 2;// xen ke
            j = j != 0 ? 0 : 1;//
            while (m < 10) {
                paramArrayOfInt[k][m] = ay.a(i);//0->5.0
                m += 2;
            }
        }
        k = ay.a(5) + 2 + 2;
        int m = ay.a(6) + 1 + 2;
        //Maybe default 7
        if (paramArrayOfInt[k][m] != 7) {
            k--;
        }
        int n = ay.a(4);
        //Random can move
        switch (n) {
            case 0:
                int tmp169_168 = paramArrayOfInt[(k - 1)][m];
                paramArrayOfInt[k][(m + 1)] = tmp169_168;
                paramArrayOfInt[k][(m - 1)] = tmp169_168;
                break;
            case 1:
                int tmp200_199 = paramArrayOfInt[(k + 1)][m];
                paramArrayOfInt[k][(m + 1)] = tmp200_199;
                paramArrayOfInt[k][(m - 1)] = tmp200_199;
                break;
            case 2:
                int tmp231_230 = paramArrayOfInt[k][(m - 1)];
                paramArrayOfInt[(k + 1)][m] = tmp231_230;
                paramArrayOfInt[(k - 1)][m] = tmp231_230;
                break;
            default:
                int tmp262_261 = paramArrayOfInt[k][(m + 1)];
                paramArrayOfInt[(k + 1)][m] = tmp262_261;
                paramArrayOfInt[(k - 1)][m] = tmp262_261;
        }
        j = 1;
        // Random other, not match
        for (int i1 = 2; i1 < 10; i1++) {
            int i2 = j != 0 ? 3 : 2;
            j = j != 0 ? 0 : 1;
            while (i2 < 10) {
                boolean[] arrayOfBoolean = new boolean[e.h.length];
                int i3 = paramArrayOfInt[i1][(i2 - 1)];
                int i4 = paramArrayOfInt[i1][(i2 - 2)];
                int i5 = paramArrayOfInt[i1][(i2 + 1)];
                int i6 = paramArrayOfInt[i1][(i2 + 2)];
                if (i3 == i4) {
                    arrayOfBoolean[i3] = true;
                }
                if (i5 == i6) {
                    arrayOfBoolean[i5] = true;
                }
                if (i5 == i3) {
                    arrayOfBoolean[i3] = true;
                }
                int i7 = paramArrayOfInt[(i1 - 1)][i2];
                int i8 = paramArrayOfInt[(i1 - 2)][i2];
                int i9 = paramArrayOfInt[(i1 + 1)][i2];
                int i10 = paramArrayOfInt[(i1 + 2)][i2];
                if (i7 == i8) {
                    arrayOfBoolean[i7] = true;
                }
                if (i9 == i10) {
                    arrayOfBoolean[i9] = true;
                }
                if (i7 == i9) {
                    arrayOfBoolean[i7] = true;
                }
                int i11 = ay.a(arrayOfBoolean.length);
                if (arrayOfBoolean[i11] != 0) {
                    do {
                        i11 = (i11 + 1) % arrayOfBoolean.length;
                    } while (arrayOfBoolean[i11] != 0);
                }
                paramArrayOfInt[i1][i2] = i11;
                i2 += 2;
            }
        }
    }

    //Ramdom word red 10%
    public void a(int[][] paramArrayOfInt, int paramInt1, int paramInt2) {
        paramArrayOfInt[paramInt1][paramInt2] = ay.a(e.h.length);
        if ((paramArrayOfInt[paramInt1][paramInt2] == 0) && (ay.a(100) < 11)) {
            paramArrayOfInt[paramInt1][paramInt2] = 6;
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.w

 * JD-Core Version:    0.7.0.1

 */