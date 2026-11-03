// Character slots (plan/thiet-ke-nhieu-nhan-vat.md): up to 3 characters, each with its own record
// stores (map, quests, level, relics), sharing gold, items and lives. This class keeps the summary the
// menu draws (PlayerPrefs "l12.slots.v1", JSON, section 3.3) and the creation credits; the game side
// (which stores the game reads) is Loan12.C_PortScreens.
using System;
using UnityEngine;

namespace Loan12.UnityPort
{
    [Serializable]
    public class SlotInfo
    {
        public bool used;
        public int hero;
        public string name = "";
        public int level = 1;
        public long createdAt;
        public long lastPlayedAt;
    }

    [Serializable]
    public class SlotData
    {
        public int version = 1;
        public int activeSlot = -1;
        public int createCredits = 1;
        public int everCreated;
        public SlotInfo[] slots = {new SlotInfo(), new SlotInfo(), new SlotInfo()};
    }

    public static class Slots
    {
        const string Key = "l12.slots.v1";
        public const int Count = 3;
        static SlotData data;

        // a character being created: slot, the slot in play before, and whether the game left the menu
        static int creating = -1;
        static int before = -1;
        static bool leftMenu;

        public static SlotData Data
        {
            get
            {
                Load();
                return data;
            }
        }

        public static long Now
        {
            get { return DateTimeOffset.UtcNow.ToUnixTimeSeconds(); }
        }

        static void Load()
        {
            if (data != null)
            {
                return;
            }
            string s = PlayerPrefs.GetString(Key, "");
            if (s.Length > 0)
            {
                try
                {
                    data = JsonUtility.FromJson<SlotData>(s);
                }
                catch (Exception)
                {
                    data = null;
                }
            }
            if (data == null || data.slots == null || data.slots.Length != Count)
            {
                data = Migrate();
                Save();
            }
            // clean up: stores of a slot whose creation never finished (killed app ...)
            for (int i = 0; i < Count; i++)
            {
                if (!data.slots[i].used && SafeUsed(i))
                {
                    C_PortScreens.wipeSlot(i);
                }
            }
            if (data.activeSlot >= 0 && !data.slots[data.activeSlot].used)
            {
                data.activeSlot = MostRecent();
            }
        }

        /// <summary>First run of this version: the save of earlier versions becomes slot 1 (3.4).</summary>
        static SlotData Migrate()
        {
            SlotData d = new SlotData();
            if (SafeUsed(0))
            {
                d.slots[0].used = true;
                d.slots[0].hero = C_PortScreens.slotHero(0);
                d.slots[0].name = C_PortScreens.slotName(0);
                d.slots[0].level = C_PortScreens.slotLevel(0);
                d.slots[0].createdAt = Now;
                d.slots[0].lastPlayedAt = Now;
                d.activeSlot = 0;
                d.createCredits = 0;
                d.everCreated = 1;
            }
            return d;
        }

        static bool SafeUsed(int n)
        {
            try
            {
                return C_PortScreens.slotUsed(n);
            }
            catch (Exception)
            {
                return false;
            }
        }

        public static void Save()
        {
            PlayerPrefs.SetString(Key, JsonUtility.ToJson(data));
            PlayerPrefs.Save();
        }

        public static int Used
        {
            get
            {
                int n = 0;
                foreach (SlotInfo s in Data.slots)
                {
                    if (s.used)
                    {
                        n++;
                    }
                }
                return n;
            }
        }

        public static int FirstEmpty()
        {
            for (int i = 0; i < Count; i++)
            {
                if (!Data.slots[i].used)
                {
                    return i;
                }
            }
            return -1;
        }

        public static bool CanCreate
        {
            get { return Data.createCredits > 0 || Rewards.Vip; }
        }

        public static void GrantCredit()
        {
            Data.createCredits = 1;
            Save();
        }

        static int MostRecent()
        {
            int best = -1;
            for (int i = 0; i < Count; i++)
            {
                if (data.slots[i].used && (best < 0 || data.slots[i].lastPlayedAt > data.slots[best].lastPlayedAt))
                {
                    best = i;
                }
            }
            return best;
        }

        /// <summary>"Chơi": slot n becomes the character in play.</summary>
        public static void Play(int n)
        {
            Data.activeSlot = n;
            data.slots[n].lastPlayedAt = Now;
            Save();
            C_PortScreens.request(C_PortScreens.f_MENU_CONTINUE, n);
        }

        /// <summary>Starts the creation of a character in slot n (the credit is taken when it is done).</summary>
        public static void Create(int n)
        {
            Load();
            creating = n;
            before = data.activeSlot;
            leftMenu = false;
            C_PortScreens.request(C_PortScreens.f_MENU_NEW, n);
        }

        public static bool Creating
        {
            get { return creating >= 0; }
        }

        public static int CreatingSlot
        {
            get { return creating; }
        }

        /// <summary>Called every frame: finishes or cancels a creation when the game reaches the map / menu.</summary>
        public static void Watch()
        {
            if (creating < 0)
            {
                return;
            }
            int kind;
            try
            {
                if (C_PortScreens.busy())
                {
                    return;
                }
                kind = C_PortScreens.screenKind();
            }
            catch (Exception)
            {
                return;
            }
            if (kind != 2)
            {
                leftMenu = true;
            }
            if (kind == 4)
            {
                // name and hero chosen: the character exists (order of 3.3: save, used, credit, active)
                SlotInfo s = data.slots[creating];
                s.used = true;
                s.hero = C_dd.d();
                s.name = C_dd.c();
                s.level = 1;
                s.createdAt = Now;
                s.lastPlayedAt = Now;
                if (!Rewards.Vip || data.createCredits > 0)
                {
                    data.createCredits = Mathf.Max(0, data.createCredits - 1);
                }
                data.everCreated++;
                data.activeSlot = creating;
                Save();
                creating = -1;
            }
            else if (kind == 2 && leftMenu)
            {
                // back at the menu before the map: cancelled, no credit used
                C_PortScreens.wipeSlot(creating);
                creating = -1;
                if (before >= 0)
                {
                    C_PortScreens.useSlot(before);
                }
            }
        }

        /// <summary>Refreshes the summary (level) of the slot in play from its save.</summary>
        public static void Refresh()
        {
            Load();
            int n = data.activeSlot;
            if (n < 0 || creating >= 0)
            {
                return;
            }
            try
            {
                if (C_PortScreens.f_slot == n)
                {
                    int lv = C_PortScreens.slotLevel(n);
                    if (lv != data.slots[n].level)
                    {
                        data.slots[n].level = lv;
                        Save();
                    }
                }
            }
            catch (Exception)
            {
            }
        }

        /// <summary>Deletes the character of slot n for good (order of 3.3).</summary>
        public static void Delete(int n)
        {
            Load();
            data.slots[n] = new SlotInfo();
            Save();
            C_PortScreens.wipeSlot(n);
            if (data.activeSlot == n)
            {
                data.activeSlot = MostRecent();
                Save();
            }
        }

        public static string Ago(long t)
        {
            long d = Now - t;
            if (d < 60)
            {
                return "vừa xong";
            }
            if (d < 3600)
            {
                return (d / 60) + " phút trước";
            }
            DateTime then = DateTimeOffset.FromUnixTimeSeconds(t).LocalDateTime, now = DateTime.Now;
            int days = (now.Date - then.Date).Days;
            if (days <= 0)
            {
                return "hôm nay";
            }
            if (days == 1)
            {
                return "hôm qua";
            }
            if (days <= 30)
            {
                return days + " ngày trước";
            }
            return then.ToString("dd/MM/yyyy");
        }
    }
}
