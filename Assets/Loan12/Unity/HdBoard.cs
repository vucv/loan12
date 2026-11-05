// High-resolution battle board (setting "Đồ họa nét cao"): the game does not draw the gems, selection
// frames, explosions and sparkles into its buffer while they are on the board; it records them
// (J2ME.HdLayer) and this class draws the high-resolution pictures (Resources/Loan12HD/<name>.bytes, PNG
// at 4x, made from Art/board-hd) in their place, in three layers:
//   1. the board's background (the HD version of bkboard),
//   2. the recorded pictures, in the order the game drew them,
//   3. what the game drew over them (skill effects…), copied by the layer on transparent pixels.
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class HdBoard
    {
        const int Scale = 4;
        static readonly Dictionary<string, Texture2D> textures = new Dictionary<string, Texture2D>();
        static bool? available;

        // the frame shown (copied from the layer's published frame while the battle is clean)
        static int count;
        static J2ME.Image[] imgs = new J2ME.Image[0];
        static int[] ops = new int[0];
        static int dx, dy, ox, oy, ow, oh;
        static Texture2D overTex;
        static int shownSerial = -1;

        /// <summary>True when the pictures are in the build.</summary>
        public static bool Available
        {
            get
            {
                if (available == null)
                {
                    available = Resources.Load<TextAsset>("Loan12HD/heart") != null;
                }
                return available.Value;
            }
        }

        /// <summary>Before each game tick: record the board when the setting is on and a battle is shown.</summary>
        public static void Prepare(bool on)
        {
            bool en = false;
            try
            {
                en = on && Available && C_PortUi.battle() != null;
                if (en)
                {
                    int x = C_PortUi.board(0), y = C_PortUi.board(1), w = C_PortUi.board(2), h = C_PortUi.board(3);
                    J2ME.HdLayer.rx0 = x;
                    J2ME.HdLayer.ry0 = y;
                    J2ME.HdLayer.rx1 = x + w;
                    J2ME.HdLayer.ry1 = y + h;
                }
            }
            catch (System.Exception)
            {
                en = false;
            }
            if (en != J2ME.HdLayer.enabled)
            {
                J2ME.HdLayer.enabled = en;
                J2ME.HdLayer.clear();
                count = 0;
            }
        }

        /// <summary>Takes the layer's last frame as the one to show (with the board's snapshot).</summary>
        public static void Capture()
        {
            if (!J2ME.HdLayer.enabled)
            {
                count = 0;
                return;
            }
            if (J2ME.HdLayer.serial == shownSerial)
            {
                return;
            }
            shownSerial = J2ME.HdLayer.serial;
            count = J2ME.HdLayer.pubCount;
            if (imgs.Length < count)
            {
                imgs = new J2ME.Image[J2ME.HdLayer.pubImgs.Length];
                ops = new int[J2ME.HdLayer.pubOps.Length];
            }
            System.Array.Copy(J2ME.HdLayer.pubImgs, imgs, count);
            System.Array.Copy(J2ME.HdLayer.pubOps, ops, count * J2ME.HdLayer.OP);
            dx = J2ME.HdLayer.pubDx;
            dy = J2ME.HdLayer.pubDy;
            ox = J2ME.HdLayer.pubX0;
            oy = J2ME.HdLayer.pubY0;
            ow = J2ME.HdLayer.pubW;
            oh = J2ME.HdLayer.pubH;
            if (count > 0 && ow > 0 && oh > 0 && J2ME.HdLayer.pubOver.Length == ow * oh)
            {
                if (overTex == null || overTex.width != ow || overTex.height != oh)
                {
                    if (overTex != null)
                    {
                        Object.Destroy(overTex);
                    }
                    overTex = new Texture2D(ow, oh, TextureFormat.BGRA32, false);
                    overTex.filterMode = FilterMode.Point;
                    overTex.wrapMode = TextureWrapMode.Clamp;
                }
                Unity.Collections.NativeArray<int> px = overTex.GetRawTextureData<int>();
                px.CopyFrom(J2ME.HdLayer.pubOver);
                overTex.Apply(false, false);
            }
        }

        /// <summary>
        /// Draws the board (buffer rectangle bx,by,bw,bh shown in dst). False when there is nothing in high
        /// resolution to show: the caller then draws the game's own picture.
        /// </summary>
        public static bool Draw(Rect dst, int bx, int by, int bw, int bh)
        {
            if (count <= 0 || !J2ME.HdLayer.enabled)
            {
                return false;
            }
            float k = dst.width / bw;
            // 1. the board's background
            Texture2D bk = Tex("bkboard");
            if (bk != null)
            {
                float W = bk.width / (float) Scale, H = bk.height / (float) Scale;
                float u = bx - dx - C_cx.f_l, v = by - dy - C_cx.f_m;
                Gx.DrawTextureWithTexCoords(dst, bk, new Rect(u / W, 1f - (v + bh) / H, bw / W, bh / H), false);
            }
            // 2. the recorded pictures
            int n = J2ME.HdLayer.OP;
            for (int i = 0; i < count; i++)
            {
                J2ME.Image img = imgs[i];
                Texture2D t = img == null || img.hdName == null ? null : Tex(img.hdName.TrimStart('/'));
                if (t == null)
                {
                    continue;
                }
                int o = i * n;
                int sx = ops[o], sy = ops[o + 1], w = ops[o + 2], h = ops[o + 3], tr = ops[o + 4];
                int x = ops[o + 5], y = ops[o + 6], ix0 = ops[o + 7], iy0 = ops[o + 8], ix1 = ops[o + 9], iy1 = ops[o + 10];
                bool fx = tr == 2 || tr == 3, fy = tr == 1 || tr == 3;
                int vw = ix1 - ix0, vh = iy1 - iy0;
                int srcX = fx ? sx + w - (ix1 - x) : sx + (ix0 - x);
                int srcY = fy ? sy + h - (iy1 - y) : sy + (iy0 - y);
                float IW = img.width, IH = img.height;
                Rect uv = new Rect(srcX / IW, 1f - (srcY + vh) / IH, vw / IW, vh / IH);
                if (fx)
                {
                    uv = new Rect(uv.xMax, uv.y, -uv.width, uv.height);
                }
                if (fy)
                {
                    uv = new Rect(uv.x, uv.yMax, uv.width, -uv.height);
                }
                Rect r = new Rect(dst.x + (ix0 + dx - bx) * k, dst.y + (iy0 + dy - by) * k, vw * k, vh * k);
                Gx.DrawTextureWithTexCoords(r, t, uv, true);
            }
            // 3. what the game drew over them
            if (overTex != null && overTex.width == ow && overTex.height == oh)
            {
                Rect r = new Rect(dst.x + (ox + dx - bx) * k, dst.y + (oy + dy - by) * k, ow * k, oh * k);
                Gx.DrawTextureWithTexCoords(r, overTex, new Rect(0f, 1f, 1f, -1f), true);
            }
            return true;
        }

        static Texture2D Tex(string name)
        {
            Texture2D t;
            if (textures.TryGetValue(name, out t))
            {
                return t;
            }
            TextAsset a = Resources.Load<TextAsset>("Loan12HD/" + name);
            if (a != null)
            {
                t = new Texture2D(2, 2, TextureFormat.RGBA32, false);
                t.LoadImage(a.bytes);
                t.filterMode = FilterMode.Bilinear;
                t.wrapMode = TextureWrapMode.Clamp;
                t.name = "HD " + name;
            }
            textures[name] = t;
            return t;
        }
    }
}
