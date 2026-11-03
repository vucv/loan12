// Screen 2: characters (plan/thiet-ke-nhieu-nhan-vat.md 4.3-4.7). Filled in by a later step.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchSlotsPage : IPage
    {
        public TouchSlotsPage(bool adBox = false)
        {
        }

        public bool OnGUI(Event e)
        {
            bool back = Kit.TitleBar("NHÂN VẬT") || Kit.Back();
            if (Kit.Back())
            {
                e.Use();
            }
            return !back;
        }
    }
}
