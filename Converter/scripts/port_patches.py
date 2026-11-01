#!/usr/bin/env python3
"""
Port changes (not decompiler repairs): the game's Java threads become scheduler tasks so the whole
game runs on Unity's main thread, and SMS / browser hooks are made safe for a desktop/mobile port.
Every change is marked with `PORT:` in the code.
"""
import os
import re

ROOT = os.path.join(os.environ.get('J2CS_WORK') or os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'jsrc') + os.sep


def rd(p):
    return open(ROOT + p, encoding='utf-8').read()


def wr(p, s):
    open(ROOT + p, 'w', encoding='utf-8').write(s)


def rep(p, old, new, count=1):
    s = rd(p)
    n = s.count(old)
    if n != count:
        raise SystemExit('port_patches: %s: expected %d match(es), found %d: %r' % (p, count, n, old[:80]))
    wr(p, s.replace(old, new))


def method_span(s, signature):
    i = s.index(signature)
    j = s.index('{', i)
    depth = 0
    k = j
    while True:
        c = s[k]
        if c == '{':
            depth += 1
        elif c == '}':
            depth -= 1
            if depth == 0:
                return i, k + 1
        elif c == '"':
            k += 1
            while s[k] != '"':
                if s[k] == '\\':
                    k += 1
                k += 1
        k += 1


def replace_method(p, signature, new_text):
    s = rd(p)
    a, b = method_span(s, signature)
    wr(p, s[:a] + new_text.strip('\n').lstrip() + s[b:])


# ---------------------------------------------------------------------------------------------
# helper tasks
open(ROOT + 'game/PortTask.java', 'w', encoding='utf-8').write('''package game;

/** PORT: runs at.c() from the scheduler (replaces the worker-thread pool of class ci). */
public class PortTask implements Runnable {
    private final at task;

    public PortTask(at task) {
        this.task = task;
    }

    public void run() {
        this.task.c();
    }
}
''')
open(ROOT + 'game/PortAiStep.java', 'w', encoding='utf-8').write('''package game;

/** PORT: one step of the computer opponent (class t), scheduled after the pauses it used to sleep. */
public class PortAiStep implements Runnable {
    private final t ai;
    private final int step;

    public PortAiStep(t ai, int step) {
        this.ai = ai;
        this.step = step;
    }

    public void run() {
        this.ai.portStep(this.step);
    }
}
''')

# ---------------------------------------------------------------------------------------------
# cz: main game canvas. The 25 fps loop thread becomes a 40 ms scheduler loop.
replace_method('game/cz.java', 'public void b() {', '''
    public void b() {
        j2me.Host.startLoop(this, 40); // PORT: was new Thread(this).start()
    }
''')
replace_method('game/cz.java', 'public void run() {', '''
    public void run() {
        // PORT: body of the former game-loop thread; called every 40 ms by the scheduler.
        if (this.l) {
            return; // hidden (another Displayable is shown / app paused)
        }
        try {
            if (this.e != null) {
                c();
                repaint();
            }
        } catch (Exception localException) {
            game.d.a(localException);
            localException.printStackTrace();
        }
    }
''')
rep('game/cz.java', '''        this.l = false;
        synchronized (this.m) {
            this.m.notify();
        }''', '''        this.l = false; // PORT: no loop thread to wake up any more''')
rep('game/cz.java', '''            try {
                MGMIDlet.f().a("Http://PhuThoBay.Pro");
            } catch (ConnectionNotFoundException localConnectionNotFoundException) {
            }''', '''            try {
                MGMIDlet.f().a("Http://PhuThoBay.Pro");
            } catch (Exception localConnectionNotFoundException) {
            }''')

# ---------------------------------------------------------------------------------------------
# cn: splash GameCanvas, 20 fps animation thread -> 50 ms scheduler loop / state machine
rep('game/cn.java', '        new Thread(this).start();', '        j2me.Host.startLoop(this, 50); // PORT: was new Thread(this).start()')
rep('game/cn.java', '    private Font r = null;', '    private Font r = null;\n    private boolean portDone; // PORT')
replace_method('game/cn.java', 'public void run() {', '''
    public void run() {
        // PORT: one iteration of the former splash thread loop (called every 50 ms).
        if (this.portDone) {
            return;
        }
        if (!cx.F && ((this.o) || (!MGMIDlet.f().d))) {
            d();
            if (this.q == 0) {
                if ((!this.p) && (this.g < this.n)) {
                    e();
                }
                flushGraphics();
                this.a += 1;
                if (this.a > 30) {
                    this.p = false;
                }
                if (this.a > 60) {
                    a();
                    this.q = 1;
                    this.a = 0;
                }
            } else {
                flushGraphics();
                this.a += 1;
                if (this.a > 40) {
                    this.o = false;
                }
            }
            return;
        }
        this.portDone = true;
        j2me.Host.stopLoop(this);
        if (!cx.F) {
            d();
        }
        b();
    }
''')

# ---------------------------------------------------------------------------------------------
# ci: worker-thread pool -> scheduler. (The decompiled a(at) also lacked its `return`, so a task
# would have been queued once per free slot.)
replace_method('game/ci.java', 'public void a(int paramInt) {', '''
    public void a(int paramInt) {
        if (paramInt <= 0) {
            throw new RuntimeException("Size of pool cannot be less than or equal 0");
        }
        this.d = new at[paramInt];
        this.c = false; // PORT: no worker thread
    }
''')
replace_method('game/ci.java', 'public void a(at paramat) {', '''
    public void a(at paramat) {
        if (this.d == null) {
            return;
        }
        j2me.Host.post(new PortTask(paramat)); // PORT: runs on the next tick instead of a worker thread
    }
''')
replace_method('game/ci.java', 'public void run() {', '''
    public void run() {
        // PORT: unused (tasks are posted to the scheduler)
    }
''')
replace_method('game/ci.java', 'public void b() {', '''
    public void b() {
        this.c = true;
    }
''')

