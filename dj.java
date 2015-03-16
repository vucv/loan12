import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class dj
        extends ae {
    private e[] e;
    private String f;
    private int t;
    private int u;
    private dl v;
    private int w;
    private Image x;

    public void a(e[] paramArrayOfe) {
        this.e = paramArrayOfe;
        u();
    }

    private static e[] a(String paramString, di paramdi1, di paramdi2) {
        String[] arrayOfString = new String[50];
        int i = 1;
        int j = 0;
        int m;
        if (paramdi1 != null) {
            for (k = 0; k < paramdi1.b(); k++) {
                m = paramString.indexOf("{s}");
                paramString = paramString.substring(0, m) + (String) paramdi1.b(k) + paramString.substring(m + 2, paramString.length());
            }
        }
        int k = 0;
        arrayOfString[0] = paramString;
        int n;
        while (k < i) {
            m = arrayOfString[k].indexOf("@");
            if (m >= 0) {
                for (n = i - 1; n > k; n--) {
                    arrayOfString[(n + 1)] = arrayOfString[n];
                }
                arrayOfString[(k + 1)] = arrayOfString[k].substring(m + 1);
                arrayOfString[k] = arrayOfString[k].substring(0, m);
                i++;
            }
            k++;
        }
        e[] arrayOfe = new e[i];
        for (k = 0; k < i; k++) {
            if ("{i}".equals(arrayOfString[k])) {
                arrayOfe[k] = ((e) paramdi2.b(j++));
            } else {
                n = 0;
                if ((arrayOfString[k].length() > 0) && (arrayOfString[k].charAt(0) == '*')) {
                    n = 1;
                }
                if (n != 0) {
                    arrayOfString[k] = arrayOfString[k].substring(1);
                }
                arrayOfe[k] = new dt(arrayOfString[k], 0);
                if ((arrayOfString[k].length() > 0) && (n != 0)) {
                    ((dt) arrayOfe[k]).a(bz.c);
                }
            }
        }
        return arrayOfe;
    }

    public static e[] a(String paramString, String[] paramArrayOfString, e[] paramArrayOfe, br parambr1, br parambr2) {
        int i = Integer.parseInt(paramString.substring(0, 3));
        e[] arrayOfe = new e[i];
        paramString = paramString.substring(4);
        if (paramArrayOfString != null) {
            for (j = 0; j < paramArrayOfString.length; j++) {
                k = paramString.indexOf("&r");
                paramString = paramString.substring(0, k) + paramArrayOfString[j] + paramString.substring(k + 2, paramString.length());
            }
        }
        int j = 0;
        int k = 0;
        do {
            if (paramString == null) {
                break;
            }
            int m = paramString.indexOf("#");
            String str1;
            if (m > -1) {
                str1 = paramString.substring(0, m);
                paramString = paramString.substring(m + 1);
            } else {
                str1 = paramString;
            }
            String str2 = str1.charAt(0);
            if (str2.equals("s")) {
                String str3 = str1.charAt(1);
                String str4 = str1.charAt(2);
                int n = 0;
                if (str4.equals("r")) {
                    n = 2;
                } else if (str4.equals("c")) {
                    n = 1;
                }
                if (str1.charAt(3).equals("$")) {
                    int i1 = Integer.parseInt(str1.substring(4, 6));
                    boolean bool = str1.charAt(4).equals("t");
                    str1 = str1.substring(7);
                    String[] arrayOfString = new String[i1];
                    for (int i2 = 0; i2 < arrayOfString.length; i2++) {
                        int i3 = str1.indexOf("|");
                        if (i3 > -1) {
                            arrayOfString[i2] = str1.substring(0, i3);
                            str1 = str1.substring(i3 + 1);
                        } else {
                            arrayOfString[i2] = str1;
                        }
                    }
                    arrayOfe[j] = new dt(arrayOfString, bool, n);
                } else {
                    arrayOfe[j] = new dt(str1.substring(3), n);
                }
                if (str3.equals("t")) {
                    ((dt) arrayOfe[j]).a(bz.c);
                }
            } else if (str2.equals("i")) {
                arrayOfe[j] = paramArrayOfe[(k++)];
            }
            j++;
        } while (j < i);
        return arrayOfe;
    }

    public static final dj a(String paramString1, String paramString2, di paramdi1, di paramdi2, de paramde1, de paramde2) {
        dj localdj = new dj(paramString1, a(paramString2, paramdi1, paramdi2));
        if (paramde2 == null) {
            localdj.c(paramde1);
        } else if (paramde1 == null) {
            localdj.c(paramde2);
        } else {
            localdj.a(paramde1);
            localdj.b(paramde2);
        }
        return localdj;
    }

    public dj(String paramString) {
        this(paramString, null);
    }

    public dj(String paramString, e[] paramArrayOfe) {
        super(1);
        this.b = false;
        v();
        this.v = new dl(10, 41, this.b_ - 15, this.c_ - 69);
        this.f = paramString;
        this.e = paramArrayOfe;
        u();
    }

    private void u() {
        if (this.e == null) {
            return;
        }
        this.w = 0;
        this.t = (this.u = 0);
        for (int i = 0; i < this.e.length; i++) {
            this.e[i].a(this.v);
            this.w += this.e[i].a() + 8;
        }
    }

    private void v() {
        try {
            this.x = cz.d;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void a(int paramInt1, int paramInt2) {
        super.a(paramInt1, paramInt2);
        int i;
        int j;
        if (this.t > 0) {
            i = this.b_ / 2 + 2 - 5;
            j = this.v.b - 2 - 15;
            if ((paramInt1 > i) && (paramInt1 < i + 10) && (paramInt2 > j) && (paramInt2 < j + 15)) {
                cx.u[12] = 2;
                return;
            }
        }
        if ((this.w > this.v.d) && (this.w - this.t - this.v.d > 0)) {
            i = this.b_ / 2 + 2 - 5;
            j = this.v.b + this.v.d + 2;
            if ((paramInt1 > i) && (paramInt1 < i + 10) && (paramInt2 > j) && (paramInt2 < j + 15)) {
                cx.u[13] = 2;
                return;
            }
        }
    }

    public void a(int paramInt) {
        super.a(paramInt);
        if ((paramInt == 12) || (paramInt == 2)) {
            this.t -= this.v.d / 2;
            if (this.t < 0) {
                this.t = 0;
            }
        } else if ((paramInt == 13) || (paramInt == 8)) {
            this.t += this.v.d / 2;
            if (this.w - this.t < this.v.d) {
                this.t = (this.w - this.v.d);
            }
            if (this.t < 0) {
                this.t = 0;
            }
        }
    }

    protected void e() {
        int i;
        if (this.u != this.t) {
            i = this.t - this.u;
            if (Math.abs(i) < 3) {
                this.u += i;
            } else {
                this.u += i / 2;
            }
        }
        if (this.e != null) {
            for (i = 0; i < this.e.length; i++) {
                this.e[i].b();
            }
        }
    }

    public void a(Graphics paramGraphics) {
        if (this.r == null) {
            paramGraphics.setColor(cx.S);
            paramGraphics.fillRect(0, 0, this.b_, this.c_);
        }
        if (this.f != null) {
            bz.c.a(paramGraphics, this.f, 2, 2, 0);
            paramGraphics.setColor(16711680);
            paramGraphics.fillRect(2, 4 + bz.c.a(), bz.c.a(this.f) + 2, 2);
        }
        if (this.e != null) {
            paramGraphics.setClip(0, this.v.b, this.b_, this.v.d);
            int i = this.v.b - this.u;
            int j = this.v.b + this.v.d;
            dl localdl = new dl();
            for (int k = 0; k < this.e.length; k++) {
                if (i > j) {
                    break;
                }
                localdl.a(this.v.a, i, this.v.c, this.e[k].a());
                if (this.v.a(localdl)) {
                    this.e[k].a(paramGraphics, this.v.a, i);
                }
                i += this.e[k].a() + 8;
            }
            paramGraphics.setClip(0, 0, this.b_, this.c_);
        }
        if (this.t > 0) {
            paramGraphics.drawRegion(this.x, 0, 0, this.x.getWidth(), this.x.getHeight(), 6, this.b_ / 2 + 2, this.v.b - 2, 33);
        }
        if ((this.w > this.v.d) && (this.w - this.t - this.v.d > 0)) {
            paramGraphics.drawRegion(this.x, 0, 0, this.x.getWidth(), this.x.getHeight(), 5, this.b_ / 2 + 2, this.v.b + this.v.d + 2, 17);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dj

 * JD-Core Version:    0.7.0.1

 */