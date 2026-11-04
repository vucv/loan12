// Screen 6 of the touch redesign: the bag tab of the map (plan/thiet-ke-ban-do.md 4), in place of the
// game's "T.Tin" sheet.
//   hero card: portrait, name, element · level, experience bar, HP and attack (base + events)
//   tap the hero card: its introduction in a popup centred on the screen (HeroGuide)
//   the 3 skills (tap: description with the real damage)
//   the 5 items (shared bag; tap: detail box with a line worked out for this hero, buy one more pack)
//   the relics of this character (old saves only: quest boxes now hold items)
using UnityEngine;

namespace Loan12.UnityPort
{
    /// <summary>What each hero is good at and how to play it, by element (all heroes share HP and attack).</summary>
    public static class HeroGuide
    {
        static readonly string[][] Strengths =
        {
            new[]
            {
                "Sát thương diện rộng mạnh nhất: Lửa Địa Ngục phá 2 vùng 4x4",
                "Phá nhiều ô một lúc, dễ tạo chuỗi nổ liên hoàn",
                "Hạ gục nhanh, hợp với lối đánh tấn công"
            },
            new[]
            {
                "Khiên Sét hóa giải mọi đòn tấn công trong 6 lượt",
                "Chuỗi Sét chỉ cần 1 ô năng lượng, phá ô rải rác",
                "Cân bằng giữa công và thủ, an toàn khi gặp địch mạnh"
            },
            new[]
            {
                "Tự hồi máu: Cam Lộ Thủy hồi 20% và hút máu từ trái tim",
                "Đóng băng địch 2 lượt, làm mất năng lượng của địch",
                "Bền bỉ nhất, hợp đánh lâu"
            }
        };

        static readonly string[][] Tips =
        {
            new[]
            {
                "Gom đủ 3 ô năng lượng rồi tung Lửa Địa Ngục khi địch còn nhiều máu",
                "Dùng Quả Cầu Lửa dọn chỗ bàn cờ đang bí nước",
                "Không tự hồi máu được: mang vật phẩm hồi máu khi đánh trùm"
            },
            new[]
            {
                "Bật Khiên Sét khi địch sắp đầy năng lượng để chặn tuyệt chiêu",
                "Trong 6 lượt có khiên, tấn công dồn dập không cần phòng thủ",
                "Để dành Sấm Sét để kết liễu"
            },
            new[]
            {
                "Đợi bàn cờ có nhiều trái tim rồi mới dùng Cam Lộ Thủy",
                "Tung Băng Phong khi địch sắp ra chiêu, rồi tranh thủ 2 lượt bị băng",
                "Mũi Tên Băng làm địch chậm tích năng lượng"
            }
        };

        const float W = 160, Size = 6f, Bullet = 5.75f;

        /// <summary>
        /// The popup centred on the screen: portrait, name, element, the game's line, strengths, tips.
        /// True when it closes.
        /// </summary>
        public static bool Box(int hero)
        {
            int el = Mathf.Clamp(C_PortScreens.heroElement(hero), 0, 2);
            string desc = Desc(hero);
            float lh = TouchUi.LineHeight(Size), bh = TouchUi.LineHeight(Bullet);
            int lines = TouchUi.Lines(desc, W - 14, Size);
            float h = 48 + lines * lh + 8 + 11 + Height(Strengths[el], bh) + 6 + 11 + Height(Tips[el], bh) + 8 + 26;
            bool close;
            Rect r = MapUi.Box(0, Kit.H, h, out close, 60);
            float y = TouchUi.V(r.position).y;
            h = TouchUi.V(new Vector2(0, r.yMax)).y - y;
            Rect shut = TouchUi.R(60, y + h - 26, 60, 18);
            bool done = false;
            if (Kit.Tap(61, shut) || close || Kit.Back())
            {
                done = true;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint)
            {
                TouchUi.Portrait(hero, TouchUi.R(17, y + 10, 30, 30));
                TouchUi.PixelText(Kit.Cut(C_PortMap.heroName(), 12), 2, TouchUi.R(53, 0, 0, 0).x, TouchUi.R(0, y + 13, 0, 0).y, 0);
                TouchUi.Label(TouchUi.R(53, y + 26, 100, 9), "Hệ " + Kit.ElementName(el) + " · Cấp " + C_PortMap.heroLevel(), 6.25f, TextAnchor.UpperLeft, Kit.Cream, true);
                float cy = y + 48;
                TouchUi.Label(TouchUi.R(17, cy, W - 14, lines * lh), desc, Size, TextAnchor.UpperLeft, Kit.Cream, false, true);
                cy += lines * lh + 8;
                cy = List(cy, "ƯU ĐIỂM", Kit.Green, Strengths[el], bh);
                cy += 6;
                List(cy, "MẸO KHI CHƠI", Kit.Gold, Tips[el], bh);
                Kit.FlatButton(shut, "Đóng", Btn.Grey, Kit.Down(61));
            }
            Kit.Unblock();
            return done;
        }

