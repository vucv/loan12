// The few IMGUI drawing calls of the touch views (textures, groups, tint, text, screen size) go through
// here. On a phone they are the GUI calls themselves. For the screenshot tour of a desktop build run
// without a screen ("-batchmode -loan12-tour <folder>": a locked Mac renders no window, and batch mode
// calls no OnGUI) the host draws the views each frame into a render texture instead (Offscreen): the
// same calls are then made with GL quads, and texts with TextGenerator.
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class Gx
    {
        public static bool Offscreen;
        public static int OffW = 720, OffH = 1560;

        static Color tint = Color.white;
        static Vector2 offset;
        static readonly Stack<Vector2> groups = new Stack<Vector2>();
        static readonly Stack<Rect> clips = new Stack<Rect>();
        static Rect clip = new Rect(-1e6f, -1e6f, 2e6f, 2e6f); // like a GUI group, offscreen drawing stays inside it
        static Material texMat;
        static TextGenerator gen;
        static readonly List<UIVertex> verts = new List<UIVertex>();

        public static int Width
        {
            get { return Offscreen ? OffW : Screen.width; }
        }

        public static int Height
        {
            get { return Offscreen ? OffH : Screen.height; }
        }

        public static Rect SafeArea
        {
            get { return Offscreen ? new Rect(0, 0, OffW, OffH) : UnityEngine.Device.Screen.safeArea; }
        }

        public static Color color
        {
            get { return Offscreen ? tint : GUI.color; }
            set
            {
                if (Offscreen)
                {
                    tint = value;
                }
                else
                {
                    GUI.color = value;
                }
            }
        }

        /// <summary>Start of an offscreen frame: pixel coordinates, top-left origin.</summary>
        public static void BeginFrame()
        {
            tint = Color.white;
            offset = Vector2.zero;
            groups.Clear();
            clips.Clear();
            clip = new Rect(-1e6f, -1e6f, 2e6f, 2e6f);
        }

        public static void BeginGroup(Rect r)
        {
            if (!Offscreen)
            {
                GUI.BeginGroup(r);
                return;
            }
            groups.Push(offset);
            clips.Push(clip);
            Rect abs = new Rect(r.x + offset.x, r.y + offset.y, r.width, r.height);
            float x0 = Mathf.Max(abs.x, clip.x), y0 = Mathf.Max(abs.y, clip.y);
            float x1 = Mathf.Min(abs.xMax, clip.xMax), y1 = Mathf.Min(abs.yMax, clip.yMax);
            clip = new Rect(x0, y0, Mathf.Max(0, x1 - x0), Mathf.Max(0, y1 - y0));
            offset += r.position;
        }

        public static void EndGroup()
        {
            if (!Offscreen)
            {
                GUI.EndGroup();
                return;
            }
            offset = groups.Count > 0 ? groups.Pop() : Vector2.zero;
            clip = clips.Count > 0 ? clips.Pop() : new Rect(-1e6f, -1e6f, 2e6f, 2e6f);
        }

        public static void DrawTexture(Rect r, Texture t)
        {
            if (!Offscreen)
            {
                GUI.DrawTexture(r, t);
                return;
            }
            Quad(r, t, new Rect(0, 0, 1, 1));
        }

        public static void DrawTextureWithTexCoords(Rect r, Texture t, Rect uv, bool alphaBlend = true)
        {
            if (!Offscreen)
            {
                GUI.DrawTextureWithTexCoords(r, t, uv, alphaBlend);
                return;
            }
            Quad(r, t, uv);
        }

        static void Quad(Rect r, Texture t, Rect uv)
        {
            if (t == null)
            {
                return;
            }
            if (texMat == null)
            {
                Shader s = Shader.Find("Sprites/Default") ?? Shader.Find("UI/Default") ?? Shader.Find("Unlit/Transparent");
                texMat = new Material(s);
            }
            texMat.mainTexture = t;
            texMat.SetPass(0);
            float x0 = r.x + offset.x, y0 = r.y + offset.y, x1 = x0 + r.width, y1 = y0 + r.height;
            // uv as in GUI: y up from the bottom of the texture
            float u0 = uv.x, u1 = uv.x + uv.width, vBottom = uv.y, vTop = uv.y + uv.height;
            // cut to the group's rect, the uv along with it
            float cx0 = Mathf.Max(x0, clip.x), cy0 = Mathf.Max(y0, clip.y), cx1 = Mathf.Min(x1, clip.xMax), cy1 = Mathf.Min(y1, clip.yMax);
            if (cx1 <= cx0 || cy1 <= cy0)
            {
                return;
            }
            float fu0 = (cx0 - x0) / (x1 - x0), fu1 = (cx1 - x0) / (x1 - x0), fv0 = (cy0 - y0) / (y1 - y0), fv1 = (cy1 - y0) / (y1 - y0);
            float nu0 = Mathf.Lerp(u0, u1, fu0), nu1 = Mathf.Lerp(u0, u1, fu1);
            float nvTop = Mathf.Lerp(vTop, vBottom, fv0), nvBottom = Mathf.Lerp(vTop, vBottom, fv1);
            x0 = cx0;
            y0 = cy0;
            x1 = cx1;
            y1 = cy1;
            u0 = nu0;
            u1 = nu1;
            vTop = nvTop;
            vBottom = nvBottom;
            GL.Begin(GL.QUADS);
            GL.Color(tint);
            GL.TexCoord2(u0, vTop);
            GL.Vertex3(x0, y0, 0);
            GL.TexCoord2(u1, vTop);
            GL.Vertex3(x1, y0, 0);
            GL.TexCoord2(u1, vBottom);
            GL.Vertex3(x1, y1, 0);
            GL.TexCoord2(u0, vBottom);
            GL.Vertex3(x0, y1, 0);
            GL.End();
        }

        /// <summary>A text as GUIStyle.Draw would draw it (style font, size, alignment, wrap, normal text color).</summary>
        public static void DrawText(GUIStyle st, Rect r, GUIContent content)
        {
            if (!Offscreen)
            {
                st.Draw(r, content, false, false, false, false);
                return;
            }
            Font font = st.font;
            if (font == null || string.IsNullOrEmpty(content.text))
            {
                return;
            }
            if (gen == null)
            {
                gen = new TextGenerator();
            }
            Color c = st.normal.textColor * tint;
            TextGenerationSettings set = new TextGenerationSettings
            {
                font = font,
                fontSize = st.fontSize,
                fontStyle = st.fontStyle,
                textAnchor = st.alignment,
                alignByGeometry = false,
                richText = false,
                color = c,
                lineSpacing = 1f,
                scaleFactor = 1f,
                resizeTextForBestFit = false,
                updateBounds = false,
                horizontalOverflow = st.wordWrap ? HorizontalWrapMode.Wrap : HorizontalWrapMode.Overflow,
                verticalOverflow = VerticalWrapMode.Overflow,
                generationExtents = new Vector2(Mathf.Max(1, r.width), Mathf.Max(1, r.height)),
                pivot = Vector2.zero
            };
            gen.Populate(content.text, set);
            verts.Clear();
            gen.GetVertices(verts);
            font.material.SetPass(0);
            float ox = r.x + offset.x, oy = r.yMax + offset.y;
            GL.Begin(GL.QUADS);
            for (int i = 0; i + 3 < verts.Count; i += 4)
            {
                // a letter outside the group's rect is left out (whole letters only)
                float gy0 = oy - verts[i].position.y, gy2 = oy - verts[i + 2].position.y;
                float gx0 = ox + verts[i].position.x, gx2 = ox + verts[i + 2].position.x;
                if (Mathf.Min(gy0, gy2) < clip.y || Mathf.Max(gy0, gy2) > clip.yMax || Mathf.Min(gx0, gx2) < clip.x || Mathf.Max(gx0, gx2) > clip.xMax)
                {
                    continue;
                }
                for (int j = 0; j < 4; j++)
                {
                    UIVertex v = verts[i + j];
                    GL.Color(v.color);
                    GL.TexCoord2(v.uv0.x, v.uv0.y);
                    GL.Vertex3(ox + v.position.x, oy - v.position.y, 0);
                }
            }
            GL.End();
        }
    }
}
