// Runs the converted MIDlet inside Unity.
//
// Every frame: input -> J2ME.Host.tick() (game logic, timers, painting into the 240x320 LCD buffer) ->
// upload the LCD to a texture -> draw it (IMGUI) with an optional on-screen phone keypad.
// MIDP high-level screens (TextBox for the hero name / phone number, the debug log Form) are shown
// with IMGUI controls. Input uses IMGUI events, so it works with both the old Input Manager and the
// new Input System, and touches arrive as pointer events (the game supports a touch screen).
using System.Collections.Generic;
using System.Globalization;
using Unity.Collections;
using UnityEngine;

namespace Loan12.UnityPort
{
    public enum KeypadMode
    {
        /// <summary>Only on touch devices (Android / iOS).</summary>
        Auto,
        Always,
        Never
    }

    [DisallowMultipleComponent]
    [AddComponentMenu("Loan12/J2ME Game Host")]
    public sealed class J2MEGameHost : MonoBehaviour
    {
        public const int LcdWidth = 240;
        public const int LcdHeight = 320;

        /// <summary>Set to false (before the first scene loads) to stop the automatic host creation.</summary>
        public static bool AutoCreate = true;

        public static J2MEGameHost Instance { get; private set; }

        [Tooltip("On-screen phone keypad: soft keys, D-pad, OK, * and #. Auto = touch devices only.")]
        public KeypadMode keypad = KeypadMode.Auto;

        [Tooltip("Scale the 240x320 screen by whole multiples only (sharper pixels, larger borders).")]
        public bool integerScaling;

        [Tooltip("Point = crisp pixels like the phone, Bilinear = smoother.")]
        public FilterMode filtering = FilterMode.Point;

        [Range(0f, 1f)] public float volume = 1f;

        static bool startedBefore; // survives only when "Reload Domain" is disabled in the editor

        UnityBackend backend;
        Texture2D lcd;
        bool running;
        bool quitting;

        // input state
        readonly Dictionary<KeyCode, int> heldKeys = new Dictionary<KeyCode, int>();
        bool pointerOnLcd;
        int padHeldCode;

        // layout (GUI coordinates, origin top-left)
        Rect lcdRect;
        readonly List<PadButton> pad = new List<PadButton>();
        bool padVisible;

        // styles (rebuilt when the screen size changes)
        int styledFor = -1;
        GUIStyle padStyle, padDownStyle, panelStyle, titleStyle, fieldStyle, buttonStyle, lineStyle;

        // high-level screens
        J2ME.TextBox editing;
        string textBuffer = "";
        bool focusField;
        Vector2 formScroll;

        struct PadButton
        {
            public Rect rect;
            public string label;
            public int code;

            public PadButton(Rect rect, string label, int code)
            {
                this.rect = rect;
                this.label = label;
                this.code = code;
            }
        }

        public UnityBackend Backend
        {
            get { return backend; }
        }

        /// <summary>The emulated LCD (bottom-up rows; draw it with flipped texture coordinates).</summary>
        public Texture2D LcdTexture
        {
            get { return lcd; }
        }

        [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.AfterSceneLoad)]
        static void Bootstrap()
        {
            if (Instance == null && AutoCreate)
            {
                new GameObject("Loan12 J2ME Host").AddComponent<J2MEGameHost>();
            }
        }

        void Awake()
        {
            if (Instance != null && Instance != this)
            {
                Destroy(gameObject);
                return;
            }
            Instance = this;
            DontDestroyOnLoad(gameObject);

            // Java formats numbers culture-independently; so must the converted code
            CultureInfo.DefaultThreadCurrentCulture = CultureInfo.InvariantCulture;
            CultureInfo.CurrentCulture = CultureInfo.InvariantCulture;

            Application.targetFrameRate = 60;
            EnsureCamera();
            EnsureAudioListener();

            lcd = new Texture2D(LcdWidth, LcdHeight, TextureFormat.BGRA32, false);
            lcd.filterMode = filtering;
            lcd.wrapMode = TextureWrapMode.Clamp;
            lcd.name = "J2ME LCD";

            backend = new UnityBackend(gameObject);
            backend.SetMasterVolume(volume);
        }

