#!/usr/bin/env python3
"""Hand-checked repairs of JD-Core output that javac rejects (one entry per decompiler artefact)."""
import os
import sys, re

ROOT = os.path.join(os.environ.get('J2CS_WORK') or os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'jsrc') + os.sep
FIXES = []  # (file, old, new, count)


def fix(f, old, new, count=1):
    FIXES.append((f, old, new, count))


# --- null arguments: JD-Core dropped the cast that selected the overload -------------------------
# ar.a(de)/b(de)/c(de) set the soft keys (null clears), ar.a(h) sets the soft-key bar (null = no bar),
# ac.a(Image) opens the "Chat Ola" dialog. See PORTING_NOTES.md for the reasoning per call.
H = '(h) null'
DE = '(de) null'
fix('game/ck.java', '            a(null);\n            c(null);', '            a((de) null);\n            c((de) null);')
fix('game/ah.java', '            a(null);\n            c(null);', '            a((de) null);\n            c((de) null);')
fix('game/dc.java', '        v();\n        a(null);\n        a(this);', '        v();\n        a((h) null);\n        a(this);')
fix('game/dc.java', '            b(null);\n            c(null);\n            a(null);', '            b((de) null);\n            c((de) null);\n            a((de) null);')
fix('game/dc.java', '        c(null);\n        if (this.c[this.e] < 0) {', '        c((de) null);\n        if (this.c[this.e] < 0) {')
fix('game/dc.java', '        } else {\n            c(null);\n        }', '        } else {\n            c((de) null);\n        }')
fix('game/f.java', 'this.a.c(null);', 'this.a.c((de) null);')
fix('game/df.java', 'a(this);\n        a(null);', 'a(this);\n        a((game.h) null);')
fix('game/df.java', '        b(null);', '        b((de) null);')
fix('game/v.java', '    public v(int paramInt) {\n        a(null);', '    public v(int paramInt) {\n        a((h) null);')
fix('com/mic/smsgame/suquan/z.java', '        a(null);\n        a(this);', '        a((game.h) null);\n        a(this);')
fix('com/mic/smsgame/suquan/q.java', '        a(null);\n        a(this);', '        a((game.h) null);\n        a(this);')
fix('com/mic/smsgame/suquan/ab.java', '            a(cz.a());\n            a(null);', '            a(cz.a());\n            a((game.h) null);')
fix('com/mic/smsgame/suquan/ac.java', '        a(this);\n        a(null);\n        cf.a().a("menu", -1);', '        a(this);\n        a((game.h) null);\n        cf.a().a("menu", -1);')
fix('com/mic/smsgame/suquan/ac.java', '            case 6:\n                a(null);', '            case 6:\n                a((Image) null);')
fix('com/mic/smsgame/suquan/ac.java', '        localdj.a(null);', '        localdj.a((game.h) null);', 2)
fix('com/mic/smsgame/suquan/m.java', '        this.C.a(null);\n        this.C.a(null);', '        this.C.a((game.de) null);\n        this.C.a((game.h) null);')
fix('com/mic/smsgame/suquan/aa.java', '        a(null);\n        a(this);', '        a((game.h) null);\n        a(this);')
fix('com/mic/smsgame/suquan/aa.java', '                a(null);\n                c(null);\n                b(new da("Bỏ qua", 6));', '                a((game.de) null);\n                c((game.de) null);\n                b(new da("Bỏ qua", 6));')
fix('com/mic/smsgame/suquan/aa.java', '((ar) localObject).a(null);', '((ar) localObject).a((game.h) null);')

# --- JD-Core lost String conversions (String.valueOf / "" + x) -----------------------------------
fix('game/an.java', 'return (cx.F ? 1 : 0) + e + f + g + h + i + j;', 'return "" + (cx.F ? 1 : 0) + e + f + g + h + i + j;')
fix('game/an.java', 'dm.a(str1, g, new o(parama));', 'dm.a(str1, String.valueOf(g), new o(parama));')
fix('game/an.java', 'dm.a(str1, h, new n(parama));', 'dm.a(str1, String.valueOf(h), new n(parama));')
fix('game/an.java', 'dm.a(str1, i, new p(parama));', 'dm.a(str1, String.valueOf(i), new p(parama));')
fix('game/an.java', 'dm.a(str1, e, new r(parama));', 'dm.a(str1, String.valueOf(e), new r(parama));')
fix('game/an.java', 'dm.a("CSE " + c + " " + paramString, j, new q());', 'dm.a("CSE " + c + " " + paramString, String.valueOf(j), new q());')
fix('game/dd.java', 'str = ao.a(str, "{e}", i2);', 'str = ao.a(str, "{e}", String.valueOf(i2));')
for k, v in [('{e}', 'i'), ('{g}', 'j'), ('{h}', 'i'), ('{a}', 'j')]:
    fix('com/mic/smsgame/suquan/aa.java', 'game.ao.a((String) localObject, "%s", %s);' % (k, v),
        'game.ao.a((String) localObject, "%s", String.valueOf(%s));' % (k, v))
