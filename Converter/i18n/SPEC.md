# Loạn 12 Sứ Quân — localization work for one language

The game is a Vietnamese historical match-3 RPG (the 12 Warlords period, 966–968 AD) for phones.
Its Unity port shows every text through `Assets/Loan12/Unity/Loc.cs`. Today it has 4 languages
(vi, en, ja, ko) in two tables with 4 tab-separated columns:

- `Assets/Loan12/Resources/Loan12Lang/strings.txt` — the interface (~610 rows)
- `Assets/Loan12/Resources/Loan12Lang/story.txt` — dialogues, intro / ending scroll, tutorial (~390 rows)

Column 1 (vi) is the KEY: it must be copied byte for byte. Column 2 (en) is a faithful translation
you can use to understand the Vietnamese. Lines starting with `#` are comments.

Your job has up to two parts; the task message says which ones and for which language code.

## Part A — translate the two tables (new languages only)

Write two files, UTF-8, LF line ends, one row per source row, same order, keeping `#` comment lines:

- `Assets/Loan12/Resources/Loan12Lang/strings_<code>.txt`
- `Assets/Loan12/Resources/Loan12Lang/story_<code>.txt`

Each row: `<vi key copied exactly>` TAB `<your translation>`. Nothing else on the line, no extra tabs.
Every non-comment source row must appear exactly once (a checker compares the keys).

Rules:
- Keep placeholders exactly: `{0}`, `{1}`, `{2}`, `{p}` (player name), `{s}` (hero name), and the
  literal `\n` escapes (two characters: backslash + n) — keep them where the line breaks belong.
- Keep separators the game relies on: ` · ` (space, middle dot, space) stays ` · `.
- Keep it short where the English is short: these are buttons, tabs, labels on a phone screen. A
  button label must not be longer than about 1.3× the English. ALL-CAPS Vietnamese labels (titles,
  buttons like "XUẤT TRẬN") are titles: write them in capitals too where the script has capitals.
- Natural, fluent, game-like wording for players of that language — not word-for-word. Match the tone:
  epic for the story, friendly and clear for the interface.
- Names of people and places: in Latin-script languages write them without Vietnamese accents as the
  English column does (Dinh Bo Linh, Hoa Lu). In zh-TW use the traditional Hán-Việt characters when
  you know them with certainty (丁部領 Đinh Bộ Lĩnh, 華閭 Hoa Lư, 吳權 Ngô Quyền, 十二使君 12 Sứ Quân,
  黎桓 Lê Hoàn, 阮匐 Nguyễn Bặc, 劉基 Lưu Cơ, 陳覽 Trần Lãm…), otherwise the unaccented Latin form.
  In th / ru transliterate into the script (Динь Бо Линь, ดิ่ญ โบ่ หลิญ) or keep Latin — be
  consistent across all rows.
- Item / skill / hero names: translate them like the English column does (Long Thần Kiếm = Dragon
  Sword …), consistently everywhere.
- The `[intro]` and `[ending]` rows: their key is the literal `[intro]` / `[ending]`; translate the
  English column (it is the whole scroll text, paragraphs separated by `\n\n`).
- Money: Vietnamese uses `1.000` (dot thousands); keep numbers as they are in the key.

## Part B — battle banter (every language, written natively, NOT translated)

During a battle each fighter says short lines in a speech bubble. Write
`Assets/Loan12/Resources/Loan12Lang/banter_<code>.txt` for your language from scratch, in the voice
of that language's players: funny, cheeky, trending, teasing the opponent, sometimes self-mocking,
very human. Lines may (and should) differ between languages — use jokes, memes, slang, internet
humor and rhythm that land for players of THAT language (Vietnamese Gen Z slang for vi, Brazilian
humor for pt, etc.). Keep it family-friendly (no slurs, no sexual content, no real politics, no
real-world brands). The setting is 10th-century, but anachronistic modern humor is welcome — that
is the charm. Avoid lines that only make sense in another language.

Format (UTF-8):

```
# banter <code>
[hero_fire.start]
line
line
[hero_fire.taunt]
...
```

A section header is `[<voice>.<event>]`; every following non-empty line is one choice. Each line
at most ~60 characters (CJK/Thai: ~24 characters) — it is a small bubble. Placeholders you may use:
`{enemy}` the opponent's name, `{me}` the speaker's own name, `{skill}` the skill's name (only in
`skill`). Use them sometimes, not in every line.

### Voices (who speaks)

Player heroes (the player picks one of 6):
- `hero_fire`   — young hot-headed hero of Fire, cocky braggart, loud, competitive, gamer energy
- `hero_storm`  — disciple of the Thunder God, cool, dry sarcasm, a bit of a nerd, deadpan
- `hero_water`  — blood of the Water God, chill, laid-back, gentle teasing, always hungry
- `hero_phoenix`— disciple of the Fire Phoenix immortal, dramatic, theatrical, loves an audience
- `hero_queen`  — a heroine of Lightning, confident, sassy, witty comebacks, "queen" vibes
- `hero_dragon` — descendant of the North Sea Dragon King, noble, polite but cutting, a little vain

Others (opponents, or allies sparring in the arena):
- `ally`    — loyal generals of Đinh Bộ Lĩnh sparring with you: honorable, warm, competitive banter
- `mentor`  — Lưu Cơ the strategist/teacher: dry wit, lectures, proud of his student
- `warlord` — the 12 Warlords and their generals: arrogant, pompous, sore losers, grand speeches
- `lady`    — Dương Vân Nga, noble lady: elegant, sharp-tongued, unimpressed
- `soldier` — common soldiers: complain about pay, food, the boss; scared but trying; relatable
- `bandit`  — mountain bandit: street slang, shakedowns, cowardly when losing
- `critter` — bugs, bees, toads, spiders, worms: buzzing/croaking sounds, tiny ego, petty
- `trickster` — fox/cat/snake/monkey/rabbit spirits, imps: mischievous, giggling, mocking
- `brute`   — stone/tree demons, monster lion, boar, young dragon: big, slow, grumpy, simple words

### Events (when) and how many lines per voice

| event   | when it is said                                              | lines |
|---------|--------------------------------------------------------------|-------|
| start   | the battle begins                                            | 6     |
| taunt   | teasing the opponent during the fight (said often)           | 8     |
| combo   | the speaker just made a big combo / heavy hit                | 5     |
| hurt    | the speaker just took a hit                                  | 6     |
| low     | the speaker is low on HP                                     | 5     |
| heal    | the speaker healed                                           | 3     |
| block   | the speaker's shield stopped a hit                           | 3     |
| skill   | the speaker uses a skill (may use {skill})                   | 4     |
| item    | the speaker uses an item (potion, armor…)                    | 3     |
| win     | the speaker won                                              | 6     |
| lose    | the speaker lost                                             | 5     |
| revive  | the speaker got back up after falling (players only: hero_*) | 3     |

So each voice has 12 sections (11 for non-hero voices without revive): about 50–57 lines per voice,
about 800 lines per language. No duplicate lines. Give every voice a distinct personality you could
recognize without the name. Mix it up: short punchy ones, questions, onomatopoeia, emoji-free.

## When done

Run the checker and fix every problem it reports, until it prints OK:

```
python3 Converter/i18n/check.py <code>
```

Do not edit any other file. Do not commit. Reply with a 3-line summary (files written, row counts,
anything uncertain).