        void Start()
        {
            if (Instance != this)
            {
                return;
            }
            if (startedBefore)
            {
                Debug.LogWarning("Loan12: the game was already started in this domain. The converted J2ME code keeps its state " +
                                 "in static fields, so enable 'Reload Domain' (Project Settings > Editor > Enter Play Mode Options).");
            }
            startedBefore = true;
            J2ME.Host.init(backend, LcdWidth, LcdHeight);
            J2ME.Host.start(new SQMidlet());
            running = true;
        }

        void Update()
        {
            if (!running)
            {
                return;
            }
            if (lcd.filterMode != filtering)
            {
                lcd.filterMode = filtering;
            }
            if (!Mathf.Approximately(backend.masterVolume, volume))
            {
                backend.SetMasterVolume(volume);
            }

            backend.UpdateAudio();
            J2ME.Host.tick();

            if (J2ME.Host.screenDirty && J2ME.Host.screen != null)
            {
                NativeArray<int> pixels = lcd.GetRawTextureData<int>();
                pixels.CopyFrom(J2ME.Host.screen);
                lcd.Apply(false, false);
                J2ME.Host.screenDirty = false;
            }

            if (backend.exitWasRequested || J2ME.Host.exitRequested)
            {
                Quit();
            }
        }

        void OnApplicationPause(bool pause)
        {
            if (!running)
            {
                return;
            }
            if (pause)
            {
                ReleaseAllInput();
                J2ME.Host.pause();
            }
            else
            {
                J2ME.Host.resume();
            }
        }

        void OnApplicationFocus(bool focus)
        {
            if (!focus && running)
            {
                ReleaseAllInput();
            }
        }

        void OnApplicationQuit()
        {
            if (running)
            {
                running = false;
                J2ME.Host.destroy();
            }
        }

        void OnDestroy()
        {
            if (Instance == this)
            {
                Instance = null;
            }
            if (lcd != null)
            {
                Destroy(lcd);
            }
        }

        void Quit()
        {
            if (quitting)
            {
                return;
            }
            quitting = true;
            running = false;
            backend.StopAllAudio();
#if UNITY_EDITOR
            UnityEditor.EditorApplication.isPlaying = false;
#else
            Application.Quit();
#endif
        }

        /// <summary>Unity plays no sound without an AudioListener in the scene.</summary>
        void EnsureAudioListener()
        {
#pragma warning disable 0618 // FindObjectOfType: kept for Unity 2021/2022 compatibility
            if (FindObjectOfType<AudioListener>() == null)
#pragma warning restore 0618
            {
                gameObject.AddComponent<AudioListener>();
            }
        }

        static void EnsureCamera()
        {
            if (Camera.main != null)
            {
                return;
            }
            GameObject go = new GameObject("Loan12 Camera");
            go.tag = "MainCamera";
            Camera cam = go.AddComponent<Camera>();
            cam.clearFlags = CameraClearFlags.SolidColor;
            cam.backgroundColor = Color.black;
            cam.cullingMask = 0;
            cam.orthographic = true;
            DontDestroyOnLoad(go);
        }

        // ============================================================================================
        // GUI

        void OnGUI()
        {
            if (lcd == null)
            {
                return;
            }
            EnsureStyles();
            ComputeLayout();

            J2ME.Displayable shown = running && J2ME.Host.display != null ? J2ME.Host.display.getShown() : null;
            bool highLevel = shown != null && !(shown is J2ME.Canvas);
            Event e = Event.current;

            if (running && !highLevel)
            {
                HandleCanvasInput(e);
            }

            if (e.type == EventType.Repaint)
            {
                GUI.DrawTextureWithTexCoords(lcdRect, lcd, new Rect(0f, 1f, 1f, -1f), false);
                if (padVisible && !highLevel)
                {
                    foreach (PadButton b in pad)
                    {
                        GUI.Box(b.rect, b.label, b.code == padHeldCode ? padDownStyle : padStyle);
                    }
                }
            }

            if (highLevel)
            {
                DrawHighLevelScreen(shown, e);
            }
            else if (editing != null)
            {
                editing = null;
            }
        }

