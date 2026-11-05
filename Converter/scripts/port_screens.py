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

# ---------------------------------------------------------------------------------------------
# battle: "THOÁT TRẬN" on the touch pause box gives the battle up - the game's own defeat (state 9 with
# the player beaten), taken at the next "waiting for a move" step so no move or AI step is cut short
rep('game/bu.java', """    public ae portPauseDialog; // the "dừng game" dialog of this battle""", """    public ae portPauseDialog; // the "dừng game" dialog of this battle
    public boolean portForfeit; // PORT-UI: the player gave the battle up (pause box "Thoát trận")""")
rep('game/bu.java', """                int i = this.s.a();
                bn localbn = this.s.b(i);
                if ((!this.s.d) && (localbn.p() <= 0)) {""", """                if (this.portForfeit && !this.s.d) { // PORT-UI: given up, a defeat
                    this.portForfeit = false;
                    this.E = false;
                    f(9);
                    return;
                }
                int i = this.s.a();
                bn localbn = this.s.b(i);
                if ((!this.s.d) && (localbn.p() <= 0)) {""")

# ---------------------------------------------------------------------------------------------
# map dialogues (quests, events, after a battle) and the arena are drawn by Unity too
rep('game/ca.java', """    public boolean a() {
        this.f += 6;""", """    // PORT-UI: the utterance shown, for the Unity dialogue view (PortMap.talk*)
    public int portSpeaker() {
        return this.c < this.a.length ? this.a[this.c] : -1;
    }

    public String portText() {
        return this.c < this.b.length ? ao.a(this.b[this.c], "{p}", this.h) : "";
    }

    public int portIndex() {
        return this.c;
    }

    public int portCount() {
        return this.a.length;
    }

    /** PORT-UI: the next utterance at once (Unity shows each one whole); false after the last */
    public boolean portNext() {
        this.f = this.e.length;
        return a();
    }

    public boolean a() {
        this.f += 6;""")
rep('game/co.java', """    public boolean a() {
        return this.j >= 0;
    }""", """    public boolean a() {
        return this.j >= 0;
    }

    /** PORT-UI: level chosen (-1: none can be paid) */
    public int portLevel() {
        return this.j;
    }

    /** PORT-UI: level i is open and its ticket can be paid */
    public boolean portCan(int i) {
        return (i >= 0) && (i < this.h.length) && this.h[i];
    }

    /** PORT-UI: choose level i (like the arrow keys) */
    public void portSelect(int i) {
        if (portCan(i)) {
            this.j = i;
            b(i);
        }
    }""")
rep('com/mic/smsgame/suquan/aa.java', """    // PORT-UI: state and actions for the Unity map view (see game.PortMap)
    public int portState() {""", """    // PORT-UI: state and actions for the Unity map view (see game.PortMap)
    public game.ca portTalk() {
        return this.f == 2 ? this.O : null;
    }

    public game.co portArenaBox() {
        return this.f == 9 ? this.P : null;
    }

    /** next utterance of the dialogue ("Tiếp"), or what follows it after the last one */
    public void portTalkNext() {
        if ((this.f == 2) && (this.O != null) && !this.O.portNext()) {
            y();
        }
    }

    /** "Bỏ qua" of the dialogue */
    public void portTalkSkip() {
        if ((this.f == 2) && (this.O != null)) {
            y();
        }
    }

    public int portState() {""")

# ---------------------------------------------------------------------------------------------
# battle end: the game stopped at once when HP reached 0 (state 9, then the result after 20 ticks)
# while gems were still exploding and attacks flying; the touch view froze the board there. Now state
# 9 waits (at most 3 s more) until those effects are over, and the battle reports its outcome from
# then on, so the touch view's THẮNG / THUA starts on a settled board.
rep('game/i.java', """    public int o() {
        return this.A.a();
    }""", """    public int o() {
        return this.A.a();
    }

    /** PORT-UI: gems, combo texts, attacks flying to a fighter or a skill effect are still playing
     *  (shields and freezes that last several turns, types 4 and 8, do not count) */
    public boolean portBusy() {
        int j = 0;
        for (j = 0; j < this.s.b(); j++) {
            if (((s) this.s.b(j)).m()) {
                return true;
            }
        }
        for (j = 0; j < this.l.b(); j++) {
            if (((b) this.l.b(j)).a()) {
                return true;
            }
        }
        for (j = 0; j < this.o.b(); j++) {
            if (((p) this.o.b(j)).m()) {
                return true;
            }
        }
        for (j = 0; j < this.p.b(); j++) {
            ab e = (ab) this.p.b(j);
            if (e.m() && (e.b() != 4) && (e.b() != 8)) {
                return true;
            }
        }
        return false;
    }""")
