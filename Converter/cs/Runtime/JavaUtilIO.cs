// Hand-written C# replacements for the java.util and java.io classes (CLDC 1.0 subset) used by the
// converted game and the MIDP emulation layer. Byte arrays are sbyte[] like Java's signed byte[].
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;

namespace J2ME
{
    /// <summary>java.util.Enumeration</summary>
    public interface Enumeration
    {
        bool hasMoreElements();
        object nextElement();
    }

    /// <summary>java.util.Vector (equality uses Equals like Java's equals()).</summary>
    public class Vector
    {
        protected readonly List<object> items;

        public Vector()
        {
            items = new List<object>();
        }

        public Vector(int initialCapacity)
        {
            items = new List<object>(Math.Max(0, initialCapacity));
        }

        public Vector(int initialCapacity, int capacityIncrement) : this(initialCapacity)
        {
        }

        public int size()
        {
            return items.Count;
        }

        public bool isEmpty()
        {
            return items.Count == 0;
        }

        public int capacity()
        {
            return items.Capacity;
        }

        void Check(int index, int limit)
        {
            if (index < 0 || index >= limit)
            {
                throw new IndexOutOfRangeException(index + " >= " + limit);
            }
        }

        public object elementAt(int index)
        {
            Check(index, items.Count);
            return items[index];
        }

        public object firstElement()
        {
            if (items.Count == 0)
            {
                throw new NoSuchElementException();
            }
            return items[0];
        }

        public object lastElement()
        {
            if (items.Count == 0)
            {
                throw new NoSuchElementException();
            }
            return items[items.Count - 1];
        }

        public void setElementAt(object obj, int index)
        {
            Check(index, items.Count);
            items[index] = obj;
        }

        public void removeElementAt(int index)
        {
            Check(index, items.Count);
            items.RemoveAt(index);
        }

        public void insertElementAt(object obj, int index)
        {
            Check(index, items.Count + 1);
            items.Insert(index, obj);
        }

        public void addElement(object obj)
        {
            items.Add(obj);
        }

        public bool removeElement(object obj)
        {
            int i = indexOf(obj);
            if (i < 0)
            {
                return false;
            }
            items.RemoveAt(i);
            return true;
        }

        public void removeAllElements()
        {
            items.Clear();
        }

        public int indexOf(object obj)
        {
            return indexOf(obj, 0);
        }

        public int indexOf(object obj, int index)
        {
            for (int i = Math.Max(0, index); i < items.Count; i++)
            {
                if (obj == null ? items[i] == null : obj.Equals(items[i]))
                {
                    return i;
                }
            }
            return -1;
        }

        public int lastIndexOf(object obj)
        {
            for (int i = items.Count - 1; i >= 0; i--)
            {
                if (obj == null ? items[i] == null : obj.Equals(items[i]))
                {
                    return i;
                }
            }
            return -1;
        }

        public bool contains(object obj)
        {
            return indexOf(obj) >= 0;
        }

        public void copyInto(object[] anArray)
        {
            items.CopyTo(anArray, 0);
        }

        public void setSize(int newSize)
        {
            while (items.Count > newSize)
            {
                items.RemoveAt(items.Count - 1);
            }
            while (items.Count < newSize)
            {
                items.Add(null);
            }
        }

        public void trimToSize()
        {
        }

        public void ensureCapacity(int minCapacity)
        {
            if (items.Capacity < minCapacity)
            {
                items.Capacity = minCapacity;
            }
        }

        public Enumeration elements()
        {
            return new VectorEnumeration(this);
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < items.Count; i++)
            {
                if (i > 0)
                {
                    sb.Append(", ");
                }
                sb.Append(JString.valueOf(items[i]));
            }
            return sb.Append(']').ToString();
        }

        sealed class VectorEnumeration : Enumeration
        {
            readonly Vector v;
            int next;

            public VectorEnumeration(Vector v)
            {
                this.v = v;
            }

            public bool hasMoreElements()
            {
                return next < v.items.Count;
            }

