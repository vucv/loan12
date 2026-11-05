// Languages of the touch interface: Tiếng Việt (the game's own), English, 日本語, 한국어, then Bahasa
// Indonesia, ไทย, 繁體中文, Português (BR), Español, Deutsch, Русский.
//
// The interface keeps its Vietnamese texts in the code; TouchUi translates each text as it draws or
// measures it (Label, Lines, TextWidth, PixelText, PixelWidth), so no call site changes. The table,
// Resources/Loan12Lang/strings.txt, has one row per Vietnamese text: vi \t en \t ja \t ko; the later
// languages have a file each, strings_<code>.txt / story_<code>.txt, rows vi \t text.
//   - exact texts: "Cửa hàng" -> "Shop"
//   - templates with {0}, {1}...: "Hệ {0} · Cấp {1}" -> "{0} · Lv {1}"; what a {n} stands for is
//     translated on its own ("Hỏa" -> "Fire"), numbers stay as they are
//   - texts joined with " · " are translated part by part when the whole is not in the table
// Not translated (by choice, for now): the story (dialogues, the game's own descriptions). Names
// (heroes, places, enemies) found nowhere in the table lose their Vietnamese accents in the other
// languages ("Đinh Bộ Lĩnh" -> "Dinh Bo Linh"); longer texts (sentences) are left as they are.
// Japanese and Korean letters come from the phone's own fonts (Unity's font fallback): Inter and the
// game's bitmap font have none, so pixel titles in those languages are drawn with the system font.
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;
using UnityEngine;

namespace Loan12.UnityPort
{
    public enum Lang
    {
        Vi = 0,
        En = 1,
        Ja = 2,
        Ko = 3,
        Id = 4,
        Th = 5,
        Zh = 6,
        Pt = 7,
        Es = 8,
        De = 9,
        Ru = 10
    }

    public static class Loc
    {
        const string Pref = "l12.lang";
        public static readonly string[] Names =
            {"Tiếng Việt", "English", "日本語", "한국어", "Bahasa Indonesia", "ไทย", "繁體中文", "Português (BR)", "Español", "Deutsch", "Русский"};

        /// <summary>File codes (strings_&lt;code&gt;.txt, banter_&lt;code&gt;.txt).</summary>
        public static readonly string[] Codes = {"vi", "en", "ja", "ko", "id", "th", "zh", "pt", "es", "de", "ru"};

        const int Count = 11;

        static Lang? lang;
        static Dictionary<string, string[]> exact;
        static List<Template> templates;
        static readonly Dictionary<string, string> cache = new Dictionary<string, string>();

        sealed class Template
        {
            public Regex re;
            public bool dotted; // its Vietnamese text has " · " in it
            public string[] to;
        }

        public static Lang Current
        {
            get
            {
                if (lang == null)
                {
                    int saved = PlayerPrefs.GetInt(Pref, -1);
                    lang = saved >= 0 && saved < Count ? (Lang) saved : FromSystem();
                }
                return lang.Value;
            }
            set
            {
                lang = value;
                PlayerPrefs.SetInt(Pref, (int) value);
                PlayerPrefs.Save();
                cache.Clear();
                TouchUi.ClearTextCache();
            }
        }

        static Lang FromSystem()
        {
            switch (Application.systemLanguage)
            {
                case SystemLanguage.Vietnamese:
                    return Lang.Vi;
                case SystemLanguage.Japanese:
                    return Lang.Ja;
                case SystemLanguage.Korean:
                    return Lang.Ko;
                case SystemLanguage.Indonesian:
                    return Lang.Id;
                case SystemLanguage.Thai:
                    return Lang.Th;
                case SystemLanguage.ChineseTraditional:
                case SystemLanguage.ChineseSimplified:
                case SystemLanguage.Chinese:
                    return Lang.Zh;
                case SystemLanguage.Portuguese:
                    return Lang.Pt;
                case SystemLanguage.Spanish:
                    return Lang.Es;
                case SystemLanguage.German:
                    return Lang.De;
                case SystemLanguage.Russian:
                case SystemLanguage.Ukrainian:
                case SystemLanguage.Belarusian:
                    return Lang.Ru;
                case SystemLanguage.Unknown:
                    return Lang.Vi;
                default:
                    return Lang.En;
            }
        }

        /// <summary>A script the game's bitmap font has not (Japanese, Korean, Thai, Chinese, Russian).</summary>
        public static bool Cjk
        {
            get { return Current == Lang.Ja || Current == Lang.Ko || Current == Lang.Th || Current == Lang.Zh || Current == Lang.Ru; }
        }