rep('game/bu.java', """        switch (this.v) {
            case 9:
                f(12);""", """        switch (this.v) {
            case 9:
                if (this.t.portBusy() && (this.portEndWait < 75)) { // PORT-UI: let the board settle
                    this.portEndWait += 1;
                    return;
                }
                this.portEndWait = 0;
                f(12);""")
rep('game/bu.java', """    public boolean portForfeit; // PORT-UI: the player gave the battle up (pause box "Thoát trận")""",
    """    public boolean portForfeit; // PORT-UI: the player gave the battle up (pause box "Thoát trận")
    private int portEndWait;    // PORT-UI: ticks state 9 waited for the board's effects""")
rep('game/bu.java', """    public int portOutcome() {
        if ((this.v != 9) && (this.v != 12)) {
            return 0;
        }""", """    public int portOutcome() {
        if (this.v != 12) {
            return 0; // PORT-UI: not while state 9 lets the board's explosions and attacks finish
        }""")

# ---------------------------------------------------------------------------------------------
# rewards: the touch interface shows them on its result screens instead of the game's text boxes
# - "Chúc mừng ... hoàn thành nhiệm vụ / sự kiện": goes straight on ("Đồng ý"); a quest opens its
#   reward panel (cr, drawn by the touch result view), an event leaves a note for the map to show
# - "Bạn đã nhận được 1 vật phẩm (hộp bảo vật) ... mở ngay?": no question; the box now holds items
#   (PortMap.openBox; relics needed an SMS to open) and the battle result shows "+1 hộp vật phẩm"
# - "Bạn đã mở thêm được 1 mức luyện tập": a note on the map
# - items won in a battle (5 in a row) are counted for its result screen
rep('com/mic/smsgame/suquan/aa.java', """                if (!((String) localObject).equals("")) {
                    ae localae2 = cz.a("Chúc mừng", (String) localObject, "Đồng ý", 9, 1);""", """                if (!((String) localObject).equals("") && game.PortScreens.touch) { // PORT-UI
                    if (dd.m(dd.d)) {
                        game.PortMap.note = (String) localObject;
                    }
                    b(0, 9);
                } else if (!((String) localObject).equals("")) {
                    ae localae2 = cz.a("Chúc mừng", (String) localObject, "Đồng ý", 9, 1);""")
rep('com/mic/smsgame/suquan/aa.java', """            this.ak = false;
            this.an = bg.t();
            if (this.an >= 0) {""", """            this.ak = false;
            // PORT-UI: the quest's box holds items (relics needed an SMS to open); a note tells which
            this.an = game.PortScreens.touch ? -1 : bg.t();

            if (this.an >= 0) {""")
rep('com/mic/smsgame/suquan/aa.java', """    private void z() {
        ae localae""", """    private void z() {
        if (game.PortScreens.touch) { // PORT-UI: a note on the touch map instead of a dialog
            game.PortMap.note = "Đã mở thêm 1 mức luyện tập ở Đấu trường. Hãy vào đó thử sức!";
            return;
        }
        ae localae""")
rep('com/mic/smsgame/suquan/aa.java', """    private void F() {
        dd.c = this.T;""", """    private void F() {
        game.PortMap.battleQuest = this.L == 0 ? this.z[this.B].e : -1; // PORT-UI: the quest of this battle
        dd.c = this.T;""")
rep('game/bu.java', """            if (i >= 0) {
                this.s.a[i].b += 1;
                bg.a(i, 1, 6);
            }""", """            if (i >= 0) {
                this.s.a[i].b += 1;
                bg.a(i, 1, 6);
                this.portWon[i] += 1; // PORT-UI: shown on the result screen
            }""")
