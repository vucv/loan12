// Screen 12: settings (plan/thiet-ke-cai-dat.md 2). Filled in by the next step.
using UnityEngine;

namespace Loan12.UnityPort
{
    public sealed class TouchSettingsPage : IPage
    {
        public enum Where
        {
            Menu,
            Map,
            Battle
        }

        readonly Where where;

        public TouchSettingsPage(Where w)
        {
            where = w;
        }

        public bool OnGUI(Event e)
        {
            bool back = Kit.TitleBar("CÀI ĐẶT") || Kit.Back();
            if (Kit.Back())
            {
                e.Use();
            }
            return !back;
        }
    }
}
