import com.mg.smsgame.main.a;

import java.io.IOException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

class dm
        implements MessageListener {
    private static dm a;
    private static ct b;
    private ba c;
    private static MessageConnection d;

    protected dm() {
        a(an.b());
    }

    public static dm a() {
        if (a == null) {
            a = new dm();
        }
        return a;
    }

    public static void a(MessageConnection paramMessageConnection) {
        a().b(paramMessageConnection);
    }

    private void b(MessageConnection paramMessageConnection) {
        d = paramMessageConnection;
        try {
            d.setMessageListener(this);
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void notifyIncomingMessage(MessageConnection paramMessageConnection) {
        b = new ct(this.c, paramMessageConnection);
        b.a();
    }

    public static void a(String paramString1, String paramString2, a parama) {
        a().b(paramString1, paramString2, parama);
    }

    private void b(String paramString1, String paramString2, a parama) {
        cs.a().a(new bc(paramString1, paramString2, parama));
    }

    public void a(ba paramba) {
        this.c = paramba;
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     dm

 * JD-Core Version:    0.7.0.1

 */