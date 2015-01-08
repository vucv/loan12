public class b
{
  dw a = new dw();
  dw b = null;
  static byte[] c = { -128 };
  private static final char[] d = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  
  private void a(byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
  {
    paramArrayOfInt[0] = (paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 3)] << 24);
    paramArrayOfInt[1] = (paramArrayOfByte[(paramInt + 4)] & 0xFF | (paramArrayOfByte[(paramInt + 5)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 6)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 7)] << 24);
    paramArrayOfInt[2] = (paramArrayOfByte[(paramInt + 8)] & 0xFF | (paramArrayOfByte[(paramInt + 9)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 10)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 11)] << 24);
    paramArrayOfInt[3] = (paramArrayOfByte[(paramInt + 12)] & 0xFF | (paramArrayOfByte[(paramInt + 13)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 14)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 15)] << 24);
    paramArrayOfInt[4] = (paramArrayOfByte[(paramInt + 16)] & 0xFF | (paramArrayOfByte[(paramInt + 17)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 18)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 19)] << 24);
    paramArrayOfInt[5] = (paramArrayOfByte[(paramInt + 20)] & 0xFF | (paramArrayOfByte[(paramInt + 21)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 22)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 23)] << 24);
    paramArrayOfInt[6] = (paramArrayOfByte[(paramInt + 24)] & 0xFF | (paramArrayOfByte[(paramInt + 25)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 26)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 27)] << 24);
    paramArrayOfInt[7] = (paramArrayOfByte[(paramInt + 28)] & 0xFF | (paramArrayOfByte[(paramInt + 29)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 30)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 31)] << 24);
    paramArrayOfInt[8] = (paramArrayOfByte[(paramInt + 32)] & 0xFF | (paramArrayOfByte[(paramInt + 33)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 34)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 35)] << 24);
    paramArrayOfInt[9] = (paramArrayOfByte[(paramInt + 36)] & 0xFF | (paramArrayOfByte[(paramInt + 37)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 38)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 39)] << 24);
    paramArrayOfInt[10] = (paramArrayOfByte[(paramInt + 40)] & 0xFF | (paramArrayOfByte[(paramInt + 41)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 42)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 43)] << 24);
    paramArrayOfInt[11] = (paramArrayOfByte[(paramInt + 44)] & 0xFF | (paramArrayOfByte[(paramInt + 45)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 46)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 47)] << 24);
    paramArrayOfInt[12] = (paramArrayOfByte[(paramInt + 48)] & 0xFF | (paramArrayOfByte[(paramInt + 49)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 50)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 51)] << 24);
    paramArrayOfInt[13] = (paramArrayOfByte[(paramInt + 52)] & 0xFF | (paramArrayOfByte[(paramInt + 53)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 54)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 55)] << 24);
    paramArrayOfInt[14] = (paramArrayOfByte[(paramInt + 56)] & 0xFF | (paramArrayOfByte[(paramInt + 57)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 58)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 59)] << 24);
    paramArrayOfInt[15] = (paramArrayOfByte[(paramInt + 60)] & 0xFF | (paramArrayOfByte[(paramInt + 61)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 62)] & 0xFF) << 16 | paramArrayOfByte[(paramInt + 63)] << 24);
  }
  
  private void a(dw paramdw, byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
  {
    int i = paramdw.a[0];
    int j = paramdw.a[1];
    int k = paramdw.a[2];
    int m = paramdw.a[3];
    int[] arrayOfInt = paramArrayOfInt;
    a(paramArrayOfByte, paramInt, paramArrayOfInt);
    i += (j & k | (j ^ 0xFFFFFFFF) & m) + arrayOfInt[0] + -680876936;
    i = (i << 7 | i >>> 25) + j;
    m += (i & j | (i ^ 0xFFFFFFFF) & k) + arrayOfInt[1] + -389564586;
    m = (m << 12 | m >>> 20) + i;
    k += (m & i | (m ^ 0xFFFFFFFF) & j) + arrayOfInt[2] + 606105819;
    k = (k << 17 | k >>> 15) + m;
    j += (k & m | (k ^ 0xFFFFFFFF) & i) + arrayOfInt[3] + -1044525330;
    j = (j << 22 | j >>> 10) + k;
    i += (j & k | (j ^ 0xFFFFFFFF) & m) + arrayOfInt[4] + -176418897;
    i = (i << 7 | i >>> 25) + j;
    m += (i & j | (i ^ 0xFFFFFFFF) & k) + arrayOfInt[5] + 1200080426;
    m = (m << 12 | m >>> 20) + i;
    k += (m & i | (m ^ 0xFFFFFFFF) & j) + arrayOfInt[6] + -1473231341;
    k = (k << 17 | k >>> 15) + m;
    j += (k & m | (k ^ 0xFFFFFFFF) & i) + arrayOfInt[7] + -45705983;
    j = (j << 22 | j >>> 10) + k;
    i += (j & k | (j ^ 0xFFFFFFFF) & m) + arrayOfInt[8] + 1770035416;
    i = (i << 7 | i >>> 25) + j;
    m += (i & j | (i ^ 0xFFFFFFFF) & k) + arrayOfInt[9] + -1958414417;
    m = (m << 12 | m >>> 20) + i;
    k += (m & i | (m ^ 0xFFFFFFFF) & j) + arrayOfInt[10] + -42063;
    k = (k << 17 | k >>> 15) + m;
    j += (k & m | (k ^ 0xFFFFFFFF) & i) + arrayOfInt[11] + -1990404162;
    j = (j << 22 | j >>> 10) + k;
    i += (j & k | (j ^ 0xFFFFFFFF) & m) + arrayOfInt[12] + 1804603682;
    i = (i << 7 | i >>> 25) + j;
    m += (i & j | (i ^ 0xFFFFFFFF) & k) + arrayOfInt[13] + -40341101;
    m = (m << 12 | m >>> 20) + i;
    k += (m & i | (m ^ 0xFFFFFFFF) & j) + arrayOfInt[14] + -1502002290;
    k = (k << 17 | k >>> 15) + m;
    j += (k & m | (k ^ 0xFFFFFFFF) & i) + arrayOfInt[15] + 1236535329;
    j = (j << 22 | j >>> 10) + k;
    i += (j & m | k & (m ^ 0xFFFFFFFF)) + arrayOfInt[1] + -165796510;
    i = (i << 5 | i >>> 27) + j;
    m += (i & k | j & (k ^ 0xFFFFFFFF)) + arrayOfInt[6] + -1069501632;
    m = (m << 9 | m >>> 23) + i;
    k += (m & j | i & (j ^ 0xFFFFFFFF)) + arrayOfInt[11] + 643717713;
    k = (k << 14 | k >>> 18) + m;
    j += (k & i | m & (i ^ 0xFFFFFFFF)) + arrayOfInt[0] + -373897302;
    j = (j << 20 | j >>> 12) + k;
    i += (j & m | k & (m ^ 0xFFFFFFFF)) + arrayOfInt[5] + -701558691;
    i = (i << 5 | i >>> 27) + j;
    m += (i & k | j & (k ^ 0xFFFFFFFF)) + arrayOfInt[10] + 38016083;
    m = (m << 9 | m >>> 23) + i;
    k += (m & j | i & (j ^ 0xFFFFFFFF)) + arrayOfInt[15] + -660478335;
    k = (k << 14 | k >>> 18) + m;
    j += (k & i | m & (i ^ 0xFFFFFFFF)) + arrayOfInt[4] + -405537848;
    j = (j << 20 | j >>> 12) + k;
    i += (j & m | k & (m ^ 0xFFFFFFFF)) + arrayOfInt[9] + 568446438;
    i = (i << 5 | i >>> 27) + j;
    m += (i & k | j & (k ^ 0xFFFFFFFF)) + arrayOfInt[14] + -1019803690;
    m = (m << 9 | m >>> 23) + i;
    k += (m & j | i & (j ^ 0xFFFFFFFF)) + arrayOfInt[3] + -187363961;
    k = (k << 14 | k >>> 18) + m;
    j += (k & i | m & (i ^ 0xFFFFFFFF)) + arrayOfInt[8] + 1163531501;
    j = (j << 20 | j >>> 12) + k;
    i += (j & m | k & (m ^ 0xFFFFFFFF)) + arrayOfInt[13] + -1444681467;
    i = (i << 5 | i >>> 27) + j;
    m += (i & k | j & (k ^ 0xFFFFFFFF)) + arrayOfInt[2] + -51403784;
    m = (m << 9 | m >>> 23) + i;
    k += (m & j | i & (j ^ 0xFFFFFFFF)) + arrayOfInt[7] + 1735328473;
    k = (k << 14 | k >>> 18) + m;
    j += (k & i | m & (i ^ 0xFFFFFFFF)) + arrayOfInt[12] + -1926607734;
    j = (j << 20 | j >>> 12) + k;
    i += (j ^ k ^ m) + arrayOfInt[5] + -378558;
    i = (i << 4 | i >>> 28) + j;
    m += (i ^ j ^ k) + arrayOfInt[8] + -2022574463;
    m = (m << 11 | m >>> 21) + i;
    k += (m ^ i ^ j) + arrayOfInt[11] + 1839030562;
    k = (k << 16 | k >>> 16) + m;
    j += (k ^ m ^ i) + arrayOfInt[14] + -35309556;
    j = (j << 23 | j >>> 9) + k;
    i += (j ^ k ^ m) + arrayOfInt[1] + -1530992060;
    i = (i << 4 | i >>> 28) + j;
    m += (i ^ j ^ k) + arrayOfInt[4] + 1272893353;
    m = (m << 11 | m >>> 21) + i;
    k += (m ^ i ^ j) + arrayOfInt[7] + -155497632;
    k = (k << 16 | k >>> 16) + m;
    j += (k ^ m ^ i) + arrayOfInt[10] + -1094730640;
    j = (j << 23 | j >>> 9) + k;
    i += (j ^ k ^ m) + arrayOfInt[13] + 681279174;
    i = (i << 4 | i >>> 28) + j;
    m += (i ^ j ^ k) + arrayOfInt[0] + -358537222;
    m = (m << 11 | m >>> 21) + i;
    k += (m ^ i ^ j) + arrayOfInt[3] + -722521979;
    k = (k << 16 | k >>> 16) + m;
    j += (k ^ m ^ i) + arrayOfInt[6] + 76029189;
    j = (j << 23 | j >>> 9) + k;
    i += (j ^ k ^ m) + arrayOfInt[9] + -640364487;
    i = (i << 4 | i >>> 28) + j;
    m += (i ^ j ^ k) + arrayOfInt[12] + -421815835;
    m = (m << 11 | m >>> 21) + i;
    k += (m ^ i ^ j) + arrayOfInt[15] + 530742520;
    k = (k << 16 | k >>> 16) + m;
    j += (k ^ m ^ i) + arrayOfInt[2] + -995338651;
    j = (j << 23 | j >>> 9) + k;
    i += (k ^ (j | m ^ 0xFFFFFFFF)) + arrayOfInt[0] + -198630844;
    i = (i << 6 | i >>> 26) + j;
    m += (j ^ (i | k ^ 0xFFFFFFFF)) + arrayOfInt[7] + 1126891415;
    m = (m << 10 | m >>> 22) + i;
    k += (i ^ (m | j ^ 0xFFFFFFFF)) + arrayOfInt[14] + -1416354905;
    k = (k << 15 | k >>> 17) + m;
    j += (m ^ (k | i ^ 0xFFFFFFFF)) + arrayOfInt[5] + -57434055;
    j = (j << 21 | j >>> 11) + k;
    i += (k ^ (j | m ^ 0xFFFFFFFF)) + arrayOfInt[12] + 1700485571;
    i = (i << 6 | i >>> 26) + j;
    m += (j ^ (i | k ^ 0xFFFFFFFF)) + arrayOfInt[3] + -1894986606;
    m = (m << 10 | m >>> 22) + i;
    k += (i ^ (m | j ^ 0xFFFFFFFF)) + arrayOfInt[10] + -1051523;
    k = (k << 15 | k >>> 17) + m;
    j += (m ^ (k | i ^ 0xFFFFFFFF)) + arrayOfInt[1] + -2054922799;
    j = (j << 21 | j >>> 11) + k;
    i += (k ^ (j | m ^ 0xFFFFFFFF)) + arrayOfInt[8] + 1873313359;
    i = (i << 6 | i >>> 26) + j;
    m += (j ^ (i | k ^ 0xFFFFFFFF)) + arrayOfInt[15] + -30611744;
    m = (m << 10 | m >>> 22) + i;
    k += (i ^ (m | j ^ 0xFFFFFFFF)) + arrayOfInt[6] + -1560198380;
    k = (k << 15 | k >>> 17) + m;
    j += (m ^ (k | i ^ 0xFFFFFFFF)) + arrayOfInt[13] + 1309151649;
    j = (j << 21 | j >>> 11) + k;
    i += (k ^ (j | m ^ 0xFFFFFFFF)) + arrayOfInt[4] + -145523070;
    i = (i << 6 | i >>> 26) + j;
    m += (j ^ (i | k ^ 0xFFFFFFFF)) + arrayOfInt[11] + -1120210379;
    m = (m << 10 | m >>> 22) + i;
    k += (i ^ (m | j ^ 0xFFFFFFFF)) + arrayOfInt[2] + 718787259;
    k = (k << 15 | k >>> 17) + m;
    j += (m ^ (k | i ^ 0xFFFFFFFF)) + arrayOfInt[9] + -343485551;
    j = (j << 21 | j >>> 11) + k;
    paramdw.a[0] += i;
    paramdw.a[1] += j;
    paramdw.a[2] += k;
    paramdw.a[3] += m;
  }
  
  private void a(dw paramdw, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.b = null;
    if (paramInt2 - paramInt1 > paramArrayOfByte.length) {
      paramInt2 = paramArrayOfByte.length - paramInt1;
    }
    int i = (int)(paramdw.b & 0x3F);
    paramdw.b += paramInt2;
    int j = 64 - i;
    int k;
    if (paramInt2 >= j)
    {
      int[] arrayOfInt = new int[16];
      if (j == 64)
      {
        j = 0;
      }
      else
      {
        for (k = 0; k < j; k++) {
          paramdw.c[(k + i)] = paramArrayOfByte[(k + paramInt1)];
        }
        a(paramdw, paramdw.c, 0, arrayOfInt);
      }
      for (k = j; k + 63 < paramInt2; k += 64) {
        a(paramdw, paramArrayOfByte, k + paramInt1, arrayOfInt);
      }
      i = 0;
    }
    else
    {
      k = 0;
    }
    if (k < paramInt2)
    {
      int m = k;
      while (k < paramInt2)
      {
        paramdw.c[(i + k - m)] = paramArrayOfByte[(k + paramInt1)];
        k++;
      }
    }
  }
  
  private byte[] a(int[] paramArrayOfInt, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    int j;
    int i = j = 0;
    while (j < paramInt)
    {
      arrayOfByte[j] = ((byte)(paramArrayOfInt[i] & 0xFF));
      arrayOfByte[(j + 1)] = ((byte)(paramArrayOfInt[i] >>> 8 & 0xFF));
      arrayOfByte[(j + 2)] = ((byte)(paramArrayOfInt[i] >>> 16 & 0xFF));
      arrayOfByte[(j + 3)] = ((byte)(paramArrayOfInt[i] >>> 24 & 0xFF));
      i++;
      j += 4;
    }
    return arrayOfByte;
  }
  
  private byte[] a()
  {
    if (this.b == null)
    {
      dw localdw = new dw(this.a);
      int[] arrayOfInt = { (int)(localdw.b << 3), (int)(localdw.b >> 29) };
      byte[] arrayOfByte = a(arrayOfInt, 8);
      int i = (int)(localdw.b & 0x3F);
      int j = i < 56 ? 56 - i : 120 - i;
      a(localdw, c, 0, j);
      a(localdw, arrayOfByte, 0, 8);
      this.b = localdw;
    }
    return a(this.b.a, 16);
  }
  
  public static byte[] a(String paramString)
  {
    return a(paramString, null);
  }
  
  public static byte[] a(String paramString1, String paramString2)
  {
    return a(ao.c(paramString1, paramString2));
  }
  
  public static byte[] a(byte[] paramArrayOfByte)
  {
    return a(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static byte[] a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    b localb = new b();
    localb.a(localb.a, paramArrayOfByte, 0, paramArrayOfByte.length);
    return localb.a();
  }
  
  public static String b(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar = new char[paramArrayOfByte.length * 2];
    int i = 0;
    int j = 0;
    while (i < paramArrayOfByte.length)
    {
      arrayOfChar[(j++)] = d[(paramArrayOfByte[i] >>> 4 & 0xF)];
      arrayOfChar[(j++)] = d[(paramArrayOfByte[i] & 0xF)];
      i++;
    }
    return new String(arrayOfChar);
  }
  
  public static boolean a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1 == null) {
      return paramArrayOfByte2 == null;
    }
    if (paramArrayOfByte2 == null) {
      return false;
    }
    int i = 16;
    if (paramArrayOfByte1.length < 16)
    {
      if (paramArrayOfByte2.length != paramArrayOfByte1.length) {
        return false;
      }
      i = paramArrayOfByte1.length;
    }
    else if (paramArrayOfByte2.length < 16)
    {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (paramArrayOfByte1[j] != paramArrayOfByte2[j]) {
        return false;
      }
    }
    return true;
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     b
 * JD-Core Version:    0.7.0.1
 */