// Screen 3 of the touch redesign: choosing the hero (plan/thiet-ke-giao-dien.md 3,
// thiet-ke-nhieu-nhan-vat.md 4.5). Drawn over the game's hero screen (suquan.z), which keeps the choice.
//
//   big card of the highlighted hero: element, HP and attack at level 1, the game's description,
//   its 3 skills (icon + name)
//   grid of the 6 heroes ("Ô x" tag on heroes another slot already has)
//   "Bạn đã có tướng này ở Ô x (vẫn tạo được)" / CHỌN TƯỚNG NÀY (then the game asks for the name)
// Back asks "Hủy tạo nhân vật?"; yes returns to the menu without using the creation credit.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchHeroView : ITouchView, IOwnsKeys
    {
        bool askCancel;

        static readonly string[] SkillIcons =
        {
            "/fireballicon", "/meteoricon", "/hellfireicon", "/chainlightingicon", "/shieldlightingicon",
            "/blastlightingicon", "/icebolticon", "/healingicon", "/frozenicon"
        };

        public static string SkillIcon(int id)
        {
            return id >= 0 && id < SkillIcons.Length ? SkillIcons[id] : SkillIcons[0];
        }

        public bool Active()
        {
            try
            {
                return C_PortScreens.screenKind() == 3 && !C_PortScreens.hasDialog();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            askCancel = false;
        }

        /// <summary>Slot (1-based) of another character with this hero, or 0.</summary>
        static int OwnedIn(int hero)
        {
            SlotData d = Slots.Data;
            for (int i = 0; i < Slots.Count; i++)
            {
                if (d.slots[i].used && d.slots[i].hero == hero && i != Slots.CreatingSlot)
                {
                    return i + 1;
                }
            }
            return 0;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            bool busy = C_PortScreens.busy();
            int sel = C_PortScreens.heroSelected();
            float H = Kit.H;
            float button = H - 41, band = button - 15;
            float cell = 44, gridTop = band - 2 * cell - 6;
            float cardTop = 26, cardH = gridTop - 6 - cardTop;

            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
                TouchUi.Wallpaper(0.6f);
            }
            if (askCancel)
            {
                Kit.Modal(new Rect(0, 0, 0, 0));
            }
            bool creating = Slots.Creating && Slots.Used > 0;
            if (Kit.TitleBar(creating ? "TẠO NHÂN VẬT" : "CHỌN TƯỚNG") && !busy)
            {
                askCancel = true;
            }
            if (Kit.Paint && Slots.Creating)
            {
                Rect pill = TouchUi.R(150, 7, 26, 12);
                TouchUi.Fill(pill, TouchUi.GoldLine);
                TouchUi.Fill(new Rect(pill.x + TouchUi.K, pill.y + TouchUi.K, pill.width - 2 * TouchUi.K, pill.height - 2 * TouchUi.K), TouchUi.Hex(0x2A1236));
                TouchUi.Label(pill, "Ô " + (Slots.CreatingSlot + 1), 6.5f, TextAnchor.MiddleCenter, Kit.Cream, true);
            }

            // the card
            if (Kit.Paint)
            {
                DrawCard(TouchUi.R(6, cardTop, 168, cardH), sel);
            }

            // the grid
            for (int i = 0; i < 6; i++)
            {
                float cx = 15 + (i % 3) * 54, cy = gridTop + (i / 3) * cell;
                Rect hit = TouchUi.R(cx - 3, cy - 2, 40, cell);
                if (Kit.Tap(10 + i, hit) && !busy)
                {
                    C_PortScreens.heroSelect(i);
                }
                if (Kit.Paint)
                {
                    float k = TouchUi.K;
                    if (i == sel)
                    {
                        TouchUi.Fill(TouchUi.R(cx - 4, cy - 3, 42, 42), TouchUi.Hex(0x8BE05C));
                    }
                    TouchUi.Portrait(i, TouchUi.R(cx, cy, 34, 34));
                    TouchUi.Label(TouchUi.R(cx - 6, cy + 33, 46, 9), Kit.ElementName(C_PortScreens.heroElement(i)), 6.5f, TextAnchor.UpperCenter,
                        i == sel ? Color.white : Kit.Cream, true);
                    int owned = OwnedIn(i);
                    if (owned > 0)
                    {
                        Rect tag = TouchUi.R(cx + 20, cy - 4, 17, 10);
                        TouchUi.Fill(tag, Kit.Gold);
                        TouchUi.Fill(new Rect(tag.x + k, tag.y + k, tag.width - 2 * k, tag.height - 2 * k), TouchUi.Hex(0x1A0C16));
                        TouchUi.Label(tag, "Ô " + owned, 5.5f, TextAnchor.MiddleCenter, Kit.Gold, true, false, false);
                    }
                }
            }

            int dup = OwnedIn(sel);
            if (Kit.Paint && dup > 0)
            {
                Rect b = TouchUi.R(8, band, 164, 12);
                Kit.Box(b, TouchUi.Hex(0x2A1A0A));
                TouchUi.Label(b, "Bạn đã có tướng này ở Ô " + dup + " (vẫn tạo được)", 5.75f, TextAnchor.MiddleCenter, Kit.Cream);
            }

            Rect ok = TouchUi.R(10, button, 160, 35);
            if (Kit.Tap(20, ok) && !busy)
            {
                C_PortScreens.request(C_PortScreens.f_HERO_OK, 0);
            }
            if (Kit.Paint)
            {
                TouchUi.RedButton(ok, "CHỌN TƯỚNG NÀY", !busy, Kit.Down(20));
            }
            Kit.Unblock();

            if (askCancel)
            {
                CancelBox();
            }
            else if (Kit.Back())
            {
                askCancel = true;
                e.Use();
            }
            Kit.End();
        }

        static void DrawCard(Rect r, int h)
        {
            float k = TouchUi.K;
            TouchUi.Panel(r);
            float top = TouchUi.V(r.position).y;
            float pic = r.height / k >= 170 ? 60 : 44;
            TouchUi.Portrait(h, TouchUi.R(14, top + 10, pic, pic));
            float tx = 14 + pic + 8;
            int el = C_PortScreens.heroElement(h);
            TouchUi.PixelText("HỆ " + Kit.ElementName(el).ToUpperInvariant(), 1, TouchUi.R(tx, 0, 0, 0).x, TouchUi.R(0, top + 18, 0, 0).y, 0, true);
            TouchUi.Label(TouchUi.R(tx, top + 28, 100, 9), Kit.Title(C_PortScreens.heroName(h)), 6.75f, TextAnchor.UpperLeft, Color.white, true);
            TouchUi.Label(TouchUi.R(tx, top + 39, 100, 9), "Máu " + C_PortScreens.heroHp(h) + " · Tấn công " + C_PortScreens.heroAttack(h), 6.25f, TextAnchor.UpperLeft, TouchUi.Hex(0xFFE39A));
            TouchUi.Label(TouchUi.R(tx, top + 49, 100, 9), "Cấp 1 · tăng theo cấp", 5.75f, TextAnchor.UpperLeft, Kit.Muted);

            float y = top + 10 + pic + 6;
            string desc = C_PortScreens.heroDesc(h);
            int lines = Mathf.Min(3, TouchUi.Lines(desc, 150));
            TouchUi.Label(TouchUi.R(15, y, 150, lines * UiFont.LineHeight), desc, 6.25f, TextAnchor.UpperLeft, Kit.Cream, false, true);
            y += lines * UiFont.LineHeight + 6;

            float bottom = TouchUi.V(new Vector2(0, r.yMax)).y;
            float icon = Mathf.Min(30, bottom - y - 24);
            if (icon < 14)
            {
                return;
            }
            TouchUi.PixelText("TUYỆT CHIÊU", 1, TouchUi.R(15, 0, 0, 0).x, TouchUi.R(0, y + 5, 0, 0).y, 0, true);
            y += 13;
            int atk = C_PortScreens.heroAttack(h);
            for (int i = 0; i < 3; i++)
            {
                int id = C_PortScreens.heroSkill(h, i);
                float cx = 36 + i * 54;
                Rect ir = TouchUi.R(cx - icon / 2, y, icon, icon);
                TouchUi.Fill(new Rect(ir.x - k, ir.y - k, ir.width + 2 * k, ir.height + 2 * k), TouchUi.Outline);
                TouchUi.Sprite(TouchUi.Img(SkillIcon(id)), ir);
                TouchUi.Label(TouchUi.R(cx - 27, y + icon + 2, 54, 9), C_PortScreens.skillTitle(id), 5.75f, TextAnchor.UpperCenter, Kit.Cream);
            }
        }

        void CancelBox()
        {
            float k = TouchUi.K;
            Rect box = TouchUi.R(14, Kit.H / 2 - 40, 152, 80);
            Kit.Modal(box);
            Rect yes = TouchUi.R(24, Kit.H / 2 + 10, 62, 22), no = TouchUi.R(94, Kit.H / 2 + 10, 62, 22);
            if (Kit.Tap(31, yes))
            {
                askCancel = false;
                C_PortScreens.request(C_PortScreens.f_HERO_BACK, 0);
            }
            if (Kit.Tap(32, no) || Kit.Outside())
            {
                askCancel = false;
            }
            if (Kit.Back())
            {
                askCancel = false;
                Kit.E.Use();
            }
            if (Kit.Paint)
            {
                TouchUi.Dim(0.65f);
                TouchUi.Panel(box);
                TouchUi.PixelText("HỦY TẠO NHÂN VẬT?", 1, box.center.x, box.y + 16 * k, 1, true);
                TouchUi.Label(TouchUi.R(20, Kit.H / 2 - 20, 140, 20), "Chưa mất lượt tạo, lần sau tạo lại được.", 6.25f, TextAnchor.MiddleCenter, Kit.Cream, false, true);
                Kit.FlatButton(yes, "Có", Btn.Red, Kit.Down(31));
                Kit.FlatButton(no, "Không", Btn.Grey, Kit.Down(32));
            }
            Kit.Unblock();
        }
    }
}
