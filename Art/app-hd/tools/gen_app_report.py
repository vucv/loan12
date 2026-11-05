import base64, io, os
from PIL import Image

R = '/home/claude/loan12/Art/app-hd'


def u(im):
    b = io.BytesIO(); im.save(b, 'PNG', optimize=True)
    return 'data:image/png;base64,' + base64.b64encode(b.getvalue()).decode()


def orig(n):
    return Image.open('%s/orig/%s.png' % (R, n)).convert('RGBA')


def hd(g, n, lvl=None):
    return Image.open('%s/%s/%s%s.png' % (R, g, n, '.' + lvl if lvl else '')).convert('RGBA')


LABEL = {
    'fireball': 'Cầu lửa', 'hellfire': 'Địa ngục lửa', 'frozen': 'Đóng băng', 'blastlighting': 'Sét nổ',
    'chainlighting': 'Sét chuỗi', 'icebolt': 'Băng tiễn', 'healing': 'Hồi máu', 'shieldlighting': 'Khiên sét',
    'powerattack': 'Đòn mạnh', 'defenceshield': 'Khiên thủ', 'bloodthrowaround': 'Máu văng',
    'chainlightingpart1': 'Sét chuỗi (mảnh 1)', 'chainlightingpart2': 'Sét chuỗi (mảnh 2)', 'vchainlighting': 'Sét dọc',
    'vhchainlighting': 'Sét chéo', 'fireballicon': 'Biểu tượng cầu lửa', 'hellfireicon': 'Biểu tượng địa ngục lửa',
    'frozenicon': 'Biểu tượng đóng băng', 'blastlightingicon': 'Biểu tượng sét nổ', 'chainlightingicon': 'Biểu tượng sét chuỗi',
    'healingicon': 'Biểu tượng hồi máu', 'icebolticon': 'Biểu tượng băng tiễn', 'shieldlightingicon': 'Biểu tượng khiên sét',
    'meteoricon': 'Biểu tượng mưa thiên thạch', 'avatar': 'Bảng chân dung (8 chân dung đầu)', 'm__m': 'Bản đồ (một góc)',
    'm__e1': 'Tranh bản đồ lớn', 'm__e2': 'Tranh bản đồ nhỏ', 'm__af': 'Biểu tượng bản đồ', 'm__ar': 'Biểu tượng bản đồ',
    'm__b': 'Biểu tượng bản đồ', 'm__bl': 'Thanh bản đồ', 'm__ff': 'Khung đỏ', 'm__fsw': 'Kiếm', 'm__g': 'Đồng xu',
    'm__hm': 'Biểu tượng bản đồ', 'm__ih': 'Tim', 'm__isw': 'Biểu tượng bản đồ', 'm__l': 'Biểu tượng bản đồ', 'm__lock': 'Khóa',
    'm__lv': 'Thanh bản đồ', 'm__mt1': 'Giọt và chấm màu', 'm__r': 'Biểu tượng bản đồ', 'm__sw': 'Kiếm chéo', 'm__vk': 'Mũi tên di chuyển',
    'itemshops': 'Vật phẩm cửa hàng', 'r__1': 'Phần thưởng 1', 'r__2': 'Phần thưởng 2', 'r__3': 'Phần thưởng 3', 'r__4': 'Phần thưởng 4',
    'r__br': 'Khung túi đồ', 'r__g': 'Biểu tượng nhỏ', 'r__q': 'Biểu tượng nhỏ', 'blood': 'Thanh máu', 'bloodext': 'Thanh máu thêm',
    'mana': 'Thanh năng lượng', 'food': 'Thanh lương thực', 'numbersmall': 'Chữ số nhỏ', 'arrowfocus1': 'Mũi tên chọn 1',
    'arrowfocus2': 'Mũi tên chọn 2', '_arrow': 'Mũi tên', 'frame1': 'Khung trên', 'frame2': 'Viền dọc', 'frame3': 'Viền ngang',
    'borderskb': 'Viền phím', 'bkmenu': 'Nền menu (một góc)', 'bksplashscreen': 'Nền màn chờ (một góc)',
    'strwin': 'Chữ THẮNG', 'strlose': 'Chữ THUA', 'title': 'Tiêu đề 12 Sứ Quân', '_corner': 'Góc khung',
}
CROP = {'avatar': (0, 0, 120, 60), 'm__m': (130, 130, 250, 250), 'bkmenu': (40, 80, 160, 200),
        'bksplashscreen': (60, 100, 180, 220)}

