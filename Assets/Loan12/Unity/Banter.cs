// What the fighters say in a battle (speech bubbles): written for each language on its own, not
// translated (Resources/Loan12Lang/banter_<code>.txt, see Converter/i18n/SPEC.md). Each character
// speaks with a voice (personality) and each moment of the battle has its lines:
//   [voice.event]   one line per choice; {me} the speaker's name, {enemy} the opponent's, {skill} the skill.
// Lines come out of a shuffled deck per section, so the same one does not come back until the others
// have been said.
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class Banter
    {
        static Dictionary<string, List<string>> lines;
        static readonly Dictionary<string, List<int>> decks = new Dictionary<string, List<int>>();
        static readonly Dictionary<string, int> lastLine = new Dictionary<string, int>();
        static Lang loadedFor = (Lang) (-1);
        static readonly System.Random rnd = new System.Random();

        /// <summary>The voice of a character (its portrait / table index, see ea.a).</summary>
        public static string Voice(int who)
        {
            switch (who)
            {
                case 0:
                    return "hero_fire";
                case 1:
                    return "hero_storm";
                case 2:
                    return "hero_water";
                case 3:
                    return "hero_phoenix";
                case 4:
                    return "hero_queen";
                case 5:
                    return "hero_dragon";
                case 6:
                case 7:
                case 9:
                    return "ally";
                case 8:
                    return "mentor";
                case 16:
                    return "lady";
                case 21:
                case 23:
                case 25:
                case 31:
                case 35:
                case 37:
                case 39:
                case 41:
                case 75:
                case 76:
                    return "soldier";
                case 42:
                    return "bandit";
                case 43:
                case 44:
                case 45:
                case 61:
                case 62:
                case 63:
                case 64:
                case 66:
                case 68:
                case 69:
                case 73:
                    return "critter";
                case 46:
                case 48:
                case 50:
                case 51:
                case 52:
                case 55:
                case 56:
                case 67:
                case 71:
                case 72:
                    return "trickster";
                case 47:
                case 49:
                case 53:
                case 54:
                case 57:
                case 58:
                case 59:
                case 60:
                case 65:
                case 70:
                case 74:
                    return "brute";
                default:
                    return "warlord"; // the 12 Warlords and their generals
            }
        }

        /// <summary>A line for the character at this moment of the battle, or null when there is none.</summary>
        public static string Line(int who, string evt, string me, string enemy, string skill = null)
        {
            Load();
            string voice = Voice(who);
            string key = voice + "." + evt;
            List<string> list;
            if (!lines.TryGetValue(key, out list) || list.Count == 0)
            {
                // a voice without this moment: the heroes' common fallback
                if (evt == "revive" || !lines.TryGetValue("hero_fire." + evt, out list) || list.Count == 0)
                {
                    return null;
                }
                key = "hero_fire." + evt;
            }
            string s = list[Draw(key, list.Count)];
            s = s.Replace("{me}", me ?? "").Replace("{enemy}", enemy ?? "");
            if (s.Contains("{skill}"))
            {
                s = string.IsNullOrEmpty(skill) ? s.Replace("{skill}", "") : s.Replace("{skill}", skill);
            }
            return s;
        }

        /// <summary>The next index of a shuffled deck; a new shuffle never starts with the last one said.</summary>
        static int Draw(string key, int count)
        {
            List<int> deck;
            if (!decks.TryGetValue(key, out deck) || deck.Count == 0)
            {
                deck = new List<int>(count);
                for (int i = 0; i < count; i++)
                {
                    deck.Add(i);
                }
                for (int i = count - 1; i > 0; i--)
                {
                    int j = rnd.Next(i + 1);
                    int t = deck[i];
                    deck[i] = deck[j];
                    deck[j] = t;
                }
                int last;
                if (count > 1 && lastLine.TryGetValue(key, out last) && deck[0] == last)
                {
                    deck.RemoveAt(0);
                    deck.Add(last);
                }
                decks[key] = deck;
            }
            int n = deck[0];
            deck.RemoveAt(0);
            lastLine[key] = n;
            return n;
        }

        static void Load()
        {
            if (lines != null && loadedFor == Loc.Current)
            {
                return;
            }
            loadedFor = Loc.Current;
            lines = new Dictionary<string, List<string>>();
            decks.Clear();
            lastLine.Clear();
            // the language's own lines; English, then Vietnamese, while a language has none yet
            string[] tries = {Loc.Codes[(int) Loc.Current], "en", "vi"};
            foreach (string code in tries)
            {
                TextAsset a = Resources.Load<TextAsset>("Loan12Lang/banter_" + code);
                if (a != null)
                {
                    Parse(a.text);
                    return;
                }
            }
        }

        static void Parse(string text)
        {
            List<string> cur = null;
            foreach (string raw in text.Split('\n'))
            {
                string line = raw.Trim();
                if (line.Length == 0 || line.StartsWith("#"))
                {
                    continue;
                }
                if (line.StartsWith("[") && line.EndsWith("]"))
                {
                    string key = line.Substring(1, line.Length - 2);
                    if (!lines.TryGetValue(key, out cur))
                    {
                        cur = new List<string>();
                        lines[key] = cur;
                    }
                    continue;
                }
                if (cur != null)
                {
                    cur.Add(line);
                }
            }
        }
    }
}
