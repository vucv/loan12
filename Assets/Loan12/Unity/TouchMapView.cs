// Screen 5 of the touch redesign: the map and the 4-tab bar (plan/thiet-ke-ban-do.md 2-3).
//
// Drawn over the game's map screen (suquan.aa), which keeps walking, encounters, quests and saving.
//   top bar (lives, gold +, ⚙) and the quest tracker
//   the map at a whole-number scale, dragged with one finger, following the hero; locked cities,
//   open roads (gold dots), the way about to be walked (white), quest / event / guard marks, the
//   selection ring, the hero; an arrow at the edge towards the main quest when it is off screen
//   a tap on a place walks there at once (on the hero's own place: its dialogue / battle); the
//   "Đang đi tới … · Dừng" strip while walking
//   tab bar: Bản đồ · Túi đồ · Cửa hàng · Nhiệm vụ (the other tabs are pages drawn by their classes)
// Back: closes the panel / goes back to the map tab / opens the settings (map version).
using System.Collections;
using UnityEngine;

namespace Loan12.UnityPort
{
    /// <summary>A tab of the map screen, drawn between the top bar and the tab bar.</summary>
    public interface IMapTab
    {
        void OnGUI(TouchMapView map, float top, float bottom);
        /// <summary>Back pressed: true when the tab handled it (closed its popup).</summary>
        bool Back();
    }

    public sealed class TouchMapView : ITouchView, IOwnsKeys
    {
        public const int TabMap = 0, TabBag = 1, TabShop = 2, TabQuest = 3;
        public const float NavH = 37;

        public int tab;
        public readonly IMapTab[] tabs = new IMapTab[4];

        TouchSettingsPage settings;
        int selected = -1;
        float camX, camY, velX, velY;
        float lastTouch = -10f;
        bool camReady;
        bool dragMap;
        public int tutorial = -1;
        int lastSlot = -1;
        readonly int[] route = new int[64];
        int routeLen;
        int routeFor = -2;

        public static TouchMapView Instance { get; private set; }

        public TouchMapView()
        {
            Instance = this;
            tabs[TabBag] = new TouchBagTab();
            tabs[TabShop] = new TouchShopTab();
            tabs[TabQuest] = new TouchQuestTab();
        }

        public bool Active()
        {
            try
            {
                return C_PortScreens.screenKind() == 4 && C_PortMap.free();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            settings = null;
            dragMap = false;
            camReady = false;
            if (C_PortScreens.f_slot != lastSlot)
            {
                // another character (or back from the menu): tabs start fresh
                lastSlot = C_PortScreens.f_slot;
                tab = TabMap;
                selected = -1;
            }
            else if (!C_PortMap.walking() && selected >= 0 && selected == C_PortMap.playerNode())
            {
                selected = -1; // back from a battle / dialogue at the node
            }
        }

        // ------------------------------------------------------------------------- layout

        float ViewTop
        {
            get { return 24; }
        }

        float ViewBottom
        {
            get { return Kit.H - NavH; }
        }

        /// <summary>Screen pixels per map pixel: the map always shows about 240 map pixels across.</summary>
        // where a locked town's lock goes, from the town's point (x, y in the map picture), by town
        static readonly int[] LockAt = {-8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -8, -9, -4, -9, -8, -9, -8, -4, -8, -3, -8, 0, -8, -9, -8, -2, -22, -1, -8, -9, -3, -9, 3, -9};

        static float S
        {
            get { return Mathf.Max(1, Mathf.Floor(Gx.Width / 240f)); }
        }

        Rect ViewRect()
        {
            Rect a = TouchUi.R(0, ViewTop, 180, ViewBottom - ViewTop);
            return new Rect(0, a.y, Gx.Width, a.height);
        }

        Vector2 ToScreen(float mx, float my)
        {
            Rect v = ViewRect();
            return new Vector2(v.x + (mx - camX) * S, v.y + (my - camY) * S);
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            Rewards.Tick();
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0A121E));
            }
            if (settings != null)
            {
                if (!settings.OnGUI(e))
                {
                    settings = null;
                }
                Kit.End();
                return;
            }
            // a message of the game (event done, arena level open), once
            if (C_PortMap.f_note != null)
            {
                Kit.Toast(C_PortMap.f_note, 4f);
                C_PortMap.f_note = null;
            }
            if (!camReady)
            {
                CenterOn(C_PortMap.playerX(), C_PortMap.playerY(), true);
                camReady = true;
            }
            if (tab == TabMap)
            {
                MapTab();
            }
            else
            {
                // the tab's list scrolls under the bars: its taps stay between them
                Rect area = TouchUi.R(0, ViewTop, 180, ViewBottom - ViewTop);
                Kit.Clip(new Rect(0, area.y, Gx.Width, area.height));
                tabs[tab].OnGUI(this, ViewTop, ViewBottom);
                Kit.Unclip();
            }

