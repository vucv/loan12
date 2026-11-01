// Screen 5 of the touch redesign (plan/thiet-ke-giao-dien.md): preparing a battle.
//
// Shown instead of the game's VS box on the map (after its short intro):
//   the two fighters / who moves first / the 3 items to take into battle (a 4th slot is reserved for
//   VIP) / the bag with quick purchase (gold) / a hint about the enemy / XUẤT TRẬN.
// "Xuất trận" and "Rút lui" press the VS box's own soft keys, so the game's checks (lives, ...) apply.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchPrepareView : ITouchView
    {
        const int GridH = 352;

        int downKind, downArg; // 1 fight, 2 retreat, 3 slot, 4 bag item, 5 buy, 6 confirm buy, 7 close confirm, 8 outside confirm
        int confirmType = -1;
        string toast = "";
        float toastTime = -10f;

        public bool Active()
        {
            try
            {
                return C_PortUi.prepareView();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            downKind = 0;
            confirmType = -1;
        }

        static Rect SlotRect(int i)
        {
            return TouchUi.R(10 + i * 42, 152, 36, 36);
        }

        static Rect BagRect(int i)
        {
            return TouchUi.R(10 + i * 42, 210, 36, 36);
        }

        static Rect BuyRect(int i)
        {
            return TouchUi.R(10 + i * 42, 248, 36, 14);
        }

        static Rect FightRect()
        {
            return TouchUi.R(20, 318, 140, 30);
        }

        static Rect BackRect()
        {
            return TouchUi.R(3, 0, 20, 20);
        }

        static Rect ConfirmRect()
        {
            return TouchUi.R(10, 110, 160, 152);
        }

        static Rect ConfirmBuy()
        {
            return TouchUi.R(96, 228, 66, 24);
        }

        static Rect ConfirmClose()
        {
            return TouchUi.R(18, 229, 66, 22);
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            TouchUi.Begin(GridH);
            HandleInput(e, haptics);
            if (e.type == EventType.Repaint)
            {
                Draw();
            }
        }

        // ---------------------------------------------------------------------------------- input

        void HandleInput(Event e, bool haptics)
        {
            Vector2 p = e.mousePosition;
            if (e.type == EventType.MouseDown && e.button == 0)
            {
                downKind = 0;
                if (confirmType >= 0)
                {
                    downKind = ConfirmBuy().Contains(p) ? 6 : (ConfirmClose().Contains(p) ? 7 : (!ConfirmRect().Contains(p) ? 8 : 0));
                }
                else
                {
                    if (FightRect().Contains(p))
                    {
                        downKind = 1;
                    }
                    else if (BackRect().Contains(p) && C_PortUi.canRetreat())
                    {
                        downKind = 2;
                    }
                    for (int i = 0; i < 4; i++)
                    {
                        if (i < Loadout.Slots && SlotRect(i).Contains(p))
                        {
                            downKind = 3;
                            downArg = i;
                        }
                        if (BagRect(i).Contains(p))
                        {
                            downKind = 4;
                            downArg = i;
                        }
                        if (BuyRect(i).Contains(p))
                        {
                            downKind = 5;
                            downArg = i;
                        }
                    }
                }
                if (downKind != 0)
                {
                    Haptics.Tap(haptics);
                }
                e.Use();
            }
            else if (e.type == EventType.MouseUp)
            {
                int[] slots = Loadout.Get();
                switch (downKind)
                {
                    case 1:
                        if (FightRect().Contains(p))
                        {
                            C_PortUi.request(C_PortUi.f_SCREEN_KEY, 0); // "Đánh"
                        }
                        break;
                    case 2:
                        if (BackRect().Contains(p))
                        {
                            C_PortUi.request(C_PortUi.f_SCREEN_KEY, 2); // "Rút lui"
                        }
                        break;
                    case 3:
                        if (SlotRect(downArg).Contains(p) && slots[downArg] >= 0)
                        {
                            slots[downArg] = -1;
                            Loadout.Set(slots);
                        }
                        break;
                    case 4:
                        if (BagRect(downArg).Contains(p))
                        {
                            Toggle(slots, Loadout.Usable[downArg]);
                        }
                        break;
                    case 5:
                        if (BuyRect(downArg).Contains(p))
                        {
                            confirmType = Loadout.Usable[downArg];
                        }
                        break;
                    case 6:
                        if (ConfirmBuy().Contains(p))
                        {
                            Buy(confirmType);
                            confirmType = -1;
                        }
                        break;
                    case 7:
                    case 8:
                        confirmType = -1;
                        break;
                }
                downKind = 0;
                e.Use();
            }
        }

        void Toggle(int[] slots, int type)
        {
            int at = System.Array.IndexOf(slots, type);
            if (at >= 0)
            {
                slots[at] = -1;
                Loadout.Set(slots);
                return;
            }
            if (C_PortUi.owned(type) <= 0)
            {
                Toast("Chưa có " + C_PortUi.itemName(type) + ", bấm MUA để mua");
                return;
            }
            int free = System.Array.IndexOf(slots, -1);
            if (free < 0)
            {
                Toast("Đã đủ 3 món, chạm vào ô trên để bỏ bớt");
                return;
            }
            slots[free] = type;
            Loadout.Set(slots);
        }

        void Buy(int type)
        {
            int price = C_PortUi.price(type), pack = C_PortUi.packSize(type);
            if (C_PortUi.buy(type))
            {
                Toast("Đã mua " + pack + " " + C_PortUi.itemName(type) + " (-" + TouchUi.Money(price) + " vàng)");
                int[] slots = Loadout.Get();
                if (System.Array.IndexOf(slots, type) < 0 && System.Array.IndexOf(slots, -1) >= 0)
                {
                    slots[System.Array.IndexOf(slots, -1)] = type;
                    Loadout.Set(slots);
                }
            }
            else
            {
                Toast("Không đủ vàng");
            }
        }

        void Toast(string s)
        {
            toast = s;
            toastTime = Time.unscaledTime;
        }

        // ---------------------------------------------------------------------------------- drawing

        void Draw()
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
            TouchUi.PixelText("CHUẨN BỊ XUẤT TRẬN", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 5, 0, 0).y, 1);
            if (C_PortUi.canRetreat())
            {
                Rect b = BackRect();
                TouchUi.Button(b, Btn.Dark, downKind == 2);
                Color c = TouchUi.Hex(0xF0E8D8);
                for (int i = 0; i < 6; i++)
                {
                    TouchUi.Fill(new Rect(b.x + (4 + i) * k, b.y + (10 - i) * k, k, (1 + 2 * i) * k), c);
                }
                TouchUi.Fill(new Rect(b.x + 10 * k, b.y + 8 * k, 6 * k, 5 * k), c);
            }

            // the two fighters
            TouchUi.Nine(TouchUi.Img("/m/ff"), 12, TouchUi.R(4, 22, 172, 100));
            TouchUi.Portrait(C_PortUi.vsPortrait(0), TouchUi.R(12, 30, 64, 64));
            TouchUi.Portrait(C_PortUi.vsPortrait(1), TouchUi.R(104, 30, 64, 64), true);
            Rect sw = TouchUi.R(77, 50, 26, 26);
            TouchUi.Fill(sw, TouchUi.Outline);
            Texture2D swords = TouchUi.Img("/m/sw");
            TouchUi.Sprite(swords, TouchUi.R(71, 48, swords.width, swords.height));
            TouchUi.PixelText(C_PortUi.vsName(0).ToUpperInvariant(), 2, TouchUi.R(12, 0, 0, 0).x, TouchUi.R(0, 96, 0, 0).y, 0);
            TouchUi.PixelText(C_PortUi.vsName(1).ToUpperInvariant(), 2, TouchUi.R(168, 0, 0, 0).x, TouchUi.R(0, 96, 0, 0).y, 2);
            TouchUi.Label(TouchUi.R(12, 108, 80, 10), "Cấp " + C_PortUi.vsLevel(0) + "  ·  " + C_PortUi.vsHp(0) + " máu", 7, TextAnchor.UpperLeft, TouchUi.Hex(0xFFE6A0));
            TouchUi.Label(TouchUi.R(88, 108, 80, 10), C_PortUi.vsHp(1) + " máu  ·  Cấp " + C_PortUi.vsLevel(1), 7, TextAnchor.UpperRight, TouchUi.Hex(0xFFE6A0));
            string first = C_PortUi.playerFirst() ? "Bạn được đi trước" : C_PortUi.vsName(1) + " được đi trước";
            TouchUi.Label(TouchUi.R(0, 124, 180, 10), first, 7, TextAnchor.MiddleCenter, TouchUi.TextSoft);

            // the items to take
            TouchUi.Panel(TouchUi.R(4, 136, 172, 176));
            TouchUi.PixelText("MANG VÀO TRẬN", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 140, 0, 0).y, 1);
            Texture2D focus = TouchUi.Img("/focusitem");
            int[] slots = Loadout.Get();
            for (int i = 0; i < 4; i++)
            {
                Rect r = i < 3 ? SlotRect(i) : TouchUi.R(10 + 3 * 42, 152, 36, 36);
                if (i == 3)
                {
                    TouchUi.Fill(r, TouchUi.Hex(0x140812));
                    Texture2D lk = TouchUi.Img("/m/lock");
                    TouchUi.Sprite(lk, TouchUi.R(10 + 3 * 42 + 10, 156, 16, 16));
                    TouchUi.PixelText("VIP", 2, r.center.x, TouchUi.R(0, 173, 0, 0).y, 1);
                    continue;
                }
                if (slots[i] < 0)
                {
                    TouchUi.Fill(r, TouchUi.Hex(0x140812));
                    TouchUi.Label(r, "+", 12, TextAnchor.MiddleCenter, TouchUi.GoldLine, true);
                    continue;
                }
                TouchUi.Sprite(focus, r, new Rect(0, 0, 36, 36));
                TouchUi.Item(slots[i], new Rect(r.x + 4 * k, r.y + 4 * k, 28 * k, 28 * k));
                Count(r, C_PortUi.owned(slots[i]));
            }

            // the bag with quick purchase
            TouchUi.PixelText("TÚI ĐỒ", 2, TouchUi.R(10, 0, 0, 0).x, TouchUi.R(0, 194, 0, 0).y, 0);
            Texture2D gold = TouchUi.Img("/gold");
            TouchUi.Label(TouchUi.R(90, 194, 70, 12), TouchUi.Money(C_PortUi.gold()), 8, TextAnchor.MiddleRight, TouchUi.TextGold, true);
            TouchUi.Sprite(gold, TouchUi.R(160, 189, 18, 18));
            for (int i = 0; i < 4; i++)
            {
                int type = Loadout.Usable[i];
                int owned = C_PortUi.owned(type);
                bool chosen = System.Array.IndexOf(slots, type) >= 0;
                Rect r = BagRect(i);
                TouchUi.Sprite(focus, r, new Rect(chosen ? 0 : 36, 0, 36, 36));
                TouchUi.Item(type, new Rect(r.x + 4 * k, r.y + 4 * k, 28 * k, 28 * k), owned > 0 ? 1f : 0.4f);
                Count(r, owned);
                if (chosen)
                {
                    Rect t = new Rect(r.x + k, r.y + k, 10 * k, 9 * k);
                    TouchUi.Fill(t, TouchUi.Hex(0x2F8A22));
                    // tick mark
                    TouchUi.Fill(new Rect(t.x + 2 * k, t.y + 4 * k, k, 2 * k), Color.white);
                    TouchUi.Fill(new Rect(t.x + 3 * k, t.y + 5 * k, k, 2 * k), Color.white);
                    TouchUi.Fill(new Rect(t.x + 4 * k, t.y + 4 * k, k, 2 * k), Color.white);
                    TouchUi.Fill(new Rect(t.x + 5 * k, t.y + 3 * k, k, 2 * k), Color.white);
                    TouchUi.Fill(new Rect(t.x + 6 * k, t.y + 2 * k, k, 2 * k), Color.white);
                    TouchUi.Fill(new Rect(t.x + 7 * k, t.y + 1 * k, k, 2 * k), Color.white);
                }
                int price = C_PortUi.price(type);
                Rect b = BuyRect(i);
                bool afford = price > 0 && C_PortUi.gold() >= price;
                TouchUi.Button(b, afford ? Btn.Gold : Btn.Grey, downKind == 5 && downArg == i);
                TouchUi.Label(b, "MUA " + TouchUi.Money(price), 6, TextAnchor.MiddleCenter, Color.white, true);
            }

            // a hint about the enemy
            TouchUi.Label(TouchUi.R(12, 268, 156, 40), Hint(), 7, TextAnchor.UpperLeft, TouchUi.TextGold, false, true);

            // toast
            float age = Time.unscaledTime - toastTime;
            if (age < 2.2f && toast.Length > 0)
            {
                Color c = TouchUi.TextGreen;
                c.a = age < 1.8f ? 1f : 1f - (age - 1.8f) / 0.4f;
                TouchUi.Fill(TouchUi.R(6, 300, 168, 13), new Color(0.05f, 0.02f, 0.05f, 0.85f * c.a));
                TouchUi.Label(TouchUi.R(6, 300, 168, 13), toast, 7, TextAnchor.MiddleCenter, c, true);
            }

            TouchUi.RedButton(FightRect(), "XUẤT TRẬN", true, downKind == 1);

            if (confirmType >= 0)
            {
                DrawConfirm();
            }
        }

        static void Count(Rect r, int n)
        {
            float k = TouchUi.K;
            Rect c = new Rect(r.xMax - 15 * k, r.yMax - 11 * k, 13 * k, 9 * k);
            TouchUi.Fill(c, new Color(0f, 0f, 0f, 0.8f));
            TouchUi.Label(c, n.ToString(), 6.5f, TextAnchor.MiddleRight, Color.white, true);
        }

        static string Hint()
        {
            string enemy = C_PortUi.vsName(1);
            int pa = C_PortUi.vsAttack(0), ea = C_PortUi.vsAttack(1);
            int ph = C_PortUi.vsHp(0), eh = C_PortUi.vsHp(1);
            if (ea > pa || C_PortUi.vsLevel(1) > C_PortUi.vsLevel(0))
            {
                return "Gợi ý: " + enemy + " ra đòn mạnh, nên mang Quỷ Diện Giáp để đỡ đòn.";
            }
            if (eh > ph)
            {
                return "Gợi ý: " + enemy + " nhiều máu, Long Thần Kiếm giúp đánh nhanh hơn.";
            }
            return "Gợi ý: mang Bình Thuốc để hồi máu khi cần.";
        }

        void DrawConfirm()
        {
            float k = TouchUi.K;
            TouchUi.Dim(0.6f);
            int type = confirmType;
            int price = C_PortUi.price(type), pack = C_PortUi.packSize(type);
            Rect p = ConfirmRect();
            TouchUi.Panel(p);
            TouchUi.PixelText(C_PortUi.itemName(type).ToUpperInvariant(), 1, p.center.x, TouchUi.R(0, 118, 0, 0).y, 1);
            TouchUi.Item(type, TouchUi.R(18, 134, 28, 28));
            TouchUi.Label(TouchUi.R(52, 134, 112, 28), "Mua " + pack + " cái giá " + TouchUi.Money(price) + " vàng?", 7.5f, TextAnchor.MiddleLeft, Color.white, true, true);
            TouchUi.Label(TouchUi.R(18, 166, 146, 42), C_PortUi.itemDesc(type), 7, TextAnchor.UpperLeft, TouchUi.TextSoft, false, true);
            bool afford = C_PortUi.gold() >= price;
            TouchUi.Button(ConfirmClose(), Btn.Grey, downKind == 7);
            TouchUi.PixelText("ĐÓNG", 2, ConfirmClose().center.x, ConfirmClose().center.y, 1, true);
            TouchUi.Button(ConfirmBuy(), afford ? Btn.Gold : Btn.Grey, downKind == 6);
            TouchUi.PixelText("MUA", 2, ConfirmBuy().center.x, ConfirmBuy().center.y, 1, true);
            TouchUi.Label(TouchUi.R(10, 212, 160, 10), afford ? "Bạn đang có " + TouchUi.Money(C_PortUi.gold()) + " vàng" : "Không đủ vàng", 7,
                TextAnchor.MiddleCenter, afford ? TouchUi.TextGold : TouchUi.Hex(0xFF9A80));
        }
    }
}
