// Screen 1 of the touch redesign: the main menu (plan/thiet-ke-cai-dat.md 1, thiet-ke-nhieu-nhan-vat.md 4.1-4.2).
//
//   the game's menu background with its title animation (falls in, shakes, bounces)
//   CHƠI TIẾP (portrait, name · element · level of the character in play) + NHÂN VẬT n/3
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
        bool askExit;
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
                    TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
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

        // buttons from the bottom up: THOÁT GAME, CÀI ĐẶT, NHÂN VẬT (when there are characters), the main one
        float Bottom
        {
            get { return Kit.H - 18; }
        }

        Rect ExitRect()
        {
            return TouchUi.R(37.5f, Bottom - 22, 105, 22);
        }

        Rect SettingsRect()
        {
            return TouchUi.R(37.5f, Bottom - 22 - 7 - 22, 105, 22);
        }

        Rect SlotsRect()
        {
            return TouchUi.R(27.5f, Bottom - 51 - 8 - 25, 125, 25);
        }

        Rect ContinueRect(bool slots)
        {
            float top = slots ? Bottom - 84 - 9 - 37 : Bottom - 51 - 10 - 37;
            return TouchUi.R(10, top, 160, 37);
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
                    TouchUi.PixelText("NHÂN VẬT", 1, sr.x + 52 * k, sr.center.y + dy, 1, true);
                    Rect pill = new Rect(sr.xMax - 38 * k, sr.y + 6 * k + dy, 26 * k, 13 * k);
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
        /// The game's menu background (/bkmenu) over the whole screen, and its title animation: the title
        /// falls in from the top, the screen shakes as it lands, then it bounces a little (suquan.c).
        /// </summary>
        void DrawBackground()
        {
            TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Hex(0x0E0303));
            Texture2D bg = TouchUi.Img("/bkmenu");
            float sc = Mathf.Max(Screen.width / 240f, Screen.height / 320f);
            float t = Time.unscaledTime - shownAt;
            const float fall = 0.5f;
            // shake for 0.4 s after the landing (the game: cz.a(10), 10 ticks)
            Vector2 shake = Vector2.zero;
            if (t > fall && t < fall + 0.4f)
            {
                float a = TouchUi.K * 2;
                shake = new Vector2(Mathf.Round(Random.Range(-a, a)), Mathf.Round(Random.Range(-a, a)));
            }
            Rect r = new Rect((Screen.width - 240 * sc) / 2 + shake.x, (Screen.height - 320 * sc) / 2 + shake.y, 240 * sc, 320 * sc);
            GUI.DrawTexture(r, bg);
            // a darker band behind the buttons
            float bandTop = ContinueRect(Slots.Data.activeSlot >= 0).y - 10 * TouchUi.K;
            Color dark = new Color(0.05f, 0.01f, 0.01f, 0.8f);
            TouchUi.Shade(new Rect(0, bandTop - 30 * TouchUi.K, Screen.width, 30 * TouchUi.K), dark, false);
            TouchUi.Fill(new Rect(0, bandTop, Screen.width, Screen.height - bandTop), dark);

            Texture2D title = TouchUi.Img("/title");
            float tw = title.width * sc * 1.25f, th = title.height * sc * 1.25f;
            float landY = r.y + 110 * sc; // bottom of the title, like the game
            float y;
            if (t < fall)
            {
                float p = t / fall;
                y = Mathf.Lerp(-th, landY, p * p);
            }
            else
            {
                int[] bounce = {-2, -6, 0, -3, 0, -2, 0, -1};
                int i = (int) ((t - fall) / 0.04f);
                y = landY + (i < bounce.Length ? bounce[i] * sc : 0);
            }
            GUI.DrawTexture(new Rect(Mathf.Round(r.center.x - tw / 2), Mathf.Round(y - th), tw, th), title);
        }

        static void DrawContinue(Rect r, SlotInfo s, bool down, bool busy)
        {
            float k = TouchUi.K, dy = down ? k : 0;
            Color old = GUI.color;
            if (busy)
            {
                GUI.color = new Color(0.6f, 0.56f, 0.6f, 1f);
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
            GUI.color = old;
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
                TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Hex(0x0E0303));
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
