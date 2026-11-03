#!/usr/bin/env python3
"""
PORT-UI, steps 2 and 3 of the touch redesign (plan/thiet-ke-giao-dien.md): menu, settings, character
slots, hero choice, map, bag, shop and quests are drawn by Unity. This script runs after
port_patches.py: it copies the bridge classes of Converter/port/ into jsrc/game and makes the small
changes in the game they need (marked PORT-UI).
"""
import os
import re
import shutil

W = os.environ.get('J2CS_WORK') or os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ROOT = os.path.join(W, 'jsrc') + os.sep


def rd(p):
    return open(ROOT + p, encoding='utf-8').read()


def wr(p, s):
    open(ROOT + p, 'w', encoding='utf-8').write(s)


def rep(p, old, new, count=1):
    s = rd(p)
    n = s.count(old)
    if n != count:
        raise SystemExit('port_screens: %s: expected %d match(es), found %d: %r' % (p, count, n, old[:80]))
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


def in_method(p, signature, pairs):
    """replace text inside one method only"""
    s = rd(p)
    a, b = method_span(s, signature)
    body = s[a:b]
    for old, new in pairs:
        if old not in body:
            raise SystemExit('port_screens: %s %s: %r not found' % (p, signature, old))
        body = body.replace(old, new)
    wr(p, s[:a] + body + s[b:])


# ---------------------------------------------------------------------------------------------
# bridge classes
for fn in sorted(os.listdir(os.path.join(W, 'port'))):
    if fn.endswith('.java'):
        shutil.copy(os.path.join(W, 'port', fn), ROOT + 'game/' + fn)

rep('game/PortUi.java', """    public static void process() {
""", """    public static void process() {
        PortScreens.process(); // PORT-UI: menu, slots, map ...
""")

# ---------------------------------------------------------------------------------------------
# character slots (plan/thiet-ke-nhieu-nhan-vat.md, 3.2): bg.b / bg.c are the stores of the slot in
# play (PortScreens.useSlot); lives, volume, sound flags (16b #4-#8, #11, #13, #14) and the wallet
# with the items (161b #9) are shared by every character, in the account stores.
rep('game/bg.java', """    public static dr c = dr.a("161b", 161);
""", """    public static dr c = dr.a("161b", 161);
    /** PORT-UI: records shared by every character slot (lives, volume ...; gold and items) */
    public static dr acct = dr.a("acct_16b", 16);
    public static dr acctC = dr.a("acct_161b", 161);

    static {
        PortScreens.migrateAccount(); // PORT-UI
    }
""")
SHARED_16 = ['public static final boolean g()', 'public static final byte[] h()', 'public static void b(byte[] paramArrayOfByte)',
             'public static void i()', 'private static final int b()', 'public static boolean j()', 'public static void k()',
             'public static boolean l()', 'public static int m()', 'public static void n()', 'public static void c(int paramInt)',
             'public static byte[] o()', 'public static void d(int paramInt)', 'public static void p()',
             'public static void a(boolean paramBoolean)']
for sig in SHARED_16:
    s = rd('game/bg.java')
    a, b = method_span(s, sig)
    body = re.sub(r'dr\.([abc])\(b([,)])', r'dr.\1(acct\2', s[a:b])
    if body == s[a:b]:
        raise SystemExit('port_screens: bg %s: no store access' % sig)
    wr('game/bg.java', s[:a] + body + s[b:])
for sig in ['public static void a(int paramInt1, int paramInt2, int paramInt3)', 'public static void b(int paramInt1, int paramInt2)']:
    in_method('game/bg.java', sig, [('(c, 9', '(acctC, 9'), ('dr.a(c)', 'dr.a(acctC)')])
for sig in ['public static int[] v()', 'public static int f(int paramInt)']:
    in_method('game/bg.java', sig, [('(c, 9', '(acctC, 9')])
# a new game no longer clears the shared lives (#5) and wallet (#9)
in_method('game/bg.java', 'public static void a()', [('            dr.c(b, 5);\n', ''), ('            dr.c(c, 9);\n', '')])

# ---------------------------------------------------------------------------------------------
# start-up sound question: answered by the touch interface (volume is a Unity setting)
rep('game/v.java', """    public void b() {
        cz.d().e.a(1, this.w, false);
    }""", """    public void b() {
        cz.d().e.a(1, this.w, false);
    }

    /** PORT-UI: "Chọn" with the sound on (the touch interface has its own volume setting) */
    public void portSkip() {
        this.f = 100;
        b(0, this.c);
    }""")