# ---------------------------------------------------------------------------------------------
# bv: screen switcher. It handed the switch to the pool after sleeping 100 ms (to show the loading
# screen first); now the task is simply scheduled 100 ms later.
rep('game/bv.java', '''        f();
        ci.a().a(this);''', '''        f();
        j2me.Host.postDelayed(new PortTask(this), 100); // PORT: was ci (worker) + Thread.sleep(100)''')
rep('game/bv.java', '''    public void c() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
        }
''', '''    public void c() {
''')

# ---------------------------------------------------------------------------------------------
# t: computer opponent. It ran on the pool and slept 1 s between thinking / selecting / swapping.
s = rd('game/t.java')
a, b = method_span(s, 'public void c() {')
body = s[a:b]
# keep the move-evaluation code exactly as decompiled
m1 = body.index('this.b.c(120);')
m2 = body.index('a locala1 = (a) localdi.b(j);')
evaluation = body[m1:m2]
new_c = '''public void c() {
        j2me.Host.postDelayed(new PortAiStep(this, 1), 1000); // PORT: was Thread.sleep(1000) on a worker
    }

    private a portMove; // PORT
    private int portSkill; // PORT

    /** PORT: the old c() split at its sleeps. */
    public void portStep(int step) {
        try {
            if (step == 1) {
                int m = 0;
                ''' + evaluation.strip() + '''
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
    }'''
s = s[:a] + new_c + s[b:]
wr('game/t.java', s)

# ---------------------------------------------------------------------------------------------
# cs: SMS sender thread -> task posted per request
rep('game/cs.java', '''    protected cs() {
        Thread localThread = new Thread(this);
        localThread.start();
    }''', '''    protected cs() {
        // PORT: no sender thread; requests are processed by run() on the next tick
    }''')
s = rd('game/cs.java')
a, b = method_span(s, 'public void run() {')
body = s[a:b]
inner_start = body.index('} else {\n                cx.H = true;') + len('} else {\n')
inner_end = body.rindex('            }\n        }\n    }')
inner = body[inner_start:inner_end]
new_run = '''public void run() {
        // PORT: one request of the former sender-thread loop
        if (this.a == null) {
            return;
        }
''' + '\n'.join(l[8:] if l.startswith('        ') else l for l in inner.split('\n')) + '''
    }'''
wr('game/cs.java', s[:a] + new_run + s[b:])
rep('game/cs.java', '''        this.a = parambc;
        synchronized (this.e) {
            this.e.notify();
        }''', '''        this.a = parambc;
        j2me.Host.post(this); // PORT''')

# ct: incoming-SMS reader thread: not supported in the port
rep('game/ct.java', '''        Thread localThread = new Thread(this);
        localThread.start();''', '''        // PORT: incoming SMS are not supported (no reader thread)''')

# ---------------------------------------------------------------------------------------------
# MGMIDlet.a(url): phones had to quit to open the browser; the port keeps running.
rep('com/mg/smsgame/main/MGMIDlet.java', '''        f().platformRequest(paramString);
        f().notifyDestroyed();''', '''        f().platformRequest(paramString); // PORT: no notifyDestroyed() - the game keeps running''')

# ---------------------------------------------------------------------------------------------
# Original-game bug (touch only): in the shop, a tap on the last pixel row of the item grid gave
# item index 6..8 of 6 -> ArrayIndexOutOfBoundsException in every following paint (frozen shop).
rep('com/mic/smsgame/suquan/q.java', 'if ((paramInt2 >= i) && (paramInt2 <= i + 76)) {',
    'if ((paramInt2 >= i) && (paramInt2 < i + 76)) { // PORT-FIX (original bug): was <= (row 2 does not exist)')

# =============================================================================================
# PORT-TOUCH: finger-friendly controls for modern touch screens. The game already supported
# touch (Nokia stylus phones); these changes make it comfortable with fingers.
# ---------------------------------------------------------------------------------------------
# 1. drags reach the screens: cz.pointerDragged -> bv.portDrag -> cx.s/cx.t -> ar.a_ (the game had
#    the plumbing for drags - scrolling text lists - but never received pointerDragged)
rep('game/cz.java', """    protected void pointerReleased(int paramInt1, int paramInt2) {
        if (this.e != null) {
            this.e.c(paramInt1 - cx.j, paramInt2 - cx.k);
        }
    }
""", """    protected void pointerReleased(int paramInt1, int paramInt2) {
        if (this.e != null) {
            this.e.c(paramInt1 - cx.j, paramInt2 - cx.k);
        }
    }

    protected void pointerDragged(int paramInt1, int paramInt2) { // PORT-TOUCH
        if (this.e != null) {
            this.e.portDrag(paramInt1 - cx.j, paramInt2 - cx.k);
        }
    }
""")
rep('game/bv.java', """    public abstract void m();
}""", """    public abstract void m();

    /** PORT-TOUCH: pointer drag (screen coordinates), consumed by the active screen's update. */
    public final void portDrag(int paramInt1, int paramInt2) {
        if (this.a) {
            return;
        }
        cx.s = paramInt1;
        cx.t = paramInt2;
    }

    /** PORT-TOUCH: tells the host the soft keys of the active dialog / screen (shown on big touch buttons). */
    private void portPublishSoftKeys() {
        ar localar = null;
        if ((!this.l) || (this.i == null)) {
            if (this.d != null) {
                localar = this.d;
            } else {
                localar = this.b;
            }
        }
        if (localar == null) {
            j2me.Host.setSoftKeys(null, null, null);
        } else {
            j2me.Host.setSoftKeys(localar.portSoftLabel(0), localar.portSoftLabel(1), localar.portSoftLabel(2));
        }
    }
}""")
rep('game/bv.java', """    public final void a() {
        if (this.q > 0) {""", """    public final void a() {
        portPublishSoftKeys(); // PORT-TOUCH
        if (this.q > 0) {""")

