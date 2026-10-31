import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.code.Symbol.*;
import com.sun.tools.javac.code.Type.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import static com.sun.tools.javac.code.TypeTag.*;

/**
 * Java (1.4-style, as produced by JD-Core + the port fixes) -> C# 9 translator for Unity.
 * Uses javac's attributed trees so every name, overload and conversion is resolved exactly like javac.
 *
 * usage: java J2CS <config> <source files...>
 */
public class J2CS {
    // ------------------------------------------------------------------------------------------------
    // configuration
    static final Map<String, String> PKG_NS = new LinkedHashMap<>();
    static final Map<String, String> PKG_PREFIX = new LinkedHashMap<>();
    static final Map<String, String> CLASS_RENAME = new HashMap<>();
    static final Map<String, String> PKG_OUT = new HashMap<>();
    static final Map<String, String> JDK_TYPE = new HashMap<>();      // instance/declaration type names
    static final Map<String, String> JDK_STATIC = new HashMap<>();    // owner used for static members
    static final Set<String> CS_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "as", "base", "bool", "break", "byte", "case", "catch", "char", "checked", "class", "const",
            "continue", "decimal", "default", "delegate", "do", "double", "else", "enum", "event", "explicit", "extern",
            "false", "finally", "fixed", "float", "for", "foreach", "goto", "if", "implicit", "in", "int", "interface",
            "internal", "is", "lock", "long", "namespace", "new", "null", "object", "operator", "out", "override",
            "params", "private", "protected", "public", "readonly", "ref", "return", "sbyte", "sealed", "short",
            "sizeof", "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof",
            "uint", "ulong", "unchecked", "unsafe", "ushort", "using", "virtual", "void", "volatile", "while"));

    static {
        String[] j2me = {"javax.microedition.lcdui", "javax.microedition.lcdui.game", "javax.microedition.midlet",
                "javax.microedition.rms", "javax.microedition.media", "javax.microedition.media.control",
                "javax.microedition.io", "javax.wireless.messaging", "j2me"};
        for (String p : j2me) {
            PKG_NS.put(p, "J2ME");
            PKG_PREFIX.put(p, "");
            PKG_OUT.put(p, "J2ME");
        }
        PKG_NS.put("game", "Loan12");
        PKG_PREFIX.put("game", "C_");
        PKG_OUT.put("game", "Game");
        PKG_NS.put("com.mic.smsgame.suquan", "Loan12");
        PKG_PREFIX.put("com.mic.smsgame.suquan", "SQ_");
        PKG_OUT.put("com.mic.smsgame.suquan", "Game");
        PKG_NS.put("com.mg.smsgame.main", "Loan12");
        PKG_PREFIX.put("com.mg.smsgame.main", "MG_");
        PKG_OUT.put("com.mg.smsgame.main", "Game");
        CLASS_RENAME.put("com.mic.smsgame.suquan.SQMidlet", "SQMidlet");
        CLASS_RENAME.put("com.mg.smsgame.main.MGMIDlet", "MGMIDlet");
        CLASS_RENAME.put("game.do_", "C_do");
        CLASS_RENAME.put("game.PortTask", "PortTask");
        CLASS_RENAME.put("game.PortAiStep", "PortAiStep");

        JDK_TYPE.put("java.lang.Object", "object");
        JDK_TYPE.put("java.lang.String", "string");
        JDK_TYPE.put("java.lang.StringBuffer", "global::System.Text.StringBuilder");
        JDK_TYPE.put("java.lang.Throwable", "global::System.Exception");
        JDK_TYPE.put("java.lang.Exception", "global::System.Exception");
        JDK_TYPE.put("java.lang.Error", "global::System.Exception");
        JDK_TYPE.put("java.lang.RuntimeException", "global::System.SystemException");
        JDK_TYPE.put("java.lang.NullPointerException", "global::System.NullReferenceException");
        JDK_TYPE.put("java.lang.ArrayIndexOutOfBoundsException", "global::System.IndexOutOfRangeException");
        JDK_TYPE.put("java.lang.IndexOutOfBoundsException", "global::System.IndexOutOfRangeException");
        JDK_TYPE.put("java.lang.StringIndexOutOfBoundsException", "global::System.ArgumentOutOfRangeException");
        JDK_TYPE.put("java.lang.ClassCastException", "global::System.InvalidCastException");
        JDK_TYPE.put("java.lang.ArithmeticException", "global::System.ArithmeticException");
        JDK_TYPE.put("java.lang.NumberFormatException", "global::System.FormatException");
        JDK_TYPE.put("java.lang.IllegalArgumentException", "global::System.ArgumentException");
        JDK_TYPE.put("java.lang.IllegalStateException", "global::System.InvalidOperationException");
        JDK_TYPE.put("java.lang.NegativeArraySizeException", "global::System.OverflowException");
        JDK_TYPE.put("java.lang.OutOfMemoryError", "global::System.OutOfMemoryException");
        JDK_TYPE.put("java.lang.SecurityException", "J2ME.SecurityException");
        JDK_TYPE.put("java.lang.InterruptedException", "J2ME.InterruptedException");
        JDK_TYPE.put("java.lang.ClassNotFoundException", "J2ME.ClassNotFoundException");
        JDK_TYPE.put("java.io.IOException", "global::System.IO.IOException");
        JDK_TYPE.put("java.io.EOFException", "global::System.IO.EndOfStreamException");
        JDK_TYPE.put("java.io.UnsupportedEncodingException", "J2ME.UnsupportedEncodingException");
        JDK_TYPE.put("java.io.InterruptedIOException", "J2ME.InterruptedIOException");
        JDK_TYPE.put("java.lang.Class", "global::System.Type");
        for (String s : new String[]{"java.lang.Integer", "java.lang.Long", "java.lang.Character", "java.lang.Boolean",
                "java.lang.Short", "java.lang.Byte", "java.lang.Runtime", "java.lang.Thread", "java.lang.Runnable",
                "java.io.InputStream", "java.io.OutputStream", "java.io.ByteArrayInputStream",
                "java.io.ByteArrayOutputStream", "java.io.DataInputStream", "java.io.DataOutputStream",
                "java.io.PrintStream", "java.util.Vector", "java.util.Stack", "java.util.Hashtable",
                "java.util.Enumeration", "java.util.Random", "java.util.Date"}) {
            JDK_TYPE.put(s, "J2ME." + s.substring(s.lastIndexOf('.') + 1));
        }
        JDK_STATIC.putAll(JDK_TYPE);
        JDK_STATIC.put("java.lang.String", "J2ME.JString");
        JDK_STATIC.put("java.lang.Math", "J2ME.JMath");
        JDK_STATIC.put("java.lang.System", "J2ME.JSystem");
        JDK_STATIC.put("java.lang.Object", "J2ME.JObject");
        JDK_STATIC.put("java.lang.Class", "J2ME.JClass");
    }

    // ------------------------------------------------------------------------------------------------
    static Context ctx;
    static Types types;
    static Symtab syms;
    static final Map<ClassSymbol, JCClassDecl> DECLS = new LinkedHashMap<>();
    static final Map<ClassSymbol, JCCompilationUnit> UNIT = new HashMap<>();
    static final Map<MethodSymbol, MethodSymbol> OVERRIDES = new HashMap<>();
    /** abstract classes: interface methods they leave unimplemented (emitted as `public abstract` stubs) */
    static final Map<ClassSymbol, List<MethodSymbol>> STUBS = new HashMap<>();
    static final Set<MethodSymbol> OVERRIDDEN = new HashSet<>();
    static final List<String> PROBLEMS = new ArrayList<>();
    static final Set<String> JDK_MEMBERS = new TreeSet<>();
    static String outRoot;

    public static void main(String[] args) throws Exception {
        outRoot = args[0];
        List<String> files = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        JavacTool tool = JavacTool.create();
        StandardJavaFileManager fm = tool.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
        JavacTaskImpl task = (JavacTaskImpl) tool.getTask(null, fm, diags,
                List.of("-proc:none", "-encoding", "UTF-8", "-source", "8", "-target", "8", "-nowarn", "-Xlint:-options"),
                null, fm.getJavaFileObjectsFromStrings(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        boolean err = false;
        for (Diagnostic<?> d : diags.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                System.err.println(d);
                err = true;
            }
        }
        if (err) System.exit(1);
        ctx = task.getContext();
        types = Types.instance(ctx);
        syms = Symtab.instance(ctx);
        for (CompilationUnitTree u : units) {
            JCCompilationUnit cu = (JCCompilationUnit) u;
            for (JCTree t : cu.defs) {
                if (t instanceof JCClassDecl) {
                    JCClassDecl cd = (JCClassDecl) t;
                    DECLS.put(cd.sym, cd);
                    UNIT.put(cd.sym, cu);
                    for (JCTree m : cd.defs) {
                        if (m instanceof JCClassDecl) PROBLEMS.add("nested class not supported: " + ((JCClassDecl) m).sym);
                    }
                }
            }
        }
        computeOverrides();
        checkNames();
        for (ClassSymbol cs : DECLS.keySet()) {
            String code = new ClassWriter(cs).write();
            String pkg = cs.packge().fullname.toString();
            Path dir = Paths.get(outRoot, PKG_OUT.get(pkg));
            Files.createDirectories(dir);
            byte[] bytes = code.getBytes(StandardCharsets.UTF_8);
            boolean ascii = true;
            for (byte x : bytes) if (x < 0) { ascii = false; break; }
            try (OutputStream os = Files.newOutputStream(dir.resolve(csName(cs).replace("@", "") + ".cs"))) {
                if (!ascii) os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                os.write(bytes);
            }
        }
        System.out.println("classes: " + DECLS.size());
        System.out.println("JDK members used: " + JDK_MEMBERS.size());
        try (PrintWriter pw = new PrintWriter(Paths.get(outRoot, "jdk-members.txt").toFile(), "UTF-8")) {
            for (String s : JDK_MEMBERS) pw.println(s);
        }
        for (String p : PROBLEMS) System.out.println("PROBLEM: " + p);
        System.out.println("problems: " + PROBLEMS.size());
    }

    // ------------------------------------------------------------------------------------------------
    // naming
    static boolean translated(Symbol s) {
        return s instanceof ClassSymbol && DECLS.containsKey(s);
    }

    static String esc(String n) {
        return CS_KEYWORDS.contains(n) ? "@" + n : n;
    }

    static String csName(ClassSymbol cs) {
        String fq = cs.getQualifiedName().toString();
        if (CLASS_RENAME.containsKey(fq)) return CLASS_RENAME.get(fq);
        String pkg = cs.packge().fullname.toString();
        String pre = PKG_PREFIX.get(pkg);
        if (pre == null) throw new RuntimeException("no package mapping for " + fq);
        return esc(pre + cs.name);
    }

    static boolean isGame(ClassSymbol cs) {
        return "Loan12".equals(PKG_NS.get(cs.packge().fullname.toString()));
    }

    /** C# type name usable in declarations, casts, new, typeof. */
    static String ty(Type t) {
        switch (t.getTag()) {
            case BOOLEAN:
                return "bool";
            case BYTE:
                return "sbyte";
            case CHAR:
                return "char";
            case SHORT:
                return "short";
            case INT:
                return "int";
            case LONG:
                return "long";
            case FLOAT:
                return "float";
            case DOUBLE:
                return "double";
            case VOID:
                return "void";
            case ARRAY:
                return ty(((ArrayType) t).elemtype) + "[]";
            case BOT:
                return "object";
            case CLASS: {
                ClassSymbol cs = (ClassSymbol) t.tsym;
                if (translated(cs)) return csName(cs);
                String fq = cs.getQualifiedName().toString();
                String m = JDK_TYPE.get(fq);
                if (m == null) {
                    PROBLEMS.add("unmapped JDK type " + fq);
                    return "/*?" + fq + "*/object";
                }
                return m;
            }
            default:
                PROBLEMS.add("type tag " + t.getTag() + " for " + t);
                return "object";
        }
    }

    /** owner expression for static members */
    static String staticOwner(ClassSymbol cs) {
        if (translated(cs)) return csName(cs);
        String fq = cs.getQualifiedName().toString();
        String m = JDK_STATIC.get(fq);
        if (m == null) {
            PROBLEMS.add("unmapped static owner " + fq);
            return "/*?" + fq + "*/object";
        }
        return m;
    }

    static boolean isInterfaceConstHolder(ClassSymbol cs) {
        if (!translated(cs) || (cs.flags() & Flags.INTERFACE) == 0) return false;
        for (Symbol s : cs.members().getSymbols()) if (s instanceof VarSymbol) return true;
        return false;
    }

    static String constHolder(ClassSymbol cs) {
        return csName(cs).replace("@", "") + "Const";
    }

    static String fieldName(VarSymbol v) {
        ClassSymbol owner = (ClassSymbol) v.owner;
        String n = v.name.toString();
        if (translated(owner) && isGame(owner)) return "f_" + n;
        return esc(n);
    }

    static String methodName(MethodSymbol m) {
        String n = m.name.toString();
        if (n.equals("toString") && m.params().isEmpty()) return "ToString";
        if (n.equals("hashCode") && m.params().isEmpty()) return "GetHashCode";
        if (n.equals("equals") && m.params().size() == 1 && m.params().head.type.tsym == syms.objectType.tsym) return "Equals";
        return esc(n);
    }

    static boolean isObjectOverride(MethodSymbol m) {
        String n = methodName(m);
        return n.equals("ToString") || n.equals("GetHashCode") || n.equals("Equals");
    }

    // ------------------------------------------------------------------------------------------------
    // inheritance
    static List<ClassSymbol> superclasses(ClassSymbol cs) {
        List<ClassSymbol> r = new ArrayList<>();
        Type st = cs.getSuperclass();
        while (st != null && st.getTag() == CLASS) {
            ClassSymbol s = (ClassSymbol) st.tsym;
            r.add(s);
            st = s.getSuperclass();
        }
        return r;
    }

    static void computeStubs() {
        for (ClassSymbol cs : DECLS.keySet()) {
            if ((cs.flags() & Flags.ABSTRACT) == 0 || (cs.flags() & Flags.INTERFACE) != 0) continue;
            Set<ClassSymbol> ifs = new LinkedHashSet<>();
            allInterfaces(cs, ifs);
            List<MethodSymbol> list = new ArrayList<>();
            Set<String> done = new HashSet<>();
            for (ClassSymbol i : ifs) {
                for (Symbol s : i.members().getSymbols()) {
                    if (!(s instanceof MethodSymbol)) continue;
                    MethodSymbol im = (MethodSymbol) s;
                    if (implementation(im, cs) != null) continue;
                    if (stubbedInSuper(cs, im)) continue;
                    String sig = methodNameS(im) + im.type.getParameterTypes();
                    if (done.add(sig)) list.add(im);
                }
            }
            STUBS.put(cs, list);
        }
    }

    static boolean stubbedInSuper(ClassSymbol cs, MethodSymbol im) {
        for (ClassSymbol s : superclasses(cs)) {
            if (!translated(s) || (s.flags() & Flags.ABSTRACT) == 0) continue;
            Set<ClassSymbol> ifs = new LinkedHashSet<>();
            allInterfaces(s, ifs);
            if (ifs.contains((ClassSymbol) im.owner) && implementation(im, s) == null) return true;
        }
        return false;
    }

    static String methodNameS(MethodSymbol m) {
        return m.name.toString();
    }

    static void computeOverrides() {
        computeStubs();
        for (ClassSymbol cs : DECLS.keySet()) {
            for (Symbol s : cs.members().getSymbols()) {
                if (!(s instanceof MethodSymbol)) continue;
                MethodSymbol m = (MethodSymbol) s;
                if (m.isConstructor() || m.isStatic() || (m.flags() & Flags.PRIVATE) != 0) continue;
                for (ClassSymbol sup : superclasses(cs)) {
                    MethodSymbol found = null;
                    for (Symbol e : sup.members().getSymbolsByName(m.name)) {
                        if (e instanceof MethodSymbol && !e.isStatic() && (e.flags() & Flags.PRIVATE) == 0
                                && m.overrides(e, cs, types, false)) {
                            found = (MethodSymbol) e;
                            break;
                        }
                    }
                    if (found == null && STUBS.containsKey(sup)) {
                        for (MethodSymbol im : STUBS.get(sup)) {
                            if (im.name == m.name && m.overrides(im, cs, types, false)) {
                                found = im;
                                break;
                            }
                        }
                    }
                    if (found != null) {
                        OVERRIDES.put(m, found);
                        OVERRIDDEN.add(found);
                        break;
                    }
                }
            }
        }
    }

    static void checkNames() {
        for (ClassSymbol cs : DECLS.keySet()) {
            Set<String> methods = new HashSet<>();
            for (Symbol s : cs.members().getSymbols()) if (s instanceof MethodSymbol && !((MethodSymbol) s).isConstructor()) methods.add(methodName((MethodSymbol) s));
            for (Symbol s : cs.members().getSymbols()) {
                if (s instanceof VarSymbol && methods.contains(fieldName((VarSymbol) s)))
                    PROBLEMS.add("field/method name clash " + cs + "." + s);
            }
            if (methods.contains(csName(cs))) PROBLEMS.add("member named like its class: " + cs);
        }
    }

    /** Interfaces implemented (transitively), for abstract stubs. */
    static void allInterfaces(ClassSymbol cs, Set<ClassSymbol> out) {
        for (Type t : cs.getInterfaces()) {
            ClassSymbol i = (ClassSymbol) t.tsym;
            if (out.add(i)) allInterfaces(i, out);
        }
        Type st = cs.getSuperclass();
        if (st != null && st.getTag() == CLASS) allInterfaces((ClassSymbol) st.tsym, out);
    }

    static MethodSymbol implementation(MethodSymbol im, ClassSymbol cs) {
        for (ClassSymbol c = cs; c != null; ) {
            for (Symbol e : c.members().getSymbolsByName(im.name)) {
                if (e instanceof MethodSymbol && !e.isStatic() && ((MethodSymbol) e).overrides(im, cs, types, false)
                        && (e.flags() & Flags.ABSTRACT) == 0)
                    return (MethodSymbol) e;
                if (e instanceof MethodSymbol && !e.isStatic() && (e.flags() & Flags.ABSTRACT) != 0
                        && (c.flags() & Flags.INTERFACE) == 0 && ((MethodSymbol) e).overrides(im, cs, types, false))
                    return (MethodSymbol) e; // declared abstract in a superclass: fine
            }
            Type st = c.getSuperclass();
            c = (st != null && st.getTag() == CLASS) ? (ClassSymbol) st.tsym : null;
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------------
    static boolean isPrim(Type t) {
        return t.isPrimitive();
    }

    static boolean isString(Type t) {
        return t.getTag() == CLASS && t.tsym == syms.stringType.tsym;
    }

    static long constLong(Object v) {
        if (v instanceof Character) return (Character) v;
        if (v instanceof Boolean) return 0;
        return ((Number) v).longValue();
    }

    /** C# implicit numeric conversion check (incl. constant rules). */
    static boolean csImplicit(Type from, Type to, Object constVal) {
        TypeTag f = from.getTag(), t = to.getTag();
        if (f == t) return true;
        if (f == BOOLEAN || t == BOOLEAN) return false;
        if (constVal != null && f == INT) {
            long v = constLong(constVal);
            if (t == BYTE && v >= -128 && v <= 127) return true;
            if (t == SHORT && v >= -32768 && v <= 32767) return true;
        }
        switch (f) {
            case BYTE:
                return t == SHORT || t == INT || t == LONG || t == FLOAT || t == DOUBLE;
            case SHORT:
                return t == INT || t == LONG || t == FLOAT || t == DOUBLE;
            case CHAR:
                return t == INT || t == LONG || t == FLOAT || t == DOUBLE;
            case INT:
                return t == LONG || t == FLOAT || t == DOUBLE;
            case LONG:
                return t == FLOAT || t == DOUBLE;
            case FLOAT:
                return t == DOUBLE;
            default:
                return false;
        }
    }

    static String lit(Object v, Type t) {
        switch (t.getTag()) {
            case BOOLEAN:
                return ((Integer) v) != 0 ? "true" : "false";
            case CHAR:
                return charLit((char) ((Integer) v).intValue());
            case BYTE:
                return "((sbyte)" + paren(String.valueOf(((Number) v).intValue())) + ")";
            case SHORT:
                return "((short)" + paren(String.valueOf(((Number) v).intValue())) + ")";
            case INT:
                return String.valueOf(((Number) v).intValue());
            case LONG:
                return ((Number) v).longValue() + "L";
            case FLOAT: {
                float f = ((Number) v).floatValue();
                if (Float.isNaN(f)) return "float.NaN";
                if (Float.isInfinite(f)) return f > 0 ? "float.PositiveInfinity" : "float.NegativeInfinity";
                return Float.toString(f) + "f";
            }
            case DOUBLE: {
                double d = ((Number) v).doubleValue();
                if (Double.isNaN(d)) return "double.NaN";
                if (Double.isInfinite(d)) return d > 0 ? "double.PositiveInfinity" : "double.NegativeInfinity";
                String s = Double.toString(d);
                return s.contains(".") || s.contains("E") ? s : s + ".0";
            }
            case CLASS:
                if (v instanceof String) return strLit((String) v);
            default:
                PROBLEMS.add("literal of type " + t + " = " + v);
                return String.valueOf(v);
        }
    }

    static String paren(String s) {
        return s.startsWith("-") ? "(" + s + ")" : s;
    }

    static String charLit(char c) {
        switch (c) {
            case '\'':
                return "'\\''";
            case '\\':
                return "'\\\\'";
            case '\n':
                return "'\\n'";
            case '\r':
                return "'\\r'";
            case '\t':
                return "'\\t'";
            case 0:
                return "'\\0'";
            default:
                if (c < 32 || c >= 127) return String.format("'\\u%04x'", (int) c);
                return "'" + c + "'";
        }
    }

    static String strLit(String s) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    b.append("\\\"");
                    break;
                case '\\':
                    b.append("\\\\");
                    break;
                case '\n':
                    b.append("\\n");
                    break;
                case '\r':
                    b.append("\\r");
                    break;
                case '\t':
                    b.append("\\t");
                    break;
                default:
                    if (c < 32 || (c >= 0xD800 && c <= 0xDFFF) || c == 0x2028 || c == 0x2029 || c == 0x85)
                        b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
            }
        }
        return b.append('"').toString();
    }

    // ================================================================================================
    static class ClassWriter {
        final ClassSymbol cs;
        final JCClassDecl cd;
        final JCCompilationUnit cu;
        final String src;
        final StringBuilder out = new StringBuilder();
        int indent;
        // per method state
        MethodSymbol curMethod;
        Map<VarSymbol, String> localNames;
        Set<String> usedLocals;
        Map<Name, String[]> labels; // label -> {breakLabel, continueLabel}
        Set<String> usedLabels;
        Deque<JCTree> loopStack = new ArrayDeque<>();
        int tmpCounter;
        long lastCommentPos;

        ClassWriter(ClassSymbol cs) {
            this.cs = cs;
            this.cd = DECLS.get(cs);
            this.cu = UNIT.get(cs);
            String s;
            try {
                s = cu.sourcefile.getCharContent(true).toString();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            this.src = s;
        }

        void line(String s) {
            for (int i = 0; i < indent; i++) out.append("    ");
            out.append(s).append('\n');
        }

        // ---- comments --------------------------------------------------------------------------
        /** emits // and block comments found in the source between lastCommentPos and pos */
        void comments(int pos) {
            if (pos <= lastCommentPos || pos < 0) return;
            String gap = src.substring((int) Math.max(0, lastCommentPos), Math.min(pos, src.length()));
            lastCommentPos = pos;
            int i = 0;
            while (i < gap.length()) {
                char c = gap.charAt(i);
                if (c == '"' || c == '\'') { // inside code (should not happen in gaps) - skip literal
                    int j = i + 1;
                    while (j < gap.length() && gap.charAt(j) != c) {
                        if (gap.charAt(j) == '\\') j++;
                        j++;
                    }
                    i = j + 1;
                    continue;
                }
                if (gap.startsWith("//", i)) {
                    int e = gap.indexOf('\n', i);
                    if (e < 0) e = gap.length();
                    String txt = gap.substring(i, e).trim();
                    if (txt.length() > 2) line(txt);
                    i = e;
                    continue;
                }
                if (gap.startsWith("/*", i)) {
                    int e = gap.indexOf("*/", i + 2);
                    if (e < 0) e = gap.length() - 2;
                    String txt = gap.substring(i, e + 2);
                    if (!txt.contains("JD-Core") && !txt.contains("Qualified Name")) {
                        for (String l : txt.split("\n")) {
                            String t = l.trim();
                            if (!t.isEmpty()) line(t.startsWith("*") ? " " + t : t);
                        }
                    }
                    i = e + 2;
                    continue;
                }
                i++;
            }
        }

        int startPos(JCTree t) {
            return TreeInfo.getStartPos(t);
        }

        int endPos(JCTree t) {
            return TreeInfo.getEndPos(t, cu.endPositions);
        }

        // ---- class ------------------------------------------------------------------------------
        String write() {
            String ns = PKG_NS.get(cs.packge().fullname.toString());
            out.append("// Generated by J2CS from ").append(cs.getQualifiedName())
                    .append(ns.equals("Loan12") ? " (decompiled J2ME game source).\n" : " (MIDP emulation layer).\n");
            out.append("// Do not edit by hand if you intend to re-run the converter; see PORTING_NOTES.md.\n");
            if (ns.equals("Loan12")) out.append("using J2ME;\n");
            out.append("#pragma warning disable 0108, 0162, 0164, 0168, 0169, 0219, 0414, 0649, 0652, 0659, 0661, 0168, 1717\n\n");
            out.append("namespace ").append(ns).append("\n{\n");
            indent = 1;
            lastCommentPos = startPos(cd);
            boolean isIface = (cs.flags() & Flags.INTERFACE) != 0;
            if (isIface) writeInterface();
            else writeClass();
            indent = 0;
            out.append("}\n");
            return out.toString();
        }

        String access(long flags) {
            if ((flags & Flags.PRIVATE) != 0) return "private ";
            return "public ";
        }

        void writeInterface() {
            List<String> sup = new ArrayList<>();
            for (Type t : cs.getInterfaces()) sup.add(ty(t));
            line("public interface " + csName(cs) + (sup.isEmpty() ? "" : " : " + String.join(", ", sup)));
            line("{");
            indent++;
            List<JCVariableDecl> consts = new ArrayList<>();
            for (JCTree t : cd.defs) {
                if (t instanceof JCMethodDecl) {
                    JCMethodDecl md = (JCMethodDecl) t;
                    comments(startPos(md));
                    line(ty(md.sym.getReturnType()) + " " + methodName(md.sym) + "(" + params(md, false) + ");");
                    lastCommentPos = endPos(md);
                } else if (t instanceof JCVariableDecl) {
                    consts.add((JCVariableDecl) t);
                }
            }
            indent--;
            line("}");
            if (!consts.isEmpty()) {
                out.append('\n');
                line("/// <summary>Constants declared in Java interface " + cs.name + ".</summary>");
                line("public static class " + constHolder(cs));
                line("{");
                indent++;
                for (JCVariableDecl v : consts) writeStaticField(v, true);
                indent--;
                line("}");
            }
        }

        void writeClass() {
            StringBuilder h = new StringBuilder("public ");
            if ((cs.flags() & Flags.ABSTRACT) != 0) h.append("abstract ");
            h.append("class ").append(csName(cs));
            List<String> bases = new ArrayList<>();
            Type st = cs.getSuperclass();
            if (st != null && st.getTag() == CLASS && st.tsym != syms.objectType.tsym) bases.add(ty(st));
            for (Type t : cs.getInterfaces()) bases.add(ty(t));
            if (!bases.isEmpty()) h.append(" : ").append(String.join(", ", bases));
            line(h.toString());
            line("{");
            indent++;

            // static & instance initialisation in Java textual order
            List<JCTree> staticInit = new ArrayList<>();
            List<JCTree> instInit = new ArrayList<>();
            boolean hasCtor = false;
            for (JCTree t : cd.defs) {
                if (t instanceof JCVariableDecl) {
                    JCVariableDecl v = (JCVariableDecl) t;
                    if (v.init == null) continue;
                    if (v.sym.isStatic()) {
                        if (!isConst(v.sym)) staticInit.add(v);
                    } else instInit.add(v);
                } else if (t instanceof JCBlock) {
                    if ((((JCBlock) t).flags & Flags.STATIC) != 0) staticInit.add(t);
                    else instInit.add(t);
                } else if (t instanceof JCMethodDecl && ((JCMethodDecl) t).sym.isConstructor()) {
                    hasCtor = true;
                }
            }
            String initName = "__init_" + csName(cs).replace("@", "");

            for (JCTree t : cd.defs) {
                if (t instanceof JCVariableDecl) {
                    JCVariableDecl v = (JCVariableDecl) t;
                    if (v.sym.isStatic()) writeStaticField(v, false);
                    else writeInstanceField(v);
                }
            }
            if (!staticInit.isEmpty()) {
                out.append('\n');
                line("static " + csName(cs) + "()");
                line("{");
                indent++;
                beginMethod(null);
                for (JCTree t : staticInit) {
                    if (t instanceof JCVariableDecl) {
                        JCVariableDecl v = (JCVariableDecl) t;
                        line(staticOwnerExpr(v.sym) + "." + fieldName(v.sym) + " = " + init(v.init, v.sym.type) + ";");
                    } else {
                        comments(startPos(t));
                        for (JCStatement s : ((JCBlock) t).stats) stmt(s);
                    }
                }
                indent--;
                line("}");
            }
            if (!instInit.isEmpty()) {
                out.append('\n');
                line("private void " + initName + "()");
                line("{");
                indent++;
                beginMethod(null);
                for (JCTree t : instInit) {
                    if (t instanceof JCVariableDecl) {
                        JCVariableDecl v = (JCVariableDecl) t;
                        line("this." + fieldName(v.sym) + " = " + init(v.init, v.sym.type) + ";");
                    } else {
                        for (JCStatement s : ((JCBlock) t).stats) stmt(s);
                    }
                }
                indent--;
                line("}");
            }
            if (!hasCtor && !instInit.isEmpty()) {
                out.append('\n');
                line("public " + csName(cs) + "()");
                line("{");
                line("    this." + initName + "();");
                line("}");
            }
            for (JCTree t : cd.defs) {
                if (t instanceof JCMethodDecl) {
                    out.append('\n');
                    writeMethod((JCMethodDecl) t, instInit.isEmpty() ? null : initName);
                }
            }
            // abstract classes must declare interface methods they leave unimplemented
            for (MethodSymbol im : STUBS.getOrDefault(cs, Collections.emptyList())) {
                out.append('\n');
                StringBuilder p = new StringBuilder();
                int k = 0;
                for (VarSymbol v : im.params()) {
                    if (k++ > 0) p.append(", ");
                    p.append(ty(v.type)).append(' ').append(esc(v.name.toString()));
                }
                line("public abstract " + ty(im.getReturnType()) + " " + methodName(im) + "(" + p + "); // from interface " + im.owner.name);
            }
            indent--;
            line("}");
        }

        boolean isConst(VarSymbol v) {
            return v.isStatic() && (v.flags() & Flags.FINAL) != 0 && v.getConstValue() != null
                    && (isPrim(v.type) || isString(v.type));
        }

        String staticOwnerExpr(VarSymbol v) {
            ClassSymbol o = (ClassSymbol) v.owner;
            if (isInterfaceConstHolder(o)) return constHolder(o);
            return staticOwner(o);
        }

        void writeStaticField(JCVariableDecl v, boolean inConstHolder) {
            comments(startPos(v));
            lastCommentPos = endPos(v);
            if (isConst(v.sym)) {
                line("public const " + ty(v.sym.type) + " " + fieldName(v.sym) + " = " + lit(v.sym.getConstValue(), v.sym.type) + ";");
            } else if (inConstHolder) {
                beginMethod(null);
                line("public static " + ty(v.sym.type) + " " + fieldName(v.sym) + " = " + init(v.init, v.sym.type) + ";");
            } else {
                line(access(v.sym.flags()) + "static " + hiding(v.sym) + ty(v.sym.type) + " " + fieldName(v.sym) + ";");
            }
        }

        void writeInstanceField(JCVariableDecl v) {
            comments(startPos(v));
            lastCommentPos = endPos(v);
            line(access(v.sym.flags()) + hiding(v.sym) + ty(v.sym.type) + " " + fieldName(v.sym) + ";");
        }

        /** `new ` when a field hides an accessible base field with the same C# name */
        String hiding(VarSymbol v) {
            for (ClassSymbol s : superclasses(cs)) {
                if (!translated(s)) continue;
                for (Symbol e : s.members().getSymbolsByName(v.name)) {
                    if (e instanceof VarSymbol && (e.flags() & Flags.PRIVATE) == 0) return "new ";
                }
            }
            return "";
        }

        String params(JCMethodDecl md, boolean register) {
            StringBuilder p = new StringBuilder();
            int k = 0;
            for (JCVariableDecl v : md.params) {
                if (k++ > 0) p.append(", ");
                String n = register ? declareLocal(v.sym) : esc(v.name.toString());
                p.append(ty(v.sym.type)).append(' ').append(n);
            }
            return p.toString();
        }

        void beginMethod(MethodSymbol m) {
            curMethod = m;
            localNames = new HashMap<>();
            usedLocals = new HashSet<>();
            labels = new HashMap<>();
            usedLabels = new HashSet<>();
            tmpCounter = 0;
        }

        String declareLocal(VarSymbol v) {
            String base = v.name.toString();
            String n = base;
            int k = 1;
            while (usedLocals.contains(n) || CS_KEYWORDS.contains(n)) {
                n = base + "_" + (k++);
            }
            usedLocals.add(n);
            localNames.put(v, n);
            return n;
        }

        void writeMethod(JCMethodDecl md, String initName) {
            MethodSymbol m = md.sym;
            comments(startPos(md));
            beginMethod(m);
            String ps = params(md, true);
            if (m.isConstructor()) {
                JCStatement first = md.body.stats.isEmpty() ? null : md.body.stats.head;
                String chain = "";
                boolean callsThis = false;
                List<JCStatement> body = new ArrayList<>(md.body.stats);
                if (first != null && TreeInfo.isSelfCall(first)) {
                    JCMethodInvocation mi = (JCMethodInvocation) ((JCExpressionStatement) first).expr;
                    String nm = TreeInfo.name(mi.meth).toString();
                    callsThis = nm.equals("this");
                    MethodSymbol target = (MethodSymbol) TreeInfo.symbol(mi.meth);
                    String args = args(mi, target);
                    if (!(nm.equals("super") && mi.args.isEmpty())) chain = " : " + (callsThis ? "this" : "base") + "(" + args + ")";
                    body.remove(0);
                }
                line(access(m.flags()) + csName(cs) + "(" + ps + ")" + chain);
                line("{");
                indent++;
                if (!callsThis && initName != null) line("this." + initName + "();");
                lastCommentPos = startPos(md.body) + 1;
                for (JCStatement s : body) stmt(s);
                comments(endPos(md.body) - 1);
                indent--;
                line("}");
                lastCommentPos = endPos(md);
                return;
            }
            StringBuilder mods = new StringBuilder(access(m.flags()));
            boolean abs = (m.flags() & Flags.ABSTRACT) != 0;
            if (m.isStatic()) {
                mods.append("static ");
                if (hidesStatic(m)) mods.append("new ");
            } else if (isObjectOverride(m)) {
                mods.append("override ");
            } else if (OVERRIDES.containsKey(m)) {
                mods.append(abs ? "abstract override " : "override ");
            } else if (abs) {
                mods.append("abstract ");
            } else if (OVERRIDDEN.contains(m) && (m.flags() & Flags.PRIVATE) == 0) {
                mods.append("virtual ");
            }
            if ((m.flags() & Flags.PRIVATE) == 0 && !m.isStatic() && !OVERRIDES.containsKey(m) && hidesInstance(m))
                mods.append("new ");
            String ret = ty(m.getReturnType());
            String name = methodName(m);
            if (name.equals("Equals")) ps = "object " + localNames.get(md.params.head.sym);
            if (abs || md.body == null) {
                line(mods + ret + " " + name + "(" + ps + ");");
                lastCommentPos = endPos(md);
                return;
            }
            line(mods + ret + " " + name + "(" + ps + ")");
            line("{");
            indent++;
            lastCommentPos = startPos(md.body) + 1;
            for (JCStatement s : md.body.stats) stmt(s);
            comments(endPos(md.body) - 1);
            indent--;
            line("}");
            lastCommentPos = endPos(md);
        }

        boolean hidesStatic(MethodSymbol m) {
            for (ClassSymbol s : superclasses(cs)) {
                if (!translated(s)) continue;
                for (Symbol e : s.members().getSymbolsByName(m.name)) {
                    if (e instanceof MethodSymbol && e.isStatic() && (e.flags() & Flags.PRIVATE) == 0
                            && types.isSameTypes(((MethodSymbol) e).type.getParameterTypes(), m.type.getParameterTypes()))
                        return true;
                }
            }
            return false;
        }

        boolean hidesInstance(MethodSymbol m) {
            // a non-overriding instance method with the same signature as an accessible base method
            // (static, or package-private in another package - Java does not override those)
            for (ClassSymbol s : superclasses(cs)) {
                if (!translated(s)) continue;
                for (Symbol e : s.members().getSymbolsByName(m.name)) {
                    if (e instanceof MethodSymbol && !((MethodSymbol) e).isConstructor() && (e.flags() & Flags.PRIVATE) == 0
                            && types.isSameTypes(((MethodSymbol) e).type.getParameterTypes(), m.type.getParameterTypes()))
                        return true;
                }
            }
            return false;
        }

        // ================================================================================================
        // statements
        void block(JCStatement s) {
            if (s instanceof JCBlock) {
                line("{");
                indent++;
                lastCommentPos = startPos(s) + 1;
                for (JCStatement x : ((JCBlock) s).stats) stmt(x);
                comments(endPos(s) - 1);
                indent--;
                line("}");
            } else {
                line("{");
                indent++;
                stmt(s);
                indent--;
                line("}");
            }
        }

        void stmt(JCStatement s) {
            comments(startPos(s));
            switch (s.getTag()) {
                case BLOCK:
                    block(s);
                    break;
                case VARDEF: {
                    JCVariableDecl v = (JCVariableDecl) s;
                    String n = declareLocal(v.sym);
                    String init = v.init != null ? init(v.init, v.sym.type) : defaultValue(v.sym.type);
                    line(ty(v.sym.type) + " " + n + " = " + init + ";");
                    break;
                }
                case EXEC: {
                    line(exprStmt(((JCExpressionStatement) s).expr) + ";");
                    break;
                }
                case IF: {
                    JCIf i = (JCIf) s;
                    line("if (" + cond(i.cond) + ")");
                    block(i.thenpart);
                    JCStatement e = i.elsepart;
                    while (e != null) {
                        if (e instanceof JCIf) {
                            JCIf ei = (JCIf) e;
                            comments(startPos(ei));
                            line("else if (" + cond(ei.cond) + ")");
                            block(ei.thenpart);
                            e = ei.elsepart;
                        } else {
                            line("else");
                            block(e);
                            e = null;
                        }
                    }
                    break;
                }
                case WHILELOOP: {
                    JCWhileLoop w = (JCWhileLoop) s;
                    line("while (" + cond(w.cond) + ")");
                    loopBody(s, w.body);
                    break;
                }
                case DOLOOP: {
                    JCDoWhileLoop d = (JCDoWhileLoop) s;
                    line("do");
                    loopBody(s, d.body);
                    line("while (" + cond(d.cond) + ");");
                    break;
                }
                case FORLOOP: {
                    JCForLoop f = (JCForLoop) s;
                    List<String> init = new ArrayList<>();
                    String declType = null;
                    for (JCStatement x : f.init) {
                        if (x instanceof JCVariableDecl) {
                            JCVariableDecl v = (JCVariableDecl) x;
                            String n = declareLocal(v.sym);
                            declType = ty(v.sym.type);
                            init.add(n + " = " + (v.init != null ? init(v.init, v.sym.type) : defaultValue(v.sym.type)));
                        } else {
                            init.add(exprStmt(((JCExpressionStatement) x).expr));
                        }
                    }
                    List<String> step = new ArrayList<>();
                    for (JCExpressionStatement x : f.step) step.add(exprStmt(x.expr));
                    String i0 = (declType != null ? declType + " " : "") + String.join(", ", init);
                    line("for (" + i0 + "; " + (f.cond == null ? "" : cond(f.cond)) + "; " + String.join(", ", step) + ")");
                    loopBody(s, f.body);
                    break;
                }
                case LABELLED: {
                    JCLabeledStatement l = (JCLabeledStatement) s;
                    String base = "lbl_" + l.label;
                    String[] names = {base + "_break", base + "_continue"};
                    labels.put(l.label, names);
                    JCStatement body = l.body;
                    stmt(body);
                    if (usedLabels.contains(names[0])) line(names[0] + ": ;");
                    break;
                }
                case SWITCH:
                    switchStmt((JCSwitch) s);
                    break;
                case BREAK: {
                    JCBreak b = (JCBreak) s;
                    if (b.label != null) {
                        String n = labels.get(b.label)[0];
                        usedLabels.add(n);
                        line("goto " + n + ";");
                    } else line("break;");
                    break;
                }
                case CONTINUE: {
                    JCContinue c = (JCContinue) s;
                    if (c.label != null) {
                        String n = labels.get(c.label)[1];
                        usedLabels.add(n);
                        line("goto " + n + ";");
                    } else line("continue;");
                    break;
                }
                case RETURN: {
                    JCReturn r = (JCReturn) s;
                    if (r.expr == null) line("return;");
                    else line("return " + init(r.expr, curMethod.getReturnType()) + ";");
                    break;
                }
                case THROW:
                    line("throw " + expr(((JCThrow) s).expr) + ";");
                    break;
                case TRY:
                    tryStmt((JCTry) s);
                    break;
                case SYNCHRONIZED: {
                    JCSynchronized sy = (JCSynchronized) s;
                    line("lock (" + expr(sy.lock) + ")");
                    block(sy.body);
                    break;
                }
                case SKIP:
                    line(";");
                    break;
                default:
                    PROBLEMS.add("statement " + s.getTag() + " in " + cs + " : " + s);
                    line("/* UNSUPPORTED: " + s.toString().replace("*/", "* /") + " */");
            }
            lastCommentPos = Math.max(lastCommentPos, endPos(s));
        }

        /** loop body with a continue-label (if the loop is labelled and the label is used for continue) */
        void loopBody(JCStatement loop, JCStatement body) {
            String cont = null;
            JCLabeledStatement wrapper = findLabelFor(loop);
            if (wrapper != null) cont = labels.get(wrapper.label)[1];
            line("{");
            indent++;
            if (body instanceof JCBlock) {
                lastCommentPos = startPos(body) + 1;
                for (JCStatement x : ((JCBlock) body).stats) stmt(x);
                comments(endPos(body) - 1);
            } else stmt(body);
            if (cont != null && usesLabelContinue(body, wrapper.label)) {
                usedLabels.add(cont);
                line(cont + ": ;");
            }
            indent--;
            line("}");
        }

        JCLabeledStatement findLabelFor(JCStatement loop) {
            // search the class tree for a labelled statement whose body is this loop
            final JCLabeledStatement[] res = {null};
            new com.sun.tools.javac.tree.TreeScanner() {
                @Override
                public void visitLabelled(JCLabeledStatement t) {
                    if (t.body == loop) res[0] = t;
                    super.visitLabelled(t);
                }
            }.scan(cd);
            return res[0];
        }

        boolean usesLabelContinue(JCTree body, Name label) {
            final boolean[] r = {false};
            new com.sun.tools.javac.tree.TreeScanner() {
                @Override
                public void visitContinue(JCContinue t) {
                    if (label.equals(t.label)) r[0] = true;
                }
            }.scan(body);
            return r[0];
        }

        void tryStmt(JCTry t) {
            line("try");
            block(t.body);
            List<Type> seen = new ArrayList<>();
            List<String> seenCs = new ArrayList<>();
            for (JCCatch c : t.catchers) {
                Type ct = c.param.sym.type;
                String cty = ty(ct);
                boolean dup = seenCs.contains(cty) || seenCs.contains("global::System.Exception") || catchSubsumed(cty, seenCs);
                if (dup) {
                    line("// (catch " + ct + " is subsumed by an earlier catch clause in C#)");
                    continue;
                }
                seenCs.add(cty);
                String n = declareLocal(c.param.sym);
                line("catch (" + cty + " " + n + ")");
                block(c.body);
            }
            if (t.finalizer != null) {
                line("finally");
                block(t.finalizer);
            }
        }

        boolean catchSubsumed(String cty, List<String> seen) {
            // C# exception hierarchy facts for the mapped types
            Map<String, String[]> parents = new HashMap<>();
            parents.put("global::System.NullReferenceException", new String[]{"global::System.SystemException"});
            parents.put("global::System.IndexOutOfRangeException", new String[]{"global::System.SystemException"});
            parents.put("global::System.InvalidCastException", new String[]{"global::System.SystemException"});
            parents.put("global::System.ArithmeticException", new String[]{"global::System.SystemException"});
            parents.put("global::System.FormatException", new String[]{"global::System.SystemException"});
            parents.put("global::System.ArgumentException", new String[]{"global::System.SystemException"});
            parents.put("global::System.ArgumentOutOfRangeException", new String[]{"global::System.ArgumentException", "global::System.SystemException"});
            parents.put("global::System.InvalidOperationException", new String[]{"global::System.SystemException"});
            parents.put("global::System.OverflowException", new String[]{"global::System.ArithmeticException", "global::System.SystemException"});
            parents.put("global::System.IO.IOException", new String[]{"global::System.SystemException"});
            parents.put("global::System.IO.EndOfStreamException", new String[]{"global::System.IO.IOException", "global::System.SystemException"});
            String[] p = parents.get(cty);
            if (p == null) return false;
            for (String x : p) if (seen.contains(x)) return true;
            return false;
        }

        // ---- switch -------------------------------------------------------------------------------
        void switchStmt(JCSwitch sw) {
            Type st = sw.selector.type;
            line("switch (" + expr(sw.selector) + ")");
            line("{");
            indent++;
            List<JCCase> cases = sw.cases;
            for (int i = 0; i < cases.size(); i++) {
                JCCase c = cases.get(i);
                comments(startPos(c));
                line(caseLabel(c, st) + ":");
                if (c.stats.isEmpty() && i + 1 < cases.size()) continue; // shares the next group's statements
                indent++;
                for (JCStatement x : c.stats) stmt(x);
                if (canCompleteNormally(c.stats)) {
                    // Java falls through into the next group; C# needs an explicit jump
                    if (i + 1 < cases.size()) line("goto " + caseLabel(cases.get(i + 1), st) + ";");
                    else line("break;");
                }
                indent--;
            }
            indent--;
            line("}");
        }

        String caseLabel(JCCase c, Type st) {
            if (c.labels.isEmpty() || c.labels.head instanceof JCDefaultCaseLabel) return "default";
            JCExpression e = ((JCConstantCaseLabel) c.labels.head).expr;
            Object v = e.type.constValue();
            if (v == null) {
                PROBLEMS.add("non-constant case label " + e);
                return "case " + expr(e);
            }
            // literal of the switch type
            return "case " + lit(v instanceof Character ? (int) (Character) v : v, st);
        }

        boolean canCompleteNormally(List<JCStatement> stats) {
            if (stats.isEmpty()) return true;
            return canComplete(stats.get(stats.size() - 1));
        }

        boolean canComplete(JCStatement s) {
            switch (s.getTag()) {
                case BREAK:
                case CONTINUE:
                case RETURN:
                case THROW:
                    return false;
                case BLOCK: {
                    List<JCStatement> l = ((JCBlock) s).stats;
                    return l.isEmpty() || canComplete(l.get(l.size() - 1));
                }
                case IF: {
                    JCIf i = (JCIf) s;
                    if (i.elsepart == null) return true;
                    return canComplete(i.thenpart) || canComplete(i.elsepart);
                }
                case WHILELOOP: {
                    JCWhileLoop w = (JCWhileLoop) s;
                    if (isTrue(w.cond)) return containsBreakFor(w.body);
                    return true;
                }
                case FORLOOP: {
                    JCForLoop f = (JCForLoop) s;
                    if (f.cond == null || isTrue(f.cond)) return containsBreakFor(f.body);
                    return true;
                }
                case TRY: {
                    JCTry t = (JCTry) s;
                    if (t.finalizer != null && !canComplete(t.finalizer)) return false;
                    if (canComplete(t.body)) return true;
                    for (JCCatch c : t.catchers) if (canComplete(c.body)) return true;
                    return false;
                }
                case SWITCH:
                    return true;
                default:
                    return true;
            }
        }

        boolean isTrue(JCExpression c) {
            Object v = c.type.constValue();
            return v != null && ((Integer) v) != 0;
        }

        boolean containsBreakFor(JCTree body) {
            final boolean[] r = {false};
            new com.sun.tools.javac.tree.TreeScanner() {
                int depth;

                @Override
                public void visitBreak(JCBreak b) {
                    if (b.label != null || depth == 0) r[0] = true;
                }

                @Override
                public void visitWhileLoop(JCWhileLoop t) {
                    depth++;
                    super.visitWhileLoop(t);
                    depth--;
                }

                @Override
                public void visitForLoop(JCForLoop t) {
                    depth++;
                    super.visitForLoop(t);
                    depth--;
                }

                @Override
                public void visitDoLoop(JCDoWhileLoop t) {
                    depth++;
                    super.visitDoLoop(t);
                    depth--;
                }

                @Override
                public void visitSwitch(JCSwitch t) {
                    depth++;
                    super.visitSwitch(t);
                    depth--;
                }
            }.scan(body);
            return r[0];
        }

        // ================================================================================================
        // expressions
        String defaultValue(Type t) {
            switch (t.getTag()) {
                case BOOLEAN:
                    return "false";
                case BYTE:
                    return "(sbyte)0";
                case CHAR:
                    return "'\\0'";
                case SHORT:
                    return "(short)0";
                case INT:
                    return "0";
                case LONG:
                    return "0L";
                case FLOAT:
                    return "0f";
                case DOUBLE:
                    return "0.0";
                default:
                    return "null";
            }
        }

        String cond(JCExpression e) {
            return expr(TreeInfo.skipParens(e));
        }

        /** expression converted for assignment to `target` (inserts the casts C# needs) */
        String init(JCExpression e, Type target) {
            if (e instanceof JCNewArray && ((JCNewArray) e).elemtype == null) {
                return arrayInit((JCNewArray) e, target);
            }
            return conv(e, target);
        }

        String conv(JCExpression e, Type target) {
            Type src = e.type;
            String x = expr(e);
            if (isPrim(target) && isPrim(src) && src.getTag() != target.getTag()) {
                Object cv = src.constValue();
                if (!csImplicit(src, target, cv)) {
                    if (cv != null && isPureLiteral(e)) return lit(cv instanceof Character ? (int) (Character) cv : convertConst(cv, src, target), target);
                    return "((" + ty(target) + ")(" + x + "))";
                }
            }
            return x;
        }

        Object convertConst(Object v, Type from, Type to) {
            long l = constLong(v);
            switch (to.getTag()) {
                case BYTE:
                    return (int) (byte) l;
                case SHORT:
                    return (int) (short) l;
                case CHAR:
                    return (int) (char) l;
                case INT:
                    return (int) l;
                case LONG:
                    return l;
                default:
                    return v;
            }
        }

        String arrayInit(JCNewArray na, Type target) {
            Type elem = ((ArrayType) target).elemtype;
            List<String> items = new ArrayList<>();
            for (JCExpression x : na.elems) items.add(init(x, elem));
            String body = String.join(", ", items);
            return "new " + ty(target) + " { " + body + " }";
        }

        boolean isPureLiteral(JCTree e) {
            final boolean[] ok = {true};
            new com.sun.tools.javac.tree.TreeScanner() {
                @Override
                public void visitIdent(JCIdent t) {
                    ok[0] = false;
                }

                @Override
                public void visitSelect(JCFieldAccess t) {
                    ok[0] = false;
                }

                @Override
                public void visitApply(JCMethodInvocation t) {
                    ok[0] = false;
                }
            }.scan(e);
            return ok[0];
        }

        /** expression used as a statement */
        String exprStmt(JCExpression e) {
            return expr(e);
        }

        String expr(JCExpression e) {
            // fold pure literal constant expressions (handles 0xFFFFFFFF, (byte)200, char constants...)
            Object cv = e.type != null ? e.type.constValue() : null;
            if (cv != null && !(e instanceof JCLiteral) && !(e instanceof JCIdent) && !(e instanceof JCFieldAccess)
                    && !(e instanceof JCParens && (((JCParens) e).expr instanceof JCIdent || ((JCParens) e).expr instanceof JCFieldAccess))) {
                return lit(cv, e.type);
            }
            switch (e.getTag()) {
                case LITERAL: {
                    JCLiteral l = (JCLiteral) e;
                    if (l.typetag == BOT) return "null";
                    Object v = l.value;
                    if (l.typetag == BOOLEAN) return ((Integer) v) != 0 ? "true" : "false";
                    if (l.typetag == CLASS) return strLit((String) v);
                    return lit(v, e.type);
                }
                case PARENS:
                    return "(" + expr(((JCParens) e).expr) + ")";
                case IDENT:
                    return ident((JCIdent) e);
                case SELECT:
                    return select((JCFieldAccess) e);
                case APPLY:
                    return apply((JCMethodInvocation) e);
                case NEWCLASS:
                    return newClass((JCNewClass) e);
                case NEWARRAY:
                    return newArray((JCNewArray) e);
                case ASSIGN: {
                    JCAssign a = (JCAssign) e;
                    return expr(a.lhs) + " = " + init(a.rhs, a.lhs.type);
                }
                case CONDEXPR: {
                    JCConditional c = (JCConditional) e;
                    Type t = c.type;
                    return "(" + cond(c.cond) + " ? " + condBranch(c.truepart, t) + " : " + condBranch(c.falsepart, t) + ")";
                }
                case TYPECAST: {
                    JCTypeCast tc = (JCTypeCast) e;
                    Type to = tc.clazz.type;
                    Type from = tc.expr.type;
                    if (types.isSameType(to, from) && !isPrim(to)) return expr(tc.expr);
                    return "((" + ty(to) + ")(" + expr(tc.expr) + "))";
                }
                case TYPETEST: {
                    JCInstanceOf io = (JCInstanceOf) e;
                    return "(" + expr(io.expr) + " is " + ty(((JCExpression) io.pattern).type) + ")";
                }
                case INDEXED: {
                    JCArrayAccess aa = (JCArrayAccess) e;
                    return primary(aa.indexed) + "[" + conv(aa.index, syms.intType) + "]";
                }
                case POS:
                case NEG:
                case NOT:
                case COMPL: {
                    JCUnary u = (JCUnary) e;
                    String op = u.getTag() == JCTree.Tag.POS ? "+" : u.getTag() == JCTree.Tag.NEG ? "-" : u.getTag() == JCTree.Tag.NOT ? "!" : "~";
                    String a = expr(u.arg);
                    if (!(u.arg instanceof JCParens) && (u.arg instanceof JCUnary || u.arg instanceof JCBinary || a.startsWith("-") || a.startsWith("+")))
                        a = "(" + a + ")";
                    return op + a;
                }
                case PREINC:
                case PREDEC:
                case POSTINC:
                case POSTDEC: {
                    JCUnary u = (JCUnary) e;
                    String a = expr(u.arg);
                    switch (u.getTag()) {
                        case PREINC:
                            return "++" + a;
                        case PREDEC:
                            return "--" + a;
                        case POSTINC:
                            return a + "++";
                        default:
                            return a + "--";
                    }
                }
                case BITOR_ASG:
                case BITXOR_ASG:
                case BITAND_ASG:
                case SL_ASG:
                case SR_ASG:
                case USR_ASG:
                case PLUS_ASG:
                case MINUS_ASG:
                case MUL_ASG:
                case DIV_ASG:
                case MOD_ASG:
                    return assignOp((JCAssignOp) e);
                default:
                    if (e instanceof JCBinary) return binary((JCBinary) e);
                    PROBLEMS.add("expression " + e.getTag() + " in " + cs + ": " + e);
                    return "/*?" + e + "*/";
            }
        }

        String condBranch(JCExpression b, Type t) {
            if (b.type.getTag() == BOT) return isPrim(t) ? expr(b) : "((" + ty(t) + ")null)";
            if (isPrim(t)) return conv(b, t);
            if (!types.isSameType(b.type, t)) return "((" + ty(t) + ")(" + expr(b) + "))";
            return expr(b);
        }

        /** operand that will be followed by `.member` or `[index]` */
        String primary(JCExpression e) {
            String s = expr(e);
            if (e instanceof JCIdent || e instanceof JCFieldAccess || e instanceof JCMethodInvocation || e instanceof JCParens
                    || e instanceof JCArrayAccess || (e instanceof JCLiteral && ((JCLiteral) e).typetag == CLASS)
                    || s.startsWith("((")) return s;
            return "(" + s + ")";
        }

        static final Set<JCTree.Tag> REL = EnumSet.of(JCTree.Tag.LT, JCTree.Tag.GT, JCTree.Tag.LE, JCTree.Tag.GE);

        String binary(JCBinary b) {
            JCTree.Tag tag = b.getTag();
            String op = opText(tag);
            // string concatenation
            if (tag == JCTree.Tag.PLUS && isString(b.type)) {
                return strOperand(b.lhs) + " + " + strOperand(b.rhs);
            }
            if (tag == JCTree.Tag.USR) {
                Type lt = types.unboxedTypeOrType(b.lhs.type);
                String l = expr(b.lhs);
                String r = shiftCount(b.rhs);
                if (b.type.getTag() == LONG) return "((long)((ulong)(" + l + ") >> " + r + "))";
                return "((int)((uint)(" + l + ") >> " + r + "))";
            }
            if (tag == JCTree.Tag.SL || tag == JCTree.Tag.SR) {
                return expr(b.lhs) + " " + op + " " + shiftCount(b.rhs);
            }
            String s = expr(b.lhs) + " " + op + " " + expr(b.rhs);
            if (REL.contains(tag)) return "(" + s + ")";
            return s;
        }

        String shiftCount(JCExpression r) {
            if (r.type.getTag() == LONG) return "((int)(" + expr(r) + "))";
            String s = expr(r);
            return (r instanceof JCBinary) ? "(" + s + ")" : s;
        }

        String strOperand(JCExpression e) {
            Type t = e.type;
            String s = expr(e);
            if (t.getTag() == BOOLEAN) return "J2ME.JString.valueOf(" + s + ")";
            if (t.getTag() == FLOAT || t.getTag() == DOUBLE) return "J2ME.JString.valueOf(" + s + ")";
            if (!isPrim(t) && !isString(t) && t.getTag() != BOT) return "J2ME.JString.valueOf((object)(" + s + "))";
            if (e instanceof JCConditional || e instanceof JCAssign || e instanceof JCAssignOp) return "(" + s + ")";
            if (e instanceof JCBinary && !isString(e.type)) return "(" + s + ")";
            return s;
        }

        String opText(JCTree.Tag t) {
            switch (t) {
                case OR:
                    return "||";
                case AND:
                    return "&&";
                case BITOR:
                    return "|";
                case BITXOR:
                    return "^";
                case BITAND:
                    return "&";
                case EQ:
                    return "==";
                case NE:
                    return "!=";
                case LT:
                    return "<";
                case GT:
                    return ">";
                case LE:
                    return "<=";
                case GE:
                    return ">=";
                case SL:
                    return "<<";
                case SR:
                    return ">>";
                case USR:
                    return ">>>";
                case PLUS:
                    return "+";
                case MINUS:
                    return "-";
                case MUL:
                    return "*";
                case DIV:
                    return "/";
                case MOD:
                    return "%";
                default:
                    PROBLEMS.add("operator " + t);
                    return "?";
            }
        }

        String assignOp(JCAssignOp a) {
            JCTree.Tag base = a.getTag().noAssignOp();
            Type lt = a.lhs.type;
            Type rt = a.rhs.type;
            String l = expr(a.lhs);
            boolean shift = base == JCTree.Tag.SL || base == JCTree.Tag.SR || base == JCTree.Tag.USR;
            if (isString(lt)) {
                return l + " += " + strOperand(a.rhs);
            }
            boolean needExpand = false;
            if (base == JCTree.Tag.USR) needExpand = true;
            else if (!shift && isPrim(lt) && isPrim(rt) && !csImplicit(rt, lt, rt.constValue())) needExpand = true;
            if (!needExpand) {
                String r = shift ? shiftCount(a.rhs) : expr(a.rhs);
                return l + " " + opText(base) + "= " + r;
            }
            if (!sideEffectFree(a.lhs)) {
                PROBLEMS.add("compound assignment with side effects needs manual care: " + a + " in " + cs);
            }
            // x op= y  ->  x = (T)(x op y)   (Java's implicit narrowing)
            String opexpr;
            if (base == JCTree.Tag.USR) {
                Type pt = lt.getTag() == LONG ? syms.longType : syms.intType;
                if (pt.getTag() == LONG) opexpr = "(long)((ulong)(" + l + ") >> " + shiftCount(a.rhs) + ")";
                else opexpr = "(int)((uint)(" + l + ") >> " + shiftCount(a.rhs) + ")";
            } else {
                opexpr = l + " " + opText(base) + " " + (shift ? shiftCount(a.rhs) : "(" + expr(a.rhs) + ")");
            }
            return l + " = ((" + ty(lt) + ")(" + opexpr + "))";
        }

        boolean sideEffectFree(JCExpression e) {
            final boolean[] ok = {true};
            new com.sun.tools.javac.tree.TreeScanner() {
                @Override
                public void visitApply(JCMethodInvocation t) {
                    ok[0] = false;
                }

                @Override
                public void visitUnary(JCUnary t) {
                    if (t.getTag() == JCTree.Tag.PREINC || t.getTag() == JCTree.Tag.PREDEC
                            || t.getTag() == JCTree.Tag.POSTINC || t.getTag() == JCTree.Tag.POSTDEC) ok[0] = false;
                    super.visitUnary(t);
                }

                @Override
                public void visitAssign(JCAssign t) {
                    ok[0] = false;
                }

                @Override
                public void visitAssignop(JCAssignOp t) {
                    ok[0] = false;
                }

                @Override
                public void visitNewClass(JCNewClass t) {
                    ok[0] = false;
                }
            }.scan(e);
            return ok[0];
        }

        // ---- names --------------------------------------------------------------------------------
        String ident(JCIdent id) {
            Symbol s = id.sym;
            if (s instanceof VarSymbol) {
                VarSymbol v = (VarSymbol) s;
                if (v.owner instanceof MethodSymbol || v.owner.kind == Kinds.Kind.MTH || localNames.containsKey(v)) {
                    String n = localNames.get(v);
                    if (n == null) {
                        PROBLEMS.add("unknown local " + v + " in " + cs);
                        return esc(v.name.toString());
                    }
                    return n;
                }
                if (v.name.contentEquals("this")) return "this";
                if (v.name.contentEquals("super")) return "base";
                return fieldRef(null, v);
            }
            if (s instanceof ClassSymbol) return typeRef((ClassSymbol) s);
            if (s.name.contentEquals("this")) return "this";
            if (s.name.contentEquals("super")) return "base";
            PROBLEMS.add("ident " + id + " kind " + s.kind + " in " + cs);
            return id.toString();
        }

        String typeRef(ClassSymbol c) {
            if (isInterfaceConstHolder(c)) return constHolder(c);
            return staticOwner(c);
        }

        String fieldRef(JCExpression qualifier, VarSymbol v) {
            ClassSymbol owner = (ClassSymbol) v.owner;
            if (v.isStatic()) {
                if (!translated(owner)) {
                    JDK_MEMBERS.add("field " + owner.getQualifiedName() + "." + v.name);
                    return jdkStaticField(owner, v);
                }
                return staticOwnerExpr(v) + "." + fieldName(v);
            }
            if (!translated(owner)) {
                if (v.name.contentEquals("length") && qualifier != null && qualifier.type.getTag() == ARRAY)
                    return primary(qualifier) + ".Length";
                JDK_MEMBERS.add("field " + owner.getQualifiedName() + "." + v.name);
                PROBLEMS.add("JDK instance field " + owner + "." + v.name);
                return (qualifier == null ? "this" : primary(qualifier)) + "." + esc(v.name.toString());
            }
            String q = qualifier == null ? "this" : primary(qualifier);
            if (isSuper(qualifier)) q = "base";
            return q + "." + fieldName(v);
        }

        String jdkStaticField(ClassSymbol owner, VarSymbol v) {
            String fq = owner.getQualifiedName() + "." + v.name;
            switch (fq) {
                case "java.lang.Integer.MAX_VALUE":
                    return "int.MaxValue";
                case "java.lang.Integer.MIN_VALUE":
                    return "int.MinValue";
                case "java.lang.Long.MAX_VALUE":
                    return "long.MaxValue";
                case "java.lang.Long.MIN_VALUE":
                    return "long.MinValue";
                case "java.lang.Math.PI":
                    return "global::System.Math.PI";
                default:
                    return staticOwner(owner) + "." + esc(v.name.toString());
            }
        }

        String select(JCFieldAccess fa) {
            Symbol s = fa.sym;
            if (s instanceof VarSymbol) {
                if (fa.name.contentEquals("length") && fa.selected.type.getTag() == ARRAY) return primary(fa.selected) + ".Length";
                if (fa.name.contentEquals("this")) return "this";
                return fieldRef(isTypeName(fa.selected) ? null : fa.selected, (VarSymbol) s);
            }
            if (s instanceof ClassSymbol) return typeRef((ClassSymbol) s);
            PROBLEMS.add("select " + fa + " in " + cs);
            return fa.toString();
        }

        boolean isSuper(JCExpression e) {
            return e instanceof JCIdent && ((JCIdent) e).name.contentEquals("super");
        }

        boolean isTypeName(JCExpression e) {
            Symbol s = TreeInfo.symbol(e);
            return s instanceof ClassSymbol || s instanceof PackageSymbol;
        }

        // ---- calls --------------------------------------------------------------------------------
        String args(JCMethodInvocation mi, MethodSymbol m) {
            return argList(mi.args, m, true);
        }

        String argList(com.sun.tools.javac.util.List<JCExpression> args, MethodSymbol m, boolean exactForOverloads) {
            List<Type> pts = m.type.getParameterTypes();
            boolean overloaded = isOverloaded(m);
            List<String> r = new ArrayList<>();
            int i = 0;
            for (JCExpression a : args) {
                Type pt = pts.get(i++);
                String s = conv(a, pt);
                if (overloaded && !isPrim(pt)) {
                    if (a.type.getTag() == BOT || (!types.isSameType(a.type, pt) && !isPrim(a.type))) s = "((" + ty(pt) + ")(" + s + "))";
                } else if (overloaded && isPrim(pt) && isPrim(a.type) && a.type.getTag() != pt.getTag()) {
                    if (!s.startsWith("((")) s = "((" + ty(pt) + ")(" + s + "))";
                }
                r.add(s);
            }
            return String.join(", ", r);
        }

        boolean isOverloaded(MethodSymbol m) {
            ClassSymbol owner = (ClassSymbol) m.owner;
            List<ClassSymbol> chain = new ArrayList<>();
            chain.add(owner);
            chain.addAll(superclasses(owner));
            Set<ClassSymbol> ifs = new HashSet<>();
            allInterfaces(owner, ifs);
            chain.addAll(ifs);
            for (ClassSymbol c : chain) {
                for (Symbol e : c.members().getSymbolsByName(m.name)) {
                    if (e != m && e instanceof MethodSymbol && ((MethodSymbol) e).params().size() == m.params().size()
                            && !types.isSameTypes(((MethodSymbol) e).type.getParameterTypes(), m.type.getParameterTypes()))
                        return true;
                }
            }
            return false;
        }

        String apply(JCMethodInvocation mi) {
            MethodSymbol m = (MethodSymbol) TreeInfo.symbol(mi.meth);
            ClassSymbol owner = (ClassSymbol) m.owner;
            String name = TreeInfo.name(mi.meth).toString();
            if (name.equals("super") || name.equals("this")) {
                PROBLEMS.add("constructor call in body " + cs);
                return "/*" + mi + "*/";
            }
            JCExpression qual = (mi.meth instanceof JCFieldAccess) ? ((JCFieldAccess) mi.meth).selected : null;
            boolean superCall = isSuper(qual);
            if (!translated(owner)) return jdkCall(mi, m, qual);
            String a = args(mi, m);
            if (m.isStatic()) return staticOwner(owner) + "." + methodName(m) + "(" + a + ")";
            String recv;
            Type recvType;
            if (qual == null) {
                recv = "this";
                recvType = cs.type;
            } else if (superCall) {
                return "base." + methodName(m) + "(" + a + ")";
            } else {
                recv = primary(qual);
                recvType = qual.type;
            }
            // C# drops base-class overloads when a more derived class declares an applicable one:
            // call through the declaring class when the receiver class adds overloads of the same name.
            if (recvType.tsym instanceof ClassSymbol && recvType.tsym != owner && (owner.flags() & Flags.INTERFACE) == 0
                    && derivedDeclaresSameName((ClassSymbol) recvType.tsym, owner, m)) {
                recv = "((" + csName(owner) + ")" + recv + ")";
            }
            return recv + "." + methodName(m) + "(" + a + ")";
        }

        boolean derivedDeclaresSameName(ClassSymbol from, ClassSymbol declaring, MethodSymbol m) {
            ClassSymbol c = from;
            while (c != null && c != declaring) {
                for (Symbol e : c.members().getSymbolsByName(m.name)) {
                    if (e instanceof MethodSymbol && !((MethodSymbol) e).isConstructor()) {
                        MethodSymbol em = (MethodSymbol) e;
                        if (OVERRIDES.containsKey(em) || isObjectOverride(em)) continue; // overrides do not hide
                        if ((em.flags() & Flags.PRIVATE) != 0 && c != cs) continue;
                        return true;
                    }
                }
                Type st = c.getSuperclass();
                c = (st != null && st.getTag() == CLASS) ? (ClassSymbol) st.tsym : null;
            }
            return false;
        }

        String jdkCall(JCMethodInvocation mi, MethodSymbol m, JCExpression qual) {
            ClassSymbol owner = (ClassSymbol) m.owner;
            String fq = owner.getQualifiedName().toString();
            String name = m.name.toString();
            JDK_MEMBERS.add("method " + fq + "." + name + m.type.getParameterTypes());
            String a = argList(mi.args, m, false);
            String recv = qual == null ? "this" : (isTypeName(qual) ? null : primary(qual));
            // java.lang.Object / Throwable members
            if (!m.isStatic()) {
                if (name.equals("toString") && m.params().isEmpty()) return recv + ".ToString()";
                if (name.equals("equals") && m.params().size() == 1 && !fq.equals("java.lang.String"))
                    return "J2ME.JObject.equals(" + recv + ", " + a + ")";
                if (name.equals("hashCode") && m.params().isEmpty() && !fq.equals("java.lang.String"))
                    return recv + ".GetHashCode()";
                if (name.equals("getClass")) return recv + ".GetType()";
                if (fq.equals("java.lang.Class") && name.equals("getName")) return "J2ME.JObject.className(" + recv + ")";
                if (fq.equals("java.lang.Throwable") || fq.equals("java.lang.Exception")) {
                    if (name.equals("printStackTrace")) return "J2ME.JRuntime.printStackTrace(" + recv + ")";
                    if (name.equals("getMessage")) return "J2ME.JRuntime.getMessage(" + recv + ")";
                }
                if (fq.equals("java.lang.Object") && (name.equals("wait") || name.equals("notify") || name.equals("notifyAll"))) {
                    PROBLEMS.add("monitor call " + mi + " in " + cs);
                    return "J2ME.JRuntime." + name + "(" + recv + (a.isEmpty() ? "" : ", " + a) + ")";
                }
                if (fq.equals("java.lang.Object")) {
                    PROBLEMS.add("Object method " + name + " in " + cs);
                }
                // String / StringBuffer / shims: same Java names (extension methods or shim members)
                return recv + "." + esc(name) + "(" + a + ")";
            }
            return staticOwner(owner) + "." + esc(name) + "(" + a + ")";
        }

        String newClass(JCNewClass nc) {
            if (nc.def != null) {
                PROBLEMS.add("anonymous class in " + cs);
                return "/* anonymous class */null";
            }
            MethodSymbol ctor = (MethodSymbol) nc.constructor;
            ClassSymbol c = (ClassSymbol) nc.type.tsym;
            String a = argList(nc.args, ctor, true);
            if (!translated(c)) {
                String fq = c.getQualifiedName().toString();
                JDK_MEMBERS.add("new " + fq + ctor.type.getParameterTypes());
                if (fq.equals("java.lang.String")) return "J2ME.JString.newString(" + a + ")";
                if (fq.equals("java.lang.Object")) return "new object()";
            }
            return "new " + ty(nc.type) + "(" + a + ")";
        }

        String newArray(JCNewArray na) {
            if (na.elems != null) {
                return arrayInit(na, na.type);
            }
            Type t = na.type;
            List<String> dims = new ArrayList<>();
            for (JCExpression d : na.dims) dims.add(conv(d, syms.intType));
            int total = 0;
            Type x = t;
            while (x.getTag() == ARRAY) {
                total++;
                x = ((ArrayType) x).elemtype;
            }
            if (dims.size() == 1) {
                // new T[n] or new T[n][] ...
                StringBuilder s = new StringBuilder("new " + ty(x) + "[" + dims.get(0) + "]");
                for (int i = 1; i < total; i++) s.append("[]");
                return s.toString();
            }
            // new T[a][b]... -> jagged allocation helper
            Type leaf = t;
            for (int i = 0; i < dims.size(); i++) leaf = ((ArrayType) leaf).elemtype;
            return "J2ME.JArray.New" + dims.size() + "<" + ty(leaf) + ">(" + String.join(", ", dims) + ")";
        }
    }
}