            public object nextElement()
            {
                if (next >= v.items.Count)
                {
                    throw new NoSuchElementException();
                }
                return v.items[next++];
            }
        }
    }

    /// <summary>java.util.Stack</summary>
    public class Stack : Vector
    {
        public object push(object item)
        {
            items.Add(item);
            return item;
        }

        public object pop()
        {
            object o = peek();
            items.RemoveAt(items.Count - 1);
            return o;
        }

        public object peek()
        {
            if (items.Count == 0)
            {
                throw new EmptyStackException();
            }
            return items[items.Count - 1];
        }

        public bool empty()
        {
            return items.Count == 0;
        }

        public int search(object o)
        {
            int i = lastIndexOf(o);
            return i >= 0 ? items.Count - i : -1;
        }
    }

    /// <summary>java.util.Random with Java's exact 48-bit LCG (same sequence for the same seed).</summary>
    public class Random
    {
        private const long Multiplier = 0x5DEECE66DL;
        private const long Addend = 0xBL;
        private const long Mask = (1L << 48) - 1;
        private long seed;

        public Random() : this(JSystem.currentTimeMillis() ^ 0x2A5B3C4DL)
        {
        }

        public Random(long seed)
        {
            setSeed(seed);
        }

        public void setSeed(long seed)
        {
            this.seed = (seed ^ Multiplier) & Mask;
        }

        protected int next(int bits)
        {
            seed = unchecked(seed * Multiplier + Addend) & Mask;
            return (int) ((ulong) seed >> (48 - bits));
        }

        public int nextInt()
        {
            return next(32);
        }

        public int nextInt(int n)
        {
            if (n <= 0)
            {
                throw new ArgumentException("n must be positive");
            }
            if ((n & -n) == n)
            {
                return (int) ((n * (long) next(31)) >> 31);
            }
            int bits, val;
            do
            {
                bits = next(31);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        public long nextLong()
        {
            return unchecked(((long) next(32) << 32) + next(32));
        }

        public bool nextBoolean()
        {
            return next(1) != 0;
        }

        public float nextFloat()
        {
            return next(24) / (float) (1 << 24);
        }

        public double nextDouble()
        {
            return (((long) next(26) << 27) + next(27)) * (1.0 / (1L << 53));
        }
    }

    /// <summary>java.util.Date (milliseconds since 1970).</summary>
    public class Date
    {
        private long millis;

        public Date() : this(JSystem.currentTimeMillis())
        {
        }

        public Date(long millis)
        {
            this.millis = millis;
        }

        public long getTime()
        {
            return millis;
        }

        public void setTime(long time)
        {
            millis = time;
        }

        public override bool Equals(object o)
        {
            Date d = o as Date;
            return d != null && d.millis == millis;
        }

        public override int GetHashCode()
        {
            return (int) millis ^ (int) (millis >> 32);
        }

        /// <summary>Java format: "Thu Jan 01 07:00:00 GMT+07:00 1970".</summary>
        public override string ToString()
        {
            DateTimeOffset t = DateTimeOffset.FromUnixTimeMilliseconds(millis).ToLocalTime();
            TimeSpan off = t.Offset;
            string zone = "GMT" + (off < TimeSpan.Zero ? "-" : "+") + off.ToString("hh\\:mm", CultureInfo.InvariantCulture);
            return t.ToString("ddd MMM dd HH:mm:ss", CultureInfo.InvariantCulture) + " " + zone + " " +
                   t.Year.ToString(CultureInfo.InvariantCulture);
        }
    }

    // ============================================================================================
    // java.io

    /// <summary>java.io.InputStream</summary>
    public abstract class InputStream
    {
        public abstract int read();

        public virtual int read(sbyte[] b)
        {
            return read(b, 0, b.Length);
        }

        public virtual int read(sbyte[] b, int off, int len)
        {
            if (b == null)
            {
                throw new NullReferenceException();
            }
            if (off < 0 || len < 0 || off > b.Length - len)
            {
                throw new IndexOutOfRangeException();
            }
            if (len == 0)
            {
                return 0;
            }
            int c = read();
            if (c == -1)
            {
                return -1;
            }
            b[off] = (sbyte) c;
            int i = 1;
            for (; i < len; i++)
            {
                c = read();
                if (c == -1)
                {
                    break;
                }
                b[off + i] = (sbyte) c;
            }
            return i;
        }

        public virtual long skip(long n)
        {
            long remaining = n;
            while (remaining > 0)
            {
                if (read() < 0)
                {
                    break;
                }
                remaining--;
            }
            return n - remaining;
        }

        public virtual int available()
        {
            return 0;
        }

        public virtual void close()
        {
        }

        public virtual void mark(int readlimit)
        {
        }

        public virtual void reset()
        {
            throw new IOException("mark/reset not supported");
        }

        public virtual bool markSupported()
        {
            return false;
        }
    }

    /// <summary>java.io.ByteArrayInputStream</summary>
    public class ByteArrayInputStream : InputStream
    {
        protected sbyte[] buf;
        protected int pos;
        protected int markPos;
        protected int count;

        public ByteArrayInputStream(sbyte[] buf) : this(buf, 0, buf.Length)
        {
        }

        public ByteArrayInputStream(sbyte[] buf, int offset, int length)
        {
            this.buf = buf;
            pos = offset;
            count = Math.Min(offset + length, buf.Length);
            markPos = offset;
        }

        public override int read()
        {
            return pos < count ? (buf[pos++] & 0xff) : -1;
        }

        public override int read(sbyte[] b, int off, int len)
        {
            if (b == null)
            {
                throw new NullReferenceException();
            }
            if (off < 0 || len < 0 || off > b.Length - len)
            {
                throw new IndexOutOfRangeException();
            }
            if (pos >= count)
            {
                return -1;
            }
            if (pos + len > count)
            {
                len = count - pos;
            }
            if (len <= 0)
            {
                return 0;
            }
            Buffer.BlockCopy(buf, pos, b, off, len);
            pos += len;
            return len;
        }

        public override long skip(long n)
        {
            if (pos + n > count)
            {
                n = count - pos;
            }
            if (n < 0)
            {
                return 0;
            }
            pos += (int) n;
            return n;
        }

        public override int available()
        {
            return count - pos;
        }

        public override bool markSupported()
        {
            return true;
        }

        public override void mark(int readAheadLimit)
        {
            markPos = pos;
        }

        public override void reset()
        {
            pos = markPos;
        }
    }

    /// <summary>java.io.OutputStream</summary>
    public abstract class OutputStream
    {
        public abstract void write(int b);

        public virtual void write(sbyte[] b)
        {
            write(b, 0, b.Length);
        }

        public virtual void write(sbyte[] b, int off, int len)
        {
            if (off < 0 || len < 0 || off > b.Length - len)
            {
                throw new IndexOutOfRangeException();
            }
            for (int i = 0; i < len; i++)
            {
                write(b[off + i]);
            }
        }

        public virtual void flush()
        {
        }

        public virtual void close()
        {
        }
    }

    /// <summary>java.io.ByteArrayOutputStream</summary>
    public class ByteArrayOutputStream : OutputStream
    {
        protected sbyte[] buf;
        protected int count;

        public ByteArrayOutputStream() : this(32)
        {
        }

        public ByteArrayOutputStream(int size)
        {
            buf = new sbyte[Math.Max(1, size)];
        }

        void Ensure(int needed)
        {
            if (needed > buf.Length)
            {
                sbyte[] n = new sbyte[Math.Max(buf.Length * 2, needed)];
                Buffer.BlockCopy(buf, 0, n, 0, count);
                buf = n;
            }
        }

        public override void write(int b)
        {
            Ensure(count + 1);
            buf[count++] = unchecked((sbyte) b);
        }

        public override void write(sbyte[] b, int off, int len)
        {
            if (off < 0 || len < 0 || off > b.Length - len)
            {
                throw new IndexOutOfRangeException();
            }
            Ensure(count + len);
            Buffer.BlockCopy(b, off, buf, count, len);
            count += len;
        }

        public void reset()
        {
            count = 0;
        }

        public int size()
        {
            return count;
        }

        public sbyte[] toByteArray()
        {
            sbyte[] r = new sbyte[count];
            Buffer.BlockCopy(buf, 0, r, 0, count);
            return r;
        }

        public override string ToString()
        {
            return JString.newString(buf, 0, count);
        }
    }

    /// <summary>java.io.DataInputStream (big-endian, Java "modified UTF-8" strings).</summary>
    public class DataInputStream : InputStream
    {
        protected InputStream @in;

        public DataInputStream(InputStream @in)
        {
            this.@in = @in;
        }

        public override int read()
        {
            return @in.read();
        }

        public override int read(sbyte[] b, int off, int len)
        {
            return @in.read(b, off, len);
        }

        public override long skip(long n)
        {
            return @in.skip(n);
        }

        public override int available()
        {
            return @in.available();
        }

        public override void close()
        {
            @in.close();
        }

        public override void mark(int readlimit)
        {
            @in.mark(readlimit);
        }

        public override void reset()
        {
            @in.reset();
        }

        public override bool markSupported()
        {
            return @in.markSupported();
        }

        int ReadOrEof()
        {
            int c = @in.read();
            if (c < 0)
            {
                throw new EndOfStreamException();
            }
            return c;
        }

        public void readFully(sbyte[] b)
        {
            readFully(b, 0, b.Length);
        }

        public void readFully(sbyte[] b, int off, int len)
        {
            int n = 0;
            while (n < len)
            {
                int r = @in.read(b, off + n, len - n);
                if (r < 0)
                {
                    throw new EndOfStreamException();
                }
                n += r;
            }
        }

        public int skipBytes(int n)
        {
            int total = 0;
            while (total < n)
            {
                long cur = @in.skip(n - total);
                if (cur <= 0)
                {
                    break;
                }
                total += (int) cur;
            }
            return total;
        }

        public bool readBoolean()
        {
            return ReadOrEof() != 0;
        }

        public sbyte readByte()
        {
            return unchecked((sbyte) ReadOrEof());
        }

        public int readUnsignedByte()
        {
            return ReadOrEof();
        }

        public short readShort()
        {
            int a = ReadOrEof();
            int b = ReadOrEof();
            return unchecked((short) ((a << 8) | b));
        }

        public int readUnsignedShort()
        {
            int a = ReadOrEof();
            int b = ReadOrEof();
            return (a << 8) | b;
        }

        public char readChar()
        {
            int a = ReadOrEof();
            int b = ReadOrEof();
            return (char) ((a << 8) | b);
        }

        public int readInt()
        {
            int a = ReadOrEof();
            int b = ReadOrEof();
            int c = ReadOrEof();
            int d = ReadOrEof();
            return (a << 24) | (b << 16) | (c << 8) | d;
        }

        public long readLong()
        {
            long hi = readInt() & 0xFFFFFFFFL;
            long lo = readInt() & 0xFFFFFFFFL;
            return unchecked((long) ((ulong) hi << 32 | (ulong) lo));
        }

        public string readUTF()
        {
            return readUTF(this);
        }

        public static string readUTF(DataInputStream input)
        {
            int utflen = input.readUnsignedShort();
            sbyte[] bytes = new sbyte[utflen];
            input.readFully(bytes, 0, utflen);
            StringBuilder sb = new StringBuilder(utflen);
            int i = 0;
            while (i < utflen)
            {
                int c = bytes[i] & 0xff;
                switch (c >> 4)
                {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        i++;
                        sb.Append((char) c);
                        break;
                    case 12:
                    case 13:
                    {
                        if (i + 2 > utflen)
                        {
                            throw new UTFDataFormatException("malformed input: partial character at end");
                        }
                        int c2 = bytes[i + 1] & 0xff;
                        if ((c2 & 0xC0) != 0x80)
                        {
                            throw new UTFDataFormatException("malformed input around byte " + i);
                        }
                        sb.Append((char) (((c & 0x1F) << 6) | (c2 & 0x3F)));
                        i += 2;
                        break;
                    }
                    case 14:
                    {
                        if (i + 3 > utflen)
                        {
                            throw new UTFDataFormatException("malformed input: partial character at end");
                        }
                        int c2 = bytes[i + 1] & 0xff;
                        int c3 = bytes[i + 2] & 0xff;
                        if ((c2 & 0xC0) != 0x80 || (c3 & 0xC0) != 0x80)
                        {
                            throw new UTFDataFormatException("malformed input around byte " + i);
                        }
                        sb.Append((char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F)));
                        i += 3;
                        break;
                    }
                    default:
                        throw new UTFDataFormatException("malformed input around byte " + i);
                }
            }
            return sb.ToString();
        }
    }

    /// <summary>java.io.DataOutputStream</summary>
    public class DataOutputStream : OutputStream
    {
        protected OutputStream @out;
        protected int written;

        public DataOutputStream(OutputStream @out)
        {
            this.@out = @out;
        }

        public override void write(int b)
        {
            @out.write(b);
            written++;
        }

        public override void write(sbyte[] b, int off, int len)
        {
            @out.write(b, off, len);
            written += len;
        }

        public override void flush()
        {
            @out.flush();
        }

        public override void close()
        {
            @out.flush();
            @out.close();
        }

        public int size()
        {
            return written;
        }

        public void writeBoolean(bool v)
        {
            write(v ? 1 : 0);
        }

        public void writeByte(int v)
        {
            write(v);
        }

        public void writeShort(int v)
        {
            write((v >> 8) & 0xFF);
            write(v & 0xFF);
        }

        public void writeChar(int v)
        {
            writeShort(v);
        }

        public void writeInt(int v)
        {
            write((v >> 24) & 0xFF);
            write((v >> 16) & 0xFF);
            write((v >> 8) & 0xFF);
            write(v & 0xFF);
        }

        public void writeLong(long v)
        {
            writeInt((int) (v >> 32));
            writeInt((int) v);
        }

        public void writeChars(string s)
        {
            for (int i = 0; i < s.Length; i++)
            {
                writeChar(s[i]);
            }
        }

        public void writeUTF(string str)
        {
            int utflen = 0;
            for (int i = 0; i < str.Length; i++)
            {
                char c = str[i];
                if (c >= 0x0001 && c <= 0x007F)
                {
                    utflen++;
                }
                else if (c > 0x07FF)
                {
                    utflen += 3;
                }
                else
                {
                    utflen += 2;
                }
            }
            if (utflen > 65535)
            {
                throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes");
            }
            sbyte[] bytes = new sbyte[utflen + 2];
            int k = 0;
            bytes[k++] = unchecked((sbyte) ((utflen >> 8) & 0xFF));
            bytes[k++] = unchecked((sbyte) (utflen & 0xFF));
            for (int i = 0; i < str.Length; i++)
            {
                char c = str[i];
                if (c >= 0x0001 && c <= 0x007F)
                {
                    bytes[k++] = (sbyte) c;
                }
                else if (c > 0x07FF)
                {
                    bytes[k++] = unchecked((sbyte) (0xE0 | ((c >> 12) & 0x0F)));
                    bytes[k++] = unchecked((sbyte) (0x80 | ((c >> 6) & 0x3F)));
                    bytes[k++] = unchecked((sbyte) (0x80 | (c & 0x3F)));
                }
                else
                {
                    bytes[k++] = unchecked((sbyte) (0xC0 | ((c >> 6) & 0x1F)));
                    bytes[k++] = unchecked((sbyte) (0x80 | (c & 0x3F)));
                }
            }
            write(bytes, 0, k);
        }
    }
}