# 2. base screen: press, then drag, then release (a quick swipe can arrive within one game tick);
#    release y was read from the x variable (original bug, harmless until releases are used)
rep('game/ar.java', """            i = cx.q;
            j = cx.q;
            if ((i > -1) && (j > -1)) {
                e(i, j);
                cx.q = cx.r = -1;
            }
            i = cx.s;
            j = cx.t;
            if ((i > -1) && (j > -1)) {
                a_(i, j);
                cx.s = cx.t = -1;
            }""", """            i = cx.s; // PORT-TOUCH: drags before the release
            j = cx.t;
            if ((i > -1) && (j > -1)) {
                a_(i, j);
                cx.s = cx.t = -1;
            }
            i = cx.q;
            j = cx.r; // PORT-FIX (original bug): was cx.q
            if ((i > -1) && (j > -1)) {
                e(i, j);
                cx.q = cx.r = -1;
            }""")
rep('game/ar.java', """    public final void a_(int paramInt1, int paramInt2) {
        c(paramInt2 - this.m);
        d(paramInt1 - this.n);
        this.m = paramInt2;
        this.n = paramInt1;
    }""", """    public final void a_(int paramInt1, int paramInt2) {
        c(paramInt2 - this.m);
        d(paramInt1 - this.n);
        this.m = paramInt2;
        this.n = paramInt1;
        portDragTo(paramInt1, paramInt2); // PORT-TOUCH
    }

    /** PORT-TOUCH: pointer dragged to (x, y), absolute screen coordinates. */
    public void portDragTo(int paramInt1, int paramInt2) {
    }

    /** PORT-TOUCH: label of soft key 0 = left, 1 = centre, 2 = right; null when there is none. */
    public String portSoftLabel(int paramInt) {
        de localde = this.a[paramInt];
        if (localde instanceof da) {
            return ((da) localde).portLabel();
        }
        return null;
    }""")
rep('game/da.java', """    public void a(Graphics paramGraphics) {""", """    public String portLabel() { // PORT-TOUCH
        return this.f;
    }

    public void a(Graphics paramGraphics) {""")

# 3. soft keys: the whole third of the soft-key bar is the button (was only the label, 13 px high)
rep('game/de.java', """    protected int e;
""", """    protected int e;
    private int portSlot = -1; // PORT-TOUCH: 0 left, 1 centre, 2 right
""")
rep('game/de.java', """    public void a(int paramInt1, int paramInt2, int paramInt3) {
        this.b = paramInt1;""", """    public void a(int paramInt1, int paramInt2, int paramInt3) {
        this.portSlot = paramInt3 == 20 ? 0 : (paramInt3 == 17 ? 1 : (paramInt3 == 24 ? 2 : -1)); // PORT-TOUCH
        this.b = paramInt1;""")
rep('game/de.java', """    public final boolean a(int paramInt1, int paramInt2) {
        return (paramInt2 > this.c) && (paramInt1 >= this.b) && (paramInt1 < this.b + this.d);
    }""", """    public final boolean a(int paramInt1, int paramInt2) {
        if ((this.portSlot >= 0) && (this.c >= cx.g - 20)) {
            // PORT-TOUCH: soft-key bar button = its third of the whole bar
            int i = cx.f / 3;
            if (paramInt2 <= this.c - 4) {
                return false;
            }
            if (this.portSlot == 0) {
                return paramInt1 < i;
            }
            if (this.portSlot == 2) {
                return paramInt1 >= cx.f - i;
            }
            return (paramInt1 >= i) && (paramInt1 < cx.f - i);
        }
        return (paramInt2 > this.c) && (paramInt1 >= this.b) && (paramInt1 < this.b + this.d);
    }""")

