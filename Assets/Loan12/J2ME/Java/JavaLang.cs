// Hand-written C# replacements for the java.lang classes (CLDC 1.0 subset) used by the converted game
// and by the MIDP emulation layer. The converter (J2CS) maps java.lang.String / StringBuffer to the
// .NET types and calls these helpers with the Java method names, so the converted code stays close to
// the original. Behaviour follows Java wherever the game can observe a difference.
using System;
using System.Globalization;
using System.Text;

namespace J2ME
{
    /// <summary>java.lang.Runnable</summary>
    public interface Runnable
    {
        void run();
    }

    /// <summary>Static members of java.lang.String (valueOf, constructors).</summary>
    public static class JString
    {
        public static string valueOf(bool b)
        {
            return b ? "true" : "false";
        }

        public static string valueOf(char c)
        {
            return c.ToString();
        }

        public static string valueOf(int i)
        {
            return i.ToString(CultureInfo.InvariantCulture);
        }

        public static string valueOf(long l)
        {
            return l.ToString(CultureInfo.InvariantCulture);
        }

        public static string valueOf(float f)
        {
            return JFormat.FloatToString(f);
        }

        public static string valueOf(double d)
        {
            return JFormat.DoubleToString(d);
        }

        public static string valueOf(char[] chars)
        {
            return new string(chars);
        }

        public static string valueOf(object o)
        {
            return o == null ? "null" : o.ToString();
        }

        public static string newString(sbyte[] bytes)
        {
            return newString(bytes, 0, bytes.Length);
        }

        public static string newString(sbyte[] bytes, int offset, int length)
        {
            // the phones the game targeted (and the Java harness) decode with UTF-8
            CheckRange(bytes.Length, offset, length);
            return Encoding.UTF8.GetString(JBytes.ToBytes(bytes, offset, length));
        }

        public static string newString(sbyte[] bytes, int offset, int length, string encoding)
        {
            CheckRange(bytes.Length, offset, length);
            return JEncoding.Get(encoding).GetString(JBytes.ToBytes(bytes, offset, length));
        }

        public static string newString(sbyte[] bytes, string encoding)
        {
            return newString(bytes, 0, bytes.Length, encoding);
        }

        public static string newString(char[] chars)
        {
            return new string(chars);
        }

        public static string newString(char[] chars, int offset, int length)
        {
            CheckRange(chars.Length, offset, length);
            return new string(chars, offset, length);
        }

        public static string newString(string s)
        {
            return s;
        }

        public static string newString()
        {
            return "";
        }

        static void CheckRange(int total, int offset, int length)
        {
            if (offset < 0 || length < 0 || offset > total - length)
            {
                throw new IndexOutOfRangeException("offset " + offset + ", length " + length + ", size " + total);
            }
        }
    }

    /// <summary>Instance methods of java.lang.String, as extension methods with the Java names.</summary>
    public static class JStringExtensions
    {
        public static int length(this string s)
        {
            return s.Length;
        }

        public static char charAt(this string s, int index)
        {
            return s[index];
        }

        public static bool equals(this string s, object o)
        {
            if (s == null)
            {
                throw new NullReferenceException();
            }
            return s.Equals(o);
        }

        public static bool equalsIgnoreCase(this string s, string o)
        {
            if (s == null)
            {
                throw new NullReferenceException();
            }
            return o != null && string.Equals(s, o, StringComparison.OrdinalIgnoreCase);
        }

        public static int compareTo(this string s, string o)
        {
            return string.CompareOrdinal(s, o);
        }

        public static bool startsWith(this string s, string prefix)
        {
            return s.StartsWith(prefix, StringComparison.Ordinal);
        }

        public static bool startsWith(this string s, string prefix, int offset)
        {
            if (offset < 0 || offset > s.Length - prefix.Length)
            {
                return false;
            }
            return string.CompareOrdinal(s, offset, prefix, 0, prefix.Length) == 0;
        }

