// Player builds: menu "Loan12/Build Android APK" or, from the command line,
//   Unity -batchmode -quit -projectPath <project> -buildTarget Android \
//         -executeMethod Loan12.UnityPort.EditorTools.Loan12Build.BuildAndroid [-apk <output.apk>]
using System;
using System.IO;
using UnityEditor;
using UnityEditor.Build;
using UnityEditor.Build.Reporting;
using UnityEngine;

namespace Loan12.UnityPort.EditorTools
{
    public static class Loan12Build
    {
        public const string AndroidPackage = "com.vucv.loan12";
        public const string DefaultApkPath = "Builds/Android/Loan12.apk";

        [MenuItem("Loan12/Build Android APK", false, 60)]
        public static void BuildAndroidMenu()
        {
            BuildAndroid();
        }

        public static void BuildAndroid()
        {
            string apk = ArgValue("-apk") ?? DefaultApkPath;
            if (!File.Exists(Loan12ProjectSetup.ScenePath))
            {
                Loan12ProjectSetup.CreateScene(false);
            }
            Loan12ProjectSetup.EnsureSceneInBuildSettings();

            PlayerSettings.productName = "Loạn 12 Sứ Quân";
            PlayerSettings.defaultInterfaceOrientation = UIOrientation.Portrait;
            PlayerSettings.SetApplicationIdentifier(NamedBuildTarget.Android, AndroidPackage);
            // IL2CPP + ARM64: current phones are 64-bit only; ARMv7 keeps older phones working
            PlayerSettings.SetScriptingBackend(NamedBuildTarget.Android, ScriptingImplementation.IL2CPP);
            PlayerSettings.Android.targetArchitectures = AndroidArchitecture.ARM64 | AndroidArchitecture.ARMv7;
            EditorUserBuildSettings.buildAppBundle = false;

            Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(apk)));
            BuildPlayerOptions options = new BuildPlayerOptions();
            options.scenes = new[] {Loan12ProjectSetup.ScenePath};
            options.locationPathName = apk;
            options.target = BuildTarget.Android;
            options.targetGroup = BuildTargetGroup.Android;
            options.options = BuildOptions.None;

            BuildReport report = BuildPipeline.BuildPlayer(options);
            BuildSummary summary = report.summary;
            bool ok = summary.result == BuildResult.Succeeded;
            Debug.Log("Loan12: Android build " + summary.result + " -> " + Path.GetFullPath(apk) +
                      " (" + summary.totalSize / (1024 * 1024) + " MB, " + summary.totalErrors + " errors, " +
                      (int) summary.totalTime.TotalSeconds + " s)");
            if (Application.isBatchMode)
            {
                EditorApplication.Exit(ok ? 0 : 1);
            }
            else if (ok)
            {
                EditorUtility.RevealInFinder(apk);
            }
        }

        /// <summary>macOS player (Builds/Mac/Loan12.app), e.g. to run "-loan12-shots &lt;folder&gt;" screenshot tours.</summary>
        public static void BuildMac()
        {
            if (!File.Exists(Loan12ProjectSetup.ScenePath))
            {
                Loan12ProjectSetup.CreateScene(false);
            }
            BuildPlayerOptions options = new BuildPlayerOptions();
            options.scenes = new[] {Loan12ProjectSetup.ScenePath};
            options.locationPathName = ArgValue("-app") ?? "Builds/Mac/Loan12.app";
            options.target = BuildTarget.StandaloneOSX;
            options.targetGroup = BuildTargetGroup.Standalone;
            options.options = BuildOptions.None;
            BuildReport report = BuildPipeline.BuildPlayer(options);
            Debug.Log("Loan12: macOS build " + report.summary.result);
            if (Application.isBatchMode)
            {
                EditorApplication.Exit(report.summary.result == BuildResult.Succeeded ? 0 : 1);
            }
        }

        static string ArgValue(string name)
        {
            string[] args = Environment.GetCommandLineArgs();
            for (int i = 0; i < args.Length - 1; i++)
            {
                if (args[i] == name)
                {
                    return args[i + 1];
                }
            }
            return null;
        }
    }
}
