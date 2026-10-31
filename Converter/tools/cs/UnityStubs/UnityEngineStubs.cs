// Compile-check stubs: the subset of the UnityEngine / UnityEditor API used by the port, with the real
// signatures (Unity 2021.3 - 6). Only used to type-check the Unity glue outside of Unity.
using System;

namespace Unity.Collections
{
    public struct NativeArray<T> where T : struct
    {
        public int Length { get { return 0; } }
        public T this[int index] { get { return default(T); } set { } }
        public void CopyFrom(T[] array) { }
        public T[] ToArray() { return null; }
    }
}

namespace UnityEngine
{
    public class Object
    {
        public string name { get; set; }
        public static void Destroy(Object obj) { }
        public static void Destroy(Object obj, float t) { }
        public static void DestroyImmediate(Object obj) { }
        public static void DontDestroyOnLoad(Object target) { }
        public static implicit operator bool(Object exists) { return exists != null; }
        public static bool operator ==(Object x, Object y) { return ReferenceEquals(x, y); }
        public static bool operator !=(Object x, Object y) { return !ReferenceEquals(x, y); }
        public override bool Equals(object other) { return base.Equals(other); }
        public override int GetHashCode() { return base.GetHashCode(); }
    }

    public class Component : Object
    {
        public GameObject gameObject { get { return null; } }
        public Transform transform { get { return null; } }
        public string tag { get; set; }
        public T GetComponent<T>() { return default(T); }
    }

    public class Transform : Component { }

    public class Behaviour : Component
    {
        public bool enabled { get; set; }
    }

    public class MonoBehaviour : Behaviour { }

    public class ScriptableObject : Object { }

    public sealed class GameObject : Object
    {
        public GameObject() { }
        public GameObject(string name) { }
        public T AddComponent<T>() where T : Component { return null; }
        public T GetComponent<T>() { return default(T); }
        public string tag { get; set; }
        public Transform transform { get { return null; } }
    }

    public enum CameraClearFlags { Skybox = 1, Color = 2, SolidColor = 2, Depth = 3, Nothing = 4 }

    public sealed class Camera : Behaviour
    {
        public static Camera main { get { return null; } }
        public CameraClearFlags clearFlags { get; set; }
        public Color backgroundColor { get; set; }
        public int cullingMask { get; set; }
        public bool orthographic { get; set; }
    }

    public struct Color
    {
        public float r, g, b, a;
        public Color(float r, float g, float b, float a) { this.r = r; this.g = g; this.b = b; this.a = a; }
        public Color(float r, float g, float b) { this.r = r; this.g = g; this.b = b; a = 1f; }
        public static Color black { get { return new Color(0, 0, 0, 1); } }
        public static Color white { get { return new Color(1, 1, 1, 1); } }
    }

    public struct Vector2
    {
        public float x, y;
        public Vector2(float x, float y) { this.x = x; this.y = y; }
    }

    public struct Rect
    {
        public Rect(float x, float y, float width, float height) { this.x = x; this.y = y; this.width = width; this.height = height; }
        public float x { get; set; }
        public float y { get; set; }
        public float width { get; set; }
        public float height { get; set; }
        public float xMin { get { return x; } }
        public float yMin { get { return y; } }
        public float xMax { get { return x + width; } }
        public float yMax { get { return y + height; } }
        public Vector2 center { get { return new Vector2(x + width / 2, y + height / 2); } }
        public bool Contains(Vector2 point) { return false; }
    }

    public class RectOffset
    {
        public RectOffset() { }
        public RectOffset(int left, int right, int top, int bottom) { }
    }

    public static class Mathf
    {
        public static float Min(float a, float b) { return a; }
        public static int Min(int a, int b) { return a; }
        public static float Max(float a, float b) { return a; }
        public static int Max(int a, int b) { return a; }
        public static float Clamp(float value, float min, float max) { return value; }
        public static int Clamp(int value, int min, int max) { return value; }
        public static float Clamp01(float value) { return value; }
        public static int FloorToInt(float f) { return 0; }
        public static int RoundToInt(float f) { return 0; }
        public static float Floor(float f) { return f; }
        public static float Round(float f) { return f; }
        public static bool Approximately(float a, float b) { return false; }
    }