        static string Desc(int hero)
        {
            try
            {
                return C_PortScreens.heroDesc(hero);
            }
            catch (System.Exception)
            {
                return "";
            }
        }

        static float Height(string[] list, float bh)
        {
            float h = 0;
            foreach (string s in list)
            {
                h += TouchUi.Lines(s, W - 22, Bullet) * bh + 2;
            }
            return h;
        }

        static float List(float y, string title, Color c, string[] list, float bh)
        {
            float k = TouchUi.K;
            TouchUi.Label(TouchUi.R(17, y, W - 14, 9), title, 6, TextAnchor.UpperLeft, c, true);
            y += 11;
            foreach (string s in list)
            {
                int n = TouchUi.Lines(s, W - 22, Bullet);
                Rect dot = TouchUi.R(19, y + bh / 2 - 1, 2.5f, 2.5f);
                TouchUi.Fill(dot, c);
                TouchUi.Label(TouchUi.R(25, y, W - 22, n * bh), s, Bullet, TextAnchor.UpperLeft, Color.white, false, true);
                y += n * bh + 2;
            }
            return y;
        }
    }

    /// <summary>Pieces shared by the map tabs: centred box, close cross, tab scrolling.</summary>
    public static class MapUi
    {
        /// <summary>A box 162 wide centred between top and bottom (plan 2.7); taps outside close it.</summary>
        public static Rect Box(float top, float bottom, float h, out bool close, int id)
        {
            h = Mathf.Min(h, bottom - top - 8);
            float y = Mathf.Round(top + (bottom - top - h) / 2);
            Rect r = TouchUi.R(9, y, 162, h);
            Kit.Modal(r);
            Rect x = TouchUi.R(157, y + 4, 11, 11);
            close = Kit.Tap(id, x) || Kit.Outside();
            if (Kit.Paint)
            {
                TouchUi.Dim(0.65f);
                TouchUi.Panel(r);
                Cross(x);
            }
            return r;
        }

        public static void Cross(Rect close)
        {
            float k = TouchUi.K;
            TouchUi.Fill(close, TouchUi.Outline);
            TouchUi.Fill(new Rect(close.x + k, close.y + k, close.width - 2 * k, close.height - 2 * k), TouchUi.Hex(0x3A1838));
            for (int i = 2; i < 9; i++)
            {
                TouchUi.Fill(new Rect(close.x + i * k, close.y + i * k, k, k), Kit.Cream);
                TouchUi.Fill(new Rect(close.xMax - (i + 1) * k, close.y + i * k, k, k), Kit.Cream);
            }
        }

        /// <summary>Vertical scrolling of a tab's content; returns the new offset.</summary>
        public static float Scroll(float scroll, float contentH, float room)
        {
            Vector2 d = Kit.Drag();
            if (d.y != 0)
            {
                scroll -= d.y;
            }
            return Mathf.Clamp(scroll, 0, Mathf.Max(0, contentH - room));
        }