for fld in ['H', 'G', 'J', 'K', 'I']:
    fix('game/cr.java', 'bz.c.a(paramGraphics, this.%s, ' % fld, 'bz.c.a(paramGraphics, String.valueOf(this.%s), ' % fld, -1)
fix('com/mic/smsgame/suquan/g.java', 'bz.c.a(paramGraphics, this.c, ', 'bz.c.a(paramGraphics, String.valueOf(this.c), ')
fix('game/dj.java', 'String str2 = str1.charAt(0);', 'String str2 = String.valueOf(str1.charAt(0));')
fix('game/dj.java', 'String str3 = str1.charAt(1);', 'String str3 = String.valueOf(str1.charAt(1));')
fix('game/dj.java', 'String str4 = str1.charAt(2);', 'String str4 = String.valueOf(str1.charAt(2));')
fix('game/dj.java', 'if (str1.charAt(3).equals("$")) {', 'if (String.valueOf(str1.charAt(3)).equals("$")) {')
fix('game/dj.java', 'boolean bool = str1.charAt(4).equals("t");', 'boolean bool = String.valueOf(str1.charAt(4)).equals("t");')

# --- boolean stored as int by JD-Core --------------------------------------------------------------
fix('game/bg.java', 'dr.a(b, 13, new byte[]{i}, 0, 1);', 'dr.a(b, 13, new byte[]{(byte) i}, 0, 1);')
fix('game/bg.java', '''        i = 1;
        for (int j = 0; j < arrayOfBoolean.length; j++) {
            i &= arrayOfBoolean[j];
        }
        return i;''', '''        boolean all = true; // PORT-FIX: JD-Core merged this boolean with the int loop variable
        for (int j = 0; j < arrayOfBoolean.length; j++) {
            all &= arrayOfBoolean[j];
        }
        return all;''')
fix('game/bg.java', 'if (arrayOfBoolean[paramArrayOfInt[j]] != 0) {', 'if (arrayOfBoolean[paramArrayOfInt[j]]) {')
fix('game/dr.java', 'if (arrayOfBoolean[i] == 0) {', 'if (!arrayOfBoolean[i]) {')
fix('game/co.java', '(paramArrayOfBoolean[i1] != 0)', '(paramArrayOfBoolean[i1])')
fix('game/co.java', '} while (this.h[this.j] == 0);', '} while (!this.h[this.j]);', 2)
fix('game/co.java', 'if (this.z[i2] != 0) {', 'if (this.z[i2]) {')
fix('game/co.java', 'if (this.h[i2] == 0) {', 'if (!this.h[i2]) {')
fix('com/mic/smsgame/suquan/w.java', 'if (arrayOfBoolean[i11] != 0) {', 'if (arrayOfBoolean[i11]) {')
fix('com/mic/smsgame/suquan/w.java', '} while (arrayOfBoolean[i11] != 0);', '} while (arrayOfBoolean[i11]);')
fix('com/mic/smsgame/suquan/aa.java', '''        int tmp370_369 = 1;
        this.aq[1] = tmp370_369;
        this.aq[0] = tmp370_369;''', '''        this.aq[1] = true; // PORT-FIX: was `aq[0] = aq[1] = true` garbled by JD-Core
        this.aq[0] = true;''')
fix('com/mic/smsgame/suquan/aa.java', 'if (this.aq[2] == 0) {', 'if (!this.aq[2]) {')
fix('com/mic/smsgame/suquan/aa.java', '(this.aq[3] == 0)', '(!this.aq[3])')
fix('com/mic/smsgame/suquan/aa.java', '(this.aq[4] == 0)', '(!this.aq[4])')

# --- JD-Core typed locals as Object ---------------------------------------------------------------
fix('game/dc.java', 'this.C.a(new e[]{localObject2, ', 'this.C.a(new e[]{(e) localObject2, ')
fix('game/an.java', 'cz.d().e.a(i2, localObject4, localObject3);', 'cz.d().e.a(i2, (String) localObject4, (String) localObject3);')
fix('game/an.java', 'cz.d().e.a(i3, localObject5, (String) localObject6, localObject7);', 'cz.d().e.a(i3, (String) localObject5, (String) localObject6, (String) localObject7);')
fix('game/bu.java', 'localObject[j].a();', '((dx[]) localObject)[j].a();')
fix('game/bx.java', 'for (i8 = 0; i8 < localObject.length; i8++) {\n                game.y.b(paramGraphics, localObject[i8], ',
    'for (i8 = 0; i8 < ((int[]) localObject).length; i8++) {\n                game.y.b(paramGraphics, ((int[]) localObject)[i8], ')
