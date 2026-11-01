// Screen 7 of the touch redesign (plan/thiet-ke-giao-dien.md): the result of a won battle (or a quest
// reward / an arena fight), shown instead of the game's "Kết quả" panel.
//   THẮNG / the hero, level and experience (counting up while the rewards are added, "Lên cấp" when it
//   happens) / the rewards / gold owned / TIẾP TỤC.
// TIẾP TỤC presses the panel's own "Đóng"; before the panel accepts it, a tap only skips its short wait.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchResultView : ITouchView
    {
        const int GridH = 352;

        int downKind; // 1 continue, 2 elsewhere
        int startLevel = -1;
        object shownFor;

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
            shownFor = null;
            startLevel = -1;
        }

        static Rect ContinueRect()
        {
            return TouchUi.R(20, 314, 140, 32);
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            TouchUi.Begin(GridH);
            if (shownFor == null)
            {
                shownFor = this;
                startLevel = C_PortUi.resultLevel();
            }
            Vector2 p = e.mousePosition;
            if (e.type == EventType.MouseDown && e.button == 0)
            {
                downKind = ContinueRect().Contains(p) ? 1 : 2;
                Haptics.Tap(haptics && downKind == 1);
                e.Use();
            }
            else if (e.type == EventType.MouseUp)
            {
                if (downKind == 1 && ContinueRect().Contains(p) && C_PortUi.resultReady())
                {
                    C_PortUi.request(C_PortUi.f_DIALOG_KEY, 1); // the panel's "Đóng"
                    shownFor = null;
                }
                else if (downKind != 0 && !C_PortUi.resultReady())
                {
                    C_PortUi.request(C_PortUi.f_RESULT_SKIP, 0);
                }
                downKind = 0;
                e.Use();
            }
            if (e.type == EventType.Repaint)
            {
                Draw();
            }
        }

        void Draw()
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
            int mode = C_PortUi.resultMode();

            // title
            if (mode == 0)
            {
                Texture2D win = TouchUi.Img("/strwin");
                int fw = win.width / 3;
                TouchUi.Sprite(win, TouchUi.R(90 - fw / 2f, 4, fw, win.height), new Rect(fw, 0, fw, win.height));
            }
            else
            {
                TouchUi.PixelText(mode == 1 ? "PHẦN THƯỞNG NHIỆM VỤ" : "LUYỆN TẬP", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, 30, 0, 0).y, 1);
            }

            // the hero
            TouchUi.Panel(TouchUi.R(6, 76, 168, 228));
            TouchUi.Portrait(C_PortUi.resultPortrait(), TouchUi.R(14, 88, 34, 34));
            TouchUi.PixelText(C_PortUi.resultName().ToUpperInvariant(), 2, TouchUi.R(54, 0, 0, 0).x, TouchUi.R(0, 88, 0, 0).y, 0);
            int level = C_PortUi.resultLevel();
            bool up = startLevel >= 0 && level > startLevel;
            TouchUi.Label(TouchUi.R(100, 88, 68, 12), up ? "Lên cấp " + level + "!" : "Cấp " + level, 7.5f, TextAnchor.MiddleRight,
                up ? TouchUi.TextGreen : TouchUi.Hex(0xFFE6A0), true);
            int exp = C_PortUi.resultExp(), lo = C_PortUi.resultExpLo(), hi = C_PortUi.resultExpHi();
            float frac = hi > lo ? Mathf.Clamp01((float) (exp - lo) / (hi - lo)) : 1f;
            Bar(TouchUi.R(54, 104, 114, 7), frac, TouchUi.Hex(0xB050E0), TouchUi.Hex(0x200828));
            TouchUi.Label(TouchUi.R(54, 113, 114, 10), "Kinh nghiệm " + exp + "/" + hi, 6.5f, TextAnchor.UpperLeft, TouchUi.TextSoft);

            // rewards
            float y = 134;
            if (mode == 0)
            {
                Row(ref y, "/gold", "Vàng nhặt được", "+" + C_PortUi.resultGoldGain());
                Row(ref y, "/book", "Kinh nghiệm thu thập", "+" + C_PortUi.resultExpGain());
                Row(ref y, "/book", "Thưởng thắng trận", "+" + C_PortUi.resultWinBonus());
            }
            else if (mode == 1)
            {
                Row(ref y, "/gold", "Thưởng nhiệm vụ: vàng", "+" + C_PortUi.resultGoldGain());
                Row(ref y, "/book", "Thưởng nhiệm vụ: kinh nghiệm", "+" + C_PortUi.resultExpGain());
            }
            else
            {
                Row(ref y, "/book", "Kinh nghiệm luyện tập", "+" + C_PortUi.resultExpGain());
            }

            // totals
            Rect tot = TouchUi.R(14, 262, 152, 24);
            TouchUi.Fill(tot, TouchUi.Hex(0x2A1426));
            TouchUi.Sprite(TouchUi.Img("/gold"), TouchUi.R(18, 263, 22, 22));
            TouchUi.Label(TouchUi.R(44, 262, 70, 24), "Vàng hiện có", 7, TextAnchor.MiddleLeft, TouchUi.TextSoft);
            TouchUi.Label(TouchUi.R(100, 262, 62, 24), TouchUi.Money(C_PortUi.resultGold()), 8.5f, TextAnchor.MiddleRight, TouchUi.TextGold, true);

            bool ready = C_PortUi.resultReady();
            TouchUi.RedButton(ContinueRect(), "TIẾP TỤC", ready, downKind == 1 && ready);
            if (!ready)
            {
                TouchUi.Label(TouchUi.R(0, 300, 180, 10), "Chạm để bỏ qua", 6.5f, TextAnchor.MiddleCenter, TouchUi.Hex(0xB8A8B8));
            }
        }

        static void Row(ref float y, string icon, string label, string value)
        {
            float k = TouchUi.K;
            Rect r = TouchUi.R(14, y, 152, 24);
            TouchUi.Fill(r, TouchUi.PurpleDark);
            Texture2D t = TouchUi.Img(icon);
            TouchUi.Sprite(t, TouchUi.R(16, y + 1, 22, 22), new Rect(0, 0, Mathf.Min(22, t.width), Mathf.Min(22, t.height)));
            TouchUi.Label(TouchUi.R(42, y, 90, 24), label, 7, TextAnchor.MiddleLeft, Color.white, false, true);
            TouchUi.PixelText(value, 1, r.xMax - 4 * k, r.center.y, 2, true);
            y += 28;
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