# 4. battle board: one tap selects a gem (was: first tap only moved the cursor), a tap on a neighbour
#    or a swipe swaps, a tap on the selected gem deselects. The tutorial keeps its original rules.
replace_method('game/ac.java', 'public void a(int paramInt1, int paramInt2) {', """public void a(int paramInt1, int paramInt2) {
        int j;
        this.portSwipe = false;
        this.portUnselect = false;
        if (paramInt2 >= cx.g - 15) {
            int i = cx.f / 3;
            j = i + i;
            if (paramInt1 > j) {
                this.b.g();
            } else if (paramInt1 > i) {
                this.b.s();
            } else {
                this.b.r();
            }
            return;
        }
        dl localdl = this.b.h();
        if (localdl.b(paramInt1, paramInt2)) {
            j = (paramInt1 - localdl.a) / 21 + 2;
            int k = (paramInt2 - localdl.b) / 21 + 2;
            int m = this.b.k();
            int n = this.b.l();
            if ((k == m) && (j == n)) {
                if (this.b.j()) {
                    this.portUnselect = true; // decided on release: a swipe keeps the selection
                } else {
                    this.b.i();
                }
            } else if (this.b.portTraining()) {
                // tutorial: original rules for the other gems (it checks every step)
                if (this.b.j()) {
                    int i1 = k - m;
                    int i2 = j - n;
                    if (i1 == 1) {
                        this.b.n();
                    } else if (i1 == -1) {
                        this.b.m();
                    } else if (i2 == 1) {
                        this.b.p();
                    } else if (i2 == -1) {
                        this.b.o();
                    }
                    return;
                } else {
                    this.b.c(k, j);
                }
            } else if ((this.b.j()) && (Math.abs(k - m) + Math.abs(j - n) == 1)) {
                if (k - m == 1) {
                    this.b.n();
                } else if (k - m == -1) {
                    this.b.m();
                } else if (j - n == 1) {
                    this.b.p();
                } else {
                    this.b.o();
                }
                return;
            } else {
                this.b.c(k, j);
                if ((this.b.k() == k) && (this.b.l() == j) && (!this.b.j())) {
                    this.b.i();
                }
            }
            this.portSwipe = (this.b.j()) && (this.b.k() == k) && (this.b.l() == j);
            this.portX = paramInt1;
            this.portY = paramInt2;
        }
    }

    // PORT-TOUCH: swipe state of the current press
    private boolean portSwipe;
    private boolean portUnselect;
    private int portX;
    private int portY;

    /** PORT-TOUCH: a drag of 8+ pixels from the selected gem swaps it with its neighbour that way. */
    public void portDrag(int paramInt1, int paramInt2) {
        if ((!this.portSwipe) || (!this.b.j())) {
            return;
        }
        int i = paramInt1 - this.portX;
        int j = paramInt2 - this.portY;
        int k = Math.abs(i);
        int m = Math.abs(j);
        if ((k < 8) && (m < 8)) {
            return;
        }
        this.portSwipe = false;
        this.portUnselect = false;
        if (k > m) {
            if (i > 0) {
                this.b.p();
            } else {
                this.b.o();
            }
        } else if (j > 0) {
            this.b.n();
        } else {
            this.b.m();
        }
    }

    /** PORT-TOUCH: releasing a tap on the already selected gem deselects it. */
    public void portRelease(int paramInt1, int paramInt2) {
        if ((this.portUnselect) && (this.b.j())) {
            this.b.i();
        }
        this.portUnselect = false;
        this.portSwipe = false;
    }""")
rep('game/bu.java', """    public di q() {""", """    // PORT-TOUCH: drags / releases go to the human player while the board waits for input
    public void portDrag(int paramInt1, int paramInt2) {
        if ((this.v == 8) && (this.s.a() == 0) && ((this.u[0] instanceof ac))) {
            ((ac) this.u[0]).portDrag(paramInt1, paramInt2);
        }
    }

    public void portRelease(int paramInt1, int paramInt2) {
        if ((this.v == 8) && (this.s.a() == 0) && ((this.u[0] instanceof ac))) {
            ((ac) this.u[0]).portRelease(paramInt1, paramInt2);
        }
    }

    public boolean portTraining() {
        return this.s.d;
    }

    /** soft keys of the battle bar: left (skills) and right (pause); the centre ("bag") is the # key */
    public String portBarLabel(int paramInt) {
        if (paramInt == 1) {
            return null;
        }
        return this.t.portLabel(paramInt);
    }

    public di q() {""")
rep('game/i.java', """    public void a(String paramString, int paramInt) {
        this.v[paramInt] = paramString;
    }""", """    public void a(String paramString, int paramInt) {
        this.v[paramInt] = paramString;
    }

    public String portLabel(int paramInt) { // PORT-TOUCH
        return this.v[paramInt];
    }""")
rep('com/mic/smsgame/suquan/ab.java', """    public void a(int paramInt1, int paramInt2) {
        this.e.a(paramInt1, paramInt2);
    }""", """    public void a(int paramInt1, int paramInt2) {
        this.e.a(paramInt1, paramInt2);
    }

    public void portDragTo(int paramInt1, int paramInt2) { // PORT-TOUCH
        if (this.e != null) {
            this.e.portDrag(paramInt1, paramInt2);
        }
    }

    public void g(int paramInt1, int paramInt2) { // PORT-TOUCH: pointer released
        if (this.e != null) {
            this.e.portRelease(paramInt1, paramInt2);
        }
    }

    public String portSoftLabel(int paramInt) { // PORT-TOUCH: the battle draws its own soft-key bar
        if (this.e != null) {
            return this.e.portBarLabel(paramInt);
        }
        return null;
    }""")

