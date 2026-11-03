// Lives in the touch interface (plan/README.md, monetisation): when a battle is started without lives the
// game's SMS purchase ("Bạn đã hết mạng chơi. Mua thêm?") is not offered; this box is, instead:
//   watch a rewarded ad for 1 life / use a Bình Hồi Mạng (lives back to the maximum) / buy one with gold.
// Bình Hồi Mạng is an item of the touch port, kept with the shared account (the game's bag has room for
// its own 6 items only): sold in the shop, shown in the bag, used outside battles.
using UnityEngine;

namespace Loan12.UnityPort
{
    /// <summary>
    /// +1 life every 30 minutes while below the maximum, counted on the clock (so it keeps going while the
    /// game is closed). The time of the next life is kept in PlayerPrefs; full lives stop the timer.
    /// </summary>
    public static class LifeTimer
    {
        public const int Period = 30 * 60;
        const string Pref = "l12.lives.next";
        static float checkedAt = -10f;

        static long Now
        {
            get { return System.DateTimeOffset.UtcNow.ToUnixTimeSeconds(); }
        }

        static long Next
        {
            get { return long.Parse(PlayerPrefs.GetString(Pref, "0")); }
            set
            {
                PlayerPrefs.SetString(Pref, value.ToString());
                PlayerPrefs.Save();
            }
        }

        /// <summary>Gives the lives that are due (called every frame, works once a second).</summary>
        public static void Tick()
        {
            if (Time.unscaledTime - checkedAt < 1f)
            {
                return;
            }
            checkedAt = Time.unscaledTime;
            try
            {
                int lives = C_PortScreens.lives(), max = C_PortScreens.maxLives();
                long next = Next, now = Now;
                if (lives >= max)
                {
                    if (next != 0)
                    {
                        Next = 0;
                    }
                    return;
                }
                if (next == 0 || next - now > Period)
                {
                    Next = now + Period;
                    return;
                }
                int add = 0;
                while (next <= now && lives + add < max)
                {
                    add++;
                    next += Period;
                }
                if (add > 0)
                {
                    C_PortScreens.addLives(add);
                    Next = lives + add >= max ? 0 : next;
                }
            }
            catch (System.Exception)
            {
                // the game's stores are not ready yet (boot)
            }
        }

        /// <summary>Seconds until the next life; -1 when lives are full.</summary>
        public static int Left
        {
            get
            {
                long next = Next;
                return next == 0 ? -1 : (int) System.Math.Max(0, next - Now);
            }
        }

        public static string Clock(int seconds)
        {
            return (seconds / 60).ToString("00") + ":" + (seconds % 60).ToString("00");
        }
    }

    public static class Refill
    {
        /// <summary>Item type used for it next to the game's 0..5 (MapUi).</summary>
        public const int Type = 100;
        public const int Price = 1000;
        const string Pref = "l12.item.refill";

        public static int Count
        {
            get { return PlayerPrefs.GetInt(Pref, 0); }
            set
            {
                PlayerPrefs.SetInt(Pref, Mathf.Max(0, value));
                PlayerPrefs.Save();
            }
        }

        public static bool Needed
        {
            get { return C_PortScreens.lives() < C_PortScreens.maxLives(); }
        }

        /// <summary>Uses one: lives back to the maximum. False when there is none or lives are full.</summary>
        public static bool Use()
        {
            if (Count <= 0 || !Needed)
            {
                return false;
            }
            Count = Count - 1;
            C_PortScreens.refillLives();
            Haptics.Tap(Kit.Haptic);
            return true;
        }

        /// <summary>Buys packs with gold (shared wallet); nothing when the gold is short.</summary>
        public static bool Buy(int packs)
        {
            int total = Price * packs;
            if (packs <= 0 || C_PortScreens.gold() < total)
            {
                return false;
            }
            C_PortScreens.addGold(-total);
            Count = Count + packs;
            return true;
        }

        /// <summary>The icon: the game's heart with a small green "+".</summary>
        public static void Icon(Rect r, float dim = 1f)
        {
            float k = TouchUi.K;
            Color old = GUI.color;
            GUI.color = old * new Color(dim, dim, dim, 1f);
            Rect h = new Rect(r.x + r.width * 0.08f, r.y + r.height * 0.08f, r.width * 0.84f, r.height * 0.84f);
            TouchUi.Sprite(TouchUi.Img("/m/ih"), h);
            float p = Mathf.Max(5 * k, Mathf.Round(r.width * 0.42f / k) * k);
            Rect b = new Rect(r.xMax - p, r.yMax - p, p, p);
            TouchUi.Fill(b, TouchUi.Outline);
            TouchUi.Fill(new Rect(b.x + k, b.y + k, b.width - 2 * k, b.height - 2 * k), TouchUi.Hex(0x2F9A3C));
            float t = Mathf.Max(k, Mathf.Round(p / k / 5) * k);
            TouchUi.Fill(new Rect(b.center.x - t / 2, b.y + 2 * k, t, b.height - 4 * k), Color.white);
            TouchUi.Fill(new Rect(b.x + 2 * k, b.center.y - t / 2, b.width - 4 * k, t), Color.white);
            GUI.color = old;
        }
    }

    /// <summary>
    /// "Hết mạng": over the map (preparing a battle, the arena) when the game was asked to fight without lives,
    /// or when the lives on the top bar are tapped.
    /// </summary>
    public sealed class TouchLivesView : ITouchView, IOwnsKeys
    {
        /// <summary>The lives on the map's top bar were tapped.</summary>
        public static bool Requested;
        string error;

