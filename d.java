import com.mg.smsgame.main.MGMIDlet;

import java.io.PrintStream;
import java.util.Date;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

public class d
        extends Form
        implements CommandListener {
    private static d a;
    private int b = 255;
    private int c = 0;

    protected d() {
        super("All log");
        setCommandListener(this);
        addCommand(new Command("Back", 2, 1));
        addCommand(new Command("Clear", 4, 1));
        append("Start at " + new Date(System.currentTimeMillis()).toString());
    }

    public void commandAction(Command paramCommand, Displayable paramDisplayable) {
        if (paramCommand.getLabel().equals("Back")) {
            MGMIDlet.f().a.setCurrent(cz.d());
        } else if (paramCommand.getLabel().equals("Clear")) {
            deleteAll();
            this.c = 0;
            append("Start at " + new Date(System.currentTimeMillis()).toString());
        }
    }

    public static d a() {
        if (a == null) {
            a = new d();
        }
        return a;
    }

    private void b(String paramString) {
        if (!cx.F) {
            return;
        }
        if ((this.b & 0x1) == 0) {
            return;
        }
        if (this.c >= 50) {
            a().delete(0);
        }
        append(paramString);
        System.out.println(paramString);
        this.c += 1;
    }

    public static void a(String paramString) {
        a().b(paramString);
    }

    private void b(Throwable paramThrowable) {
        if (!cx.F) {
            return;
        }
        if ((this.b & 0x2) == 0) {
            return;
        }
        if (this.c >= 50) {
            a().delete(0);
        }
        paramThrowable.printStackTrace();
        append(paramThrowable.toString());
        this.c += 1;
    }

    public static void a(Throwable paramThrowable) {
        a().b(paramThrowable);
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     d

 * JD-Core Version:    0.7.0.1

 */