# 5. map: tap a place to walk there along the open roads (the arrows around the hero still work)
rep('com/mic/smsgame/suquan/aa.java', """    public void a(int paramInt) {
        if (this.f == 1) {
            return;
        }""", """    // PORT-TOUCH: walking to a tapped place, one road (= one arrow key) at a time
    private int[] portPath = new int[64];
    private int portPathLen;
    private int portPathPos;

    public void a(int paramInt1, int paramInt2) {
        this.portPathLen = 0;
        if (this.f != 0) {
            return;
        }
        int i = -1;
        int j = 484;
        int k;
        for (k = 0; k < this.z.length; k++) {
            if (this.z[k].d) {
                int m = this.g + this.z[k].b - paramInt1;
                int n = this.a_ + this.z[k].c - paramInt2;
                if (m * m + n * n < j) {
                    j = m * m + n * n;
                    i = k;
                }
            }
        }
        if ((i < 0) || (i == this.B)) {
            return;
        }
        int i1 = this.z.length;
        int[] arrayOfInt1 = new int[i1];
        int[] arrayOfInt2 = new int[i1];
        int[] arrayOfInt3 = new int[i1];
        for (k = 0; k < i1; k++) {
            arrayOfInt1[k] = -2;
        }
        int i2 = 0;
        int i3 = 0;
        arrayOfInt3[i3++] = this.B;
        arrayOfInt1[this.B] = -1;
        while ((i2 < i3) && (arrayOfInt1[i] == -2)) {
            int i4 = arrayOfInt3[i2++];
            for (int i5 = 0; i5 < 4; i5++) {
                int i6 = bq.c[i4][i5];
                if ((i6 >= 0) && (i6 < this.A.length) && (this.A[i6].c)) {
                    int i7 = this.A[i6].a == i4 ? this.A[i6].b : this.A[i6].a;
                    if ((i7 >= 0) && (i7 < i1) && (arrayOfInt1[i7] == -2)) {
                        arrayOfInt1[i7] = i4;
                        arrayOfInt2[i7] = i5;
                        arrayOfInt3[i3++] = i7;
                    }
                }
            }
        }
        if (arrayOfInt1[i] == -2) {
            return;
        }
        int i8 = 0;
        for (k = i; arrayOfInt1[k] != -1; k = arrayOfInt1[k]) {
            i8++;
        }
        if (i8 > this.portPath.length) {
            return;
        }
        int i9 = i8;
        for (k = i; arrayOfInt1[k] != -1; k = arrayOfInt1[k]) {
            this.portPath[--i9] = arrayOfInt2[k];
        }
        this.portPathLen = i8;
        this.portPathPos = 0;
        portWalk();
    }

    private void portWalk() {
        if (this.portPathPos >= this.portPathLen) {
            return;
        }
        if ((this.f != 0) && (this.f != 1)) {
            this.portPathLen = 0; // something happened on the way (event, encounter): stop
            return;
        }
        if ((this.f != 0) || (cz.d().e.d != null)) {
            return; // still on the road, or a dialog is open
        }
        int i = this.portPath[this.portPathPos++];
        a(i == 0 ? 12 : (i == 1 ? 13 : (i == 2 ? 14 : 15)));
        if (this.f != 1) {
            this.portPathLen = 0;
        }
    }

    public void a(int paramInt) {
        if (this.f == 1) {
            return;
        }""")
rep('com/mic/smsgame/suquan/aa.java', """    public void a() {
        Object localObject;
        if (this.ak) {""", """    public void a() {
        Object localObject;
        portWalk(); // PORT-TOUCH
        if (this.ak) {""")

# 6. tutorial texts mention touch as well as keys
rep('game/w.java', '"Hãy bấm phím (5) hoặc phím OK để chọn thanh kiếm0*#"', '"Hãy chạm vào thanh kiếm (hoặc bấm phím 5) để chọn0*#"')
rep('game/w.java', '"Bấm phím (8) hoặc phím xuống để di chuyển xuống"', '"Vuốt xuống hoặc chạm ô bên dưới (phím 8) để đổi chỗ"')
rep('game/w.java', '"Tướng quân đã được thưởng 1 vật phẩm, hãy bấm phím # để chọn và sử dụng vật phẩm."', "\"Tướng quân đã được thưởng 1 vật phẩm, hãy chạm 'Túi' (hoặc bấm phím #) để chọn và sử dụng vật phẩm.\"")

