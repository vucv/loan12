// Screen 6 of the touch redesign (plan/thiet-ke-giao-dien.md): the battle.
//
//   whose turn + pause / the board (cut from the game's screen) / a line describing what happened /
//   the game's own fighter strip (player left, enemy right; health, time, energy bars as before) /
//   skills / the items taken into battle; over the meander background of the game's battle screen.
// Effects: numbers rising from the bars (damage, healing, energy), a line of text, item effect badges
// on the portraits and speech bubbles (hurt, low health, skills, start and end of the battle). One tap
// on a skill or an item opens a popup (description, USE, CLOSE; a tap anywhere else closes it).
// At the end the board freezes and THẮNG / THUA plays over it until the result screen is ready.
// The game keeps the rules, the AI and the animations on the board. The board and the fighter strip are
// copied from the game's screen while nothing covers them, so a dialog of the game never shows through
// (the pause and the result are Unity screens drawn over this one).
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBattleView : ITouchView
    {
        const int GridH = 346;

        // the game's fighter strip on its 240x320 screen: both portraits and their bars (x 33..206)
        const int StripX = 33, StripY = 228, StripW = 174, StripH = 50;
        const float StripTop = 216; // where it goes on the grid (x 3..176, centred)

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
        static readonly string[] WinLines = {"Ha ha! Biết lợi hại của ta chưa!", "Thắng rồi!", "Kẻ nào dám cản đường ta nữa?"};
        static readonly string[] GloatLines = {"Ngươi còn non lắm!", "Về luyện thêm đi!", "Ha ha! Chỉ có vậy thôi sao?"};

        // input
        bool boardHeld;
        int downKind, downArg; // 1 skill, 2 item, 3 pause, 4 popup USE, 5 popup CLOSE, 6 outside popup
        public int popupKind, popupArg; // 1 skill slot, 2 item slot
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
        bool bubbleRaw;                 // the text is already in the current language (Banter)
        int pendingSide = -1;           // a reply said a moment later (the other fighter answers)
        string pendingText;
        float pendingAt, nextTaunt;

        // the board and the fighter strip as last seen without anything on top (BGRA rows, top first)
        Texture2D boardSnap, stripSnap;

        // end of the battle: 0 playing, 1 won, 2 lost
        int outcome;
        float endStart;

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

        /// <summary>
        /// The item buttons (2 to 4): the items chosen before the battle; in the tutorial (no preparing screen)
        /// the items the battle holds, so its "use an item" step can be done.
        /// </summary>
        static int[] Slots()
        {
            if (!C_PortScreens.training())
            {
                int[] chosen = Loadout.Get();
                itemN = chosen.Length;
                for (int i = 0; i < chosen.Length; i++)
                {
                    if (chosen[i] == 5)
                    {
                        chosen[i] = -1; // Ngọc Ấn works before the battle, not in it
                    }
                }
                return chosen;
            }
            int[] r = {-1, -1, -1};
            itemN = 3;
            int n = 0;
            foreach (int t in new[] {1, 0, 3, 4})
            {
                if (n < 3 && C_PortUi.itemCount(t) > 0)
                {
                    r[n++] = t;
                }
            }
            return r;
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
            Layout();
            if (Time.frameCount != lastFrame)
            {
                lastFrame = Time.frameCount;
                TrackEvents();
                Snapshot();
            }
            HandleInput(e, haptics);
            if (e.type == EventType.Repaint)
            {
                Draw(lcd);
            }
        }

        void Layout()
        {
            TouchUi.Begin(GridH);
            bx = C_PortUi.board(0);
            by = C_PortUi.board(1);
            bw = C_PortUi.board(2);
            bh = C_PortUi.board(3);
            Rect r = TouchUi.R(90 - bw / 2f, 24, bw, bh);
            boardRect = new Rect(Mathf.Round(r.x), Mathf.Round(r.y), r.width, r.height);
        }

        /// <summary>
        /// The battle as it stands, without effects or popups: the background of the pause and result
        /// screens. Leaves the grid set up for the battle's layout.
        /// </summary>
        public void DrawBackdrop(Texture2D lcd)
        {
            Layout();
            DrawScene(lcd);
        }

        // ---------------------------------------------------------------------------------- snapshots

        /// <summary>Copies the board and the fighter strip while the game's screen shows only the battle.</summary>
        void Snapshot()
        {
            int[] screen = J2ME.Host.screen;
            if (screen == null || outcome != 0 || C_PortUi.dialog() != null || C_PortUi.transition())
            {
                return; // keep the last clean picture (a dialog, or THẮNG / THUA, is drawn over it)
            }
            Copy(screen, ref boardSnap, bx, by, bw, bh);
            Copy(screen, ref stripSnap, StripX, StripY, StripW, StripH);
        }

        static void Copy(int[] screen, ref Texture2D snap, int x, int y, int w, int h)
        {
            const int W = J2MEGameHost.LcdWidth;
            if (snap == null || snap.width != w || snap.height != h)
            {
                if (snap != null)
                {
                    Object.Destroy(snap);
                }
                snap = new Texture2D(w, h, TextureFormat.BGRA32, false);
                snap.filterMode = FilterMode.Point;
                snap.wrapMode = TextureWrapMode.Clamp;
            }
            Unity.Collections.NativeArray<int> dst = snap.GetRawTextureData<int>();
            for (int row = 0; row < h; row++)
            {
                Unity.Collections.NativeArray<int>.Copy(screen, (y + row) * W + x, dst, row * w, w);
            }
            snap.Apply(false, false);
        }

        /// <summary>A snapshot (rows stored top first, like the game's screen) into a screen rectangle.</summary>
        static void DrawSnap(Texture2D snap, Rect dst)
        {
            Gx.DrawTextureWithTexCoords(dst, snap, new Rect(0f, 1f, 1f, -1f), false);
        }

        // ---------------------------------------------------------------------------------- rects

        static Rect PauseRect()
        {
            return TouchUi.R(158, 0, 20, 20);
        }

        static Rect PillRect()
        {
            return TouchUi.R(40, 1, 100, 18);
        }

        // three equal buttons across the screen, 2 px from each edge
        static Rect SkillRect(int i)
        {
            return TouchUi.R(2 + i * 60, 272, 56, 36);
        }

        // the item buttons: 2 to 4 (Packs.ItemSlots) equal ones across the screen, 4 px apart
        static int itemN = 3;

        static Rect ItemRect(int i)
        {
            float w = (176f - (itemN - 1) * 4f) / itemN;
            return TouchUi.R(2 + i * (w + 4), 311, w, 34);
        }

        // The popup grows with its description: header (name, picture, details) 96, the description,
        // a note when the button cannot be used, the buttons. It sits right above its button.
        const float PopupDescW = 152;

        Rect PopupRect()
        {
            int lines = TouchUi.Lines(PopupDesc(), PopupDescW);
            float h = 96 + lines * UiFont.LineHeight + 6 + (PopupNote().Length > 0 ? 15 : 0) + 38;
            float bottom = popupKind == 1 ? 264 : 303;
            return TouchUi.R(6, bottom - h, 168, h);
        }

        // the two popup buttons: same size, side by side, centred
        Rect PopupUse()
        {
            Rect p = PopupRect();
            Vector2 v = TouchUi.V(new Vector2(p.x, p.yMax));
            return TouchUi.R(94, v.y - 34, 72, 26);
        }

        Rect PopupClose()
        {
            Rect p = PopupRect();
            Vector2 v = TouchUi.V(new Vector2(p.x, p.yMax));
            return TouchUi.R(14, v.y - 34, 72, 26);
        }

        string PopupDesc()
        {
            if (popupKind == 1)
            {
                return C_PortUi.skillDesc(popupArg);
            }
            int type = Slots()[popupArg];
            string desc = type >= 0 ? C_PortUi.itemDesc(type) : "";
            if (type == 4)
            {
                desc += " (+" + Mathf.Max(1, C_PortUi.maxHp(0) / 10) + " máu)";
            }
            return desc;
        }

        string PopupNote()
        {
            if (!C_PortUi.waiting())
            {
                return "Chờ đến lượt của bạn";
            }
            if (popupKind == 1)
            {
                return SkillAffordable(popupArg) ? "" : "Cần thêm " + Mathf.Max(0, C_PortUi.skillCost(popupArg) - C_PortUi.mana(0)) + " năng lượng";
            }
            int type = Slots()[popupArg];
            if (type < 0 || C_PortUi.itemCount(type) <= 0)
            {
                return "Đã hết, mua thêm ở màn chuẩn bị";
            }
            return C_PortUi.itemUsed() ? "Lượt này đã dùng 1 vật phẩm" : "";
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
            int[] slots = Slots();
            if (outcome != 0)
            {
                // the battle is over: nothing to press while THẮNG / THUA plays
                if (e.type == EventType.MouseDown || e.type == EventType.MouseUp || e.type == EventType.MouseDrag)
                {
                    e.Use();
                }
                return;
            }
            if (C_PortScreens.training() && C_PortScreens.coachWaits() && popupKind == 0)
            {
                if (e.type == EventType.MouseDown && e.button == 0)
                {
                    C_PortScreens.request(C_PortScreens.f_COACH_NEXT, 0);
                    Haptics.Tap(haptics);
                    e.Use();
                }
                else if (e.type == EventType.MouseUp || e.type == EventType.MouseDrag)
                {
                    e.Use();
                }
                return;
            }
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
                    for (int i = 0; i < Mathf.Max(3, slots.Length); i++)
                    {
                        if (i < 3 && SkillRect(i).Contains(p) && i < C_PortUi.skillCount())
                        {
                            downKind = 1;
                            downArg = i;
                        }
                        if (i < slots.Length && ItemRect(i).Contains(p) && slots[i] >= 0)
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
            int[] slots = Slots();
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
            return new Vector2(side == 0 ? 61 : 118, StripTop + 16);
        }

        static Vector2 ManaAt(int side)
        {
            return new Vector2(side == 0 ? 61 : 118, StripTop + 34);
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
                outcome = 0;
                pendingSide = -1;
                SayB(1, B(1, "start"), Pick(StartLines), true);
                Later(0, B(0, "start"), 1.8f);
                lostAcc[0] = lostAcc[1] = healAcc[0] = healAcc[1] = 0;
                nextTaunt = Time.unscaledTime + 35f;
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
                    // the move's damage adds up; what is said comes when it has settled (React)
                    lostAcc[s] += -dh;
                    accAt[s] = Time.unscaledTime;
                    blockedAcc[s] = blockedAcc[s] || C_PortUi.effect(s, 3) > 0;
                }
                else if (dh > 0)
                {
                    Float(s * 10 + 2, "+", dh, " máu", TouchUi.Hex(0x7CE05A), HpAt(s));
                    Tick(who + " hồi " + dh + " máu");
                    if (prevHp[s] <= 0 && s == 0)
                    {
                        SayB(0, B(0, "revive"), Pick(HealLines), true); // back on his feet (the second chance)
                    }
                    else
                    {
                        healAcc[s] += dh;
                        accAt[s] = Time.unscaledTime;
                    }
                }
                prevHp[s] = hp;
                if (!lowSaid[s] && hp > 0 && hp * 5 < maxHp)
                {
                    lowSaid[s] = true;
                    SayB(s, B(s, "low"), LowLines[s][0], true);
                    if (rnd.Next(2) == 0)
                    {
                        Later(1 - s, B(1 - s, "taunt"), 1.8f); // sometimes the other one rubs it in
                    }
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
                        SayB(s, B(s, "skill", Loc.T(skill)), "Nếm thử " + skill + "!", true);
                    }
                }
                prevMana[s] = mana;

                int shield = C_PortUi.effect(s, 3), sword = C_PortUi.effect(s, 0);
                if (shield > 0 && prevShield[s] == 0)
                {
                    Tick("Quỷ Diện Giáp chặn 3/4 đòn");
                }
                if (sword > 0 && prevSword[s] == 0)
                {
                    Tick("Long Thần Kiếm: đánh x2");
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
                    string said = B(0, "item");
                    if (said != null)
                    {
                        Say(0, said, true);
                    }
                }
                prevItems[t] = c;
            }
            int now = C_PortUi.outcome();
            if (now != outcome)
            {
                outcome = now;
                endStart = Time.unscaledTime;
                if (outcome != 0)
                {
                    popupKind = 0;
                    downKind = 0;
                    if (boardHeld)
                    {
                        J2ME.Host.pointerReleased(bx + bw / 2, by + bh / 2);
                        boardHeld = false;
                    }
                    bubbleSide = -1; // the last word: the winner, then the loser
                    pendingSide = -1;
                    int winner = outcome == 1 ? 0 : 1;
                    SayB(winner, B(winner, "win"), Pick(outcome == 1 ? WinLines : GloatLines), true);
                    Later(1 - winner, B(1 - winner, "lose"), 1.9f);
                    Tick(outcome == 1 ? C_PortUi.name(0) + " đã chiến thắng!" : C_PortUi.name(0) + " đã thua trận");
                }
            }
            // what a move did, once its hits and heals have settled
            float now0 = Time.unscaledTime;
            for (int s = 0; s < 2; s++)
            {
                if ((lostAcc[s] > 0 || healAcc[s] > 0) && now0 - accAt[s] > 0.7f)
                {
                    React(s);
                }
            }
            // a reply due, or (rarely) some small talk when nobody has spoken for a long while
            if (pendingSide >= 0 && now0 >= pendingAt && (bubbleSide < 0 || now0 - bubbleTime > 1.6f))
            {
                bubbleSide = -1;
                Say(pendingSide, pendingText, true);
                pendingSide = -1;
            }
            else if (outcome == 0 && pendingSide < 0 && now0 >= nextTaunt && (bubbleSide < 0 || now0 - bubbleTime > 2.2f))
            {
                int side = rnd.Next(2);
                string said = B(side, "taunt");
                if (said != null)
                {
                    Say(side, said, true);
                }
                nextTaunt = now0 + 30f + (float) rnd.NextDouble() * 20f; // small talk only now and then
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

        void Say(int side, string text, bool raw = false, bool key = false)
        {
            // one bubble at a time; the ordinary ones not too often (key moments: start, skill, low, win)
            float since = Time.unscaledTime - bubbleTime;
            if (string.IsNullOrEmpty(text) || (bubbleSide >= 0 && since < 1.2f) || (!key && since < 3.5f))
            {
                return;
            }
            bubbleSide = side;
            bubbleText = text;
            bubbleTime = Time.unscaledTime;
            bubbleRaw = raw;
            nextTaunt = Mathf.Max(nextTaunt, Time.unscaledTime + 25f);
        }

        /// <summary>A Banter line (already in the current language), or the old Vietnamese one when there is none.</summary>
        void SayB(int side, string banter, string fallback, bool key = false)
        {
            if (banter != null)
            {
                Say(side, banter, true, key);
            }
            else
            {
                Say(side, fallback, false, key);
            }
        }

        // a move's effect on each side, added up until it settles
        readonly int[] lostAcc = new int[2], healAcc = new int[2];
        readonly float[] accAt = new float[2];
        readonly bool[] blockedAcc = new bool[2];

        /// <summary>What a move did to side s, said once: a heavy blow makes the attacker brag (or the victim
        /// cry out), a shield that took most of it, a fair hit, a big heal; small changes pass in silence.</summary>
        void React(int s)
        {
            int maxHp = Mathf.Max(1, C_PortUi.maxHp(s));
            int lost = lostAcc[s], healed = healAcc[s];
            bool blocked = blockedAcc[s];
            lostAcc[s] = healAcc[s] = 0;
            blockedAcc[s] = false;
            if (lost > 0 && C_PortUi.hp(s) <= 0)
            {
                return; // the end of the battle speaks
            }
            if (lost * 100 >= maxHp * 22)
            {
                // a heavy blow: the attacker brags, or the one hit cries out
                if (rnd.Next(2) == 0)
                {
                    SayB(1 - s, B(1 - s, "combo"), Pick(GloatLines));
                }
                else
                {
                    SayB(s, B(s, "hurt"), Pick(HurtLines[s]));
                }
            }
            else if (blocked && lost > 0 && lost * 100 < maxHp * 10)
            {
                SayB(s, B(s, "block"), Pick(BlockLines));
            }
            else if (lost * 100 >= maxHp * 12)
            {
                SayB(s, B(s, "hurt"), Pick(HurtLines[s]));
            }
            else if (healed * 100 >= maxHp * 15)
            {
                SayB(s, B(s, "heal"), Pick(HealLines));
            }
        }

        /// <summary>What a fighter says at this moment (Banter, in the current language), or null.</summary>
        string B(int side, string evt, string skill = null)
        {
            string s = null;
            try
            {
                s = Banter.Line(C_PortUi.portrait(side), evt, C_PortUi.name(side), C_PortUi.name(1 - side), skill);
            }
            catch (System.Exception)
            {
                s = null;
            }
            return s;
        }

        /// <summary>A line said a moment later (an answer); a newer one replaces it.</summary>
        void Later(int side, string text, float delay)
        {
            if (string.IsNullOrEmpty(text))
            {
                return;
            }
            pendingSide = side;
            pendingText = text;
            pendingAt = Time.unscaledTime + delay;
        }

        void Tick(string msg)
        {
            // several things at once share the line while they fit on it
            if (Time.unscaledTime - tickerTime < 0.6f && ticker.Length > 0 && !ticker.Contains(msg) && TouchUi.TextWidth(ticker + ", " + msg, 6.5f, false) <= 176)
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
            DrawScene(lcd);
            DrawFloaters();
            if (outcome != 0)
            {
                DrawEnd();
            }
            DrawBubble();
            DrawCoach();
            if (popupKind != 0)
            {
                DrawPopup();
            }
        }

        /// <summary>
        /// Tutorial battle: the coach's message in a speech bubble from Lưu Cơ, the opponent of this battle
        /// (right portrait), like the fighters' own lines, above the portraits; its height follows the
        /// text. The game's lessons about keys and its old menus are told the touch way (CoachText).
        /// </summary>
        void DrawCoach()
        {
            string text = C_PortScreens.training() ? CoachText(C_PortScreens.coach()) : null;
            if (string.IsNullOrEmpty(text))
            {
                return;
            }
            float k = TouchUi.K;
            bool waits = C_PortScreens.coachWaits();
            const float size = 7f, textW = 146;
            float lh = TouchUi.LineHeight(size);
            int lines = Mathf.Max(1, TouchUi.Lines(text, textW, size));
            float h = 12 + lines * lh + (waits ? 12 : 5);
            int gem = C_PortScreens.coachGem();
            float y = StripTop - 8 - h; // the tail ends at the portraits
            Rect b = TouchUi.R(8, y, 164, h);
            Color line = new Color(0.01f, 0.01f, 0.01f, 1f), fill = new Color(1f, 1f, 1f, 1f);
            BubbleShape(b, 161, line, fill);
            TouchUi.Label(TouchUi.R(15, y + 3, 100, 9), "Lưu Cơ", 5.75f, TextAnchor.UpperLeft, TouchUi.Hex(0x9A5A12), true, false, false);
            TouchUi.Label(TouchUi.R(15, y + 12, textW, lines * lh + 2), text, size, TextAnchor.UpperLeft, new Color(0.1f, 0.04f, 0.1f, 1f), true, true, false);
            if (gem >= 0)
            {
                string[] gems = {"/sword", "/rice", "/heart", "/yinyang", "/gold", "/book", "/swordred"}; // bs.d
                if (gem < gems.Length)
                {
                    float bob = Mathf.Round(Mathf.Sin(Time.unscaledTime * 6) * 1.5f);
                    TouchUi.Sprite(TouchUi.Img(gems[gem]), TouchUi.R(154, y + 1 + bob, 10, 10)); // the gem to line up, top right
                }
            }
            if (waits)
            {
                float a = 0.55f + 0.45f * Mathf.Sin(Time.unscaledTime * 5);
                TouchUi.Label(TouchUi.R(15, y + h - 12, textW, 9), "Chạm để tiếp tục", 5.75f, TextAnchor.UpperRight, new Color(0.47f, 0.23f, 0.46f, a), true, false, false);
            }
        }

        /// <summary>The tutorial's lines that speak of keys, the T.Chiêu menu or the Túi menu, told for touch.</summary>
        static readonly System.Collections.Generic.Dictionary<string, string> TouchLessons = new System.Collections.Generic.Dictionary<string, string>
        {
            {"Hãy chạm vào thanh kiếm (hoặc bấm phím 5) để chọn", "Hãy chạm vào thanh kiếm để chọn"},
            {"Vuốt xuống hoặc chạm ô bên dưới (phím 8) để đổi chỗ", "Vuốt xuống hoặc chạm ô bên dưới để đổi chỗ"},
            {"Tướng quân hãy mở bảng phép thuật bằng cách nhấn phím 'T.Chiêu' ,chọn một loại tuyệt chiêu và bấm 'Dùng'.",
                "Tướng quân hãy chạm vào một tuyệt chiêu ở hàng dưới các chân dung, rồi bấm 'Dùng'."},
            {"Tướng quân đã được thưởng 1 vật phẩm, hãy chạm 'Túi' (hoặc bấm phím #) để chọn và sử dụng vật phẩm.",
                "Tướng quân đã được thưởng 1 vật phẩm. Hãy chạm vào vật phẩm ở hàng dưới cùng, rồi bấm 'Dùng'."}
        };

        static string CoachText(string s)
        {
            string t;
            return s != null && TouchLessons.TryGetValue(s.Trim(), out t) ? t : s;
        }

        /// <summary>A white speech bubble with a dark pixel outline and a tail at grid x = tail, down (or up).</summary>
        static void BubbleShape(Rect b, float tail, Color line, Color fill, bool tailUp = false)
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(b.x + k, b.y, b.width - 2 * k, b.height), line);
            TouchUi.Fill(new Rect(b.x, b.y + k, b.width, b.height - 2 * k), line);
            TouchUi.Fill(new Rect(b.x + 2 * k, b.y + k, b.width - 4 * k, b.height - 2 * k), fill);
            TouchUi.Fill(new Rect(b.x + k, b.y + 2 * k, b.width - 2 * k, b.height - 4 * k), fill);
            Vector2 tv = TouchUi.V(new Vector2(0, tailUp ? b.y : b.yMax));
            float dir = tailUp ? -1 : 1;
            for (int i = 0; i < 6; i++)
            {
                float w = 6 - i;
                TouchUi.Fill(TouchUi.R(tail - w / 2f - 1, tv.y + dir * i - (tailUp ? 1 : 0), w + 2, 1), line);
                if (i < 5)
                {
                    TouchUi.Fill(TouchUi.R(tail - w / 2f, tv.y + dir * (i - 1) - (tailUp ? 1 : 0), w, 1), fill);
                }
            }
        }

        /// <summary>Background, turn, board, the line of text, the fighters, skills and items.</summary>
        void DrawScene(Texture2D lcd)
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Background);
            TouchUi.Wallpaper();

            // whose turn + pause
            bool mine = C_PortUi.waiting() && outcome == 0;
            int turn = C_PortUi.turn();
            Rect pill = PillRect();
            TouchUi.Fill(pill, TouchUi.Outline);
            Color pc0 = outcome == 1 ? TouchUi.Hex(0xB97A14) : (outcome == 2 ? TouchUi.Hex(0x4E4A50)
                : (mine ? TouchUi.Hex(0x2F8A22) : (turn == 1 ? TouchUi.Hex(0xA3201B) : TouchUi.Hex(0x4E4A50))));
            TouchUi.Fill(new Rect(pill.x + k, pill.y + k, pill.width - 2 * k, pill.height - 2 * k), pc0);
            TouchUi.Fill(new Rect(pill.x + 2 * k, pill.y + k, pill.width - 4 * k, k), new Color(1f, 1f, 1f, 0.25f));
            string turnText = outcome == 1 ? "CHIẾN THẮNG" : (outcome == 2 ? "THẤT BẠI" : (mine ? "LƯỢT CỦA BẠN" : (turn == 1 ? "LƯỢT ĐỐI THỦ" : "...")));
            TouchUi.Label(pill, turnText, 7.5f, TextAnchor.MiddleCenter, Color.white, true);
            Rect pr = PauseRect();
            bool canPause = mine;
            TouchUi.Button(pr, canPause ? Btn.Dark : Btn.Grey, downKind == 3);
            Color pc = canPause ? TouchUi.Hex(0xF0E8D8) : TouchUi.Hex(0x6A6070);
            TouchUi.Fill(new Rect(pr.x + 6 * k, pr.y + 5 * k, 3 * k, 10 * k), pc);
            TouchUi.Fill(new Rect(pr.x + 11 * k, pr.y + 5 * k, 3 * k, 10 * k), pc);

            // the board, cut from the game's screen, in a frame like the game's (dark, gold, dark)
            Rect f = new Rect(boardRect.x - 3 * k, boardRect.y - 3 * k, boardRect.width + 6 * k, boardRect.height + 6 * k);
            TouchUi.Fill(f, TouchUi.Outline);
            TouchUi.Fill(new Rect(f.x + k, f.y + k, f.width - 2 * k, f.height - 2 * k), TouchUi.Hex(0xF5B735));
            TouchUi.Fill(new Rect(f.x + 2 * k, f.y + 2 * k, f.width - 4 * k, f.height - 4 * k), TouchUi.Outline);
            if (boardSnap != null)
            {
                DrawSnap(boardSnap, boardRect);
            }
            else
            {
                DrawLcd(lcd, boardRect, bx, by, bw, bh);
            }

            // what happened
            Rect line = TouchUi.R(0, 198, 180, 16);
            TouchUi.Fill(line, new Color(0.05f, 0.02f, 0.05f, 0.72f));
            if (ticker.Length > 0)
            {
                float age = Time.unscaledTime - tickerTime;
                Color tc = TouchUi.TextGold;
                tc.a = age < 4f ? 1f : Mathf.Clamp01(1f - (age - 4f));
                TouchUi.Label(line, ticker, 6.5f, TextAnchor.MiddleCenter, tc);
            }

            // the game's fighter strip: portraits and bars exactly as in the original, on its brown panel
            TouchUi.Fill(TouchUi.R(0, StripTop - 1, 180, 1), TouchUi.Outline);
            TouchUi.Fill(TouchUi.R(0, StripTop, 180, StripH), TouchUi.Hex(0x8B5A49));
            TouchUi.Fill(TouchUi.R(0, StripTop + StripH, 180, 1), TouchUi.Outline);
            TouchUi.Fill(TouchUi.R(0, StripTop + StripH + 1, 180, 2), TouchUi.Hex(0xF5B735));
            TouchUi.Fill(TouchUi.R(0, StripTop + StripH + 3, 180, 1), TouchUi.Outline);
            Rect strip = TouchUi.R(90 - StripW / 2f, StripTop, StripW, StripH);
            if (stripSnap != null)
            {
                DrawSnap(stripSnap, strip);
            }
            else
            {
                DrawLcd(lcd, strip, StripX, StripY, StripW, StripH);
            }
            DrawEffectBadges();

            // skills
            int n = C_PortUi.skillCount();
            for (int i = 0; i < 3; i++)
            {
                Rect r = SkillRect(i);
                bool usable = SkillEnabled(i) && outcome == 0;
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
                    Color old = Gx.color;
                    Gx.color = old * (usable ? Color.white : new Color(0.5f, 0.47f, 0.52f, 1f));
                    TouchUi.Sprite(SkillTexture(i, icon), new Rect(r.x + 3 * k, r.y + 3 * k, 30 * k, 30 * k));
                    Gx.color = old;
                }
                TouchUi.Fill(new Rect(r.x + 44 * k, r.y + 6 * k, 5 * k, 5 * k), TouchUi.Hex(0x3C7CE8));
                TouchUi.Label(new Rect(r.x + 34 * k, r.y + 15 * k, 18 * k, 14 * k), C_PortUi.skillCost(i).ToString(), 8.5f, TextAnchor.MiddleCenter,
                    usable ? TouchUi.TextGold : TouchUi.Hex(0x9A90A0), true);
            }

            // items taken into battle
            int[] slots = Slots();
            for (int i = 0; i < slots.Length; i++)
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
                if (r.width >= 50 * k)
                {
                    TouchUi.Item(type, new Rect(r.x + 3 * k, r.y + 3 * k, 28 * k, 28 * k), count > 0 ? (ItemEnabled(type) ? 1f : 0.75f) : 0.35f);
                    TouchUi.Label(new Rect(r.x + 32 * k, r.y + 10 * k, r.width - 34 * k, 14 * k), "x" + count, 8.5f, TextAnchor.MiddleCenter,
                        count > 0 ? Color.white : TouchUi.Hex(0x8A8090), true);
                }
                else
                {
                    // narrow (4 buttons): the item in the middle, the count in its corner
                    TouchUi.Item(type, new Rect(r.center.x - 14 * k, r.y + 3 * k, 28 * k, 28 * k), count > 0 ? (ItemEnabled(type) ? 1f : 0.75f) : 0.35f);
                    TouchUi.Label(new Rect(r.x, r.y, r.width - 3 * k, r.height - 2 * k), "x" + count, 7, TextAnchor.LowerRight,
                        count > 0 ? Color.white : TouchUi.Hex(0x8A8090), true);
                }
            }
        }

        /// <summary>THẮNG / THUA over the frozen board (the game's own banner, bigger and animated).</summary>
        void DrawEnd()
        {
            float k = TouchUi.K;
            float t = Time.unscaledTime - endStart;
            bool won = outcome == 1;
            TouchUi.Fill(boardRect, won ? new Color(0.06f, 0.02f, 0.02f, Mathf.Min(0.6f, t * 2f)) : new Color(0.02f, 0.02f, 0.05f, Mathf.Min(0.72f, t * 2f)));

            Texture2D img = TouchUi.Img(won ? "/strwin" : "/strlose");
            int fw = img.width / 3, fh = img.height;
            Vector2 c = boardRect.center;
            float p = Mathf.Clamp01(t / 0.45f);
            float scale, dy = 0;
            if (won)
            {
                scale = Mathf.LerpUnclamped(2.4f, 1f, EaseOutBack(p)) * (1f + 0.025f * Mathf.Sin(t * 5f));
            }
            else
            {
                scale = 1.1f;
                dy = -(1f - EaseOutBounce(p)) * 90f; // falls in from above
            }
            float w = fw * scale * k, h = fh * scale * k;
            Rect dst = new Rect(c.x - w / 2f, c.y - h / 2f + dy * k, w, h);
            int frame = t < 0.18f ? 0 : 1 + (int) ((t - 0.18f) / 0.16f) % 2;

            Color old = Gx.color;
            if (won)
            {
                // a burst of light when the banner lands, then gold sparkles around it
                float bt = (t - 0.3f) / 0.5f;
                if (bt > 0f && bt < 1f)
                {
                    Texture2D burst = TouchUi.Img("/m/e2");
                    float bs = (1.4f + 1.6f * bt) * k;
                    Gx.color = old * new Color(1f, 0.9f, 0.55f, 1f - bt);
                    TouchUi.Sprite(burst, new Rect(c.x - burst.width * bs / 2f, c.y - burst.height * bs / 2f, burst.width * bs, burst.height * bs));
                    Gx.color = old;
                }
                if (t > 0.4f)
                {
                    for (int i = 0; i < 12; i++)
                    {
                        float a = i * 2.39996f; // golden angle: spread around the banner
                        float rx = Mathf.Cos(a) * (58 + 16 * (i % 3)), ry = Mathf.Sin(a) * (34 + 12 * (i % 2));
                        float tw = Mathf.Sin(t * 6f + i * 1.7f);
                        if (tw > 0f)
                        {
                            Sparkle(new Vector2(c.x + rx * k, c.y + ry * k), 1 + Mathf.RoundToInt(2 * tw), new Color(1f, 0.86f, 0.35f, tw));
                        }
                    }
                }
            }
            Gx.color = old * new Color(1f, 1f, 1f, Mathf.Clamp01(t / 0.12f));
            TouchUi.Sprite(img, dst, new Rect(frame * fw, 0, fw, fh));
            Gx.color = old;
        }

        /// <summary>A pixel twinkle: a cross of arm pixels with a white centre.</summary>
        static void Sparkle(Vector2 at, int arm, Color c)
        {
            float k = TouchUi.K;
            float x = Mathf.Round(at.x / k) * k, y = Mathf.Round(at.y / k) * k;
            TouchUi.Fill(new Rect(x - arm * k, y, (2 * arm + 1) * k, k), c);
            TouchUi.Fill(new Rect(x, y - arm * k, k, (2 * arm + 1) * k), c);
            TouchUi.Fill(new Rect(x, y, k, k), new Color(1f, 1f, 1f, c.a));
        }

        static float EaseOutBack(float x)
        {
            const float c1 = 1.70158f, c3 = c1 + 1f;
            float u = x - 1f;
            return 1f + c3 * u * u * u + c1 * u * u;
        }

        static float EaseOutBounce(float x)
        {
            const float n1 = 7.5625f, d1 = 2.75f;
            if (x < 1f / d1)
            {
                return n1 * x * x;
            }
            if (x < 2f / d1)
            {
                x -= 1.5f / d1;
                return n1 * x * x + 0.75f;
            }
            if (x < 2.5f / d1)
            {
                x -= 2.25f / d1;
                return n1 * x * x + 0.9375f;
            }
            x -= 2.625f / d1;
            return n1 * x * x + 0.984375f;
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
            Gx.DrawTextureWithTexCoords(dst, lcd, new Rect(x / W, (y + h) / H, w / W, -h / H), false);
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
                    TouchUi.Label(new Rect(b.xMax - 2 * k, b.yMax - 2 * k, 0, 0), turns.ToString(), 6, TextAnchor.MiddleCenter, Color.white, true);
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
                Vector2 at = f.at + new Vector2(0, -2 - 8 * Mathf.Min(1f, age / 1.2f));
                string t = f.Text;
                TouchUi.Label(TouchUi.R(at.x - 40, at.y - 6, 80, 12), t, t.Length <= 4 ? 10 : 7, TextAnchor.MiddleCenter, c, true);
            }
        }

        void DrawBubble()
        {
            if (bubbleSide < 0 || (C_PortScreens.training() && !string.IsNullOrEmpty(C_PortScreens.coach())))
            {
                return; // one bubble at a time: the coach's comes first in the tutorial
            }
            float k = TouchUi.K;
            float age = Time.unscaledTime - bubbleTime;
            float alpha = age < 1.8f ? 1f : Mathf.Clamp01(1f - (age - 1.8f) / 0.4f);
            Rect b = bubbleSide == 0 ? TouchUi.R(2, 160, 140, 36) : TouchUi.R(38, 160, 140, 36);
            float tail = bubbleSide == 0 ? 19 : 161;
            Color line = new Color(0.01f, 0.01f, 0.01f, alpha), fill = new Color(1f, 1f, 1f, alpha);
            BubbleShape(b, tail, line, fill);
            Rect text = new Rect(b.x + 5 * k, b.y + 3 * k, b.width - 10 * k, b.height - 6 * k);
            if (bubbleRaw)
            {
                TouchUi.LabelRaw(text, bubbleText, 7f, TextAnchor.MiddleCenter, new Color(0.1f, 0.04f, 0.1f, alpha), true, true, false);
            }
            else
            {
                TouchUi.Label(text, bubbleText, 7.5f, TextAnchor.MiddleCenter, new Color(0.1f, 0.04f, 0.1f, alpha), true, true, false);
            }
        }

        void DrawPopup()
        {
            float k = TouchUi.K;
            TouchUi.Dim(0.55f);
            Rect p = PopupRect();
            TouchUi.Panel(p);
            float py = TouchUi.V(new Vector2(p.x, p.y)).y;
            int[] slots = Slots();
            bool usable = PopupUsable();
            Rect anchorButton;
            // a row of details right of the picture (capitals at py + 28, + 41, ...)
            System.Func<int, Rect> detail = i => TouchUi.R(82, py + 25 + i * UiFont.LineHeight, 88, UiFont.LineHeight);
            if (popupKind == 1)
            {
                int i = popupArg;
                anchorButton = SkillRect(i);
                TouchUi.PixelText(C_PortUi.skillName(i).ToUpperInvariant(), 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, py + 15, 0, 0).y, 1, true);
                J2ME.Image icon = C_PortUi.skillIcon(i);
                Rect ic = TouchUi.R(13, py + 26, 64, 64);
                TouchUi.Fill(ic, TouchUi.Outline);
                if (icon != null)
                {
                    TouchUi.Sprite(SkillTexture(i, icon), new Rect(ic.x + 2 * k, ic.y + 2 * k, 60 * k, 60 * k));
                }
                int id = C_PortUi.skillId(0, i);
                TouchUi.Label(detail(0), "Tuyệt chiêu " + (i + 1), 7, TextAnchor.UpperLeft, TouchUi.TextSoft);
                TouchUi.Label(detail(1), "Hệ " + Elements[Mathf.Clamp(id / 3, 0, 2)], 7, TextAnchor.UpperLeft, TouchUi.TextSoft);
                Rect cost = detail(2);
                TouchUi.Fill(new Rect(cost.x, cost.y + 5 * k, 6 * k, 6 * k), TouchUi.Outline);
                TouchUi.Fill(new Rect(cost.x + k, cost.y + 6 * k, 4 * k, 4 * k), TouchUi.Hex(0x3C7CE8));
                TouchUi.Label(new Rect(cost.x + 9 * k, cost.y, cost.width - 9 * k, cost.height), "Cần " + C_PortUi.skillCost(i), 7, TextAnchor.UpperLeft, TouchUi.Hex(0x9FD0FF));
                TouchUi.Label(detail(3), "Đang có " + C_PortUi.mana(0), 7, TextAnchor.UpperLeft, TouchUi.Hex(0x9FD0FF));
            }
            else
            {
                int type = slots[popupArg];
                anchorButton = ItemRect(popupArg);
                TouchUi.PixelText(C_PortUi.itemName(type).ToUpperInvariant(), 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, py + 15, 0, 0).y, 1, true);
                TouchUi.Item(type, TouchUi.R(15, py + 28, 56, 56));
                TouchUi.Label(detail(0), "Còn " + C_PortUi.itemCount(type), 7.5f, TextAnchor.UpperLeft, TouchUi.TextGreen, true);
                Rect lim = detail(1);
                lim.height *= 2;
                TouchUi.Label(lim, "Mỗi lượt dùng 1 vật phẩm", 7, TextAnchor.UpperLeft, TouchUi.TextSoft, false, true);
            }
            string desc = PopupDesc(), note = PopupNote();
            int lines = TouchUi.Lines(desc, PopupDescW);
            TouchUi.Label(TouchUi.R(14, py + 96, PopupDescW, lines * UiFont.LineHeight), desc, 7.5f, TextAnchor.UpperLeft, Color.white, false, true);
            if (note.Length > 0)
            {
                TouchUi.Label(TouchUi.R(14, py + 96 + lines * UiFont.LineHeight + 4, PopupDescW, UiFont.LineHeight), note, 7, TextAnchor.UpperLeft, TouchUi.Hex(0xFF9A80));
            }
            Rect close = PopupClose(), use = PopupUse();
            TouchUi.TextButton(close, "ĐÓNG", Btn.Grey, downKind == 5);
            TouchUi.RedButton(use, "DÙNG", usable, downKind == 4 && usable);
            TouchUi.Pointer(anchorButton.center.x, p.yMax);
            TouchUi.Label(TouchUi.R(0, py - 17, 180, 14), "Chạm ra ngoài để đóng", 7, TextAnchor.MiddleCenter, TouchUi.Hex(0xD8D0E0));
        }
    }
}
