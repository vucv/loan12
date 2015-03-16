public class ao {
    public static boolean a(String paramString) {
        if (paramString == null) {
            return true;
        }
        String str = paramString.trim();
        return (str == null) || (str.length() == 0);
    }

    public static boolean a(String paramString1, String paramString2) {
        if ((paramString1 == null) || (paramString2 == null)) {
            return false;
        }
        String str1 = paramString1.toLowerCase();
        String str2 = paramString2.toLowerCase();
        return str1.equals(str2);
    }

    public static String[] b(String paramString1, String paramString2) {
        return a(paramString1, paramString2, -1, false);
    }

    public static String[] a(String paramString1, String paramString2, int paramInt, boolean paramBoolean) {
        if ((paramInt == 0) || (paramString1 == null)) {
            return null;
        }
        if (paramString2 == null) {
            return new String[]{paramString1};
        }
        di localdi = new di();
        String str1 = paramString1;
        int i = paramString2.length();
        int j = 0;
        if (paramBoolean) {
            j = str1.toLowerCase().indexOf(paramString2.toLowerCase());
        } else {
            j = str1.indexOf(paramString2);
        }
        if (j >= 0) {
            int k = 0;
            while (j >= 0) {
                if (paramInt == 1) {
                    if (!a(str1)) {
                        localdi.a(str1);
                    }
                    return a(localdi);
                }
                if (j > k) {
                    String str2 = str1.substring(k, j);
                    localdi.a(str2);
                    if (paramInt > 0) {
                        paramInt--;
                    }
                }
                k = 0;
                str1 = str1.substring(j + i);
                if (paramBoolean) {
                    j = str1.toLowerCase().indexOf(paramString2.toLowerCase());
                } else {
                    j = str1.indexOf(paramString2);
                }
            }
            if (!a(str1)) {
                localdi.a(str1);
            }
            return a(localdi);
        }
        return new String[]{paramString1};
    }

    public static String[] a(di paramdi) {
        if ((paramdi == null) || (paramdi.b() == 0)) {
            return null;
        }
        int i = paramdi.b();
        String[] arrayOfString = new String[i];
        for (int j = 0; j < i; j++) {
            arrayOfString[j] = ((String) paramdi.b(j));
        }
        return arrayOfString;
    }

    public static byte[] b(String paramString) {
        return c(paramString, null);
    }

    public static byte[] c(String paramString1, String paramString2) {
        if (paramString2 == null) {
            paramString2 = "UTF-8";
        }
        return paramString1.getBytes(paramString2);
    }

    public static String a(byte[] paramArrayOfByte) {
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
            return null;
        }
        try {
            return new String(paramArrayOfByte, 0, paramArrayOfByte.length, "UTF-8");
        } catch (Throwable localThrowable) {
        }
        return new String(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static String a(String paramString1, String paramString2, String paramString3) {
        int i = 0;
        int j = 0;
        StringBuffer localStringBuffer = new StringBuffer();
        while ((j = paramString1.indexOf(paramString2, i)) >= 0) {
            localStringBuffer.append(paramString1.substring(i, j));
            localStringBuffer.append(paramString3);
            i = j + paramString2.length();
        }
        localStringBuffer.append(paramString1.substring(i));
        return localStringBuffer.toString();
    }
}



/* Location:           Z:\home\likewise-open\SUTRIXMEDIA1\vu.cap\Downloads\loan-12-su-quan.jar

 * Qualified Name:     ao

 * JD-Core Version:    0.7.0.1

 */