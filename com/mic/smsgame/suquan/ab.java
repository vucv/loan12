package com.mic.smsgame.suquan;

import ai;
import aj;
import ar;
import bs;
import bu;
import bv;
import c;
import cq;
import cz;
import d;
import dd;
import i;
import java.io.PrintStream;
import javax.microedition.lcdui.Graphics;

public class ab
  extends aj
  implements e
{
  cq c;
  i d;
  bu e;
  
  public ab()
  {
    long l = System.currentTimeMillis();
    try
    {
      bs.b();
      try
      {
        byte[] arrayOfByte = (byte[])null;
        int i = 0;
        if (f.e())
        {
          System.out.println("khasc nullllllllllllll");
          arrayOfByte = f.d();
          f.f();
          dd.c = ai.a(arrayOfByte, i);
          i += 4;
          dd.e = new c();
          i += dd.e.b(arrayOfByte, i);
          dd.f = new c();
          i += dd.f.b(arrayOfByte, i);
        }
        this.c = new cq(dd.d(dd.c), dd.e, dd.f);
        this.d = new i(this.c);
        this.e = new bu(this.c, this.d);
        this.e.a(arrayOfByte, i);
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
        d.a(localException1);
        f.f();
        cz.a().a(5, 7, false);
        return;
      }
      a(cz.a());
      a(null);
      dd.h();
      try
      {
        bs.a().c();
        bs.a().b(dd.e.f);
        bs.a().b(dd.f.f);
      }
      catch (Exception localException2)
      {
        bs.a().j();
        localException2.printStackTrace();
        d.a(localException2);
      }
      d.a("DONE LOADPLAY" + (System.currentTimeMillis() - l));
    }
    catch (Exception localException3)
    {
      localException3.printStackTrace();
      d.a(localException3);
      cz.a().a(5, 7, false);
    }
    this.q = 5;
  }
  
  public void r() {}
  
  public void a(int paramInt)
  {
    this.e.b(paramInt);
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    this.e.a(paramInt1, paramInt2);
  }
  
  public void a()
  {
    this.e.c();
    this.d.d();
  }
  
  public void a(Graphics paramGraphics)
  {
    this.d.a(paramGraphics);
  }
  
  public void e()
  {
    this.e.t();
  }
  
  public void t()
  {
    this.d.a(false);
  }
  
  public void s()
  {
    this.d.a(true);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.ab
 * JD-Core Version:    0.7.0.1
 */