            if (Kit.Paint)
            {
                // solid behind the resource bar (and the notch area above it)
                TouchUi.Fill(new Rect(0, 0, Gx.Width, TouchUi.R(0, ViewTop, 0, 0).y), TouchUi.Hex(0x0A121E));
            }
            int bar = Kit.TopBar(4);
            if (bar == 1)
            {
                TouchLivesView.Requested = true; // lives: watch an ad / Bình Hồi Mạng
            }
            else if (bar == 2)
            {
                OpenTab(TabShop);
                ((TouchShopTab) tabs[TabShop]).sub = 1;
            }
            else if (bar == 3)
            {
                OpenSettings();
            }
            NavBar();

            if (tutorial >= 0)
            {
                Tutorial();
            }
            else if (Kit.Back())
            {
                e.Use();
                if (tab != TabMap)
                {
                    if (!tabs[tab].Back())
                    {
                        tab = TabMap;
                    }
                }
                else if (selected >= 0)
                {
                    selected = -1;
                }
                else
                {
                    OpenSettings();
                }
            }
            Kit.DrawToast(ViewBottom - 14);
            Rewards.DrawOverlay();
            Kit.End();

            // first time on the map for this character: the 3-step tutorial
            if (tutorial < 0 && Kit.Paint && tab == TabMap && !TutorialSeen() && C_PortMap.state() == 0 && !C_PortMap.walking())
            {
                StartTutorial();
            }
        }

        public void OpenTab(int t)
        {
            if (t != TabMap && C_PortMap.walking())
            {
                C_PortMap.request(C_PortMap.f_STOP, 0); // stops at the end of the road (plan 2.4)
            }
            tab = t;
            if (t == TabQuest)
            {
                ((TouchQuestTab) tabs[TabQuest]).Opened();
            }
        }

        void OpenSettings()
        {
            if (C_PortMap.walking())
            {
                C_PortMap.request(C_PortMap.f_STOP, 0);
            }
            settings = new TouchSettingsPage(TouchSettingsPage.Where.Map);
            settings.OnMenu = () => C_PortMap.request(C_PortMap.f_MENU, 0);
            settings.OnSwitchCharacter = () =>
            {
                C_PortMap.request(C_PortMap.f_MENU, 0);
                if (TouchMenuView.Instance != null)
                {
                    TouchMenuView.Instance.Open(new TouchSlotsPage());
                }
            };
            settings.OnMapTutorial = StartTutorial;
        }

        /// <summary>Map tab, centred on a node with its info panel (quests tab: "Đi tới").</summary>
        public void ShowNode(int n, bool go)
        {
            tab = TabMap;
            selected = go ? -1 : n;
            CenterOn(C_PortMap.nodeX(n), C_PortMap.nodeY(n), false);
            lastTouch = Time.unscaledTime;
            if (go)
            {
                Go(n);
            }
        }

        // ------------------------------------------------------------------------- camera

        void CenterOn(float mx, float my, bool now)
        {
            Rect v = ViewRect();
            float tx = mx - v.width / S / 2, ty = my - v.height / S / 2;
            // with the info panel open, the hero is pushed up above it
            if (selected >= 0)
            {
                ty += 41 * TouchUi.K / S;
            }
            Clamp(ref tx, ref ty);
            if (now)
            {
                camX = tx;
                camY = ty;
            }
            else
            {
                goalX = tx;
                goalY = ty;
                gliding = true;
            }
        }

        float goalX, goalY;
        bool gliding;

        void Clamp(ref float x, ref float y)
        {
            Rect v = ViewRect();
            float vw = v.width / S, vh = v.height / S;
            x = vw >= 380 ? (380 - vw) / 2 : Mathf.Clamp(x, 0, 380 - vw);
            y = vh >= 380 ? (380 - vh) / 2 : Mathf.Clamp(y, 0, 380 - vh);
        }

        void UpdateCamera()
        {
            float dt = Mathf.Min(0.05f, Time.unscaledDeltaTime);
            if (!dragMap)
            {
                camX += velX * dt;
                camY += velY * dt;
                float decay = Mathf.Pow(0.02f, dt);
                velX *= decay;
                velY *= decay;
            }
            if (gliding)
            {
                float t = 1 - Mathf.Pow(0.0005f, dt);
                camX = Mathf.Lerp(camX, goalX, t);
                camY = Mathf.Lerp(camY, goalY, t);
                if (Mathf.Abs(camX - goalX) < 0.3f && Mathf.Abs(camY - goalY) < 0.3f)
                {
                    gliding = false;
                }
            }
            else if (!dragMap && Time.unscaledTime - lastTouch > 3f)
            {
                // follow the hero (slides back 3 s after the last drag)
                Rect v = ViewRect();
                float tx = C_PortMap.playerX() - v.width / S / 2, ty = C_PortMap.playerY() - v.height / S / 2;
                if (selected >= 0)
                {
                    ty += 41 * TouchUi.K / S;
                }
                Clamp(ref tx, ref ty);
                float t = 1 - Mathf.Pow(0.004f, dt);
                camX = Mathf.Lerp(camX, tx, t);
                camY = Mathf.Lerp(camY, ty, t);
            }
            Clamp(ref camX, ref camY);
        }

        // ------------------------------------------------------------------------- map tab


        Rect StripRect()
        {
            return TouchUi.R(4, ViewBottom - 17, 172, 15);
        }

        void MapTab()
        {
            Rect view = ViewRect();
            Event e = Kit.E;
            bool walking = C_PortMap.walking();
            Rect panel = walking ? StripRect() : new Rect(0, 0, 0, 0);
            Rect tracker = TouchUi.R(4, 26, 172, 15);

            // drag the map / tap a node
            if (e.type == EventType.MouseDown && e.button == 0 && view.Contains(e.mousePosition) && !panel.Contains(e.mousePosition)
                && !tracker.Contains(e.mousePosition))
            {
                dragMap = true;
                velX = velY = 0;
                gliding = false;
            }
            if (dragMap)
            {
                Vector2 d = Kit.Drag();
                if (d != Vector2.zero)
                {
                    float mx = d.x * TouchUi.K / S, my = d.y * TouchUi.K / S;
                    camX -= mx;
                    camY -= my;
                    float dt = Mathf.Max(0.008f, Time.unscaledDeltaTime);
                    velX = -mx / dt;
                    velY = -my / dt;
                    lastTouch = Time.unscaledTime;
                }
                if (e.type == EventType.MouseUp)
                {
                    dragMap = false;
                    if (!Kit.Dragging)
                    {
                        velX = velY = 0;
                        TapMap(e.mousePosition);
                    }
                }
            }
            if (Kit.Paint)
            {
                UpdateCamera();
                DrawMap(view);
            }

            // quest tracker
            int q = C_PortMap.questIndex(), total = C_PortMap.questCount();
            if (q < total)
            {
                if (Kit.Tap(100, tracker))
                {
                    ShowNode(C_PortMap.questNode(q), true);
                }
                if (Kit.Paint)
                {
                    DrawTracker(tracker, q, total);
                    DrawQuestArrow(view, q);
                }
            }

            // walking strip ("Đang đi tới … · Dừng"); a tap on a place walks there at once
            if (walking)
            {
                Strip();
            }
        }

        void TapMap(Vector2 p)
        {
            Rect v = ViewRect();
            float best = (11 * TouchUi.K) * (11 * TouchUi.K);
            int hit = -1, locked = -1;
            for (int n = 0; n < C_PortMap.nodeCount(); n++)
            {
                Vector2 s = ToScreen(C_PortMap.nodeX(n), C_PortMap.nodeY(n));
                float d = (s - p).sqrMagnitude;
                if (d < best)
                {
                    if (C_PortMap.nodeOpen(n))
                    {
                        best = d;
                        hit = n;
                        locked = -1;
                    }
                    else if (C_PortMap.nodeType(n) == 0)
                    {
                        locked = n;
                    }
                }
            }
            // the hero himself: his node
            Vector2 hs = ToScreen(C_PortMap.playerX(), C_PortMap.playerY());
            if (hit < 0 && (hs - p).sqrMagnitude < best)
            {
                hit = C_PortMap.playerNode();
            }
            if (hit >= 0)
            {
                // a tap walks straight there (no info panel to confirm first)
                selected = -1;
                string place = QuestData.Place(hit);
                if (hit == C_PortMap.playerNode() && !C_PortMap.walking())
                {
                    if (C_PortMap.nodeContent(hit) != 0)
                    {
                        Go(hit); // standing there: what the game does on arrival (dialogue, battle, arena)
                    }
                    else
                    {
                        Kit.Toast("Bạn đang ở " + place);
                    }
                }
                else if (C_PortMap.roadsTo(hit) < 0)
                {
                    Kit.Toast("Chưa có đường tới " + place);
                }
                else
                {
                    Go(hit); // also a new destination while walking, from the end of the current road
                }
            }
            else if (locked >= 0)
            {
                Kit.Toast("Chưa mở. Làm tiếp nhiệm vụ chính để mở vùng này.");
            }
            else
            {
                selected = -1;
            }
        }

        void Go(int n)
        {
            if (n == C_PortMap.playerNode() && !C_PortMap.walking())
            {
                C_PortMap.request(C_PortMap.f_START, 0);
                return;
            }
            C_PortMap.request(C_PortMap.f_GO, n);
            lastTouch = -10f; // the camera follows the hero again
        }

        // ------------------------------------------------------------------------- drawing

        void DrawMap(Rect view)
        {
            float s = S, k = TouchUi.K;
            Gx.BeginGroup(view);
            Vector2 o = new Vector2(-camX * s, -camY * s);
            Rect mapR = new Rect(o.x, o.y, 380 * s, 380 * s);
            Gx.DrawTexture(mapR, TouchUi.Img("/m/m"));

            int nodes = C_PortMap.nodeCount();
            // locked cities
            for (int n = 0; n < nodes; n++)
            {
                if (C_PortMap.nodeType(n) == 0 && !C_PortMap.nodeOpen(n))
                {
                    Vector2 c = o + new Vector2(C_PortMap.nodeX(n), C_PortMap.nodeY(n)) * s;
                    // a lock 1.5x the game's on the town's house, no dark disc under it, moved where it
                    // covers none of the town names printed in the map picture (LockAt, worked out from the picture)
                    float ls = 18 * s;
                    Vector2 at = n < LockAt.Length / 2 ? new Vector2(LockAt[2 * n], LockAt[2 * n + 1]) : new Vector2(-8, -9);
                    c += at * s;
                    Gx.DrawTexture(new Rect(Mathf.Round(c.x - ls / 2), Mathf.Round(c.y - ls / 2), ls, ls), TouchUi.Img("/m/lock"));
                }
            }
            // open roads: gold dots
            Color gold = TouchUi.Hex(0xE6BE5A);
            for (int r = 0; r < C_PortMap.roadCount(); r++)
            {
                if (C_PortMap.roadOpen(r))
                {
                    Dots(o, C_PortMap.roadA(r), C_PortMap.roadB(r), 6.5f * k, 2 * k, gold);
                }
            }
            // the way about to be walked (or being walked)
            int target = C_PortMap.walking() ? C_PortMap.walkTarget() : selected;
            if (target >= 0 && target != C_PortMap.playerNode())
            {
                if (routeFor != target * 100 + C_PortMap.playerNode())
                {
                    routeFor = target * 100 + C_PortMap.playerNode();
                    int roads = C_PortMap.route(target, route);
                    routeLen = roads < 0 ? 0 : Mathf.Min(route.Length, roads + 1);
                }
                for (int i = 0; i + 1 < routeLen; i++)
                {
                    Dots(o, route[i], route[i + 1], 5 * k, 3 * k, Color.white);
                }
            }
            // marks: red quest, orange event, yellow guards
            Texture2D mt = TouchUi.Img("/m/mt1");
            int frame = (int) (Time.unscaledTime * 6) % 4;
            float bob = frame == 1 || frame == 3 ? -1 : (frame == 2 ? -2 : 0);
            for (int n = 0; n < nodes; n++)
            {
                int c = C_PortMap.nodeContent(n);
                if (c >= 1 && c <= 3)
                {
                    Vector2 p = o + new Vector2(C_PortMap.nodeX(n) + 5, C_PortMap.nodeY(n) - 26 + bob) * s;
                    TouchUi.Sprite(mt, new Rect(p.x, p.y, 7 * s, 24 * s), new Rect((c - 1) * 7, 0, 7, 24));
                }
            }
            // selection ring
            if (selected >= 0)
            {
                Vector2 c = o + new Vector2(C_PortMap.nodeX(selected), C_PortMap.nodeY(selected)) * s;
                Ring(c, 16 * k, 12.5f * k, 1.5f * k, TouchUi.Hex(0xFFA000));
            }
            // hero
            Texture2D hm = TouchUi.Img("/m/hm");
            Vector2 h = o + new Vector2(C_PortMap.playerX(), C_PortMap.playerY()) * s;
            // twice the game's size; the feet where the small one's were, so it stands over the town
            float hw = 26 * s, hh = 34 * s;
            Gx.DrawTexture(new Rect(Mathf.Round(h.x - hw / 2), Mathf.Round(h.y + 8.5f * s - hh), hw, hh), hm);
            Gx.EndGroup();
        }

        void Dots(Vector2 o, int a, int b, float step, float size, Color c)
        {
            float s = S;
            Vector2 pa = o + new Vector2(C_PortMap.nodeX(a), C_PortMap.nodeY(a)) * s;
            Vector2 pb = o + new Vector2(C_PortMap.nodeX(b), C_PortMap.nodeY(b)) * s;
            float len = (pb - pa).magnitude;
            int n = Mathf.Max(1, Mathf.FloorToInt(len / step));
            for (int i = 1; i < n; i++)
            {
                Vector2 p = Vector2.Lerp(pa, pb, i / (float) n);
                TouchUi.Fill(new Rect(Mathf.Round(p.x - size / 2), Mathf.Round(p.y - size / 2), size, size), c);
            }
        }

        static void Oval(Vector2 c, float rx, float ry, Color col)
        {
            float k = TouchUi.K;
            for (float y = -ry; y < ry; y += k)
            {
                float w = rx * Mathf.Sqrt(Mathf.Max(0, 1 - (y + k / 2) * (y + k / 2) / (ry * ry)));
                TouchUi.Fill(new Rect(Mathf.Round(c.x - w), Mathf.Round(c.y + y), Mathf.Round(2 * w), k), col);
            }
        }

        static void Ring(Vector2 c, float rx, float ry, float t, Color col)
        {
            int steps = 48;
            for (int i = 0; i < steps; i++)
            {
                float a = i * Mathf.PI * 2 / steps;
                Vector2 p = c + new Vector2(Mathf.Cos(a) * rx, Mathf.Sin(a) * ry);
                TouchUi.Fill(new Rect(Mathf.Round(p.x - t / 2), Mathf.Round(p.y - t / 2), t * 1.4f, t * 1.4f), col);
            }
        }

        void DrawTracker(Rect r, int q, int total)
        {
            float k = TouchUi.K;
            Kit.Box(r, new Color(0.13f, 0.06f, 0.12f, 0.92f));
            TouchUi.Sprite(TouchUi.Img("/m/mt1"), new Rect(r.x + 3 * k, r.y + 2 * k, 3.5f * k, 12 * k), new Rect(0, 0, 7, 24));
            string t = QuestData.Title(q);
            int enemy = C_PortMap.questEnemy(q);
            if (C_PortMap.questType(q) != 0 && enemy >= 0)
            {
                t += " · đánh " + QuestData.Who(enemy);
            }
            TouchUi.Label(new Rect(r.x + 10 * k, r.y, 128 * k, r.height), Kit.Cut(t, 34), 6.25f, TextAnchor.MiddleLeft, TouchUi.Hex(0xFFE39A), true);
            TouchUi.Label(new Rect(r.xMax - 36 * k, r.y, 28 * k, r.height), (q + 1) + "/" + total, 6, TextAnchor.MiddleRight, Kit.Cream, true);
            Kit.Chevron(r.xMax - 2 * k, r.center.y);
        }

        void DrawQuestArrow(Rect view, int q)
        {
            float k = TouchUi.K;
            int n = C_PortMap.questNode(q);
            Vector2 p = ToScreen(C_PortMap.nodeX(n), C_PortMap.nodeY(n));
            Rect inner = new Rect(view.x + 12 * k, view.y + 30 * k, view.width - 24 * k, view.height - 50 * k);
            if (inner.Contains(p) || n == C_PortMap.playerNode())
            {
                return;
            }
            Vector2 c = inner.center, d = p - c;
            float tx = d.x == 0 ? 1e9f : (d.x > 0 ? inner.xMax - c.x : inner.x - c.x) / d.x;
            float ty = d.y == 0 ? 1e9f : (d.y > 0 ? inner.yMax - c.y : inner.y - c.y) / d.y;
            Vector2 at = c + d * Mathf.Min(tx, ty);
            Vector2 dir = d.normalized;
            // triangle pointing along dir, white outline
            for (int i = 0; i < 9; i++)
            {
                float w = (9 - i) * k;
                Vector2 m = at - dir * (9 - i) * k;
                Vector2 side = new Vector2(-dir.y, dir.x);
                for (float j = -w / 2; j <= w / 2; j += k)
                {
                    Vector2 q2 = m + side * j;
                    TouchUi.Fill(new Rect(q2.x - k / 2, q2.y - k / 2, k * 1.3f, k * 1.3f), (Mathf.Abs(j) > w / 2 - k || i == 0) ? Color.white : TouchUi.Hex(0xE02424));
                }
            }
            int roads = C_PortMap.roadsTo(n);
            string label = QuestData.Place(n) + (roads > 0 ? " · " + roads + " đường" : "");
            float lw = TouchUi.TextWidth(label) + 8;
            float lx = Mathf.Clamp(at.x - dir.x * 14 * k - lw * k / 2, view.x + 2 * k, view.xMax - lw * k - 2 * k);
            float ly = Mathf.Clamp(at.y - dir.y * 14 * k + 8 * k, view.y + 20 * k, view.yMax - 14 * k);
            Rect lr = new Rect(lx, ly, lw * k, 11 * k);
            TouchUi.Fill(lr, new Color(0.08f, 0.03f, 0.06f, 0.85f));
            TouchUi.Label(lr, label, 5.75f, TextAnchor.MiddleCenter, Color.white, true);
        }

        // ------------------------------------------------------------------------- info panel

        void Strip()
        {
            Rect r = StripRect();
            float k = TouchUi.K;
            int t = C_PortMap.walkTarget();
            Rect stop = new Rect(r.xMax - 34 * k, r.y + 2 * k, 31 * k, r.height - 4 * k);
            if (Kit.Tap(110, stop))
            {
                C_PortMap.request(C_PortMap.f_STOP, 0);
            }
            if (Kit.Paint)
            {
                Kit.Box(r, new Color(0.18f, 0.08f, 0.17f, 0.95f));
                string place = t >= 0 ? QuestData.Place(t) : "";
                TouchUi.Label(new Rect(r.x + 6 * k, r.y, r.width - 44 * k, r.height), "Đang đi tới " + place + "…", 6.25f, TextAnchor.MiddleLeft, Kit.Cream, true);
                Kit.FlatButton(stop, "Dừng", Btn.Grey, Kit.Down(110));
            }
        }




        // ------------------------------------------------------------------------- tab bar

        static readonly string[] TabNames = {"Bản đồ", "Túi đồ", "Cửa hàng", "Nhiệm vụ"};

        void NavBar()
        {
            float k = TouchUi.K;
            float y = Kit.H - NavH;
            if (Kit.Paint)
            {
                Rect bar = TouchUi.R(0, y, 180, NavH);
                TouchUi.Fill(new Rect(0, bar.y, Gx.Width, Gx.Height - bar.y), TouchUi.Hex(0x1E1020));
                TouchUi.Fill(new Rect(0, bar.y, Gx.Width, k), TouchUi.Hex(0x4A2049));
            }
            for (int i = 0; i < 4; i++)
            {
                Rect r = TouchUi.R(i * 45, y, 45, NavH);
                if (Kit.Tap(9100 + i, r) && tutorial < 0) // ids apart from the tabs' own (quest rows use 200+)
                {
                    if (i == tab && i != TabMap)
                    {
                        tabs[i].Back();
                    }
                    OpenTab(i);
                }
                if (!Kit.Paint)
                {
                    continue;
                }
                bool on = i == tab;
                if (on)
                {
                    TouchUi.Fill(r, TouchUi.Hex(0x6E3470));
                    TouchUi.Fill(new Rect(r.x, r.y, r.width, 2 * k), Kit.Gold);
                }
                switch (i)
                {
                    case 0:
                        // the game's two crossed swords (m/sw without its frame), in a gold frame
                        Rect sw = new Rect(r.center.x - 9 * k, r.y + 5 * k, 18 * k, 15 * k);
                        TouchUi.Fill(new Rect(sw.x - 2 * k, sw.y - 2 * k, sw.width + 4 * k, sw.height + 4 * k), TouchUi.Outline);
                        TouchUi.Fill(new Rect(sw.x - k, sw.y - k, sw.width + 2 * k, sw.height + 2 * k), Kit.Gold);
                        TouchUi.Sprite(TouchUi.Img("/m/sw"), sw, new Rect(5, 2, 29, 25));
                        break;
                    case 1:
                        // the hero (the map's old icon)
                        TouchUi.Sprite(TouchUi.Img("/m/hm"), new Rect(r.center.x - 6.5f * k, r.y + 4 * k, 13 * k, 17 * k));
                        break;
                    case 2:
                        TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(r.center.x - 9 * k, r.y + 3 * k, 18 * k, 18 * k));
                        break;
                    case 3:
                        TouchUi.Sprite(TouchUi.Img("/book"), new Rect(r.center.x - 9 * k, r.y + 3 * k, 18 * k, 18 * k));
                        break;
                }
                TouchUi.Label(new Rect(r.x, r.y + 23 * k, r.width, 9 * k), TabNames[i], 6, TextAnchor.UpperCenter, on ? Kit.Gold : Kit.Cream, on);
                int badge = Badge(i);
                if (badge >= 0)
                {
                    Kit.Badge(r.center.x + 9 * k, r.y + 5 * k, badge);
                }
            }
        }

        /// <summary>-1 no dot, 0 a dot, n a number.</summary>
        int Badge(int i)
        {
            if (i == TabQuest)
            {
                int n = ((TouchQuestTab) tabs[TabQuest]).NewCount();
                return n > 0 ? n : -1;
            }
            if (i == TabShop)
            {
                return ((TouchShopTab) tabs[TabShop]).FreeReady() ? 0 : -1;
            }
            return -1;
        }

        // ------------------------------------------------------------------------- tutorial

        string TutorialKey
        {
            get { return "l12.mapTutorial.s" + C_PortScreens.f_slot; }
        }

        bool TutorialSeen()
        {
            return PlayerPrefs.GetInt(TutorialKey, 0) == 1;
        }

        void StartTutorial()
        {
            tab = TabMap;
            tutorial = 0;
            int q = C_PortMap.questIndex();
            if (q < C_PortMap.questCount())
            {
                CenterOn(C_PortMap.nodeX(C_PortMap.questNode(q)), C_PortMap.nodeY(C_PortMap.questNode(q)), false);
                lastTouch = Time.unscaledTime + 30f;
            }
            PlayerPrefs.SetInt(TutorialKey, 1);
            PlayerPrefs.Save();
        }

        static readonly string[] TutorialText =
        {
            "Dấu đỏ là nhiệm vụ chính. Chạm vào một nơi, tướng sẽ tự đi tới theo đường.",
            "Dấu cam là sự kiện phụ, dấu vàng là quân canh. Xem hết ở thẻ Nhiệm vụ."
        };

        void Tutorial()
        {
            float k = TouchUi.K;
            int q = C_PortMap.questIndex();
            int node = q < C_PortMap.questCount() ? C_PortMap.questNode(q) : C_PortMap.playerNode();
            Rect focus;
            if (tutorial == 0)
            {
                Vector2 p = ToScreen(C_PortMap.nodeX(node), C_PortMap.nodeY(node));
                focus = new Rect(p.x - 16 * k, p.y - 16 * k, 32 * k, 32 * k);
            }
            else
            {
                focus = TouchUi.R(135, Kit.H - NavH, 45, NavH);
            }
            Kit.Modal(new Rect(0, 0, Gx.Width, Gx.Height));
            if (Kit.Tap(300, new Rect(0, 0, Gx.Width, Gx.Height)) || Kit.Back())
            {
                tutorial++;
                if (tutorial > 1)
                {
                    tutorial = -1;
                    selected = -1;
                    lastTouch = -10f;
                }
            }
            if (Kit.Paint && tutorial >= 0)
            {
                // dim everything but the focus
                Color dim = new Color(0.02f, 0.01f, 0.03f, 0.62f);
                TouchUi.Fill(new Rect(0, 0, Gx.Width, focus.y), dim);
                TouchUi.Fill(new Rect(0, focus.yMax, Gx.Width, Gx.Height - focus.yMax), dim);
                TouchUi.Fill(new Rect(0, focus.y, focus.x, focus.height), dim);
                TouchUi.Fill(new Rect(focus.xMax, focus.y, Gx.Width - focus.xMax, focus.height), dim);
                float pulse = (Mathf.Sin(Time.unscaledTime * 6) + 1) * k;
                Rect fr = new Rect(focus.x - pulse, focus.y - pulse, focus.width + 2 * pulse, focus.height + 2 * pulse);
                TouchUi.Fill(new Rect(fr.x, fr.y, fr.width, k), Kit.Gold);
                TouchUi.Fill(new Rect(fr.x, fr.yMax - k, fr.width, k), Kit.Gold);
                TouchUi.Fill(new Rect(fr.x, fr.y, k, fr.height), Kit.Gold);
                TouchUi.Fill(new Rect(fr.xMax - k, fr.y, k, fr.height), Kit.Gold);
                string text = TutorialText[tutorial];
                int lines = TouchUi.Lines(text, 140);
                float h = lines * UiFont.LineHeight + 22;
                float fy = TouchUi.V(focus.position).y, fb = TouchUi.V(new Vector2(0, focus.yMax)).y;
                float by = fy > Kit.H / 2 ? fy - h - 10 : fb + 10;
                Rect box = TouchUi.R(14, by, 152, h);
                TouchUi.Panel(box);
                TouchUi.Label(TouchUi.R(20, by + 6, 140, lines * UiFont.LineHeight), text, 6.5f, TextAnchor.UpperCenter, Color.white, true, true);
                TouchUi.Label(TouchUi.R(20, by + h - 12, 140, 9), (tutorial + 1) + "/2 · chạm để tiếp", 5.5f, TextAnchor.UpperCenter, Kit.Muted);
            }
            Kit.Unblock();
        }
    }
}
