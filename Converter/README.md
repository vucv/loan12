# Converter — từ mã dịch ngược đến dự án Unity

Bộ công cụ đã tạo ra `Assets/Loan12/Game` và `Assets/Loan12/J2ME/MIDP`. Chạy lại khi muốn sửa game ở mức mã Java
rồi sinh lại C#. Unity không đọc thư mục này (nằm ngoài `Assets/`).

## Yêu cầu

- JDK 17+ (đã dùng JDK 21): `javac` / `java` (bộ dịch dùng API nội bộ của javac).
- Python 3 (Pillow chỉ cần cho `sheet.py`, `mg2png.py`, `make_fonts.py`).
- Tuỳ chọn: PowerShell 7 (`pwsh`, biến `PWSH`) để biên dịch kiểm tra C# bằng Roslyn có sẵn trong PowerShell và chạy test C#.
- Mã dịch ngược gốc: nhánh `backup` của repo — `git clone -b backup https://github.com/vucv/loan12 decompiled`
  (hoặc đặt biến `J2CS_SRC` tới thư mục chứa mã đó).

## Chạy toàn bộ

```
cd Converter
git clone -b backup https://github.com/vucv/loan12 decompiled   # mã dịch ngược gốc
scripts/convert_all.sh            # cập nhật dự án Unity chứa thư mục Converter
```

Các bước (mỗi bước là một script):

| Bước | Script | Kết quả |
|---|---|---|
| 1 | `build_tools.sh` | biên dịch công cụ + lớp MIDP Java (`midp/src`) |
| 2 | `prepare_java.sh` | `jsrc/`: mã Java đã sửa lỗi dịch ngược và port (đánh dấu `PORT-FIX` / `PORT`) |
| 3 | `build_java.sh` | biên dịch game Java + bộ chạy thử (phải 0 lỗi) |
| 4 | `j2cs.sh` | `cs/J2ME`, `cs/Game`: C# sinh từ Java (`tools/J2CS.java`) |
| 5 | `cs_build.sh` | biên dịch C# (Roslyn, C# 9) cùng `cs/Runtime` và bộ chạy thử C# |
| 6 | `make_unity_project.py` | chép C#, tài nguyên JAR, font, âm thanh vào `Assets/Loan12` |

Bước 2 gồm: `fix_syntax.py` (biểu thức / mảng hỏng), `ScopeFix` (biến cục bộ sai phạm vi), `ObscureFix` (tên lớp bị
trường cùng tên che), `fix_manual.py` (sửa từng chỗ, có giải thích), `port_patches.py` (thread → bộ lập lịch, đồng
hồ / thuộc tính hệ thống qua Host, SMS, URL, một lỗi gốc của game).

## Kiểm thử (chạy không màn hình, so sánh Java ↔ C#)

```
scripts/run.sh    harness/scripts/s4.txt runs/j4     # bản Java (giả lập MIDP Java)
scripts/cs_run.sh harness/scripts/s4.txt runs/c4     # bản C# đã dịch
python3 scripts/cmp_frames.py runs/j4 runs/c4        # so từng pixel các ảnh chụp
```

Ngôn ngữ kịch bản: `wait ms`, `key mã...` (mã phím Nokia: -1..-4 hướng, -5 OK, -6/-7 phím mềm, 48–57 số),
`tap x y`, `shot tên`, `burst tên n ms`, `text chuỗi` (TextBox), `cmd nhãn` (lệnh MIDP), `stores`,
`monkey seed bước chụp_mỗi [win]` (bấm ngẫu nhiên; `win`: bot đi nước hợp lệ và để đối thủ còn 1 máu để đi
sâu vào các màn sau). Đồng hồ là ảo (20 ms mỗi tick) nên hai bản cho kết quả giống hệt nhau.

## Thư mục

```
midp/src/        lớp giả lập MIDP 2.0 viết bằng Java 1.4 (nguồn của Assets/Loan12/J2ME/MIDP)
cs/Runtime/      thay thế java.* viết tay bằng C# (nguồn của Assets/Loan12/J2ME/Java)
jsrc/            mã Java game sau khi sửa (sinh ở bước 2, để đọc / so sánh)
tools/           J2CS.java (bộ dịch), ScopeFix / ObscureFix / OverloadCheck, cs/cscheck.ps1, cs/UnityStubs
harness/         bộ chạy thử Java (src/) và C# (cs/), kịch bản test (scripts/)
assets/          font bitmap cho Font MIDP (j2me/), âm thanh OGG (ogg/), ảnh .mg giải ra PNG để xem (png/)
scripts/         các bước trên + công cụ tài nguyên: mg2png.py, make_fonts.py, MidiRender.java, encode_audio.sh
```

Âm thanh: `MidiRender.java` render MIDI bằng bộ tổng hợp Gervill của JDK với soundfont GeneralUser GS
(tải riêng), `encode_audio.sh` chuẩn hoá âm lượng và mã hoá OGG (ffmpeg); AMR được giải mã bằng ffmpeg.
