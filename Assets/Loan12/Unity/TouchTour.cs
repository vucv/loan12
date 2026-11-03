// Developer aid: "-loan12-tour <folder>" on the command line (desktop builds) plays through the touch
// screens by calling the same actions the buttons call, and saves a screenshot of each screen. The
// saved game of the build is cleared first ("-loan12-tour" is never used by players). Lets the phone
// layout be checked without a phone.
using System.Collections;
using System.IO;
using UnityEngine;

namespace Loan12.UnityPort
{
    public static class TouchTour
    {
        static string dir;
        static int n;

        public static IEnumerator Run(string folder)
        {
            dir = folder;
            Directory.CreateDirectory(dir);
            Application.runInBackground = true;
            Debug.Log("Loan12 tour: start");

            yield return Until(() => Kind() == 2, 25);
            yield return new WaitForSeconds(1.5f);
            yield return Shot("menu_first");

            // settings, rules, credits
            TouchSettingsPage settings = new TouchSettingsPage(TouchSettingsPage.Where.Menu);
            TouchMenuView.Instance.Open(settings);
            yield return Shot("settings");
            TouchMenuView.Instance.Open(new RulesPage());
            yield return Shot("rules");
            TouchMenuView.Instance.Open(new AboutPage());
            yield return Shot("about");
            TouchMenuView.Instance.Open(null);

            // first character: story -> hero -> name -> map
            TouchMenuView.Instance.NewCharacter();
            yield return Until(() => Kind() == 5, 10);
            yield return new WaitForSeconds(2f);
            yield return Shot("story");
            C_PortScreens.request(C_PortScreens.f_SKIP, 0);
            yield return Until(() => Kind() == 3, 15);
            yield return new WaitForSeconds(1f);
            yield return Shot("hero");
            yield return Hooks("hero");
            C_PortScreens.request(C_PortScreens.f_HERO_OK, 0);
            yield return Until(() => J2ME.Host.display.getShown() is J2ME.TextBox, 5);
            yield return Shot("name");
            J2ME.TextBox tb = (J2ME.TextBox) J2ME.Host.display.getShown();
            tb.setString("Hùng Dũng");
            for (int i = 0; i < tb.getCommandCount(); i++)
            {
                if (tb.getCommand(i).getLabel() == "Xong")
                {
                    tb.fireCommand(tb.getCommand(i));
                }
            }
            yield return Until(() => Kind() == 4, 15);
            yield return new WaitForSeconds(3f);
            yield return Shot("map");
            yield return Hooks("map");

            // back to the menu: CHƠI TIẾP + NHÂN VẬT
            yield return ToMenu();
            yield return Until(() => Kind() == 2, 15);
            yield return new WaitForSeconds(1.5f);
            yield return Shot("menu_continue");
            yield return Hooks("menu");

            Debug.Log("Loan12 tour: done (" + n + " shots)");
            yield return new WaitForSeconds(0.5f);
            Application.Quit();
        }

        /// <summary>From the map back to the menu (saves the map).</summary>
        public static System.Func<IEnumerator> ToMenu = ClassicToMenu;

        static IEnumerator ClassicToMenu()
        {
            C_PortUi.request(C_PortUi.f_SCREEN_KEY, 2); // "Thoát"
            yield return Until(() => C_PortScreens.hasDialog(), 5);
            yield return new WaitForSeconds(0.5f);
            C_PortUi.request(C_PortUi.f_DIALOG_KEY, 0); // "Có"
        }

        /// <summary>Extra steps of later screens, by stage name.</summary>
        static IEnumerator Hooks(string stage)
        {
            if (Extra != null)
            {
                IEnumerator e = Extra(stage);
                if (e != null)
                {
                    yield return e;
                }
            }
        }

        public static System.Func<string, IEnumerator> Extra;

        public static int Kind()
        {
            try
            {
                return C_PortScreens.busy() ? -1 : C_PortScreens.screenKind();
            }
            catch (System.Exception)
            {
                return -1;
            }
        }

        public static IEnumerator Until(System.Func<bool> cond, float seconds)
        {
            float end = Time.realtimeSinceStartup + seconds;
            while (!cond() && Time.realtimeSinceStartup < end)
            {
                yield return null;
            }
            if (!cond())
            {
                Debug.LogWarning("Loan12 tour: timed out (" + seconds + " s), screen " + Kind());
            }
        }

        public static IEnumerator Shot(string name)
        {
            yield return new WaitForSeconds(0.6f);
            yield return new WaitForEndOfFrame();
            n++;
            string path = Path.Combine(dir, n.ToString("00") + "_" + name + ".png");
            ScreenCapture.CaptureScreenshot(path);
            Debug.Log("Loan12 tour: " + path);
            yield return new WaitForSeconds(0.3f);
        }
    }
}
