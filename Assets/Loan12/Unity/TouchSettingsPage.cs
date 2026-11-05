// Screen 12 of the touch redesign: settings (plan/thiet-ke-cai-dat.md 2).
//
// Opened from the menu (⚙), from the map (⚙ / Back, in place of the game's "back to the menu?" box) and
// from the battle's pause box (sound only). Groups of 21-px rows that scroll when they do not fit;
// every change is saved at once. Sub pages: game rules (2.4) and credits (2.5).
using System;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchSettingsPage : IPage
    {
        public enum Where
        {
            Menu,
            Map,
            Battle
        }

        const float Row = 21;
        const string Version = "1.0.0";

        readonly Where where;
        IPage sub;
        float scroll;
        float contentH;
        int versionTaps;
        float versionTapAt;
        bool showId;

        /// <summary>Map only: "MENU", "Đổi nhân vật", "Hướng dẫn bản đồ".</summary>
        public Action OnMenu, OnSwitchCharacter, OnMapTutorial;

        public TouchSettingsPage(Where w)
        {
            where = w;
        }

        float Top
        {
            get { return 26; }
        }

        float Bottom
        {
            get { return where == Where.Map ? Kit.H - 42 : Kit.H; }
        }

        public bool OnGUI(Event e)
        {
            if (sub != null)
            {
                if (!sub.OnGUI(e))
                {
                    sub = null;
                }
                return true;
            }
            J2MEGameHost host = J2MEGameHost.Instance;
            bool close = false;
            if (langOpen)
            {
                Kit.Modal(new Rect(0, 0, 0, 0)); // the language box is open: nothing underneath takes taps
            }

            // scrolling
            float room = Bottom - Top;
            Vector2 d = Kit.Drag();
            if (d.y != 0 && contentH > room)
            {
                scroll = Mathf.Clamp(scroll - d.y, 0, contentH - room);
            }
            scroll = Mathf.Clamp(scroll, 0, Mathf.Max(0, contentH - room));
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x141014));
            }

            float y = Top + 4 - scroll;
            // rows scroll under the title bar (and the map's bottom bar): their taps stay between them
            Rect area = TouchUi.R(0, Top, 180, Bottom - Top);
            Kit.Clip(new Rect(0, area.y, Gx.Width, area.height));
            // 1. sound
            y = GroupTitle("ÂM THANH", y);
            int rows = 5;
            Rect g = TouchUi.R(6, y, 168, rows * Row + 2);
            if (Kit.Paint)
            {
                Kit.GroupBox(g);
            }
            bool sound = host == null || host.SoundOn;
            if (RowTap(101, y, "Âm thanh", "Nhạc và tiếng trong game", true) && host != null)
            {
                host.SoundOn = !host.SoundOn;
            }
            if (Kit.Paint)
            {
                Kit.Switch(TouchUi.R(146, y + 5, 22, 11), sound);
            }
            y += Row;
            if (Kit.Paint)
            {
                Divider(y);
                TouchUi.Label(TouchUi.R(14, y + 1, 50, Row - 2), "Âm lượng", 7, TextAnchor.MiddleLeft, Kit.Cream, true);
            }
            int vol = host == null ? 80 : host.Volume;
            float v = Kit.Slider(102, TouchUi.R(54, y, 100, Row), TouchUi.R(60, y + 1, 88, Row - 2), vol / 100f, sound);
            int nv = Mathf.RoundToInt(v * 20) * 5;
            if (host != null && nv != vol)
            {
                host.Volume = nv;
            }
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(150, y + 1, 18, Row - 2), nv.ToString(), 7, TextAnchor.MiddleRight, sound ? Kit.Cream : Kit.Muted, true);
            }
            y += Row;
            if (Kit.Paint)
            {
                Divider(y);
            }
            bool vibrate = host == null || host.HapticsOn;
            if (RowTap(103, y, "Rung", "Khi trúng đòn, mua hàng, giữ để xoá", true) && host != null)
            {
                host.HapticsOn = !host.HapticsOn;
                Haptics.Tap(host.HapticsOn);
            }
            if (Kit.Paint)
            {
                Kit.Switch(TouchUi.R(146, y + 5, 22, 11), vibrate);
            }
            y += Row;
            if (Kit.Paint)
            {
                Divider(y);
            }
            bool hd = host == null || host.HdBoardOn;
            if (RowTap(105, y, "Đồ họa nét cao", "Ngọc và hiệu ứng trên bàn đấu", true) && host != null)
            {
                host.HdBoardOn = !host.HdBoardOn;
            }
            if (Kit.Paint)
            {
                Kit.Switch(TouchUi.R(146, y + 5, 22, 11), hd);
            }
            y += Row;
            if (Kit.Paint)
            {
                Divider(y);
            }
            // language of the interface and the story: a box with the 4 of them
            if (RowTap(104, y, "Ngôn ngữ", "Language", true))
            {
                langOpen = true;
            }
            RightText(y, Loc.Names[(int) Loc.Current]);
            y += Row + 2 + 6;

            if (where != Where.Battle)
            {
                // 2. game
                y = GroupTitle("TRÒ CHƠI", y);
                rows = where == Where.Map ? 3 : 2;
                g = TouchUi.R(6, y, 168, rows * Row + 2);
                if (Kit.Paint)
                {
                    Kit.GroupBox(g);
                }
                if (RowTap(201, y, "Luật chơi", "Xếp biểu tượng, vật phẩm, cấp độ", true))
                {
                    sub = new RulesPage();
                }
                Chevron(y);
                y += Row;
                bool onMap = where == Where.Map;
                if (Kit.Paint)
                {
                    Divider(y);
                }
                if (RowTap(202, y, "Hướng dẫn bản đồ", onMap ? "Xem lại 3 bước chỉ tay" : "Mở khi đang ở bản đồ", onMap) && OnMapTutorial != null)
                {
                    OnMapTutorial();
                    return false;
                }
                Chevron(y, onMap);
                y += Row;
                if (onMap)
                {
                    if (Kit.Paint)
                    {
                        Divider(y);
                    }
                    if (RowTap(203, y, "Đổi nhân vật", null, true) && OnSwitchCharacter != null)
                    {
                        OnSwitchCharacter();
                        return false;
                    }
                    RightText(y, Slots.Used + "/3");
                    y += Row;
                }
                y += 2 + 6;

                // 3. purchases and ads (only from the menu; the map keeps it short)
                if (where == Where.Menu)
                {
                    y = GroupTitle("MUA HÀNG VÀ QUẢNG CÁO", y);
                    g = TouchUi.R(6, y, 168, 2 * Row + 2);
                    if (Kit.Paint)
                    {
                        Kit.GroupBox(g);
                    }
                    if (RowTap(301, y, "VIP", null, !Rewards.Vip) && TouchMenuView.Instance != null)
                    {
                        TouchMenuView.Instance.OpenShop(2);
                        return false;
                    }
                    RightText(y, Rewards.Vip ? "Đang có" : "Chưa có", !Rewards.Vip);
                    y += Row;
                    if (Kit.Paint)
                    {
                        Divider(y);
                    }
                    if (RowTap(302, y, "Khôi phục giao dịch", null, true))
                    {
                        Kit.Toast("Không có giao dịch nào để khôi phục");
                    }
                    Chevron(y);
                    y += Row + 2 + 6;
                }

                // 4. support
                y = GroupTitle("HỖ TRỢ", y);
                bool full = where == Where.Menu;
                rows = full ? 4 : 2;
                g = TouchUi.R(6, y, 168, rows * Row + 2);
                if (Kit.Paint)
                {
                    Kit.GroupBox(g);
                }
                if (RowTap(401, y, "Đánh giá game", null, true))
                {
                    Store.Rate();
                }
                Chevron(y);
                y += Row;
                if (full)
                {
                    if (Kit.Paint)
                    {
                        Divider(y);
                    }
                    if (RowTap(402, y, "Chia sẻ với bạn bè", null, true))
                    {
                        Store.Share();
                    }
                    Chevron(y);
                    y += Row;
                    if (Kit.Paint)
                    {
                        Divider(y);
                    }
                    if (RowTap(403, y, "Chính sách quyền riêng tư", null, true))
                    {
                        Store.Privacy();
                    }
                    if (Kit.Paint)
                    {
                        ExternalIcon(y);
                    }
                    y += Row;
                }
                if (Kit.Paint)
                {
                    Divider(y);
                }
                if (RowTap(404, y, "Thông tin và tác giả", null, true))
                {
                    sub = new AboutPage();
                }
                Chevron(y);
                y += Row + 2 + 4;

                // version line (7 taps: anonymous player id for support)
                Rect vr = TouchUi.R(6, y, 168, 12);
                if (Kit.Tap(501, vr))
                {
                    versionTaps = Time.unscaledTime - versionTapAt < 1.2f ? versionTaps + 1 : 1;
                    versionTapAt = Time.unscaledTime;
                    if (versionTaps >= 7)
                    {
                        showId = true;
                    }
                }
                if (Kit.Paint)
                {
                    string line = showId ? "Mã người chơi: " + PlayerId() : "Phiên bản " + Application.version + " · Loạn 12 sứ quân · 2010 MicroGame";
                    TouchUi.Label(vr, line, 5.75f, TextAnchor.MiddleCenter, Kit.Muted, false, false, false);
                }
                y += 16;
            }
            contentH = y + scroll - Top;
            Kit.Unclip();

            // bars over the scrolled content
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, TouchUi.R(0, Top, 0, 0).y), TouchUi.Hex(0x0A070A));
            }
            if (Kit.TitleBar("CÀI ĐẶT"))
            {
                close = true;
            }
            if (where == Where.Map)
            {
                Rect bar = TouchUi.R(0, Kit.H - 42, 180, 42);
                Rect menu = TouchUi.R(6, Kit.H - 34, 79, 25), resume = TouchUi.R(88, Kit.H - 34, 86, 25);
                if (Kit.Tap(601, menu) && OnMenu != null)
                {
                    OnMenu();
                    return false;
                }
                if (Kit.Tap(602, resume))
                {
                    close = true;
                }
                if (Kit.Paint)
                {
                    TouchUi.Fill(new Rect(0, bar.y, Gx.Width, Gx.Height - bar.y), TouchUi.Hex(0x0A070A));
                    TouchUi.Fill(new Rect(0, bar.y, Gx.Width, TouchUi.K), TouchUi.GoldLine);
                    TouchUi.TextButton(menu, "MENU", Btn.Red, Kit.Down(601));
                    TouchUi.RedButton(resume, "TIẾP TỤC", true, Kit.Down(602));
                }
            }
            if (langOpen)
            {
                Kit.Unblock();
                LangBox();
            }
            else if (Kit.Back())
            {
                close = true;
                e.Use();
            }
            Kit.DrawToast(Kit.H - 60);
            return !close;
        }

        public bool langOpen;

        // each language: its own name, its English name, a code badge (no country flags: a language is
        // spoken in many countries)
        static readonly string[] LangSub =
            {"Vietnamese", "English", "Japanese", "Korean", "Indonesian", "Thai", "Chinese (Traditional)", "Portuguese", "Spanish", "German", "Russian"};

        static readonly int[] LangColor = {0xDA251D, 0x1F3C88, 0xBC002D, 0x0047A0, 0xCE1126, 0x2D2A4A, 0xB8860B, 0x009C3B, 0xC60B1E, 0x3A3A3A, 0x0039A6};

        void LangBox()
        {
            float k = TouchUi.K;
            const float cellH = 28, cellW = 72;
            int n = Loc.Names.Length, rows = (n + 1) / 2;
            float h = 30 + rows * (cellH + 4) + 6;
            bool close;
            Rect box = MapUi.Box(0, Kit.H, h, out close, 700);
            float y = TouchUi.V(box.position).y;
            for (int i = 0; i < n; i++)
            {
                Rect r = TouchUi.R(17 + (i % 2) * (cellW + 2), y + 28 + (i / 2) * (cellH + 4), cellW, cellH);
                bool on = (int) Loc.Current == i;
                if (Kit.Tap(710 + i, r))
                {
                    if (!on)
                    {
                        Loc.Current = (Lang) i;
                        Kit.Toast(Loc.Names[i]);
                    }
                    langOpen = false;
                }
                if (Kit.Paint)
                {
                    Kit.Box(r, on ? TouchUi.Hex(0x5A3A10) : (Kit.Down(710 + i) ? TouchUi.Hex(0x4A2A48) : TouchUi.Hex(0x2E1A2C)));
                    if (on)
                    {
                        TouchUi.Fill(new Rect(r.x, r.y, r.width, k), Kit.Gold);
                    }
                    Rect badge = new Rect(r.x + 4 * k, r.y + 8 * k, 15 * k, 12 * k);
                    TouchUi.Fill(new Rect(badge.x - k, badge.y - k, badge.width + 2 * k, badge.height + 2 * k), TouchUi.Hex(0x120810));
                    TouchUi.Fill(badge, TouchUi.Hex(LangColor[i]));
                    TouchUi.Label(badge, Loc.Codes[i].ToUpperInvariant(), 5.5f, TextAnchor.MiddleCenter, Color.white, true, false, false);
                    // the language's own name, then its English name (both left as they are)
                    float ns = 6.5f;
                    while (ns > 5f && TouchUi.TextWidth(Loc.Names[i], ns, true) > cellW - 26)
                    {
                        ns -= 0.25f; // a long name ("Bahasa Indonesia") gets smaller, not cut
                    }
                    TouchUi.Label(new Rect(r.x + 22 * k, r.y + 4 * k, r.width - 24 * k, 12 * k), Loc.Names[i], ns, TextAnchor.UpperLeft, Color.white, true);
                    TouchUi.Label(new Rect(r.x + 22 * k, r.y + 16 * k, r.width - 24 * k, 9 * k), LangSub[i], 4.75f, TextAnchor.UpperLeft, Kit.Muted);
                    if (on)
                    {
                        Kit.Check(r.xMax - 7 * k, r.y + 7 * k, Kit.Gold);
                    }
                }
            }
            if (Kit.Paint)
            {
                TouchUi.PixelText("NGÔN NGỮ", 1, box.center.x, TouchUi.R(0, y + 14, 0, 0).y, 1, true);
            }
            if (close || Kit.Back())
            {
                langOpen = false;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            Kit.Unblock();
        }

        // ---------------------------------------------------------------------------------- rows

        static float GroupTitle(string s, float y)
        {
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(8, y, 160, 9), s, 6, TextAnchor.MiddleLeft, Kit.Gold, true);
            }
            return y + 10;
        }

        static void Divider(float y)
        {
            TouchUi.Fill(TouchUi.R(12, y, 156, 0.5f), Kit.Line);
        }

        /// <summary>A row: title (and a smaller second line); tappable when enabled.</summary>
        static bool RowTap(int id, float y, string title, string subLine, bool enabled)
        {
            Rect r = TouchUi.R(8, y + 1, 164, Row - 1);
            bool hit = enabled && Kit.Tap(id, r);
            if (Kit.Paint)
            {
                if (Kit.Down(id) && enabled)
                {
                    TouchUi.Fill(r, new Color(1, 1, 1, 0.08f));
                }
                Color c = enabled ? Kit.Cream : Kit.Muted;
                if (string.IsNullOrEmpty(subLine))
                {
                    TouchUi.Label(TouchUi.R(14, y + 1, 130, Row - 1), title, 7, TextAnchor.MiddleLeft, c, true);
                }
                else
                {
                    TouchUi.Label(TouchUi.R(14, y + 2, 130, 9), title, 7, TextAnchor.UpperLeft, c, true);
                    TouchUi.Label(TouchUi.R(14, y + 11, 130, 8), subLine, 5.75f, TextAnchor.UpperLeft, Kit.Muted, false, false, false);
                }
            }
            return hit;
        }

        static void Chevron(float y, bool enabled = true)
        {
            if (Kit.Paint)
            {
                Color old = Gx.color;
                if (!enabled)
                {
                    Gx.color = old * new Color(1, 1, 1, 0.4f);
                }
                Kit.Chevron(TouchUi.R(168, 0, 0, 0).x, TouchUi.R(0, y + Row / 2, 0, 0).y);
                Gx.color = old;
            }
        }

        static void RightText(float y, string s, bool chevron = true)
        {
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(100, y + 1, chevron ? 60 : 66, Row - 1), s, 6.5f, TextAnchor.MiddleRight, Kit.Gold, true);
                if (chevron)
                {
                    Chevron(y);
                }
            }
        }

        static void ExternalIcon(float y)
        {
            float k = TouchUi.K;
            Rect r = TouchUi.R(159, y + 6, 8, 8);
            TouchUi.Fill(new Rect(r.x, r.y, r.width - 3 * k, k), Kit.Cream);
            TouchUi.Fill(new Rect(r.x, r.yMax - k, r.width, k), Kit.Cream);
            TouchUi.Fill(new Rect(r.x, r.y, k, r.height), Kit.Cream);
            TouchUi.Fill(new Rect(r.xMax - k, r.y + 3 * k, k, r.height - 3 * k), Kit.Cream);
            for (int i = 0; i < 4; i++)
            {
                TouchUi.Fill(new Rect(r.x + (3 + i) * k, r.y + (3 - i) * k, k, k), Kit.Cream);
            }
        }

        static string PlayerId()
        {
            string id = PlayerPrefs.GetString("l12.playerId", "");
            if (id.Length == 0)
            {
                id = Guid.NewGuid().ToString("N").Substring(0, 12).ToUpperInvariant();
                PlayerPrefs.SetString("l12.playerId", id);
                PlayerPrefs.Save();
            }
            return id;
        }
    }

    /// <summary>2.4: the rules of the game ("Hướng dẫn" of the old menu, rewritten in two boxes).</summary>
    public sealed class RulesPage : IPage
    {
        float scroll, contentH;

        static readonly string[][] Gems =
        {
            new[] {"/sword", "Thanh kiếm", "Tấn công đối phương."},
            new[] {"/swordred", "Kiếm lửa", "Mạnh hơn kiếm thường, nổ thêm 1 ô xung quanh; xếp chung được với kiếm thường."},
            new[] {"/yinyang", "Âm dương", "Lấy năng lượng để dùng tuyệt chiêu."},
            new[] {"/rice", "Bánh chưng", "Lấy lương thực. Lương thực tiêu hao mỗi lượt của đối phương; hết là thua."},
            new[] {"/gold", "Thỏi vàng", "Lấy vàng để mua vật phẩm."},
            new[] {"/book", "Cuốn sách", "Lấy kinh nghiệm để lên cấp."},
            new[] {"/heart", "Trái tim", "Hồi máu. Hết máu là thua trận."},
        };

        static readonly string[] Moves =
        {
            "Xếp ít nhất 3 biểu tượng cùng loại, ngang hoặc dọc.",
            "Xếp 4 được thêm 1 lượt và có thể rơi vật phẩm; xếp 5 luôn rơi 1 vật phẩm.",
            "Xếp sai mất lượt và mất máu.",
        };

        public bool OnGUI(Event e)
        {
            float top = 26, room = Kit.H - top;
            Vector2 d = Kit.Drag();
            if (d.y != 0)
            {
                scroll = Mathf.Clamp(scroll - d.y, 0, Mathf.Max(0, contentH - room));
            }
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x141014));
            }
            float y = top + 4 - scroll;
            // how to move
            float h1 = 14;
            foreach (string m in Moves)
            {
                h1 += TouchUi.Lines(m, 150) * UiFont.LineHeight + 2;
            }
            if (Kit.Paint)
            {
                Kit.GroupBox(TouchUi.R(6, y, 168, h1 + 4));
                TouchUi.Label(TouchUi.R(12, y + 3, 150, 9), "CÁCH ĐI", 6, TextAnchor.UpperLeft, Kit.Gold, true);
            }
            float ly = y + 14;
            foreach (string m in Moves)
            {
                int n = TouchUi.Lines(m, 150);
                if (Kit.Paint)
                {
                    TouchUi.Fill(TouchUi.R(12, ly + 4, 2, 2), Kit.Gold);
                    TouchUi.Label(TouchUi.R(17, ly, 150, n * UiFont.LineHeight), m, 6, TextAnchor.UpperLeft, Kit.Cream, false, true);
                }
                ly += n * UiFont.LineHeight + 2;
            }
            y += h1 + 4 + 8;
            // gems
            float h2 = 14;
            foreach (string[] gm in Gems)
            {
                h2 += Mathf.Max(24, 10 + TouchUi.Lines(gm[2], 128) * UiFont.LineHeight) + 2;
            }
            h2 += UiFont.LineHeight + 4;
            if (Kit.Paint)
            {
                Kit.GroupBox(TouchUi.R(6, y, 168, h2));
                TouchUi.Label(TouchUi.R(12, y + 3, 150, 9), "BIỂU TƯỢNG", 6, TextAnchor.UpperLeft, Kit.Gold, true);
            }
            float gy = y + 14;
            foreach (string[] gm in Gems)
            {
                int n = TouchUi.Lines(gm[2], 128);
                float rh = Mathf.Max(24, 10 + n * UiFont.LineHeight);
                if (Kit.Paint)
                {
                    TouchUi.Sprite(TouchUi.Img(gm[0]), TouchUi.R(12, gy + 1, 22, 22));
                    TouchUi.Label(TouchUi.R(40, gy, 128, 9), gm[1], 6.5f, TextAnchor.UpperLeft, TouchUi.Hex(0xFFE9C4), true);
                    TouchUi.Label(TouchUi.R(40, gy + 10, 128, n * UiFont.LineHeight), gm[2], 6, TextAnchor.UpperLeft, Kit.Cream, false, true);
                }
                gy += rh + 2;
            }
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(12, gy, 156, UiFont.LineHeight), "Cấp càng cao thì tấn công và máu càng lớn.", 6, TextAnchor.UpperLeft, Kit.Green, false, true);
            }
            y += h2 + 8;
            contentH = y + scroll - top;
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, TouchUi.R(0, top, 0, 0).y), TouchUi.Hex(0x0A070A));
            }
            bool close = Kit.TitleBar("LUẬT CHƠI");
            if (Kit.Back())
            {
                close = true;
                e.Use();
            }
            return !close;
        }
    }

    /// <summary>2.5: credits, from the game's own table (h.f), without the dead web address.</summary>
    public sealed class AboutPage : IPage
    {
        float scroll, contentH;

        public bool OnGUI(Event e)
        {
            float top = 26, room = Kit.H - top;
            Vector2 d = Kit.Drag();
            if (d.y != 0)
            {
                scroll = Mathf.Clamp(scroll - d.y, 0, Mathf.Max(0, contentH - room));
            }
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x141014));
            }
            float y = top + 6 - scroll;
            int n = 0;
            try
            {
                n = C_PortScreens.creditCount();
            }
            catch (Exception)
            {
            }
            for (int i = 0; i < n; i++)
            {
                int len = C_PortScreens.creditLength(i);
                if (len == 0)
                {
                    continue;
                }
                string head = C_PortScreens.credit(i, 0);
                if (head.IndexOf("PhuThoBay", StringComparison.OrdinalIgnoreCase) >= 0 || head.IndexOf("hỗ trợ", StringComparison.OrdinalIgnoreCase) >= 0)
                {
                    continue;
                }
                bool skip = false;
                for (int j = 1; j < len; j++)
                {
                    if (C_PortScreens.credit(i, j).IndexOf("PhuThoBay", StringComparison.OrdinalIgnoreCase) >= 0)
                    {
                        skip = true;
                    }
                }
                if (skip)
                {
                    continue;
                }
                y = Entry(y, head, i, len);
            }
            y = Text(y, "Bản chuyển sang Unity", "Màn hình cảm ứng toàn màn hình · " + DateTime.Now.Year);
            y = Text(y, "Ghi chú", "Tình tiết, cốt truyện và tên nhân vật phụ của trò chơi dựa vào sự kiện Loạn 12 Sứ Quân trong lịch sử Việt Nam ở thế kỷ X.");
            contentH = y + scroll - top;
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Gx.Width, TouchUi.R(0, top, 0, 0).y), TouchUi.Hex(0x0A070A));
            }
            bool close = Kit.TitleBar("THÔNG TIN");
            if (Kit.Back())
            {
                close = true;
                e.Use();
            }
            return !close;
        }

        static float Entry(float y, string head, int i, int len)
        {
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(12, y, 156, 9), head, 6.5f, TextAnchor.UpperCenter, Kit.Gold, true);
            }
            y += 11;
            for (int j = 1; j < len; j++)
            {
                string line = C_PortScreens.credit(i, j);
                if (line.Length == 0)
                {
                    continue;
                }
                if (Kit.Paint)
                {
                    TouchUi.Label(TouchUi.R(12, y, 156, 9), line, 6.5f, TextAnchor.UpperCenter, Kit.Cream);
                }
                y += UiFont.LineHeight;
            }
            return y + 8;
        }

        static float Text(float y, string head, string body)
        {
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(12, y, 156, 9), head, 6.5f, TextAnchor.UpperCenter, Kit.Gold, true);
            }
            y += 11;
            int n = TouchUi.Lines(body, 150);
            if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(15, y, 150, n * UiFont.LineHeight), body, 6, TextAnchor.UpperCenter, Kit.Cream, false, true);
            }
            return y + n * UiFont.LineHeight + 8;
        }
    }

    /// <summary>Store page, sharing and the privacy policy (Unity side, not the game).</summary>
    public static class Store
    {
        public const string PackageId = "com.vucv.loan12";

        public static void Rate()
        {
            // In-App Review needs the Play Core plugin; until it is added, open the store page.
            Application.OpenURL("market://details?id=" + PackageId);
        }

        public static void Share()
        {
            string text = "Chơi Loạn 12 Sứ Quân cùng mình: https://play.google.com/store/apps/details?id=" + PackageId;
#if UNITY_ANDROID && !UNITY_EDITOR
            try
            {
                using (AndroidJavaClass intentClass = new AndroidJavaClass("android.content.Intent"))
                using (AndroidJavaObject intent = new AndroidJavaObject("android.content.Intent"))
                using (AndroidJavaClass player = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
                {
                    intent.Call<AndroidJavaObject>("setAction", intentClass.GetStatic<string>("ACTION_SEND"));
                    intent.Call<AndroidJavaObject>("setType", "text/plain");
                    intent.Call<AndroidJavaObject>("putExtra", intentClass.GetStatic<string>("EXTRA_TEXT"), text);
                    AndroidJavaObject activity = player.GetStatic<AndroidJavaObject>("currentActivity");
                    AndroidJavaObject chooser = intentClass.CallStatic<AndroidJavaObject>("createChooser", intent, "Chia sẻ");
                    activity.Call("startActivity", chooser);
                }
                return;
            }
            catch (Exception)
            {
            }
#endif
            GUIUtility.systemCopyBuffer = text;
            Kit.Toast("Đã chép lời mời vào bộ nhớ tạm");
        }

        /// <summary>The policy page has to exist before release (Google Play, AdMob); its address goes here.</summary>
        public const string PrivacyUrl = "";

        public static void Privacy()
        {
            if (PrivacyUrl.Length == 0)
            {
                Kit.Toast("Trang chính sách quyền riêng tư sẽ có trước khi phát hành");
                return;
            }
            Application.OpenURL(PrivacyUrl);
        }
    }
}