        // ---- layout --------------------------------------------------------------------------

        void ComputeLayout()
        {
            Rect safe = Screen.safeArea;
            // Screen.safeArea is bottom-up; GUI coordinates are top-down
            Rect area = new Rect(safe.x, Screen.height - safe.yMax, safe.width, safe.height);
            if (area.width <= 0 || area.height <= 0)
            {
                area = new Rect(0, 0, Screen.width, Screen.height);
            }

            padVisible = keypad == KeypadMode.Always || (keypad == KeypadMode.Auto && Application.isMobilePlatform);
            pad.Clear();
            bool portrait = area.height >= area.width;

            Rect lcdArea = area;
            if (padVisible && portrait)
            {
                float padH = Mathf.Min(area.height * 0.34f, area.width * 0.62f);
                lcdArea = new Rect(area.x, area.y, area.width, area.height - padH);
            }
            else if (padVisible)
            {
                float side = Mathf.Min(area.width * 0.25f, area.height * 0.75f);
                lcdArea = new Rect(area.x + side, area.y, area.width - 2 * side, area.height);
            }

            float scale = Mathf.Min(lcdArea.width / LcdWidth, lcdArea.height / LcdHeight);
            if (integerScaling && scale >= 1f)
            {
                scale = Mathf.Floor(scale);
            }
            float w = LcdWidth * scale;
            float h = LcdHeight * scale;
            lcdRect = new Rect(Mathf.Round(lcdArea.x + (lcdArea.width - w) / 2f), Mathf.Round(lcdArea.y + (lcdArea.height - h) / 2f), w, h);

            if (!padVisible)
            {
                return;
            }
            if (portrait)
            {
                Rect padArea = new Rect(area.x, lcdArea.yMax, area.width, area.yMax - lcdArea.yMax);
                float cw = padArea.width / 5f;
                float ch = padArea.height / 3f;
                AddPad(padArea, cw, ch, 0, 0, "Trái", -6);
                AddPad(padArea, cw, ch, 4, 0, "Phải", -7);
                AddPad(padArea, cw, ch, 2, 0, "▲", -1);
                AddPad(padArea, cw, ch, 1, 1, "◄", -3);
                AddPad(padArea, cw, ch, 2, 1, "OK", -5);
                AddPad(padArea, cw, ch, 3, 1, "►", -4);
                AddPad(padArea, cw, ch, 2, 2, "▼", -2);
                AddPad(padArea, cw, ch, 0, 2, "*", 42);
                AddPad(padArea, cw, ch, 4, 2, "#", 35);
            }
            else
            {
                Rect left = new Rect(area.x, area.y, lcdArea.x - area.x, area.height);
                Rect right = new Rect(lcdArea.xMax, area.y, area.xMax - lcdArea.xMax, area.height);
                float cw = left.width / 3f;
                float ch = Mathf.Min(left.height / 4f, cw);
                Rect l = new Rect(left.x, left.y + (left.height - ch * 3) / 2f, left.width, ch * 3);
                AddPad(l, cw, ch, 0, 0, "Trái", -6);
                AddPad(l, cw, ch, 1, 0, "▲", -1);
                AddPad(l, cw, ch, 0, 1, "◄", -3);
                AddPad(l, cw, ch, 1, 1, "OK", -5);
                AddPad(l, cw, ch, 2, 1, "►", -4);
                AddPad(l, cw, ch, 1, 2, "▼", -2);
                AddPad(l, cw, ch, 0, 2, "*", 42);
                Rect r = new Rect(right.x, l.y, right.width, ch * 3);
                AddPad(r, cw, ch, 2, 0, "Phải", -7);
                AddPad(r, cw, ch, 1, 1, "OK", -5);
                AddPad(r, cw, ch, 2, 2, "#", 35);
            }
        }