rep('game/bu.java', """    private int portEndWait;""", """    private int portEndWait;
    public int[] portWon = new int[6]; // PORT-UI: items won in this battle (5 in a row), by type""")

# - item drops (touch): more often than the game's (40% on a line of 5): a line of 4 drops one item
#   1 time in 10, a line of 5 or more about 1 time in 3, 2 boxes at most a battle; the item flies out of the board (t.d) as in the game
rep('game/bu.java', """                        if (i6 >= 5) {
                            int i7 = g(i6);""", """                        if (i6 >= (game.PortScreens.touch ? 4 : 5)) {
                            int i7 = game.PortScreens.touch ? portDrop(i6) : g(i6);""")
rep('game/bu.java', """    private int portEndWait;""", """    private int portEndWait;

    /** PORT-UI: the touch interface's drop (rates above): items 3, 4, 5 a little more often than 0 and 1 */
    private int portDrop(int len) {
        if (this.s.d) {
            return g(len);
        }
        if ((this.portBoxes >= 2) || (ay.a(100) >= (len >= 5 ? 35 : 10))) { // at most 2 boxes a battle
            return -1;
        }
        int r = ay.a(100);
        int i = r < 15 ? 0 : (r < 30 ? 1 : (r < 55 ? 3 : (r < 75 ? 4 : 5)));
        this.s.a[i].b += 1;
        bg.a(i, 1, 6);
        this.portWon[i] += 1;
        return i;
    }""")

# ---------------------------------------------------------------------------------------------
# start-up: no logo splash (MicroGame, partner) and no 1.2 s cover before the menu in the touch port
rep('game/cn.java', """        if (this.portDone) {
            return;
        }""", """        if (this.portDone) {
            return;
        }
        if (PortScreens.touch) {
            this.o = false; // PORT-UI: no logos
            this.q = 1;
        }""")
rep('com/mic/smsgame/suquan/l.java', """        if (this.d == 30) {""", """        if ((this.d == 30) || ((this.d == 2) && game.PortScreens.touch)) { // PORT-UI: no wait""")

# tutorial battle: the touch battle view shows the coach's messages itself
rep('game/w.java', """    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u) {
            return;
        }""", """    /** PORT-UI: the message shown (without its gem marker), or null */
    public String portText() {
        if (!this.u || (this.l < 0) || (this.l >= this.a.length) || (this.g >= this.a[this.l].length)) {
            return null;
        }
        String str = this.a[this.l][this.g];
        if (str.endsWith("*#")) {
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }

    /** PORT-UI: gem drawn after the message (index into bs.d), -1 none */
    public int portGem() {
        if (!this.u || (this.l < 0) || (this.l >= this.a.length) || (this.g >= this.a[this.l].length)) {
            return -1;
        }
        String str = this.a[this.l][this.g];
        return str.endsWith("*#") ? Integer.parseInt(str.substring(str.length() - 3, str.length() - 2)) : -1;
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u || PortScreens.touch) { // PORT-UI: drawn by the touch view
            return;
        }""")
rep('game/bu.java', """    public boolean portTraining() {
        return this.s.d;
    }""", """    public boolean portTraining() {
        return this.s.d;
    }

    /** PORT-UI: the coach's message of the tutorial, or null */
    public String portCoach() {
        return (this.s.d) && (this.t.g != null) ? this.t.g.portText() : null;
    }

    public int portCoachGem() {
        return (this.s.d) && (this.t.g != null) ? this.t.g.portGem() : -1;
    }

    /** PORT-UI: the tutorial waits for a tap to go on (any key did that) */
    public boolean portCoachWaits() {
        return this.v == 14;
    }

    public void portCoachNext() {
        if (this.v == 14) {
            this.t.g.f();
        }
    }""")

rep('game/PortUi.java', """        bu b = battle();
        if ((b == null) || (b.portState() == null) || b.portTraining()) {
            return false;
        }
        ae d = dialog();
        if (d == null) {
            return true;
        }""", """        bu b = battle();
        if ((b == null) || (b.portState() == null)) { // PORT-UI: the tutorial battle too
            return false;
        }
        ae d = dialog();
        if (d == null) {
            return true;
        }""")

