// The battle box of the map opening before "Chuẩn bị" (and closing after "Rút lui"), the game's map state 3
// with its box animation: the touch map stays on the screen as it was, untouchable, instead of the game's own
// 240x320 picture at another scale (a jump). Last of the views: only when no other one is active.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchMapHoldView : ITouchView
    {
        public bool Active()
        {
            try
            {
                return TouchMapView.Instance != null && C_PortScreens.screenKind() == 4 && !C_PortScreens.hasDialog() && C_PortMap.state() == 3;
            }
            catch (System.Exception)
            {
                return false;
            }
        }

        public void Reset()
        {
        }

        public void OnGUI(Event e, Texture2D lcd, bool haptics)
        {
            if (e.type == EventType.Repaint)
            {
                TouchMapView.Instance.OnGUI(e, lcd, haptics); // painting only: no touch reaches it
            }
        }
    }
}
