// Screen 6 of the touch redesign (plan/thiet-ke-giao-dien.md): the battle.
//
//   whose turn + pause / the board (cut from the game's screen) / a line describing what happened /
//   the game's own fighter strip (player left, enemy right; health, time, energy bars as before) /
//   skills / the items taken into battle.
// Effects: numbers rising from the bars (damage, healing, energy), a line of text, item effect badges
// on the portraits and speech bubbles (hurt, low health, skills, start of the battle). One tap on a
// skill or an item opens a popup (description, USE, CLOSE; a tap anywhere else closes it).
// The game keeps the rules, the AI and the animations on the board; dialogs, the tutorial and screen
// transitions are shown by the classic full-screen view (the host switches automatically).
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBattleView : ITouchView
    {
        const int GridH = 346;

        // the game's fighter strip on its 240x320 screen (both portraits and their bars)
        const int StripX = 30, StripY = 228, StripW = 180, StripH = 48;
        const float StripTop = 212; // where it goes on the grid

        static readonly string[] Elements = {"Hỏa", "Lôi", "Thủy"};

        static readonly string[][] HurtLines =
        {
            new[] {"Á! Đau quá...", "Hự! Ngươi khá lắm...", "Không xong rồi!"},
            new[] {"Á! Ngươi khá lắm...", "Hự... lợi hại!", "Đáng ghét!"},
        };
        static readonly string[][] LowLines =
        {
            new[] {"Không lẽ ta thua ở đây sao..."},
            new[] {"Ta... chưa chịu thua đâu!"},
        };
        static readonly string[] StartLines = {"Kẻ nào dám đến đây? Nạp mạng!", "Ngươi chạy đâu cho thoát!", "Để xem ngươi được mấy chiêu!"};
        static readonly string[] HealLines = {"Khỏe lại rồi!", "Vẫn còn sức chiến đấu!"};
        static readonly string[] BlockLines = {"Hừ! Chẳng nhằm nhò gì.", "Đòn này chưa đủ đâu!"};

        // input
        bool boardHeld;
        int downKind, downArg; // 1 skill, 2 item, 3 pause, 4 popup USE, 5 popup CLOSE, 6 outside popup
        int popupKind, popupArg; // 1 skill slot, 2 item slot
        int bx, by, bw, bh;
        Rect boardRect;

        // effects
        object lastBattle;
        readonly int[] prevHp = new int[2], prevMana = new int[2], prevShield = new int[2], prevSword = new int[2];
        readonly bool[] lowSaid = new bool[2];
        readonly Dictionary<int, int> prevItems = new Dictionary<int, int>();
        int lastFrame = -1;
        string ticker = "";
        float tickerTime = -10f;
        readonly System.Random rnd = new System.Random(12);

        struct Floater
        {
            public string sign, suffix;
            public int amount;
            public Color color;
            public Vector2 at;
            public float t0;
            public int key;

            public string Text
            {
                get { return sign + amount + suffix; }
            }
        }

        readonly List<Floater> floaters = new List<Floater>();
        int bubbleSide = -1;
        string bubbleText;
        float bubbleTime;

        public bool Active()
        {
            try
            {
                return C_PortUi.battleView();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            if (boardHeld)
            {
                J2ME.Host.pointerReleased(bx + bw / 2, by + bh / 2);
                boardHeld = false;
            }
            downKind = 0;
            popupKind = 0;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            TouchUi.Begin(GridH);
            bx = C_PortUi.board(0);
            by = C_PortUi.board(1);
            bw = C_PortUi.board(2);
            bh = C_PortUi.board(3);
            boardRect = TouchUi.R(90 - bw / 2f, 24, bw, bh);
            if (Time.frameCount != lastFrame)
            {
                lastFrame = Time.frameCount;
                TrackEvents();
            }
            HandleInput(e, haptics);
            if (e.type == EventType.Repaint)
            {
                Draw(lcd);
            }
        }

        // ---------------------------------------------------------------------------------- rects

        static Rect PauseRect()
        {
            return TouchUi.R(157, 0, 20, 20);
        }

        static Rect SkillRect(int i)
        {
            return TouchUi.R(4 + i * 59, 264, 55, 40);
        }

        static Rect ItemRect(int i)
        {
            return TouchUi.R(4 + i * 59, 308, 55, 34);
        }

        Rect PopupRect()
        {
            return popupKind == 1 ? TouchUi.R(6, 76, 168, 180) : TouchUi.R(6, 118, 168, 182);
        }

        Rect PopupUse()
        {
            Rect p = PopupRect();
            Vector2 v = TouchUi.V(new Vector2(p.x, p.yMax));
            return TouchUi.R(84, v.y - 34, 84, 26);
        }

        Rect PopupClose()
        {
            Rect p = PopupRect();
            Vector2 v = TouchUi.V(new Vector2(p.x, p.yMax));
            return TouchUi.R(14, v.y - 32, 64, 22);
        }

        // ---------------------------------------------------------------------------------- state

        static bool SkillAffordable(int i)
        {
            return i < C_PortUi.skillCount() && i < C_PortUi.skillsUsable();
        }

        static bool SkillEnabled(int i)
        {
            return C_PortUi.waiting() && SkillAffordable(i);
        }

        static bool ItemEnabled(int type)
        {
            return type >= 0 && C_PortUi.waiting() && !C_PortUi.itemUsed() && C_PortUi.itemCount(type) > 0;
        }

        // ---------------------------------------------------------------------------------- input

        void HandleInput(Event e, bool haptics)
        {
            Vector2 p = e.mousePosition;
            int[] slots = Loadout.Get();
            switch (e.type)
            {
                case EventType.MouseDown:
                    if (e.button != 0)
                    {
                        return;
                    }
                    downKind = 0;
                    if (popupKind != 0)
                    {
                        if (PopupUse().Contains(p))
                        {
                            downKind = 4;
                        }
                        else if (PopupClose().Contains(p))
                        {
                            downKind = 5;
                        }
                        else if (!PopupRect().Contains(p))
                        {
                            downKind = 6;
                        }
                        e.Use();
                        return;
                    }
                    if (boardRect.Contains(p))
                    {
                        boardHeld = true;
                        int lx, ly;
                        ToLcd(p, out lx, out ly);
                        J2ME.Host.pointerPressed(lx, ly);
                        e.Use();
                        return;
                    }
                    if (PauseRect().Contains(p) && C_PortUi.waiting())
                    {
                        downKind = 3;
                    }
                    for (int i = 0; i < 3; i++)
                    {
                        if (SkillRect(i).Contains(p) && i < C_PortUi.skillCount())
                        {
                            downKind = 1;
                            downArg = i;
                        }
                        if (ItemRect(i).Contains(p) && slots[i] >= 0)
                        {
                            downKind = 2;
                            downArg = i;
                        }
                    }
                    if (downKind != 0)
                    {
                        Haptics.Tap(haptics);
                    }
                    e.Use();
                    break;
                case EventType.MouseDrag:
                    if (boardHeld)
                    {
                        int lx, ly;
                        ToLcd(p, out lx, out ly);
                        J2ME.Host.pointerDragged(lx, ly);
                        e.Use();
                    }
                    break;
                case EventType.MouseUp:
                    if (boardHeld)
                    {
                        int lx, ly;
                        ToLcd(p, out lx, out ly);
                        J2ME.Host.pointerReleased(lx, ly);
                        boardHeld = false;
                        e.Use();
                        return;
                    }
                    switch (downKind)
                    {
                        case 1:
                            if (SkillRect(downArg).Contains(p))
                            {
                                popupKind = 1;
                                popupArg = downArg;
                            }
                            break;
                        case 2:
                            if (ItemRect(downArg).Contains(p))
                            {
                                popupKind = 2;
                                popupArg = downArg;
                            }
                            break;
                        case 3:
                            if (PauseRect().Contains(p))
                            {
                                C_PortUi.request(C_PortUi.f_PAUSE, 0);
                            }
                            break;
                        case 4:
                            if (PopupUse().Contains(p) && PopupUsable())
                            {
                                if (popupKind == 1)
                                {
                                    C_PortUi.request(C_PortUi.f_SKILL, popupArg);
                                }
                                else
                                {
                                    C_PortUi.request(C_PortUi.f_ITEM, slots[popupArg]);
                                }
                                Haptics.Tap(haptics);
                                popupKind = 0;
                            }
                            break;
                        case 5:
                        case 6:
                            popupKind = 0;
                            break;
                    }
                    downKind = 0;
                    e.Use();
                    break;
            }
        }

        bool PopupUsable()
        {
            if (popupKind == 1)
            {
                return SkillEnabled(popupArg);
            }
            int[] slots = Loadout.Get();
            return ItemEnabled(slots[popupArg]);
        }

        void ToLcd(Vector2 p, out int x, out int y)
        {
            x = bx + Mathf.Clamp(Mathf.FloorToInt((p.x - boardRect.x) / TouchUi.K), 0, bw - 1);
            y = by + Mathf.Clamp(Mathf.FloorToInt((p.y - boardRect.y) / TouchUi.K), 0, bh - 1);
        }

        // ---------------------------------------------------------------------------------- events

        static Vector2 HpAt(int side)
        {
            return new Vector2(side == 0 ? 61 : 118, StripTop + 6);
        }

        static Vector2 ManaAt(int side)
        {
            return new Vector2(side == 0 ? 61 : 118, StripTop + 26);
        }

        void TrackEvents()
        {
            object battle = C_PortUi.battle();
            if (battle != lastBattle)
            {
                lastBattle = battle;
                for (int s = 0; s < 2; s++)
                {
                    prevHp[s] = C_PortUi.hp(s);
                    prevMana[s] = C_PortUi.mana(s);
                    prevShield[s] = C_PortUi.effect(s, 3);
                    prevSword[s] = C_PortUi.effect(s, 0);
                    lowSaid[s] = false;
                }
                prevItems.Clear();
                foreach (int t in Loadout.Usable)
                {
                    prevItems[t] = C_PortUi.itemCount(t);
                }
                floaters.Clear();
                ticker = "";
                popupKind = 0;
                Say(1, Pick(StartLines));
                return;
            }
            for (int s = 0; s < 2; s++)
            {
                string who = C_PortUi.name(s);
                int hp = C_PortUi.hp(s), maxHp = C_PortUi.maxHp(s);
                int dh = hp - prevHp[s];
                if (dh < 0)
                {
                    Float(s * 10 + 1, "-", -dh, "", TouchUi.Hex(0xFF5A4A), HpAt(s));
                    Tick(who + " mất " + (-dh) + " máu");
                    if (C_PortUi.effect(s, 3) > 0 && -dh < maxHp / 10)
                    {
                        Say(s, Pick(BlockLines));
                    }
                    else if (-dh * 100 >= maxHp * 15)
                    {
                        Say(s, Pick(HurtLines[s]));
                    }
                }
                else if (dh > 0)
                {
                    Float(s * 10 + 2, "+", dh, " máu", TouchUi.Hex(0x7CE05A), HpAt(s));
                    Tick(who + " hồi " + dh + " máu");
                    if (dh * 100 >= maxHp * 10)
                    {
                        Say(s, Pick(HealLines));
                    }
                }
                prevHp[s] = hp;
                if (!lowSaid[s] && hp > 0 && hp * 5 < maxHp)
                {
                    lowSaid[s] = true;
                    Say(s, LowLines[s][0]);
                }

                int mana = C_PortUi.mana(s), maxMana = C_PortUi.maxMana(s);
                int dm = mana - prevMana[s];
                if (dm > 0)
                {
                    Float(s * 10 + 3, "+", dm, " năng lượng", TouchUi.Hex(0x7FB8FF), ManaAt(s));
                }
                else if (dm < 0 && maxMana >= 3 && -dm >= maxMana / 3)
                {
                    int k = Mathf.Clamp(-dm / (maxMana / 3) - 1, 0, 2);
                    string skill = C_PortUi.skillName(s, k);
                    if (skill.Length > 0)
                    {
                        Tick(who + " dùng " + skill);
                        Say(s, "Nếm thử " + skill + "!");
                    }
                }
                prevMana[s] = mana;

                int shield = C_PortUi.effect(s, 3), sword = C_PortUi.effect(s, 0);
                if (shield > 0 && prevShield[s] == 0)
                {
                    Tick("Quỷ Diện Giáp: chặn 3/4 sát thương");
                }
                if (sword > 0 && prevSword[s] == 0)
                {
                    Tick("Long Thần Kiếm: sức tấn công x2");
                }
                prevShield[s] = shield;
                prevSword[s] = sword;
            }
            foreach (int t in Loadout.Usable)
            {
                int c = C_PortUi.itemCount(t);
                int before;
                if (prevItems.TryGetValue(t, out before) && c < before)
                {
                    Tick(C_PortUi.name(0) + " dùng " + C_PortUi.itemName(t));
                }
                prevItems[t] = c;
            }
            floaters.RemoveAll(f => Time.unscaledTime - f.t0 > 1.4f);
            if (bubbleSide >= 0 && Time.unscaledTime - bubbleTime > 2.2f)
            {
                bubbleSide = -1;
            }
        }

        string Pick(string[] lines)
        {
            return lines[rnd.Next(lines.Length)];
        }

        void Say(int side, string text)
        {
            if (bubbleSide >= 0 && Time.unscaledTime - bubbleTime < 1.2f)
            {
                return; // one bubble at a time
            }
            bubbleSide = side;
            bubbleText = text;
            bubbleTime = Time.unscaledTime;
        }

        void Tick(string msg)
        {
            if (Time.unscaledTime - tickerTime < 0.6f && ticker.Length > 0 && ticker.Length + msg.Length < 70 && !ticker.Contains(msg))
            {
                ticker += ", " + msg;
            }
            else
            {
                ticker = msg;
            }
            tickerTime = Time.unscaledTime;
        }

        /// <summary>A rising number; repeated changes within a moment add up instead of piling up.</summary>
        void Float(int key, string sign, int amount, string suffix, Color color, Vector2 at)
        {
            for (int i = floaters.Count - 1; i >= 0; i--)
            {
                Floater f = floaters[i];
                if (f.key == key && Time.unscaledTime - f.t0 < 0.5f)
                {
                    f.amount += amount;
                    f.t0 = Time.unscaledTime;
                    floaters[i] = f;
                    return;
                }
            }
            floaters.Add(new Floater {sign = sign, amount = amount, suffix = suffix, color = color, at = at, t0 = Time.unscaledTime, key = key});
        }

        // ---------------------------------------------------------------------------------- drawing

        void Draw(Texture2D lcd)
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);

            // whose turn + pause
            bool mine = C_PortUi.waiting();
            int turn = C_PortUi.turn();
            Rect pill = TouchUi.R(44, 2, 92, 15);
            TouchUi.Fill(pill, TouchUi.Outline);
            TouchUi.Fill(new Rect(pill.x + k, pill.y + k, pill.width - 2 * k, pill.height - 2 * k),
                mine ? TouchUi.Hex(0x2F8A22) : (turn == 1 ? TouchUi.Hex(0xA3201B) : TouchUi.Hex(0x4E4A50)));
            TouchUi.Label(pill, mine ? "LƯỢT CỦA BẠN" : (turn == 1 ? "LƯỢT ĐỐI THỦ" : "..."), 7.5f, TextAnchor.MiddleCenter, Color.white, true);
            Rect pr = PauseRect();
            TouchUi.Button(pr, C_PortUi.waiting() ? Btn.Dark : Btn.Grey, downKind == 3);
            Color pc = C_PortUi.waiting() ? TouchUi.Hex(0xF0E8D8) : TouchUi.Hex(0x6A6070);
            TouchUi.Fill(new Rect(pr.x + 6 * k, pr.y + 5 * k, 3 * k, 10 * k), pc);
            TouchUi.Fill(new Rect(pr.x + 11 * k, pr.y + 5 * k, 3 * k, 10 * k), pc);

            // the board, cut from the game's screen
            Rect frame = new Rect(boardRect.x - 2 * k, boardRect.y - 2 * k, boardRect.width + 4 * k, boardRect.height + 4 * k);
            TouchUi.Fill(frame, TouchUi.Outline);
            TouchUi.Fill(new Rect(frame.x + k, frame.y + k, frame.width - 2 * k, frame.height - 2 * k), TouchUi.GoldLine);
            DrawLcd(lcd, boardRect, bx, by, bw, bh);

            // what happened
            Rect line = TouchUi.R(2, 198, 176, 13);
            TouchUi.Fill(line, TouchUi.Hex(0x140812));
            if (ticker.Length > 0)
            {
                float age = Time.unscaledTime - tickerTime;
                Color tc = TouchUi.TextGold;
                tc.a = age < 4f ? 1f : Mathf.Clamp01(1f - (age - 4f));
                TouchUi.Label(line, ticker, 6.5f, TextAnchor.MiddleCenter, tc);
            }

            // the game's fighter strip: portraits and bars exactly as in the original
            DrawLcd(lcd, TouchUi.R(0, StripTop, StripW, StripH), StripX, StripY, StripW, StripH);
            DrawEffectBadges();

            // skills
            int n = C_PortUi.skillCount();
            for (int i = 0; i < 3; i++)
            {
                Rect r = SkillRect(i);
                bool usable = SkillEnabled(i);
                bool open = popupKind == 1 && popupArg == i;
                TouchUi.Button(r, i < n && SkillAffordable(i) ? Btn.Dark : Btn.Grey, downKind == 1 && downArg == i);
                if (open)
                {
                    Outline(r, TouchUi.Hex(0xF7A901));
                }
                if (i >= n)
                {
                    continue;
                }
                J2ME.Image icon = C_PortUi.skillIcon(i);
                if (icon != null)
                {
                    Color old = GUI.color;
                    GUI.color = usable ? Color.white : new Color(0.5f, 0.47f, 0.52f, 1f);
                    TouchUi.Sprite(SkillTexture(i, icon), new Rect(r.x + 3 * k, r.y + 5 * k, 30 * k, 30 * k));
                    GUI.color = old;
                }
                TouchUi.Fill(new Rect(r.x + 37 * k, r.y + 8 * k, 5 * k, 5 * k), TouchUi.Hex(0x3C7CE8));
                TouchUi.Label(new Rect(r.x + 30 * k, r.y + 16 * k, 22 * k, 14 * k), C_PortUi.skillCost(i).ToString(), 8.5f, TextAnchor.MiddleRight,
                    usable ? TouchUi.TextGold : TouchUi.Hex(0x9A90A0), true);
            }

            // items taken into battle
            int[] slots = Loadout.Get();
            for (int i = 0; i < 3; i++)
            {
                Rect r = ItemRect(i);
                int type = slots[i];
                int count = type >= 0 ? C_PortUi.itemCount(type) : 0;
                TouchUi.Button(r, count > 0 ? Btn.Dark : Btn.Grey, downKind == 2 && downArg == i);
                if (popupKind == 2 && popupArg == i)
                {
                    Outline(r, TouchUi.Hex(0xF7A901));
                }
                if (type < 0)
                {
                    TouchUi.Label(r, "—", 9, TextAnchor.MiddleCenter, TouchUi.Hex(0x8A8090));
                    continue;
                }
                TouchUi.Item(type, new Rect(r.x + 4 * k, r.y + 3 * k, 28 * k, 28 * k), count > 0 ? (ItemEnabled(type) ? 1f : 0.75f) : 0.35f);
                TouchUi.Label(new Rect(r.x + 30 * k, r.y + 12 * k, 22 * k, 14 * k), "x" + count, 8.5f, TextAnchor.MiddleRight,
                    count > 0 ? Color.white : TouchUi.Hex(0x8A8090), true);
            }

            DrawFloaters();
            DrawBubble();
            if (popupKind != 0)
            {
                DrawPopup();
            }
        }

        static void Outline(Rect r, Color c)
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(r.x, r.y, r.width, k), c);
            TouchUi.Fill(new Rect(r.x, r.yMax - k, r.width, k), c);
            TouchUi.Fill(new Rect(r.x, r.y, k, r.height), c);
            TouchUi.Fill(new Rect(r.xMax - k, r.y, k, r.height), c);
        }

        /// <summary>A rectangle of the game's 240x320 screen (top-down rows) into a screen rectangle.</summary>
        static void DrawLcd(Texture2D lcd, Rect dst, int x, int y, int w, int h)
        {
            const float W = J2MEGameHost.LcdWidth, H = J2MEGameHost.LcdHeight;
            GUI.DrawTextureWithTexCoords(dst, lcd, new Rect(x / W, (y + h) / H, w / W, -h / H), false);
        }

        readonly Texture2D[] skillTex = new Texture2D[3];
        readonly J2ME.Image[] skillSrc = new J2ME.Image[3];

        Texture2D SkillTexture(int i, J2ME.Image icon)
        {
            if (skillSrc[i] != icon || skillTex[i] == null)
            {
                if (skillTex[i] != null)
                {
                    Object.Destroy(skillTex[i]);
                }
                skillTex[i] = TouchUi.ToTexture(icon, false);
                skillSrc[i] = icon;
            }
            return skillTex[i];
        }

        void DrawEffectBadges()
        {
            float k = TouchUi.K;
            for (int s = 0; s < 2; s++)
            {
                float x = s == 0 ? 22 : 146;
                foreach (int type in new[] {3, 0})
                {
                    int turns = C_PortUi.effect(s, type);
                    if (turns <= 0)
                    {
                        continue;
                    }
                    Rect b = TouchUi.R(x, StripTop + 2, 14, 14);
                    TouchUi.Fill(b, TouchUi.Outline);
                    TouchUi.Sprite(TouchUi.Img("/itemshops"), new Rect(b.x + k, b.y + k, 12 * k, 12 * k), new Rect(type * 28 + 4, 4, 20, 20));
                    TouchUi.Label(new Rect(b.x + 6 * k, b.y + 7 * k, 9 * k, 8 * k), turns.ToString(), 6, TextAnchor.MiddleRight, Color.white, true);
                    x += s == 0 ? -15 : 15;
                }
            }
        }

        void DrawFloaters()
        {
            foreach (Floater f in floaters)
            {
                float age = Time.unscaledTime - f.t0;
                Color c = f.color;
                c.a = age < 0.9f ? 1f : Mathf.Clamp01(1f - (age - 0.9f) / 0.5f);
                Vector2 at = f.at + new Vector2(0, -6 - 10 * Mathf.Min(1f, age / 1.2f));
                string t = f.Text;
                TouchUi.Label(TouchUi.R(at.x - 40, at.y - 6, 80, 12), t, t.Length <= 4 ? 10 : 7, TextAnchor.MiddleCenter, c, true);
            }
        }

        void DrawBubble()
        {
            if (bubbleSide < 0)
            {
                return;
            }
            float k = TouchUi.K;
            float age = Time.unscaledTime - bubbleTime;
            float alpha = age < 1.8f ? 1f : Mathf.Clamp01(1f - (age - 1.8f) / 0.4f);
            Rect b = bubbleSide == 0 ? TouchUi.R(2, 168, 120, 30) : TouchUi.R(58, 168, 120, 30);
            float tail = bubbleSide == 0 ? 19 : 161;
            Color line = new Color(0.01f, 0.01f, 0.01f, alpha), fill = new Color(1f, 1f, 1f, alpha);
            TouchUi.Fill(new Rect(b.x + k, b.y, b.width - 2 * k, b.height), line);
            TouchUi.Fill(new Rect(b.x, b.y + k, b.width, b.height - 2 * k), line);
            TouchUi.Fill(new Rect(b.x + 2 * k, b.y + k, b.width - 4 * k, b.height - 2 * k), fill);
            TouchUi.Fill(new Rect(b.x + k, b.y + 2 * k, b.width - 2 * k, b.height - 4 * k), fill);
            Vector2 tv = TouchUi.V(new Vector2(0, b.yMax));
            for (int i = 0; i < 6; i++)
            {
                float w = 6 - i;
                Rect row = TouchUi.R(tail - w / 2f - 1, tv.y + i, w + 2, 1);
                TouchUi.Fill(row, line);
                if (i < 5)
                {
                    TouchUi.Fill(TouchUi.R(tail - w / 2f, tv.y - 1 + i, w, 1), fill);
                }
            }
            Rect text = new Rect(b.x + 5 * k, b.y + 2 * k, b.width - 10 * k, b.height - 4 * k);
            TouchUi.Label(text, bubbleText, 7.5f, TextAnchor.MiddleCenter, new Color(0.1f, 0.04f, 0.1f, alpha), true, true);
        }

        void DrawPopup()
        {
            float k = TouchUi.K;
            TouchUi.Dim(0.55f);
            Rect p = PopupRect();
            TouchUi.Panel(p);
            Vector2 top = TouchUi.V(new Vector2(p.x, p.y));
            float py = top.y;
            int[] slots = Loadout.Get();
            bool usable = PopupUsable();
            string note;
            Rect anchorButton;
            if (popupKind == 1)
            {
                int i = popupArg;
                anchorButton = SkillRect(i);
                TouchUi.PixelText(C_PortUi.skillName(i).ToUpperInvariant(), 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, py + 8, 0, 0).y, 1);
                J2ME.Image icon = C_PortUi.skillIcon(i);
                Rect ic = TouchUi.R(13, py + 25, 64, 64);
                TouchUi.Fill(ic, TouchUi.Outline);
                if (icon != null)
                {
                    TouchUi.Sprite(SkillTexture(i, icon), new Rect(ic.x + 2 * k, ic.y + 2 * k, 60 * k, 60 * k));
                }
                int id = C_PortUi.skillId(0, i);
                TouchUi.Label(TouchUi.R(82, py + 27, 90, 10), "Tuyệt chiêu " + (i + 1), 7, TextAnchor.UpperLeft, TouchUi.TextSoft);
                TouchUi.Label(TouchUi.R(82, py + 39, 90, 10), "Hệ " + Elements[Mathf.Clamp(id / 3, 0, 2)], 7, TextAnchor.UpperLeft, TouchUi.TextSoft);
                TouchUi.Fill(TouchUi.R(82, py + 56, 5, 5), TouchUi.Hex(0x3C7CE8));
                TouchUi.Label(TouchUi.R(90, py + 53, 84, 10), "Cần " + C_PortUi.skillCost(i) + " năng lượng", 7, TextAnchor.UpperLeft, TouchUi.Hex(0x9FD0FF));
                TouchUi.Label(TouchUi.R(90, py + 65, 84, 10), "Đang có " + C_PortUi.mana(0), 7, TextAnchor.UpperLeft, TouchUi.Hex(0x9FD0FF));
                TouchUi.Label(TouchUi.R(14, py + 96, 152, 46), C_PortUi.skillDesc(i), 7.5f, TextAnchor.UpperLeft, Color.white, false, true);
                if (!C_PortUi.waiting())
                {
                    note = "Chờ đến lượt của bạn";
                }
                else if (!SkillAffordable(i))
                {
                    note = "Cần thêm " + Mathf.Max(0, C_PortUi.skillCost(i) - C_PortUi.mana(0)) + " năng lượng";
                }
                else
                {
                    note = "";
                }
            }
            else
            {
                int type = slots[popupArg];
                anchorButton = ItemRect(popupArg);
                TouchUi.PixelText(C_PortUi.itemName(type).ToUpperInvariant(), 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, py + 8, 0, 0).y, 1);
                Rect ic = TouchUi.R(13, py + 25, 60, 60);
                TouchUi.Item(type, ic);
                int count = C_PortUi.itemCount(type);
                TouchUi.Label(TouchUi.R(82, py + 30, 90, 10), "Còn " + count, 7.5f, TextAnchor.UpperLeft, TouchUi.TextGreen, true);
                TouchUi.Label(TouchUi.R(82, py + 44, 90, 22), "Mỗi lượt dùng 1 vật phẩm", 7, TextAnchor.UpperLeft, TouchUi.TextSoft, false, true);
                string desc = C_PortUi.itemDesc(type);
                if (type == 4)
                {
                    desc += " (+" + Mathf.Max(1, C_PortUi.maxHp(0) / 10) + " máu)";
                }
                TouchUi.Label(TouchUi.R(14, py + 94, 152, 50), desc, 7.5f, TextAnchor.UpperLeft, Color.white, false, true);
                if (!C_PortUi.waiting())
                {
                    note = "Chờ đến lượt của bạn";
                }
                else if (count <= 0)
                {
                    note = "Đã hết, mua thêm ở màn chuẩn bị";
                }
                else if (C_PortUi.itemUsed())
                {
                    note = "Lượt này đã dùng 1 vật phẩm";
                }
                else
                {
                    note = "";
                }
            }
            Rect close = PopupClose(), use = PopupUse();
            if (note.Length > 0)
            {
                Vector2 nv = TouchUi.V(new Vector2(0, use.y));
                TouchUi.Label(TouchUi.R(14, nv.y - 13, 152, 10), note, 7, TextAnchor.UpperLeft, TouchUi.Hex(0xFF9A80));
            }
            TouchUi.Button(close, Btn.Grey, downKind == 5);
            TouchUi.PixelText("ĐÓNG", 2, close.center.x, close.center.y, 1, true);
            TouchUi.RedButton(use, "DÙNG", usable, downKind == 4 && usable);
            TouchUi.Pointer(anchorButton.center.x, p.yMax);
            Vector2 hint = TouchUi.V(new Vector2(0, p.y));
            TouchUi.Label(TouchUi.R(0, hint.y - 14, 180, 10), "Chạm ra ngoài để đóng", 7, TextAnchor.MiddleCenter, TouchUi.Hex(0xD8D0E0));
        }
    }
}
