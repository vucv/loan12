# Ghi chú kỹ thuật: port Loạn 12 Sứ Quân (J2ME → Unity)

## 1. Hướng tiếp cận

Không viết lại game. Mã game gốc (dịch ngược từ JAR bằng JD-Core 0.7) được **sửa cho đúng và chạy được**, rồi
**dịch tự động sang C#**; phần còn thiếu là nền tảng MIDP 2.0 của điện thoại, được viết lại:

```
JAR dịch ngược (JD-Core) ─► sửa lỗi dịch ngược ─► thay đổi khi port ─► Java chạy trên giả lập MIDP (kiểm thử)
                                                                     └► J2CS (javac) ─► C# ─► Unity
```

- **Giả lập MIDP** (`Converter/midp/src`, Java 1.4 thuần, dịch sang `Assets/Loan12/J2ME/MIDP`): Display, Canvas /
  GameCanvas (phím, con trỏ, repaint), Graphics phần mềm trên mảng ARGB (clip, anchor, drawRegion với 8 phép
  xoay/lật, tam giác, cung, drawRGB, copyArea), Image (bộ giải PNG + inflate tự viết), Font (font bitmap tạo từ
  DejaVu Sans, đủ chữ tiếng Việt game dùng), RecordStore, Player / Manager (MIDI, AMR → backend), TextBox / Form,
  Command, MessageConnection (SMS giả), PushRegistry.
- **Đơn luồng**: game gốc chạy nhiều thread (vòng lặp game 40 ms, splash, pool worker `ci`, AI, gửi SMS). Bản port
  chuyển thành tác vụ của bộ lập lịch `J2ME.Host` (post / postDelayed / startLoop); Unity gọi `Host.tick()` mỗi
  frame: lệnh → phím / chạm → tác vụ đến hạn → âm thanh → vẽ. Không có khoá, không có race.
- **Backend** (`J2ME.Backend`): mọi thứ phụ thuộc nền tảng (tài nguyên, đồng hồ, lưu trữ, âm thanh, URL, rung,
  log). `UnityBackend` cho Unity; bộ test dùng backend không màn hình với đồng hồ ảo.

## 2. Lỗi dịch ngược đã sửa (`PORT-FIX` trong mã)

JD-Core 0.7 sinh mã không biên dịch được (~700 lỗi javac) và có những lỗi âm thầm làm sai game:

| Loại | Ví dụ / hậu quả | Cách sửa |
|---|---|---|
| Biểu thức hỏng | `( << 7 \| i >>> 25)`, `= =`, `=.;`, `00040`, `03`, `0 - 1` | `fix_syntax.py`, có ghi chú từng chỗ |
| **Mất phần tử 0 / null ở cuối mảng khởi tạo** | đệm MD5 `{-128}` thay vì 64 byte → kiểm tra bản quyền sai → "Thiết bị không phù hợp để chơi game"; bảng sin thiếu sin(180°) → tràn mảng; bảng bản đồ 41 nút, hàng quân địch, kịch bản hội thoại 46 màn, font, khung hình tia sét, nhân lọc mờ | khôi phục theo kích thước mà mã sử dụng |
| `null` in thành `0` | bảng String[][] / int[][] | `fix_syntax.py` |
| Phạm vi biến cục bộ sai | 61 biến khai báo trong khối nhưng dùng ngoài khối | `ScopeFix` (51 chỗ) + sửa tay (đưa khai báo lên) |
| Tên lớp bị che bởi trường cùng tên | `y.a(...)` khi lớp có trường `y` | `ObscureFix` (149 chỗ ghi đầy đủ tên gói) |
| Mất ép kiểu chọn overload | `a(null)`, `a(this)` gọi nhầm accessor tĩnh tổng hợp → NPE khi mở màn hình | `fix_manual.py` (`(h) null` = không thanh phím mềm, `(de) null` = xoá phím mềm, `(b) this` = listener) |
| Mất chuyển đổi String, boolean thành int, local kiểu Object, thiếu `throws` | lỗi biên dịch / logic | `fix_manual.py` (68 sửa, có ghi chú) |

Kiểm chứng: MD5 tự viết của game so với JDK; bảng được khôi phục theo chỉ số mà mã truy cập; chạy thử toàn bộ.

## 3. Thay đổi khi port (`PORT` trong mã)

- Thread → tác vụ lập lịch (`PortTask`, `PortAiStep`; vòng lặp game gọi mỗi 40 ms như cũ; AI nghỉ 1 s giữa các bước).
- `System.currentTimeMillis()` / `System.getProperty()` → `Host.now()` / `Host.property()` (Unity: đồng hồ thật).
- Tài nguyên: `getResourceAsStream` → `j2me.Res.open` (giữ đường dẫn để backend âm thanh tìm file OGG).
- SMS: không gửi; luồng mua hàng coi như thành công (bản gốc cũng không chờ kết quả gửi). Không nhận SMS.
- `platformRequest(url)`: mở trình duyệt, game không thoát như trên điện thoại.
- **Lỗi gốc của game**: trong cửa hàng, chạm đúng hàng pixel cuối của lưới vật phẩm cho chỉ số 6–8 / 6 → lỗi mỗi
  lần vẽ (màn hình đứng). Sửa `<=` thành `<` (chỉ ảnh hưởng điều khiển cảm ứng).
