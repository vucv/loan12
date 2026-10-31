// Headless test driver for the C# translation: same script language, clock and backend behaviour as the
// Java harness (harness/src/Run.java + DesktopBackend.java), so screenshots can be compared pixel by pixel.
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using J2ME;

namespace HarnessCs
{
    public sealed class DesktopBackend : Backend
    {
        public long now = 1400000000000L;
        readonly string resRoot;
        readonly string j2meRoot;
        public readonly SortedDictionary<string, sbyte[]> stores = new SortedDictionary<string, sbyte[]>(StringComparer.Ordinal);
        public readonly List<string> events = new List<string>();
        public bool exited;
        static readonly Dictionary<string, double> Dur = new Dictionary<string, double>();

        static DesktopBackend()
        {
            string[][] d =
            {
                new[] {"intro", "25.25"}, new[] {"menu", "50"}, new[] {"m", "53.3"}, new[] {"victory", "32"}, new[] {"win", "6.27"},
                new[] {"lose", "5.6"}, new[] {"attack", "0.76"}, new[] {"explodechess", "0.38"}, new[] {"fire1", "1.34"}, new[] {"fire2", "2.06"},
                new[] {"fire3", "1.59"}, new[] {"ice1", "2.08"}, new[] {"ice2", "2.58"}, new[] {"ice3", "2.04"}, new[] {"lightning1", "2.1"},
                new[] {"lightning2", "1.86"}, new[] {"lightning3", "2.1"}, new[] {"lvu", "2.04"}, new[] {"useitem", "2.15"}, new[] {"vs", "1.65"}
            };
            foreach (string[] e in d)
            {
                Dur[e[0]] = double.Parse(e[1], System.Globalization.CultureInfo.InvariantCulture);
            }
        }

        sealed class Clip
        {
            public string name;
            public long start;
            public int loops;
            public bool playing;
            public long durUs;
        }

        public DesktopBackend(string resRoot, string j2meRoot)
        {
            this.resRoot = resRoot;
            this.j2meRoot = j2meRoot;
        }

        public readonly SortedSet<string> requested = new SortedSet<string>(StringComparer.Ordinal);

        public sbyte[] getResource(string path)
        {
            requested.Add(path);
            string f = path.StartsWith("/j2me/", StringComparison.Ordinal)
                ? Path.Combine(j2meRoot, path.Substring(6))
                : Path.Combine(resRoot, path.TrimStart('/'));
            if (!File.Exists(f))
            {
                Console.WriteLine("[res] missing " + path);
                return null;
            }
            return JBytes.ToSBytes(File.ReadAllBytes(f));
        }

        public long currentTimeMillis()
        {
            return now;
        }

        public string getProperty(string key)
        {
            return key == "microedition.platform" ? "J2ME-Unity" : null;
        }

        public string getAppProperty(string key)
        {
            return null;
        }

        public sbyte[] loadStore(string name)
        {
            sbyte[] b;
            return stores.TryGetValue(name, out b) ? (sbyte[]) b.Clone() : null;
        }

        public void saveStore(string name, sbyte[] data)
        {
            stores[name] = (sbyte[]) data.Clone();
        }

        public void deleteStore(string name)
        {
            stores.Remove(name);
        }

        public string[] listStores()
        {
            return new List<string>(stores.Keys).ToArray();
        }

        public object audioOpen(string resourcePath, string contentType)
        {
            Clip c = new Clip();
            c.name = resourcePath.Substring(resourcePath.LastIndexOf('/') + 1, resourcePath.LastIndexOf('.') - resourcePath.LastIndexOf('/') - 1);
            double d;
            c.durUs = (long) ((Dur.TryGetValue(c.name, out d) ? d : 1.0) * 1e6);
            events.Add("audioOpen " + c.name);
            return c;
        }

        public void audioStart(object h, int loopCount)
        {
            Clip c = (Clip) h;
            c.start = now;
            c.loops = loopCount;
            c.playing = true;
            events.Add("audioStart " + c.name + " loops=" + loopCount);
        }

        public void audioStop(object h)
        {
            ((Clip) h).playing = false;
        }

        public void audioClose(object h)
        {
            ((Clip) h).playing = false;
        }