# out of lives: no SMS purchase; the touch interface offers a rewarded ad / the refill item instead
rep('com/mic/smsgame/suquan/aa.java', """                if (!bg.l()) {
                    localae1 = cz.a""", """                if (!bg.l() && game.PortScreens.touch) { // PORT-UI
                    game.PortMap.noLives = true;
                } else if (!bg.l()) {
                    localae1 = cz.a""")

# the intro / ending scroll (df + cl): in another language the touch interface draws the translated
# text over it (TouchStoryView); the game then hides its own lines, keeps the map behind and the timing
rep('game/cl.java', """    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (this.b >= this.a.length) {
            return;
        }""", """    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if ((this.b >= this.a.length) || game.PortScreens.storyHidden) { // PORT-UI
            return;
        }""")
rep('game/cl.java', """    public int c() {
        return this.b;
    }""", """    public int c() {
        return this.b;
    }

    /** PORT-UI: how far the text has scrolled, 0..1 */
    public float portProgress() {
        int total = this.a.length * 15 + this.s;
        float done = this.b * 15 + this.q + this.s - this.e;
        return Math.max(0f, Math.min(1f, done / total));
    }""")
rep('game/df.java', """public df(boolean paramBoolean)""", """/** PORT-UI: 4 the intro scroll, 1 the ending scroll, -1 none */
public int portStory()
        {
        if ((this.c == 3) || (this.c == 0)) {
            return this.c == 3 ? 4 : 1; // the picture opening before the text
        }
        return (this.y != null) && !this.y.a() && ((this.c == 4) || (this.c == 1)) ? this.c : -1;
        }

/** PORT-UI: while the picture opens, its open part inside the picture (x, y, w, h); null once open */
public int[] portStoryOpening()
        {
        if ((this.c != 3) && (this.c != 0)) {
            return null;
        }
        return new int[] {this.C.a - this.A.a, this.C.b - this.A.b, this.C.c, this.C.d};
        }

public float portStoryProgress()
        {
        return this.y == null ? 0f : this.y.portProgress();
        }

public df(boolean paramBoolean)""")

# developer aid (screenshot tour of the desktop build): play one good move for the player
rep('game/bu.java', """    private int portEndWait;""", """    private int portEndWait;

    /** PORT-UI (tour): the player's move, a sword line when there is one; false when it is not the player's turn */
    public boolean portBotMove() {
        if ((this.v != 8) || (this.s == null) || (this.s.a() != 0)) {
            return false;
        }
        di moves = q();
        if ((moves == null) || (moves.b() == 0)) {
            return false;
        }
        int pick = 0;
        search:
        for (int k = 0; k < moves.b(); k++) {
            com.mic.smsgame.suquan.a m = (com.mic.smsgame.suquan.a) moves.b(k);
            int t1 = d(m.a, m.b), t2 = d(m.c, m.d);
            int[] types = {t2, t2, t1, t1};
            for (int i = 0; i < m.e.length; i++) {
                if (((m.e[i] & 0xFF) >= 3) && ((types[i] == 0) || (types[i] == 6))) {
                    pick = k;
                    break search;
                }
            }
        }
        com.mic.smsgame.suquan.a m = (com.mic.smsgame.suquan.a) moves.b(pick);
        a(m.a, m.b, m.c, m.d);
        return true;
    }""")

# the story scroll drawn by the touch interface: the game's soft key (BỎ QUA) is not drawn either
rep('game/ar.java', """    protected final void c(Graphics paramGraphics) {
        if (b()) {""", """    protected final void c(Graphics paramGraphics) {
        if (game.PortScreens.storyHidden && (this instanceof df)) { // PORT-UI
            return;
        }
        if (b()) {""")

# the tutorial battle starts with no items (the bag's items are not brought into it): only the item
# the tutorial gives (5 in a row) is there, and using it takes nothing from the bag
rep('game/cq.java', """        arrayOfInt[5] = 0;
        this.a = new dx[arrayOfInt.length];""", """        arrayOfInt[5] = 0;
        if (this.d) {
            arrayOfInt = new int[arrayOfInt.length]; // PORT-UI: no items in the tutorial
        }
        this.a = new dx[arrayOfInt.length];""")