        void AddPad(Rect area, float cw, float ch, int col, int row, string label, int code)
        {
            float m = Mathf.Min(cw, ch) * 0.08f;
            pad.Add(new PadButton(new Rect(area.x + col * cw + m, area.y + row * ch + m, cw - 2 * m, ch - 2 * m), label, code));
        }

        int PadCodeAt(Vector2 p)
        {
            if (!padVisible)
            {
                return 0;
            }
            foreach (PadButton b in pad)
            {
                if (b.rect.Contains(p))
                {
                    return b.code;
                }
            }
            return 0;
        }

        void ToLcd(Vector2 p, out int x, out int y)
        {
            x = Mathf.Clamp(Mathf.FloorToInt((p.x - lcdRect.x) / lcdRect.width * LcdWidth), 0, LcdWidth - 1);
            y = Mathf.Clamp(Mathf.FloorToInt((p.y - lcdRect.y) / lcdRect.height * LcdHeight), 0, LcdHeight - 1);
        }

        // ---- input ---------------------------------------------------------------------------

        void HandleCanvasInput(Event e)
        {
            int x, y;
            switch (e.type)
            {
                case EventType.KeyDown:
                {
                    int code = MapKey(e.keyCode);
                    if (code != 0)
                    {
                        if (!heldKeys.ContainsKey(e.keyCode))
                        {
                            heldKeys[e.keyCode] = code;
                            J2ME.Host.keyPressed(code);
                        }
                        e.Use();
                    }
                    break;
                }
                case EventType.KeyUp:
                {
                    int code;
                    if (heldKeys.TryGetValue(e.keyCode, out code))
                    {
                        heldKeys.Remove(e.keyCode);
                        J2ME.Host.keyReleased(code);
                        e.Use();
                    }
                    break;
                }
                case EventType.MouseDown:
                {
                    if (e.button != 0)
                    {
                        break;
                    }
                    int code = PadCodeAt(e.mousePosition);
                    if (code != 0)
                    {
                        padHeldCode = code;
                        J2ME.Host.keyPressed(code);
                        e.Use();
                    }
                    else if (lcdRect.Contains(e.mousePosition))
                    {
                        ToLcd(e.mousePosition, out x, out y);
                        pointerOnLcd = true;
                        J2ME.Host.pointerPressed(x, y);
                        e.Use();
                    }
                    break;
                }
                case EventType.MouseDrag:
                    if (pointerOnLcd)
                    {
                        ToLcd(e.mousePosition, out x, out y);
                        J2ME.Host.pointerDragged(x, y);
                        e.Use();
                    }
                    break;
                case EventType.MouseUp:
                    if (padHeldCode != 0)
                    {
                        J2ME.Host.keyReleased(padHeldCode);
                        padHeldCode = 0;
                        e.Use();
                    }
                    else if (pointerOnLcd)
                    {
                        ToLcd(e.mousePosition, out x, out y);
                        pointerOnLcd = false;
                        J2ME.Host.pointerReleased(x, y);
                        e.Use();
                    }
                    break;
            }
        }

        void ReleaseAllInput()
        {
            foreach (KeyValuePair<KeyCode, int> k in heldKeys)
            {
                J2ME.Host.keyReleased(k.Value);
            }
            heldKeys.Clear();
            if (padHeldCode != 0)
            {
                J2ME.Host.keyReleased(padHeldCode);
                padHeldCode = 0;
            }
            pointerOnLcd = false;
        }

