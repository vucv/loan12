// Screen 1 of the touch redesign: the main menu (plan/thiet-ke-cai-dat.md 1, thiet-ke-nhieu-nhan-vat.md 4.1-4.2).
//
//   the cover (/bksplashscreen) above the buttons, animated: fades in, breathes, a glint, rising embers
//   one column of buttons, same width and gap: CHƠI TIẾP (portrait, name · element · level of the character in play) + NHÂN VẬT n/3
//   or, before the first character: CHƠI MỚI and "Tạo nhân vật đầu tiên · miễn phí"
//   CÀI ĐẶT, THOÁT GAME
//
// Sub pages opened from the menu (settings, characters, shop ...) are drawn by this view too: the game
// stays on its menu screen underneath. Back closes a sub page; on the menu it asks before quitting.
using UnityEngine;

namespace Loan12.UnityPort
{
    public interface IPage
    {
        /// <summary>Draws the page and handles its taps; false when it wants to close.</summary>
        bool OnGUI(Event e);
    }

    public sealed class TouchMenuView : ITouchView, IOwnsKeys
    {
        IPage page;
        public bool askExit;
        bool refreshed;
        float shownAt;

        public static TouchMenuView Instance { get; private set; }

        public TouchMenuView()
        {
            Instance = this;
        }

        public bool Active()
        {
            try
            {
                Rewards.Tick();
                Slots.Watch();
                return C_PortScreens.screenKind() == 2 && !C_PortScreens.hasDialog();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            askExit = false;
            refreshed = false;
            shownAt = Time.unscaledTime;
        }

        /// <summary>Opens a sub page over the menu.</summary>
        public void Open(IPage p)
        {
            page = p;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (!refreshed)
            {
                refreshed = true;
                Slots.Refresh();
            }
            Kit.Begin(e);
            if (page != null)
            {
                if (Kit.Paint)
                {
                    TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Background);
                }
                if (!page.OnGUI(e))
                {
                    page = null;
                }
                Rewards.DrawOverlay();
                Kit.End();
                return;
            }
            DrawMenu();
            if (askExit)
            {
                ExitBox();
            }
            else if (Kit.Back())
            {
                askExit = true;
                e.Use();
            }
            Kit.DrawToast(Kit.H - 40);
            Rewards.DrawOverlay();
            Kit.End();
        }

        // ---------------------------------------------------------------------------------- menu

        // one column, same width, same gap: the main one (37 high), NHÂN VẬT (when there are characters),
        // CÀI ĐẶT, THOÁT GAME, from the bottom up
        const float BtnX = 15, BtnW = 150, BtnH = 24, MainH = 37, Gap = 8;

        float Bottom
        {
            get { return Kit.H - 16; }
        }

        Rect ExitRect()
        {
            return TouchUi.R(BtnX, Bottom - BtnH, BtnW, BtnH);
        }

        Rect SettingsRect()
        {
            return TouchUi.R(BtnX, Bottom - 2 * BtnH - Gap, BtnW, BtnH);
        }

        Rect SlotsRect()
        {
            return TouchUi.R(BtnX, Bottom - 3 * BtnH - 2 * Gap, BtnW, BtnH);
        }

        Rect ContinueRect(bool slots)
        {
            int below = slots ? 3 : 2;
            return TouchUi.R(BtnX, Bottom - below * (BtnH + Gap) - MainH, BtnW, MainH);
        }

        void DrawMenu()
        {
            bool busy = SafeBusy();
            SlotData d = Slots.Data;
            bool any = d.activeSlot >= 0;
            if (Kit.Paint)
            {
                DrawBackground();
            }

            Rect main = ContinueRect(any);
            if (any)
            {
                SlotInfo s = d.slots[d.activeSlot];
                if (Kit.Tap(1, main) && !busy)
                {
                    Slots.Play(d.activeSlot);
                }
                if (Kit.Tap(2, SlotsRect()) && !busy)
                {
                    Open(new TouchSlotsPage());
                }
                if (Kit.Paint)
                {
                    DrawContinue(main, s, Kit.Down(1), busy);
                    Rect sr = SlotsRect();
                    Kit.FramedButton(sr, TouchUi.Hex(0x46245C), Kit.Down(2));
                    float k = TouchUi.K, dy = Kit.Down(2) ? k : 0;
                    TouchUi.PixelText("NHÂN VẬT", 1, sr.center.x, sr.center.y + dy, 1, true);
                    Rect pill = new Rect(sr.xMax - 32 * k, sr.y + 5.5f * k + dy, 26 * k, 13 * k);
                    TouchUi.Fill(pill, TouchUi.GoldLine);
                    TouchUi.Fill(new Rect(pill.x + k, pill.y + k, pill.width - 2 * k, pill.height - 2 * k), TouchUi.Hex(0x2A1236));
                    TouchUi.Label(pill, Slots.Used + "/3", 7, TextAnchor.MiddleCenter, Kit.Cream, true);
                }
            }
            else
            {
                if (Kit.Tap(3, main) && !busy)
                {
                    NewCharacter();
                }
                if (Kit.Paint)
                {
                    string line = Slots.CanCreate ? (d.everCreated == 0 ? "Tạo nhân vật đầu tiên · miễn phí" : "Tạo nhân vật mới")
                        : "Xem 1 quảng cáo để tạo nhân vật";
                    Kit.MainButton(main, "CHƠI MỚI", line, !busy, Kit.Down(3));
                }
            }

            if (Kit.Tap(4, SettingsRect()) && !busy)
            {
                Open(new TouchSettingsPage(TouchSettingsPage.Where.Menu));
            }
            if (Kit.Tap(5, ExitRect()) && !busy)
            {
                askExit = true;
            }
            if (Kit.Paint)
            {
                Rect sr = SettingsRect(), er = ExitRect();
                Kit.FramedButton(sr, TouchUi.Hex(0x2A1426), Kit.Down(4));
                TouchUi.PixelText("CÀI ĐẶT", 1, sr.center.x, sr.center.y + (Kit.Down(4) ? TouchUi.K : 0), 1, true);
                Kit.FramedButton(er, TouchUi.Hex(0x2A1426), Kit.Down(5));
                TouchUi.PixelText("THOÁT GAME", 1, er.center.x, er.center.y + (Kit.Down(5) ? TouchUi.K : 0), 1, true);
            }
        }

