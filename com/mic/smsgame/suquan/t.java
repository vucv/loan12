package com.mic.smsgame.suquan;

import az;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import y;

public class t
{
  private int[] a = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45 };
  private Image b;
  
  public t()
  {
    this.b = az.b("/numbersmall");
  }
  
  public t(Image paramImage)
  {
    this.b = paramImage;
  }
  
  public void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = String.valueOf(paramInt1).length();
    int j = i * 6 - 1;
    switch (paramInt4)
    {
    case 0: 
      paramInt2 += j;
      break;
    case 1: 
      paramInt2 += j / 2;
    }
    paramInt2 -= 5;
    for (int k = 0; k < i; k++)
    {
      int m = paramInt1 % 10;
      y.a(paramGraphics, this.b, this.a[m], 0, 5, 7, paramInt2, paramInt3, 0);
      paramInt1 /= 10;
      paramInt2 -= 6;
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.t
 * JD-Core Version:    0.7.0.1
 */