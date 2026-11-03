// Languages of the touch interface: Tiếng Việt (the game's own), English, 日本語, 한국어.
//
// The interface keeps its Vietnamese texts in the code; TouchUi translates each text as it draws or
// measures it (Label, Lines, TextWidth, PixelText, PixelWidth), so no call site changes. The table,
// Resources/Loan12Lang/strings.txt, has one row per Vietnamese text: vi \t en \t ja \t ko.
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
        Ko = 3
    }

    public static class Loc
    {
        const string Pref = "l12.lang";
        public static readonly string[] Names = {"Tiếng Việt", "English", "日本語", "한국어"};

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
                    lang = saved >= 0 && saved <= 3 ? (Lang) saved : FromSystem();
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
                case SystemLanguage.Unknown:
                    return Lang.Vi;
                default:
                    return Lang.En;
            }
        }

        /// <summary>Japanese or Korean: no glyphs in Inter or the game's font.</summary>
        public static bool Cjk
        {
            get { return Current == Lang.Ja || Current == Lang.Ko; }
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
            if (trimmed.Length == 0 || IsNumber(trimmed))
            {
                return s;
            }
            if (trimmed != s && exact.TryGetValue(trimmed, out row))
            {
                return s.Replace(trimmed, Pick(row, trimmed));
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

        /// <summary>A letter the game's bitmap font cannot draw (Japanese, Korean...).</summary>
        public static bool NeedsSystemFont(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return false;
            }
            foreach (char c in s)
            {
                if (c >= 0x2E80)
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
            TextAsset a = Resources.Load<TextAsset>("Loan12Lang/strings");
            if (a == null)
            {
                return;
            }
            foreach (string raw in a.text.Split('\n'))
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
                string[] row = new string[4];
                for (int i = 0; i < 4; i++)
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
            // longer templates first: "Hệ {0} · Cấp {1}" before "Hệ {0}"
            templates.Sort((x, y) => Literal(y.to[0]).CompareTo(Literal(x.to[0])));
        }
    }
}