- Ngoại lệ trong `paint()` được bắt và ghi log (như thread sự kiện của điện thoại), log lặp lại được thưa dần.

## 4. Bộ dịch Java → C# (`Converter/tools/J2CS.java`)

Dùng cây cú pháp **đã phân tích kiểu của javac**, nên mỗi tên, overload, hằng số, chuyển kiểu đều đúng như javac hiểu:

- Tên: lớp `game.xx` → `C_xx`, `suquan.xx` → `SQ_xx`, `mg...main.xx` → `MG_xx` (namespace `Loan12`); trường game →
  `f_tên` (Java cho phép trường và phương thức trùng tên, C# thì không); MIDP → namespace `J2ME`.
- `virtual` / `override` / `abstract` / `new` tính theo quan hệ override thật của Java; hằng giao diện → lớp `XConst`;
  khởi tạo tĩnh → static constructor; khởi tạo instance → `__init_X()` gọi sau constructor lớp cha (đúng thứ tự Java).
- Số học: `byte` → `sbyte`; ép kiểu thu hẹp ngầm của `+=` / `-=`…; `>>>` → dịch không dấu; hằng số gập sẵn
  (C# báo lỗi tràn hằng); `char` ↔ `int`; chia, dịch, tràn số giữ ngữ nghĩa Java (unchecked).
- Điều khiển: rơi qua `case` → `goto case`; `break` / `continue` có nhãn → `goto`; mỗi biến cục bộ có tên duy nhất
  trong phương thức (luật phạm vi của C# chặt hơn).
- Gọi hàm: thêm ép kiểu cho `null` và đối số khi có overload; ép người nhận về lớp khai báo khi lớp con có phương
  thức cùng tên (C# bỏ qua overload của lớp cha trong trường hợp đó, Java thì không).
- `String` / `StringBuffer` → `string` / `StringBuilder` + phương thức mở rộng tên Java (`substring`, `indexOf` so sánh
  ordinal, `trim` kiểu Java…); ghép chuỗi với `bool` / đối tượng đi qua `JString.valueOf` ("true", "null" như Java).
- `java.util.Random` viết lại đúng thuật toán LCG 48-bit của Java (cùng seed → cùng dãy số).
- Khác biệt còn lại (không ảnh hưởng game theo kiểm thử): `==` giữa hai `string` trong C# so nội dung; nối chuỗi
  `null` cho "" thay vì "null"; ép double → int ngoài phạm vi không bão hoà như Java.

## 5. Kiểm chứng

- Mã Java sau sửa: **0 lỗi** javac; chạy trên giả lập MIDP Java qua các màn hình: splash, âm thanh, menu, nhập tên,
  cốt truyện, bản đồ, cửa hàng, bộ sưu tập, đấu trường, trận đấu (người và AI), thắng / thua, lên cấp, lưu game.
- Mã C#: Roslyn biên dịch **0 lỗi** (C# 9 và C# 7.3), phần gắn Unity được kiểm tra với bộ stub API Unity
  (`Converter/tools/cs/UnityStubs`) — chưa được chạy trong Unity Editor thật trong môi trường chuyển đổi.
- **So sánh vi sai Java ↔ C#** (cùng kịch bản, đồng hồ ảo): 5 kịch bản tay (62 ảnh), 9 lần "monkey" bấm ngẫu nhiên
  (~1.000 ảnh) và 8 lần có bot đi nước hợp lệ (~1.200 ảnh, gần 500 trận thắng, đi qua nhiều màn của bản đồ):
  **mọi ảnh chụp trùng từng pixel** và chuỗi sự kiện âm thanh giống hệt.
- Ngoại lệ còn thấy đều có ở bản gốc và được game tự bắt: ảnh tên rỗng `"/.mg"` trong bảng ảnh của bàn cờ;
  RecordStore chưa tồn tại ở lần chạy đầu.

## 6. Tài nguyên

- `.mg` = PNG với 4 byte đầu thay bằng độ dài; giữ nguyên byte gốc (`Resources/J2ME/*_mg.bytes`), game tự giải mã.
- Âm thanh: 6 bản MIDI render bằng Gervill + GeneralUser GS (bản lặp được nối mượt), 14 hiệu ứng AMR-NB giải mã bằng
  ffmpeg; chuẩn hoá âm lượng, OGG Vorbis. Bốn bản nhạc dài được import dạng streaming.
- Font hệ thống MIDP (nhỏ / vừa / lớn) là font bitmap tạo từ DejaVu Sans; font chữ trong game là ảnh của game.

## 7. Hạn chế đã biết

- Âm sắc nhạc khác loa điện thoại (soundfont khác).
- Không có SMS / mạng; các nút "tải game", "chat" chỉ mở trình duyệt tới địa chỉ gốc (có thể không còn hoạt động).
- Điều khiển đa chạm không cần thiết (game theo lượt); bàn phím ảo là một chạm.
- Trạng thái game nằm trong biến static: cần *Reload Domain* khi Play trong editor; muốn "khởi động lại" game trong
  lúc chạy thì nạp lại domain (thoát Play / mở lại app).