        public static bool endsWith(this string s, string suffix)
        {
            return s.EndsWith(suffix, StringComparison.Ordinal);
        }

        public static int indexOf(this string s, int ch)
        {
            return s.IndexOf((char) ch);
        }

        public static int indexOf(this string s, int ch, int fromIndex)
        {
            if (fromIndex < 0)
            {
                fromIndex = 0;
            }
            if (fromIndex >= s.Length)
            {
                return -1;
            }
            return s.IndexOf((char) ch, fromIndex);
        }

        public static int indexOf(this string s, string str)
        {
            return s.IndexOf(str, StringComparison.Ordinal);
        }

        public static int indexOf(this string s, string str, int fromIndex)
        {
            if (fromIndex < 0)
            {
                fromIndex = 0;
            }
            if (fromIndex > s.Length)
            {
                return str.Length == 0 ? s.Length : -1;
            }
            return s.IndexOf(str, fromIndex, StringComparison.Ordinal);
        }

        public static int lastIndexOf(this string s, int ch)
        {
            return s.LastIndexOf((char) ch);
        }

        public static int lastIndexOf(this string s, string str)
        {
            return s.LastIndexOf(str, StringComparison.Ordinal);
        }

        public static string substring(this string s, int beginIndex)
        {
            if (beginIndex < 0 || beginIndex > s.Length)
            {
                throw new ArgumentOutOfRangeException("beginIndex", "String index out of range: " + beginIndex);
            }
            return s.Substring(beginIndex);
        }

        public static string substring(this string s, int beginIndex, int endIndex)
        {
            if (beginIndex < 0 || endIndex > s.Length || beginIndex > endIndex)
            {
                throw new ArgumentOutOfRangeException("beginIndex", "String index out of range: " + beginIndex + ".." + endIndex);
            }
            return s.Substring(beginIndex, endIndex - beginIndex);
        }

        public static string toLowerCase(this string s)
        {
            return s.ToLowerInvariant();
        }

        public static string toUpperCase(this string s)
        {
            return s.ToUpperInvariant();
        }

        /// <summary>Java trims every char &lt;= ' ' (not only Unicode white space).</summary>
        public static string trim(this string s)
        {
            int st = 0;
            int len = s.Length;
            while (st < len && s[st] <= ' ')
            {
                st++;
            }
            while (st < len && s[len - 1] <= ' ')
            {
                len--;
            }
            return (st > 0 || len < s.Length) ? s.Substring(st, len - st) : s;
        }

        public static string replace(this string s, char oldChar, char newChar)
        {
            return s.Replace(oldChar, newChar);
        }

        public static string concat(this string s, string str)
        {
            return s + str;
        }

        public static char[] toCharArray(this string s)
        {
            return s.ToCharArray();
        }

        public static void getChars(this string s, int srcBegin, int srcEnd, char[] dst, int dstBegin)
        {
            s.CopyTo(srcBegin, dst, dstBegin, srcEnd - srcBegin);
        }

        public static sbyte[] getBytes(this string s)
        {
            return JBytes.ToSBytes(Encoding.UTF8.GetBytes(s));
        }

        public static sbyte[] getBytes(this string s, string encoding)
        {
            return JBytes.ToSBytes(JEncoding.Get(encoding).GetBytes(s));
        }

        /// <summary>Java's String.hashCode (s[0]*31^(n-1) + ... + s[n-1]).</summary>
        public static int hashCode(this string s)
        {
            int h = 0;
            for (int i = 0; i < s.Length; i++)
            {
                h = unchecked(31 * h + s[i]);
            }
            return h;
        }

        public static string intern(this string s)
        {
            return string.Intern(s);
        }
    }

    /// <summary>java.lang.StringBuffer methods on System.Text.StringBuilder.</summary>
    public static class JStringBufferExtensions
    {
        public static StringBuilder append(this StringBuilder sb, string s)
        {
            return sb.Append(s ?? "null");
        }

        public static StringBuilder append(this StringBuilder sb, char c)
        {
            return sb.Append(c);
        }

