// Shared drawing kit of the touch screens (plan/thiet-ke-giao-dien.md, option B).
//
// Everything is laid out on a virtual grid 180 original pixels wide, scaled by a whole number so the
// game's pixel art stays sharp. Sprites, frames and the bitmap fonts come from the game itself
// (Loan12.C_PortUi.image / text); small text, which needs lower-case letters, uses UiFont (a bold pixel
// font with every Vietnamese letter, drawn at the same scale and outlined like the game's fonts).
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    /// <summary>One screen drawn by Unity instead of the game's 240x320 layout.</summary>
    public interface ITouchView
    {
        bool Active();
        void OnGUI(Event e, Texture2D lcd, bool haptics);
        /// <summary>The view is going away: release anything still pressed, close popups.</summary>
        void Reset();
    }

    public enum Btn
    {
        Green,
        Gold,
        Red,
        Grey,
        Dark
    }

    public static class TouchUi
    {
        public const int GridW = 180;

        /// <summary>Size of one original pixel on the screen.</summary>
        public static float K = 1f;

        static Vector2 origin;
        static Rect area;

        static readonly Dictionary<string, Texture2D> images = new Dictionary<string, Texture2D>();
        static readonly Dictionary<string, Texture2D> texts = new Dictionary<string, Texture2D>();

        public static readonly Color Purple = Hex(0x773B75);
        public static readonly Color PurpleDark = Hex(0x4A2049);
        public static readonly Color Outline = Hex(0x020301);
        public static readonly Color GoldLine = Hex(0xC89B3C);
        public static readonly Color Background = Hex(0x170B12);
        public static readonly Color TextGold = Hex(0xFFE38A);
        public static readonly Color TextSoft = Hex(0xE8D0F0);
        public static readonly Color TextGreen = Hex(0xC8F0B0);

        static readonly int[][] Pal =
        {
            new[] {0x0F2E0A, 0x2F8A22, 0x8BE05C},
            new[] {0x4A2E04, 0xB97A14, 0xFFD36B},
            new[] {0x3D0707, 0xA3201B, 0xFF7A5C},
            new[] {0x1C1C1C, 0x4E4A50, 0x8C8890},
            new[] {0x140812, 0x3A1838, 0x6E3C6A},
        };

        // ------------------------------------------------------------------ layout

        /// <summary>Fits a 180 x gridH layout in the safe area (whole-number scale when possible).</summary>
        public static void Begin(int gridH)
        {
            Rect safe = UnityEngine.Device.Screen.safeArea;
            area = new Rect(safe.x, Screen.height - safe.yMax, safe.width, safe.height);
            if (area.width <= 0 || area.height <= 0)
            {
                area = new Rect(0, 0, Screen.width, Screen.height);
            }
            K = Mathf.Min(area.width / GridW, area.height / gridH);
            if (K >= 1f)
            {
                K = Mathf.Floor(K);
            }
            origin = new Vector2(Mathf.Round(area.x + (area.width - GridW * K) / 2f), Mathf.Round(area.y + (area.height - gridH * K) / 2f));
        }

        public static Rect R(float x, float y, float w, float h)
        {
            return new Rect(origin.x + x * K, origin.y + y * K, w * K, h * K);
        }

        /// <summary>Screen point -> virtual grid point.</summary>
        public static Vector2 V(Vector2 p)
        {
            return new Vector2((p.x - origin.x) / K, (p.y - origin.y) / K);
        }

        // ------------------------------------------------------------------ assets

        public static Texture2D Img(string name)
        {
            Texture2D t;
            if (!images.TryGetValue(name, out t) || t == null)
            {
                J2ME.Image img = C_PortUi.image(name);
                t = img == null ? Texture2D.whiteTexture : ToTexture(img, false);
                images[name] = t;
            }
            return t;
        }

        /// <summary>Text in the game's bitmap font: 1 = gold, 2 = white with outline.</summary>
        public static Texture2D Text(string s, int font)
        {
            string key = font + "|" + s;
            Texture2D t;
            if (!texts.TryGetValue(key, out t) || t == null)
            {
                t = ToTexture(C_PortUi.text(s, font), true);
                texts[key] = t;
            }
            return t;
        }

        /// <summary>J2ME image (ARGB, top-down) -> point-filtered texture (bottom-up rows).</summary>
        public static Texture2D ToTexture(J2ME.Image img, bool magentaIsClear)
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
                    byte a = (byte) ((uint) v >> 24);
                    if (magentaIsClear && (v & 0xFFFFFF) == 0xFF00FF)
                    {
                        a = 0;
                    }
                    px[(h - 1 - y) * w + x] = new Color32((byte) (v >> 16), (byte) (v >> 8), (byte) v, a);
                }
            }
            Texture2D t = new Texture2D(w, h, TextureFormat.RGBA32, false);
            t.filterMode = FilterMode.Point;
            t.wrapMode = TextureWrapMode.Clamp;
            t.SetPixels32(px);
            t.Apply(false, true);
            return t;
        }

        // ------------------------------------------------------------------ primitives

        public static Color Hex(int rgb)
        {
            return new Color32((byte) (rgb >> 16), (byte) (rgb >> 8), (byte) rgb, 255);
        }

        /// <summary>A plain rectangle (tinted by GUI.color too, so a whole view can fade).</summary>
        public static void Fill(Rect r, Color c)
        {
            Color old = GUI.color;
            GUI.color = old * c;
            GUI.DrawTexture(r, Texture2D.whiteTexture);
            GUI.color = old;
        }

        // ------------------------------------------------------------------ backgrounds

        static Texture2D tile, ramp;

        /// <summary>A rectangle of a game image as its own texture (point filtered), e.g. a tile to repeat.</summary>
        public static Texture2D Region(string name, int x, int y, int w, int h, TextureWrapMode wrap)
        {
            J2ME.Image img = C_PortUi.image(name);
            Texture2D t = new Texture2D(w, h, TextureFormat.RGBA32, false);
            t.filterMode = FilterMode.Point;
            t.wrapMode = wrap;
            if (img != null)
            {
                int[] argb = new int[w * h];
                img.getRGB(argb, 0, w, x, y, w, h);
                Color32[] px = new Color32[w * h];
                for (int row = 0; row < h; row++)
                {
                    for (int col = 0; col < w; col++)
                    {
                        int v = argb[row * w + col];
                        px[(h - 1 - row) * w + col] = new Color32((byte) (v >> 16), (byte) (v >> 8), (byte) v, 255);
                    }
                }
                t.SetPixels32(px);
            }
            t.Apply(false, true);
            return t;
        }

        /// <summary>
        /// Background of the touch screens: the meander tiles behind the game's battle board, darkened,
        /// with darker top and bottom edges.
        /// </summary>
        public static void Wallpaper(float brightness = 0.72f)
        {
            if (tile == null)
            {
                tile = Region("/bkboard", 35, 53, 21, 21, TextureWrapMode.Repeat);
            }
            float ts = 21 * K;
            // tiles start at the layout's left edge, so the pattern sits the same way on every screen
            float ox = origin.x - Mathf.Ceil(origin.x / ts) * ts, oy = origin.y - Mathf.Ceil(origin.y / ts) * ts;
            Rect all = new Rect(ox, oy, Screen.width - ox, Screen.height - oy);
            Color old = GUI.color;
            GUI.color = old * new Color(brightness, brightness * 0.94f, brightness * 0.98f, 1f);
            GUI.DrawTextureWithTexCoords(all, tile, new Rect(0, 1 - all.height / ts, all.width / ts, all.height / ts), false);
            GUI.color = old;
            float edge = Mathf.Min(Screen.height * 0.16f, 50 * K);
            Shade(new Rect(0, 0, Screen.width, edge), new Color(0.03f, 0.01f, 0.02f, 0.75f), true);
            Shade(new Rect(0, Screen.height - edge, Screen.width, edge), new Color(0.03f, 0.01f, 0.02f, 0.75f), false);
        }

        /// <summary>A colour fading out across the rectangle: strongest at the top (or at the bottom).</summary>
        public static void Shade(Rect r, Color c, bool strongAtTop)
        {
            if (ramp == null)
            {
                ramp = new Texture2D(1, 32, TextureFormat.RGBA32, false);
                ramp.wrapMode = TextureWrapMode.Clamp;
                ramp.filterMode = FilterMode.Bilinear;
                Color32[] px = new Color32[32];
                for (int i = 0; i < 32; i++)
                {
                    px[i] = new Color32(255, 255, 255, (byte) (255 * i / 31));
                }
                ramp.SetPixels32(px);
                ramp.Apply(false, true);
            }
            Color old = GUI.color;
            GUI.color = old * c;
            GUI.DrawTextureWithTexCoords(r, ramp, strongAtTop ? new Rect(0, 0, 1, 1) : new Rect(0, 1, 1, -1), true);
            GUI.color = old;
        }

        /// <summary>Part of a texture (source rectangle in pixels, top-left origin) into a screen rectangle.</summary>
        public static void Sprite(Texture2D t, Rect dst, Rect src, bool flipX = false, bool flipY = false)
        {
            Rect uv = new Rect(src.x / t.width, (t.height - src.y - src.height) / t.height, src.width / t.width, src.height / t.height);
            if (flipX)
            {
                uv.x += uv.width;
                uv.width = -uv.width;
            }
            if (flipY)
            {
                uv.y += uv.height;
                uv.height = -uv.height;
            }
            GUI.DrawTextureWithTexCoords(dst, t, uv, true);
        }

        public static void Sprite(Texture2D t, Rect dst)
        {
            Sprite(t, dst, new Rect(0, 0, t.width, t.height));
        }

        /// <summary>Nine-slice of a framed sprite, corners c pixels, drawn at the current scale.</summary>
        public static void Nine(Texture2D t, int c, Rect d)
        {
            float ck = c * K;
            int w = t.width, h = t.height;
            Sprite(t, new Rect(d.x, d.y, ck, ck), new Rect(0, 0, c, c));
            Sprite(t, new Rect(d.xMax - ck, d.y, ck, ck), new Rect(w - c, 0, c, c));
            Sprite(t, new Rect(d.x, d.yMax - ck, ck, ck), new Rect(0, h - c, c, c));
            Sprite(t, new Rect(d.xMax - ck, d.yMax - ck, ck, ck), new Rect(w - c, h - c, c, c));
            Sprite(t, new Rect(d.x + ck, d.y, d.width - 2 * ck, ck), new Rect(c, 0, w - 2 * c, c));
            Sprite(t, new Rect(d.x + ck, d.yMax - ck, d.width - 2 * ck, ck), new Rect(c, h - c, w - 2 * c, c));
            Sprite(t, new Rect(d.x, d.y + ck, ck, d.height - 2 * ck), new Rect(0, c, c, h - 2 * c));
            Sprite(t, new Rect(d.xMax - ck, d.y + ck, ck, d.height - 2 * ck), new Rect(w - c, c, c, h - 2 * c));
            Sprite(t, new Rect(d.x + ck, d.y + ck, d.width - 2 * ck, d.height - 2 * ck), new Rect(c, c, w - 2 * c, h - 2 * c));
        }

        /// <summary>The game's purple dialog box: dark outline, purple fill, gold meander corners.</summary>
        public static void Panel(Rect r)
        {
            float k = K;
            Fill(new Rect(r.x - k, r.y - k, r.width + 2 * k, r.height + 2 * k), Outline);
            Fill(r, Hex(0x2B1F0C));
            Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), Purple);
            Fill(new Rect(r.x + 2 * k, r.y + k, r.width - 4 * k, k), Hex(0x8B4F87));
            Texture2D c = Img("/_corner");
            float s = 16 * k;
            Sprite(c, new Rect(r.x + k, r.y + k, s, s), new Rect(0, 0, 16, 16));
            Sprite(c, new Rect(r.xMax - k - s, r.y + k, s, s), new Rect(0, 0, 16, 16), true);
            Sprite(c, new Rect(r.x + k, r.yMax - k - s, s, s), new Rect(0, 0, 16, 16), false, true);
            Sprite(c, new Rect(r.xMax - k - s, r.yMax - k - s, s, s), new Rect(0, 0, 16, 16), true, true);
        }

        /// <summary>Pixel button: outline, fill, light top/left edge, dark bottom edge.</summary>
        public static void Button(Rect r, Btn style, bool down)
        {
            int[] p = Pal[(int) style];
            float k = K;
            Fill(new Rect(r.x + k, r.y, r.width - 2 * k, r.height), Hex(p[0]));
            Fill(new Rect(r.x, r.y + k, r.width, r.height - 2 * k), Hex(p[0]));
            Fill(new Rect(r.x + k, r.y + k, r.width - 2 * k, r.height - 2 * k), down ? Hex(p[2]) : Hex(p[1]));
            if (!down)
            {
                Fill(new Rect(r.x + 2 * k, r.y + k, r.width - 4 * k, k), Hex(p[2]));
                Fill(new Rect(r.x + k, r.y + 2 * k, k, r.height - 4 * k), Hex(p[2]));
                Fill(new Rect(r.x + 2 * k, r.yMax - 2 * k, r.width - 4 * k, k), Hex(p[0]));
            }
        }

        /// <summary>The big dark-red framed button of the game (m/ff) with a gold title.</summary>
        public static void RedButton(Rect r, string text, bool enabled, bool down)
        {
            Color old = GUI.color;
            if (!enabled)
            {
                GUI.color = new Color(0.5f, 0.46f, 0.5f, 1f); // the whole button greyed out
            }
            Nine(Img("/m/ff"), 12, r);
            if (down)
            {
                Fill(new Rect(r.x + 4 * K, r.y + 4 * K, r.width - 8 * K, r.height - 8 * K), new Color(1f, 0.85f, 0.4f, 0.25f));
            }
            PixelText(text, 1, r.center.x, r.center.y + (down ? K : 0), 1, true);
            GUI.color = old;
        }

        /// <summary>A pixel button with its label in the game's white font, centred.</summary>
        public static void TextButton(Rect r, string text, Btn style, bool down, bool enabled = true)
        {
            Button(r, enabled ? style : Btn.Grey, down && enabled);
            Color old = GUI.color;
            if (!enabled)
            {
                GUI.color = old * new Color(0.72f, 0.7f, 0.75f, 1f);
            }
            PixelText(text, 2, r.center.x, r.center.y + (down && enabled ? K : 0), 1, true);
            GUI.color = old;
        }

        // The game's bitmap fonts draw a text 1 px in from the left of its image; capitals fill rows
        // 3..11 of the 18-row image (accents above, dots below). Texts are centred on the letters, not
        // on the image, or they sit a little high and to the left.
        const float InkLeft = 1f, CapTop = 3f, CapBottom = 12f;

        /// <summary>Width of a text in the game's bitmap font, in grid pixels.</summary>
        public static float PixelWidth(string s, int font)
        {
            return string.IsNullOrEmpty(s) ? 0 : Text(s, font).width - 3;
        }

        /// <summary>
        /// Text in the game's bitmap font. anchor 0 left, 1 centre, 2 right (of the letters); y is the top
        /// of the text image, or with centreY the middle of the capital letters.
        /// </summary>
        public static void PixelText(string s, int font, float x, float y, int anchor, bool centreY = false)
        {
            if (string.IsNullOrEmpty(s))
            {
                return;
            }
            Texture2D t = Text(s, font);
            float w = t.width * K, h = t.height * K, ink = (t.width - 3) * K;
            float left = anchor == 1 ? x - InkLeft * K - ink / 2f : (anchor == 2 ? x - InkLeft * K - ink : x - InkLeft * K);
            float top = centreY ? y - (CapTop + CapBottom) / 2f * K : y;
            GUI.DrawTexture(new Rect(Mathf.Round(left), Mathf.Round(top), w, h), t);
        }

        /// <summary>
        /// Text in the touch screens' pixel font (UiFont), tinted with color. The anchor places the letters
        /// in r by the capitals of the first line and the baseline of the last (accents stick out above,
        /// descenders below); wrap breaks the text at r's width. The font has a single size, so size and
        /// strong only document the intent of the caller.
        /// </summary>
        public static void Label(Rect r, string s, float size, TextAnchor anchor, Color color, bool strong = false, bool wrap = false)
        {
            if (string.IsNullOrEmpty(s))
            {
                return;
            }
            UiFont.Block b = UiFont.Get(s, wrap ? Mathf.Max(8, Mathf.FloorToInt(r.width / K + 0.01f)) : 0);
            int a = (int) anchor, col = a % 3, row = a / 3;
            float ink = b.width * K, block = UiFont.BlockHeight(b.lines) * K;
            float left = col == 0 ? r.x : (col == 1 ? r.center.x - ink / 2f : r.xMax - ink);
            float capTop = row == 0 ? r.y + 3 * K : (row == 1 ? r.center.y - block / 2f : r.yMax - block - 3 * K);
            Rect dst = new Rect(Mathf.Round(left - UiFont.Pad * K), Mathf.Round(capTop - b.firstCapTop * K), b.tex.width * K, b.tex.height * K);
            Color old = GUI.color;
            GUI.color = old * color;
            GUI.DrawTexture(dst, b.tex);
            GUI.color = old;
        }

        /// <summary>Lines a text takes when wrapped at width grid pixels.</summary>
        public static int Lines(string s, float width)
        {
            return string.IsNullOrEmpty(s) ? 0 : UiFont.Wrap(s, Mathf.FloorToInt(width + 0.01f)).Count;
        }

        /// <summary>Width of a one-line text in grid pixels.</summary>
        public static float TextWidth(string s)
        {
            return string.IsNullOrEmpty(s) ? 0 : UiFont.Width(s);
        }

        /// <summary>30x30 portrait from /avatar inside a dark frame (frame rect = portrait + 2 px).</summary>
        public static void Portrait(int idx, Rect frame, bool flipX = false)
        {
            Fill(frame, Outline);
            Fill(new Rect(frame.x + K, frame.y + K, frame.width - 2 * K, frame.height - 2 * K), GoldLine);
            Rect pic = new Rect(frame.x + 2 * K, frame.y + 2 * K, frame.width - 4 * K, frame.height - 4 * K);
            Sprite(Img("/avatar"), pic, new Rect(idx % 10 * 30, idx / 10 * 30, 30, 30), flipX);
        }

        /// <summary>Item icon (shop index 0..5) from /itemshops.</summary>
        public static void Item(int type, Rect dst, float dim = 1f)
        {
            Color old = GUI.color;
            GUI.color = new Color(dim, dim, dim, 1f);
            Sprite(Img("/itemshops"), dst, new Rect(type * 28, 0, 28, 28));
            GUI.color = old;
        }

        /// <summary>Darkens the whole screen behind a popup.</summary>
        public static void Dim(float alpha = 0.6f)
        {
            Fill(new Rect(0, 0, Screen.width, Screen.height), new Color(0.03f, 0.01f, 0.03f, alpha));
        }

        /// <summary>Downward pointer under a popup panel (to the button it belongs to).</summary>
        public static void Pointer(float cx, float yTop)
        {
            for (int i = 0; i < 7; i++)
            {
                float w = (7 - i) * 2 * K;
                Fill(new Rect(cx - w / 2 - K, yTop + i * K, w + 2 * K, K), Outline);
                if (i < 6)
                {
                    Fill(new Rect(cx - w / 2 + K, yTop + i * K, Mathf.Max(0, w - 2 * K), K), Purple);
                }
            }
        }

        public static string Money(int v)
        {
            return v.ToString("#,0", System.Globalization.CultureInfo.InvariantCulture).Replace(',', '.');
        }
    }

    /// <summary>The (up to) three items taken into battle; chosen on the "Chuẩn bị xuất trận" screen.</summary>
    public static class Loadout
    {
        const string Pref = "Loan12.Loadout";
        public const int Slots = 3;

        /// <summary>Item types the battle can use (Ngọc Ấn works by itself, Ngân Lượng is gold).</summary>
        public static readonly int[] Usable = {4, 3, 0, 1};

        public static int[] Get()
        {
            int[] r = {-1, -1, -1};
            string s = PlayerPrefs.GetString(Pref, "");
            if (s.Length == 0)
            {
                // nothing chosen yet: the first items the player owns
                int n = 0;
                foreach (int t in Usable)
                {
                    if (n < Slots && SafeOwned(t) > 0)
                    {
                        r[n++] = t;
                    }
                }
                return r;
            }
            string[] parts = s.Split(',');
            for (int i = 0; i < Slots && i < parts.Length; i++)
            {
                int v;
                if (int.TryParse(parts[i], out v) && System.Array.IndexOf(Usable, v) >= 0)
                {
                    r[i] = v;
                }
            }
            return r;
        }

        public static void Set(int[] slots)
        {
            PlayerPrefs.SetString(Pref, slots[0] + "," + slots[1] + "," + slots[2]);
            PlayerPrefs.Save();
        }

        static int SafeOwned(int t)
        {
            try
            {
                return C_PortUi.owned(t);
            }
            catch (System.Exception)
            {
                return 0;
            }
        }
    }
}
