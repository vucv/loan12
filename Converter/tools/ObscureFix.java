import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Finds `X.member` where the simple name X resolves to a variable although a class X is visible
 * (obfuscated jars reuse names like `d`, `y`, `u` for both fields and classes; JD-Core prints the
 * class reference unqualified, which javac then binds to the field).
 *
 * JD-Core always writes instance fields as `this.x`, so an unqualified X that binds to an instance
 * field or to a primitive local can only mean the class: those are rewritten to the fully qualified
 * class name. Everything else is only reported.
 *
 * usage: java ObscureFix [--apply] -cp <classpath> <sourcefiles...>
 */
public class ObscureFix {
    public static void main(String[] args) throws Exception {
        boolean apply = false;
        String cp = ".";
        List<String> files = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--apply")) apply = true;
            else if (args[i].equals("-cp")) cp = args[++i];
            else files.add(args[i]);
        }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {},
                List.of("-proc:none", "-cp", cp, "-encoding", "UTF-8", "-XDshould-stop.ifError=FLOW",
                        "-XDshould-stop.at=FLOW"), null, fm.getJavaFileObjectsFromStrings(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        Trees trees = Trees.instance(task);
        SourcePositions sp = trees.getSourcePositions();
        javax.lang.model.util.Elements elements = task.getElements();

        for (CompilationUnitTree cu : units) {
            String src = cu.getSourceFile().getCharContent(true).toString();
            String pkg = cu.getPackageName() == null ? "" : cu.getPackageName().toString();
            // visible classes by simple name: imports + same package
            Map<String, String> visible = new HashMap<>();
            PackageElement pe = elements.getPackageElement(pkg);
            if (pe != null) {
                for (Element e : pe.getEnclosedElements()) {
                    if (e instanceof TypeElement) visible.put(e.getSimpleName().toString(), ((TypeElement) e).getQualifiedName().toString());
                }
            }
            for (ImportTree it : cu.getImports()) {
                String q = it.getQualifiedIdentifier().toString();
                if (!q.endsWith("*")) visible.put(q.substring(q.lastIndexOf('.') + 1), q);
            }
            List<long[]> edits = new ArrayList<>();
            List<String> repl = new ArrayList<>();
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitMemberSelect(MemberSelectTree ms, Void v) {
                    ExpressionTree ex = ms.getExpression();
                    if (ex instanceof IdentifierTree) {
                        String name = ((IdentifierTree) ex).getName().toString();
                        String cls = visible.get(name);
                        if (cls != null) {
                            Element el = trees.getElement(new TreePath(getCurrentPath(), ex));
                            if (el != null && (el.getKind() == ElementKind.FIELD || el.getKind() == ElementKind.LOCAL_VARIABLE
                                    || el.getKind() == ElementKind.PARAMETER || el.getKind() == ElementKind.EXCEPTION_PARAMETER)) {
                                long pos = sp.getStartPosition(cu, ex);
                                long line = cu.getLineMap().getLineNumber(pos);
                                boolean isStatic = el.getModifiers().contains(Modifier.STATIC);
                                TypeMirror t = el.asType();
                                boolean prim = t.getKind().isPrimitive();
                                String what = el.getKind() + (isStatic ? " static" : "") + " " + t + " in " + el.getEnclosingElement();
                                boolean fix = (el.getKind() == ElementKind.FIELD && !isStatic) || prim;
                                // static field / reference local: fix too when the member does not exist on that type
                                if (!fix && t instanceof DeclaredType) {
                                    String member = ms.getIdentifier().toString();
                                    boolean has = false;
                                    for (Element m : elements.getAllMembers((TypeElement) ((DeclaredType) t).asElement())) {
                                        if (m.getSimpleName().contentEquals(member)) has = true;
                                    }
                                    if (!has) fix = true;
                                }
                                if (!fix && t.getKind() == TypeKind.ARRAY && !ms.getIdentifier().contentEquals("length")) fix = true;
                                System.out.println((fix ? "FIX    " : "REVIEW ") + cu.getSourceFile().getName() + ":" + line + "  "
                                        + name + "." + ms.getIdentifier() + "  -> " + cls + "   [" + what + "]");
                                if (fix) {
                                    edits.add(new long[]{pos, sp.getEndPosition(cu, ex)});
                                    repl.add(cls);
                                }
                            }
                        }
                        return null;
                    }
                    return super.visitMemberSelect(ms, v);
                }
            }.scan(new TreePath(cu), null);
            if (apply && !edits.isEmpty()) {
                StringBuilder sb = new StringBuilder(src);
                Integer[] idx = new Integer[edits.size()];
                for (int i = 0; i < idx.length; i++) idx[i] = i;
                Arrays.sort(idx, (a, b) -> Long.compare(edits.get(b)[0], edits.get(a)[0]));
                for (int i : idx) sb.replace((int) edits.get(i)[0], (int) edits.get(i)[1], repl.get(i));
                Files.write(Paths.get(cu.getSourceFile().toUri()), sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
