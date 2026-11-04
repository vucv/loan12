// Developer aid, part 2 of the "-loan12-tour" screenshot tour: the screens the first part does not reach
// (map popups and tabs, the character slots, a battle with its popups, pause and both results). The views
// keep their sub-states in private fields, so the tour sets them by reflection - nothing here is used by
// players and no view changes for it.
using System.Collections;
using System.Reflection;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class TouchTourMore
    {
        [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.BeforeSceneLoad)]
        static void Register()
        {
            TouchTour.Extra = Stage;
        }

        const BindingFlags Any = BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic;

        static void Set(object o, string field, object v)
        {
            FieldInfo f = o.GetType().GetField(field, Any);
            if (f == null)
            {
                Debug.LogWarning("Loan12 tour: no field " + o.GetType().Name + "." + field);
                return;
            }
            f.SetValue(o, v);
        }

        static object Get(object o, string field)
        {
            FieldInfo f = o.GetType().GetField(field, Any);
            return f == null ? null : f.GetValue(o);
        }

        static IEnumerator Stage(string stage)
        {
            switch (stage)
            {
                case "map":
                    return Map();
                case "menu":
                    return Menu();
                default:
                    return null;
            }
        }

        // ------------------------------------------------------------------------- menu

        static IEnumerator Menu()
        {
            TouchMenuView menu = TouchMenuView.Instance;
            TouchSlotsPage slots = new TouchSlotsPage();
            menu.Open(slots);
            yield return TouchTour.Shot("slots");
            Set(slots, "deleting", 0);
            Set(slots, "sheetAt", Time.unscaledTime - 1f);
            yield return TouchTour.Shot("slots_delete");
            menu.Open(new TouchSlotsPage(true));
            yield return new WaitForSeconds(0.5f);
            yield return TouchTour.Shot("slots_ad");
            menu.Open(null);
            for (int sub = 0; sub < 3; sub++)
            {
                menu.OpenShop(sub);
                yield return TouchTour.Shot("menu_shop_" + sub);
            }
            menu.Open(null);
        }

        // ------------------------------------------------------------------------- map

        static IEnumerator Map()
        {
            TouchMapView map = TouchMapView.Instance;
            if (map == null)
            {
                yield break;
            }
            // the first-time tutorial: its second step, then off
            if ((int) Get(map, "tutorial") >= 0)
            {
                Set(map, "tutorial", 1);
                yield return TouchTour.Shot("map_tutorial_2");
                Set(map, "tutorial", -1);
                Set(map, "lastTouch", -10f);
            }
            yield return TouchTour.Shot("map_clean");

            // settings opened from the map
            Set(map, "settings", new TouchSettingsPage(TouchSettingsPage.Where.Map));
            yield return TouchTour.Shot("map_settings");
            Set(map, "settings", null);

            // lives
            TouchLivesView.Requested = true;
            yield return TouchTour.Shot("map_lives");
            TouchLivesView.Requested = false;

            // bag
            map.tab = TouchMapView.TabBag;
            object bag = map.tabs[TouchMapView.TabBag];
            yield return TouchTour.Shot("bag");
            Set(bag, "detail", MapUi.BagItems[0]);
            yield return TouchTour.Shot("bag_item");
            Set(bag, "detail", -1);
            Set(bag, "skillDetail", 0);
            yield return TouchTour.Shot("bag_skill");
            Set(bag, "skillDetail", -1);
            Set(bag, "guide", true);
            yield return TouchTour.Shot("bag_guide");
            Set(bag, "guide", false);

            // shop
            map.tab = TouchMapView.TabShop;
            TouchShopTab shop = (TouchShopTab) map.tabs[TouchMapView.TabShop];
            shop.sub = 0;
            yield return TouchTour.Shot("shop_items");
            Set(shop, "buying", MapUi.BagItems[0]);
            Set(shop, "packs", 1);
            yield return TouchTour.Shot("shop_buy");
            Set(shop, "buying", -1);
            shop.sub = 1;
            yield return TouchTour.Shot("shop_gold");
            shop.sub = 2;
            yield return TouchTour.Shot("shop_offers");
            shop.sub = 0;

            // quests
            map.tab = TouchMapView.TabQuest;
            TouchQuestTab quest = (TouchQuestTab) map.tabs[TouchMapView.TabQuest];
            quest.sub = 0;
            yield return TouchTour.Shot("quest_main");
            Set(quest, "detail", C_PortMap.questIndex());
            yield return TouchTour.Shot("quest_detail");
            Set(quest, "detail", -1);
            quest.sub = 1;
            yield return TouchTour.Shot("quest_events");
            quest.sub = 2;
            yield return TouchTour.Shot("quest_guards");
            quest.sub = 0;
            map.tab = TouchMapView.TabMap;
            yield return new WaitForSeconds(0.5f);

            // a battle that is won, then one that is given up
            yield return Battle(true);
            yield return Battle(false);
        }

        static bool OnFreeMap()
        {
            return TouchTour.Kind() == 4 && C_PortMap.free() && !C_PortMap.walking();
        }

        static IEnumerator Battle(bool win)
        {
            string tag = win ? "" : "2_";
            yield return TouchTour.Until(OnFreeMap, 20);
            int q = C_PortMap.questIndex();
            if (q >= C_PortMap.questCount())
            {
                yield break;
            }
            C_PortMap.request(C_PortMap.f_GO, C_PortMap.questNode(q));
            yield return new WaitForSeconds(1f);
            if (win)
            {
                yield return TouchTour.Shot("map_walking");
            }
            yield return TouchTour.Until(() => C_PortMap.talking() || C_PortUi.prepareView() || C_PortUi.battleView(), 60);
            if (C_PortMap.talking())
            {
                yield return new WaitForSeconds(1f);
                yield return TouchTour.Shot(tag + "talk");
                for (int i = 0; i < 20 && C_PortMap.talking(); i++)
                {
                    C_PortMap.request(C_PortMap.f_TALK_SKIP, 0);
                    yield return new WaitForSeconds(0.8f);
                }
            }
            yield return TouchTour.Until(() => C_PortUi.prepareView() || C_PortUi.battleView(), 20);
            if (C_PortUi.prepareView())
            {
                yield return new WaitForSeconds(0.8f);
                yield return TouchTour.Shot(tag + "prepare");
                if (win)
                {
                    TouchPrepareView prep = Host<TouchPrepareView>();
                    if (prep != null && Loadout.Usable.Length > 0)
                    {
                        Set(prep, "confirmType", Loadout.Usable[0]);
                        yield return TouchTour.Shot("prepare_buy");
                        Set(prep, "confirmType", -1);
                    }
                }
                C_PortUi.request(C_PortUi.f_SCREEN_KEY, 0); // "Xuất trận"
            }
            yield return TouchTour.Until(() => C_PortUi.battleView() && C_PortUi.waiting() || C_PortScreens.coachWaits(), 30);
            yield return new WaitForSeconds(1.5f);
            yield return TouchTour.Shot(tag + "battle");
            yield return Coach(win ? "battle_coach" : null);

            TouchBattleView view = Host<TouchBattleView>();
            if (!win)
            {
                C_PortUi.request(C_PortUi.f_PAUSE, 0);
                yield return TouchTour.Until(C_PortUi.pauseView, 5);
                yield return TouchTour.Shot("pause");
                C_PortScreens.request(C_PortScreens.f_FORFEIT, 0);
                yield return Result("result_lost");
                yield break;
            }

            // popups of a skill and an item
            yield return TouchTour.Until(() => C_PortUi.waiting() && C_PortUi.turn() == 0, 20);
            if (view != null)
            {
                Set(view, "popupKind", 1);
                Set(view, "popupArg", 0);
                yield return TouchTour.Shot("battle_popup_skill");
                Set(view, "popupKind", 2);
                Set(view, "popupArg", 0);
                yield return TouchTour.Shot("battle_popup_item");
                Set(view, "popupKind", 0);
            }

            // play: enemy left on 1 HP, the bot makes matching moves
            bool enemyShot = false, moveShot = false;
            float end = Time.realtimeSinceStartup + 120f;
            while (!C_PortUi.resultView() && Time.realtimeSinceStartup < end)
            {
                yield return Coach(null);
                if (C_PortUi.battleView() && C_PortUi.turn() == 1 && !enemyShot)
                {
                    yield return new WaitForSeconds(1.2f);
                    yield return TouchTour.Shot("battle_enemy_turn");
                    enemyShot = true;
                }
                if (enemyShot)
                {
                    WeakenEnemy();
                }
                if (C_PortUi.waiting() && C_PortUi.turn() == 0)
                {
                    BotMove();
                    yield return new WaitForSeconds(0.6f);
                    if (!moveShot)
                    {
                        yield return TouchTour.Shot("battle_effects");
                        moveShot = true;
                    }
                }
                yield return new WaitForSeconds(0.3f);
            }
            yield return Result("result_won");
        }

        static IEnumerator Coach(string shot)
        {
            for (int i = 0; i < 8 && C_PortScreens.coachWaits(); i++)
            {
                if (shot != null && i == 0)
                {
                    yield return TouchTour.Shot(shot);
                }
                C_PortScreens.request(C_PortScreens.f_COACH_NEXT, 0);
                yield return new WaitForSeconds(1f);
            }
        }

        static IEnumerator Result(string name)
        {
            yield return TouchTour.Until(C_PortUi.resultView, 30);
            yield return new WaitForSeconds(2.5f);
            yield return TouchTour.Shot(name);
            if (!C_PortUi.resultReady())
            {
                C_PortUi.request(C_PortUi.f_RESULT_SKIP, 0);
                yield return new WaitForSeconds(1.5f);
                yield return TouchTour.Shot(name + "_done");
            }
            for (int i = 0; i < 10 && C_PortUi.resultView(); i++)
            {
                if (C_PortUi.resultMode() == 3)
                {
                    C_PortUi.request(C_PortUi.f_END_BACK, 0);
                }
                else if (C_PortUi.resultReady())
                {
                    C_PortUi.request(C_PortUi.f_DIALOG_KEY, 1);
                }
                else
                {
                    C_PortUi.request(C_PortUi.f_RESULT_SKIP, 0);
                }
                yield return new WaitForSeconds(1f);
            }
            // a dialogue after the battle
            yield return TouchTour.Until(() => C_PortMap.talking() || OnFreeMap(), 20);
            if (C_PortMap.talking())
            {
                yield return new WaitForSeconds(1f);
                yield return TouchTour.Shot(name + "_talk");
                for (int i = 0; i < 20 && C_PortMap.talking(); i++)
                {
                    C_PortMap.request(C_PortMap.f_TALK_SKIP, 0);
                    yield return new WaitForSeconds(0.8f);
                }
            }
        }

        static T Host<T>() where T : class
        {
            J2MEGameHost host = J2MEGameHost.Instance;
            if (host == null)
            {
                return null;
            }
            foreach (FieldInfo f in host.GetType().GetFields(Any))
            {
                object v = f.GetValue(host);
                if (v is T)
                {
                    return (T) v;
                }
                System.Collections.IEnumerable list = v as System.Collections.IEnumerable;
                if (list != null && !(v is string))
                {
                    foreach (object o in list)
                    {
                        if (o is T)
                        {
                            return (T) o;
                        }
                    }
                }
            }
            return null;
        }

        // ------------------------------------------------------------------------- bot (as the Java harness)

        static SQ_ab CurrentBattle()
        {
            C_cz canvas = C_cz.f_a;
            if (canvas == null || canvas.f_e == null)
            {
                return null;
            }
            return canvas.f_e.f_b as SQ_ab;
        }

        static void WeakenEnemy()
        {
            SQ_ab cur = CurrentBattle();
            if (cur == null || cur.f_c == null)
            {
                return;
            }
            C_bn[] fighters = Get(cur.f_c, "f_f") as C_bn[];
            if (fighters == null)
            {
                return;
            }
            foreach (C_bn f in fighters)
            {
                if (f != null && f.b() >= 6 && (int) Get(f, "f_c") > 1)
                {
                    Set(f, "f_c", 1);
                }
            }
        }

        static void BotMove()
        {
            SQ_ab cur = CurrentBattle();
            if (cur == null || cur.f_e == null || cur.f_c == null)
            {
                return;
            }
            C_bu board = cur.f_e;
            if ((int) Get(board, "f_v") != 8 || cur.f_c.a() != 0)
            {
                return;
            }
            C_di moves = board.q();
            if (moves == null || moves.b() == 0)
            {
                return;
            }
            int pick = 0;
            for (int k = 0; k < moves.b() && pick == 0; k++)
            {
                SQ_a m = (SQ_a) moves.b(k);
                int t1 = board.d(m.f_a, m.f_b), t2 = board.d(m.f_c, m.f_d);
                int[] types = {t2, t2, t1, t1};
                for (int i = 0; i < m.f_e.Length; i++)
                {
                    if ((m.f_e[i] & 0xFF) >= 3 && (types[i] == 0 || types[i] == 6))
                    {
                        pick = k;
                        break;
                    }
                }
            }
            SQ_a mv = (SQ_a) moves.b(pick);
            board.a(mv.f_a, mv.f_b, mv.f_c, mv.f_d);
        }
    }
}
