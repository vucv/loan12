// The intro and ending scroll (the game's df screen), in every language: the game keeps its picture
// (the map, without its soft-key bar) and its timing, hides its own lines (PortScreens.storyHidden) and
// this view draws the story as paragraphs over it (the game's own lines joined in Vietnamese, the
// table's in the other languages), scrolled as far as the game's text has gone. BỎ QUA (top right)
// skips like the game's soft key.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchStoryView : ITouchView, IOwnsKeys
    {
        public bool Active()
        {
            try
            {
                return C_PortScreens.screenKind() == 5 && C_PortScreens.story() >= 0 && !C_PortScreens.hasDialog();
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
            string text = Loc.Block(kind == 1 ? "[ending]" : "[intro]") ?? C_PortScreens.storyText(kind) ?? "";
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
                // the game's picture over the whole width, without its soft-key bar (its own BỎ QUA)
                const float shown = 278f; // rows above the soft-key labels (BỎ QUA sits near row 288)
                float W = J2MEGameHost.LcdWidth, H = J2MEGameHost.LcdHeight;
                float s = Gx.Width / W;
                float w = W * s, h = shown * s;
                Gx.DrawTextureWithTexCoords(new Rect(0, 0, w, h), lcd, new Rect(0, 1, 1, -shown / H), false);
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
