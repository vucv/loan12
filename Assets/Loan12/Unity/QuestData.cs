// New strings of the map and quest tabs (plan/thiet-ke-ban-do.md, appendix A and 6.2): a title for
// each of the 46 main quests and the names of the 9 chapters. Everything else comes from the game.
namespace Loan12.UnityPort
{
    public static class QuestData
    {
        public static readonly string[] Titles =
        {
            "Học binh pháp với Lưu Cơ", "Nhận lệnh hộ tống chủ tướng", "Hộ tống chủ tướng về Kỳ Bố", "Bàn việc xưng hiệu",
            "Dẹp sơn tặc ở Tam Điệp", "Dự đại lễ xưng Vạn Thắng Vương", "Kết hòa ước với Phạm Bạch Hổ", "Bàn kế đánh Bình Kiều",
            "Đánh chiếm Bình Kiều", "Chọn hướng tiến quân", "Đánh Ngô Nhật Khánh", "Nhận tin Lã Đường đánh Đằng Châu",
            "Cứu viện Đằng Châu", "Bàn kế đánh Tế Giang", "Phá quân canh Tế Giang", "Diệt Lã Đường ở Tế Giang",
            "Từ biệt Phạm Bạch Hổ", "Phá đồn Bảo Đà", "Bắt Đỗ Cảnh Thạc", "Củng cố Đỗ Động", "Giữ thành Đỗ Động",
            "Bàn kế đánh Tây Phù Liệt", "Hỏi tội Nguyễn Trí Khả", "Diệt Nguyễn Siêu", "Bàn kế phá liên minh",
            "Cứu viện Ô Man", "Chuẩn bị đánh Siêu Loại", "Đánh vào Siêu Loại", "Đuổi bắt Lý Khuê", "Xuất binh đánh Tiên Du",
            "Đánh Nguyễn Thủ Tiệp", "Bàn kế đánh Cổ Loa", "Phá quân canh Cổ Loa", "Đánh Lữ Xử Bình ở Cổ Loa",
            "Bàn kế đánh Tam Đái", "Phá quân canh Tam Đái", "Đánh Nguyễn Khoan ở Tam Đái", "Nhận tin Kỳ Bố bị vây",
            "Giải vây Kỳ Bố", "Bàn kế đánh Hồi Hồ", "Phá quân canh Hồi Hồ", "Đánh Kiều Công Hãn", "Đánh Kiều Thuận",
            "Bàn việc xưng đế", "Diệt Ấu Rồng ở Hoa Lư", "Lễ lên ngôi Đinh Tiên Hoàng"
        };

        public static readonly string[] Chapters =
        {
            "Khởi binh ở Hoa Lư", "Bình Kiều", "Cứu Đằng Châu, diệt Tế Giang", "Đỗ Động Giang", "Tây Phù Liệt",
            "Siêu Loại và Tiên Du", "Cổ Loa và Tam Đái", "Kỳ Bố và Hồi Hồ", "Thống nhất giang sơn"
        };

        /// <summary>First quest (0-based) of each chapter, and the end.</summary>
        public static readonly int[] ChapterStart = {0, 6, 9, 16, 21, 25, 30, 36, 42, 46};

        public static string Title(int q)
        {
            return q >= 0 && q < Titles.Length ? Titles[q] : "Nhiệm vụ " + (q + 1);
        }

        public static int Chapter(int q)
        {
            for (int c = 0; c < Chapters.Length; c++)
            {
                if (q < ChapterStart[c + 1])
                {
                    return c;
                }
            }
            return Chapters.Length - 1;
        }

        /// <summary>Name of a map node; points on the roads without a name: "trạm gác gần {city}".</summary>
        public static string Place(int n)
        {
            string s = C_PortMap.nodeName(n);
            if (s.Length > 0 && C_PortMap.nodeType(n) == 0)
            {
                return s;
            }
            if (s.Length > 0)
            {
                return s;
            }
            int best = -1;
            float bd = 1e9f;
            for (int i = 0; i < C_PortMap.nodeCount(); i++)
            {
                if (C_PortMap.nodeType(i) != 0)
                {
                    continue;
                }
                float dx = C_PortMap.nodeX(i) - C_PortMap.nodeX(n), dy = C_PortMap.nodeY(i) - C_PortMap.nodeY(n);
                if (dx * dx + dy * dy < bd)
                {
                    bd = dx * dx + dy * dy;
                    best = i;
                }
            }
            return best >= 0 ? "trạm gác gần " + C_PortMap.nodeName(best) : "Trạm gác";
        }

        /// <summary>Name of the character on a portrait (the game's own table).</summary>
        public static string Who(int portrait)
        {
            if (portrait < 0)
            {
                return "";
            }
            try
            {
                return Kit.Title(C_PortScreens.heroName(portrait));
            }
            catch (System.Exception)
            {
                return "";
            }
        }
    }
}
