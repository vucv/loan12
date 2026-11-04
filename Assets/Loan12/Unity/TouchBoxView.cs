// Gift boxes (hộp quà): dropped in battles (a line of 4 or more) and given by quests, they wait in
// PortMap.boxes and are opened here, on the map, once the results and dialogues are over.
//   1. the closed chest (light rays, glow, twinkling stars) with how many boxes there are, MỞ HỘP
//   2. opening: the chest shakes harder and harder, a white flash, coins and stars burst out
//   3. what they held, one reward per box (PortMap.openBoxes: gold, experience or a shop item, the
//      dearer the rarer), the rows popping in one after the other with their numbers counting up, a
//      coloured frame by rarity; XEM QUẢNG CÁO · X2 (a rewarded ad gives it all once more) and NHẬN
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBoxView : ITouchView, IOwnsKeys
    {
        const float OpenTime = 1.0f;   // the opening animation
        const float GrantAt = 0.78f;   // when in it the boxes open (the flash)

        int stage; // 0 closed, 2 opening, 1 opened
        int count; // boxes opened
        bool doubled;
        float openedAt, openingAt, doubledAt = -10f;

        bool visible;          // on screen: stays until NHẬN, unless the game takes the map
        float calmSince = -1f; // since when the map has been free (it flickers between free and busy
        float lastCheck = -1f; // for a moment after the results): the boxes wait for it to settle
        float shownAt = -10f;  // the box screen fades in over the map

        // rewards in the order shown: gold, experience, then items from common to rare
        static readonly int[] Order = {2, C_PortMap.f_BOX_EXP, 4, 5, 3, C_PortMap.f_BOX_REFILL, 0, 1};

        sealed class Bit
        {
            public Vector2 p, v;
            public Color c;
            public float life, age, size;
            public bool star;
        }

        readonly List<Bit> bits = new List<Bit>();
        float lastFrame;
        static Texture2D glowTex, raysA, raysB;

        public bool Active()
        {
            try
            {
                float now = Time.unscaledTime;
                if (now - lastCheck > 0.2f)
                {
                    calmSince = -1f; // not asked for a while (another view had the screen): start again
                }
                lastCheck = now;
                bool onMap = C_PortScreens.screenKind() == 4 && !C_PortScreens.hasDialog();
                if (stage != 0)
                {
                    return onMap;
                }
                if (C_PortMap.f_boxes <= 0)
                {
                    visible = false;
                    return false;
                }
                if (visible)
                {
                    // a dialogue that starts behind waits for NHẬN (it waits for a tap anyway)
                    if (onMap && !C_PortMap.walking() && !C_PortUi.prepareView())
                    {
                        return true;
                    }
                    visible = false;
                    calmSince = -1f;
                    return false;
                }
                // right after the results: on the free map, or before the next dialogue (a quest's ending talk,
                // the next quest's opening) so the boxes come first
                if (onMap && (C_PortMap.free() || C_PortMap.talking()) && !C_PortMap.walking() && !C_PortUi.prepareView())
                {
                    if (calmSince < 0)
                    {
                        calmSince = now;
                    }
                    // a dialogue waits for a tap: no need to wait for it to settle
                    if (now - calmSince >= 0.5f || C_PortMap.talking())
                    {
                        visible = true;
                        shownAt = now;
                        return true;
                    }
                }
                else
                {
                    calmSince = -1f;
                }
                return false;
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
        }

        public int Stage
        {
            get { return stage; }
        }

        /// <summary>MỞ HỘP: the opening animation starts (the boxes open during it).</summary>
        public void StartOpening()
        {
            stage = 2;
            openingAt = Time.unscaledTime;
        }

        /// <summary>The boxes open into the bag (and the hero): straight to the list.</summary>
        public void Open()
        {
            count = C_PortMap.f_boxes;
            C_PortMap.openBoxes();
            Refill.Count = Refill.Count + C_PortMap.opened(C_PortMap.f_BOX_REFILL);
            stage = 1;
            doubled = false;
            openedAt = Time.unscaledTime;
        }

        /// <summary>NHẬN: back to the map.</summary>
        public void Take()
        {
            stage = 0;
            visible = false;
            bits.Clear();
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            Rewards.Tick();
            float k = TouchUi.K, H = Kit.H, now = Time.unscaledTime;
            if (stage == 2 && now - openingAt >= GrantAt && count == 0)
            {
                Open();
                stage = 2; // the animation goes on a moment
                Burst(haptics);
            }
            if (stage == 2 && now - openingAt >= OpenTime)
            {
                stage = 1;
                openedAt = now;
            }

            int rows = 0;
            foreach (int t in Order)
            {
                if (C_PortMap.opened(t) > 0)
                {
                    rows++;
                }
            }
            float panelH = stage == 1 ? 128 + rows * 26 : 176;
            float top = Mathf.Round((H - panelH) / 2);
            Rect panel = TouchUi.R(10, top, 160, panelH);
            Rect open = TouchUi.R(30, top + panelH - 40, 120, 26);
            Rect ad = TouchUi.R(22, top + panelH - 70, 136, 24);
            Rect take = TouchUi.R(46, top + panelH - 40, 88, 26);
            if (!Rewards.Busy)
            {
                if (stage == 0 && Kit.Tap(1, open))
                {
                    count = 0;
                    StartOpening();
                }
                else if (stage == 1 && !doubled && now - openedAt > 0.4f && Kit.Tap(2, ad))
                {
                    Rewards.Show("boxes_x2", r =>
                    {
                        if (r == 1 && !doubled)
                        {
                            C_PortMap.doubleOpened();
                            Refill.Count = Refill.Count + C_PortMap.opened(C_PortMap.f_BOX_REFILL);
                            doubled = true;
                            doubledAt = Time.unscaledTime;
                            Burst(haptics);
                        }
                    });
                }
                else if (stage == 1 && now - openedAt > 0.4f && Kit.Tap(3, take))
                {
                    Take();
                }
            }
            if (Kit.Back())
            {
                e.Use();
            }

            if (Kit.Paint)
            {
                float dt = Mathf.Clamp(now - lastFrame, 0f, 0.05f);
                lastFrame = now;
                float fade = Mathf.Clamp01((now - shownAt) / 0.25f);
                MapBackdrop.Draw(0.78f * fade);
                if (fade < 1f)
                {
                    // the panel rises into place as the map darkens
                    panel.y += (1f - fade) * 12f * k;
                    TouchUi.Panel(panel);
                    Rewards.DrawOverlay();
                    Kit.End();
                    return;
                }
                TouchUi.Panel(panel);
                TouchUi.PixelText("HỘP QUÀ", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 12, 0, 0).y, 1, true);
                if (stage == 1)
                {
                    DrawOpened(top, panelH, now, ad, take);
                }
                else
                {
                    DrawChest(top, now, open);
                }
                Bits(dt);
                // the flash of the opening
                if (stage != 0)
                {
                    float f = now - openingAt - GrantAt;
                    float a = f < 0 ? Mathf.Clamp01((f + 0.12f) / 0.12f) : Mathf.Clamp01(1f - f / 0.35f);
                    if (a > 0 && now - openingAt < OpenTime + 0.4f)
                    {
                        TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), new Color(1f, 0.97f, 0.85f, 0.85f * a));
                    }
                }
            }
            Rewards.DrawOverlay();
            Kit.End();
        }

        // ------------------------------------------------------------------------- the chest

        void DrawChest(float top, float now, Rect open)
        {
            float k = TouchUi.K;
            Vector2 c = TouchUi.R(90, top + 72, 0, 0).position;
            float opening = stage == 2 ? Mathf.Clamp01((now - openingAt) / GrantAt) : 0f;

            // light rays (two sets crossfading: they seem to turn) and a soft glow
            Textures();
            float rs = (96 + 24 * opening) * k;
            float turn = 0.5f + 0.5f * Mathf.Sin(now * (1.6f + 6f * opening));
            Color old = Gx.color;
            Gx.color = new Color(1f, 0.86f, 0.4f, (0.35f + 0.4f * opening) * turn);
            Gx.DrawTexture(new Rect(c.x - rs / 2, c.y - rs / 2, rs, rs), raysA);
            Gx.color = new Color(1f, 0.86f, 0.4f, (0.35f + 0.4f * opening) * (1f - turn));
            Gx.DrawTexture(new Rect(c.x - rs / 2, c.y - rs / 2, rs, rs), raysB);
            float gs = (70 + 10 * Mathf.Sin(now * 3f) + 40 * opening) * k;
            Gx.color = new Color(1f, 0.9f, 0.5f, 0.55f + 0.3f * opening);
            Gx.DrawTexture(new Rect(c.x - gs / 2, c.y - gs / 2, gs, gs), glowTex);
            Gx.color = old;

            // the chest: bobbing, then shaking harder and harder and growing as it opens
            Texture2D chest = TouchUi.Img("/r/g");
            float scale = 3f * (1f + 0.18f * opening * opening);
            float cw = chest.width * scale * k, ch = chest.height * scale * k;
            float bob = stage == 0 ? Mathf.Round(Mathf.Sin(now * 4f) * 2f) * k : 0f;
            float shake = opening * opening * 3.5f * k;
            float sx = shake > 0 ? Mathf.Round(Mathf.Sin(now * 70f) * shake) : 0f;
            float sy = shake > 0 ? Mathf.Round(Mathf.Cos(now * 53f) * shake * 0.5f) : 0f;
            TouchUi.Sprite(chest, new Rect(c.x - cw / 2 + sx, c.y - ch / 2 + bob + sy, cw, ch));

            // stars twinkling around it
            for (int i = 0; i < 6; i++)
            {
                float ph = now * 1.3f + i * 1.7f;
                float tw = Mathf.Max(0f, Mathf.Sin(ph * 2.2f));
                if (tw <= 0.05f)
                {
                    continue;
                }
                float ang = i * 1.047f + Mathf.Floor(ph / 2.86f);
                float rad = (34 + 8 * Mathf.Sin(i * 3.1f)) * k;
                Star(new Vector2(c.x + Mathf.Cos(ang) * rad, c.y + Mathf.Sin(ang) * rad * 0.8f), tw);
            }

            if (stage == 0)
            {
                int n = C_PortMap.f_boxes;
                TouchUi.Label(TouchUi.R(14, top + 108, 152, 14), n > 1 ? "Bạn nhận được " + n + " hộp quà" : "Bạn nhận được 1 hộp quà", 7.5f,
                    TextAnchor.MiddleCenter, Kit.Cream, true);
                TouchUi.Label(TouchUi.R(14, top + 122, 152, 12), "Vàng, kinh nghiệm hoặc vật phẩm quý", 6.5f, TextAnchor.MiddleCenter, Kit.Muted);
                // the button breathes a little
                float pulse = 1f + 0.03f * Mathf.Sin(now * 5f);
                Rect b = new Rect(open.center.x - open.width * pulse / 2, open.center.y - open.height * pulse / 2, open.width * pulse, open.height * pulse);
                TouchUi.RedButton(b, "MỞ HỘP", true, Kit.Down(1));
            }
            else
            {
                TouchUi.Label(TouchUi.R(14, top + 116, 152, 14), "Đang mở…", 7.5f, TextAnchor.MiddleCenter, Kit.Gold, true);
            }
        }

        // ------------------------------------------------------------------------- what came out

        void DrawOpened(float top, float panelH, float now, Rect ad, Rect take)
        {
            float k = TouchUi.K;
            float age = now - openedAt;
            TouchUi.Label(TouchUi.R(14, top + 24, 152, 12), "Đã mở " + count + " hộp quà" + (doubled ? " · x2" : ""), 7,
                TextAnchor.MiddleCenter, doubled ? TouchUi.TextGreen : Kit.Cream, true);
            float y = top + 40;
            int row = 0;
            foreach (int t in Order)
            {
                int got = C_PortMap.opened(t);
                if (got <= 0)
                {
                    continue;
                }
                // each row pops in (grows past its size and settles), its number counts up
                float ra = age - 0.15f - row * 0.16f;
                if (ra <= 0)
                {
                    y += 26;
                    row++;
                    continue;
                }
                float pop = ra < 0.18f ? Mathf.Lerp(0.3f, 1.12f, ra / 0.18f) : (ra < 0.3f ? Mathf.Lerp(1.12f, 1f, (ra - 0.18f) / 0.12f) : 1f);
                float da = now - doubledAt;
                if (doubled && da < 0.4f)
                {
                    pop *= 1f + 0.1f * Mathf.Sin(da / 0.4f * Mathf.PI); // a bump when doubled
                }
                int total = doubled ? got * 2 : got;
                int from = doubled ? got : 0;
                float countT = doubled ? Mathf.Clamp01(da / 0.5f) : Mathf.Clamp01((ra - 0.15f) / 0.5f);
                int shown = Mathf.RoundToInt(Mathf.Lerp(from, total, countT));

                Rect r = TouchUi.R(20, y, 140, 23);
                Rect s = new Rect(r.center.x - r.width * pop / 2, r.center.y - r.height * pop / 2, r.width * pop, r.height * pop);
                Color rim = Rim(t);
                if (rim.a > 0)
                {
                    float glow = 0.6f + 0.4f * Mathf.Sin(now * 4f + row);
                    TouchUi.Fill(new Rect(s.x - 1.5f * k, s.y - 1.5f * k, s.width + 3 * k, s.height + 3 * k), new Color(rim.r, rim.g, rim.b, rim.a * glow));
                }
                TouchUi.Fill(s, TouchUi.PurpleDark);
                Rect icon = new Rect(s.x + 3 * k * pop, s.y + 1.5f * k * pop, 20 * k * pop, 20 * k * pop);
                if (t == 2)
                {
                    TouchUi.Sprite(TouchUi.Img("/gold"), icon);
                }
                else if (t == C_PortMap.f_BOX_EXP)
                {
                    TouchUi.Sprite(TouchUi.Img("/book"), icon);
                }
                else
                {
                    MapUi.Icon(t == C_PortMap.f_BOX_REFILL ? Refill.Type : t, icon);
                }
                if (pop > 0.95f)
                {
                    TouchUi.Label(new Rect(s.x + 28 * k, s.y, 80 * k, s.height), Name(t), 6.5f, TextAnchor.MiddleLeft, rim.a > 0 ? rim : Color.white, rim.a > 0);
                    TouchUi.PixelText("+" + TouchUi.Money(shown), 1, s.xMax - 6 * k, s.center.y, 2, true);
                }
                // a rare one sparkles
                if (rim.a > 0 && ra > 0.3f)
                {
                    float tw = Mathf.Max(0f, Mathf.Sin(now * 3f + row * 2f));
                    Star(new Vector2(icon.xMax - k, icon.y + k), tw);
                }
                y += 26;
                row++;
            }
            TouchUi.Label(TouchUi.R(14, y + 2, 152, 12), "Đã cất vào túi đồ", 6.25f, TextAnchor.MiddleCenter, Kit.Muted);
            if (!doubled)
            {
                Kit.AdButton(ad, "XEM QUẢNG CÁO · X2", Kit.Down(2), !Rewards.Busy);
            }
            TouchUi.TextButton(take, "NHẬN", Btn.Red, Kit.Down(3));
        }

        static string Name(int t)
        {
            if (t == 2)
            {
                return "Vàng";
            }
            if (t == C_PortMap.f_BOX_EXP)
            {
                return "Kinh nghiệm";
            }
            return t == C_PortMap.f_BOX_REFILL ? MapUi.Name(Refill.Type) : C_PortUi.itemName(t);
        }

        /// <summary>The frame of a reward by its rarity: none (gold, experience, Bình Thuốc), blue (Ngọc Ấn),
        /// purple (Quỷ Diện Giáp, Bình Hồi Mạng), gold (Long Thần Kiếm, Nhân Sâm).</summary>
        static Color Rim(int t)
        {
            if (t == 5)
            {
                return new Color(0.45f, 0.75f, 1f, 1f);
            }
            if (t == 3 || t == C_PortMap.f_BOX_REFILL)
            {
                return new Color(0.85f, 0.5f, 1f, 1f);
            }
            if (t == 0 || t == 1)
            {
                return new Color(1f, 0.82f, 0.25f, 1f);
            }
            return new Color(0, 0, 0, 0);
        }

        // ------------------------------------------------------------------------- effects

        /// <summary>Coins, sparks and stars thrown out of the chest.</summary>
        void Burst(bool haptics)
        {
            Haptics.Tap(haptics);
            float k = TouchUi.K;
            Vector2 c = TouchUi.R(90, Mathf.Round((Kit.H - 176) / 2) + 72, 0, 0).position;
            Color[] cols = {TouchUi.Hex(0xFFD84A), TouchUi.Hex(0xFFF2B0), TouchUi.Hex(0xFF9A3A), TouchUi.Hex(0x8CD8FF), Color.white};
            for (int i = 0; i < 46; i++)
            {
                float a = Random.Range(0f, Mathf.PI * 2f);
                float sp = Random.Range(60f, 190f) * k;
                bits.Add(new Bit
                {
                    p = c,
                    v = new Vector2(Mathf.Cos(a) * sp, Mathf.Sin(a) * sp - 70f * k),
                    c = cols[i % cols.Length],
                    life = Random.Range(0.9f, 1.8f),
                    size = Mathf.Round(Random.Range(1.5f, 3.5f)) * k,
                    star = i % 6 == 0
                });
            }
        }

        void Bits(float dt)
        {
            float k = TouchUi.K;
            for (int i = bits.Count - 1; i >= 0; i--)
            {
                Bit b = bits[i];
                b.age += dt;
                if (b.age >= b.life)
                {
                    bits.RemoveAt(i);
                    continue;
                }
                b.v.y += 260f * k * dt; // gravity
                b.v *= 1f - 0.8f * dt;  // air
                b.p += b.v * dt;
                float a = 1f - b.age / b.life;
                if (b.star)
                {
                    Star(b.p, a);
                }
                else
                {
                    TouchUi.Fill(new Rect(Mathf.Round(b.p.x), Mathf.Round(b.p.y), b.size, b.size), new Color(b.c.r, b.c.g, b.c.b, a));
                }
            }
        }

        /// <summary>A twinkling star (the game's star sprite) of brightness a.</summary>
        static void Star(Vector2 c, float a)
        {
            if (a <= 0.02f)
            {
                return;
            }
            float k = TouchUi.K;
            Texture2D st = TouchUi.Img("/star");
            int fw = st.width / 3;
            float s = (6 + 5 * a) * k;
            Color old = Gx.color;
            Gx.color = new Color(1, 1, 1, a);
            TouchUi.Sprite(st, new Rect(c.x - s / 2, c.y - s / 2, s, s), new Rect(0, 0, fw, st.height));
            Gx.color = old;
        }

        /// <summary>A soft round glow and two sets of light rays, made once.</summary>
        static void Textures()
        {
            if (glowTex != null)
            {
                return;
            }
            const int n = 64;
            glowTex = new Texture2D(n, n, TextureFormat.RGBA32, false);
            raysA = new Texture2D(n * 2, n * 2, TextureFormat.RGBA32, false);
            raysB = new Texture2D(n * 2, n * 2, TextureFormat.RGBA32, false);
            for (int y = 0; y < n; y++)
            {
                for (int x = 0; x < n; x++)
                {
                    float d = new Vector2(x - n / 2f + 0.5f, y - n / 2f + 0.5f).magnitude / (n / 2f);
                    float a = Mathf.Clamp01(1f - d);
                    glowTex.SetPixel(x, y, new Color(1, 1, 1, a * a));
                }
            }
            int m = n * 2;
            for (int y = 0; y < m; y++)
            {
                for (int x = 0; x < m; x++)
                {
                    Vector2 v = new Vector2(x - m / 2f + 0.5f, y - m / 2f + 0.5f);
                    float d = v.magnitude / (m / 2f);
                    float ang = Mathf.Atan2(v.y, v.x);
                    float fall = Mathf.Clamp01(1f - d) * Mathf.Clamp01(d * 4f);
                    float ra = Mathf.Pow(Mathf.Max(0f, Mathf.Cos(ang * 6f)), 6f) * fall;
                    float rb = Mathf.Pow(Mathf.Max(0f, Mathf.Cos(ang * 6f + Mathf.PI / 2f)), 6f) * fall;
                    raysA.SetPixel(x, y, new Color(1, 1, 1, ra));
                    raysB.SetPixel(x, y, new Color(1, 1, 1, rb));
                }
            }
            glowTex.Apply();
            raysA.Apply();
            raysB.Apply();
        }
    }
}
