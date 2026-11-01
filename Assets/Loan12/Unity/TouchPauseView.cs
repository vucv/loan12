// The pause of a battle in the touch interface, drawn over the battle (plan: "Tạm dừng: tiếp tục /
// âm thanh / thoát"). It replaces two dialogs of the game:
//   - "Bạn muốn dừng game lại? Có / Không" (the pause button): TIẾP TỤC = "Không"; LƯU VÀ THOÁT = "Có",
//     which saves the battle and goes to the menu ("Chơi tiếp" resumes it);
//   - "Bấm tiếp tục để chơi tiếp" (the game paused itself when the app went to the background).
// Sound and vibration are switched here too (settings of the Unity host, kept between sessions).
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchPauseView : ITouchView
    {
        const int GridH = 346;

        readonly TouchBattleView battle;
        int downKind; // 1 resume, 2 sound, 3 vibration, 4 save and quit
        float shownAt = -1f;

        public TouchPauseView(TouchBattleView battle)
        {
            this.battle = battle;
        }

        public bool Active()
        {
            try
            {
                return C_PortUi.pauseView();
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
        }

        static bool CanQuit()
        {
            return C_PortUi.pauseKind() == 1;
        }

        static Rect PanelRect()
        {
            return CanQuit() ? TouchUi.R(14, 50, 152, 236) : TouchUi.R(14, 82, 152, 172);
        }

        static float Top()
        {
            return TouchUi.V(new Vector2(0, PanelRect().y)).y;
        }

        static Rect ResumeRect()
        {
            return TouchUi.R(26, Top() + 60, 128, 30);
        }

        static Rect SoundRect()
        {
            return TouchUi.R(26, Top() + 98, 128, 24);
        }

        static Rect VibrateRect()
        {
            return TouchUi.R(26, Top() + 128, 128, 24);
        }

        static Rect QuitRect()
        {
            return TouchUi.R(26, Top() + 168, 128, 24);
        }

        int Hit(Vector2 p)
        {
            if (ResumeRect().Contains(p))
            {
                return 1;
            }
            if (SoundRect().Contains(p))
            {
                return 2;
            }
            if (VibrateRect().Contains(p))
            {
                return 3;
            }
            return CanQuit() && QuitRect().Contains(p) ? 4 : 0;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (shownAt < 0f)
            {
                shownAt = Time.unscaledTime;
            }
            TouchUi.Begin(GridH);
            Vector2 p = e.mousePosition;
            J2MEGameHost host = J2MEGameHost.Instance;
            if (e.type == EventType.MouseDown && e.button == 0)
            {
                downKind = Hit(p);
                if (downKind != 0)
                {
                    Haptics.Tap(haptics);
                }
                e.Use();
            }
            else if (e.type == EventType.MouseUp)
            {
                if (downKind != 0 && Hit(p) == downKind)
                {
                    switch (downKind)
                    {
                        case 1:
                            C_PortUi.request(C_PortUi.f_PAUSE_RESUME, 0);
                            break;
                        case 2:
                            if (host != null)
                            {
                                host.SoundOn = !host.SoundOn;
                            }
                            break;
                        case 3:
                            if (host != null)
                            {
                                host.HapticsOn = !host.HapticsOn;
                                Haptics.Tap(host.HapticsOn);
                            }
                            break;
                        case 4:
                            C_PortUi.request(C_PortUi.f_PAUSE_QUIT, 0);
                            break;
                    }
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
                if (battle != null)
                {
                    battle.DrawBackdrop(lcd);
                    TouchUi.Begin(GridH);
                }
                else
                {
                    TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
                }
                Draw(host);
            }
        }

        void Draw(J2MEGameHost host)
        {
            float k = TouchUi.K;
            float t = Mathf.Clamp01((Time.unscaledTime - shownAt) / 0.18f);
            TouchUi.Dim(0.62f * t);
            Color old = GUI.color;
            GUI.color = old * new Color(1f, 1f, 1f, t);
            Rect panel = PanelRect();
            panel.y += (1f - t) * 12 * k;
            float top = TouchUi.V(new Vector2(0, panel.y)).y;
            TouchUi.Panel(panel);
            TouchUi.PixelText("TẠM DỪNG", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 18, 0, 0).y, 1, true);
            string sub = CanQuit() ? "Trận đấu đang tạm dừng" : "Bạn vừa rời trò chơi, trận đấu đã tạm dừng";
            TouchUi.Label(TouchUi.R(16, top + 30, 148, 26), sub, 7, TextAnchor.MiddleCenter, TouchUi.TextSoft, false, true);

            TouchUi.RedButton(ResumeRect(), "TIẾP TỤC", true, downKind == 1);
            bool sound = host == null || host.SoundOn, vibrate = host == null || host.HapticsOn;
            Toggle(SoundRect(), "ÂM THANH", sound, downKind == 2);
            Toggle(VibrateRect(), "RUNG", vibrate, downKind == 3);
            if (CanQuit())
            {
                Rect q = QuitRect();
                TouchUi.Fill(TouchUi.R(26, top + 160, 128, 1), TouchUi.Hex(0x9A6A98));
                TouchUi.TextButton(q, "LƯU VÀ THOÁT", Btn.Red, downKind == 4);
                TouchUi.Label(TouchUi.R(18, top + 194, 144, 30), "Chọn Chơi tiếp ở menu để đánh tiếp trận này", 6.5f,
                    TextAnchor.UpperCenter, TouchUi.Hex(0xD8C8DC), false, true);
            }
            GUI.color = old;
        }

        /// <summary>A setting: its name on the left, a BẬT / TẮT switch on the right.</summary>
        static void Toggle(Rect r, string name, bool on, bool down)
        {
            float k = TouchUi.K;
            TouchUi.Button(r, Btn.Dark, down);
            float dy = down ? k : 0;
            TouchUi.PixelText(name, 2, r.x + 8 * k, r.center.y + dy, 0, true);
            Rect sw = new Rect(r.xMax - 42 * k, r.y + 4 * k + dy, 36 * k, r.height - 8 * k);
            TouchUi.Fill(sw, TouchUi.Outline);
            TouchUi.Fill(new Rect(sw.x + k, sw.y + k, sw.width - 2 * k, sw.height - 2 * k), on ? TouchUi.Hex(0x2F8A22) : TouchUi.Hex(0x4E4A50));
            TouchUi.PixelText(on ? "BẬT" : "TẮT", 2, sw.center.x, sw.center.y, 1, true);
        }
    }
}