    public static class Debug
    {
        public static void Log(object message) { }
        public static void LogWarning(object message) { }
        public static void LogError(object message) { }
    }

    public enum RuntimePlatform { OSXEditor = 0, WindowsEditor = 7, Android = 11, IPhonePlayer = 8 }

    public static class Application
    {
        public static int targetFrameRate { get; set; }
        public static bool isMobilePlatform { get { return false; } }
        public static bool runInBackground { get; set; }
        public static RuntimePlatform platform { get { return RuntimePlatform.WindowsEditor; } }
        public static void Quit() { }
        public static void OpenURL(string url) { }
    }

    public enum ScreenOrientation { Portrait = 1, PortraitUpsideDown = 2, LandscapeLeft = 3, LandscapeRight = 4, AutoRotation = 5 }

    public enum FullScreenMode { ExclusiveFullScreen = 0, FullScreenWindow = 1, MaximizedWindow = 2, Windowed = 3 }

    public sealed class Screen
    {
        public static int width { get { return 0; } }
        public static int height { get { return 0; } }
        public static float dpi { get { return 0; } }
        public static Rect safeArea { get { return default(Rect); } }
        public static ScreenOrientation orientation { get; set; }
    }

    public static class Resources
    {
        public static T Load<T>(string path) where T : Object { return null; }
        public static void UnloadAsset(Object assetToUnload) { }
    }

    public class TextAsset : Object
    {
        public byte[] bytes { get { return null; } }
        public string text { get { return null; } }
    }

    public sealed class AudioClip : Object
    {
        public float length { get { return 0; } }
    }

    public enum AudioClipLoadType { DecompressOnLoad = 0, CompressedInMemory = 1, Streaming = 2 }

    public sealed class AudioSource : Behaviour
    {
        public AudioClip clip { get; set; }
        public bool loop { get; set; }
        public float volume { get; set; }
        public float pitch { get; set; }
        public float time { get; set; }
        public bool isPlaying { get { return false; } }
        public bool playOnAwake { get; set; }
        public void Play() { }
        public void Stop() { }
        public void Pause() { }
        public void UnPause() { }
    }

    public enum FilterMode { Point = 0, Bilinear = 1, Trilinear = 2 }

    public enum TextureWrapMode { Repeat = 0, Clamp = 1, Mirror = 2, MirrorOnce = 3 }

    public enum TextureFormat { Alpha8 = 1, RGB24 = 3, RGBA32 = 4, ARGB32 = 5, BGRA32 = 14 }

    public class Texture : Object
    {
        public FilterMode filterMode { get; set; }
        public TextureWrapMode wrapMode { get; set; }
        public virtual int width { get { return 0; } set { } }
        public virtual int height { get { return 0; } set { } }
    }

    public sealed class Texture2D : Texture
    {
        public Texture2D(int width, int height, TextureFormat textureFormat, bool mipChain) { }
        public Unity.Collections.NativeArray<T> GetRawTextureData<T>() where T : struct { return default(Unity.Collections.NativeArray<T>); }
        public void Apply(bool updateMipmaps, bool makeNoLongerReadable) { }
        public void Apply() { }
    }

    public enum TextAnchor { UpperLeft = 0, UpperCenter = 1, UpperRight = 2, MiddleLeft = 3, MiddleCenter = 4, MiddleRight = 5, LowerLeft = 6, LowerCenter = 7, LowerRight = 8 }

    public enum FontStyle { Normal = 0, Bold = 1, Italic = 2, BoldAndItalic = 3 }

    public sealed class GUIStyleState
    {
        public Texture2D background { get; set; }
        public Color textColor { get; set; }
    }

    public sealed class GUIStyle
    {
        public GUIStyle() { }
        public GUIStyle(GUIStyle other) { }
        public int fontSize { get; set; }
        public FontStyle fontStyle { get; set; }
        public TextAnchor alignment { get; set; }
        public bool wordWrap { get; set; }
        public GUIStyleState normal { get { return null; } set { } }
        public GUIStyleState active { get { return null; } set { } }
        public GUIStyleState hover { get { return null; } set { } }
        public RectOffset padding { get { return null; } set { } }
    }

    public sealed class GUISkin : ScriptableObject
    {
        public GUIStyle box { get; set; }
        public GUIStyle button { get; set; }
        public GUIStyle label { get; set; }
        public GUIStyle textField { get; set; }
    }