# ---------------------------------------------------------------------------------------------
# PORT-UI: touch redesign (plan/thiet-ke-giao-dien.md, option B). The Unity host draws the battle
# itself (bars, skills, items around the board); the game keeps the rules, the AI, the animations of
# the board and every other screen. PortUi exposes read-only state and queues the touch actions,
# which the game runs at the start of its next frame (same thread, same order as a key press).
# ---------------------------------------------------------------------------------------------
open(ROOT + 'game/PortUi.java', 'w', encoding='utf-8').write('''package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * PORT-UI: what the Unity touch interface needs from the game, and its queued actions.
 * Screens drawn by Unity: 5 preparing a battle (the VS box on the map), 6 the battle, 7 the result.
 * Actions are queued and run by the screen manager at the start of the game's next frame.
 */
public final class PortUi {
    public static final int SKILL = 1;       // arg: skill slot 0..2
    public static final int ITEM = 2;        // arg: item type (shop index)
    public static final int PAUSE = 3;
    public static final int SCREEN_KEY = 4;  // soft key of the current screen, arg: 0 left, 1 centre, 2 right
    public static final int DIALOG_KEY = 5;  // soft key of the dialog on top
    public static final int RESULT_SKIP = 6; // result panel: stop waiting
    private static int reqKind;
    private static int reqArg;

    /** damage of each skill = attack * E / F (as the game's skill window computes it) */
    static final int[] SK_E = {1, 3, 5, 4, 0, 2, 4, 0, 3};
    static final int[] SK_F = {1, 2, 2, 5, 1, 1, 5, 5, 2};
    /** gold prices of the shop items; Long Thần Kiếm and Nhân Sâm were sold by SMS only (plan: 1500) */
    static final int[] PRICE = {1500, 1500, -1, 1000, 250, 500};

    private PortUi() {
    }

    // ---- screens ------------------------------------------------------------------------------

    static ar screen() {
        cz c = cz.a;
        return (c == null) || (c.e == null) ? null : c.e.b;
    }

    static ae dialog() {
        cz c = cz.a;
        return (c == null) || (c.e == null) ? null : c.e.d;
    }

    static boolean transition() {
        bv v = cz.a.e;
        return v.l && (v.i != null);
    }

    /** the battle object while the battle screen is the current screen, else null */
    public static bu battle() {
        ar s = screen();
        if (s instanceof com.mic.smsgame.suquan.ab) {
            return ((com.mic.smsgame.suquan.ab) s).portBattle();
        }
        return null;
    }

    /** 6: the plain battle is on screen: no dialog on top, no screen transition, not the tutorial */
    public static boolean battleView() {
        bu b = battle();
        if ((b == null) || (b.portState() == null) || (dialog() != null) || transition()) {
            return false;
        }
        return !b.portTraining();
    }

    /** 5: the map shows the VS box of a coming battle, ready for "Đánh" / "Rút lui" */
    public static boolean prepareView() {
        ar s = screen();
        if (!(s instanceof com.mic.smsgame.suquan.aa) || (dialog() != null) || transition()) {
            return false;
        }
        return ((com.mic.smsgame.suquan.aa) s).portVsReady();
    }

    /** 7: the result panel of a won battle / a quest reward is open */
    public static boolean resultView() {
        return (dialog() instanceof cr) && !transition();
    }

    // ---- 5: preparing a battle ----------------------------------------------------------------

    static c side(int side) {
        return side == 0 ? dd.e : dd.f;
    }

    public static boolean canRetreat() {
        ar s = screen();
        return (s instanceof com.mic.smsgame.suquan.aa) && ((com.mic.smsgame.suquan.aa) s).portCanRetreat();
    }

    public static String vsName(int side) {
        c f = side(side);
        return f == null ? "" : u.a(f.a);
    }

    public static int vsPortrait(int side) {
        c f = side(side);
        return f == null ? 0 : f.a;
    }

    public static int vsLevel(int side) {
        c f = side(side);
        return f == null ? 0 : f.e;
    }

    public static int vsHp(int side) {
        c f = side(side);
        return f == null ? 0 : f.c;
    }

    public static int vsMaxHp(int side) {
        c f = side(side);
        return f == null ? 1 : Math.max(1, f.b);
    }

    public static int vsAttack(int side) {
        c f = side(side);
        return f == null ? 0 : f.d;
    }

    /** who moves first (decided when the VS box opens; Ngọc Ấn makes it the player) */
    public static boolean playerFirst() {
        return dd.h;
    }

    public static int owned(int type) {
        return bg.f(type);
    }

    public static int gold() {
        return bg.f(2);
    }

    public static int price(int type) {
        return PRICE[type];
    }

    public static int packSize(int type) {
        return com.mic.smsgame.suquan.q.c[type];
    }

    /** buys one pack with gold, as the shop does; false when there is not enough gold */
    public static boolean buy(int type) {
        int p = PRICE[type];
        if ((p <= 0) || (bg.f(2) < p)) {
            return false;
        }
        bg.b(2, p);
        bg.a(type, packSize(type), 6);
        return true;
    }

    public static String itemName(int type) {
        String s = com.mic.smsgame.suquan.h.c[type];
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    public static String itemDesc(int type) {
        return com.mic.smsgame.suquan.h.d[type];
    }

    // ---- 6: the battle ------------------------------------------------------------------------

    static bn fighter(int side) {
        bu b = battle();
        if ((b == null) || (b.portState() == null)) {
            return null;
        }
        return b.portState().b(side);
    }

    /** side 0 = player, 1 = enemy */
    public static String name(int side) {
        bn f = fighter(side);
        return f == null ? "" : u.a(f.b());
    }

    /** index of the 30x30 portrait in /avatar (10 per row) */
    public static int portrait(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.b();
    }

    public static int level(int side) {
        c f = side(side);
        return f == null ? 0 : f.e;
    }

    public static int hp(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.r();
    }

    public static int maxHp(int side) {
        bn f = fighter(side);
        return f == null ? 1 : Math.max(1, f.s());
    }

    public static int mana(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.o();
    }

    public static int maxMana(int side) {
        bn f = fighter(side);
        return f == null ? 1 : Math.max(1, f.n());
    }

    /** turns left of an active item effect (type 0 sword, 3 shield ...) on a fighter */
    public static int effect(int side, int type) {
        bn f = fighter(side);
        if ((f == null) || (type < 0) || (type >= f.a.length)) {
            return 0;
        }
        return f.a[type].b;
    }

    /** 0 = the player, 1 = the enemy */
    public static int turn() {
        bu b = battle();
        return b == null ? -1 : b.portState().a();
    }

    /** the board waits for the player's move */
    public static boolean waiting() {
        bu b = battle();
        return (b != null) && b.portWaiting();
    }

    public static int skillCount() {
        bn f = fighter(0);
        return f == null ? 0 : f.d().length;
    }

    public static int skillCount(int side) {
        bn f = fighter(side);
        return f == null ? 0 : f.d().length;
    }

    /** skill id 0..8 (0-2 fire, 3-5 lightning, 6-8 water) */
    public static int skillId(int side, int k) {
        bn f = fighter(side);
        return (f == null) || (k >= f.d().length) ? 0 : f.d()[k];
    }

    public static String skillName(int k) {
        return skillName(0, k);
    }

    public static String skillName(int side, int k) {
        bn f = fighter(side);
        return (f == null) || (k >= f.d().length) ? "" : com.mic.smsgame.suquan.h.a[f.d()[k]];
    }

    /** the game's description of the player's k-th skill with its damage filled in */
    public static String skillDesc(int k) {
        bn f = fighter(0);
        if ((f == null) || (k >= f.d().length)) {
            return "";
        }
        int id = f.d()[k];
        String str = com.mic.smsgame.suquan.h.b[id];
        int j = str.indexOf("=");
        if (j >= 0) {
            str = str.substring(0, j) + (f.m() * SK_E[id] / SK_F[id]) + str.substring(j + 1);
        }
        return str;
    }

    /** mana the k-th skill costs (the game: max mana * (k + 1) / 3) */
    public static int skillCost(int k) {
        bn f = fighter(0);
        return f == null ? 0 : f.n() * (k + 1) / 3;
    }

    /** number of skills the player can afford now (the first ones) */
    public static int skillsUsable() {
        bn f = fighter(0);
        if ((f == null) || (f.d().length < 1)) {
            return 0;
        }
        return f.o() * 3 / f.n();
    }

    public static Image skillIcon(int k) {
        Image[] icons = bs.a().i;
        return (icons == null) || (k >= icons.length) ? null : icons[k];
    }

    /** how many of an item (shop index 0..5) the player can still use in this battle */
    public static int itemCount(int type) {
        bu b = battle();
        if ((b == null) || (type == 2)) {
            return 0;
        }
        if ((type == 1) && b.portGinsengUsed()) {
            return 0;
        }
        dx[] arr = b.portState().a;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].a == type) {
                return arr[i].b;
            }
        }
        return 0;
    }

    /** an item was already used in this turn (one per turn) */
    public static boolean itemUsed() {
        bu b = battle();
        return (b != null) && b.portItemUsed();
    }

    /** the board on the 240x320 screen: x, y, w, h */
    public static int board(int i) {
        bu b = battle();
        dl r = b == null ? null : b.h();
        if (r == null) {
            return i < 2 ? 36 + (i == 1 ? 18 : 0) : 168;
        }
        switch (i) {
            case 0:
                return r.a;
            case 1:
                return r.b;
            case 2:
                return r.c > 0 ? r.c : 168;
            default:
                return r.d > 0 ? r.d : 168;
        }
    }

    // ---- 7: result ----------------------------------------------------------------------------

    static cr result() {
        ae d = dialog();
        return d instanceof cr ? (cr) d : null;
    }

    /** 0 battle won, 1 quest reward, 2 arena */
    public static int resultMode() {
        cr r = result();
        return r == null ? 0 : r.portMode();
    }

    public static String resultName() {
        cr r = result();
        return r == null ? "" : u.a(r.portPortrait());
    }

    public static int resultPortrait() {
        cr r = result();
        return r == null ? 0 : r.portPortrait();
    }

    public static int resultLevel() {
        cr r = result();
        return r == null ? 0 : r.portLevel();
    }

    /** experience now (counts up while the rewards are added) and the bounds of the current level */
    public static int resultExp() {
        cr r = result();
        return r == null ? 0 : r.portExp();
    }

    public static int resultExpLo() {
        cr r = result();
        return r == null ? 0 : r.portExpLo();
    }

    public static int resultExpHi() {
        cr r = result();
        return r == null ? 1 : r.portExpHi();
    }

    public static int resultHp() {
        cr r = result();
        return r == null ? 0 : r.portHp();
    }

    public static int resultMaxHp() {
        cr r = result();
        return r == null ? 1 : Math.max(1, r.portMaxHp());
    }

    public static int resultAttack() {
        cr r = result();
        return r == null ? 0 : r.portAttack();
    }

    /** gold owned now (counts up while the rewards are added) */
    public static int resultGold() {
        cr r = result();
        return r == null ? 0 : r.portGold();
    }

    public static int resultGoldGain() {
        cr r = result();
        return r == null ? 0 : r.portGoldGain();
    }

    public static int resultExpGain() {
        cr r = result();
        return r == null ? 0 : r.portExpGain();
    }

    public static int resultWinBonus() {
        cr r = result();
        return r == null ? 0 : r.portWinBonus();
    }

    /** the panel accepts "Đóng" (its short wait is over) */
    public static boolean resultReady() {
        cr r = result();
        return (r != null) && r.portReady();
    }

    // ---- shared -------------------------------------------------------------------------------

    public static Image image(String name) {
        return az.b(name);
    }

    /** text in one of the game's bitmap fonts (1 = gold, 2 = white), on magenta (= transparent) */
    public static Image text(String s, int font) {
        dh f = font == 1 ? bz.c : bz.d;
        int w = Math.max(1, f.a(s) + 2);
        int h = f.a() + 4;
        Image img = Image.createImage(w, h);
        Graphics g = img.getGraphics();
        g.setColor(0xFF00FF);
        g.fillRect(0, 0, w, h);
        f.a(g, s, 1, 1, 0);
        return img;
    }

    /** queue an action for the next frame of the game */
    public static void request(int kind, int arg) {
        reqKind = kind;
        reqArg = arg;
    }

    /** runs the queued action; called by the screen manager at the start of a frame */
    public static void process() {
        if (reqKind == 0) {
            return;
        }
        int kind = reqKind;
        int arg = reqArg;
        reqKind = 0;
        switch (kind) {
            case SKILL:
            case ITEM:
            case PAUSE: {
                bu b = battle();
                if ((b == null) || !battleView()) {
                    return;
                }
                if (kind == SKILL) {
                    b.portUseSkill(arg);
                } else if (kind == ITEM) {
                    b.portUseItem(arg);
                } else {
                    b.g();
                }
                break;
            }
            case SCREEN_KEY: {
                ar s = screen();
                if ((s != null) && (dialog() == null)) {
                    s.portPress(arg);
                }
                break;
            }
            case DIALOG_KEY: {
                ae d = dialog();
                if (d != null) {
                    d.portPress(arg);
                }
                break;
            }
            case RESULT_SKIP: {
                cr r = result();
                if (r != null) {
                    r.portSkip();
                }
                break;
            }
        }
    }
}
''')
rep('game/bu.java', """    public String portBarLabel(int paramInt) {""", """    // PORT-UI: state and actions for the Unity touch interface (see PortUi)
    public cq portState() {
        return this.s;
    }

    /** the board waits for the human player's move */
    public boolean portWaiting() {
        return (this.s != null) && (this.v == 8) && (this.s.a() == 0) && (this.u != null) && (this.u[0] instanceof ac);
    }

    public boolean portItemUsed() {
        return this.K;
    }

    public boolean portGinsengUsed() {
        return this.L;
    }

    /** the k-th skill, with the checks of the skill window (r() + "Dùng") */
    public void portUseSkill(int k) {
        if (!portWaiting()) {
            return;
        }
        if ((this.s.d) && (this.t.g.g() != 3)) {
            return;
        }
        if ((k < 0) || (k >= d())) {
            return;
        }
        a("skill", k);
    }

    /** an item, with the checks of the item window (s() + "Dùng") */
    public void portUseItem(int type) {
        if ((!portWaiting()) || (type == 2)) {
            return;
        }
        int i = this.s.c();
        if ((i == 1) && (this.t.g.g() != 5)) {
            return;
        }
        if (this.K) {
            return;
        }
        if (this.L) {
            this.s.a[1].b = 0;
        }
        dx[] arr = this.s.a;
        for (int j = 0; j < arr.length; j++) {
            if ((arr[j].a == type) && (arr[j].b > 0)) {
                arr[j].a();
                bg.g(type);
                a("item", type);
                return;
            }
        }
    }

    public String portBarLabel(int paramInt) {""")
