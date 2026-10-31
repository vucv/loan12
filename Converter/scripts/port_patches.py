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
