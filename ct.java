import java.io.IOException;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

class ct
        implements Runnable {
    private Message a;
    private MessageConnection b;
    private String c;
    private String d = "";
    private ba e;

    ct(ba paramba, MessageConnection paramMessageConnection) {
        this.b = paramMessageConnection;
        this.e = paramba;
    }

    public void a() {
        Thread localThread = new Thread(this);
        localThread.start();
    }

    public void run() {
        while (!cx.N) {
            try {
                Thread.sleep(100L);
            } catch (Exception localException) {
            }
        }
        b();
    }

    void b() {
        try {
            this.a = this.b.receive();
            if (this.a != null) {
                this.c = this.a.getAddress();
                String str = this.c;
                if ((this.a instanceof TextMessage)) {
                    this.d = ((TextMessage) this.a).getPayloadText();
                    if (this.e != null) {
                        this.e.a(this.d, str);
                    }
                } else {
                    byte[] arrayOfByte = ((BinaryMessage) this.a).getPayloadData();
                    this.d = new String(arrayOfByte);
                    if (this.e != null) {
                        this.e.a(this.d, str);
                    }
                }
            }
        } catch (IOException localIOException) {
        }
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ct

 * JD-Core Version:    0.7.0.1

 */