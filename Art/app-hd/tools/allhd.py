# Bước 1 nâng cấp đồ họa toàn app: tạo bản 4x cho mọi hình theo nhóm.
#   sprite : hình nhỏ có viền (smooth2.process: viền ngoài nắn thẳng/tròn, bên trong mượt nhẹ)
#   strip  : dải hoạt cảnh, xử lý từng khung (khung thưa chỉ làm mượt nhẹ để giữ hạt)
#   paint  : tranh nhiều chi tiết (chân dung, bản đồ, nền, biểu tượng chiêu): 2 mức
#            nhẹ = Scale2x 16x thu về 4x (chỉ khử răng cưa)
#            vừa = Scale2x 8x, làm mượt vùng màu (sigma 2.5) rồi thu về 4x
# python3 allhd.py <out_dir> [name...]   (orig PNG ở all/<name>.png)
import sys, os, numpy as np
from PIL import Image, ImageEnhance
from scipy.ndimage import gaussian_filter
from smooth2 import scale2x, process, frames

GROUPS = {
    'skill': {
        'strip': ['fireball', 'hellfire', 'frozen', 'blastlighting', 'chainlighting', 'icebolt', 'healing',
                  'shieldlighting', 'powerattack', 'defenceshield', 'bloodthrowaround'],
        'sprite': ['chainlightingpart1', 'chainlightingpart2', 'vchainlighting', 'vhchainlighting'],
        'paint': ['fireballicon', 'hellfireicon', 'frozenicon', 'blastlightingicon', 'chainlightingicon',
                  'healingicon', 'icebolticon', 'shieldlightingicon', 'meteoricon'],
    },
    'portrait': {'paint': ['avatar']},
    'map': {
        'paint': ['m__m', 'm__e1', 'm__e2'],
        'sprite': ['m__af', 'm__ar', 'm__b', 'm__bl', 'm__ff', 'm__fsw', 'm__g', 'm__hm', 'm__ih', 'm__isw',
                   'm__l', 'm__lock', 'm__lv', 'm__mt1', 'm__r', 'm__sw', 'm__vk'],
    },
    'items': {
        'strip': ['itemshops'],
        'sprite': ['r__1', 'r__2', 'r__3', 'r__4', 'r__br', 'r__g', 'r__q', 'blood', 'bloodext', 'mana', 'food',
                   'numbersmall', 'arrowfocus1', 'arrowfocus2', '_arrow', 'frame1', 'frame2', 'frame3', 'borderskb'],
    },
    'screens': {
        'paint': ['bkmenu', 'bksplashscreen'],
        'sprite': ['strwin', 'strlose', 'title', '_corner'],
    },
}


def enhance(im):
    rgb = im.convert('RGB'); al = im.split()[3]
    rgb = ImageEnhance.Color(rgb).enhance(1.08); rgb = ImageEnhance.Contrast(rgb).enhance(1.04)
    rgb.putalpha(al)
    return rgb


def down(big, w, h):
    return Image.fromarray(big.astype(np.uint8), 'RGBA').convert('RGBa').resize((w * 4, h * 4), Image.BOX).convert('RGBA')


def paint_light(o):
    big = o
    for _ in range(4):
        big = scale2x(big)
    return enhance(down(big, o.shape[1], o.shape[0]))


def paint_medium(o, sigma=2.5):
    big = o
    for _ in range(3):
        big = scale2x(big)
    big = big.copy(); big[big[..., 3] == 0] = 0
    cols, lab = np.unique(big.reshape(-1, 4), axis=0, return_inverse=True)
    lab = lab.reshape(big.shape[:2])
    best = np.full(lab.shape, -1.0, np.float32); out = np.zeros(lab.shape, np.int32)
    for i in range(len(cols)):
        m = lab == i
        if not m.any():
            continue
        g = gaussian_filter(m.astype(np.float32), sigma, mode='nearest')
        upd = g > best; best[upd] = g[upd]; out[upd] = i
    return enhance(down(cols[out], o.shape[1], o.shape[0]))