        public void audioVolume(object h, int level, bool muted)
        {
        }

        public void audioRate(object h, int ratePermille)
        {
            if (ratePermille != 1000)
            {
                events.Add("audioRate " + ((Clip) h).name + " " + ratePermille);
            }
        }

        public bool audioIsPlaying(object h)
        {
            Clip c = (Clip) h;
            if (!c.playing)
            {
                return false;
            }
            if (c.loops < 0)
            {
                return true;
            }
            long el = (now - c.start) * 1000;
            if (el >= c.durUs * c.loops)
            {
                c.playing = false;
                return false;
            }
            return true;
        }

        public long audioDuration(object h)
        {
            return ((Clip) h).durUs;
        }

        public long audioPosition(object h)
        {
            Clip c = (Clip) h;
            if (!c.playing)
            {
                return 0;
            }
            return ((now - c.start) * 1000) % c.durUs;
        }

        public void audioSetPosition(object h, long micros)
        {
        }

        public bool platformRequest(string url)
        {
            events.Add("platformRequest " + url);
            return false;
        }

        public void exitRequested()
        {
            exited = true;
            events.Add("exit");
        }

        public void vibrate(int millis)
        {
        }

        public void log(string message)
        {
            Console.WriteLine("[log] " + message);
        }
    }

    public static class Program
    {
        static DesktopBackend b;
        static string outDir;
        static string lastShown = "";

        public static int Main(string[] args)
        {
            System.Globalization.CultureInfo.CurrentCulture = System.Globalization.CultureInfo.InvariantCulture;
            string script = args[0];
            outDir = args[1];
            Directory.CreateDirectory(outDir);
            b = new DesktopBackend(args.Length > 2 ? args[2] : "decompiled", args.Length > 3 ? args[3] : "assets/j2me");
            Host.init(b, 240, 320);
            MIDlet m = new Loan12.SQMidlet();
            Host.start(m);
            foreach (string raw in File.ReadAllLines(script))
            {
                string line = raw.Trim();
                if (line.Length == 0 || line.StartsWith("#", StringComparison.Ordinal))
                {
                    continue;
                }
                string[] p = line.Split(new[] {' ', '\t'}, 2, StringSplitOptions.RemoveEmptyEntries);
                string arg = p.Length > 1 ? p[1].Trim() : "";
                switch (p[0])
                {
                    case "wait":
                        Advance(int.Parse(arg));
                        break;
                    case "key":
                        foreach (string k in arg.Split(new[] {' ', '\t'}, StringSplitOptions.RemoveEmptyEntries))
                        {
                            int code = int.Parse(k);
                            Host.keyPressed(code);
                            Advance(60);
                            Host.keyReleased(code);
                            Advance(200);
                        }
                        break;
                    case "tap":
                    {
                        string[] xy = arg.Split(new[] {' ', '\t'}, StringSplitOptions.RemoveEmptyEntries);
                        int x = int.Parse(xy[0]), y = int.Parse(xy[1]);
                        Host.pointerPressed(x, y);
                        Advance(60);
                        Host.pointerReleased(x, y);
                        Advance(200);
                        break;
                    }
                    case "shot":
                        Shot(arg);
                        break;
                    case "burst":
                    {
                        string[] q = arg.Split(new[] {' ', '\t'}, StringSplitOptions.RemoveEmptyEntries);
                        int n = int.Parse(q[1]), iv = int.Parse(q[2]);
                        for (int k = 0; k < n; k++)
                        {
                            Shot(q[0] + "_" + (k < 10 ? "0" : "") + k);
                            Advance(iv);
                        }
                        break;
                    }
                    case "text":
                    {
                        Displayable d = Host.display.getShown();
                        TextBox tb = d as TextBox;
                        if (tb != null)
                        {
                            tb.setString(arg);
                            Console.WriteLine("TextBox <- " + arg);
                        }
                        else
                        {
                            Console.WriteLine("WARN: no TextBox shown (" + d + ")");
                        }
                        break;
                    }
                    case "cmd":
                    {
                        Displayable d = Host.display.getShown();
                        bool ok = false;
                        for (int i = 0; d != null && i < d.getCommandCount(); i++)
                        {
                            Command c = d.getCommand(i);
                            if (c.getLabel() == arg)
                            {
                                Host.queueCommand(d, c);
                                ok = true;
                            }
                        }
                        if (!ok)
                        {
                            Console.WriteLine("WARN: no command " + arg);
                        }
                        Advance(100);
                        break;
                    }
                    case "monkey":
                        Monkey(arg);
                        break;
                    case "stores":
                        foreach (KeyValuePair<string, sbyte[]> e in b.stores)
                        {
                            Console.WriteLine("store " + e.Key + " " + e.Value.Length + " bytes");
                        }
                        break;
                    default:
                        Console.WriteLine("unknown: " + line);
                        break;
                }
            }
            Console.WriteLine("events: [" + string.Join(", ", b.events) + "]");
            Console.WriteLine("resources: " + string.Join(" ", b.requested));
            return 0;
        }