        /// <summary>
        /// Keyboard -> phone key codes (Nokia style, as the game expects):
        /// arrows / WASD = D-pad, Enter / Space = OK (fire), Q / F1 = left soft key,
        /// E / F2 / Esc / Backspace (Android back) = right soft key, 0-9 = number keys, Z = *, X = #.
        /// </summary>
        public static int MapKey(KeyCode k)
        {
            switch (k)
            {
                case KeyCode.UpArrow:
                case KeyCode.W:
                    return J2ME.Canvas.KEYCODE_UP;
                case KeyCode.DownArrow:
                case KeyCode.S:
                    return J2ME.Canvas.KEYCODE_DOWN;
                case KeyCode.LeftArrow:
                case KeyCode.A:
                    return J2ME.Canvas.KEYCODE_LEFT;
                case KeyCode.RightArrow:
                case KeyCode.D:
                    return J2ME.Canvas.KEYCODE_RIGHT;
                case KeyCode.Return:
                case KeyCode.KeypadEnter:
                case KeyCode.Space:
                    return J2ME.Canvas.KEYCODE_FIRE;
                case KeyCode.Q:
                case KeyCode.F1:
                    return J2ME.Canvas.KEYCODE_SOFT_LEFT;
                case KeyCode.E:
                case KeyCode.F2:
                case KeyCode.Escape:
                case KeyCode.Backspace:
                    return J2ME.Canvas.KEYCODE_SOFT_RIGHT;
                case KeyCode.Z:
                case KeyCode.KeypadMultiply:
                case KeyCode.Asterisk:
                    return J2ME.Canvas.KEY_STAR;
                case KeyCode.X:
                case KeyCode.KeypadDivide:
                case KeyCode.Hash:
                    return J2ME.Canvas.KEY_POUND;
            }
            if (k >= KeyCode.Alpha0 && k <= KeyCode.Alpha9)
            {
                return J2ME.Canvas.KEY_NUM0 + (k - KeyCode.Alpha0);
            }
            if (k >= KeyCode.Keypad0 && k <= KeyCode.Keypad9)
            {
                return J2ME.Canvas.KEY_NUM0 + (k - KeyCode.Keypad0);
            }
            return 0;
        }

        // ---- MIDP high-level screens (TextBox / Form / Alert) --------------------------------

        void DrawHighLevelScreen(J2ME.Displayable d, Event e)
        {
            Rect panel = lcdRect;
            float u = panel.height / 16f;
            GUI.Box(panel, GUIContent.none, panelStyle);
            string title = d.getTitle();
            GUI.Label(new Rect(panel.x + u * 0.5f, panel.y + u * 0.3f, panel.width - u, u * 1.4f), title ?? "", titleStyle);

            float top = panel.y + u * 2f;
            float bottom = panel.yMax - u * 2.2f;
            J2ME.TextBox tb = d as J2ME.TextBox;
            J2ME.Form form = d as J2ME.Form;
            if (tb != null)
            {
                if (editing != tb)
                {
                    editing = tb;
                    textBuffer = tb.getString() ?? "";
                    focusField = true;
                }
                GUI.SetNextControlName("J2ME.TextBox");
                string typed = GUI.TextField(new Rect(panel.x + u * 0.5f, top, panel.width - u, u * 1.6f), textBuffer, tb.getMaxSize(), fieldStyle);
                if (typed != textBuffer)
                {
                    typed = FilterText(typed, tb.getConstraints());
                    try
                    {
                        tb.setString(typed);
                        textBuffer = typed;
                    }
                    catch (System.Exception ex)
                    {
                        Debug.LogWarning("[J2ME] TextBox rejected input: " + ex.Message);
                    }
                }
                if (focusField && e.type == EventType.Repaint)
                {
                    GUI.FocusControl("J2ME.TextBox");
                    focusField = false;
                }
            }
            else if (form != null)
            {
                Rect view = new Rect(panel.x + u * 0.5f, top, panel.width - u, bottom - top);
                float lineH = u * 0.9f;
                int n = form.size();
                Rect content = new Rect(0, 0, view.width - u * 0.6f, Mathf.Max(view.height, n * lineH));
                formScroll = GUI.BeginScrollView(view, formScroll, content);
                for (int i = 0; i < n; i++)
                {
                    GUI.Label(new Rect(0, i * lineH, content.width, lineH), form.getText(i), lineStyle);
                }
                GUI.EndScrollView();
            }

            // commands: one button each (they replace the phone's soft keys)
            int count = d.getCommandCount();
            if (count > 0)
            {
                float bw = (panel.width - u) / count;
                for (int i = 0; i < count; i++)
                {
                    J2ME.Command c = d.getCommand(i);
                    Rect r = new Rect(panel.x + u * 0.5f + i * bw + u * 0.1f, panel.yMax - u * 1.9f, bw - u * 0.2f, u * 1.5f);
                    if (GUI.Button(r, c.getLabel(), buttonStyle))
                    {
                        J2ME.Host.queueCommand(d, c);
                    }
                }
            }

            // Enter = confirm command, Esc / Android back = back command
            if (e.type == EventType.KeyDown && (e.keyCode == KeyCode.Return || e.keyCode == KeyCode.KeypadEnter || e.keyCode == KeyCode.Escape))
            {
                J2ME.Command c = FindCommand(d, e.keyCode == KeyCode.Escape);
                if (c != null)
                {
                    J2ME.Host.queueCommand(d, c);
                    e.Use();
                }
            }
        }

