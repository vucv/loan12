// Screen 7 of the touch redesign (plan/thiet-ke-giao-dien.md): the result.
//   Won battle / quest reward / arena: THẮNG, the hero, level and experience (counting up while the
//   rewards are added, "Lên cấp" when it happens), the rewards, gold owned, TIẾP TỤC (the panel's "Đóng";
//   before the panel accepts it, a tap only skips its short wait).
//   Lost battle: THUA, the hero, what was lost (1 life, experience), ĐÁNH LẠI / TRỞ VỀ (the game's
//   "Có" / "Không" or "Trở về").
// After a battle it slides in over the dimmed, frozen battle, right after the THẮNG / THUA animation.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchResultView : ITouchView
    {
        const int GridH = 352;

        readonly TouchBattleView battle;
        int downKind; // 1 continue / retry, 2 elsewhere, 3 back (lost)
        int startLevel = -1;
        float shownAt = -1f;

        public TouchResultView(TouchBattleView battle)
        {
            this.battle = battle;
        }

        public bool Active()
        {
            try
            {
                return C_PortUi.resultView();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            downKind = 0;
            shownAt = -1f;
            startLevel = -1;
        }

        static bool Lost()
        {
            return C_PortUi.resultMode() == 3;
        }

        // lost with a second chance: two buttons side by side; otherwise one wide button
        static Rect MainRect()
        {
            return Lost() && C_PortUi.lossRetry() ? TouchUi.R(80, 314, 90, 32) : TouchUi.R(20, 314, 140, 32);
        }

        static Rect BackRect()
        {
            return TouchUi.R(10, 314, 66, 32);
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (shownAt < 0f)
            {
                shownAt = Time.unscaledTime;
                startLevel = Lost() ? C_PortUi.vsLevel(0) : C_PortUi.resultLevel();
            }
            TouchUi.Begin(GridH);
            Vector2 p = e.mousePosition;
            bool lost = Lost(), retry = lost && C_PortUi.lossRetry();
            if (e.type == EventType.MouseDown && e.button == 0)
            {
                downKind = MainRect().Contains(p) ? 1 : (retry && BackRect().Contains(p) ? 3 : 2);
                Haptics.Tap(haptics && downKind != 2);
                e.Use();
            }
            else if (e.type == EventType.MouseUp)
            {
                if (Time.unscaledTime - shownAt < 0.3f)
                {
                    // still sliding in
                }
                else if (lost)
                {
                    if (downKind == 1 && MainRect().Contains(p))
                    {
                        C_PortUi.request(retry ? C_PortUi.f_END_RETRY : C_PortUi.f_END_BACK, 0);
                    }
                    else if (downKind == 3 && BackRect().Contains(p))
                    {
                        C_PortUi.request(C_PortUi.f_END_BACK, 0);
                    }
                }
                else if (downKind == 1 && MainRect().Contains(p) && C_PortUi.resultReady())
                {
                    C_PortUi.request(C_PortUi.f_DIALOG_KEY, 1); // the panel's "Đóng"
                }
                else if (downKind != 0 && !C_PortUi.resultReady())
                {
                    C_PortUi.request(C_PortUi.f_RESULT_SKIP, 0);
                }
                downKind = 0;
                e.Use();
            }
            else if (e.type == EventType.MouseDrag)
            {
                e.Use();
            }
            if (e.type == EventType.Repaint)
            {
                bool afterBattle = battle != null && C_PortUi.battle() != null;
                if (afterBattle)
                {
                    battle.DrawBackdrop(lcd);
                    TouchUi.Begin(GridH);
                }
                else
                {
                    TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Background);
                    TouchUi.Wallpaper();
                }
                Draw(afterBattle);
            }
        }

        void Draw(bool afterBattle)
        {
            float k = TouchUi.K;
            float t = Mathf.Clamp01((Time.unscaledTime - shownAt) / 0.3f);
            float ease = 1f - (1f - t) * (1f - t);
            TouchUi.Dim((afterBattle ? 0.8f : 0.45f) * ease);
            Color old = Gx.color;
            Gx.color = old * new Color(1f, 1f, 1f, ease);
            float slide = (1f - ease) * 18f;
            if (Lost())
            {
                DrawLost(slide);
            }
            else
            {
                DrawWon(slide);
            }
            Gx.color = old;
        }

        void Hero(float y, int portrait, string name, int level)
        {
            TouchUi.Portrait(portrait, TouchUi.R(14, y, 34, 34));
            TouchUi.PixelText(name.ToUpperInvariant(), 2, TouchUi.R(54, 0, 0, 0).x, TouchUi.R(0, y + 8, 0, 0).y, 0, true);
            bool up = startLevel >= 0 && level > startLevel;
            string text = up ? "Lên cấp " + level + "!" : "Cấp " + level;
            Color c = up ? TouchUi.TextGreen : TouchUi.Hex(0xFFE6A0);
            // beside the name when there is room; a long name puts it on the experience line, right side
            float nameEnd = 54 + TouchUi.PixelWidth(name.ToUpperInvariant(), 2) + 4;
            if (nameEnd <= 166 - TouchUi.TextWidth(text, 7.5f, true))
            {
                TouchUi.Label(TouchUi.R(96, y + 1, 70, 14), text, 7.5f, TextAnchor.MiddleRight, c, true);
            }
            else
            {
                TouchUi.Label(TouchUi.R(54, y + 23, 112, 11), text, 6.5f, TextAnchor.UpperRight, c, true);
            }
        }

        void DrawWon(float slide)
        {
            int mode = C_PortUi.resultMode();
            int quest = mode == 1 ? C_PortMap.questIndex() : -1;

            // title
            if (mode == 0)
            {
                Texture2D win = TouchUi.Img("/strwin");
                int fw = win.width / 3;
                float pulse = 1f + 0.02f * Mathf.Sin(Time.unscaledTime * 4f);
                float w = fw * pulse, h = win.height * pulse;
                TouchUi.Sprite(win, TouchUi.R(90 - w / 2f, 38 - h / 2f + slide, w, h), new Rect(fw * (1 + (int) (Time.unscaledTime / 0.2f) % 2), 0, fw, win.height));
            }
            else if (mode == 1)
            {
                TouchUi.PixelText("HOÀN THÀNH NHIỆM VỤ", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 32 + slide, 0, 0).y, 1, true);
                if (quest >= 0 && quest < C_PortMap.questCount())
                {
                    TouchUi.Label(TouchUi.R(8, 44 + slide, 164, 12), (quest + 1) + ". " + Loc.T(QuestData.Title(quest)), 7, TextAnchor.UpperCenter, TouchUi.TextGold, true);
                    TouchUi.Label(TouchUi.R(8, 56 + slide, 164, 10), "Chương " + (QuestData.Chapter(quest) + 1) + " · " + QuestData.Chapters[QuestData.Chapter(quest)],
                        6, TextAnchor.UpperCenter, TouchUi.TextSoft);
                }
            }
            else
            {
                TouchUi.PixelText("LUYỆN TẬP", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 40 + slide, 0, 0).y, 1, true);
            }

            // the hero
            float y0 = 76 + slide;
            TouchUi.Panel(TouchUi.R(6, y0, 168, 228));
            Hero(y0 + 10, C_PortUi.resultPortrait(), C_PortUi.resultName(), C_PortUi.resultLevel());
            int exp = C_PortUi.resultExp(), lo = C_PortUi.resultExpLo(), hi = C_PortUi.resultExpHi();
            float frac = hi > lo ? Mathf.Clamp01((float) (exp - lo) / (hi - lo)) : 1f;
            Bar(TouchUi.R(54, y0 + 24, 112, 7), frac, TouchUi.Hex(0xB050E0), TouchUi.Hex(0x200828));
            string expText = "Kinh nghiệm " + TouchUi.Money(exp) + "/" + TouchUi.Money(hi);
            if (TouchUi.TextWidth(expText, 6.5f, false) > 112)
            {
                expText = TouchUi.Money(exp) + "/" + TouchUi.Money(hi);
            }
            TouchUi.Label(TouchUi.R(54, y0 + 33, 112, 11), expText, 6.5f, TextAnchor.UpperLeft, TouchUi.TextSoft);

            // rewards
            float y = y0 + 50;
            rowH = 22;
            if (mode == 0)
            {
                // gift boxes dropped in the battle (opened on the map afterwards)
                int boxes = C_PortMap.battleBoxes();
                y = y0 + 50;
                Row(ref y, "/gold", "Vàng nhặt được", "+" + C_PortUi.resultGoldGain());
                Row(ref y, "/book", "Kinh nghiệm", "+" + C_PortUi.resultExpGain());
                Row(ref y, "/book", "Thưởng thắng trận", "+" + C_PortUi.resultWinBonus());
                if (boxes > 0)
                {
                    Row(ref y, "/r/g", "Hộp quà rơi trong trận", "+" + boxes);
                }
            }
            else if (mode == 1)
            {
                Row(ref y, "/gold", "Vàng thưởng", "+" + C_PortUi.resultGoldGain());
                Row(ref y, "/book", "Kinh nghiệm thưởng", "+" + C_PortUi.resultExpGain());
                int qb = quest >= 0 ? C_PortMap.questBoxes(quest) : 0;
                if (qb > 0)
                {
                    Row(ref y, "/r/g", "Hộp quà", "+" + qb);
                }
                string opens = Opens(quest);
                if (opens.Length > 0)
                {
                    Row(ref y, "/m/lock", "Mở thêm: " + opens, "");
                }
            }
            else
            {
                Row(ref y, "/book", "Kinh nghiệm luyện tập", "+" + C_PortUi.resultExpGain());
            }

            // gold owned
            Total(y0 + 186, "/gold", "Vàng hiện có", TouchUi.ShortMoney(C_PortUi.resultGold()), TouchUi.TextGold);

            bool ready = C_PortUi.resultReady();
            TouchUi.RedButton(MainRect(), "TIẾP TỤC", ready, downKind == 1 && ready);
            if (!ready)
            {
                TouchUi.Label(TouchUi.R(0, y0 + 213, 180, 11), "Chạm để bỏ qua", 6, TextAnchor.MiddleCenter, TouchUi.Hex(0xB8A8B8));
            }
        }

        /// <summary>Cities a quest opens (its dd.m nodes), by name.</summary>
        static string Opens(int q)
        {
            if (q < 0)
            {
                return "";
            }
            int[] nodes = C_PortMap.questOpens(q);
            if (nodes == null)
            {
                return "";
            }
            string s = "";
            foreach (int n in nodes)
            {
                if (C_PortMap.nodeType(n) == 0 && s.IndexOf(C_PortMap.nodeName(n)) < 0)
                {
                    s += (s.Length > 0 ? ", " : "") + C_PortMap.nodeName(n);
                }
            }
            return s;
        }

        void DrawLost(float slide)
        {
            Texture2D lose = TouchUi.Img("/strlose");
            int fw = lose.width / 3;
            TouchUi.Sprite(lose, TouchUi.R(90 - fw / 2f, 38 - lose.height / 2f + slide, fw, lose.height), new Rect(fw, 0, fw, lose.height));

            float y0 = 76 + slide;
            TouchUi.Panel(TouchUi.R(6, y0, 168, 228));
            Hero(y0 + 10, C_PortUi.vsPortrait(0), C_PortUi.vsName(0), C_PortUi.vsLevel(0));
            TouchUi.Label(TouchUi.R(54, y0 + 22, 112, 14), "thua " + TouchUi.ShortName(C_PortUi.vsName(1), 14), 7, TextAnchor.MiddleLeft, TouchUi.TextSoft);

            float y = y0 + 56;
            Row(ref y, "/heart", "Mạng chơi", "-1");
            int exp = C_PortUi.lossExp();
            if (exp > 0)
            {
                Row(ref y, "/book", "Kinh nghiệm bị phạt", "-" + exp);
            }
            int lostBoxes = C_PortMap.battleBoxes();
            if (lostBoxes > 0)
            {
                Row(ref y, "/r/g", "Hộp quà rơi trong trận (giữ lại)", "+" + lostBoxes);
            }
            TouchUi.Label(TouchUi.R(16, y, 148, 56), "Gợi ý: mua Bình Thuốc hoặc Quỷ Diện Giáp ở màn chuẩn bị trước khi đánh lại.", 7,
                TextAnchor.UpperLeft, TouchUi.TextGold, false, true);

            bool retry = C_PortUi.lossRetry();
            if (retry)
            {
                TouchUi.Label(TouchUi.R(0, y0 + 202, 180, 14), "Đánh lại trận này?", 7.5f, TextAnchor.MiddleCenter, Color.white, true);
                Rect back = BackRect();
                TouchUi.Button(back, Btn.Grey, downKind == 3);
                TouchUi.PixelText("TRỞ VỀ", 2, back.center.x, back.center.y + (downKind == 3 ? TouchUi.K : 0), 1, true);
                TouchUi.RedButton(MainRect(), "ĐÁNH LẠI", true, downKind == 1);
            }
            else
            {
                TouchUi.RedButton(MainRect(), "TRỞ VỀ", true, downKind == 1);
            }
        }

        static float rowH = 22; // a one-line row

        /// <summary>A reward row: icon (or the item of type <paramref name="item"/>), label, value on the right.</summary>
        static void Row(ref float y, string icon, string label, string value, int item = -1)
        {
            float k = TouchUi.K;
            // a two-line label makes the row taller
            float vw = TouchUi.PixelWidth(value, 1);
            float lw = 152 - 30 - vw - 12;
            int lines = Mathf.Max(1, TouchUi.Lines(label, lw, 6.5f));
            float h = lines > 1 ? rowH + 8 : rowH;
            Rect r = TouchUi.R(14, y, 152, h);
            TouchUi.Fill(r, TouchUi.PurpleDark);
            float box = Mathf.Min(20, rowH - 2);
            if (item >= 0)
            {
                TouchUi.Item(item, TouchUi.R(17 + (20 - box) / 2f, y + (h - box) / 2f, box, box));
            }
            else
            {
                Texture2D t = TouchUi.Img(icon);
                float ic = Mathf.Min(box, Mathf.Min(t.width, t.height));
                TouchUi.Sprite(t, TouchUi.R(17 + (20 - ic) / 2f, y + (h - ic) / 2f, ic, ic));
            }
            TouchUi.Label(TouchUi.R(42, y, lw, h), label, 6.5f, TextAnchor.MiddleLeft, Color.white, false, true);
            if (value.Length > 0)
            {
                TouchUi.PixelText(value, 1, r.xMax - 6 * k, r.center.y, 2, true);
            }
            y += h + (rowH < 22 ? 2 : 3);
        }

        static void Total(float y, string icon, string label, string value, Color c)
        {
            TouchUi.Fill(TouchUi.R(14, y, 152, 24), TouchUi.Hex(0x2A1426));
            TouchUi.Sprite(TouchUi.Img(icon), TouchUi.R(16, y + 1, 22, 22));
            TouchUi.Label(TouchUi.R(42, y, 70, 24), label, 7, TextAnchor.MiddleLeft, TouchUi.TextSoft);
            TouchUi.Label(TouchUi.R(100, y, 60, 24), value, 8.5f, TextAnchor.MiddleRight, c, true);
        }

        static void Bar(Rect r, float frac, Color fill, Color back)
        {
            float k = TouchUi.K;
            TouchUi.Fill(r, TouchUi.Outline);
            Rect inner = new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k);
            TouchUi.Fill(inner, back);
            TouchUi.Fill(new Rect(inner.x, inner.y, inner.width * frac, inner.height), fill);
        }
    }
}
