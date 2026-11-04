// Screen 5 of the touch redesign (plan/thiet-ke-giao-dien.md): preparing a battle.
//
// Shown instead of the game's VS box on the map (after its short intro):
//   the two fighters / who moves first / the 3 items to take into battle (a 4th slot is reserved for
//   VIP) / the bag with quick purchase (gold) / a hint about the enemy / XUẤT TRẬN.
// "Xuất trận" and "Rút lui" press the VS box's own soft keys, so the game's checks (lives, ...) apply.
// Rows of slots and buttons are centred on the screen (equal margins on both sides).
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchPrepareView : ITouchView
    {
        const int GridH = 372;

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

        // four 36-px cells 6 px apart: x 9..171, centred on 90
        static float CellX(int i)
        {
            return 9 + i * 42;
        }

        static Rect SlotRect(int i)
        {
            return TouchUi.R(CellX(i), 176, 36, 36);
        }

        static Rect BagRect(int i)
        {
            return TouchUi.R(CellX(i), 234, 36, 36);
        }

        static Rect BuyRect(int i)
        {
            return TouchUi.R(CellX(i), 272, 36, 15);
        }

        // XUẤT TRẬN alone, or RÚT LUI + XUẤT TRẬN side by side (8 px margins, room around the words)
        static Rect FightRect()
        {
            return C_PortUi.canRetreat() ? TouchUi.R(76, 342, 96, 30) : TouchUi.R(20, 342, 140, 30);
        }

        static Rect BackRect()
        {
            return TouchUi.R(8, 342, 64, 30);
        }

        // the purchase popup grows with the item's description; centred on the bag
        Rect ConfirmRect()
        {
            int lines = confirmType >= 0 ? TouchUi.Lines(C_PortUi.itemDesc(confirmType), 144) : 3;
            float h = 66 + lines * UiFont.LineHeight + 4 + 16 + 40;
            return TouchUi.R(10, Mathf.Round(230 - h / 2f), 160, h);
        }

        float ConfirmBottom()
        {
            return TouchUi.V(new Vector2(0, ConfirmRect().yMax)).y;
        }

        Rect ConfirmClose()
        {
            return TouchUi.R(18, ConfirmBottom() - 34, 68, 24);
        }

        Rect ConfirmBuy()
        {
            return TouchUi.R(94, ConfirmBottom() - 34, 68, 24);
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
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Background);
            TouchUi.Wallpaper();
            TouchUi.PixelText("CHUẨN BỊ XUẤT TRẬN", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 11, 0, 0).y, 1, true);

            // the two fighters
            TouchUi.Nine(TouchUi.Img("/m/ff"), 12, TouchUi.R(4, 22, 172, 118));
            TouchUi.Portrait(C_PortUi.vsPortrait(0), TouchUi.R(12, 30, 64, 64));
            TouchUi.Portrait(C_PortUi.vsPortrait(1), TouchUi.R(104, 30, 64, 64), true);
            Rect sw = TouchUi.R(77, 50, 26, 26);
            TouchUi.Fill(sw, TouchUi.Outline);
            Texture2D swords = TouchUi.Img("/m/sw");
            TouchUi.Sprite(swords, TouchUi.R(71, 48, swords.width, swords.height));
            TouchUi.PixelText(C_PortUi.vsName(0).ToUpperInvariant(), 2, TouchUi.R(12, 0, 0, 0).x, TouchUi.R(0, 103, 0, 0).y, 0, true);
            TouchUi.PixelText(C_PortUi.vsName(1).ToUpperInvariant(), 2, TouchUi.R(168, 0, 0, 0).x, TouchUi.R(0, 103, 0, 0).y, 2, true);
            Color stat = TouchUi.Hex(0xFFE6A0);
            TouchUi.Label(TouchUi.R(12, 109, 76, 13), "Cấp " + C_PortUi.vsLevel(0), 7, TextAnchor.UpperLeft, stat);
            TouchUi.Label(TouchUi.R(12, 122, 76, 13), C_PortUi.vsHp(0) + " máu", 7, TextAnchor.UpperLeft, stat);
            TouchUi.Label(TouchUi.R(92, 109, 76, 13), "Cấp " + C_PortUi.vsLevel(1), 7, TextAnchor.UpperRight, stat);
            TouchUi.Label(TouchUi.R(92, 122, 76, 13), C_PortUi.vsHp(1) + " máu", 7, TextAnchor.UpperRight, stat);
            string first = C_PortUi.playerFirst() ? "Bạn được đi trước" : C_PortUi.vsName(1) + " được đi trước";
            TouchUi.Label(TouchUi.R(0, 142, 180, 14), first, 7, TextAnchor.MiddleCenter, TouchUi.TextSoft);

            // the items to take
            TouchUi.Panel(TouchUi.R(4, 158, 172, 180));
            TouchUi.PixelText("MANG VÀO TRẬN", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 167, 0, 0).y, 1, true);
            Texture2D focus = TouchUi.Img("/focusitem");
            int[] slots = Loadout.Get();
            for (int i = 0; i < 4; i++)
            {
                Rect r = i < 3 ? SlotRect(i) : TouchUi.R(CellX(3), 176, 36, 36);
                if (i == 3)
                {
                    TouchUi.Fill(r, TouchUi.Hex(0x140812));
                    Texture2D lk = TouchUi.Img("/m/lock");
                    TouchUi.Sprite(lk, TouchUi.R(CellX(3) + 10, 180, 16, 16));
                    TouchUi.PixelText("VIP", 2, r.center.x, TouchUi.R(0, 204, 0, 0).y, 1, true);
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
            TouchUi.PixelText("TÚI ĐỒ", 2, TouchUi.R(CellX(0), 0, 0, 0).x, TouchUi.R(0, 224, 0, 0).y, 0, true);
            Texture2D gold = TouchUi.Img("/gold");
            TouchUi.Label(TouchUi.R(84, 217, 70, 14), TouchUi.Money(C_PortUi.gold()), 8, TextAnchor.MiddleRight, TouchUi.TextGold, true);
            TouchUi.Sprite(gold, TouchUi.R(155, 216, 16, 16));
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
                TouchUi.Label(b, TouchUi.Money(price), 6, TextAnchor.MiddleCenter, Color.white, true);
            }

            // a hint about the enemy
            // centred and narrower than the panel, so the lines keep clear of its corner ornaments
            TouchUi.Label(TouchUi.R(22, 289, 136, 45), Hint(), 7, TextAnchor.UpperCenter, TouchUi.TextGold, false, true);

            // toast
            float age = Time.unscaledTime - toastTime;
            if (age < 2.2f && toast.Length > 0)
            {
                Color c = TouchUi.TextGreen;
                c.a = age < 1.8f ? 1f : 1f - (age - 1.8f) / 0.4f;
                TouchUi.Fill(TouchUi.R(6, 294, 168, 34), new Color(0.05f, 0.02f, 0.05f, 0.9f * c.a));
                TouchUi.Label(TouchUi.R(10, 294, 160, 34), toast, 7, TextAnchor.MiddleCenter, c, true, true);
            }

            if (C_PortUi.canRetreat())
            {
                TouchUi.TextButton(BackRect(), "RÚT LUI", Btn.Grey, downKind == 2);
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
            TouchUi.Label(new Rect(r.xMax - 3 * k, r.yMax - 3 * k, 0, 0), n.ToString(), 6.5f, TextAnchor.LowerRight, Color.white, true);
        }

        static string Hint()
        {
            string enemy = C_PortUi.vsName(1);
            int pa = C_PortUi.vsAttack(0), ea = C_PortUi.vsAttack(1);
            int ph = C_PortUi.vsHp(0), eh = C_PortUi.vsHp(1);
            if (ea > pa || C_PortUi.vsLevel(1) > C_PortUi.vsLevel(0))
            {
                return "Gợi ý: " + enemy + " đánh mạnh, nên mang Quỷ Diện Giáp.";
            }
            if (eh > ph)
            {
                return "Gợi ý: " + enemy + " nhiều máu, nên mang Long Thần Kiếm.";
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
            float py = TouchUi.V(new Vector2(0, p.y)).y;
            TouchUi.PixelText(C_PortUi.itemName(type).ToUpperInvariant(), 1, p.center.x, TouchUi.R(0, py + 15, 0, 0).y, 1, true);
            TouchUi.Item(type, TouchUi.R(18, py + 28, 28, 28));
            TouchUi.Label(TouchUi.R(52, py + 28, 112, 28), "Mua " + pack + " cái giá " + TouchUi.Money(price) + " vàng?", 7.5f, TextAnchor.MiddleLeft, Color.white, true, true);
            string desc = C_PortUi.itemDesc(type);
            int lines = TouchUi.Lines(desc, 144);
            TouchUi.Label(TouchUi.R(18, py + 62, 144, lines * UiFont.LineHeight), desc, 7, TextAnchor.UpperLeft, TouchUi.TextSoft, false, true);
            bool afford = C_PortUi.gold() >= price;
            TouchUi.Label(TouchUi.R(10, py + 66 + lines * UiFont.LineHeight, 160, 14), afford ? "Bạn đang có " + TouchUi.Money(C_PortUi.gold()) + " vàng" : "Không đủ vàng", 7,
                TextAnchor.MiddleCenter, afford ? TouchUi.TextGold : TouchUi.Hex(0xFF9A80));
            TouchUi.TextButton(ConfirmClose(), "ĐÓNG", Btn.Grey, downKind == 7);
            TouchUi.TextButton(ConfirmBuy(), "MUA", Btn.Gold, downKind == 6, afford);
        }
    }
}