# hero choice: the touch view picks the hero
rep('com/mic/smsgame/suquan/z.java', """    private void u() {""", """    /** PORT-UI: hero highlighted */
    public int portHero() {
        return this.f;
    }

    /** PORT-UI: highlight hero i (like a tap on its portrait) */
    public void portSelect(int i) {
        if ((i >= 0) && (i < this.w.length) && (i != this.f)) {
            this.f = i;
            this.v = bz.a(this.w[this.f], this.t[0].getWidth() - 15);
        }
    }

    private void u() {""")

# ---------------------------------------------------------------------------------------------
# map (plan/thiet-ke-ban-do.md 3): state for the Unity map view, walking to a node, the old key
# tutorial replaced by the touch view's own
rep('com/mic/smsgame/suquan/aa.java', """    private void portWalk() {
        if (this.portPathPos >= this.portPathLen) {
            return;
        }""", """    private int portGoal = -1;
    private boolean portRetarget;

    // PORT-UI: state and actions for the Unity map view (see game.PortMap)
    public int portState() {
        return this.f;
    }

    public boolean portNodeOpen(int n) {
        return (n >= 0) && (n < this.z.length) && this.z[n].d;
    }

    public boolean portRoadOpen(int r) {
        return (r >= 0) && (r < this.A.length) && this.A[r].c;
    }

    public int portNodeQuest(int n) {
        return this.z[n].e;
    }

    public int portNodeEvent(int n) {
        return this.z[n].f;
    }

    public int portGuard(int n) {
        return this.S[n];
    }

    public int portQuest() {
        return this.T;
    }

    public boolean portArena(int i) {
        return (i >= 0) && (i < this.aq.length) && this.aq[i];
    }

    public int portNode() {
        return this.B;
    }

    public int portX() {
        return this.F;
    }

    public int portY() {
        return this.G;
    }

    public boolean portWalking() {
        return (this.f == 1) || (this.portPathPos < this.portPathLen) || this.portRetarget;
    }

    public int portTarget() {
        return portWalking() ? this.portGoal : -1;
    }

    /** stops at the end of the road being walked */
    public void portStop() {
        this.portPathLen = 0;
        this.portRetarget = false;
    }

    /** "BẮT ĐẦU": what the game does when the hero arrives at the node he stands on */
    public void portArrive() {
        if (this.f == 0) {
            f(0);
        }
    }

    /** walks to node n along the shortest open way (from the end of the road being walked) */
    public void portGo(int n) {
        if ((n < 0) || (n >= this.z.length)) {
            return;
        }
        this.portGoal = n;
        if (this.f == 0) {
            this.portRetarget = false;
            a(this.g + this.z[n].b, this.a_ + this.z[n].c);
        } else {
            this.portPathLen = 0;
            this.portRetarget = true;
        }
    }

    /** roads of the shortest open way from the hero's node to n (-1: none); its nodes into out */
    public int portRoute(int n, int[] out) {
        int i1 = this.z.length;
        if ((n < 0) || (n >= i1)) {
            return -1;
        }
        int[] prev = new int[i1];
        int[] queue = new int[i1];
        for (int k = 0; k < i1; k++) {
            prev[k] = -2;
        }
        int head = 0;
        int tail = 0;
        queue[tail++] = this.B;
        prev[this.B] = -1;
        while ((head < tail) && (prev[n] == -2)) {
            int at = queue[head++];
            for (int d = 0; d < 4; d++) {
                int r = bq.c[at][d];
                if ((r >= 0) && (r < this.A.length) && this.A[r].c) {
                    int to = this.A[r].a == at ? this.A[r].b : this.A[r].a;
                    if ((to >= 0) && (to < i1) && (prev[to] == -2)) {
                        prev[to] = at;
                        queue[tail++] = to;
                    }
                }
            }
        }
        if (prev[n] == -2) {
            return -1;
        }
        int len = 0;
        for (int k = n; prev[k] != -1; k = prev[k]) {
            len++;
        }
        if (out != null) {
            int at = len;
            for (int k = n; at >= 0; k = prev[k]) {
                if (at < out.length) {
                    out[at] = k;
                }
                at--;
                if (prev[k] == -1) {
                    break;
                }
            }
        }
        return len;
    }

    private void portWalk() {
        if (this.portRetarget && (this.f == 0) && (cz.d().e.d == null)) {
            this.portRetarget = false;
            if (this.portGoal >= 0) {
                a(this.g + this.z[this.portGoal].b, this.a_ + this.z[this.portGoal].c);
            }
        }
        if (this.portPathPos >= this.portPathLen) {
            return;
        }""")
rep('com/mic/smsgame/suquan/aa.java', """                if (e) {
                    e = false;
                    G();
                }""", """                if (e) {
                    e = false;
                    if (!game.PortScreens.touch) { // PORT-UI: the touch map has its own tutorial
                        G();
                    }
                }""")

print('port_screens: ok')