    public class GUIContent
    {
        public static GUIContent none = new GUIContent("");
        public GUIContent() { }
        public GUIContent(string text) { }
    }

    public class GUI
    {
        public static GUISkin skin { get; set; }
        public static Color color { get; set; }
        public static void DrawTextureWithTexCoords(Rect position, Texture image, Rect texCoords) { }
        public static void DrawTextureWithTexCoords(Rect position, Texture image, Rect texCoords, bool alphaBlend) { }
        public static void Box(Rect position, string text) { }
        public static void Box(Rect position, string text, GUIStyle style) { }
        public static void Box(Rect position, GUIContent content, GUIStyle style) { }
        public static void Label(Rect position, string text) { }
        public static void Label(Rect position, string text, GUIStyle style) { }
        public static bool Button(Rect position, string text) { return false; }
        public static bool Button(Rect position, string text, GUIStyle style) { return false; }
        public static string TextField(Rect position, string text) { return text; }
        public static string TextField(Rect position, string text, int maxLength) { return text; }
        public static string TextField(Rect position, string text, int maxLength, GUIStyle style) { return text; }
        public static void SetNextControlName(string name) { }
        public static void FocusControl(string name) { }
        public static Vector2 BeginScrollView(Rect position, Vector2 scrollPosition, Rect viewRect) { return scrollPosition; }
        public static void EndScrollView() { }
    }

    public enum EventType { MouseDown = 0, MouseUp = 1, MouseMove = 2, MouseDrag = 3, KeyDown = 4, KeyUp = 5, ScrollWheel = 6, Repaint = 7, Layout = 8, Used = 12 }

    public enum KeyCode
    {
        None = 0, Backspace = 8, Tab = 9, Return = 13, Escape = 27, Space = 32, Hash = 35, Asterisk = 42,
        Alpha0 = 48, Alpha1 = 49, Alpha2 = 50, Alpha3 = 51, Alpha4 = 52, Alpha5 = 53, Alpha6 = 54, Alpha7 = 55, Alpha8 = 56, Alpha9 = 57,
        A = 97, D = 100, E = 101, Q = 113, S = 115, W = 119, X = 120, Z = 122,
        Keypad0 = 256, Keypad1 = 257, Keypad2 = 258, Keypad3 = 259, Keypad4 = 260, Keypad5 = 261, Keypad6 = 262, Keypad7 = 263,
        Keypad8 = 264, Keypad9 = 265, KeypadPeriod = 266, KeypadDivide = 267, KeypadMultiply = 268, KeypadMinus = 269,
        KeypadPlus = 270, KeypadEnter = 271, UpArrow = 273, DownArrow = 274, RightArrow = 275, LeftArrow = 276, F1 = 282, F2 = 283
    }

    public sealed class Event
    {
        public static Event current { get; set; }
        public EventType type { get; set; }
        public KeyCode keyCode { get; set; }
        public Vector2 mousePosition { get; set; }
        public int button { get; set; }
        public char character { get; set; }
        public void Use() { }
    }

    public static class PlayerPrefs
    {
        public static bool HasKey(string key) { return false; }
        public static string GetString(string key) { return ""; }
        public static string GetString(string key, string defaultValue) { return defaultValue; }
        public static void SetString(string key, string value) { }
        public static void DeleteKey(string key) { }
        public static void Save() { }
    }

    public sealed class Handheld
    {
        public static void Vibrate() { }
    }

    public enum RuntimeInitializeLoadType { AfterSceneLoad = 0, BeforeSceneLoad = 1, AfterAssembliesLoaded = 2, BeforeSplashScreen = 3, SubsystemRegistration = 4 }

    [AttributeUsage(AttributeTargets.Method, AllowMultiple = false)]
    public class RuntimeInitializeOnLoadMethodAttribute : Attribute
    {
        public RuntimeInitializeOnLoadMethodAttribute() { }
        public RuntimeInitializeOnLoadMethodAttribute(RuntimeInitializeLoadType loadType) { }
    }

    [AttributeUsage(AttributeTargets.Class, Inherited = false)]
    public sealed class DisallowMultipleComponent : Attribute { }

    public sealed class AddComponentMenu : Attribute
    {
        public AddComponentMenu(string menuName) { }
    }