def strip(o):
    res = Image.new('RGBA', (o.shape[1] * 4, o.shape[0] * 4))
    for a, b in frames(o):
        a0 = max(0, a - 2); b0 = min(o.shape[1], b + 2)
        part = o[:, a0:b0]
        ys = np.where((part[..., 3] > 0).any(axis=1))[0]
        cover = (part[..., 3] > 0).sum() / max(1, (b - a) * (ys[-1] - ys[0] + 1))
        sp = (4, 8) if cover >= 0.4 else (1.5, 0.01)
        res.alpha_composite(process(part, s_in=sp[0], s_out=sp[1]), (a0 * 4, 0))
    return res


def kind_of(name):
    for g, kinds in GROUPS.items():
        for k, names in kinds.items():
            if name in names:
                return g, k
    return None, None


if __name__ == '__main__':
    out = sys.argv[1]
    names = sys.argv[2:] or [n for g in GROUPS.values() for ns in g.values() for n in ns]
    for n in names:
        g, k = kind_of(n)
        o = np.array(Image.open('all/%s.png' % n).convert('RGBA'))
        os.makedirs(os.path.join(out, g), exist_ok=True)
        if k == 'paint':
            paint_light(o).save(os.path.join(out, g, n + '.light.png'))
            paint_medium(o).save(os.path.join(out, g, n + '.medium.png'))
        elif k == 'strip':
            strip(o).save(os.path.join(out, g, n + '.png'))
        else:
            process(o, grid=False).save(os.path.join(out, g, n + '.png'))
        print(g, k, n, flush=True)


def dither_fix(o, im):
    """Vùng tô chấm xen kẽ (điểm ảnh có >= 3 trong 4 điểm kề khác trạng thái trong suốt): thay bằng lớp bán trong suốt mịn."""
    a = o[..., 3] > 0
    P = np.pad(a, 1)
    diff = (P[:-2, 1:-1] != a).astype(int) + (P[2:, 1:-1] != a) + (P[1:-1, :-2] != a) + (P[1:-1, 2:] != a)
    dz = diff >= 3
    if dz.sum() < 8:
        return im
    from scipy.ndimage import binary_dilation, zoom
    dz = binary_dilation(dz, iterations=1)
    # trung bình (tiền nhân alpha) quanh mỗi điểm ở độ phân giải gốc, rồi phóng 4x mượt
    f = o.astype(np.float32) / 255.0
    pm = f[..., :3] * f[..., 3:4]
    k = lambda x: gaussian_filter(x, 0.8, mode='nearest')
    al = k(f[..., 3]); rgb = np.stack([k(pm[..., i]) for i in range(3)], -1)
    # hình dạng: độ phủ của các chấm, cắt ngưỡng thành viền mịn; màu: trung bình các chấm; độ đục 0.6
    cover = np.clip(zoom(al, 4, order=1), 0, 1)
    shape = np.clip((cover - 0.25) * 6, 0, 1)
    sel = dz & a
    mean = (o[sel][:, :3].astype(np.float32) / 255.0).mean(0) if sel.any() else np.zeros(3)
    blur = np.concatenate([np.broadcast_to(mean, shape.shape + (3,)), (shape * 0.6)[..., None]], -1)
    mask = np.repeat(np.repeat(dz, 4, 0), 4, 1)
    mask = gaussian_filter(mask.astype(np.float32), 2)[..., None]
    base = np.array(im).astype(np.float32) / 255.0
    out = base * (1 - mask) + blur * mask
    return Image.fromarray((np.clip(out, 0, 1) * 255).astype(np.uint8), 'RGBA')


DITHER = ['m__vk']
if __name__ == '__main__':
    for n in (sys.argv[2:] or DITHER):
        if n in DITHER:
            g, k = kind_of(n)
            p = os.path.join(sys.argv[1], g, n + '.png')
            o = np.array(Image.open('all/%s.png' % n).convert('RGBA'))
            dither_fix(o, Image.open(p).convert('RGBA')).save(p)
            print('dither', n)