        static J2ME.Command FindCommand(J2ME.Displayable d, bool back)
        {
            J2ME.Command fallback = null;
            for (int i = 0; i < d.getCommandCount(); i++)
            {
                J2ME.Command c = d.getCommand(i);
                int t = c.getCommandType();
                bool isBack = t == J2ME.Command.BACK || t == J2ME.Command.CANCEL || t == J2ME.Command.EXIT || t == J2ME.Command.STOP;
                if (isBack == back)
                {
                    return c;
                }
                if (fallback == null)
                {
                    fallback = c;
                }
            }
            return back ? null : fallback;
        }

        /// <summary>Applies the TextField input constraint (NUMERIC, PHONENUMBER, DECIMAL).</summary>
        static string FilterText(string s, int constraints)
        {
            int kind = constraints & 0xFFFF;
            if (kind != 2 && kind != 3 && kind != 5)
            {
                return s;
            }
            System.Text.StringBuilder sb = new System.Text.StringBuilder(s.Length);
            foreach (char ch in s)
            {
                bool ok = ch >= '0' && ch <= '9';
                if (kind == 2 && ch == '-' && sb.Length == 0)
                {
                    ok = true;
                }
                if (kind == 3 && (ch == '+' || ch == '*' || ch == '#'))
                {
                    ok = true;
                }
                if (kind == 5 && (ch == '.' || (ch == '-' && sb.Length == 0)))
                {
                    ok = true;
                }
                if (ok)
                {
                    sb.Append(ch);
                }
            }
            return sb.ToString();
        }

        void EnsureStyles()
        {
            int key = Screen.width * 10000 + Screen.height;
            if (styledFor == key && padStyle != null)
            {
                return;
            }
            styledFor = key;
            int baseSize = Mathf.Max(12, Mathf.RoundToInt(Mathf.Min(Screen.width, Screen.height) / 26f));

            padStyle = new GUIStyle(GUI.skin.box);
            padStyle.fontSize = baseSize;
            padStyle.alignment = TextAnchor.MiddleCenter;
            padStyle.normal.textColor = new Color(0.9f, 0.9f, 0.9f, 1f);

            padDownStyle = new GUIStyle(padStyle);
            padDownStyle.normal.background = GUI.skin.button.active.background;
            padDownStyle.normal.textColor = Color.white;

            panelStyle = new GUIStyle(GUI.skin.box);

            titleStyle = new GUIStyle(GUI.skin.label);
            titleStyle.fontSize = baseSize;
            titleStyle.fontStyle = FontStyle.Bold;
            titleStyle.alignment = TextAnchor.MiddleLeft;
            titleStyle.normal.textColor = Color.white;

            fieldStyle = new GUIStyle(GUI.skin.textField);
            fieldStyle.fontSize = baseSize;
            fieldStyle.alignment = TextAnchor.MiddleLeft;

            buttonStyle = new GUIStyle(GUI.skin.button);
            buttonStyle.fontSize = baseSize;

            lineStyle = new GUIStyle(GUI.skin.label);
            lineStyle.fontSize = Mathf.Max(10, baseSize * 3 / 4);
            lineStyle.wordWrap = false;
            lineStyle.normal.textColor = Color.white;
        }
    }
}
