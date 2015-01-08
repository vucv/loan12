public class ai
{
  public static int a(byte[] paramArrayOfByte)
  {
    return a(paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3]);
  }
  
  public static int a(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
  {
    return ((paramByte1 & 0xFF) << 24) + ((paramByte2 & 0xFF) << 16) + ((paramByte3 & 0xFF) << 8) + (paramByte4 & 0xFF);
  }
  
  public static int a(byte[] paramArrayOfByte, int paramInt)
  {
    return a(paramArrayOfByte[paramInt], paramArrayOfByte[(paramInt + 1)], paramArrayOfByte[(paramInt + 2)], paramArrayOfByte[(paramInt + 3)]);
  }
  
  public static byte[] a(int paramInt)
  {
    return new byte[] { (byte)(paramInt >>> 24 & 0xFF), (byte)(paramInt >>> 16 & 0xFF), (byte)(paramInt >>> 8 & 0xFF), (byte)(paramInt >>> 0 & 0xFF) };
  }
}


/* Location:           C:\Users\CapVanVu\Downloads\loan-12-su-quan.jar
 * Qualified Name:     ai
 * JD-Core Version:    0.7.0.1
 */