import sys
sys.path.insert(0, '/tmp/claude-0/-home-claude/60c945cf-b8c5-5e09-80e3-32c155f36941/scratchpad')
from allhd import GROUPS

GNAME = {'skill': 'Hiệu ứng và biểu tượng chiêu', 'portrait': 'Chân dung', 'map': 'Bản đồ',
         'items': 'Vật phẩm, phần thưởng, thanh', 'screens': 'Nền và chữ lớn'}
GNOTE = {'skill': 'Dải hoạt cảnh xử lý từng khung; biểu tượng chiêu là tranh nên có 2 mức.',
         'portrait': 'Tranh nhiều chi tiết: mức nhẹ chỉ khử răng cưa, mức vừa làm mượt thêm các vùng màu.',
         'map': 'Bản đồ lớn và hai tranh bản đồ có 2 mức; mũi tên tô chấm xen kẽ được chuyển thành lớp bán trong suốt.',
         'items': 'Viền ngoài nắn thẳng, nắn tròn; giữ chi tiết bên trong.',
         'screens': 'Nền có 2 mức; chữ lớn và góc khung làm mượt viền.'}

sections = []
for g, kinds in GROUPS.items():
    rows = ''
    paint = 'paint' in kinds
    for k in ('paint', 'strip', 'sprite'):
        for n in kinds.get(k, []):
            o = orig(n)
            c = CROP.get(n)
            if c:
                o = o.crop(c)
            W = o.width * 4
            disp = min(W, 380 if paint else 560)
            cells = ['<img class="px" style="width:%dpx" src="%s" alt="">' % (disp, u(o.resize((o.width * 4, o.height * 4), Image.NEAREST)))]
            lv = ['light', 'medium'] if k == 'paint' else [None]
            for l in lv:
                h = hd(g, n, l)
                if c:
                    h = h.crop(tuple(v * 4 for v in c))
                cells.append('<img style="width:%dpx" src="%s" alt="">' % (disp, u(h)))
            if paint and k != 'paint':
                cells.append('')
            rows += '<tr><th scope="row">%s<span class="dim">%s, %d×%d</span></th>%s</tr>' % (
                LABEL.get(n, n), n.replace('__', '/'), orig(n).width, orig(n).height,
                ''.join('<td>%s</td>' % x for x in cells))
    head = '<th scope="col">Hình</th><th scope="col">Gốc</th>' + (
        '<th scope="col">Mức nhẹ</th><th scope="col">Mức vừa</th>' if paint else '<th scope="col">Nét cao</th>')
    sections.append('''<section id="%s"><h2>%s</h2><p class="note">%s</p>
<div class="tablewrap"><table><thead><tr>%s</tr></thead><tbody>%s</tbody></table></div></section>''' % (g, GNAME[g], GNOTE[g], head, rows))

