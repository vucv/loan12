// Touch redesign, option B (plan/thiet-ke-giao-dien.md): the battle screen drawn by Unity.
//
// The converted game still runs the battle (rules, AI, animations of the board). This view replaces
// its 240x320 layout while the plain battle is on screen:
//   enemy bar (portrait, name, level, health, energy, pause) / the board, cut from the game's screen
//   and scaled by whole numbers / whose turn / player bar / skills row / items row.
// Data and actions go through Loan12.C_PortUi (game/PortUi.java). Whenever the game shows a dialog
// (results, pause, confirmations), a transition or the tutorial, the host falls back to the classic
// full-screen view, so nothing the game draws is lost.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBattleView
    {
        // virtual grid (pixels of the original art): 180 wide, 352 high, scaled by a whole number
        const int GridW = 180;
        const int GridH = 352;

        const int ActSkill = 1, ActItem = 2, ActPause = 3;

        static readonly int[] ItemTypes = {4, 3, 0, 1};
        static readonly string[] ItemNames = {"Bình Thuốc", "Quỷ Diện Giáp", "Long Thần Kiếm", "Nhân Sâm"};

        static readonly Color Outline = Hex(0x020301);
        static readonly Color Gold = Hex(0xC89B3C);
        static readonly Color PanelRed = Hex(0x800000);
        static readonly Color ButtonFill = Hex(0x3A1838);
        static readonly Color ButtonEdge = Hex(0x6E3C6A);
        static readonly Color ButtonDown = Hex(0x773B75);
        static readonly Color Background = Hex(0x170B12);

        Texture2D avatar, items;
        readonly Texture2D[] skillIcons = new Texture2D[3];
        int skillIconsHero = -1;
        Font bold;
        GUIStyle text;

        float k;
        Rect enemyRect, boardRect, turnRect, playerRect, pauseRect;
        readonly Rect[] skillRects = new Rect[3];
        readonly Rect[] itemRects = new Rect[4];
        int bx, by, bw, bh; // the board on the game's screen

        bool boardHeld;
        int heldKind, heldArg;

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

        /// <summary>Releases a touch still held when the view hands over to the classic screen.</summary>
        public void Reset()
        {
            if (boardHeld)
            {
                J2ME.Host.pointerReleased(bx, by);
                boardHeld = false;
            }
            heldKind = 0;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            EnsureAssets();
            Layout();
            HandleInput(e, haptics);
            if (e.type == EventType.Repaint)
            {
                Draw(lcd);
            }
        }

        // ---------------------------------------------------------------------------------- layout

        void Layout()
        {
            Rect safe = UnityEngine.Device.Screen.safeArea;
            Rect area = new Rect(safe.x, Screen.height - safe.yMax, safe.width, safe.height);
            if (area.width <= 0 || area.height <= 0)
            {
                area = new Rect(0, 0, Screen.width, Screen.height);
            }
            k = Mathf.Min(area.width / GridW, area.height / GridH);
            if (k >= 1f)
            {
                k = Mathf.Floor(k); // whole numbers keep the pixel art sharp
            }
            float x0 = Mathf.Round(area.x + (area.width - GridW * k) / 2f);
            float y0 = Mathf.Round(area.y + (area.height - GridH * k) / 2f);

            bx = C_PortUi.board(0);
            by = C_PortUi.board(1);
            bw = C_PortUi.board(2);
            bh = C_PortUi.board(3);

            enemyRect = R(x0, y0, 2, 0, 176, 38);
            pauseRect = R(x0, y0, 153, 9, 21, 21);
            boardRect = R(x0, y0, 90 - bw / 2f, 43, bw, bh);
            turnRect = R(x0, y0, 48, 43 + bh + 3, 84, 13);
            float py = 43 + bh + 19;
            playerRect = R(x0, y0, 2, py, 176, 38);
            for (int i = 0; i < 3; i++)
            {
                skillRects[i] = R(x0, y0, 4 + i * 58, py + 43, 54, 40);
            }
            for (int i = 0; i < 4; i++)
            {
                itemRects[i] = R(x0, y0, 4 + i * 43.5f, py + 87, 41, 34);
            }
        }

        Rect R(float x0, float y0, float x, float y, float w, float h)
        {
            return new Rect(x0 + x * k, y0 + y * k, w * k, h * k);
        }

        // ---------------------------------------------------------------------------------- input

        void HandleInput(Event e, bool haptics)
        {
            Vector2 p = e.mousePosition;
            switch (e.type)
            {
                case EventType.MouseDown:
                    if (e.button != 0)
                    {
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
                    heldKind = 0;
                    if (pauseRect.Contains(p) && PauseEnabled())
                    {
                        heldKind = ActPause;
                    }
                    for (int i = 0; i < 3; i++)
                    {
                        if (skillRects[i].Contains(p) && SkillEnabled(i))
                        {
                            heldKind = ActSkill;
                            heldArg = i;
                        }
                    }
                    for (int i = 0; i < 4; i++)
                    {
                        if (itemRects[i].Contains(p) && ItemEnabled(i))
                        {
                            heldKind = ActItem;
                            heldArg = i;
                        }
                    }
                    if (heldKind != 0)
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
                    if (heldKind != 0 && HeldRect().Contains(p))
                    {
                        switch (heldKind)
                        {
                            case ActPause:
                                C_PortUi.request(C_PortUi.f_PAUSE, 0);
                                break;
                            case ActSkill:
                                C_PortUi.request(C_PortUi.f_SKILL, heldArg);
                                break;
                            case ActItem:
                                C_PortUi.request(C_PortUi.f_ITEM, ItemTypes[heldArg]);
                                break;
                        }
                    }
                    heldKind = 0;
                    e.Use();
                    break;
            }
        }

        Rect HeldRect()
        {
            switch (heldKind)
            {
                case ActPause:
                    return pauseRect;
                case ActSkill:
                    return skillRects[heldArg];
                case ActItem:
                    return itemRects[heldArg];
                default:
                    return new Rect();
            }
        }

        void ToLcd(Vector2 p, out int x, out int y)
        {
            x = bx + Mathf.Clamp(Mathf.FloorToInt((p.x - boardRect.x) / k), 0, bw - 1);
            y = by + Mathf.Clamp(Mathf.FloorToInt((p.y - boardRect.y) / k), 0, bh - 1);
        }

        static bool PauseEnabled()
        {
            return C_PortUi.waiting();
        }

        static bool SkillEnabled(int i)
        {
            return C_PortUi.waiting() && i < C_PortUi.skillCount() && i < C_PortUi.skillsUsable();
        }

        static bool ItemEnabled(int i)
        {
            return C_PortUi.waiting() && !C_PortUi.itemUsed() && C_PortUi.itemCount(ItemTypes[i]) > 0;
        }

        // ---------------------------------------------------------------------------------- drawing

        void Draw(Texture2D lcd)
        {
            Fill(new Rect(0, 0, Screen.width, Screen.height), Background);

            DrawFighter(enemyRect, 1, true);
            Button(pauseRect, PauseEnabled(), heldKind == ActPause);
            float u = k;
            Color pc = PauseEnabled() ? Hex(0xF0E8D8) : Hex(0x6A6070);
            Fill(new Rect(pauseRect.x + 6 * u, pauseRect.y + 5 * u, 3 * u, 11 * u), pc);
            Fill(new Rect(pauseRect.x + 12 * u, pauseRect.y + 5 * u, 3 * u, 11 * u), pc);

            // the board, cut from the game's screen
            Rect frame = new Rect(boardRect.x - 2 * k, boardRect.y - 2 * k, boardRect.width + 4 * k, boardRect.height + 4 * k);
            Fill(frame, Outline);
            Fill(new Rect(frame.x + k, frame.y + k, frame.width - 2 * k, frame.height - 2 * k), Gold);
            GUI.DrawTextureWithTexCoords(boardRect, lcd, new Rect((float) bx / J2MEGameHost.LcdWidth, (float) (by + bh) / J2MEGameHost.LcdHeight,
                (float) bw / J2MEGameHost.LcdWidth, -(float) bh / J2MEGameHost.LcdHeight), false);

            // whose turn
            bool mine = C_PortUi.waiting();
            int turn = C_PortUi.turn();
            Color tc = mine ? Hex(0x2F8A22) : (turn == 1 ? Hex(0xA3201B) : Hex(0x4E4A50));
            Fill(turnRect, Outline);
            Fill(new Rect(turnRect.x + k, turnRect.y + k, turnRect.width - 2 * k, turnRect.height - 2 * k), tc);
            Label(turnRect, mine ? "LƯỢT CỦA BẠN" : (turn == 1 ? "LƯỢT ĐỐI THỦ" : "..."), 7, TextAnchor.MiddleCenter, Color.white);

            DrawFighter(playerRect, 0, false);

            // skills
            EnsureSkillIcons();
            int n = C_PortUi.skillCount();
            for (int i = 0; i < 3; i++)
            {
                Rect r = skillRects[i];
                bool on = SkillEnabled(i);
                Button(r, on, heldKind == ActSkill && heldArg == i);
                if (i >= n)
                {
                    continue;
                }
                if (skillIcons[i] != null)
                {
                    Rect ic = new Rect(r.x + 3 * k, r.y + 5 * k, 30 * k, 30 * k);
                    GUI.color = on ? Color.white : new Color(0.45f, 0.42f, 0.48f, 1f);
                    GUI.DrawTexture(ic, skillIcons[i]);
                    GUI.color = Color.white;
                }
                Fill(new Rect(r.x + 36 * k, r.y + 9 * k, 5 * k, 5 * k), Hex(0x3C7CE8));
                Label(new Rect(r.x + 33 * k, r.y + 15 * k, 19 * k, 14 * k), C_PortUi.skillCost(i).ToString(), 9, TextAnchor.MiddleRight,
                    on ? Hex(0xFFE38A) : Hex(0x9A90A0));
            }

            // items
            for (int i = 0; i < 4; i++)
            {
                Rect r = itemRects[i];
                bool on = ItemEnabled(i);
                int count = C_PortUi.itemCount(ItemTypes[i]);
                Button(r, on, heldKind == ActItem && heldArg == i);
                Rect ic = new Rect(r.x + 2 * k, r.y + 3 * k, 28 * k, 28 * k);
                GUI.color = count > 0 ? (on ? Color.white : new Color(0.7f, 0.68f, 0.72f, 1f)) : new Color(0.35f, 0.32f, 0.38f, 1f);
                GUI.DrawTextureWithTexCoords(ic, items, new Rect(ItemTypes[i] * 28f / items.width, 0f, 28f / items.width, 1f));
                GUI.color = Color.white;
                Label(new Rect(r.x + 24 * k, r.y + 20 * k, 15 * k, 12 * k), "x" + count, 8, TextAnchor.MiddleRight,
                    count > 0 ? Hex(0xFFFFFF) : Hex(0x8A8090));
            }
        }

        void DrawFighter(Rect r, int side, bool enemy)
        {
            Fill(r, Outline);
            Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), Gold);
            Fill(new Rect(r.x + 2 * k, r.y + 2 * k, r.width - 4 * k, r.height - 4 * k), PanelRed);
            // portrait
            Rect pf = new Rect(r.x + 4 * k, r.y + 3 * k, 32 * k, 32 * k);
            Fill(pf, Outline);
            int idx = C_PortUi.portrait(side);
            Rect pic = new Rect(pf.x + k, pf.y + k, 30 * k, 30 * k);
            float sx = idx % 10 * 30, sy = idx / 10 * 30;
            GUI.DrawTextureWithTexCoords(pic, avatar, new Rect(sx / avatar.width, (avatar.height - sy - 30) / avatar.height,
                30f / avatar.width, 30f / avatar.height));

            float tx = r.x + 40 * k;
            float right = enemy ? r.x + 148 * k : r.xMax - 5 * k;
            Label(new Rect(tx, r.y + 2 * k, right - tx, 13 * k), C_PortUi.name(side), 9, TextAnchor.MiddleLeft, Color.white);
            Label(new Rect(tx, r.y + 2 * k, right - tx, 13 * k), "Cấp " + C_PortUi.level(side), 7, TextAnchor.MiddleRight, Hex(0xFFE6A0));

            int hp = C_PortUi.hp(side), maxHp = C_PortUi.maxHp(side);
            int mana = C_PortUi.mana(side), maxMana = C_PortUi.maxMana(side);
            Rect hpBar = new Rect(tx, r.y + 17 * k, right - tx, 8 * k);
            Bar(hpBar, (float) hp / maxHp, Hex(0xD02828), Hex(0x3A0808));
            Label(hpBar, hp + "/" + maxHp, 6, TextAnchor.MiddleCenter, Color.white);
            Bar(new Rect(tx, r.y + 27 * k, right - tx, 6 * k), (float) mana / maxMana, Hex(0x3C7CE8), Hex(0x0A1A3A));

            // active item effects (double attack, shield) next to the portrait
            int[] fx = {0, 3};
            float ex = pf.xMax - 11 * k;
            foreach (int t in fx)
            {
                if (C_PortUi.effect(side, t) > 0)
                {
                    Rect er = new Rect(ex, pf.yMax - 11 * k, 12 * k, 12 * k);
                    Fill(er, Outline);
                    GUI.DrawTextureWithTexCoords(new Rect(er.x + k, er.y + k, 10 * k, 10 * k), items, new Rect(t * 28f / items.width, 0f, 28f / items.width, 1f));
                    ex -= 13 * k;
                }
            }
        }

        void Button(Rect r, bool enabled, bool down)
        {
            Fill(r, Outline);
            Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), enabled ? (down ? Gold : ButtonEdge) : Hex(0x3A3440));
            Fill(new Rect(r.x + 2 * k, r.y + 2 * k, r.width - 4 * k, r.height - 4 * k), enabled ? (down ? ButtonDown : ButtonFill) : Hex(0x26222A));
        }

        void Bar(Rect r, float frac, Color fill, Color back)
        {
            Fill(r, Outline);
            Rect inner = new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k);
            Fill(inner, back);
            Fill(new Rect(inner.x, inner.y, inner.width * Mathf.Clamp01(frac), inner.height), fill);
        }

        static void Fill(Rect r, Color c)
        {
            Color old = GUI.color;
            GUI.color = c;
            GUI.DrawTexture(r, Texture2D.whiteTexture);
            GUI.color = old;
        }

        void Label(Rect r, string s, int size, TextAnchor anchor, Color color)
        {
            text.fontSize = Mathf.Max(8, Mathf.RoundToInt(size * k));
            text.alignment = anchor;
            text.normal.textColor = new Color(0.08f, 0.03f, 0.07f, 1f);
            float sh = Mathf.Max(1f, Mathf.Round(k * 0.6f));
            GUI.Label(new Rect(r.x + sh, r.y + sh, r.width, r.height), s, text);
            text.normal.textColor = color;
            GUI.Label(r, s, text);
        }

        static Color Hex(int rgb)
        {
            return new Color32((byte) (rgb >> 16), (byte) (rgb >> 8), (byte) rgb, 255);
        }

        // ---------------------------------------------------------------------------------- assets

        void EnsureAssets()
        {
            if (avatar == null)
            {
                avatar = ToTexture(C_PortUi.image("/avatar"));
            }
            if (items == null)
            {
                items = ToTexture(C_PortUi.image("/itemshops"));
            }
            if (text == null)
            {
                bold = Resources.Load<Font>("Loan12Fonts/Inter-Bold");
                text = new GUIStyle(GUI.skin.label);
                if (bold != null)
                {
                    text.font = bold;
                }
                text.fontStyle = bold != null ? FontStyle.Normal : FontStyle.Bold;
                text.clipping = TextClipping.Overflow;
                text.wordWrap = false;
                text.padding = new RectOffset(0, 0, 0, 0);
            }
        }

        void EnsureSkillIcons()
        {
            int hero = C_PortUi.portrait(0);
            if (hero == skillIconsHero)
            {
                return;
            }
            skillIconsHero = hero;
            for (int i = 0; i < 3; i++)
            {
                if (skillIcons[i] != null)
                {
                    Object.Destroy(skillIcons[i]);
                    skillIcons[i] = null;
                }
                J2ME.Image img = C_PortUi.skillIcon(i);
                if (img != null)
                {
                    skillIcons[i] = ToTexture(img);
                }
            }
        }

        /// <summary>J2ME image (ARGB, top-down) -> point-filtered texture (bottom-up).</summary>
        static Texture2D ToTexture(J2ME.Image img)
        {
            int w = img.getWidth(), h = img.getHeight();
            int[] argb = new int[w * h];
            img.getRGB(argb, 0, w, 0, 0, w, h);
            Color32[] px = new Color32[w * h];
            for (int y = 0; y < h; y++)
            {
                for (int x = 0; x < w; x++)
                {
                    int v = argb[y * w + x];
                    px[(h - 1 - y) * w + x] = new Color32((byte) (v >> 16), (byte) (v >> 8), (byte) v, (byte) ((uint) v >> 24));
                }
            }
            Texture2D t = new Texture2D(w, h, TextureFormat.RGBA32, false);
            t.filterMode = FilterMode.Point;
            t.wrapMode = TextureWrapMode.Clamp;
            t.SetPixels32(px);
            t.Apply(false, true);
            return t;
        }
    }
}
