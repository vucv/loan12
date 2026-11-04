// The intro and ending scroll (the game's df screen), in every language: the game keeps its map picture
// (its map rect, shown centred in a gold frame) and its timing, hides its own lines and soft key
// (PortScreens.storyHidden) and this view draws the story as paragraphs inside the picture (the game's own lines joined in Vietnamese, the
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
                TouchUi.Fill(new Rect(0, 0, Gx.Width, Gx.Height), TouchUi.Hex(0x05070C));
                // the game's map picture (its rect A in df: 20, 30, 200 x 130 on the 240 x 320 screen),
                // centred on the screen in a gold frame; the story scrolls inside it only
                const float mx = 20, my = 30, mw = 200, mh = 130;
                const float boxW = 172, boxH = boxW * mh / mw;
                float boxY = Mathf.Round((Kit.H - boxH) / 2);
                Rect box = TouchUi.R(4, boxY, boxW, boxH);
                TouchUi.Fill(new Rect(box.x - 2 * k, box.y - 2 * k, box.width + 4 * k, box.height + 4 * k), TouchUi.Outline);
                TouchUi.Fill(new Rect(box.x - k, box.y - k, box.width + 2 * k, box.height + 2 * k), TouchUi.GoldLine);
                float W = J2MEGameHost.LcdWidth, H = J2MEGameHost.LcdHeight;
                Gx.DrawTextureWithTexCoords(box, lcd, new Rect(mx / W, (my + mh) / H, mw / W, -mh / H), false);
                TouchUi.Fill(box, new Color(0.02f, 0.01f, 0.03f, 0.5f));

                const float size = 7.5f;
                float width = boxW - 16;
                float lh = TouchUi.LineHeight(size);
                int lines = TouchUi.Lines(text, width, size);
                float contentH = lines * lh;
                float p = Mathf.Clamp01(C_PortScreens.storyProgress());
                // enters at the bottom of the picture, leaves at its top, as far as the game's own text
                float top = boxH - p * (contentH + boxH);
                Gx.BeginGroup(box);
                Rect r = new Rect(8 * k, top * k, width * k, contentH * k + lh * k);
                TouchUi.Label(r, text, size, TextAnchor.UpperCenter, TouchUi.Hex(0xFFE9B8), false, true);
                Gx.EndGroup();

                TouchUi.Button(skip, Btn.Dark, Kit.Down(1));
                TouchUi.PixelText("BỎ QUA", 1, skip.center.x, skip.center.y + (Kit.Down(1) ? k : 0), 1, true);
            }
            Kit.End();
        }
    }
}