rep('com/mic/smsgame/suquan/ab.java', """    public String portSoftLabel(int paramInt) { // PORT-TOUCH: the battle draws its own soft-key bar""", """    public game.bu portBattle() { // PORT-UI
        return this.e;
    }

    public String portSoftLabel(int paramInt) { // PORT-TOUCH: the battle draws its own soft-key bar""")
rep('game/bv.java', """        portPublishSoftKeys(); // PORT-TOUCH
""", """        portPublishSoftKeys(); // PORT-TOUCH
        PortUi.process(); // PORT-UI
""")
rep('game/ar.java', """    /** PORT-TOUCH: label of soft key 0 = left, 1 = centre, 2 = right; null when there is none. */""", """    /** PORT-UI: presses soft key 0 = left, 1 = centre, 2 = right, exactly like its key would. */
    public boolean portPress(int slot) {
        if ((this.s == null) || (slot < 0) || (slot > 2) || (this.a[slot] == null) || (this.b[slot] != 0)) {
            return false;
        }
        this.a[slot].b();
        this.b[slot] = 3;
        return true;
    }

    /** PORT-TOUCH: label of soft key 0 = left, 1 = centre, 2 = right; null when there is none. */""")
rep('com/mic/smsgame/suquan/aa.java', """    public void b() {
        ad localad = new ad(dd.e, this.W, this.X);""", """    /** PORT-UI: the VS box is shown and its intro is over ("Đánh" is available) */
    public boolean portVsReady() {
        return (this.f == 3) && (this.Z == null) && (this.N != null);
    }

    /** PORT-UI: "Rút lui" is offered */
    public boolean portCanRetreat() {
        return this.D >= 0;
    }

    public void b() {
        ad localad = new ad(dd.e, this.W, this.X);""")