        /// <summary>The text in the current language (the Vietnamese text when there is no translation).</summary>
        public static string T(string s)
        {
            if (string.IsNullOrEmpty(s) || Current == Lang.Vi)
            {
                return s;
            }
            string r;
            if (cache.TryGetValue(s, out r))
            {
                return r;
            }
            Load();
            r = Translate(s, 0);
            if (cache.Count > 4000)
            {
                cache.Clear();
            }
            cache[s] = r;
            return r;
        }

        static string Translate(string s, int depth)
        {
            string[] row;
            if (exact.TryGetValue(s, out row))
            {
                return Pick(row, s);
            }
            string trimmed = s.Trim();
            if (trimmed.Length == 0 || IsNumber(trimmed) || NeedsSystemFont(s))
            {
                return s; // nothing to translate, or already Japanese / Korean
            }
            if (trimmed != s && exact.TryGetValue(trimmed, out row))
            {
                return s.Replace(trimmed, Pick(row, trimmed));
            }
            // dialogues name the player's hero: "{p} tướng quân!" in the table
            string player = PlayerName();
            if (player.Length > 1 && s.Contains(player) && exact.TryGetValue(s.Replace(player, "{p}"), out row))
            {
                return Pick(row, s).Replace("{p}", NoAccents(player));
            }
            if (depth < 6)
            {
                // cut texts ("…" at the end)
                if (s.EndsWith("…") && s.Length > 1)
                {
                    return Translate(s.Substring(0, s.Length - 1), depth + 1) + "…";
                }
                // templates that span " · " first, then the parts, then the other templates
                string r = Apply(s, depth, true);
                if (r != null)
                {
                    return r;
                }
                if (s.Contains(" · "))
                {
                    return Join(s, " · ", depth);
                }
                r = Apply(s, depth, false);
                if (r != null)
                {
                    return r;
                }
                if (s.Contains(", "))
                {
                    return Join(s, ", ", depth);
                }
            }
            // a name: without accents; a sentence: as it is
            return LooksLikeName(trimmed) ? NoAccents(s) : s;
        }

        static string Apply(string s, int depth, bool dotted)
        {
            foreach (Template t in templates)
            {
                if (t.dotted != dotted)
                {
                    continue;
                }
                Match m = t.re.Match(s);
                if (!m.Success)
                {
                    continue;
                }
                string to = t.to[(int) Current];
                if (string.IsNullOrEmpty(to))
                {
                    to = t.to[1];
                }
                if (string.IsNullOrEmpty(to))
                {
                    return null;
                }
                for (int g = 1; g < m.Groups.Count; g++)
                {
                    to = to.Replace("{" + (g - 1) + "}", Translate(m.Groups[g].Value, depth + 1));
                }
                return to;
            }
            return null;
        }

        static string Join(string s, string sep, int depth)
        {
            string[] parts = s.Split(new[] {sep}, System.StringSplitOptions.None);
            for (int i = 0; i < parts.Length; i++)
            {
                parts[i] = Translate(parts[i], depth + 1);
            }
            return string.Join(sep, parts);
        }

        static string PlayerName()
        {
            try
            {
                return C_PortMap.heroName() ?? "";
            }
            catch (System.Exception)
            {
                return "";
            }
        }

        /// <summary>A text of the table by its key ("[intro]", "[ending]"); null in Vietnamese or when missing.</summary>
        public static string Block(string key)
        {
            if (Current == Lang.Vi)
            {
                return null;
            }
            Load();
            string[] row;
            return exact.TryGetValue(key, out row) ? Pick(row, null) : null;
        }

        static string Pick(string[] row, string s)
        {
            string to = row[(int) Current];
            return string.IsNullOrEmpty(to) ? (string.IsNullOrEmpty(row[1]) ? s : row[1]) : to;
        }

        static bool IsNumber(string s)
        {
            foreach (char c in s)
            {
                if (!(char.IsDigit(c) || c == '.' || c == ',' || c == '/' || c == '+' || c == '-' || c == '%' || c == ':' || c == 'x' || c == ' '))
                {
                    return false;
                }
            }
            return true;
        }

        static bool LooksLikeName(string s)
        {
            if (s.Length > 26)
            {
                return false;
            }
            foreach (char c in s)
            {
                if (c == '!' || c == '?' || c == '.' || c == ':' || c == ';')
                {
                    return false;
                }
            }
            return s.Split(' ').Length <= 5;
        }

