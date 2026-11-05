# Đồ họa bàn đấu nét cao (phương án A)

- `orig/` hình gốc của game (giải từ `Assets/Loan12/Resources/J2ME/*_mg.bytes`, 4 byte đầu thay bằng `\x89PNG`).
- `A/` cùng hình, gấp 4 lần:
  - ngọc, hoạt cảnh nổ, sao, tinh thể, khung vật phẩm: Scale2x 4 lần (16x), làm mượt mạnh viền ngoài (đoạn thẳng thẳng, đường cong tròn),
    làm mượt nhẹ bên trong và xóa chấm nhiễu, rồi thu về 4x (khử răng cưa); khung nổ thưa (hạt lấp lánh) chỉ làm mượt nhẹ.
    Ô lúa giữ nét lưới bên trong. Màu +8%, tương phản +4%. (`tools/smooth2.py`)
  - khung chọn (`focus1`, `focus2`, `zoomfocus1`) và vòng nổ kiếm (`explodesword`): vẽ lại bằng hình học chính xác, cùng màu gốc. (`tools/geom.py`)
  - nền bàn `bkboard` và ô nền `cell_a`, `cell_b` (cắt từ nền bàn): Scale2x 16x thu về 4x.
- `tools/boards.py` ghép bàn 8×8 mẫu.
- `report.html` trang so sánh.