        public static StringBuilder append(this StringBuilder sb, char[] chars)
        {
            return sb.Append(chars);
        }

        public static StringBuilder append(this StringBuilder sb, int i)
        {
            return sb.Append(JString.valueOf(i));
        }

        public static StringBuilder append(this StringBuilder sb, long l)
        {
            return sb.Append(JString.valueOf(l));
        }

        public static StringBuilder append(this StringBuilder sb, bool b)
        {
            return sb.Append(JString.valueOf(b));
        }

        public static StringBuilder append(this StringBuilder sb, float f)
        {
            return sb.Append(JString.valueOf(f));
        }

        public static StringBuilder append(this StringBuilder sb, double d)
        {
            return sb.Append(JString.valueOf(d));
        }

        public static StringBuilder append(this StringBuilder sb, object o)
        {
            return sb.Append(JString.valueOf(o));
        }

        public static int length(this StringBuilder sb)
        {
            return sb.Length;
        }

        public static char charAt(this StringBuilder sb, int index)
        {
            return sb[index];
        }

        public static void setCharAt(this StringBuilder sb, int index, char c)
        {
            sb[index] = c;
        }

        public static void setLength(this StringBuilder sb, int length)
        {
            sb.Length = length;
        }

        public static StringBuilder insert(this StringBuilder sb, int offset, string s)
        {
            return sb.Insert(offset, s ?? "null");
        }

        public static StringBuilder insert(this StringBuilder sb, int offset, char c)
        {
            return sb.Insert(offset, c);
        }

        public static StringBuilder deleteCharAt(this StringBuilder sb, int index)
        {
            return sb.Remove(index, 1);
        }

        public static StringBuilder delete(this StringBuilder sb, int start, int end)
        {
            if (end > sb.Length)
            {
                end = sb.Length;
            }
            return sb.Remove(start, end - start);
        }

        public static StringBuilder reverse(this StringBuilder sb)
        {
            char[] a = sb.ToString().ToCharArray();
            Array.Reverse(a);
            sb.Length = 0;
            return sb.Append(a);
        }
    }

    /// <summary>java.lang.Integer (only what CLDC offers).</summary>
    public sealed class Integer
    {
        public const int MIN_VALUE = int.MinValue;
        public const int MAX_VALUE = int.MaxValue;
        private readonly int value;

        public Integer(int value)
        {
            this.value = value;
        }

        public int intValue()
        {
            return value;
        }

        public long longValue()
        {
            return value;
        }

        public static int parseInt(string s)
        {
            return parseInt(s, 10);
        }

        /// <summary>Java semantics: optional '-', digits of the radix only, no white space, overflow is an error.</summary>
        public static int parseInt(string s, int radix)
        {
            if (s == null)
            {
                throw new FormatException("null");
            }
            int len = s.Length;
            if (len == 0 || radix < 2 || radix > 36)
            {
                throw new FormatException("For input string: \"" + s + "\"");
            }
            int i = 0;
            bool negative = false;
            if (s[0] == '-' || s[0] == '+')
            {
                negative = s[0] == '-';
                i = 1;
                if (len == 1)
                {
                    throw new FormatException("For input string: \"" + s + "\"");
                }
            }
            long result = 0;
            for (; i < len; i++)
            {
                int d = Character.digit(s[i], radix);
                if (d < 0)
                {
                    throw new FormatException("For input string: \"" + s + "\"");
                }
                result = result * radix + d;
                if (result > 2147483648L)
                {
                    throw new FormatException("For input string: \"" + s + "\"");
                }
            }
            if (negative)
            {
                result = -result;
            }
            if (result > int.MaxValue || result < int.MinValue)
            {
                throw new FormatException("For input string: \"" + s + "\"");
            }
            return (int) result;
        }

        public static Integer valueOf(string s)
        {
            return new Integer(parseInt(s));
        }

        public static string toString(int i)
        {
            return JString.valueOf(i);
        }

