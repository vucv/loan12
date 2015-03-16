import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class w
        extends ds {
    private Image e;
    private int f;
    String[][] a = {{"Tướng quân hãy làm theo chỉ dẫn của tôi.", "Hãy bấm phím (5) hoặc phím OK để chọn thanh kiếm0*#"}, {"Bấm phím (8) hoặc phím xuống để di chuyển xuống"}, {"Khá lắm!", "Để tấn công kẻ thù hãy xếp 3 thanh kiếm các loại sát nhau theo hàng ngang hoặc dọc.", "Bây giờ đến lượt ta..."}, {"Tướng quân đã bị thương", "Để hồi phục sinh lực hãy xếp 3 trái tim2*#"}, {"Tốt lắm.", "Phía dưới có 1 thanh màu xanh lá tượng trưng cho lương thực, hết lương thực thì sẽ thua cuộc.", "Để lấy lương thực hãy xếp 3 bánh chưng1*#"}, {"Rất tốt!", "Thanh màu xanh ngọc bích là năng lượng", "Để lấy năng lượng hãy xếp 3 biểu tượng âm dương3*#"}, {"Cây năng lượng có 3 phần ứng với 3 tuyệt chiêu", "Tướng quân hãy mở bảng phép thuật bằng cách nhấn phím 'T.Chiêu' ,chọn một loại tuyệt chiêu và bấm 'Dùng'."}, {"Tuyệt vời!", "Tướng quân sẽ được thăng cấp khi thu được nhiều kinh nghiệm chiến đấu, cấp càng cao thì chỉ số tấn công và sinh lực càng cao.", "Để lấy kinh nghiệm chiến đấu hãy xếp các biểu tượng cuốn sách.5*#", "Ngoài ra, để mua các vật phẩm hỗ trợ tại cửa hàng tướng quân phải thu thập các thỏi vàng.4*#", "Nếu xếp 4 biểu tượng trở lên sẽ được đi thêm 1 lượt.", "Và xếp 5 biểu tượng trở lên sẽ nhận được thêm các vật phẩm hỗ trợ", "Bây giờ hãy thử xếp 5 biểu tượng1*#"}, {"Tướng quân đã được thưởng 1 vật phẩm, hãy bấm phím # để chọn và sử dụng vật phẩm."}, {"Chú ý, một lượt đi phải ghép ít nhất 3 biểu tượng giống nhau, nếu thực hiện không đúng sẽ bị mất lượt đi và tiêu hao sinh lực. Hãy cẩn trọng."}};
    private int g;
    private int h;
    private int i;
    private String[] j;
    private int k;
    private int l;
    private int[] m = {0, 6, 0, 7, 0, 2, 0, 1, 0, 1, 0, 1, 0, 3, 0, 1, 0, 5, 04};
    int[][] b = {{2, 4, 3, 4}, {6, 9, 6, 8}, {7, 7, 6, 7}, {8, 4, 9, 4}, {6, 3, 7, 3}, {2, 2, 2, 2}, {4, 3, 4, 2}, {2, 2, 2, 2}};
    int c;
    cm d = new cm("/f/ari_b");

    public void a() {
        this.c += 1;
    }

    public int[] b() {
        return this.b[this.c];
    }

    public w() {
        this.p = (cx.l + 32);
        this.q = (cx.m + 225);
        this.r = 176;
        this.u = false;
        this.i = (this.d.a() + 2);
        this.l = -1;
    }

    public void c() {
        this.k += 1;
    }

    public void d() {
        this.l += 1;
        if (this.l >= this.a.length) {
            return;
        }
        this.h = this.a[this.l].length;
        this.g = 0;
        this.u = true;
        a(this.a[this.l][this.g]);
    }

    public void e() {
        this.h = 1;
        this.g = (this.a[this.l].length - 1);
        this.u = true;
        a(this.a[this.l][this.g]);
    }

    public void f() {
        this.g += 1;
        if (this.g >= this.h) {
            this.u = false;
        } else {
            a(this.a[this.l][this.g]);
        }
    }

    private void a(String paramString) {
        this.e = null;
        if (paramString.substring(paramString.length() - 2).equals("*#")) {
            n = Integer.parseInt(paramString.substring(paramString.length() - 3, paramString.length() - 2));
            this.e = bs.a().d[n];
            paramString = paramString.substring(0, paramString.length() - 3);
        }
        int n = this.r - 4;
        this.j = this.d.a(paramString, n);
        this.s = (this.i * this.j.length);
        if (this.e != null) {
            this.s += 15;
        } else {
            this.s += 10;
        }
        this.f = this.d.a(this.j[(this.j.length - 1)]);
    }

    public int g() {
        return this.m[this.k];
    }

    private final void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        paramGraphics.setColor(paramInt7);
        paramGraphics.fillRect(paramInt1 + 4, paramInt2, paramInt3 - 8, paramInt4);
        paramGraphics.fillRect(paramInt1, paramInt2 + 4, paramInt3, paramInt4 - 8);
        paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 2, paramInt3 - 2, paramInt4 - 4);
        paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 1, paramInt3 - 4, paramInt4 - 2);
        paramGraphics.setColor(paramInt6);
        paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 2, paramInt3 - 4, paramInt4 - 4);
        paramGraphics.fillRect(paramInt1 + 4, paramInt2 + 1, paramInt3 - 8, paramInt4 - 2);
        paramGraphics.fillRect(paramInt1 + 1, paramInt2 + 4, paramInt3 - 2, paramInt4 - 8);
        if (paramInt5 >= 0) {
            paramGraphics.fillTriangle(paramInt5 - 5, paramInt2 + paramInt4 - 1, paramInt5 + 5, paramInt2 + paramInt4 - 1, paramInt5, paramInt2 + paramInt4 + 7);
            paramGraphics.setColor(paramInt7);
            paramGraphics.drawLine(paramInt5, paramInt2 + paramInt4 + 7, paramInt5 - 5, paramInt2 + paramInt4 - 1);
            paramGraphics.drawLine(paramInt5, paramInt2 + paramInt4 + 7, paramInt5 + 5, paramInt2 + paramInt4 - 1);
        }
    }

    public void h() {
        if (!this.u) {
        }
    }

    public void a(Graphics paramGraphics, int paramInt1, int paramInt2) {
        if (!this.u) {
            return;
        }
        int n = this.q - this.s;
        int i1 = this.p + 4;
        a(paramGraphics, this.p, n, this.r, this.s, this.p + this.r - 25, 16777215, 0);
        n += 5;
        for (int i2 = 0; i2 < this.j.length; i2++) {
            this.d.a(paramGraphics, this.j[i2], i1, n, 0);
            n += this.i;
        }
        if (this.e != null) {
            paramGraphics.drawImage(this.e, i1 + this.f - 1, n - this.i - 2, 0);
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     w

 * JD-Core Version:    0.7.0.1

 */