rep('game/bu.java', """                arr[j].a();
                bg.g(type);""", """                arr[j].a();
                if (!this.s.d) {
                    bg.g(type); // PORT-UI: the tutorial's item is not from the bag
                }""")

# the map's "two swords" animation when the hero meets an enemy (s, before the battle box): its state for
# the touch map, which draws it itself over its own map
rep('game/s.java', """    public boolean c() {
        return this.i;
    }""", """    public boolean c() {
        return this.i;
    }

    /** PORT-UI: x, y (map), swords' offset, sparkle frame, burst (0/1), burst distance, done (0/1) */
    public int[] portState() {
        return new int[] {this.p, this.q, this.g, this.f, this.j ? 1 : 0, this.h, this.i ? 1 : 0};
    }""")
rep('com/mic/smsgame/suquan/aa.java', """    public boolean portVsReady() {""", """    /** PORT-UI: the swords animation before the battle box (s.portState), or null */
    public int[] portSlash() {
        return (this.f == 3) && (this.Z != null) ? this.Z.portState() : null;
    }

    public boolean portVsReady() {""")

# Ngọc Ấn (item 5) is not used by itself before a battle any more: on the touch interface the player
# chooses it on the prepare screen (PortScreens.useSeal); the arena does not use it
rep('com/mic/smsgame/suquan/aa.java', """                this.Z.a();
                if (bg.f(5) > 0) {""", """                this.Z.a();
                if (!game.PortScreens.touch && (bg.f(5) > 0)) { // PORT-UI: chosen on the prepare screen""")
rep('game/co.java', """        if (bg.f(5) > 0) {
            dd.h = true;
            bg.g(5);""", """        if ((!PortScreens.touch && (bg.f(5) > 0)) || PortMap.takeArenaSeal()) { // PORT-UI: Ngọc Ấn only when chosen
            dd.h = true;
            bg.g(5);""")

# the arena's first visit: its rules in a touch box (TouchArenaView) instead of the game's text screen
rep('com/mic/smsgame/suquan/aa.java', """                if (!this.ai) {
                    this.ai = true;
                    localObject = dj.a("Đấu trường",""", """                if (!this.ai && game.PortScreens.touch) { // PORT-UI
                    this.ai = true;
                    game.PortMap.arenaIntro = true;
                }
                if (!this.ai) {
                    this.ai = true;
                    localObject = dj.a("Đấu trường",""")

# gift boxes (touch): a battle's drops are boxes (the chest flies out of the board), a quest gives
# boxes too (PortMap.questBoxes); they wait in PortMap.boxes and are opened on the map (TouchBoxView)
rep('game/bu.java', """        int r = ay.a(100);
        int i = r < 15 ? 0 : (r < 30 ? 1 : (r < 55 ? 3 : (r < 75 ? 4 : 5)));
        this.s.a[i].b += 1;
        bg.a(i, 1, 6);
        this.portWon[i] += 1;
        return i;""", """        this.portBoxes += 1;
        PortMap.boxes += 1;
        return 6; // the chest (dn draws it instead of an item)""")
rep('game/bu.java', """    public int[] portWon = new int[6];""", """    public int portBoxes; // PORT-UI: gift boxes dropped in this battle
    public int[] portWon = new int[6];""")
rep('game/dn.java', """        if (this.b > 15) {
            if (this.b % 2 == 0) {
                y.a(paramGraphics, this.a, this.c * this.r, 0, this.r, this.s, this.p, this.q - 7, 3);
            }
        } else {
            y.a(paramGraphics, this.a, this.c * this.r, 0, this.r, this.s, this.p, this.q - (this.b >> 1), 3);
        }""", """        // PORT-UI: kind 6 is a gift box (r/g), the others the item sheet
        Image img = this.a;
        int sx = this.c * this.r, w = this.r, h = this.s;
        if (this.c >= 6) {
            if (portBox == null) {
                portBox = az.b("/r/g");
            }
            img = portBox;
            sx = 0;
            w = img.getWidth();
            h = img.getHeight();
        }
        if (this.b > 15) {
            if (this.b % 2 == 0) {
                y.a(paramGraphics, img, sx, 0, w, h, this.p, this.q - 7, 3);
            }
        } else {
            y.a(paramGraphics, img, sx, 0, w, h, this.p, this.q - (this.b >> 1), 3);
        }""")
