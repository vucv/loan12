import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Lists calls where javac had to choose between several applicable overloads. JD-Core prints calls
 * without the casts that selected the overload in the original bytecode, so javac may now pick a
 * different (more specific) method - e.g. a synthetic accessor `static void a(aa)` instead of the
 * inherited `a(listener)` for `a(this)`.
 */
public class OverloadCheck {
    public static void main(String[] args) throws Exception {
        String cp = ".";
        List<String> files = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-cp")) cp = args[++i];
            else files.add(args[i]);
        }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-proc:none", "-cp", cp, "-encoding", "UTF-8"),
                null, fm.getJavaFileObjectsFromStrings(files));
        Iterable<? extends CompilationUnitTree> units = task.parse();
        task.analyze();
        Trees trees = Trees.instance(task);
        Types types = task.getTypes();
        Elements elements = task.getElements();
        SourcePositions sp = trees.getSourcePositions();
        for (CompilationUnitTree cu : units) {
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitMethodInvocation(MethodInvocationTree mi, Void v) {
                    Element el = trees.getElement(getCurrentPath());
                    if (el instanceof ExecutableElement) {
                        ExecutableElement chosen = (ExecutableElement) el;
                        TypeElement owner = null;
                        ExpressionTree sel = mi.getMethodSelect();
                        if (sel instanceof MemberSelectTree) {
                            TypeMirror t = trees.getTypeMirror(new TreePath(getCurrentPath(), ((MemberSelectTree) sel).getExpression()));
                            if (t instanceof DeclaredType) owner = (TypeElement) ((DeclaredType) t).asElement();
                            else {
                                Element qe = trees.getElement(new TreePath(getCurrentPath(), ((MemberSelectTree) sel).getExpression()));
                                if (qe instanceof TypeElement) owner = (TypeElement) qe;
                            }
                        } else {
                            // unqualified: enclosing class
                            TreePath p = getCurrentPath();
                            while (p != null && !(p.getLeaf() instanceof ClassTree)) p = p.getParentPath();
                            if (p != null) owner = (TypeElement) trees.getElement(p);
                        }
                        if (owner != null) {
                            List<TypeMirror> argTypes = new ArrayList<>();
                            for (ExpressionTree a : mi.getArguments()) {
                                argTypes.add(trees.getTypeMirror(new TreePath(getCurrentPath(), a)));
                            }
                            List<String> applicable = new ArrayList<>();
                            for (Element m : elements.getAllMembers(owner)) {
                                if (!(m instanceof ExecutableElement)) continue;
                                if (!m.getSimpleName().contentEquals(chosen.getSimpleName())) continue;
                                ExecutableElement em = (ExecutableElement) m;
                                if (em.getParameters().size() != argTypes.size()) continue;
                                boolean ok = true;
                                for (int i = 0; i < argTypes.size() && ok; i++) {
                                    TypeMirror at = argTypes.get(i);
                                    TypeMirror pt = em.getParameters().get(i).asType();
                                    if (at == null || at.getKind() == TypeKind.ERROR) ok = false;
                                    else if (at.getKind() == TypeKind.NULL) ok = !pt.getKind().isPrimitive();
                                    else ok = types.isAssignable(at, pt);
                                }
                                if (ok) applicable.add(em.getEnclosingElement().getSimpleName() + "." + em + (em.getModifiers().contains(Modifier.STATIC) ? " static" : ""));
                            }
                            if (applicable.size() > 1) {
                                long line = cu.getLineMap().getLineNumber(sp.getStartPosition(cu, mi));
                                System.out.println(cu.getSourceFile().getName().replaceAll(".*/jsrc/", "") + ":" + line
                                        + "  " + mi + "\n     chosen: " + chosen.getEnclosingElement().getSimpleName() + "." + chosen
                                        + (chosen.getModifiers().contains(Modifier.STATIC) ? " static" : "") + "\n     applicable: " + applicable);
                            }
                        }
                    }
                    return super.visitMethodInvocation(mi, v);
                }
            }.scan(new TreePath(cu), null);
        }
    }
}
