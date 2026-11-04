// The game's two swords crossing on the hero when he meets an enemy, drawn over the touch map at its scale.
// The battle box of the map opening before "Chuẩn bị" (and closing after "Rút lui"), the game's map state 3
// with its box animation: the touch map stays on the screen as it was, untouchable, instead of the game's own
// 240x320 picture at another scale (a jump). Last of the views: only when no other one is active.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchMapHoldView : ITouchView
    {
        public bool Active()
        {
            try
            {
                return TouchMapView.Instance != null && C_PortScreens.screenKind() == 4 && !C_PortScreens.hasDialog() && C_PortMap.state() == 3;
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
            if (e.type == EventType.Repaint)
            {
                TouchMapView.Instance.OnGUI(e, lcd, haptics); // painting only: no touch reaches it
                DrawSlash();
            }
        }

        /// <summary>The game's animation (s): two swords (m/fsw, one mirrored) closing in on the hero, then
        /// blue sparks (crystalblue, 3 frames) bursting from him; positions in map pixels, as the game.</summary>
        static void DrawSlash()
        {
            int[] a = C_PortMap.slash();
            if (a == null || a[6] != 0 || !TouchMapView.HasOrigin)
            {
                return;
            }
            float s = TouchMapView.S;
            Vector2 c = TouchMapView.Origin + new Vector2(a[0], a[1]) * s;
            float off = a[2] * s;
            Texture2D sword = TouchUi.Img("/m/fsw");
            float sw = sword.width * s, sh = sword.height * s;
            TouchUi.Sprite(sword, new Rect(c.x + off - sw / 2, c.y + off - sh / 2, sw, sh));
            TouchUi.Sprite(sword, new Rect(c.x - off - sw / 2, c.y + off - sh / 2, sw, sh), new Rect(0, 0, sword.width, sword.height), true);
            Texture2D spark = TouchUi.Img("/crystalblue");
            int fw = spark.width / 3, frame = Mathf.Clamp(a[3], 0, 2);
            Rect src = new Rect(fw * frame, 0, fw, spark.height);
            float pw = fw * s, ph = spark.height * s, h = a[5] * s;
            if (a[4] == 0)
            {
                TouchUi.Sprite(spark, new Rect(c.x - pw / 2, c.y - ph / 2, pw, ph), src);
                return;
            }
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    if (dx != 0 || dy != 0)
                    {
                        TouchUi.Sprite(spark, new Rect(c.x + dx * h - pw / 2, c.y + dy * h - ph / 2, pw, ph), src);
                    }
                }
            }
        }
    }
}
