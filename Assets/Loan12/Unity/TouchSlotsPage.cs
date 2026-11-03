// Screen 2 of the touch redesign: characters (plan/thiet-ke-nhieu-nhan-vat.md 4.3-4.7).
//
//   3 slot cards (character: portrait, name, element · level, last played, trash; empty: create states
//   C/D/E/F of section 2 - only the first empty slot can be used)
//   hint line, or the "Đã đủ 3 nhân vật" band
//   CHƠI with the selected character
// Bottom sheets: "Nhân vật mới" (watch an ad for a creation credit) and "Xóa nhân vật?" (hold 2 s).
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchSlotsPage : IPage
    {
        int selected;
        int deleting = -1;
        bool adBox;
        string adError;
        float sheetAt;
        float holdStart = -1f, holdLevel;
        float flashAt = -10f;
        bool closeAfter;
        bool allGone;

        public TouchSlotsPage(bool openAdBox = false)
        {
            SlotData d = Slots.Data;
            selected = d.activeSlot >= 0 ? d.activeSlot : 0;
            if (openAdBox)
            {
                OpenAdBox();
                closeAfter = Slots.Used == 0; // opened from CHƠI MỚI on the menu
            }
        }

        void OpenAdBox()
        {
            adBox = true;
            adError = null;
            sheetAt = Time.unscaledTime;
        }

        bool Small
        {
            get { return Kit.H < 340; }
        }

        float CardH
        {
            get { return Small ? 52 : 59; }
        }

        float CardY(int i)
        {
            return (Small ? 38 : 48) + i * (CardH + 8);
        }

        public bool OnGUI(Event e)
        {
            SlotData d = Slots.Data;
            if (!d.slots[selected].used)
            {
                selected = d.activeSlot >= 0 ? d.activeSlot : 0;
            }
            bool sheet = adBox || deleting >= 0;
            bool busy = Rewards.Busy || C_PortScreens.busy();
            bool close = false;
            if (Kit.Paint)
            {
                TouchUi.Fill(new Rect(0, 0, Screen.width, Screen.height), TouchUi.Background);
                TouchUi.Wallpaper(0.55f);
            }
            if (sheet)
            {
                Kit.Modal(new Rect(0, 0, 0, 0)); // nothing below the sheet takes taps
            }
            if (Kit.TitleBar("NHÂN VẬT"))
            {
                close = true;
            }
            if (Kit.Paint)
            {
                string intro = Small ? "Lưu tối đa 3 nhân vật." : "Lưu tối đa 3 nhân vật, mỗi nhân vật có bản đồ và cấp riêng. Vàng và vật phẩm dùng chung.";
                TouchUi.Label(TouchUi.R(9, 25, 162, 20), intro, 6, TextAnchor.UpperCenter, TouchUi.Hex(0xDCD2C8), false, true);
            }

            int first = Slots.FirstEmpty();
            for (int i = 0; i < Slots.Count; i++)
            {
                Rect r = TouchUi.R(7, CardY(i), 166, CardH);
                SlotInfo s = d.slots[i];
                if (s.used)
                {
                    Rect trash = TouchUi.R(173 - 24, CardY(i), 24, 24);
                    if (Kit.Tap(30 + i, trash))
                    {
                        deleting = i;
                        holdLevel = 0;
                        sheetAt = Time.unscaledTime;
                    }
                    else if (Kit.Tap(20 + i, r))
                    {
                        selected = i;
                    }
                    if (Kit.Paint)
                    {
                        DrawCard(r, i, s, i == selected, i == d.activeSlot);
                    }
                }
                else
                {
                    bool usable = i == first;
                    if (usable && Kit.Tap(20 + i, r) && !busy)
                    {
                        if (Slots.CanCreate)
                        {
                            Slots.Create(i);
                        }
                        else
                        {
                            OpenAdBox();
                        }
                    }
                    if (Kit.Paint)
                    {
                        DrawEmpty(r, i, usable, Kit.Down(20 + i));
                    }
                }
            }

            float below = CardY(Slots.Count - 1) + CardH + 6;
            if (first < 0)
            {
                Rect band = TouchUi.R(7, below, 166, 27);
                if (Kit.Tap(40, band))
                {
                    flashAt = Time.unscaledTime;
                }
                if (Kit.Paint)
                {
                    Kit.Box(band, TouchUi.Hex(0x2A1426));
                    TouchUi.Sprite(TouchUi.Img("/m/lock"), TouchUi.R(12, below + 6, 15, 15));
                    TouchUi.Label(TouchUi.R(32, below + 3, 136, 10), "Đã đủ 3 nhân vật.", 6.5f, TextAnchor.UpperLeft, Kit.Gold, true);
                    TouchUi.Label(TouchUi.R(32, below + 13, 136, 10), "Muốn tạo nhân vật khác, hãy xóa 1 nhân vật.", 5.75f, TextAnchor.UpperLeft, Kit.Cream);
                }
            }
            else if (Kit.Paint)
            {
                TouchUi.Label(TouchUi.R(7, below, 166, 10), "Chạm vào thẻ để chọn · biểu tượng thùng rác để xóa", 5.75f, TextAnchor.UpperCenter, Kit.Muted);
            }

            // CHƠI
            Rect play = TouchUi.R(10, Kit.H - 41, 160, 35);
            bool canPlay = d.slots[selected].used;
            if (canPlay && Kit.Tap(41, play) && !busy)
            {
                Slots.Play(selected);
                close = true;
            }
            if (Kit.Paint && canPlay)
            {
                SlotInfo s = d.slots[selected];
                Kit.MainButton(play, "CHƠI", Kit.Cut(Kit.Title(s.name), 14) + " · " + Kit.ElementName(C_PortScreens.heroElement(s.hero)) + " · Cấp " + s.level, !busy, Kit.Down(41));
            }
            Kit.Unblock();

            if (adBox)
            {
                AdSheet(ref close);
            }
            else if (deleting >= 0)
            {
                DeleteSheet();
            }
            else if (Kit.Back())
            {
                close = true;
                e.Use();
            }
            Kit.DrawToast(Kit.H - 60);
            return !close && !allGone;
        }

        // ---------------------------------------------------------------------------------- cards

        void DrawCard(Rect r, int i, SlotInfo s, bool sel, bool active)
        {
            float k = TouchUi.K;
            if (sel)
            {
                TouchUi.Fill(new Rect(r.x - 1.5f * k, r.y - 1.5f * k, r.width + 3 * k, r.height + 3 * k), TouchUi.Hex(0x8BE05C));
            }
            TouchUi.Panel(r);
            float py = r.y + (r.height - 32 * k) / 2;
            TouchUi.Portrait(s.hero, new Rect(r.x + 7 * k, py, 32 * k, 32 * k));
            float tx = r.x + 46 * k;
            TouchUi.PixelText(Kit.Cut(s.name, 12), 2, tx, r.y + 6 * k, 0);
            int el = C_PortScreens.heroElement(s.hero);
            TouchUi.Label(new Rect(tx, r.y + 24 * k, 110 * k, 9 * k), "Hệ " + Kit.ElementName(el) + " · Cấp " + s.level, 6.75f, TextAnchor.UpperLeft, Kit.Cream, true);
            TouchUi.Label(new Rect(tx, r.y + 35 * k, 110 * k, 9 * k), "Chơi gần nhất: " + Slots.Ago(s.lastPlayedAt), 5.75f, TextAnchor.UpperLeft, Kit.Muted);
            if (active)
            {
                Rect tag = new Rect(r.x + 4 * k, r.yMax - 12 * k, 40 * k, 9 * k);
                TouchUi.Fill(tag, TouchUi.Hex(0x1C5A1A));
                TouchUi.Label(tag, "ĐANG CHƠI", 5, TextAnchor.MiddleCenter, Kit.Green, true, false, false);
            }
            // trash, flashing when the full band was tapped
            float f = Time.unscaledTime - flashAt;
            bool flash = f < 0.6f && ((int) (f / 0.15f)) % 2 == 0;
            Rect tr = new Rect(r.xMax - 20 * k, r.y + 4 * k, 16 * k, 18 * k);
            TouchUi.Button(tr, flash ? Btn.Gold : Btn.Dark, Kit.Down(30 + i));
            Color c = Kit.Cream;
            TouchUi.Fill(new Rect(tr.x + 4 * k, tr.y + 4 * k, 8 * k, k), c);
            TouchUi.Fill(new Rect(tr.x + 6 * k, tr.y + 3 * k, 4 * k, k), c);
            TouchUi.Fill(new Rect(tr.x + 5 * k, tr.y + 6 * k, 6 * k, 8 * k), c);
            TouchUi.Fill(new Rect(tr.x + 7 * k, tr.y + 7 * k, k, 6 * k), TouchUi.Hex(0x3A1838));
            TouchUi.Fill(new Rect(tr.x + 9 * k, tr.y + 7 * k, k, 6 * k), TouchUi.Hex(0x3A1838));
        }

        void DrawEmpty(Rect r, int i, bool usable, bool down)
        {
            float k = TouchUi.K;
            Color old = GUI.color;
            if (!usable)
            {
                GUI.color = old * new Color(1, 1, 1, 0.5f);
            }
            TouchUi.Fill(r, new Color(0.1f, 0.04f, 0.1f, 0.8f));
            bool credit = Slots.CanCreate && !Rewards.Vip || Slots.Data.createCredits > 0;
            // dashed border: gold when a credit is there, grey when locked
            Color dash = credit || Rewards.Vip ? TouchUi.GoldLine : TouchUi.Hex(0x6E6070);
            for (float x = r.x; x < r.xMax; x += 6 * k)
            {
                TouchUi.Fill(new Rect(x, r.y, 3 * k, k), dash);
                TouchUi.Fill(new Rect(x, r.yMax - k, 3 * k, k), dash);
            }
            for (float y = r.y; y < r.yMax; y += 6 * k)
            {
                TouchUi.Fill(new Rect(r.x, y, k, 3 * k), dash);
                TouchUi.Fill(new Rect(r.xMax - k, y, k, 3 * k), dash);
            }
            Rect icon = new Rect(r.x + 9 * k, r.y + (r.height - 30 * k) / 2, 30 * k, 30 * k);
            Kit.Box(icon, TouchUi.Hex(0x22101E));
            if (credit || Rewards.Vip)
            {
                TouchUi.Fill(new Rect(icon.center.x - k, icon.y + 8 * k, 2 * k, 14 * k), TouchUi.GoldLine);
                TouchUi.Fill(new Rect(icon.x + 8 * k, icon.center.y - k, 14 * k, 2 * k), TouchUi.GoldLine);
            }
            else
            {
                TouchUi.Sprite(TouchUi.Img("/m/lock"), new Rect(icon.x + 7 * k, icon.y + 7 * k, 16 * k, 16 * k));
            }
            TouchUi.Label(new Rect(r.x + 46 * k, r.y + 6 * k, 110 * k, 9 * k), "Ô " + (i + 1) + " · trống", 6.5f, TextAnchor.UpperLeft, Kit.Muted, true);
            Rect b = new Rect(r.x + 46 * k, r.y + r.height - 25 * k, 112 * k, 18 * k);
            if (Rewards.Vip && Slots.Data.createCredits == 0)
            {
                Kit.FlatButton(b, "Tạo ngay (VIP)", Btn.Gold, down);
            }
            else if (credit)
            {
                TouchUi.Button(b, Btn.Red, down);
                TouchUi.PixelText("TẠO NHÂN VẬT", 2, b.center.x, b.center.y + (down ? k : 0), 1, true);
            }
            else if (Rewards.Ready)
            {
                Kit.AdButton(b, "Xem QC để tạo", down);
            }
            else
            {
                Kit.AdButton(b, "Đang tải quảng cáo…", false, false);
            }
            GUI.color = old;
        }

        // ---------------------------------------------------------------------------------- sheets

        static float Slide(float at)
        {
            return 1f - Mathf.Clamp01((Time.unscaledTime - at) / 0.15f);
        }

        void AdSheet(ref bool closePage)
        {
            float k = TouchUi.K;
            float h = 150, y = Kit.H - h + Slide(sheetAt) * h;
            Rect sheet = TouchUi.R(0, y, 180, h + 4);
            Kit.Modal(sheet);
            int have = Slots.Used;
            Rect watch = TouchUi.R(15, y + 84, 150, 24), later = TouchUi.R(45, y + 113, 90, 18), vip = TouchUi.R(10, y + 134, 160, 10);
            bool busy = Rewards.Busy;
            if (!busy && Kit.Tap(50, watch))
            {
                adError = null;
                Rewards.Show("slot_create", result =>
                {
                    if (result == 1)
                    {
                        Slots.GrantCredit();
                        adBox = false;
                        int n = Slots.FirstEmpty();
                        if (n >= 0)
                        {
                            Slots.Create(n);
                        }
                    }
                    else
                    {
                        adError = result == 0 ? "Cần xem hết quảng cáo để nhận lượt tạo" : "Chưa có quảng cáo, thử lại sau ít phút";
                    }
                });
            }
            bool dismiss = !busy && (Kit.Tap(51, later) || Kit.Outside() || Kit.Back());
            if (!busy && !Rewards.Vip && Kit.Tap(52, vip) && TouchMenuView.Instance != null)
            {
                adBox = false;
                TouchMenuView.Instance.OpenShop(2);
            }
            if (dismiss)
            {
                adBox = false;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
                if (closeAfter)
                {
                    closePage = true;
                }
            }
            if (Kit.Paint)
            {
                TouchUi.Dim(0.65f * (1 - Slide(sheetAt)));
                TouchUi.Panel(sheet);
                TouchUi.PixelText("NHÂN VẬT MỚI", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, y + 12, 0, 0).y, 1, true);
                for (int i = 0; i < 6; i++)
                {
                    TouchUi.Portrait(i, TouchUi.R(18 + i * 24.5f, y + 22, 21, 21));
                }
                string word = have == 1 ? "Một" : "Hai";
                string text = "Xem 1 quảng cáo ngắn để mở lượt tạo nhân vật thứ " + (have + 1) + "." + (have > 0 ? " " + word + " nhân vật hiện có giữ nguyên." : "");
                TouchUi.Label(TouchUi.R(12, y + 48, 156, 30), text, 6.25f, TextAnchor.UpperCenter, Kit.Cream, false, true);
                if (adError != null)
                {
                    TouchUi.Label(TouchUi.R(12, y + 72, 156, 10), adError, 5.75f, TextAnchor.UpperCenter, Kit.Red, true);
                }
                if (busy)
                {
                    Kit.FlatButton(watch, "", Btn.Green, false);
                    Kit.Spinner(watch.center);
                }
                else
                {
                    Kit.AdButton(watch, "XEM QUẢNG CÁO", Kit.Down(50));
                }
                Kit.FlatButton(later, "Để sau", Btn.Grey, Kit.Down(51));
                if (!Rewards.Vip)
                {
                    TouchUi.Label(vip, "VIP: tạo ngay, không cần xem quảng cáo", 5.75f, TextAnchor.MiddleCenter, TouchUi.Hex(0xFFE39A));
                }
            }
            Kit.Unblock();
        }

        void DeleteSheet()
        {
            float k = TouchUi.K;
            SlotInfo s = Slots.Data.slots[deleting];
            int relics = 0;
            try
            {
                relics = C_PortScreens.slotRelics(deleting);
            }
            catch (System.Exception)
            {
            }
            float h = relics > 0 ? 166 : 156, y = Kit.H - h + Slide(sheetAt) * h;
            Rect sheet = TouchUi.R(0, y, 180, h + 4);
            Kit.Modal(sheet);
            float lossH = relics > 0 ? 40 : 30;
            Rect hold = TouchUi.R(12, y + 72 + lossH, 156, 24), cancel = TouchUi.R(55, y + 100 + lossH, 70, 18);

            // hold 2 s to delete; releasing early runs the bar back in 0.2 s
            bool held = false;
            if (Kit.Tap(60, hold))
            {
                // released: not long enough
            }
            if (Kit.Held(60) && hold.Contains(Kit.E.mousePosition))
            {
                held = true;
            }
            if (Kit.E.type == EventType.Repaint)
            {
                float dt = Time.unscaledDeltaTime;
                holdLevel = held ? holdLevel + dt / 2f : Mathf.Max(0, holdLevel - dt / 0.2f);
                if (holdLevel >= 1f)
                {
                    string name = Kit.Title(s.name);
                    Haptics.Tap(Kit.Haptic);
                    Slots.Delete(deleting);
                    deleting = -1;
                    holdLevel = 0;
                    Kit.Toast("Đã xóa " + name);
                    allGone = Slots.Used == 0; // no character left: back to the menu (CHƠI MỚI)
                    selected = Slots.Data.activeSlot >= 0 ? Slots.Data.activeSlot : 0;
                    Kit.Unblock();
                    return;
                }
            }
            if (Kit.Tap(61, cancel) || Kit.Outside() || Kit.Back())
            {
                deleting = -1;
                if (Kit.E.type == EventType.KeyDown)
                {
                    Kit.E.Use();
                }
            }
            if (Kit.Paint && deleting >= 0)
            {
                TouchUi.Dim(0.65f * (1 - Slide(sheetAt)));
                TouchUi.Panel(sheet);
                TouchUi.PixelText("XÓA NHÂN VẬT?", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, y + 12, 0, 0).y, 1, true);
                TouchUi.Portrait(s.hero, TouchUi.R(14, y + 24, 32, 32));
                TouchUi.PixelText(Kit.Cut(s.name, 12), 2, TouchUi.R(52, 0, 0, 0).x, TouchUi.R(0, y + 26, 0, 0).y, 0);
                TouchUi.Label(TouchUi.R(52, y + 43, 120, 9), "Hệ " + Kit.ElementName(C_PortScreens.heroElement(s.hero)) + " · Cấp " + s.level, 6.5f, TextAnchor.UpperLeft, Kit.Cream, true);
                Rect loss = TouchUi.R(12, y + 60, 156, lossH);
                TouchUi.Fill(loss, TouchUi.Hex(0xE07070));
                TouchUi.Fill(new Rect(loss.x + k, loss.y + k, loss.width - 2 * k, loss.height - 2 * k), TouchUi.Hex(0x341428));
                TouchUi.Label(TouchUi.R(18, y + 62, 150, 9), "Sẽ mất vĩnh viễn:", 6, TextAnchor.UpperLeft, TouchUi.Hex(0xFF9A8A), true);
                TouchUi.Label(TouchUi.R(18, y + 71, 150, 9), "· Tiến độ bản đồ và nhiệm vụ", 5.75f, TextAnchor.UpperLeft, Kit.Cream);
                TouchUi.Label(TouchUi.R(18, y + 80, 150, 9), "· Cấp " + s.level + " và kinh nghiệm", 5.75f, TextAnchor.UpperLeft, Kit.Cream);
                if (relics > 0)
                {
                    TouchUi.Label(TouchUi.R(18, y + 89, 150, 9), "· " + relics + " bảo vật đã sưu tập", 5.75f, TextAnchor.UpperLeft, Kit.Cream);
                }
                TouchUi.Label(TouchUi.R(12, y + 61 + lossH, 156, 9), "Vàng, vật phẩm, mạng chơi và VIP vẫn giữ nguyên.", 5.5f, TextAnchor.UpperCenter, Kit.Muted);
                TouchUi.Button(hold, Btn.Red, false);
                TouchUi.Fill(new Rect(hold.x + k, hold.y + k, (hold.width - 2 * k) * holdLevel, hold.height - 2 * k), TouchUi.Hex(0xFF7A5C));
                TouchUi.PixelText("GIỮ 2 GIÂY ĐỂ XÓA", 2, hold.center.x, hold.center.y, 1, true);
                Kit.FlatButton(cancel, "Hủy", Btn.Grey, Kit.Down(61));
            }
            Kit.Unblock();
        }
    }
}
