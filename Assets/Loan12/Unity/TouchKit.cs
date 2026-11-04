// Immediate-mode helpers for the touch screens of steps 2 and 3 (menu, settings, characters, map,
// bag, shop, quests): full-height layout on the 180-wide grid, tap / scroll handling, and the shared
// pieces of those screens (top resource bar, switches, sliders, framed buttons, toasts).
//
// Each view calls Kit.Begin(e) at the start of OnGUI and Kit.End() at its end, and between them both
// draws (on Repaint) and asks Kit.Tap(id, rect) for taps, so drawing and hit areas never disagree.
// Kit.Modal blocks every tap that is not inside the popup drawn after it.
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class Kit
    {
        public static Event E;
        /// <summary>Height of the layout in grid pixels (the width is always 180).</summary>
        public static float H;

        static int pressed = -1;
        static Vector2 downAt;
        static bool moved;
        static bool blocked;
        static Rect allowed;
        static bool clipped;
        static Rect clip;
        static Vector2 lastDrag;

        public static bool Paint
        {
            get { return E != null && E.type == EventType.Repaint; }
        }

        public static bool Haptic = true;

        /// <summary>Full-height layout: whole-number scale from the width, height from the safe area.</summary>
        public static void Begin(Event e, float minH = 300)
        {
            E = e;
            blocked = false;
            clipped = false;
            Rect safe = Gx.SafeArea;
            Rect area = new Rect(safe.x, Gx.Height - safe.yMax, safe.width, safe.height);
            if (area.width <= 0 || area.height <= 0)
            {
                area = new Rect(0, 0, Gx.Width, Gx.Height);
            }
            float k = Mathf.Min(area.width / TouchUi.GridW, area.height / minH);
            if (k >= 1f)
            {
                k = Mathf.Floor(k);
            }
            H = Mathf.Floor(area.height / k);
            TouchUi.BeginAt(k, new Vector2(Mathf.Round(area.x + (area.width - TouchUi.GridW * k) / 2f), area.y));
            J2MEGameHost host = J2MEGameHost.Instance;
            Haptic = host == null || host.HapticsOn;
            if (e.type == EventType.MouseDown && e.button == 0)
            {
                downAt = e.mousePosition;
                lastDrag = downAt;
                moved = false;
                pressed = -1;
            }
            else if (e.type == EventType.MouseDrag)
            {
                if ((e.mousePosition - downAt).magnitude > 6 * TouchUi.K)
                {
                    moved = true;
                }
            }
        }

        public static void End()
        {
            if (E.type == EventType.MouseUp)
            {
                pressed = -1;
                E.Use();
            }
            else if (E.type == EventType.MouseDown || E.type == EventType.MouseDrag)
            {
                E.Use(); // the game underneath never sees taps on these screens
            }
        }

        /// <summary>From here on only taps inside r count (a popup); outside them, Outside() is true.</summary>
        public static void Modal(Rect r)
        {
            blocked = true;
            allowed = r;
        }

        public static void Unblock()
        {
            blocked = false;
        }

        /// <summary>
        /// Until Unclip, taps count only inside r: the content of a scrolled area that slides under the bars
        /// above and below it must not take their taps.
        /// </summary>
        public static void Clip(Rect r)
        {
            clipped = true;
            clip = r;
        }

        public static void Unclip()
        {
            clipped = false;
        }

        static bool Allowed(Vector2 p)
        {
            return (!blocked || allowed.Contains(p)) && (!clipped || clip.Contains(p));
        }

        /// <summary>A tap released inside r (the press started inside r, and the finger did not scroll).</summary>
        public static bool Tap(int id, Rect r)
        {
            if (E.type == EventType.MouseDown && E.button == 0 && r.Contains(E.mousePosition) && Allowed(E.mousePosition) && pressed < 0)
            {
                pressed = id;
                Haptics.Tap(Haptic);
                return false;
            }
            if (E.type == EventType.MouseUp && pressed == id)
            {
                pressed = -1;
                bool hit = r.Contains(E.mousePosition) && !moved;
                return hit;
            }
            return false;
        }

        /// <summary>The finger is down on the element (for the pressed look).</summary>
        public static bool Down(int id)
        {
            return pressed == id && !moved;
        }

        /// <summary>The press started on the element and is still held (for hold-to-confirm buttons).</summary>
        public static bool Held(int id)
        {
            return pressed == id;
        }

        /// <summary>A tap outside the popup set by Modal (closes it).</summary>
        public static bool Outside()
        {
            if (E.type == EventType.MouseDown && E.button == 0 && blocked && !allowed.Contains(E.mousePosition))
            {
                pressed = -2;
                return false;
            }
            if (E.type == EventType.MouseUp && pressed == -2)
            {
                pressed = -1;
                return !allowed.Contains(E.mousePosition);
            }
            return false;
        }

        /// <summary>Vertical drag of this event in grid pixels (scrolling lists), 0 otherwise.</summary>
        public static Vector2 Drag()
        {
            if (E.type != EventType.MouseDrag)
            {
                return Vector2.zero;
            }
            Vector2 d = (E.mousePosition - lastDrag) / TouchUi.K;
            lastDrag = E.mousePosition;
            return moved ? d : Vector2.zero;
        }

        public static bool Dragging
        {
            get { return moved; }
        }

        public static bool Back()
        {
            return E.type == EventType.KeyDown && (E.keyCode == KeyCode.Escape || E.keyCode == KeyCode.Backspace);
        }

        // ------------------------------------------------------------------ toast

        static string toast;
        static float toastAt = -10f;
        static float toastFor = 2.2f;

        public static void Toast(string s, float seconds = 2.2f)
        {
            toast = s;
            toastAt = Time.unscaledTime;
            toastFor = seconds;
        }

        public static void DrawToast(float y)
        {
            float t = Time.unscaledTime - toastAt;
            if (!Paint || t > toastFor || string.IsNullOrEmpty(toast))
            {
                return;
            }
            Color old = Gx.color;
            Gx.color = old * new Color(1, 1, 1, Mathf.Clamp01((toastFor - t) / 0.3f));
            float w = Mathf.Min(170, TouchUi.TextWidth(toast) + 16);
            int lines = TouchUi.Lines(toast, w - 10);
            float h = lines * UiFont.LineHeight + 10;
            Rect r = TouchUi.R(90 - w / 2, y - h / 2, w, h);
            TouchUi.Fill(new Rect(r.x - TouchUi.K, r.y - TouchUi.K, r.width + 2 * TouchUi.K, r.height + 2 * TouchUi.K), TouchUi.GoldLine);
            TouchUi.Fill(r, new Color(0.08f, 0.03f, 0.08f, 0.94f));
            TouchUi.Label(new Rect(r.x + 5 * TouchUi.K, r.y, r.width - 10 * TouchUi.K, r.height), toast, 6.5f, TextAnchor.MiddleCenter, Color.white, false, true);
            Gx.color = old;
        }

        // ------------------------------------------------------------------ pieces

        public static readonly Color Gold = TouchUi.Hex(0xFFD400);
        public static readonly Color Cream = TouchUi.Hex(0xF4E4C8);
        public static readonly Color Muted = TouchUi.Hex(0xB8A8BC);
        public static readonly Color Green = TouchUi.Hex(0x8BE05C);
        public static readonly Color Red = TouchUi.Hex(0xFF6A5A);
        public static readonly Color Group = TouchUi.Hex(0x773B75);
        public static readonly Color Line = TouchUi.Hex(0x9A6A98);

        /// <summary>Dark box with a gold line (top bar items, info boxes).</summary>
        public static void Box(Rect r, Color fill)
        {
            float k = TouchUi.K;
            TouchUi.Fill(r, TouchUi.GoldLine);
            TouchUi.Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), TouchUi.Outline);
            TouchUi.Fill(new Rect(r.x + 2 * k, r.y + 2 * k, r.width - 4 * k, r.height - 4 * k), fill);
        }

        /// <summary>A settings group: purple with a gold line, no corner ornaments.</summary>
        public static void GroupBox(Rect r)
        {
            Box(r, Group);
        }

        /// <summary>The game's framed button (m/ff) filled with another colour (purple, dark ...).</summary>
        public static void FramedButton(Rect r, Color fill, bool down)
        {
            float k = TouchUi.K;
            TouchUi.Nine(TouchUi.Img("/m/ff"), 12, r);
            TouchUi.Fill(new Rect(r.x + 4 * k, r.y + 4 * k, r.width - 8 * k, r.height - 8 * k), fill);
            if (down)
            {
                TouchUi.Fill(new Rect(r.x + 4 * k, r.y + 4 * k, r.width - 8 * k, r.height - 8 * k), new Color(1f, 0.85f, 0.4f, 0.22f));
            }
        }

        /// <summary>The big red framed button with a gold title and an optional second line.</summary>
        public static void MainButton(Rect r, string title, string sub, bool enabled, bool down)
        {
            if (string.IsNullOrEmpty(sub))
            {
                TouchUi.RedButton(r, title, enabled, down);
                return;
            }
            Color old = Gx.color;
            if (!enabled)
            {
                Gx.color = new Color(0.5f, 0.46f, 0.5f, 1f);
            }
            TouchUi.Nine(TouchUi.Img("/m/ff"), 12, r);
            float k = TouchUi.K, dy = down ? k : 0;
            if (down)
            {
                TouchUi.Fill(new Rect(r.x + 4 * k, r.y + 4 * k, r.width - 8 * k, r.height - 8 * k), new Color(1f, 0.85f, 0.4f, 0.25f));
            }
            TouchUi.PixelText(title, 1, r.center.x, r.y + r.height * 0.36f + dy, 1, true);
            TouchUi.Label(new Rect(r.x, r.y + r.height * 0.56f + dy, r.width, 9 * k), sub, 6.5f, TextAnchor.UpperCenter, Cream);
            Gx.color = old;
        }

        /// <summary>A flat button (green ad, gold price, grey ...) with a label in the small font.</summary>
        public static void FlatButton(Rect r, string label, Btn style, bool down, bool enabled = true)
        {
            TouchUi.Button(r, enabled ? style : Btn.Grey, down && enabled);
            Color old = Gx.color;
            if (!enabled)
            {
                Gx.color = old * new Color(0.75f, 0.72f, 0.78f, 1f);
            }
            TouchUi.Label(new Rect(r.x, r.y + (down && enabled ? TouchUi.K : 0), r.width, r.height), label, 7, TextAnchor.MiddleCenter, Color.white, true);
            Gx.color = old;
        }

        /// <summary>Green "watch an ad" button: a play triangle, then the label (the small font has no ▶).</summary>
        public static void AdButton(Rect r, string label, bool down, bool enabled = true)
        {
            float k = TouchUi.K;
            TouchUi.Button(r, enabled ? Btn.Green : Btn.Grey, down && enabled);
            float dy = down && enabled ? k : 0;
            float w = TouchUi.TextWidth(label) + 9;
            float x = r.center.x - w * k / 2;
            Play(x, r.center.y + dy, Color.white);
            Color old = Gx.color;
            if (!enabled)
            {
                Gx.color = old * new Color(0.75f, 0.72f, 0.78f, 1f);
            }
            TouchUi.Label(new Rect(x + 8 * k, r.y + dy, w * k, r.height), label, 7, TextAnchor.MiddleLeft, Color.white, true);
            Gx.color = old;
        }

        /// <summary>Small right-pointing triangle (play), 5 wide, centred on cy.</summary>
        public static void Play(float x, float cy, Color c)
        {
            float k = TouchUi.K;
            for (int i = 0; i < 5; i++)
            {
                float h = (9 - 2 * i) * k;
                TouchUi.Fill(new Rect(x + i * k, cy - h / 2, k, h), c);
            }
        }

        /// <summary>Back arrow button of a sub page (18x18).</summary>
        public static void BackArrow(Rect r, bool down)
        {
            float k = TouchUi.K;
            Box(r, down ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x2B1426));
            Color c = Cream;
            float cx = r.x + r.width * 0.5f, cy = r.center.y;
            // shaft
            TouchUi.Fill(new Rect(cx - 2 * k, cy - k, 7 * k, 3 * k), c);
            // head
            for (int i = 0; i < 5; i++)
            {
                TouchUi.Fill(new Rect(cx - 6 * k + i * k, cy - i * k, k, (2 * i + 1) * k), c);
            }
        }

        /// <summary>A pixel gear (settings).</summary>
        public static void Gear(Rect r, bool down)
        {
            float k = TouchUi.K;
            Box(r, down ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x2B1426));
            Color c = TouchUi.Hex(0xD8D0C8);
            Vector2 m = r.center;
            // body ring and teeth on a 12x12 pattern
            string[] g =
            {
                "....##....",
                ".#.####.#.",
                "##########",
                ".###..###.",
                "###....###",
                "###....###",
                ".###..###.",
                "##########",
                ".#.####.#.",
                "....##....",
            };
            float s = Mathf.Max(1, Mathf.Floor(r.width / k / 14)) * k;
            float ox = m.x - 5 * s, oy = m.y - 5 * s;
            for (int y = 0; y < g.Length; y++)
            {
                for (int x = 0; x < g[y].Length; x++)
                {
                    if (g[y][x] == '#')
                    {
                        TouchUi.Fill(new Rect(ox + x * s, oy + y * s, s, s), c);
                    }
                }
            }
        }

        /// <summary>The resource bar: lives (shared) / gold with + / settings gear. Returns 1 lives, 2 gold, 3 gear.</summary>
        public static int TopBar(float y, bool gear = true)
        {
            int hit = 0;
            Rect lives = TouchUi.R(4, y, 64, 18), goldBox = TouchUi.R(72, y, gear ? 84 : 104, 18), gearR = TouchUi.R(158, y, 18, 18);
            int n = 0, max = 4, gold = 0;
            try
            {
                n = C_PortScreens.lives();
                max = C_PortScreens.maxLives();
                gold = C_PortScreens.gold();
            }
            catch (System.Exception)
            {
            }
            if (Tap(9001, lives))
            {
                hit = 1;
            }
            if (Tap(9002, goldBox))
            {
                hit = 2;
            }
            if (gear && Tap(9003, gearR))
            {
                hit = 3;
            }
            if (Paint)
            {
                float k = TouchUi.K;
                Box(lives, TouchUi.Hex(0x22101E));
                TouchUi.Sprite(TouchUi.Img("/m/ih"), TouchUi.R(9, y + 2, 14, 14));
                int left = n < max ? LifeTimer.Left : -1;
                if (left >= 0)
                {
                    // not full: the count, and the time to the next life (+1 every 30 minutes)
                    TouchUi.PixelText(n + "/" + max, 1, lives.x + 22 * k, lives.center.y, 0, true);
                    TouchUi.Label(new Rect(lives.x + 38 * k, lives.y, 24 * k, lives.height), LifeTimer.Clock(left), 5.5f, TextAnchor.MiddleRight, Kit.Cream, true);
                }
                else
                {
                    TouchUi.PixelText(n + "/" + max, 2, lives.x + 22 * k, lives.center.y, 0, true);
                }
                Box(goldBox, TouchUi.Hex(0x22101E));
                TouchUi.Sprite(TouchUi.Img("/gold"), TouchUi.R(76, y + 1, 16, 16));
                TouchUi.PixelText(TouchUi.Money(gold), 1, goldBox.x + 21 * k, goldBox.center.y, 0, true);
                Rect plus = new Rect(goldBox.xMax - 15 * k, goldBox.y + 3 * k, 12 * k, 12 * k);
                TouchUi.Button(plus, Btn.Green, Down(9002));
                TouchUi.Fill(new Rect(plus.center.x - k, plus.y + 3 * k, 2 * k, 6 * k), Color.white);
                TouchUi.Fill(new Rect(plus.x + 3 * k, plus.center.y - k, 6 * k, 2 * k), Color.white);
                if (gear)
                {
                    Gear(gearR, Down(9003));
                }
            }
            return hit;
        }

        /// <summary>Title bar of a sub page: back arrow + pixel title. True when the arrow is tapped.</summary>
        public static bool TitleBar(string title, float y = 4)
        {
            Rect back = TouchUi.R(4, y, 18, 18);
            bool hit = Tap(9010, back);
            if (Paint)
            {
                BackArrow(back, Down(9010));
                TouchUi.PixelText(title, 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, y + 9, 0, 0).y, 1, true);
            }
            return hit;
        }

        /// <summary>On / off switch (green when on).</summary>
        public static void Switch(Rect r, bool on, bool enabled = true)
        {
            float k = TouchUi.K;
            Color old = Gx.color;
            if (!enabled)
            {
                Gx.color = old * new Color(1, 1, 1, 0.45f);
            }
            TouchUi.Fill(r, TouchUi.Outline);
            TouchUi.Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), on ? TouchUi.Hex(0x2F9A3C) : TouchUi.Hex(0x4E4A50));
            float knob = r.height - 4 * k;
            float x = on ? r.xMax - 2 * k - knob : r.x + 2 * k;
            TouchUi.Fill(new Rect(x, r.y + 2 * k, knob, knob), TouchUi.Hex(0xF0E6D2));
            Gx.color = old;
        }

        /// <summary>Horizontal slider, value 0..1; returns the new value while dragged.</summary>
        public static float Slider(int id, Rect hit, Rect bar, float value, bool enabled)
        {
            float k = TouchUi.K;
            if (enabled)
            {
                Vector2 p = E.mousePosition;
                if (E.type == EventType.MouseDown && E.button == 0 && hit.Contains(p) && Allowed(p))
                {
                    pressed = id;
                    value = Mathf.Clamp01((p.x - bar.x) / bar.width);
                }
                else if (E.type == EventType.MouseDrag && pressed == id)
                {
                    value = Mathf.Clamp01((p.x - bar.x) / bar.width);
                    moved = false; // a slider drag is not a scroll
                }
            }
            if (Paint)
            {
                Color old = Gx.color;
                if (!enabled)
                {
                    Gx.color = old * new Color(1, 1, 1, 0.4f);
                }
                Rect track = new Rect(bar.x, bar.center.y - 2 * k, bar.width, 4 * k);
                TouchUi.Fill(track, TouchUi.Outline);
                TouchUi.Fill(new Rect(track.x, track.y + k, track.width * value, 2 * k), TouchUi.Hex(0x8CC4FF));
                float kx = bar.x + bar.width * value;
                TouchUi.Fill(new Rect(kx - 5 * k, bar.center.y - 5 * k, 10 * k, 10 * k), TouchUi.Outline);
                TouchUi.Fill(new Rect(kx - 4 * k, bar.center.y - 4 * k, 8 * k, 8 * k), TouchUi.Hex(0xF0E6D2));
                Gx.color = old;
            }
            return value;
        }

        public static bool SliderReleased(int id)
        {
            return E.type == EventType.MouseUp && pressed == id;
        }

        /// <summary>"›" chevron at the right of a row.</summary>
        public static void Chevron(float xRight, float cy)
        {
            float k = TouchUi.K;
            for (int i = 0; i < 4; i++)
            {
                TouchUi.Fill(new Rect(xRight - 6 * k + i * k, cy - 4 * k + i * k, k, k), Cream);
                TouchUi.Fill(new Rect(xRight - 6 * k + i * k, cy + 3 * k - i * k, k, k), Cream);
            }
        }

        /// <summary>Check mark (✓) in a colour, about 7x6 grid pixels.</summary>
        public static void Check(float x, float y, Color c)
        {
            float k = TouchUi.K;
            int[] ys = {3, 4, 5, 4, 3, 2, 1};
            for (int i = 0; i < ys.Length; i++)
            {
                TouchUi.Fill(new Rect(x + i * k, y + ys[i] * k, k, 2 * k), c);
            }
        }

        /// <summary>
        /// "Something new" mark: the game's small red exclamation mark (/m/mt1), bouncing and pulsing.
        /// n is kept for the callers (counts) but only the mark is drawn.
        /// </summary>
        public static void Badge(float cx, float cy, int n)
        {
            float k = TouchUi.K;
            float t = Time.unscaledTime;
            float bob = Mathf.Round(Mathf.Abs(Mathf.Sin(t * 4f)) * -2.5f) * k; // little hops
            float pulse = 1f + 0.12f * Mathf.Max(0, Mathf.Sin(t * 8f));
            float w = 4.5f * pulse * k, h = 15f * pulse * k;
            Rect r = new Rect(Mathf.Round(cx - w / 2), Mathf.Round(cy - h / 2 + bob), w, h);
            // soft glow behind it
            TouchUi.Fill(new Rect(r.x - k, r.y + k, r.width + 2 * k, r.height - 2 * k), new Color(1f, 0.2f, 0.1f, 0.25f + 0.15f * Mathf.Sin(t * 8f)));
            TouchUi.Sprite(TouchUi.Img("/m/mt1"), r, new Rect(0, 0, 7, 24));
        }

        /// <summary>Spinner (waiting for an ad, a purchase ...).</summary>
        public static void Spinner(Vector2 c)
        {
            float k = TouchUi.K;
            int step = (int) (Time.unscaledTime * 10) % 8;
            for (int i = 0; i < 8; i++)
            {
                float a = i * Mathf.PI / 4;
                float alpha = ((i - step + 8) % 8) / 8f;
                Rect d = new Rect(c.x + Mathf.Cos(a) * 5 * k - k, c.y + Mathf.Sin(a) * 5 * k - k, 2 * k, 2 * k);
                TouchUi.Fill(d, new Color(1, 1, 1, 0.25f + 0.75f * alpha));
            }
        }

        public static string ElementName(int e)
        {
            return e == 0 ? "Hỏa" : (e == 1 ? "Lôi" : "Thủy");
        }

        /// <summary>"Hùng Dũng" from the game's upper-case names ("HÙNG DŨNG").</summary>
        public static string Title(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return "";
            }
            System.Text.StringBuilder b = new System.Text.StringBuilder(s.Length);
            bool start = true;
            foreach (char ch in s)
            {
                b.Append(start ? char.ToUpperInvariant(ch) : char.ToLowerInvariant(ch));
                start = ch == ' ';
            }
            return b.ToString();
        }

        public static string Cut(string s, int max)
        {
            return s.Length > max ? s.Substring(0, max - 1) + "…" : s;
        }
    }

    /// <summary>
    /// Rewarded ads and the VIP flag (plan/README.md). PLACEHOLDER until the ad SDK is integrated: an ad
    /// is simulated by a short wait and always rewarded, so every flow can be played and tested.
    /// </summary>
    public static class Rewards
    {
        const string VipPref = "l12.vip";
        static System.Action<int> pending;
        static float until;

        public static bool Vip
        {
            get { return PlayerPrefs.GetInt(VipPref, 0) == 1; }
            set
            {
                PlayerPrefs.SetInt(VipPref, value ? 1 : 0);
                PlayerPrefs.Save();
            }
        }

        public static bool Ready
        {
            get { return true; }
        }

        public static bool Busy
        {
            get { return pending != null; }
        }

        /// <summary>Shows a rewarded ad; done(1) watched to the end, done(0) closed early, done(-1) no ad.</summary>
        public static void Show(string placement, System.Action<int> done)
        {
            if (pending != null)
            {
                return;
            }
            Debug.Log("Loan12: rewarded ad '" + placement + "' (simulated)");
            pending = done;
            until = Time.unscaledTime + 1.5f;
        }

        /// <summary>Called by the views every frame: finishes a simulated ad.</summary>
        public static void Tick()
        {
            if (pending != null && Time.unscaledTime >= until)
            {
                System.Action<int> d = pending;
                pending = null;
                d(1);
            }
        }

        /// <summary>The simulated ad covers the screen like a real one.</summary>
        public static void DrawOverlay()
        {
            if (pending == null || !Kit.Paint)
            {
                return;
            }
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), new Color(0, 0, 0, 0.92f));
            TouchUi.Label(TouchUi.R(10, Kit.H / 2 - 20, 160, 12), "QUẢNG CÁO (THỬ NGHIỆM)", 7, TextAnchor.MiddleCenter, Kit.Gold, true);
            TouchUi.Label(TouchUi.R(10, Kit.H / 2 - 6, 160, 12), "Sẽ thay bằng quảng cáo thật", 6, TextAnchor.MiddleCenter, Kit.Muted);
            Kit.Spinner(TouchUi.R(90, Kit.H / 2 + 16, 0, 0).position);
        }
    }
}
