// The small-text font of the touch screens: Galmuri9 by Lee Minseo (SIL Open Font License 1.1, see
// Resources/Loan12Fonts/Galmuri-OFL.md), baked by Converter/scripts/make_ui_font.py into 1-bit bitmaps
// at its own pixel size and made bold like the game's letters. Texts are drawn at the same whole-number
// scale as the game's pixel art, white with a 1-pixel dark outline like the game's fonts, and tinted
// with GUI.color. Each text (and wrap width) is turned into a texture once and kept.
using System.Collections.Generic;
using System.Text;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class UiFont
    {
        struct Glyph
        {
            public int xo, yo, w, h, adv;
            public byte[] bits; // rows, most significant bit first
        }

        /// <summary>A text turned into a texture (white letters, dark outline, transparent elsewhere).</summary>
        public sealed class Block
        {
            public Texture2D tex;
            public int width;      // of the widest line, outline excluded (pixels)
            public int lines;
            public int firstCapTop; // row of the texture where the capitals of the first line start
        }

        public const int Pad = 1;   // the outline
        const int Above = 4;        // room for stacked accents above the first line
        static readonly Color32 Ink = new Color32(255, 255, 255, 255), Edge = new Color32(0x14, 0x06, 0x12, 255);

        static Dictionary<int, Glyph> glyphs;
        static int ascent = 11, lineHeight = 13, capHeight = 9;
        static readonly Dictionary<string, Block> cache = new Dictionary<string, Block>();

        /// <summary>Distance between two lines, in pixels.</summary>
        public static int LineHeight
        {
            get
            {
                Load();
                return lineHeight;
            }
        }

        public static int CapHeight
        {
            get
            {
                Load();
                return capHeight;
            }
        }

        static void Load()
        {
            if (glyphs != null)
            {
                return;
            }
            glyphs = new Dictionary<int, Glyph>();
            TextAsset a = Resources.Load<TextAsset>("Loan12Fonts/ui_font");
            if (a == null)
            {
                return;
            }
            byte[] b = a.bytes;
            if (b.Length < 10 || b[0] != 'L' || b[1] != '1' || b[2] != '2' || b[3] != 'F')
            {
                return;
            }
            ascent = b[5];
            lineHeight = b[6];
            capHeight = b[7];
            int n = b[8] | (b[9] << 8), p = 10;
            for (int i = 0; i < n && p + 9 <= b.Length; i++)
            {
                int code = b[p] | (b[p + 1] << 8) | (b[p + 2] << 16) | (b[p + 3] << 24);
                Glyph g = new Glyph {xo = (sbyte) b[p + 4], yo = (sbyte) b[p + 5], w = b[p + 6], h = b[p + 7], adv = b[p + 8]};
                p += 9;
                int len = (g.w * g.h + 7) / 8;
                g.bits = new byte[len];
                System.Array.Copy(b, p, g.bits, 0, Mathf.Min(len, b.Length - p));
                p += len;
                glyphs[code] = g;
            }
        }

        static bool Find(char c, out Glyph g)
        {
            if (glyphs.TryGetValue(c, out g))
            {
                return true;
            }
            return glyphs.TryGetValue('?', out g);
        }

        /// <summary>Width of a one-line text in pixels (outline excluded).</summary>
        public static int Width(string s)
        {
            Load();
            int w = 0;
            foreach (char c in s)
            {
                Glyph g;
                if (Find(c, out g))
                {
                    w += g.adv;
                }
            }
            return w;
        }

        /// <summary>Breaks a text into lines no wider than width pixels (at spaces; "\n" forces a break).</summary>
        public static List<string> Wrap(string s, int width)
        {
            List<string> lines = new List<string>();
            foreach (string para in s.Split('\n'))
            {
                if (width <= 0)
                {
                    lines.Add(para);
                    continue;
                }
                StringBuilder cur = new StringBuilder();
                foreach (string word in para.Split(' '))
                {
                    string next = cur.Length == 0 ? word : cur + " " + word;
                    if (cur.Length > 0 && Width(next) > width)
                    {
                        lines.Add(cur.ToString());
                        cur.Length = 0;
                        cur.Append(word);
                    }
                    else
                    {
                        cur.Length = 0;
                        cur.Append(next);
                    }
                }
                lines.Add(cur.ToString());
            }
            return lines;
        }

        /// <summary>The texture of a text; wrapWidth 0 keeps it on one line. outline: the dark edge (not for dark text on light).</summary>
        public static Block Get(string s, int wrapWidth, bool outline = true)
        {
            Load();
            string key = (outline ? "o" : "p") + wrapWidth + "|" + s;
            Block blk;
            if (cache.TryGetValue(key, out blk) && blk.tex != null)
            {
                return blk;
            }
            if (cache.Count > 400)
            {
                // texts that change (counters, the line of events) would pile up: start again
                foreach (Block old in cache.Values)
                {
                    if (old.tex != null)
                    {
                        Object.Destroy(old.tex);
                    }
                }
                cache.Clear();
            }
            List<string> lines = Wrap(s, wrapWidth);
            int widest = 1;
            foreach (string ln in lines)
            {
                widest = Mathf.Max(widest, Width(ln));
            }
            int w = widest + 2 * Pad + 1, h = Above + (lines.Count - 1) * lineHeight + ascent + 3 + 2 * Pad;
            byte[] mask = new byte[w * h]; // 0 empty, 1 letter, 2 outline
            for (int li = 0; li < lines.Count; li++)
            {
                int x = Pad, baseline = Pad + Above + ascent + li * lineHeight;
                foreach (char c in lines[li])
                {
                    Glyph g;
                    if (!Find(c, out g))
                    {
                        continue;
                    }
                    for (int gy = 0; gy < g.h; gy++)
                    {
                        int y = baseline + g.yo + gy;
                        if (y < 0 || y >= h)
                        {
                            continue;
                        }
                        for (int gx = 0; gx < g.w; gx++)
                        {
                            int i = gy * g.w + gx;
                            int px = x + g.xo + gx;
                            if ((g.bits[i >> 3] & (0x80 >> (i & 7))) != 0 && px >= 0 && px < w)
                            {
                                mask[y * w + px] = 1;
                            }
                        }
                    }
                    x += g.adv;
                }
            }
            Color32[] pixels = new Color32[w * h];
            for (int y = 0; y < h; y++)
            {
                for (int x = 0; x < w; x++)
                {
                    Color32 c = new Color32(0, 0, 0, 0);
                    if (mask[y * w + x] == 1)
                    {
                        c = Ink;
                    }
                    else if (outline)
                    {
                        for (int dy = -1; dy <= 1 && c.a == 0; dy++)
                        {
                            for (int dx = -1; dx <= 1; dx++)
                            {
                                int yy = y + dy, xx = x + dx;
                                if (yy >= 0 && yy < h && xx >= 0 && xx < w && mask[yy * w + xx] == 1)
                                {
                                    c = Edge;
                                    break;
                                }
                            }
                        }
                    }
                    pixels[(h - 1 - y) * w + x] = c; // textures store the bottom row first
                }
            }
            Texture2D t = new Texture2D(w, h, TextureFormat.RGBA32, false);
            t.filterMode = FilterMode.Point;
            t.wrapMode = TextureWrapMode.Clamp;
            t.SetPixels32(pixels);
            t.Apply(false, true);
            blk = new Block {tex = t, width = widest, lines = lines.Count, firstCapTop = Pad + Above + ascent - capHeight};
            cache[key] = blk;
            return blk;
        }

        /// <summary>Height of a block from the capitals of its first line to the baseline of its last.</summary>
        public static int BlockHeight(int lines)
        {
            Load();
            return (lines - 1) * lineHeight + capHeight;
        }
    }
}
