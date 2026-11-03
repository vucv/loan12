// Screen 7: shop (plan/thiet-ke-ban-do.md 5). Filled in by a later step.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchShopTab : IMapTab
    {
        public int sub;

        public bool FreeReady()
        {
            return false;
        }

        public void OnGUI(TouchMapView map, float top, float bottom)
        {
            if (Kit.Paint)
            {
                TouchUi.PixelText("CỬA HÀNG", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 20, 0, 0).y, 1, true);
            }
        }

        public bool Back()
        {
            return false;
        }
    }
}
