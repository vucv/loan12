package game;
import com.mic.smsgame.suquan.a;
import com.mic.smsgame.suquan.e;

import java.io.PrintStream;

public class t
        extends ch
        implements at, e {
    public t(bu parambu, int paramInt) {
        super(parambu, paramInt);
    }

    public void a() {
        System.out.println("start AI");
        ci.a().a(this);
    }

    public void b() {
    }

    public void c() {
        j2me.Host.postDelayed(new PortAiStep(this, 1), 1000); // PORT: was Thread.sleep(1000) on a worker
    }

    private a portMove; // PORT
    private int portSkill; // PORT

    /** PORT: the old c() split at its sleeps. */
    public void portStep(int step) {
        try {
            if (step == 1) {
                int m = 0;
                this.b.c(120);
            di localdi = this.b.q();
            //
            if (localdi != null) {
                int i = 0;
                int j = 0;
                for (int k = localdi.b() - 1; k > -1; k--) {
                    m = 0;
                    a locala2 = (a) localdi.b(k);
                    int n = this.b.d(locala2.a, locala2.b);
                    int i1 = this.b.d(locala2.c, locala2.d);
                    int[] arrayOfInt = {i1, i1, n, n};
                    for (int i2 = locala2.e.length - 1; i2 > -1; i2--) {
                        int i3 = locala2.e[i2] & 0xFF;
                        if (i3 >= 3) {
                            if ((i2 == 0) || (i2 == 2)) {
                                arrayOfInt[i2] = this.b.a(arrayOfInt[i2], locala2.a, locala2.b, (locala2.e[i2] & 0xFF0000) >> 16, (locala2.e[i2] & 0xFF00) >> 8);
                            } else {
                                arrayOfInt[i2] = this.b.b(arrayOfInt[i2], locala2.a, locala2.b, (locala2.e[i2] & 0xFF0000) >> 16, (locala2.e[i2] & 0xFF00) >> 8);
                            }
                            int i4 = this.b.e(arrayOfInt[i2], i3);
                            m += i4;
                        }
                    }
                    if ((m >= i) && (!this.b.e(this.a))) {
                        j = k;
                        i = m;
                    }
                }
                    a locala1 = (a) localdi.b(j);
                    m = this.b.d();
                    //random 50%
                    if ((m >= 1) && (ay.a(100) < 50)) {
                        this.portSkill = m;
                        j2me.Host.postDelayed(new PortAiStep(this, 2), 1000);
                    } else {
                        this.b.a(locala1);
                        this.portMove = locala1;
                        j2me.Host.postDelayed(new PortAiStep(this, 3), 1000);
                    }
                }
            } else if (step == 2) {
                //Dung Skill
                this.b.a(ay.a(this.portSkill));
            } else if (step == 3) {
                a locala1 = this.portMove;
                this.b.a(locala1.a, locala1.b, locala1.c, locala1.d);
            }
        } catch (Exception localException) {
            game.d.a(localException);
            localException.printStackTrace();
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     t

 * JD-Core Version:    0.7.0.1

 */