fix('game/bx.java', 'paramGraphics.setColor(localObject[i9]);', 'paramGraphics.setColor(((int[]) localObject)[i9]);')

# --- wrong class picked because of same simple name in two packages --------------------------------
fix('com/mic/smsgame/suquan/aa.java', 'import game.ad;', 'import game.ad;\nimport game.ae;\nimport game.ea;')
fix('com/mic/smsgame/suquan/y.java', 'this.k = new z();', 'this.k = new game.z();')

# --- checked exceptions JD-Core did not declare ---------------------------------------------------
fix('game/cf.java', 'private void b(String paramString, int paramInt) {', 'private void b(String paramString, int paramInt) throws Exception {')

# --- second round ----------------------------------------------------------------------------------
fix('com/mic/smsgame/suquan/aa.java', '        a(null);\n        c(null);\n        b(null);', '        a((game.de) null);\n        c((game.de) null);\n        b((game.de) null);')
fix('com/mic/smsgame/suquan/aa.java', 'game.an.b("", cx.G, new ae(this));', 'game.an.b("", cx.G, new com.mic.smsgame.suquan.ae(this));')
fix('game/bc.java', '} catch (UnsupportedEncodingException localUnsupportedEncodingException) {', '} catch (Exception localUnsupportedEncodingException) { // PORT-FIX: JD-Core dropped the throws clause of b.a(String)')

fix('com/mg/smsgame/main/MGMIDlet.java', 'public void a(String paramString) {', 'public void a(String paramString) throws ConnectionNotFoundException { // PORT-FIX: JD-Core dropped throws')
fix('com/mg/smsgame/main/MGMIDlet.java', 'import javax.microedition.io.Connector;', 'import javax.microedition.io.ConnectionNotFoundException;\nimport javax.microedition.io.Connector;')

fix('game/bg.java', '    private final String test;\n', '')
fix('game/bg.java', 'public static int a(byte[] paramArrayOfByte, InputStream paramInputStream, int paramInt) {', 'public static int a(byte[] paramArrayOfByte, InputStream paramInputStream, int paramInt) throws java.io.IOException {')

fix('game/ao.java', '        return paramString1.getBytes(paramString2);', '        try { // PORT-FIX: JD-Core dropped `throws UnsupportedEncodingException`\n            return paramString1.getBytes(paramString2);\n        } catch (java.io.UnsupportedEncodingException e) {\n            return paramString1.getBytes();\n        }')

fix('com/mic/smsgame/suquan/k.java', 'private void b(String paramString, int paramInt) {', 'private void b(String paramString, int paramInt) throws Exception {')

# dd.f is a static field of type c; these calls mean the class suquan.f (record-store helper)
for call in ['f.a(101, paramInt);', 'return f.b(101);', 'f.a(103, paramInt);', 'return f.b(103);',
             'String str = f.a(104);', 'f.a(104, paramString);', 'return f.b(105);', 'f.a(105, paramInt);']:
    fix('game/dd.java', '        ' + call, '        ' + call.replace('f.', 'com.mic.smsgame.suquan.f.', 1))

# ao.c no longer throws (see above) -> these catch clauses become catch-alls
for v in ['localUnsupportedEncodingException)', 'localUnsupportedEncodingException1)', 'localUnsupportedEncodingException2)']:
    fix('game/al.java', '} catch (UnsupportedEncodingException ' + v, '} catch (Exception ' + v, -1)

# a(this) bound to the synthetic accessor `static a(<own class>)` instead of ar.a(listener)
fix('game/dc.java', '        a((h) null);\n        a(this);', '        a((h) null);\n        a((com.mg.smsgame.main.b) this); // PORT-FIX: overload (was bound to the static accessor)')
fix('com/mic/smsgame/suquan/aa.java', '        a((game.h) null);\n        a(this);', '        a((game.h) null);\n        a((com.mg.smsgame.main.b) this); // PORT-FIX: overload (was bound to the static accessor)')
fix('com/mic/smsgame/suquan/q.java', '        a((game.h) null);\n        a(this);', '        a((game.h) null);\n        a((com.mg.smsgame.main.b) this); // PORT-FIX: overload (was bound to the static accessor)')


def main():
    files = {}
    bad = 0
    for f, old, new, count in FIXES:
        if f not in files:
            files[f] = open(ROOT + f, encoding='utf-8').read()
        s = files[f]
        if new and new in s and old in new:
            continue
        n = s.count(old)
        if n == 0:
            if s.count(new) > 0:
                continue  # already applied
            print('NOT FOUND', f, repr(old[:70]))
            bad += 1
            continue
        if count != -1 and n != count:
            print('COUNT MISMATCH', f, n, '!=', count, repr(old[:70]))
            bad += 1
            continue
        files[f] = s.replace(old, new)
    for f, s in files.items():
        open(ROOT + f, 'w', encoding='utf-8').write(s)
    print('fixes:', len(FIXES), 'problems:', bad)


main()