nav = ''.join('<a href="#%s">%s</a>' % (g, GNAME[g]) for g in GROUPS)
html = '''<title>Đồ họa toàn app Loạn 12</title>
<meta name="claude:viewport-fit-sides" content="cover">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;600;800&family=Noto+Serif:wght@700&display=swap">
<style>
/* Layout: một cột; mỗi nhóm là một bảng gốc / bản mới, ảnh phóng cùng cỡ; điều hướng nhóm ở đầu trang */
:root{--bg:#1a130a;--panel:#261b0e;--line:#4a3517;--fg:#f3e6cc;--muted:#bfa983;--gold:#f2b632;--teal:#3fe3e8;--tile:#3a2c16;
--display:"Noto Serif",Georgia,serif;--body:"Be Vietnam Pro",system-ui,sans-serif;color-scheme:dark}
body{background:var(--bg);color:var(--fg);font-family:var(--body);line-height:1.55}
.wrap{max-width:1500px;margin:0 auto;padding-block:28px 48px;padding-left:max(16px,env(safe-area-inset-left,0px));padding-right:max(16px,env(safe-area-inset-right,0px));display:grid;gap:36px}
h1{font-family:var(--display);font-size:clamp(26px,4vw,38px);line-height:1.2;margin:0;color:var(--gold);text-wrap:balance}
h2{font-family:var(--display);font-size:22px;margin:0 0 6px;text-wrap:balance}
p{margin:0;max-width:72ch;color:var(--muted)} p strong{color:var(--fg)}
.lead{display:grid;gap:10px}
.eyebrow{font-size:12px;letter-spacing:.12em;text-transform:uppercase;color:var(--teal);font-weight:600}
nav{display:flex;flex-wrap:wrap;gap:8px} nav a{color:var(--fg);text-decoration:none;border:1px solid var(--line);border-radius:999px;padding:4px 12px;font-size:14px;background:var(--panel)}
nav a:hover,nav a:focus-visible{border-color:var(--gold);outline:none}
.note{margin-bottom:12px}
.px{image-rendering:pixelated}
.tablewrap{overflow-x:auto;border:1px solid var(--line);border-radius:8px}
table{border-collapse:collapse;width:100%%}
th,td{padding:10px 12px;border-bottom:1px solid var(--line);text-align:left;vertical-align:middle}
thead th{font-size:13px;letter-spacing:.06em;text-transform:uppercase;color:var(--muted);background:var(--panel);white-space:nowrap}
tbody th{font-weight:600;font-size:14px;min-width:140px}
td img{display:block;max-width:none;height:auto;background:var(--tile);border-radius:4px}
.dim{display:block;font-size:12px;font-weight:400;color:var(--muted);font-variant-numeric:tabular-nums}
ul{margin:0;padding-left:20px;color:var(--muted)} li{margin:4px 0} li strong{color:var(--fg)}
</style>
<div class="wrap">
<header class="lead"><span class="eyebrow">Nhánh upgrade-graphics · Toàn app · Bước 1</span>
<h1>Đồ họa toàn app: hình gốc và bản nét cao</h1>
<p>Bộ hình nguồn nét cao (gấp 4) cho mọi nhóm hình ngoài bàn đấu: <strong>%d hình</strong>. Chưa đưa vào game. Mỗi hình đặt bản gốc phóng 4 lần giữ hạt cạnh bản mới, cùng cỡ hiển thị. Tranh nhiều chi tiết (chân dung, bản đồ, nền, biểu tượng chiêu) có 2 mức để chọn. Hình lớn chỉ hiện một góc để thấy rõ chi tiết.</p>
<nav aria-label="Nhóm hình">%s</nav></header>
%s
<section class="lead"><h2>Cần bạn chọn</h2>
<ul><li><strong>Tranh dùng mức nào:</strong> nhẹ (giữ nét vẽ gốc, chỉ khử răng cưa) hay vừa (mượt hơn, ít hạt hơn). Có thể chọn riêng cho chân dung, bản đồ, nền, biểu tượng chiêu.</li>
<li><strong>Hình nào cần chỉnh thêm</strong>, ghi tên hình.</li></ul>
<p>Sau khi bạn duyệt, mình gửi hướng đưa bộ hình vào game (Unity dùng bản nét cao khi có, lớp nét cao của trận đấu mở rộng cho hiệu ứng chiêu và dải nhân vật) trước khi sửa code. File nằm trong <strong>Art/app-hd</strong> trên nhánh upgrade-graphics.</p></section>
</div>''' % (sum(len(ns) for g in GROUPS.values() for ns in g.values()), nav, '\n'.join(sections))
open(R + '/report.html', 'w').write(html)
print(len(html))
