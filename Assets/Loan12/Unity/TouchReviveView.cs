// Second chance: beaten while the enemy has less than half its HP left, the battle stands still on
// this offer (bu.portReviveHold): XEM QUẢNG CÁO brings the hero back with 30 HP and the battle goes on
// (once per battle); BỎ CUỘC lets the defeat go on as before. Not in the tutorial battle.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchReviveView : ITouchView
    {
        const int GridH = 346;

        readonly TouchBattleView battle;
        float shownAt = -1f;

        public TouchReviveView(TouchBattleView battle)
        {
            this.battle = battle;
        }

        public bool Active()
        {
            try
            {
                return C_PortScreens.reviveOffer();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
            shownAt = -1f;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (shownAt < 0f)
            {
                shownAt = Time.unscaledTime;
            }
            Kit.Begin(e, GridH);
            Rewards.Tick();
            float k = TouchUi.K, H = Kit.H;
            const float h = 210;
            float t = Mathf.Clamp01((Time.unscaledTime - shownAt) / 0.2f);
            float top = Mathf.Round((H - h) / 2) + (1f - t) * 12f;
            Rect panel = TouchUi.R(14, top, 152, h);
            Rect ad = TouchUi.R(24, top + 138, 132, 26);
            Rect give = TouchUi.R(44, top + 172, 92, 22);
            if (!Rewards.Busy && t >= 1f)
            {
                if (Kit.Tap(1, ad))
                {
                    Rewards.Show("revive", r =>
                    {
                        if (r == 1)
                        {
                            C_PortScreens.revive(true);
                            Haptics.Tap(haptics);
                        }
                    });
                }
                else if (Kit.Tap(2, give))
                {
                    C_PortScreens.revive(false);
                }
            }

            if (Kit.Paint)
            {
                if (battle != null)
                {
                    battle.DrawBackdrop(lcd);
                    TouchUi.Begin(GridH);
                }
                TouchUi.Dim(0.66f * t);
                Color old = Gx.color;
                Gx.color = old * new Color(1f, 1f, 1f, t);
                TouchUi.Panel(panel);
                // a slow red pulse: the hero is down
                float pulse = 0.5f + 0.5f * Mathf.Sin(Time.unscaledTime * 4f);
                TouchUi.PixelText("HỒI SINH?", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 16, 0, 0).y, 1, true);

                // the two fighters: the hero down, the enemy's HP bar under half
                TouchUi.Portrait(C_PortUi.portrait(0), TouchUi.R(30, top + 30, 40, 40));
                TouchUi.Fill(TouchUi.R(30, top + 30, 40, 40), new Color(0.6f, 0f, 0f, 0.35f + 0.2f * pulse));
                TouchUi.Portrait(C_PortUi.portrait(1), TouchUi.R(110, top + 30, 40, 40), true);
                TouchUi.Label(TouchUi.R(70, top + 36, 40, 28), "VS", 8, TextAnchor.MiddleCenter, Kit.Gold, true);
                int hp = C_PortUi.hp(1), max = Mathf.Max(1, C_PortUi.maxHp(1));
                Rect bar = TouchUi.R(110, top + 73, 40, 5);
                TouchUi.Fill(bar, TouchUi.Outline);
                TouchUi.Fill(new Rect(bar.x + k, bar.y + k, (bar.width - 2 * k) * Mathf.Clamp01(hp / (float) max), bar.height - 2 * k), TouchUi.Hex(0xE0383A));
                TouchUi.Label(TouchUi.R(14, top + 82, 152, 12), TouchUi.ShortName(C_PortUi.name(1), 14) + " chỉ còn " + hp + "/" + max + " máu!", 7, TextAnchor.MiddleCenter,
                    TouchUi.TextGold, true);
                TouchUi.Label(TouchUi.R(20, top + 98, 140, 30), "Xem quảng cáo để hồi 30 máu và đánh tiếp.", 6.75f, TextAnchor.UpperCenter, Kit.Cream,
                    false, true);
                Kit.AdButton(ad, "HỒI 30 MÁU", Kit.Down(1), !Rewards.Busy);
                TouchUi.TextButton(give, "BỎ CUỘC", Btn.Grey, Kit.Down(2));
                Gx.color = old;
            }
            Rewards.DrawOverlay();
            Kit.End();
        }
    }
}
