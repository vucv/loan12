// Platform services for the MIDP emulation, implemented with Unity: JAR resources (TextAssets),
// audio (AudioSources playing the converted OGG clips), RecordStore persistence (PlayerPrefs),
// clock, URLs, vibration and logging.
using System;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class UnityBackend : J2ME.Backend
    {
        /// <summary>Resources folder holding the JAR files: "/m/af.mg" is Resources/J2ME/m/af_mg.bytes.</summary>
        public const string ResourceRoot = "J2ME/";

        /// <summary>Converted audio: "/audio/menu.mid" plays Resources/J2ME/clips/menu.ogg.</summary>
        public const string ClipRoot = "J2ME/clips/";

        const string StorePrefix = "J2ME.rms.";
        const string StoreIndexKey = "J2ME.rms.__index";

        static readonly Dictionary<string, string> AppProperties = new Dictionary<string, string>
        {
            {"MIDlet-Name", "Loan 12 Su Quan"},
            {"MIDlet-Vendor", "Zing4u"},
            {"MIDlet-Version", "1.0.0"},
            {"MicroEdition-Configuration", "CLDC-1.0"},
            {"MicroEdition-Profile", "MIDP-2.0"},
        };

        readonly GameObject audioHost;
        readonly List<AudioHandle> handles = new List<AudioHandle>();

        /// <summary>Master volume (0..1) applied on top of the game's own volume control.</summary>
        public float masterVolume = 1f;

        /// <summary>Set when the MIDlet called notifyDestroyed().</summary>
        public bool exitWasRequested;

        public UnityBackend(GameObject audioHost)
        {
            this.audioHost = audioHost;
        }

        // ---- resources -----------------------------------------------------------------------

        public static string ResourceKey(string jarPath)
        {
            return ResourceRoot + jarPath.TrimStart('/').Replace('.', '_');
        }

        public sbyte[] getResource(string path)
        {
            TextAsset asset = Resources.Load<TextAsset>(ResourceKey(path));
            if (asset == null)
            {
                return null;
            }
            sbyte[] data = J2ME.JBytes.ToSBytes(asset.bytes);
            Resources.UnloadAsset(asset);
            return data;
        }

        // ---- clock / properties -------------------------------------------------------------

        public long currentTimeMillis()
        {
            return DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        }

        public string getProperty(string key)
        {
            switch (key)
            {
                case "microedition.platform":
                    return "J2ME-Unity";
                case "microedition.locale":
                    return "vi-VN";
                case "microedition.encoding":
                    return "UTF-8";
                case "microedition.configuration":
                    return "CLDC-1.0";
                case "microedition.profiles":
                    return "MIDP-2.0";
                default:
                    return null;
            }
        }

        public string getAppProperty(string key)
        {
            string v;
            return key != null && AppProperties.TryGetValue(key, out v) ? v : null;
        }

        // ---- record stores (PlayerPrefs, base64) ---------------------------------------------

        public sbyte[] loadStore(string name)
        {
            string key = StorePrefix + name;
            if (!PlayerPrefs.HasKey(key))
            {
                return null;
            }
            try
            {
                return J2ME.JBytes.ToSBytes(Convert.FromBase64String(PlayerPrefs.GetString(key)));
            }
            catch (FormatException)
            {
                Debug.LogWarning("[J2ME] record store '" + name + "' is corrupt; ignoring it");
                return null;
            }
        }

        public void saveStore(string name, sbyte[] data)
        {
            PlayerPrefs.SetString(StorePrefix + name, Convert.ToBase64String(J2ME.JBytes.ToBytes(data)));
            List<string> names = new List<string>(listStores());
            if (!names.Contains(name))
            {
                names.Add(name);
                PlayerPrefs.SetString(StoreIndexKey, string.Join("\n", names.ToArray()));
            }
            PlayerPrefs.Save();
        }

        public void deleteStore(string name)
        {
            PlayerPrefs.DeleteKey(StorePrefix + name);
            List<string> names = new List<string>(listStores());
            if (names.Remove(name))
            {
                PlayerPrefs.SetString(StoreIndexKey, string.Join("\n", names.ToArray()));
            }
            PlayerPrefs.Save();
        }

        public string[] listStores()
        {
            string index = PlayerPrefs.GetString(StoreIndexKey, "");
            return index.Length == 0 ? new string[0] : index.Split('\n');
        }

        /// <summary>Deletes every record store (= a fresh install of the game).</summary>
        public static void DeleteAllSavedStores()
        {
            string index = PlayerPrefs.GetString(StoreIndexKey, "");
            if (index.Length > 0)
            {
                foreach (string name in index.Split('\n'))
                {
                    PlayerPrefs.DeleteKey(StorePrefix + name);
                }
            }
            PlayerPrefs.DeleteKey(StoreIndexKey);
            PlayerPrefs.Save();
        }

        // ---- audio ---------------------------------------------------------------------------

        sealed class AudioHandle
        {
            public string name;
            public AudioSource source;
            public int loopsLeft;     // plays still to start after the current one (finite loop counts)
            public bool active;       // started and not stopped
            public float volume = 1f; // game volume (0..1)
        }

        public object audioOpen(string resourcePath, string contentType)
        {
            string name = Path.GetFileNameWithoutExtension(resourcePath);
            AudioClip clip = Resources.Load<AudioClip>(ClipRoot + name);
            if (clip == null)
            {
                Debug.LogWarning("[J2ME] no converted audio clip for " + resourcePath + " (expected Resources/" + ClipRoot + name + ")");
                return null;
            }
            AudioHandle h = new AudioHandle();
            h.name = name;
            h.source = audioHost.AddComponent<AudioSource>();
            h.source.playOnAwake = false;
            h.source.clip = clip;
            h.source.volume = masterVolume;
            handles.Add(h);
            return h;
        }

        public void audioStart(object handle, int loopCount)
        {
            AudioHandle h = (AudioHandle) handle;
            if (h.source == null)
            {
                return;
            }
            h.source.loop = loopCount < 0;
            h.loopsLeft = loopCount < 0 ? 0 : Math.Max(0, loopCount - 1);
            h.active = true;
            h.source.Play();
        }

        public void audioStop(object handle)
        {
            AudioHandle h = (AudioHandle) handle;
            h.active = false;
            if (h.source != null)
            {
                h.source.Stop();
            }
        }

        public void audioClose(object handle)
        {
            AudioHandle h = (AudioHandle) handle;
            h.active = false;
            if (h.source != null)
            {
                h.source.Stop();
                UnityEngine.Object.Destroy(h.source);
                h.source = null;
            }
            handles.Remove(h);
        }

        public void audioVolume(object handle, int level, bool muted)
        {
            AudioHandle h = (AudioHandle) handle;
            h.volume = muted ? 0f : Mathf.Clamp01(level / 100f);
            if (h.source != null)
            {
                h.source.volume = h.volume * masterVolume;
            }
        }

        public void audioRate(object handle, int ratePermille)
        {
            AudioHandle h = (AudioHandle) handle;
            if (h.source != null)
            {
                h.source.pitch = Mathf.Clamp(ratePermille / 1000f, 0.25f, 4f);
            }
        }

        public bool audioIsPlaying(object handle)
        {
            AudioHandle h = (AudioHandle) handle;
            if (!h.active || h.source == null)
            {
                return false;
            }
            if (h.source.isPlaying)
            {
                return true;
            }
            if (h.loopsLeft > 0)
            {
                h.loopsLeft--;
                h.source.Play();
                return true;
            }
            h.active = false;
            return false;
        }

        public long audioDuration(object handle)
        {
            AudioHandle h = (AudioHandle) handle;
            return h.source == null || h.source.clip == null ? -1 : (long) (h.source.clip.length * 1000000.0);
        }

        public long audioPosition(object handle)
        {
            AudioHandle h = (AudioHandle) handle;
            return h.source == null ? 0 : (long) (h.source.time * 1000000.0);
        }

        public void audioSetPosition(object handle, long micros)
        {
            AudioHandle h = (AudioHandle) handle;
            if (h.source != null && h.source.clip != null)
            {
                h.source.time = Mathf.Clamp((float) (micros / 1000000.0), 0f, Mathf.Max(0f, h.source.clip.length - 0.01f));
            }
        }

        /// <summary>Called every frame by the host: restarts finite loops whose current play ended.</summary>
        public void UpdateAudio()
        {
            for (int i = 0; i < handles.Count; i++)
            {
                AudioHandle h = handles[i];
                if (h.active && h.source != null && !h.source.isPlaying && h.loopsLeft > 0)
                {
                    h.loopsLeft--;
                    h.source.Play();
                }
            }
        }

        public void SetMasterVolume(float v)
        {
            masterVolume = Mathf.Clamp01(v);
            foreach (AudioHandle h in handles)
            {
                if (h.source != null)
                {
                    h.source.volume = h.volume * masterVolume;
                }
            }
        }

        public void StopAllAudio()
        {
            foreach (AudioHandle h in handles)
            {
                if (h.source != null)
                {
                    h.source.Stop();
                }
                h.active = false;
            }
        }

        // ---- platform ------------------------------------------------------------------------

        public bool platformRequest(string url)
        {
            if (!string.IsNullOrEmpty(url))
            {
                Application.OpenURL(url);
            }
            return false;
        }

        public void exitRequested()
        {
            exitWasRequested = true;
        }

        public void vibrate(int millis)
        {
#if UNITY_ANDROID || UNITY_IOS
            if (millis > 0)
            {
                Handheld.Vibrate();
            }
#endif
        }

        public void log(string message)
        {
            Debug.Log("[J2ME] " + message);
        }
    }
}
