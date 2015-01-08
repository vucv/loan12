package com.mic.smsgame.suquan;

import ay;

public class d
  extends j
{
  private int[][] a = {
          { 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9 },
          { 9, 11, 10, 10, 10, 10, 10, 10, 10, 10, 11, 9 },
          { 9, 11, 1, 4, 0, 2, 3, 5, 5, 0, 11, 9 },
          { 9, 11, 1, 0, 4, 0, 2, 5, 4, 1, 11, 9 },
          { 9, 11, 0, 1, 0, 0, 3, 1, 0, 0, 11, 9 },
          { 9, 11, 1, 0, 5, 1, 1, 0, 0, 5, 11, 9 },
          { 9, 11, 1, 3, 2, 4, 2, 5, 2, 6, 11, 9 },
          { 9, 11, 3, 2, 3, 2, 4, 2, 4, 2, 11, 9 },
          { 9, 11, 4, 4, 1, 3, 0, 4, 2, 5, 11, 9 },
          { 9, 11, 1, 1, 0, 0, 1, 1, 2, 3, 11, 9 },
          { 9, 11, 10, 10, 10, 10, 10, 10, 10, 10, 11, 9 },
          { 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9 } };
  
  public void a(int[][] paramArrayOfInt)
  {
    for (int i = 2; i < 10; i++) {
      for (int j = 2; j < 10; j++) {
        paramArrayOfInt[i][j] = this.a[i][j];
      }
    }
    paramArrayOfInt = this.a;
  }
  
  public void a(int[][] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    paramArrayOfInt[paramInt1][paramInt2] = (1 + ay.a(e.h.length - 1));
  }
}



/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar

 * Qualified Name:     com.mic.smsgame.suquan.d

 * JD-Core Version:    0.7.0.1

 */