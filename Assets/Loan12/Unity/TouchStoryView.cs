// The intro and ending scroll (the game's df screen) in English, 日本語, 한국어: the game keeps its picture
// and its timing, hides its own Vietnamese lines (PortScreens.storyHidden) and this view draws the
// translated story over it, scrolled as far as the game's text has gone. In Vietnamese the game draws
// it as before. BỎ QUA (top right) skips like the game's soft key.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchStoryView : ITouchView, IOwnsKeys
    {
        public bool Active()
        {
            try
            {
                return Loc.Current != Lang.Vi && C_PortScreens.screenKind() == 5 && C_PortScreens.story() >= 0 && !C_PortScreens.hasDialog();
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
            float k = TouchUi.K;
            int kind = C_PortScreens.story();
            string text = Loc.Block(kind == 1 ? "[ending]" : "[intro]") ?? "";
            if (kind == 1)
            {
                text = text.Replace("{s}", Loc.NoAccents(C_PortMap.heroName() ?? ""));
            }
            Rect skip = TouchUi.R(118, 8, 56, 18);
            if ((Kit.Tap(1, skip) || Kit.Back()) && kind >= 0)
            {
                C_PortScreens.request(C_PortScreens.f_SKIP, 0);
                if (e.type == EventType.KeyDown)
                {
                    e.Use();
                }
            }
            if (Kit.Paint)
            {
                // the game's picture, fitted, then a dark veil for the text
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x05070C));
                float s = Mathf.Min(Gx.Width / (float) J2MEGameHost.LcdWidth, Gx.Height / (float) J2MEGameHost.LcdHeight);
                float w = J2MEGameHost.LcdWidth * s, h = J2MEGameHost.LcdHeight * s;
                Gx.DrawTextureWithTexCoords(new Rect((Gx.Width - w) / 2, (Gx.Height - h) / 2, w, h), lcd, new Rect(0, 1, 1, -1), false);
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), new Color(0.02f, 0.01f, 0.03f, 0.55f));

                const float size = 7.5f, width = 150;
                float lh = TouchUi.LineHeight(size);
                int lines = TouchUi.Lines(text, width, size);
                float contentH = lines * lh;
                float p = Mathf.Clamp01(C_PortScreens.storyProgress());
                // enters from the bottom, leaves at the top, as far as the game's own text
                float top = Kit.H - p * (contentH + Kit.H - 30);
                Rect area = TouchUi.R(0, 30, 180, Kit.H - 30);
                Gx.BeginGroup(area);
                Rect r = new Rect(15 * k, (top - 30) * k, width * k, contentH * k + lh * k);
                TouchUi.Label(r, text, size, TextAnchor.UpperCenter, TouchUi.Hex(0xFFE9B8), false, true);
                Gx.EndGroup();

                TouchUi.Button(skip, Btn.Dark, Kit.Down(1));
                TouchUi.PixelText("BỎ QUA", 1, skip.center.x, skip.center.y + (Kit.Down(1) ? k : 0), 1, true);
            }
            Kit.End();
        }
    }
}
