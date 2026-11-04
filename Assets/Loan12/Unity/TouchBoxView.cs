// Gift boxes (hộp quà): dropped in battles (a line of 4 or more) and given by quests, they wait in
// PortMap.boxes and are opened here, on the map, once the results and dialogues are over.
//   1. the closed chest with how many boxes there are, MỞ HỘP
//   2. what they held (one item each, already in the bag), XEM QUẢNG CÁO · X2 (a rewarded ad gives the
//      same items once more) and NHẬN
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBoxView : ITouchView, IOwnsKeys
    {
        int stage; // 0 closed, 1 opened
        int count; // boxes opened
        bool doubled;
        float openedAt;

        bool visible;         // on screen: stays until NHẬN, unless a dialogue or the game takes the map
        float calmSince = -1f; // since when the map has been free (it flickers between free and busy
        float lastCheck = -1f; // for a moment after the results): the boxes wait for it to settle
        float shownAt = -10f;  // the box screen fades in over the map

        public bool Active()
        {
            try
            {
                float now = Time.unscaledTime;
                if (now - lastCheck > 0.2f)
                {
                    calmSince = -1f; // not asked for a while (another view had the screen): start again
                }
                lastCheck = now;
                bool onMap = C_PortScreens.screenKind() == 4 && !C_PortScreens.hasDialog();
                if (stage == 1)
                {
                    return onMap;
                }
                if (C_PortMap.f_boxes <= 0)
                {
                    visible = false;
                    return false;
                }
                if (visible)
                {
                    if (onMap && !C_PortMap.talking() && !C_PortMap.walking() && !C_PortUi.prepareView())
                    {
                        return true;
                    }
                    visible = false;
                    calmSince = -1f;
                    return false;
                }
                if (onMap && C_PortMap.free() && !C_PortMap.walking())
                {
                    if (calmSince < 0)
                    {
                        calmSince = now;
                    }
                    if (now - calmSince >= 0.5f)
                    {
                        visible = true;
                        shownAt = now;
                        return true;
                    }
                }
                else
                {
                    calmSince = -1f;
                }
                return false;
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
        }

        public int Stage
        {
            get { return stage; }
        }

        /// <summary>MỞ HỘP: the waiting boxes are opened into the bag.</summary>
        public void Open()
        {
            count = C_PortMap.f_boxes;
            C_PortMap.openBoxes();
            stage = 1;
            doubled = false;
            openedAt = Time.unscaledTime;
        }

        /// <summary>NHẬN: back to the map.</summary>
        public void Take()
        {
            stage = 0;
            visible = false;
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            Kit.Begin(e);
            Rewards.Tick();
            float k = TouchUi.K, H = Kit.H;
            int[] types = MapUi.Items;
            int kinds = 0;
            foreach (int t in types)
            {
                if (C_PortMap.opened(t) > 0)
                {
                    kinds++;
                }
            }
            float panelH = stage == 0 ? 176 : 130 + kinds * 26;
            float top = Mathf.Round((H - panelH) / 2);
            Rect panel = TouchUi.R(10, top, 160, panelH);

            Rect open = TouchUi.R(30, top + panelH - 40, 120, 26);
            Rect ad = TouchUi.R(22, top + panelH - 70, 136, 24);
            Rect take = TouchUi.R(46, top + panelH - 40, 88, 26);
            if (!Rewards.Busy)
            {
                if (stage == 0 && Kit.Tap(1, open))
                {
                    Open();
                }
                else if (stage == 1 && !doubled && Kit.Tap(2, ad))
                {
                    Rewards.Show("boxes_x2", r =>
                    {
                        if (r == 1 && !doubled)
                        {
                            C_PortMap.doubleOpened();
                            doubled = true;
                            openedAt = Time.unscaledTime;
                        }
                    });
                }
                else if (stage == 1 && Kit.Tap(3, take))
                {
                    Take();
                }
            }
            if (Kit.Back())
            {
                e.Use();
            }

            if (Kit.Paint)
            {
                float fade = Mathf.Clamp01((Time.unscaledTime - shownAt) / 0.25f);
                MapBackdrop.Draw(0.78f * fade);
                if (fade < 1f)
                {
                    // the panel rises into place as the map darkens
                    float rise = (1f - fade) * 12f * k;
                    panel.y += rise;
                    TouchUi.Panel(panel);
                    Rewards.DrawOverlay();
                    Kit.End();
                    return;
                }
                TouchUi.Panel(panel);
                TouchUi.PixelText("HỘP QUÀ", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 12, 0, 0).y, 1, true);
                Texture2D chest = TouchUi.Img("/r/g");
                if (stage == 0)
                {
                    int n = C_PortMap.f_boxes;
                    // the chest bobbing in a soft glow
                    float bob = Mathf.Round(Mathf.Sin(Time.unscaledTime * 4f) * 2f);
                    Vector2 c = TouchUi.R(90, top + 72, 0, 0).position;
                    float glow = 0.18f + 0.08f * Mathf.Sin(Time.unscaledTime * 3f);
                    for (int i = 3; i >= 1; i--)
                    {
                        float r = (24 + i * 8) * k;
                        TouchUi.Fill(new Rect(c.x - r, c.y - r * 0.8f, 2 * r, 1.6f * r), new Color(1f, 0.85f, 0.35f, glow / i));
                    }
                    float cw = chest.width * 3 * k, ch = chest.height * 3 * k;
                    TouchUi.Sprite(chest, new Rect(c.x - cw / 2, c.y - ch / 2 + bob * k, cw, ch));
                    TouchUi.Label(TouchUi.R(14, top + 108, 152, 14), n > 1 ? "Bạn nhận được " + n + " hộp quà" : "Bạn nhận được 1 hộp quà", 7.5f,
                        TextAnchor.MiddleCenter, Kit.Cream, true);
                    TouchUi.Label(TouchUi.R(14, top + 122, 152, 12), "Mỗi hộp có 1 vật phẩm", 6.5f, TextAnchor.MiddleCenter, Kit.Muted);
                    TouchUi.RedButton(open, "MỞ HỘP", true, Kit.Down(1));
                }
                else
                {
                    TouchUi.Label(TouchUi.R(14, top + 24, 152, 12), "Đã mở " + count + " hộp quà" + (doubled ? " · x2" : ""), 7,
                        TextAnchor.MiddleCenter, doubled ? TouchUi.TextGreen : Kit.Cream, true);
                    float age = Time.unscaledTime - openedAt;
                    float y = top + 42;
                    int row = 0;
                    foreach (int t in types)
                    {
                        int got = C_PortMap.opened(t);
                        if (got <= 0)
                        {
                            continue;
                        }
                        // rows appear one after the other
                        float a = Mathf.Clamp01((age - row * 0.12f) / 0.2f);
                        Color old = Gx.color;
                        Gx.color = new Color(1, 1, 1, a);
                        Rect r = TouchUi.R(20, y, 140, 23);
                        TouchUi.Fill(r, TouchUi.PurpleDark);
                        TouchUi.Item(t, TouchUi.R(23, y + 1.5f, 20, 20));
                        TouchUi.Label(TouchUi.R(48, y, 80, 23), C_PortUi.itemName(t), 6.5f, TextAnchor.MiddleLeft, Color.white, false);
                        TouchUi.PixelText("+" + (doubled ? got * 2 : got), 1, r.xMax - 6 * k, r.center.y, 2, true);
                        Gx.color = old;
                        y += 26;
                        row++;
                    }
                    TouchUi.Label(TouchUi.R(14, y + 2, 152, 12), "Đã cất vào túi đồ", 6.25f, TextAnchor.MiddleCenter, Kit.Muted);
                    if (!doubled)
                    {
                        Kit.AdButton(ad, "XEM QUẢNG CÁO · X2", Kit.Down(2), !Rewards.Busy);
                    }
                    TouchUi.TextButton(take, "NHẬN", Btn.Red, Kit.Down(3));
                }
            }
            Rewards.DrawOverlay();
            Kit.End();
        }
    }
}
