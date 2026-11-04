// Dialogues on the map (quests, events, after a battle) and the arena of Tam Điệp, drawn by Unity
// instead of the game's 240x320 bubble and box (plan/thiet-ke-giao-dien.md 4, "Hội thoại cốt truyện").
//
// Dialogue: the map darkened behind, the speaker's big portrait and name plate over a large text box,
// the two centred vertically; a tap anywhere goes on to the next line, BỎ QUA (top right) skips the rest. The game keeps
// the dialogue (suquan.aa state 2) and does what follows it (battle, reward ...).
//
// Arena: the 5 levels (ticket in gold, open / locked with its condition / too expensive), the opponent
// of the chosen level (HP, attack, experience a win gives), RÚT LUI / ĐÁNH. The game's arena box (co)
// keeps the choice and its rules (ticket paid on "Đánh", lives checked like any battle).
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class MapBackdrop
    {
        /// <summary>The map at the map view's scale, centred on the hero, darkened.</summary>
        public static void Draw(float darken)
        {
            Texture2D map = TouchUi.Img("/m/m");
            float s = TouchMapView.S;
            if (TouchMapView.HasOrigin)
            {
                // exactly where the map view left it: no jump between the map and what covers it
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0A121E));
                Gx.DrawTexture(new Rect(TouchMapView.Origin.x, TouchMapView.Origin.y, 380 * s, 380 * s), map);
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), new Color(0.03f, 0.02f, 0.05f, darken));
                return;
            }
            float vw = Gx.Width / s, vh = Gx.Height / s;
            float x = C_PortMap.playerX() - vw / 2, y = C_PortMap.playerY() - vh / 2;
            x = vw >= 380 ? (380 - vw) / 2 : Mathf.Clamp(x, 0, 380 - vw);
            y = vh >= 380 ? (380 - vh) / 2 : Mathf.Clamp(y, 0, 380 - vh);
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x0A121E));
            Gx.DrawTexture(new Rect(-x * s, -y * s, 380 * s, 380 * s), map);
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), new Color(0.03f, 0.02f, 0.05f, darken));
        }
    }

    public sealed class TouchTalkView : ITouchView, IOwnsKeys
    {
        int shownIndex = -1;
        float shownAt;

        public bool Active()
        {
            try
            {
                return C_PortScreens.screenKind() == 4 && C_PortMap.talking();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            shownIndex = -1;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            float k = TouchUi.K, H = Kit.H;
            int index = C_PortMap.talkNowIndex();
            if (index != shownIndex)
            {
                shownIndex = index;
                shownAt = Time.unscaledTime;
            }
            bool busy = C_PortScreens.busy();
            Rect skip = TouchUi.R(118, 4, 58, 18);
            if (Kit.Tap(1, skip) && !busy)
            {
                C_PortMap.request(C_PortMap.f_TALK_SKIP, 0);
            }
            else if (Kit.Tap(2, new Rect(0, 0, Gx.Width, Gx.Height)) && !busy && Time.unscaledTime - shownAt > 0.25f)
            {
                C_PortMap.request(C_PortMap.f_TALK_NEXT, 0);
            }
            if (Kit.Back())
            {
                e.Use();
                C_PortMap.request(C_PortMap.f_TALK_SKIP, 0);
            }
            if (Kit.Paint)
            {
                MapBackdrop.Draw(0.35f);
                int who = C_PortMap.talkNowSpeaker();
                string text = C_PortMap.talkNowText();
                float size = 7.5f;
                int lines = Mathf.Max(4, TouchUi.Lines(text, 152, size));
                float boxH = Mathf.Min(H * 0.5f, lines * TouchUi.LineHeight(size) + 36);
                // portrait + text box as one block, centred vertically
                const float pic0 = 50; // portrait size: leaves the name plate room up to the right edge
                float block = (who >= 0 ? pic0 + 2 : 0) + boxH;
                float boxY = Mathf.Round((H - block) / 2f) + (who >= 0 ? pic0 + 2 : 0);
                float t = Mathf.Clamp01((Time.unscaledTime - shownAt) / 0.15f);
                if (who >= 0)
                {
                    Rect pic = TouchUi.R(14, boxY - pic0 + (1 - t) * 6, pic0, pic0);
                    TouchUi.Fill(new Rect(pic.x - 2 * k, pic.y - 2 * k, pic.width + 4 * k, pic.height + 4 * k), TouchUi.GoldLine);
                    TouchUi.Portrait(who, pic);
                    // the name plate fits between the portrait and the screen's edge: the game's font, or a
                    // smaller one for a long name
                    string name = QuestData.Who(who).ToUpperInvariant();
                    const float px = 14 + pic0 + 6, room = 174 - px;
                    float pw = TouchUi.PixelWidth(name, 1) + 16;
                    float w = Mathf.Clamp(pw, 60, room);
                    Rect plate = TouchUi.R(px, boxY - 20, w, 16);
                    Kit.Box(plate, TouchUi.Hex(0x46245C));
                    if (pw <= room)
                    {
                        TouchUi.PixelText(name, 1, plate.center.x, plate.center.y, 1, true);
                    }
                    else
                    {
                        TouchUi.Label(plate, name, 7, TextAnchor.MiddleCenter, TouchUi.Hex(0xFFD84A), true);
                    }
                }
                Rect box = TouchUi.R(6, boxY, 168, boxH);
                TouchUi.Panel(box);
                Color old = Gx.color;
                Gx.color = old * new Color(1, 1, 1, t);
                TouchUi.Label(TouchUi.R(14, boxY + 8, 152, boxH - 26), text, size, TextAnchor.UpperLeft, Color.white, false, true);
                Gx.color = old;
                // centred under the text: the hint, and the bobbing arrow below it
                TouchUi.Label(TouchUi.R(14, boxY + boxH - 21, 152, 10), "Chạm để tiếp tục · " + (index + 1) + "/" + C_PortMap.talkNowCount(),
                    6, TextAnchor.UpperCenter, TouchUi.Hex(0xD8C8DC));
                float bob = Mathf.Round(Mathf.Sin(Time.unscaledTime * 5) * 1.5f);
                for (int i = 0; i < 4; i++)
                {
                    TouchUi.Fill(TouchUi.R(90 - 3.5f + i, boxY + boxH - 10 + i + bob, 7 - 2 * i, 1), Kit.Gold);
                }
                TouchUi.Button(skip, Btn.Dark, Kit.Down(1));
                TouchUi.PixelText("BỎ QUA", 1, skip.center.x, skip.center.y + (Kit.Down(1) ? k : 0), 1, true);
            }
            Kit.End();
        }
    }

    public sealed class TouchArenaView : ITouchView, IOwnsKeys
    {
        static readonly string[] Need = {"mở sẵn", "mở sẵn", "thắng Lê Hoàn", "thắng Nguyễn Ninh", "đạt cấp 30"};

        static readonly string[] Rules =
        {
            "Mỗi lần vào đấu phải trả vé bằng vàng.",
            "Thắng: nhận kinh nghiệm theo cấp hiện tại.",
            "Thua: mất 1 mạng, không bị trừ kinh nghiệm.",
            "Vàng và kinh nghiệm nhặt trong trận không được tính; máu không bị ảnh hưởng.",
            "Lúc đầu mở 2 mức, thắng các tướng mạnh để mở thêm 3 mức."
        };

        /// <summary>The arena's rules on its first visit (the game's text screen, redone): ĐỒNG Ý closes it.</summary>
        static void Intro(Event e)
        {
            float k = TouchUi.K, H = Kit.H;
            float lineH = 0;
            foreach (string r in Rules)
            {
                lineH += TouchUi.Lines(r, 132, 6.75f) * TouchUi.LineHeight(6.75f) + 5;
            }
            float h = 58 + lineH + 40;
            float top = Mathf.Round((H - h) / 2);
            Rect ok = TouchUi.R(46, top + h - 34, 88, 24);
            if (Kit.Tap(30, ok) || Kit.Back())
            {
                C_PortMap.f_arenaIntro = false;
                if (e.type == EventType.KeyDown)
                {
                    e.Use();
                }
            }
            if (!Kit.Paint)
            {
                return;
            }
            MapBackdrop.Draw(0.72f);
            TouchUi.Panel(TouchUi.R(8, top, 164, h));
            TouchUi.PixelText("ĐẤU TRƯỜNG", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 13, 0, 0).y, 1, true);
            TouchUi.Label(TouchUi.R(16, top + 24, 148, 22), "Đã mở Đấu trường Tam Điệp: nơi luyện điểm kinh nghiệm.", 7, TextAnchor.UpperCenter, Kit.Cream, true, true);
            float y = top + 50;
            foreach (string r in Rules)
            {
                float rh = TouchUi.Lines(r, 132, 6.75f) * TouchUi.LineHeight(6.75f);
                TouchUi.Fill(TouchUi.R(20, y + 3, 3, 3), Kit.Gold);
                TouchUi.Label(TouchUi.R(28, y, 132, rh + 2), r, 6.75f, TextAnchor.UpperLeft, Color.white, false, true);
                y += rh + 5;
            }
            TouchUi.TextButton(ok, "ĐỒNG Ý", Btn.Red, Kit.Down(30));
        }

        public bool Active()
        {
            try
            {
                return C_PortScreens.screenKind() == 4 && C_PortMap.arena();
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
            Kit.Begin(e);
            float k = TouchUi.K, H = Kit.H;
            if (C_PortMap.f_arenaIntro)
            {
                Intro(e);
                Kit.End();
                return;
            }
            bool busy = C_PortScreens.busy();
            int chosen = C_PortMap.arenaChosen();
            int levels = Mathf.Min(5, C_PortMap.arenaLevels());
            int gold = C_PortScreens.gold();
            if (Kit.Paint)
            {
                MapBackdrop.Draw(0.72f);
            }
            Kit.TopBar(4, false);
            if (Kit.Paint)
            {
                TouchUi.PixelText("ĐẤU TRƯỜNG", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 33, 0, 0).y, 1, true);
                TouchUi.Label(TouchUi.R(6, 40, 168, 10), "Tam Điệp · luyện kinh nghiệm, trả vé bằng vàng", 6, TextAnchor.UpperCenter, Kit.Cream);
            }

            // levels
            float y = 54, rowH = Mathf.Clamp((H - 54 - 170) / 5f, 22, 30);
            for (int i = 0; i < levels; i++)
            {
                Rect r = TouchUi.R(6, y + i * (rowH + 3), 168, rowH);
                bool open = C_PortMap.arenaOpen(i), can = C_PortMap.arenaCan(i);
                if (Kit.Tap(10 + i, r) && !busy)
                {
                    if (can)
                    {
                        C_PortMap.arenaSelect(i);
                    }
                    else if (open)
                    {
                        Kit.Toast("Thiếu vàng: vé mức " + (i + 1) + " là " + TouchUi.Money(C_PortMap.arenaCost(i)) + " vàng");
                    }
                    else
                    {
                        Kit.Toast("Mức " + (i + 1) + " chưa mở: " + Need[i]);
                    }
                }
                if (!Kit.Paint)
                {
                    continue;
                }
                bool sel = i == chosen;
                if (sel)
                {
                    TouchUi.Fill(new Rect(r.x - 1.5f * k, r.y - 1.5f * k, r.width + 3 * k, r.height + 3 * k), TouchUi.Hex(0x8BE05C));
                }
                Kit.Box(r, sel ? TouchUi.Hex(0x4A2049) : (can ? TouchUi.Hex(0x2E1630) : TouchUi.Hex(0x1C1220)));
                Color old = Gx.color;
                if (!can)
                {
                    Gx.color = old * new Color(1, 1, 1, 0.55f);
                }
                TouchUi.PixelText("MỨC " + (i + 1), 1, r.x + 8 * k, r.center.y, 0, true);
                if (open)
                {
                    TouchUi.Sprite(TouchUi.Img("/gold"), new Rect(r.x + 58 * k, r.center.y - 6 * k, 12 * k, 12 * k));
                    TouchUi.Label(new Rect(r.x + 72 * k, r.y, 50 * k, r.height), TouchUi.Money(C_PortMap.arenaCost(i)) + " vàng", 6.5f, TextAnchor.MiddleLeft,
                        can ? Kit.Gold : Kit.Red, true);
                    TouchUi.Label(new Rect(r.xMax - 52 * k, r.y, 48 * k, r.height), can ? "+" + C_PortMap.arenaPercent(i) + "% cấp" : "Thiếu vàng", 6,
                        TextAnchor.MiddleRight, can ? Kit.Green : Kit.Red, true);
                }
                else
                {
                    TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(r.x + 58 * k, r.center.y - 6 * k, 12 * k, 12 * k));
                    TouchUi.Label(new Rect(r.x + 74 * k, r.y, 92 * k, r.height), "Chưa mở · " + Need[i], 6, TextAnchor.MiddleLeft, Kit.Muted);
                }
                Gx.color = old;
            }

            // the opponent of the chosen level
            float oy = y + levels * (rowH + 3) + 6;
            Rect card = TouchUi.R(6, oy, 168, 86);
            if (Kit.Paint)
            {
                TouchUi.Panel(card);
                if (chosen >= 0)
                {
                    int enemy = C_PortMap.arenaEnemy(chosen);
                    if (enemy >= 0)
                    {
                        TouchUi.Portrait(enemy, TouchUi.R(14, oy + 10, 40, 40));
                    }
                    TouchUi.Label(TouchUi.R(60, oy + 8, 108, 10), "Đối thủ mức " + (chosen + 1), 6, TextAnchor.UpperLeft, Kit.Muted);
                    TouchUi.Label(TouchUi.R(60, oy + 18, 108, 11), QuestData.Who(enemy) + " · cấp " + C_PortMap.arenaEnemyLevel(chosen), 7, TextAnchor.UpperLeft,
                        Color.white, true);
                    TouchUi.Sprite(TouchUi.Img("/m/ih"), TouchUi.R(60, oy + 33, 10, 10));
                    TouchUi.Label(TouchUi.R(73, oy + 32, 40, 11), TouchUi.Money(C_PortMap.arenaEnemyHp(chosen)), 6.5f, TextAnchor.UpperLeft, Kit.Cream, true);
                    TouchUi.Sprite(TouchUi.Img("/m/isw"), TouchUi.R(112, oy + 33, 10, 10));
                    TouchUi.Label(TouchUi.R(125, oy + 32, 40, 11), TouchUi.Money(C_PortMap.arenaEnemyAttack(chosen)), 6.5f, TextAnchor.UpperLeft, Kit.Cream, true);
                    TouchUi.Label(TouchUi.R(14, oy + 54, 152, 10), "Thắng: +" + TouchUi.Money(C_PortMap.arenaExp(chosen)) + " kinh nghiệm (" +
                        C_PortMap.arenaPercent(chosen) + "% một cấp)", 6.5f, TextAnchor.UpperLeft, Kit.Green, true);
                    TouchUi.Label(TouchUi.R(14, oy + 66, 152, 18), "Thua: mất 1 mạng, không trừ kinh nghiệm. Vàng, KN nhặt trong trận không tính.", 5.5f,
                        TextAnchor.UpperLeft, Kit.Muted, false, true);
                }
                else
                {
                    TouchUi.Label(TouchUi.R(14, oy + 20, 152, 40), "Bạn không đủ vàng cho mức nào đang mở. Mở rương miễn phí hoặc mua vàng ở Cửa hàng.",
                        6.5f, TextAnchor.UpperCenter, Kit.Cream, false, true);
                }
            }

            Rect leave = TouchUi.R(6, H - 38, 62, 30), fight = TouchUi.R(72, H - 40, 102, 34);
            if (Kit.Tap(30, leave) && !busy)
            {
                C_PortMap.request(C_PortMap.f_ARENA_LEAVE, 0);
            }
            if (Kit.Tap(31, fight) && !busy && chosen >= 0)
            {
                C_PortMap.request(C_PortMap.f_ARENA_FIGHT, 0);
            }
            if (Kit.Back())
            {
                e.Use();
                C_PortMap.request(C_PortMap.f_ARENA_LEAVE, 0);
            }
            if (Kit.Paint)
            {
                TouchUi.TextButton(leave, "RÚT LUI", Btn.Grey, Kit.Down(30));
                Kit.MainButton(fight, "ĐÁNH", chosen >= 0 ? "Vé " + TouchUi.Money(C_PortMap.arenaCost(chosen)) + " vàng" : null, chosen >= 0 && !busy, Kit.Down(31));
            }
            Kit.DrawToast(H - 58);
            Kit.End();
        }
    }
}
