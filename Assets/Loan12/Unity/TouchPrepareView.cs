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

        /// <summary>The arena's battle is being prepared (ĐÁNH in the arena): the same screen, its opponent.</summary>
        public static bool ArenaOpen;

        // lets the map's encounter animation (the crossing swords) finish before the VS screen appears
        const float EncounterDelay = 0f;
        float readySince = -1f; // when the game's VS box became ready, -1 when it is not

        static bool Arena
        {
            get { return ArenaOpen && !C_PortUi.prepareView(); }
        }

        public bool Active()
        {
            try
            {
                if (ArenaOpen && !(C_PortScreens.screenKind() == 4 && C_PortMap.arena()))
                {
                    ArenaOpen = false; // the arena has gone (left, or its battle started)
                }
                bool prepare = C_PortUi.prepareView();
                if (!prepare)
                {
                    readySince = -1f;
                    return ArenaOpen;
                }
                if (ArenaOpen)
                {
                    return true; // the arena has no encounter animation: no delay
                }
                // meeting an enemy on the map: the touch map (TouchMapHoldView) keeps showing the encounter
                // animation, the VS screen comes once it is over
                if (readySince < 0f)
                {
                    readySince = Time.unscaledTime;
                }
                return Time.unscaledTime - readySince >= EncounterDelay;
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

        // ---- the two fighters: the map's VS box, or the arena's chosen level

        static int ArenaLevel
        {
            get { return Mathf.Max(0, C_PortMap.arenaChosen()); }
        }

        static int VsPortrait(int i)
        {
            return !Arena ? C_PortUi.vsPortrait(i) : (i == 0 ? C_PortMap.hero() : C_PortMap.arenaEnemy(ArenaLevel));
        }

        static string VsName(int i)
        {
            return !Arena ? C_PortUi.vsName(i) : (i == 0 ? C_PortMap.heroName() : QuestData.Who(C_PortMap.arenaEnemy(ArenaLevel)));
        }

        static int VsLevel(int i)
        {
            return !Arena ? C_PortUi.vsLevel(i) : (i == 0 ? C_PortMap.heroLevel() : C_PortMap.arenaEnemyLevel(ArenaLevel));
        }

        static int VsHp(int i)
        {
            return !Arena ? C_PortUi.vsHp(i) : (i == 0 ? C_PortMap.heroMaxHp() : C_PortMap.arenaEnemyHp(ArenaLevel));
        }

        static int VsAttack(int i)
        {
            return !Arena ? C_PortUi.vsAttack(i) : (i == 0 ? C_PortMap.heroAttack() : C_PortMap.arenaEnemyAttack(ArenaLevel));
        }

        /// <summary>1 the player moves first, -1 the enemy, 0 drawn by lot (the arena between equal levels).</summary>
        static int First()
        {
            if (!Arena)
            {
                return C_PortUi.playerFirst() ? 1 : -1;
            }
            int a = VsLevel(0), b = VsLevel(1);
            return a > b ? 1 : (a < b ? -1 : 0);
        }

        static bool CanRetreat()
        {
            return Arena || C_PortUi.canRetreat();
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

        // the bag row has 5 items (Ngọc Ấn too): 30-px cells 3 px apart, x 9..171
        const int BagCount = 5;

        static Rect BagRect(int i)
        {
            return TouchUi.R(9 + i * 33, 236, 30, 30);
        }

        static Rect BuyRect(int i)
        {
            return TouchUi.R(9 + i * 33, 269, 30, 15);
        }

        // XUẤT TRẬN alone, or RÚT LUI + XUẤT TRẬN side by side (8 px margins, room around the words)
        static Rect FightRect()
        {
            return CanRetreat() ? TouchUi.R(76, 342, 96, 30) : TouchUi.R(20, 342, 140, 30);
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
                    else if (BackRect().Contains(p) && CanRetreat())
                    {
                        downKind = 2;
                    }
                    for (int i = 0; i < BagCount; i++)
                    {
                        if (i < Loadout.MaxSlots && SlotRect(i).Contains(p))
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
                            // Ngọc Ấn taken: one is used, the player moves first; chosen again for the next battle
                            int at = System.Array.IndexOf(slots, 5);
                            if (at >= 0)
                            {
                                if (Arena)
                                {
                                    C_PortMap.f_arenaSeal = First() < 1; // used when the arena pays the ticket
                                }
                                else if (!C_PortUi.playerFirst())
                                {
                                    C_PortScreens.useSeal();
                                }
                                slots[at] = -1;
                                Loadout.Set(slots);
                            }
                            if (Arena)
                            {
                                ArenaOpen = false;
                                C_PortMap.request(C_PortMap.f_ARENA_FIGHT, 0); // the arena's "Đánh": ticket, battle
                            }
                            else
                            {
                                C_PortUi.request(C_PortUi.f_SCREEN_KEY, 0); // "Đánh"
                            }
                        }
                        break;
                    case 2:
                        if (BackRect().Contains(p))
                        {
                            if (Arena)
                            {
                                ArenaOpen = false; // back to the arena's levels
                            }
                            else
                            {
                                C_PortUi.request(C_PortUi.f_SCREEN_KEY, 2); // "Rút lui"
                            }
                        }
                        break;
                    case 3:
                        if (SlotRect(downArg).Contains(p) && downArg >= slots.Length)
                        {
                            // a locked slot: what opens it
                            Toast(downArg == 2 ? "Ô thứ 3 mở khi mua Gói khởi đầu (Cửa hàng · Ưu đãi)" : "Ô thứ 4 mở khi mua gói VIP (Cửa hàng · Ưu đãi)");
                        }
                        else if (SlotRect(downArg).Contains(p) && slots[downArg] >= 0)
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
                Toast("Đã đủ " + slots.Length + " món, chạm vào ô trên để bỏ bớt");
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
            TouchUi.Portrait(VsPortrait(0), TouchUi.R(12, 30, 64, 64));
            TouchUi.Portrait(VsPortrait(1), TouchUi.R(104, 30, 64, 64), true);
            Rect sw = TouchUi.R(77, 50, 26, 26);
            TouchUi.Fill(sw, TouchUi.Outline);
            Texture2D swords = TouchUi.Img("/m/sw");
            TouchUi.Sprite(swords, TouchUi.R(71, 48, swords.width, swords.height));
            // the two names, each in its half: the game's font when both fit, else both in a smaller one
            string n0 = VsName(0).ToUpperInvariant(), n1 = VsName(1).ToUpperInvariant();
            const float half = 76;
            if (TouchUi.PixelWidth(n0, 2) <= half && TouchUi.PixelWidth(n1, 2) <= half)
            {
                TouchUi.PixelText(n0, 2, TouchUi.R(12, 0, 0, 0).x, TouchUi.R(0, 103, 0, 0).y, 0, true);
                TouchUi.PixelText(n1, 2, TouchUi.R(168, 0, 0, 0).x, TouchUi.R(0, 103, 0, 0).y, 2, true);
            }
            else
            {
                float ns = 8f;
                while (ns > 5.5f && (TouchUi.TextWidth(n0, ns, true) > half || TouchUi.TextWidth(n1, ns, true) > half))
                {
                    ns -= 0.5f;
                }
                TouchUi.NameLabel(TouchUi.R(12, 96, half, 14), n0, ns, TextAnchor.MiddleLeft, Color.white, true);
                TouchUi.NameLabel(TouchUi.R(168 - half, 96, half, 14), n1, ns, TextAnchor.MiddleRight, Color.white, true);
            }
            Color stat = TouchUi.Hex(0xFFE6A0);
            TouchUi.Label(TouchUi.R(12, 109, 76, 13), "Cấp " + VsLevel(0), 7, TextAnchor.UpperLeft, stat);
            TouchUi.Label(TouchUi.R(12, 122, 76, 13), VsHp(0) + " máu", 7, TextAnchor.UpperLeft, stat);
            TouchUi.Label(TouchUi.R(92, 109, 76, 13), "Cấp " + VsLevel(1), 7, TextAnchor.UpperRight, stat);
            TouchUi.Label(TouchUi.R(92, 122, 76, 13), VsHp(1) + " máu", 7, TextAnchor.UpperRight, stat);
            // Ngọc Ấn in the slots: the player moves first (one is used on XUẤT TRẬN)
            bool seal = System.Array.IndexOf(Loadout.Get(), 5) >= 0 && C_PortUi.owned(5) > 0;
            int f0 = First();
            string first = f0 == 1 ? "Bạn được đi trước" : (seal ? "Bạn được đi trước (Ngọc Ấn)" : (f0 == 0 ? "Bốc thăm ai đi trước (cùng cấp)" : VsName(1) + " được đi trước"));
            TouchUi.Label(TouchUi.R(0, 142, 180, 14), first, 7, TextAnchor.MiddleCenter, seal && f0 != 1 ? TouchUi.TextGreen : TouchUi.TextSoft);

            // the items to take
            TouchUi.Panel(TouchUi.R(4, 158, 172, 180));
            TouchUi.PixelText("MANG VÀO TRẬN", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 167, 0, 0).y, 1, true);
            Texture2D focus = TouchUi.Img("/focusitem");
            int[] slots = Loadout.Get();
            for (int i = 0; i < Loadout.MaxSlots; i++)
            {
                Rect r = SlotRect(i);
                if (i >= slots.Length)
                {
                    // locked: the 3rd slot comes with the starter pack, the 4th with VIP
                    TouchUi.Fill(r, TouchUi.Hex(0x140812));
                    Texture2D lk = TouchUi.Img("/m/lock");
                    TouchUi.Sprite(lk, TouchUi.R(CellX(i) + 10, 179, 16, 16));
                    if (i == 3)
                    {
                        TouchUi.PixelText("VIP", 2, r.center.x, TouchUi.R(0, 204, 0, 0).y, 1, true);
                    }
                    else
                    {
                        TouchUi.Label(TouchUi.R(CellX(i), 196, 36, 14), "KHỞI ĐẦU", 5, TextAnchor.MiddleCenter, Kit.Gold, true);
                    }
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
            for (int i = 0; i < BagCount; i++)
            {
                int type = Loadout.Usable[i];
                int owned = C_PortUi.owned(type);
                bool chosen = System.Array.IndexOf(slots, type) >= 0;
                Rect r = BagRect(i);
                TouchUi.Sprite(focus, r, new Rect(chosen ? 0 : 36, 0, 36, 36));
                TouchUi.Item(type, new Rect(r.x + 3 * k, r.y + 3 * k, 24 * k, 24 * k), owned > 0 ? 1f : 0.4f);
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
                TouchUi.Label(b, TouchUi.Money(price), 5.5f, TextAnchor.MiddleCenter, Color.white, true);
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

            if (CanRetreat())
            {
                TouchUi.TextButton(BackRect(), Arena ? "TRỞ LẠI" : "RÚT LUI", Btn.Grey, downKind == 2);
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
            string enemy = VsName(1);
            int pa = VsAttack(0), ea = VsAttack(1);
            int ph = VsHp(0), eh = VsHp(1);
            if (ea > pa || VsLevel(1) > VsLevel(0))
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