        /// <summary>Section heading on the left, an optional note on the right.</summary>
        public static void Heading(float y, string left, string right)
        {
            if (!Kit.Paint)
            {
                return;
            }
            TouchUi.PixelText(left, 1, TouchUi.R(6, 0, 0, 0).x, TouchUi.R(0, y + 5, 0, 0).y, 0, true);
            if (!string.IsNullOrEmpty(right))
            {
                TouchUi.Label(TouchUi.R(90, y, 84, 10), right, 5.75f, TextAnchor.MiddleRight, Kit.Muted);
            }
        }

        /// <summary>The game's experience bar (m/lv frame) filled to f.</summary>
        public static void ExpBar(Rect r, float f)
        {
            float k = TouchUi.K;
            TouchUi.Fill(r, TouchUi.Outline);
            TouchUi.Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), TouchUi.Hex(0x2A1236));
            TouchUi.Fill(new Rect(r.x + k, r.y + k, (r.width - 2 * k) * Mathf.Clamp01(f), r.height - 2 * k), TouchUi.Hex(0xB54BC0));
            TouchUi.Fill(new Rect(r.x + k, r.y + k, (r.width - 2 * k) * Mathf.Clamp01(f), k), TouchUi.Hex(0xE89AF0));
        }

        /// <summary>Item types of the bag in the order of the plan (no Ngân Lượng: that is gold).</summary>
        public static readonly int[] Items = {0, 1, 3, 4, 5};

        /// <summary>The bag and the shop: the game's items, then Bình Hồi Mạng of the touch port.</summary>
        public static readonly int[] BagItems = {0, 1, 3, 4, 5, Refill.Type};

        public static string Name(int t)
        {
            return t == Refill.Type ? "Bình Hồi Mạng" : C_PortUi.itemName(t);
        }

        public static string Desc(int t)
        {
            return t == Refill.Type
                ? "Hồi đầy mạng chơi (" + C_PortScreens.maxLives() + " mạng). Dùng ngoài trận, ở túi đồ hoặc khi hết mạng."
                : C_PortUi.itemDesc(t);
        }

        public static int Owned(int t)
        {
            return t == Refill.Type ? Refill.Count : C_PortUi.owned(t);
        }

        public static int Price(int t)
        {
            return t == Refill.Type ? Refill.Price : C_PortUi.price(t);
        }

        public static int Pack(int t)
        {
            return t == Refill.Type ? 1 : C_PortUi.packSize(t);
        }

        public static bool Buy(int t, int packs)
        {
            return t == Refill.Type ? Refill.Buy(packs) : C_PortMap.buy(t, packs);
        }

        public static void Icon(int t, Rect r, float dim = 1f)
        {
            if (t == Refill.Type)
            {
                Refill.Icon(r, dim);
            }
            else
            {
                TouchUi.Item(t, r, dim);
            }
        }

        public static string Limit(int t)
        {
            switch (t)
            {
                case 0:
                case 3:
                    return "Dùng trong 1 lượt";
                case 1:
                    return "1 lần mỗi trận";
                case 4:
                    return "1 lần mỗi lượt";
                case Refill.Type:
                    return "Dùng ngoài trận";
                default:
                    return "Tự dùng khi bắt đầu trận";
            }
        }

        /// <summary>The line worked out for the hero (green in the detail box).</summary>
        public static string ForHero(int t)
        {
            int d = C_PortMap.heroAttack(), b = C_PortMap.heroMaxHp();
            switch (t)
            {
                case 0:
                    return "Tấn công lượt đó: " + d + " thành " + 2 * d;
                case 1:
                    return "Máu trận đó: " + b + " thành " + 2 * b;
                case 3:
                    return "Chặn 3/4 sát thương của lượt địch";
                case 4:
                    return "Với máu " + b + " của bạn: +" + b / 10 + " máu";
                case Refill.Type:
                    return "Mạng chơi: " + C_PortScreens.lives() + "/" + C_PortScreens.maxLives() + " thành " + C_PortScreens.maxLives() + "/" + C_PortScreens.maxLives();
                default:
                    return "Bạn luôn được đi trước";
            }
        }
    }

    public sealed class TouchBagTab : IMapTab
    {
        float scroll, contentH;
        public int detail = -1; // public: the screenshot tour opens the boxes
        public int skillDetail = -1;
        public bool guide;

        public bool Back()
        {
            if (detail >= 0 || skillDetail >= 0 || guide)
            {
                detail = -1;
                skillDetail = -1;
                guide = false;
                return true;
            }
            return false;
        }

        public void OnGUI(TouchMapView map, float top, float bottom)
        {
            float k = TouchUi.K;
            bool popup = detail >= 0 || skillDetail >= 0 || guide;
            if (popup)
            {
                Kit.Modal(new Rect(0, 0, 0, 0));
            }
            else
            {
                scroll = MapUi.Scroll(scroll, contentH, bottom - top);
            }
            float y = top + 4 - scroll;

            // hero card
            int hero = C_PortMap.hero(), lv = C_PortMap.heroLevel();
            // tap the card: the hero's introduction (popup)
            if (Kit.Tap(5, TouchUi.R(4, y, 172, 96)))
            {
                guide = true;
            }
            if (Kit.Paint)
            {
                Rect card = TouchUi.R(4, y, 172, 96);
                TouchUi.Panel(card);
                TouchUi.Label(TouchUi.R(10, y + 84, 148, 9), "Thông tin nhân vật", 5.25f, TextAnchor.UpperRight, TouchUi.Hex(0x8CC4FF), true);
                TouchUi.Portrait(hero, TouchUi.R(10, y + 8, 32, 32));
                TouchUi.PixelText(Kit.Cut(C_PortMap.heroName(), 12), 2, TouchUi.R(48, 0, 0, 0).x, TouchUi.R(0, y + 7, 0, 0).y, 0);
                TouchUi.Label(TouchUi.R(48, y + 24, 120, 9), "Hệ " + Kit.ElementName(C_PortScreens.heroElement(hero)) + " · Cấp " + lv, 6.75f, TextAnchor.UpperLeft, Kit.Cream, true);
                int exp = C_PortMap.heroExp(), lo = C_PortMap.heroExpFor(lv), hi = C_PortMap.heroExpFor(lv + 1);
                MapUi.ExpBar(TouchUi.R(48, y + 35, 120, 6), hi > lo ? (exp - lo) / (float) (hi - lo) : 1);
                TouchUi.Label(TouchUi.R(48, y + 42, 124, 9), "Kinh nghiệm " + TouchUi.Money(exp) + " / " + TouchUi.Money(hi) + " (lên cấp " + (lv + 1) + ")", 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                Stat(y + 56, 10, "/heart", "Máu", C_PortMap.heroMaxHp(), C_PortMap.heroBonusHp());
                Stat(y + 56, 92, "/sword", "Tấn công", C_PortMap.heroAttack(), C_PortMap.heroBonusAttack());
            }
            y += 96 + 8;

            // skills
            MapUi.Heading(y, "TUYỆT CHIÊU", "Chạm để xem");
            y += 12;
            int skills = Mathf.Min(3, C_PortMap.heroSkillCount());
            for (int i = 0; i < skills; i++)
            {
                Rect r = TouchUi.R(4 + i * 58, y, 54, 34);
                int id = C_PortMap.heroSkill(i);
                if (Kit.Tap(10 + i, r))
                {
                    skillDetail = i;
                }
                if (Kit.Paint)
                {
                    Kit.Box(r, Kit.Down(10 + i) ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x3A1838));
                    TouchUi.Sprite(TouchUi.Img(TouchHeroView.SkillIcon(id)), new Rect(r.x + 4 * k, r.y + 4 * k, 15 * k, 15 * k));
                    TouchUi.Label(new Rect(r.x + 21 * k, r.y + 3 * k, 32 * k, 18 * k), C_PortScreens.skillTitle(id), 5.5f, TextAnchor.UpperLeft, Kit.Cream, true, true);
                    TouchUi.Label(new Rect(r.x + 3 * k, r.y + 22 * k, 50 * k, 9 * k), (i + 1) + " ô năng lượng", 5.25f, TextAnchor.UpperLeft, TouchUi.Hex(0x8CC4FF));
                }
            }
            y += 34 + 8;

            // items
            MapUi.Heading(y, "VẬT PHẨM", "Dùng trong trận");
            y += 12;
            int cells = MapUi.BagItems.Length + 1;
            for (int i = 0; i < cells; i++)
            {
                Rect r = TouchUi.R(4 + (i % 3) * 58, y + (i / 3) * 50, 54, 46);
                if (Kit.Tap(20 + i, r))
                {
                    if (i < MapUi.BagItems.Length)
                    {
                        detail = MapUi.BagItems[i];
                    }
                    else
                    {
                        map.OpenTab(TouchMapView.TabShop);
                        ((TouchShopTab) map.tabs[TouchMapView.TabShop]).sub = 0;
                    }
                }
                if (!Kit.Paint)
                {
                    continue;
                }
                if (i == MapUi.BagItems.Length)
                {
                    TouchUi.Button(r, Btn.Green, Kit.Down(20 + i));
                    TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(r.center.x - 9 * k, r.y + 5 * k, 18 * k, 18 * k));
                    TouchUi.Label(new Rect(r.x, r.y + 26 * k, r.width, 9 * k), "Cửa hàng", 6, TextAnchor.UpperCenter, Color.white, true);
                    TouchUi.Label(new Rect(r.x, r.y + 35 * k, r.width, 9 * k), "mua thêm", 5.5f, TextAnchor.UpperCenter, Color.white);
                    continue;
                }
                int t = MapUi.BagItems[i], n = MapUi.Owned(t);
                Kit.Box(r, Kit.Down(20 + i) ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x3A1838));
                MapUi.Icon(t, new Rect(r.center.x - 12 * k, r.y + 3 * k, 24 * k, 24 * k), n > 0 ? 1f : 0.35f);
                Rect tag = new Rect(r.xMax - 17 * k, r.y + 2 * k, 15 * k, 9 * k);
                TouchUi.Fill(tag, n > 0 ? TouchUi.Hex(0x1A0C16) : TouchUi.Hex(0xA3201B));
                TouchUi.Label(tag, "x" + n, 5.5f, TextAnchor.MiddleCenter, Color.white, true, false, false);
                TouchUi.Label(new Rect(r.x, r.y + 28 * k, r.width, 9 * k), MapUi.Name(t), 5.5f, TextAnchor.UpperCenter, Kit.Cream, true);
                string note = n == 0 ? "Hết · chạm để mua" : (t == 5 ? "tự dùng đầu trận" : (t == Refill.Type ? "hồi đầy mạng" : ""));
                if (note.Length > 0)
                {
                    TouchUi.Label(new Rect(r.x, r.y + 37 * k, r.width, 9 * k), note, 5, TextAnchor.UpperCenter, n == 0 ? Kit.Red : Kit.Muted);
                }
            }
            y += ((cells + 2) / 3) * 50 + 8;

            // relics of this character: only from an old save now (quest boxes hold items)
            int owned = 0;
            for (int i = 0; i < 4; i++)
            {
                if (C_PortMap.relicOwned(i))
                {
                    owned++;
                }
            }
            if (owned > 0)
            {
                MapUi.Heading(y, "BẢO VẬT", owned + "/4");
                y += 12;
            }
            for (int i = 0; i < 4 && owned > 0; i++)
            {
                if (!Kit.Paint)
                {
                    break;
                }
                bool have = C_PortMap.relicOwned(i);
                Rect r = TouchUi.R(4 + i * 43.5f, y, 40, 46);
                Kit.Box(r, have ? TouchUi.Hex(0x4A3010) : TouchUi.Hex(0x241A24));
                if (have)
                {
                    TouchUi.Fill(new Rect(r.x, r.y, r.width, k), Kit.Gold);
                }
                Color old = Gx.color;
                Gx.color = have ? old : old * new Color(0.3f, 0.3f, 0.32f, 1f);
                TouchUi.Sprite(TouchUi.Img("/r/" + (i + 1)), new Rect(r.center.x - 12 * k, r.y + 3 * k, 24 * k, 24 * k));
                Gx.color = old;
                if (!have)
                {
                    TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(r.center.x - 5 * k, r.y + 9 * k, 10 * k, 10 * k));
                }
                TouchUi.Label(new Rect(r.x + k, r.y + 29 * k, r.width - 2 * k, 16 * k), C_PortMap.relicName(i), 5.25f, TextAnchor.UpperCenter, have ? Kit.Cream : Kit.Muted, false, true);
            }
            y += owned > 0 ? 46 + 10 : 0;
            contentH = y + scroll - top;
            Kit.Unblock();

            if (detail >= 0)
            {
                ItemBox(map, top, bottom);
            }
            else if (skillDetail >= 0)
            {
                SkillBox(top, bottom);
            }
            else if (guide && HeroGuide.Box(hero))
            {
                guide = false;
            }
        }

        static void Stat(float y, float x, string icon, string name, int value, int bonus)
        {
            float k = TouchUi.K;
            TouchUi.Sprite(TouchUi.Img(icon), TouchUi.R(x, y, 16, 16));
            TouchUi.Label(TouchUi.R(x + 19, y - 1, 60, 9), name, 5.75f, TextAnchor.UpperLeft, Kit.Muted);
            TouchUi.Label(TouchUi.R(x + 19, y + 7, 60, 10), TouchUi.Money(value), 7, TextAnchor.UpperLeft, Color.white, true);
            if (bonus > 0)
            {
                TouchUi.Label(TouchUi.R(x, y + 19, 80, 9), "gốc " + (value - bonus) + " · sự kiện +" + bonus, 5.25f, TextAnchor.UpperLeft, Kit.Green);
            }
        }

        void ItemBox(TouchMapView map, float top, float bottom)
        {
            int t = detail;
            bool refill = t == Refill.Type;
            string desc = MapUi.Desc(t);
            int lines = TouchUi.Lines(desc, 146);
            float h = 70 + lines * UiFont.LineHeight + (refill ? 76 : 50);
            bool close;
            Rect r = MapUi.Box(top, bottom, h, out close, 40);
            float y = TouchUi.V(r.position).y;
            int price = MapUi.Price(t), pack = MapUi.Pack(t), gold = C_PortScreens.gold();
            bool enough = price > 0 && gold >= price;
            Rect use = TouchUi.R(17, y + h - 78, 146, 22);
            Rect buy = TouchUi.R(17, y + h - 52, 146, 22), shut = TouchUi.R(60, y + h - 26, 60, 18);
            bool canUse = refill && Refill.Count > 0 && Refill.Needed;
            if (refill && Kit.Tap(43, use) && canUse)
            {
                Refill.Use();
                Kit.Toast("Đã hồi đầy " + C_PortScreens.maxLives() + " mạng");
            }
            if (Kit.Tap(41, buy))
            {
                if (enough)
                {
                    if (MapUi.Buy(t, 1))
                    {
                        Haptics.Tap(Kit.Haptic);
                        Kit.Toast("Đã mua " + pack + " " + MapUi.Name(t) + " (-" + TouchUi.Money(price) + " vàng)");
                    }
                }
                else
                {
                    detail = -1;
                    map.OpenTab(TouchMapView.TabShop);
                    ((TouchShopTab) map.tabs[TouchMapView.TabShop]).sub = 1;
                    Kit.Unblock();
                    return;
                }
            }
            if (Kit.Tap(42, shut) || close || Kit.Back())
            {
                detail = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint)
            {
                MapUi.Icon(t, TouchUi.R(17, y + 10, 40, 40));
                TouchUi.Label(TouchUi.R(62, y + 12, 94, 10), MapUi.Name(t), 7, TextAnchor.UpperLeft, Color.white, true);
                TouchUi.Label(TouchUi.R(62, y + 24, 94, 9), "Đang có: " + MapUi.Owned(t), 6.25f, TextAnchor.UpperLeft, Kit.Gold, true);
                TouchUi.Label(TouchUi.R(62, y + 34, 94, 9), MapUi.Limit(t), 5.75f, TextAnchor.UpperLeft, Kit.Muted);
                TouchUi.Label(TouchUi.R(17, y + 56, 146, lines * UiFont.LineHeight), desc, 6, TextAnchor.UpperLeft, Kit.Cream, false, true);
                TouchUi.Label(TouchUi.R(17, y + 58 + lines * UiFont.LineHeight, 146, 9), MapUi.ForHero(t), 6, TextAnchor.UpperLeft, Kit.Green, true);
                if (refill)
                {
                    string ut = Refill.Count <= 0 ? "Chưa có Bình Hồi Mạng" : (!Refill.Needed ? "Mạng đang đầy" : "DÙNG · hồi đầy mạng");
                    Kit.FlatButton(use, ut, Btn.Red, Kit.Down(43), canUse);
                }
                if (enough)
                {
                    Kit.FlatButton(buy, "MUA THÊM " + pack + " · " + TouchUi.Money(price) + " vàng", Btn.Gold, Kit.Down(41));
                }
                else
                {
                    Kit.FlatButton(buy, "Thiếu " + TouchUi.Money(Mathf.Max(0, price - gold)) + " vàng · nạp thêm", Btn.Grey, Kit.Down(41));
                }
                Kit.FlatButton(shut, "Đóng", Btn.Grey, Kit.Down(42));
            }
            Kit.Unblock();
        }

        void SkillBox(float top, float bottom)
        {
            int id = C_PortMap.heroSkill(skillDetail);
            string text = C_PortScreens.skillText(id, C_PortMap.heroAttack());
            int lines = TouchUi.Lines(text, 146);
            float h = 52 + lines * UiFont.LineHeight + 30;
            bool close;
            Rect r = MapUi.Box(top, bottom, h, out close, 50);
            float y = TouchUi.V(r.position).y;
            Rect shut = TouchUi.R(60, y + h - 26, 60, 18);
            if (Kit.Tap(51, shut) || close || Kit.Back())
            {
                skillDetail = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint)
            {
                TouchUi.Sprite(TouchUi.Img(TouchHeroView.SkillIcon(id)), TouchUi.R(17, y + 10, 30, 30));
                TouchUi.Label(TouchUi.R(52, y + 12, 100, 10), C_PortScreens.skillTitle(id), 7, TextAnchor.UpperLeft, Color.white, true);
                TouchUi.Label(TouchUi.R(52, y + 25, 100, 9), "Hệ " + Kit.ElementName(id / 3) + " · " + (skillDetail + 1) + " ô năng lượng", 6, TextAnchor.UpperLeft, TouchUi.Hex(0x8CC4FF));
                TouchUi.Label(TouchUi.R(17, y + 46, 146, lines * UiFont.LineHeight), text, 6, TextAnchor.UpperLeft, Kit.Cream, false, true);
                Kit.FlatButton(shut, "Đóng", Btn.Grey, Kit.Down(51));
            }
            Kit.Unblock();
        }
    }
}
