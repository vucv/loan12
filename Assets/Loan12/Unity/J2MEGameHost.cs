// Runs the converted MIDlet inside Unity.
//
// Every frame: input -> J2ME.Host.tick() (game logic, timers, painting into the 240x320 LCD buffer) ->
// upload the LCD to a texture -> draw it (IMGUI). The touch interface replaces three screens with Unity
// views drawn around the game's own picture: getting ready (TouchPrepareView), the battle
// (TouchBattleView) and the result (TouchResultView). An optional on-screen phone keypad remains for
// other devices.
// MIDP high-level screens (TextBox for the hero name / phone number, the debug log Form) are shown
// with IMGUI controls and, on phones, the system keyboard. Input uses IMGUI events, so it works with
// both the old Input Manager and the new Input System; touches arrive as pointer events (the game
// handles taps and swipes itself).
using System.Collections;
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
        const string KeypadHiddenPref = "Loan12.KeypadHidden";

        /// <summary>Set to false (before the first scene loads) to stop the automatic host creation.</summary>
        public static bool AutoCreate = true;

        public static J2MEGameHost Instance { get; private set; }

        [Tooltip("On-screen phone keypad (soft keys, D-pad, OK, * and #). Not needed with the touch interface. Auto = touch devices only.")]
        public KeypadMode onScreenKeypad = KeypadMode.Never; // renamed from "keypad" (default was Auto) so old scenes get the new default

        [Tooltip("Touch interface (plan/thiet-ke-giao-dien.md): Unity draws the battle screen around the game's board.")]
        public bool touchInterface = true;

        [Tooltip("Scale the 240x320 screen by whole multiples only (sharper pixels, larger borders).")]
        public bool integerScaling;

        [Tooltip("Point = crisp pixels like the phone, Bilinear = smoother.")]
        public FilterMode filtering = FilterMode.Point;

        [Range(0f, 1f)] public float volume = 1f;

        [Tooltip("Short vibration when an on-screen key is pressed (Android).")]
        public bool haptics = true;

        static bool startedBefore; // survives only when "Reload Domain" is disabled in the editor

        UnityBackend backend;
        Texture2D lcd;
        bool running;
        bool quitting;

        // input state
        readonly Dictionary<KeyCode, int> heldKeys = new Dictionary<KeyCode, int>();
        bool pointerOnLcd;
        int padHeldCode;
        bool keypadHidden;

        // touch interface (option B of the redesign plan)
        // first active wins: a result panel lies over the finished battle, the VS panel over the map
        readonly ITouchView[] touchViews = {new TouchResultView(), new TouchPrepareView(), new TouchBattleView()};
        ITouchView touchShown;

        // layout (GUI coordinates, origin top-left)
        Rect lcdRect;
        Rect toggleRect;
        readonly List<PadButton> pad = new List<PadButton>();
        bool padVisible;
        bool padAllowed;

        // styles (rebuilt when the screen size changes)
        int styledFor = -1;
        GUIStyle keyStyle, keyDownStyle, softStyle, softDownStyle, softOffStyle, toggleStyle;
        GUIStyle panelStyle, titleStyle, fieldStyle, buttonStyle, lineStyle;
        readonly List<Texture2D> generated = new List<Texture2D>();

        // high-level screens
        J2ME.TextBox editing;
        string textBuffer = "";
        bool focusField;
        Vector2 formScroll;
        TouchScreenKeyboard systemKeyboard;
        bool openSystemKeyboard;

        enum PadKind
        {
            Key,
            SoftLeft,
            SoftRight
        }

        struct PadButton
        {
            public Rect rect;
            public string label;
            public int code;
            public PadKind kind;

            public PadButton(Rect rect, string label, int code, PadKind kind)
            {
                this.rect = rect;
                this.label = label;
                this.code = code;
                this.kind = kind;
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

        /// <summary>Hides / shows the on-screen keypad (remembered on this device).</summary>
        public bool KeypadHidden
        {
            get { return keypadHidden; }
            set
            {
                keypadHidden = value;
                PlayerPrefs.SetInt(KeypadHiddenPref, value ? 1 : 0);
                PlayerPrefs.Save();
            }
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
            if (Application.isMobilePlatform)
            {
                Screen.sleepTimeout = SleepTimeout.NeverSleep; // a turn-based game: no dimming while thinking
                Screen.orientation = ScreenOrientation.Portrait;
            }
            keypadHidden = PlayerPrefs.GetInt(KeypadHiddenPref, 0) == 1;
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

            string shots = CommandLineValue("-loan12-shots");
            if (shots != null)
            {
                StartCoroutine(ScreenshotTour(shots));
            }
        }

        static string CommandLineValue(string name)
        {
            string[] args = System.Environment.GetCommandLineArgs();
            for (int i = 0; i < args.Length - 1; i++)
            {
                if (args[i] == name)
                {
                    return args[i + 1];
                }
            }
            return null;
        }

        /// <summary>
        /// Developer aid ("-loan12-shots <folder>" on the command line): plays the first screens with key
        /// presses, saves screenshots of the touch layout (keypad forced on) and quits. Lets the phone UI be
        /// checked from a desktop build. Never active otherwise.
        /// </summary>
        IEnumerator ScreenshotTour(string folder)
        {
            System.IO.Directory.CreateDirectory(folder);
            onScreenKeypad = KeypadMode.Always;
            Application.runInBackground = true;
            yield return new WaitForSeconds(7.5f);
            ScreenCapture.CaptureScreenshot(System.IO.Path.Combine(folder, "01_sound.png"));
            yield return TourKey(J2ME.Canvas.KEYCODE_FIRE);
            yield return new WaitForSeconds(5f);
            ScreenCapture.CaptureScreenshot(System.IO.Path.Combine(folder, "02_menu.png"));
            yield return TourKey(J2ME.Canvas.KEYCODE_FIRE);
            yield return new WaitForSeconds(3f);
            yield return TourKey(J2ME.Canvas.KEYCODE_SOFT_RIGHT);
            yield return new WaitForSeconds(3f);
            ScreenCapture.CaptureScreenshot(System.IO.Path.Combine(folder, "03_heroes.png"));
            KeypadHidden = true;
            yield return new WaitForSeconds(0.5f);
            ScreenCapture.CaptureScreenshot(System.IO.Path.Combine(folder, "04_keypad_hidden.png"));
            KeypadHidden = false;
            yield return new WaitForSeconds(1f);
            Application.Quit();
        }

        IEnumerator TourKey(int code)
        {
            padHeldCode = code;
            J2ME.Host.keyPressed(code);
            yield return new WaitForSeconds(0.15f);
            J2ME.Host.keyReleased(code);
            padHeldCode = 0;
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
            UpdateSystemKeyboard();
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
            foreach (Texture2D t in generated)
            {
                if (t != null)
                {
                    Destroy(t);
                }
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

            // touch interface: Unity draws these screens around the game's own picture
            ITouchView view = null;
            if (running && !highLevel && touchInterface)
            {
                foreach (ITouchView v in touchViews)
                {
                    if (v.Active())
                    {
                        view = v;
                        break;
                    }
                }
            }
            if (view != touchShown)
            {
                // hand-over: nothing stays pressed in the view that is going away
                if (touchShown != null)
                {
                    touchShown.Reset();
                }
                ReleaseAllInput();
                if (view != null)
                {
                    view.Reset();
                }
                touchShown = view;
            }
            if (view != null)
            {
                if (e.type == EventType.KeyDown || e.type == EventType.KeyUp)
                {
                    HandleCanvasInput(e); // keyboard / Android back still reach the game
                }
                view.OnGUI(e, lcd, haptics);
                return;
            }

            if (running && !highLevel)
            {
                HandleCanvasInput(e);
            }

            if (e.type == EventType.Repaint)
            {
                GUI.DrawTextureWithTexCoords(lcdRect, lcd, new Rect(0f, 1f, 1f, -1f), false);
                if (!highLevel)
                {
                    DrawKeypad();
                }
            }

            if (highLevel)
            {
                DrawHighLevelScreen(shown, e);
            }
            else if (editing != null)
            {
                editing = null;
                CloseSystemKeyboard();
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

            padAllowed = onScreenKeypad == KeypadMode.Always || (onScreenKeypad == KeypadMode.Auto && Application.isMobilePlatform);
            padVisible = padAllowed && !keypadHidden;
            pad.Clear();
            toggleRect = new Rect(0, 0, 0, 0);
            bool portrait = area.height >= area.width;
            float unit = Mathf.Min(area.width, area.height);
            float toggleH = padAllowed ? Mathf.Max(28f, unit * 0.075f) : 0f;

            Rect lcdArea = area;
            if (padVisible && portrait)
            {
                float padH = Mathf.Min(area.height * 0.38f, area.width * 0.78f) + toggleH;
                lcdArea = new Rect(area.x, area.y, area.width, area.height - padH);
            }
            else if (padVisible)
            {
                float side = Mathf.Min(area.width * 0.25f, area.height * 0.8f);
                lcdArea = new Rect(area.x + side, area.y, area.width - 2 * side, area.height);
            }
            else if (padAllowed)
            {
                lcdArea = new Rect(area.x, area.y, area.width, area.height - toggleH);
            }

            float scale = Mathf.Min(lcdArea.width / LcdWidth, lcdArea.height / LcdHeight);
            if (integerScaling && scale >= 1f)
            {
                scale = Mathf.Floor(scale);
            }
            float w = LcdWidth * scale;
            float h = LcdHeight * scale;
            // portrait: keep the screen at the top so the thumbs reach the keys below it
            float top = padVisible && portrait ? lcdArea.y + Mathf.Min((lcdArea.height - h) / 2f, unit * 0.04f) : lcdArea.y + (lcdArea.height - h) / 2f;
            lcdRect = new Rect(Mathf.Round(lcdArea.x + (lcdArea.width - w) / 2f), Mathf.Round(top), w, h);

            if (!padAllowed)
            {
                return;
            }
            float tw = Mathf.Max(toggleH * 4.5f, 120f);
            if (!padVisible)
            {
                toggleRect = new Rect(area.xMax - tw - toggleH * 0.3f, area.yMax - toggleH, tw, toggleH * 0.9f);
                return;
            }
            if (portrait)
            {
                float padTop = lcdRect.yMax + unit * 0.02f;
                Rect padArea = new Rect(area.x + unit * 0.03f, padTop, area.width - unit * 0.06f, area.yMax - toggleH - padTop);
                float cw = padArea.width / 3f;
                float ch = padArea.height / 3f;
                AddPad(padArea, cw, ch, 0, 0, PadKind.SoftLeft, null, J2ME.Canvas.KEYCODE_SOFT_LEFT);
                AddPad(padArea, cw, ch, 1, 0, PadKind.Key, "▲", J2ME.Canvas.KEYCODE_UP);
                AddPad(padArea, cw, ch, 2, 0, PadKind.SoftRight, null, J2ME.Canvas.KEYCODE_SOFT_RIGHT);
                AddPad(padArea, cw, ch, 0, 1, PadKind.Key, "◄", J2ME.Canvas.KEYCODE_LEFT);
                AddPad(padArea, cw, ch, 1, 1, PadKind.Key, "OK", J2ME.Canvas.KEYCODE_FIRE);
                AddPad(padArea, cw, ch, 2, 1, PadKind.Key, "►", J2ME.Canvas.KEYCODE_RIGHT);
                AddPad(padArea, cw, ch, 0, 2, PadKind.Key, "*", J2ME.Canvas.KEY_STAR);
                AddPad(padArea, cw, ch, 1, 2, PadKind.Key, "▼", J2ME.Canvas.KEYCODE_DOWN);
                AddPad(padArea, cw, ch, 2, 2, PadKind.Key, "#", J2ME.Canvas.KEY_POUND);
                toggleRect = new Rect(area.x + (area.width - tw) / 2f, area.yMax - toggleH, tw, toggleH * 0.9f);
            }
            else
            {
                Rect left = new Rect(area.x, area.y, lcdArea.x - area.x, area.height - toggleH);
                Rect right = new Rect(lcdArea.xMax, area.y, area.xMax - lcdArea.xMax, area.height - toggleH);
                float cw = left.width / 3f;
                float ch = Mathf.Min(left.height / 3.5f, cw);
                Rect l = new Rect(left.x, left.y + (left.height - ch * 3) / 2f, left.width, ch * 3);
                AddPad(l, cw, ch, 1, 0, PadKind.Key, "▲", J2ME.Canvas.KEYCODE_UP);
                AddPad(l, cw, ch, 0, 1, PadKind.Key, "◄", J2ME.Canvas.KEYCODE_LEFT);
                AddPad(l, cw, ch, 1, 1, PadKind.Key, "OK", J2ME.Canvas.KEYCODE_FIRE);
                AddPad(l, cw, ch, 2, 1, PadKind.Key, "►", J2ME.Canvas.KEYCODE_RIGHT);
                AddPad(l, cw, ch, 1, 2, PadKind.Key, "▼", J2ME.Canvas.KEYCODE_DOWN);
                Rect r = new Rect(right.x, l.y, right.width, ch * 3);
                float rw = right.width / 2f;
                AddPad(r, rw, ch, 0, 0, PadKind.SoftLeft, null, J2ME.Canvas.KEYCODE_SOFT_LEFT);
                AddPad(r, rw, ch, 1, 0, PadKind.SoftRight, null, J2ME.Canvas.KEYCODE_SOFT_RIGHT);
                AddPad(r, rw, ch, 0, 2, PadKind.Key, "*", J2ME.Canvas.KEY_STAR);
                AddPad(r, rw, ch, 1, 2, PadKind.Key, "#", J2ME.Canvas.KEY_POUND);
                toggleRect = new Rect(area.xMax - tw - toggleH * 0.3f, area.yMax - toggleH, tw, toggleH * 0.9f);
            }
        }

        void AddPad(Rect area, float cw, float ch, int col, int row, PadKind kind, string label, int code)
        {
            float m = Mathf.Min(cw, ch) * 0.07f;
            pad.Add(new PadButton(new Rect(area.x + col * cw + m, area.y + row * ch + m, cw - 2 * m, ch - 2 * m), label, code, kind));
        }

        void DrawKeypad()
        {
            if (padVisible)
            {
                foreach (PadButton b in pad)
                {
                    bool down = b.code == padHeldCode;
                    if (b.kind == PadKind.Key)
                    {
                        GUI.Box(b.rect, b.label, down ? keyDownStyle : keyStyle);
                        continue;
                    }
                    string label = SoftLabel(b.kind == PadKind.SoftLeft ? 0 : 2);
                    GUI.Box(b.rect, label ?? "", label == null ? softOffStyle : (down ? softDownStyle : softStyle));
                }
            }
            if (padAllowed && toggleRect.width > 0)
            {
                GUI.Box(toggleRect, keypadHidden ? "▲ Hiện bàn phím" : "▼ Ẩn bàn phím", toggleStyle);
            }
        }

        /// <summary>The game's current soft-key label (null when the slot is unused).</summary>
        static string SoftLabel(int slot)
        {
            string[] labels = J2ME.Host.softKeyLabels;
            if (labels == null || slot >= labels.Length)
            {
                return null;
            }
            string s = labels[slot];
            if (s == null)
            {
                return null;
            }
            s = s.Trim();
            return s.Length == 0 ? null : s;
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
                    if (padAllowed && toggleRect.Contains(e.mousePosition))
                    {
                        KeypadHidden = !keypadHidden;
                        Haptics.Tap(haptics);
                        e.Use();
                        break;
                    }
                    int code = PadCodeAt(e.mousePosition);
                    if (code != 0)
                    {
                        padHeldCode = code;
                        J2ME.Host.keyPressed(code);
                        Haptics.Tap(haptics);
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
            if (pointerOnLcd)
            {
                pointerOnLcd = false;
            }
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

        bool UseSystemKeyboard
        {
            get { return Application.isMobilePlatform && TouchScreenKeyboard.isSupported; }
        }

        /// <summary>Phones: the TextBox is edited with the system keyboard (opened when the TextBox appears).</summary>
        void UpdateSystemKeyboard()
        {
            J2ME.TextBox tb = editing;
            if (tb == null || !UseSystemKeyboard)
            {
                return;
            }
            if (openSystemKeyboard)
            {
                openSystemKeyboard = false;
                int kind = tb.getConstraints() & 0xFFFF;
                TouchScreenKeyboardType type = kind == 3 ? TouchScreenKeyboardType.PhonePad
                    : (kind == 2 || kind == 5) ? TouchScreenKeyboardType.NumberPad : TouchScreenKeyboardType.Default;
                systemKeyboard = TouchScreenKeyboard.Open(textBuffer, type, false, false, false, false, tb.getTitle() ?? "", tb.getMaxSize());
            }
            if (systemKeyboard == null)
            {
                return;
            }
            string typed = FilterText(systemKeyboard.text ?? "", tb.getConstraints());
            if (typed.Length > tb.getMaxSize())
            {
                typed = typed.Substring(0, tb.getMaxSize());
            }
            if (typed != textBuffer)
            {
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
            if (systemKeyboard.status == TouchScreenKeyboard.Status.Done)
            {
                systemKeyboard = null;
                J2ME.Command ok = FindCommand(tb, false);
                if (ok != null)
                {
                    J2ME.Host.queueCommand(tb, ok);
                }
            }
            else if (systemKeyboard.status != TouchScreenKeyboard.Status.Visible)
            {
                systemKeyboard = null; // cancelled / lost focus: the panel's buttons stay available
            }
        }

        void CloseSystemKeyboard()
        {
            if (systemKeyboard != null)
            {
                systemKeyboard.active = false;
                systemKeyboard = null;
            }
            openSystemKeyboard = false;
        }

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
                    CloseSystemKeyboard();
                    editing = tb;
                    textBuffer = tb.getString() ?? "";
                    focusField = true;
                    openSystemKeyboard = UseSystemKeyboard;
                }
                Rect field = new Rect(panel.x + u * 0.5f, top, panel.width - u, u * 1.6f);
                if (UseSystemKeyboard)
                {
                    // the system keyboard edits the text; tapping the field opens it again
                    if (GUI.Button(field, textBuffer.Length > 0 ? textBuffer : "…", fieldStyle))
                    {
                        openSystemKeyboard = true;
                    }
                }
                else
                {
                    GUI.SetNextControlName("J2ME.TextBox");
                    string typed = GUI.TextField(field, textBuffer, tb.getMaxSize(), fieldStyle);
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
                        CloseSystemKeyboard();
                        J2ME.Host.queueCommand(d, c);
                        Haptics.Tap(haptics);
                    }
                }
            }

            // Enter = confirm command, Esc / Android back = back command
            if (e.type == EventType.KeyDown && (e.keyCode == KeyCode.Return || e.keyCode == KeyCode.KeypadEnter || e.keyCode == KeyCode.Escape))
            {
                J2ME.Command c = FindCommand(d, e.keyCode == KeyCode.Escape);
                if (c != null)
                {
                    CloseSystemKeyboard();
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

        // ---- styles --------------------------------------------------------------------------

        void EnsureStyles()
        {
            int key = Screen.width * 10000 + Screen.height;
            if (styledFor == key && keyStyle != null)
            {
                return;
            }
            styledFor = key;
            int baseSize = Mathf.Max(12, Mathf.RoundToInt(Mathf.Min(Screen.width, Screen.height) / 26f));
            int border = Mathf.Max(8, Mathf.RoundToInt(Mathf.Min(Screen.width, Screen.height) / 60f));

            if (generated.Count == 0)
            {
                generated.Add(Rounded(new Color32(46, 40, 52, 235), new Color32(120, 104, 84, 255)));   // 0 key
                generated.Add(Rounded(new Color32(96, 82, 64, 245), new Color32(232, 196, 120, 255)));  // 1 key down
                generated.Add(Rounded(new Color32(104, 34, 30, 240), new Color32(214, 160, 82, 255)));  // 2 soft key
                generated.Add(Rounded(new Color32(168, 66, 48, 250), new Color32(255, 214, 140, 255))); // 3 soft key down
                generated.Add(Rounded(new Color32(40, 36, 44, 150), new Color32(80, 72, 64, 160)));     // 4 unused soft key
                generated.Add(Rounded(new Color32(30, 28, 34, 200), new Color32(90, 80, 70, 200)));     // 5 toggle
            }

            keyStyle = KeyStyle(generated[0], baseSize * 3 / 2, new Color(0.95f, 0.92f, 0.86f), border);
            keyDownStyle = KeyStyle(generated[1], baseSize * 3 / 2, Color.white, border);
            softStyle = KeyStyle(generated[2], baseSize, new Color(1f, 0.92f, 0.75f), border);
            softDownStyle = KeyStyle(generated[3], baseSize, Color.white, border);
            softOffStyle = KeyStyle(generated[4], baseSize, new Color(0.6f, 0.58f, 0.55f), border);
            toggleStyle = KeyStyle(generated[5], Mathf.Max(10, baseSize * 2 / 3), new Color(0.8f, 0.78f, 0.74f), border);

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

        static GUIStyle KeyStyle(Texture2D background, int fontSize, Color text, int border)
        {
            GUIStyle s = new GUIStyle();
            s.normal.background = background;
            s.normal.textColor = text;
            s.border = new RectOffset(border, border, border, border);
            s.alignment = TextAnchor.MiddleCenter;
            s.fontSize = fontSize;
            s.fontStyle = FontStyle.Bold;
            s.wordWrap = true;
            return s;
        }

        /// <summary>A small rounded-rectangle texture (anti-aliased, 2 px outline) used as a 9-slice button.</summary>
        static Texture2D Rounded(Color32 fill, Color32 outline)
        {
            const int size = 48;
            const float radius = 14f;
            Texture2D t = new Texture2D(size, size, TextureFormat.RGBA32, false);
            t.filterMode = FilterMode.Bilinear;
            t.wrapMode = TextureWrapMode.Clamp;
            Color32[] px = new Color32[size * size];
            for (int y = 0; y < size; y++)
            {
                for (int x = 0; x < size; x++)
                {
                    float cx = Mathf.Max(radius - (x + 0.5f), (x + 0.5f) - (size - radius), 0f);
                    float cy = Mathf.Max(radius - (y + 0.5f), (y + 0.5f) - (size - radius), 0f);
                    float dist = radius - Mathf.Sqrt(cx * cx + cy * cy); // distance inside the edge
                    float alpha = Mathf.Clamp01(dist + 0.5f);
                    Color32 c = dist < 2.5f ? outline : fill;
                    px[y * size + x] = new Color32(c.r, c.g, c.b, (byte) (c.a * alpha));
                }
            }
            t.SetPixels32(px);
            t.Apply(false, true);
            return t;
        }
    }

    /// <summary>Short haptic tick for on-screen keys (Android; respects the system haptics setting).</summary>
    static class Haptics
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        static AndroidJavaObject view;
        static bool unavailable;

        public static void Tap(bool enabled)
        {
            if (!enabled || unavailable)
            {
                return;
            }
            try
            {
                if (view == null)
                {
                    using (AndroidJavaClass player = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
                    {
                        AndroidJavaObject activity = player.GetStatic<AndroidJavaObject>("currentActivity");
                        AndroidJavaObject window = activity.Call<AndroidJavaObject>("getWindow");
                        view = window.Call<AndroidJavaObject>("getDecorView");
                    }
                }
                view.Call<bool>("performHapticFeedback", 1); // HapticFeedbackConstants.VIRTUAL_KEY
            }
            catch (System.Exception)
            {
                unavailable = true;
            }
        }
#else
        public static void Tap(bool enabled)
        {
        }
#endif
    }
}
