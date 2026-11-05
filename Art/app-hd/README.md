# Đồ họa nét cao toàn app (bước 1: hình nguồn)

Bàn đấu ở `Art/board-hd`. Thư mục này là các nhóm còn lại, đều phóng 4 lần từ hình gốc của game
(`Assets/Loan12/Resources/J2ME`, 4 byte đầu thay bằng `\x89PNG`). Tên `m__x` là hình `/m/x`, `r__x` là `/r/x`.

| Nhóm | Thư mục | Cách làm |
|---|---|---|
| Hiệu ứng chiêu và biểu tượng chiêu | `skill/` | dải hoạt cảnh: từng khung, viền mượt; biểu tượng là tranh (2 mức) |
| Chân dung | `portrait/` | tranh (2 mức) |
| Bản đồ | `map/` | bản đồ lớn, khung sự kiện: tranh (2 mức); biểu tượng: viền mượt; mũi tên chấm xen kẽ thành lớp bán trong suốt |
| Vật phẩm, phần thưởng, thanh | `items/` | viền mượt |
| Nền, chữ lớn | `screens/` | nền: tranh (2 mức); chữ THẮNG / THUA, tiêu đề, góc khung: viền mượt |

Tranh có 2 mức: `.light.png` (nhẹ: chỉ khử răng cưa) và `.medium.png` (vừa: làm mượt vùng màu).
`orig/` là hình gốc. Tạo lại: `tools/allhd.py <thư mục ra>` (cần `tools/smooth2.py`, hình gốc ở `all/<tên>.png`).
`report.html` là trang so sánh.