        public static string toHexString(int i)
        {
            return unchecked((uint) i).ToString("x", CultureInfo.InvariantCulture);
        }

        public override bool Equals(object o)
        {
            Integer other = o as Integer;
            return other != null && other.value == value;
        }

        public override int GetHashCode()
        {
            return value;
        }

        public override string ToString()
        {
            return JString.valueOf(value);
        }
    }

    /// <summary>java.lang.Character helpers.</summary>
    public static class Character
    {
        public static int digit(char c, int radix)
        {
            int d;
            if (c >= '0' && c <= '9')
            {
                d = c - '0';
            }
            else if (c >= 'a' && c <= 'z')
            {
                d = c - 'a' + 10;
            }
            else if (c >= 'A' && c <= 'Z')
            {
                d = c - 'A' + 10;
            }
            else
            {
                return -1;
            }
            return d < radix ? d : -1;
        }

        public static bool isDigit(char c)
        {
            return char.IsDigit(c);
        }

        public static bool isUpperCase(char c)
        {
            return char.IsUpper(c);
        }

        public static bool isLowerCase(char c)
        {
            return char.IsLower(c);
        }

        public static char toLowerCase(char c)
        {
            return char.ToLowerInvariant(c);
        }

        public static char toUpperCase(char c)
        {
            return char.ToUpperInvariant(c);
        }
    }

    /// <summary>java.lang.Math (int versions keep Java's overflow behaviour: abs(MIN_VALUE) == MIN_VALUE).</summary>
    public static class JMath
    {
        public const double PI = Math.PI;
        public const double E = Math.E;

        public static int abs(int a)
        {
            return a < 0 ? unchecked(-a) : a;
        }

        public static long abs(long a)
        {
            return a < 0 ? unchecked(-a) : a;
        }

        public static float abs(float a)
        {
            return Math.Abs(a);
        }

        public static double abs(double a)
        {
            return Math.Abs(a);
        }

        public static int max(int a, int b)
        {
            return a >= b ? a : b;
        }

        public static long max(long a, long b)
        {
            return a >= b ? a : b;
        }

        public static float max(float a, float b)
        {
            return Math.Max(a, b);
        }

        public static double max(double a, double b)
        {
            return Math.Max(a, b);
        }

        public static int min(int a, int b)
        {
            return a <= b ? a : b;
        }

        public static long min(long a, long b)
        {
            return a <= b ? a : b;
        }

        public static float min(float a, float b)
        {
            return Math.Min(a, b);
        }

        public static double min(double a, double b)
        {
            return Math.Min(a, b);
        }

        public static double sin(double a)
        {
            return Math.Sin(a);
        }

        public static double cos(double a)
        {
            return Math.Cos(a);
        }

        public static double tan(double a)
        {
            return Math.Tan(a);
        }

        public static double atan(double a)
        {
            return Math.Atan(a);
        }

        public static double atan2(double y, double x)
        {
            return Math.Atan2(y, x);
        }

        public static double sqrt(double a)
        {
            return Math.Sqrt(a);
        }

        public static double floor(double a)
        {
            return Math.Floor(a);
        }

        public static double ceil(double a)
        {
            return Math.Ceiling(a);
        }

        public static double toRadians(double deg)
        {
            return deg / 180.0 * Math.PI;
        }

        public static double toDegrees(double rad)
        {
            return rad * 180.0 / Math.PI;
        }
    }

    /// <summary>java.lang.System</summary>
    public static class JSystem
    {
        public static readonly PrintStream @out = new PrintStream(false);
        public static readonly PrintStream err = new PrintStream(true);

        public static void arraycopy(object src, int srcPos, object dest, int destPos, int length)
        {
            if (src == null || dest == null)
            {
                throw new NullReferenceException("arraycopy");
            }
            Array s = (Array) src;
            Array d = (Array) dest;
            if (length < 0 || srcPos < 0 || destPos < 0 || srcPos > s.Length - length || destPos > d.Length - length)
            {
                throw new IndexOutOfRangeException("arraycopy: last source index " + (srcPos + length) + " out of bounds");
            }
            Array.Copy(s, srcPos, d, destPos, length);
        }

