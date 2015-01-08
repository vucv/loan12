package com.mic.smsgame.suquan;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class u
{
  public static void a(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image[] paramArrayOfImage)
  {
    int i = paramArrayOfImage[0].getWidth();
    paramGraphics.setColor(paramInt4);
    paramGraphics.fillRect(paramInt1 + 9, paramInt2 + 9, i - 18, paramInt3 - 18);
    paramGraphics.setColor(3932160);
    paramGraphics.drawRect(paramInt1 + 8, paramInt2 + 8, i - 17, paramInt3 - 17);
    paramGraphics.setColor(14323025);
    paramGraphics.drawRect(paramInt1 + 7, paramInt2 + 7, i - 15, paramInt3 - 15);
    paramGraphics.setColor(1838350);
    paramGraphics.drawRect(paramInt1 + 6, paramInt2 + 6, i - 13, paramInt3 - 13);
    paramGraphics.setColor(9132617);
    paramGraphics.drawRect(paramInt1 + 5, paramInt2 + 5, i - 11, paramInt3 - 11);
    paramGraphics.setColor(0);
    paramGraphics.drawRect(paramInt1 + 4, paramInt2 + 4, i - 9, paramInt3 - 9);
    paramGraphics.drawImage(paramArrayOfImage[0], paramInt1, paramInt2, 20);
    paramGraphics.drawRegion(paramArrayOfImage[0], 0, 0, paramArrayOfImage[0].getWidth(), paramArrayOfImage[0].getHeight(), 1, paramInt1, paramInt2 + paramInt3, 36);
    if (paramArrayOfImage.length > 1)
    {
      paramGraphics.drawImage(paramArrayOfImage[1], paramInt1 + 1, paramInt2 + 23, 17);
      paramGraphics.drawImage(paramArrayOfImage[1], paramInt1 + i - 2, paramInt2 + 23, 17);
    }
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     com.mic.smsgame.suquan.u
 * JD-Core Version:    0.7.0.1
 */