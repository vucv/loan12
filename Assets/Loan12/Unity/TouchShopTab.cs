// Screen 7 of the touch redesign: the shop (plan/thiet-ke-ban-do.md 5), in place of the game's shop
// (6 small cells and a "Có / Không" box). A tab of the map, and a page of its own from the + next to
// the gold on the menu.
//   Vật phẩm: 2-column item cards, the "Mua" box with a number of packs (gold, shared wallet)
//   Vàng: the free chest (rewarded ad, 5 a day, 1 hour apart) and the 4 gold packs
//   Ưu đãi: starter pack, lifetime VIP, restore purchases
// Real-money purchases need Google Play Billing, not integrated yet: their buttons show "— đ" and do
// nothing (plan 5.5.6, "no price"), so nothing can be bought by mistake.
using System;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchShopTab : IMapTab
    {
        /// <summary>Sub tab: 0 items, 1 gold, 2 offers.</summary>
        public int sub;
        float scroll, contentH;
        int lastSub = -1;
        public int buying = -1;
        int packs = 1;
        float plusHeldAt = -1f, plusRepeat;

        const int ChestGold = 200, ChestPerDay = 5;
        const long ChestGap = 3600;

        static readonly int[] PackGold = {5000, 12500, 26000, 55000};
        static readonly string[] PackTag = {"", "+12%", "PHỔ BIẾN", "LỜI NHẤT"};

        public bool Back()
        {
            if (buying >= 0)
            {
                buying = -1;
                return true;
            }
            return false;
        }

        // ------------------------------------------------------------------------- free chest

        static long Now
        {
            get { return DateTimeOffset.UtcNow.ToUnixTimeSeconds(); }
        }

        static int Today
        {
            get { return int.Parse(DateTime.Now.ToString("yyyyMMdd")); }
        }

        static int ChestsLeft()
        {
            if (PlayerPrefs.GetInt("l12.chestDay", 0) != Today)
            {
                return ChestPerDay;
            }
            return Mathf.Max(0, ChestPerDay - PlayerPrefs.GetInt("l12.chestCount", 0));
        }

        /// <summary>Seconds until the chest can be opened again (0: now; -1: the clock went back).</summary>
        static long ChestWait()
        {
            long last = long.Parse(PlayerPrefs.GetString("l12.chestLast", "0"));
            if (last > Now)
            {
                return -1;
            }
            return Math.Max(0, last + ChestGap - Now);
        }

        public bool FreeReady()
        {
            return ChestsLeft() > 0 && ChestWait() == 0;
        }

        static void ChestOpened()
        {
            int day = Today;
            int count = PlayerPrefs.GetInt("l12.chestDay", 0) == day ? PlayerPrefs.GetInt("l12.chestCount", 0) : 0;
            PlayerPrefs.SetInt("l12.chestDay", day);
            PlayerPrefs.SetInt("l12.chestCount", count + 1);
            PlayerPrefs.SetString("l12.chestLast", Now.ToString());
            PlayerPrefs.Save();
            C_PortScreens.addGold(ChestGold);
            Haptics.Tap(Kit.Haptic);
            Kit.Toast("+" + ChestGold + " vàng vào ví chung");
        }

        // ------------------------------------------------------------------------- drawing

        public void OnGUI(TouchMapView map, float top, float bottom)
        {
            float k = TouchUi.K;
            if (sub != lastSub)
            {
                lastSub = sub;
                scroll = 0;
            }
            if (buying >= 0)
            {
                Kit.Modal(new Rect(0, 0, 0, 0));
            }
            // sub tabs
            string[] names = {"VẬT PHẨM", "VÀNG", "ƯU ĐÃI"};
            for (int i = 0; i < 3; i++)
            {
                Rect r = TouchUi.R(4 + i * 58, top + 3, 56, 16);
                if (Kit.Tap(10 + i, r))
                {
                    sub = i;
                }
                if (Kit.Paint)
                {
                    TouchUi.Button(r, i == sub ? Btn.Dark : Btn.Grey, false);
                    if (i == sub)
                    {
                        TouchUi.Fill(new Rect(r.x + k, r.yMax - 2 * k, r.width - 2 * k, 2 * k), Kit.Gold);
                    }
                    TouchUi.Label(r, names[i], 6, TextAnchor.MiddleCenter, i == sub ? Kit.Gold : Kit.Cream, true);
                    if ((i == 1 && FreeReady()) || (i == 2 && !Starter && !PlayerPrefs.HasKey("l12.starterSeen")))
                    {
                        Kit.Badge(r.xMax - 3 * k, r.y + 2 * k, 0);
                    }
                }
            }
            if (sub == 2 && !PlayerPrefs.HasKey("l12.starterSeen"))
            {
                PlayerPrefs.SetInt("l12.starterSeen", 1);
            }
            float listTop = top + 22;
            if (buying < 0)
            {
                scroll = MapUi.Scroll(scroll, contentH, bottom - listTop);
            }
            float y = listTop + 2 - scroll;
            if (sub == 0)
            {
                y = Items(map, y);
            }
            else if (sub == 1)
            {
                y = Gold(y);
            }
            else
            {
                y = Offers(y);
            }
            contentH = y + scroll - listTop;
            if (Kit.Paint)
            {
                // the sub tabs stay above the scrolled list
                TouchUi.Fill(new Rect(0, TouchUi.R(0, top, 0, 0).y, Gx.Width, 22 * k), TouchUi.Hex(0x0A121E));
                for (int i = 0; i < 3; i++)
                {
                    Rect r = TouchUi.R(4 + i * 58, top + 3, 56, 16);
                    TouchUi.Button(r, i == sub ? Btn.Dark : Btn.Grey, false);
                    if (i == sub)
                    {
                        TouchUi.Fill(new Rect(r.x + k, r.yMax - 2 * k, r.width - 2 * k, 2 * k), Kit.Gold);
                    }
                    TouchUi.Label(r, names[i], 6, TextAnchor.MiddleCenter, i == sub ? Kit.Gold : Kit.Cream, true);
                    if ((i == 1 && FreeReady()) || (i == 2 && !Starter && !PlayerPrefs.HasKey("l12.starterSeen")))
                    {
                        Kit.Badge(r.xMax - 3 * k, r.y + 2 * k, 0);
                    }
                }
            }
            Kit.Unblock();
            if (buying >= 0)
            {
                BuyBox(top, bottom);
            }
        }

        float Items(TouchMapView map, float y)
        {
            float k = TouchUi.K;
            int gold = C_PortScreens.gold();
            int cells = MapUi.BagItems.Length + 1;
            for (int i = 0; i < cells; i++)
            {
                Rect r = TouchUi.R(4 + (i % 2) * 88, y + (i / 2) * 88, 84, 84);
                if (Kit.Tap(20 + i, r))
                {
                    if (i < MapUi.BagItems.Length)
                    {
                        buying = MapUi.BagItems[i];
                        packs = 1;
                    }
                    else
                    {
                        sub = 1;
                    }
                }
                if (!Kit.Paint)
                {
                    continue;
                }
                if (i == MapUi.BagItems.Length)
                {
                    TouchUi.Button(r, Btn.Green, Kit.Down(20 + i));
                    TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(r.center.x - 15 * k, r.y + 14 * k, 30 * k, 30 * k));
                    TouchUi.Label(new Rect(r.x, r.y + 50 * k, r.width, 10 * k), "Thiếu vàng?", 7, TextAnchor.UpperCenter, Color.white, true);
                    TouchUi.Label(new Rect(r.x, r.y + 62 * k, r.width, 9 * k), "Rương miễn phí, gói vàng", 5.5f, TextAnchor.UpperCenter, Color.white);
                    continue;
                }
                int t = MapUi.BagItems[i], price = MapUi.Price(t);
                TouchUi.Panel(r);
                if (Kit.Down(20 + i))
                {
                    TouchUi.Fill(r, new Color(1, 1, 1, 0.08f));
                }
                MapUi.Icon(t, new Rect(r.center.x - 17.5f * k, r.y + 8 * k, 35 * k, 35 * k));
                Rect tag = new Rect(r.xMax - 26 * k, r.y + 5 * k, 22 * k, 9 * k);
                TouchUi.Fill(tag, TouchUi.Hex(0x1A0C16));
                TouchUi.Label(tag, "Có " + MapUi.Owned(t), 5.5f, TextAnchor.MiddleCenter, Color.white, true, false, false);
                TouchUi.Label(new Rect(r.x, r.y + 45 * k, r.width, 9 * k), MapUi.Name(t), 6.25f, TextAnchor.UpperCenter, Color.white, true);
                TouchUi.Label(new Rect(r.x, r.y + 54 * k, r.width, 9 * k), MapUi.Pack(t) + " cái", 5.5f, TextAnchor.UpperCenter, Kit.Muted);
                Rect pb = new Rect(r.x + 10 * k, r.yMax - 19 * k, r.width - 20 * k, 14 * k);
                bool enough = gold >= price;
                TouchUi.Button(pb, enough ? Btn.Gold : Btn.Grey, false);
                TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(pb.x + 3 * k, pb.y + 2 * k, 10 * k, 10 * k));
                TouchUi.Label(new Rect(pb.x + 12 * k, pb.y, pb.width - 14 * k, pb.height), TouchUi.Money(price), 6.5f, TextAnchor.MiddleCenter, Color.white, true);
            }
            y += ((cells + 1) / 2) * 88 + 4;
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(4, y, 172, 9), "Vàng và vật phẩm dùng chung cho mọi nhân vật", 5.5f, TextAnchor.UpperCenter, Kit.Muted);
            }
            return y + 14;
        }

        void BuyBox(float top, float bottom)
        {
            float k = TouchUi.K;
            int t = buying, price = MapUi.Price(t), pack = MapUi.Pack(t), gold = C_PortScreens.gold();
            int max = Mathf.Clamp(price > 0 ? gold / price : 0, 1, 10);
            packs = Mathf.Clamp(packs, 1, max);
            int total = price * packs;
            bool enough = gold >= total;
            bool close;
            float h = 175;
            Rect r = MapUi.Box(top, bottom, h, out close, 40);
            float y = TouchUi.V(r.position).y;
            Rect minus = TouchUi.R(30, y + 60, 24, 24), plus = TouchUi.R(126, y + 60, 24, 24);
            Rect buy = TouchUi.R(17, y + h - 52, 146, 24), shut = TouchUi.R(60, y + h - 24, 60, 17);
            if (Kit.Tap(41, minus))
            {
                packs = Mathf.Max(1, packs - 1);
            }
            if (Kit.Tap(42, plus))
            {
                if (Time.unscaledTime - plusHeldAt < 0.4f || plusHeldAt < 0)
                {
                    packs = Mathf.Min(max, packs + 1);
                }
                plusHeldAt = -1f;
            }
            // holding + keeps adding (every 0.15 s)
            if (Kit.Held(42))
            {
                if (plusHeldAt < 0)
                {
                    plusHeldAt = Time.unscaledTime;
                    plusRepeat = plusHeldAt + 0.4f;
                }
                else if (Kit.Paint && Time.unscaledTime >= plusRepeat)
                {
                    plusRepeat += 0.15f;
                    packs = Mathf.Min(max, packs + 1);
                }
            }
            else if (Kit.E.type != EventType.MouseUp)
            {
                plusHeldAt = -1f;
            }
            if (Kit.Tap(43, buy))
            {
                if (enough)
                {
                    if (MapUi.Buy(t, packs))
                    {
                        Haptics.Tap(Kit.Haptic);
                        Kit.Toast("Đã mua " + packs * pack + " " + MapUi.Name(t) + " (-" + TouchUi.Money(total) + " vàng)");
                        buying = -1;
                    }
                }
                else
                {
                    buying = -1;
                    sub = 1;
                }
            }
            if (Kit.Tap(44, shut) || close || Kit.Back())
            {
                buying = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint)
            {
                TouchUi.PixelText("MUA", 1, r.center.x, TouchUi.R(0, y + 12, 0, 0).y, 1, true);
                MapUi.Icon(t, TouchUi.R(17, y + 22, 30, 30));
                TouchUi.Label(TouchUi.R(52, y + 24, 104, 9), MapUi.Name(t), 7, TextAnchor.UpperLeft, Color.white, true);
                TouchUi.Label(TouchUi.R(52, y + 35, 104, 9), TouchUi.Money(price) + " vàng / gói " + pack + " cái · có " + MapUi.Owned(t), 5.75f, TextAnchor.UpperLeft, Kit.Muted);
                TouchUi.TextButton(minus, "-", Btn.Dark, Kit.Down(41), packs > 1);
                TouchUi.TextButton(plus, "+", Btn.Dark, Kit.Down(42), packs < max);
                TouchUi.PixelText(packs + " GÓI", 1, r.center.x, TouchUi.R(0, y + 72, 0, 0).y, 1, true);
                TouchUi.Label(TouchUi.R(17, y + 88, 146, 9), "= " + packs * pack + " " + MapUi.Name(t), 6.25f, TextAnchor.UpperCenter, Kit.Cream, true);
                TouchUi.Label(TouchUi.R(17, y + 99, 146, 9), enough ? "Còn lại sau khi mua: " + TouchUi.Money(gold - total) + " vàng" : "Bạn có " + TouchUi.Money(gold) + " vàng",
                    5.75f, TextAnchor.UpperCenter, enough ? Kit.Muted : Kit.Red);
                if (enough)
                {
                    Kit.FlatButton(buy, "MUA · " + TouchUi.Money(total) + " vàng", Btn.Gold, Kit.Down(43));
                }
                else
                {
                    Kit.FlatButton(buy, "+ VÀNG (thiếu " + TouchUi.Money(total - gold) + ")", Btn.Green, Kit.Down(43));
                }
                Kit.FlatButton(shut, "Đóng", Btn.Grey, Kit.Down(44));
            }
            Kit.Unblock();
        }

        float Gold(float y)
        {
            float k = TouchUi.K;
            // free chest
            Rect card = TouchUi.R(4, y, 172, 52);
            int left = ChestsLeft();
            long wait = ChestWait();
            bool vip = Rewards.Vip;
            Rect open = TouchUi.R(118, y + 28, 52, 18);
            bool can = left > 0 && wait == 0 && !Rewards.Busy;
            if (Kit.Tap(50, open) && can)
            {
                if (vip)
                {
                    ChestOpened();
                }
                else
                {
                    Rewards.Show("gold_chest", res =>
                    {
                        if (res == 1)
                        {
                            ChestOpened();
                        }
                        else
                        {
                            Kit.Toast(res == 0 ? "Cần xem hết quảng cáo để nhận vàng" : "Chưa có quảng cáo, thử lại sau ít phút");
                        }
                    });
                }
            }
            if (Kit.Paint)
            {
                TouchUi.Button(card, Btn.Green, false);
                TouchUi.Fill(new Rect(card.x + 2 * k, card.y + 2 * k, card.width - 4 * k, card.height - 4 * k), TouchUi.Hex(0x1F5A18));
                TouchUi.Sprite(TouchUi.Img("/r/g"), TouchUi.R(9, y + 8, 36, 38));
                TouchUi.PixelText("RƯƠNG MIỄN PHÍ", 1, TouchUi.R(50, 0, 0, 0).x, TouchUi.R(0, y + 10, 0, 0).y, 0, true);
                TouchUi.Label(TouchUi.R(50, y + 17, 122, 9), "+" + ChestGold + " vàng · còn " + left + "/" + ChestPerDay + " lượt hôm nay", 5.75f, TextAnchor.UpperLeft, Color.white, true);
                TouchUi.Label(TouchUi.R(50, y + 29, 66, 18), left > 0 ? "Mỗi lần mở cách nhau ít nhất 1 giờ" : "Lượt mới lúc 0:00", 5.25f, TextAnchor.UpperLeft, TouchUi.Hex(0xC8F0B0), false, true);
                if (left == 0)
                {
                    Kit.FlatButton(open, "Hết lượt", Btn.Grey, false, false);
                }
                else if (wait != 0)
                {
                    string s = wait < 0 ? "Chờ" : (wait / 60).ToString("00") + ":" + (wait % 60).ToString("00");
                    Kit.FlatButton(open, s, Btn.Grey, false, false);
                }
                else if (vip)
                {
                    Kit.FlatButton(open, "MỞ NGAY", Btn.Gold, Kit.Down(50));
                }
                else if (Rewards.Busy)
                {
                    Kit.FlatButton(open, "", Btn.Green, false);
                    Kit.Spinner(open.center);
                }
                else
                {
                    Kit.AdButton(open, "MỞ", Kit.Down(50));
                }
            }
            y += 52 + 8;
            // gold packs (Google Play prices, not connected yet)
            for (int i = 0; i < 4; i++)
            {
                Rect r = TouchUi.R(4 + (i % 2) * 88, y + (i / 2) * 89, 84, 85);
                if (Kit.Tap(60 + i, r))
                {
                    Kit.Toast("Mua bằng tiền thật sẽ mở khi kết nối Google Play");
                }
                if (!Kit.Paint)
                {
                    continue;
                }
                if (i == 2)
                {
                    TouchUi.Fill(new Rect(r.x - k, r.y - k, r.width + 2 * k, r.height + 2 * k), Kit.Gold);
                }
                TouchUi.Panel(r);
                for (int b = 0; b <= i; b++)
                {
                    float bx = r.center.x - (i + 1) * 9 * k + b * 18 * k;
                    TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(bx, r.y + 12 * k - (b % 2) * 3 * k, 18 * k, 18 * k));
                }
                TouchUi.PixelText(TouchUi.Money(PackGold[i]), 1, r.center.x, r.y + 42 * k, 1, true);
                TouchUi.Label(new Rect(r.x, r.y + 49 * k, r.width, 9 * k), "vàng", 5.75f, TextAnchor.UpperCenter, Kit.Cream);
                if (PackTag[i].Length > 0)
                {
                    Rect tag = new Rect(r.x + 3 * k, r.y + 3 * k, (TouchUi.TextWidth(PackTag[i]) + 6) * k, 9 * k);
                    TouchUi.Fill(tag, i == 1 ? TouchUi.Hex(0x2F8A22) : (i == 2 ? TouchUi.Hex(0xD0501A) : TouchUi.Hex(0x7A3A9A)));
                    TouchUi.Label(tag, PackTag[i], 5.25f, TextAnchor.MiddleCenter, Color.white, true, false, false);
                }
                Rect pb = new Rect(r.x + 8 * k, r.yMax - 20 * k, r.width - 16 * k, 15 * k);
                Kit.FlatButton(pb, "— đ", Btn.Grey, false, false);
            }
            y += 2 * 89 + 2;
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(4, y, 172, 18), "Vàng dùng chung cho mọi nhân vật · giá theo Google Play (chưa kết nối)", 5.5f, TextAnchor.UpperCenter, Kit.Muted, false, true);
            }
            return y + 22;
        }

        static bool Starter
        {
            get { return PlayerPrefs.GetInt("l12.starterBought", 0) == 1; }
        }

        float Offers(float y)
        {
            float k = TouchUi.K;
            // starter pack
            Rect card = TouchUi.R(4, y, 172, 117);
            if (Kit.Paint)
            {
                Kit.Box(card, TouchUi.Hex(0x6E2428));
                TouchUi.PixelText("GÓI KHỞI ĐẦU", 1, TouchUi.R(10, 0, 0, 0).x, TouchUi.R(0, y + 11, 0, 0).y, 0, true);
                Rect tag = TouchUi.R(132, y + 5, 40, 10);
                TouchUi.Fill(tag, Kit.Gold);
                TouchUi.Label(tag, "CHỈ 1 LẦN", 5.25f, TextAnchor.MiddleCenter, TouchUi.Hex(0x3A1000), true, false, false);
                string[] what = {"3.000 vàng", "Long Thần Kiếm x3", "Nhân Sâm x3", "Đầy mạng"};
                for (int i = 0; i < 4; i++)
                {
                    Rect cell = TouchUi.R(8 + i * 41.5f, y + 22, 39, 44);
                    Kit.Box(cell, TouchUi.Hex(0x3A1418));
                    Rect ic = new Rect(cell.center.x - 10 * k, cell.y + 4 * k, 20 * k, 20 * k);
                    if (i == 0)
                    {
                        TouchUi.Sprite(TouchUi.Img("/gold"), ic);
                    }
                    else if (i == 3)
                    {
                        TouchUi.Sprite(TouchUi.Img("/m/ih"), new Rect(ic.x + 3 * k, ic.y + 3 * k, 14 * k, 14 * k));
                    }
                    else
                    {
                        TouchUi.Item(i - 1, ic);
                    }
                    TouchUi.Label(new Rect(cell.x + k, cell.y + 26 * k, cell.width - 2 * k, 16 * k), what[i], 5.25f, TextAnchor.UpperCenter, Kit.Cream, true, true);
                }
                TouchUi.Label(TouchUi.R(8, y + 70, 164, 9), "Trị giá 6.000 vàng và đầy mạng", 6, TextAnchor.UpperCenter, TouchUi.Hex(0xFFE39A), true);
                Rect pb = TouchUi.R(40, y + 84, 100, 22);
                if (Starter)
                {
                    Kit.FlatButton(pb, "ĐÃ MUA", Btn.Green, false, true);
                }
                else
                {
                    Kit.FlatButton(pb, "— đ", Btn.Grey, false, false);
                }
            }
            y += 117 + 8;
            // VIP
            Rect vipCard = TouchUi.R(4, y, 172, 128);
            if (Kit.Paint)
            {
                Kit.Box(vipCard, TouchUi.Hex(0x463214));
                TouchUi.Item(5, TouchUi.R(10, y + 8, 26, 26));
                TouchUi.PixelText("VIP TRỌN ĐỜI", 1, TouchUi.R(42, 0, 0, 0).x, TouchUi.R(0, y + 14, 0, 0).y, 0, true);
                TouchUi.Label(TouchUi.R(42, y + 22, 130, 9), "Mua 1 lần, dùng mãi, cho mọi nhân vật", 5.75f, TextAnchor.UpperLeft, Kit.Cream);
                string[] perks =
                {
                    "Nhận thưởng quảng cáo ngay, không phải xem", "Ô vật phẩm thứ 4 khi chuẩn bị xuất trận", "Tối đa 6 mạng (thường 5)",
                    "Tạo nhân vật mới không cần xem quảng cáo"
                };
                for (int i = 0; i < perks.Length; i++)
                {
                    Kit.Check(TouchUi.R(12, 0, 0, 0).x, TouchUi.R(0, y + 38 + i * 13, 0, 0).y, Kit.Green);
                    TouchUi.Label(TouchUi.R(22, y + 37 + i * 13, 150, 9), perks[i], 5.75f, TextAnchor.UpperLeft, Color.white);
                }
                Rect pb = TouchUi.R(40, y + 96, 100, 22);
                if (Rewards.Vip)
                {
                    Kit.FlatButton(pb, "ĐÃ KÍCH HOẠT", Btn.Gold, false);
                }
                else
                {
                    Kit.FlatButton(pb, "— đ", Btn.Grey, false, false);
                }
            }
            y += 128 + 8;
            Rect restore = TouchUi.R(40, y, 100, 12);
            if (Kit.Tap(70, restore))
            {
                Kit.Toast("Không có giao dịch nào để khôi phục");
            }
            if (Kit.Paint)
            {
                TouchUi.Label(restore, "Khôi phục giao dịch", 6.25f, TextAnchor.MiddleCenter, TouchUi.Hex(0x8CC4FF), true);
                float w = TouchUi.TextWidth("Khôi phục giao dịch") * k;
                TouchUi.Fill(new Rect(restore.center.x - w / 2, restore.yMax - k, w, k), TouchUi.Hex(0x8CC4FF));
                TouchUi.Label(TouchUi.R(4, y + 16, 172, 18), "Mua bằng tiền thật sẽ mở khi game kết nối Google Play", 5.5f, TextAnchor.UpperCenter, Kit.Muted, false, true);
            }
            return y + 36;
        }
    }

    /// <summary>The shop as a page of its own (the + next to the gold on the menu).</summary>
    public sealed class ShopPage : IPage
    {
        readonly TouchShopTab shop = new TouchShopTab();

        public ShopPage(int sub)
        {
            shop.sub = sub;
        }

        public bool OnGUI(Event e)
        {
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0A121E));
            }
            Rect area = TouchUi.R(0, 26, 180, Kit.H - 26);
            Kit.Clip(new Rect(0, area.y, Gx.Width, area.height));
            shop.OnGUI(null, 26, Kit.H);
            Kit.Unclip();
            bool close = false;
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, TouchUi.R(0, 26, 0, 0).y), TouchUi.Hex(0x0A070A));
            }
            if (Kit.TitleBar("CỬA HÀNG"))
            {
                close = true;
            }
            if (Kit.Back())
            {
                e.Use();
                if (!shop.Back())
                {
                    close = true;
                }
            }
            Kit.DrawToast(Kit.H - 30);
            return !close;
        }
    }
}
