import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Repairs JD-Core's local-variable scoping mistakes in decompiled sources.
 *
 * JD-Core 0.7 often declares a local inside a block/for-init and keeps using it after the block
 * ends (or uses it before the declaration). javac then either rejects the code or - worse - binds
 * the name to a field / inherited constant with the same name. For every method we find simple-name
 * uses that are not covered by the scope of a local declaration of that name in the same method,
 * and hoist that variable to the top of the method body (declarations become assignments).
 *
 * usage: java ScopeFix [--dry] file.java...
 */
public class ScopeFix {
    static class Decl {
        VariableTree tree;
        long start;      // declaration start
        long scopeStart; // where the name becomes visible
        long scopeEnd;   // end of enclosing block / for statement
        boolean inForInit;
        ForLoopTree forLoop;
        String typeText;
        boolean primitive;
    }

    static class Use {
        String name;
        long pos;
        boolean qualifier; // used as `name.member`
    }

    static String src;
    static SourcePositions sp;
    static CompilationUnitTree cu;
    static List<long[]> edits = new ArrayList<>(); // start,end,index into texts
    static List<String> editTexts = new ArrayList<>();
    static List<String> report = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        boolean dry = false;
        List<String> files = new ArrayList<>();
        for (String a : args) {
            if (a.equals("--dry")) dry = true;
            else files.add(a);
        }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        int changedFiles = 0;
        for (String f : files) {
            src = new String(Files.readAllBytes(Paths.get(f)), StandardCharsets.UTF_8);
            StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
            JavacTask task = (JavacTask) jc.getTask(null, fm, d -> {}, List.of("-proc:none"), null,
                    fm.getJavaFileObjects(f));
            Iterator<? extends CompilationUnitTree> it = task.parse().iterator();
            cu = it.next();
            sp = Trees.instance(task).getSourcePositions();
            edits.clear();
            editTexts.clear();
            report.clear();
            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitMethod(MethodTree m, Void v) {
                    if (m.getBody() != null) processBody(m.getBody(), m, false);
                    return null; // no nested methods (no inner classes in this code base)
                }

                @Override
                public Void visitBlock(BlockTree b, Void v) {
                    // initializer blocks directly in a class
                    if (getCurrentPath().getParentPath().getLeaf() instanceof ClassTree) {
                        processBody(b, null, true);
                        return null;
                    }
                    return super.visitBlock(b, v);
                }
            }.scan(new TreePath(cu), null);
            for (String r : report) System.out.println(f + ": " + r);
            if (!edits.isEmpty()) {
                changedFiles++;
                // apply edits back to front
                Integer[] idx = new Integer[edits.size()];
                for (int i = 0; i < idx.length; i++) idx[i] = i;
                Arrays.sort(idx, (a, b) -> Long.compare(edits.get(b)[0], edits.get(a)[0]));
                StringBuilder sb = new StringBuilder(src);
                for (int i : idx) {
                    long[] e = edits.get(i);
                    sb.replace((int) e[0], (int) e[1], editTexts.get(i));
                }
                if (!dry) Files.write(Paths.get(f), sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        System.out.println("files changed: " + changedFiles);
    }

    static void processBody(BlockTree body, MethodTree method, boolean initializer) {
        List<Decl> decls = new ArrayList<>();
        List<Use> uses = new ArrayList<>();
        Set<String> params = new HashSet<>();
        if (method != null) for (VariableTree p : method.getParameters()) params.add(p.getName().toString());

        new TreeScanner<Void, Void>() {
            Deque<Tree> blocks = new ArrayDeque<>();

            @Override
            public Void visitBlock(BlockTree b, Void v) {
                blocks.push(b);
                super.visitBlock(b, v);
                blocks.pop();
                return null;
            }

            @Override
            public Void visitCase(CaseTree c, Void v) {
                return super.visitCase(c, v);
            }

            @Override
            public Void visitForLoop(ForLoopTree f, Void v) {
                for (StatementTree s : f.getInitializer()) {
                    if (s instanceof VariableTree) {
                        VariableTree vt = (VariableTree) s;
                        Decl d = mk(vt);
                        d.inForInit = true;
                        d.forLoop = f;
                        d.scopeEnd = sp.getEndPosition(cu, f);
                        decls.add(d);
                        if (vt.getInitializer() != null) scan(vt.getInitializer(), v);
                    } else {
                        scan(s, v);
                    }
                }
                scan(f.getCondition(), v);
                scan(f.getUpdate(), v);
                scan(f.getStatement(), v);
                return null;
            }

            @Override
            public Void visitVariable(VariableTree vt, Void v) {
                Decl d = mk(vt);
                Tree enclosing = blocks.peek();
                d.scopeEnd = enclosing != null ? sp.getEndPosition(cu, enclosing) : Long.MAX_VALUE;
                // a switch case group shares the switch block scope - approximated by enclosing block
                decls.add(d);
                if (vt.getInitializer() != null) scan(vt.getInitializer(), v);
                return null;
            }

            @Override
            public Void visitCatch(CatchTree c, Void v) {
                // catch parameter: scope = catch block; never misused by JD-Core
                scan(c.getBlock(), v);
                return null;
            }

            @Override
            public Void visitMethodInvocation(MethodInvocationTree mi, Void v) {
                ExpressionTree sel = mi.getMethodSelect();
                if (sel instanceof IdentifierTree) {
                    // method name, not a variable
                } else {
                    scan(sel, v);
                }
                for (ExpressionTree a : mi.getArguments()) scan(a, v);
                return null;
            }

            @Override
            public Void visitMemberSelect(MemberSelectTree ms, Void v) {
                ExpressionTree e = ms.getExpression();
                if (e instanceof IdentifierTree) {
                    Use u = new Use();
                    u.name = ((IdentifierTree) e).getName().toString();
                    u.pos = sp.getStartPosition(cu, e);
                    u.qualifier = true;
                    uses.add(u);
                    return null;
                }
                return super.visitMemberSelect(ms, v);
            }

            @Override
            public Void visitIdentifier(IdentifierTree id, Void v) {
                Use u = new Use();
                u.name = id.getName().toString();
                u.pos = sp.getStartPosition(cu, id);
                uses.add(u);
                return null;
            }

            @Override
            public Void visitNewClass(NewClassTree nc, Void v) {
                if (nc.getEnclosingExpression() != null) scan(nc.getEnclosingExpression(), v);
                for (ExpressionTree a : nc.getArguments()) scan(a, v);
                return null; // identifier = type
            }

            @Override
            public Void visitTypeCast(TypeCastTree tc, Void v) {
                scan(tc.getExpression(), v);
                return null;
            }

            @Override
            public Void visitInstanceOf(InstanceOfTree io, Void v) {
                scan(io.getExpression(), v);
                return null;
            }

            @Override
            public Void visitNewArray(NewArrayTree na, Void v) {
                if (na.getDimensions() != null) for (ExpressionTree d : na.getDimensions()) scan(d, v);
                if (na.getInitializers() != null) for (ExpressionTree d : na.getInitializers()) scan(d, v);
                return null;
            }

            @Override
            public Void visitLabeledStatement(LabeledStatementTree l, Void v) {
                return scan(l.getStatement(), v);
            }

            @Override
            public Void visitBreak(BreakTree b, Void v) {
                return null;
            }

            @Override
            public Void visitContinue(ContinueTree c, Void v) {
                return null;
            }
        }.scan(body, null);

        // group declarations by name
        Map<String, List<Decl>> byName = new LinkedHashMap<>();
        for (Decl d : decls) byName.computeIfAbsent(d.tree.getName().toString(), k -> new ArrayList<>()).add(d);

        List<Decl> hoist = new ArrayList<>();
        for (Map.Entry<String, List<Decl>> e : byName.entrySet()) {
            String name = e.getKey();
            List<Decl> ds = e.getValue();
            boolean violated = false;
            for (Use u : uses) {
                if (!u.name.equals(name)) continue;
                boolean covered = false;
                for (Decl d : ds) {
                    if (u.pos >= d.scopeStart && u.pos < d.scopeEnd) {
                        covered = true;
                        break;
                    }
                }
                if (covered) continue;
                if (params.contains(name)) continue;
                if (u.qualifier) {
                    boolean allPrim = true;
                    for (Decl d : ds) allPrim &= d.primitive;
                    if (allPrim) continue; // `k.foo()` with only int locals named k -> class k
                }
                violated = true;
                break;
            }
            // also: the same name declared twice where scopes overlap would be illegal; after hoisting fine.
            if (!violated) continue;
            String t0 = ds.get(0).typeText;
            boolean sameType = true;
            for (Decl d : ds) sameType &= d.typeText.equals(t0);
            if (!sameType) {
                report.add("MANUAL: '" + name + "' used out of scope but declared with types " + typesOf(ds)
                        + " at line " + cu.getLineMap().getLineNumber(ds.get(0).start));
                continue;
            }
            hoist.addAll(ds);
        }
        if (hoist.isEmpty()) return;

        // insertion point: start of body (after super()/this() call in constructors)
        long insertAt = sp.getStartPosition(cu, body) + 1;
        if (method != null && method.getName().contentEquals("<init>") && !body.getStatements().isEmpty()) {
            StatementTree first = body.getStatements().get(0);
            if (first instanceof ExpressionStatementTree) {
                ExpressionTree ex = ((ExpressionStatementTree) first).getExpression();
                if (ex instanceof MethodInvocationTree) {
                    String sel = ((MethodInvocationTree) ex).getMethodSelect().toString();
                    if (sel.equals("super") || sel.equals("this")) insertAt = sp.getEndPosition(cu, first);
                }
            }
        }
        LinkedHashMap<String, String> hoisted = new LinkedHashMap<>();
        for (Decl d : hoist) hoisted.putIfAbsent(d.tree.getName().toString(), d.typeText);
        report.add("hoist " + hoisted + " in " + (method != null ? method.getName() : "<init-block>")
                + " @line " + cu.getLineMap().getLineNumber(sp.getStartPosition(cu, body)));
        StringBuilder ins = new StringBuilder();
        for (Map.Entry<String, String> h : hoisted.entrySet()) {
            ins.append("\n        ").append(h.getValue()).append(' ').append(h.getKey()).append(" = ")
                    .append(defaultFor(h.getValue())).append("; // PORT-FIX: hoisted (decompiler scope bug)");
        }
        addEdit(insertAt, insertAt, ins.toString());

        // rewrite declarations
        Set<ForLoopTree> doneFor = new HashSet<>();
        for (Decl d : hoist) {
            VariableTree vt = d.tree;
            if (d.inForInit) {
                if (doneFor.contains(d.forLoop)) continue;
                doneFor.add(d.forLoop);
                // rewrite the whole init list: `int i = 0, j = 1` -> `i = 0, j = 1`
                List<? extends StatementTree> init = d.forLoop.getInitializer();
                long s = sp.getStartPosition(cu, init.get(0));
                long eEnd = sp.getEndPosition(cu, init.get(init.size() - 1));
                StringBuilder r = new StringBuilder();
                for (StatementTree st : init) {
                    VariableTree v = (VariableTree) st;
                    if (!hoisted.containsKey(v.getName().toString())) {
                        report.add("MANUAL: for-init mixes hoisted/non-hoisted vars at line "
                                + cu.getLineMap().getLineNumber(s));
                    }
                    if (r.length() > 0) r.append(", ");
                    r.append(v.getName()).append(" = ").append(v.getInitializer() == null
                            ? defaultFor(typeText(v)) : text(v.getInitializer()));
                }
                // the init list text may include the trailing ';' handled by the for statement itself
                addEdit(s, eEnd, r.toString());
            } else {
                long s = sp.getStartPosition(cu, vt);
                long e = sp.getEndPosition(cu, vt);
                // detect multi-declarations `int a, b;` sharing the type: javac gives each its own tree
                String original = src.substring((int) s, (int) e);
                String repl;
                if (vt.getInitializer() == null) {
                    repl = ";";
                } else {
                    repl = vt.getName() + " = " + text(vt.getInitializer()) + ";";
                }
                if (!original.endsWith(";")) {
                    report.add("MANUAL: odd declaration at line " + cu.getLineMap().getLineNumber(s) + ": " + original);
                    continue;
                }
                addEdit(s, e, repl);
            }
        }
    }

    static String typesOf(List<Decl> ds) {
        Set<String> s = new LinkedHashSet<>();
        for (Decl d : ds) s.add(d.typeText);
        return s.toString();
    }

    static void addEdit(long s, long e, String t) {
        edits.add(new long[]{s, e});
        editTexts.add(t);
    }

    static String text(Tree t) {
        return src.substring((int) sp.getStartPosition(cu, t), (int) sp.getEndPosition(cu, t));
    }

    static String typeText(VariableTree vt) {
        return text(vt.getType());
    }

    static Decl mk(VariableTree vt) {
        Decl d = new Decl();
        d.tree = vt;
        d.start = sp.getStartPosition(cu, vt);
        d.scopeStart = d.start;
        d.typeText = typeText(vt).replaceAll("\\s+", "");
        Tree t = vt.getType();
        d.primitive = t instanceof PrimitiveTypeTree;
        return d;
    }

    static String defaultFor(String type) {
        switch (type) {
            case "int":
            case "long":
            case "short":
            case "byte":
            case "char":
            case "float":
            case "double":
                return type.equals("long") ? "0L" : (type.equals("int") ? "0" : "(" + type + ") 0");
            case "boolean":
                return "false";
            default:
                return "null";
        }
    }
}