        static bool monkeyMode;
        static int exitsIgnored;
        static readonly int[] MonkeyKeys = {-1, -2, -3, -4, -5, -5, -5, 53, 50, 56, 52, 54, -6, -7, 35, 42, 48, 49, 51, 55, 57};

        /// <summary>monkey seed steps shotEvery: random keys / taps / pauses (same sequence as the Java harness).</summary>
        static void Monkey(string arg)
        {
            string[] q = arg.Split(new[] {' ', '\t'}, StringSplitOptions.RemoveEmptyEntries);
            long seed = long.Parse(q[0]);
            int steps = int.Parse(q[1]);
            int shotEvery = int.Parse(q[2]);
            bool win = q.Length > 3 && q[3] == "win";
            J2ME.Random r = new J2ME.Random(seed);
            monkeyMode = true;
            for (int k = 0; k < steps; k++)
            {
                Displayable d = Host.display.getShown();
                if (d != null && !(d is J2ME.Canvas))
                {
                    TextBox tb = d as TextBox;
                    if (tb != null)
                    {
                        tb.setString("Monkey");
                    }
                    int want = tb != null ? Command.OK : Command.BACK;
                    Command pick = null;
                    for (int i = 0; i < d.getCommandCount(); i++)
                    {
                        Command c = d.getCommand(i);
                        if (pick == null || c.getCommandType() == want)
                        {
                            pick = c;
                        }
                        if (c.getCommandType() == want)
                        {
                            break;
                        }
                    }
                    if (pick != null)
                    {
                        Host.queueCommand(d, pick);
                    }
                    Advance(100);
                    continue;
                }
                if (win)
                {
                    WeakenEnemy();
                    if (BotMove())
                    {
                        Advance(300);
                        continue;
                    }
                }
                int choice = r.nextInt(100);
                if (choice < 85)
                {
                    int code = MonkeyKeys[r.nextInt(MonkeyKeys.Length)];
                    Host.keyPressed(code);
                    Advance(60);
                    Host.keyReleased(code);
                    Advance(40 + r.nextInt(400));
                }
                else if (choice < 95)
                {
                    int x = r.nextInt(240), y = r.nextInt(320);
                    Host.pointerPressed(x, y);
                    Advance(60);
                    Host.pointerReleased(x, y);
                    Advance(100 + r.nextInt(300));
                }
                else
                {
                    Advance(500 + r.nextInt(2000));
                }
                if (shotEvery > 0 && k % shotEvery == 0)
                {
                    Shot("m" + seed + "_" + k.ToString("D5"));
                }
            }
            monkeyMode = false;
            Console.WriteLine("monkey: exits ignored " + exitsIgnored + ", enemies weakened " + weakened + ", bot moves " + botMoves);
            Shot("m" + seed + "_end");
        }

        static int weakened;
        static int botMoves;

