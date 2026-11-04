// Developer aid: "-loan12-tour <folder>" on the command line (desktop builds) plays through the touch
// screens by calling the same actions the buttons call, and saves a screenshot of each screen, named by
// group ("A1_menu.png" ...) so they can be compared side by side. The saved game of the build is
// cleared first ("-loan12-tour" is never used by players). Lets the phone layout be checked without a
// phone. Groups:
//   A start and menu   B new character   C map   D bag   E shop   F quests   G dialogue and arena
//   H battle   I results   J characters   K languages
using System.Collections;
using System.IO;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class TouchTour
    {
        static string dir;
        static int n;

        public static IEnumerator Run(string folder)
        {
            dir = folder;
            Directory.CreateDirectory(dir);
            Application.runInBackground = true;
            Debug.Log("Loan12 tour: start");
            Loc.Current = Lang.Vi;

            // ---- A: start and menu
            yield return Until(() => Kind() == 1 || Kind() == 2, 25);
            if (Kind() == 1)
            {
                yield return Shot("A", "boot");
            }
            yield return Until(() => Kind() == 2, 25);
            yield return new WaitForSeconds(1.5f);
            yield return Shot("A", "menu_new");
            TouchMenuView menu = TouchMenuView.Instance;
            menu.askExit = true;
            yield return Shot("A", "menu_exit_confirm");
            menu.askExit = false;
            TouchSettingsPage settings = new TouchSettingsPage(TouchSettingsPage.Where.Menu);
            menu.Open(settings);
            yield return Shot("A", "settings");
            settings.langOpen = true;
            yield return Shot("A", "settings_language");
            settings.langOpen = false;
            menu.Open(new RulesPage());
            yield return Shot("A", "rules");
            menu.Open(new AboutPage());
            yield return Shot("A", "about");
            menu.Open(new ShopPage(0));
            yield return Shot("A", "shop_from_menu");
            menu.Open(null);

            // ---- B: new character: story -> hero -> name -> map
            menu.NewCharacter();
            yield return Until(() => Kind() == 5, 10);
            yield return new WaitForSeconds(4f);
            yield return Shot("B", "story_intro");
            Loc.Current = Lang.En;
            yield return new WaitForSeconds(0.5f);
            yield return Shot("B", "story_intro_en");
            Loc.Current = Lang.Vi;
            C_PortScreens.request(C_PortScreens.f_SKIP, 0);
            yield return Until(() => Kind() == 3, 15);
            yield return new WaitForSeconds(1f);
            yield return Shot("B", "hero_choose");
            C_PortScreens.request(C_PortScreens.f_HERO_OK, 0);
            yield return Until(() => J2ME.Host.display.getShown() is J2ME.TextBox, 5);
            yield return Shot("B", "hero_name");
            J2ME.TextBox tb = J2ME.Host.display.getShown() as J2ME.TextBox;
            if (tb != null)
            {
                tb.setString("Hùng Dũng");
                for (int i = 0; i < tb.getCommandCount(); i++)
                {
                    if (tb.getCommand(i).getLabel() == "Xong")
                    {
                        tb.fireCommand(tb.getCommand(i));
                    }
                }
            }

            // ---- C: map
            yield return Until(() => Kind() == 4, 15);
            yield return new WaitForSeconds(3f);
            TouchMapView map = TouchMapView.Instance;
            yield return Shot("C", "map_tutorial_1");
            if (map != null)
            {
                map.tutorial = 1;
                yield return Shot("C", "map_tutorial_2");
                map.tutorial = -1;
            }
            yield return Shot("C", "map");
            TouchLivesView.Requested = true;
            yield return Shot("C", "lives_box");
            TouchLivesView.Requested = false;

            // ---- first quest: dialogue and the tutorial battle
            yield return PlayQuest(120);

            // ---- D: bag
            yield return Until(() => Kind() == 4 && C_PortMap.free(), 20);
            yield return new WaitForSeconds(1f);
            if (map != null)
            {
                map.OpenTab(TouchMapView.TabBag);
                yield return Shot("D", "bag");
                TouchBagTab bag = (TouchBagTab) map.tabs[TouchMapView.TabBag];
                bag.guide = true;
                yield return Shot("D", "bag_hero_guide");
                bag.guide = false;
                bag.skillDetail = 0;
                yield return Shot("D", "bag_skill");
                bag.skillDetail = -1;
                bag.detail = 4;
                yield return Shot("D", "bag_item");
                bag.detail = Refill.Type;
                yield return Shot("D", "bag_item_refill");
                bag.detail = -1;

                // ---- E: shop
                map.OpenTab(TouchMapView.TabShop);
                TouchShopTab shop = (TouchShopTab) map.tabs[TouchMapView.TabShop];
                for (int s = 0; s < 3; s++)
                {
                    shop.sub = s;
                    yield return Shot("E", "shop_" + (s == 0 ? "items" : (s == 1 ? "gold" : "offers")));
                }
                shop.sub = 0;
                shop.buying = 4;
                yield return Shot("E", "shop_buy_box");
                shop.buying = -1;

                // ---- F: quests
                map.OpenTab(TouchMapView.TabQuest);
                TouchQuestTab quests = (TouchQuestTab) map.tabs[TouchMapView.TabQuest];
                for (int s = 0; s < 3; s++)
                {
                    quests.sub = s;
                    yield return Shot("F", "quests_" + (s == 0 ? "main" : (s == 1 ? "events" : "guards")));
                }
                quests.sub = 0;
                quests.detail = 0;
                yield return Shot("F", "quest_detail");
                quests.detail = -1;
                quests.readQ = 0;
                quests.readSet = 0;
                quests.readLine = 0;
                yield return Shot("F", "quest_reader");
                quests.readQ = -1;
                map.OpenTab(TouchMapView.TabMap);
            }

            // ---- H / I: more quests until a real battle (prepare, battle, pause, win), then one lost
            yield return PlayQuest(150);
            yield return PlayQuest(150);
            yield return PlayQuest(150);
            yield return PlayQuest(150, true);

            // ---- G: the arena, if a road leads there already
            yield return Arena();

            // ---- J: back to the menu: CHƠI TIẾP + NHÂN VẬT
            yield return Until(() => Kind() == 4 && C_PortMap.free(), 20);
            yield return ToMenu();
            yield return Until(() => Kind() == 2, 15);
            yield return new WaitForSeconds(1.5f);
            yield return Shot("J", "menu_continue");
            TouchSlotsPage slots = new TouchSlotsPage();
            menu.Open(slots);
            yield return Shot("J", "characters");
            slots.deleting = Slots.Data.activeSlot;
            yield return Shot("J", "character_delete");
            slots.deleting = -1;
            menu.Open(null);

            // ---- K: the same screens in English, Japanese and Korean
            string[] langs = {"en", "ja", "ko"};
            for (int l = 0; l < 3; l++)
            {
                Loc.Current = (Lang) (l + 1);
                yield return Shot("K", langs[l] + "_menu");
                settings = new TouchSettingsPage(TouchSettingsPage.Where.Menu);
                menu.Open(settings);
                yield return Shot("K", langs[l] + "_settings");
                menu.Open(null);
            }
            Loc.Current = Lang.Vi;
            Slots.Play(Slots.Data.activeSlot);
            yield return Until(() => Kind() == 4, 20);
            yield return new WaitForSeconds(2f);
            map = TouchMapView.Instance;
            for (int l = 0; l < 3; l++)
            {
                Loc.Current = (Lang) (l + 1);
                if (map != null)
                {
                    map.OpenTab(TouchMapView.TabBag);
                    yield return Shot("K", langs[l] + "_bag");
                    TouchBagTab bag = (TouchBagTab) map.tabs[TouchMapView.TabBag];
                    bag.guide = true;
                    yield return Shot("K", langs[l] + "_bag_hero_guide");
                    bag.guide = false;
                    map.OpenTab(TouchMapView.TabQuest);
                    yield return Shot("K", langs[l] + "_quests");
                    TouchQuestTab quests = (TouchQuestTab) map.tabs[TouchMapView.TabQuest];
                    quests.readQ = 0;
                    quests.readSet = 0;
                    quests.readLine = 1;
                    yield return Shot("K", langs[l] + "_dialogue");
                    quests.readQ = -1;
                    map.OpenTab(TouchMapView.TabMap);
                }
            }
            Loc.Current = Lang.Vi;

            Debug.Log("Loan12 tour: done (" + n + " shots)");
            yield return new WaitForSeconds(0.5f);
            Application.Quit();
        }

        static readonly System.Collections.Generic.HashSet<string> seen = new System.Collections.Generic.HashSet<string>();

        /// <summary>Once per tour: a screenshot the first time a screen shows up.</summary>
        static IEnumerator Once(string group, string name)
        {
            if (seen.Add(group + name))
            {
                yield return Shot(group, name);
            }
        }

        /// <summary>
        /// Goes to the current main quest, starts it and plays through what follows (dialogues, the
        /// prepare screen, the battle with good moves and a weakened enemy, the results), until back on
        /// the map. lose: the battle is given up from the pause screen instead.
        /// </summary>
        static IEnumerator PlayQuest(float seconds, bool lose = false)
        {
            yield return Until(() => Kind() == 4 && C_PortMap.free() && !C_PortMap.walking(), 20);
            int q = C_PortMap.questIndex();
            if (q >= C_PortMap.questCount())
            {
                yield break;
            }
            int node = C_PortMap.questNode(q);
            if (C_PortMap.playerNode() != node)
            {
                C_PortMap.request(C_PortMap.f_GO, node);
                yield return new WaitForSeconds(0.5f);
                yield return Until(() => !C_PortMap.walking(), 40);
                yield return new WaitForSeconds(0.8f);
            }
            if (Kind() == 4 && C_PortMap.free())
            {
                C_PortMap.request(C_PortMap.f_START, 0);
            }
            float end = Time.realtimeSinceStartup + seconds;
            bool fought = false, paused = false;
            float lastMove = 0f;
            while (Time.realtimeSinceStartup < end)
            {
                yield return null;
                if (C_PortScreens.busy())
                {
                    continue;
                }
                if (Kind() == 4 && C_PortMap.talking())
                {
                    yield return new WaitForSeconds(0.6f);
                    yield return Once("G", "dialogue");
                    C_PortMap.request(C_PortMap.f_TALK_SKIP, 0);
                    yield return new WaitForSeconds(0.5f);
                    continue;
                }
                if (C_PortUi.prepareView())
                {
                    yield return new WaitForSeconds(0.8f);
                    yield return Once("H", "prepare");
                    C_PortUi.request(C_PortUi.f_SCREEN_KEY, 0); // "Đánh"
                    yield return new WaitForSeconds(1f);
                    continue;
                }
                if (C_PortUi.pauseView())
                {
                    continue;
                }
                if (C_PortScreens.hasDialog() && !C_PortUi.resultView())
                {
                    // a box of the game's own (no touch view for it): kept as found, then its first choice
                    yield return new WaitForSeconds(0.6f);
                    yield return Once("H", "game_dialog");
                    C_PortUi.request(C_PortUi.f_DIALOG_KEY, 0);
                    yield return new WaitForSeconds(1f);
                    continue;
                }
                if (C_PortUi.battleView())
                {
                    fought = true;
                    bool training = C_PortScreens.training();
                    if (training && C_PortScreens.coachWaits())
                    {
                        yield return new WaitForSeconds(0.6f);
                        yield return Once("H", "battle_tutorial");
                        C_PortScreens.request(C_PortScreens.f_COACH_NEXT, 0);
                        yield return new WaitForSeconds(0.6f);
                        continue;
                    }
                    if (!C_PortUi.waiting())
                    {
                        continue;
                    }
                    if (training)
                    {
                        // the tutorial battle: after its first lessons, skip it the way the game offers
                        // (pause -> "Bạn có muốn bỏ qua phần luyện tập không?" -> Có)
                        C_PortUi.request(C_PortUi.f_PAUSE, 0);
                        yield return Until(() => C_PortScreens.hasDialog(), 5);
                        yield return Once("H", "tutorial_skip_dialog");
                        C_PortUi.request(C_PortUi.f_DIALOG_KEY, 0);
                        yield return new WaitForSeconds(2f);
                        continue;
                    }
                    if (!seen.Contains("Hbattle"))
                    {
                        yield return new WaitForSeconds(1f);
                        yield return Once("H", "battle");
                        TouchBattleView bv = J2MEGameHost.View<TouchBattleView>();
                        if (bv != null)
                        {
                            bv.popupKind = 1;
                            bv.popupArg = 0;
                            yield return Shot("H", "battle_skill_popup");
                            bv.popupKind = 0;
                        }
                    }
                    if (lose || !paused)
                    {
                        paused = true;
                        C_PortUi.request(C_PortUi.f_PAUSE, 0);
                        yield return Until(() => C_PortUi.pauseView(), 5);
                        yield return Once("H", "pause");
                        TouchPauseView pv = J2MEGameHost.View<TouchPauseView>();
                        if (pv != null)
                        {
                            pv.confirm = true;
                            yield return Once("H", "pause_leave_confirm");
                            pv.confirm = false;
                        }
                        if (lose)
                        {
                            C_PortScreens.request(C_PortScreens.f_FORFEIT, 0);
                            yield return new WaitForSeconds(2f);
                            continue;
                        }
                        C_PortUi.request(C_PortUi.f_PAUSE_RESUME, 0);
                        yield return new WaitForSeconds(1f);
                        continue;
                    }
                    C_bn enemy = C_PortUi.fighter(1);
                    if (enemy != null)
                    {
                        enemy.m(1);
                    }
                    if (Time.realtimeSinceStartup - lastMove > 1.2f)
                    {
                        C_bu b = C_PortUi.battle();
                        if (b != null && b.portBotMove())
                        {
                            lastMove = Time.realtimeSinceStartup;
                        }
                    }
                    continue;
                }
                if (C_PortUi.resultView())
                {
                    int mode = C_PortUi.resultMode();
                    if (!C_PortUi.resultReady())
                    {
                        yield return new WaitForSeconds(1.2f);
                        yield return Once("I", "result_counting_" + mode);
                        C_PortUi.request(C_PortUi.f_RESULT_SKIP, 0);
                        yield return new WaitForSeconds(0.8f);
                        continue;
                    }
                    yield return new WaitForSeconds(0.8f);
                    yield return Once("I", mode == 3 ? "result_lost" : (mode == 1 ? "quest_complete" : "result_" + mode));
                    C_PortUi.request(mode == 3 ? C_PortUi.f_END_BACK : C_PortUi.f_DIALOG_KEY, mode == 3 ? 0 : 1);
                    yield return new WaitForSeconds(1.2f);
                    continue;
                }
                if (Kind() == 4 && C_PortMap.free() && fought)
                {
                    yield return new WaitForSeconds(1f);
                    yield return Once("C", "map_after_battle");
                    yield break;
                }
                if (Kind() == 4 && C_PortMap.free() && C_PortMap.questIndex() != q)
                {
                    yield break; // a quest without a battle
                }
            }
            Debug.LogWarning("Loan12 tour: quest " + q + " still going after " + seconds + " s, screen " + Kind());
        }

        static IEnumerator Arena()
        {
            yield return Until(() => Kind() == 4 && C_PortMap.free(), 10);
            TouchArenaView arena = J2MEGameHost.View<TouchArenaView>();
            if (arena != null && arena.Active())
            {
                yield return Shot("G", "arena");
                C_PortMap.request(C_PortMap.f_ARENA_LEAVE, 0);
            }
        }

        /// <summary>From the map back to the menu (saves the map).</summary>
        public static System.Func<IEnumerator> ToMenu = MapToMenu;

        static IEnumerator MapToMenu()
        {
            C_PortMap.request(C_PortMap.f_MENU, 0);
            yield return new WaitForSeconds(0.5f);
        }

        public static int Kind()
        {
            try
            {
                return C_PortScreens.busy() ? -1 : C_PortScreens.screenKind();
            }
            catch (System.Exception)
            {
                return -1;
            }
        }

        public static IEnumerator Until(System.Func<bool> cond, float seconds)
        {
            float end = Time.realtimeSinceStartup + seconds;
            while (!Safe(cond) && Time.realtimeSinceStartup < end)
            {
                yield return null;
            }
            if (!Safe(cond))
            {
                Debug.LogWarning("Loan12 tour: timed out (" + seconds + " s), screen " + Kind());
            }
        }

        static bool Safe(System.Func<bool> cond)
        {
            try
            {
                return cond();
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public static IEnumerator Shot(string group, string name)
        {
            yield return new WaitForSeconds(0.7f);
            yield return Gx.Offscreen ? null : new WaitForEndOfFrame();
            n++;
            string path = Path.Combine(dir, group + n.ToString("00") + "_" + name + ".png");
            RenderTexture rt = J2MEGameHost.Instance != null ? J2MEGameHost.Instance.Offscreen : null;
            if (Gx.Offscreen && rt != null)
            {
                // batch mode: the picture the host drew offscreen this frame
                RenderTexture prev = RenderTexture.active;
                RenderTexture.active = rt;
                Texture2D t = new Texture2D(rt.width, rt.height, TextureFormat.RGB24, false);
                t.ReadPixels(new Rect(0, 0, rt.width, rt.height), 0, 0);
                t.Apply();
                RenderTexture.active = prev;
                File.WriteAllBytes(path, t.EncodeToPNG());
                Object.Destroy(t);
            }
            else
            {
                ScreenCapture.CaptureScreenshot(path, 2);
            }
            Debug.Log("Loan12 tour: " + path);
            yield return new WaitForSeconds(0.3f);
        }
    }
}