        /// <summary>Wall clock from the host (the Unity backend uses the real clock).</summary>
        public static long currentTimeMillis()
        {
            if (Host.backend != null)
            {
                return Host.now();
            }
            return DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        }

        public static string getProperty(string key)
        {
            return Host.property(key);
        }

        public static void gc()
        {
            // the CLR collects on its own; a forced collection per screen change would only cause hitches
        }

        public static int identityHashCode(object o)
        {
            return o == null ? 0 : System.Runtime.CompilerServices.RuntimeHelpers.GetHashCode(o);
        }
    }

    /// <summary>System.out / System.err: lines go to the host log.</summary>
    public sealed class PrintStream
    {
        private readonly StringBuilder pending = new StringBuilder();
        private readonly bool isError;

        public PrintStream(bool isError)
        {
            this.isError = isError;
        }

        public void print(string s)
        {
            pending.Append(s ?? "null");
        }

        public void print(object o)
        {
            print(JString.valueOf(o));
        }

        public void print(int i)
        {
            print(JString.valueOf(i));
        }

        public void print(long l)
        {
            print(JString.valueOf(l));
        }

        public void print(char c)
        {
            pending.Append(c);
        }

        public void print(bool b)
        {
            print(JString.valueOf(b));
        }

        public void println()
        {
            string line = pending.ToString();
            pending.Length = 0;
            if (Host.backend != null)
            {
                Host.log(isError ? "[err] " + line : line);
            }
            else
            {
                Console.WriteLine(line);
            }
        }

        public void println(string s)
        {
            print(s);
            println();
        }

        public void println(object o)
        {
            print(o);
            println();
        }

        public void println(int i)
        {
            print(i);
            println();
        }

        public void println(long l)
        {
            print(l);
            println();
        }

        public void println(char c)
        {
            print(c);
            println();
        }

        public void println(bool b)
        {
            print(b);
            println();
        }

        public void flush()
        {
        }
    }

    /// <summary>java.lang.Thread: the port is single threaded, only the static helpers remain.</summary>
    public sealed class Thread
    {
        public static void sleep(long millis)
        {
            System.Threading.Thread.Sleep((int) Math.Min(millis, int.MaxValue));
        }

        public static void yield()
        {
        }
    }

    /// <summary>java.lang.Object members that have no direct C# spelling.</summary>
    public static class JObject
    {
        public static bool equals(object a, object b)
        {
            if (a == null)
            {
                throw new NullReferenceException();
            }
            return a.Equals(b);
        }

        public static string className(Type t)
        {
            return t.FullName;
        }
    }

    /// <summary>java.lang.Class static members.</summary>
    public static class JClass
    {
        /// <summary>No optional J2ME APIs (FileConnection, Nokia UI ...) exist in the port.</summary>
        public static Type forName(string name)
        {
            throw new ClassNotFoundException(name);
        }
    }

    /// <summary>Throwable helpers.</summary>
    public static class JRuntime
    {
        public static void printStackTrace(Exception e)
        {
            Host.log("Exception (caught by the game): " + e);
        }

        public static string getMessage(Exception e)
        {
            return e == null ? null : e.Message;
        }
    }

    /// <summary>Jagged array allocation for Java's rectangular `new T[a][b]`.</summary>
    public static class JArray
    {
        public static T[][] New2<T>(int a, int b)
        {
            if (b < 0)
            {
                throw new OverflowException("negative array size");
            }
            T[][] r = new T[a][];
            for (int i = 0; i < a; i++)
            {
                r[i] = new T[b];
            }
            return r;
        }

        public static T[][][] New3<T>(int a, int b, int c)
        {
            if (b < 0 || c < 0)
            {
                throw new OverflowException("negative array size");
            }
            T[][][] r = new T[a][][];
            for (int i = 0; i < a; i++)
            {
                r[i] = New2<T>(b, c);
            }
            return r;
        }
    }