        static int Literal(string template)
        {
            return Regex.Replace(template, @"\{\d\}", "").Length;
        }

        /// <summary>"Đinh Bộ Lĩnh" -> "Dinh Bo Linh".</summary>
        public static string NoAccents(string s)
        {
            string d = s.Replace('đ', 'd').Replace('Đ', 'D').Normalize(NormalizationForm.FormD);
            StringBuilder b = new StringBuilder(d.Length);
            foreach (char c in d)
            {
                if (CharUnicodeInfo.GetUnicodeCategory(c) != UnicodeCategory.NonSpacingMark)
                {
                    b.Append(c);
                }
            }
            return b.ToString().Normalize(NormalizationForm.FormC);
        }

        // the letters of the game's bitmap font (dh): Latin and Vietnamese only
        const string BitmapLetters = "abcdđefghijklmnopqrstuvwxyzáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵ"
                                     + "ABCDĐEFGHIJKLMNOPQRSTUVWXYZÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ";

        /// <summary>A letter the game's bitmap font cannot draw (Japanese, Korean, Thai, Cyrillic, ñ, ß, ü ...):
        /// the text is drawn with the system font instead.</summary>
        public static bool NeedsSystemFont(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return false;
            }
            foreach (char c in s)
            {
                if (c >= 0x2E80 || (char.IsLetter(c) && BitmapLetters.IndexOf(c) < 0))
                {
                    return true;
                }
            }
            return false;
        }

        static void Load()
        {
            if (exact != null)
            {
                return;
            }
            exact = new Dictionary<string, string[]>();
            templates = new List<Template>();
            // the interface (strings) and the story (dialogues, intro / ending, tutorial)
            foreach (string file in new[] {"Loan12Lang/strings", "Loan12Lang/story"})
            {
                TextAsset a = Resources.Load<TextAsset>(file);
                if (a != null)
                {
                    LoadTable(a.text);
                }
            }
            // the later languages: a file per table and language, rows vi \t text
            Dictionary<string, Template> byKey = new Dictionary<string, Template>();
            foreach (Template t in templates)
            {
                byKey[t.to[0]] = t;
            }
            for (int i = 4; i < Count; i++)
            {
                foreach (string table in new[] {"strings", "story"})
                {
                    TextAsset a = Resources.Load<TextAsset>("Loan12Lang/" + table + "_" + Codes[i]);
                    if (a != null)
                    {
                        LoadColumn(a.text, i, byKey);
                    }
                }
            }
            // longer templates first: "Hệ {0} · Cấp {1}" before "Hệ {0}"
            templates.Sort((x, y) => Literal(y.to[0]).CompareTo(Literal(x.to[0])));
        }

        static void LoadColumn(string text, int col, Dictionary<string, Template> byKey)
        {
            foreach (string raw in text.Split('\n'))
            {
                string line = raw.TrimEnd('\r');
                if (line.Length == 0 || line.StartsWith("#"))
                {
                    continue;
                }
                int tab = line.IndexOf('\t');
                if (tab <= 0)
                {
                    continue;
                }
                string key = line.Substring(0, tab).Replace("\\n", "\n"), val = line.Substring(tab + 1).Replace("\\n", "\n");
                string[] row;
                Template t;
                if (exact.TryGetValue(key, out row))
                {
                    row[col] = val;
                }
                else if (byKey.TryGetValue(key, out t))
                {
                    t.to[col] = val;
                }
                else
                {
                    row = new string[Count];
                    row[0] = key;
                    row[col] = val;
                    exact[key] = row;
                }
            }
        }

        static void LoadTable(string text)
        {
            foreach (string raw in text.Split('\n'))
            {
                string line = raw.TrimEnd('\r');
                if (line.Length == 0 || line.StartsWith("#"))
                {
                    continue;
                }
                string[] c = line.Split('\t');
                if (c.Length < 2)
                {
                    continue;
                }
                string[] row = new string[Count];
                for (int i = 0; i < Count; i++)
                {
                    row[i] = i < c.Length ? c[i].Replace("\\n", "\n") : "";
                }
                if (Regex.IsMatch(row[0], @"\{\d\}"))
                {
                    // the {n} of a template appear in order in the Vietnamese text
                    string pattern = "^" + Regex.Replace(Regex.Escape(row[0]), @"\\\{\d\}", "(.+?)") + "$";
                    templates.Add(new Template {re = new Regex(pattern, RegexOptions.Singleline), to = row, dotted = row[0].Contains(" · ")});
                }
                else
                {
                    exact[row[0]] = row;
                }
            }
        }
    }
}
