// Screen 6: bag (plan/thiet-ke-ban-do.md 4). Filled in by a later step.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchBagTab : IMapTab
    {
        public void OnGUI(TouchMapView map, float top, float bottom)
        {
            if (Kit.Paint)
            {
                TouchUi.PixelText("TÚI ĐỒ", 1, TouchUi.R(90, 0, 0, 0).x, TouchUi.R(0, top + 20, 0, 0).y, 1, true);
            }
        }

        public bool Back()
        {
            return false;
        }
    }
}
