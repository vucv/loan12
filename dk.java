import javax.microedition.media.Controllable;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

public class dk
  implements at, PlayerListener
{
  private Player a;
  private VolumeControl b;
  private String c = "";
  private boolean d = true;
  private boolean e;
  
  public dk() {}
  
  public dk(String paramString)
  {
    a(paramString);
  }
  
  public void a(String paramString)
  {
    if (!this.d) {
      return;
    }
    d.a("Play " + paramString + " - " + this.c + "...");
    if (paramString.equals(this.c))
    {
      d();
      return;
    }
    try
    {
      this.d = false;
      if ((!cx.x) || (!cx.A)) {
        return;
      }
      a();
      this.a = Manager.createPlayer("".getClass().getResourceAsStream("/audio/" + paramString + ".amr"), "audio/amr");
      if (cx.D) {
        this.a.setLoopCount(-1);
      } else {
        this.a.setLoopCount(1);
      }
      this.a.realize();
      this.a.prefetch();
      this.a.addPlayerListener(this);
      this.b = ((VolumeControl)this.a.getControl("VolumeControl"));
      if (this.b != null) {
        if (cx.D)
        {
          this.b.setMute(true);
          this.b.setLevel(cx.y);
          this.a.start();
        }
        else
        {
          this.b.setMute(!cx.x);
          this.b.setLevel(cx.y);
        }
      }
      d.a("Write currFile " + this.c);
      this.c = paramString;
      bg.a(false);
      this.d = true;
    }
    catch (Throwable localThrowable)
    {
      this.d = true;
      d.a("[Create Effect Sound" + localThrowable);
      bg.a(true);
      a();
    }
  }
  
  public void a()
  {
    if (this.a != null) {
      try
      {
        this.a.stop();
        this.a.deallocate();
        this.a.close();
        this.a = null;
        this.b = null;
      }
      catch (MediaException localMediaException)
      {
        this.a = null;
        this.b = null;
        d.a("[Destroy Effect Sound" + localMediaException);
      }
    }
  }
  
  public void b()
  {
    if (!cx.x) {
      return;
    }
    if (!cx.A) {
      return;
    }
    if ((this.a == null) && (!this.c.equals(""))) {
      a(this.c);
    }
    ci.a().a(this);
  }
  
  public void d()
  {
    if (this.a != null) {
      try
      {
        this.a.stop();
        this.a.setMediaTime(0L);
      }
      catch (Throwable localThrowable)
      {
        a();
        d.a("[STOP EFFECT Music]: " + localThrowable);
      }
    }
  }
  
  public void c()
  {
    try
    {
      if (cx.D)
      {
        this.a.setMediaTime(0L);
        if (this.b != null) {
          this.b.setMute(!cx.x);
        }
      }
      else
      {
        if (cx.V) {
          cf.a().b();
        }
        this.a.setMediaTime(0L);
        if (this.b != null)
        {
          this.b.setMute(!cx.x);
          this.b.setLevel(cx.y);
        }
        this.a.start();
      }
    }
    catch (Exception localException)
    {
      d.a("[Play Effect Sound" + localException);
      bg.a(true);
      if (!cx.A) {
        a();
      }
    }
  }
  
  public boolean e()
  {
    return this.e;
  }
  
  public void playerUpdate(Player paramPlayer, String paramString, Object paramObject)
  {
    if (paramString.equals("endOfMedia"))
    {
      try
      {
        if (cx.D)
        {
          if (this.b != null)
          {
            this.b.setLevel(cx.y);
            this.b.setMute(true);
          }
        }
        else
        {
          if (this.b != null)
          {
            this.b.setMute(!cx.x);
            this.b.setLevel(cx.y);
          }
          if (cx.V)
          {
            d();
            cf.a().e();
          }
        }
      }
      catch (Exception localException1)
      {
        try
        {
          this.a.setMediaTime(0L);
        }
        catch (Exception localException2) {}
      }
      this.e = false;
    }
    else if (paramString.equals("started"))
    {
      this.e = true;
    }
    else if (paramString.equals("stopped"))
    {
      this.e = false;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     dk
 * JD-Core Version:    0.7.0.1
 */