rep('game/dn.java', """    private int d;
""", """    private int d;
    private static Image portBox; // PORT-UI
""")

# the quest's gift boxes are counted when its reward is given (A), for every quest that has them
rep('com/mic/smsgame/suquan/aa.java', """                this.ak = dd.f(this.z[this.B].e);
                this.am = dd.g(this.z[this.B].e);""", """                this.ak = dd.f(this.z[this.B].e);
                this.am = dd.g(this.z[this.B].e);
                if (game.PortScreens.touch) { // PORT-UI: its boxes wait to be opened on the map
                    game.PortMap.boxes += game.PortMap.questBoxes(this.z[this.B].e);
                }""")

# second chance (touch): beaten while the enemy is below half its HP, the battle stops on an offer
# (TouchReviveView): a rewarded ad brings the hero back with 30 HP, once per battle; or the defeat
rep('game/bu.java', """    private boolean y() {
        bn[] arrayOfbn = this.s.d();""", """    private boolean y() {
        bn[] arrayOfbn = this.s.d();
        if (portReviveHold(arrayOfbn)) {
            return true; // PORT-UI: waiting for the player's answer (the battle stands still)
        }""")
rep('game/bu.java', """    public int portBoxes;""", """    public int portBoxes;
    public boolean portReviveOffer;    // PORT-UI: the second chance is offered now
    public int portV() { return this.v; } // PORT-UI (tour): the battle's state
    public boolean portReviveUsed;     // taken (or turned down) in this battle

    /** PORT-UI: the hero has fallen, the enemy is below half its HP, no second chance yet */
    private boolean portReviveHold(bn[] f) {
        if (!PortScreens.touch || this.s.d || this.portReviveUsed || (f[0].r() > 0) || (f[1].r() <= 0)) {
            this.portReviveOffer = false;
            return false;
        }
        if (f[1].r() * 2 >= f[1].s()) {
            return false;
        }
        this.portReviveOffer = true;
        return true;
    }

    /** PORT-UI: the answer: back with 30 HP (an ad was watched), or the defeat goes on */
    public void portRevive(boolean back) {
        if (!this.portReviveOffer) {
            return;
        }
        this.portReviveOffer = false;
        this.portReviveUsed = true;
        if (back) {
            this.s.d()[0].m(30);
        }
    }""")

# a skill used (either side): counted so the touch view names it in a speech bubble at once
rep('game/bu.java', """        if (paramInt < 0) {
            return;
        }
        int i1 = arrayOfInt[paramInt];""", """        if (paramInt < 0) {
            return;
        }
        this.portSkillSide = i; // PORT-UI
        this.portSkillIndex = paramInt;
        this.portSkillCount += 1;
        int i1 = arrayOfInt[paramInt];""")
rep('game/bu.java', """    public int portBoxes;""", """    public int portBoxes;
    public int portSkillSide, portSkillIndex, portSkillCount; // PORT-UI: the last skill used, and how many so far""")


# sounds (touch): the gem explosion always plays (it used to be skipped while another sound was still
# playing), and the "being hit" sound starts with the hit animation instead of near its end
rep('game/bs.java', """    public void g() {
        if (l()) {
            return;
        }""", """    public void g() {
        if (game.PortScreens.touch) { // PORT-UI: every explosion is heard, restarting the clip
            c(this.G);
            a(this.G);
            return;
        }
        if (l()) {
            return;
        }""")
rep('game/bx.java', """        if (this.k < 1) {
            this.k = paramInt2;
        }
        if (this.k < paramInt2) {""", """        if (this.k < 1) {
            this.k = paramInt2;
            if (game.PortScreens.touch) {
                bs.a().f(); // PORT-UI: the hit sound with the start of the animation
            }
        }
        if (this.k < paramInt2) {""")
rep('game/bx.java', """            if (this.k == 7) {
                bs.a().f();
                if (this.j == 0) {""", """            if (this.k == 7) {
                if (!game.PortScreens.touch) {
                    bs.a().f();
                }
                if (this.j == 0) {""")

print('port_screens: ok')