rep('game/cr.java', """        this.P = new dk("lvu");
    }""", """        this.P = new dk("lvu");
        this.portG0 = this.G; // PORT-UI: the rewards before they are counted into the totals
        this.portH0 = this.H;
        this.portK0 = this.K;
        this.portJ0 = this.J;
        this.portI0 = this.I;
    }

    // PORT-UI: what the Unity result screen shows
    private int portG0;
    private int portH0;
    private int portK0;
    private int portJ0;
    private int portI0;

    public int portMode() {
        return dd.a >= 0 ? 2 : (this.N ? 1 : 0);
    }

    public int portPortrait() {
        return this.v.a;
    }

    public int portLevel() {
        return this.v.e;
    }

    public int portExp() {
        return this.w;
    }

    public int portExpLo() {
        return this.F;
    }

    public int portExpHi() {
        return this.E;
    }

    public int portHp() {
        return this.v.c;
    }

    public int portMaxHp() {
        return this.v.b;
    }

    public int portAttack() {
        return this.v.d;
    }

    public int portGold() {
        return this.x;
    }

    public int portGoldGain() {
        return this.N ? this.portH0 : this.portG0;
    }

    public int portExpGain() {
        return this.N ? this.portJ0 : this.portK0;
    }

    public int portWinBonus() {
        return this.N ? 0 : this.portI0;
    }

    public boolean portReady() {
        return this.L <= 0;
    }

    public void portSkip() {
        if (this.L > 0) {
            this.L = 1;
        }
    }""")

# ---------------------------------------------------------------------------------------------
# time and system properties come from the host backend (Unity: real clock; test harness: virtual
# clock, which makes test runs reproducible - Random is seeded from the clock)
for dirpath, _, files in os.walk(ROOT):
    for fn in files:
        if not fn.endswith('.java'):
            continue
        p = os.path.join(dirpath, fn)[len(ROOT):]
        s = rd(p)
        s2 = s.replace('System.currentTimeMillis()', 'j2me.Host.now() /* PORT */').replace('System.getProperty(', 'j2me.Host.property(')
        if s2 != s:
            wr(p, s2)

print('port_patches: ok')
