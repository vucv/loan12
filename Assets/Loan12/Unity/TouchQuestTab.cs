// Screen 8 of the touch redesign: the quests tab of the map (plan/thiet-ke-ban-do.md 6). The game had
// no list, only "!" marks on the map. Three kinds of work, exactly those of the game's code:
//   Chính tuyến: the 46 main quests in 9 chapters (dd.l, progress aa.T); done ones can be read again
//   Sự kiện: the 8 side events (dd.n) and the arena levels of Tam Điệp
//   Quân canh: the 14 guard posts (dd.p)
// Rows of what can be done have "Đi tới" (map tab, walk there). The red badge on the tab counts the
// main quests that are new since the tab was last opened.
using System.Collections.Generic;
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchQuestTab : IMapTab
    {
        public int sub;
        float scroll, contentH;
        int lastSub = -1;
        readonly bool[] open = new bool[9];
        int openedFor = -1;
        int detail = -1;
        bool newThisTime;
        bool scrollToCurrent;

        // dialogue reader (read-only)
        int readQ = -1, readSet, readLine;

        string SeenKey
        {
            get { return "l12.seenT.s" + C_PortScreens.f_slot; }
        }

        public void Opened()
        {
            int t = C_PortMap.questIndex();
            int seen = PlayerPrefs.GetInt(SeenKey, -1);
            newThisTime = seen >= 0 && t > seen;
            PlayerPrefs.SetInt(SeenKey, t);
            PlayerPrefs.Save();
            scrollToCurrent = sub == 0;
        }

        public int NewCount()
        {
            int seen = PlayerPrefs.GetInt(SeenKey, -1);
            if (seen < 0)
            {
                // never opened: nothing to count yet (the first quest shows on the tracker)
                return 0;
            }
            return Mathf.Max(0, C_PortMap.questIndex() - seen);
        }

        public bool Back()
        {
            if (readQ >= 0)
            {
                readQ = -1;
                return true;
            }
            if (detail >= 0)
            {
                detail = -1;
                return true;
            }
            return false;
        }

        public void OnGUI(TouchMapView map, float top, float bottom)
        {
            float k = TouchUi.K;
            int T = C_PortMap.questIndex(), total = C_PortMap.questCount();
            int cur = QuestData.Chapter(Mathf.Min(T, total - 1));
            if (openedFor != C_PortScreens.f_slot * 1000 + cur)
            {
                openedFor = C_PortScreens.f_slot * 1000 + cur;
                for (int i = 0; i < open.Length; i++)
                {
                    open[i] = i == cur;
                }
            }
            if (sub != lastSub)
            {
                lastSub = sub;
                scroll = 0;
            }
            bool popup = detail >= 0 || readQ >= 0;
            if (popup)
            {
                Kit.Modal(new Rect(0, 0, 0, 0));
            }

            int evDone = 0, evOpen = 0, gDone = 0, gOpen = 0;
            for (int e = 0; e < C_PortMap.eventCount(); e++)
            {
                int st = C_PortMap.eventState(e);
                if (st > 0)
                {
                    evOpen++;
                }
                if (st == 2)
                {
                    evDone++;
                }
            }
            for (int n = 0; n < C_PortMap.nodeCount(); n++)
            {
                if (!C_PortMap.guardNode(n))
                {
                    continue;
                }
                int st = C_PortMap.guardState(n);
                if (st > 0)
                {
                    gOpen++;
                }
                if (st == 2)
                {
                    gDone++;
                }
            }
            string[] names = {"CHÍNH TUYẾN " + Mathf.Min(T, total) + "/" + total, "SỰ KIỆN " + evDone + "/" + evOpen, "QUÂN CANH " + gDone + "/" + gOpen};

            float listTop = top + 22;
            if (!popup)
            {
                scroll = MapUi.Scroll(scroll, contentH, bottom - listTop);
            }
            float y = listTop + 2 - scroll;
            if (sub == 0)
            {
                y = Main(map, y, T, total, listTop, bottom);
            }
            else if (sub == 1)
            {
                y = Events(map, y);
            }
            else
            {
                y = Guards(map, y, gOpen);
            }
            contentH = y + scroll - listTop;

            // sub tabs over the list
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, TouchUi.R(0, top, 0, 0).y, Screen.width, 22 * k), TouchUi.Hex(0x0A121E));
            }
            for (int i = 0; i < 3; i++)
            {
                Rect r = TouchUi.R(4 + i * 58, top + 3, 56, 16);
                if (Kit.Tap(10 + i, r))
                {
                    sub = i;
                }
                if (Kit.Paint)
                {
                    TouchUi.Button(r, i == sub ? Btn.Dark : Btn.Grey, false);
                    if (i == sub)
                    {
                        TouchUi.Fill(new Rect(r.x + k, r.yMax - 2 * k, r.width - 2 * k, 2 * k), Kit.Gold);
                    }
                    TouchUi.Label(r, names[i], 5.25f, TextAnchor.MiddleCenter, i == sub ? Kit.Gold : Kit.Cream, true);
                }
            }
            Kit.Unblock();

            if (readQ >= 0)
            {
                Reader(top, bottom);
            }
            else if (detail >= 0)
            {
                Detail(map, top, bottom, T);
            }
        }

        // ------------------------------------------------------------------------- main line

        float Main(TouchMapView map, float y, int T, int total, float listTop, float bottom)
        {
            float k = TouchUi.K;
            int curCh = QuestData.Chapter(Mathf.Min(T, total - 1));
            if (Kit.Paint)
            {
                MapUi.ExpBar(TouchUi.R(6, y + 2, 133, 7), T / (float) total);
                TouchUi.Label(TouchUi.R(142, y, 34, 11), "Chương " + (curCh + 1) + "/9", 5.75f, TextAnchor.MiddleRight, Kit.Cream, true);
            }
            y += 14;
            if (T >= total)
            {
                Rect done = TouchUi.R(4, y, 172, 40);
                if (Kit.Paint)
                {
                    Kit.Box(done, TouchUi.Hex(0x5A4010));
                    TouchUi.PixelText("ĐÃ THỐNG NHẤT GIANG SƠN!", 1, done.center.x, done.center.y, 1, true);
                }
                y += 46;
            }
            for (int c = 0; c < QuestData.Chapters.Length; c++)
            {
                int a = QuestData.ChapterStart[c], b = QuestData.ChapterStart[c + 1];
                int doneIn = Mathf.Clamp(T - a, 0, b - a);
                bool locked = a > T;
                Rect row = TouchUi.R(4, y, 172, 18);
                if (!locked && Kit.Tap(100 + c, row))
                {
                    open[c] = !open[c];
                }
                if (Kit.Paint)
                {
                    Kit.Box(row, c == curCh && T < total ? TouchUi.Hex(0x4A2049) : TouchUi.Hex(0x2A1426));
                    if (doneIn == b - a)
                    {
                        Kit.Check(row.x + 5 * k, row.y + 5 * k, Kit.Green);
                    }
                    else if (!locked)
                    {
                        Rect dot = new Rect(row.x + 4 * k, row.y + 4 * k, 10 * k, 10 * k);
                        TouchUi.Fill(dot, TouchUi.Hex(0xE02424));
                        TouchUi.Label(dot, (c + 1).ToString(), 5, TextAnchor.MiddleCenter, Color.white, true, false, false);
                    }
                    else
                    {
                        TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(row.x + 4 * k, row.y + 4 * k, 10 * k, 10 * k));
                    }
                    string title = "Chương " + (c + 1) + " · " + (locked ? "???" : QuestData.Chapters[c]);
                    TouchUi.Label(new Rect(row.x + 17 * k, row.y, 120 * k, row.height), title, 6.25f, TextAnchor.MiddleLeft, locked ? Kit.Muted : Color.white, true);
                    TouchUi.Label(new Rect(row.xMax - 40 * k, row.y, 28 * k, row.height), doneIn + "/" + (b - a), 5.75f, TextAnchor.MiddleRight, Kit.Cream);
                    if (!locked)
                    {
                        // open / closed arrow
                        float ax = row.xMax - 8 * k, ay = row.center.y;
                        for (int i = 0; i < 4; i++)
                        {
                            float w = (7 - 2 * i) * k;
                            TouchUi.Fill(open[c] ? new Rect(ax - w / 2, ay - 2 * k + i * k, w, k) : new Rect(ax - 2 * k + i * k, ay - w / 2, k, w), Kit.Cream);
                        }
                    }
                }
                y += 20;
                if (locked || !open[c])
                {
                    continue;
                }
                int shownLocked = 0;
                for (int q = a; q < b; q++)
                {
                    if (q < T)
                    {
                        y = DoneRow(q, y);
                    }
                    else if (q == T)
                    {
                        if (scrollToCurrent)
                        {
                            scrollToCurrent = false;
                            float want = (y + scroll) - listTop - (bottom - listTop) / 3;
                            scroll = Mathf.Max(0, want);
                        }
                        y = CurrentCard(map, q, y);
                    }
                    else if (shownLocked < 2)
                    {
                        shownLocked++;
                        if (Kit.Paint)
                        {
                            Rect r = TouchUi.R(10, y, 166, 19);
                            TouchUi.Fill(r, new Color(0.1f, 0.05f, 0.1f, 0.7f));
                            TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(r.x + 3 * k, r.y + 4 * k, 10 * k, 10 * k));
                            TouchUi.Label(new Rect(r.x + 17 * k, r.y + 1 * k, 140 * k, 9 * k), (q + 1) + ". ???", 6, TextAnchor.UpperLeft, Kit.Muted, true);
                            TouchUi.Label(new Rect(r.x + 17 * k, r.y + 10 * k, 140 * k, 9 * k), "Mở khi xong nhiệm vụ " + q, 5.25f, TextAnchor.UpperLeft, Kit.Muted);
                        }
                        y += 21;
                    }
                    else
                    {
                        if (Kit.Paint)
                        {
                            TouchUi.Label(TouchUi.R(10, y, 166, 9), "+ " + (b - q) + " nhiệm vụ nữa trong chương", 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                        }
                        y += 11;
                        break;
                    }
                }
                y += 4;
            }
            return y + 6;
        }

        float DoneRow(int q, float y)
        {
            float k = TouchUi.K;
            Rect r = TouchUi.R(10, y, 166, 21);
            if (Kit.Tap(200 + q, r))
            {
                detail = q;
            }
            if (Kit.Paint)
            {
                TouchUi.Fill(r, Kit.Down(200 + q) ? new Color(1, 1, 1, 0.1f) : new Color(0.16f, 0.08f, 0.16f, 0.8f));
                Kit.Check(r.x + 4 * k, r.y + 6 * k, Kit.Green);
                TouchUi.Label(new Rect(r.x + 15 * k, r.y + 1 * k, 130 * k, 9 * k), Kit.Cut((q + 1) + ". " + QuestData.Title(q), 34), 6, TextAnchor.UpperLeft, Kit.Cream, true);
                TouchUi.Label(new Rect(r.x + 15 * k, r.y + 11 * k, 130 * k, 9 * k), Kit.Cut(Kind(q), 44), 5.25f, TextAnchor.UpperLeft, Kit.Muted);
                // book: the dialogue can be read again
                Rect bk = new Rect(r.xMax - 15 * k, r.y + 4 * k, 12 * k, 12 * k);
                TouchUi.Sprite(TouchUi.Img("/book"), bk);
            }
            return y + 23;
        }

        static string Kind(int q)
        {
            int type = C_PortMap.questType(q), enemy = C_PortMap.questEnemy(q);
            string s = (type == 0 ? "Hội thoại" : (type == 1 ? "Trận hướng dẫn" : "Đánh")) + " · " + QuestData.Place(C_PortMap.questNode(q));
            if (type != 0 && enemy >= 0)
            {
                s += " · " + QuestData.Who(enemy);
            }
            return s;
        }

        float CurrentCard(TouchMapView map, int q, float y)
        {
            float k = TouchUi.K;
            Rect r = TouchUi.R(6, y, 170, 63);
            int node = C_PortMap.questNode(q);
            bool here = node == C_PortMap.playerNode() && !C_PortMap.walking();
            Rect go = TouchUi.R(124, y + 5, 48, 16);
            if (Kit.Tap(300, go))
            {
                map.ShowNode(node, true);
            }
            else if (Kit.Tap(301, r))
            {
                detail = q;
            }
            if (!Kit.Paint)
            {
                return y + 67;
            }
            TouchUi.Fill(new Rect(r.x - 1.25f * k, r.y - 1.25f * k, r.width + 2.5f * k, r.height + 2.5f * k), TouchUi.Hex(0xFF7864));
            TouchUi.Panel(r);
            TouchUi.Fill(TouchUi.R(12, y + 6, 2, 6), TouchUi.Hex(0xE02424));
            TouchUi.Fill(TouchUi.R(12, y + 13, 2, 2), TouchUi.Hex(0xE02424));
            TouchUi.Label(TouchUi.R(17, y + 5, 70, 9), (q + 1) + ". ĐANG LÀM", 6, TextAnchor.UpperLeft, TouchUi.Hex(0xFF9A8A), true);
            if (newThisTime)
            {
                Rect tag = TouchUi.R(80, y + 5, 20, 9);
                TouchUi.Fill(tag, TouchUi.Hex(0xE02424));
                TouchUi.Label(tag, "MỚI", 5, TextAnchor.MiddleCenter, Color.white, true, false, false);
            }
            Kit.FlatButton(go, here ? "BẮT ĐẦU" : "ĐI TỚI", Btn.Red, Kit.Down(300));
            int type = C_PortMap.questType(q), enemy = C_PortMap.questEnemy(q);
            int face = type != 0 && enemy >= 0 ? enemy : C_PortMap.questSpeaker(q);
            float tx = 12;
            if (face >= 0)
            {
                TouchUi.Portrait(face, TouchUi.R(11, y + 20, 30, 30));
                tx = 46;
            }
            TouchUi.Label(TouchUi.R(tx, y + 21, 168 - tx, 10), Kit.Cut(QuestData.Title(q), 30), 6.75f, TextAnchor.UpperLeft, Color.white, true);
            string subLine = type != 0 && enemy >= 0
                ? "Đánh bại " + QuestData.Who(enemy) + " · cấp " + C_PortMap.questEnemyLevel(q) + " · " + QuestData.Place(node)
                : "Gặp " + QuestData.Who(face) + " · " + QuestData.Place(node);
            TouchUi.Label(TouchUi.R(tx, y + 32, 168 - tx, 9), Kit.Cut(subLine, 42), 5.5f, TextAnchor.UpperLeft, Kit.Cream);
            float x = tx;
            x = Tag(x, y + 44, C_PortMap.questExp(q) > 0 ? "+" + C_PortMap.questExp(q) + " KN" : null, TouchUi.Hex(0x7A3A9A));
            x = Tag(x, y + 44, C_PortMap.questGold(q) > 0 ? "+" + C_PortMap.questGold(q) + " vàng" : null, TouchUi.Hex(0x9A6A10));
            Tag(x, y + 44, C_PortMap.questRelic(q) > 0 ? "+1 hộp vật phẩm" : null, TouchUi.Hex(0x10807A));
            return y + 67;
        }

        static float Tag(float x, float y, string s, Color c)
        {
            if (string.IsNullOrEmpty(s))
            {
                return x;
            }
            float w = TouchUi.TextWidth(s) + 6;
            Rect r = TouchUi.R(x, y, w, 10);
            TouchUi.Fill(r, c);
            TouchUi.Label(r, s, 5.25f, TextAnchor.MiddleCenter, Color.white, true, false, false);
            return x + w + 3;
        }

        // ------------------------------------------------------------------------- detail

        void Detail(TouchMapView map, float top, float bottom, int T)
        {
            float k = TouchUi.K;
            int q = detail;
            bool done = q < T;
            float h = 190;
            bool close;
            Rect r = MapUi.Box(top, bottom, h, out close, 400);
            float y = TouchUi.V(r.position).y;
            int node = C_PortMap.questNode(q);
            Rect go = TouchUi.R(17, y + h - 70, 146, 20), read = TouchUi.R(17, y + h - 46, 146, 20), shut = TouchUi.R(60, y + h - 23, 60, 17);
            if (!done && Kit.Tap(401, go))
            {
                detail = -1;
                map.ShowNode(node, true);
                Kit.Unblock();
                return;
            }
            if (Kit.Tap(402, read))
            {
                readQ = q;
                readSet = 0;
                readLine = 0;
            }
            if (Kit.Tap(403, shut) || close || Kit.Back())
            {
                detail = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint)
            {
                int c = QuestData.Chapter(q);
                TouchUi.Label(TouchUi.R(17, y + 6, 130, 9), "Chương " + (c + 1) + " · nhiệm vụ " + (q + 1), 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                int type = C_PortMap.questType(q), enemy = C_PortMap.questEnemy(q);
                int face = type != 0 && enemy >= 0 ? enemy : C_PortMap.questSpeaker(q);
                if (face >= 0)
                {
                    TouchUi.Portrait(face, TouchUi.R(17, y + 17, 32, 32));
                }
                TouchUi.Label(TouchUi.R(54, y + 18, 106, 20), QuestData.Title(q), 6.75f, TextAnchor.UpperLeft, Color.white, true, true);
                if (done)
                {
                    Kit.Check(TouchUi.R(54, 0, 0, 0).x, TouchUi.R(0, y + 39, 0, 0).y, Kit.Green);
                    TouchUi.Label(TouchUi.R(63, y + 38, 90, 9), "Đã hoàn thành", 6, TextAnchor.UpperLeft, Kit.Green, true);
                }
                else
                {
                    TouchUi.Label(TouchUi.R(54, y + 38, 90, 9), "! Đang làm", 6, TextAnchor.UpperLeft, TouchUi.Hex(0xFF7864), true);
                }
                float ry = y + 54;
                ry = InfoRow(ry, "Loại", type == 0 ? "Hội thoại" : (type == 1 ? "Trận hướng dẫn" : "Đánh"));
                ry = InfoRow(ry, "Nơi", QuestData.Place(node));
                if (type != 0 && enemy >= 0)
                {
                    ry = InfoRow(ry, "Đối thủ", QuestData.Who(enemy) + " · cấp " + C_PortMap.questEnemyLevel(q));
                }
                string reward = "";
                if (C_PortMap.questExp(q) > 0)
                {
                    reward += C_PortMap.questExp(q) + " KN";
                }
                if (C_PortMap.questGold(q) > 0)
                {
                    reward += (reward.Length > 0 ? " · " : "") + C_PortMap.questGold(q) + " vàng";
                }
                if (C_PortMap.questRelic(q) > 0)
                {
                    reward += (reward.Length > 0 ? " · " : "") + "hộp vật phẩm";
                }
                ry = InfoRow(ry, done ? "Đã nhận" : "Thưởng", reward.Length > 0 ? reward : "—");
                int[] opens = C_PortMap.questOpens(q);
                if (opens != null && opens.Length > 0)
                {
                    string list = "";
                    if (done)
                    {
                        List<string> names = new List<string>();
                        foreach (int n in opens)
                        {
                            if (C_PortMap.nodeType(n) == 0 && names.IndexOf(C_PortMap.nodeName(n)) < 0)
                            {
                                names.Add(C_PortMap.nodeName(n));
                            }
                        }
                        list = names.Count > 0 ? string.Join(", ", names.ToArray()) : "—";
                    }
                    else
                    {
                        list = "???";
                    }
                    InfoRow(ry, "Mở thêm", Kit.Cut(list, 30));
                }
                if (!done)
                {
                    TouchUi.RedButton(go, node == C_PortMap.playerNode() ? "BẮT ĐẦU" : "ĐI TỚI", true, Kit.Down(401));
                }
                Kit.FlatButton(read, "Đọc lại hội thoại", Btn.Dark, Kit.Down(402));
                Kit.FlatButton(shut, "Đóng", Btn.Grey, Kit.Down(403));
            }
            Kit.Unblock();
        }

        static float InfoRow(float y, string left, string right)
        {
            TouchUi.Fill(TouchUi.R(17, y + 12, 146, 0.5f), Kit.Line);
            TouchUi.Label(TouchUi.R(17, y + 1, 50, 10), left, 5.75f, TextAnchor.UpperLeft, Kit.Muted);
            TouchUi.Label(TouchUi.R(60, y + 1, 103, 10), right, 5.75f, TextAnchor.UpperRight, Color.white, true);
            return y + 13;
        }

        // ------------------------------------------------------------------------- reader

        void Reader(float top, float bottom)
        {
            float k = TouchUi.K;
            int count = C_PortMap.talkCount(readSet, readQ);
            // after the opening dialogue of a finished quest comes the one after the victory
            if (readLine >= count && readSet == 0 && readQ < C_PortMap.questIndex() && C_PortMap.talkCount(1, readQ) > 0)
            {
                readSet = 1;
                readLine = 0;
                count = C_PortMap.talkCount(1, readQ);
            }
            if (readLine >= count)
            {
                readQ = -1;
                return;
            }
            Rect all = new Rect(0, 0, Screen.width, Screen.height);
            Kit.Modal(all);
            Rect skip = TouchUi.R(130, top + 4, 44, 16);
            if (Kit.Tap(501, skip) || Kit.Back())
            {
                readQ = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
                Kit.Unblock();
                return;
            }
            if (Kit.Tap(500, all))
            {
                readLine++;
            }
            if (Kit.Paint)
            {
                TouchUi.Dim(0.8f);
                int who = C_PortMap.talkSpeaker(readSet, readQ, readLine);
                string text = C_PortMap.talkText(readSet, readQ, readLine);
                int lines = Mathf.Max(3, TouchUi.Lines(text, 150));
                float h = lines * UiFont.LineHeight + 24;
                float by = bottom - h - 8;
                if (who >= 0)
                {
                    TouchUi.Portrait(who, TouchUi.R(8, by - 66, 64, 64));
                    Rect plate = TouchUi.R(76, by - 16, 96, 13);
                    TouchUi.Fill(plate, TouchUi.Hex(0x1A0C16));
                    TouchUi.Label(plate, QuestData.Who(who), 6.25f, TextAnchor.MiddleCenter, Kit.Gold, true);
                }
                Rect box = TouchUi.R(6, by, 168, h);
                TouchUi.Panel(box);
                TouchUi.Label(TouchUi.R(15, by + 8, 150, lines * UiFont.LineHeight), text, 6.5f, TextAnchor.UpperLeft, Color.white, false, true);
                TouchUi.Label(TouchUi.R(15, by + h - 11, 150, 9), (readSet == 0 ? "Trước nhiệm vụ" : "Sau khi thắng") + " · chạm để tiếp", 5.25f, TextAnchor.UpperRight, Kit.Muted);
                Kit.FlatButton(skip, "Bỏ qua", Btn.Grey, Kit.Down(501));
            }
            Kit.Unblock();
        }

        // ------------------------------------------------------------------------- events, guards

        float Events(TouchMapView map, float y)
        {
            float k = TouchUi.K;
            int n = C_PortMap.eventCount();
            // can be done first, nearest first; then done; then not open
            List<int> order = new List<int>();
            for (int e = 0; e < n; e++)
            {
                order.Add(e);
            }
            order.Sort((a, b) =>
            {
                int sa = Rank(C_PortMap.eventState(a)), sb = Rank(C_PortMap.eventState(b));
                if (sa != sb)
                {
                    return sa.CompareTo(sb);
                }
                return Dist(C_PortMap.eventNode(a)).CompareTo(Dist(C_PortMap.eventNode(b)));
            });
            foreach (int e in order)
            {
                int st = C_PortMap.eventState(e), node = C_PortMap.eventNode(e);
                Rect r = TouchUi.R(4, y, 172, 30);
                if (st == 1 && Kit.Tap(600 + e, r))
                {
                    map.ShowNode(node, true);
                }
                if (Kit.Paint)
                {
                    int enemy = C_PortMap.eventEnemy(e);
                    bool fight = C_PortMap.eventType(e) != 0 && enemy >= 0;
                    string reward = Reward(e);
                    if (st == 1)
                    {
                        TouchUi.Fill(new Rect(r.x - k, r.y - k, r.width + 2 * k, r.height + 2 * k), TouchUi.Hex(0xFF8C1A));
                        TouchUi.Fill(r, Kit.Down(600 + e) ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x3A1838));
                        Mark(r.x + 3 * k, r.y + 9 * k, TouchUi.Hex(0xFF8C1A));
                        if (enemy >= 0)
                        {
                            TouchUi.Portrait(enemy, new Rect(r.x + 8 * k, r.y + 3 * k, 24 * k, 24 * k));
                        }
                        TouchUi.Label(new Rect(r.x + 36 * k, r.y + 3 * k, 110 * k, 9 * k), Kit.Cut((fight ? QuestData.Who(enemy) : "Gặp gỡ") + " · " + QuestData.Place(node), 30), 6.25f, TextAnchor.UpperLeft, Color.white, true);
                        TouchUi.Label(new Rect(r.x + 36 * k, r.y + 14 * k, 110 * k, 9 * k), Kit.Cut((fight ? "cấp " + C_PortMap.eventEnemyLevel(e) : "gặp gỡ") + (reward.Length > 0 ? " · " + reward : ""), 36), 5.5f, TextAnchor.UpperLeft, TouchUi.Hex(0xFFE39A));
                        TouchUi.Label(new Rect(r.xMax - 34 * k, r.y, 30 * k, r.height), "Đi tới", 6, TextAnchor.MiddleRight, Kit.Gold, true);
                    }
                    else if (st == 2)
                    {
                        TouchUi.Fill(r, new Color(0.14f, 0.08f, 0.14f, 0.7f));
                        Kit.Check(r.x + 4 * k, r.y + 11 * k, Kit.Green);
                        Color old = GUI.color;
                        GUI.color = old * new Color(1, 1, 1, 0.55f);
                        if (enemy >= 0)
                        {
                            TouchUi.Portrait(enemy, new Rect(r.x + 14 * k, r.y + 3 * k, 24 * k, 24 * k));
                        }
                        GUI.color = old;
                        TouchUi.Label(new Rect(r.x + 42 * k, r.y + 3 * k, 120 * k, 9 * k), Kit.Cut((fight ? QuestData.Who(enemy) : "Gặp gỡ") + " · " + QuestData.Place(node), 30), 6, TextAnchor.UpperLeft, Kit.Muted, true);
                        TouchUi.Label(new Rect(r.x + 42 * k, r.y + 14 * k, 120 * k, 9 * k), reward.Length > 0 ? "Đã nhận " + reward.Replace("Thưởng: ", "") : "Đã xong", 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                    }
                    else
                    {
                        TouchUi.Fill(r, new Color(0.1f, 0.05f, 0.1f, 0.7f));
                        TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(r.x + 6 * k, r.y + 8 * k, 14 * k, 14 * k));
                        TouchUi.Label(new Rect(r.x + 26 * k, r.y + 4 * k, 120 * k, 9 * k), "???", 6.25f, TextAnchor.UpperLeft, Kit.Muted, true);
                        TouchUi.Label(new Rect(r.x + 26 * k, r.y + 15 * k, 140 * k, 9 * k), "Ở vùng chưa mở trên bản đồ", 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                    }
                }
                y += 33;
            }
            // arena of Tam Điệp (node 24)
            if (C_PortMap.nodeOpen(24))
            {
                int openLv = 0;
                for (int i = 0; i < 5; i++)
                {
                    if (C_PortMap.arenaOpen(i))
                    {
                        openLv++;
                    }
                }
                y += 4;
                MapUi.Heading(y, "ĐẤU TRƯỜNG · TAM ĐIỆP", openLv + "/5 mức");
                y += 12;
                string[] need = {"mở sẵn", "mở sẵn", "thắng Lê Hoàn", "thắng Nguyễn Ninh", "đạt cấp 30"};
                for (int i = 0; i < 5; i++)
                {
                    bool o = C_PortMap.arenaOpen(i);
                    Rect r = TouchUi.R(4 + i * 35, y, 32, 24);
                    if (o && Kit.Tap(700 + i, r))
                    {
                        map.ShowNode(24, true);
                    }
                    if (Kit.Paint)
                    {
                        TouchUi.Button(r, o ? Btn.Green : Btn.Grey, Kit.Down(700 + i));
                        TouchUi.Label(new Rect(r.x, r.y + 2 * k, r.width, 9 * k), "Mức " + (i + 1), 5.75f, TextAnchor.UpperCenter, Color.white, true);
                        TouchUi.Label(new Rect(r.x + k, r.y + 11 * k, r.width - 2 * k, 12 * k), need[i], 4.75f, TextAnchor.UpperCenter, o ? TouchUi.Hex(0xE0FFD0) : Kit.Cream, false, true, false);
                    }
                }
                y += 28;
            }
            return y + 6;
        }

        static int Rank(int state)
        {
            return state == 1 ? 0 : (state == 2 ? 1 : 2);
        }

        static int Dist(int node)
        {
            int d = C_PortMap.roadsTo(node);
            return d < 0 ? 999 : d;
        }

        static string Reward(int e)
        {
            int a = C_PortMap.eventAttack(e), h = C_PortMap.eventHp(e);
            string s = "";
            if (a > 0)
            {
                s += "+" + a + " tấn công";
            }
            if (h > 0)
            {
                s += (s.Length > 0 ? ", " : "") + "+" + h + " máu";
            }
            return s;
        }

        static void Mark(float x, float y, Color c)
        {
            float k = TouchUi.K;
            TouchUi.Fill(new Rect(x, y, 2 * k, 7 * k), c);
            TouchUi.Fill(new Rect(x, y + 8 * k, 2 * k, 2 * k), c);
        }

        float Guards(TouchMapView map, float y, int openCount)
        {
            float k = TouchUi.K;
            int lockedCount = 0;
            List<int> rows = new List<int>();
            for (int n = 0; n < C_PortMap.nodeCount(); n++)
            {
                if (!C_PortMap.guardNode(n))
                {
                    continue;
                }
                if (C_PortMap.guardState(n) == 0)
                {
                    lockedCount++;
                }
                else
                {
                    rows.Add(n);
                }
            }
            rows.Sort((a, b) =>
            {
                int sa = C_PortMap.guardState(a), sb = C_PortMap.guardState(b);
                return sa != sb ? sa.CompareTo(sb) : Dist(a).CompareTo(Dist(b));
            });
            foreach (int n in rows)
            {
                int st = C_PortMap.guardState(n);
                Rect r = TouchUi.R(4, y, 172, 30);
                if (st == 1 && Kit.Tap(800 + n, r))
                {
                    map.ShowNode(n, true);
                }
                if (Kit.Paint)
                {
                    int enemy = C_PortMap.guardEnemy(n);
                    if (st == 1)
                    {
                        TouchUi.Fill(new Rect(r.x - k, r.y - k, r.width + 2 * k, r.height + 2 * k), Kit.Gold);
                        TouchUi.Fill(r, Kit.Down(800 + n) ? TouchUi.Hex(0x5A2A58) : TouchUi.Hex(0x3A1838));
                        Mark(r.x + 3 * k, r.y + 9 * k, Kit.Gold);
                        if (enemy >= 0)
                        {
                            TouchUi.Portrait(enemy, new Rect(r.x + 8 * k, r.y + 3 * k, 24 * k, 24 * k));
                        }
                        TouchUi.Label(new Rect(r.x + 36 * k, r.y + 3 * k, 110 * k, 9 * k), Kit.Cut(QuestData.Who(enemy) + " · " + QuestData.Place(n), 32), 6.25f, TextAnchor.UpperLeft, Color.white, true);
                        string lv = C_PortMap.guardScales(n) ? "cấp " + C_PortMap.heroLevel() + " (bằng cấp bạn)" : "cấp " + C_PortMap.guardLevel(n);
                        TouchUi.Label(new Rect(r.x + 36 * k, r.y + 14 * k, 110 * k, 9 * k), lv, 5.5f, TextAnchor.UpperLeft, TouchUi.Hex(0xFFE39A));
                        TouchUi.Label(new Rect(r.xMax - 34 * k, r.y, 30 * k, r.height), "Đi tới", 6, TextAnchor.MiddleRight, Kit.Gold, true);
                    }
                    else
                    {
                        TouchUi.Fill(r, new Color(0.14f, 0.08f, 0.14f, 0.7f));
                        Kit.Check(r.x + 4 * k, r.y + 11 * k, Kit.Green);
                        TouchUi.Label(new Rect(r.x + 16 * k, r.y + 3 * k, 140 * k, 9 * k), Kit.Cut(QuestData.Place(n), 34), 6, TextAnchor.UpperLeft, Kit.Muted, true);
                        TouchUi.Label(new Rect(r.x + 16 * k, r.y + 14 * k, 140 * k, 9 * k), "Đã dẹp", 5.5f, TextAnchor.UpperLeft, Kit.Muted);
                    }
                }
                y += 33;
            }
            if (lockedCount > 0 && Kit.Paint)
            {
                Rect r = TouchUi.R(4, y, 172, 18);
                TouchUi.Fill(r, new Color(0.1f, 0.05f, 0.1f, 0.7f));
                TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(r.x + 5 * k, r.y + 4 * k, 10 * k, 10 * k));
                TouchUi.Label(new Rect(r.x + 20 * k, r.y, 150 * k, r.height), "Còn " + lockedCount + " trạm gác ở vùng chưa mở", 5.75f, TextAnchor.MiddleLeft, Kit.Muted);
            }
            if (lockedCount > 0)
            {
                y += 22;
            }
            return y + 6;
        }
    }
}