        /// <summary>Test bot: on the player's turn (board waiting for input) play a valid move, preferring sword matches.</summary>
        static bool BotMove()
        {
            object canvas = Loan12.C_cz.f_a;
            if (canvas == null)
            {
                return false;
            }
            object screens = Fld(typeof(Loan12.C_cz), "f_e").GetValue(canvas);
            object cur = screens == null ? null : Fld(typeof(Loan12.C_bv), "f_b").GetValue(screens);
            if (!(cur is Loan12.SQ_ab))
            {
                return false;
            }
            Loan12.C_bu board = (Loan12.C_bu) Fld(typeof(Loan12.SQ_ab), "f_e").GetValue(cur);
            Loan12.C_cq st = (Loan12.C_cq) Fld(typeof(Loan12.SQ_ab), "f_c").GetValue(cur);
            if (board == null || st == null)
            {
                return false;
            }
            if ((int) Fld(typeof(Loan12.C_bu), "f_v").GetValue(board) != 8 || st.a() != 0)
            {
                return false;
            }
            Loan12.C_di moves = board.q();
            if (moves == null || moves.b() == 0)
            {
                return false;
            }
            int pick = 0;
            bool found = false;
            for (int k = 0; k < moves.b() && !found; k++)
            {
                Loan12.SQ_a m = (Loan12.SQ_a) moves.b(k);
                int t1 = board.d(m.f_a, m.f_b), t2 = board.d(m.f_c, m.f_d);
                int[] types = {t2, t2, t1, t1};
                for (int i = 0; i < m.f_e.Length; i++)
                {
                    if ((m.f_e[i] & 0xFF) >= 3 && (types[i] == 0 || types[i] == 6))
                    {
                        pick = k;
                        found = true;
                        break;
                    }
                }
            }
            Loan12.SQ_a mv = (Loan12.SQ_a) moves.b(pick);
            board.a(mv.f_a, mv.f_b, mv.f_c, mv.f_d);
            botMoves++;
            return true;
        }

        static System.Reflection.FieldInfo Fld(Type t, string name)
        {
            return t.GetField(name, System.Reflection.BindingFlags.Instance | System.Reflection.BindingFlags.Public | System.Reflection.BindingFlags.NonPublic);
        }

        /// <summary>Test cheat for exploring later stages: in a battle, the computer opponent is left with 1 HP.</summary>
        static void WeakenEnemy()
        {
            object canvas = Loan12.C_cz.f_a; // the instance, if created (d() would create it)
            if (canvas == null)
            {
                return;
            }
            object screens = Fld(typeof(Loan12.C_cz), "f_e").GetValue(canvas);
            object cur = screens == null ? null : Fld(typeof(Loan12.C_bv), "f_b").GetValue(screens);
            if (!(cur is Loan12.SQ_ab))
            {
                return;
            }
            object board = Fld(typeof(Loan12.SQ_ab), "f_c").GetValue(cur);
            if (board == null)
            {
                return;
            }
            Loan12.C_bn[] fighters = (Loan12.C_bn[]) Fld(typeof(Loan12.C_cq), "f_f").GetValue(board);
            System.Reflection.FieldInfo hp = Fld(typeof(Loan12.C_bn), "f_c");
            foreach (Loan12.C_bn f in fighters)
            {
                if (f != null && f.b() >= 6 && (int) hp.GetValue(f) > 1)
                {
                    hp.SetValue(f, 1);
                    weakened++;
                }
            }
        }

        static void Advance(int ms)
        {
            for (int t = 0; t < ms; t += 20)
            {
                b.now += 20;
                Host.tick();
                Displayable d = Host.display != null ? Host.display.getShown() : null;
                string name = d == null ? "null" : d.GetType().Name;
                if (name != lastShown)
                {
                    Console.WriteLine("t=" + (b.now - 1400000000000L) + " shown: " + name);
                    lastShown = name;
                }
                if (monkeyMode && (b.exited || Host.exitRequested))
                {
                    // keep exploring: the game asked to quit, pretend it did not happen
                    b.exited = false;
                    Host.exitRequested = false;
                    exitsIgnored++;
                }
                if (b.exited)
                {
                    Console.WriteLine("EXIT requested");
                    return;
                }
            }
        }

        static void Shot(string name)
        {
            int[] s = Host.screen;
            byte[] bytes = new byte[s.Length * 4];
            Buffer.BlockCopy(s, 0, bytes, 0, bytes.Length);
            File.WriteAllBytes(Path.Combine(outDir, name + ".raw"), bytes);
            Console.WriteLine("shot " + name);
        }
    }
}
