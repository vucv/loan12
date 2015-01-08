import com.mg.smsgame.main.a;
import com.mg.smsgame.main.b;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

class cs
  implements b, Runnable
{
  private bc a;
  private bc b;
  private static cs c;
  private boolean d;
  private Object e = new Object();
  
  public static cs a()
  {
    if (c == null) {
      c = new cs();
    }
    cz.f();
    return c;
  }
  
  protected cs()
  {
    Thread localThread = new Thread(this);
    localThread.start();
  }
  
  public void run()
  {
    this.d = false;
    while (!this.d) {
      if (this.a == null)
      {
        try
        {
          synchronized (this.e)
          {
            this.e.wait();
          }
        }
        catch (Throwable localThrowable1)
        {
          localThrowable1.printStackTrace();
        }
      }
      else
      {
        cx.H = true;
        cz.d().a(cz.b("Chú ý", "Đang gửi tin nhắn SMS", false), false);
        String str = "sms://" + this.a.a;
        MessageConnection localMessageConnection = null;
        try
        {
          localMessageConnection = (MessageConnection)Connector.open(str);
          TextMessage localTextMessage = (TextMessage)localMessageConnection.newMessage("text");
          localTextMessage.setAddress(str);
          localTextMessage.setPayloadText(this.a.b);
          try
          {
            al.c();
          }
          catch (Throwable localThrowable3)
          {
            localThrowable3.printStackTrace();
          }
          cz.d().a(false);
          if (this.a.c != null) {
            this.a.c.a();
          }
          cx.H = false;
          this.a = null;
        }
        catch (Throwable localThrowable2)
        {
          localThrowable2.printStackTrace();
          this.b = this.a;
          this.a = null;
          cz.d().a(false);
          ae localae = cz.a("Chú ý", "Không thể gửi tin SMS. Bạn có muốn gửi lại không?", "Có", 1, "Không", 2, 1);
          localae.a(this);
          cz.d().a(localae);
        }
        finally
        {
          if (localMessageConnection != null) {
            try
            {
              localMessageConnection.close();
            }
            catch (IOException localIOException2)
            {
              localIOException2.printStackTrace();
            }
          }
          if ((!cx.F) && (al.a())) {
            al.b();
          }
        }
      }
    }
  }
  
  void a(bc parambc)
  {
    if (parambc == null) {
      return;
    }
    this.a = parambc;
    synchronized (this.e)
    {
      this.e.notify();
    }
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 2)
    {
      cx.H = false;
      cz.d().a(false);
      cz.d().a(true);
    }
    else if (paramInt2 == 1)
    {
      cz.d().a(false);
      a(this.b);
      this.b = null;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     cs
 * JD-Core Version:    0.7.0.1
 */