# Đồ họa bàn đấu nét cao (phương án A)

- `orig/` hình gốc của game (giải từ `Assets/Loan12/Resources/J2ME/*_mg.bytes`, 4 byte đầu thay bằng `\x89PNG`).
- `A/` cùng hình, gấp 4 lần: phóng 16 lần bằng Scale2x rồi thu về 4 lần (khử răng cưa), tăng nhẹ màu (+8%) và tương phản (+4%).
  Ô lúa giữ nét lưới bên trong (chỉ lấy viền đã làm mịn). `cell_a`, `cell_b` là ô nền cắt từ `bkboard`.
- `tools/` script tạo lại: `scale_aa.py A 4 <tên...>` rồi `finish.py A <tên...>`; `boards.py` ghép bàn 8×8 mẫu.
- `report.html` trang so sánh.
