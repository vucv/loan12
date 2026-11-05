# A (bản 3): khử răng cưa / nhiễu đường viền
#  1. Scale2x 4 lần (16x)
#  2. Viền ngoài (vùng trong suốt và các màu viền tối bám theo nó) làm mượt mạnh: đoạn thẳng thẳng, đường cong tròn
#  3. Bên trong làm mượt nhẹ (giữ chi tiết nhỏ), xóa chấm nhiễu lẻ
#  4. Thu về 4x bằng trung bình (khử răng cưa), tăng nhẹ màu / tương phản
#  Hình hình học (khung chọn, vòng nổ kiếm) vẽ lại bằng hình học chính xác (geom.py).
import sys, numpy as np
from PIL import Image, ImageEnhance
from scipy.ndimage import gaussian_filter, distance_transform_edt


def scale2x(a):
    h, w, _ = a.shape
    P = np.pad(a, ((1, 1), (1, 1), (0, 0)), mode='edge')
    E = P[1:-1, 1:-1]; B = P[:-2, 1:-1]; H = P[2:, 1:-1]; D = P[1:-1, :-2]; F = P[1:-1, 2:]
    eq = lambda x, y: np.all(x == y, axis=2)[..., None]
    c = (~eq(B, H)) & (~eq(D, F))
    e0 = np.where(c & eq(D, B), D, E); e1 = np.where(c & eq(B, F), F, E)
    e2 = np.where(c & eq(D, H), D, E); e3 = np.where(c & eq(H, F), F, E)
    o = np.empty((h * 2, w * 2, 4), a.dtype)
    o[0::2, 0::2] = e0; o[0::2, 1::2] = e1; o[1::2, 0::2] = e2; o[1::2, 1::2] = e3
    return o


def smooth_labels(lab, n, sigma):
    best = np.full(lab.shape, -1.0); out = np.zeros(lab.shape, np.int32)
    for i in range(n):
        mask = lab == i
        if not mask.any():
            continue
        m = gaussian_filter(mask.astype(np.float32), sigma, mode='nearest')
        upd = m > best; best[upd] = m[upd]; out[upd] = i
    return out


def nearest_fill(lab, valid):
    _, (iy, ix) = distance_transform_edt(~valid, return_indices=True)
    return lab[iy, ix]


def process(o, s_in=5, s_out=12, grid=False):
    big = o
    for _ in range(4):
        big = scale2x(big)
    big = big.copy(); big[big[..., 3] == 0] = 0
    flat = big.reshape(-1, 4)
    cols, lab = np.unique(flat, axis=0, return_inverse=True)
    lab = lab.reshape(big.shape[:2])
    tr = [i for i, c in enumerate(cols) if c[3] == 0]
    t = tr[0] if tr else -1
    T = lab == t
    outline = []
    if T.any():
        near = distance_transform_edt(~T) <= 20
        for i in range(len(cols)):
            m = lab == i
            if i == t or not m.any():
                continue
            if (m & near).sum() / m.sum() > 0.5 and int(cols[i][:3].astype(int).sum()) < 200:
                outline.append(i)
    Om = T | np.isin(lab, outline)
    Ts = gaussian_filter(T.astype(np.float32), s_out) > 0.5 if T.any() else np.zeros_like(T)
    Os = (gaussian_filter(Om.astype(np.float32), s_out) > 0.5) | Ts
    if s_out < 0.5:
        Ts = T; Os = Om
    inner = [i for i in range(len(cols)) if i != t and i not in outline]
    res = smooth_labels(lab, len(cols), s_in)
    if inner:
        bad = ~np.isin(res, inner)
        res = np.where(bad, nearest_fill(res, ~bad), res)
    if outline:
        ol = nearest_fill(lab, np.isin(lab, outline))
        res = np.where(Os & ~Ts, ol, res)
    if t >= 0:
        res = np.where(Ts, t, res)
    h, w = o.shape[:2]
    im = Image.fromarray(cols[res].astype(np.uint8), 'RGBA').convert('RGBa').resize((w * 4, h * 4), Image.BOX).convert('RGBA')
    if grid:  # ô lúa: giữ nét lưới bên trong, chỉ lấy viền đã làm mịn
        nn = Image.fromarray(o, 'RGBA').resize(im.size, Image.NEAREST)
        r, g, b, _ = nn.split(); im = Image.merge('RGBA', (r, g, b, im.split()[3]))
    rgb = im.convert('RGB'); al = im.split()[3]
    rgb = ImageEnhance.Color(rgb).enhance(1.08); rgb = ImageEnhance.Contrast(rgb).enhance(1.04)
    rgb.putalpha(al)
    return rgb


def frames(o):
    """Các khung của một dải hoạt cảnh: những đoạn cột có điểm ảnh, cách nhau bởi cột trong suốt."""
    cols = (o[..., 3] > 0).any(axis=0); segs = []; x = 0; W = o.shape[1]
    while x < W:
        if cols[x]:
            s = x
            while x < W and cols[x]:
                x += 1
            segs.append((s, x))
        else:
            x += 1
    return segs


STRIPS = ('explode', 'star', 'crystal')

if __name__ == '__main__':
    out = sys.argv[1]
    for n in sys.argv[2:]:
        o = np.array(Image.open('orig/%s.png' % n).convert('RGBA'))
        if n.startswith(STRIPS):
            res = Image.new('RGBA', (o.shape[1] * 4, o.shape[0] * 4))
            for a, b in frames(o):
                a0 = max(0, a - 2); b0 = min(o.shape[1], b + 2)
                part = o[:, a0:b0]
                ys = np.where((part[..., 3] > 0).any(axis=1))[0]
                cover = (part[..., 3] > 0).sum() / max(1, (b - a) * (ys[-1] - ys[0] + 1))
                # khung thưa (hạt lấp lánh): chỉ làm mượt nhẹ để không mất hạt
                sp = (4, 8) if cover >= 0.4 else (1.5, 0.01)
                res.alpha_composite(process(part, s_in=sp[0], s_out=sp[1]), (a0 * 4, 0))
            res.save('%s/%s.png' % (out, n))
        else:
            process(o, grid=(n == 'rice')).save('%s/%s.png' % (out, n))