        public bool Active()
        {
            try
            {
                return (C_PortMap.f_noLives || Requested) && C_PortScreens.screenKind() == 4 && !C_PortScreens.hasDialog();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            error = null;
        }

        static void Close()
        {
            C_PortMap.f_noLives = false;
            Requested = false;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            Rewards.Tick();
            float k = TouchUi.K;
            int lives = C_PortScreens.lives(), max = C_PortScreens.maxLives(), have = Refill.Count;
            bool none = lives <= 0;
            bool busy = Rewards.Busy;
            float h = 186;
            float y = Mathf.Round((Kit.H - h) / 2);
            Rect box = TouchUi.R(9, y, 162, h);
            Kit.Modal(box);
            Rect ad = TouchUi.R(19, y + 82, 142, 24), use = TouchUi.R(19, y + 112, 142, 24), later = TouchUi.R(55, y + h - 28, 70, 18);
            bool full = lives >= max;
            if (!busy && !full && Kit.Tap(1, ad))
            {
                error = null;
                if (Rewards.Vip)
                {
                    C_PortScreens.addLives(1);
                    Kit.Toast("+1 mạng chơi");
                    Close();
                }
                else
                {
                    Rewards.Show("life", res =>
                    {
                        if (res == 1)
                        {
                            C_PortScreens.addLives(1);
                            Kit.Toast("+1 mạng chơi");
                            Close();
                        }
                        else
                        {
                            error = res == 0 ? "Cần xem hết quảng cáo để nhận mạng" : "Chưa có quảng cáo, thử lại sau ít phút";
                        }
                    });
                }
            }
            if (!busy && !full && Kit.Tap(2, use))
            {
                if (have > 0)
                {
                    Refill.Use();
                    Kit.Toast("Đã hồi đầy " + max + " mạng");
                    Close();
                }
                else if (Refill.Buy(1))
                {
                    Refill.Use();
                    Kit.Toast("Đã mua và dùng Bình Hồi Mạng: đầy " + max + " mạng");
                    Close();
                }
                else
                {
                    error = "Thiếu " + TouchUi.Money(Refill.Price - C_PortScreens.gold()) + " vàng để mua Bình Hồi Mạng";
                }
            }
            if (!busy && (Kit.Tap(3, later) || Kit.Outside() || Kit.Back()))
            {
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
                Close();
            }
            if (Kit.Paint)
            {
                MapBackdrop.Draw(0.55f);
                TouchUi.Dim(0.4f);
                TouchUi.Panel(box);
                TouchUi.PixelText(none ? "HẾT MẠNG" : "MẠNG CHƠI", 1, box.center.x, TouchUi.R(0, y + 14, 0, 0).y, 1, true);
                // hearts
                for (int i = 0; i < max; i++)
                {
                    Color old = GUI.color;
                    GUI.color = i < lives ? old : old * new Color(0.25f, 0.22f, 0.27f, 1f);
                    TouchUi.Sprite(TouchUi.Img("/m/ih"), TouchUi.R(90 - max * 10 + i * 20 + 3, y + 26, 14, 14));
                    GUI.color = old;
                }
                string line = none ? "Bạn đã hết mạng chơi. Mỗi trận cần 1 mạng (thắng thì được trả lại)."
                    : (full ? "Mạng chơi đang đầy (" + lives + "/" + max + ")." : "Bạn còn " + lives + "/" + max + " mạng. Mỗi trận cần 1 mạng.");
                if (!full && LifeTimer.Left >= 0)
                {
                    line += " +1 mạng sau " + LifeTimer.Clock(LifeTimer.Left) + ".";
                }
                TouchUi.Label(TouchUi.R(18, y + 46, 144, 24), line, 6.25f, TextAnchor.UpperCenter, Kit.Cream, false, true);
                if (error != null)
                {
                    TouchUi.Label(TouchUi.R(18, y + 70, 144, 10), error, 5.5f, TextAnchor.UpperCenter, Kit.Red, true);
                }
                if (busy)
                {
                    Kit.FlatButton(ad, "", Btn.Green, false);
                    Kit.Spinner(ad.center);
                }
                else if (Rewards.Vip)
                {
                    Kit.FlatButton(ad, "+1 MẠNG (VIP)", Btn.Gold, Kit.Down(1), !full);
                }
                else
                {
                    Kit.AdButton(ad, "XEM QUẢNG CÁO · +1 MẠNG", Kit.Down(1), !full);
                }
                string useText = have > 0 ? "DÙNG BÌNH HỒI MẠNG (có " + have + ")" : "MUA BÌNH HỒI MẠNG · " + TouchUi.Money(Refill.Price) + " vàng";
                TouchUi.Button(use, full ? Btn.Grey : (have > 0 ? Btn.Red : Btn.Gold), Kit.Down(2) && !full);
                Refill.Icon(new Rect(use.x + 3 * k, use.y + 3 * k, 18 * k, 18 * k), full ? 0.5f : 1f);
                TouchUi.Label(new Rect(use.x + 22 * k, use.y, use.width - 24 * k, use.height), useText, 6.25f, TextAnchor.MiddleCenter, Color.white, true);
                TouchUi.Label(TouchUi.R(18, y + 139, 144, 10), "Bình Hồi Mạng: hồi đầy " + max + " mạng", 5.5f, TextAnchor.UpperCenter, Kit.Muted);
                Kit.FlatButton(later, "Để sau", Btn.Grey, Kit.Down(3));
            }
            Kit.Unblock();
            Kit.DrawToast(y - 14);
            Rewards.DrawOverlay();
            Kit.End();
        }
    }
}
