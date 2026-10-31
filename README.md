# Loạn 12 Sứ Quân — bản Unity

Dự án Unity chạy game J2ME **Loạn 12 Sứ Quân** (MIDlet `com.mic.smsgame.suquan.SQMidlet`, MIDP 2.0, màn hình 240×320) được chuyển từ mã dịch ngược của file JAR. Mã dịch ngược gốc (Java + tài nguyên JAR) được giữ nguyên ở nhánh **`backup`**.

Logic game là **mã gốc của game**, được sửa lỗi dịch ngược rồi dịch tự động Java → C#, chạy trên một lớp giả lập MIDP 2.0 viết lại cho Unity. Vì vậy gameplay, hình ảnh, chữ, lưu game… giống bản điện thoại. Màn hình 240×320 được vẽ ra texture và phóng to theo màn hình máy.

## Mở dự án

1. Unity **2021.3 LTS trở lên** (đã kiểm tra biên dịch được với C# 7.3 và C# 9, tức là từ Unity 2019.4). Không cần package nào thêm.
2. Unity Hub → *Add project from disk* → chọn thư mục này.
3. Lần mở đầu tiên, script editor tự tạo scene `Assets/Loan12/Scenes/Loan12.unity`, đưa vào Build Settings và chỉnh màn hình dọc. Bấm **Play** là chạy.
   (Nếu chưa có scene thì Play ở scene bất kỳ cũng chạy: đối tượng `Loan12 J2ME Host` tự được tạo.)

Menu **Loan12** trong editor: mở / tạo lại scene, áp dụng lại Player Settings, xoá save game.

## Điều khiển

| Phím điện thoại | Bàn phím |
|---|---|
| Lên / xuống / trái / phải | Mũi tên hoặc W / S / A / D |
| OK (giữa) | Enter hoặc Space |
| Phím mềm trái | Q hoặc F1 |
| Phím mềm phải (trở về) | E, F2, Esc, Backspace (nút Back trên Android) |
| Số 0–9 | 0–9 (hàng số hoặc numpad) |
| `*` / `#` | Z / X (hoặc `*` `/` trên numpad) |

- Chuột / chạm: game có hỗ trợ màn hình cảm ứng (chạm ô để chọn và đổi ô, chạm phím mềm ở đáy màn hình).
- Trên Android / iOS hiện thêm bàn phím ảo (phím mềm, D-pad, OK, `*`, `#`). Chỉnh ở component `J2ME Game Host` → *Keypad*: Auto / Always / Never. Có thêm tuỳ chọn phóng to theo số nguyên, lọc Point / Bilinear, âm lượng.
- Ô nhập tên nhân vật (TextBox của MIDP) hiện thành ô nhập của Unity, các lệnh (Xong / Trở về) thành nút bấm.

## Cấu trúc

```
Assets/Loan12/
  Game/          168 file C# — mã game, dịch tự động từ Java (namespace Loan12; lớp C_xx, SQ_xx, MG_xx)
  J2ME/MIDP/     59 file — lớp giả lập MIDP 2.0 (lcdui, Canvas, Graphics, Image/PNG, RMS, media...), dịch tự động
  J2ME/Java/     thay thế java.lang / java.util / java.io (String, Vector, Random đúng thuật toán Java...)
  Unity/         J2MEGameHost (vòng lặp, vẽ, nhập liệu, TextBox), UnityBackend (tài nguyên, âm thanh, lưu game)
  Editor/        tạo scene, Player Settings, import nhạc dạng streaming
  Resources/J2ME tài nguyên gốc của JAR (".mg" → "_mg.bytes"), font, âm thanh đã chuyển sang OGG (clips/)
Converter/       công cụ chuyển đổi + bộ test (xem Converter/README.md)
PORTING_NOTES.md chi tiết kỹ thuật: lỗi dịch ngược đã sửa, các thay đổi khi port, cách kiểm chứng
```

Bốn assembly (asmdef): `Loan12.J2ME` và `Loan12.Game` không phụ thuộc UnityEngine; `Loan12.Unity` là phần gắn với Unity; `Loan12.Editor` chỉ có trong editor.

## Lưu ý

- **Save game** nằm trong PlayerPrefs (khoá `J2ME.rms.*`), tương đương RecordStore trên điện thoại.
- **SMS**: bản gốc mua vật phẩm / mạng chơi qua tin nhắn tính phí. Bản port không gửi SMS: các giao dịch được coi là thành công. Liên kết web (free.ola.vn…) mở bằng trình duyệt.
- **Âm thanh**: nhạc MIDI và hiệu ứng AMR gốc được chuyển sang OGG (MIDI render bằng soundfont GeneralUser GS), nên tiếng nhạc khác đôi chút so với loa điện thoại.
- Trong editor nên để bật *Reload Domain* (Enter Play Mode Options) vì game giữ trạng thái trong biến static như bản Java.
- Không sửa tay file trong `Game/` và `J2ME/MIDP/` nếu còn muốn chạy lại bộ chuyển đổi; sửa ở mã Java trong `Converter/` rồi sinh lại.