    /// <summary>byte[] (Java, signed) &lt;-&gt; byte[] (.NET, unsigned) copies.</summary>
    public static class JBytes
    {
        public static byte[] ToBytes(sbyte[] src)
        {
            return ToBytes(src, 0, src.Length);
        }

        public static byte[] ToBytes(sbyte[] src, int offset, int length)
        {
            byte[] r = new byte[length];
            Buffer.BlockCopy(src, offset, r, 0, length);
            return r;
        }

        public static sbyte[] ToSBytes(byte[] src)
        {
            if (src == null)
            {
                return null;
            }
            sbyte[] r = new sbyte[src.Length];
            Buffer.BlockCopy(src, 0, r, 0, src.Length);
            return r;
        }
    }

    static class JEncoding
    {
        public static Encoding Get(string name)
        {
            string n = name == null ? "" : name.Trim().ToUpperInvariant().Replace("_", "-");
            switch (n)
            {
                case "UTF-8":
                case "UTF8":
                    return new UTF8Encoding(false);
                case "ISO-8859-1":
                case "ISO8859-1":
                case "ISO8859_1":
                case "LATIN1":
                    return Encoding.GetEncoding(28591);
                case "US-ASCII":
                case "ASCII":
                    return Encoding.ASCII;
                case "UTF-16BE":
                    return new UnicodeEncoding(true, false);
                case "UTF-16LE":
                    return new UnicodeEncoding(false, false);
                default:
                    throw new UnsupportedEncodingException(name);
            }
        }
    }

    /// <summary>Java-style float/double to string (enough for logs and string concatenation).</summary>
    static class JFormat
    {
        public static string FloatToString(float f)
        {
            if (float.IsNaN(f))
            {
                return "NaN";
            }
            if (float.IsPositiveInfinity(f))
            {
                return "Infinity";
            }
            if (float.IsNegativeInfinity(f))
            {
                return "-Infinity";
            }
            return Decimalize(f.ToString("R", CultureInfo.InvariantCulture));
        }

        public static string DoubleToString(double d)
        {
            if (double.IsNaN(d))
            {
                return "NaN";
            }
            if (double.IsPositiveInfinity(d))
            {
                return "Infinity";
            }
            if (double.IsNegativeInfinity(d))
            {
                return "-Infinity";
            }
            return Decimalize(d.ToString("R", CultureInfo.InvariantCulture));
        }

        static string Decimalize(string s)
        {
            return (s.IndexOf('.') < 0 && s.IndexOf('E') < 0) ? s + ".0" : s;
        }
    }

    // ---- exceptions without a direct .NET counterpart -------------------------------------------
    public class InterruptedException : Exception
    {
        public InterruptedException()
        {
        }

        public InterruptedException(string message) : base(message)
        {
        }
    }

    public class ClassNotFoundException : Exception
    {
        public ClassNotFoundException()
        {
        }

        public ClassNotFoundException(string message) : base(message)
        {
        }
    }

    public class SecurityException : Exception
    {
        public SecurityException()
        {
        }

        public SecurityException(string message) : base(message)
        {
        }
    }

    public class UnsupportedEncodingException : System.IO.IOException
    {
        public UnsupportedEncodingException()
        {
        }

        public UnsupportedEncodingException(string message) : base(message)
        {
        }
    }

    public class InterruptedIOException : System.IO.IOException
    {
        public InterruptedIOException()
        {
        }

        public InterruptedIOException(string message) : base(message)
        {
        }
    }

    public class UTFDataFormatException : System.IO.IOException
    {
        public UTFDataFormatException()
        {
        }

        public UTFDataFormatException(string message) : base(message)
        {
        }
    }

    public class EmptyStackException : SystemException
    {
        public EmptyStackException() : base("stack is empty")
        {
        }
    }

    public class NoSuchElementException : SystemException
    {
        public NoSuchElementException() : base("no such element")
        {
        }
    }
}