    [AttributeUsage(AttributeTargets.Field, Inherited = true, AllowMultiple = false)]
    public class TooltipAttribute : Attribute
    {
        public TooltipAttribute(string tooltip) { }
    }

    [AttributeUsage(AttributeTargets.Field, Inherited = true, AllowMultiple = false)]
    public sealed class RangeAttribute : Attribute
    {
        public RangeAttribute(float min, float max) { }
    }
}

namespace UnityEngine.SceneManagement
{
    public struct Scene
    {
        public string path { get { return null; } }
        public string name { get { return null; } }
        public bool isDirty { get { return false; } }
        public bool IsValid() { return false; }
    }

    public static class SceneManager
    {
        public static Scene GetActiveScene() { return default(Scene); }
        public static void MoveGameObjectToScene(GameObject go, Scene scene) { }
    }
}

namespace UnityEditor
{
    using UnityEngine;

    [AttributeUsage(AttributeTargets.Class)]
    public class InitializeOnLoadAttribute : Attribute { }

    [AttributeUsage(AttributeTargets.Method, AllowMultiple = true)]
    public sealed class MenuItem : Attribute
    {
        public MenuItem(string itemName) { }
        public MenuItem(string itemName, bool isValidateFunction) { }
        public MenuItem(string itemName, bool isValidateFunction, int priority) { }
    }

    public enum PlayModeStateChange { EnteredEditMode, ExitingEditMode, EnteredPlayMode, ExitingPlayMode }

    public sealed class EditorApplication
    {
        public delegate void CallbackFunction();
        public static CallbackFunction delayCall;
        public static bool isPlaying { get; set; }
        public static bool isPlayingOrWillChangePlaymode { get { return false; } }
        public static event Action<PlayModeStateChange> playModeStateChanged { add { } remove { } }
    }

    public class EditorUtility
    {
        public static bool DisplayDialog(string title, string message, string ok, string cancel) { return false; }
    }

    public class AssetDatabase
    {
        public static void Refresh() { }
    }

    public class EditorBuildSettingsScene
    {
        public EditorBuildSettingsScene(string path, bool enabled) { }
        public string path { get; set; }
        public bool enabled { get; set; }
    }

    public class EditorBuildSettings : Object
    {
        public static EditorBuildSettingsScene[] scenes { get; set; }
    }

    public enum UIOrientation { Portrait = 0, PortraitUpsideDown = 1, LandscapeRight = 2, LandscapeLeft = 3, AutoRotation = 4 }

    public sealed class PlayerSettings : Object
    {
        public static string productName { get; set; }
        public static string companyName { get; set; }
        public static UIOrientation defaultInterfaceOrientation { get; set; }
        public static bool runInBackground { get; set; }
        public static int defaultScreenWidth { get; set; }
        public static int defaultScreenHeight { get; set; }
        public static bool resizableWindow { get; set; }
        public static FullScreenMode fullScreenMode { get; set; }
    }

    public class AssetImporter : Object { }

    public struct AudioImporterSampleSettings
    {
        public AudioClipLoadType loadType;
    }

    public sealed class AudioImporter : AssetImporter
    {
        public AudioImporterSampleSettings defaultSampleSettings { get; set; }
    }

    public class AssetPostprocessor
    {
        public string assetPath { get; set; }
        public AssetImporter assetImporter { get { return null; } }
    }
}

namespace UnityEditor.SceneManagement
{
    using UnityEngine.SceneManagement;

    public enum NewSceneSetup { EmptyScene = 0, DefaultGameObjects = 1 }

    public enum NewSceneMode { Single = 0, Additive = 1 }

    public enum OpenSceneMode { Single = 0, Additive = 1, AdditiveWithoutLoading = 2 }

    public sealed class EditorSceneManager : SceneManager2
    {
        public static Scene NewScene(NewSceneSetup setup, NewSceneMode mode) { return default(Scene); }
        public static Scene OpenScene(string scenePath, OpenSceneMode mode = OpenSceneMode.Single) { return default(Scene); }
        public static bool SaveScene(Scene scene, string dstScenePath = "", bool saveAsCopy = false) { return true; }
        public static bool CloseScene(Scene scene, bool removeScene) { return true; }
        public static bool SaveCurrentModifiedScenesIfUserWantsTo() { return true; }
    }

    public class SceneManager2 { }
}