        /// <summary>
        /// The cover (/bksplashscreen) above the buttons, as before, now animated: it fades and settles in,
        /// then breathes slowly (a light zoom and drift), a glint crosses it now and then, and embers rise
        /// from the bottom.
        /// </summary>
        void DrawBackground()
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0E0303));
            float t = Time.unscaledTime - shownAt;
            Texture2D cover = TouchUi.Img("/bksplashscreen");
            float top = 22, room = TouchUi.V(ContinueRect(Slots.Data.activeSlot >= 0).position).y - 10 - top;
            float h = Mathf.Min(250, room), w = h * 0.75f;
            Rect frame = TouchUi.R(90 - w / 2, top, w, h);

            float intro = Mathf.Clamp01(t / 0.8f), ease = 1 - (1 - intro) * (1 - intro);
            float zoom = 1.08f - 0.06f * ease + 0.025f * Mathf.Sin(t * 0.7f);
            Vector2 drift = new Vector2(Mathf.Sin(t * 0.33f), Mathf.Cos(t * 0.27f)) * 2.5f * k;
            Rect r = new Rect(frame.center.x - frame.width * zoom / 2 + drift.x, frame.center.y - frame.height * zoom / 2 + drift.y + (1 - ease) * 10 * k,
                frame.width * zoom, frame.height * zoom);
            Gx.BeginGroup(frame);
            Color old = Gx.color;
            Gx.color = new Color(old.r, old.g, old.b, ease);
            Gx.DrawTexture(new Rect(r.x - frame.x, r.y - frame.y, r.width, r.height), cover);
            // a glint every 5 s
            float g = (t % 5f) / 1.1f;
            if (g < 1f)
            {
                float gx = Mathf.Lerp(-0.3f, 1.3f, g) * frame.width;
                for (int i = 0; i < 6; i++)
                {
                    Gx.color = new Color(1f, 0.92f, 0.7f, 0.05f * ease * (6 - Mathf.Abs(i - 2.5f) * 2) / 6);
                    Gx.DrawTexture(new Rect(gx + i * 4 * k, 0, 4 * k, frame.height), Texture2D.whiteTexture);
                }
            }
            Gx.color = old;
            Gx.EndGroup();
            float fade = 30 * k;
            TouchUi.Shade(new Rect(frame.x, frame.yMax - fade, frame.width, fade + k), TouchUi.Hex(0x0E0303), false);
            TouchUi.Shade(new Rect(frame.x, frame.y, frame.width, 10 * k), TouchUi.Hex(0x0E0303), true);

            // embers: fixed seeds, each rises and fades over its own period
            for (int i = 0; i < 18; i++)
            {
                float seed = i * 0.6180339f % 1f, period = 3.5f + seed * 3f;
                float p = ((t + seed * period * 3.1f) % period) / period;
                float x = 8 + ((i * 37) % 164) + Mathf.Sin(t * (0.8f + seed) + i) * 6;
                float y = Kit.H * (0.95f - p * 0.85f);
                float a = Mathf.Sin(p * Mathf.PI) * 0.75f * ease;
                float size = (1 + (i % 3) * 0.5f) * k;
                Gx.color = new Color(1f, 0.55f + 0.3f * seed, 0.15f, a);
                Gx.DrawTexture(new Rect(TouchUi.R(x, 0, 0, 0).x, TouchUi.R(0, y, 0, 0).y, size * 1.5f, size * 1.5f), Texture2D.whiteTexture);
            }
            Gx.color = old;
        }

        static void DrawContinue(Rect r, SlotInfo s, bool down, bool busy)
        {
            float k = TouchUi.K, dy = down ? k : 0;
            Color old = Gx.color;
            if (busy)
            {
                Gx.color = new Color(0.6f, 0.56f, 0.6f, 1f);
            }
            TouchUi.Nine(TouchUi.Img("/m/ff"), 12, r);
            if (down)
            {
                TouchUi.Fill(new Rect(r.x + 4 * k, r.y + 4 * k, r.width - 8 * k, r.height - 8 * k), new Color(1f, 0.85f, 0.4f, 0.25f));
            }
            Rect pic = new Rect(r.x + 6 * k, r.y + 6 * k + dy, 25 * k, 25 * k);
            TouchUi.Portrait(s.hero, pic);
            TouchUi.PixelText("CHƠI TIẾP", 1, r.x + 37 * k, r.y + 14 * k + dy, 0, true);
            string sub = Kit.Cut(Kit.Title(s.name), 14) + " · Hệ " + Kit.ElementName(SafeElement(s.hero)) + " · Cấp " + s.level;
            TouchUi.Label(new Rect(r.x + 37 * k, r.y + 21 * k + dy, r.width - 42 * k, 9 * k), sub, 6.5f, TextAnchor.UpperLeft, Kit.Cream);
            Gx.color = old;
        }

        static int SafeElement(int hero)
        {
            try
            {
                return C_PortScreens.heroElement(hero);
            }
            catch (System.Exception)
            {
                return 0;
            }
        }

        static bool SafeBusy()
        {
            try
            {
                return C_PortScreens.busy() || Rewards.Busy;
            }
            catch (System.Exception)
            {
                return true;
            }
        }

        /// <summary>CHƠI MỚI / an empty slot: free credit, VIP, or the "Nhân vật mới" ad box first.</summary>
        public void NewCharacter()
        {
            int n = Slots.FirstEmpty();
            if (n < 0)
            {
                Kit.Toast("Đã đủ 3 nhân vật. Hãy xóa 1 nhân vật để tạo mới.");
                return;
            }
            if (Slots.CanCreate)
            {
                Slots.Create(n);
                return;
            }
            Open(new TouchSlotsPage(true));
        }

        /// <summary>The shop as a page of its own (from the + next to the gold). tab 0 items, 1 gold, 2 offers</summary>
        public void OpenShop(int tab)
        {
            Open(new ShopPage(tab));
        }

        void ExitBox()
        {
            float k = TouchUi.K;
            Rect box = TouchUi.R(14, Kit.H / 2 - 40, 152, 80);
            Kit.Modal(box);
            Rect yes = TouchUi.R(24, Kit.H / 2 + 10, 62, 22), no = TouchUi.R(94, Kit.H / 2 + 10, 62, 22);
            if (Kit.Tap(11, yes))
            {
                askExit = false;
                C_PortScreens.request(C_PortScreens.f_EXIT, 0);
            }
            if (Kit.Tap(12, no) || Kit.Outside())
            {
                askExit = false;
            }
            if (Kit.Back())
            {
                askExit = false;
                Kit.E.Use();
            }
            if (Kit.Paint)
            {
                TouchUi.Dim(0.65f);
                TouchUi.Panel(box);
                TouchUi.PixelText("THOÁT GAME?", 1, box.center.x, box.y + 16 * k, 1, true);
                TouchUi.Label(TouchUi.R(20, Kit.H / 2 - 20, 140, 20), "Tiến độ đã được lưu.", 6.5f, TextAnchor.MiddleCenter, Kit.Cream, false, true);
                Kit.FlatButton(yes, "Thoát", Btn.Red, Kit.Down(11));
                Kit.FlatButton(no, "Ở lại", Btn.Grey, Kit.Down(12));
            }
            Kit.Unblock();
        }
    }

    /// <summary>
    /// Start-up: the game's logos, its sound question and its cover wait are skipped (the volume is in the
    /// settings); shows the cover picture while the game loads.
    /// </summary>
    public sealed class TouchBootView : ITouchView, IOwnsKeys
    {
        float asked = -10f;

        public bool Active()
        {
            try
            {
                // the game's logo canvas (no game screen yet), the sound question, the cover before the menu
                if (C_PortUi.screen() == null)
                {
                    return true;
                }
                int k = C_PortScreens.screenKind();
                return (k == 1 || k == 8) && !C_PortScreens.hasDialog();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (C_PortScreens.screenKind() == 1 && !C_PortScreens.busy() && Time.unscaledTime - asked > 1f)
            {
                asked = Time.unscaledTime;
                C_PortScreens.request(C_PortScreens.f_SOUND_SKIP, 0);
            }
            Kit.Begin(e);
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0E0303));
                float h = Mathf.Min(240, Kit.H - 60), w = h * 0.75f;
                Rect r = TouchUi.R(90 - w / 2, 30, w, h);
                TouchUi.Sprite(TouchUi.Img("/bksplashscreen"), r);
                TouchUi.Shade(new Rect(r.x, r.yMax - 27 * TouchUi.K, r.width, 28 * TouchUi.K), TouchUi.Hex(0x0E0303), false);
                Kit.Spinner(TouchUi.R(90, Kit.H - 40, 0, 0).position);
            }
            Kit.End();
        }
    }
}
