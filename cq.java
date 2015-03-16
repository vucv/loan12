import com.mic.smsgame.suquan.a;
import com.mic.smsgame.suquan.d;
import com.mic.smsgame.suquan.e;
import com.mic.smsgame.suquan.j;
import com.mic.smsgame.suquan.w;

import java.io.PrintStream;

public class cq
        implements e {
    private int[][] e;
    private bn[] f;
    private int g;
    private int k;
    private int l = 2;
    private int m = 2;
    public dx[] a;
    public int b;
    public int c;
    //random board
    private j n;
    // Huong dan
    public boolean d = false;
    private boolean o;

    public cq(int paramInt, c paramc1, c paramc2) {
        this.g = paramInt;
        this.d = (paramInt == 1);
        this.e = new int[12][12];
        i();
        this.f = new bn[2];
        this.f[0] = a(paramc1);
        this.f[1] = a(paramc2);
        int i = this.f[0].e() > this.f[1].e() ? this.f[0].e() : this.f[1].e();
        int j = this.f[0].m() < this.f[1].m() ? this.f[0].m() : this.f[1].m();
        this.b = (j / 2);
        this.c = 240;
        int i1 = i / j * 3 * 120;
        for (int i2 = 0; i2 < this.f.length; i2++) {
            this.f[i2].h(i1);
        }
        b();
        if (this.d) {
            this.f[0].e(9);
        }
    }

    public int a(byte[] paramArrayOfByte, int paramInt) {
        int i = paramInt;
        for (int j = 2; j < 10; j++) {
            for (int i1 = 2; i1 < 10; i1++) {
                paramArrayOfByte[(paramInt++)] = ((byte) this.e[j][i1]);
            }
        }
        System.arraycopy(ai.a(this.l), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        System.arraycopy(ai.a(this.m), 0, paramArrayOfByte, paramInt, 4);
        paramInt += 4;
        for (j = 0; j < this.f.length; j++) {
            paramInt += this.f[j].a(paramArrayOfByte, paramInt);
        }
        return paramInt - i;
    }

    public int b(byte[] paramArrayOfByte, int paramInt) {
        int i = paramInt;
        for (int j = 2; j < 10; j++) {
            for (int i1 = 2; i1 < 10; i1++) {
                this.e[j][i1] = paramArrayOfByte[(paramInt++)];
            }
        }
        this.l = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        this.m = ai.a(paramArrayOfByte, paramInt);
        paramInt += 4;
        for (j = 0; j < this.f.length; j++) {
            paramInt += this.f[j].b(paramArrayOfByte, paramInt);
        }
        return paramInt - i;
    }

    public int a() {
        return this.k;
    }

    public void a(int paramInt) {
        this.k = paramInt;
    }

    public void b() {
        int[] arrayOfInt = bg.v();
        if (arrayOfInt == null) {
            arrayOfInt = new int[6];
        }
        arrayOfInt[5] = 0;
        this.a = new dx[arrayOfInt.length];
        for (int i = 0; i < this.a.length; i++) {
            this.a[i] = new dx();
            this.a[i].a = i;
            this.a[i].b = arrayOfInt[i];
        }
        d(5);
        c(5);
        for (i = 0; i < this.f.length; i++) {
            this.f[i].a();
        }
    }

    private bn a(c paramc) {
        return new bn(paramc);
    }

    public int c() {
        return this.g;
    }

    public bn[] d() {
        return this.f;
    }

    public bn b(int paramInt) {
        return this.f[paramInt];
    }

    public int a(int paramInt1, int paramInt2) {
        return this.e[paramInt1][paramInt2];
    }

    public int[][] e() {
        return this.e;
    }

    public int f() {
        return this.l;
    }

    public void c(int paramInt) {
        this.l = paramInt;
    }

    public int g() {
        return this.m;
    }

    public void d(int paramInt) {
        this.m = paramInt;
    }

    public boolean h() {
        return this.o;
    }

    public void a(boolean paramBoolean) {
        this.o = paramBoolean;
    }

    public boolean b(int paramInt1, int paramInt2) {
        if (paramInt1 < 2) {
            paramInt1 = 9;
        } else if (paramInt1 >= 10) {
            paramInt1 = 2;
        }
        if (paramInt2 < 2) {
            paramInt2 = 9;
        } else if (paramInt2 >= 10) {
            paramInt2 = 2;
        }
        if (g(paramInt1, paramInt2)) {
            this.l = paramInt1;
            this.m = paramInt2;
            return true;
        }
        return false;
    }

    public boolean c(int paramInt1, int paramInt2) {
        if (!this.o) {
            return true;
        }
        return (paramInt1 >= 2) && (paramInt1 < 10) && (paramInt2 >= 2) && (paramInt2 < 10);
    }

    private boolean g(int paramInt1, int paramInt2) {
        return this.e[paramInt1][paramInt2] >= 0;
    }

    public void i() {
        //Choice board huong dan and fight
        if (this.d) {
            this.n = new d();
        } else {
            this.n = new w();
        }
        int i = 0;
        for (int j = 11; j >= 0; j--) {
            this.e[i][j] =.;
        }
        i = 11;
        for (j = 11; j >= 0; j--) {
            this.e[i][j] = 8;
        }
        j = 0;
        for (i = 11; i >= 0; i--) {
            this.e[i][j] = 9;
        }
        j = 11;
        for (i = 11; i >= 0; i--) {
            this.e[i][j] = 9;
        }
        i = 1;
        for (j = 10; j >= 1; j--) {
            this.e[i][j] = 10;
        }
        i = 10;
        for (j = 10; j >= 1; j--) {
            this.e[i][j] = 10;
        }
        j = 1;
        for (i = 10; i >= 1; i--) {
            this.e[i][j] = 11;
        }
        j = 10;
        for (i = 10; i >= 1; i--) {
            this.e[i][j] = 11;
        }
    }

    //Init board
    public void j() {
        this.n.a(this.e);
        //Print to debug
        a(this.e);
    }

    //Ramdom at x,y. Maybe after detroy some block in board
    public void d(int paramInt1, int paramInt2) {
        this.n.a(this.e, paramInt1, paramInt2);
    }

    //check match V
    public int e(int paramInt1, int paramInt2) {
        if (this.e[paramInt1][paramInt2] >= 7) {
            return 0;
        }
        int i = -16777216;
        int j = 1;
        int i1 = 1;
        int i2 = e.i[this.e[paramInt1][paramInt2]];
        while (i2 == e.i[this.e[paramInt1][(paramInt2 - i1)]]) {
            i1++;
            j++;
        }
        i |= (i1 - 1 & 0xFF) << 16;
        i1 = 1;
        while (i2 == e.i[this.e[paramInt1][(paramInt2 + i1)]]) {
            i1++;
            j++;
        }
        i |= (i1 - 1 & 0xFF) << 8;
        i |= j & 0xFF;
        return i;
    }

    ////check match H
    public int f(int paramInt1, int paramInt2) {
        if (this.e[paramInt1][paramInt2] >= 7) {
            return 0;
        }
        int i = 0;
        int j = 1;
        int i1 = 1;
        int i2 = e.i[this.e[paramInt1][paramInt2]];
        while (i2 == e.i[this.e[(paramInt1 - i1)][paramInt2]]) {
            i1++;
            j++;
        }
        i |= (i1 - 1 & 0xFF) << 16;
        i1 = 1;
        while (i2 == e.i[this.e[(paramInt1 + i1)][paramInt2]]) {
            i1++;
            j++;
        }
        i |= (i1 - 1 & 0xFF) << 8;
        i |= j & 0xFF;
        return i;
    }

    public a a(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        a locala = c(paramInt1, paramInt2, paramInt3, paramInt4);
        return locala;
    }

    public void b(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        a(this.e, paramInt1, paramInt2, paramInt3, paramInt4);
    }

    private void a(int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        int i = paramArrayOfInt[paramInt1][paramInt2];
        paramArrayOfInt[paramInt1][paramInt2] = paramArrayOfInt[paramInt3][paramInt4];
        paramArrayOfInt[paramInt3][paramInt4] = i;
    }

    //swap
    private a c(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        try {
            if (this.e[paramInt1][paramInt2] == this.e[paramInt3][paramInt4]) {
                return null;
            }
            if ((this.e[paramInt1][paramInt2] >= 7) || (this.e[paramInt3][paramInt4] >= 7)) {
                return null;
            }
            //swap for check score
            a(this.e, paramInt1, paramInt2, paramInt3, paramInt4);
            int i = 0;
            //check move 4 de
            int j = e(paramInt1, paramInt2);
            int i1 = f(paramInt1, paramInt2);
            int i2 = e(paramInt3, paramInt4);
            int i3 = f(paramInt3, paramInt4);
            if (((j & 0xFF) >= 3) || ((i1 & 0xFF) >= 3) || ((i2 & 0xFF) >= 3) || ((i3 & 0xFF) >= 3)) {
                i = 1;
            }
            //reswap
            a(this.e, paramInt1, paramInt2, paramInt3, paramInt4);
            if (i != 0) {
                int[] arrayOfInt = {j, i1, i2, i3};
                a locala = new a();
                locala.a = paramInt1;
                locala.b = paramInt2;
                locala.c = paramInt3;
                locala.d = paramInt4;
                locala.e = arrayOfInt;
                return locala;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public di k() {
        di localdi = new di();
        for (int i = 2; i < 10; i++) {
            for (int j = 2; j < 10; j++) {
                a locala = c(i, j, i + 1, j);
                if (locala != null) {
                    localdi.a(locala);
                }
                locala = c(i, j, i, j + 1);
                if (locala != null) {
                    localdi.a(locala);
                }
            }
        }
        return localdi;
    }

    public static void a(int[][] paramArrayOfInt) {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                System.out.print(paramArrayOfInt[i][j] + "\t");
            }
            System.out.println();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     cq

 * JD-Core Version:    0.7.0.1

 */