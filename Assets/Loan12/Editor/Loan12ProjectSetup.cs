// Editor helpers for the converted project:
//  * creates Assets/Loan12/Scenes/Loan12.unity (camera + J2ME host) and puts it in the build settings,
//  * sets portrait orientation / product name once,
//  * imports the long music clips as streaming audio,
//  * menu "Loan12" with the same actions plus "Delete saved game".
using System.IO;
using UnityEditor;
using UnityEditor.SceneManagement;
using UnityEngine;
using UnityEngine.SceneManagement;

namespace Loan12.UnityPort.EditorTools
{
    [InitializeOnLoad]
    public static class Loan12ProjectSetup
    {
        public const string ScenePath = "Assets/Loan12/Scenes/Loan12.unity";
        const string MarkerPath = "ProjectSettings/Loan12PortSetup.txt";

        static Loan12ProjectSetup()
        {
            EditorApplication.delayCall += AutoSetup;
        }

        static void AutoSetup()
        {
            if (EditorApplication.isPlayingOrWillChangePlaymode)
            {
                return;
            }
            if (!File.Exists(ScenePath))
            {
                CreateScene(false);
            }
            EnsureSceneInBuildSettings();
            if (!File.Exists(MarkerPath))
            {
                ApplyPlayerSettings();
                File.WriteAllText(MarkerPath, "Loan12 port: player settings applied once. Delete this file to apply them again.\n");
            }
            Scene active = SceneManager.GetActiveScene();
            if (string.IsNullOrEmpty(active.path) && !active.isDirty && File.Exists(ScenePath))
            {
                EditorSceneManager.OpenScene(ScenePath);
            }
        }

        [MenuItem("Loan12/Open Game Scene", false, 1)]
        static void OpenScene()
        {
            if (!File.Exists(ScenePath))
            {
                CreateScene(false);
            }
            if (EditorSceneManager.SaveCurrentModifiedScenesIfUserWantsTo())
            {
                EditorSceneManager.OpenScene(ScenePath);
            }
        }

        [MenuItem("Loan12/Recreate Game Scene", false, 2)]
        static void RecreateScene()
        {
            CreateScene(true);
        }

        static void CreateScene(bool overwrite)
        {
            if (File.Exists(ScenePath) && !overwrite)
            {
                return;
            }
            Directory.CreateDirectory(Path.GetDirectoryName(ScenePath));
            // a fresh, untouched "Untitled" scene is simply replaced; otherwise the new scene is created
            // next to the open one (Unity refuses that while an unsaved untitled scene has changes)
            Scene active = SceneManager.GetActiveScene();
            bool replace = string.IsNullOrEmpty(active.path) && !active.isDirty;
            Scene scene;
            try
            {
                scene = EditorSceneManager.NewScene(NewSceneSetup.EmptyScene, replace ? NewSceneMode.Single : NewSceneMode.Additive);
            }
            catch (System.InvalidOperationException ex)
            {
                Debug.LogWarning("Loan12: could not create " + ScenePath + " now (" + ex.Message + "). Save your scene and use " +
                                 "the menu Loan12 > Recreate Game Scene. Pressing Play works anyway: the game host creates itself.");
                return;
            }

            GameObject camGo = new GameObject("Main Camera");
            camGo.tag = "MainCamera";
            Camera cam = camGo.AddComponent<Camera>();
            cam.clearFlags = CameraClearFlags.SolidColor;
            cam.backgroundColor = Color.black;
            cam.cullingMask = 0;
            cam.orthographic = true;
            camGo.AddComponent<AudioListener>();
            SceneManager.MoveGameObjectToScene(camGo, scene);

            GameObject host = new GameObject("Loan12 J2ME Host");
            host.AddComponent<J2MEGameHost>();
            SceneManager.MoveGameObjectToScene(host, scene);

            EditorSceneManager.SaveScene(scene, ScenePath);
            if (!replace)
            {
                EditorSceneManager.CloseScene(scene, true);
            }
            AssetDatabase.Refresh();
            EnsureSceneInBuildSettings();
            Debug.Log("Loan12: created " + ScenePath);
        }

        static void EnsureSceneInBuildSettings()
        {
            if (!File.Exists(ScenePath))
            {
                return;
            }
            EditorBuildSettingsScene[] scenes = EditorBuildSettings.scenes;
            foreach (EditorBuildSettingsScene s in scenes)
            {
                if (s.path == ScenePath)
                {
                    return;
                }
            }
            EditorBuildSettingsScene[] list = new EditorBuildSettingsScene[scenes.Length + 1];
            list[0] = new EditorBuildSettingsScene(ScenePath, true);
            System.Array.Copy(scenes, 0, list, 1, scenes.Length);
            EditorBuildSettings.scenes = list;
        }

        [MenuItem("Loan12/Apply Player Settings (portrait, name)", false, 20)]
        static void ApplyPlayerSettings()
        {
            PlayerSettings.productName = "Loạn 12 Sứ Quân";
            PlayerSettings.defaultInterfaceOrientation = UIOrientation.Portrait;
            PlayerSettings.runInBackground = false;
            PlayerSettings.defaultScreenWidth = 480;
            PlayerSettings.defaultScreenHeight = 720;
            PlayerSettings.resizableWindow = true;
            PlayerSettings.fullScreenMode = FullScreenMode.Windowed;
            Debug.Log("Loan12: player settings applied (portrait, 480x720 window, product name).");
        }

        [MenuItem("Loan12/Delete Saved Game (PlayerPrefs)", false, 40)]
        static void DeleteSaves()
        {
            if (EditorUtility.DisplayDialog("Loan12", "Delete the saved game (all J2ME record stores in PlayerPrefs)?", "Delete", "Cancel"))
            {
                UnityBackend.DeleteAllSavedStores();
                Debug.Log("Loan12: saved game deleted.");
            }
        }
    }

    /// <summary>The four music tracks are long: stream them instead of decompressing them into memory.</summary>
    public sealed class Loan12AudioImport : AssetPostprocessor
    {
        static readonly string[] Music = {"intro", "menu", "m", "victory"};

        void OnPreprocessAudio()
        {
            if (!assetPath.Replace('\\', '/').Contains("/Resources/J2ME/clips/"))
            {
                return;
            }
            string name = Path.GetFileNameWithoutExtension(assetPath);
            if (System.Array.IndexOf(Music, name) < 0)
            {
                return;
            }
            AudioImporter importer = (AudioImporter) assetImporter;
            AudioImporterSampleSettings s = importer.defaultSampleSettings;
            s.loadType = AudioClipLoadType.Streaming;
            importer.defaultSampleSettings = s;
        }
    }
}
