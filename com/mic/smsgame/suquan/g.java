package com.mic.smsgame.suquan;

import ap;
import ar;
import az;
import be;
import bz;
import cv;
import dl;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import y;

public class g
  extends cv
{
  private Image v;
  private int w;
  private int x;
  private int[] y;
  private int[] z;
  private dl A;
  
  public g()
  {
    this.t = 0;
    this.v = az.b("/crystalblue");
    int i = 50;
    this.A = new dl((this.b_ - (i + 20)) / 2, (this.c_ - (i + 20)) / 2, i + 20, i + 20);
    int j = this.A.a + this.A.c / 2;
    int k = this.A.b + this.A.d / 2;
    int m = i / 2;
    int n = 8;
    int i1 = 360 / n;
    int i2 = -90;
    this.y = new int[n];
    this.z = new int[n];
    for (int i3 = 0; i3 < n; i3++)
    {
      this.y[i3] = (j + (m * be.b(i2) >> 14));
      this.z[i3] = (k + (m * be.a(i2) >> 14));
      i2 += i1;
    }
    this.q = -1000;
  }
  
  public void a()
  {
    this.x = this.w;
    this.w = ((this.w + 1) % this.y.length);
    super.a();
  }
  
  protected void b(Graphics paramGraphics)
  {
    paramGraphics.setColor(0);
    paramGraphics.fillRect(this.A.a, this.A.b, this.A.c, this.A.d);
    bz.c.a(paramGraphics, this.c, this.A.a + (this.A.c >> 1), this.A.b - 4 + (this.A.d >> 1), 1);
    int i = this.v.getWidth() / 3;
    int j = this.v.getHeight();
    for (int k = this.y.length - 1; k > -1; k--) {
      y.a(paramGraphics, this.v, 0, 0, i, j, this.y[k], this.z[k], 3);
    }
    y.a(paramGraphics, this.v, i, 0, i, j, this.y[this.x], this.z[this.x], 3);
    y.a(paramGraphics, this.v, i + i, 0, i, j, this.y[this.w], this.z[this.w], 3);
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.g
 * JD-Core Version:    0.7.0.1
 */